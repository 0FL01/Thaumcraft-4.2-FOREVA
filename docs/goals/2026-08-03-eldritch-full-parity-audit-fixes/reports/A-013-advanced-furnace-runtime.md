# Audit Packet: A-013 - Advanced Alchemical Furnace runtime

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Assignment-ID: A-013
Status: complete
Report-Revision: 1
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Compare the Advanced Alchemical Furnace runtime against Thaumcraft 4.2.3.5: relevant `BlockAlchemyFurnace` metadata, `TileAlchemyFurnaceAdvanced`, `TileAlchemyFurnaceAdvancedNozzle`, any slave/related tile, directly required basic-furnace backend, `ContainerAlchemyFurnace`, `GuiAlchemyFurnace`, and `WandManager` formation. Audit formation and teardown, inventory, smelting/aspects/vis/fuel, essentia transport and suction, synchronization and persistence, automation, activation, drops, interaction, and rendered state.
- Anti-scope: Recipe definitions, research declaration metadata, unrelated furnace implementations, unrelated vis-network internals, broad rendering review beyond state supplied by this runtime, product edits, and central Goal Ledger edits.
- Oracle and comparison direction: S-003 Thaumcraft 4.2.3.5 bytecode -> S-005 Forge 1.12.2 port.
- Questions: Do the input and formed layouts, metadata mapping, teardown, drops, processing costs, cooldown, vis charging, essentia output, inventory and shift-click routing, sided automation, GUI state, NBT and packets, comparator/light/render state, and interactions match? Which differences are required 1.12 adaptations or intentional safety/data-preservation changes?
- Expected evidence: Exact CFR decompilation of every original target class/method, independent bytecode adjudication where CFR precedence was ambiguous, current source comparison, and inspection of focused tests.
- Read/write permissions: Product files and central ledger files read-only; only this report writable.
- Effort/tool budget: Targeted static audit and focused existing test execution; no product modification and no client smoke.
- Stop conditions: Every named runtime surface is compared; every material delta has exact both-side evidence, impact, reproduction, confidence, hazards, and test gap; positive parity and intentional adaptations are explicit; no product path is changed.
- Continuation predecessor: none.

## Source Anchors

- S-003: `Thaumcraft-1.7.10-4.2.3.5.jar`, SHA-256 `3dba9786966974701578a658d1bb369bf35bdf5f363079f5ac9c4910a39113be`.
- S-005: branch `master`, baseline HEAD `a1a2973e4fd4b38b4e49789391ecc4292c998373`.
- Exact TC4 classes decompiled:
  - `thaumcraft/common/blocks/BlockAlchemyFurnace.class`
  - `thaumcraft/common/tiles/TileAlchemyFurnaceAdvanced.class`
  - `thaumcraft/common/tiles/TileAlchemyFurnaceAdvancedNozzle.class`
  - `thaumcraft/common/tiles/TileAlchemyFurnace.class`
  - `thaumcraft/common/container/ContainerAlchemyFurnace.class`
  - `thaumcraft/client/gui/GuiAlchemyFurnace.class`
  - `thaumcraft/client/renderers/tile/TileAlchemyFurnaceAdvancedRenderer.class`
  - `thaumcraft/common/items/wands/WandManager.class`
  - directly required `thaumcraft/common/blocks/BlockStoneDevice.class` and `thaumcraft/api/visnet/VisNetHandler.class`
- No `TileAlchemyFurnaceAdvancedSlave` or other advanced-furnace slave class exists in S-003 or the port. The only proxy component is `TileAlchemyFurnaceAdvancedNozzle`.

## Coverage Performed

- Port gameplay/runtime files inspected:
  - `src/main/java/thaumcraft/common/blocks/BlockAlchemyFurnace.java`
  - `src/main/java/thaumcraft/common/blocks/BlockStoneDevice.java`
  - `src/main/java/thaumcraft/common/tiles/TileAlchemyFurnaceAdvanced.java`
  - `src/main/java/thaumcraft/common/tiles/TileAlchemyFurnaceAdvancedNozzle.java`
  - `src/main/java/thaumcraft/common/tiles/TileAlchemyFurnace.java`
  - `src/main/java/thaumcraft/common/tiles/TileAlembic.java`
  - `src/main/java/thaumcraft/common/tiles/TileBellows.java`
  - `src/main/java/thaumcraft/common/container/ContainerAlchemyFurnace.java`
  - `src/main/java/thaumcraft/client/gui/GuiAlchemyFurnace.java`
  - `src/main/java/thaumcraft/common/items/wands/WandManager.java`
  - `src/main/java/thaumcraft/api/TileThaumcraft.java`
  - `src/main/java/thaumcraft/api/visnet/VisNetHandler.java`
- Port client/state files inspected:
  - `src/main/java/thaumcraft/client/renderers/tile/TileAlchemyFurnaceAdvancedRenderer.java`
  - `src/main/java/thaumcraft/client/ClientProxy.java`
  - `src/main/java/thaumcraft/common/CommonProxy.java`
  - `src/main/java/thaumcraft/common/config/ConfigBlocks.java`
- Existing tests inspected:
  - `TileAlchemyFurnaceAdvancedRuntimeTest`
  - `TileAlchemyFurnaceSyncRuntimeTest`
  - `VisNetworkChargingRuntimeTest`
  - `WandManagerAdvancedAlchemyFurnaceRuntimeTest`
  - `AdvancedAlchemyFurnaceCommonBackendStaticGuardTest`
  - `ContainerAlchemyFurnaceLayoutContractTest`
  - `AlchemyFurnaceAdvancedRendererContractTest`
  - `AlchemyFurnaceAdvancedRendererFidelityStaticGuardTest`
  - `BlockStoneDeviceContractTest`
