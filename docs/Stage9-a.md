# Stage 9-a — Gap-анализ и план закрытия

## 1. Цель фазы

Stage 9-a закрывает фундамент контентных регистраций для рецептов и данных, которые нужны для progression parity в Forge 1.12.2: lifecycle `ConfigRecipes`, vanilla/Forge recipes, smelting, smelting bonus, object aspect tags, ore dictionary integration, recipe ids/naming и базовый аудит отсутствующих/дублирующихся рецептов.

Фаза не включает глубокий разбор arcane/infusion/crucible рецептов и research pages, кроме инфраструктуры, через которую эти данные регистрируются и связываются с `ConfigResearch.recipes`.

## 2. Scope фазы

- `thaumcraft.common.config.ConfigRecipes`: registration lifecycle, helper methods for shaped/shapeless ore/NBT recipes, smelting and smelting bonus.
- Forge 1.12.2 recipe data: `src/main/resources/assets/thaumcraft/recipes/*.json` and/or `RegistryEvent.Register<IRecipe>` for custom recipes where JSON is not enough.
- Vanilla/normal crafting recipes that were ordinary `GameRegistry`/`CraftingManager` recipes in 1.7.10.
- Smelting recipes and `ThaumcraftApi.addSmeltingBonus` parity.
- `ConfigAspects` object tags, complex object tags, ore dictionary tags, mod item/block tags, grouped metadata tags.
- `Config.initModCompatibility()` ore dictionary scan and compat flags.
- Recipe IDs/naming conventions for Forge 1.12.2 and audit foundation for missing/duplicate ids.
- Runtime/manual validation scenarios for recipe load, smelting, ore dictionary detection and scanning/object aspect visibility.

## 3. Источники сравнения

- `docs/PRD.md:395-416`, `docs/PRD.md:540-556` — Stage 9 scope and acceptance language.
- `src/main/java/thaumcraft/common/Thaumcraft.java:186-192`, `215-281` — post-init and recipe registry flow.
- `src/main/java/thaumcraft/common/config/ConfigRecipes.java` and `thaumcraft.common.config.recipes.*` — current recipe registration foundation.
- `src/main/java/thaumcraft/common/config/ConfigAspects.java` — current aspect-tag baseline.
- `src/main/java/thaumcraft/common/config/Config.java` — ore-dictionary compat flags and lifecycle hooks.
- `src/main/java/thaumcraft/common/items/armor/RecipesRobeArmorDyes.java`, `RecipesVoidRobeArmorDyes.java` — custom `IRecipe` examples.
- `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`, `ConfigAspects.class`, `Config.class`, `Thaumcraft.class` — original reference material.

## 4. Текущее состояние Stage 9-a

Stage 9-a is not parity-complete, but it is no longer stub-only.

Current implementation present:

- `ConfigRecipes.init()` now clears/rebuilds `ConfigResearch.recipes` and wires recipe slice initialization.
- Recipe registration is split between `ConfigRecipes.java` and slice classes under `thaumcraft.common.config.recipes`.
- `Thaumcraft.registerRecipes(RegistryEvent.Register<IRecipe>)` calls `ConfigRecipes.registerSpecialRecipes(...)`.
- `ConfigRecipesSpecialSlice` registers custom/special Forge recipes with registry names.
- `ConfigRecipesSmeltingSlice` registers smelting baseline and smelting bonus baseline.
- `ConfigResearch.recipes` receives many recipe handles needed by Thaumonomicon pages.

Current blockers are no longer “foundation missing”; they are validation and audit gaps:

- recipe corpus/static guards do not prove all recipes are craftable in-game;
- fallback research-page recipe handles can hide missing registered recipes;
- JSON absence is not automatically a blocker where Java `IRecipe` registration is intentionally used;
- recipe id coverage and duplicate/missing audits must be tied to live registry/runtime behavior, not source text only;
- ore-tag coverage supports scanning but still needs gameplay validation.

## 5. Gap list

### GAP-1: Recipe registration foundation exists, but runtime craftability is not fully validated

**Статус:** implementation present; runtime validation open
**Критичность:** high

The earlier claim that `ConfigRecipes` is a stub is stale. Current `ConfigRecipes` has a hub/slice structure and registration path for special recipes, smelting, arcane, infusion, crucible, and research recipe handles. Remaining work is to validate that the foundation behaves correctly in Forge 1.12.2:

- every registered `IRecipe` has a stable registry name;
- research-page recipe handles point to actually registered or intentionally display-only recipe objects;
- representative normal/special recipes craft in a real crafting inventory;
- recipe collisions and duplicate ids are tested against the live registry;
- fallback display recipes are labelled and do not mask missing craftable recipes.

