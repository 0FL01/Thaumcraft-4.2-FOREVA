# Stage 9-c — Gap-анализ и план закрытия

## 1. Цель фазы

Stage 9-c закрывает только infusion-срез Stage 9: API/классы infusion-рецептов, менеджер поиска, регистрационные данные рецептов, recipe/research IDs, instability, essentia/aspect costs, pedestal components, infusion enchantment recipes и интеграцию Infusion Matrix с поиском/запуском/завершением рецепта.

Цель по PRD: crafting/research/content data должны быть достаточны для progression parity, а infusion recipes и unlock flow должны совпадать с Thaumcraft 4.2.3.5; compile success сам по себе не является parity-доказательством (`docs/PRD.md:5`, `docs/PRD.md:17`, `docs/PRD.md:540`, `docs/PRD.md:545`, `docs/PRD.md:551`).

## 2. Scope фазы

- `thaumcraft.api.crafting.InfusionRecipe` и `InfusionEnchantmentRecipe` как публичная API-модель infusion-рецептов.
- `ThaumcraftApi.addInfusionCraftingRecipe`, `addInfusionEnchantmentRecipe`, `getInfusionRecipe` и общий список `ThaumcraftApi.getCraftingRecipes()`.
- `ThaumcraftCraftingManager.findMatchingInfusionRecipe`, `findMatchingInfusionEnchantmentRecipe` и использование infusion recipes при генерации аспектов предметов.
- `TileInfusionMatrix` как runtime-интеграция lookup/start/progress/completion для infusion crafting и infusion enchantment.
- Полный набор reference infusion crafting registrations из `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`, включая research key, `ConfigResearch.recipes` key, output, instability, aspects, central input, components и conditional mirror recipes.
- Полный набор reference infusion enchantment registrations из `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`, включая custom Thaumcraft enchants и vanilla enchants.
- Dynamic runic armor augmentation, потому что в reference она представлена как `InfusionRunicAugmentRecipe extends InfusionRecipe`.
- Focal manipulator/upgrade recipes: в найденном reference infusion-срезе отдельные focal manipulator upgrade recipes не обнаружены; focal items `FocusHellbat`, `FocusPortableHole`, `FocusWarding` представлены обычными infusion crafting recipes и входят в scope.
- Research pages анализируются только как dependency для отображения и unlock flow recipe IDs; глубокий анализ research tree Stage 9-a/b/d/e не входит в этот документ.
- Client FX/render Stage 8 не анализируется; зависимость отмечается только там, где она мешает ручной проверке Infusion Matrix сценариев.

## 3. Источники сравнения

