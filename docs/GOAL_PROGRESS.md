# Durable Goal Progress

Last updated: 2026-05-15
Branch: `codex/durable-goal-stage8-9`

## Contract Checklist

- [x] Read source-of-truth files: `AGENTS.md`, `docs/PRD.md`, `docs/GOAL.md`, `build.gradle`, `Dockerfile`.
- [x] Read active stage plans: `docs/Stage3.md` through `docs/Stage9-e.md`.
- [x] Start from `git status --short`: clean at recon start.
- [x] Work on a dedicated branch instead of `master`/`main`.
- [x] Establish baseline non-GUI validation.
- [ ] Close or explicitly classify remaining Stage 3-7 blockers before Stage 8/9 parity claims.
- [ ] Implement Stage 8-a client bootstrap and side-safe boundaries.
- [ ] Implement Stage 8-b GUI routing/screens/resources to non-GUI-verifiable completion.
- [ ] Implement Stage 8-c tile/block render registration/classes/resources to non-GUI-verifiable completion.
- [ ] Implement Stage 8-d entity render registration/classes/resources to non-GUI-verifiable completion.
- [ ] Implement Stage 8-e FX/particles/beams/bolts/shader-adjacent safe fallbacks.
- [ ] Implement Stage 9-a recipe/content foundation.
- [ ] Implement Stage 9-b arcane crafting parity.
- [ ] Implement Stage 9-c infusion crafting/enchanting parity.
- [ ] Implement Stage 9-d crucible/alchemy/thaumatorium content flow.
- [ ] Implement Stage 9-e research/Thaumonomicon progression content.
- [ ] Complete Phase 10 polish: localization, sounds, crash cleanup, performance sanity, docs/status cleanup.
- [ ] Run final non-GUI validation: `./scripts/dev.sh validate --smoke`, targeted `build`/`check-jar`/static scans where needed, and `git diff --check`.
- [ ] Record GUI/manual graphics checks as skipped by user instruction where they require user interaction or unavailable graphics.
- [ ] End with clean `git status --short` after intended checkpoint commits, or document remaining diffs.

## Current Evidence

- `git status --short`: clean at the start of this run.
- Active branch was created from clean `master`: `codex/durable-goal-stage8-9`.
- `docs/Stage3.md` contains fresh-world core closure notes dated 2026-05-14. Remaining Stage 3 items are documented dependencies/manual scenario coverage, not a pre-Phase8 implementation blocker.
- `docs/Stage4.md` contains fresh-world server/common closure notes dated 2026-05-14. Remaining Stage 4 items are Phase 8/9 dependencies or explicitly deferred reference pieces.
- `docs/Stage5.md` remains open. Recent closure notes show substantial wand, pouch, hover harness, item resource, ItemKey, Compass Stone, Loot Bag, and focus parity work, but remaining common/server utility, relic, focus, hover, and manual scenario validation gaps remain.
- `docs/Stage6.md` remains open. Golem/trunk item routes, Pech behavior, Cultist Portal loot, boss/projectile validation, drops/sounds, and manual scenario matrix remain active blockers.
- `docs/Stage7.md` remains open. Worldgen pipeline, Eldritch ring/maze bootstrap, safe teleporter, Outer Lands room templates, BlockLoot/room block contracts, Greatwood structures, and runtime evidence remain active blockers.
- Stage 8-a through Stage 8-e are documented as not complete.
- Stage 9-a through Stage 9-e are documented as not complete.
- `./scripts/dev.sh validate` — passes on current builds with compact MCP leak summary.
- `./scripts/dev.sh validate --smoke` — passes on current builds after the 2026-05-15 wrapper/logging fix.
- Direct `./scripts/dev.sh check-jar` remains the verbose MCP leak listing path.

## Skipped GUI/Manual Graphics Checks

- Arcane Workbench client smoke/manual open/visual parity was skipped on 2026-05-15 because `DISPLAY=` and user-driven GUI/graphics validation is excluded by instruction.
- Arcane Bore client smoke/manual open/visual parity was skipped on 2026-05-15 because `DISPLAY=` and user-driven GUI/graphics validation is excluded by instruction.
- Thaumatorium client smoke/manual open/visual parity was skipped on 2026-05-15 because `DISPLAY=` and user-driven GUI/graphics validation is excluded by instruction.
- Focal Manipulator client smoke/manual open/visual parity was skipped on 2026-05-15 because `DISPLAY=` and user-driven GUI/graphics validation is excluded by instruction.
- Focus Pouch, Hand Mirror, and Hover Harness client smoke/manual open/item movement parity was skipped on 2026-05-15 because `DISPLAY=` and user-driven GUI/graphics validation is excluded by instruction.
- Deconstruction Table and Alchemy Furnace client smoke/manual open/visual parity was skipped on 2026-05-15 because `DISPLAY=` and user-driven GUI/graphics validation is excluded by instruction.
- Magic Box and Spa client smoke/manual open/visual parity was skipped on 2026-05-15 because `DISPLAY=` and user-driven GUI/graphics validation is excluded by instruction.
- Traveling Trunk client smoke/manual open/visual parity was skipped on 2026-05-15 because `DISPLAY=` and user-driven GUI/graphics validation is excluded by instruction.
- Pech client smoke/manual open/trade visual parity was skipped on 2026-05-15 because `DISPLAY=` and user-driven GUI/graphics validation is excluded by instruction.
- Golem client smoke/manual open/visual parity was skipped on 2026-05-15 because `DISPLAY=` and user-driven GUI/graphics validation is excluded by instruction.
- Research Table client smoke/manual open/visual parity was skipped on 2026-05-15 because `DISPLAY=` and user-driven GUI/graphics validation is excluded by instruction.
- Thaumonomicon/Research Browser client smoke/manual open/visual parity was skipped on 2026-05-15 because `DISPLAY=` and user-driven GUI/graphics validation is excluded by instruction.
- Future GUI/client visual checks that require user-driven Minecraft control, screenshots, or unavailable X11/graphics stack will be recorded as: `SKIPPED by user instruction: GUI/graphics/user-interactive validation excluded`.

## Baseline Validation

- `./scripts/dev.sh compileJava` — passed on 2026-05-14 before gameplay/code changes.
- `./scripts/dev.sh check-jar` — passed on 2026-05-15 after script repair (MCP mapping cache path resolved in `scripts/dev.sh`).
- `./scripts/dev.sh smoke-server` — passed on 2026-05-15 after the smoke wrapper fix in `scripts/dev.sh` (temporary Log4j2 console config plus readiness fallback to `run/logs/latest.log`).
- `./scripts/dev.sh validate` — passed on 2026-05-15 with compact MCP leak summary for `5114` MCP leak lines / `1028` unique leaks.
- `./scripts/dev.sh validate --smoke` — passed on 2026-05-15, including compact server smoke validation.

## Checkpoint Log

### 2026-05-15 — Stage 9-e scan clue unlock baseline (`@KEY`)

Scope:

- Added `ResearchManager.createClue(World, EntityPlayer, Object, AspectList)` with reference-aligned matching path for hidden/lost entries:
  - item triggers via `InventoryUtils.areItemStacksEqual(...)`;
  - entity trigger string matches;
  - aspect trigger matches against awarded scan aspects.
- Wired `ScanManager.completeScan` to:
  - preserve scan clue object (`ItemStack` or entity key string),
  - track actually awarded aspects from `checkAndSyncAspectKnowledge(...)`,
  - call `ResearchManager.createClue(...)` after successful server-side scan completion.
- Clue unlocks are now granted as `@KEY` through normal research completion sync path, avoiding direct full-research completion.
- Updated `docs/Stage9-e.md` GAP-6 status from absent to partial with remaining entity-ID/runtime parity limits documented.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5662` MCP leak lines / `1114` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: server ready (`Done (...)`), no fatal markers.
- Crash report scan under `run/` remained clean during smoke stage.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Full hidden/lost clue parity still depends on broader trigger-bearing `ConfigResearch` population and runtime verification scenarios.
- Entity-trigger string compatibility (`Thaumcraft.*` legacy IDs vs modern registry names) still needs targeted parity validation.

### 2026-05-15 — Stage 9-e text-only research graph expansion baseline

Scope:

- Expanded `ConfigResearch` with additional reference-aligned entries that do not depend on `recipes.get(...)` objects:
  - BASICS: `ENCHANT`, `NODETAPPER1`, `NODEPRESERVE`, `NODETAPPER2`, `CRIMSON`
  - THAUMATURGY (virtual auto-unlock): `CAP_iron`, `ROD_wood`
  - ELDRITCH: `ELDRITCHMAJOR`
- Preserved reference metadata for this slice: category IDs, coordinates, aspect tags, parent links, flags (`stub/round/hidden/special/autoUnlock`), and page text keys.
- Added reference warp metadata for `CRIMSON` via `ThaumcraftApi.addWarpToResearch("CRIMSON", 3)`.
- Updated `docs/Stage9-e.md` current-state summary to reflect the expanded safe subset while keeping recipe-backed graph work open.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5662` MCP leak lines / `1114` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: server ready (`Done (...)`), no fatal markers.
- Crash report scan under `run/` remained clean during smoke stage.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Stage 9-e still lacks the majority of research entries/pages and most recipe-backed page wiring.
- Hidden/lost clue unlock flow and full research-note progression parity remain open.

### 2026-05-15 — Stage 9-e BASIC text-only research entry baseline

Scope:

- Extended `ConfigResearch` beyond categories with a safe first research-entry slice that has no recipe-object dependencies:
  - `ASPECTS`
  - `PECH`
  - `NODES`
  - `WARP`
- Preserved reference category, icon, coordinate, and page-key wiring for this subset, including `stub`/`round`/`autoUnlock` flags.
- Kept scope intentionally narrow to avoid null recipe-page crashes while Stage 9-e recipe-backed page graph is still incomplete.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5662` MCP leak lines / `1114` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: server ready (`Done (...)`), no fatal markers.
- Crash report scan under `run/` remained clean during smoke stage.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Stage 9-e research graph remains mostly unported (201 reference entries target).
- Recipe-backed research pages/triggers/warp metadata are still open and require incremental guarded registration.

### 2026-05-15 — Stage 9-e research localization corpus import baseline

Scope:

- Imported missing research localization corpus from `thaumcraft_src/assets/thaumcraft/lang/en_US.lang` into the port `en_us.lang` without overwriting existing keys:
  - `tc.research_name.*`
  - `tc.research_text.*`
  - `tc.research_page.*`
  - plus discovery/reveal helper strings: `item.discovery.name`, `item.researchnotes.unknown.1`, `item.researchnotes.unknown.2`
- Resulting key coverage:
  - `tc.research_(name|text|page).*` count now matches reference (`707`);
  - with category keys, total research localization key surface now matches the reference baseline (`713`).
- Updated `docs/Stage9-e.md` language status text to reflect corpus import completion with remaining runtime/manual validation limits.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5662` MCP leak lines / `1114` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: server ready (`Done (...)`), no fatal markers.
- Crash report scan under `run/` remained clean during smoke stage.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Research categories and localization are now in place, but `ConfigResearch` research-item/page graph registration remains largely unported.
- Client/manual validation of Thaumonomicon page rendering is still excluded and remains documented as skipped.

### 2026-05-15 — Stage 9-e research category + icon/backdrop baseline

Scope:

- Restored Stage 9-e category registration baseline in `ConfigResearch`:
  - `ConfigResearch.init()` now calls `initCategories()`;
  - registered `BASICS`, `THAUMATURGY`, `ALCHEMY`, `ARTIFICE`, `GOLEMANCY`, `ELDRITCH` with reference icon/background `ResourceLocation` paths.
- Added missing research-content misc assets from `thaumcraft_src`:
  - copied `textures/misc/r_*.png` and `textures/misc/research1.png` … `research5.png` into port resources.
- Updated Stage 9-e gap doc status to reflect partial closure:
  - GAP-1 moved from absent to partial (categories registered; entries/pages still open);
  - GAP-3 moved from absent to partial (category/status keys present; full research text corpus still missing);
  - GAP-4 moved from absent to partial (baseline research GUI/misc assets present; full page-image coverage still pending).

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5662` MCP leak lines / `1114` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: server ready (`Done (...)`), no fatal markers.
- Crash report scan under `run/` remained clean during smoke stage.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- `ConfigResearch` still does not register research items/pages, so category containers are populated but progression graph is still absent.
- Full `tc.research_name.*` / `tc.research_text.*` / `tc.research_page.*` localization corpus remains to be ported.
- Stage 9-e note/clue progression, scan-trigger clue unlocks, and packet-side prerequisite enforcement remain open.

### 2026-05-15 — Stage 9-b ArcaneStone2/3/4 non-arcane recipe parity baseline

Scope:

- Added missing `blockStairsArcaneStone` content path:
  - new `BlockStairsArcaneStone` class based on `blockCosmeticSolid` meta `7` (arcane stone);
  - `ConfigBlocks` registration (`init`, `getAllBlocks`, `registerItemBlocks`) and resource wiring.
- Added resource coverage for the new stairs block:
  - `blockstates/blockstairsarcanestone.json`;
  - block models (`blockstairsarcanestone`, `_inner`, `_outer`);
  - item model (`models/item/blockstairsarcanestone.json`).
- Implemented non-arcane reference key registrations in recipe registry stage:
  - added `arcanestone2`, `arcanestone3`, `arcanestone4` shaped recipes via `ShapedOreRecipe` in `registerSpecialRecipes`;
  - persisted `IRecipe` handles and restored `ConfigResearch.recipes` keys `ArcaneStone2`, `ArcaneStone3`, `ArcaneStone4` during `ConfigRecipes.init()`.
- Added lang entry for the new stairs block.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5661` MCP leak lines / `1114` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.167s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Stage 9-b recipe key parity is now functionally broader, but Arcane Workbench runtime/manual scenario matrix and Stage 9-d/e research/content closure remain open.

### 2026-05-15 — Stage 9-b Arcane Door unblock baseline

Scope:

- Added missing Arcane Door content surface:
  - `BlockArcaneDoor` with ownership-gated activation (`TileOwned` owner/access checks) and locked-fail sound path;
  - `ItemArcaneDoor` placement flow for two-block door placement plus owner assignment on both halves.
- Wired Arcane Door into registries:
  - `ConfigBlocks.blockArcaneDoor` init/getAllBlocks registration;
  - `ConfigItems.itemArcaneDoor` declaration/init/registration in item bootstrap.
- Added missing Stage 9-b arcane recipe key:
  - `ArcaneDoor` (`WARDEDARCANA`) with reference-aligned aspects/pattern/components.
- Added Arcane Door item model/lang entries and copied door block textures from `thaumcraft_src` (`adoorbot`, `adoortop`).
- Focused arcane key audit now reports `0` missing arcane API keys (`addArcaneCraftingRecipe` + `addShapelessArcaneCraftingRecipe`); remaining Stage 9-b recipe-key deltas are only non-arcane `GameRegistry` keys (`ArcaneStone2/3/4`).

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5660` MCP leak lines / `1114` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.069s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Stage 9-b still has unresolved non-arcane reference keys (`ArcaneStone2/3/4`) tied to missing `blockStairsArcaneStone` and non-arcane `GameRegistry` path parity.
- Arcane Workbench runtime/manual scenario matrix and Stage 9-d/e research/content closure remain open.

### 2026-05-15 — Stage 9-b Levitator unblock baseline

Scope:

- Replaced `TileLifter` stub with reference-aligned lift behavior:
  - vertical stack/range recomputation, redstone power gating, entity lift motion/fall-distance handling, and periodic update cadence.
- Added missing `BlockLifter` implementation and wired neighbor/stack update hooks plus client sparkle hook.
- Registered `blockLifter` in `ConfigBlocks` (`init`, `getAllBlocks`, `registerItemBlocks`) and added block resources/models/lang.
- Added the missing arcane recipe registration key `Levitator` (`LEVITATOR`) in `initializeArcaneRecipeBaseline()`, preserving reference aspects/pattern/components.
- Copied source textures for lifter assets from `thaumcraft_src` (`lifterside`, `liftertop`, `arcaneearbottom`, `animatedglow` + `.mcmeta`).
- Focused arcane key audit now reports only one missing arcane API key (`ArcaneDoor`); non-arcane reference keys `ArcaneStone2/3/4` remain outside arcane API path.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5642` MCP leak lines / `1111` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.158s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Stage 9-b unresolved arcane blockers now are `ArcaneDoor` and non-arcane-path `ArcaneStone2/3/4`.
- Stage 9-b still requires runtime/manual Arcane Workbench scenario validation and broader Stage 9-d/e research/content closure.

