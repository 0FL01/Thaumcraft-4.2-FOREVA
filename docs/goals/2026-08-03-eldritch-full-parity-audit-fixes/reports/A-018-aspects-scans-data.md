# Audit Packet: A-018 - Eldritch aspects and scan data

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Assignment-ID: A-018
Status: complete
Report-Revision: 1
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Compare Eldritch-specific object and entity aspect registrations, metadata and wildcard behavior, explicit versus recipe-derived tags, player scan aliases, phenomenon scan data, and the item/entity scan data consumed by the 16 TC4 `ELDRITCH` researches against Thaumcraft 4.2.3.5.
- Anti-scope: Generic scan-engine behavior except where needed to calculate an in-scope resolved `AspectList` or direct prerequisite effect; unrelated object tags; product edits; central Goal Ledger edits.
- Oracle and comparison direction: S-003/S-004 Thaumcraft 4.2.3.5 `ConfigAspects`, `ScanManager`, `ConfigResearch`, `ConfigRecipes`, `ThaumcraftCraftingManager`, and `ThaumcraftApi` -> S-005 Forge 1.12.2 port.
- Questions: Do target items, blocks, metadata values, wildcards, entities, player aliases, and amounts match? Do recipe-derived Eldritch outputs resolve to the same aspects after scaling and six-aspect culling? Do the registered lists preserve the scan prerequisites of Eldritch research triggers?
- Expected evidence: Fresh CFR/`javap` inspection of the exact TC4 methods; direct current-source comparison; exact derived-list arithmetic; focused existing-test inspection; command record.
- Read/write permissions: Product files and central ledger files read-only; only this report writable.
- Stop conditions: Every explicit Eldritch registration and scan-data entry is compared; each confirmed resolved-list delta has both-side causal evidence; derived outputs not fully resolved are recorded as test debt rather than claimed as parity.

## Coverage Performed

- Port aspect data: `src/main/java/thaumcraft/common/config/ConfigAspects.java`, including vanilla End objects, TC items/blocks, Void equipment preservation tags, and relevant entity tags.
- Port scan data and consumers: `src/main/java/thaumcraft/common/lib/research/ScanManager.java`, `src/main/java/thaumcraft/common/items/relics/ItemThaumometer.java`, `src/main/java/thaumcraft/common/lib/research/ResearchManager.java`, and `src/main/java/thaumcraft/common/config/research/ConfigResearchEldritch.java`.
- Port derivation inputs: `ConfigRecipesArcaneSlice`, `ConfigRecipesCrucibleSlice`, `ConfigRecipesInfusionSlice`, `ConfigRecipesInfusionEquipmentSlice`, `ConfigRecipesInfusionDeviceSlice`, `ThaumcraftCraftingManager`, `ThaumcraftApi`, `ThaumcraftApiHelper`, and `AspectList`.
- Oracle methods freshly inspected from S-003/S-004: `ConfigAspects.registerItemAspects`, `ConfigAspects.registerEntityAspects`, `ScanManager`, `ConfigResearch.initEldritchResearch`, the relevant `ConfigRecipes` initialization methods, `ThaumcraftCraftingManager` tag generation/culling inputs, and `ThaumcraftApi` tag registration/lookup.
- Exact MCP mappings used to adjudicate causal vanilla items include `field_151128_bU -> QUARTZ`, `field_151043_k -> GOLD_INGOT`, `field_151042_j -> IRON_INGOT`, `field_151045_i -> DIAMOND`, `field_151032_g -> ARROW`, and `field_151066_bu -> CAULDRON`.
- Existing focused tests inspected: `ConfigAspectsEldritchParityTest`, `ConfigAspectsAlchemyTagCoverageTest`, `ConfigAspectsStage9aCraftedOutputTagCoverageTest`, `ScanProgressionRuntimeTest`, `ScanManagerValidScanRuntimeTest`, and `ResearchManagerEntityTriggerMatchTest`.
- Excluded generic scan mechanics are owned by A-029. This report records only their direct data consequence: a non-primal scan aspect requires its two immediate components to be discovered.