- PRD и phase acceptance: `docs/PRD.md:5`, `docs/PRD.md:17`, `docs/PRD.md:540`, `docs/PRD.md:545`, `docs/PRD.md:551`.
- Текущий lifecycle вызывает recipe/research init в post-init: `src/main/java/thaumcraft/common/Thaumcraft.java:186`, `src/main/java/thaumcraft/common/Thaumcraft.java:188`, `src/main/java/thaumcraft/common/Thaumcraft.java:190`.
- Текущая registration-заглушка: `src/main/java/thaumcraft/common/config/ConfigRecipes.java:5`, `src/main/java/thaumcraft/common/config/ConfigRecipes.java:7`, `src/main/java/thaumcraft/common/config/ConfigRecipes.java:8`.
- Текущая research-заглушка, напрямую влияющая на recipe gates/pages: `src/main/java/thaumcraft/common/config/ConfigResearch.java:3`, `src/main/java/thaumcraft/common/config/ConfigResearch.java:5`, `src/main/java/thaumcraft/common/config/ConfigResearch.java:6`.
- Текущий API регистрации infusion recipes: `src/main/java/thaumcraft/api/ThaumcraftApi.java:90`, `src/main/java/thaumcraft/api/ThaumcraftApi.java:106`, `src/main/java/thaumcraft/api/ThaumcraftApi.java:115`, `src/main/java/thaumcraft/api/ThaumcraftApi.java:121`, `src/main/java/thaumcraft/api/ThaumcraftApi.java:318`, `src/main/java/thaumcraft/api/ThaumcraftApi.java:323`.
- Текущие recipe classes: `src/main/java/thaumcraft/api/crafting/InfusionRecipe.java:11`, `src/main/java/thaumcraft/api/crafting/InfusionRecipe.java:28`, `src/main/java/thaumcraft/api/crafting/InfusionRecipe.java:64`, `src/main/java/thaumcraft/api/crafting/InfusionEnchantmentRecipe.java:14`, `src/main/java/thaumcraft/api/crafting/InfusionEnchantmentRecipe.java:31`, `src/main/java/thaumcraft/api/crafting/InfusionEnchantmentRecipe.java:107`.
- Текущий crafting manager lookup: `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:302`, `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:442`, `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:452`.
- Текущая Infusion Matrix интеграция: `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:196`, `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:212`, `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:234`, `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:261`, `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:331`.
- Текущие ResearchPage constructors для infusion страниц: `src/main/java/thaumcraft/api/research/ResearchPage.java:56`, `src/main/java/thaumcraft/api/research/ResearchPage.java:84`, `src/main/java/thaumcraft/api/research/ResearchPage.java:90`.
- Reference class files: `thaumcraft_src/thaumcraft/api/crafting/InfusionRecipe.class`, `thaumcraft_src/thaumcraft/api/crafting/InfusionEnchantmentRecipe.class`, `thaumcraft_src/thaumcraft/common/lib/crafting/InfusionRunicAugmentRecipe.class`, `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`, `thaumcraft_src/thaumcraft/common/tiles/TileInfusionMatrix.class`, `thaumcraft_src/thaumcraft/api/ThaumcraftApi.class`.
- Reference lang keys for recipe page display: `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:662`, `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:663`.
- Current lang file lacks those keys in the inspected header/body: `src/main/resources/assets/thaumcraft/lang/en_us.lang:1`, `src/main/resources/assets/thaumcraft/lang/en_us.lang:118`.
- Commands run for this analysis: `git status --short`; `rg -n "Infusion|infusion|InfusionRecipe|InfusionEnchantment|ThaumcraftApi\.addInfusion|addInfusion" src/main/java`; `rg -n "addInfusion(Crafting|Enchantment)Recipe|InfusionRunicAugmentRecipe" src/main/java src/main/resources`; `cfr --silent true thaumcraft_src/thaumcraft/api/crafting/InfusionRecipe.class`; `cfr --silent true thaumcraft_src/thaumcraft/api/crafting/InfusionEnchantmentRecipe.class`; `cfr --silent true thaumcraft_src/thaumcraft/common/lib/crafting/InfusionRunicAugmentRecipe.class`; `cfr --silent true thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class | rg -n "addInfusion(Crafting|Enchantment)Recipe|InfusionRunicAugmentRecipe"`; `cfr --silent true thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class | rg "addInfusionCraftingRecipe" | wc -l`; `cfr --silent true thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class | rg "addInfusionEnchantmentRecipe" | wc -l`; `cfr --silent true thaumcraft_src/thaumcraft/common/tiles/TileInfusionMatrix.class`.

## 4. Текущее состояние Stage 9-c

Текущая реализация имеет базовые классы и lookup surface, но не имеет infusion content data. `ThaumcraftApi` хранит общий `craftingRecipes` список и умеет добавлять `InfusionRecipe`/`InfusionEnchantmentRecipe` (`src/main/java/thaumcraft/api/ThaumcraftApi.java:90`, `src/main/java/thaumcraft/api/ThaumcraftApi.java:106`, `src/main/java/thaumcraft/api/ThaumcraftApi.java:115`, `src/main/java/thaumcraft/api/ThaumcraftApi.java:323`). `InfusionRecipe` и `InfusionEnchantmentRecipe` близки к reference по базовому matching/research/enchantment logic (`src/main/java/thaumcraft/api/crafting/InfusionRecipe.java:28`, `src/main/java/thaumcraft/api/crafting/InfusionEnchantmentRecipe.java:31`). `ThaumcraftCraftingManager` ищет infusion recipes в `ThaumcraftApi.getCraftingRecipes()` (`src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:442`, `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:452`). `TileInfusionMatrix` вызывает эти lookup methods при wand start и умеет переносить output/essentia/components в runtime state (`src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:196`, `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:212`, `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:219`, `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:234`, `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:241`, `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:331`).

