# Stage 9-d — Gap-анализ и план закрытия

## 1. Цель фазы

Stage 9-d закрывает только crucible/alchemy content flow: регистрацию crucible recipes, связь recipe IDs/names с research gates, matching/output behavior для `TileCrucible`, программирование и потребление crucible recipes в `TileThaumatorium`, а также alchemy furnace/smelting-side данные только там, где референс регистрирует их как alchemy content data.

Цель не включает arcane/infusion/general smelting/research pages целиком, кроме прямых зависимостей, которые блокируют crucible/alchemy recipes.

## 2. Scope фазы

В scope Stage 9-d входят:

- API и менеджер: `src/main/java/thaumcraft/api/crafting/CrucibleRecipe.java`, `src/main/java/thaumcraft/api/ThaumcraftApi.java`, `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java`.
- Crucible runtime content behavior: `src/main/java/thaumcraft/common/tiles/TileCrucible.java`.
- Thaumatorium recipe programming/consumption only where it consumes crucible recipes: `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java`, `src/main/java/thaumcraft/common/tiles/TileThaumatoriumTop.java`, `src/main/java/thaumcraft/common/container/ContainerThaumatorium.java`.
- Alchemy/crucible content registration: `src/main/java/thaumcraft/common/config/ConfigRecipes.java`, direct dependency `src/main/java/thaumcraft/common/config/ConfigResearch.java`, direct dependency `src/main/java/thaumcraft/common/config/ConfigAspects.java`.
- Lifecycle hook that should invoke recipe/aspect/research registration: `src/main/java/thaumcraft/common/Thaumcraft.java`.
- Alchemy content resources only if they are required by recipe outputs/catalysts and are already represented by registered items/blocks under `ConfigItems`/`ConfigBlocks`.

Out of scope:

- General arcane/infusion recipe parity, except research/crafting references that directly gate Stage 9-d recipes.
- General smelting parity, except alchemical smelting bonuses registered by the reference in `ConfigRecipes`.
- Stage 8 client visuals. Dependency note: `TileCrucible.receiveClientEvent` has Phase 8 visual TODOs at `src/main/java/thaumcraft/common/tiles/TileCrucible.java:510-518`, but those do not change crucible recipe data/matching.

## 3. Источники сравнения

Текущая реализация:

- `AGENTS.md:5-16`, `AGENTS.md:111-122`, `AGENTS.md:155-173` define source-of-truth, workflow, and runtime validation expectations.
- `docs/PRD.md:3-18`, `docs/PRD.md:57-69`, `docs/PRD.md:160-171`, `docs/PRD.md:173-185`, `docs/PRD.md:526-526` define parity goals, stable contracts, methodology, status model, and crucible smoke expectation.
- `build.gradle:24-29` confirms Java 8 / Forge 1.12.2 / stable_39 target.
- `Dockerfile:61-69` provides CFR for reference class inspection.
- `src/main/java/thaumcraft/common/config/ConfigRecipes.java:7-13` is the current recipe registration stub.
- `src/main/java/thaumcraft/common/config/ConfigResearch.java:5-7` is the current research registration stub.
- `src/main/java/thaumcraft/common/config/ConfigAspects.java:12-16` initializes only current aspect groups.
- `src/main/java/thaumcraft/common/Thaumcraft.java:188-190` invokes `ConfigRecipes.init()`, `ConfigAspects.init()`, and `ConfigResearch.init()`.
- `src/main/java/thaumcraft/api/crafting/CrucibleRecipe.java:17-78` implements recipe data, catalyst matching, aspect matching/removal, and output access.
- `src/main/java/thaumcraft/api/ThaumcraftApi.java:129-149` registers and looks up crucible recipes.
- `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:272-283` generates object tags from crucible recipes.
- `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:468-497` matches crucible recipes by research, catalyst, aspects, and specificity.
- `src/main/java/thaumcraft/common/tiles/TileCrucible.java:328-402` consumes catalyst/aspects/water and emits crucible output.
- `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java:56-107`, `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java:124-159`, `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java:184-238`, `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java:490-493` implement thaumatorium recipe execution, completion, persistence, and redstone stop behavior.
- `src/main/java/thaumcraft/common/container/ContainerThaumatorium.java:72-119` implements recipe list/programming by crucible recipe hash.

Reference material:

- `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`, decompiled with CFR, contains `initializeAlchemyRecipes()` and calls it from recipe init. Relevant decompiled blocks: `initializeAlchemyRecipes()` registers Balanced Shard, Alumentum, Nitor, Thaumium, Void Metal/Seed, Tallow, alchemical duplication/manufacture/entropic processing, native clusters, transmutation nuggets, Ethereal Bloom, Liquid Death, Bottled Taint, golems, golem cores, Bath Salts, and Sane Soap; smelting bonus registrations are in the same class.
- `thaumcraft_src/thaumcraft/api/crafting/CrucibleRecipe.class`, decompiled with CFR, matches current `CrucibleRecipe` structure and methods.
- `thaumcraft_src/thaumcraft/api/ThaumcraftApi.class`, decompiled with CFR, matches current `addCrucibleRecipe`, `getCrucibleRecipe`, and `getCrucibleRecipeFromHash` shape.
- `thaumcraft_src/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.class`, decompiled with CFR, contains reference `findMatchingCrucibleRecipe()` and `generateTagsFromCrucibleRecipes()`.
- `thaumcraft_src/thaumcraft/common/tiles/TileCrucible.class`, decompiled with CFR, contains reference `attemptSmelt()` behavior.
- `thaumcraft_src/thaumcraft/common/tiles/TileThaumatorium.class`, decompiled with CFR, contains reference crucible-recipe programming/consumption and `getUpgrades()` behavior.
- `thaumcraft_src/thaumcraft/common/container/ContainerThaumatorium.class`, decompiled with CFR, contains reference thaumatorium recipe list/programming behavior.
- `thaumcraft_src/thaumcraft/common/config/ConfigAspects.class`, decompiled with CFR, contains reference Thaumcraft item/block aspect tags used by crucible decomposition and recipe generated aspects.

Lightweight commands used for analysis:

- `git status --short`
- `rg` via grep/glob tools for `Crucible`, `Thaumatorium`, `addCrucibleRecipe`, `ConfigRecipes.init`, `ConfigResearch.init`, and Stage 9 TODOs.
- `cfr --silent true thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`
- `cfr --silent true thaumcraft_src/thaumcraft/common/config/ConfigAspects.class`
- `cfr --silent true thaumcraft_src/thaumcraft/api/crafting/CrucibleRecipe.class`
- `cfr --silent true thaumcraft_src/thaumcraft/api/ThaumcraftApi.class`
- `cfr --silent true thaumcraft_src/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.class`
- `cfr --silent true thaumcraft_src/thaumcraft/common/tiles/TileCrucible.class`
- `cfr --silent true thaumcraft_src/thaumcraft/common/tiles/TileThaumatorium.class`
- `cfr --silent true thaumcraft_src/thaumcraft/common/container/ContainerThaumatorium.class`

No build or runtime validation was run; this task is documentation-only gap analysis.

## 4. Текущее состояние Stage 9-d

API/runtime baseline exists but content data is not registered.

- `CrucibleRecipe` is structurally ported and matches the reference API behavior: research key, output, catalyst as `ItemStack` or ore dictionary `ArrayList`, `AspectList`, hash generation, `matches`, `catalystMatches`, `removeMatching`, and `getRecipeOutput` are present at `src/main/java/thaumcraft/api/crafting/CrucibleRecipe.java:17-78`.
- `ThaumcraftApi.addCrucibleRecipe`, `getCrucibleRecipe`, and `getCrucibleRecipeFromHash` are present at `src/main/java/thaumcraft/api/ThaumcraftApi.java:129-149`.
- `ThaumcraftCraftingManager.findMatchingCrucibleRecipe` follows the reference matching model: research gate, single-item catalyst copy, aspect/catalyst match, and highest aspect-type-count priority at `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:468-497`.
- `TileCrucible.attemptSmelt` follows the reference recipe/output loop closely: reads thrower, finds recipe, fires crafting event, removes aspects, drains 50 mB water, ejects output, otherwise decomposes item aspects at `src/main/java/thaumcraft/common/tiles/TileCrucible.java:328-402`.
- `TileThaumatorium` has a functional baseline for stored crucible recipe hashes, required essentia, input catalyst, output inventory insertion, essentia suction, and completion at `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java:56-159`.
- `ContainerThaumatorium` lists/programs `CrucibleRecipe` entries by research completion, catalyst match, and recipe hash at `src/main/java/thaumcraft/common/container/ContainerThaumatorium.java:72-119`.
- `ConfigRecipes.init()` is a stub and does not register any recipes at `src/main/java/thaumcraft/common/config/ConfigRecipes.java:7-9`.
- `ConfigResearch.init()` is a stub and there is no current `ConfigResearch.recipes` map equivalent visible in source at `src/main/java/thaumcraft/common/config/ConfigResearch.java:5-7`.
- `ConfigAspects` registers a limited vanilla/ore-dictionary set, but no Thaumcraft item/block aspect tags needed by the reference alchemy chain appear in `src/main/java/thaumcraft/common/config/ConfigAspects.java:12-191`.