## Result Summary

- Three production data defects are confirmed with high confidence.
- Explicit Eldritch object/block registrations, relevant entity registrations, player aliases, and research scan-trigger declarations otherwise match TC4.
- The main derived-data root is the port's invented explicit balanced-shard tag combined with broader vanilla material-tag differences. It changes `itemResource:14`, `itemResource:15`, `focusPrimal`, and `itemWandCap:7` even though the recipes and generation algorithms match.
- `blockMetalDevice:0` is independently under-tagged because the port replaced TC4's cauldron-derived list with an incorrect constant.

## Atomic Findings

### A-018-F01 - Focus Primal resolves to the wrong scan AspectList

- Type: defect
- Severity: high
- Confidence: high
- Source/oracle locator: S-003/S-004 `ConfigAspects.registerItemAspects`, `ConfigRecipes.initializeAlchemyRecipes`, `ConfigRecipes.initializeArcaneRecipes`, and `ThaumcraftCraftingManager.generateTagsFromCrucibleRecipes/generateTagsFromArcaneRecipes/getAspectsFromIngredients`; S-005 `ConfigAspects.java:133-211,311-355`, `ConfigRecipesCrucibleSlice.java:23-34`, `ConfigRecipesArcaneSlice.java:35-47,417-424`, `ThaumcraftCraftingManager.java:121-197,265-435`, and `ThaumcraftApiHelper.java:32-72`.
- Observed: The port's final scan list for `ConfigItems.focusPrimal` is `[CRYSTAL 27, FIRE 11, WATER 11, EARTH 11, ORDER 11, ENTROPY 11]`.
- Expected: TC4's final scan list is `[CRYSTAL 18, GREED 14, AIR 11, WATER 10, ORDER 10, ENTROPY 10]`.
- Effect: `ROD_primal_staff` uses `focusPrimal` as an item trigger at `ConfigResearchEldritch.java:333-335`. TC4 requires the immediate parents of `CRYSTAL` (`EARTH`, `ORDER`) and `GREED` (`MAN`, `HUNGER`) before that item can be scanned. The port list has no `GREED`, so the `MAN`/`HUNGER` prerequisite is lost. Awarded aspect pools also differ substantially.
- Candidate disposition: required.

#### Causal registration chain

The recipes and generation formulas match; the registered ingredients do not.

| Input | TC4 resolved/registered list | Port resolved/registered list | Cause |
|---|---|---|---|
| Balanced shard, `itemShard:6` | `[MAGIC 1, AIR 2, CRYSTAL 1, FIRE 1, WATER 1, EARTH 1, ORDER 1, ENTROPY 1]` | `[AIR 2, FIRE 2, WATER 2, EARTH 2, ORDER 2, ENTROPY 2, CRYSTAL 1]` | TC4 has no explicit meta-6 registration. With recipes initialized first, the first matching `BalancedShard_0` crucible recipe derives from air shard `[MAGIC 1, AIR 2, CRYSTAL 1]` and adds `floor(sqrt(2)) = 1` of each other primal. Port `ConfigAspects.java:352-353` invents an explicit symmetric list and omits `MAGIC`.
| Salis Mundus, `itemResource:14` | `[MAGIC 3, AIR 2, FIRE 1, WATER 1, EARTH 1, ORDER 1, ENTROPY 1]` | `[AIR 2, FIRE 2, WATER 2, EARTH 2, ORDER 2, ENTROPY 2, MAGIC 2]` | Both copy balanced-shard tags, add `MAGIC 2`, and remove `CRYSTAL`; the differing source list produces the exact delta.
| Gold ingot | `[METAL 3, GREED 2]` | `[METAL 6]` | TC4 explicit vanilla item registration versus port `ConfigAspects.java:175`.
| Diamond | `[CRYSTAL 4, GREED 4]` | `[CRYSTAL 8]` | TC4 `gemDiamond` registration versus port `ConfigAspects.java:176`.
| Nether quartz | `[CRYSTAL 1, ENERGY 1]` | `[]` | TC4 explicitly registers the quartz item. The port registers quartz ore at `ConfigAspects.java:58` and the `oreQuartz` dictionary at `:324`, but never `Items.QUARTZ`.

