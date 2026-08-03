# Audit Packet: A-012 - Void equipment runtime

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Assignment-ID: A-012
Status: complete
Report-Revision: 1
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Compare Void runtime equipment against bundled Thaumcraft 4.2.3.5: all five `ItemVoid` tool classes, base Void armor, Void Robe armor, `RecipesVoidRobeArmorDyes`, Void wand-cap and material contracts, and only their immediately required helpers. Audit repair inputs and self-repair cadence, durability/statistics, armor effects and warp, set behavior, goggles/revealer behavior, dye/display/NBT behavior, tool combat callbacks/AOE, and client/server semantics.
- Anti-scope: Recipe bodies, research declarations, general registration beyond establishing the actual equipment/cap surface, unrelated equipment, product edits, and central Goal Ledger edits.
- Oracle and comparison direction: Bundled TC4 4.2.3.5 bytecode and exact inherited Minecraft 1.7.10 behavior -> Forge 1.12.2 port and exact inherited Forge/Minecraft 1.12.2 behavior.
- Questions: Does each item retain its TC4 material, durability, attack, repair, self-repair, rarity, warp, potion, armor, vis, revealer, dye, and NBT semantics? Are callback timing and logical-side behavior still equivalent after the Forge version change? Are Void cap values and directly exposed handles retained?
- Expected evidence: Exact CFR decompilation of the original classes, inherited 1.7.10 and 1.12.2 callback/stat behavior where adaptation matters, current-source inspection, and focused existing-test inspection.
- Read/write permissions: Product files, original artifacts, and central ledger files read-only; only this report writable.
- Stop conditions: Every assigned runtime surface is classified as a defect, positive parity, or explicit platform adaptation/test gap, with no product edit.

## Coverage Performed

- Exact TC4 equipment classes decompiled:
  - `thaumcraft/common/items/equipment/ItemVoidSword.class`
  - `thaumcraft/common/items/equipment/ItemVoidPickaxe.class`
  - `thaumcraft/common/items/equipment/ItemVoidAxe.class`
  - `thaumcraft/common/items/equipment/ItemVoidShovel.class`
  - `thaumcraft/common/items/equipment/ItemVoidHoe.class`
  - `thaumcraft/common/items/armor/ItemVoidArmor.class`
  - `thaumcraft/common/items/armor/ItemVoidRobeArmor.class`
  - `thaumcraft/common/items/armor/RecipesVoidRobeArmorDyes.class`
- Immediate oracle helpers inspected: `RecipesRobeArmorDyes`, `WandCap`, `ItemWandCap`, relevant `ItemWandCasting` methods, `WandManager.getTotalVisDiscount`, `EventHandlerRunic.getFinalWarp`, `ConfigItems.initializeItems`, and `ThaumcraftApi` material declarations.
- Exact inherited behavior inspected where source-level comparison was insufficient: Minecraft 1.7.10 `InventoryPlayer`, `ItemSword`, `ItemTool`, `ItemPickaxe`, `ItemAxe`, `ItemSpade`, and `ItemHoe`; Forge/Minecraft 1.12.2 `InventoryPlayer` and the corresponding item classes.
- Current port surfaces inspected: all matching classes under `src/main/java`, `ConfigItems`, `ThaumcraftApi`, wand component initialization, wand cap lookup/consumption, vis discount aggregation, goggles/revealer consumers, item-color registration, robe models/assets, and focused tests listed below.
- Uncovered by assignment: recipe implementation bodies, research declaration parity, unrelated registration, manual in-game rendering, live combat sampling, addon linkage smoke, and runtime server/client smoke.

## Atomic Findings

### A-012-F01 - Equipped Void armor self-repairs twice per cadence boundary

