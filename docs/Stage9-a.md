# Stage 9-a — Gap-анализ и план закрытия

## 1. Цель фазы

Stage 9-a закрывает фундамент контентных регистраций для рецептов и данных, которые нужны для progression parity в Forge 1.12.2: lifecycle `ConfigRecipes`, vanilla/Forge recipes, smelting, smelting bonus, object aspect tags, ore dictionary integration, recipe ids/naming и базовый аудит отсутствующих/дублирующихся рецептов.

Фаза не включает глубокий разбор arcane/infusion/crucible рецептов и research pages, кроме инфраструктуры, через которую эти данные регистрируются и связываются с `ConfigResearch.recipes`.

Stage 9-a сейчас нельзя считать завершенной: есть blocker/high gaps в `ConfigRecipes`, Forge recipe registration, resource recipes, smelting, ore dictionary flags и object tags.

## 2. Scope фазы

- `thaumcraft.common.config.ConfigRecipes`: структура регистрации, lifecycle, helper methods для shaped/shapeless ore/NBT recipes, smelting и smelting bonus.
- Forge 1.12.2 recipe data: `src/main/resources/assets/thaumcraft/recipes/*.json` и/или `RegistryEvent.Register<IRecipe>` для custom recipes, где JSON недостаточен.
- Vanilla/normal crafting recipes, которые в 1.7.10 были обычными `GameRegistry`/`CraftingManager` рецептами.
- Smelting recipes и `ThaumcraftApi.addSmeltingBonus` parity.
- `ConfigAspects` object tags, complex object tags, ore dictionary tags, mod item/block tags, grouped metadata tags.
- `Config.initModCompatibility` ore dictionary scan: `foundCopper*`, `foundTin*`, `foundSilver*`, `foundLead*`, compat aspect tags, compat nuggets/smelting recipes.
- Recipe IDs/naming conventions for Forge 1.12.2 and audit foundation for missing/duplicate ids.
- Runtime/manual validation scenarios for recipe load, smelting, ore dictionary detection and scanning/object aspect visibility.

## 3. Источники сравнения

- Current PRD: `docs/PRD.md:395-416`, `docs/PRD.md:540-545`, `docs/PRD.md:547-556`.
- Current lifecycle: `src/main/java/thaumcraft/common/Thaumcraft.java:164-193`, `src/main/java/thaumcraft/common/Thaumcraft.java:215-281`.
- Current recipes: `src/main/java/thaumcraft/common/config/ConfigRecipes.java:1-14`.
- Current aspects: `src/main/java/thaumcraft/common/config/ConfigAspects.java:10-191`.
- Current ore flags/compat: `src/main/java/thaumcraft/common/config/Config.java:165-173`, `src/main/java/thaumcraft/common/config/Config.java:356-358`.
- Current recipe consumers/generation: `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:262-333`, `src/main/java/thaumcraft/api/ThaumcraftApi.java:67-88`, `src/main/java/thaumcraft/api/ThaumcraftApi.java:219-272`.
- Current custom recipes: `src/main/java/thaumcraft/common/items/armor/RecipesRobeArmorDyes.java:13-110`, `src/main/java/thaumcraft/common/items/armor/RecipesVoidRobeArmorDyes.java:5-10`.
- Reference class files: `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`, `thaumcraft_src/thaumcraft/common/config/ConfigAspects.class`, `thaumcraft_src/thaumcraft/common/config/Config.class`, `thaumcraft_src/thaumcraft/common/Thaumcraft.class`, `thaumcraft_src/thaumcraft/api/ThaumcraftApi.class`.
- Decompiled reference lines captured during analysis: `/home/stfu/.local/share/opencode/tool-output/tool_e27f2d61a001uI7XLw1VyBGdph:63-79`, `/home/stfu/.local/share/opencode/tool-output/tool_e27f2d61a001uI7XLw1VyBGdph:413-530`, `/home/stfu/.local/share/opencode/tool-output/tool_e27f30df9001wfBWATRSNLbBpT:30-37`, `/home/stfu/.local/share/opencode/tool-output/tool_e27f30df9001wfBWATRSNLbBpT:81-461`.
- Commands run: `git status --short`; `cfr --silent true thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`; `cfr --silent true thaumcraft_src/thaumcraft/common/config/ConfigAspects.class`; `cfr --silent true thaumcraft_src/thaumcraft/common/config/Config.class | rg -n "foundCopper|OreDictionary|getOres|registerOre|initModCompatibility"`; `cfr --silent true thaumcraft_src/thaumcraft/common/Thaumcraft.class | rg -n "initModCompatibility|ConfigRecipes|ConfigAspects|preInit|postInit|init\\("`; focused `rg`/glob scans for recipes, smelting, ore dictionary and TODO/stub markers.