Главный blocker: `ConfigRecipes.init()` вызывается в post-init, но сам метод содержит только comment-заглушку (`src/main/java/thaumcraft/common/Thaumcraft.java:186`, `src/main/java/thaumcraft/common/Thaumcraft.java:188`, `src/main/java/thaumcraft/common/config/ConfigRecipes.java:7`, `src/main/java/thaumcraft/common/config/ConfigRecipes.java:8`). Current scan нашел только API methods, но ни одной реализации `addInfusionCraftingRecipe`/`addInfusionEnchantmentRecipe` вне `ThaumcraftApi` (`rg -n "addInfusion(Crafting|Enchantment)Recipe|InfusionRunicAugmentRecipe" src/main/java src/main/resources`). Reference `ConfigRecipes.class` содержит 63 `addInfusionCraftingRecipe` registrations, 24 `addInfusionEnchantmentRecipe` registrations и отдельное добавление `InfusionRunicAugmentRecipe`.

Из-за отсутствующих data registrations Stage 9-c нельзя считать завершенной: Infusion Matrix может искать рецепты, но список пуст для всех Thaumcraft infusion outputs. Дополнительно отсутствует dynamic class `src/main/java/thaumcraft/common/lib/crafting/InfusionRunicAugmentRecipe.java`, отсутствует population of `ConfigResearch.recipes`, отсутствуют research entries/pages для прямого unlock/display flow, и не проведена ручная runtime проверка altar scenarios.

## 5. Gap list

### GAP-1: Отсутствует регистрация всех infusion crafting recipes

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigRecipes.java:5`
- `src/main/java/thaumcraft/common/config/ConfigRecipes.java:7`
- `src/main/java/thaumcraft/common/config/ConfigRecipes.java:8`
- `src/main/java/thaumcraft/common/Thaumcraft.java:188`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`
- `thaumcraft_src/thaumcraft/api/ThaumcraftApi.class`

**Что не совпадает:**

Reference `ConfigRecipes.initializeArcaneRecipes()` регистрирует 63 `ThaumcraftApi.addInfusionCraftingRecipe(...)` записи и кладет их в `ConfigResearch.recipes.put(...)`. Current `ConfigRecipes.init()` вообще не вызывает `ThaumcraftApi.addInfusionCraftingRecipe`, поэтому `ThaumcraftApi.getCraftingRecipes()` не содержит Thaumcraft infusion crafting data, а `ThaumcraftCraftingManager.findMatchingInfusionRecipe()` не может найти ни один content recipe (`src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:442`, `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:444`).

Reference set включает как минимум recipe keys/outputs из decompiled `ConfigRecipes.class`: wand caps/rods (`WandCapSilver`, `WandCapThaumium`, `WandCapVoid`, `WandRodObsidian`, `WandRodIce`, `WandRodQuartz`, `WandRodReed`, `WandRodBlaze`, `WandRodBone`, `WandRodSilverwood`, `WandRodPrimalStaff`), foci (`FocusHellbat`, `FocusPortableHole`, `FocusWarding`), blocks/devices (`WandPed`, `WandPedFocus`, `NodeStabilizerAdv`, `JarBrain`, `ArcaneBore`, `LampGrowth`, `LampFertility`, `EssentiaReservoir`), golem/core recipes (`AdvancedGolem`, `CoreAlchemy`, `CoreSorting`, `CoreLumber`, `CoreFishing`, `CoreUse`), baubles/runic/flight (`HoverHarness`, `HoverGirdle`, `VisAmulet`, `RunicAmulet`, `RunicAmuletEmergency`, `RunicRing`, `RunicRingCharged`, `RunicRingHealing`, `RunicGirdle`, `RunicGirdleKinetic`, `RunicGirdleKinetic_2`), optional mirrors under `Config.allowMirrors`, elemental tools, armor/robes/masks, `SanityCheck`, `SinStone`, `PrimalCrusher`, `EldritchEye`.