- Type: defect
- Severity: high
- Confidence: high
- Source/oracle locator: S-003/S-004 `ItemVoidArmor.class` and `ItemVoidRobeArmor.class`, CFR `func_77663_a` and `onArmorTick`; exact Minecraft 1.7.10 `InventoryPlayer.func_70429_k`; exact Forge 1.12.2 `InventoryPlayer.decrementAnimations`; S-005 `src/main/java/thaumcraft/common/items/armor/ItemVoidArmor.java:41-49,68-72` and `ItemVoidRobeArmor.java:120-128`.
- Observed: Both port armor classes invoke the same repair helper from ordinary `onUpdate` and from `onArmorTick`. `repairVoidArmor` repairs exactly one durability when the world is server-side, the stack is damaged, the owner is living, and `entity.ticksExisted % 20 == 0`.
- Expected: TC4 also declares both methods, but its inherited inventory lifecycle does not ordinary-update equipped armor. Stored armor receives `onUpdate`; equipped armor receives the Forge armor callback. A worn piece therefore receives one repair invocation at each 20-tick boundary.
- Exact callback cadence:

| State | TC4 callbacks at tick `% 20 == 0` | Port callbacks at tick `% 20 == 0` | Effective repair |
|---|---:|---:|---:|
| Damaged piece in main inventory | one `onUpdate` | one `onUpdate` | 1 durability per 20 ticks on both sides |
| Damaged equipped piece | one `onArmorTick` | `onUpdate` plus `onArmorTick` | TC4 1 versus port 2 durability per 20 ticks |

- Evidence: Exact 1.12 `InventoryPlayer.decrementAnimations` iterates `allInventories`, which contains main, armor, and offhand inventories, and calls each stack's update animation/`Item.onUpdate`; it then independently iterates `armorInventory` and calls `Item.onArmorTick`. Exact 1.7.10 `InventoryPlayer.func_70429_k` ordinary-updates only its 36-slot main array; the armor array is not part of that loop.
- Effect/reproduction: Equip a base Void or Void Robe piece damaged by at least 2 durability. Advance the authoritative player tick across a value divisible by 20. The port lowers item damage by 2; TC4 lowers it by 1. This halves effective regeneration time for worn pieces.
- Affected surface: All four base Void armor slots and every registered Void Robe slot; stored stacks remain correct.
- Regression hazards: Preserve server authority, the damaged-stack guard, living-owner requirement, exact 20-tick cadence, one-point repair amount, repair while stored, and repair while equipped. Avoid globally changing `InventoryPlayer` or shared Forge callbacks.
- Test gap: `src/test/java/thaumcraft/common/items/armor/ItemVoidArmorParityTest.java` invokes `repairVoidArmor` directly once and verifies a one-point decrement, but never drives the combined `onUpdate` plus `onArmorTick` lifecycle for an equipped stack.
- Candidate disposition: required.

### A-012-F02 - The port exposes an invented fourth Void Robe piece with runtime effects

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-003/S-004 `ConfigItems.class` field surface and CFR `initializeItems`; `ItemVoidRobeArmor.class`; S-005 `src/main/java/thaumcraft/common/config/ConfigItems.java:123-126,662-684`, `ItemVoidRobeArmor.java:40-205`, and `src/main/java/thaumcraft/client/ClientProxy.java:768-774`.
- Observed: The port declares and registers `itemBootsVoidRobe` as an `ItemVoidRobeArmor` feet item. Exact TC4 declares and constructs only Void Robe helmet, chest, and leggings.
- Exact added boots surface:
  - Armor slot: FEET.
  - Armor material: ordinary Void material, factor 10.
  - Maximum durability: `13 * 10 = 130`.
  - Protection: 3.
  - Enchantability: 10.
  - Rarity: epic.
  - Vis discount: 5%.
  - Warp: 2.
  - Runic charge: 0.
  - Repair item: Void ingot, item-resource metadata 16.
  - Automatic repair: inherited Void Robe behavior, including A-012-F01 while equipped.
  - Special armor: normal/blockable, unblockable, fire, absorption-cap, and fall-durability behavior inherited from `ItemVoidRobeArmor`.