## 4. Текущее состояние Stage 9-a

- `docs/Stage9-a.md` отсутствовал до этого анализа; создан заново.
- `ConfigRecipes.init()` существует, но является заглушкой с комментарием `Phase 9: register all recipes`; `oreDictRecipe(Object input, Object[] output)` также заглушка и имеет неправильную форму API для реального 1.12.2 recipe registration (`src/main/java/thaumcraft/common/config/ConfigRecipes.java:7-13`).
- `Thaumcraft.postInit()` вызывает `ConfigRecipes.init()`, затем `ConfigAspects.init()`, затем `ConfigResearch.init()`, затем `Config.initModCompatibility()` (`src/main/java/thaumcraft/common/Thaumcraft.java:186-192`). В референсе порядок другой: `Config.initModCompatibility()`, `ConfigItems.postInit()`, `ConfigRecipes.init()`, `ConfigAspects.init()`, `ConfigResearch.init()` (`thaumcraft_src/thaumcraft/common/Thaumcraft.class`; decompiled `/home/stfu/.local/share/opencode/tool-output/tool_e27f2d61a001uI7XLw1VyBGdph` не содержит lifecycle, команда показала reference lines 210-217).
- Нет `RegistryEvent.Register<IRecipe>` handler для custom recipes (`src/main/java/thaumcraft/common/Thaumcraft.java:215-281` содержит blocks/items/entities/potions/enchantments/biomes/villagers only).
- Нет Forge 1.12.2 JSON recipes: `src/main/resources/assets/thaumcraft/recipes/` отсутствует; `src/main/resources/data/` отсутствует.
- Smelting registration отсутствует в текущем `ConfigRecipes`; `ThaumcraftApi.addSmeltingBonus` API есть (`src/main/java/thaumcraft/api/ThaumcraftApi.java:67-88`), но текущий код не регистрирует bonuses.
- `ConfigAspects` регистрирует только малую hand-written часть vanilla/object/ore tags (`src/main/java/thaumcraft/common/config/ConfigAspects.java:12-190`), тогда как reference `ConfigAspects` содержит entity tags, много vanilla tags, ore dictionary tags, mod item/block tags и complex tags (`thaumcraft_src/thaumcraft/common/config/ConfigAspects.class`; decompiled `/home/stfu/.local/share/opencode/tool-output/tool_e27f30df9001wfBWATRSNLbBpT:30-37`, `/home/stfu/.local/share/opencode/tool-output/tool_e27f30df9001wfBWATRSNLbBpT:81-461`).
- Ore dictionary flags объявлены, но не заполняются (`src/main/java/thaumcraft/common/config/Config.java:165-173`), а `Config.initModCompatibility()` является Phase 4 stub (`src/main/java/thaumcraft/common/config/Config.java:356-358`).
- Existing robe dye custom recipe classes implement `IRecipe`, but are never registered and have no registry names in current Stage 9-a infrastructure (`src/main/java/thaumcraft/common/items/armor/RecipesRobeArmorDyes.java:13-110`, `src/main/java/thaumcraft/common/items/armor/RecipesVoidRobeArmorDyes.java:5-10`).
- Recipe id/naming audit foundation is absent: no `ResourceLocation` naming strategy, no duplicate/missing recipe scan, no current file list to compare against reference recipe keys.

## 5. Gap list