**Что нужно доделать:**

Port the reference infusion crafting registration block into the 1.12.2 `ConfigRecipes.init()` flow, preserving research keys, `ConfigResearch.recipes` map keys, output stacks/NBT object outputs, instability values, AspectList costs, central input, component order and conditional config gates.

**Как доделать:**
- Add 1.12.2 imports and registration code in `src/main/java/thaumcraft/common/config/ConfigRecipes.java`.
- Use existing 1.12.2 `ConfigItems`, `ConfigBlocks`, `Config`, `Aspect`, `AspectList`, `ThaumcraftApi`, vanilla `Items`/`Blocks`, `NBTTagByte`, `NBTTagInt` equivalents.
- Preserve `ConfigResearch.recipes.put("key", recipe)` keys exactly as reference because research pages later look up these IDs.
- Preserve reference research gates, for example `"CAP_thaumium"`, `"FOCUSPORTABLEHOLE"`, `"RUNICARMOR"`, `"MASKGRINNINGDEVIL"`, `"OCULUS"`.
- Preserve optional mirror gating through the current 1.12.2 mirror config field if present; if absent, port the config field or document the dependency before changing behavior.
- Run a focused scan after implementation to verify current code contains all reference `ConfigResearch.recipes` keys and 63 `addInfusionCraftingRecipe` calls or intentionally documented 1.12-only substitutes.

**Критерии приемки:**
- [ ] `ConfigRecipes.init()` registers every reference infusion crafting recipe or documents a justified 1.12.2-incompatible exception with replacement behavior.
- [ ] `rg -n "addInfusionCraftingRecipe" src/main/java/thaumcraft/common/config/ConfigRecipes.java` reports the expected reference count: 63, plus any explicitly documented additions/removals.
- [ ] Every registered recipe preserves reference `ConfigResearch.recipes` key, research gate, instability, AspectList costs, central input and components.
- [ ] At least one representative recipe from each subgroup can be found via `ThaumcraftCraftingManager.findMatchingInfusionRecipe` in a focused test or manual scenario.

**Риски / зависимости:**

Some referenced outputs/items/blocks may still be partial in earlier phases; that is a direct dependency only where a recipe cannot instantiate its input/output. Missing implementation should be handled by fixing the target class/item/block, not by silently dropping the recipe. Research page creation is a direct Stage 9 dependency and covered separately in GAP-4.

### GAP-2: Отсутствует регистрация всех infusion enchantment recipes

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigRecipes.java:7`
- `src/main/java/thaumcraft/common/config/ConfigRecipes.java:8`
- `src/main/java/thaumcraft/api/ThaumcraftApi.java:115`
- `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:452`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`
- `thaumcraft_src/thaumcraft/api/crafting/InfusionEnchantmentRecipe.class`
- `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:663`

**Что не совпадает:**

Current API can construct `InfusionEnchantmentRecipe`, and the matrix can look for them, but current code registers zero enchantment infusion recipes. Reference `ConfigRecipes.class` registers 24 entries: `InfEnchRepair`, `InfEnchHaste`, and `InfEnch0` through `InfEnch21`, all gated by `"INFUSIONENCHANTMENT"`, with specific vanilla/custom enchantment IDs, instability values, aspects and components. Without these registrations, the reference Thaumonomicon infusion enchantment flow is unusable even if `InfusionEnchantmentRecipe.matches()` exists (`src/main/java/thaumcraft/api/crafting/InfusionEnchantmentRecipe.java:31`, `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:234`).

**Что нужно доделать:**

Port all reference infusion enchantment registrations to 1.12.2 enchantment objects and current Thaumcraft custom enchant fields.

