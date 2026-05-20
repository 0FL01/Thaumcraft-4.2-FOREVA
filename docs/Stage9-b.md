# Stage 9-b — Gap-анализ и план закрытия

## 1. Цель фазы

Stage 9-b закрывает arcane crafting parity для Thaumcraft 4.2.3.5 на Forge 1.12.2: данные arcane recipes, API `IArcaneRecipe`, shaped/shapeless behavior, Arcane Workbench matching/output/vis consumption, dynamic wand/sceptre recipes, recipe research gates and research recipe handles.

Stage 9-b сейчас нельзя считать завершенной: blocker gaps остаются в регистрации рецептов, workbench crafting flow, dynamic wand/sceptre recipes, research recipe map/gates и 1.12.2 `ItemStack.EMPTY` совместимости.

## 2. Scope фазы

- Arcane crafting recipes only: shaped, shapeless and dynamic arcane recipes.
- `thaumcraft.api.crafting.IArcaneRecipe`, `ShapedArcaneRecipe`, `ShapelessArcaneRecipe` API behavior as required by arcane recipe data.
- `ThaumcraftApi.addArcaneCraftingRecipe`, `addShapelessArcaneCraftingRecipe`, `getCraftingRecipes` manager integration.
- `ConfigRecipesArcaneSlice.initializeArcaneRecipeBaseline()` equivalent and all reference arcane recipe keys/outputs/costs/research gates (the slice is called from hub `ConfigRecipes.init()`).
- Arcane Workbench server container/output slot/vis consumption path required to craft these recipes.
- Wand/rod/cap/sceptre dynamic recipes and vis/aspect cost calculation.
- Research gates/unlocks only where they directly gate arcane recipes or store arcane recipe handles.
- Excluded except as direct dependency: infusion/crucible recipe data, normal recipe data, full research page content, Stage 8 render/FX/client polish.

## 3. Источники сравнения

- Current PRD Stage 9 scope: `docs/PRD.md:400-416`.
- Current API: `src/main/java/thaumcraft/api/ThaumcraftApi.java:90-103`, `src/main/java/thaumcraft/api/crafting/IArcaneRecipe.java:9-23`, `src/main/java/thaumcraft/api/crafting/ShapedArcaneRecipe.java:16-193`, `src/main/java/thaumcraft/api/crafting/ShapelessArcaneRecipe.java:16-133`.
- Current recipe config (hub + slices): `src/main/java/thaumcraft/common/config/ConfigRecipes.java` delegates to slice files under `thaumcraft/common/config/recipes/`. Research handle map: `src/main/java/thaumcraft/common/config/ConfigResearch.java:3-8`.
- Current workbench integration: `src/main/java/thaumcraft/common/container/ContainerArcaneWorkbench.java:10-43`, `src/main/java/thaumcraft/common/tiles/TileArcaneWorkbench.java:6-18`, `src/main/java/thaumcraft/common/blocks/BlockTable.java:145-155`, `src/main/java/thaumcraft/common/CommonProxy.java:96-99`, `src/main/java/thaumcraft/client/ClientProxy.java:79-84`.
- Current wand component/cost support: `src/main/java/thaumcraft/api/wands/WandCap.java:76-86`, `src/main/java/thaumcraft/api/wands/WandRod.java:88-114`, `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:215-247`, `src/main/java/thaumcraft/common/Thaumcraft.java:317-354`.
- Current aspect generation consumer for arcane recipes: `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:262-300`, `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:349-360`.
- Reference arcane recipe registrations: `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`, decompiled with `cfr --silent true thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`; relevant output lines include `initializeArcaneRecipes` at decompiled lines 183-276 and dynamic adds at 225-228, plus `ThaumcraftApi.getCraftingRecipes().add(ra)` at decompiled line 358.
- Reference workbench flow: `thaumcraft_src/thaumcraft/common/container/ContainerArcaneWorkbench.class`, `thaumcraft_src/thaumcraft/common/container/SlotCraftingArcaneWorkbench.class`, `thaumcraft_src/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.class`, `thaumcraft_src/thaumcraft/common/lib/crafting/ArcaneWandRecipe.class`, `thaumcraft_src/thaumcraft/common/lib/crafting/ArcaneSceptreRecipe.class`.
- Commands run for this analysis: `git status --short`; `jar tf Thaumcraft-1.7.10-4.2.3.5.jar | rg 'Arcane|MagicWorkbench|ConfigRecipes|crafting|ContainerArcane|GuiArcane'`; `find thaumcraft_src -path '*ConfigRecipes*' -o -path '*ArcaneRecipe*' -o -path '*ArcaneWorkbench*' -o -path '*ArcaneWandRecipe*' -o -path '*ArcaneSceptreRecipe*' | sort`; focused `rg` scans for `addArcaneCraftingRecipe`, `IArcaneRecipe`, `findMatchingArcaneRecipe`, `ConfigResearch.recipes`, workbench classes and Stage 9 stubs; `cfr --silent true` decompiles listed above; `cfr --silent true thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class | rg -c 'addArcaneCraftingRecipe|addShapelessArcaneCraftingRecipe'` returned `89`.