### 2026-05-15 — Stage 9-b/9-c Hungry Chest + TravelTrunk unblock baseline

Scope:

- Ported reference Hungry Chest block/tile baseline from 1.7.10 into 1.12.2:
  - added `BlockChestHungry` with inventory collision consume path, comparator output, activation inventory open, and bounded non-full-cube collision box;
  - added `TileChestHungry` inventory persistence/ticking/lid open-close event behavior and tile registration.
- Wired Hungry Chest into registry/resources:
  - `ConfigBlocks` block field/init/itemblock/tile registration updated for `blockChestHungry` / `TileChestHungry`;
  - added blockstate/model files and copied source textures (`woodplain.png`, `chesthungry.png`) from `thaumcraft_src/assets`.
- Closed coupled recipe blockers:
  - Stage 9-b arcane key `HungryChest` registered in `initializeArcaneRecipeBaseline()`;
  - Stage 9-c infusion key `TravelTrunk` registered in `initializeInfusionEquipmentArmorRecipeBaseline()`.
- Focused key audits:
  - infusion keys: `63/63` reference keys registered (no missing);
  - arcane recipe-key blocker list reduced to `5` unresolved (`ArcaneDoor`, `Levitator`, `ArcaneStone2/3/4`).

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5624` MCP leak lines / `1109` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.247s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Stage 9-b still has explicit unresolved arcane blockers: `ArcaneDoor`, `Levitator`, and non-arcane-path `ArcaneStone2/3/4`.
- Stage 9-c runtime/research-page/manual scenario matrix remains open.

### 2026-05-15 — Stage 9-c infusion equipment/armor/mask/utility baseline

Scope:

- Expanded Stage 9-c GAP-1 infusion crafting coverage with the remaining equipment-heavy reference subset:
  - hover/runic/bauble chain: `HoverHarness`, `HoverGirdle`, `VisAmulet`, `RunicAmulet`, `RunicAmuletEmergency`, `RunicRing`, `RunicRingCharged`, `RunicRingHealing`, `RunicGirdle`, `RunicGirdleKinetic`, `RunicGirdleKinetic_2`
  - elemental/tool/armor chain: `ElementalAxe`, `ElementalPick`, `ElementalSword`, `ElementalShovel`, `ElementalHoe`, `BootsTraveller`, `ThaumiumFortressHelm`, `ThaumiumFortressChest`, `ThaumiumFortressLegs`, `VoidRobeHelm`, `VoidRobeChest`, `VoidRobeLegs`
  - object-output and utility chain: `HelmGoggles`, `MaskGrinningDevil`, `MaskAngryGhost`, `MaskSippingFiend`, `SanityCheck`, `SinStone`, `PrimalCrusher`, `EldritchEye`
- Added `NBTTagInt` support for reference mask object outputs (`{"mask", NBTTagInt(...)}`).
- Preserved reference keys/research gates/instability/aspect formulas/central inputs/components, including potion metadata variants for runic upgrades.
- Ran focused key audit (`ConfigRecipes.class` vs `registerInfusionRecipe`): unresolved reference infusion crafting keys reduced from `32` to `1`; only `TravelTrunk` remains blocked by missing `ConfigBlocks.blockChestHungry`.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5579` MCP leak lines / `1098` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.083s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- `TravelTrunk` reference infusion recipe is still blocked until `ConfigBlocks.blockChestHungry` (or an explicit 1.12.2-compatible equivalent) exists.
- Stage 9-c runtime/research-page/manual scenario matrix remains open.

### 2026-05-15 — Stage 9-c infusion golem/core/device baseline

Scope:

- Expanded Stage 9-c GAP-1 with a tightly coupled golem/core/device infusion subset:
  - `AdvancedGolem`, `CoreAlchemy`, `CoreSorting`, `CoreLumber`, `CoreFishing`, `CoreUse`
  - `ArcaneBore`, `LampGrowth`, `LampFertility`, `EssentiaReservoir`
- Added support for reference object-output infusion entry (`AdvancedGolem`) via helper accepting `Object` result payload (`{"advanced", NBTTagByte(1)}`).
- Preserved reference keys/research gates/instability/aspect formulas/central inputs/components for this subset.
- Refreshed infusion key audit: unresolved reference infusion crafting keys reduced from `42` to `32`.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5570` MCP leak lines / `1095` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.222s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Stage 9-c infusion crafting coverage remains incomplete (`32` keys outstanding by reference key audit), including runic/armor/tool/bauble progression groups and `TravelTrunk`.
- Runtime/research-page verification matrix is still open.

### 2026-05-15 — Stage 9-c infusion focus/device/mirror baseline

Scope:

- Expanded Stage 9-c GAP-1 infusion crafting coverage with a tightly coupled focus/device/mirror subset:
  - `FocusHellbat`, `FocusPortableHole`, `FocusWarding`
  - `WandPed`, `WandPedFocus`, `NodeStabilizerAdv`, `JarBrain`
  - conditional mirror branch under `Config.allowMirrors`: `Mirror`, `MirrorHand`, `MirrorEssentia`
- Preserved reference keys, research gates, instability, aspect costs, central inputs, and component lists for this subset.
- Updated Stage 9-c infusion key audit: unresolved infusion crafting keys reduced to `42`.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5558` MCP leak lines / `1092` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.160s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Stage 9-c infusion crafting registrations remain incomplete (`42` keys outstanding by reference key audit).
- Runtime/research-page verification for infusion scenarios remains open.

### 2026-05-15 — Stage 9-c infusion enchantment baseline

Scope:

- Added helper-based infusion enchantment registration block to `ConfigRecipes.init()`:
  - custom recipes: `InfEnchRepair`, `InfEnchHaste` (using current `Config.enchRepair`, `Config.enchHaste`);
  - vanilla set: `InfEnch0` ... `InfEnch21` mapped to 1.12 `Enchantments.*`.
- Preserved reference keys, gate (`INFUSIONENCHANTMENT`), instability, aspect costs, and component lists for all 24 entries.
- Added `registerInfusionEnchantmentRecipe(...)` helper and updated `docs/Stage9-c.md` GAP-2 status to partial closure with key-level coverage.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5552` MCP leak lines / `1088` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.186s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Runtime verification of representative armor/weapon/tool infusion enchant scenarios remains open.
- Research page/display parity and broader Stage 9-c acceptance matrix remain open.

### 2026-05-15 — Stage 9-c infusion wand-component baseline

Scope:

- Started Stage 9-c GAP-1 recipe-data population by adding infusion wand component registrations to `ConfigRecipes.init()` via a dedicated helper (`registerInfusionRecipe`).
- Ported reference-aligned infusion crafting entries with stable `ConfigResearch.recipes` keys:
  - `WandCapSilver`, `WandCapThaumium`, `WandCapVoid`
  - `WandRodObsidian`, `WandRodIce`, `WandRodQuartz`, `WandRodReed`, `WandRodBlaze`, `WandRodBone`, `WandRodSilverwood`, `WandRodPrimalStaff`
- Preserved reference research keys, instability values, aspect formulas (including reference blaze-cost secondary aspects), central inputs, and component lists for this subset.
- Updated `docs/Stage9-c.md` GAP-1 status/analysis from absent to partial for this baseline.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5519` MCP leak lines / `1086` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.181s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- This checkpoint ports only the wand-component infusion subset; Stage 9-c still needs the remaining infusion crafting set and all infusion enchantment registrations.
- Full Stage 9-c acceptance (63 crafting + 24 enchantment registrations, research page/display parity, runtime scenario matrix) remains open.

### 2026-05-15 — Stage 9-b arcane remaining static subset and key audit

Scope:

- Expanded `ConfigRecipes.initializeArcaneRecipeBaseline()` with the remaining portable static arcane subset:
  - `Banner_0..15`, `WardedGlass`, `FluxScrubber`, `ArcanePressurePlate` (conditional)
  - `NodeStabilizer`, `NodeTransducer`, `NodeRelay`, `NodeChargeRelay`, `FocalManipulator`, `GolemFetter`
  - `PaveTravel`, `ArcaneLamp`, `ArcaneSpa`, `PaveWard`, `ArcaneEar`
  - `WandRodIceStaff`, `WandRodQuartzStaff`, `WandRodReedStaff`, `WandRodBlazeStaff`, `WandRodBoneStaff`
  - `TinyHat`, `TinyFez`, `TinyBowtie`, `TinyGlasses`, `TinyDart`, `TinyVisor`, `TinyArmor`, `TinyHammer`
- Added banner NBT color tagging (`color` byte) and persisted all new handles in `ConfigResearch.recipes`.
- Ran reference-vs-current key audit for `initializeArcaneRecipes()`: unresolved keys reduced to 6 (`ArcaneDoor`, `HungryChest`, `Levitator`, `ArcaneStone2/3/4`) with concrete blockers documented in `docs/Stage9-b.md`.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5516` MCP leak lines / `1085` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.221s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Remaining unresolved keys are blocked by currently missing output objects (`itemArcaneDoor`, `blockChestHungry`, `blockLifter`) and non-arcane `GameRegistry` recipe path for `ArcaneStone2/3/4` (`ArcaneStone3` also depends on missing `blockStairsArcaneStone`).
- Stage 9-d/e research content/category/page population is still required for full gate-key and recipe-key lookup parity.
- Arcane Workbench manual/client scenario verification remains outside current non-GUI scope.

### 2026-05-15 — Stage 9-b arcane alchemy/tube static subset

Scope:

- Expanded `ConfigRecipes.initializeArcaneRecipeBaseline()` with the next tightly coupled static arcane subset from reference `initializeArcaneRecipes()`:
  - `Filter`, `AlchemyFurnace`, `Alembic`, `Bellows`, `Tube`, `Resonator`
  - `TubeValve`, `TubeFilter`, `TubeRestrict`, `TubeOneway`, `TubeBuffer`
  - `AlchemicalConstruct`, `AdvAlchemyConstruct`, `Centrifuge`, `EssentiaCrystalizer`, `MnemonicMatrix`
- Reused the shapeless arcane helper path for tube conversion variants and persisted all handles to `ConfigResearch.recipes`.
- Preserved reference-aligned research keys/aspect costs/patterns with 1.12 constants resolved through MCP stable_39 mappings.
- Updated `docs/Stage9-b.md` GAP-1/GAP-6 notes to include this alchemy/tube subset coverage.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5506` MCP leak lines / `1080` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.188s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Stage 9-b static arcane coverage is now broader but still incomplete versus full reference `initializeArcaneRecipes()` set.
- Stage 9-d/e research content/category/page population is still required for full gate-key and recipe-key lookup parity.
- Arcane Workbench manual/client scenarios remain outside current non-GUI validation scope.

### 2026-05-15 — Stage 9-b arcane focus/golem/utility static subset

Scope:

- Expanded `ConfigRecipes.initializeArcaneRecipeBaseline()` with a tightly coupled static arcane subset from the reference `initializeArcaneRecipes()` block:
  - `MirrorGlass`, `BoneBow`, `PrimalArrow_0..5`, `InfusionMatrix`, `ArcanePedestal`
  - `FocusShock`, `FocusTrade`, `FocusExcavation`, `FocusPrimal`, `FocusPouch`
  - `Deconstructor`, `ArcaneBoreBase`, `EnchantedFabric`
  - `GolemBell`, `CoreBlank`, `UpgradeAir`..`UpgradeEntropy`
- Added shapeless arcane helper registration path (`registerShapelessArcaneRecipe`) with `ConfigResearch.recipes` handle writes.
- Kept recipes reference-aligned for pattern/research/aspect keys and 1.12 constants resolved from MCP stable_39 field mappings.
- Updated `docs/Stage9-b.md` GAP-1/GAP-6 notes to reflect the expanded static subset coverage.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5502` MCP leak lines / `1078` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.134s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- This checkpoint adds a larger static subset, but full Stage 9-b arcane recipe coverage from reference `initializeArcaneRecipes()` is still incomplete.
- Stage 9-d/e research content/category/page population is still required for full recipe-key lookup and unlock parity.
- Arcane Workbench manual/client scenario validation remains outside current non-GUI scope.

### 2026-05-15 — Stage 9-b static arcane recipe baseline subset

Scope:

- Added a concrete static arcane recipe baseline in `ConfigRecipes.init()` via `initializeArcaneRecipeBaseline()`.
- Ported a first reference-aligned subset of static arcane entries with `ConfigResearch.recipes` handles:
  - `PrimalCharm`, `IronKey`/`GoldKey`, `ArcaneStone1`, `WardedJar`, `JarVoid`
  - `WandCapGold`, `WandCapCopper` (conditional), `WandCapSilverInert` (conditional), `WandCapThaumiumInert`, `WandCapVoidInert`
  - `WandRodGreatwood`, `WandRodGreatwoodStaff`, `WandRodObsidianStaff`, `WandRodSilverwoodStaff`
  - `FocusFire`, `FocusFrost`, `RobeChest`, `RobeLegs`, `RobeBoots`, `Goggles`
- Added safe wand cost helpers (`WandCap`/`WandRod`) used by cap/rod recipe aspect costs.
- Updated `docs/Stage9-b.md` to mark GAP-1 and GAP-6 as partial closures and to reflect this baseline progress.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5490` MCP leak lines / `1074` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.134s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- This checkpoint ports only a baseline subset of static arcane recipes; full reference `initializeArcaneRecipes()` coverage remains open.
- Stage 9-d/e research content/category/page population is still required for full gate-key parity and recipe-key lookup behavior.
- Arcane Workbench manual/client scenarios remain out of scope for current non-GUI validation.

### 2026-05-15 — Stage 9 lifecycle recipe-map reset ordering fix

Scope:

- Fixed a post-init ordering side effect where research recipe-handle map resets could erase recipe registrations made earlier in the same lifecycle.
- Moved `ConfigResearch.recipes.clear()` from `ConfigResearch.init()` to the start of `ConfigRecipes.init()` so clearing occurs before dynamic recipe registration.
- Kept existing post-init call order while preventing recipe-handle wipeouts after `ConfigRecipes.init()`.
- Updated `docs/Stage9-a.md` GAP-9 notes/status to capture this targeted lifecycle fix and remaining broader lifecycle gaps.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5480` MCP leak lines / `1069` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.296s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- This fixes map-reset ordering only; full Stage 9 lifecycle parity (ore-compat timing, static recipe population, aspect/object-tag completeness, research content order) remains open.
- Recipe/event registration remains partial and needs additional Stage 9-a/9-b/9-e implementation checkpoints.

### 2026-05-15 — Stage 9-b dynamic arcane wand/sceptre recipe baseline

Scope:

- Added `ArcaneWandRecipe` and `ArcaneSceptreRecipe` under `thaumcraft.common.lib.crafting` as `IArcaneRecipe` implementations.
- Ported reference-style layout checks, cap/rod matching, research gating (`CAP_*`, `ROD_*`, `SCEPTRE`), primal aspect cost formulas, and output tagging for wand/sceptre craft results.
- Wired `ConfigRecipes.init()` to register dynamic arcane recipes into `ThaumcraftApi.getCraftingRecipes()` with duplicate guards.
- Updated `docs/Stage9-b.md` GAP-2 status/acceptance notes to reflect class+registration closure and remaining runtime scenario limits.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5480` MCP leak lines / `1069` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.165s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- This checkpoint restores dynamic recipe classes and registration only; full end-to-end proof still depends on broader Stage 9-b static recipe/research content population.
- Manual/client Arcane Workbench interaction remains outside current non-GUI validation scope.