**Как доделать:**
- Add all `ThaumcraftApi.addInfusionEnchantmentRecipe` calls to `src/main/java/thaumcraft/common/config/ConfigRecipes.java`.
- Map 1.7.10 `Enchantment.field_...` references to 1.12.2 `Enchantments.*` or `Config.enchRepair`/`Config.enchHaste`/equivalent current custom enchant objects.
- Preserve `ConfigResearch.recipes.put` keys: `InfEnchRepair`, `InfEnchHaste`, `InfEnch0` ... `InfEnch21`.
- Preserve research gate `"INFUSIONENCHANTMENT"`, instability, aspects and component stacks.
- Verify `InfusionEnchantmentRecipe.matches()` works for enchantable vanilla tools/armor and respects max level/compatibility (`src/main/java/thaumcraft/api/crafting/InfusionEnchantmentRecipe.java:35`, `src/main/java/thaumcraft/api/crafting/InfusionEnchantmentRecipe.java:38`, `src/main/java/thaumcraft/api/crafting/InfusionEnchantmentRecipe.java:42`).

**Критерии приемки:**
- [ ] `rg -n "addInfusionEnchantmentRecipe" src/main/java/thaumcraft/common/config/ConfigRecipes.java` reports the expected reference count: 24.
- [ ] Custom enchant recipes for Repair and Haste are registered and use current 1.12.2 custom enchant objects.
- [ ] Vanilla enchant recipes map to the correct 1.12.2 enchantments and preserve reference instability/aspect/component data.
- [ ] Manual or focused test scenario can perform at least one armor enchant, one weapon enchant and one tool enchant through Infusion Matrix.

**Риски / зависимости:**

Custom enchant registration must already expose usable 1.12.2 `Enchantment` instances. If current custom enchant fields are IDs or are registered differently, this blocks direct porting and must be fixed in the enchantment/config layer before Stage 9-c can close.

### GAP-3: Отсутствует dynamic InfusionRunicAugmentRecipe

**Статус:** частично закрыт (server/runtime baseline implemented)
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/crafting/InfusionRunicAugmentRecipe.java`
- `src/main/java/thaumcraft/common/config/ConfigRecipes.java`
- `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java`

**Референс:**
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/common/lib/crafting/InfusionRunicAugmentRecipe.class`
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/common/tiles/TileInfusionMatrix.class`
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/common/config/ConfigRecipes.class`

**Что не совпадает:**

Port now contains `InfusionRunicAugmentRecipe extends InfusionRecipe` with reference-aligned research gate (`RUNICAUGMENTATION`), `IRunicArmor` central-item check, dynamic component sizing from `EventHandlerRunic.getFinalCharge(input)`, `RS.HARDEN` output mutation, dynamic aspects (`32 * 2^charge` split into ARMOR/MAGIC and full ENERGY), and instability formula `5 + finalCharge / 2`.

`ConfigRecipes.init()` now adds `new InfusionRunicAugmentRecipe()` into `ThaumcraftApi.getCraftingRecipes()` with an idempotence guard, and `TileInfusionMatrix.craftingStart` now special-cases `InfusionRunicAugmentRecipe` to use `getComponents(recipeInput)` for dynamic pedestal ingredient capture as in reference.

**Что нужно доделать:**

Keep and validate the runic augment recipe path in live infusion scenarios.

**Как доделать:**
- Add focused runtime coverage for first and repeated runic augment infusions.
- Confirm final consumed components count and hardening NBT mutation under server runtime.

**Критерии приемки:**
- [x] `src/main/java/thaumcraft/common/lib/crafting/InfusionRunicAugmentRecipe.java` exists and matches reference behavior for research, matching, output NBT, aspects, instability and components.
- [x] The recipe is present in `ThaumcraftApi.getCraftingRecipes()` after `ConfigRecipes.init()`.
- [x] `TileInfusionMatrix` uses dynamic `getComponents(recipeInput)` for this recipe path.
- [ ] Infusion Matrix consumes the correct number of runic augmentation components for current central-item final charge.
- [ ] Manual scenario verifies at least first and repeated runic augmentation attempts on a supported runic armor/bauble item.

**Риски / зависимости:**

This depends on the current runic armor implementation and hardening/final-charge storage. If runic armor NBT or helper methods differ from reference, the recipe must bridge to current behavior while preserving reference NBT key `RS.HARDEN` unless a prior phase explicitly changed it.

