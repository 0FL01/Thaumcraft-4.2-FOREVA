# Stage 9-e — Gap-анализ и план закрытия

## 1. Цель фазы

Stage 9-e закрывает research content и Thaumonomicon progression для порта Thaumcraft 4.2.3.5 на Forge 1.12.2: категории исследований, research entries, страницы Thaumonomicon, ссылки страниц на рецепты/аспекты/сущности/блоки/предметы, и контентные условия открытия исследований.

Фаза не включает глубокую реализацию рецептов и клиентского GUI, но должна проверить, что research keys, page references, category icons/backgrounds, lang keys и unlock-flow совместимы с оригинальным контентом. По PRD Stage 9 должен обеспечить progression parity, а риск прямо связан с совпадением registry names, research keys, recipe ids и GUI references (`docs/PRD.md:395`, `docs/PRD.md:399`, `docs/PRD.md:403`, `docs/PRD.md:408`, `docs/PRD.md:409`, `docs/PRD.md:410`, `docs/PRD.md:411`, `docs/PRD.md:415`, `docs/PRD.md:416`).

## 2. Scope фазы

- Research categories: `BASICS`, `THAUMATURGY`, `ALCHEMY`, `ARTIFICE`, `GOLEMANCY`, `ELDRITCH`.
- Research items/entries: ключи, категории, координаты, complexity, aspect tags, parents, hidden parents, siblings, флаги `autoUnlock`, `stub`, `round`, `secondary`, `concealed`, `hidden`, `lost`, `special`, triggers.
- Research pages: text pages, concealed text pages, image/aspect pages, vanilla crafting pages, arcane crafting pages, crucible pages, infusion pages, infusion-enchantment pages, compound/list pages, smelting pages.
- Thaumonomicon content references: category icons/backgrounds, page text keys, image paths, recipe page objects, item/entity/aspect trigger IDs.
- Lang keys: category names, research names, research subtitles, research page text, research-note status text.
- Recipe/research unlock flow as references only: recipe keys and page references must align with arcane/infusion/crucible recipe registration, without deep-analyzing recipe implementation.
- Research note/content flow required by original behavior: note creation, hex-grid note data, completion, discovery use, hidden research from knowledge fragments.
- Scanning/discovery prerequisites only as research-content gates: hidden/lost research triggers from item/entity/aspect scans.
- Thaumonomicon GUI references only as content data/IDs; client GUI rendering itself is a dependency outside Stage 9-e.

## 3. Источники сравнения

