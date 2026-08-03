# Audit Packet: A-009 — Eldritch Registries and Materials

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Assignment-ID: A-009
Status: complete
Report-Revision: 1
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Audit the item, block, tile, and entity registration and material surface required by the 16 TC4 `ELDRITCH` researches, their recipe outputs/inputs, and their direct runtime consumers. Check registry IDs/names, metadata variants, stack limits, durability, tool/armor material statistics, armor slots, per-meta tile mappings, entity local IDs/order, tracking tuples, and spawn-egg colors.
- Anti-scope: Deep item, block, tile, entity, recipe, world-generation, rendering, or research behavior; recipe body parity; categories outside Eldritch except direct dependencies and consumers; product edits; central Goal Ledger edits.
- Oracle and comparison direction: S-003/S-004 bundled Thaumcraft 4.2.3.5 classes -> S-005 Forge 1.12.2 port. Forge 1.12 registry and equipment-slot API changes are adaptations, not defects, unless they alter the semantic surface.
- Questions: Are all required objects registered under equivalent IDs? Are all research/runtime metadata values valid and equivalent? Do item limits, tool/armor statistics, and armor slots match? Do Eldritch block metas create the same tiles? Do relevant entities preserve registration order/local IDs, names, tracking, velocity, and eggs?
- Expected evidence: CFR decompilation of original `ConfigItems`, `ConfigBlocks`, `ConfigEntities`, and directly relevant item/block classes; current source inspection; focused existing-test inspection; exact command record.
- Read/write permissions: Product files and central ledger files read-only; only this report writable.
- Effort/tool budget: Bounded source/bytecode audit of the registration/material surface; stop before deep runtime or recipe-body comparison.
- Stop conditions: Every referenced registration/material surface is either matched, recorded as an atomic divergence, or explicitly left unknown; no product edit; no unresolved high-confidence source conflict.
- Continuation predecessor: none

## Coverage Performed

- Current registration roots inspected: `src/main/java/thaumcraft/common/config/ConfigItems.java`, `ConfigBlocks.java`, `ConfigEntities.java`, and `research/ConfigResearchEldritch.java`.
- Current supporting surfaces inspected: `ThaumcraftApi.java`; Eldritch/resource/wand/primal/sanity/void/cultist item classes; `BlockEldritch`, `BlockMetalDevice`, `BlockEssentiaReservoir`, and their ItemBlocks; direct cultist and Outer Lands entity equipment consumers; focused tests named below.
- Oracle roots inspected: `thaumcraft_src/thaumcraft/common/config/ConfigItems.class`, `ConfigBlocks.class`, `ConfigEntities.class`, `ConfigResearch.class`, plus directly relevant original item and block classes. All original artifacts remained read-only.
- Research dependency map covered: resource metas 15-17; Eldritch Object metas 0-4; void tools and armor; Void Robe armor; void cap metas 7/8; Primal Focus, Primal Crusher, and primal staff meta 100; Sanity Checker; Advanced Alchemical Construct meta 3; Essentia Reservoir; Eldritch structure block metas/tiles; Primal/Eldritch projectiles and Outer Lands/cultist entities.
- Uncovered scope: Deep runtime behavior and recipe bodies by assignment; visual behavior; live Forge registry dump; in-game creative-tab/equipment observation; runtime smoke. No such validation was needed to establish the static registration/material deltas below.

## Atomic Findings

### A-009-F01 — Cultist gear is bound to the wrong armor materials

- Type: defect
- Severity: high
- Confidence: high
- Source/oracle locator: S-003/S-004 `thaumcraft/common/config/ConfigItems.class`, CFR `initializeItems()`; S-005 `src/main/java/thaumcraft/common/config/ConfigItems.java:221-233,686-747`; material definitions at `src/main/java/thaumcraft/api/ThaumcraftApi.java:40-44`; direct consumers at `EntityCultistCleric.java:77-82`, `EntityCultistKnight.java:50-53`, `EntityCultistLeader.java:73-76`, and `EntityInhabitedZombie.java:43-48`.
- Observed: The port aliases cultist robes and boots to `ThaumcraftApi.armorMatSpecial`, cultist plate to `armorMatThaumium`, and cultist leader plate to `armorMatVoid`, then constructs the slot-correct items from those aliases.
- Expected: TC4 constructs robe head/chest/legs, plate head/chest/legs, and cultist boots with `ItemArmor.ArmorMaterial.IRON`; leader head/chest/legs use `ThaumcraftApi.armorMatThaumiumFortress`. The 1.12 armor-slot API may replace numeric slots, but it must retain those material semantics.
- Exact deltas:

| Gear | TC4 material | Port material | TC4 semantic protection H/C/L/F | Port semantic protection H/C/L/F | TC4 durability factor | Port factor | TC4 enchantability | Port enchantability |
|---|---|---|---|---|---:|---:|---:|---:|
| Cultist robe | Iron | Special | `2/6/5/2` | `1/2/3/1` | 15 | 25 | 9 | 25 |
| Cultist plate | Iron | Thaumium | `2/6/5/2` | `2/5/6/2` | 15 | 25 | 9 | 25 |
| Cultist boots | Iron | Special | `2/6/5/2` | `1/2/3/1` | 15 | 25 | 9 | 25 |
| Cultist leader | Fortress | Void | `3/7/6/3` | `3/7/6/3` | 40 | 10 | 25 | 10 |

- Exact per-piece maximum-durability effects under 1.12 slot multipliers:

| Piece | TC4 expected | Port observed | Delta |
|---|---:|---:|---:|
| Robe head | 165 | 275 | +110 |
| Robe chest | 240 | 400 | +160 |
| Robe legs | 225 | 375 | +150 |
| Plate head | 165 | 275 | +110 |
| Plate chest | 240 | 400 | +160 |
| Plate legs | 225 | 375 | +150 |
| Cultist boots | 195 | 325 | +130 |
| Leader head | 440 | 110 | -330 |
| Leader chest | 640 | 160 | -480 |
| Leader legs | 600 | 150 | -450 |

- Effect: Equipped cultist clerics, knights, leaders, and inhabited zombies receive materially different protection, durability, and enchantability. Robe chest protection falls from 6 to 2; leader armor keeps protection but loses 75% of its durability factor; plate chest/legs protection is transposed relative to TC4. Because the item classes also defer to material repair behavior after their explicit repair check, substituting materials changes the inherited repair-material surface as well.
- Affected paths/symbols: `ConfigItems.ARMOR_CULTIST`, `ARMOR_CULTIST_PLATE`, `ARMOR_CULTIST_LEADER`, `ARMOR_CULTIST_BOOTS`; all `item*Cultist*` constructions and their direct entity equipment consumers.
- Evidence/reproduction: CFR `ConfigItems.initializeItems()` emits `ItemArmor.ArmorMaterial.IRON` for robe, plate, and boots and `ThaumcraftApi.armorMatThaumiumFortress` for leader pieces. Current aliases are explicit at `ConfigItems.java:227-230`. Existing `ItemVoidArmorParityTest.java:53-57` demonstrates the repository's required 1.7-to-1.12 slot-order interpretation for Fortress/Void, while no corresponding cultist material assertion exists.
- Regression hazards: Preserve all current registry names, concrete cultist item classes, `EntityEquipmentSlot` assignments, chest aliases, creative-tab placement, textures/models, rarity, vis/warp behavior, and entity loadouts. Do not “fix” this by changing shared public material definitions when restoring the original constructor material is sufficient.
- Candidate disposition: required

### A-009-F02 — Crimson Sword uses Void instead of its dedicated CVOID material