#### Exact intermediate and final lists

The TC4 and port Primal Charm recipes are the same: six elemental shards, one balanced shard, two gold ingots, and six primal recipe aspects at 25. Ingredient aspects are multiplied by `0.75` with integer truncation; each recipe aspect adds `floor(sqrt(25)) = 5`.

| Stage | TC4 | Port |
|---|---|---|
| `itemResource:15` Primal Charm | `[MAGIC 5, AIR 8, CRYSTAL 5, METAL 4, GREED 3, EARTH 7, FIRE 7, WATER 7, ORDER 7, ENTROPY 7]` | `[MAGIC 4, AIR 8, CRYSTAL 5, FIRE 8, WATER 8, METAL 9, EARTH 8, ORDER 8, ENTROPY 8]` |
| Focus ingredients before `0.75` | Charm + four quartz + four diamonds | Charm + four untagged quartz + four diamonds | 
| Focus raw list after ingredient scaling and six `+5` recipe additions | `[CRYSTAL 18, GREED 14, ENERGY 3, MAGIC 3, AIR 11, METAL 3, EARTH 10, FIRE 10, WATER 10, ORDER 10, ENTROPY 10]` | `[CRYSTAL 27, MAGIC 3, AIR 11, FIRE 11, WATER 11, METAL 6, EARTH 11, ORDER 11, ENTROPY 11]` |
| Final scan list after `ThaumcraftApiHelper.cullTags` | `[CRYSTAL 18, GREED 14, AIR 11, WATER 10, ORDER 10, ENTROPY 10]` | `[CRYSTAL 27, FIRE 11, WATER 11, EARTH 11, ORDER 11, ENTROPY 11]` |

TC4 traversal inserts `EARTH` and then `FIRE` before the remaining equal-valued primals are culled; current traversal leaves `FIRE` before the equal-valued set. The listed final six are therefore deterministic under each implementation's `LinkedHashMap` order and strict-less-than culling loop, not an unordered approximation.

- Regression hazards: Do not fix only `focusPrimal` with a direct tag. Restore causal ingredient data so Salis Mundus, the Primal Charm, the Void cap, and other consumers agree. Preserve the matching Focus recipe, output, research key, and six-aspect culling algorithm.

### A-018-F02 - The balanced-shard/Salis Mundus delta removes Eldritch from the Void cap

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: The balanced-shard and `itemResource:14` registrations in A-018-F01; S-003/S-004 `ConfigRecipes.initializeArcaneRecipes/initializeInfusionRecipes`; S-005 `ConfigRecipesArcaneSlice.java:305-312`, `ConfigRecipesInfusionSlice.java:45-56`, `Thaumcraft.java:369`, and the generation/culling methods cited above.
- Observed: The port's final `itemWandCap:7` scan list is `[ENTROPY 9, ORDER 9, FIRE 9, AIR 9, EARTH 6, MAGIC 6]`. It contains no `ELDRITCH`.
- Expected: TC4 resolves the finished Void cap to `[ENTROPY 6, ORDER 6, FIRE 6, AIR 9, MAGIC 9, ELDRITCH 4]`.
- Effect: Scanning the Void cap no longer requires the immediate `ELDRITCH` parents `VOID` and `DARKNESS`, awards no Eldritch pool, and awards inflated primal amounts instead. This is an Eldritch research output and a direct consequence of the same incorrect explicit balanced-shard baseline.
- Candidate disposition: required with A-018-F01.

#### Exact derivation