- `docs/PRD.md:395`-`docs/PRD.md:416` — Stage 9 scope and risks.
- `src/main/java/thaumcraft/common/Thaumcraft.java:186`-`src/main/java/thaumcraft/common/Thaumcraft.java:191` — current lifecycle calls `ConfigRecipes.init()`, `ConfigAspects.init()`, `ConfigResearch.init()` in post-init.
- `src/main/java/thaumcraft/common/config/ConfigResearch.java:1`-`src/main/java/thaumcraft/common/config/ConfigResearch.java:13` — current research registration placeholder with a baseline `recipes` map scaffold.
- `.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:50`-`.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:65` — original `ConfigResearch.init()` flow.
- `.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:67`-`.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:73` — original category registration.
- `.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:76`-`.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:417` — original research entries across all six categories (port splits these per category into 6 slice files; see note above).
- `src/main/java/thaumcraft/api/research/ResearchCategories.java:12`-`src/main/java/thaumcraft/api/research/ResearchCategories.java:67` and `.stage9e-ref/thaumcraft/api/research/ResearchCategories.java:21`-`.stage9e-ref/thaumcraft/api/research/ResearchCategories.java:76` — category container API, structurally ported.
- `src/main/java/thaumcraft/api/research/ResearchItem.java:13`-`src/main/java/thaumcraft/api/research/ResearchItem.java:256` and `.stage9e-ref/thaumcraft/api/research/ResearchItem.java:19`-`.stage9e-ref/thaumcraft/api/research/ResearchItem.java:256` — research item API, structurally ported with one current extra callback field.
- `src/main/java/thaumcraft/api/research/ResearchPage.java:15`-`src/main/java/thaumcraft/api/research/ResearchPage.java:128` — current page model and page types.
- `.stage9e-ref/thaumcraft/common/lib/research/ResearchManager.java:84`-`.stage9e-ref/thaumcraft/common/lib/research/ResearchManager.java:160` — original clue, note creation entry points, hidden research selection.
- `.stage9e-ref/thaumcraft/common/lib/research/ResearchManager.java:268`-`.stage9e-ref/thaumcraft/common/lib/research/ResearchManager.java:387` — original research note NBT/hex-grid serialization.
- `.stage9e-ref/thaumcraft/common/lib/research/ResearchManager.java:428`-`.stage9e-ref/thaumcraft/common/lib/research/ResearchManager.java:455` — original parent/hidden-parent requisite checks.
- `.stage9e-ref/thaumcraft/common/lib/research/ResearchNoteData.java:10`-`.stage9e-ref/thaumcraft/common/lib/research/ResearchNoteData.java:21` — original note data model.
- `.stage9e-ref/thaumcraft/common/items/ItemResearchNotes.java:87`-`.stage9e-ref/thaumcraft/common/items/ItemResearchNotes.java:119` — original note/discovery use behavior.
- `.stage9e-ref/thaumcraft/common/lib/research/ScanManager.java:351`-`.stage9e-ref/thaumcraft/common/lib/research/ScanManager.java:418` — original scan completion clue unlock hook.
- `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:865`-`thaumcraft_src/assets/thaumcraft/lang/en_US.lang:870` — original category lang keys.
- `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:888`-`thaumcraft_src/assets/thaumcraft/lang/en_US.lang:1003` and following research section through the same file — original research name/text/page keys.
- `src/main/resources/assets/thaumcraft/lang/en_us.lang:1`-`src/main/resources/assets/thaumcraft/lang/en_us.lang:118` — current lang file with item/static GUI keys only.
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_researchback.png`, `thaumcraft_src/assets/thaumcraft/textures/gui/gui_researchbackeldritch.png`, `thaumcraft_src/assets/thaumcraft/textures/gui/gui_researchbook.png`, `thaumcraft_src/assets/thaumcraft/textures/gui/gui_researchbook_overlay.png` — original Thaumonomicon GUI/content backgrounds.
- `thaumcraft_src/assets/thaumcraft/textures/misc/r_thaumaturgy.png`, `thaumcraft_src/assets/thaumcraft/textures/misc/r_crucible.png`, `thaumcraft_src/assets/thaumcraft/textures/misc/r_artifice.png`, `thaumcraft_src/assets/thaumcraft/textures/misc/r_golemancy.png`, `thaumcraft_src/assets/thaumcraft/textures/misc/r_eldritch.png` — original category icons.
- `src/main/resources/assets/thaumcraft/textures/items/researchnotes.png`, `src/main/resources/assets/thaumcraft/textures/items/researchnotesoverlay.png`, `src/main/resources/assets/thaumcraft/textures/items/thaumonomicon.png`, `src/main/resources/assets/thaumcraft/textures/items/thaumonomiconcheat.png` — current research-adjacent item textures present.

Lightweight analysis commands run:

- `git status --short` — showed only pre-existing untracked Stage 8/9 docs before this document was created.
- `jar tf Thaumcraft-1.7.10-4.2.3.5.jar | rg 'Research|ConfigResearch|Thaumonomicon|research|lang/en_US|textures/gui/gui_research|researchback'` — confirmed reference class/resource locations.
- `cfr --silent true --outputdir .stage9e-ref Thaumcraft-1.7.10-4.2.3.5.jar ...` — temporary decompile for reference analysis.
- `rg -o 'new ResearchItem\("[^"]+' .stage9e-ref/thaumcraft/common/config/ConfigResearch.java | sed 's/.*("//' | sort > .stage9e-ref/ref_research_keys.txt && wc -l .stage9e-ref/ref_research_keys.txt` — counted 201 original research entries.
- `rg -o 'recipes\.get\("[^"]+' .stage9e-ref/thaumcraft/common/config/ConfigResearch.java | sed 's/.*("//' | sort -u | wc -l` — counted 276 unique original recipe references used by research pages.
- `rg -o 'tc\.research_page\.[A-Za-z0-9_]+\.[0-9]+' .stage9e-ref/thaumcraft/common/config/ConfigResearch.java | sort -u | wc -l` — counted 302 explicit original research page text references from `ConfigResearch`.
- `rg -c '^tc\.research_' thaumcraft_src/assets/thaumcraft/lang/en_US.lang` — counted 713 original research localization keys.

> **Note — ConfigResearch file split:** `ConfigResearch.java` content has been refactored per category into 7 files (kernel + 6 category slices), all in `thaumcraft.common.config`:
> - `ConfigResearch.java` — kernel: recipe map, helpers, `initCategories()`, `init()` orchestrator.
> - `ConfigResearchBasics.java` — `initBasicResearchBaseline()`, `initBasicResearchProgressionBaseline()`, `initBasicResearchTextOnlyExtended()`.
> - `ConfigResearchAlchemy.java` — `initAlchemyResearchBaseline()`, `initAlchemyResearchTextOnlyBaseline()`.
> - `ConfigResearchArtifice.java` — `initArtificeResearchBaseline()`, `initArtificeResearchTextOnlyBaseline()`.
> - `ConfigResearchThaumaturgy.java` — `initThaumaturgyResearchBaseline()`, `initThaumaturgyResearchTextOnlyBaseline()`.
> - `ConfigResearchGolemancy.java` — `initGolemancyResearchBaseline()`, `initGolemancyResearchTextOnlyBaseline()`.
> - `ConfigResearchEldritch.java` — `initEldritchResearchBaseline()`, `initEldritchResearchTextOnlyBaseline()`.
> Behavior, registry names, research keys, recipe keys, and `init()` call order are unchanged. References below that point to `ConfigResearch.java` category blocks now refer to the corresponding slice file.

## 4. Текущее состояние Stage 9-e

Current implementation is not Stage 9-e complete.

The port does call `ConfigResearch.init()` during post-init after recipe and aspect initialization (`src/main/java/thaumcraft/common/Thaumcraft.java:186`-`src/main/java/thaumcraft/common/Thaumcraft.java:191`). Current `ConfigResearch.init()` now registers the six reference research categories plus a broader safe baseline across BASICS/ALCHEMY/ARTIFICE/GOLEMANCY/THAUMATURGY/ELDRITCH (`ASPECTS`, `PECH`, `NODES`, `WARP`, `RESEARCH`, `KNOWFRAG`, `THAUMONOMICON`, `ORE`, `PLANTS`, `RESEARCHER1`, `DECONSTRUCTOR`, `RESEARCHER2`, `RESEARCHDUPE`, `PHIAL`, `CRUCIBLE`, `NITOR`, `ALUMENTUM`, `DISTILESSENTIA`, `THAUMIUM`, alchemy progression branch `TALLOW`/`ALCHEMICALDUPLICATION`/`ALCHEMICALMANUFACTURE`/`ENTROPICPROCESSING`/`LIQUIDDEATH`/`BOTTLETAINT`/`PUREIRON`/`PUREGOLD`/conditional `PURECOPPER`/`PURETIN`/`PURESILVER`/`PURELEAD`/`TRANSIRON`/`TRANSGOLD`/conditional `TRANSCOPPER`/`TRANSTIN`/`TRANSSILVER`/`TRANSLEAD`/`ETHEREALBLOOM`/`BATHSALTS`/`SANESOAP`/`JARLABEL`/`ARCANESPA`/`JARVOID` (with recipe-backed pages for `ARCANESPA`/`JARVOID` and the `WardedJar` page in `JARLABEL`), essentia transport chain `TUBES`/`TUBEFILTER`/`ESSENTIACRYSTAL`/`CENTRIFUGE`/`THAUMATORIUM` (now with list-backed recipe-layout key populated in `ConfigRecipes`), `BASICARTIFACE` (with recipe-backed `PrimalCharm`/`MirrorGlass` pages), `ARCANESTONE`, `GRATE`, `TABLE`, `ARCTABLE`, `RESTABLE`, `THAUMOMETER`, `PAVETRAVEL`, `PAVEWARD`, `GOGGLES`, `ARCANEEAR`, `SINSTONE`, `LEVITATOR`, `INFERNALFURNACE` (list-backed recipe-layout key populated), `BELLOWS`, `ARCANEBORE`, `ARCANELAMP`, `LAMPGROWTH`, `LAMPFERTILITY`, `FLUXSCRUB` (recipe-backed `FluxScrubber` page), conditional mirror branch `MIRROR`/`MIRRORHAND`/`MIRRORESSENTIA`, `JARBRAIN`, `INFUSIONENCHANTMENT`, `ARMORFORTRESS`, `HELMGOGGLES`, fortress mask branch `MASKGRINNINGDEVIL`/`MASKANGRYGHOST`/`MASKSIPPINGFIEND`, `BOOTSTRAVELLER`, `HOVERHARNESS`, `HOVERGIRDLE`, `ENCHFABRIC`, `RUNICARMOR`, `RUNICCHARGED`, `RUNICHEALING`, `RUNICKINETIC`, `RUNICEMERGENCY`, `RUNICAUGMENTATION`, `BANNERS`, `ELEMENTALAXE`, `ELEMENTALPICK`, `ELEMENTALSWORD`, `ELEMENTALSHOVEL`, `ELEMENTALHOE`, conditional `WARDEDARCANA`, `BONEBOW`, `PRIMALARROW`, `INFUSION`, `HUNGRYCHEST`, tiny golem decoration branch `TINYHAT`/`TINYGLASSES`/`TINYBOWTIE`/`TINYFEZ`/`TINYDART`/`TINYVISOR`/`TINYARMOR`/`TINYHAMMER`, golem progression branch with recipe-backed entries `GOLEMBELL`/`GOLEMFETTER`/`UPGRADEAIR`/`UPGRADEEARTH`/`UPGRADEFIRE`/`UPGRADEWATER`/`UPGRADEORDER`/`UPGRADEENTROPY`/`TRAVELTRUNK`/`COREGATHER`/`CORESORTING`/`COREUSE`/`COREFISHING`/`CORELUMBER`/`COREALCHEMY`/`ADVANCEDGOLEM` plus text-only `GOLEMSTRAW`/`GOLEMWOOD`/`GOLEMCLAY`/`GOLEMSTONE`/`GOLEMIRON`/`GOLEMTHAUMIUM`/`GOLEMFLESH`/`GOLEMTALLOW`/`COREFILL`/`COREEMPTY`/`COREHARVEST`/`COREGUARD`/`COREBUTCHER`/`CORELIQUID`, `ENCHANT`, `NODETAPPER1`, `NODEPRESERVE`, `NODETAPPER2`, `NODEJAR`, `CRIMSON`, `BASICTHAUMATURGY`, `FOCUSFIRE`, `FOCUSFROST`, `FOCUSHELLBAT`, conditional `FOCUSWARDING`, `FOCUSEXCAVATION`, `FOCUSSHOCK`, `FOCUSTRADE`, `FOCUSPORTABLEHOLE`, `FOCUSPOUCH`, `CAP_gold`, optional `CAP_copper`, `CAP_thaumium`, conditional `CAP_silver`, `ROD_greatwood`, `ROD_reed`, `ROD_blaze`, `ROD_obsidian`, `ROD_ice`, `ROD_quartz`, `ROD_bone`, `ROD_silverwood`, `SCEPTRE`, `ROD_greatwood_staff`, `ROD_reed_staff`, `ROD_blaze_staff`, `ROD_obsidian_staff`, `ROD_ice_staff`, `ROD_quartz_staff`, `ROD_bone_staff`, `ROD_silverwood_staff`, `NODESTABILIZER`, `NODESTABILIZERADV`, `VISPOWER`, `FOCALMANIPULATION`, `VAMPBAT`, `WANDPED`, `VISAMULET`, `WANDPEDFOC`, `VISCHARGERELAY`, `CAP_iron`, `ROD_wood`, `ELDRITCHMINOR`, `OCULUS`, `ENTEROUTER`, `OUTERREV`, `PRIMPEARL`, `PRIMNODE`, `ADVALCHEMYFURNACE` (list-backed recipe-layout key populated), `PRIMALCRUSHER`, `VOIDMETAL`, `ESSENTIARESERVOIR`, `CAP_void`, `ARMORVOIDFORTRESS`, `FOCUSPRIMAL`, `SANITYCHECK`, `ROD_primal_staff`, `ELDRITCHMAJOR`). The full graph, most recipe-backed pages, and full trigger parity are still incomplete (`src/main/java/thaumcraft/common/config/ConfigResearch.java`).

Incremental note: `ConfigRecipes` now also supplies `Clusters0..6` plus crucible keys `GolemStraw/Wood/Tallow/Clay/Flesh/Stone/Iron/Thaumium` and `CoreFill/Empty/Harvest/Guard/Butcher/Liquid`, and current `ConfigResearch` uses those keys for recipe-backed GOLEMANCY page slots where available. The same checkpoint also wires `MundaneAmulet`/`MundaneRing`/`MundaneBelt`, `BlockFlesh`, and `BlockTallow` recipe keys so `BASICARTIFACE`, `GOLEMFLESH`, and `GOLEMTALLOW` can include their reference recipe-backed page slots.

The low-level API containers for categories, items, and pages are mostly present and structurally close to the 1.7.10 reference (`src/main/java/thaumcraft/api/research/ResearchCategories.java:12`-`src/main/java/thaumcraft/api/research/ResearchCategories.java:67`, `src/main/java/thaumcraft/api/research/ResearchItem.java:13`-`src/main/java/thaumcraft/api/research/ResearchItem.java:256`, `src/main/java/thaumcraft/api/research/ResearchPage.java:15`-`src/main/java/thaumcraft/api/research/ResearchPage.java:128`). This is an API baseline, not content parity.

The current lang file now includes the reference `tc.research_category.*` set and full `tc.research_name.*`, `tc.research_text.*`, and `tc.research_page.*` corpus imported from `thaumcraft_src` for Stage 9-e baseline coverage. Runtime GUI/manual verification of page rendering remains pending because user-interactive client checks are excluded.

The current resources now include Thaumonomicon/research GUI backgrounds and baseline research misc icons (`r_*.png`) with `research1..5` images under `src/main/resources/assets/thaumcraft/textures/misc/`. Additional image paths referenced by full research-page text still need validation once the full research text corpus is ported.

The current research note flow is incomplete. `ItemResearchNotes` directly grants the `key` NBT value on right click and consumes the stack (`src/main/java/thaumcraft/common/items/ItemResearchNotes.java:43`-`src/main/java/thaumcraft/common/items/ItemResearchNotes.java:54`), while the reference distinguishes incomplete notes, completed discoveries, knowledge fragments, prerequisite errors, sounds, tooltips, rarity, colors, and hidden-research conversion (`.stage9e-ref/thaumcraft/common/items/ItemResearchNotes.java:87`-`.stage9e-ref/thaumcraft/common/items/ItemResearchNotes.java:174`). The reference `ResearchNoteData` class has no current port file.

The current scan flow records scans and awards aspects, but does not call the original clue creation logic after a successful scan (`src/main/java/thaumcraft/common/lib/research/ScanManager.java:175`-`src/main/java/thaumcraft/common/lib/research/ScanManager.java:220`). The reference calls `ResearchManager.createClue(...)` after successful scan aspect awards (`.stage9e-ref/thaumcraft/common/lib/research/ScanManager.java:398`-`.stage9e-ref/thaumcraft/common/lib/research/ScanManager.java:414`).

The current direct completion packet grants research without checking prerequisites or research cost, except for null research lookup and duplicate completion (`src/main/java/thaumcraft/common/lib/network/playerdata/PacketPlayerCompleteToServer.java:45`-`src/main/java/thaumcraft/common/lib/network/playerdata/PacketPlayerCompleteToServer.java:69`). Original primary research normally creates a note first and completed discoveries check prerequisites before granting (`.stage9e-ref/thaumcraft/common/lib/research/ResearchManager.java:120`-`.stage9e-ref/thaumcraft/common/lib/research/ResearchManager.java:134`, `.stage9e-ref/thaumcraft/common/items/ItemResearchNotes.java:87`-`.stage9e-ref/thaumcraft/common/items/ItemResearchNotes.java:104`).

## 5. Gap list

### GAP-1: Research categories and research entries are not registered

**Статус:** partial  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigResearch.java:3`-`src/main/java/thaumcraft/common/config/ConfigResearch.java:7`
- `src/main/java/thaumcraft/common/Thaumcraft.java:186`-`src/main/java/thaumcraft/common/Thaumcraft.java:191`