## 4. Текущее состояние Stage 9-b

- `ThaumcraftApi` can allocate and append `ShapedArcaneRecipe`/`ShapelessArcaneRecipe` to the global `craftingRecipes` list, but no current code calls either method (`src/main/java/thaumcraft/api/ThaumcraftApi.java:94-103`; focused scan found no current calls outside the API itself).
- `ConfigRecipes.init()` now calls `ConfigRecipesArcaneSlice.initializeArcaneRecipeBaseline()` among other slice methods, but full reference arcane key coverage is still open.
- `ConfigResearch.recipes` map scaffold now exists, and `ConfigRecipesArcaneSlice` writes a baseline subset of arcane recipe handles, but full reference key coverage and Stage 9-d/e research-page integration are still missing.
- Current `IArcaneRecipe`, `ShapedArcaneRecipe` and `ShapelessArcaneRecipe` are structurally close to the 1.7.10 API, but their matching code still uses `null` emptiness assumptions that are unsafe with Forge 1.12.2 inventories returning `ItemStack.EMPTY` (`src/main/java/thaumcraft/api/crafting/ShapedArcaneRecipe.java:143-167`, `src/main/java/thaumcraft/api/crafting/ShapelessArcaneRecipe.java:81-105`; `TileMagicWorkbench.getStackInSlot` returns `ItemStack.EMPTY` at `src/main/java/thaumcraft/common/tiles/TileMagicWorkbench.java:28-30`).
- `ThaumcraftCraftingManager` now exposes restored arcane matcher methods used by the workbench path, but end-to-end runtime coverage is still blocked by incomplete static recipe population and research content.
- `ContainerArcaneWorkbench` server baseline (slots/output/wand restriction/vis consumption path) is now implemented, but representative runtime craft scenarios are still pending.
- `TileArcaneWorkbench` is effectively only a named ticking `TileMagicWorkbench`; inventory storage exists in the parent, but no workbench-specific crafting behavior is present (`src/main/java/thaumcraft/common/tiles/TileArcaneWorkbench.java:6-18`, `src/main/java/thaumcraft/common/tiles/TileMagicWorkbench.java:19-20`).
- Server GUI routing opens `ContainerArcaneWorkbench`, but the client GUI route returns `null`, so a player cannot use the normal Arcane Workbench UI even if server recipes existed (`src/main/java/thaumcraft/common/CommonProxy.java:96-99`, `src/main/java/thaumcraft/client/ClientProxy.java:79-84`). This is a direct dependency from Stage 8/client GUI work.
- Reference `initializeArcaneRecipes()` registers 89 shaped/shapeless arcane recipes and additional dynamic recipes; current implementation now contains dynamic recipes plus a partial static baseline, but full coverage is still missing.

## 5. Gap list

### GAP-1: Arcane recipe data registration неполная

**Статус:** частично закрыт (added static baseline subset + recipe-handle writes)  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigRecipes.java` hub + `ConfigRecipesArcaneSlice.java` slice
- `src/main/java/thaumcraft/api/ThaumcraftApi.java:94-103`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`, decompiled `initializeArcaneRecipes()` lines 183-276
- `thaumcraft_src/thaumcraft/api/ThaumcraftApi.class`, arcane recipe add methods equivalent to current API