### 2026-05-15 — Stage 9-b Arcane Workbench server container baseline

Scope:

- Implemented Arcane Workbench server container flow in `ContainerArcaneWorkbench` with reference-compatible slot layout:
  - output slot (`tile 9`),
  - wand slot (`tile 10`),
  - 3x3 crafting grid (`tile 0..8`),
  - player inventory/hotbar slots.
- Added `SlotLimitedByWand` to constrain wand slot inputs to non-staff `ItemWandCasting`.
- Added `SlotCraftingArcaneWorkbench` to fire crafting events, consume vis costs via wand crafting path, consume grid ingredients, and apply container-item remainders.
- Wired `onCraftMatrixChanged` to mirror reference order: vanilla crafting result first, then arcane result when wand vis cost probe succeeds.
- Added `TileMagicWorkbench` null/`ItemStack.EMPTY` hardening for stack initialization, NBT read/write, and slot mutation paths to prevent null-slot regressions in the container flow.
- Updated `docs/Stage9-b.md` GAP-4 status/evidence to reflect server baseline closure and remaining runtime scenario dependencies.

Validation:

- Initial `./scripts/dev.sh validate --smoke` run failed at `compileJava` due incorrect `SlotLimitedByWand#onTake` override return type (`void` vs `ItemStack`) on Forge 1.12.
- Fixed slot class signature by removing the invalid override and reran validation.
- Final `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5468` MCP leak lines / `1069` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.187s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- This checkpoint implements server container logic only; end-to-end arcane crafting proof still depends on Stage 9-b recipe population and research gate registration (GAP-1/2/6/7).
- Client/manual Arcane Workbench GUI interaction remains excluded from validation scope.

### 2026-05-15 — Stage 9-b arcane matcher method surface baseline

Scope:

- Restored missing `ThaumcraftCraftingManager` arcane workbench matcher methods:
  - `findMatchingArcaneRecipe(IInventory, EntityPlayer)`
  - `findMatchingArcaneRecipeAspects(IInventory, EntityPlayer)`
- Implemented reference-aligned recipe iteration (`ThaumcraftApi.getCraftingRecipes()` + `IArcaneRecipe.matches(...)`) with 1.12-safe empty fallbacks (`ItemStack.EMPTY`, empty `AspectList`) and null guards.
- Updated `docs/Stage9-b.md` GAP-3 to reflect that manager method surface exists while runtime workbench flow remains blocked by GAP-1/2/4.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5437` MCP leak lines / `1063` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.313s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- This checkpoint restores only manager-side query methods; `ContainerArcaneWorkbench` is still placeholder-level and does not consume vis/output via these methods.
- Arcane recipe content registration/research gates are still open (Stage 9-b GAP-1/2/6/7).

### 2026-05-15 — Stage 9-b arcane matcher EMPTY/null compatibility baseline

Scope:

- Fixed Forge 1.12 empty-stack compatibility in `ShapedArcaneRecipe` and `ShapelessArcaneRecipe` without changing public API signatures.
- `ShapedArcaneRecipe.checkMatch` now treats `ItemStack.EMPTY` like an empty slot for blank target cells.
- `ShapelessArcaneRecipe.matches` now skips empty stacks (`null` or `isEmpty()`), not only `null`.
- `checkItemEquals` in both classes now performs null/empty short-circuit checks before item/meta/tag comparisons.
- Updated `docs/Stage9-b.md` GAP-5 status/acceptance notes to mark matcher fix coverage and keep end-to-end runtime crafting validation open.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5437` MCP leak lines / `1063` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.183s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- This checkpoint fixes matcher semantics only; full runtime proof still depends on Stage 9-b arcane recipe registration and research gating population.
- Arcane Workbench GUI/manual crafting paths remain outside current non-GUI validation scope.

### 2026-05-15 — Stage 9-c dynamic runic augment infusion baseline

Scope:

- Added `InfusionRunicAugmentRecipe` port in `thaumcraft.common.lib.crafting` with reference-aligned server behavior: research gate, runic-armor central-item match, dynamic components by current runic charge, `RS.HARDEN` output mutation, dynamic aspect costs, and instability scaling.
- Wired `ConfigRecipes.init()` to register `InfusionRunicAugmentRecipe` into `ThaumcraftApi.getCraftingRecipes()` with a duplicate guard.
- Updated `TileInfusionMatrix.craftingStart` to mirror the reference special-case path and capture dynamic components via `InfusionRunicAugmentRecipe#getComponents(recipeInput)`.
- Updated `docs/Stage9-c.md` GAP-3 status/acceptance/checklist to mark implementation closure and keep runtime scenario verification open.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5435` MCP leak lines / `1063` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.258s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- This checkpoint restores runic augment recipe class/registration/matrix dynamic component wiring, but does not yet include a dedicated runtime infusion scenario asserting per-charge component consumption and repeated hardening attempts.
- Stage 9-c recipe population blockers (GAP-1/2/4) remain open.

### 2026-05-15 — Stage 9-c infusion recipe-type localization keys baseline

Scope:

- Added missing reference localization keys `recipe.type.infusion` and `recipe.type.infusionenchantment` to `src/main/resources/assets/thaumcraft/lang/en_us.lang`.
- Updated `docs/Stage9-c.md` GAP-6 status/acceptance/checklist to reflect that the key-presence part is now covered and only runtime GUI rendering confirmation remains open.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5428` MCP leak lines / `1063` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.349s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- This checkpoint only restores recipe-type localization keys and does not validate live Thaumonomicon page rendering in a client runtime session.
- Broader Stage 9-c recipe/research population blockers remain open.

### 2026-05-15 — Stage 9-c infusion crafted-event inventory source alignment

Scope:

- Aligned infusion completion crafted-event inventory source with 1.7.10 reference usage by switching `TileInfusionMatrix` to `new InventoryFake(this.recipeIngredients)` instead of a central-input-only fake inventory.
- Added a `List<ItemStack>` constructor overload in `InventoryFake` to preserve the reference call pattern without changing public addon API surfaces.
- Updated `docs/Stage9-c.md` GAP-5 status/notes and Stage 9-c closure checklist to reflect the implemented fix and remaining runtime-listener verification gap.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5428` MCP leak lines / `1063` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.102s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- This checkpoint aligns event inventory source to reference (`recipeIngredients`), but does not alter the original behavior where the ingredient list may already be depleted by completion time.
- No dedicated runtime listener assertion for crafted-event inventory contents has been added yet.

### 2026-05-15 — Stage 9 research map scaffold baseline

Scope:

- Added baseline `ConfigResearch.recipes` scaffold (`Map<String, Object>`) to restore the reference-side contract used by Stage 9 research page recipe lookups.
- Updated `ConfigResearch.init()` to clear the recipe map as an explicit pre-registration reset point.
- Updated `docs/Stage9-e.md` analysis text to reflect that the map scaffold now exists while content/category/page registration and recipe-map population are still open.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5428` MCP leak lines / `1063` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.134s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- The scaffold is intentionally empty; no Stage 9 recipe keys are populated yet.
- `ConfigResearch.init()` still does not register categories/research entries/pages.

### 2026-05-15 — Stage 9-e research completion packet progression guardrails

Scope:

- Hardened `PacketPlayerCompleteToServer` server semantics to match reference progression boundaries:
  - prerequisite gate via `ResearchManager.doesPlayerHaveRequisites(...)`,
  - `type=0` secondary-only completion path with aspect-pool costs,
  - `type=1` primary note-creation path instead of direct completion.
- Added `ResearchManager.createResearchNoteForPlayer(...)` plus `getResearchSlot(...)` and `consumeInkFromPlayer(...)` helpers for server-side note creation from paper + scribing tools.
- Restricted sibling research grants to sibling entries that also pass prerequisite checks.
- Updated `docs/Stage9-e.md` GAP-7 status/notes to reflect implemented server guardrails and remaining runtime coverage limits.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5428` MCP leak lines / `1063` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.258s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Client Thaumonomicon interaction code is still baseline-level, so live packet paths are not manually exercised.
- Full Stage 9-e research content/category/page population is still open and required for end-to-end progression validation.

### 2026-05-15 — Stage 5 robe dye recipe registration baseline

Scope:

- Added Forge 1.12 recipe registry wiring for robe dyes via `Thaumcraft#registerRecipes(RegistryEvent.Register<IRecipe>)`.
- Implemented `ConfigRecipes.registerSpecialRecipes(...)` and registered `forge:robearmordye` plus `forge:voidrobearmordye`.
- Kept recipe registration idempotent with an internal one-time guard.
- Updated `docs/Stage5.md` GAP-13 status/details to reflect that recipe logic, armor color NBT support, and registration baseline are now present.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5408` MCP leak lines / `1060` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (` ready-state present.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- This checkpoint registers only the robe/void-robe special dye recipes; it does not implement the full Stage 9 recipe content set.
- Manual/runtime robe dye scenarios remain unverified under current non-GUI constraints.

### 2026-05-15 — Stage 8-b Research Table container polish

Scope:

- Restored `ContainerResearchTable` baseline behavior: `IScribeTools` + `ItemResearchNotes` slot restrictions, reference slot layout, player inventory binding, shift-click routing, and button-id `5` duplicate wiring.
- Added `TileResearchTable.duplicate(EntityPlayer)` server-side path for completed notes: validates required resources, deducts aspect pool cost through capability data, consumes paper/feather, increments note copy metadata in NBT, and produces a duplicate note item.
- Added `bonusAspects` persistence and slot validation hooks in `TileResearchTable` to better match reference data surfaces used by research GUI logic.
- Updated `docs/Stage8-b.md` gap status/evidence for this container-side parity step.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5404` MCP leak lines / `1057` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.276s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Full research-note puzzle and aspect placement/erase interaction parity is still not implemented.
- No-ink/copy GUI paths still need manual runtime verification under client GUI checks.
- Manual GUI runtime checks remain skipped under current constraints.

### 2026-05-15 — Stage 8-b research GUI localization baseline

Scope:

- Added missing research/browser/table language keys from the 1.7.10 reference into `src/main/resources/assets/thaumcraft/lang/en_us.lang`.
- Included the current Stage 8-b baseline set: `tc.researchmissing`, `tc.research.purchase`, `tc.research.short`, `tc.research.getprim`, `tc.research.shortprim`, `tc.research.hasnote`, `tc.research.popup`, `tc.research.copy`, `tc.research_category.BASICS`, `tc.research_category.THAUMATURGY`, `tc.research_category.ALCHEMY`, `tc.research_category.ARTIFICE`, `tc.research_category.GOLEMANCY`, `tc.research_category.ELDRITCH`, `tile.researchtable.noink.0`, `tile.researchtable.noink.1`.
- Updated `docs/Stage8-b.md` GAP-13 and research-table notes to reflect that these baseline keys are now present while font/runtime verification remains open.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5404` MCP leak lines / `1057` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.296s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- This checkpoint only adds localization keys; it does not complete research puzzle/interaction parity.
- Research GUI visual text parity is still open despite baseline key coverage.
- Manual GUI visual/runtime checks remain skipped under current constraints.

### 2026-05-15 — Stage 8-b research GUI font-init baseline

Scope:

- Added reference-style `galFontRenderer` fields in `GuiResearchTable` and `GuiResearchBrowser`.
- Initialized `galFontRenderer` in `initGui()` using the client `FontRenderer` with draw-time null fallback, mirroring the 1.7.10 baseline where research GUIs keep a dedicated font renderer handle.
- Updated `docs/Stage8-b.md` GAP-13 notes to reflect that language keys and baseline font-init wiring are in place while visual/runtime verification remains open.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5408` MCP leak lines / `1060` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.198s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- This checkpoint wires font initialization only; it does not port full research browser/table interaction parity.
- Research GUI visual parity still needs manual/client verification.

### 2026-05-15 — Stage 8-b research support texture baseline

Scope:

- Copied additional reference research GUI textures: `arcane.png`, `gui_researchback.png`, `gui_researchbackeldritch.png`, `hex1.png`, `hex2.png`, `hud.png`.
- Copied direct reference research misc textures: `parchment3.png`, `script.png`, and `script.png.mcmeta`.
- Updated `docs/Stage8-b.md` GAP-12 and research-GUI gap notes to reflect the expanded resource baseline and remaining parity wiring limits.

Validation:

- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5380` MCP leak lines / `1057` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.147s)!`.
- Crash report scan under `run/` returned no files.

Remaining limits:

- Research Table and Thaumonomicon advanced behavior is still baseline-only; texture presence does not imply full rendering/interaction parity.
- Manual GUI visual checks remain skipped under current `DISPLAY=` / user-interaction constraints.

### 2026-05-15 — Stage 8-b Research Table and Thaumonomicon GUI baseline

Scope:

- Added baseline `GuiResearchTable` backed by `ContainerResearchTable`.
- Added baseline `GuiResearchBrowser`, `GuiResearchRecipe`, and `GuiResearchPopup` screens.
- Routed client GUI ID `10` (`GUI_RESEARCH_TABLE`) to tile-backed `GuiResearchTable` and GUI ID `12` (`GUI_THAUMONOMICON`) to `GuiResearchBrowser`.
- Copied original research GUI textures used by these baseline screens: `guiresearchtable2.png`, `gui_research.png`, `gui_researchbook.png`, `gui_researchbook_overlay.png`.
- Updated `docs/Stage8-b.md` with the implemented baselines and remaining research-GUI parity limits.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5380` MCP leak lines / `1057` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.198s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Research Table note puzzle interactions, aspect/rune flows, and no-ink/copy states are not yet ported.
- Thaumonomicon category/node/recipe/popup behavior is only baseline and not parity-complete.
- Extended research GUI texture/lang coverage and manual visual parity remain open.

### 2026-05-15 — Stage 8-b Golem GUI baseline

Scope:

- Added `GuiGolem` backed by `ContainerGolem`.
- Ported a texture-backed Golem GUI baseline: original `guigolem.png`, inventory/filter panel rendering, core/sort toggle rendering, and color/scroll click handling mapped to the reference button-id scheme.
- Routed client GUI ID `0` through `ClientProxy#getClientGuiElement` using entity id lookup and `EntityGolemBase` type validation.
- Updated `ContainerGolem` with reference-style scroll state (`currentScroll`, `maxScroll`), refreshable scrolled ghost-slot binding, and `enchantItem` button handling for toggle/scroll/color interactions.
- Copied original `guigolem.png`.
- Updated `docs/Stage8-b.md` with the implemented baseline and remaining client/manual limits.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5349` MCP leak lines / `1052` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.205s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Golem GUI open/toggle/scroll/color scenarios were not manually observed.
- Exact visual parity for model preview and fluid-slot tooltip behavior remains open.
- Full core-specific golem interaction matrix remains tied to manual runtime coverage.

### 2026-05-15 — Stage 8-b Pech GUI baseline

Scope:

- Added `GuiPech` backed by `ContainerPech`.
- Ported a texture-backed Pech GUI baseline: original `gui_pech.png`, reference window size (`175x232`), valued-input/output-empty trade-button visibility logic, and trade click handling through the existing container button path.
- Routed client GUI ID `1` through `ClientProxy#getClientGuiElement` using entity id lookup and `EntityPech` type validation.
- Copied original `gui_pech.png`.
- Updated `docs/Stage8-b.md` with the implemented baseline and remaining client/manual limits.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5333` MCP leak lines / `1052` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.167s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Pech GUI open/trade scenarios were not manually observed.
- Exact visual/button UX parity still needs client inspection.
- Full tamed-Pech trade loop validation remains tied to manual runtime coverage.

### 2026-05-15 — Stage 8-b Traveling Trunk GUI baseline

Scope:

- Added `GuiTravelingTrunk` backed by `ContainerTravelingTrunk`.
- Ported a texture-backed Traveling Trunk GUI baseline: original `guitrunkbase.png`, health bar, stay icon, and stay/move toggle click handling through the existing container button path.
- Routed client GUI ID `2` through `ClientProxy#getClientGuiElement` using entity id lookup and `EntityTravelingTrunk` type validation.
- Added trunk GUI language keys `entity.trunk.guiname`, `entity.trunk.move`, and `entity.trunk.stay`.
- Updated `ContainerTravelingTrunk` to handle button id `1` so stay/move toggles server-side.
- Copied original `guitrunkbase.png`.
- Updated `docs/Stage8-b.md` with the implemented baseline and remaining client/manual limits.