- Aggregate effect: The original three-piece Void Robe surface can contribute at most 15% vis discount and 6 equipment warp. The port's four pieces can contribute 20% and 8 respectively. Neither side contains a separate full-set bonus callback; the difference is the sum of per-piece behavior.
- Dye/display effect: `RecipesVoidRobeArmorDyes` accepts the added boots because it targets every `ItemVoidRobeArmor`. The boots therefore carry `display.color` and consume dyes/cauldron water like a robe piece. However, `ClientProxy.registerItemColorHandlers` registers the color handler only for the legitimate helmet, chest, and leggings, omitting the invented boots. Their inventory tint does not consistently display the NBT color.
- Effect/reproduction: Obtain the creative/command-visible boots, equip them with the three legitimate pieces, and query wand consumption and equipment warp. The player gains the extra 5% discount and 2 warp. Dyeing the boots mutates NBT, while the item model is not routed through the Void Robe item-color handler.
- Evidence: Original `javap -p ConfigItems` has only `itemHelmetVoidRobe`, `itemChestVoidRobe`, and `itemLegsVoidRobe`; original CFR initialization creates only those three. Current source explicitly creates and adds a fourth item at `ConfigItems.java:680-684`.
- Regression hazards: Preserve the three legitimate legacy IDs `ItemHelmetVoidFortress`, `ItemChestplateVoidFortress`, and `ItemLeggingsVoidFortress`. Before removing the added ID, adjudicate any persisted-data obligation of this WIP port; TC4 provides no compatibility basis for it. Do not weaken per-piece robe behavior to compensate for the extra item.
- Test gap: Existing armor tests instantiate and validate only helmet/chest/leggings, but do not reject a fourth registered piece, assert aggregate three-piece vis/warp, or exercise item-color routing. This runtime finding overlaps the registration delta recorded by A-009-F03 but adds the equipment, dye, and aggregate effects.
- Candidate disposition: required, subject only to persisted-data policy.

### A-012-F03 - Inherited 1.12 constructors change three exact Void tool attack modifiers

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: Exact Minecraft 1.7.10 `ItemSword`, `ItemTool`, `ItemPickaxe`, `ItemAxe`, and `ItemSpade`; exact Forge/Minecraft 1.12.2 equivalents; S-005 constructors at `ItemVoidSword.java:28-30`, `ItemVoidPickaxe.java:19-21`, `ItemVoidAxe.java:19-21`, and `ItemVoidShovel.java:19-21`; material at `ThaumcraftApi.java:38`.
- Observed: The port preserves the Void material attack contribution of 3.0 but delegates sword, pickaxe, and shovel attack construction to changed 1.12 vanilla base values. The axe explicitly supplies 6.0 and remains exact.
- Exact main-hand attack attribute modifiers:

| Tool | TC4 inherited formula | TC4 modifier | Port inherited/explicit formula | Port modifier | Delta |
|---|---|---:|---|---:|---:|
| Void Sword | `4.0 + 3.0` | 7.0 | `3.0 + 3.0` | 6.0 | -1.0 |
| Void Pickaxe | `2.0 + 3.0` | 5.0 | `1.0 + 3.0` | 4.0 | -1.0 |
| Void Axe | `3.0 + 3.0` | 6.0 | explicit `6.0` | 6.0 | 0.0 |
| Void Shovel | `1.0 + 3.0` | 4.0 | `1.5 + 3.0` | 4.5 | +0.5 |

