# Audit Packet: A-015 - Sanity Checker Runtime

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Assignment-ID: A-015
Status: complete
Report-Revision: 1
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Compare `ItemSanityChecker` and its client HUD, render, input, synchronization, persistence, and runtime path against exact Thaumcraft 4.2.3.5 behavior. Audit use/equip conditions, warp categories and calculations, displayed bands/text/icons/colors, update frequency, first-/third-person behavior, client/server authority, and NBT/durability semantics.
- Anti-scope: Recipe and research metadata; unrelated warp-event gameplay; product edits; central Goal Ledger edits.
- Oracle and direction: Exact classes from `Thaumcraft-1.7.10-4.2.3.5.jar` and byte-identical extracted classes under `thaumcraft_src/**` -> current Forge 1.12.2 port.
- Read/write permissions: Original artifacts, product code, tests, and central ledger read-only; only this report writable.
- Stop condition: Every named runtime surface is either recorded as a proven divergence, an explicit parity control, or a stated test gap/assumption.

## Coverage Performed

- Port item and registration/model path:
  - `src/main/java/thaumcraft/common/items/relics/ItemSanityChecker.java`
  - `src/main/java/thaumcraft/common/config/ConfigItems.java`
  - `src/main/java/thaumcraft/client/ClientProxy.java`
  - `src/main/java/thaumcraft/client/ClientModelRegistry.java`
  - `src/main/resources/assets/thaumcraft/models/item/itemsanitychecker.json`
- Port HUD and runtime path:
  - `src/main/java/thaumcraft/client/lib/RenderEventHandler.java`
  - `src/main/java/thaumcraft/client/lib/UtilsFX.java`
  - `src/main/java/thaumcraft/client/lib/ClientTickEventsFML.java`
  - `src/main/java/thaumcraft/client/ClientProxy.java`
- Port warp state, persistence, and synchronization:
  - `src/main/java/thaumcraft/common/lib/capabilities/PlayerKnowledgeCapability.java`
  - `src/main/java/thaumcraft/common/lib/capabilities/PlayerKnowledgeProvider.java`
  - `src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncWarp.java`
  - `src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java`
  - `src/main/java/thaumcraft/common/lib/research/ResearchManager.java`
  - `src/main/java/thaumcraft/common/lib/WarpEvents.java`
- Existing tests inspected:
  - `src/test/java/thaumcraft/common/items/relics/ItemSanityCheckerRuntimeTest.java`
  - `src/test/java/thaumcraft/common/items/relics/ItemSanityCheckerStaticGuardTest.java`
  - `src/test/java/thaumcraft/client/SanityCheckerHudStaticGuardTest.java`
  - `src/test/java/thaumcraft/common/lib/capabilities/PlayerKnowledgeCapabilityTest.java`
- Exact TC4 classes decompiled and bytecode-checked:
  - `thaumcraft/common/items/relics/ItemSanityChecker.class`
  - `thaumcraft/client/lib/ClientTickEventsFML.class`
  - `thaumcraft/client/lib/UtilsFX.class`
  - `thaumcraft/common/lib/research/PlayerKnowledge.class`
  - `thaumcraft/common/lib/research/ResearchManager.class`
  - `thaumcraft/common/lib/network/playerdata/PacketSyncWarp.class`
  - `thaumcraft/common/lib/events/EventHandlerEntity.class`
  - `thaumcraft/common/config/ConfigItems.class`
  - `thaumcraft/client/ClientProxy.class`
- Forge 1.12.2 event ordering and inherited item behavior were checked against `forgeBin-1.12.2-14.23.5.2847.jar`, especially `GuiIngameForge`, `EntityRenderer`, `Minecraft`, `Item`, and `ItemStack`.

## Atomic Findings

### A-015-F01 - Existing TC4 warp persistence has no import path