### GAP-1: `ConfigRecipes` является заглушкой вместо registration foundation

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigRecipes.java:5-14`
- `src/main/java/thaumcraft/common/Thaumcraft.java:186-192`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`
- `/home/stfu/.local/share/opencode/tool-output/tool_e27f2d61a001uI7XLw1VyBGdph:63-79`

**Что не совпадает:**

Reference `ConfigRecipes.init()` initializes basic wand data, smelting, normal recipes, arcane recipes, infusion recipes, infusion enchantment recipes, alchemy recipes, compound recipes, and custom recipe sorter entries. Current `init()` does nothing except a comment. Reference helper methods return `IRecipe`; current `oreDictRecipe(Object input, Object[] output)` returns `void`, has unusable parameter names/types for recipe output registration, and is also empty.

**Что нужно доделать:**

Build the Forge 1.12.2 recipe registration foundation before filling all later Stage 9 recipe families. For Stage 9-a, this foundation must at least support vanilla/Forge recipes, smelting, smelting bonuses, custom `IRecipe` registration, stable ids and storing recipe objects where research infrastructure expects them.

**Как доделать:**
- `src/main/java/thaumcraft/common/config/ConfigRecipes.java`: replace stubs with real Stage 9-a methods: `init()`, `initializeSmelting()`, `initializeNormalRecipes()` or equivalent split, `oreDictRecipe(ItemStack, Object[])`, `shapelessOreDictRecipe(ItemStack, Object[])`, NBT shapeless helper if needed by normal recipes.
- `src/main/java/thaumcraft/common/Thaumcraft.java`: add recipe registry event or call a dedicated `ConfigRecipes.registerRecipes(IForgeRegistry<IRecipe>)` during `RegistryEvent.Register<IRecipe>` for non-JSON/custom recipes.
- Preserve reference recipe keys when storing into `ConfigResearch.recipes` where recipe unlock infrastructure depends on them.

**Критерии приемки:**
- [ ] `ConfigRecipes.init()` has no Phase 9 stub comments and runs concrete Stage 9-a registration steps.
- [ ] Helpers return registered `IRecipe` instances or stable handles needed by research/unlock code.
- [ ] Stage 9-a recipe registration is reachable in Forge 1.12.2 lifecycle without relying on post-registration mutation that Forge ignores.
- [ ] Focused load test confirms no missing registry names or duplicate recipe ids.

**Риски / зависимости:**

Dependency: Stage 9-b/c/d/e will depend on the same `ConfigRecipes` foundation, but this gap is Stage 9-a because ordinary recipes, smelting and recipe IDs cannot work without it.

### GAP-2: Forge 1.12.2 JSON recipe data отсутствует полностью

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/resources/assets/thaumcraft/recipes/` absent
- `src/main/resources/data/` absent
- `build.gradle:47-55`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`
- `/home/stfu/.local/share/opencode/tool-output/tool_e27f2d61a001uI7XLw1VyBGdph:413-481`

**Что не совпадает:**

Reference Minecraft 1.7.10 registers ordinary crafting recipes in code through `CraftingManager` and `GameRegistry`. Forge 1.12.2 should use JSON recipes where appropriate, but the current port has no recipe JSON resource location at all. The resource pipeline copies non-`mcmod.info` resources (`build.gradle:53-55`), so recipe JSON files would be packaged if present, but they are absent.

**Что нужно доделать:**

Create the Stage 9-a vanilla/Forge JSON recipe corpus for ordinary shaped/shapeless recipes that do not require NBT or custom code, using stable `thaumcraft:<id>` names and outputs matching current registry names.

**Как доделать:**
- Add `src/main/resources/assets/thaumcraft/recipes/*.json` for simple vanilla/Forge recipes.
- Keep custom/NBT/dynamic recipes in Java via an `IRecipe` registry event.
- Map reference normal recipe outputs from `/home/stfu/.local/share/opencode/tool-output/tool_e27f2d61a001uI7XLw1VyBGdph:413-481` to 1.12.2 item/block registry names from `ConfigItems` and `ConfigBlocks`.
- Add an audit list that records which reference recipe keys are JSON, which are code recipes, and which are deferred to Stage 9-b/c/d/e because they are arcane/infusion/crucible/research-page dependent.