- Effect/reproduction: Inspect each stack's MAINHAND `SharedMonsterAttributes.ATTACK_DAMAGE` modifier, or perform fully charged attacks with no enchantments/effects. Relative to TC4, the sword and pickaxe lose 1.0 attribute damage and the shovel gains 0.5. The axe is the positive control.
- Adaptation classification: Attack cooldown and attribute plumbing are required 1.12 platform behavior, but these exact item modifiers are not forced by signature compatibility; the old values can be retained with item-specific modifiers/constructors. The inherited origin therefore explains but does not eliminate the semantic delta.
- AOE boundary: Neither exact TC4 Void class nor the port defines custom Void AOE. The port sword can participate in vanilla 1.12 sweep attacks when the global combat conditions are met; that is inherited 1.12 combat-system behavior, not a missing/custom Void callback.
- Regression hazards: Preserve material level 4, 150 uses, efficiency 8.0, material attack contribution 3.0, enchantability 10, normal 1.12 cooldown integration, mining classes/effectiveness, Weakness callbacks, repair, self-repair, rarity, and warp. Do not modify shared `ThaumcraftApi.toolMatVoid` to correct per-item constructor bases.
- Test gap: `src/test/java/thaumcraft/common/items/equipment/ItemVoidEquipmentParityTest.java` locks the material tuple and axe attack/speed intent but does not assert sword, pickaxe, or shovel modifiers. It therefore passes with all three deltas.
- Candidate disposition: required.

### A-012-F04 - Void Hoe entity hits now consume one durability

- Type: defect
- Severity: low
- Confidence: high
- Source/oracle locator: Exact Minecraft 1.7.10 `ItemHoe`; exact Forge/Minecraft 1.12.2 `ItemHoe.hitEntity`; original `ItemVoidHoe.class#onLeftClickEntity`; S-005 `src/main/java/thaumcraft/common/items/equipment/ItemVoidHoe.java:37-41`.
- Observed: Port `onLeftClickEntity` applies the correct 80-tick Weakness effect and delegates to the 1.12 parent callback. The inherited 1.12 hoe combat path damages the stack by one on a successful entity hit.
- Expected: Exact 1.7.10 `ItemHoe` has no corresponding `hitEntity` durability override. Original `ItemVoidHoe.onLeftClickEntity` applies Weakness and delegates, but that delegation does not consume durability.
- Exact delta: successful entity hit durability cost 0 -> 1.
- Effect/reproduction: Hit a living entity successfully with a damaged or undamaged Void Hoe. The port increases item damage by one; TC4 leaves it unchanged. The one-per-second self-repair can conceal the difference during casual observation.
- Regression hazards: Preserve the 80-tick server-authoritative Weakness application, PvP gate, superclass event cancellation/return semantics, tilling durability, 150 maximum uses, enchantability override 5, charm repair, self-repair, rarity, and warp.
- Test gap: Existing tests call the shared debuff helper but do not drive a successful hoe entity hit and compare durability before/after.
- Candidate disposition: required.

### A-012-F05 - The direct `ConfigItems.WAND_CAP_VOID` handle is missing

- Type: compatibility defect
- Severity: low
- Confidence: high
- Source/oracle locator: S-003/S-004 `thaumcraft/common/config/ConfigItems.class`, public field surface and CFR initialization; S-005 `src/main/java/thaumcraft/common/config/ConfigItems.java:56-64` and `src/main/java/thaumcraft/common/Thaumcraft.java:365-369`.
- Observed: The port constructs the exact Void `WandCap` and inserts it in `WandCap.caps`, but does not retain it in a `ConfigItems.WAND_CAP_VOID` field. The other original direct cap/rod handles are also no longer represented there, but this packet records only the assigned Void handle.
- Expected: TC4 exposes `public static WandCap WAND_CAP_VOID` and assigns the newly constructed Void cap to it during item/component initialization.
- Runtime effect: Internal port runtime remains functional because wand NBT resolves `cap="void"` through `WandCap.caps.get("void")`.
- Compatibility effect/reproduction: Source compiled against `ConfigItems.WAND_CAP_VOID` cannot compile against the port. Previously compiled bytecode resolving that exact common-class field can fail with `NoSuchFieldError`.
- Boundary classification: `thaumcraft.common.config.ConfigItems` is outside the supported `thaumcraft.api.*` boundary, so this is lower severity than loss of a public API symbol. It remains an exact TC4 direct contract requested by this assignment.
- Regression hazards: Preserve one canonical `WandCap` instance per tag, map registration order, tag `void`, texture derivation, item metadata, craft cost, and all NBT lookups. Restoring a handle must reference the map's canonical instance rather than create a duplicate cap.
- Test gap: No test asserts the direct field or the complete Void cap constants. Existing wand tests use synthetic caps or map lookup.
- Candidate disposition: required if common-class compatibility is in scope; otherwise explicitly accept/defer.