- Type: migration defect/candidate
- Severity: high for migrated TC4 worlds; none for fresh 1.12 worlds
- Confidence: high
- Original locator: `EventHandlerEntity.playerLoad/playerSave`; `ResearchManager.loadPlayerData/savePlayerData`; `PlayerKnowledge.setWarpPerm/setWarpSticky/setWarpTemp`.
- Port locator: `src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java:674-700`; `src/main/java/thaumcraft/common/lib/capabilities/PlayerKnowledgeCapability.java:323-373`; `src/main/java/thaumcraft/common/lib/capabilities/PlayerKnowledgeProvider.java:49-78`.
- Original: Player load resolves UUID/current, legacy UUID, and pre-1.7 `.thaum` files plus `.thaumback`. `ResearchManager.loadPlayerData` imports `Thaumcraft.eldritch`, `Thaumcraft.eldritch.temp`, `Thaumcraft.eldritch.sticky`, and `Thaumcraft.eldritch.counter`. For legacy data lacking a sticky key, it splits the old Eldritch value equally between permanent and sticky. Save writes the same four keys.
- Port: Player load grants auto-unlock research but never resolves or parses TC4 `.thaum`/`.thaumback` files and contains no references to the four original keys. Current values persist only in `ForgeCaps/thaumcraft:player_knowledge` as `warpPerm`, `warpSticky`, `warpTemp`, and `warpCounter`.
- Visible effect: A player opening a TC4 world in the port can lose the checker-visible permanent, sticky, and temporary bands because the current capability begins at zero. The effect also extends beyond this HUD, but this finding is limited to the checker's displayed runtime state.
- Migration candidate:
  - Add a one-time, server-side importer at the player-file load boundary.
  - Reuse TC4's UUID/current, legacy UUID, and backup file resolution rather than scanning arbitrary files.
  - Read the exact four `Thaumcraft.eldritch*` integer keys and apply them through capability setters.
  - Preserve TC4's legacy no-sticky split: halve the old value and assign that half to both permanent and sticky.
  - Never overwrite a capability that already has authoritative 1.12 data; persist an explicit migration decision/marker so a later zero-warp state is not re-imported.
  - Let the existing join `PacketSyncWarp` path synchronize the imported result rather than adding a second client protocol.
  - Add fixtures for current UUID, legacy UUID, backup fallback, no-sticky conversion, existing-capability precedence, and repeat-login idempotence.
- Regression hazards: Preserve current ForgeCaps keys and normal 1.12 persistence. Do not repeatedly resurrect warp intentionally reduced to zero. Do not edit or delete the source `.thaum` file until a separately approved migration policy requires it.
- Test gap: No TC4 `.thaum` fixture-to-capability migration test exists.

### A-015-F02 - The port adds a right-click action absent from TC4

- Type: behavior divergence
- Severity: medium
- Confidence: high
- Original locator: Exact `ItemSanityChecker.class`, full CFR and `javap -p -c` method surface.
- Port locator: `src/main/java/thaumcraft/common/items/relics/ItemSanityChecker.java:30-42`.
- Original: The class defines only construction, icon registration/access, creative-subitem population, and uncommon rarity. It does not override the inherited item-use/right-click method, produce text, read player knowledge, or report warp numerically.
- Port: `onItemRightClick` reads permanent, sticky, and temporary server capability values; sums them; sends one action-bar `tc.sanity` message; and returns `EnumActionResult.SUCCESS`.
- Exact delta: In 1.12 the inherited `Item.onItemRightClick` result would be `PASS` with no effect. The port instead returns `SUCCESS` and emits `Current Warp: <total>` from either hand.
- Visible effect: Right-clicking the checker displays a numerical total that TC4 never displayed and consumes the interaction as successful. An offhand checker can produce this text even though only a selected main-hand checker activates the gauge.
- Client/server semantics: Message generation is server-authoritative because it runs only under `!world.isRemote`. It does not mutate warp or stack state.
- Test conflict: `ItemSanityCheckerRuntimeTest.rightClickEmitsOneActionbarMessageInsteadOfOverwritingIt` and `ItemSanityCheckerStaticGuardTest.sanityCheckerKeepsSingleTotalWarpMessageContract` positively enforce the added behavior rather than TC4 parity.
- Candidate disposition: Remove the override for strict TC4 runtime parity, subject to product-owner adjudication if the numerical action is an intentional port extension.

### A-015-F03 - The HUD has an extra 0.625 scale transform