**Критерии приемки:**
- [ ] `src/main/resources/assets/thaumcraft/recipes/` exists and contains Stage 9-a ordinary JSON recipes.
- [ ] Every JSON recipe has a stable lowercase `thaumcraft` recipe id and references valid registered items/blocks.
- [ ] Recipes requiring NBT/custom matching are not incorrectly represented as lossy JSON.
- [ ] Server/client load logs show recipes parsed without JSON errors.

**Риски / зависимости:**

Forge 1.12.2 recipe JSON cannot represent all reference behaviors. NBT/capability/dynamic recipes must remain Java recipes with explicit registry names.

### GAP-3: Vanilla/normal crafting recipe parity отсутствует

**Статус:** отсутствует  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigRecipes.java:7-13`
- `src/main/resources/assets/thaumcraft/recipes/` absent

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`
- `/home/stfu/.local/share/opencode/tool-output/tool_e27f2d61a001uI7XLw1VyBGdph:413-481`

**Что не совпадает:**

Reference `initializeNormalRecipes()` registers nugget decompression/compression, bauble blanks, meat treats, greatwood/silverwood planks/stairs/slabs, thaumium/void armor/tools, table, phial, scribing tools, thaumometer, crystal clusters, cosmetic blocks and other ordinary recipes. Current port registers none of them.

**Что нужно доделать:**

Port the normal recipe set into Forge 1.12.2 JSON and Java custom recipes, keeping output parity and research recipe handles where applicable.

**Как доделать:**
- `src/main/resources/assets/thaumcraft/recipes/*.json`: simple shaped/shapeless recipes.
- `src/main/java/thaumcraft/common/config/ConfigRecipes.java`: Java recipes for ore dictionary, NBT and dynamic outputs.
- `src/main/java/thaumcraft/common/config/ConfigResearch.java`: ensure recipe-key map exists before `ConfigRecipes` stores handles, or move only infrastructure-safe recipe handle storage into Stage 9-a.
- Validate key examples from reference: `WandBasic`, `WandCapIron`, `Table`, `Phial`, `Thaumometer`, `Thaumium*`, `Void*`, `Clusters*`, `JarLabel*`.

**Критерии приемки:**
- [ ] All Stage 9-a normal reference recipes are present as JSON or registered Java recipes.
- [ ] Output counts and metadata/subtype values match the original where the current item/block implementation supports them.
- [ ] Ore dictionary ingredients such as `stickWood`, `slabWood`, `plankWood`, `ingotThaumium`, `ingotVoid`, `nuggetIron` resolve in-game.
- [ ] Manual crafting grid checks cover at least wand basic, wand cap iron, table, phial, thaumometer, thaumium tool/armor and void tool/armor.

**Риски / зависимости:**

Some reference recipes target items/blocks that may not be fully ported or may have renamed/missing meta variants. Missing content should be recorded as blocker for that specific recipe, not silently skipped.

### GAP-4: Smelting recipes и smelting bonus не зарегистрированы

**Статус:** отсутствует  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigRecipes.java:7-14`
- `src/main/java/thaumcraft/api/ThaumcraftApi.java:67-88`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`
- `/home/stfu/.local/share/opencode/tool-output/tool_e27f2d61a001uI7XLw1VyBGdph:484-510`

**Что не совпадает:**

Reference registers furnace outputs for cinnabar ore, amber ore, magical log charcoal, native/pure nuggets and quicksilver, then registers 18 smelting bonus entries with `ThaumcraftApi.addSmeltingBonus`. Current `ThaumcraftApi` still has smelting bonus storage and lookup, but nothing populates it.

**Что нужно доделать:**

Port reference `initializeSmelting()` to Forge 1.12.2 using `GameRegistry.addSmelting`/valid 1.12.2 APIs and populate `ThaumcraftApi` smelting bonus map.