Validation:

- `./scripts/dev.sh compileJava` — failed once due to a bad `SoundEvents` import path in `GuiTravelingTrunk`; fixed in-checkpoint.
- `./scripts/dev.sh compileJava` — passed after the import fix.
- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5316` MCP leak lines / `1051` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.294s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Traveling Trunk open/toggle/close scenarios were not manually observed.
- Exact owner-label text and stay-toggle UX parity still need client inspection.
- Runtime verification across trunk upgrade variants still needs manual coverage.

### 2026-05-15 — Stage 8-b Magic Box and Spa GUI baseline

Scope:

- Added `GuiMagicBox` backed by `ContainerMagicBox`.
- Ported a chest-style Magic Box GUI baseline using `textures/gui/container/generic_54.png`.
- Added `GuiSpa` backed by `ContainerSpa`.
- Ported a texture-backed Spa GUI baseline: original `gui_spa.png`, fluid tooltip/level bar rendering from tile tank state, and mix-toggle icon/tooltip/click handling.
- Routed client GUI IDs `18` and `19` through `ClientProxy#getClientGuiElement`.
- Restored baseline server/container support needed by these screens: Magic Box chest/player slot binding and shift-click routing; Spa bath-salts slot, mix-toggle button path, and minimal persisted `TileSpa` inventory/tank/capability state.
- Added Spa open routing in `BlockStoneDevice` for active stone-device meta `12`.
- Copied original `gui_spa.png` and added `thaumcraft.spa` plus `text.spa.mix.true/false` language keys.
- Updated `docs/Stage8-b.md` with the implemented baseline and remaining client/manual/gameplay limits.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5292` MCP leak lines / `1047` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.175s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Magic Box and Spa open scenarios were not manually observed.
- Exact visual layout and hover/click behavior still need client inspection.
- Magic Box linked-storage behavior and Spa world-fluid automation remain outside this GUI checkpoint.

### 2026-05-15 — Stage 8-b Deconstruction and Alchemy Furnace GUI baseline

Scope:

- Added `GuiDeconstructionTable` backed by `ContainerDeconstructionTable`.
- Ported the original Deconstruction GUI baseline: `gui_decontable.png`, breaktime progress bar, aspect icon/tooltip, and aspect claim clicks sent through the existing container button path.
- Added `GuiAlchemyFurnace` backed by `ContainerAlchemyFurnace`.
- Ported the original Alchemy Furnace GUI baseline: `gui_alchemyfurnace.png`, burn, cook-progress, vis-content, and overlay bars.
- Routed client GUI IDs `8` and `9` through `ClientProxy#getClientGuiElement` with matching tile type checks.
- Restored the Deconstruction Table input/player slot layout, aspect-bearing input filter, `breaktime` property sync, shift-click behavior, and server aspect-claim handling.
- Copied original `gui_decontable.png` and `gui_alchemyfurnace.png`.
- Updated `docs/Stage8-b.md` with the implemented baseline and remaining client/manual/gameplay limits.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5224` MCP leak lines / `1042` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.208s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Deconstruction Table and Alchemy Furnace block-open scenarios were not manually observed.
- Exact visual layout and hover/click behavior still need client inspection.
- Full Deconstruction item-breaking behavior remains outside this GUI checkpoint.
- Magic Box and Spa GUI IDs are still open under Stage 8-b.

### 2026-05-15 — Stage 8-b item GUI texture baseline

Scope:

- Replaced Focus Pouch, Hand Mirror, and Hover Harness placeholder backgrounds with original texture-backed GUI rendering.
- Moved Focus Pouch slot coordinates to the reference tall layout so the slots align with `gui_focuspouch.png`.
- Disabled hotbar-key swaps in all three screens to preserve the reference `func_146983_a` behavior.
- Copied original `gui_focuspouch.png`, `guihandmirror.png`, and `guihoverharness.png`.
- Updated `docs/Stage8-b.md` with the implemented baseline and remaining item-movement validation limits.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5190` MCP leak lines / `1040` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.326s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Manual open/close and item movement scenarios were not observed.
- Backing-item/hotbar-key duplication checks still need client-runtime coverage.
- Hand Mirror remote transport and Hover Harness jar persistence were not manually exercised.

### 2026-05-15 — Stage 8-b Focal Manipulator GUI baseline

Scope:

- Added `GuiFocalManipulator` backed by `ContainerFocalManipulator`.
- Ported a texture-backed baseline: possible focus upgrade icons, selected upgrade cost/progress display, hover text, local selection, and start clicks sent through the existing container `enchantItem` path.
- Routed client GUI ID `20` through `ClientProxy#getClientGuiElement` with a `TileFocalManipulator` type check.
- Copied the original `gui_wandtable.png` and added the focus upgrade plus `wandtable.text1..3` language keys used by the screen.
- Updated `docs/Stage8-b.md` with the implemented baseline and remaining client/manual validation limits.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5187` MCP leak lines / `1040` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.177s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Focal Manipulator open/focus insertion/start scenarios were not manually observed.
- Exact visual layout and sparkle animation parity remain open.
- Stage 9/content availability can still affect visible upgrade data through focus item coverage.

### 2026-05-15 — Stage 8-b Thaumatorium GUI baseline

Scope:

- Added `GuiThaumatorium` backed by `ContainerThaumatorium`.
- Ported the original GUI baseline: recipe index tracking, output rendering, aspect icon/progress rendering, recipe/aspect scroll controls, and selection click handling through `sendEnchantPacket`.
- Routed client GUI ID `3` through `ClientProxy#getClientGuiElement` with a `TileThaumatorium` type check.
- Copied the original `gui_thaumatorium.png` and the original `textures/aspects/**` icon set from `thaumcraft_src/assets/thaumcraft/`.
- Updated `docs/Stage8-b.md` with the implemented baseline and remaining client/manual validation limits.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5173` MCP leak lines / `1039` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.236s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Thaumatorium lower/top block open was not manually observed.
- Empty and populated catalyst scenarios still need client/runtime inspection.
- Stage 9 recipe/content registration can still affect available recipe lists and output behavior.

### 2026-05-15 — Stage 8-b Arcane Bore GUI baseline

Scope:

- Added `GuiArcaneBore` backed by `ContainerArcaneBore`.
- Ported the original bore GUI baseline: `176x141` background, nearly-broken pickaxe overlay, and width/speed/property text from `TileArcaneBore` state.
- Routed client GUI ID `15` through `ClientProxy#getClientGuiElement` with a `TileArcaneBore` type check.
- Copied the original `gui_arcanebore.png` from `thaumcraft_src/assets/thaumcraft/textures/gui/` into the port resource tree.
- Updated `docs/Stage8-b.md` with the implemented baseline and remaining client/manual validation limits.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5149` MCP leak lines / `1030` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.266s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Arcane Bore right-click/open was not manually observed.
- Visual parity of the slot layout and text overlay still needs client inspection.
- Bore renderer/state animation parity remains outside this GUI checkpoint.

### 2026-05-15 — Stage 8-b Arcane Workbench GUI baseline

Scope:

- Added a minimal `GuiArcaneWorkbench` client screen backed by `ContainerArcaneWorkbench`.
- Routed client GUI ID `13` through `ClientProxy#getClientGuiElement` with a `TileArcaneWorkbench` type check.
- Copied the original `gui_arcaneworkbench.png` from `thaumcraft_src/assets/thaumcraft/textures/gui/` into the port resource tree.
- Updated `docs/Stage8-b.md` to record this as a baseline, not full GUI parity.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh validate --smoke` — passed: status, compile, tests `10/10`, jar, check-jar summary `5134` MCP leak lines / `1028` unique leaks, and server smoke.
- `run/smoke-server.log` evidence: `Registering entities`; `Forge Mod Loader has successfully loaded 6 mods`; `Done (1.205s)!`.
- Crash report scan under `run/` returned no files.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY=` and GUI/graphics/user-interactive validation is excluded.

Remaining limits:

- Arcane Workbench right-click/open was not manually observed.
- The screen currently provides the original background texture baseline only; wand vis, recipe aspect cost display, and full visual parity remain open.
- Stage 9 recipe/content availability can still affect workbench output behavior.

### 2026-05-14 — Stage 6/7 BlockLoot urn/crate path

Scope:

- Ported `BlockLoot` and `BlockLootItem` for the three reference rarity metas.
- Registered `blockLootUrn` and `blockLootCrate` with item blocks.
- Copied original urn/crate textures from `thaumcraft_src/assets/thaumcraft/textures/blocks/`.
- Added simple Forge 1.12.2 blockstate/model fallbacks so the blocks resolve as resources until Stage 8 renderer parity work.
- Moved shared loot reward generation into `Utils.generateLoot(...)`.
- Replaced the Cultist Portal stage 0 vanilla chest placeholder with `blockLootCrate`.
- Replaced Outer Lands `GenCommon`/`GenNestRoom` urn/crate placeholders with `blockLootUrn`/`blockLootCrate`.

Validation:

- `./scripts/dev.sh compileJava` — initially failed because `BlockLoot.getSubBlocks(...)` used a non-existent 1.12.2 `Block.isInCreativeTab(...)`; fixed during the checkpoint.
- `./scripts/dev.sh compileJava` — passed after the fix.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- This closes the direct vanilla chest/stone substitution for the shared urn/crate path only.
- Full loot-table parity still depends on completing/populating the broader loot registration tables currently deferred to Stage 9/content work.
- Stage 6 Cultist Portal stage progression/reward scenario was not manually run by user instruction.
- Stage 7 full Outer Lands room traversal, `blockSlabStone`, other room templates, and worldgen runtime evidence remain open.
- GUI/client visual validation for the new model fallbacks was not run.

### 2026-05-14 — Stage 7 cosmetic stone slab room block

Scope:

- Ported the reference `blockCosmeticSlabStone`/`blockCosmeticDoubleSlabStone` pair as Forge 1.12.2 `BlockSlab` implementations.
- Registered the single and double slab blocks; registered the `ItemSlab` bridge for the single slab item.
- Replaced `GenLibraryRoom`'s vanilla `Blocks.DOUBLE_STONE_SLAB` helper with `ConfigBlocks.blockSlabStone.getStateFromMeta(meta)`.
- Copied the reference slab source textures `arcane_stone.png` and `es_1.png` from `thaumcraft_src/assets/thaumcraft/textures/blocks/`.
- Added temporary Forge 1.12.2 blockstate/model fallbacks for arcane/eldritch bottom, top, and double slab states.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- Direct `blockSlabStone` room-block substitution is closed for `GenLibraryRoom`.
- Full Outer Lands room traversal/manual generation validation remains skipped by user instruction.
- Other Stage 7 room/worldgen placeholders still need separate reference audits.
- Client visual/model inspection was not run; JSONs are resource fallbacks, not a renderer parity claim.

### 2026-05-14 — Stage 7 Eldritch room block metadata contract

Scope:

- Expanded `BlockEldritch` from the current `0..4` meta clamp to the reference `0..10` meta range used by Outer Lands rooms.
- Restored server-side tile entity creation for metas `0` altar, `1` obelisk, `3` cap, `8` lock, `9` crab spawner, and `10` trap.
- Restored reference-like hardness, resistance, light, drop, XP, and no-creature-spawn behavior for the expanded metas.
- Added temporary resource fallbacks for `blockeldritch` meta variants and copied the matching original textures from `thaumcraft_src/assets/thaumcraft/textures/blocks/`.

Validation:

- `./scripts/dev.sh compileJava` — initially failed on a duplicate `damageDropped(...)` method left from the current implementation; fixed during the checkpoint.
- `./scripts/dev.sh compileJava` — passed after the fix.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- This closes the metadata/TE instantiation contract needed by generated room blocks, but not full `TileEldritchAltar`, `TileEldritchLock`, `TileEldritchCrabSpawner`, or `TileEldritchTrap` behavioral parity.
- Renderer parity for altar/obelisk/cap/lock/crab spawner/trap remains Stage 8 work; JSONs are non-GUI resource fallbacks only.
- Full Outer Lands room traversal/manual generation validation remains skipped by user instruction.

### 2026-05-14 — Stage 7 safe Outer Lands teleporter placement

Scope:

- Replaced fixed `y=60` placement in `TeleporterThaumcraft` with reference-like search for the nearest `ConfigBlocks.blockEldritchPortal` within 128 blocks.
- Added per-teleporter portal-position caching through the inherited Forge 1.12.2 `destinationCoordinateCache`.
- Zeroed entity velocity on placement and moved arrivals beside the found portal instead of inside an arbitrary fixed column.
- Added a safe top-position fallback when no existing portal can be found, avoiding silent placement at a hardcoded Y coordinate.
- Added null target-world guards in `BlockEldritchPortal` before calling `changeDimension(...)`.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- Safe teleporter placement is advanced, but GAP-3 is not fully closed until trigger ownership and manual portal traversal evidence are handled.
- Portal generation/maze placement is not proven by this checkpoint.

### 2026-05-14 — Stage 7 Eldritch portal trigger ownership

Scope:

- Ported `TileEldritchPortal` to a tickable server-side trigger matching the original ownership model.
- Added a 5-tick player scan around the portal block, mounted/ridden player guards, and the original 100-tick player portal cooldown behavior.
- Moved dimension transfer out of `BlockEldritchPortal` collision handling and into the tile entity.
- Preserved the safe `TeleporterThaumcraft` placement/search path from the previous checkpoint for both Outer Lands entry and Overworld return.
- Granted `ENTEROUTER` through the current `ResearchManager` when a player enters the Outer Lands for the first time.
- Made the portal block pass-through, non-replaceable, and unbreakable, aligning it with the original non-colliding portal block contract.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- Runtime portal traversal was not manually run because user-driven GUI/client control is excluded from this durable goal.
- Full runtime proof still requires targeted in-world/manual scenarios where noted below.
- GAP-3 is advanced but not fully closed because arrival beside a generated `GenPortal` room still depends on Stage 7 ring/maze/room generation validation.

### 2026-05-14 — Stage 7 Eldritch ring and maze bootstrap

Scope:

- Replaced the placeholder Overworld `WorldGenEldritchRing` obsidian-circle-plus-portal layout with a reference-like support pad, altar, cap, obelisk, and banner layout.
- Added reference-like surface validation for ring spawn points and `MazeHandler.mazesInRange(...)` overlap checks using ring `chunkX`, `chunkZ`, `width`, and `height`.
- Moved ring maze bootstrap back into the surface structure branch with odd random `11..21` maze dimensions and `MazeThread(chunkX, chunkZ, width, height, random.nextLong())`.
- Restored reference-like sea-level/top-height gating and dark/eerie aura node creation above the ring altar.
- Added minimal persistent `TileEldritchAltar` state for `spawner`, `spawntype`, `spawnedClerics`, `open`, and `eyes`.
- Added minimal persistent `TileBanner.facing` state so generated ring banners can keep their orientation.

Validation:

- `./scripts/dev.sh compileJava` — initially failed because ring-local `bx`/`bz` variables shadowed later hilltop variables in `generateStructures(...)`; fixed during the checkpoint.
- `./scripts/dev.sh compileJava` — passed after the fix.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- Full `TileEldritchAltar` cultist/guardian spawning and portal-opening behavior was not ported by this ring checkpoint; the following altar checkpoint handles the spawner part while portal opening remains open.
- Runtime ring discovery, `labyrinth.dat` save evidence, and portal traversal were not manually run because user-driven GUI/client control is excluded from this durable goal
- Hilltop altar, mound/barrow, normal/spider Greatwood, and broader surface worldgen parity remain open Stage 7 work.