- Uncovered evidence: No manual in-game GUI, automation, formation-FX, or TESR observation was performed. No runtime scale/profile measurement was made for synchronization frequency.

## Atomic Findings

### A-013-F01 - Basic furnace GUI progress fields are never synchronized by its container

- Type: defect
- Severity: high
- Confidence: high
- Source/oracle locator: S-003 `ContainerAlchemyFurnace.class`, CFR methods `func_75132_a`, `func_75142_b`, and `func_75137_b`; S-005 `src/main/java/thaumcraft/common/container/ContainerAlchemyFurnace.java:12-74`, `src/main/java/thaumcraft/client/gui/GuiAlchemyFurnace.java:38-50`, and `src/main/java/thaumcraft/common/tiles/TileAlchemyFurnace.java:410-435`.
- Observed: The port container creates the two furnace slots and player inventory, but has no `addListener`, `detectAndSendChanges`, or `updateProgressBar` implementation. `TileAlchemyFurnace` exposes five fields through `getField`/`setField`, but no container code sends them. The GUI reads `furnaceBurnTime`, `currentItemBurnTime`, `furnaceCookTime`, `smeltTime`, and `vis` directly from the client tile.
- Expected: TC4 sends all five values when a crafter/listener joins and sends each changed value from `detectAndSendChanges`; the client applies each progress-bar update to the tile.
- Exact deltas:
  - TC4 field ID 0: `furnaceCookTime`; port sends nothing.
  - TC4 field ID 1: `furnaceBurnTime`; port sends nothing.
  - TC4 field ID 2: `currentItemBurnTime`; port sends nothing.
  - TC4 field ID 3: `vis`; port sends nothing.
  - TC4 field ID 4: `smeltTime`; port sends nothing.
  - Port tile field IDs use a different internal order, which is valid if the container consistently uses that order, but no sender exists.
- Impact/reproduction: Open a basic alchemical furnace while it smelts. The cook and burn gauges use the initial chunk/tile snapshot and sporadic block-update packets rather than per-tick container properties, so progress remains stale and jumps instead of advancing normally. The advanced furnace has no GUI; this defect affects the basic core used before formation.
- Regression hazards: Preserve the original slot coordinates, GUI texture, five values, 16-bit-safe values, interaction distance, and 1.12 `IContainerListener` API. Do not depend on broad tile update packets as a substitute for window properties.
- Test gap: `ContainerAlchemyFurnaceLayoutContractTest` checks slot coordinates only. No listener/property runtime test exists.
- Candidate disposition: required.

### A-013-F02 - Shift-click prioritizes aspect input over fuel

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-003 `ContainerAlchemyFurnace.class`, CFR `func_82846_b`; S-005 `src/main/java/thaumcraft/common/container/ContainerAlchemyFurnace.java:50-72`, `src/main/java/thaumcraft/common/tiles/TileAlchemyFurnace.java:382-390`, and `src/main/java/thaumcraft/common/config/ConfigAspects.java:179`.
- Observed: For a player-inventory stack, the port first asks whether slot 0 accepts the stack and merges there; it tests `TileAlchemyFurnace.isItemFuel` only if slot 0 rejects it.
- Expected: TC4 checks fuel first, attempts fuel slot 1, and only falls back to input slot 0 if the fuel merge fails. Non-fuel aspect-bearing stacks go directly to input slot 0.
- Exact deltas: Coal is both valid fuel and explicitly tagged with `ENERGY 2` in the port. TC4 shift-click routes it to slot 1 first; the port's `isItemValidForSlot(0, coal)` succeeds and routes it to slot 0, so the later fuel branch is unreachable.
- Impact/reproduction: Put coal in player inventory and shift-click it with an empty furnace. It enters the ingredient slot instead of the fuel slot and does not start normal furnace burning.
- Regression hazards: Preserve TC4's fuel-first order and fallback to input when the fuel slot cannot accept the stack. Preserve player-main/hotbar transfer ranges and empty-stack handling.
- Test gap: No furnace container runtime test exercises shift-click routing for an item that is both fuel and aspect-bearing.
- Candidate disposition: required.

### A-013-F03 - Bottom-face fuel insertion is exposed but rejected

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-003 `TileAlchemyFurnace.class`, CFR `func_94128_d`, `func_102007_a`, and `func_102008_b`; S-005 `src/main/java/thaumcraft/common/tiles/TileAlchemyFurnace.java:24-26,393-406`.
- Observed: The port exposes fuel slot 1 for `EnumFacing.DOWN`, no slots for UP, and ingredient slot 0 for horizontal faces. It then rejects every insertion where `direction == EnumFacing.DOWN`.
- Expected: TC4 exposes the same slot sets but rejects insertion only from side index 1, UP. Fuel insertion into exposed slot 1 from side index 0, DOWN, is accepted when the stack is fuel.
- Exact deltas:
  - Accessible slots: parity - DOWN `[1]`, UP `[]`, horizontal `[0]`.
  - Insertion gate: TC4 `side != UP && isItemValidForSlot`; port `side != DOWN && isItemValidForSlot`.
  - Extraction gate remains parity: extraction from the DOWN fuel slot is limited to buckets; other faces/slots use the original condition.
- Impact/reproduction: Query `getSlotsForFace(DOWN)` and then call `canInsertItem(1, fuel, DOWN)`. The port returns false for the exact slot it advertises. Addon pipes and sided inventory helpers cannot reproduce TC4 bottom fuel input.
- Regression hazards: Do not expose a new top slot, alter horizontal ingredient input, or broaden bottom extraction beyond the original bucket exception.
- Test gap: No `TileAlchemyFurnace` sided-inventory test covers accessible slots plus insertion/extraction as a matrix.
- Candidate disposition: required.