### GAP-4: Research recipe map/pages for infusion unlock/display are absent

**Статус:** отсутствует  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigResearch.java:3`
- `src/main/java/thaumcraft/common/config/ConfigResearch.java:5`
- `src/main/java/thaumcraft/common/config/ConfigResearch.java:6`
- `src/main/java/thaumcraft/api/research/ResearchPage.java:84`
- `src/main/java/thaumcraft/api/research/ResearchPage.java:90`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`
- `thaumcraft_src/thaumcraft/common/config/ConfigResearch.class`
- `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:1518`
- `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:1519`
- `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:1520`

**Что не совпадает:**

Reference recipe registration stores every infusion recipe handle in `ConfigResearch.recipes` under stable IDs, then research pages can reference those handles and unlock/display recipes. Current `ConfigResearch.init()` is a stub and no `ConfigResearch.recipes` map was found in current source, while `ConfigRecipes.init()` also does not populate such a map. Current `ResearchPage` has constructors for infusion recipes and infusion enchantment recipes, but nothing creates pages that reference actual infusion recipe objects (`src/main/java/thaumcraft/api/research/ResearchPage.java:84`, `src/main/java/thaumcraft/api/research/ResearchPage.java:90`).

This is a direct Stage 9-c dependency because `InfusionRecipe.matches()` and `InfusionEnchantmentRecipe.matches()` enforce research completion (`src/main/java/thaumcraft/api/crafting/InfusionRecipe.java:32`, `src/main/java/thaumcraft/api/crafting/InfusionEnchantmentRecipe.java:32`), and because PRD requires recipe/research unlock flows (`docs/PRD.md:542`, `docs/PRD.md:543`, `docs/PRD.md:545`).

**Что нужно доделать:**

Recreate enough of the reference `ConfigResearch.recipes` registry and infusion research pages so registered infusion recipes have stable handles, appear in Thaumonomicon pages and are gated by matching research keys.

**Как доделать:**
- Add or restore `ConfigResearch.recipes` with the reference semantics if it is not implemented elsewhere.
- In `ConfigRecipes.init()`, put every Stage 9-c infusion recipe into `ConfigResearch.recipes` using reference keys.
- In `ConfigResearch.init()`, create or restore research entries/pages that directly include infusion recipes and infusion enchantment recipes with `new ResearchPage(...)`.
- Verify the research key in each `InfusionRecipe`/`InfusionEnchantmentRecipe` matches the page/unlock key that should grant access.
- Keep analysis and implementation limited to direct infusion gates/pages; do not expand into unrelated arcane/crucible pages while closing this gap.

**Критерии приемки:**
- [ ] A stable `ConfigResearch.recipes` map or equivalent exists and contains all Stage 9-c recipe IDs from reference.
- [ ] Every registered infusion recipe has a matching research page or documented direct unlock path.
- [ ] `InfusionRecipe.matches()` returns false before research and true after the relevant research completion for representative recipes.
- [ ] Thaumonomicon displays infusion and infusion enchantment recipes without null recipe/page crashes.

**Риски / зависимости:**

This overlaps with broader Stage 9 research registration, but only the infusion recipe handles/pages are required here. If the whole research tree is still absent, Stage 9-c remains blocked even if recipes are registered, because players cannot discover/unlock them normally.

### GAP-5: Infusion completion crafting event uses wrong inventory context

**Статус:** частично закрыт (reference-aligned event inventory source)  
**Критичность:** medium

**Текущая реализация:**
- `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java`
- `src/main/java/thaumcraft/common/container/InventoryFake.java`