- Type: visual behavior divergence
- Severity: medium
- Confidence: high
- Original locator: `ClientTickEventsFML.renderSanityHud`; `UtilsFX.drawTexturedQuad`.
- Port locator: `src/main/java/thaumcraft/client/lib/RenderEventHandler.java:297-360`; especially line 330; `src/main/java/thaumcraft/client/lib/UtilsFX.java:133-143`.
- Original: After establishing scaled GUI coordinates, the method draws all gauge quads directly. There is no scale call around the sanity gauge.
- Port: The equivalent quad sequence is wrapped by `GlStateManager.scale(0.625F, 0.625F, 1.0F)`.
- Exact visible delta: The nominal `20 x 76` gauge becomes `12.5 x 47.5` GUI pixels. The eight-pixel fill width becomes five GUI pixels. Its `(1,1)` origin is also scaled to `(0.625,0.625)` before rasterization.
- Preserve controls: Quad coordinates, UVs, colors, category order, fill calculations, and source texture are otherwise exact; those controls are enumerated below.
- Test conflict: `SanityCheckerHudStaticGuardTest` explicitly requires the non-original `0.625F` scale.
- Candidate disposition: Remove only the extra scale while retaining the current Forge overlay matrix/state setup.
- Test gap: No framebuffer or screenshot assertion checks the actual GUI dimensions.

### A-015-F04 - The port draws the gauge before Forge text instead of after the game overlay

- Type: render-order divergence
- Severity: low
- Confidence: high
- Original locator: `ClientTickEventsFML.renderTick`, `TickEvent.RenderTickEvent` phase `END`; `renderSanityHud`.
- Port locator: `src/main/java/thaumcraft/client/lib/RenderEventHandler.java:284-295`; Forge 1.12 `GuiIngameForge.renderHUDText`.
- Original: `renderSanityHud` runs from render-tick phase `END`, after the ordinary in-game overlay has rendered.
- Port: `renderNotifications(RenderGameOverlayEvent.Text)` draws notifications, then the sanity gauge, then wand HUD content while Forge is inside `renderHUDText`. After subscribers return, Forge draws the event's left/right demo and F3 debug strings.
- Visible effect: Debug/demo text can be drawn over the port gauge. TC4's later render-tick draw puts the gauge over already-rendered overlay text.
- Candidate disposition: Use a 1.12 overlay hook whose ordering is after ordinary overlay text while preserving once-per-frame behavior and Forge-safe GL state restoration.
- Test gap: No visual overlap test runs with F3/debug or demo text enabled.

### A-015-F05 - Capability NBT deserialization bypasses warp clamping

- Type: robustness/semantic divergence
- Severity: low
- Confidence: high for malformed or edited NBT; normal gameplay writes nonnegative values
- Original locator: `ResearchManager.loadPlayerData`; `PlayerKnowledge.setWarpPerm/setWarpSticky/setWarpTemp`.
- Port locator: `src/main/java/thaumcraft/common/lib/capabilities/PlayerKnowledgeCapability.java:61-109,337-373`; `src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncWarp.java:54-68`.
- Original: Loaded integers are passed through setters, each of which clamps with `Math.max(0, amount)`.
- Port: Public setters clamp, and client packet application uses those setters, but `deserializeNBT` assigns all four integers directly from NBT.
- Visible effect: Corrupt or manually edited ForgeCaps data can leave negative server warp. The added right-click server calculation can then report a negative total, while the client copy received through `PacketSyncWarp` clamps each category to zero and renders an empty gauge. This creates a server/client discrepancy not possible through the original load path.
- Candidate disposition: Route deserialization through setters or clamp each integer during load, including `warpCounter`.
- Test gap: `PlayerKnowledgeCapabilityTest.negativeWarpAndRunicValuesClampToZero` checks public setters, not negative serialized input.

## Positive Parity Controls

### A-015-PC01 - Item identity, limits, rarity, durability, and stack NBT

- Both implementations use stack limit 1, no subtypes, maximum damage 0, uncommon rarity, and the Thaumcraft creative tab.
- The port's additional `setNoRepair()` is inert because an item with max damage 0 is not damageable or repairable in normal item semantics.
- Neither implementation consumes durability, mutates metadata, or reads/writes checker-stack NBT.
- TC4 explicitly contributes one checker stack to its creative subitem list. The port's ordinary non-subtyped item registration supplies the equivalent single creative stack.