- Both sides register Void cap craft cost 9.
- The inert cap recipe consumes five Void nuggets. Their divided crafting value contributes zero; recipe aspects add `ENTROPY 5`, `ORDER 5`, `FIRE 4`, and `AIR 4`.
- The finished cap infusion consumes that inert cap and four `itemResource:14` stacks. Ingredient totals are scaled by `0.75`.
- Each finished-cap infusion aspect is 18, so `ENERGY`, `VOID`, `ELDRITCH`, and `AURA` each add `floor(sqrt(18)) = 4`.

| Stage | TC4 | Port |
|---|---|---|
| Four Salis Mundus stacks | `[MAGIC 12, AIR 8, FIRE 4, WATER 4, EARTH 4, ORDER 4, ENTROPY 4]` | `[AIR 8, FIRE 8, WATER 8, EARTH 8, ORDER 8, ENTROPY 8, MAGIC 8]` |
| Raw finished-cap list | `[ENTROPY 6, ORDER 6, FIRE 6, AIR 9, MAGIC 9, WATER 3, EARTH 3, ENERGY 4, VOID 4, ELDRITCH 4, AURA 4]` | `[ENTROPY 9, ORDER 9, FIRE 9, AIR 9, WATER 6, EARTH 6, MAGIC 6, ENERGY 4, VOID 4, ELDRITCH 4, AURA 4]` |
| Final scan list | `[ENTROPY 6, ORDER 6, FIRE 6, AIR 9, MAGIC 9, ELDRITCH 4]` | `[ENTROPY 9, ORDER 9, FIRE 9, AIR 9, EARTH 6, MAGIC 6]` |

- Regression hazards: Preserve the exact matching inert/finished cap recipes, cap cost 9, infusion additions, and generic culling. Correct the source registrations rather than exempting `ELDRITCH` from culling.

### A-018-F03 - `blockMetalDevice:0` has METAL 4 instead of the TC4-derived METAL 21

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-003/S-004 `ConfigAspects.registerItemAspects` statement constructing `blockMetalDevice` from `Items.field_151066_bu` (`CAULDRON`); S-003/S-004 `ThaumcraftCraftingManager.getAspectsFromIngredients`; S-005 `ConfigAspects.java:393` and `ThaumcraftCraftingManager.java:411-427`.
- Observed: Port registration and final scan list are `[METAL 4, CRAFT 4, MAGIC 4]`.
- Expected: TC4 registration and final scan list are `[METAL 21, CRAFT 4, MAGIC 4]`.
- Exact cause: TC4 executes `new AspectList(new ItemStack(Items.CAULDRON, 1, Short.MAX_VALUE)).add(CRAFT, 4).add(MAGIC, 4)`. The cauldron recipe uses seven iron ingots. Iron is `[METAL 4]`, and `floor(7 * 4 * 0.75) = 21`. The port replaced that causal copy with hard-coded `METAL 4` while retaining the two added aspects.
- Effect: Scanning or decomposing the base Metal Device returns 17 fewer Metal points. Compound parent types are unchanged, so this does not alter the direct `OUTERREV`, `PRIMPEARL`, or primal-staff trigger prerequisites. `blockMetalDevice:3` remains recipe-derived and is not overridden by the meta-0 registration.
- Candidate disposition: required.
- Regression hazards: Preserve metadata specificity: fix only the default/meta-0 registration and do not wildcard it across all Metal Device metas. A direct `[METAL 21, CRAFT 4, MAGIC 4]` registration or an equivalent safe cauldron derivation preserves the TC4 data.

## Positive Parity

### A-018-PC01 - Explicit vanilla and Thaumcraft object data

Unless identified as a defect above, the following in-scope explicit registrations match exactly. `Short.MAX_VALUE` and `OreDictionary.WILDCARD_VALUE` both equal metadata 32767.