### A-013-F04 - Advanced heat omits TC4's unconditional decay at zero and negative values

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-003 `TileAlchemyFurnaceAdvanced.class`, CFR `func_145845_h`; S-005 `src/main/java/thaumcraft/common/tiles/TileAlchemyFurnaceAdvanced.java:88-109` and `src/test/java/thaumcraft/common/tiles/VisNetworkChargingRuntimeTest.java:222-240`.
- Observed: Every fifth server tick the port decrements heat only when `heat > 0`, then drains up to 50 FIRE vis when `heat <= maxPower`.
- Expected: TC4 executes `int pt = this.heat--;` unconditionally before attempting FIRE vis drain.
- Exact deltas:
  - Starting at heat 0 with four FIRE vis available: TC4 becomes `-1 + 4 = 3`; the port becomes `0 + 4 = 4`.
  - Starting at heat 0 with no FIRE vis: TC4 becomes -1 per five-tick cycle and accumulates negative heat debt; the port remains 0.
  - When vis later returns, TC4 must repay the negative heat debt before becoming usable; the port charges immediately from zero.
  - Positive heat decay, FIRE/ENTROPY/WATER drain requests of 50, maximum checks, process costs, and max values otherwise match.
- Impact/reproduction: Starve a formed furnace of FIRE vis for 500 ticks, then reconnect a source. TC4 begins at approximately -100 heat and must recover; the port begins at 0. Even a newly powered furnace differs by one heat on its first charge cycle.
- Regression hazards: Preserve the five-tick cadence, separate three-primal drains, `<= maxPower` overfill behavior, and server-only mutation. This is odd but explicit TC4 gameplay, not a required 1.12 API change.
- Test gap/current conflict: `VisNetworkChargingRuntimeTest` explicitly expects port heat 4 after the first powered cycle while expecting power1/power2 4. A TC4 parity correction must change that heat expectation to 3 and add a starvation/recovery case.
- Candidate disposition: required.

### A-013-F05 - Charging synchronization is much broader than TC4

- Type: benign_delta
- Severity: medium
- Confidence: high for code delta; medium for production performance impact
- Source/oracle locator: S-003 `TileAlchemyFurnaceAdvanced.class`, CFR `func_145845_h` and `onDataPacket`; S-005 `src/main/java/thaumcraft/common/tiles/TileAlchemyFurnaceAdvanced.java:88-109,143-184` and `src/main/java/thaumcraft/api/TileThaumcraft.java:29-50`.
- Observed: The port calls `sync()` whenever heat, power1, or power2 changes. `sync()` marks the chunk dirty and sends a tile/block update, normally once every five ticks while any reservoir charges. It relights only when `BlockAlchemyFurnace.getHeatLight` changes.
- Expected: TC4 records pre-decay heat as `pt`, changes all three reservoirs, and calls `markBlockForUpdate` only when `pt / 50 != heat / 50`. Power-only changes do not independently trigger that packet. TC4's packet callback relights the block.
- Exact deltas: Port synchronization trigger is any of three integer changes; TC4 trigger is a heat 50-bucket transition. The port can therefore send approximately four updates per second per actively charging furnace, while TC4 sends only at heat bucket transitions. Port relighting uses the exact rendered light boundary instead of every 50 heat.
- Impact/reproduction: Attach a steady vis source and count `notifyBlockUpdate` plus dirty marks for several seconds. The port updates every charge cycle even after heat is stable when either secondary reservoir still changes. This improves persistence and client freshness but can create avoidable network/chunk-dirty load at scale.
- Regression hazards: The goal charter explicitly preserves explicit block/tile synchronization. Do not remove dirty marking or leave clients stale merely to copy TC4's weak persistence. Any cadence reduction needs focused save/reload, comparator, light, and TESR evidence.
- Test gap: `VisNetworkChargingRuntimeTest.TestAdvancedAlchemyFurnace` overrides `sync()` to no-op, so it cannot detect packet/dirty cadence. No scale/profile evidence exists.
- Intentional delta classification: 1.12 synchronization/persistence hardening; preserve semantics, adjudicate cadence separately rather than treating the TC4 under-sync as automatically desirable.
- Candidate disposition: preserve as an adaptation; defer performance optimization absent measured impact.

### A-013-F06 - Formation preserves stored aspects that TC4 discards

- Type: benign_delta
- Severity: medium
- Confidence: high
- Source/oracle locator: S-003 `WandManager.class`, CFR `createAdvancedAlchemicalFurnace`; S-003 `BlockStoneDevice.class`, CFR `func_149749_a`; S-005 `src/main/java/thaumcraft/common/items/wands/WandManager.java:1061-1120` and `src/test/java/thaumcraft/common/items/wands/WandManagerAdvancedAlchemyFurnaceRuntimeTest.java:58-81`.
- Observed: Before replacing the basic center, the port writes its custom NBT, removes `Items`, separately takes its two inventory stacks, creates the advanced tile, reads source data into it, and transfers or drops the inventory. The shared `Aspects` tag therefore migrates into the advanced furnace and `vis` is recomputed from it.
- Expected: TC4 directly replaces the basic center block. `BlockStoneDevice` drops the two inventory stacks, but the basic tile's internal aspect list is not an inventory and is lost.
- Exact deltas: Up to 50 stored essentia changes from discarded in TC4 to preserved in the port. Input and fuel stacks are dropped in both successful formation paths; the port's explicit transfer/drop logic additionally prevents loss or duplication during replacement/failure rollback.
- Impact/reproduction: Fill a basic furnace with seven FIRE essentia, form the advanced furnace, and inspect its contents. TC4 starts empty; the port retains seven FIRE. The existing runtime test explicitly requires the migrated value.
- Regression hazards: Preserve no-duplication behavior, source inventory drops, rollback on failed center replacement, and the fact that basic burn/cook state is not translated into advanced heat/power. Do not blindly copy unrelated basic NBT fields into advanced semantics.
- Test coverage: `upperSourceActivationCreatesDedicatedLayoutAndPreservesCenterContents` locks both aspect preservation and item accounting.
- Intentional delta classification: deliberate player-data preservation, not an API adaptation and not a parity defect.
- Candidate disposition: preserve.