**Референс:**
- `.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:50`-`.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:65`
- `.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:67`-`.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:73`
- `.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:76`-`.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:417`

**Что не совпадает:**

Reference creates a default wood/iron wand icon, registers six categories, then registers 201 research entries across Thaumaturgy, Artifice, Alchemy, Golemancy, Basics, and Eldritch. Current `ConfigResearch.init()` now has categories plus a small BASIC text-only baseline, but the vast majority of entries are still missing.

Reference category IDs and assets:

- `BASICS` icon `textures/items/thaumonomiconcheat.png`, background `textures/gui/gui_researchback.png`.
- `THAUMATURGY` icon `textures/misc/r_thaumaturgy.png`, background `textures/gui/gui_researchback.png`.
- `ALCHEMY` icon `textures/misc/r_crucible.png`, background `textures/gui/gui_researchback.png`.
- `ARTIFICE` icon `textures/misc/r_artifice.png`, background `textures/gui/gui_researchback.png`.
- `GOLEMANCY` icon `textures/misc/r_golemancy.png`, background `textures/gui/gui_researchback.png`.
- `ELDRITCH` icon `textures/misc/r_eldritch.png`, background `textures/gui/gui_researchbackeldritch.png`.

**Что нужно доделать:**

Port `ConfigResearch` content registration from the 1.7.10 reference, adapted to 1.12.2 names/classes while preserving research keys, category IDs, coordinates, parents, flags, triggers, page order, recipe keys, page text keys, icons, and warp metadata.