| Target | Exact AspectList | Metadata semantics |
|---|---|---|
| Ender Pearl | `[ELDRITCH 4, MAGIC 2, TRAVEL 4]` | default |
| Far music disc | `[SENSES 4, AIR 4, ELDRITCH 4, GREED 4]` | default |
| Nether Star | `[ELDRITCH 8, MAGIC 8, ORDER 8, LIGHT 8]` | default |
| Dragon Egg | `[ELDRITCH 8, BEAST 8, MAGIC 8]` | default |
| End Portal Frame | `[ELDRITCH 4, MECHANISM 4, TRAVEL 4]` | wildcard |
| `blockCosmeticSolid:0` | `[EARTH 4, DARKNESS 2, ELDRITCH 2]` | exact/default |
| `blockCosmeticSolid:11`, `:12` | `[EARTH 1, ELDRITCH 1]` each | exact |
| Primal Arrow | recipe-derived list augmented with `WEAPON 1`; final registered data remains `WEAPON 1` | complex/default |
| `blockWoodenDevice:1` | recipe-derived list augmented with `SENSES 4` | exact/complex |
| `blockWoodenDevice:8` | `[ELDRITCH 1, TREE 2, CLOTH 3]` | exact |
| Pech focus | `[MAGIC 5, POISON 5, ENTROPY 5, ELDRITCH 5, WEAPON 5]` | default |
| Cultist plate head/chest/legs | `[METAL 5, ELDRITCH 1]` each | wildcard damage |
| Cultist robe head/chest/legs | `[METAL 3, CLOTH 2, ELDRITCH 1]` each | wildcard damage |
| Cultist leader head/chest/legs | `[METAL 5, ELDRITCH 2]` each | wildcard damage |
| Cultist boots | `[METAL 4, ELDRITCH 1]` | wildcard damage |
| `itemEldritchObject:0` | `[ELDRITCH 5, AURA 3, MAGIC 3, SENSES 3, SOUL 3]` | exact |
| `itemEldritchObject:1` | `[MIND 5, MAGIC 3, ELDRITCH 3, SOUL 3]` | exact |
| `itemEldritchObject:2` | `[TRAP 4, MIND 4, MECHANISM 4]` | exact |
| `itemEldritchObject:3` | `[AIR 16, EARTH 16, FIRE 16, WATER 16, ORDER 16, ENTROPY 16]` | exact |
| `blockEldritch:*` | `[VOID 8, ELDRITCH 8, SENSES 4]` | wildcard fallback, including meta 10 |
| `blockEldritch:3` | `[VOID 4, ELDRITCH 4]` | exact overrides wildcard |
| `blockEldritch:4` | `[LIGHT 1, EARTH 1, ELDRITCH 1]` | exact overrides wildcard |
| `blockEldritch:5` | `[MIND 2, EARTH 1, ELDRITCH 1]` | exact overrides wildcard |
| `blockEldritch:6` | `[METAL 2, MECHANISM 2, ELDRITCH 1]` | exact overrides wildcard |
| Eldritch Portal block | `[VOID 8, ELDRITCH 8, TRAVEL 8]` | default |

- `itemEldritchObject:4` has no explicit tag on either side.
- Exact/grouped registrations are checked before wildcard data on both sides. Consequently metas 3-6 resolve to their exact lists, while `OUTERREV` meta 10 resolves to the wildcard list.

### A-018-PC02 - Void Seed, Void ingot, and base Void equipment

- Both sides derive Void Seed from Ender Pearl plus crucible aspects `DARKNESS 8`, `VOID 8`, `ELDRITCH 2`, producing `[ELDRITCH 5, MAGIC 2, TRAVEL 4, DARKNESS 2, VOID 2]`.
- Both sides derive Void ingot from Void Seed plus `METAL 8`, producing `[ELDRITCH 5, MAGIC 2, TRAVEL 4, DARKNESS 2, VOID 2, METAL 2]`.
- TC4 derives base Void armor/tools from crafting recipes. The port's direct `getVoidEquipmentRecipeTags` registrations reproduce the same ingredient counts and `0.75` scaling so that Forge recipe-registry differences do not erase the original data.