**Что не совпадает:**
Reference calls `initializeArcaneRecipes()` from `ConfigRecipes.init()` and fills `ConfigResearch.recipes` with arcane recipe objects. Port now has a concrete arcane baseline method in `ConfigRecipesArcaneSlice.initializeArcaneRecipeBaseline()`, called from hub `ConfigRecipes.init()`, and registers an expanded static subset with direct `ConfigResearch.recipes.put(...)` handles, including:
- baseline keys from prior checkpoint (`PrimalCharm`, `IronKey`/`GoldKey`, `ArcaneStone1`, `WardedJar`, `JarVoid`, representative wand cap/rod staff keys, `FocusFire`/`FocusFrost`, robe parts, `Goggles`);
- added focus/golem/utility block (`MirrorGlass`, `BoneBow`, `PrimalArrow_0..5`, `InfusionMatrix`, `ArcanePedestal`, `FocusShock`, `FocusTrade`, `FocusExcavation`, `FocusPrimal`, `FocusPouch`, `Deconstructor`, `ArcaneBoreBase`, `EnchantedFabric`, `GolemBell`, `CoreBlank`, `UpgradeAir`..`UpgradeEntropy`).
- added alchemy/tube/thaumatorium block (`Filter`, `AlchemyFurnace`, `Alembic`, `Bellows`, `Tube`, `Resonator`, `TubeValve`, `TubeFilter`, `TubeRestrict`, `TubeOneway`, `TubeBuffer`, `AlchemicalConstruct`, `AdvAlchemyConstruct`, `Centrifuge`, `EssentiaCrystalizer`, `MnemonicMatrix`).

Full parity is still open: many of the 89 reference arcane adds are not yet ported.
Current key audit for arcane API registrations has no missing `addArcaneCraftingRecipe` / `addShapelessArcaneCraftingRecipe` keys.
The remaining non-arcane reference keys (`ArcaneStone2`, `ArcaneStone3`, `ArcaneStone4`) are now covered via registered shaped recipes in `registerSpecialRecipes`, with `ConfigResearch.recipes` handles restored and `blockStairsArcaneStone` added.

**Что нужно доделать:**
Port `initializeArcaneRecipeBaseline()` to full coverage and register all original arcane recipe entries with matching research keys, outputs, ingredients, aspect costs and conditional ore-dictionary branches.

**Как доделать:**
- `src/main/java/thaumcraft/common/config/recipes/ConfigRecipesArcaneSlice.java`: expand the existing `initializeArcaneRecipeBaseline()` method, called from hub `ConfigRecipes.init()` at the lifecycle point where items/blocks/wand components and `ConfigResearch.recipes` are ready.
- `src/main/java/thaumcraft/common/config/recipes/ConfigRecipesArcaneSlice.java`: add missing reference entries from decompiled lines 188-276, including `Banner_0..15`, `PrimalCharm`, `ArcaneDoor`, `WardedGlass`, `IronKey`, `GoldKey`, `ArcanePressurePlate`, `NodeStabilizer`, `NodeRelay`, `FocalManipulator`, `ArcaneStone1`, `PaveTravel`, `ArcaneLamp`, `ArcaneSpa`, `Levitator`, `ArcaneEar`, `MirrorGlass`, `BoneBow`, `PrimalArrow_0..5`, `InfusionMatrix`, `ArcanePedestal`, `WardedJar`, `JarVoid`, wand caps/rods/staff rods/foci/golem upgrades/robe/goggles and decoration entries.
- `src/main/java/thaumcraft/common/config/recipes/ConfigRecipesArcaneSlice.java`: preserve `Config.foundCopperIngot`/`Config.foundSilverIngot` conditional recipes for copper and silver caps.
- `src/main/java/thaumcraft/common/config/ConfigResearch.java`: expose/store the same recipe-key map used by research pages.

**Критерии приемки:**
- [x] Focused audit shows every reference arcane key from `initializeArcaneRecipes()` exists in current registration or is documented with a concrete missing-content blocker.
- [x] `ConfigRecipes.init()` (hub) now calls `ConfigRecipesArcaneSlice.initializeArcaneRecipeBaseline()` which writes recipe handles to `ConfigResearch.recipes`.
- [x] Aspect costs and research strings are ported for representative baseline keys: `PrimalCharm`, `ArcaneStone1`, `WandCapGold`, `WandRodGreatwood`, `FocusFire`, `Goggles`.

**Риски / зависимости:**
Depends on Stage 9-a lifecycle/recipe foundation and current item/block registry names. Missing item/block variants must be handled as concrete blockers, not silently skipped.

### GAP-2: Dynamic wand and sceptre arcane recipes отсутствуют

**Статус:** частично закрыт (dynamic recipes + registration baseline implemented)
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/crafting/ArcaneWandRecipe.java`
- `src/main/java/thaumcraft/common/lib/crafting/ArcaneSceptreRecipe.java`
- `src/main/java/thaumcraft/common/config/ConfigRecipes.java`
- `src/main/java/thaumcraft/common/Thaumcraft.java:317-354`
- `src/main/java/thaumcraft/api/wands/WandCap.java:76-86`
- `src/main/java/thaumcraft/api/wands/WandRod.java:88-114`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/crafting/ArcaneWandRecipe.class`
- `thaumcraft_src/thaumcraft/common/lib/crafting/ArcaneSceptreRecipe.class`
- `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`, decompiled lines 225-228