**Референс:**
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/common/tiles/TileInfusionMatrix.class`
- `Thaumcraft-1.7.10-4.2.3.5.jar!/thaumcraft/common/container/InventoryFake.class`

**Что не совпадает:**

Reference `craftingFinish` fires Forge `ItemCraftedEvent` with `new InventoryFake(this.recipeIngredients)`. Port now follows this behavior: `TileInfusionMatrix` passes `recipeIngredients` and `InventoryFake` has a `List<ItemStack>` constructor matching reference usage. The previous central-input-only inventory context divergence is removed.

**Что нужно доделать:**

Keep Infusion Matrix completion aligned with reference by firing the crafting event via `new InventoryFake(this.recipeIngredients)`.

**Как доделать:**
- Keep `TileInfusionMatrix.craftingFinish` wired to `new InventoryFake(this.recipeIngredients)`.
- Keep `InventoryFake` constructor surface compatible with list-based callsites.
- Verify no unrelated public API signature changes are required.

**Критерии приемки:**
- [x] `ItemCraftedEvent` inventory source is `recipeIngredients`, not only the central input.
- [x] The event still fires after ItemStack outputs, NBT-tag outputs and enchantment outputs.
- [ ] A manual or test listener confirms expected inventory contents during one representative infusion completion.

**Риски / зависимости:**

`recipeIngredients` is consumed during craft progress, so by `craftingFinish` the list may already be empty in both reference and port; this remains a known behavioral limitation unless a future change intentionally diverges from 1.7.10 semantics.

### GAP-6: Infusion recipe page localization keys are missing from current assets

**Статус:** частично закрыт (ключи добавлены, GUI-runtime проверка открыта)  
**Критичность:** medium

**Текущая реализация:**
- `src/main/resources/assets/thaumcraft/lang/en_us.lang:1`
- `src/main/resources/assets/thaumcraft/lang/en_us.lang:118`

**Референс:**
- `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:662`
- `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:663`

**Что не совпадает:**

Reference has `recipe.type.infusion=Arcane Infusion` and `recipe.type.infusionenchantment=Infusion Enchantment`. These keys are now present in the current `en_us.lang`, removing the known missing-localization baseline for infusion recipe type labels.

**Что нужно доделать:**

Keep the reference recipe type localization keys in current assets for infusion research/page display.

**Как доделать:**
- Keep `recipe.type.infusion` and `recipe.type.infusionenchantment` in `src/main/resources/assets/thaumcraft/lang/en_us.lang`.
- Verify live GUI rendering once Stage 8/9 client research-page runtime checks are available.

**Критерии приемки:**
- [x] Current `en_us.lang` contains `recipe.type.infusion=Arcane Infusion`.
- [x] Current `en_us.lang` contains `recipe.type.infusionenchantment=Infusion Enchantment`.
- [ ] Infusion and infusion enchantment Thaumonomicon pages render localized recipe type names.

**Риски / зависимости:**

This is content/display-facing and overlaps with broader research GUI/runtime parity. Key presence is now covered; live page rendering still depends on Stage 8/9 client runtime validation.

### GAP-7: Infusion Matrix runtime scenarios are not manually validated

**Статус:** требует проверки  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:97`
- `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:181`
- `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:196`
- `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:261`
- `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:331`
- `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:613`

**Референс:**
- `thaumcraft_src/thaumcraft/common/tiles/TileInfusionMatrix.class`

**Что не совпадает:**

Static comparison shows current matrix contains many corresponding server-side paths: multiblock validation, wand activation/start, recipe lookup, essentia drain, component consumption, instability events and completion. However, no runtime smoke/manual evidence was found for Stage 9-c scenarios, and PRD/AGENTS explicitly disallow parity claims based on compile success alone (`docs/PRD.md:17`, `docs/PRD.md:551`, `AGENTS.md:157`, `AGENTS.md:173`). Because current recipe data is absent, most matrix recipe paths cannot currently be exercised with real Thaumcraft recipes.

Some behavior also differs or needs focused confirmation after recipe data lands: current `readCustomNBT`/`writeCustomNBT` persist more recipe state than reference (`src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:107`, `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:141`), current enchantment NBT stores registry names (`src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:124`, `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:163`), current flux placement checks replaceability (`src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:568`, `src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java:581`), and client FX packets are less complete than reference. These are not all Stage 9-c blockers by themselves, but they must be scenario-tested before claiming completion.

**Что нужно доделать:**

After data gaps are fixed, run focused runtime/manual scenarios for infusion crafting and infusion enchantments through the actual Infusion Matrix.