- Type: defect
- Severity: high
- Confidence: high
- Source/oracle locator: S-003/S-004 `thaumcraft/common/items/equipment/ItemCrimsonSword.class`, CFR full class and constructor; S-005 `src/main/java/thaumcraft/common/config/ConfigItems.java:547-551`, `src/main/java/thaumcraft/api/ThaumcraftApi.java:38`, and `src/main/java/thaumcraft/common/items/equipment/ItemCrimsonSword.java:24-29`.
- Observed: `ConfigItems` constructs `itemCrimsonSword` with shared `TOOLMAT_VOID`, whose exact values are harvest level 4, max uses 150, efficiency 8.0, material attack damage 3.0, and enchantability 10.
- Expected: TC4 `ItemCrimsonSword` owns and uses `toolMatCrimsonVoid = EnumHelper.addToolMaterial("CVOID", 4, 200, 8.0f, 3.5f, 20)`.
- Exact deltas: material name `CVOID` -> `VOID`; max uses 200 -> 150 (-50, 25% lower); material attack damage 3.5 -> 3.0 (-0.5); enchantability 20 -> 10 (-10, 50% lower). Harvest level 4 and efficiency 8.0 match.
- Effect: The registered `thaumcraft:itemswordcrimson` is less durable, has a lower material attack contribution, and receives weaker/less frequent enchantments than TC4. Cultist leaders directly equip it on non-Easy difficulty and as fallback gear at `EntityCultistLeader.java:77-81,97-100`.
- Affected paths/symbols: `ConfigItems.itemCrimsonSword`, `ItemCrimsonSword` constructor/material ownership, and direct Cultist Leader equipment consumers.
- Evidence/reproduction: CFR of original `ItemCrimsonSword.class` shows the exact static `CVOID` declaration and no-argument constructor `super(toolMatCrimsonVoid)`; current `ConfigItems.java:547` passes `TOOLMAT_VOID` instead.
- Regression hazards: Preserve registry name `ItemSwordCrimson`, rarity, repair/auto-repair, combat debuffs, warp, and direct leader equipment selection. A dedicated material must not silently alter public `ThaumcraftApi.toolMatVoid` or ordinary Void tools.
- Candidate disposition: required

### A-009-F03 — A fourth Void Robe armor piece is registered although TC4 has only three

- Type: defect
- Severity: low
- Confidence: high
- Source/oracle locator: S-003/S-004 `thaumcraft/common/config/ConfigItems.class` field list and CFR `initializeItems()`; S-005 `src/main/java/thaumcraft/common/config/ConfigItems.java:123-126,662-684`; `ConfigResearchEldritch.java:249-270`; `ConfigRecipesInfusionEquipmentSlice.java:129-148`.
- Observed: The port declares and registers `itemBootsVoidRobe` as `thaumcraft:itembootsvoidrobe`, constructed as `ItemVoidRobeArmor(ARMOR_VOID_ROBE, ..., EntityEquipmentSlot.FEET)` with translation key `thaumcraft.boots_void_robe`. `ItemVoidRobeArmor` places it in the creative tab. It has Void material factor 10, feet protection 3, enchantability 10, and maximum durability 130.
- Expected: TC4 has only `itemHelmetVoidRobe`, `itemChestVoidRobe`, and `itemLegsVoidRobe`. It has no `itemBootsVoidRobe` field, initialization, registry entry, research page, or recipe.
- Exact deltas: registered Void Robe piece count 3 -> 4; added registry ID `thaumcraft:itembootsvoidrobe`; added armor slot FEET; added unresearched creative stack with max damage 130 and protection 3. The legitimate three legacy registry IDs remain `ItemHelmetVoidFortress`, `ItemChestplateVoidFortress`, and `ItemLeggingsVoidFortress` after lowercase namespacing.
- Effect: The port exposes an invented armor item outside TC4 progression. `ARMORVOIDFORTRESS` and its recipes still expose only helm/chest/legs, so normal progression is not blocked, but registry compatibility and creative inventory no longer match TC4.
- Affected paths/symbols: `ConfigItems.itemBootsVoidRobe` declaration/construction/list membership and client registration/model consumers that assume it exists.
- Evidence/reproduction: `javap -p ConfigItems.class` lists only the three original Void Robe fields; CFR `initializeItems()` registers only those three. Current source contains the fourth declaration and construction. Current research and recipe slices independently enumerate only three outputs.
- Regression hazards: Do not remove or rename the three legitimate `*VoidFortress` registry IDs. Before deleting the extra ID, check whether this unshipped WIP port has any persisted-world compatibility obligation; the audit found no TC4 compatibility basis for it.
- Candidate disposition: required, subject only to the repository's persisted-data policy during implementation

### A-009-F04 — Wand-cap creative metadata ignores optional copper/silver gates