| Item | Registered base AspectList before intrinsic item bonus |
|---|---|
| Void helmet | `[ELDRITCH 18, MAGIC 7, TRAVEL 15, DARKNESS 7, VOID 7, METAL 7]` |
| Void chestplate | `[ELDRITCH 30, MAGIC 12, TRAVEL 24, DARKNESS 12, VOID 12, METAL 12]` |
| Void leggings | `[ELDRITCH 26, MAGIC 10, TRAVEL 21, DARKNESS 10, VOID 10, METAL 10]` |
| Void boots | `[ELDRITCH 15, MAGIC 6, TRAVEL 12, DARKNESS 6, VOID 6, METAL 6]` |
| Void sword | `[ELDRITCH 7, MAGIC 3, TRAVEL 6, DARKNESS 3, VOID 3, METAL 3]` |
| Void pickaxe / axe | `[ELDRITCH 11, MAGIC 4, TRAVEL 9, DARKNESS 4, VOID 4, METAL 4, TREE 1]` |
| Void shovel | `[ELDRITCH 3, MAGIC 1, TRAVEL 3, DARKNESS 1, VOID 1, METAL 1, TREE 1]` |
| Void hoe | `[ELDRITCH 7, MAGIC 3, TRAVEL 6, DARKNESS 3, VOID 3, METAL 3, TREE 1]` |

`ConfigAspectsEldritchParityTest` additionally proves that armor/tool intrinsic bonuses and six-aspect culling match the TC4 derivation for all nine items.

### A-018-PC03 - Relevant entity aspect data

Namespaced 1.12 entity keys are a benign registry adaptation. Exact aspect values match:

| TC4 entity / port key | Exact AspectList |
|---|---|
| `Enderman` / `minecraft:enderman` | `[ELDRITCH 4, TRAVEL 2, AIR 2]` |
| `Blaze` / `minecraft:blaze` | `[ELDRITCH 4, FIRE 1]` |
| `EnderDragon` / `minecraft:ender_dragon` | `[ELDRITCH 20, BEAST 20, ENTROPY 20]` |
| `EnderCrystal` / `minecraft:ender_crystal` | `[ELDRITCH 3, MAGIC 3, HEAL 3]` |
| `Thaumcraft.PrimalOrb` / `thaumcraft:primalorb` | `[AIR 5, ENTROPY 10, MAGIC 10, ENERGY 10]` |
| `Thaumcraft.EldritchGuardian` / `thaumcraft:eldritchguardian` | `[ELDRITCH 4, DEATH 2, UNDEAD 4]` |
| `Thaumcraft.EldritchOrb` / `thaumcraft:eldritchorb` | `[ELDRITCH 2, DEATH 2]` |
| `Thaumcraft.CultistKnight` / `thaumcraft:cultistknight` | `[ELDRITCH 1, MAN 2, ENTROPY 1]` |
| `Thaumcraft.CultistCleric` / `thaumcraft:cultistcleric` | `[ELDRITCH 1, MAN 2, ENTROPY 1]` |

- TC4 has no explicit `ConfigAspects` registration for Eldritch Warden, Eldritch Golem, Eldritch Crab, Cultist Leader, Cultist Portal, or the other later Outer Lands entities. Their absence from the port's aspect table is parity, not an omission.
- `ResearchManager.entityTriggerMatches` expands legacy `namespace.path`, modern `namespace:path`, and short forms case-insensitively. Therefore the original `Thaumcraft.PrimalOrb` research trigger matches the port registry key `thaumcraft:primalorb`.

### A-018-PC04 - Player aliases and End Portal scan adaptation

- Named player data matches exactly: `azanor` -> `[MAN 4, ELDRITCH 20]`; `direwolf20` -> `[MAN 4, BEAST 20]`; `pahimar` -> `[MAN 4, EXCHANGE 20]`.
- Other players retain the original deterministic name-seeded selection of three aspects at 4 each in addition to `MAN 4`.
- TC4 explicitly tags the End Portal block wildcard `[ELDRITCH 4, TRAVEL 4]`; its `ScanManager.scanPhenomena` otherwise returns `null` and has no separate Eldritch phenomenon table.
- Vanilla End Portal has no usable ItemBlock scan route in 1.12. The port preserves the same data as phenomenon key `BLOCK:minecraft:end_portal`: `ItemThaumometer.java:182-185` emits type 3, and `ScanManager.java:319-321` resolves `[ELDRITCH 4, TRAVEL 4]`.
- This is a legitimate itemless-block adaptation. It does not invent new aspect values or a new research trigger.