### A-013-F07 - Formation sparkle coverage is reduced from the full shell to two points

- Type: defect
- Severity: low
- Confidence: high for packet delta; medium for exact visual impact without client observation
- Source/oracle locator: S-003 `WandManager.class`, CFR `createAdvancedAlchemicalFurnace`; S-005 `src/main/java/thaumcraft/common/items/wands/WandManager.java:1164-1173`.
- Observed: The port sends `PacketFXBlockSparkle` at the center and `center.up()`, then plays the wand sound.
- Expected: TC4 loops `aa=-1..1`, `bb=0..1`, `cc=-1..1` and sends one sparkle packet at every point in the 3x2x3 volume, 18 packets total, then plays the wand sound.
- Exact deltas: Sparkle packet count 18 -> 2; shell/corner/cardinal points 16 -> 0; center and upper-center points remain; sound volume/pitch and 32-block targeting radius remain equivalent.
- Impact/reproduction: Form the structure while observing from within 32 blocks. The port animation is concentrated at two center points instead of covering the transformed shell.
- Regression hazards: Preserve server-authoritative formation, one sound, packet dimension/radius, and avoid duplicate client-local effects. Do not change layout state to repair a visual packet fan-out.
- Test gap: Formation tests assert states and vis consumption but do not count FX packets. No manual visual validation was run.
- Candidate disposition: required.

### A-013-F08 - Advanced cooldown and pending teardown now survive save and packet round trips

- Type: benign_delta
- Severity: low
- Confidence: high
- Source/oracle locator: S-003 `TileAlchemyFurnaceAdvanced.class`, CFR `readCustomNBT`, `writeCustomNBT`, `func_145839_a`, and `func_145841_b`; S-005 `src/main/java/thaumcraft/common/tiles/TileAlchemyFurnaceAdvanced.java:41-62,73-83` and `src/test/java/thaumcraft/common/tiles/TileAlchemyFurnaceAdvancedRuntimeTest.java:92-126`.
- Observed: The port persists and synchronizes `processed` and `destroy` in addition to aspects, vis, heat, power1, and power2. Breaking a non-center component also marks the center tile dirty after setting `destroy`.
- Expected: TC4 persists aspects, vis, heat, power1, and power2 only. `processed` and `destroy` return to zero/false after reload and are not part of the update packet.
- Exact deltas:
  - Mid-cooldown reload: TC4 clears the remaining item-processing delay; port resumes it.
  - Pending component-triggered teardown across chunk save/unload: TC4 may lose `destroy`; port restores and completes teardown.
  - Client packet payload: port includes both transient fields; TC4 does not.
- Impact/reproduction: Save/reload while `processed > 0`, or unload immediately after setting `destroy`. Port retains lifecycle continuity rather than resetting the operation.
- Regression hazards: Never allow client packet data to authoritatively initiate teardown. Preserve server-side `destroy` handling and one-tick restoration. If packet payload is narrowed, disk persistence must remain explicit.
- Test coverage: `updatePacketRoundTripCarriesAllBackendStateAndExactRenderBounds` intentionally locks both fields in the packet. No actual chunk unload/reload teardown test exists.
- Intentional delta classification: lifecycle/data-integrity hardening; preserve.
- Candidate disposition: preserve.

### A-013-F09 - Basic furnace NBT preserves more state than TC4

- Type: benign_delta
- Severity: low
- Confidence: high
- Source/oracle locator: S-003 `TileAlchemyFurnace.class`, CFR `readCustomNBT`, `writeCustomNBT`, `func_145839_a`, and `func_145841_b`; S-005 `src/main/java/thaumcraft/common/tiles/TileAlchemyFurnace.java:217-262` and `src/test/java/thaumcraft/common/tiles/TileAlchemyFurnaceSyncRuntimeTest.java:40-76`.
- Observed: The port persists `CurrentBurnTime` directly and honors a saved `Vis` value when present, falling back to aspect size only when the key is absent.
- Expected: TC4 custom sync carries `BurnTime` and `Vis`, but full NBT does not write `CurrentBurnTime`; on load it recomputes current burn duration from the current fuel-slot stack and then resets `vis` to the aspect-list size.
- Exact deltas:
  - Active consumed fuel with an empty fuel slot: port reload retains the original total burn duration; TC4 recomputes zero.
  - Legacy/key-only `Vis` without aspect entries: port retains the key; TC4 full load resolves to aspect size zero.
  - Core item stacks, cook time, speed boost, custom name, aspects, burn time, and NBT key names otherwise remain compatible.