**Что не совпадает:**
Port now includes both dynamic recipes and registration:
- `ArcaneWandRecipe` and `ArcaneSceptreRecipe` implement `IArcaneRecipe`;
- hub `ConfigRecipes.init()` calls `ConfigRecipesArcaneSlice` which adds both into `ThaumcraftApi.getCraftingRecipes()` with duplicate guards;
- recipe logic follows reference layouts, research checks, cap/rod matching, primal-aspect cost formulas, and sceptre output tagging.

**Что нужно доделать:**
Keep and runtime-validate dynamic arcane recipe behavior once broader Stage 9-b recipe population is in place.

**Как доделать:**
- Keep dynamic recipe registration (via hub `ConfigRecipes.init()` → `ConfigRecipesArcaneSlice`) with duplicate guards.
- Preserve layout and cost rules from reference for wand/sceptre dynamic outputs.
- Add focused runtime scenarios once arcane static recipe registration/research map population is available.

**Критерии приемки:**
- [x] Dynamic `ArcaneWandRecipe` and `ArcaneSceptreRecipe` classes exist and are registered into `ThaumcraftApi.getCraftingRecipes()`.
- [x] Wand/sceptre output paths set cap/rod metadata and sceptre tag with reference-aligned cost formulas.
- [x] Recipe `matches` logic enforces cap/rod research gates and `SCEPTRE` gate.
- [ ] Runtime scenario validates dynamic crafting outputs and vis costs through Arcane Workbench flow.

**Риски / зависимости:**
Depends on wand component registration (`src/main/java/thaumcraft/common/Thaumcraft.java:317-354`) and on research completion checks. Staff rod tags in current `StaffRod` must match reference recipe names before sceptre/staff scenarios can pass.

### GAP-3: Arcane Workbench matching methods отсутствуют в crafting manager

**Статус:** частично закрыт (method surface restored; runtime flow depends on GAP-1/2/4)
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:43-90`
- `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:285-300`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.class`, decompiled `findMatchingArcaneRecipe` and `findMatchingArcaneRecipeAspects` methods lines 147-193

**Что не совпадает:**
Reference has public methods used by the Arcane Workbench container to find matching `IArcaneRecipe` output and aspect cost. Port now restores both methods in `ThaumcraftCraftingManager`:
- `findMatchingArcaneRecipe(IInventory, EntityPlayer)`
- `findMatchingArcaneRecipeAspects(IInventory, EntityPlayer)`

The methods iterate `ThaumcraftApi.getCraftingRecipes()`, filter `IArcaneRecipe`, call `matches(inv, player.world, player)`, and return recipe output/aspects. They are null-safe for `IInventory`/`EntityPlayer` and use 1.12-safe empty result conventions (`ItemStack.EMPTY`, empty `AspectList`).

**Что нужно доделать:**
Keep the restored matching methods and wire them into the Arcane Workbench runtime container flow.

**Как доделать:**
- Keep `findMatchingArcaneRecipe` and `findMatchingArcaneRecipeAspects` in `ThaumcraftCraftingManager`.
- When implementing GAP-4, route `ContainerArcaneWorkbench` output/vis checks through these methods.
- Preserve `ItemStack.EMPTY` conventions across all container call sites.

**Критерии приемки:**
- [x] `ThaumcraftCraftingManager` now exposes `findMatchingArcaneRecipe` and `findMatchingArcaneRecipeAspects`.
- [x] The restored methods are null-safe for inventory/player input and return 1.12-safe empty values.
- [ ] Container can query a matching arcane recipe output for `ArcaneStone1` after research is complete.
- [ ] Container can query a matching aspect cost for both static and dynamic recipes.
- [ ] No false mismatch occurs for representative Arcane Workbench runtime scenarios with `ItemStack.EMPTY`.

**Риски / зависимости:**
Depends on GAP-1/GAP-2 recipe data. The method signatures are internal current source behavior, not public API, so they can be adapted to 1.12.2 conventions if all call sites agree.

### GAP-4: Arcane Workbench container/output/vis consumption flow не реализован

