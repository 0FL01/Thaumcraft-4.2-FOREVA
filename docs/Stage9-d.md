# Stage 9-d — Gap-анализ и план закрытия

## 1. Цель фазы

Stage 9-d закрывает crucible/alchemy content flow: регистрацию crucible recipes, связь recipe ids/names с research gates, matching/output behavior для `TileCrucible`, программирование и потребление crucible recipes в `TileThaumatorium`, а также alchemy furnace/smelting-side данные только там, где референс регистрирует их как alchemy content data.

Цель не включает arcane/infusion/general smelting/research pages целиком, кроме прямых зависимостей, которые блокируют crucible/alchemy recipes.

## 2. Scope фазы

- API и менеджер: `src/main/java/thaumcraft/api/crafting/CrucibleRecipe.java`, `src/main/java/thaumcraft/api/ThaumcraftApi.java`, `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java`.
- Crucible runtime content behavior: `src/main/java/thaumcraft/common/tiles/TileCrucible.java`.
- Thaumatorium recipe programming/consumption where it consumes crucible recipes: `src/main/java/thaumcraft/common/tiles/TileThaumatorium.java`, `src/main/java/thaumcraft/common/tiles/TileThaumatoriumTop.java`, `src/main/java/thaumcraft/common/container/ContainerThaumatorium.java`.
- Alchemy/crucible content registration: `src/main/java/thaumcraft/common/config/ConfigRecipes.java`, direct dependency `src/main/java/thaumcraft/common/config/research/ConfigResearch.java`, direct dependency `src/main/java/thaumcraft/common/config/ConfigAspects.java`.
- Lifecycle hook that invokes recipe/aspect/research registration: `src/main/java/thaumcraft/common/Thaumcraft.java`.
- Alchemy content resources only if they are required by recipe outputs/catalysts and are already represented by registered items/blocks under `ConfigItems`/`ConfigBlocks`.

## 3. Источники сравнения

- `AGENTS.md:5-16`, `AGENTS.md:155-173` — source-of-truth and runtime validation rules.
- `docs/PRD.md:395-416`, `docs/PRD.md:526-526` — Stage 9 scope and crucible smoke/validation language.
- `src/main/java/thaumcraft/common/Thaumcraft.java:186-192` — current post-init order.
- `src/main/java/thaumcraft/common/config/recipes/ConfigRecipesCrucibleSlice.java`, `ConfigRecipesSmeltingSlice.java` — current crucible/smelting baseline.
- `src/main/java/thaumcraft/common/config/research/ConfigResearch.java` — current recipe-handle map.
- `src/main/java/thaumcraft/common/tiles/TileCrucible.java`, `TileThaumatorium.java`, `TileThaumatoriumTop.java`, `ContainerThaumatorium.java` — current runtime baselines.
- `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`, `ConfigAspects.class`, `Thaumcraft.class`, `ThaumcraftApi.class` — original reference material.

## 4. Текущее состояние Stage 9-d

Stage 9-d is not complete, but the old “content data is absent” framing is stale.

Current implementation present:

- `CrucibleRecipe` API and lookup paths exist.
- `ThaumcraftApi.addCrucibleRecipe`, `getCrucibleRecipe`, and `getCrucibleRecipeFromHash` are present.
- `ThaumcraftCraftingManager.findMatchingCrucibleRecipe()` follows the reference matching model: research gate, single-item catalyst copy, aspect/catalyst match, and highest aspect-type-count priority.
- `TileCrucible.attemptSmelt()` follows the reference recipe/output loop closely: read thrower, find recipe, fire crafting event, remove aspects, drain water, and eject output.
- `TileThaumatorium` has a functional baseline for stored crucible recipe hashes, required essentia, input catalyst, output inventory insertion, essentia suction, and completion.
- `ContainerThaumatorium` lists/programs `CrucibleRecipe` entries by research completion, catalyst match, and recipe hash.
- `ConfigRecipesCrucibleSlice.initializeCrucibleRecipeBaseline()` now registers a broad crucible corpus, and `ConfigRecipesSmeltingSlice` now provides the alchemical smelting bonus baseline.
- `TileThaumatorium.getUpgrades()`/brainbox scan is restored, so recipe capacity is no longer hard-locked to the one-slot placeholder baseline.
- `ConfigAspects` now carries a larger alchemy-critical tag baseline for crucible decomposition and recipe-generated tags.

Current blockers are runtime and semantic confidence, not total absence of code:

- crucible/thaumatorium scenarios are not runtime-verified;
- exact/extra essentia behavior and output blocking still need scenario checks;
- research-gate visibility and recipe-handle alignment still need validation;
- fallback and generated tags still need reference comparison in gameplay;
- save/load and mid-operation reload behavior remains unproven.

## 5. Checkpoint notes

- `2026-05-16` — GAP-1 crucible alchemy corpus expanded; `test` and `validate --smoke` passed.
- `2026-05-16` — GAP-2 research-gate static guard added; static only.
- `2026-05-16` — GAP-3 brainbox upgrade baseline restored; `test` and `validate --smoke` passed.
- `2026-05-16` — GAP-4 smelting bonus baseline restored; `test` and `validate --smoke` passed.
- `2026-05-16` — GAP-5 alchemy aspect baseline expanded; `test` and `validate --smoke` passed.
- `2026-05-16` — GAP-6 server smoke baseline refresh passed.