- Type: defect
- Severity: low
- Confidence: high
- Source/oracle locator: S-003/S-004 `thaumcraft/common/items/wands/ItemWandCap.class`, CFR `func_150895_a`; S-005 `src/main/java/thaumcraft/common/items/wands/ItemWandCap.java:11-27`, `src/main/java/thaumcraft/common/Thaumcraft.java:365-403`.
- Observed: Port `ItemWandCap.getSubItems()` unconditionally adds every metadata value from 0 through 8.
- Expected: TC4 always exposes metas 0, 1, 2, 6, 7, and 8; exposes meta 3 only when `Config.foundCopperIngot`; and exposes metas 4 and 5 only when `Config.foundSilverIngot`.
- Exact deltas: With no optional copper, meta 3 changes hidden -> exposed. With no optional silver, metas 4 and 5 change hidden -> exposed. Void finished/inert metas 7/8 and all other unconditional metas match. Stack limit 64, subtype flag, max damage 0, and metadata-based translation keys match.
- Effect: Configurations lacking copper or silver show creative stacks whose optional material/cap or recipe registration is absent, producing orphan or misleading variants. This does not block Void cap progression because metas 7 and 8 remain unconditional as in TC4.
- Affected paths/symbols: `ItemWandCap.getSubItems`; optional cap initialization and optional inert-cap recipes are preserve controls.
- Evidence/reproduction: CFR original `func_150895_a` contains explicit `Config.foundCopperIngot` and `Config.foundSilverIngot` branches; current source is a single unconditional `for (int meta = 0; meta <= 8; meta++)` loop. `Thaumcraft.initOptionalWandComponents()` still gates actual copper/silver cap registration, proving the creative-list mismatch is not an intentional removal of optionality.
- Regression hazards: Preserve metadata identities and ordering-independent membership, especially Void metas 7/8; do not gate gold, thaumium, or void variants; continue using the existing config discovery flags.
- Candidate disposition: required

## Positive Parity

### Registry IDs and item metadata

- All directly relevant TC4 item registration tokens are retained via `ConfigBlocks.legacyPath(...)` and the `thaumcraft` namespace, including `ItemResource`, `ItemEldritchObject`, all five Void tools, four base Void armor pieces, the three legitimate Void Robe `*VoidFortress` IDs, `ItemPrimalCrusher`, `FocusPrimal`, `ItemSanityChecker`, `WandCap`, and `WandRod`. Lowercasing the path is required by 1.12 `ResourceLocation` and is a benign adaptation.
- `ItemResource` retains 19 metas `0..18`, skips meta 5 in creative, uses max stack 64/max damage 0, and keeps Eldritch-critical metas 15 charm, 16 void ingot, and 17 void seed. Meta 15 retains its per-stack limit of 1.
- `ItemEldritchObject` retains max stack 1, max damage 0, subtype metas 0..4, and all five creative variants.
- Void cap outputs retain finished meta 7 and inert meta 8. Primal staff retains Wand Rod meta 100. Wand Rod stack/subtype/max-damage surface and the full registered rod/staff metadata set match.
- Primal Focus and Sanity Checker retain stack limit 1. Primal Crusher retains `PRIMALVOID` material values level 5, 500 uses, efficiency 8.0, attack damage 4.0, enchantability 20.
- Ordinary Void tools retain `VOID` values level 4, 150 uses, efficiency 8.0, attack damage 3.0, enchantability 10. Base Void armor and Void Robe pieces use Void protection `3/7/6/3`, durability factor 10, and correct HEAD/CHEST/LEGS/FEET slots where those pieces exist.

### Blocks, metas, ItemBlocks, and tiles