- Impact/reproduction: Save/reload a burning furnace after the active fuel item has already been consumed, then inspect GUI burn scaling; or load NBT containing `Vis` but no `Aspects`. The port preserves more exact/legacy state.
- Regression hazards: Preserve legacy NBT compatibility, aspect-list truth during normal writes, and exact original public keys. Do not remove `CurrentBurnTime` without considering existing WIP-port saves.
- Test coverage: `TileAlchemyFurnaceSyncRuntimeTest` intentionally locks key-only `Vis` preservation. It does not test active-fuel reload scaling.
- Intentional delta classification: save compatibility and GUI continuity hardening; preserve.
- Candidate disposition: preserve.

### A-013-F10 - Sneaking no longer suppresses the basic furnace GUI

- Type: defect
- Severity: low
- Confidence: high
- Source/oracle locator: S-003 `BlockStoneDevice.class`, CFR `func_149727_a`; S-005 `src/main/java/thaumcraft/common/blocks/BlockStoneDevice.java:192-200`.
- Observed: When the tile is `TileAlchemyFurnace`, the port opens GUI 9 on the server and returns true regardless of `playerIn.isSneaking()`.
- Expected: TC4 opens GUI 9 only for metadata 0, a `TileAlchemyFurnace`, and `!player.isSneaking()`. Sneaking falls through without opening the furnace GUI.
- Exact deltas: Server sneak + right-click changes no-GUI/pass-through -> GUI open/interaction consumed. Non-sneaking GUI ID, tile position, and server authority match.
- Impact/reproduction: Sneak-right-click a basic alchemical furnace with an empty hand or another interactable item. The port opens the GUI where TC4 suppresses it.
- Regression hazards: Preserve client-side interaction consumption needed by 1.12, server-only GUI creation, GUI ID 9, and normal non-sneaking activation.
- Test gap: `BlockStoneDeviceContractTest` does not cover furnace activation or sneaking.
- Candidate disposition: required.

### A-013-F11 - A mismatched filter prevents an existing alembic aspect from filling

- Type: defect
- Severity: low
- Confidence: high for behavior delta; medium for normal-game reachability
- Source/oracle locator: S-003 `TileAlchemyFurnace.class`, CFR `func_145845_h`; S-005 `src/main/java/thaumcraft/common/tiles/TileAlchemyFurnace.java:102-143` and `src/main/java/thaumcraft/common/tiles/TileAlembic.java:19-55`.
- Observed: During the first alembic pass, the port fills a nonempty alembic only when its filter is null or equals its currently stored aspect.
- Expected: TC4's first pass checks only that the alembic has an aspect, is below capacity, and the furnace contains that same aspect. It does not inspect `aspectFilter` when continuing an existing aspect.
- Exact deltas: For an alembic containing AIR with `aspectFilter == FIRE`, amount below 32, and AIR in the furnace, TC4 transfers one AIR at the normal 20/40-tick interval; the port transfers none. Empty filtered alembics and matching filters retain original behavior.
- Impact/reproduction: Load or construct the mismatched state above and tick the furnace through a transfer interval. The port leaves both stores unchanged. Reachability through ordinary current UI was not established, but the state is NBT-representable and addon-visible.
- Regression hazards: Preserve filter selection for empty alembics, the exclusion list, one-unit transfer cadence, speed-boost interval, vertical chain limit of four alembics, and no loss when `addToContainer` rejects a transfer.
- Test gap: `TileAlchemyFurnaceSyncRuntimeTest` covers null-filter empty and already-filled alembics only; no mismatched-filter case exists.
- Candidate disposition: required for TC4 runtime parity; orchestrator may separately adjudicate persisted/addon-state relevance.

### A-013-F12 - Formed block creative/pick exposure is intentionally removed

- Type: benign_delta
- Severity: low
- Confidence: high
- Source/oracle locator: S-003 `BlockAlchemyFurnace.class`, CFR `func_149666_a`; S-005 `src/main/java/thaumcraft/common/blocks/BlockAlchemyFurnace.java:78-80,225-229`, `src/test/java/thaumcraft/common/tiles/TileAlchemyFurnaceAdvancedRuntimeTest.java:34-52`, and `src/test/java/thaumcraft/common/AdvancedAlchemyFurnaceCommonBackendStaticGuardTest.java:14-35`.
- Observed: The port lists no `BlockAlchemyFurnace` creative variants and returns `ItemStack.EMPTY` from pick block.
- Expected: TC4 adds formed block metadata 0 to the creative sub-block list and relies on normal pick behavior.
- Exact deltas: Creative exposure 1 stack -> 0; middle-click result formed block item -> empty. Component breaking still drops the underlying stone/metal devices with original metadata.
- Impact/reproduction: Search the creative inventory or middle-click a formed furnace. The internal multiblock block cannot be obtained directly in the port.
- Regression hazards: Do not alter teardown drops, registry identity, structure metadata, or wand formation merely to restore an internal creative item. Direct placement could bypass valid formation state.
- Test coverage: Current runtime/static tests explicitly require no listed item and empty pick result.
- Intentional delta classification: formed-structure integrity policy, not an API requirement; preserve unless the central contract explicitly chooses literal creative parity.
- Candidate disposition: preserve.

### A-013-F13 - Formed-layout activation is idempotent and legacy layouts are migrated