Result: Stage 9-d cannot be considered complete. The crucible machinery can compile and has partial behavior, but the actual alchemy recipe graph, research recipe IDs, and several alchemy data dependencies are absent or unverified.

## 5. Gap list

### GAP-1: Crucible/alchemy recipe registration is absent

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigRecipes.java:7-9`
- `src/main/java/thaumcraft/common/Thaumcraft.java:188-190`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`, method `init()` calls `initializeAlchemyRecipes()`.
- `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`, method `initializeAlchemyRecipes()` registers the full crucible/alchemy recipe set with `ThaumcraftApi.addCrucibleRecipe(...)` and stores each recipe in `ConfigResearch.recipes`.

**Что не совпадает:**

Current `ConfigRecipes.init()` contains only `// Phase 9: register all recipes`, so `ThaumcraftApi.getCraftingRecipes()` receives no Stage 9-d `CrucibleRecipe` entries from the port. The reference registers roughly 54 alchemy/crucible entries including conditional ore-mod recipes:

- `BalancedShard_0..5` under research `CRUCIBLE`.
- `Alumentum`, `Nitor`, `Thaumium`, `VoidMetal`, `VoidSeed`, `Tallow`.
- `AltGunpowder`, `AltSlime`, `AltClay`, `AltGlowstone`, `AltInk`, `AltWeb`, `AltMossyCobble`, `AltIce`, `AltCrackedBrick`, `AltBonemeal`.
- `PureIron`, `PureGold`, optional `PureCopper`, `PureTin`, `PureSilver`, `PureLead`.
- `TransIron`, `TransGold`, optional `TransCopper`, `TransTin`, `TransSilver`, `TransLead`.
- `EtherealBloom`, `LiquidDeath`, `BottleTaint`.
- `GolemStraw`, `GolemWood`, `GolemTallow`, `GolemClay`, `GolemFlesh`, `GolemStone`, `GolemIron`, `GolemThaumium`.
- `CoreGather`, `CoreFill`, `CoreEmpty`, `CoreHarvest`, `CoreGuard`, `CoreButcher`, `CoreLiquid`.
- `BathSalts`, `SaneSoap`.

Without these registrations, `ThaumcraftCraftingManager.findMatchingCrucibleRecipe()` at `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:468-497`, `ThaumcraftApi.getCrucibleRecipe()` at `src/main/java/thaumcraft/api/ThaumcraftApi.java:135-141`, and `ContainerThaumatorium.updateRecipes()` at `src/main/java/thaumcraft/common/container/ContainerThaumatorium.java:72-86` have no Stage 9-d recipe data to operate on.

**Что нужно доделать:**

Port the reference `initializeAlchemyRecipes()` data into the 1.12.2 `ConfigRecipes.init()` flow, adapting item/block constants to the current 1.12.2 classes while preserving research keys, map keys, catalysts, outputs, aspect costs, and conditional ore-mod gates.

**Как доделать:**
- Add a private `initializeAlchemyRecipes()` or equivalent in `src/main/java/thaumcraft/common/config/ConfigRecipes.java`.
- Call it from `ConfigRecipes.init()` before research pages consume recipe handles.
- Use `ThaumcraftApi.addCrucibleRecipe(String key, ItemStack result, Object catalyst, AspectList tags)` exactly as current API provides at `src/main/java/thaumcraft/api/ThaumcraftApi.java:129-132`.
- Store each returned recipe under the original recipe ID/name in the research recipe map once GAP-2 provides it.
- Preserve ore dictionary catalysts such as `dustGlowstone`, `oreIron`, `oreGold`, `oreCopper`, `nuggetCopper`, etc.
- Adapt vanilla 1.7.10 items to 1.12.2 equivalents without changing output IDs or research keys.
- Use current output/catalyst classes from `src/main/java/thaumcraft/common/config/ConfigItems.java:68`, `src/main/java/thaumcraft/common/config/ConfigItems.java:145-163`, and `src/main/java/thaumcraft/common/config/ConfigBlocks.java:25-38`.