**Статус:** частично закрыт (server container baseline implemented)
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/container/ContainerArcaneWorkbench.java`
- `src/main/java/thaumcraft/common/container/SlotCraftingArcaneWorkbench.java`
- `src/main/java/thaumcraft/common/container/SlotLimitedByWand.java`
- `src/main/java/thaumcraft/common/tiles/TileMagicWorkbench.java`
- `src/main/java/thaumcraft/common/tiles/TileArcaneWorkbench.java:6-18`
- `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:240-247`

**Референс:**
- `thaumcraft_src/thaumcraft/common/container/ContainerArcaneWorkbench.class`
- `thaumcraft_src/thaumcraft/common/container/SlotCraftingArcaneWorkbench.class`

**Что не совпадает:**
Port now includes a server-side Arcane Workbench baseline aligned with reference flow:
- container slot layout with output slot (`tile 9`), wand slot (`tile 10`), 3x3 grid (`tile 0..8`), and player inventory/hotbar;
- `onCraftMatrixChanged` path that computes vanilla result first, then arcane result when a non-staff wand in slot 10 can pay `findMatchingArcaneRecipeAspects(..., false)`;
- craft-result slot behavior that fires crafting event, consumes vis via `consumeAllVisCrafting(..., true)`, consumes ingredients and handles container items;
- transfer/slot-click drag behavior ported to 1.12 container semantics;
- `TileMagicWorkbench` null/EMPTY inventory hardening to avoid null-slot regressions in the new container path.

**Что нужно доделать:**
Keep this container baseline and validate representative runtime crafting scenarios once recipe data is populated.

**Как доделать:**
- Keep `ContainerArcaneWorkbench`, `SlotCraftingArcaneWorkbench`, and `SlotLimitedByWand` wired as the server baseline.
- Preserve reference slot index assumptions used by transfer logic (`0` output, `1` wand, `2..10` grid, `11..46` player inventory/hotbar).
- Validate vis consumption and ingredient/container-item handling with populated Stage 9-b recipe data.

**Критерии приемки:**
- [x] Arcane Workbench server container now exposes 47 slots with reference role layout.
- [x] Server container update path now resolves vanilla and arcane outputs through the restored matcher methods and wand vis-check probe.
- [x] Shift-click routing includes output-to-player flow and wand routing into slot 10.
- [x] Preview recomputation no longer recurses through `InventoryCrafting(this, ...)`; the container now keeps a detached craft-matrix snapshot and has runtime coverage for the reproduced stack-overflow path.
- [ ] Runtime scenario validates ingredient consumption + vis drain for representative static arcane recipes.
- [ ] Runtime scenario validates dynamic recipe crafting path and container-item remainder handling.

**Риски / зависимости:**
Still depends on GAP-1/GAP-2 recipe population for full end-to-end proof. Client GUI/manual checks remain out of scope for this non-GUI checkpoint.

### GAP-5: `ItemStack.EMPTY` compatibility breaks shaped/shapeless arcane matching

**Статус:** частично закрыт (EMPTY/null matcher compatibility fixed)
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/api/crafting/ShapedArcaneRecipe.java:134-168`
- `src/main/java/thaumcraft/api/crafting/ShapelessArcaneRecipe.java:77-112`
- `src/main/java/thaumcraft/common/tiles/TileMagicWorkbench.java:28-38`
- `src/main/java/thaumcraft/common/lib/InternalMethodHandler.java:49-54`

**Референс:**
- `thaumcraft_src/thaumcraft/api/crafting/ShapedArcaneRecipe.class`
- `thaumcraft_src/thaumcraft/api/crafting/ShapelessArcaneRecipe.class`
- `thaumcraft_src/thaumcraft/common/tiles/TileMagicWorkbench.class`

**Что не совпадает:**
Reference 1.7.10 inventories use `null` for empty slots. Port now normalizes both `null` and `ItemStack.EMPTY` in arcane matchers:
- `ShapedArcaneRecipe.checkMatch` treats empty slot as empty when target cell is empty.
- `ShapelessArcaneRecipe.matches` skips empty stacks, not only `null`.
- `checkItemEquals` in both shaped and shapeless classes now handles `null`/`EMPTY` safely before item/meta/tag checks.

**Что нужно доделать:**
Keep 1.12.2-safe `ItemStack.EMPTY` handling in arcane matchers and verify runtime behavior once arcane recipe population is present.

**Как доделать:**
- Keep `ShapedArcaneRecipe.checkMatch` and `ShapelessArcaneRecipe.matches` on unified null/empty semantics.
- Keep `checkItemEquals` in both classes null/empty safe.
- Preserve `InternalMethodHandler.getStackInRowAndColumn` returning `ItemStack.EMPTY`; do not reintroduce nulls globally.

**Критерии приемки:**
- [x] `ShapedArcaneRecipe` empty-slot checks now accept `null` and `ItemStack.EMPTY`.
- [x] `ShapelessArcaneRecipe` now ignores `ItemStack.EMPTY` workbench slots.
- [x] NBT-sensitive comparisons still preserve `areItemStackTagsEqualForCrafting` behavior.
- [ ] Runtime scenario with registered arcane recipes verifies shaped and shapeless matching end-to-end.