## Positive Parity

### A-012-PC01 - Void material, durability, harvesting, and enchantability

- `ThaumcraftApi.toolMatVoid` matches TC4 exactly: name `VOID`, harvest level 4, maximum uses 150, efficiency 8.0, material attack contribution 3.0, enchantability 10.
- All five registered Void tools use that material. Pickaxe/axe/shovel retain explicit Forge tool classes `pickaxe`, `axe`, and `shovel`.
- All five tools have maximum durability 150. Sword, pickaxe, axe, and shovel use enchantability 10; the Void Hoe intentionally overrides enchantability to 5 on both sides.
- All tools remain uncommon and implement warp 1.

### A-012-PC02 - Repair inputs and stored-tool self-repair

- Every Void tool accepts item-resource metadata 15, the primal charm, as its explicit repair input. Void ingot metadata 16 is not a valid tool repair input. The shared Void tool material intentionally has no port-only Void-ingot repair assignment.
- Base Void armor and Void Robe armor accept item-resource metadata 16, the Void ingot. The shared armor material repair item is configured consistently with the explicit item checks.
- Tool `onUpdate` cadence matches TC4 semantically: when held/carried by a living entity and damaged, repair one durability every 20 entity ticks. The port's server-only guard is an authority adaptation; it avoids duplicate client mutation. A-012-F01 is specific to equipped armor's two callback routes.
- Creative-mode `ItemStack.damageItem(-1, player)` semantics remain inherited: direct negative damage does not mutate a creative player's stack. Existing runtime tests positively cover that behavior.

### A-012-PC03 - Combat debuffs and logical-side behavior

- Void Sword applies vanilla Weakness for exactly 60 ticks from `hitEntity`.
- Void Pickaxe, Axe, Shovel, and Hoe apply vanilla Weakness for exactly 80 ticks from `onLeftClickEntity` when the target is living.
- Both sides suppress player-on-player debuffs when server PvP is disabled. Non-player targets and PvP-enabled player targets remain eligible.
- The port performs potion mutation only on the authoritative server. TC4's sword catches a broad exception around potion application; vanilla Weakness is a stable built-in potion in the port, so omitting that catch does not establish a valid runtime delta.
- No assigned tool has a custom Void AOE callback. Vanilla 1.12 sword sweeping and attack cooldown are platform combat behavior.

### A-012-PC04 - Base Void armor statistics and behavior

- The port's armor array `{3, 6, 7, 3}` is the correct Forge 1.12 slot-order representation of TC4's semantic head/chest/legs/feet protection `3/7/6/3` and original declaration array `{3, 7, 6, 3}`.
- Armor material factor is 10 and enchantability is 10. Exact per-piece maximum durability is head 110, chest 160, legs 150, feet 130.
- All four base Void armor pieces exist on both sides, remain uncommon, expose runic charge 0, and contribute warp 1 each.
- Base armor textures retain `void_1.png` for head/chest/feet and `void_2.png` for legs.

### A-012-PC05 - Void Robe per-piece armor, vis, warp, and special armor

- The three legitimate robe pieces use ordinary `armorMatVoid` through `ConfigItems.ARMOR_VOID_ROBE = ARMOR_VOID`; TC4 does not use `armorMatVoidFortress` for these items despite the research/registry naming.
- Legitimate robe maximum durability/protection is head 110/3, chest 160/7, and legs 150/6. Each is epic, runic charge 0, vis discount 5%, and warp 2.
- `WandManager.getTotalVisDiscount` sums all `IVisDiscountGear` in armor and Baubles, applies vis-exhaustion penalties, and divides the percentage total by 100.0, matching TC4.
- `showNodes` and `showIngamePopups` return true only for the HEAD piece. Client node and goggles-popup consumers inspect the equipped helmet and call `IRevealer`/`IGoggles`, preserving the Void Robe hood's revealer surface.
- Special armor formulas match exactly:
  - Blockable non-fire damage: priority 0 and ratio `damageReduceAmount / 25.0`.
  - Unblockable damage: priority 1 and ratio `damageReduceAmount / 35.0`.
  - Fire damage: priority 0 and ratio 0.0.
  - Absorption cap: `maxDamage + 1 - itemDamage`.
  - Armor display: `damageReduceAmount`.
  - Armor durability damage: apply incoming armor damage except for the singleton fall damage source.