**Критерии приемки:**
- [ ] `ConfigRecipes.init()` registers every reference Stage 9-d crucible recipe and no longer contains the Phase 9 stub for this scope.
- [ ] `ThaumcraftApi.getCraftingRecipes()` contains all expected `CrucibleRecipe` entries after common init in a fresh game load.
- [ ] Every reference recipe key/name, research gate, catalyst, output, and aspect cost has a current 1.12.2 equivalent documented in code or tests.
- [ ] Optional copper/tin/silver/lead recipes are gated by the same ore-detection config semantics as the reference.

**Риски / зависимости:**

Depends on GAP-2 because reference recipes are also stored in `ConfigResearch.recipes`. Depends on GAP-5 because missing object/aspect tags can make catalysts and generated aspect data wrong even after recipe registration. Runtime smoke/manual checks are required because recipe data can compile but be unusable.

### GAP-2: Research recipe map and crucible recipe gates are absent

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigResearch.java:5-7`
- `src/main/java/thaumcraft/common/config/ConfigRecipes.java:7-9`
- `src/main/java/thaumcraft/common/container/ContainerThaumatorium.java:88-90`
- `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:481-485`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`, `initializeAlchemyRecipes()` stores returned recipes as `ConfigResearch.recipes.put("RecipeName", ThaumcraftApi.addCrucibleRecipe(...))`.
- `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`, recipe research gates include `CRUCIBLE`, `ALUMENTUM`, `NITOR`, `THAUMIUM`, `VOIDMETAL`, `TALLOW`, `ALCHEMICALDUPLICATION`, `ALCHEMICALMANUFACTURE`, `ENTROPICPROCESSING`, pure/transmutation research keys, golem keys, `BATHSALTS`, and `SANESOAP`.
- `thaumcraft_src/thaumcraft/common/container/ContainerThaumatorium.class`, `updateRecipes()` only lists recipes when `ResearchManager.isResearchComplete(playerName, recipe.key)` passes or when already programmed.
- `thaumcraft_src/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.class`, `findMatchingCrucibleRecipe()` requires research completion before a crucible craft can match.

**Что не совпадает:**

Current `ConfigResearch.init()` is empty and there is no visible current `ConfigResearch.recipes` map. Current matching code already enforces research completion at `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:481-485`, and thaumatorium programming enforces it at `src/main/java/thaumcraft/common/container/ContainerThaumatorium.java:88-90`. Therefore, even after GAP-1 registers recipes, missing research entries/gates/pages can leave recipes undiscoverable, unprogrammable, or permanently locked.

**Что нужно доделать:**

Restore the reference recipe-handle map and direct crucible recipe research gates needed by Stage 9-d, without deep-porting unrelated research page content beyond what is needed to expose and unlock crucible/alchemy recipes.

**Как доделать:**
- Add the current equivalent of `ConfigResearch.recipes` in `src/main/java/thaumcraft/common/config/ConfigResearch.java` if absent.
- Store each Stage 9-d recipe handle from `ConfigRecipes.initializeAlchemyRecipes()` under the exact reference map key, for example `BalancedShard_0`, `Alumentum`, `Nitor`, `Thaumium`, `VoidMetal`, `VoidSeed`, `Tallow`, `PureIron`, `TransIron`, `EtherealBloom`, `LiquidDeath`, `BottleTaint`, `CoreGather`, `BathSalts`, `SaneSoap`.
- Ensure `ResearchManager.isResearchComplete(...)` receives the original research keys used by each `CrucibleRecipe.key`.
- Ensure research pages that directly display crucible recipes can resolve the stored recipe handles; `ThaumcraftApi.getCraftingRecipeKey()` already scans `ResearchPage.recipe` arrays for `CrucibleRecipe[]` at `src/main/java/thaumcraft/api/ThaumcraftApi.java:167-174`.
- Keep this limited to Stage 9-d recipe visibility/unlock flow; do not deep-port unrelated arcane/infusion pages here.