**Как доделать:**
- Validate multiblock activation with matrix, pedestal and pillars.
- Validate one low-instability normal item recipe, one NBT output recipe, one high-instability recipe, one optional/config-gated recipe if enabled, one runic augmentation recipe and one infusion enchantment recipe.
- Confirm research gating: recipe does not start before research and does start after research.
- Confirm essentia drain from jars, component consumption from pedestals, container item handling, output placement, crafted event firing and failure/instability behavior.
- Run `./scripts/dev.sh compileJava` and server smoke after code changes, then manual in-game validation; client smoke is needed for page/display/FX crashes if display is available.

**Критерии приемки:**
- [ ] Server starts without registry/config/recipe crashes after infusion recipes register.
- [ ] Infusion Matrix starts and completes representative normal, NBT-output, runic augment and enchantment recipes.
- [ ] Research-gated recipes fail before unlock and work after unlock.
- [ ] Instability/failure scenarios do not crash and produce expected side effects.
- [ ] Validation commands and manual scenarios are documented in this file or the implementation checkpoint report.

**Риски / зависимости:**

Dependency: Stage 8/client FX/render work may affect visual confirmation and client smoke, but it does not excuse missing server-side recipe lookup/completion validation. Missing recipe/research data from GAP-1 through GAP-4 blocks meaningful runtime validation.

## 6. Итоговый checklist закрытия Stage 9-c

- [ ] Port all 63 reference infusion crafting recipe registrations into `ConfigRecipes.init()` or a called helper.
- [ ] Port all 24 reference infusion enchantment recipe registrations.
- [ ] Preserve all reference `ConfigResearch.recipes` keys and research gates for infusion recipes.
- [x] Add/restore dynamic `InfusionRunicAugmentRecipe` and register it.
- [x] Ensure `TileInfusionMatrix` handles dynamic runic components correctly.
- [x] Fix `ItemCraftedEvent` inventory context for infusion completion.
- [x] Add missing infusion recipe type localization keys.
- [ ] Confirm all recipe outputs, central inputs and components refer to existing 1.12.2 registered items/blocks/enchantments.
- [ ] Confirm `ThaumcraftApi.getCraftingRecipes()` contains the expected infusion recipes after post-init.
- [ ] Confirm `ThaumcraftCraftingManager.findMatchingInfusionRecipe` and `findMatchingInfusionEnchantmentRecipe` find representative recipes.
- [ ] Confirm research gating works before/after unlock for representative recipes.
- [ ] Run compile/build validation after implementation.
- [ ] Run server smoke because recipes, registries/content data and tile runtime are affected.
- [ ] Run client smoke or manual client validation for Thaumonomicon infusion recipe pages if display is available.
- [ ] Manually validate representative Infusion Matrix scenarios.

## 7. Definition of Done

Stage 9-c считается ПОЛНОСТЬЮ завершенной только если:
- все blocker gaps закрыты;
- все high gaps закрыты;
- все элементы из scope Stage 9-c реализованы;
- текущая реализация соответствует референсу по поведению;
- все нужные файлы, ресурсы и регистрации присутствуют;
- отсутствуют TODO и заглушки внутри scope Stage 9-c;
- проект собирается без ошибок;
- базовые игровые сценарии Stage 9-c проверены вручную или тестами;
- ./docs/Stage9-c.md обновлен и не содержит критичных открытых вопросов.

## 8. Открытые вопросы

- Нужно подтвердить точные 1.12.2 mappings для двух custom infusion enchantments Repair/Haste: reference использует `ThaumcraftApi.enchantRepair` и `ThaumcraftApi.enchantHaste`, а текущий порт должен предоставить реальные `Enchantment` instances для `ThaumcraftApi.addInfusionEnchantmentRecipe`.
- Нужно подтвердить текущий mirror config field для условных `Mirror`, `MirrorHand`, `MirrorEssentia` recipes; reference gates them by `Config.allowMirrors`.
- Нужно подтвердить после реализации, что все reference outputs/components существуют в текущем 1.12.2 item/block registry; отсутствующие цели являются dependency blockers для соответствующих recipes.