- Neither side defines a separate Void Robe set bonus. Behavior is additive per piece.

### A-012-PC06 - Void Robe dye recipe, display color, and NBT

- `RecipesVoidRobeArmorDyes` correctly restricts the common robe dye implementation to `ItemVoidRobeArmor`.
- Match contract is exact: one target armor stack, at least one vanilla dye, no second armor stack, and no unrelated ingredient.
- Crafting output copies the armor stack and all NBT, then sets count to 1.
- Because Void Robes report `hasColor == true`, the existing color is always one blend sample. Undyed/default robes contribute decimal `6961280` (`0x6A3880`).
- Every dye contributes `EntitySheep` RGB values selected by its legacy dye damage. The recipe averages RGB, averages maximum-channel brightness, rescales the result to that brightness, and writes `(r << 16) + (g << 8) + b`, matching TC4's leather-style algorithm.
- Color remains under compound `display`, integer key `color`. Missing tag/compound/key returns default `6961280`. Setting creates only the required compounds; removing color deletes only `display.color` and preserves other display/NBT data.
- Cauldron washing is server-authoritative, requires positive water level, removes color, consumes exactly one level, updates comparator output, and succeeds even if the robe was already at its default/no-explicit-color state, matching TC4.

### A-012-PC07 - Void wand-cap runtime contract

- The canonical cap values match exactly: tag `void`, base consumption modifier 0.8, finished cap item metadata 7, and craft cost 9. The inert crafting component remains item metadata 8.
- `WandCap` derives texture `thaumcraft:textures/models/wand_cap_void.png`, stores itself under key `void`, and reports research key `CAP_void`.
- Wand NBT continues to write key `cap` with string value `void`. `ItemWandCasting.getCap` resolves the string through `WandCap.caps`; missing cap NBT falls back to `iron`.
- Consumption starts from cap modifier 0.8, applies any equipment discount, non-crafting Frugal discount, and sceptre discount, then clamps to a 0.1 minimum, matching the original ordering and semantics.
- Void cap item/model textures and finished/inert item models exist. TC4 always exposed metadata 7 and 8 in the creative list, so the port's separate optional copper/silver creative-gating issue does not alter the assigned Void cap surface.

## Legitimate Platform Adaptations

- Armor material array ordering changed between the two Minecraft APIs. `{3,6,7,3}` in this port preserves semantic `HEAD/CHEST/LEGS/FEET = 3/7/6/3`; rewriting it to the literal TC4 array would be wrong.
- Numeric armor slots map to `EntityEquipmentSlot`. Correct slot objects are adaptation, not a delta.
- Empty stacks replace null stacks in 1.12 recipes/inventories. `ItemStack.EMPTY` returns preserve the original invalid-recipe behavior.
- `EnumDyeColor.byDyeDamage` plus `EntitySheep.getDyeRgb` is the 1.12 representation of TC4's legacy sheep/dye lookup.
- Potion effects and negative-durability repair are server-authoritative in the port. Suppressing client-side mutation avoids prediction/desynchronization and preserves authoritative gameplay state.
- Vanilla 1.12 attack cooldown and sweep mechanics are global combat adaptations. They do not justify the item-specific attribute deltas in A-012-F03, but they should not be removed when restoring values.
- Forge 1.12 item-color handlers replace legacy icon render passes. The legitimate three Void Robe pieces are correctly routed and tint only their dyed base layer.
- Omitting `super.onArmorTick` in the two Void armor classes has no semantic effect because the 1.12 base implementation is empty.