### GAP-2: Recipe resources use Java registration; JSON absence is not by itself a blocker

**Статус:** accepted implementation strategy, with audit required
**Критичность:** medium

Forge 1.12.2 can load recipes from JSON or from code-registered `IRecipe` instances. Current port uses Java registration for many reference-shaped recipes. Do not document lack of JSON files as a blocker unless a specific recipe requires JSON or fails registry/runtime behavior because of the chosen strategy.

Required audit:

- list all recipe ids registered in Java;
- confirm stable registry names;
- confirm no duplicate ids;
- confirm representative recipes craft in-game;
- document any recipe intentionally display-only for research pages.

### Fallback recipe-handle warning

`ConfigRecipes.refreshLateBoundResearchRecipeHandles()` may populate `ConfigResearch.recipes` with fallback `ShapedOreRecipe`/`ShapelessOreRecipe` objects when a late-bound special recipe handle is missing. This is useful for keeping research pages from null-crashing, but it must not be counted as proof that the matching Forge recipe is registered or craftable.

Any Stage 9-a closure report must distinguish:

- registered craftable recipe;
- research display recipe handle;
- fallback display-only handle;
- missing recipe.

### GAP-3: Recipe ID and duplicate/missing audit foundation is still required

**Статус:** partial implementation; validation open
**Критичность:** high

Do not let research handles or static corpus counts hide id drift.

Required checks:

- stable lowercase `thaumcraft:<descriptive_id>` ids for Forge registry names and JSON file names where JSON is used;
- reference research keys unchanged when stored in `ConfigResearch.recipes`;
- duplicate ids reported loudly;
- missing reference Stage 9-a recipes documented with an owner/reason.

### GAP-4: Smelting and smelting bonus data are present, but scenario validation remains open

**Статус:** implementation present; runtime validation open
**Критичность:** high

`ConfigRecipesSmeltingSlice` now provides the baseline smelting and bonus registration path. The remaining work is to validate representative furnace and bonus scenarios against the current 1.12.2 item/block registry.

### GAP-5: Ore-dictionary compatibility scan and lifecycle ordering still need verification

**Статус:** partial implementation; validation open
**Критичность:** high

The compat scan and `found*` flags must be verified against the current post-init ordering. Do not assume ore-based recipes or aspect tags are correct until the scan runs at the proper lifecycle point and the resulting flags are observed in-game or through focused tests.

### GAP-6: Object/aspect tag coverage is broader, but gameplay validation still matters

**Статус:** implementation present; validation open
**Критичность:** medium/high

`ConfigAspects` now covers a much larger baseline, but Stage 9-a still needs gameplay checks for scan/object-aspect visibility, recipe-derived tag behavior, and any content that depends on ore or fallback tag lookups.

## 6. Итоговый checklist закрытия Stage 9-a

- [x] `ConfigRecipes.init()` now provides a real registration foundation instead of a stub.
- [x] Special recipes register through `RegistryEvent.Register<IRecipe>` with stable names.
- [x] Smelting baseline and smelting bonus baseline are wired into the recipe foundation.
- [ ] Validate ordinary crafting recipes in-game or through a runtime harness.
- [ ] Validate recipe ids, duplicate detection, and fallback/display-only handles against the live registry.
- [ ] Validate ore-dictionary compat scan ordering and `found*` flags.
- [ ] Validate smelting/bonus outputs for representative recipes and items.
- [ ] Validate aspect/object-tag coverage for Stage 9-a content.
- [ ] Run `./scripts/dev.sh validate --smoke` after any registration/lifecycle change.
- [ ] Document remaining recipe blockers explicitly instead of treating JSON absence as a blanket failure.

## 7. Definition of Done

Stage 9-a считается ПОЛНОСТЬЮ завершенной только если:

- все blocker gaps закрыты;
- все high gaps закрыты;
- текущая реализация соответствует референсу по поведению;
- все нужные файлы, ресурсы и регистрации присутствуют;
- отсутствуют TODO и заглушки внутри scope Stage 9-a;
- проект собирается без ошибок;
- базовые игровые сценарии Stage 9-a проверены вручную или тестами;
- `docs/Stage9-a.md` обновлен и не содержит критичных открытых вопросов.

## 8. Открытые вопросы

### Q1: JSON or Java `IRecipe`?

Simple vanilla workbench recipes should be JSON. Any recipe with NBT, ore dictionary, dynamic output, metadata-sensitive custom matching, or Thaumcraft-specific mechanics should remain Java-backed and be recorded in the recipe audit.

### Q2: What should happen to missing outputs?

Do not silently omit recipes or substitute unrelated outputs. Each recipe must be tracked as implemented, blocked by missing output, blocked by missing ingredient, blocked by missing mechanic, or deferred with owner.