- Type: benign_delta
- Severity: low
- Confidence: high
- Source/oracle locator: S-003 `WandManager.class`, CFR `createAdvancedAlchemicalFurnace`; S-005 `src/main/java/thaumcraft/common/items/wands/WandManager.java:891-934,952-1008,1031-1120` and `src/test/java/thaumcraft/common/items/wands/WandManagerAdvancedAlchemyFurnaceRuntimeTest.java:96-162`.
- Observed: The port first recognizes an already formed dedicated layout and returns true without consuming vis. It also recognizes one unambiguous old-port lower-ring layout, validates shell tile data and upper clearance, charges the normal cost, and migrates it.
- Expected: TC4 searches only for a basic stone-device center with the canonical metal rings. An already formed center is not a candidate and the method returns false. TC4 has no provisional-port legacy layout.
- Exact deltas:
  - Already formed valid structure: TC4 false; port true, zero vis, no mutation.
  - Safe legacy lower layout: TC4 no special recognition; port migrates after normal FIRE 50, WATER 50, ORDER 50 payment.
  - Canonical new-build layout, cost, and resulting dedicated metadata remain parity.
- Impact/reproduction: Trigger event 7 on a formed furnace; the port consumes the interaction but not vis. Construct the guarded lower legacy layout; only the port migrates it.
- Regression hazards: Preserve the completed `ADVALCHEMYFURNACE` research gate, zero-charge idempotence, strict legacy safety checks, canonical cost, no duplicate inventory, and rejection of ambiguous/unsafe lower builds.
- Test coverage: Existing runtime tests intentionally lock formed idempotence, canonical activation, filled source alembics, safe lower migration, and rejection of unsafe/customized lower states.
- Intentional delta classification: 1.12 interaction/legacy-save compatibility behavior; preserve.
- Candidate disposition: preserve.

### A-013-F14 - Nozzle resolution and empty-state handling are intentionally hardened

- Type: benign_delta
- Severity: low
- Confidence: high
- Source/oracle locator: S-003 `TileAlchemyFurnaceAdvancedNozzle.class`, CFR full class; S-005 `src/main/java/thaumcraft/common/tiles/TileAlchemyFurnaceAdvancedNozzle.java:12-168` and `src/test/java/thaumcraft/common/tiles/TileAlchemyFurnaceAdvancedRuntimeTest.java:128-147`.
- Observed: The port continuously resolves/revalidates the adjacent advanced center, clears stale references, resolves the outward face, returns null/zero for an empty or absent source, and synchronizes center/comparator state after extraction.
- Expected: TC4 resolves only while `facing == UNKNOWN && furnace == null`, retains the reference afterward, and directly indexes `aspects.getAspects()[0]`. Its `getEssentiaAmount` decompiles to a null conditional that can unbox null when no furnace exists.
- Exact deltas: Stale center reference remains in TC4 but is cleared/re-resolved by the port; empty aspect list can index unsafely in TC4 but returns null/zero in the port; successful extraction marks/synchronizes both implementations, with the port also explicitly updates nozzle comparators.
- Impact/reproduction: Remove/recreate the center beside a surviving nozzle or query an empty/unresolved nozzle. The port remains safe and reconnects instead of exposing stale/null behavior.
- Regression hazards: Preserve the single outward face, output-only contract, zero suction, first-aspect ordering, exact requested-amount removal, no input, and false `renderExtendedTube`.
- Test coverage: Current nozzle runtime test locks empty-state null/zero handling, outward-face output, exact removal, and vis recomputation. It does not test stale center replacement.
- Intentional delta classification: required null/empty-stack-era safety and tile-lifecycle hardening; preserve.
- Candidate disposition: preserve.

## Positive Parity Controls

### A-013-PC01 - Formation blueprint, cost, and formed metadata

- Both sides scan candidate centers within one block of the trigger position and require a basic alchemical furnace center.
- Canonical input lower ring: all eight `blockMetalDevice` meta 3. Canonical upper ring: corner meta 1 and cardinal meta 9. Filled/customized upper alembic tile data does not invalidate the block/meta blueprint.
- Both charge FIRE 50, WATER 50, and ORDER 50 only after a matching canonical structure is found.
- Formed mapping matches exactly: center 0; lower cardinals/nozzles 1; upper corners 2; upper cardinals 3; lower corners 4.
- Port leaves `center.up()` untouched, matching the original 3x2x3 transformation, whose upper-center blueprint position is not transformed.

### A-013-PC02 - Teardown and component drops

- Breaking formed center meta 0 restores all surviving shell positions to original component blocks. The broken center drops basic stone device meta 0.
- Breaking any shell component locates the center within the 3x2x3 structure, flags teardown, and the center restores the remaining shell plus itself on its next server tick.
- Drop mapping matches: formed metas 1/4 -> metal device meta 3; meta 3 -> metal device meta 9; meta 2 -> metal device meta 1; center -> stone device meta 0.
- The port's `restoring` guard prevents recursive 1.12 `breakBlock` callbacks while retaining the same final state. This is a necessary lifecycle adaptation.

### A-013-PC03 - Advanced processing and capacity

- Both accept an `EntityItem` only at center meta 0, process one item per successful collision, shrink exactly one, play the bubble sound, and kill the entity only when its stack becomes empty.
- Both derive object tags plus bonus tags, reject empty/no-aspect input, enforce total capacity `tags.visSize() + aspects.visSize() <= 500`, and have no item inventory.
- Process cost matches: heat `2 * aspect total`, ENTROPY power `aspect total`, WATER power `aspect total`.
- Cooldown formula matches because processing begins only from zero: `5 + max(0, (1 - remainingHeat / 500) * 100)` truncated to int.
- Both add all item aspects, recompute `vis` from the aspect list, and cap stored total at 500 through the precondition.

### A-013-PC04 - Vis charging and rendered state thresholds

- Every five server ticks both request up to 50 FIRE, 50 ENTROPY, and 50 WATER from `VisNetHandler` while the corresponding reservoir is `<= 500`; overfill by a final drain remains possible as in TC4.
- Advanced TESR base texture and fire overlay activate at `heat > 100`; tank texture, liquid gauges, particles, and pop sound activate at `vis > 0`.
- Dynamic block light follows integer `(heat / maxPower) * 12` above 100. The port clamps to the valid 1.12 light range but does not change reachable normal values.
- Render bounds remain exactly center minus 1 X/Z through center plus 2 X/Z and two blocks high.