### A-018-PC05 - Eldritch research scan-trigger declarations

The complete explicit trigger set matches TC4:

| Research | Trigger | Resolved data / direct prerequisite consequence |
|---|---|---|
| `OUTERREV` | `blockEldritch:5` | `[MIND 2, EARTH 1, ELDRITCH 1]`; compound parents `FIRE + SOUL` for MIND and `VOID + DARKNESS` for ELDRITCH |
| `OUTERREV` | `blockEldritch:10` | wildcard `[VOID 8, ELDRITCH 8, SENSES 4]`; parents `AIR + ENTROPY`, `VOID + DARKNESS`, and `AIR + SOUL` |
| `PRIMPEARL` | `itemEldritchObject:3` | six primals at 16; no compound-parent prerequisite |
| `ROD_primal_staff` | entity `Thaumcraft.PrimalOrb` | `[AIR 5, ENTROPY 10, MAGIC 10, ENERGY 10]`; compound parents `VOID + ENERGY` for MAGIC and `ORDER + FIRE` for ENERGY |
| `ROD_primal_staff` | `focusPrimal` | Declaration matches, but resolved data is defective as A-018-F01 records |

No additional explicit Eldritch item/entity scan trigger exists in TC4 `initEldritchResearch`, and none was added by the port.

### A-018-PC06 - Lookup and derivation mechanics used by this data

- Both sides initialize recipes before aspects/research, so recipe-derived registrations see the same lifecycle stage.
- Both look up exact metadata, grouped metadata, then wildcard; exact `blockEldritch` metas override its wildcard.
- Both normalize generated metadata for non-subtyped/damageable items, prevent recursive recipe cycles, and prefer Crucible -> Arcane -> Infusion -> vanilla crafting derivation.
- Both aggregate one copy of every recipe ingredient, multiply ingredient totals by `0.75 / outputCount` with integer truncation, add `floor(sqrt(recipeAspect) / outputCount)`, remove zero entries, cap individual generated amounts at 64, add intrinsic item bonuses, and cull to six aspects.
- These matching algorithms are positive controls: A-018-F01/F02 are caused by different registrations feeding the same algorithm, not by a speculative scan-engine difference.

## Legitimate Adaptations To Preserve

- Preserve namespaced lowercase 1.12 entity keys and the legacy trigger alias matcher.
- Preserve the End Portal phenomenon route because vanilla 1.12 has no equivalent ItemBlock target; preserve exact key and `[ELDRITCH 4, TRAVEL 4]` data.
- Preserve `OreDictionary.WILDCARD_VALUE` in place of `Short.MAX_VALUE`; both are 32767.
- Preserve direct Void equipment registrations that intentionally materialize the exact TC4 recipe-derived lists.
- Preserve exact-over-wildcard precedence for Eldritch block metadata.
- Preserve capability-based scan persistence and server-authoritative award handling; those mechanisms are outside this data correction.

## Unknowns and Test Debt