**Как доделать:**
- Implement `ConfigResearch.wand` and `ConfigResearch.recipes` equivalents if not already provided elsewhere.
- Implement `initCategories()` with the six original category keys and resource paths.
- Implement `initBasicResearch()`, `initThaumaturgyResearch()`, `initAlchemyResearch()`, `initArtificeResearch()`, `initGolemancyResearch()`, `initEldritchResearch()`.
- Preserve all original research keys such as `RESEARCH`, `ASPECTS`, `BASICTHAUMATURGY`, `INFUSION`, `DISTILESSENTIA`, `GOLEMCORE`, `VOIDMETAL`, `ELDRITCHMINOR`, and `OUTERREV`.
- Preserve all original `setParents`, `setParentsHidden`, `setSiblings`, `setAutoUnlock`, `setStub`, `setRound`, `setSecondary`, `setConcealed`, `setHidden`, `setLost`, `setSpecial`, `setItemTriggers`, `setEntityTriggers`, and `setAspectTriggers` usage.
- Preserve original `ThaumcraftApi.addWarpToResearch(...)` and item warp calls associated with research entries.
- Files to change: `src/main/java/thaumcraft/common/config/ConfigResearch.java` (kernel), `ConfigResearchBasics.java`, `ConfigResearchAlchemy.java`, `ConfigResearchArtifice.java`, `ConfigResearchThaumaturgy.java`, `ConfigResearchGolemancy.java`, `ConfigResearchEldritch.java`; likely dependent fixes in `src/main/java/thaumcraft/common/config/ConfigRecipes.java`, `src/main/java/thaumcraft/common/config/ConfigItems.java`, `src/main/java/thaumcraft/common/config/ConfigBlocks.java` only where needed for referenced icons/stacks to compile.

**Критерии приемки:**
- [ ] `ResearchCategories.getResearchList("BASICS")`, `THAUMATURGY`, `ALCHEMY`, `ARTIFICE`, `GOLEMANCY`, and `ELDRITCH` are non-null after post-init.
- [ ] Current `ConfigResearch` family (7 files: kernel + 6 category slices) registers the same 201 original research keys or every intentional omission is documented with a concrete unavailable dependency.
- [ ] Parent/hidden-parent/sibling relationships match the reference for all registered entries.
- [ ] Research entry flags, coordinates, complexity values, aspect tags, icons, and triggers match the reference.
- [ ] Warp metadata for forbidden research matches the reference.

**Риски / зависимости:**

Dependency: Stage 9 recipe registration must provide recipe objects for page references, or research registration will either crash on null recipe pages or create unusable pages. Do not claim Stage 9-e complete until the registered research graph is populated and inspectable in game.

### GAP-2: Research pages cannot resolve recipe references because recipe map/content is absent