### 2026-05-14 — Stage 7 Eldritch altar spawner lifecycle

Scope:

- Ported the server-side `TileEldritchAltar` tick loop for generated altar spawners.
- Restored persistent altar fields for `spawner`, `spawnedClerics`, `spawntype`, `eyes`, and `open`.
- Added ritual cleric spawning for spawn type `0`, including ritualist flagging and altar home positions.
- Added follow-up guard spawning for spawn type `0` once ritual clerics exist, with nearby cultist caps.
- Added Eldritch Guardian spawning for spawn type `1`, with home positions and persistence.
- Added the reference-like `checkForMaze()` helper that starts a new odd-sized maze thread when no maze exists around the altar.

Validation:

- `./scripts/dev.sh compileJava` — initially failed because the first 1.12.2 adaptation used unavailable `World.doesBlockHaveSolidTopSurface(...)` and typed the spawn helper as `EntityLiving`, which does not expose `setHomePosAndDistance(...)`; fixed during the checkpoint.
- `./scripts/dev.sh compileJava` — passed after switching to `IBlockState.isSideSolid(...)` and `EntityCreature`.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- Portal-opening behavior that consumes altar eyes/open state is still not implemented.
- Cultist/guardian spawning has not been observed in a runtime world because smoke/manual runtime validation remains excluded as user-driven client control.
- Stage 6 entity combat behavior can still affect full in-world altar scenario parity.

### 2026-05-14 — Stage 6 Cultist Portal banner facing

Scope:

- Removed the `TileBanner.setFacing()` TODO from `EntityCultistPortal` now that `TileBanner` has persistent facing state.
- Ported the reference stage-0 banner facing mapping for the four generated portal banners.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- Cultist Portal stage progression has not been observed in runtime/manual validation because user-driven scenarios are excluded.
- This only closes the direct banner-facing TODO; full boss/minion/drop/loot distribution parity remains open.

### 2026-05-14 — Stage 7 Eldritch crab spawner and trap server ticks

Scope:

- Ported `TileEldritchCrabSpawner` from its facing-only shell to the reference server tick loop.
- Restored the crab spawner's randomized startup tick, countdown/reset cadence, 16-block player activation check, 32-block nearby-crab cap, warning block event, fizz/gore sounds, and facing-based `EntityEldritchCrab` spawn with outward motion.
- Kept `facing` under the original NBT key and moved the tile to `TileThaumcraft` custom NBT like the reference.
- Ported `TileEldritchTrap` from an empty shell to the reference server tick pulse: 10-34 tick reset, 3-block player check, 2 magic damage, and 50% temporary warp application.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- Client vent particles and trap zap packet visuals are still Stage 8 FX work; `PacketFXBlockZap` currently has no payload constructor/handler.
- Spawn/trap behavior has not been observed in an Outer Lands runtime traversal because user-driven client/manual scenarios are excluded.
- `TileEldritchLock`, boss-room lifecycle, full room traversal, and broader room-template audit remain open Stage 7 work.

### 2026-05-14 — Stage 7 hilltop wisp altar template

Scope:

- Replaced the simplified `WorldGenHilltopStones` stonebrick-ring/empty-chest placeholder with the reference-like hilltop altar structure.
- Restored surface validation through `LocationIsValidSpawn(...)`, including high-altitude gating and air/surface-cover handling.
- Restored the seven-by-seven obsidian/obsidian-totem footprint, perimeter columns, optional vines, dungeon loot chest, and central mob spawner.
- Mapped the original `Thaumcraft.Wisp` spawner target deliberately to the 1.12.2 entity registry id `thaumcraft:wisp`.
- Updated the surface structure call site so the companion aura node is only created when the hilltop altar successfully generates.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- `WorldGenMound` is still the simplified placeholder and remains a Stage 7 GAP-11 blocker.
- The full reference `generateSurface(...)` control flow/chance semantics remain under GAP-1, so this checkpoint restores hilltop contents but not the entire surface worldgen pipeline.
- Hilltop generation has not been observed in a fresh runtime world because user-driven client/manual scenarios are excluded.

### 2026-05-14 — Stage 7 spider Greatwood contents

Scope:

- Added the reference-style `WorldGenGreatwoodTrees.generate(..., boolean spiders)` path.
- Restored the default `random.nextInt(8) == 0` spider-variant chance.
- Restored the variant contents: cave-spider spawner under the trunk, up to 50 webs placed adjacent to Greatwood logs/leaves, and a dungeon-loot chest below the spawner.
- Mapped the original `CaveSpider` spawner target to the 1.12.2 entity registry id `minecraft:cave_spider`.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- The full reference vegetation pipeline is still open under GAP-1, so Greatwood/Silverwood natural placement rates and allowed biome coverage are not closed by this checkpoint.
- Silverwood parameter/chance parity and ore placement parity remain open Stage 7 work.
- Spider Greatwood has not been observed in a fresh runtime world because user-driven client/manual scenarios are excluded.

### 2026-05-14 — Stage 7 surface vegetation and wild aura baseline

Scope:

- Updated `ThaumcraftWorldGenerator` to sample biome data from the chunk center, matching the reference surface-generation entry point more closely.
- Restored a `generateVegetation(...)` path with the reference outer Silverwood `random.nextInt(60) == 3` and Greatwood `random.nextInt(25) == 7` chance gates.
- Allowed Greatwood/Silverwood generation outside Magical Forest when current `BiomeHandler`/biome checks allow it, rather than only calling tree generation for exact Magical Forest chunks.
- Switched Silverwood worldgen to the reference `WorldGenSilverwoodTrees(false, 7, 4)` constructor parameters and removed the extra current-port 5% inner chance.
- Added wild aura node generation gated by `Config.genAura`/`regenAura` and `Config.nodeRarity`, using `createRandomNodeAt(..., false, false, false)` independently of Silverwood trees.
- Restored flat-world skip behavior for tree/structure generation and reintroduced dimension blacklist level distinction for normal surface tree/structure generation versus ore/aura generation.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- GAP-1 is still not closed: Nether node/totem generation, scattered-feature structure nodes, `newGen`/regen marker parity, full ore placement parity, and runtime evidence remain open.
- Wild aura node placement has not been observed in a fresh runtime world because user-driven client/manual scenarios are excluded.
- The simplified mound/barrow structure remains open under GAP-11.

### 2026-05-14 — Stage 7 structure nodes, totems, and Nether aura baseline

Scope:

- Added a `structureNode` cache to `ThaumcraftWorldGenerator` and restored scattered-feature aura node placement through `MapGenScatteredFeature#getNearestStructurePos(...)`.
- Threaded `auraGen` through scattered-feature, wild-node, structure-node, and totem generation so successful aura-source placement suppresses later duplicate node paths in the same chunk.
- Reintroduced totem pillar generation with the reference substrate checks, snow/tallgrass clearance, obsidian-totem base, obsidian-tile shaft, and node-stone cap/random mid-column node placement.
- Stopped skipping dimension `-1` in the top-level generator and added a Nether path for wild aura nodes plus the reference Nether-specific totem top-Y branch when structure generation is enabled.
- Kept End generation skipped and moved the wild-node fallback height from sea level to `WorldProvider#getAverageGroundLevel()`, matching the reference source more closely.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- GAP-1 is still not closed: `newGen`/regen chunk dirty-marker parity, full ore placement parity, flower placement parity, biome blacklist edge cases, and runtime generation evidence remain open.
- Nether aura/totem and scattered-feature node placement have not been observed in a fresh runtime world because user-driven client/manual scenarios are excluded.
- The simplified mound/barrow structure remains open under GAP-11.

### 2026-05-14 — Stage 7 ore and flower placement parity

Scope:

- Replaced the current generic ore-vein calls in `ThaumcraftWorldGenerator` with a reference-like `generateOres(...)` path.
- Restored cinnabar to 18 single-block stone replacement attempts in the lower fifth of world height.
- Restored amber to 20 single-block stone replacement attempts near terrain height minus up to 24 blocks.
- Restored infused ore to eight six-block `WorldGenMinable` veins with a one-in-three chance to select the primal meta from the local biome aspect.
- Made ore generation skip biome blacklist levels `0` and `2`, matching the original `generateOres(...)` branch.
- Restored the humid-sand flower generation branch in `generateVegetation(...)` and added the exact-position `generateFlowers(...)` overload used by the reference generator.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- GAP-1 is still not closed: `newGen`/regen chunk dirty-marker parity, broader biome blacklist/runtime edge cases, and runtime generation evidence remain open.
- Ore and flower placement have not been observed in a fresh runtime world because user-driven client/manual scenarios are excluded.
- The simplified mound/barrow structure remains open under GAP-11.

### 2026-05-14 — Stage 7 mound/barrow functional layout

Scope:

- Replaced the small procedural `WorldGenMound` placeholder with a bounded 19x19 barrow generator using the reference validation points.
- Added a buried cobblestone/mossy-cobblestone chamber, dirt/grass mound shell, and open central node handoff position.
- Placed loot urn/crate blocks at the reference offsets with matching meta probabilities and crate/urn split.
- Added a dungeon-loot-table chest at the reference chest offset, including the original trapped-chest/TNT chance.
- Configured skeleton and zombie mob spawners at the reference offsets.
- Reworked `ThaumcraftWorldGenerator.generateStructures(...)` to use the reference-like mutually exclusive mound/ring/hilltop chance ordering from the shared `getHeight(...) - 9` branch instead of the old independent Magical Forest/Taint-only mound chance.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- The mound shell is a compact functional equivalent rather than the exact 2,500-line fixed block dump from the 1.7.10 class.
- Mound/barrow generation has not been observed in a fresh runtime world because user-driven client/manual scenarios are excluded.
- GAP-1 still has `newGen`/regen chunk dirty-marker parity and broader runtime edge cases open.

### 2026-05-14 — Stage 7 Eldritch lock boss trigger baseline

Scope:

- Restored `BlockEldritch` meta `8` activation with `ItemEldritchObject` meta `2`, including lock countdown start, block update, item consumption outside creative mode, and runic shield charge sound.
- Changed `TileEldritchLock` to the same `TileThaumcraft` custom NBT/update path used by the original tile and persisted/synced `facing` plus `count`.
- Restored the server-side 100-tick pump countdown, ice completion sound, nearby airy-door clearing, and lock removal.
- Wired `BossMapData` load/create/update and the original `bossCount % 4` boss selection with the 25% extra-count chance.
- Spawned Warden, Golem, Cultist Portal, and five Taintacle/TaintacleGiant boss patterns at reference-like room anchors, including Warden/Golem initial spawn hooks.

Validation:

- `./scripts/dev.sh compileJava` — initially passed after removing bad imports/redundant direct home calls, then passed again after switching the lock to `TileThaumcraft` and refining spawn anchors.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- Full reference boss-room block mutation is not ported: obelisk/trap/urn/crate scatter, cultist-room decoration, taint/biome patching, and sparkle packets remain open.
- Boss-room activation and save/reload progression have not been observed in a fresh runtime world because user-driven client/manual scenarios are excluded.
- Stage 8 client lock sparkle/render parity remains open.

### 2026-05-14 — Stage 7 Eldritch altar eye and portal activation

Scope:

- Restored `BlockEldritch` meta `0` activation with `ItemEldritchObject` meta `0` while not sneaking.
- Restored altar eye insertion: increments `TileEldritchAltar.eyes`, consumes the eye item, syncs the tile, plays the crystal sound, and calls `checkForMaze()`.
- Restored the reference guardian-spawner transition when adding the third and fourth eyes by setting altar `spawner` and `spawntype = 1`.
- Made `TileEldritchAltar` wand-activatable for the portal-opening path.
- Restored the portal-opening gates: `OCULUS` research, exactly four eyes, not already open, dark `TileNode` above the altar, existing maze from `checkForMaze()`, and six primal aspects at 100 vis each through `consumeAllVisCrafting(...)`.
- On success, sets altar `open`, removes the dark node, places `blockEldritchPortal` above the altar, syncs the tile, and plays the wand sound.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- Generated ring activation has not been observed in a fresh runtime world because user-driven client/manual scenarios are excluded.
- The async case where `checkForMaze()` has just started a `MazeThread` still needs runtime/save validation before GAP-2/GAP-3 can close.

### 2026-05-14 — Stage 7 retrogen newGen marker execution

Scope:

- Added `ThaumcraftWorldGenerator.worldGeneration(Random, int, int, World, boolean newGen)` as the reference-style fresh/regen entry point.
- Routed Forge fresh world generation through `worldGeneration(..., true)`.
- Routed queued chunk regeneration through `worldGeneration(..., false)` from `ServerTickEventsFML`.
- Restored fresh-vs-regen gates for aura, structures, trees, cinnabar, amber, infused stone, and Nether branches as `Config.genX && (newGen || Config.regenX)`.
- Marked non-fresh generated chunks dirty after the generation pass.
- Avoided duplicate `ChunkLoc` queue entries when the same missing-marker chunk loads repeatedly before the queue drains.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- Fresh-world and retrogen runtime scenarios have not been observed because user-driven client/manual scenarios are excluded.
- Broader biome blacklist/runtime edge cases remain open until generation can be observed in-world.

### 2026-05-14 — Stage 7 key room arch and guardian count parity

Scope:

- Restored the reference key-room inner wall/arch block selection so default walls use `ROCK`, side arch detail uses `STONE_NOSPAWN`, and only the center arch slot remains open.
- Restored key-room guardian count to two base guardians, plus one on Normal or two on Hard.
- Restored the HARD-mode champion guardian path by making the guardian count reach four.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- Key-room generation has not been observed in a fresh runtime Outer Lands maze because user-driven client/manual scenarios are excluded.
- Full portal/passage/nest/library/boss room traversal remains unvalidated, and boss-room decorative block mutation remains open.

### 2026-05-14 — Stage 7 boss room mutation parity

Scope:

- Restored the reference Warden boss-room mutations: offset obelisks/caps, urn scatter, center traps/cosmetic supports, and the offset stair/crystal pedestal before spawning the Warden.
- Restored the Golem room mutations: three obelisks/caps, central pedestal, and corner-biased urn/crate scatter before spawning the Golem.
- Restored the Cultist room mutations: randomized eldritch floor pattern and the perimeter pillar/slab layout before spawning the Cultist Portal.
- Restored the Taint room mutations: 25x25 taint biome patching, taint fibre growth on exposed air, taint block scatter, and central taint crust before spawning the five taint bosses.
- Kept the existing reference-like boss spawn anchors and `BossMapData` selection flow from the prior lock checkpoint.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- Boss-room activation, room mutation, and save/reload progression have not been observed in a fresh runtime world because user-driven client/manual scenarios are excluded.
- Reference sparkle packets while clearing nearby airy door blocks remain Stage 8/client FX work.

### 2026-05-15 — Stage 6 Pech trade output generation

Scope:

- Restored the server-side Pech trade-roll button path through `ContainerPech.enchantItem(..., 0)`.
- Added current-port Pech trade tables split by Pech type, mapping reference outputs to available 1.12.2 items/blocks where direct equivalents exist.
- Restored value splitting, one-item offers from the Pech carried pack, high-value shared loot offers, one-input consumption after a roll, de-tame chance with Pech trade sound, and `PechDrop` tagging for unclaimed GUI drops.

Validation:

- `./scripts/dev.sh compileJava` — initially failed because `Items.ENCHANTED_BOOK` is typed as `Item` in 1.12.2; fixed by using `ItemEnchantedBook.getEnchantedItemStack(...)`, then passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- `GuiPech` is still absent because `ClientProxy` returns `null` for `GUI_PECH`; player-driven GUI validation remains Phase 8/client work.
- Original potion metadata and candle trade outputs do not have direct current-port equivalents in this branch and remain documented content dependencies.
- Manual Pech trade interaction, output extraction, and save/reload scenarios remain unrun because user-driven manual validation is excluded.

### 2026-05-15 — Stage 6 Pech spawn variants