## Unknowns and Conflicts

- The work-in-progress port may have produced persisted stacks containing `thaumcraft:itembootsvoidrobe`. TC4 has no such item, but removal requires an explicit persisted-data decision rather than silent ID deletion.
- A-012-F03 is an exact semantic delta caused by inherited vanilla rebalance. No source conflict exists about the values; implementation policy must decide whether item damage should preserve TC4 numerically while retaining 1.12 cooldown mechanics.
- `ConfigItems.WAND_CAP_VOID` is an exact original public field in a common package, not in the supported addon API package. Runtime parity is unaffected; compatibility policy determines whether restoring common-package linkage is required.
- No other unresolved source/decompiler conflict remains. Exact original classes, independent inherited-class evidence, and current source agree on the five findings.

## Test Debt

- A-012-F01: Add an equipped-stack lifecycle test that drives both ordinary inventory update and `onArmorTick` at tick 20, while retaining a stored-stack control. Assert exactly one point of repair in each state.
- A-012-F02: Add a registration/runtime surface guard for exactly three Void Robe pieces, aggregate 15% vis discount and 6 warp, and tint registration for every retained dyeable piece. Apply only after persisted-ID adjudication.
- A-012-F03: Extend `ItemVoidEquipmentParityTest` with exact MAINHAND attack modifiers: sword 7.0, pickaxe 5.0, axe 6.0, shovel 4.0. Keep independent attack-speed assertions appropriate to 1.12.
- A-012-F04: Add a successful Void Hoe entity-hit test asserting TC4-equivalent durability behavior while retaining Weakness 80 and the parent callback return contract.
- A-012-F05: Add a static/binary surface guard for `ConfigItems.WAND_CAP_VOID` if common-package compatibility is accepted, and a runtime identity assertion that it is the same object as `WandCap.caps.get("void")` with modifier 0.8, metadata 7, and craft cost 9.
- Positive parity: Add direct behavior coverage for Void Robe dye matching, existing-color blending, count/NBT preservation, default color `6961280`, color removal, and cauldron washing. Current tests primarily guard source fragments and model routing.
- Positive parity: Existing tests cover material tuple, repair items, one helper repair invocation, Weakness, rarity, basic robe vis/warp/revealer, and selected static contracts, but not full client/server callback composition.

## Commands and Results

All commands were run from the repository root. CFR 0.152 warnings about unavailable 1.7.10 dependencies did not prevent the assigned classes/methods from decompiling.

### Worktree and class discovery

```text
git status --short
jar tf Thaumcraft-1.7.10-4.2.3.5.jar | grep -Ei '(^|/)(ItemVoid|RecipesVoid|.*Void.*(Armor|Wand|Cap|Tool)).*\.class$' | sort
jar tf Thaumcraft-1.7.10-4.2.3.5.jar | grep '^thaumcraft/common/items/' | grep -Ei '(void|wandcap|wandrod|tool)' | sort
javap -classpath Thaumcraft-1.7.10-4.2.3.5.jar -p thaumcraft.common.config.ConfigItems
```

### Exact TC4 class decompilation

```text
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --jarfilter ItemVoidSword --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --jarfilter ItemVoidPickaxe --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --jarfilter ItemVoidAxe --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --jarfilter ItemVoidShovel --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --jarfilter ItemVoidHoe --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --jarfilter ItemVoidArmor --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --jarfilter ItemVoidRobeArmor --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --jarfilter RecipesVoidRobeArmorDyes --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --jarfilter RecipesRobeArmorDyes --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --jarfilter ConfigItems --methodname initializeItems --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --jarfilter ThaumcraftApi --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --jarfilter ItemWandCap --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --jarfilter WandCap --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --jarfilter ItemWandCasting --methodname getConsumptionModifier --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --jarfilter ItemWandCasting --methodname getCap --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --jarfilter ItemWandCasting --methodname consumeAllVis --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --jarfilter WandManager --methodname getTotalVisDiscount --silent true
/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --jarfilter EventHandlerRunic --methodname getFinalWarp --silent true
```