**Риски / зависимости:**
This touches public API classes. Do not change method signatures. Existing addon-facing behavior should remain compatible except for required 1.12.2 empty-stack semantics.

### GAP-6: Research recipe map and direct arcane gates неполные

**Статус:** частично закрыт (map scaffold + baseline arcane handles)  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigResearch.java:3-8`
- `src/main/java/thaumcraft/api/research/ResearchPage.java:46-66`
- `src/main/java/thaumcraft/api/ThaumcraftApi.java:151-188`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`, decompiled lines 188-276 for direct `ConfigResearch.recipes.put(...)` arcane entries
- `thaumcraft_src/thaumcraft/api/research/ResearchPage.class`

**Что не совпадает:**
Reference stores recipe objects under stable research recipe keys, and arcane recipe `matches` checks `ThaumcraftApiHelper.isResearchComplete(playerName, research)`. Port now has `ConfigResearch.recipes` plus direct arcane key writes from `ConfigRecipes` for:
- representative baseline entries (`PrimalCharm`, `IronKey`/`GoldKey`, `ArcaneStone1`, `WardedJar`, `JarVoid`, wand cap/rod keys, `FocusFire`/`FocusFrost`, robe parts, `Goggles`);
- focus/golem/utility entries (`MirrorGlass`, `BoneBow`, `PrimalArrow_0..5`, `InfusionMatrix`, `ArcanePedestal`, `FocusShock`, `FocusTrade`, `FocusExcavation`, `FocusPrimal`, `FocusPouch`, `Deconstructor`, `ArcaneBoreBase`, `EnchantedFabric`, `GolemBell`, `CoreBlank`, `UpgradeAir`..`UpgradeEntropy`).
- alchemy/tube/thaumatorium entries (`Filter`, `AlchemyFurnace`, `Alembic`, `Bellows`, `Tube`, `Resonator`, `TubeValve`, `TubeFilter`, `TubeRestrict`, `TubeOneway`, `TubeBuffer`, `AlchemicalConstruct`, `AdvAlchemyConstruct`, `Centrifuge`, `EssentiaCrystalizer`, `MnemonicMatrix`).

Still missing: full key coverage, actual Stage 9-d/e research page/content population, and runtime gate verification scenarios.

**Что нужно доделать:**
Restore the `ConfigResearch.recipes` storage contract and wire arcane recipe registrations into it with reference keys, then ensure direct research strings on recipes correspond to actual research keys.

**Как доделать:**
- `src/main/java/thaumcraft/common/config/ConfigResearch.java`: add the recipe handle map used by config and research pages.
- `ConfigRecipes.initializeArcaneRecipes()`: use exact reference map keys from decompiled lines 188-276.
- Research implementation owner must ensure keys such as `BASICARTIFACE`, `WARDEDARCANA`, `NODESTABILIZER`, `VISPOWER`, `FOCALMANIPULATION`, `ARCANESTONE`, `PAVETRAVEL`, `ARCANELAMP`, `INFUSION`, `CAP_gold`, `ROD_greatwood`, `FOCUSFIRE`, `GOGGLES`, `GOLEMBELL` exist and unlock recipe visibility.
- Add focused validation that an incomplete player cannot match a gated arcane recipe and a completed player can.

**Критерии приемки:**
- [ ] `ConfigResearch.recipes` contains all Stage 9-b arcane recipe handles after recipe initialization.
- [ ] Each arcane recipe's `getResearch()` key maps to a real current research entry or an explicit documented dependency.
- [ ] Research-gated crafting fails before completion and succeeds after completion for representative recipes.

**Риски / зависимости:**
Dependency: broader research page/content implementation belongs to Stage 9-d/e, but the recipe handle map and direct recipe gate keys block Stage 9-b and must exist enough for arcane recipe registration/testing.

### GAP-7: Recipe IDs/names/content coverage audit отсутствует

**Статус:** отсутствует  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigRecipes.java` hub + `ConfigRecipesArcaneSlice.java` slice
- `src/main/resources/assets/thaumcraft/recipes/` absent

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`, decompiled lines 188-276 and dynamic adds at 225-228, 358

**Что не совпадает:**
Reference arcane recipes are keyed by `ConfigResearch.recipes` names and stored in the `ThaumcraftApi` recipe list, not by Forge JSON ids. Forge 1.12.2 still needs stable resource/registry naming for any Java recipes registered through Forge recipe infrastructure, while arcane-only recipes need stable audit names for research pages and debugging. Current source has no mapping/audit from reference keys to current recipe ids, output registry names, meta values, or missing content.