Scope:

- Added `EntityPech.onInitialSpawn(...)` to restore random mainhand equipment selection from the reference spawn path.
- Restored Pech-focus wand setup with starting primal vis and type `1`, bow setup with type `2`, melee/tool/fishing-rod equipment possibilities, reduced wand drop chance, difficulty-based enchantment for non-wand gear, pickup-loot chance, and combat-task refresh after spawn setup.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- Type-specific Pech display-name localization is restored in the Pech type-name checkpoint below.
- Spawn variants have not been observed in a runtime world because user-driven manual scenarios are excluded.
- Pech group anger/trade runtime scenarios remain separate Stage 6 work.

### 2026-05-15 — Stage 6 Pech group anger

Scope:

- Restored player-damage group retaliation in `EntityPech.attackEntityFrom(...)` for nearby Pechs in the reference search volume.
- Added `EntityPech.becomeAngryAt(...)` to set revenge and attack targets, reset taming, refresh combat AI, apply the reference anger timer, and emit Pech charge status/sound when entering anger.
- Updated `EntityPech.onUpdate()` so angry Pechs reacquire their revenge target while anger remains and clear player targeting when the timer expires.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- Pech group anger, charge sound/status, and anger expiry have not been observed in a runtime world because user-driven manual scenarios are excluded.
- Client angry particle/status visual handling remains Phase 8/client work if reference visual parity is required beyond the emitted status byte.

### 2026-05-15 — Stage 6 Pech type names

Scope:

- Restored `EntityPech.getName()` to use the reference `PechType` name switch while preserving custom names.
- Added original English Pech type strings for default, mage, and stalker Pechs.
- Added lowercase 1.12 entity-name aliases beside the original `entity.Thaumcraft.Pech.*` localization keys.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- Type names have not been observed on in-game Pech entities because user-driven manual scenarios are excluded.
- Natural/command-spawn variant runtime coverage remains open under the Stage 6 manual matrix.

### 2026-05-15 — Stage 6 projectile sounds/status

Scope:

- Restored `EntityGolemOrb` reference `shock` impact sound and `zap` redirect sound instead of generic block event placeholders.
- Restored `EntityGolemOrb` squared-distance homing acceleration to match the reference path.
- Restored `EntityEldritchOrb` reference fizz sound, status byte `16`, and next-tick expiry after impact.
- Restored the original `0.1F` collision border on Golem orb, Eldritch orb, and Pech blast, while documenting Pech blast impact particles as Phase 8 visual work.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- Projectile sound/status behavior has not been observed in a runtime world because user-driven manual scenarios are excluded.
- Client burst/wisp particle rendering remains Phase 8/client work, and the broader projectile runtime sweep remains open under S6-PROJ-01.

### 2026-05-15 — Stage 6 Cultist and Taint Swarm drops

Scope:

- Restored base Cultist common drops: knowledge fragment, void seed, and coin rolls now apply to Cultist Knight, Cultist Cleric, and the Cultist Leader `super.dropFewItems(...)` path.
- Added a 1.12-compatible base Cultist rare eldritch-object drop through the existing `dropFewItems(...)` path.
- Fixed Taint Swarm drops to the reference 50% taint-slime-only result by removing the non-reference guaranteed taint-tendril fallback.
- Documented reference-confirmed silent sound slots for base Cultists and Taint Swarm ambient sound.

Validation:

- `./scripts/dev.sh compileJava` — initially failed because `dropRareDrop(int)` is not a 1.12 superclass hook; fixed by moving the rare drop into `dropFewItems(...)`, then passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- Cultist and Taint Swarm drops/sounds have not been observed in a runtime world because user-driven manual scenarios are excluded.
- The broader Stage 6 drop/sound table remains open for other mobs and bosses.

### 2026-05-15 — Stage 6 entity registry mapping

Scope:

- Documented the Stage 6 reference token to current Forge 1.12 registry-name mapping.
- Documented current local registration order for Stage 6 entities.
- Documented egg color parity and the decision to use Forge 1.12 entity eggs as the fresh-world replacement for the absent 1.7.10 `ItemSpawnerEgg`.
- Corrected the Stage 6 note that current registry paths are lowercase legacy tokens, not snake_case.

Validation:

- `git diff --check` — passed.
- No compile/build/smoke command required for this documentation-only checkpoint.

Remaining limits:

- Runtime registry smoke remains pending targeted runtime verification, so duplicate/missing registry warnings and actual Forge egg spawning remain unobserved.
- External 1.7.10 save/item compatibility remains out of scope for the active fresh-world target.

### 2026-05-15 — Stage 6 Inhabited Zombie crab spawn

Scope:

- Restored Inhabited Zombie reference health, attack damage, zero reinforcement chance, Cultist targeting, local spawn-density guard, and crabtalk/hurt sounds.
- Changed Inhabited Zombie death update to terminate after the manual crab/XP path instead of continuing through vanilla zombie death update.
- Restored Eldritch Crab helm NBT persistence, natural helm initialization, size, XP value, attack damage, helm-dependent armor/speed, and cultist-plate drop when the helm breaks.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- Inhabited Zombie kill scenarios and crab save/reload have not been observed in a runtime world because user-driven manual scenarios are excluded.
- Original Inhabited Zombie cultist helmet/legs/chest spawn equipment remains a content dependency because this branch currently exposes aggregate cultist armor items rather than the original separate helmet/legs/chest fields.

### 2026-05-15 — Stage 6 Cultist baseline attributes

Scope:

- Restored base Cultist size, XP value, break/enter-door navigation flags, 32-block follow range, and 0.3 movement speed from the reference.
- Removed the non-reference base Cultist max-health override so subclasses own their health values.
- Restored Cultist Knight max health from `35` to the reference `36`.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- Cultist runtime combat/team/equipment scenarios remain unobserved because user-driven manual scenarios are excluded.
- Cultist Knight attack/armor placeholders are intentionally left unchanged in this checkpoint until the missing separate reference armor piece items are resolved.

### 2026-05-15 — Stage 6 base boss behavior

Scope:

- Restored `EntityThaumcraftBoss` reference-derived XP, home persistence, spawn-home assignment, spawn-timer invulnerability/push suppression, air-supply immunity, non-despawn behavior, and eldritch-mob team rule.
- Restored boss anger/enrage damage cap, buffs, player message, anger particles, passive regen, aggro accounting, target reassessment, and player-count health/damage scaling.
- Restored inherited base-boss rare loot drops for Eldritch Golem/Warden-style bosses while keeping Cultist Leader's reference override to its own rare loot bag.
- Restored Eldritch Golem spawn/headless transition timers and Eldritch Warden spawn timer/status trigger.
- Added English localization for `tc.boss.enrage`.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- Boss combat, aggro retargeting, player scaling, spawn invulnerability, and reward drops have not been observed in a runtime world because user-driven manual scenarios are excluded.
- Champion-name parity remains a separate dependency because the current branch still has a simplified champion modifier helper and no restored `EntityUtils.CHAMPION_MOD` custom attribute path.
- Eldritch Golem low-hardness block-breaking / `BlockLoot` stomping is restored in the next checkpoint below, but remains runtime-unobserved.

### 2026-05-15 — Stage 6 Eldritch Golem movement behavior

Scope:

- Restored Eldritch Golem iron-golem step sound override.
- Restored movement block-crack particles using current block state ids.
- Restored server-side `BlockLoot` destruction when the Golem walks over loot blocks.
- Restored server-side low-hardness block breaking directly in the moving Golem's path.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- Eldritch Golem movement block breaking, `BlockLoot` stomping, and step sound have not been observed in a runtime world because user-driven manual scenarios are excluded.
- Headless combat timing and save/reload persistence still need runtime scenario evidence before GAP-8 can close.

### 2026-05-15 — Stage 6 projectile sweep

Scope:

- Restored Primal Orb random drift, seeker targeting against nearest non-owner living entity, squared-distance seeker acceleration, water material impact trigger, and `0.1F` collision border.
- Restored Shock Orb impact `shock` sound, redirect `zap` sound, `0.1F` collision border, thrower-inclusive area damage search, and reference-like block-Air placement scan with line-of-sight check.
- Restored Explosive Orb `0.1F` collision border.
- Restored Frost Shard constructor scatter application for scattershot/normal/boulder focus paths.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- Projectile damage/effect/sound/block side effects have not been observed in a runtime world because user-driven manual scenarios are excluded.
- Client particles for projectile trails/impacts remain Phase 8 FX work.

### 2026-05-15 — Stage 6 golem item registration

Scope:

- Added append-only `ConfigItems` registrations for the original TC4 golem/trunk item tokens: `TrunkSpawner`, `ItemGolemPlacer`, `ItemGolemCore`, `ItemGolemUpgrade`, `GolemBell`, and `ItemGolemDecoration`.
- Restored creative metadata ranges for golem placers, golem cores, golem upgrades, decorations, and traveling trunk spawner.
- Restored reference stack-size/subtype basics, golem core GUI/inventory helper metadata, golem core/upgrade rarity, and the decoration metadata-to-character helper.
- Added English localization for golem/trunk item display names and golem upgrade descriptions.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Golem and traveling trunk placement/on-use behavior remains open; this checkpoint only restores the registry and metadata surface needed to reach those workflows.
- Bell marker/linking, decoration, healing, and wheat interaction parity remains open.
- Golem/trunk runtime placement evidence remains unavailable while user-driven manual scenarios are excluded.

### 2026-05-15 — Stage 6 golem and trunk placement

Scope:

- Ported `ItemGolemPlacer` server-side spawn behavior for golem metadata, advanced/core/upgrades/deco/marker/inventory NBT, home/facing setup, owner/custom name state, entity spawn, sound, and survival stack consumption.
- Ported `ItemTrunkSpawner` server-side spawn behavior for owner UUID, custom name, upgrade/inventory NBT, living initialization, entity spawn, and survival stack consumption.
- Restored reference golem type ordering and baseline values so item metadata maps to the correct golem material.
- Added golem setup/read helpers for inventory sizing, upgrade sync, decoration sync, and placement/reload persistence.
- Added minimal traveling trunk upgrade/inventory-size and owner UUID persistence needed by the item route.
- Added `ItemGolemBell.getMarkers(...)` marker NBT restore support for golem placer stacks.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Bell marker editing/linking, decoration application/removal, golem healing/wheat interaction, and full trunk upgrade behavior remain open.
- Golem/trunk runtime placement evidence remains unavailable while user-driven manual scenarios are excluded.
- Client renderer/model parity remains Phase 8 work.

### 2026-05-15 — Stage 6 golem bell and decoration interactions

Scope:

- Restored marker equality/fuzzy matching to include side and color for side-specific and colored golem markers.
- Ported `ItemGolemBell` golem linking and block marker editing, including marker NBT, home data, color cycling for order-upgraded golems, shift-remove behavior, stale-link cleanup, golem marker sync, and orb feedback sound.
- Restored empty-marker reset behavior for linked golems.
- Restored golem decoration application conflict groups, camera-clack sound, decoration data sync, setup refresh, and stack consumption.
- Restored wheat healing, prevented bell/wand-held interactions from opening the golem GUI, resized golem inventories after upgrades, and sent bootup status after core application.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Bell left-click pickup/packing behavior for golems and traveling trunks remains open.
- Full traveling trunk upgrade behavior remains open beyond the minimal placement/persistence route.
- Runtime marker/deco/healing evidence remains unavailable while user-driven manual scenarios are excluded.
- Client marker visuals and golem/trunk rendering remain Phase 8 work.

### 2026-05-15 — Stage 6 golem bell pickup

Scope:

- Ported bell left-click pickup for traveling trunks, including owner check for upgrade `3`, sneak upgrade split-drop chance, order-upgrade inventory packing, normal inventory drops, zap sound, and entity removal.
- Ported bell left-click pickup for golems, including placer metadata, advanced/core/upgrades/deco/marker/inventory NBT packing, sneak core/upgrades split-drop behavior, custom name preservation, carried-item drop, zap sound, and entity removal.
- Added `EntityGolemBase.dropStuff()` as the shared carried-item drop path used by normal drops and bell pickup.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Full traveling trunk upgrade behavior remains open beyond pickup/packing and placement persistence.
- Runtime pickup/packing evidence remains unavailable while user-driven manual scenarios are excluded.
- Client pickup animation/visual parity remains Phase 8 work.

### 2026-05-15 — Stage 6 traveling trunk baseline

Scope:

- Restored reference-like traveling trunk durability, attack damage attribute, fire immunity, persistence, and size.
- Restored stay-aware owner following and Air-upgrade faster follow speed.
- Restored upgrade application, food healing, upgrade `3` owner access blocking, and GUI interaction routing.
- Restored fall-damage immunity, upgrade `3` damage immunity, passive healing, and upgrade `3` accelerated healing.
- Restored pickup upgrade `5` item attraction/insertion behavior with inventory remainder handling, eat sound, and status trigger.
- Restored inventory drops on normal trunk death.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Upgrade `2` owner-target defense/combat behavior and cross-dimension owner-follow transfer remain open.
- Runtime trunk upgrade, feeding, pickup, and inventory-drop evidence remains unavailable while user-driven manual scenarios are excluded.
- Client lid/heart/smoke animation parity remains Phase 8 work.

### 2026-05-15 — Stage 6 golem fluid NBT and ranged sound

Scope:

- Restored reference core `5` fluid-carried NBT persistence on the root golem entity tag.
- Restored reference `toggles` byte persistence.
- Restored carried item data-manager sync after NBT reload.
- Replaced the ranged golem attack sound TODO with `TCSounds.GOLEMIRONSHOOT` and the reference pitch formula.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Runtime save/load evidence for fluid, toggle, and carried item display sync remains unavailable while user-driven manual scenarios are excluded.
- The inactive pedestal/cosmetic-block state, death logging/bootup client sound parity, and carried fluid/essentia display sync remain open.
- Full per-core golem AI runtime scenarios remain open.

### 2026-05-15 — Stage 6 golem inactive stone

Scope:

- Restored the reference inactive-state check for golems standing on `ConfigBlocks.blockCosmeticSolid` meta `10` (`golemStoneActive`).
- Used the current Forge 1.12 block-state metadata contract for `BlockCosmeticSolid` instead of adding a new block or registry name.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.

Remaining limits:

- Runtime confirmation that golems pause on active golem stone and resume off it remains unavailable while user-driven manual scenarios are excluded.
- Death logging/bootup client sound parity and carried fluid/essentia display sync remain open.
- Full per-core golem AI runtime scenarios remain open.

### 2026-05-15 — Stage 6 traveling trunk defense

Scope:

- Restored the reference defensive anger timer for traveling trunks.
- Restored upgrade `2` target acquisition from the owner's revenge or attack target when the trunk is not staying.
- Added defensive target pursuit, melee damage through the trunk attack-damage attribute, hit status, and blaze-hit sound feedback.
- Cleared stale attack targets when anger expires or the target dies so normal owner following can resume.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Runtime confirmation of owner-defense target acquisition and attack cadence remains unavailable while user-driven manual scenarios are excluded.
- Cross-dimension owner-follow transfer remains open.
- Client lid/heart/smoke animation parity remains Phase 8 work.

### 2026-05-15 — Stage 6 traveling trunk dimension transfer

Scope:

- Added a server-side linked-trunk registry keyed by owner UUID in `EventHandlerEntity`, matching the original weak-reference linked entity pattern.
- Registered owned traveling trunks while they tick near their owner.
- Moved linked trunks to the owner player's destination world when the player joins a different world, preserving owner UUID, upgrade, stay flag, inventory, health, and custom name.
- Removed transferred source trunks only after the replacement entity successfully spawns in the target world.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Runtime confirmation of cross-dimension trunk transfer remains unavailable while user-driven manual scenarios are excluded.
- Event-driven transfer depends on the trunk being linked to its owner while both are loaded before the dimension change, matching the original linked-entity model.
- Client lid/heart/smoke animation parity remains Phase 8 work.

### 2026-05-15 — Stage 6 golem carried display sync