### A-015-PC02 - Equip, hand, perspective, and update conditions

- The gauge requires the selected main-hand/current hotbar stack to be `ItemSanityChecker` on both sides. Merely carrying it, placing it in armor slots, or holding it offhand does not activate the gauge.
- Neither side checks `thirdPersonView`; the HUD therefore remains active in both first- and third-person camera modes.
- Neither side uses a 20 Hz item tick for the gauge. Both recalculate and draw from current synchronized knowledge once per rendered HUD frame.
- Both require the normal focused in-game HUD path. The exact platform hooks differ as described in A-015-F04, but no evidence shows an intended use-key, sneak, armor, bauble, or active-use condition in TC4.
- No custom first-/third-person renderer exists for the checker on either side. TC4 uses its registered 16x16 icon and vanilla item rendering; the port uses `item/generated` with the same texture and no TEISR/custom perspective model.

### A-015-PC03 - Warp categories, totals, scaling, and band geometry

- Both read exactly permanent, sticky, and temporary warp. Equipment warp and `warpCounter` do not participate in the checker gauge.
- Total is exactly `permanent + sticky + temporary` on both sides.
- If total exceeds 100, both set component scale to `100 / originalTotal` and clamp displayed total to 100.
- Both compute the empty top gap as integer truncation of `((100 - displayedTotal) / 100) * 48`.
- Both compute temporary height as integer truncation of `(temporary / 100) * 48 * componentScale` and sticky height equivalently.
- Both draw temporary first from the gap, sticky immediately after temporary, and permanent from the cumulative offset through coordinate 48. Permanent therefore receives the remaining displayed band rather than a separately rounded height.
- Zero-valued categories skip their colored quad. Total zero still draws the empty frame. The overflow icon appears exactly when displayed total is at least 100, including an exact total of 100.

### A-015-PC04 - HUD texture regions, icons, and colors

- Backing quad: destination `(1,1)`, UV `(152,0)`, size `20 x 76`.
- Colored fill samples texture U 200 and width 8, with the same dynamic V/height coordinates on both sides.
- Temporary color: RGBA `(1.0, 0.5, 1.0, 1.0)`.
- Sticky color: RGBA `(0.75, 0.0, 0.75, 1.0)`.
- Permanent color: RGBA `(0.5, 0.0, 0.5, 1.0)`.
- Foreground/frame quad: destination `(1,1)`, UV `(176,0)`, size `20 x 76`.
- Overflow/top icon: destination `(1,1)`, UV `(216,0)`, size `20 x 16`.
- Port and original `textures/gui/hud.png` have identical SHA-256 `c55711b7bc8bb853809650f36da2775077cac7d513b8d7a94a290a4f23146175`.

### A-015-PC05 - Item texture/model content

- Port and original `textures/items/sanitychecker.png` have identical SHA-256 `fb0423752d1807f747d25c8da579654eba0018c133d92f44b35f75d06290befa`.
- Both source textures are 16x16. The port JSON uses `item/generated` and `thaumcraft:items/sanitychecker`, the direct 1.12 equivalent of TC4's registered `thaumcraft:sanitychecker` icon.
- Inventory, dropped, first-person, and third-person views use normal flat/generated-item rendering. Exact cross-version vanilla hand transforms were not treated as a defect without manual visual evidence.

### A-015-PC06 - Text and input surface

- The original gauge itself contains no labels, category names, numbers, or font rendering. The port gauge also contains no text.
- No TC4 key binding, mouse polling, held-use duration, or item-use animation is associated with the checker.
- The only text/input delta is the added port right-click action in A-015-F02; `tc.sanity.detail` is unused by the audited runtime path.

### A-015-PC07 - Client/server warp synchronization during normal play

- TC4 stores the three categories in `PlayerKnowledge` and updates the client with category-specific `PacketSyncWarp` packets.
- The port stores them in `IPlayerKnowledge` capability state and sends all categories together in `PacketSyncWarp` on join and warp mutation.
- The packet shape differs as a platform adaptation, but normal nonnegative permanent/sticky/temporary values displayed by the gauge are semantically equivalent.
- HUD rendering reads only local client state on both sides. It does not request data or mutate authoritative server state per frame.