- Relevant block registry tokens match after namespacing/lowercasing: `blockEldritch`, `blockPortalEldritch`, `blockEldritchNothing`, `blockStairsEldritch`, `blockEssentiaReservoir`, and shared `blockMetalDevice`.
- `BlockEldritch` retains metas `0..10`, exposes only meta 4 in creative, and preserves exact tile mapping: 0 `TileEldritchAltar`, 1 `TileEldritchObelisk`, 3 `TileEldritchCap`, 8 `TileEldritchLock`, 9 `TileEldritchCrabSpawner`, 10 `TileEldritchTrap`; all other metas have no tile.
- `BlockEldritchItem` retains subtype metadata passthrough and max damage 0. The `OUTERREV` trigger metas 5 and 10 are representable without remapping.
- `BlockMetalDevice` retains research-critical advanced-construct meta 3, creative exposure, subtype passthrough, and no tile at meta 3. Its full per-meta tile mapping inspected for this dependency matches TC4.
- Essentia Reservoir retains registry token, metal material, hardness 2.0, resistance 17.0, subtype ItemBlock passthrough, and `TileEssentiaReservoir` at meta 0.
- Eldritch and reservoir tile registration tokens/classes match TC4. Converting original string IDs such as `TileEldritchAltar` to namespaced lowercase `ResourceLocation`s such as `thaumcraft:tileeldritchaltar` is the required 1.12 adaptation.

### Entities

- Replacing 1.7 `EntityRegistry.registerModEntity` calls with 1.12 `EntityEntryBuilder` is benign: registration order/local numeric IDs, tracking range, update frequency, velocity flag, and egg colors are preserved for all directly relevant entries.

| Entity token | Local ID | Tracker `(range, frequency, velocity)` | Egg `(primary, secondary)` |
|---|---:|---|---|
| `PrimalOrb` | 6 | `(64, 20, true)` | none |
| `EldritchOrb` | 11 | `(64, 20, true)` | none |
| `MindSpider` | 24 | `(64, 3, true)` | `(0xAAAAAA, 0x404040)` |
| `EldritchGuardian` | 25 | `(64, 3, true)` | `(0x222222, 0x404040)` |
| `EldritchWarden` | 26 | `(64, 3, true)` | `(0x552222, 0x404040)` |
| `CultistKnight` | 27 | `(64, 3, true)` | `(0xFF5055, 0x000080)` |
| `CultistCleric` | 28 | `(64, 3, true)` | `(0xFF5055, 0x800000)` |
| `CultistLeader` | 29 | `(64, 3, true)` | `(0xFF5055, 0x505050)` |
| `CultistPortal` | 30 | `(64, 20, false)` | `(0xFF5055, 0xFF50FF)` |
| `EldritchGolem` | 31 | `(64, 3, true)` | `(0x555555, 0x404040)` |
| `EldritchCrab` | 32 | `(64, 3, true)` | `(0x555555, 0x550000)` |
| `InhabitedZombie` | 33 | `(64, 3, true)` | `(0x557755, 0x550000)` |

- Names such as original `Thaumcraft.PrimalOrb` becoming registry location `thaumcraft:primalorb` are a required namespaced-registry adaptation. Existing trigger normalization is outside this assignment, but no registration tuple was lost.

## Legitimate Platform Adaptations

- 1.7 string registry tokens are represented as `thaumcraft:<lowercase-token>` because 1.12 `ResourceLocation` paths must be lowercase.
- Separate 1.12 block and ItemBlock registration preserves original metadata passthrough; this is not duplicate content registration.
- Original armor constructor slot integers map to `EntityEquipmentSlot.HEAD/CHEST/LEGS/FEET`; slot-correct construction is preserved.
- Original armor protection arrays require 1.12 equipment-slot ordering. The repository's Fortress/Void arrays and `ItemVoidArmorParityTest` correctly demonstrate this adaptation; the cultist defect is material substitution, not use of `EntityEquipmentSlot`.
- Original entity string registration and custom spawn-egg mappings are represented by `EntityEntryBuilder.id/name/tracker/egg`. Equivalent tuples are treated as parity.
- Original tile string IDs are represented by namespaced `ResourceLocation`s while retaining the original token semantically.

## Unknowns and Conflicts

- No source conflict remains for the four findings.
- Whether the WIP port has already created persisted stacks containing the extra `thaumcraft:itembootsvoidrobe` is unknown. TC4 supplies no compatibility reason to retain it, but implementation should apply the repository's explicit persisted-data policy rather than silently deleting a shipped ID.
- No live Forge registry dump was taken. Static construction/list/event registration was sufficient to verify the audited declarations, but a post-fix registry smoke should confirm actual runtime entries.