### A-013-PC05 - Nozzle essentia transport and comparator

- Only formed metadata 1 creates a nozzle tile; metadata 0 creates the advanced center; metas 2/3/4 have no tiles.
- The nozzle connects and outputs only on its outward face, accepts no input, has no suction type or amount, minimum suction 0, and does not render an extended tube.
- It exposes the first aspect in the center `AspectList`, reports that aspect's amount, and removes only when the full requested amount exists.
- Original bytecode adjudication confirms the nozzle comparator is boolean, not a 1..15 fill scale: `floor((vis/maxVis)*14) + vis > 0 ? 1 : 0`. Port `vis > 0 ? 1 : 0` is equivalent.

### A-013-PC06 - Basic furnace inventory, fuel, smelting, and alembic transfer outside findings

- Inventory remains two slots: aspect-bearing input 0 and burnable fuel 1, stack limit 64, same GUI coordinates, same accessible face slot sets, and same bottom bucket extraction exception.
- Both use vanilla furnace burn time, consume one fuel stack item, preserve container items, identify Alumentum/resource meta 0 as speed boost, and transfer aspects to up to four vertically contiguous alembics every 20 boosted or 40 normal ticks.
- Both compute smelt time from total aspects and bellows: `aspectTotal * 10 * (1 - 0.125 * bellows)`, consume one input, and retain at most 50 total essentia.
- Empty filtered alembic selection, exclusion of already-served aspects during the random pass, and one-unit transfer cadence match. A-013-F11 isolates the existing-aspect/mismatched-filter branch.

### A-013-PC07 - GUI visuals and routing

- GUI ID remains 9 and common/client proxies reject a tile that is not `TileAlchemyFurnace`.
- Slot anchors match TC4 exactly: input `(80,8)`, fuel `(80,48)`, player inventory at `(8 + col*18, 84 + row*18)`, hotbar y 142.
- GUI texture and rectangles match: 20-pixel burn gauge, 46-pixel cook gauge, 48-pixel contents gauge, and static tank frame.
- A-013-F01 concerns only missing value transport; A-013-F10 concerns sneak activation.

### A-013-PC08 - Core persistence and packet state

- Original keys for aspects, vis, heat, power1, power2, basic inventory, burn/cook state, speed boost, and custom name remain readable/writable.
- `TileThaumcraft` maps 1.7 custom update NBT to 1.12 `SPacketUpdateTileEntity` and update tags. `EnumSkyBlock.BLOCK` is relit when relevant advanced/basic tile data arrives.
- A-013-F08 and A-013-F09 are additive lifecycle compatibility behavior, not losses of the original persistent fields.

### A-013-PC09 - Registration and class surface

- Dedicated block legacy registry path remains `blockAlchemyFurnace` under namespace `thaumcraft`.
- Tile registrations retain legacy identities `TileAlchemyFurnaceAdvanced` and `TileAlchemyFurnaceAdvancedNozzle` through namespaced 1.12 registration.
- No original or port slave class exists. Do not invent one when maintaining the nozzle proxy behavior.

## Intentional And Required Platform Adaptations

- `ForgeDirection` -> `EnumFacing`; legacy side index semantics must still be translated exactly, which A-013-F03 currently fails for DOWN/UP insertion.
- `canUpdate`/`func_145845_h` -> `ITickable.update`.
- Null `ItemStack` -> `ItemStack.EMPTY`, with safe empty checks.
- Raw metadata -> `IBlockState` while preserving formed/input metadata identities.
- `S35PacketUpdateTileEntity` -> `SPacketUpdateTileEntity` and update-tag handling.
- 1.7 block-update and recursive replacement behavior -> explicit notify, relight, comparator, and restoration guards.
- Legacy GL/TESR calls -> 1.12 `GlStateManager`, atlas sprites, and CCL OBJ parsing while preserving source transforms and thresholds.
- Nozzle stale-reference revalidation and empty-source null safety are intentional hardening.
- Preserving aspects/inventory accounting during formation, transient teardown/cooldown NBT, legacy `Vis`, active fuel duration, formed idempotence, and guarded old-port layout migration are intentional product/data-integrity deltas, not required API translations.
- Hiding the formed implementation block from creative/pick is an intentional structure-integrity delta already enforced by tests.

## Test Debt And Existing Lock-In

| Surface | Existing evidence | Missing or conflicting evidence |
| --- | --- | --- |
| GUI fields | Layout guard only | No listener initial-send, changed-field send, client update, or live gauge test; A-013-F01 is unconstrained |
| Shift-click | None | No fuel-plus-aspects routing or fallback case; A-013-F02 is unconstrained |
| Sided automation | None | No face/slot insertion-extraction matrix; A-013-F03 is unconstrained |
| Advanced heat | `VisNetworkChargingRuntimeTest` | Existing expected heat 4 positively locks the port delta; TC4 first-cycle value is 3; no starvation debt/recovery case |
| Sync cadence | Static guard requires sync and test double suppresses it | No notification/dirty count, save continuity, or performance evidence |
| Formation | Strong runtime layout, migration, rollback/accounting, legacy, and idempotence coverage | No exact FX packet-volume test and no failed-world mutation audit beyond current fakes |
| Lifecycle NBT | Advanced packet test locks `processed`/`destroy`; basic sync test locks legacy `Vis` | No actual chunk unload/reload cooldown, pending teardown, or current-fuel GUI scaling test |
| Sneaking | None | No client/server sneak activation matrix |
| Alembic filter | Null-filter empty/filled transfers covered | No mismatched existing aspect/filter case |
| Nozzle | Empty/output/removal covered | No stale center removal/replacement/re-resolution case |
| Teardown/drops | Static source guards and metadata runtime checks | No real-world break cascade/drop-count test |
| Rendering | OBJ, transforms, textures, thresholds, particles, and state restoration have strong static/asset guards | No manual in-game visual validation |