## 6. Gap list

### GAP-1: Crucible/alchemy recipe corpus is present, but parity and runtime coverage remain open

**Статус:** implementation present; parity/runtime validation open
**Критичность:** high

The old “registration is absent” wording is stale. Current code registers a broad crucible/alchemy baseline. Remaining work:

- verify every reference Stage 9-d recipe key, research gate, catalyst, output, and aspect cost;
- verify optional ore-mod gates;
- validate representative recipes in-game or through a runtime harness;
- ensure `ConfigResearch.recipes` handles match the registered recipe objects where pages expect them;
- document intentional deviations.

### GAP-2: Research recipe map and crucible gates are present, but unlock visibility still needs validation

**Статус:** implementation present; validation open
**Критичность:** medium/high

`ConfigResearch.init()` and the recipe slice flow now provide a usable recipe-handle bridge. The remaining work is to prove that the correct research keys expose the correct crucible recipes in live progression, and that missing/intentional display-only handles are clearly distinguished.

### GAP-3: Thaumatorium capacity upgrades are restored, but runtime behavior still needs validation

**Статус:** implementation present; validation open
**Критичность:** high

The brainbox capacity path is back. What remains is runtime proof that adjacent brainboxes raise capacity correctly, shrink/prune extra programmed recipes correctly, and survive save/load without desync.

### GAP-4: Alchemical smelting bonus data is present, but scenario validation remains open

**Статус:** implementation present; runtime validation open
**Критичность:** high

`ConfigRecipesSmeltingSlice` now provides the baseline smelting and bonus registration path. The remaining work is to validate representative furnace and bonus scenarios against the current 1.12.2 item/block registry.

### GAP-5: Alchemy item/block aspect tags are broader, but gameplay validation still matters

**Статус:** implementation present; validation open
**Критичность:** medium/high

`ConfigAspects` now covers a much larger baseline, but Stage 9-d still needs gameplay checks for crucible decomposition, generated tag behavior, and any content that depends on ore or fallback tag lookups.

### GAP-6: Crucible and thaumatorium scenarios are not runtime-verified

**Статус:** requires validation
**Критичность:** medium

This is the main Stage 9-d closure gate. Static guards, source comparison, and server smoke do not close Stage 9-d. Completion requires behavior execution.

Required minimum runtime matrix:

1. Crucible: valid recipe succeeds with completed research.
2. Crucible: same recipe fails without research.
3. Crucible: insufficient aspects fail without consuming catalyst incorrectly.
4. Crucible: no-aspect or invalid item is rejected/spilled according to reference behavior.
5. Thaumatorium: program recipe through `ContainerThaumatorium`.
6. Thaumatorium: fill exact required essentia and craft.
7. Thaumatorium: fill extra/partial essentia and verify remaining/cleared state against reference.
8. Thaumatorium: full output inventory behavior.
9. Save/reload before and after recipe completion.
10. Top/bottom transport behavior through `TileThaumatoriumTop`.

## 7. Итоговый checklist закрытия Stage 9-d

- [x] Crucible recipe corpus and smelting bonus baseline are registered through the current recipe slices.
- [x] Brainbox capacity behavior is restored for thaumatorium recipe programming.
- [ ] Preserve every Stage 9-d recipe ID/name, research key, catalyst, output, aspect cost, and optional ore-mod gate.
- [ ] Ensure direct recipe-handle storage in `ConfigResearch.recipes` for Stage 9-d entries.
- [ ] Verify research completion gates for both `TileCrucible` matching and `ContainerThaumatorium` programming.
- [ ] Validate alchemical smelting bonus registrations in representative scenarios.
- [ ] Add/verify aspect tags for all Stage 9-d catalysts, outputs, and generated-tag dependencies.
- [ ] Run `./scripts/dev.sh smoke-server` after registration, recipe, block/item, or tile behavior changes.
- [ ] Manually verify crucible and thaumatorium scenarios listed in GAP-6.
- [ ] Update `docs/Stage9-d.md` after implementation to remove closed blocker/high gaps or mark them closed with validation evidence.

## 8. Definition of Done

Stage 9-d считается ПОЛНОСТЬЮ завершенной только если:

- все blocker gaps закрыты;
- все high gaps закрыты;
- все элементы из scope Stage 9-d реализованы;
- текущая реализация соответствует референсу по поведению;
- все нужные файлы, ресурсы и регистрации присутствуют;
- отсутствуют TODO и заглушки внутри scope Stage 9-d;
- проект собирается без ошибок;
- базовые игровые сценарии Stage 9-d проверены вручную или тестами;
- `docs/Stage9-d.md` обновлен и не содержит критичных открытых вопросов.

## 9. Открытые вопросы

Metadata mapping must use the current 1.12.2 item/block definitions as source of truth. Reference metadata should not be pasted directly unless it matches the current port.