**Статус:** partial  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigRecipes.java:5`-`src/main/java/thaumcraft/common/config/ConfigRecipes.java:14`
- `src/main/java/thaumcraft/common/config/ConfigResearch.java:3`-`src/main/java/thaumcraft/common/config/ConfigResearch.java:7`
- `src/main/java/thaumcraft/api/research/ResearchPage.java:35`-`src/main/java/thaumcraft/api/research/ResearchPage.java:93`

**Референс:**
- `.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:51`-`.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:52`
- `.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:77`-`.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:417`

**Что не совпадает:**

Reference `ConfigResearch` uses `recipes.get("...")` throughout research pages. The decompiled reference contains 276 unique recipe keys referenced by research pages. Current `ConfigRecipes.init()` is still partial for Stage 9, but the map is no longer empty: the port now registers baseline handles for `ArcaneStone2/3/4`, BASICS progression keys (`KnowFrag`, `PlankGreatwood`, `PlankSilverwood`, `Grate`, `Phial`, `Table`, `Scribe1/2/3`, `Thaumometer`), THAUMATURGY onboarding keys (`WandCapIron`, `WandBasic`), baseline crucible keys (`Nitor`, `Alumentum`, `Thaumium`, `VoidMetal`, `VoidSeed`), and list-backed layout keys (`Thaumonomicon`, `ArcTable`, `ResTable`, `Crucible`). Most reference recipe keys remain unported. Current `ResearchPage` constructors dereference recipe outputs for single `IRecipe`, `IArcaneRecipe`, `CrucibleRecipe`, and `InfusionRecipe` pages, so missing map keys still become registration-time failures or broken pages.

**Что нужно доделать:**

Provide a complete research-visible recipe lookup contract before or together with porting `ConfigResearch`, and validate every recipe page reference used by Stage 9-e.

**Как доделать:**
- Define how 1.12.2 recipe registration stores objects under original research page keys such as `WandBasic`, `FocusFire`, `InfusionMatrix`, `Thaumium`, `Nitor`, `GolemCore`, and the infusion-enchantment keys.
- Populate `ConfigResearch.recipes` or an equivalent stable lookup before research registration runs.
- Make research registration fail loudly with a useful log for missing recipe references during development, not silently create broken pages.
- Keep page order and page type identical to reference; do not replace arcane/infusion/crucible pages with text-only placeholders.
- Files to change: `src/main/java/thaumcraft/common/config/ConfigRecipes.java`, `src/main/java/thaumcraft/common/config/ConfigResearch.java` (kernel) and the 6 category slice files (`ConfigResearchBasics.java`, `ConfigResearchAlchemy.java`, `ConfigResearchArtifice.java`, `ConfigResearchThaumaturgy.java`, `ConfigResearchGolemancy.java`, `ConfigResearchEldritch.java`), possibly recipe classes under `src/main/java/thaumcraft/api/crafting/**` and `src/main/java/thaumcraft/common/lib/crafting/**` only for key alignment.

**Критерии приемки:**
- [ ] All recipe keys referenced by current `ConfigResearch` resolve to non-null recipe/list objects before pages are created.
- [ ] Thaumonomicon pages for arcane, crucible, infusion, normal crafting, smelting, compound/list, and infusion-enchantment recipes render or expose valid recipe objects.
- [ ] `ThaumcraftApi.getCraftingRecipeKey(...)` can find research/page references for implemented recipe outputs (`src/main/java/thaumcraft/api/ThaumcraftApi.java:151`-`src/main/java/thaumcraft/api/ThaumcraftApi.java:187`).
- [ ] Crucible recipe matching still gates on completed research keys (`src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:468`-`src/main/java/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.java:483`).

**Риски / зависимости:**

Dependency: this overlaps Stage 9 recipe chunks. Stage 9-e should only validate key/page alignment, not deep recipe behavior. However, missing recipe objects block Thaumonomicon page parity and therefore block Stage 9-e completion.

### GAP-3: Research localization is absent from the port

**Статус:** partial  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/resources/assets/thaumcraft/lang/en_us.lang:1`-`src/main/resources/assets/thaumcraft/lang/en_us.lang:118`

**Референс:**
- `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:865`-`thaumcraft_src/assets/thaumcraft/lang/en_US.lang:870`
- `thaumcraft_src/assets/thaumcraft/lang/en_US.lang:888` and following research section through the file

**Что не совпадает:**

Reference English lang contains six research category keys and 713 research localization keys. The port now carries this corpus in `en_us.lang`, but Stage 9-e still requires research graph/page registration and non-GUI runtime checks before localization coverage can be treated as progression-validated.

**Что нужно доделать:**

Copy/adapt the original English research localization into the 1.12.2 lang file using lowercase locale naming while preserving every key string referenced by `ConfigResearch` and research-note flow.

**Как доделать:**
- Add category keys `tc.research_category.BASICS`, `THAUMATURGY`, `ALCHEMY`, `ARTIFICE`, `GOLEMANCY`, `ELDRITCH`.
- Add all `tc.research_name.*`, `tc.research_text.*`, and `tc.research_page.*` keys referenced by the ported `ConfigResearch`.
- Add research-note/discovery status keys used by item flow, including keys visible in the reference such as `tc.researcherror`, `item.discovery.name`, `item.researchnotes.unknown.1`, and `item.researchnotes.unknown.2` if they are not already present.
- Preserve original formatting tokens used by the content (`<BR>`, `<LINE>`, `<IMG>...`, section signs) unless the 1.12.2 renderer requires a documented adaptation.
- Files to change: `src/main/resources/assets/thaumcraft/lang/en_us.lang`; optionally other locale files only if the project later chooses broader localization, not required for Stage 9-e parity.

**Критерии приемки:**
- [ ] Every research category registered by `ConfigResearch` has a matching `tc.research_category.*` key.
- [ ] Every non-virtual research item has matching `tc.research_name.*` and `tc.research_text.*` keys unless the reference also lacks them.
- [ ] Every text page key referenced by `ConfigResearch` resolves to translated text instead of displaying the raw key.
- [ ] Research note/discovery tooltips and errors resolve to localized text.

**Риски / зависимости:**

The original file is `en_US.lang`; the port uses `en_us.lang`. File naming must remain Forge 1.12.2-compatible while preserving key names exactly.

### GAP-4: Thaumonomicon/research content assets are missing from current resources

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- Research GUI and misc icon/page textures are present in port resources, including `textures/gui/gui_research*.png`, `textures/misc/r_*.png`, and `textures/misc/research1..5.png`.
- Missing reference assets used by current content were copied from source assets: `textures/blocks/alchemyblock.png`, `textures/misc/eldritchajor1.png`, `textures/misc/eldritchajor2.png`.
- Static validation now checks that:
  - every `new ResourceLocation("thaumcraft", "...")` used in `ConfigResearch` family (kernel + 6 category slices) resolves to an existing file under `assets/thaumcraft`;
  - every `<IMG>thaumcraft:...` path in `en_us.lang` resolves to an existing file.

**Референс:**
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_researchback.png`
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_researchbackeldritch.png`
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_researchbook.png`
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_researchbook_overlay.png`
- `thaumcraft_src/assets/thaumcraft/textures/gui/gui_research.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/r_thaumaturgy.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/r_crucible.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/r_artifice.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/r_golemancy.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/r_eldritch.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/research1.png` through `research5.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/r_researcher1.png`, `r_researcher2.png`, `r_resdupe.png`, `r_nodes.png`, `r_aspects.png`, and other `r_*.png` icons referenced by research pages.

**Что не совпадает:**

Static asset coverage for currently referenced research resources is now in place, but runtime visual parity in the Thaumonomicon UI is still not validated in this headless flow.

**Что нужно доделать:**

Keep static coverage green as research content grows, and run visual/manual validation later when GUI interaction is in scope.

**Как доделать:**
- Maintain `ConfigResearchStaticGraphTest` resource assertions for both `ResourceLocation` and `<IMG>` links.
- When adding new research pages/lang, copy required assets from `thaumcraft_src/assets/thaumcraft/` and keep names unchanged.
- Defer visual missing-texture confirmation in live GUI to manual/client validation checkpoint.

**Критерии приемки:**
- [ ] All `ResourceLocation` paths used by research categories exist under `src/main/resources/assets/thaumcraft/`.
- [ ] All `ResourceLocation` icon paths used by research entries exist.
- [ ] All `<IMG>thaumcraft:...` paths in research lang text exist.
- [ ] Missing-texture warnings are absent when opening the Thaumonomicon research browser.

**Риски / зависимости:**

Dependency: client GUI rendering is Stage 8, but Stage 9-e still owns content resource paths. Missing assets do not block compilation, so this requires runtime/manual verification.

### GAP-5: Research note, discovery, and hex-grid content flow is missing

**Статус:** частично реализовано  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/research/ResearchNoteData.java` now stores reference-style note state (`key`, `color`, `hexEntries`, `hexes`, `complete`, `copies`).
- `src/main/java/thaumcraft/common/lib/utils/HexUtils.java` is now present with reference-style axial/cubic helpers, ring generation, and randomized distribution.
- `src/main/java/thaumcraft/common/lib/research/ResearchManager.java` now includes `consumeInkFromTable`, reference-style hex-grid note creation (`createNote`), NBT read/write round-trip for note grids, and connectivity completion checks (`checkResearchCompletion`/`checkConnections`).
- `src/main/java/thaumcraft/common/tiles/TileResearchTable.java` now implements server-side note processing baseline: periodic bonus recalculation, `placeAspect(...)` handling, ink consumption, aspect pool updates, and completion promotion to metadata `64`.
- `src/main/java/thaumcraft/common/lib/network/playerdata/PacketAspectPlaceToServer.java` now routes to `TileResearchTable.placeAspect(...)` instead of only marking the tile dirty.
- `src/main/java/thaumcraft/common/lib/network/playerdata/PacketAspectCombinationToServer.java` now mirrors reference-side combination flow more closely: consumes player/bonus aspect pools, synchronizes `PacketAspectPool`, and calls `ScanManager.checkAndSyncAspectKnowledge(...)` for discovered combinations.

**Референс:**
- `.stage9e-ref/thaumcraft/common/lib/research/ResearchNoteData.java:10`-`.stage9e-ref/thaumcraft/common/lib/research/ResearchNoteData.java:21`
- `.stage9e-ref/thaumcraft/common/lib/research/ResearchManager.java:120`-`.stage9e-ref/thaumcraft/common/lib/research/ResearchManager.java:134`
- `.stage9e-ref/thaumcraft/common/lib/research/ResearchManager.java:199`-`.stage9e-ref/thaumcraft/common/lib/research/ResearchManager.java:219`
- `.stage9e-ref/thaumcraft/common/lib/research/ResearchManager.java:221`-`.stage9e-ref/thaumcraft/common/lib/research/ResearchManager.java:266`
- `.stage9e-ref/thaumcraft/common/lib/research/ResearchManager.java:268`-`.stage9e-ref/thaumcraft/common/lib/research/ResearchManager.java:387`
- `.stage9e-ref/thaumcraft/common/items/ItemResearchNotes.java:87`-`.stage9e-ref/thaumcraft/common/items/ItemResearchNotes.java:174`

**Что не совпадает:**

The server-side note data model and core hex-grid flow now exist, but full parity is still incomplete. Primary note creation from full Thaumonomicon interactions is still not runtime-validated, and client-side research-table interaction/UX parity remains part of Stage 8 GUI work. Discovery/right-click behavior is improved but still needs full end-to-end parity evidence against reference progression paths.

**Что нужно доделать:**

Finish the remaining progression path and validate end-to-end behavior with the now-present server-side note/hex baseline.

**Как доделать:**
- Keep `ResearchManager.createResearchNoteForPlayer(...)` and server-side note lifecycle aligned with populated Stage 9-e research content.
- Validate that primary research actions produce notes before completion across available non-GUI harness points and server packet handlers.
- Validate table-side note completion and discovery consumption flow with non-interactive tests/harnesses where possible.
- Preserve note NBT key compatibility (`key`, `color`, `complete`, `copies`, `hexgrid`, `hexq`, `hexr`, `type`, `aspect`) for old/new note stacks in scope.
- Files/classes to continue: `src/main/java/thaumcraft/common/lib/research/ResearchManager.java`, `src/main/java/thaumcraft/common/items/ItemResearchNotes.java`, `src/main/java/thaumcraft/common/tiles/TileResearchTable.java`, Stage 8 GUI-side research table/browser handlers.

**Критерии приемки:**
- [ ] Clicking a primary research in the Thaumonomicon creates a research note instead of immediately completing the research.
- [ ] Created notes contain original-compatible NBT keys: `key`, `color`, `complete`, `copies`, `hexgrid`, `hexq`, `hexr`, `type`, `aspect`.
- [ ] Research table can complete a note by satisfying the original hex connectivity rules.
- [ ] Completed discoveries grant research only if prerequisites pass and then consume the discovery.
- [ ] Knowledge fragments/unknown notes can reveal hidden research or fail according to original behavior.

**Риски / зависимости:**

Dependency: client-side research table GUI/rendering is outside Stage 9-e, but the server/content data flow must exist for progression parity. This gap blocks Stage 9-e because original content depends on primary research notes.

### GAP-6: Hidden/lost research discovery from scans is not wired

**Статус:** частично реализовано  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/research/ScanManager.java:175`-`src/main/java/thaumcraft/common/lib/research/ScanManager.java:220`
- `src/main/java/thaumcraft/common/lib/research/ResearchManager.java:38`-`src/main/java/thaumcraft/common/lib/research/ResearchManager.java:387`
- `src/main/java/thaumcraft/api/research/ResearchItem.java:144`-`src/main/java/thaumcraft/api/research/ResearchItem.java:169`

**Референс:**
- `.stage9e-ref/thaumcraft/common/lib/research/ResearchManager.java:84`-`.stage9e-ref/thaumcraft/common/lib/research/ResearchManager.java:118`
- `.stage9e-ref/thaumcraft/common/lib/research/ResearchManager.java:137`-`.stage9e-ref/thaumcraft/common/lib/research/ResearchManager.java:160`
- `.stage9e-ref/thaumcraft/common/lib/research/ScanManager.java:351`-`.stage9e-ref/thaumcraft/common/lib/research/ScanManager.java:418`
- `.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:80`, `.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:199`, `.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:211`, `.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:213`, `.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:219`-`.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:221`, `.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:235`, `.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:393`-`.stage9e-ref/thaumcraft/common/config/ConfigResearch.java:417`

**Что не совпадает:**

The port now includes `ResearchManager.createClue(World, EntityPlayer, Object, AspectList)` and `ScanManager.completeScan` now forwards scan clue objects plus awarded-aspect data to it after successful server-side scan completion. Clue grants are stored as `@KEY` via normal research completion path. Entity-trigger matching now accepts both legacy (`Thaumcraft.Firebat`) and namespaced (`thaumcraft:firebat`) forms to reduce 1.7.10→1.12 identifier drift. `ConfigAspects` now also registers a minimal 1.7.10-parity entity-aspect baseline for current trigger-bearing entities (`minecraft:enderman`, `thaumcraft:brainyzombie`, `thaumcraft:giantbrainyzombie`, `thaumcraft:firebat`, `thaumcraft:primalorb`), and static tests enforce both:
- every `setEntityTriggers(...)` key in `ConfigResearch` has a matching `registerEntityTag(...)` entry in `ConfigAspects`;
- every `setAspectTriggers(...)` reference in `ConfigResearch` points to a declared `Aspect` API constant.
Remaining mismatch is runtime parity validation with populated content.

**Что нужно доделать:**

Finish parity validation of hidden/lost clue unlocks with populated trigger-bearing research content and confirm entity-trigger key compatibility.

**Как доделать:**
- Add `ResearchManager.createClue(World, EntityPlayer, Object, AspectList)` with original matching semantics for item, entity, and aspect triggers.
- Add/port `ResearchManager.findHiddenResearch(EntityPlayer)` for knowledge-fragment behavior.
- In `ScanManager.completeScan`, preserve the scanned clue object (`ItemStack` or entity ID string) and pass awarded aspects to `ResearchManager.createClue(...)` after successful scan.
- Ensure clue grants use `@`-prefixed research keys and do not mark the full research complete prematurely.
- Keep trigger string normalization compatibility for legacy and namespaced entity IDs (`Thaumcraft.*`, `minecraft:*`, `thaumcraft:*`) and validate with runtime scenarios.
- Files/classes to change: `src/main/java/thaumcraft/common/lib/research/ResearchManager.java`, `src/main/java/thaumcraft/common/lib/research/ScanManager.java`, `src/main/java/thaumcraft/common/config/ConfigResearch.java` (kernel) and its 6 category slice files (`ConfigResearchBasics.java`, `ConfigResearchAlchemy.java`, `ConfigResearchArtifice.java`, `ConfigResearchThaumaturgy.java`, `ConfigResearchGolemancy.java`, `ConfigResearchEldritch.java`).

**Критерии приемки:**
- [ ] Scanning a matching item trigger grants only `@RESEARCHKEY` clue state, not full research completion.
- [ ] Scanning a matching entity trigger grants the same hidden clue keys as the original.
- [ ] Aspect-triggered hidden research unlocks only when awarded scan aspects include the configured aspect.
- [ ] Hidden/lost research remains unavailable until parent and hidden-parent prerequisites match original rules.
- [ ] Knowledge-fragment use can select eligible hidden research with trigger metadata.

**Риски / зависимости:**

Entity registry names changed between 1.7.10 and 1.12.2. Trigger strings must either preserve original keys where the rest of the port does so, or be mapped explicitly and documented.

### GAP-7: Research completion packet bypasses original progression rules

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/network/playerdata/PacketPlayerCompleteToServer.java:45`-`src/main/java/thaumcraft/common/lib/network/playerdata/PacketPlayerCompleteToServer.java:69`
- `src/main/java/thaumcraft/common/lib/research/ResearchManager.java:267`-`src/main/java/thaumcraft/common/lib/research/ResearchManager.java:281`
- `src/main/java/thaumcraft/common/lib/research/ResearchManager.java:66`-`src/main/java/thaumcraft/common/lib/research/ResearchManager.java:97`

**Референс:**
- `.stage9e-ref/thaumcraft/common/lib/research/ResearchManager.java:120`-`.stage9e-ref/thaumcraft/common/lib/research/ResearchManager.java:134`
- `.stage9e-ref/thaumcraft/common/items/ItemResearchNotes.java:87`-`.stage9e-ref/thaumcraft/common/items/ItemResearchNotes.java:104`
- `.stage9e-ref/thaumcraft/common/lib/research/ResearchManager.java:428`-`.stage9e-ref/thaumcraft/common/lib/research/ResearchManager.java:455`

**Что не совпадает:**

Server packet flow now validates requisites, separates primary/secondary actions by packet `type` + `isSecondary()`, creates research notes for primary entries, charges aspect pools for secondary purchases, and keeps sibling grants behind prerequisite checks. Packet payload serialization coverage now also includes `PacketPlayerCompleteToServer` round-trip checks (key/dimension/username/type), and a static guard test now enforces presence of prerequisite/type/note-cost branching in the server handler source. Remaining mismatch is lack of runtime/manual validation of these branches with real research content data.

**Что нужно доделать:**

Verify the server-side progression paths with populated Stage 9 research content and live Thaumonomicon actions once that content is available.

**Как доделать:**
- Done: enforce prerequisite checks before any completion/note action in `PacketPlayerCompleteToServer`.
- Done: treat `type=0` as secondary purchase path (aspect cost + completion) and `type=1` as primary note-creation path.
- Done: add `ResearchManager.createResearchNoteForPlayer(...)`, `getResearchSlot(...)`, and `consumeInkFromPlayer(...)` server helpers.
- Done: restrict sibling completion to prerequisite-valid siblings only.
- Remaining: verify against populated Stage 9-e content and client click flows.

**Критерии приемки:**
- [ ] Packet cannot complete research if visible or hidden parents are missing.
- [ ] Primary research click produces a note and does not immediately add the research key to completed research.
- [ ] Secondary research direct completion consumes the correct research aspect cost before granting.
- [ ] Sibling research grants occur only after a valid main completion and preserve original sibling behavior.
- [ ] Invalid packet data cannot grant arbitrary research keys.

**Риски / зависимости:**

Dependency: client Thaumonomicon GUI action implementation is outside this chunk, but server-side content progression must reject invalid completion requests regardless of GUI status.

### GAP-8: Manual/runtime validation for research content is not possible yet and has not been performed

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigResearch.java` (kernel) + 6 category slice files (`ConfigResearchBasics.java`, `ConfigResearchAlchemy.java`, `ConfigResearchArtifice.java`, `ConfigResearchThaumaturgy.java`, `ConfigResearchGolemancy.java`, `ConfigResearchEldritch.java`) — partial category+entry baseline across all 7 files.
- `src/main/resources/assets/thaumcraft/lang/en_us.lang` — imported research localization corpus.
- `src/main/resources/assets/thaumcraft/textures/gui/` and `textures/misc/r_*.png` — research GUI/icon assets partially restored.
- `src/test/java/thaumcraft/common/config/ConfigResearchStaticGraphTest.java` — static non-GUI graph/lang validation baseline that reads the whole `ConfigResearch*.java` family (kernel + 6 category slices).

**Референс:**
- `docs/PRD.md:415`-`docs/PRD.md:416`
- `docs/PRD.md:544`-`docs/PRD.md:545`

**Что не совпадает:**

PRD explicitly says content can compile while unusable, and runtime/manual checks must verify Thaumonomicon pages and progression. The port now has an initial static validation layer and baseline content data, but full runtime/manual progression coverage is still missing.

**Что нужно доделать:**

After implementation gaps close, run focused validation and document results in this file.

**Как доделать:**
- Add a lightweight diagnostic/test that counts registered categories, research entries, null pages, null recipe page objects, missing lang keys, missing assets, missing parent targets, and impossible parent cycles.
- Run `./scripts/dev.sh compileJava` after implementation.
- Run `./scripts/dev.sh build` and `./scripts/dev.sh check-jar` before packaging.
- Run server smoke if common registration changes are included.
- Run client/manual smoke for Thaumonomicon opening, category display, sample page rendering, primary note creation, secondary direct unlock, hidden scan clue, and recipe page unlock display.
- Update this document with validation commands/results after implementation.

**Критерии приемки:**
- [ ] Automated/static validation reports no missing category, parent, hidden-parent, sibling, lang, asset, or page recipe references inside Stage 9-e scope.
- [ ] `compileJava`, `build`, and `check-jar` pass or failures are documented as pre-existing/environmental.
- [ ] Server smoke reaches normal ready state after research registration changes.
- [ ] Client/manual Thaumonomicon scenarios pass for at least one entry in each category.
- [ ] Manual progression scenarios pass for primary note, secondary research, hidden scan clue, and recipe-gated craft visibility.

**Риски / зависимости:**

Client GUI/rendering dependencies from Stage 8 may block visual verification. If so, Stage 9-e can only be marked partially implemented, not complete, until content can be opened and validated in game.

## 6. Итоговый checklist закрытия Stage 9-e

- [ ] Port original `ConfigResearch` category registration (kernel: `ConfigResearch.java`).
- [ ] Port original 201 research entries across all 6 category slice files (`ConfigResearchBasics.java`, `ConfigResearchAlchemy.java`, `ConfigResearchArtifice.java`, `ConfigResearchThaumaturgy.java`, `ConfigResearchGolemancy.java`, `ConfigResearchEldritch.java`) or document any intentionally deferred entry with a concrete blocker.
- [ ] Preserve all original research keys, category IDs, parent/hidden-parent/sibling links, flags, triggers, aspects, coordinates, complexity values, icons, and page order.
- [ ] Provide valid recipe objects/lists for every research page recipe reference used by ported entries.
- [ ] Copy or adapt all required English research lang keys into `en_us.lang`.
- [ ] Copy all required category icons, backgrounds, research-book textures, and image-page textures from `thaumcraft_src/assets/thaumcraft/`.
- [ ] Port research note NBT/data flow, note generation, note completion, discovery use, and unknown fragment hidden-research behavior.
- [ ] Wire scan-triggered hidden clue unlocks through `ResearchManager.createClue(...)`.
- [ ] Fix server-side completion packet so it cannot bypass prerequisites, note creation, or aspect/paper/ink costs.
- [ ] Add static validation for missing research references and run it after registration.
- [ ] Run compile/build validation after implementation.
- [ ] Run runtime/manual validation for Thaumonomicon content and progression scenarios.
- [ ] Update `docs/Stage9-e.md` with closure status and validation results.

## 7. Definition of Done

Stage 9-e считается ПОЛНОСТЬЮ завершенной только если:
- все blocker gaps закрыты;
- все high gaps закрыты;
- все элементы из scope Stage 9-e реализованы;
- текущая реализация соответствует референсу по поведению;
- все нужные файлы, ресурсы и регистрации присутствуют;
- отсутствуют TODO и заглушки внутри scope Stage 9-e;
- проект собирается без ошибок;
- базовые игровые сценарии Stage 9-e проверены вручную или тестами;
- ./docs/Stage9-e.md обновлен и не содержит критичных открытых вопросов.

## 8. Открытые вопросы

**Resolution:** open question здесь не архитектурный, а acceptance/dependency вопрос. Stage 9 recipe chunks должны предоставить валидные recipe objects для 276 original research-page recipe references, а Stage 8 GUI может блокировать visual Thaumonomicon smoke.

Формулировка: "Stage 9-e should register research categories, entries, pages, lang/assets, and server-side unlock/rule data independently of GUI visual smoke. However, it cannot be accepted as complete until all referenced recipe objects from Stage 9-a/b/c/d resolve correctly. If Stage 8 GUI work blocks visual Thaumonomicon validation, record it as a client-smoke dependency, not as a reason to skip research graph validation."