- `ConfigAspectsEldritchParityTest` verifies only Ender Pearl, Far disc, Nether Star, Dragon Egg, End Portal Frame, and final base Void equipment. It does not resolve any of the three defects above.
- `ConfigAspectsAlchemyTagCoverageTest` checks source-string presence for balanced shard, Salis Mundus, and Metal Device but not values. It positively requires the incorrect explicit balanced-shard line and passes with `METAL 4`.
- No behavior test asserts exact raw/final lists for balanced shard, `itemResource:14`, `itemResource:15`, `focusPrimal`, `itemWandCap:7`, or `blockMetalDevice:0`.
- `ScanProgressionRuntimeTest` checks the End Portal target type/key/aspects but not full completion with its compound-parent prerequisites.
- `ResearchManagerEntityTriggerMatchTest` covers the Primal Orb legacy/current alias. There is no table-driven test for every in-scope entity registration/value.
- Recipe declarations for `AdvAlchemyConstruct`, Primal Crusher, Essentia Reservoir, Void Robe helm/chest/legs, Sanity Checker, and the primal staff rod match TC4, as established by A-006/A-007/A-008. Their complete final recipe-derived scan lists were not independently locked by this assignment. Because several consume Salis Mundus, Primal Charm, Void material, or other broader baseline tags, recipe-definition parity alone must not be promoted as resolved AspectList parity.
- No source conflict remains for A-018-F01 through F03. The unresolved items above are coverage/test debt, not contradictory evidence.

## Recommended Regression Evidence

- Add one focused runtime fixture that initializes the actual recipes and aspect table, then asserts both raw registered/generated tags and final scan tags for balanced shard, Salis Mundus, Primal Charm, Focus Primal, Void cap, and Metal Device meta 0.
- Assert Focus Primal and Void cap direct-parent prerequisite sets, not only amounts.
- Keep positive controls for End Portal phenomenon data, exact/wildcard Eldritch block precedence, all relevant entity values, and Void base equipment final culling.
- Do not encode fixes as source-string tests; exercise `ThaumcraftCraftingManager.getObjectTags`, `getBonusTags`, and `ScanManager.getScanAspects`.

## Commands and Results

Commands were run from the repository root unless a path is explicit.

```text
git status --short
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigAspects.class --methodname registerItemAspects --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigAspects.class --methodname registerEntityAspects --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/lib/research/ScanManager.class --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigResearch.class --methodname initEldritchResearch --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.class --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/api/ThaumcraftApi.class --silent true
javap -classpath thaumcraft_src -c -p thaumcraft.common.config.ConfigAspects
javap -classpath thaumcraft_src -c -p thaumcraft.common.lib.research.ScanManager
./scripts/dev.sh test
git status --short
```

- CFR/bytecode result: pass. Exact original registrations, scan aliases/data, research triggers, recipe declarations, and derivation methods were recovered. Missing dependency names in decompilation did not obscure the relevant statements.
- MCP mapping adjudication: pass. Relevant obfuscated vanilla fields were checked against the available 1.7.10/stable mapping data.
- Test result: `./scripts/dev.sh test` passed, `BUILD SUCCESSFUL in 7s`, 9 actionable tasks, 3 executed and 6 up-to-date.
- Product diff: none.
- Build: not run. This packet changes documentation only; no product jar was made stale.
- Runtime smoke: not required and not run because no common/server product path changed.
- Manual client validation: not applicable.
- Commit: none.
- Worktree note: the goal directory and `.opencode/active-goal` were already untracked orchestrator material. This assignment edited only this report and did not modify central ledger files.

## Handoff

- Terminal status: complete.
- Material finding index: `A-018-F01` high Focus Primal resolved-list and trigger-prerequisite mismatch; `A-018-F02` medium Salis Mundus/Void-cap mismatch removing Eldritch; `A-018-F03` medium Metal Device meta-0 METAL 4 versus 21.
- Positive parity index: `A-018-PC01` explicit object/block values and wildcard precedence; `A-018-PC02` Void Seed/ingot/base equipment; `A-018-PC03` relevant entity values and absent later-entity tags; `A-018-PC04` player aliases and End Portal adaptation; `A-018-PC05` complete Eldritch trigger table; `A-018-PC06` lookup/derivation mechanics.
- Exact continuation point: Orchestrator may normalize the three findings, six preserve controls, and listed test debt into the central ledger. No further A-018 source investigation is required before normalization.
- Smallest implementation checkpoint: Correct causal vanilla/balanced-shard/Salis Mundus registrations and Metal Device meta 0 with focused resolved-list tests, then validate common/server behavior with the goal's required smoke gate.