**Критерии приемки:**
- [ ] Every Stage 9-d crucible recipe has the same recipe map key and research gate as the reference.
- [ ] A player with the relevant research can craft the recipe in a crucible and see/program it in a thaumatorium.
- [ ] A player without the relevant research cannot match the recipe in `TileCrucible` and cannot newly program it in `ContainerThaumatorium`.
- [ ] `ThaumcraftApi.getCraftingRecipeKey()` can resolve direct crucible outputs that are shown by research pages.

**Риски / зависимости:**

This is a blocker because current runtime matching already depends on research completion. It depends on Stage 9 research baseline outside this chunk only to the extent that the relevant research keys can be granted and queried. If broader research pages remain incomplete, Stage 9-d can still close only if direct recipe gates and unlock scenarios are manually verified.

### GAP-3: Thaumatorium brainbox recipe-capacity upgrades are missing

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java:45`
- `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java:56-107`
- `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java:464-477`
- `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java:490-493`
- `src/main/java/thaumcraft/common/container/ContainerThaumatorium.java:106-110`

**Референс:**
- `thaumcraft_src/thaumcraft/common/tiles/TileThaumatorium.class`, `updateEntity()` calls `getUpgrades()` every 40 ticks with heat check.
- `thaumcraft_src/thaumcraft/common/tiles/TileThaumatorium.class`, `getUpgrades()` scans adjacent `blockMetalDevice` meta 12 `TileBrainbox` blocks facing the thaumatorium and increases `maxRecipes` by 2 per valid upgrade, pruning stored recipe hashes if capacity drops.
- `thaumcraft_src/thaumcraft/common/container/ContainerThaumatorium.class`, `enchantItem()` toggles recipes and the reference tile capacity is maintained by `getUpgrades()`.

**Что не совпадает:**

Current `TileThaumatorium.maxRecipes` defaults to 1 at `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java:45`, persists through NBT at `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java:188-222`, and can be read/written as an inventory field at `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java:464-477`. However, there is no `getUpgrades()` method and no update-time brainbox scan. `ContainerThaumatorium.enchantItem()` also prevents adding recipes when `recipeHash.size() >= maxRecipes` at `src/main/java/thaumcraft/common/container/ContainerThaumatorium.java:106-110`, so the current thaumatorium is effectively locked to one programmed recipe unless something external sets field 1.

**Что нужно доделать:**

Port the reference `getUpgrades()` behavior for thaumatorium recipe capacity and call it from the existing 40-tick heat update path.

**Как доделать:**
- Add `getUpgrades()` to `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java`.
- Check adjacent positions for the current 1.12.2 equivalent of `blockMetalDevice` metadata 12 / `TileBrainbox` facing back toward the thaumatorium.
- Increase `maxRecipes` by 2 per valid brainbox, starting from 1.
- Prune `recipeHash`, `recipeEssentia`, and `recipePlayer` consistently when capacity shrinks.
- Call `getUpgrades()` together with `checkHeat()` in `TileThaumatorium.update()` when `counter == 0 || counter % 40 == 0`, matching the reference update cadence.
- Verify current `ContainerThaumatorium.enchantItem()` capacity check remains compatible after dynamic capacity is restored.

**Критерии приемки:**
- [ ] Thaumatorium with no brainbox can store one programmed crucible recipe.
- [ ] Each valid adjacent brainbox increases capacity by 2, matching the reference.
- [ ] Removing brainboxes prunes extra programmed recipe hashes, aspect lists, and player names without desync.
- [ ] Programmed recipes survive save/load through existing `recipes`, `maxrec`, and `OutputPlayer` NBT keys.

**Риски / зависимости:**

Requires current blockstate/meta mapping for `blockMetalDevice` meta 12 and `TileBrainbox` facing to be confirmed. If block metadata mappings changed in the port, using the wrong state will silently break capacity upgrades.

#### Checkpoint 2026-05-16 — GAP-3 baseline wiring restored

Статус: server/common baseline for thaumatorium brainbox upgrades implemented; runtime/manual crafting scenarios remain open.

Что сделано:

- Added `TileBrainbox` with reference-style `facing` NBT field.
- Restored `blockMetalDevice` meta `12` tile wiring in `BlockMetalDevice#createNewTileEntity(...)`.
- Restored brainbox tile-entity registration in `ConfigBlocks` (`TileBrainbox` legacy token).
- Added brainbox orientation handling in `BlockMetalDevice` placement/neighbor updates so adjacent thaumatoriums can be targeted.
- Restored `TileThaumatorium#getUpgrades()` scan and 40-tick update call, including `maxRecipes` expansion by +2 per valid brainbox and shrink-prune of `recipeHash` / `recipeEssentia` / `recipePlayer`.
- Added static contract tests:
  - `ThaumatoriumBrainboxUpgradeContractTest`
  - `BlockMetalDeviceBrainboxContractTest`

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed (including `smoke-server` ready state).