Minimum focused regression evidence for required findings:

1. Runtime container listener test for all five fields, including initial and changed sends.
2. Runtime transfer test where coal/Alumentum is both fuel and aspect-bearing.
3. Sided inventory matrix proving DOWN fuel insertion, no UP slots, horizontal input, and bucket-only bottom extraction.
4. Advanced heat tests for first powered cycle, prolonged starvation, and recovery debt.
5. Deterministic formation packet test asserting 18 sparkle positions plus one sound.
6. Basic block activation test for sneaking versus non-sneaking server behavior.
7. Alembic transfer test with an existing aspect and a different installed filter.

## Unknowns And Conflicts

- Synchronization cadence is a verified source delta, but no measured server/network harm establishes it as a defect. It also supports the charter's explicit synchronization preserve control. Treat A-013-F05 as adaptation/performance risk, not an automatic parity rewrite.
- Normal in-game creation of a nonempty alembic whose filter differs from its current aspect was not established. The state is valid NBT and addon-visible; A-013-F11's method behavior is still an exact source discrepancy.
- Visual severity of reducing 18 sparkle packets to two was not manually observed. Packet positions/count are unambiguous.
- Persisted-world reliance on port-added `processed`, `destroy`, `CurrentBurnTime`, hidden formed-block policy, or legacy layout migration is unknown. Current tests deliberately lock these behaviors, so they require explicit adjudication before removal.
- No conflict exists over comparator magnitude after bytecode inspection: despite ambiguous CFR parentheses, original JVM bytecode returns only 0 or 1.

## Commands And Results

All commands were run from the repository root. CFR 0.152 decompiled exact classes from S-003 into a disposable cache outside the repository. Missing 1.7.10 dependency warnings did not prevent target methods from decompiling.

```text
git status --short
git branch --show-current
git rev-parse HEAD
sha256sum Thaumcraft-1.7.10-4.2.3.5.jar
jar tf Thaumcraft-1.7.10-4.2.3.5.jar | grep -E '<target class expression>'
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --outputdir /home/stfu/.cache/opencode/a13-tc4 --jarfilter BlockAlchemyFurnace --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --outputdir /home/stfu/.cache/opencode/a13-tc4 --jarfilter TileAlchemyFurnaceAdvancedNozzle --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --outputdir /home/stfu/.cache/opencode/a13-tc4 --jarfilter TileAlchemyFurnaceAdvanced --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --outputdir /home/stfu/.cache/opencode/a13-tc4 --jarfilter TileAlchemyFurnace --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --outputdir /home/stfu/.cache/opencode/a13-tc4 --jarfilter ContainerAlchemyFurnace --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --outputdir /home/stfu/.cache/opencode/a13-tc4 --jarfilter GuiAlchemyFurnace --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --outputdir /home/stfu/.cache/opencode/a13-tc4 --jarfilter WandManager --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --outputdir /home/stfu/.cache/opencode/a13-tc4 --jarfilter BlockStoneDevice --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --outputdir /home/stfu/.cache/opencode/a13-tc4 --jarfilter VisNetHandler --silent true
javap -classpath Thaumcraft-1.7.10-4.2.3.5.jar -c -p thaumcraft.common.blocks.BlockAlchemyFurnace
./scripts/dev.sh test
```

- `./scripts/dev.sh test`: `BUILD SUCCESSFUL`; all current non-GUI tests passed.
- Test success does not invalidate the findings because the gaps and intentional lock-ins are listed above.
- Runtime smoke: not required and not run because no product/runtime file changed.
- Manual client visual validation: not run.
- Product diff: none.
- Central ledger diff from this assignment: none.

## Handoff

- Terminal status: complete.
- Required finding index: A-013-F01 missing GUI property sync; A-013-F02 aspect-first shift-click misroutes fuel; A-013-F03 DOWN fuel insertion rejected; A-013-F04 missing unconditional heat decay/debt; A-013-F07 reduced formation sparkle shell; A-013-F10 sneaking opens GUI; A-013-F11 mismatched-filter existing alembic stops filling.
- Intentional/adaptation index: A-013-F05 explicit broad sync/relight hardening; A-013-F06 aspect and item-accounting preservation during formation; A-013-F08 cooldown/teardown lifecycle NBT; A-013-F09 legacy Vis/current-fuel NBT; A-013-F12 hidden formed block item; A-013-F13 idempotent/legacy formation compatibility; A-013-F14 nozzle re-resolution/null safety.
- Positive parity index: A-013-PC01 canonical blueprint/cost/metas; PC02 teardown/drops; PC03 processing/capacity; PC04 vis/render thresholds; PC05 nozzle/comparator; PC06 basic smelting/fuel/alembics; PC07 GUI layout/routing; PC08 core persistence; PC09 registration/no slave.
- Exact continuation point: none; packet is ready for orchestrator normalization and intentional-delta adjudication.
- Smallest next action if continued: Normalize all seven defect findings, seven intentional deltas, nine preserve controls, unknowns, and per-finding test debt into central `RECON.md`; do not edit product code until the goal contract is frozen.