**Как доделать:**
- `src/main/java/thaumcraft/common/config/ConfigRecipes.java`: implement `initializeSmelting()`.
- Verify current names/metas for `ConfigBlocks.blockCustomOre`, `ConfigBlocks.blockMagicalLog`, `ConfigItems.itemNugget`, `ConfigItems.itemResource`, `ConfigItems.itemShard`, edible nuggets.
- Register ore-name bonuses for `oreGold`, `oreIron`, `oreCinnabar`, `oreCopper`, `oreTin`, `oreSilver`, `oreLead` and item-stack bonuses for native nuggets/meats/fish.
- Add manual checks for furnace outputs and infernal/arcane furnace bonus consumption if those systems use `ThaumcraftApi.getSmeltingBonus`.

**Критерии приемки:**
- [ ] Furnace smelting outputs match reference for custom ores, magical logs, native nuggets, balanced shard and quicksilver source item.
- [ ] `ThaumcraftApi.getSmeltingBonus` returns expected bonuses for ore dictionary and item-stack inputs.
- [ ] Manual furnace/infernal-furnace scenario confirms outputs and bonus drops.

**Риски / зависимости:**

If current item metadata does not match reference item metas, smelting can compile but produce wrong outputs. This must be checked against current `ItemResource`, `ItemNugget`, `ItemShard` subtype definitions.

### GAP-5: Ore dictionary compatibility flags and registrations are stubbed and ordered incorrectly

**Статус:** отсутствует  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/Config.java:165-173`
- `src/main/java/thaumcraft/common/config/Config.java:356-358`
- `src/main/java/thaumcraft/common/Thaumcraft.java:186-192`
- `src/main/java/thaumcraft/common/Thaumcraft.java:325-330`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/Config.class`
- `thaumcraft_src/thaumcraft/common/Thaumcraft.class`
- Reference lifecycle command output: `postInit` calls `Config.initModCompatibility()` before `ConfigRecipes.init()` and `ConfigAspects.init()`.
- Reference compat method: `cfr --silent true thaumcraft_src/thaumcraft/common/config/Config.class | sed -n '604,742p'`.

**Что не совпадает:**

Current ore flags default to false and are never set. Current `initModCompatibility()` is a Phase 4 stub. Current lifecycle calls `ConfigAspects.init()` before `Config.initModCompatibility()`, so even if compat scan were implemented in the current location, aspect registration guarded by `foundCopperIngot`, `foundTinIngot`, `foundSilverIngot`, `foundLeadIngot`, `foundCopperOre`, `foundTinOre`, `foundSilverOre`, `foundLeadOre` would still miss ore tags during the first pass. Wand component registration also checks `foundCopperIngot` and `foundSilverIngot` in `Thaumcraft.initWandComponents()` before postInit (`src/main/java/thaumcraft/common/Thaumcraft.java:317-330`), so copper/silver caps are currently never registered through ore dictionary discovery.

**Что нужно доделать:**

Implement Forge 1.12.2 ore dictionary compatibility scan and place it in a lifecycle slot where flags are available before recipes/aspects and any ore-dependent content registration that still depends on those flags.

**Как доделать:**
- `src/main/java/thaumcraft/common/config/Config.java`: implement `initModCompatibility()` ore dictionary scan adapted from reference.
- `src/main/java/thaumcraft/common/Thaumcraft.java`: reorder calls or split ore flag discovery into an earlier method called before `initWandComponents()`, `ConfigRecipes.init()` and `ConfigAspects.init()`.
- `src/main/java/thaumcraft/common/config/ConfigRecipes.java`: add compat nugget recipes/smelting created by reference compat scan.
- `src/main/java/thaumcraft/common/config/ConfigAspects.java`: ensure ore-dependent tags are registered after flags are set.

**Критерии приемки:**
- [ ] `foundCopper*`, `foundTin*`, `foundSilver*`, `foundLead*` become true when matching ore dictionary entries exist.
- [ ] Copper/silver wand caps and metal compat recipes appear when corresponding ingots exist.
- [ ] Compat nuggets compress/decompress and smelt to the first matching external ingot as in reference.
- [ ] Ore dictionary aspect tags for ingots, ores, dusts and nuggets are present after postInit.