Ограничения:

- No manual in-world confirmation of multi-brainbox placement/programming UX was run due headless non-interactive flow.
- Full Stage 9-d closure still depends on unresolved GAP-4 and GAP-5 content data work.

### GAP-4: Alchemical smelting bonus data is missing

**Статус:** отсутствует  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigRecipes.java:7-13`
- `src/main/java/thaumcraft/api/ThaumcraftApi.java:67-87`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`, smelting bonus block registers `ThaumcraftApi.addSmeltingBonus(...)` for ore dictionary ores, native clusters, and meat/fish outputs after the main recipe registrations.
- `thaumcraft_src/thaumcraft/api/ThaumcraftApi.class`, `addSmeltingBonus` and `getSmeltingBonus` provide the same API shape as current source.

**Что не совпадает:**

The current API supports smelting bonuses at `src/main/java/thaumcraft/api/ThaumcraftApi.java:67-87`, but current `ConfigRecipes.init()` never registers the reference alchemical smelting bonus data. The reference treats these as alchemy content data, not generic furnace recipes only: ore bonuses point to Thaumcraft native nuggets/clusters and meat/fish nugget outputs, which are part of alchemy furnace/progression rewards.

**Что нужно доделать:**

Port only the reference alchemical smelting bonus registrations that are tied to alchemy furnace/essentia content flow.

**Как доделать:**
- Add `ThaumcraftApi.addSmeltingBonus("oreGold", ...)`, `oreIron`, `oreCinnabar`, optional copper/tin/silver/lead, and native cluster mappings from reference `ConfigRecipes`.
- Add meat/fish nugget bonus mappings if the corresponding current items exist in `ConfigItems`.
- Preserve the reference zero-stack-count convention if current API still expects `new ItemStack(out.getItem(), 0, out.getMetadata())` internally at `src/main/java/thaumcraft/api/ThaumcraftApi.java:67-73`.
- Keep broader vanilla furnace recipe registration out of this chunk.

**Критерии приемки:**
- [ ] `ThaumcraftApi.getSmeltingBonus(...)` returns the expected bonus for ore dictionary ore inputs used by alchemy furnace flow.
- [ ] Native cluster inputs map to their expected bonus outputs.
- [ ] Meat/fish inputs map to the expected Thaumcraft nugget outputs where current items exist.
- [ ] Optional mod metal bonuses obey current ore-detection config flags.

**Риски / зависимости:**

Depends on current item metadata parity for `itemNugget`, edible nuggets, and cinnabar/native cluster outputs. This should not expand into full Stage 9-a smelting recipe parity.

### GAP-5: Thaumcraft alchemy item/block aspect tags are incomplete

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigAspects.java:12-191`
- `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:45-66`
- `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:68-116`
- `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:272-283`
- `src/main/java/thaumcraft/common/tiles/TileCrucible.java:362-378`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigAspects.class`, reference aspect tags include Thaumcraft nuggets, shards, balanced shard/salis mundus source item, `itemEssence`, alchemy blocks/plants, `blockMetalDevice`, alchemy furnace, tallow/cosmetic solids, and many recipe catalysts/outputs.
- `thaumcraft_src/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.class`, `getObjectTags`, `getBonusTags`, and `generateTagsFromCrucibleRecipes()` depend on registered object tags and generated recipe tags.
- `thaumcraft_src/thaumcraft/common/tiles/TileCrucible.class`, `attemptSmelt()` decomposes non-recipe items through object tags plus bonus tags.

**Что не совпадает:**

Current `ConfigAspects` registers a limited vanilla and ore dictionary set, but it does not register the reference Thaumcraft alchemy tags. No matches for `ConfigItems.itemShard`, `ConfigItems.itemResource`, `ConfigItems.itemNugget`, `ConfigItems.itemEssence`, `ConfigBlocks.blockCustomPlant`, `ConfigBlocks.blockCosmeticSolid`, or `ConfigBlocks.blockMetalDevice` appear in `src/main/java/thaumcraft/common/config/ConfigAspects.java`. As a result:

- Crucible decomposition can give wrong or empty aspects for Thaumcraft items.
- `generateTagsFromCrucibleRecipes()` at `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:272-283` can generate wrong tags for crucible outputs because catalysts/output chains lack source tags.
- Balanced shard and alchemy progression items can exist as items but not behave like reference alchemy content.

**Что нужно доделать:**

Port the reference aspect tags that directly affect Stage 9-d crucible/alchemy recipes and their catalysts/outputs.

**Как доделать:**
- Add current 1.12.2 registrations in `src/main/java/thaumcraft/common/config/ConfigAspects.java` for shards meta 0-6, relevant `itemResource` metas used by alchemy recipes, native clusters/nuggets, `itemEssence` metas used by Bottled Taint, and alchemy blocks/plants used as catalysts/outputs.
- Add ore dictionary tags required by Stage 9-d catalysts: `dustGlowstone`, `nuggetIron`, optional `nuggetCopper`, `nuggetTin`, `nuggetSilver`, `nuggetLead`, `oreCopper`, `oreTin`, `oreSilver`, `oreLead`, plus existing iron/gold parity checks.
- Ensure tags are registered after items/blocks exist and before crucible decomposition/manual scenarios are tested; current lifecycle calls aspects after recipes at `src/main/java/thaumcraft/common/Thaumcraft.java:188-190`, so ordering should be verified.
- Limit this work to tags needed by Stage 9-d recipes and decomposition scenarios.

**Критерии приемки:**
- [ ] Every Stage 9-d catalyst and output has a reference-equivalent aspect source: direct object tag, ore dictionary tag, or generated recipe tag.
- [ ] Throwing each major alchemy catalyst into a hot crucible produces expected aspects when it is not consumed as a recipe catalyst.
- [ ] `ThaumcraftCraftingManager.getObjectTags(...)` returns non-empty, reference-equivalent tags for shards, alchemy resources, native clusters, and alchemy blocks/plants used by Stage 9-d.
- [ ] Generated tags for crucible outputs do not recurse infinitely or collapse to empty lists.

**Риски / зависимости:**

Depends on current metadata constants for `ItemResource`, `ItemShard`, `ItemNugget`, block item metas, and ore dictionary registration. Broader aspect parity belongs outside this chunk unless a tag directly affects Stage 9-d recipe flow.

### GAP-6: Crucible and thaumatorium scenarios are not runtime-verified

**Статус:** требует проверки  
**Критичность:** medium

**Текущая реализация:**
- `src/main/java/thaumcraft/common/tiles/TileCrucible.java:328-402`
- `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java:56-159`
- `src/main/java/thaumcraft/common/container/ContainerThaumatorium.java:72-119`
- `AGENTS.md:155-173`
- `docs/PRD.md:173-185`
- `docs/PRD.md:526-526`

**Референс:**
- `thaumcraft_src/thaumcraft/common/tiles/TileCrucible.class`, `attemptSmelt()` behavior.
- `thaumcraft_src/thaumcraft/common/tiles/TileThaumatorium.class`, update/completion/fill behavior.
- `thaumcraft_src/thaumcraft/common/container/ContainerThaumatorium.class`, recipe list/programming behavior.

**Что не совпадает:**

Static comparison shows the core crucible and thaumatorium runtime methods are close to the reference, but Stage 9-d has no documented runtime/manual validation. `AGENTS.md:155-173` explicitly says compile success is not enough, and `docs/PRD.md:173-185` says only validated systems should be considered closed. Important static risks remain:

- `TileCrucible.attemptSmelt()` uses `EntityItem.getThrower()` fallback plus `entity.getEntityData().getString("thrower")` at `src/main/java/thaumcraft/common/tiles/TileCrucible.java:331-338`; reference reads entity data `thrower`, so player attribution/research completion must be checked in-game.
- `TileCrucible` drains Forge fluid tank water and emits special item output at `src/main/java/thaumcraft/common/tiles/TileCrucible.java:353-355`; reference fluid behavior was 1.7.10 ForgeDirection/tank based.
- `TileThaumatorium.completeRecipe()` resets all stored essentia after a craft at `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java:124-159`; this matches reference shape, but requires scenario validation with partial and exact essentia inputs.
- `ContainerThaumatorium` adds a capacity guard at `src/main/java/thaumcraft/common/container/ContainerThaumatorium.java:106`, while the reference relies on tile capacity updates; this must be validated after GAP-3.