### A-015-PC08 - Normal 1.12 persistence semantics

- Within newly created 1.12 worlds, Forge capability serialization preserves all three warp categories and the counter across save/load.
- Capability clone handling copies the serialized state on player recreation, and join synchronization sends the resulting categories to the local client.
- A-015-F01 concerns importing pre-existing TC4 custom files, not a failure of ordinary 1.12 ForgeCaps round trips.

## Legitimate Platform Adaptations

- TC4 icon registration is replaced by a 1.12 generated item model using the copied original texture.
- TC4 username-keyed `PlayerKnowledge` maps are replaced by a player capability.
- TC4 category-at-a-time warp packets are replaced by one packet carrying permanent, sticky, temporary, and counter values.
- 1.12 `RenderGameOverlayEvent` and GL state management may replace TC4's direct projection setup, but this does not justify changing gauge size, arithmetic, UVs, colors, or relative overlay order.
- 1.12 offhand support does not imply that offhand possession should activate a TC4 main-hand HUD.

## Assumptions, Unknowns, and Gaps

- The extracted oracle classes are assumed authoritative because their SHA-256 values matched the same class entries streamed from `Thaumcraft-1.7.10-4.2.3.5.jar`.
- Obfuscated TC4 methods were adjudicated with both CFR 0.152 and `javap` bytecode. Missing 1.7.10 dependency warnings did not obscure any cited method.
- The migration finding assumes the port is expected to open TC4 player worlds with their prior warp. If cross-version player-data migration is explicitly unsupported, A-015-F01 should be retained as a documented limitation rather than implemented silently.
- It is unknown whether this WIP port has already persisted zero/nonzero ForgeCaps beside old `.thaum` files. Migration precedence therefore requires explicit one-time semantics.
- No manual client run validated actual gauge dimensions, layer overlap, generated-item hand pose, or first-/third-person visual appearance.
- No negative-NBT runtime fixture proved the server/client disagreement in-game; the direct assignment and packet-setter paths prove the state divergence statically.
- No test simulates F3/demo overlay overlap.
- Existing sanity tests are implementation guards, not independent TC4 oracle tests; two explicitly preserve A-015-F02 and A-015-F03.
- Recipe and research metadata were excluded as required.

## Commands and Results

All commands were run from the repository root. Original artifacts were not modified.