**Что нужно доделать:**
Create an implementation-side audit while porting recipes: reference key, research gate, output item/block registry name/meta/NBT, shaped/shapeless/dynamic type, aspect cost, current registration owner, validation status.

**Как доделать:**
- Keep audit close to `ConfigRecipesArcaneSlice.initializeArcaneRecipeBaseline()` or in a dedicated Stage 9 implementation doc updated during implementation.
- Confirm each output exists in current `ConfigItems`/`ConfigBlocks` before adding the recipe.
- Assign stable Forge registry names only if any arcane recipe is implemented as an `IRecipe`; otherwise preserve stable `ConfigResearch.recipes` keys and `ThaumcraftApi` list order.
- Include conditional recipes for copper/silver separately from always-present recipes.

**Критерии приемки:**
- [ ] All 89 reference shaped/shapeless arcane add calls are mapped to current entries.
- [ ] Dynamic entries `ArcaneWandRecipe`, `ArcaneSceptreRecipe` and the custom runic augment recipe at reference decompiled line 358 are either owned by Stage 9-b if arcane or explicitly deferred if infusion-only.
- [ ] No duplicate research recipe keys or unstable generated names exist in Stage 9-b data.

**Риски / зависимости:**
Some outputs may depend on content not yet ported or renamed. Missing content must become concrete implementation blockers for that recipe; do not change registry identities to make recipes easier.

### GAP-8: Arcane Workbench client GUI is absent as a direct usability dependency

**Статус:** отсутствует  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/client/ClientProxy.java:79-84`
- `src/main/java/thaumcraft/common/blocks/BlockTable.java:153-155`
- `src/main/java/thaumcraft/common/CommonProxy.java:96-99`

**Референс:**
- `thaumcraft_src/thaumcraft/client/gui/GuiArcaneWorkbench.class`
- `thaumcraft_src/thaumcraft/common/container/ContainerArcaneWorkbench.class`

**Что не совпадает:**
Server-side block activation routes to `GUI_ARCANE_WORKBENCH`, but current client proxy returns `null` for that GUI id. Even if server recipe logic is implemented, normal player use and manual validation of arcane crafting are blocked through the GUI path.

**Что нужно доделать:**
Provide enough client GUI integration for manual Stage 9-b validation, or explicitly coordinate with the Stage 8 GUI chunk before claiming Stage 9-b complete.

**Как доделать:**
- Dependency only: implement or unblock `GuiArcaneWorkbench` in the client scope so it can open against `ContainerArcaneWorkbench`.
- Ensure the GUI uses the same server slot indices and displays wand/vis costs accurately enough for manual validation.
- Do not let Stage 9-b claim completion until at least server container tests and one GUI/manual craft path are validated.

**Критерии приемки:**
- [ ] Right-clicking an Arcane Workbench opens a client GUI instead of returning `null`.
- [ ] The GUI displays the server result slot and wand slot consistently with the container.
- [ ] At least one shaped and one shapeless arcane recipe are manually craftable through the GUI.

**Риски / зависимости:**
Dependency: this is primarily Stage 8 client GUI work, but it directly blocks Stage 9-b manual validation and normal player usability.

### GAP-9: Runtime/manual validation scenarios for arcane progression отсутствуют

**Статус:** требует проверки  
**Критичность:** high

**Текущая реализация:**
- `docs/PRD.md:413-416`
- `src/main/java/thaumcraft/common/config/ConfigRecipes.java` hub + `ConfigRecipesArcaneSlice.java` slice
- `src/main/java/thaumcraft/common/container/ContainerArcaneWorkbench.java:10-43`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`, decompiled lines 188-276
- `thaumcraft_src/thaumcraft/common/container/SlotCraftingArcaneWorkbench.class`

**Что не совпадает:**
The PRD explicitly warns that content can compile while unusable. Current Stage 9-b has no recipe data and no validation scenarios. No evidence exists that recipe gates, vis consumption, ingredient consumption, NBT outputs, dynamic wand/sceptre outputs or conditional ore recipes work in a runtime environment.

**Что нужно доделать:**
Define and run focused validation after implementation. Compile success is not sufficient.

**Как доделать:**
- Add lightweight tests or manual checklist for representative recipes: `ArcaneStone1`, `MirrorGlass`, `WandedJar`, `WandCapGold`, `WandRodGreatwood`, dynamic wand, dynamic sceptre, `FocusFire`, `Goggles`, one golem upgrade and one conditional copper/silver cap when ore dictionary entries exist.
- Run `./scripts/dev.sh compileJava` after implementation.
- Run `./scripts/dev.sh smoke-server` because recipes, containers and lifecycle are runtime-affecting.
- Run client smoke/manual GUI validation if display is available because Arcane Workbench use requires client GUI interaction.