**Что нужно доделать:**

After data gaps are fixed, run focused manual or automated scenarios for crucible and thaumatorium alchemy flow.

**Как доделать:**
- Add focused validation notes or tests for recipe matching with and without research completion.
- Manual scenario: hot water-filled crucible plus required aspects plus catalyst produces output, drains 50 mB water, removes only recipe aspects, and consumes one catalyst from a stack.
- Manual scenario: throwing an item with no aspect tags pops/rejects instead of consuming it.
- Manual scenario: thaumatorium lists only known/valid crucible recipes for the inserted catalyst, can program/unprogram recipes, pulls essentia, and outputs to adjacent inventory or world.
- Manual scenario: save/reload keeps `recipes`, `OutputPlayer`, `maxrec`, and input stack NBT stable.
- Run at least `./scripts/dev.sh compileJava` after implementation changes and `./scripts/dev.sh smoke-server` for common/server changes.

**Критерии приемки:**
- [ ] Crucible recipe craft passes for at least Balanced Shard, Alumentum/Nitor, Thaumium, and one high-tier recipe such as Void Seed or Liquid Death.
- [ ] Crucible recipe craft fails correctly without research and with insufficient aspects.
- [ ] Thaumatorium can program, persist, consume essentia for, and output at least one crucible recipe.
- [ ] Server smoke reaches normal ready state with no crash markers after Stage 9-d changes.

**Риски / зависимости:**

Depends on GAP-1, GAP-2, GAP-3, and GAP-5. Client particle/sound TODOs in `TileCrucible.receiveClientEvent` are a Stage 8 dependency and should not block Stage 9-d data closure unless they cause server/common runtime crashes.

## 6. Итоговый checklist закрытия Stage 9-d

- [ ] Port reference `ConfigRecipes.initializeAlchemyRecipes()` crucible recipe data into current `ConfigRecipes.init()` flow.
- [ ] Preserve every Stage 9-d recipe ID/name, research key, catalyst, output, aspect cost, and optional ore-mod gate.
- [ ] Restore direct recipe-handle storage equivalent to `ConfigResearch.recipes` for Stage 9-d recipe entries.
- [ ] Ensure research completion gates work for both `TileCrucible` matching and `ContainerThaumatorium` programming.
- [ ] Port Stage 9-d alchemical smelting bonus registrations only where tied to alchemy furnace/content flow.
- [ ] Add/verify aspect tags for all Stage 9-d catalysts, outputs, and generated-tag dependencies.
- [ ] Port thaumatorium `getUpgrades()`/brainbox capacity behavior and verify `maxRecipes` pruning.
- [ ] Verify NBT keys remain unchanged: `recipes`, `OutputPlayer`, `maxrec`, `Items`, `facing`, and aspect data.
- [ ] Run `./scripts/dev.sh compileJava` after implementation changes.
- [ ] Run `./scripts/dev.sh smoke-server` after implementation changes because Stage 9-d affects registration, recipes, blocks/items, and runtime tile behavior.
- [ ] Manually verify crucible and thaumatorium scenarios listed in GAP-6.
- [ ] Update `./docs/Stage9-d.md` after implementation to remove closed blocker/high gaps or mark them closed with validation evidence.

## 7. Definition of Done

Stage 9-d считается ПОЛНОСТЬЮ завершенной только если:
- все blocker gaps закрыты;
- все high gaps закрыты;
- все элементы из scope Stage 9-d реализованы;
- текущая реализация соответствует референсу по поведению;
- все нужные файлы, ресурсы и регистрации присутствуют;
- отсутствуют TODO и заглушки внутри scope Stage 9-d;
- проект собирается без ошибок;
- базовые игровые сценарии Stage 9-d проверены вручную или тестами;
- ./docs/Stage9-d.md обновлен и не содержит критичных открытых вопросов.

## 8. Открытые вопросы

- Точный current metadata mapping для всех reference outputs/catalysts должен быть подтвержден при реализации GAP-1 и GAP-5, особенно `ItemResource`, `ItemNugget`, `ItemShard`, `blockMetalDevice`, `blockCosmeticSolid`, `blockCustomPlant`, и `blockTaint`.
- Нужно решить, где именно в текущей research architecture должен жить эквивалент `ConfigResearch.recipes`, потому что current `ConfigResearch.java` является stub-файлом.