```text
git status --short
jar tf Thaumcraft-1.7.10-4.2.3.5.jar | grep -Ei 'sanity|render.*event|event.*render|hud|client.*tick|tick.*client|key|input|player.*tick'
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/items/relics/ItemSanityChecker.class --silent true
javap -classpath Thaumcraft-1.7.10-4.2.3.5.jar -p -c -constants thaumcraft.common.items.relics.ItemSanityChecker
javap -classpath Thaumcraft-1.7.10-4.2.3.5.jar -p thaumcraft.client.lib.ClientTickEventsFML
/usr/local/bin/cfr thaumcraft_src/thaumcraft/client/lib/ClientTickEventsFML.class --methodname renderTick --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/client/lib/ClientTickEventsFML.class --methodname renderSanityHud --silent true
javap -classpath Thaumcraft-1.7.10-4.2.3.5.jar -p -c thaumcraft.client.lib.ClientTickEventsFML
/usr/local/bin/cfr thaumcraft_src/thaumcraft/client/lib/UtilsFX.class --methodname drawTexturedQuad --silent true
javap -classpath Thaumcraft-1.7.10-4.2.3.5.jar -p -c thaumcraft.client.lib.UtilsFX
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/lib/research/PlayerKnowledge.class --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/lib/network/playerdata/PacketSyncWarp.class --silent true
javap -classpath Thaumcraft-1.7.10-4.2.3.5.jar -p -c thaumcraft.common.lib.network.playerdata.PacketSyncWarp
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/lib/events/EventHandlerEntity.class --methodname playerLoad --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/lib/events/EventHandlerEntity.class --methodname playerSave --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/lib/research/ResearchManager.class --methodname loadPlayerData --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/lib/research/ResearchManager.class --methodname savePlayerData --silent true
javap -classpath Thaumcraft-1.7.10-4.2.3.5.jar -p -c thaumcraft.common.config.ConfigItems
javap -classpath Thaumcraft-1.7.10-4.2.3.5.jar -p -c thaumcraft.client.ClientProxy
jdeps -verbose:class Thaumcraft-1.7.10-4.2.3.5.jar
javap -classpath /home/stfu/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeBin-1.12.2-14.23.5.2847.jar -p -c net.minecraftforge.client.GuiIngameForge
javap -classpath /home/stfu/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeBin-1.12.2-14.23.5.2847.jar -p -c net.minecraft.client.renderer.EntityRenderer
javap -classpath /home/stfu/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeBin-1.12.2-14.23.5.2847.jar -p -c net.minecraft.client.Minecraft
javap -classpath /home/stfu/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeBin-1.12.2-14.23.5.2847.jar -p -c net.minecraft.item.Item
javap -classpath /home/stfu/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeBin-1.12.2-14.23.5.2847.jar -p -c net.minecraft.item.ItemStack
sha256sum thaumcraft_src/thaumcraft/common/items/relics/ItemSanityChecker.class thaumcraft_src/thaumcraft/client/lib/ClientTickEventsFML.class thaumcraft_src/thaumcraft/common/lib/research/PlayerKnowledge.class thaumcraft_src/thaumcraft/common/lib/network/playerdata/PacketSyncWarp.class
unzip -p Thaumcraft-1.7.10-4.2.3.5.jar thaumcraft/common/items/relics/ItemSanityChecker.class | sha256sum
unzip -p Thaumcraft-1.7.10-4.2.3.5.jar thaumcraft/client/lib/ClientTickEventsFML.class | sha256sum
unzip -p Thaumcraft-1.7.10-4.2.3.5.jar thaumcraft/common/lib/research/PlayerKnowledge.class | sha256sum
unzip -p Thaumcraft-1.7.10-4.2.3.5.jar thaumcraft/common/lib/network/playerdata/PacketSyncWarp.class | sha256sum
unzip -p Thaumcraft-1.7.10-4.2.3.5.jar assets/thaumcraft/textures/gui/hud.png | sha256sum
unzip -p Thaumcraft-1.7.10-4.2.3.5.jar assets/thaumcraft/textures/items/sanitychecker.png | sha256sum
sha256sum src/main/resources/assets/thaumcraft/textures/gui/hud.png src/main/resources/assets/thaumcraft/textures/items/sanitychecker.png
identify src/main/resources/assets/thaumcraft/textures/gui/hud.png src/main/resources/assets/thaumcraft/textures/items/sanitychecker.png
```

- Result: Five evidence-backed divergences were established. All remaining audited behavior is covered by A-015-PC01 through A-015-PC08.
- Class provenance: All four explicitly hashed extracted classes matched their exact jar entries.
- Asset provenance: Both audited port textures matched the exact TC4 jar assets byte-for-byte.
- Tests/build: Not run for the read-only audit. This materialization changes documentation only and makes no product compile/runtime claim.
- Runtime smoke: Not required and not run because no product/runtime path changed.
- Manual visual validation: Not run.

## Handoff

- Terminal status: complete
- Finding index: A-015-F01 high TC4 warp-file migration gap/candidate; A-015-F02 medium added right-click total/action success; A-015-F03 medium extra HUD scale 0.625; A-015-F04 low pre-text versus post-overlay render order; A-015-F05 low raw NBT clamping bypass.
- Positive parity index: A-015-PC01 item state; A-015-PC02 equip/perspective/update; A-015-PC03 category/math/geometry; A-015-PC04 UV/icon/color/assets; A-015-PC05 generated item visual path; A-015-PC06 no original text/input; A-015-PC07 normal client/server synchronization; A-015-PC08 normal ForgeCaps persistence.
- Exact continuation point: Orchestrator may normalize the five findings, eight preserve controls, migration-policy assumption, and test gaps into central entries. No central ledger was edited by A-015.