### Inherited callback and attack evidence

```text
unzip -p run/validate/forge-1.7.10-userdev.jar conf/packaged.srg | grep 'net/minecraft/entity/player/InventoryPlayer'
unzip -p run/validate/forge-1.7.10-userdev.jar conf/packaged.srg | grep 'net/minecraft/item/ItemSword'
unzip -p run/validate/forge-1.7.10-userdev.jar conf/packaged.srg | grep -E 'net/minecraft/item/(ItemAxe|ItemPickaxe|ItemSpade|ItemHoe|ItemTool)' | grep '^CL:'
/usr/local/bin/cfr run/validate/minecraft-1.7.10-client.jar --jarfilter yx --methodname k --silent true
/usr/local/bin/cfr run/validate/minecraft-1.7.10-client.jar --jarfilter aeh --silent true
/usr/local/bin/cfr run/validate/minecraft-1.7.10-client.jar --jarfilter acg --silent true
/usr/local/bin/cfr run/validate/minecraft-1.7.10-client.jar --jarfilter abf --silent true
/usr/local/bin/cfr run/validate/minecraft-1.7.10-client.jar --jarfilter adn --silent true
/usr/local/bin/cfr run/validate/minecraft-1.7.10-client.jar --jarfilter ady --silent true
/usr/local/bin/cfr run/validate/minecraft-1.7.10-client.jar --jarfilter ada --silent true
/usr/local/bin/cfr /home/stfu/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeBin-1.12.2-14.23.5.2847.jar --jarfilter InventoryPlayer --methodname decrementAnimations --silent true
javap -classpath /home/stfu/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeBin-1.12.2-14.23.5.2847.jar -c -p net.minecraft.item.ItemSword
javap -classpath /home/stfu/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeBin-1.12.2-14.23.5.2847.jar -c -p net.minecraft.item.ItemTool
javap -classpath /home/stfu/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeBin-1.12.2-14.23.5.2847.jar -c -p net.minecraft.item.ItemAxe
javap -classpath /home/stfu/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeBin-1.12.2-14.23.5.2847.jar -c -p net.minecraft.item.ItemPickaxe
javap -classpath /home/stfu/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeBin-1.12.2-14.23.5.2847.jar -c -p net.minecraft.item.ItemSpade
javap -classpath /home/stfu/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeBin-1.12.2-14.23.5.2847.jar -c -p net.minecraft.item.ItemHoe
javap -classpath build/classes/java/main -c thaumcraft.common.items.equipment.ItemVoidAxe
```

- Targeted repository `read`, `glob`, and `grep` inspection covered all current files, assets, direct consumers, and tests named above.
- Audit result: Five semantic/contract findings verified; all remaining assigned behavior classified as positive parity or an explicit platform adaptation.
- Tests/build: Not run. This assignment was a read-only parity audit followed by report-only persistence; no product code changed.
- Runtime smoke: Not required and not run because no runtime/product path changed. No visual parity claim is made for the invented boots.
- Product diff: none.

## Handoff

- Terminal status: complete.
- Material finding index: A-012-F01 high double equipped-armor self-repair; A-012-F02 medium invented fourth Void Robe piece and aggregate/tint effects; A-012-F03 medium sword/pickaxe/shovel attack modifier deltas; A-012-F04 low Void Hoe hit durability; A-012-F05 low missing direct Void cap handle.
- Positive parity index: A-012-PC01 materials/stats; A-012-PC02 repair inputs and stored repair; A-012-PC03 Weakness/PvP/AOE; A-012-PC04 base Void armor; A-012-PC05 Void Robe armor/vis/warp/revealer/special armor; A-012-PC06 dye/display/NBT/cauldron; A-012-PC07 Void cap runtime.
- Exact continuation point: none; the packet is ready for orchestrator normalization without further A-012 investigation.
- Smallest next action if continued: Normalize the five findings and seven preserve controls into the central ledger in a separate authorized step; do not edit product code during report persistence.