**Критерии приемки:**
- [ ] Server smoke reaches ready state with no recipe, registry, classloading or GUI handler fatal errors.
- [ ] Manual/test craft consumes ingredients and wand vis exactly once for representative recipes.
- [ ] Research-gated recipes are unavailable before unlock and available after unlock.

**Риски / зависимости:**
Runtime validation depends on Stage 8 GUI availability for full manual gameplay checks. Server-side container tests can still catch recipe registration, matching and vis consumption defects before GUI parity is complete.

## 6. Итоговый checklist закрытия Stage 9-b

- [x] Replace `ConfigRecipes` Stage 9 stubs with concrete arcane recipe initialization in `ConfigRecipesArcaneSlice`.
- [ ] Port all reference shaped and shapeless arcane recipe registrations from `ConfigRecipesArcaneSlice.initializeArcaneRecipeBaseline()`.
- [x] Port and register `ArcaneWandRecipe` and `ArcaneSceptreRecipe`.
- [x] Add current `ThaumcraftCraftingManager.findMatchingArcaneRecipe` and `findMatchingArcaneRecipeAspects` equivalents.
- [ ] Implement Arcane Workbench slots, output update, craft result slot, wand slot restriction, shift-click and vis consumption.
- [x] Fix `ItemStack.EMPTY` handling in shaped and shapeless arcane recipe matching.
- [x] Restore `ConfigResearch.recipes` handle storage enough for Stage 9-b recipe keys.
- [ ] Verify all direct arcane research gate keys exist or are documented as concrete dependencies.
- [ ] Produce a recipe key/output/aspect/research coverage audit for all Stage 9-b arcane recipes.
- [ ] Run focused compile and runtime smoke validation after implementation.
- [ ] Manually validate representative arcane crafting scenarios, including dynamic wand/sceptre and at least one shapeless recipe.

## 7. Definition of Done

Stage 9-b считается ПОЛНОСТЬЮ завершенной только если:
- все blocker gaps закрыты;
- все high gaps закрыты;
- все элементы из scope Stage 9-b реализованы;
- текущая реализация соответствует референсу по поведению;
- все нужные файлы, ресурсы и регистрации присутствуют;
- отсутствуют TODO и заглушки внутри scope Stage 9-b;
- проект собирается без ошибок;
- базовые игровые сценарии Stage 9-b проверены вручную или тестами;
- ./docs/Stage9-b.md обновлен и не содержит критичных открытых вопросов.

## 8. Открытые вопросы

### Q1: Где создавать arcane recipe objects?

**Resolution:** держать Thaumcraft API arcane recipe creation в slice-файлах (`ConfigRecipesArcaneSlice`, `ConfigRecipesInfusionSlice`, `ConfigRecipesCrucibleSlice`), вызываемых из hub `ConfigRecipes.init()`; Forge `IRecipe` — через registry event. Это особенно важно, потому что текущий hub `ConfigRecipes.init()` чистит и заполняет `ConfigResearch.recipes`, а также вызывает baseline arcane/infusion/crucible init blocks из slice-файлов.

Формулировка: "Arcane recipe objects that belong to the Thaumcraft API recipe lists should be created in the recipe configuration lifecycle and recorded in `ConfigResearch.recipes`. Only recipes that are real Forge `IRecipe` instances should be registered through the Forge recipe registry event. Avoid double-registering the same logical recipe through both paths."

### Q2: Что с missing outputs/items/metas?

**Resolution:** это hard audit requirement до закрытия Stage 9-b. Каждый arcane recipe output должен быть проверен против текущих registry names, metadata, aspects, vis cost, research key и availability.

Формулировка: "Every arcane recipe must be audited against current registry names, metadata, aspects, vis cost, research key, and output availability. Missing content should block that recipe explicitly rather than being skipped."

### Q3: GUI dependency блокирует Stage 9-b?

**Resolution:** server/container часть можно закрыть раньше, но полный acceptance зависит от GUI validation path. Stage 9-b может завершить server-side recipe registration и container logic до полной валидации client GUI, но acceptance требует path открытия Arcane Workbench.

Формулировка: "Stage 9-b can complete server-side recipe registration and container logic before the client GUI is fully validated. However, full acceptance requires an Arcane Workbench opening/crafting validation path once Stage 8 GUI work is available."