**Риски / зависимости:**

Lifecycle is sensitive: Forge 1.12.2 item/block registry must be complete before scanning ore dictionary, but recipe registration must not happen too late. Splitting flag discovery from recipe registration may be required.

### GAP-6: Object/aspect tag coverage is far below reference and misses mod progression items

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigAspects.java:12-190`
- `src/main/java/thaumcraft/api/ThaumcraftApi.java:219-272`
- `src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:45-65`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigAspects.class`
- `/home/stfu/.local/share/opencode/tool-output/tool_e27f30df9001wfBWATRSNLbBpT:30-37`
- `/home/stfu/.local/share/opencode/tool-output/tool_e27f30df9001wfBWATRSNLbBpT:81-461`

**Что не совпадает:**

Current `ConfigAspects` only registers a small subset of vanilla blocks/items and generic ore tags. Reference registers entity tags, dye ore tags, many vanilla/meta/complex object tags, potion variants, ore dictionary compat tags, and a large set of Thaumcraft blocks/items such as custom ores, taint blocks, magical logs/leaves, resource metas, shards, essentia containers, baubles, eldritch objects and device blocks. Stage 9-a object tags are needed for scanning and generated recipe aspect derivation; missing tags can make progression content compile but be unscannable or have wrong essentia costs.

**Что нужно доделать:**

Port the Stage 9-a object tag set for vanilla, ore dictionary and current Thaumcraft content, preserving reference aspect lists where current items/blocks exist.

**Как доделать:**
- `src/main/java/thaumcraft/common/config/ConfigAspects.java`: expand `init()` into reference-like `registerItemAspects()` and, if kept in Stage 9-a, entity tags needed by scanning foundation.
- Add `dyes` array and register dye ore names; current reference recipe code uses dye names for candle/label recipes.
- Register mod item/block tags for current `ConfigBlocks`/`ConfigItems` fields and skip only content that is genuinely absent, with an explicit audit list.
- Validate `ThaumcraftCraftingManager.generateTagsFromCraftingRecipes()` still works with Forge 1.12.2 `ForgeRegistries.RECIPES` (`src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:317-333`).

**Критерии приемки:**
- [ ] Core vanilla/meta/ore dictionary object tags from reference are present or explicitly mapped to 1.12.2 equivalents.
- [ ] Current Thaumcraft items/blocks that participate in Stage 9-a recipes have explicit object tags.
- [ ] Scan/object aspect lookup returns expected aspects for shards, thaumium/void resources, custom ores/plants, table, thaumometer, essentia containers and key progression ingredients.
- [ ] No Stage 9-a TODO/stub remains in `ConfigAspects`.

**Риски / зависимости:**

Some reference fields point to content not yet ported or renamed in current code. Those need per-item audit decisions; do not silently drop tags for existing content.

### GAP-7: Custom `IRecipe` infrastructure is incomplete and current custom recipes are not registry-safe

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/items/armor/RecipesRobeArmorDyes.java:13-110`
- `src/main/java/thaumcraft/common/items/armor/RecipesVoidRobeArmorDyes.java:5-10`
- `src/main/java/thaumcraft/common/Thaumcraft.java:215-281`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`
- `thaumcraft_src/thaumcraft/common/items/armor/RecipesRobeArmorDyes.class`
- `thaumcraft_src/thaumcraft/common/items/armor/RecipesVoidRobeArmorDyes.class`
- `/home/stfu/.local/share/opencode/tool-output/tool_e27f2d61a001uI7XLw1VyBGdph:76-78`
- `/home/stfu/.local/share/opencode/tool-output/tool_e27f2d61a001uI7XLw1VyBGdph:477-481`

**Что не совпадает:**

Reference registers custom recipe sorter entries and adds robe dye recipes through old `GameRegistry.addRecipe`. Current custom recipe classes exist and implement Forge 1.12.2 `IRecipe`, but no recipe registry event registers them, and instances do not have registry names assigned before registration. Reference also uses `ShapelessNBTOreRecipe`, but no current equivalent exists under `src/main/java/thaumcraft/common/lib/crafting/`.