## Test Debt

- A-009-F01: `src/test/java/thaumcraft/common/entities/monster/EntityCultistBehaviorContractTest.java:77-89` verifies concrete classes, armor slots, and aliases but not material identity, max damage, protection, or enchantability. Add runtime assertions covering every distinct cultist material assignment and slot statistic.
- A-009-F02: Existing cultist tests verify weapon selection, not Crimson Sword material values. Add focused assertions for level 4, 200 uses, efficiency 8.0, material attack 3.5, and enchantability 20, and ensure ordinary Void tools remain unchanged.
- A-009-F03: `ItemVoidArmorParityTest.java:60-75` verifies the legitimate helm/chest/legs but does not reject a fourth registered piece. Add a registration-surface assertion that only the three TC4 Void Robe IDs exist, subject to any explicit migration decision.
- A-009-F04: Existing recipe tests cover optional registration but no test calls `ItemWandCap.getSubItems()` with copper/silver discovery flags disabled and enabled. Add a membership matrix for metas 3, 4, and 5 while preserving unconditional metas 0, 1, 2, 6, 7, and 8.
- Positive parity gap: No focused test currently locks the relevant entity local IDs/order/tracker/egg tuples or the complete Eldritch tile registration token/class map. Existing renderer/tile tests do not prove registry parity.

## Commands and Tools Used

- Initial/final worktree check: `git status --short`.
- Class surface discovery:
  - `javap -p thaumcraft_src/thaumcraft/common/config/ConfigItems.class`
  - `javap -p thaumcraft_src/thaumcraft/common/config/ConfigBlocks.class`
  - `javap -p thaumcraft_src/thaumcraft/common/config/ConfigEntities.class`
  - `javap -p thaumcraft_src/thaumcraft/common/config/ConfigResearch.class`
- Primary config decompilation:
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigItems.class --methodname initializeItems --silent true`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigItems.class --methodname init --silent true`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigItems.class --methodname postInit --silent true`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigBlocks.class --methodname initializeBlocks --silent true`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigBlocks.class --methodname registerBlocks --silent true`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigBlocks.class --methodname registerTileEntities --silent true`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigEntities.class --methodname init --silent true`
- Material and metadata adjudication:
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/api/ThaumcraftApi.class --silent true`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/items/equipment/ItemPrimalCrusher.class --silent true`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/items/equipment/ItemCrimsonSword.class --silent true`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/items/ItemResource.class --methodname func_150895_a --silent true`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/items/ItemEldritchObject.class --methodname func_150895_a --silent true`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/items/wands/ItemWandCap.class --methodname func_150895_a --silent true`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/items/wands/ItemWandRod.class --methodname func_150895_a --silent true`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/blocks/BlockEldritch.class --methodname hasTileEntity --silent true`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/blocks/BlockEldritch.class --methodname createTileEntity --silent true`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/blocks/BlockMetalDevice.class --methodname createTileEntity --silent true`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/blocks/BlockEssentiaReservoir.class --methodname createTileEntity --silent true`
- Cultist constructor/material comparison used CFR `<init>` and `func_82789_a` for `ItemCultistRobeArmor`, `ItemCultistPlateArmor`, `ItemCultistLeaderArmor`, and `ItemCultistBoots`.
- Current source and tests were inspected with targeted repository reads and regex searches; no source was generated or modified.
- Tests/build/runtime smoke: not run because this was a read-only audit packet. No compile or runtime claim is made.

## Handoff

- Terminal status: complete
- Material finding index: A-009-F01 high cultist armor material substitutions; A-009-F02 high missing dedicated Crimson CVOID material; A-009-F03 low extra Void Robe boots registry entry; A-009-F04 low unconditional optional-metal wand-cap creative metas.
- Exact continuation point: Orchestrator may normalize all four defects, the positive parity controls, the persisted-extra-ID unknown, and the listed test debt into stable central `F-*` entries. No further A-009 investigation is required before normalization.
- Smallest next action if continued: Directly verify current runtime material statistics in one focused test fixture, then implement only after the central RECON/GOAL contract is frozen.