Scope:

- Added `EntityGolemBase.getCarriedForDisplay()` for the synced carried stack, matching the original data-watcher access pattern.
- Restored `updateCarried()` display sync for carried items.
- Restored fluid-core display sync by publishing the carried fluid block stack with the carried amount as metadata when a fluid block exists.
- Restored essentia-core display sync with a jar display stack populated through the current `IEssentiaContainerItem` jar item path.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Runtime confirmation of carried item/fluid/essentia display changes remains unavailable while user-driven manual scenarios are excluded.
- Actual visual/render parity for displayed carried stacks remains Phase 8 work.
- Death logging and bootup client sound parity remain open.

### 2026-05-15 — Stage 6 golem death logging

Scope:

- Restored the original server-side golem death log hook in `EntityGolemBase.onDeath(...)`.
- Logged the golem instance, true damage source, and damage type before vanilla death handling, matching the reference diagnostic behavior with Forge 1.12 accessors.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Runtime confirmation of the emitted golem death log remains unavailable while user-driven manual scenarios are excluded.
- Bootup client sound parity remains open and belongs with Phase 8 client/visual work.
- Full per-core golem AI runtime scenarios remain open.

### 2026-05-15 — Stage 6 golem fire resistance

Scope:

- Restored the reference `setFire(...)` guard so fire-resistant golem types cannot be ignited.
- Restored fire-damage rejection for fire-resistant golem types with `DamageSource.isFireDamage()`.
- Made golem setup assign the vanilla fire-immunity flag from the current golem type instead of only ever setting it to true.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Runtime confirmation of clay/stone/iron/thaumium fire immunity remains unavailable while user-driven manual scenarios are excluded.
- Bootup client sound parity remains open and belongs with Phase 8 client/visual work.
- Full per-core golem AI runtime scenarios remain open.

### 2026-05-15 — Stage 6 golem armor calculation

Scope:

- Restored `EntityGolemBase.getTotalArmorValue()` to include the golem type armor value from `EnumGolemType`.
- Restored the reference visor and plate decoration armor bonuses.
- Kept the armor result capped at `20` like the reference/vanilla armor cap.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Runtime confirmation of type/decor armor damage reduction remains unavailable while user-driven manual scenarios are excluded.
- Bootup client sound parity remains open and belongs with Phase 8 client/visual work.
- Full per-core golem AI runtime scenarios remain open.

### 2026-05-15 — Stage 6 golem water pathing

Scope:

- Restored the original setup distinction where stone, iron, and thaumium golems do not avoid water while other golem types do.
- Adapted the original water-avoidance setting to Forge 1.12 by pairing `PathNavigateGround.setCanSwim(...)` with `PathNodeType.WATER` path priority.
- Kept the pathing preference inside `setupGolem()` so NBT reload and type setup refresh it.

Validation:

- `./scripts/dev.sh compileJava` — initially failed because `PathNavigateGround.setAvoidsWater(boolean)` is absent in this Forge 1.12 mapping; after adapting to `setCanSwim(...)` and `PathNodeType.WATER`, rerun passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Runtime confirmation of water traversal and avoidance per golem material remains unavailable while user-driven manual scenarios are excluded.
- This is a Forge 1.12 API adaptation of the reference water-avoidance knob, not direct use of the removed old method.
- Full per-core golem AI runtime scenarios remain open.

### 2026-05-15 — Stage 6 golem no-drowning

Scope:

- Restored `EntityGolemBase.decreaseAirSupply(...)` to return the current air value unchanged, matching the original golem no-drowning behavior.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Runtime confirmation that submerged golems do not drown remains unavailable while user-driven manual scenarios are excluded.
- Full per-core golem AI runtime scenarios remain open.

### 2026-05-15 — Stage 6 golem melee enchantments

Scope:

- Restored held-item creature damage contribution for golem melee attacks with the Forge 1.12 `EnchantmentHelper.getModifierForCreature(...)` API.
- Restored enchantment knockback motion and golem counter-motion damping after successful melee hits.
- Restored thorns and arthropod enchantment callbacks around successful golem melee hits while preserving the existing fire-upgrade behavior.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Runtime confirmation of golem melee damage, knockback, fire, thorns, and arthropod effects remains unavailable while user-driven manual scenarios are excluded.
- Full per-core golem AI runtime scenarios remain open.

### 2026-05-15 — Stage 7 biome ID policy docs

Scope:

- Corrected the Stage 7 GAP-8 text to reflect current code: legacy numeric biome ID config keys are read in `Config.syncConfigurable()`.
- Documented the active Forge 1.12 policy that Thaumcraft biome identity is registry-name based and those numeric ID values are retained only as legacy config no-ops.

Validation:

- `git diff --check` — passed.
- Runtime smoke was not required because this checkpoint is documentation-only.

Remaining limits:

- Runtime validation of biome registration, decorators, colors, and `BiomeHandler` behavior remains open.
- The policy does not close Stage 7; it only removes the stale config-loading documentation mismatch.

### 2026-05-15 — Stage 7 Greatwood biome support

Scope:

- Restored `BiomeHandler.getBiomeSupportsGreatwood(...)` to skip biome dictionary entries whose registered Greatwood-support flag is false.
- Prevents mixed-tag biomes such as wet/swamp or hot/savanna from returning `0.0` Greatwood chance just because a non-supporting tag is encountered first.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Runtime validation of Greatwood support in mixed-tag biomes remains unavailable while user-driven manual scenarios are excluded.
- `getBiomeAura(...)`, `getRandomBiomeTag(...)`, biome decorator/spawn behavior, and biome color/debug overlay behavior still need runtime/client evidence.

### 2026-05-15 — Stage 6 golem upgrade retaliation

Scope:

- Restored the reference upgrade `5` thorns-style retaliation when a golem is damaged by another entity.
- Retaliation damage scales as `upgradeCount * 2 + random.nextInt(2 * upgradeCount)` and plays the thorns hit sound on the attacker.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Runtime confirmation of upgrade `5` retaliation damage/sound remains unavailable while user-driven manual scenarios are excluded.
- Full per-core golem AI runtime scenarios remain open.

### 2026-05-15 — Stage 6 golem target range

Scope:

- Restored the reference `isValidTarget(...)` home-distance check before core-specific target rules.
- Ensures ranged and melee golem AI share the same range rejection when validating existing targets, not just when initially acquiring them.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Runtime confirmation of target drop behavior outside golem home/range remains unavailable while user-driven manual scenarios are excluded.
- Full per-core golem AI runtime scenarios remain open.

### 2026-05-15 — Stage 6 golem animal target filters

Scope:

- Restored reference animal target exclusions for normal animal-targeting golems and butcher core validation.
- Animal-targeting golems now reject hostile `IMob` entities, tamed `EntityTameable` animals, and `EntityGolem` targets.
- Butcher core now also rejects child `EntityAnimal` targets, matching the original core `9` behavior.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Runtime confirmation of animal/butcher target choice around tamed animals, child animals, and golems remains unavailable while user-driven manual scenarios are excluded.
- Full per-core golem AI runtime scenarios remain open.

### 2026-05-15 — Stage 6 golem butcher target acquisition

Scope:

- Replaced the `AINearestButcherTarget.shouldExecute()` stub with the original butcher acquisition rule.
- Butcher golems now scan within golem range, sort valid candidates by oldest entity age, and select the oldest valid target only when more than two valid same-type targets are nearby.
- Restored `AIOldestAttackableTargetSorter` to sort by `ticksExisted` descending instead of distance, matching the original butcher-task role.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Runtime confirmation of butcher culling threshold, same-type counting, and oldest-target selection remains unavailable while user-driven manual scenarios are excluded.
- Full per-core golem AI runtime scenarios remain open.

### 2026-05-15 — Stage 6 golem item pickup delay

Scope:

- Restored `AIItemPickup` to gate candidate `EntityItem`s by the original pickup-delay threshold instead of permanently skipping items that have a thrower name.
- Adapted the 1.7.10 public delay-field check to Forge 1.12.2 by reading `EntityItem.pickupDelay`/`field_145804_b` through Forge `ReflectionHelper`, with `cannotPickup()` as a fallback if reflection is unavailable.
- Restored the original golem item-pickup pop sound pitch multiplier.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Runtime confirmation that golems pick up player-thrown items after the reference delay window remains unavailable while user-driven manual scenarios are excluded.
- Full per-core golem AI runtime scenarios remain open.

### 2026-05-15 — Stage 6 golem essentia jar destination

Scope:

- Restored `GolemHelper.findJarWithRoom(...)` to rebuild the connected-jar cache per search instead of allowing stale `jarlist` state to affect later searches.
- Preserved the original priority tiers: suction-capable non-jar transports plus matching labeled non-full jars first, then progressively looser jar categories.
- Restored the reference nearest-candidate choice inside the selected tier, including the distance penalty for void jars.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Runtime confirmation of essentia emptying destination choice across labeled jars, unlabeled jars, void jars, reservoirs, and transports remains unavailable while user-driven manual scenarios are excluded.
- Full per-core golem AI runtime scenarios remain open.

### 2026-05-15 — Stage 6 golem liquid target tank

Scope:

- Restored `GolemHelper.getMissingLiquids(...)` to inspect the home-adjacent target fluid handler, matching the original flow that decides what the golem should fetch or empty into.
- Adapted the original `IFluidHandler.canFill(...)` and fluid-container filter to Forge 1.12.2 capabilities with simulated `IFluidHandler.fill(...)` and `FluidUtil.getFluidContained(...)`.
- Preserved the reference constraint that an already-carried fluid restricts candidates to the same fluid.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Runtime confirmation of liquid gather/empty behavior with Forge fluid handlers and filled-container filters remains unavailable while user-driven manual scenarios are excluded.
- Full per-core golem AI runtime scenarios remain open.

### 2026-05-15 — Stage 7 Eldritch portal support check

Scope:

- Restored `BlockEldritchPortal.neighborChanged(...)` to remove the portal if either the eldritch support block above or below is missing.
- The previous port only checked the lower support block; the original checks both vertical supports.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Runtime portal support updates, traversal, and save/reload validation remain unavailable while user-driven manual scenarios are excluded.

### 2026-05-15 — Stage 7 Outer Lands provider spawn baseline

Scope:

- Restored `WorldProviderOuter.canCoordinateBeSpawn(...)` to check the top block material instead of always returning `false`.
- Restored the reference average ground level `50` for the Outer Lands provider.
- Left `getSpawnPoint()` unchanged to avoid introducing a nullable 1.12.2 spawn point without runtime evidence.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Runtime confirmation of Outer Lands load, spawn fallback behavior, chunk population, traversal, and save/reload remains unavailable while user-driven manual scenarios are excluded.

### 2026-05-15 — Stage 7 Outer Lands structure query no-op

Scope:

- Restored the reference no-structure-query contract for `ChunkProviderOuter`.
- `generateStructures(...)` now returns `false`, `getNearestStructurePos(...)` returns `null`, and `isInsideStructure(...)` returns `false`.
- Removed the previous `MazeHandler`-backed accepted-name search so 1.12.2 structure queries cannot report stale global labyrinth cells as named structures.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Runtime command/query crash evidence for the Outer Lands provider remains unavailable while user-driven manual scenarios are excluded.

### 2026-05-15 — Stage 7 Outer Lands worldgen ownership

Scope:

- Moved new-chunk Outer Lands room generation back to `ThaumcraftWorldGenerator.worldGeneration(...)`, matching the reference ownership path.
- `ChunkProviderOuter.populate(...)` now runs biome decoration only; it no longer calls `MazeHandler.generateEldritch(...)` with provider-local population RNG.
- The Outer Lands branch now runs `MazeHandler.generateEldritch(...)` and marks the chunk dirty for both new generation and retrogen.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `git diff --check` — passed.

Remaining limits:

- Runtime confirmation of Outer Lands load, room generation, chunk population, traversal, and save/reload remains unavailable while user-driven manual scenarios are excluded.

### 2026-05-15 — Stage 7 current-status docs refresh

Scope:

- Updated `docs/Stage7.md` GAP-1 to reflect the current worldgen control-flow baseline instead of the older missing-method audit.
- Updated GAP-11 to reflect the hilltop, mound/barrow, and shared structure-branch checkpoints already present in code and progress notes.
- Reworded the remaining Stage 7 worldgen checklist from "restore" to "validate" for already-ported server-side baselines.

Validation:

- `git diff --check` — passed.

Remaining limits:

- Documentation refresh only; it does not close Stage 7 runtime/manual validation, exact mound-template parity, worldgen distribution parity, or save/reload evidence.

### 2026-05-15 — Stage 5 Hover Harness motion

Scope:

- Restored reference client-side horizontal motion damping while Hover Harness hover is active.
- Applied the reference Haste and Hover Girdle motion modifiers.
- Restored the periodic Jacob's ladder hover hum cadence and shared Hover Girdle detection with existing fuel-efficiency logic.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY` is unset in the current environment.
- `git diff --check` — passed.

Remaining limits:

- Hover Harness H-key toggle path, on/off sounds, anti-float counter reset parity, tooltip parity, and manual fuel/toggle/fall validation remain open.

### 2026-05-15 — Stage 5 Hover Harness tooltip and rarity

Scope:

- Restored reference rarity values for Hover Harness (`EPIC`) and Hover Girdle (`RARE`).
- Restored Hover Harness stored-jar tooltip behavior for discovered aspect counts, unknown aspect fallback text, and vis-discount lines.
- Added the original `tc.visdiscount` and `tc.aspect.unknown` language keys needed by the tooltip.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY` is unset in the current environment.

Remaining limits:

- Hover Harness H-key toggle path, on/off sounds, anti-float counter reset parity, client/manual tooltip display validation, and manual fuel/toggle/fall validation remain open.

### 2026-05-15 — Stage 5 Hover Harness H-key toggle

Scope:

- Added a client-only Hover Harness `H` key handler and registered it from `ClientProxy.registerKeyBindings()`.
- Matched the reference one-shot key behavior while the game has focus so held `H` does not repeatedly toggle.
- Restored `Hover.toggleHover` client packet dispatch and `hhon`/`hhoff` sounds for successful toggles.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY` is unset in the current environment.

Remaining limits:

- Hover Harness anti-float counter reset parity, client/manual H-key and sound validation, client/manual tooltip display validation, and manual fuel/toggle/fall validation remain open.
- The full reference Stage 8-a key handler remains incomplete: `F` focus-radial/remove behavior and `G` misc wand toggle dispatch are still open.

### 2026-05-15 — Stage 5 Hover Harness anti-float reset

Scope:

- Restored `Utils.resetFloatCounter(EntityPlayerMP)` using the reference reflection target names for `NetHandlerPlayServer`'s floating tick counter.
- Called the reset helper while Hover Harness hover is active on the logical server, before fall-distance reset.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY` is unset in the current environment.

Remaining limits:

- Hover Harness client/manual H-key and sound validation, client/manual tooltip display validation, and manual fuel/toggle/fall/float validation remain open.
- The full reference Stage 8-a key handler remains incomplete: `F` focus-radial/remove behavior and `G` misc wand toggle dispatch are still open.

### 2026-05-15 — Stage 8-a key handler dispatch

Scope:

- Expanded the client-only key handler to register the reference `F`, `G`, and `H` bindings.
- Added client dispatch for sneak-F focus removal, F golem-bell marker reset, G misc wand/shovel toggle, and H Hover Harness toggle.
- Preserved client-only `radialActive`/`radialLock` state for later focus radial rendering while keeping key APIs out of common classes.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY` is unset in the current environment.

Remaining limits:

- Stage 8-a keybindings still need client Controls-screen and in-world packet/manual validation.
- Focus radial rendering remains a later Stage 8 client-rendering task; this checkpoint only restores the key state/dispatch boundary.

### 2026-05-15 — Stage 5/7 Mana Bean and mana pod behavior

Scope:

- Restored Mana Bean reference-style `Aspects` NBT storage with legacy `aspect` fallback, 10-tick use duration, original nutrition/saturation shape, tooltip aspect disclosure, and client item-color registration.
- Restored eating side effects: a reference-shaped old potion table roll and 25% aspect-pool gain with capability cache refresh and `PacketAspectPool` sync.
- Restored planting constraints: beans only plant on the underside of vanilla/magical logs in Magical biomes, place the pod below the clicked log, transfer the bean aspect to `TileManaPod`, and consume one bean outside creative.
- Restored `TileManaPod` aspect persistence, mature `IAspectContainer` exposure, growth, neighboring pod aspect combination, and default primal/Herba assignment.
- Restored mana pod support checks, random growth tick, aspect-preserving bean drops, pick-block return, and worldgen growth initialization through `WorldGenManaPods`.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY` is unset in the current environment.

Remaining limits:

- Mana Bean eating, planting, pod harvest drops, and natural pod generation still need in-world/manual scenario validation.
- Mana pod blockstate/model/renderer parity remains Stage 8 visual work; this checkpoint only restores common/server behavior plus item tint registration.
- Broader Stage 7 vegetation/ore probability parity and runtime new-world sampling remain open.

### 2026-05-15 — Stage 5 Sanity Soap use completion

Scope:

- Restored the reference automatic use completion after more than 195 ticks by stopping the active hand from `onUsingTick`.
- Restored the Warp Ward potion bonus to sticky-warp cleansing chance, raising the server-side chance from 33% to 58% while the potion is active.
- Kept the existing temporary-warp removal and non-creative stack consumption behavior.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY` is unset in the current environment.

Remaining limits:

- Pure-fluid bonus chance remains blocked by the current absence of the original `blockFluidPure`/pure-water block in this port.
- Client roots/craftstart sounds and bubble FX remain Stage 8 visual/audio work.
- Manual soap-use validation still needs an in-world player scenario.

### 2026-05-15 — Stage 5 essence phial behavior

Scope:

- Restored Glass Phial vs Phial of Essentia metadata/name behavior and creative subitems with 8 essentia per filled phial.
- Restored phial transfer behavior for alembics and fillable/void jars: empty phials extract 8 essentia into a filled phial, and filled phials return 8 essentia to compatible jars while producing an empty phial.
- Declared `TileJarFillable` as an `IAspectContainer`, matching its existing method surface and allowing shared phial transfer logic.
- Restored aspect tooltips and item-color hooks for essence phials, crystal essence, and wisp essence.
- Restored Crystal Essence random aspect assignment on creation/inventory update and Wisp Essence creative variants with 2 points of each aspect.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY` is unset in the current environment.

Remaining limits:

- Phial transfer needs in-world/manual checks against alembics, normal jars, void jars, full jars, filtered jars, and full player inventories.
- Item tint/tooltip behavior is implemented but not visually validated because GUI/client validation is unavailable.
- Full jar/alembic visual renderer parity remains Stage 8.

### 2026-05-15 — Stage 5 Resonator diagnostics

Scope:

- Restored the reference item glint when the Essentia Resonator has NBT.
- Restored readable buffer diagnostics by listing each aspect/count in `TileTubeBuffer` instead of sending a raw `AspectList` object.
- Restored the untyped suction fallback text and added the missing `tc.resonator*` language keys.
- Restored the alembic-knock feedback sound on successful server-side diagnostics.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY` is unset in the current environment.

Remaining limits:

- The old CCL sub-hit retrace for per-tube-face diagnostics is not restored in this checkpoint; current output uses the clicked block side exposed by Forge's item-use callback.
- Manual diagnostics against jars, alembics, tubes, and tube buffers remain open.

### 2026-05-15 — Stage 5 Taint Bottle projectile launch

Scope:

- Restored Forge 1.12 projectile launch for `ItemBottleTaint` by calling `EntityBottleTaint.shoot(...)` before spawning the entity.
- Preserved the original reference launch profile: `-20.0F` pitch offset, `0.5F` velocity, and `1.0F` inaccuracy.
- Left the existing server-side impact behavior in `EntityBottleTaint` unchanged.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh smoke-server` — passed; the server reached `Done (1.282s)!`, the configured crash-marker scan only found the ready-state line, and no crash reports were present.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY` is unset in the current environment.

Remaining limits:

- Manual projectile flight, impact, biome-taint, fibre-placement, and entity-poison scenario checks remain open.
- Original client-side taintsplosion and bottle-break FX remain Phase 8 client visual work.

### 2026-05-15 — Stage 5 Eldritch Object common use behavior

Scope:

- Restored `ItemEldritchObject` rarity tiers from the reference: meta `2` is rare, meta `3` is epic, other metas are uncommon.
- Restored Crimson Rites right-click behavior as a non-consuming research-completion trigger with the learn sound, instead of consuming the item on use.
- Restored the primordial pearl/meta `3` node-use path: consumes the item, mutates node base aspects/modifier with the reference random ranges, syncs the node, creates the explosion, and scatters flux goo/gas around open positions.
- Restored creative obelisk placer/meta `4` block-use placement: top-side-only activation, original air checks, original block metadata layout, and no survival consumption.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — passed; no MCP-named Minecraft field/method references were found in the built universal jar.
- `./scripts/dev.sh smoke-server` — passed after the 2026-05-15 smoke wrapper/logging fix.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY` is unset in the current environment.

Remaining limits:

- Runtime scenarios for Crimson Rites research sync, primordial pearl node mutation/flux scatter, and obelisk placement remain unvalidated because manual in-world scenarios are out of scope.
- Original tooltip text and item overlay/render parity remain Phase 8/client-resource work.

### 2026-05-15 — Agent validation noise reduction

Scope:

- Added `./scripts/dev.sh validate` as a compact, stop-on-first-failure validation wrapper for git status summary, `compileJava`, non-GUI tests, `jar`, and MCP leak summary.
- Added `./scripts/dev.sh validate --smoke` to include the dedicated server smoke stage while keeping stdout to one summary line per stage.
- Wrote detailed stage logs under `run/validate/` and kept smoke details in `run/smoke-server.log`, so agents can report short summaries and inspect logs only on failure.
- Updated `AGENTS.md` and `docs/GOAL.md` to prefer `validate`/`validate --smoke` for routine checkpoint validation and keep individual Gradle/check/smoke commands for debugging or release-artifact needs.

Validation:

- `bash -n scripts/dev.sh` — passed.
- `./scripts/dev.sh validate` — passed with compact `check-jar` summary for `5114` MCP leak lines / `1028` unique leaks.
- `./scripts/dev.sh validate --smoke` — passed, including server smoke readiness.

Remaining limits:

- `validate` does not replace `build`, `apiJar`, `devJar`, `smoke-client`, or targeted static scans when a checkpoint specifically requires them.

### 2026-05-15 — Stage 5 Pure Fluid and Bucket of Purity

Scope:

- Added `blockFluidPure` and `fluidPure` registration with the original legacy registry token and Forge fluid identity.
- Ported the Pure Fluid source-block collision effect: players without Warp Ward receive `Config.potionWarpWard` for the reference duration scaled by permanent warp, then the source block is consumed.
- Restored Bucket of Purity placement and fill behavior, including empty-bucket return outside creative mode and source-block pickup through `FillBucketEvent`.
- Restored Sanity Soap's pure-fluid sticky-warp cleanse bonus now that `blockFluidPure` exists.
- Copied the original `fluidpure` block texture and animation metadata from `thaumcraft_src/assets/thaumcraft/textures/blocks/`.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh validate --smoke` — passed: git status summary, `compileJava`, tests `8/8`, `jar`, compact MCP leak summary, and server smoke readiness.
- Server smoke evidence: `run/smoke-server.log` reached `Done (1.117s)!`; the configured crash-marker scan only found the ready-state line, and no crash reports were present.
- `./scripts/dev.sh check-jar` — still fails with the existing project-wide MCP-named reference list; the compact validation summary recorded `5135` MCP leak lines / `1028` unique leaks. The visible verbose output did not identify the new pure-fluid/bucket classes specifically.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY` is unset in the current environment.

Remaining limits:

- Pure Fluid placement, pickup, Warp Ward collision, and Sanity Soap pure-fluid cleanse bonus still need in-world/manual scenario evidence; user-driven manual validation remains out of scope.
- Pure Fluid client particles/sounds and full fluid visual parity remain Stage 8 client work.

### 2026-05-15 — Stage 3/5 Research Notes NBT and use baseline

Scope:

- Added `ResearchNoteData` and research-note NBT helpers preserving original keys for `key`, `color`, `complete`, `copies`, and `hexgrid` entries.
- Changed `ItemResearchNotes` so right-click research completion requires solved note data (`complete=true`) and requisite checks instead of granting any stack with a `key` tag.
- Restored sibling completion for solved notes, hidden-discovery reveal/failure handling, knowledge fragment fallback, learn/write/erase sounds, and the note/discovery rarity split.
- Added localization for research-note invalid/requisite/forbidden messages used by the restored paths.

Validation:

- Original behavior inspected from `Thaumcraft-1.7.10-4.2.3.5.jar` with `javap` for `ItemResearchNotes`, `ResearchNoteData`, and `ResearchManager`.
- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh test` — initially failed because a new unit test instantiated `ItemStack` before Minecraft item bootstrap; the test was adjusted to cover the plain NBT helpers directly, then passed.
- `./scripts/dev.sh validate --smoke` — passed: `compileJava`, tests `10/10`, jar, compact MCP leak summary `5161` lines / `1028` unique leaks, and server smoke readiness.
- Server smoke evidence: `run/smoke-server.log` contained `Registering entities` at line `108`, `Forge Mod Loader has successfully loaded 6 mods` at line `126`, and `Done (1.353s)!` at line `138`; no crash reports were present under `run/`.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY` is unset in the current environment.

Remaining limits:

- This does not port the full reference hex-grid generation/solving algorithm, clue creation, Research Table GUI flow, or Phase 9 research content registration.
- Manual solved-note completion, hidden-discovery reveal, and research-table scenarios remain unvalidated because user-driven/manual validation is out of scope.

### 2026-05-15 — Stage 6 registration smoke evidence

Scope:

- Recorded non-GUI runtime evidence for the Stage 6 `S6-REG-01` manual matrix row now that the smoke wrapper reaches server readiness.
- Updated the Stage 6 matrix to mark load/registration smoke as `PASS` with concrete log lines for entity registration, successful Forge mod loading, and server ready state.

Validation:

- Evidence source was the latest `./scripts/dev.sh validate --smoke` run from the Pure Fluid checkpoint.
- `run/smoke-server.log` contained `Registering entities` at line `108`, `Forge Mod Loader has successfully loaded 6 mods` at line `126`, and `Done (1.117s)!` at line `138`.
- The configured crash-marker scan found no crash markers and `find run -maxdepth 2 ... crash reports ...` returned no files.

Remaining limits:

- This only validates mod load/entity-registration readiness. Stage 6 spawn, combat, AI, drops, GUI, and save/reload rows remain TODO because user-driven manual scenarios are out of scope.

### 2026-05-15 — Stage 7 server smoke evidence

Scope:

- Recorded the latest non-GUI runtime evidence against Stage 7 GAP-5 now that `validate --smoke` reaches server readiness.
- Marked only the server-load smoke acceptance item as satisfied, without closing Stage 7 worldgen, Outer Lands traversal, room generation, or save/reload parity.

Validation:

- Evidence source was the latest `./scripts/dev.sh validate --smoke` run from the Pure Fluid checkpoint.
- `run/smoke-server.log` contained `Registering entities` at line `108`, `Forge Mod Loader has successfully loaded 6 mods` at line `126`, and `Done (1.117s)!` at line `138`.
- The configured crash-marker scan found no crash markers and `find run -maxdepth 2 ... crash reports ...` returned no files.

Remaining limits:

- Stage 7 manual scenarios remain open: fresh-world worldgen distribution, Eldritch ring/portal entry, Outer Lands chunk population, maze traversal, `labyrinth.dat` save/reload, and portal return.
- This checkpoint does not close Stage 7 GAP-5 because the user-driven/manual worldgen and traversal scenarios remain outside the current automation scope.

### 2026-05-15 — Stage 5 Loot Bag shared reward path

Scope:

- Routed `ItemLootBag` reward rolls through the shared `Utils.generateLoot(...)` helper.
- Removed duplicate private loot/gear/fallback generation from the item while preserving the original roll count, drop position, coin sound, and stack consumption path.

Validation:

- Original behavior inspected from `Thaumcraft-1.7.10-4.2.3.5.jar` with `javap` for `ItemLootBag`.
- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh validate --smoke` — passed: `compileJava`, tests `10/10`, jar, compact MCP leak summary `5130` lines / `1028` unique leaks, and server smoke readiness.
- Server smoke evidence: `run/smoke-server.log` contained `Registering entities` at line `108`, `Forge Mod Loader has successfully loaded 6 mods` at line `126`, and `Done (1.252s)!` at line `138`; no crash reports were present under `run/`.

Remaining limits:

- Full reference loot-pool distribution still depends on Stage 9 content/table population.
- Manual loot-bag opening scenario evidence remains out of scope.

### 2026-05-15 — Stage 8-a client bootstrap boundaries

Scope:

- Split `ClientProxy.registerDisplayInformation()` into explicit `setupItemRenderers`, `setupEntityRenderers`, `setupBlockRenderers`, and `setupTileRenderers` boundaries.
- Kept existing generic item model/color registration under the item boundary.
- Added client-only `ClientTickEventsFML`, `RenderEventHandler`, and `ParticleEngine` bootstrap classes and registered them from `ClientProxy.registerHandlers()` alongside the tooltip handler.

Validation:

- Original bootstrap behavior inspected from `Thaumcraft-1.7.10-4.2.3.5.jar` with `javap` for `ClientProxy`, `ClientTickEventsFML`, `RenderEventHandler`, and `ParticleEngine`.
- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh validate --smoke` — passed: `compileJava`, tests `10/10`, jar, compact MCP leak summary `5130` lines / `1028` unique leaks, and dedicated server smoke readiness.
- Server smoke evidence: `run/smoke-server.log` contained `Registering entities` at line `108`, `Forge Mod Loader has successfully loaded 6 mods` at line `126`, and `Done (1.211s)!` at line `138`; no crash reports were present under `run/`.
- `./scripts/dev.sh smoke-client` — skipped because `DISPLAY` is unset and user-driven GUI/graphics validation is excluded by `docs/GOAL.md`.

Remaining limits:

- These are bootstrap boundaries only; concrete GUI/render/particle parity is still Stage 8-b/c/d/e work.
- GUI routing breadth, client packet side-boundary audit, concrete renderer registrations, and client smoke remain open.

## Next Checkpoint Candidate

After the golem carried-display, trunk transfer, death logging, fire-resistance, armor, water-pathing, no-drowning, melee-enchantment, upgrade-retaliation, target-range, animal-target-filter, butcher-acquisition, item-pickup-delay, essentia-jar-destination, liquid-target-tank, portal-support, outer-provider-spawn, outer-structure-query, outer-worldgen-ownership, Stage7-docs-refresh, hover-motion, biome policy, and Greatwood-support checkpoints, the next pre-Phase8 candidates are:

- Remaining Stage 5 low-risk Hover Harness/common-layer item and utility/relic fixes, if they can be validated without GUI/client control.
- Remaining Stage 6 selected low-risk golem AI helper fixes, if they can be kept server-safe.
- Remaining Stage 7 surface/worldgen runtime evidence and broader biome blacklist edge cases.
- Remaining Stage 7 Outer Lands room/tile behavior, especially key/boss room traversal, boss-room runtime/save evidence, and maze save/load race validation.
- Stage 9 loot/content registration, because `Utils.generateLoot(...)` now has a shared reward path but the full reference loot pool distribution still depends on populated content tables.
- Stage 6 server-side boss/manual scenario evidence remains excluded from user-driven validation, but static/reference parity blockers should continue to be reduced where possible.

Do not mark Stage 6 or Stage 7 complete from this checkpoint alone.