**Что нужно доделать:**

Add Forge 1.12.2 custom recipe registration for robe dyes and NBT/ore recipes required by Stage 9-a normal recipes.

**Как доделать:**
- `src/main/java/thaumcraft/common/Thaumcraft.java`: add `@SubscribeEvent` for `RegistryEvent.Register<IRecipe>` or delegate to `ConfigRecipes.registerRecipes`.
- `src/main/java/thaumcraft/common/items/armor/RecipesRobeArmorDyes.java`: ensure registered instances have unique `ResourceLocation` names, for example `thaumcraft:robe_armor_dyes`.
- `src/main/java/thaumcraft/common/items/armor/RecipesVoidRobeArmorDyes.java`: ensure registered instance has `thaumcraft:void_robe_armor_dyes`.
- Add or port a 1.12.2-safe `ShapelessNBTOreRecipe` only if Stage 9-a recipes cannot be represented by JSON/custom existing classes.

**Критерии приемки:**
- [ ] Custom recipes are registered during Forge recipe registry event with non-null unique registry names.
- [ ] Robe and void robe dye recipes work manually in crafting grid.
- [ ] NBT-sensitive recipes such as jar-label clearing/copying are not flattened into invalid JSON.
- [ ] No duplicate custom recipe IDs appear during load.

**Риски / зависимости:**

Forge 1.12.2 rejects registry entries without registry names. A compile-only check will not prove these recipes load correctly.

### GAP-8: Recipe IDs, naming convention and duplicate/missing audit foundation отсутствуют

**Статус:** отсутствует  
**Критичность:** high

**Текущая реализация:**
- `src/main/resources/assets/thaumcraft/recipes/` absent
- `src/main/java/thaumcraft/common/config/ConfigRecipes.java:5-14`
- `src/main/java/thaumcraft/common/Thaumcraft.java:215-281`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`
- `/home/stfu/.local/share/opencode/tool-output/tool_e27f2d61a001uI7XLw1VyBGdph:413-481`

**Что не совпадает:**

Reference has recipe keys in `ConfigResearch.recipes` such as `WandBasic`, `WandCapIron`, `Table`, `Thaumometer`, `ThaumiumHelm`, `VoidSword`, `Clusters6`, `JarLabel*`. Forge 1.12.2 requires stable registry names for recipes, but current port has no mapping from reference keys to recipe ids, no JSON file names, and no duplicate/missing recipe audit.

**Что нужно доделать:**

Define a recipe id convention and maintain an audit mapping for Stage 9-a reference recipes to current 1.12.2 ids and registration method.

**Как доделать:**
- Use lowercase `thaumcraft:<descriptive_id>` ids for Forge registry names and JSON file names.
- Keep reference research keys unchanged when values are inserted into `ConfigResearch.recipes`.
- Add a Stage 9-a audit table in code comments or a dedicated docs update after implementation listing reference key, current recipe id, registration path, and validation status.
- Add a lightweight validation scenario that lists `ForgeRegistries.RECIPES` entries under `thaumcraft` and detects duplicate/missing ids.

**Критерии приемки:**
- [ ] Every Stage 9-a recipe has a stable `thaumcraft` id.
- [ ] Reference research keys that unlock ordinary recipes map to valid current recipe entries.
- [ ] Duplicate recipe ids fail fast during validation or are clearly reported.
- [ ] Missing reference Stage 9-a recipes are documented with reason and owner gap.

**Риски / зависимости:**

Changing recipe ids later can break research page references and manual validation notes. Establish the convention before adding many JSON files.

### GAP-9: Current lifecycle can make content compile while unusable at runtime

**Статус:** реализовано неправильно  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/Thaumcraft.java:164-193`
- `src/main/java/thaumcraft/common/Thaumcraft.java:215-281`

**Референс:**
- `thaumcraft_src/thaumcraft/common/Thaumcraft.class`
- Reference lifecycle command output: `postInit` calls `Config.initModCompatibility()`, `ConfigItems.postInit()`, `ConfigRecipes.init()`, `ConfigAspects.init()`, `ConfigResearch.init()`.

**Что не совпадает:**

Current postInit order calls recipes before aspects and calls ore compatibility after research. In Forge 1.12.2, JSON recipes are loaded by the recipe registry, and custom `IRecipe` entries must be registered during the recipe registry event; postInit registration cannot be the only path. The current order also means object tags generated from recipes may run before the relevant recipes/aspects/ore flags are complete.

**Что нужно доделать:**

Separate data preparation from registry event registration and order ore flags, recipes, aspects and research so consumers see complete data.

**Как доделать:**
- Add recipe registry event for Java recipes.
- Keep postInit for data that legitimately depends on all registries and ore dictionary data.
- Ensure `ConfigAspects.init()` runs after any recipe/ore data it reads and before manual scanning validation.
- Ensure `ConfigResearch.init()` can reference recipe handles already registered or stored.

**Критерии приемки:**
- [ ] Mod load reaches server ready state with no recipe registry errors.
- [ ] Stage 9-a recipes are present in `ForgeRegistries.RECIPES` after load.
- [ ] Aspect/object tag lookup is populated after postInit for items produced by Stage 9-a recipes.
- [ ] Research recipe handle lookups do not see null/missing recipe objects for Stage 9-a normal recipes.

**Риски / зависимости:**

Dependency: Stage 9-b/c/d/e research and Thaumonomicon pages depend on this ordering, but the lifecycle bug directly blocks Stage 9-a ordinary recipes and object tags.

## 6. Итоговый checklist закрытия Stage 9-a

- [ ] Replace `ConfigRecipes` stubs with real Forge 1.12.2 Stage 9-a registration foundation.
- [ ] Add recipe registry event or delegated Java recipe registration for custom/NBT/ore recipes.
- [ ] Add `src/main/resources/assets/thaumcraft/recipes/*.json` for simple ordinary recipes.
- [ ] Port all Stage 9-a normal crafting recipes from reference or document exact missing current content blockers.
- [ ] Port smelting recipes and `ThaumcraftApi.addSmeltingBonus` registrations.
- [ ] Implement ore dictionary compatibility scan and correct lifecycle ordering for `found*` flags.
- [ ] Expand `ConfigAspects` object/ore/mod tags to reference parity for Stage 9-a content.
- [ ] Register robe dye and void robe dye custom recipes with stable registry names.
- [ ] Define recipe id convention and produce duplicate/missing recipe audit.
- [ ] Run `./scripts/dev.sh compileJava` after implementation.
- [ ] Run `./scripts/dev.sh smoke-server` because recipes/resources/registries/lifecycle are runtime-affecting.
- [ ] Manually verify crafting, smelting, ore dictionary and scanning/object aspect scenarios listed in gap acceptance criteria.

## 7. Definition of Done

Stage 9-a считается ПОЛНОСТЬЮ завершенной только если:
- все blocker gaps закрыты;
- все high gaps закрыты;
- все элементы из scope Stage 9-a реализованы;
- текущая реализация соответствует референсу по поведению;
- все нужные файлы, ресурсы и регистрации присутствуют;
- отсутствуют TODO и заглушки внутри scope Stage 9-a;
- проект собирается без ошибок;
- базовые игровые сценарии Stage 9-a проверены вручную или тестами;
- ./docs/Stage9-a.md обновлен и не содержит критичных открытых вопросов.

## 8. Открытые вопросы

- Нужно решить во время реализации, какие reference normal recipes переводятся в JSON, а какие остаются Java `IRecipe` из-за NBT/ore/dynamic behavior; это не блокирует анализ, но должно быть зафиксировано в recipe audit before implementation is considered done.
- Нужно проверить current subtype/meta definitions for `ItemResource`, `ItemNugget`, `ItemShard`, `BlockCustomOre`, `BlockCustomPlant`, `BlockCosmeticSolid/Opaque`, because smelting and recipe parity depend on exact metadata mapping.
- Нужно определить, есть ли current content gaps for reference outputs that are absent from this port; missing target items/blocks must be marked as blockers for those specific recipes rather than omitted.
