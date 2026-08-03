# Audit Packet: A-003 - Void/device research declarations

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Assignment-ID: A-003
Status: complete
Report-Revision: 1
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Compare the `ADVALCHEMYFURNACE`, `PRIMALCRUSHER`, `VOIDMETAL`, `ESSENTIARESERVOIR`, `CAP_void`, `ARMORVOIDFORTRESS`, and `SANITYCHECK` declarations in `ConfigResearchEldritch.java` against TC4 4.2.3.5 `ConfigResearch.class#initEldritchResearch`, including coordinates, icon stacks, aspect/cost data, parents and hidden parents, flags, warp, triggers, declaration order, page order/types, and recipe-handle binding.
- Anti-scope: Recipe bodies; item, block, tile, entity, crafting, or other runtime behavior beyond verifying the identity/type of a declaration's icon and page handles; other Eldritch declarations; product edits; central-ledger edits; broad re-audit.
- Oracle and comparison direction: S-003/S-004 bundled TC4 4.2.3.5 `thaumcraft.common.config.ConfigResearch#initEldritchResearch` -> S-005 Forge 1.12.2 port `thaumcraft.common.config.research.ConfigResearchEldritch#initEldritchResearchBaseline`.
- Questions: Are all scoped declaration fields and call chains exact? Do page helpers bind the same named handles to equivalent `ResearchPage` types? Do renamed port fields still identify the original icon objects? Does declaration/registration order match?
- Expected evidence: CFR decompilation and `javap -c` evidence from the bundled TC4 class; exact port line locators; targeted helper, page-constructor, registry-handle, and test inspection.
- Read/write permissions: Product and central-ledger files read-only; only this report writable.
- Effort/tool budget: Targeted static inspection and decompilation only; no build or runtime validation because no product code changes were authorized.
- Stop conditions: All seven declarations compared across every requested field, or an unresolvable decompiler/source conflict is recorded.
- Continuation predecessor: none

## Source Anchors

- S-003: `Thaumcraft-1.7.10-4.2.3.5.jar`, SHA-256 `3dba9786966974701578a658d1bb369bf35bdf5f363079f5ac9c4910a39113be`.
- S-004: `thaumcraft_src/thaumcraft/common/config/ConfigResearch.class`, SHA-256 `7ed2e392b82f75f6636abd57477a2de97566b5d395a0731b4e1e5d64e45aa50d`, method `private static void initEldritchResearch()`.
- S-005: branch `master`, baseline HEAD `a1a2973e4fd4b38b4e49789391ecc4292c998373`.
- Primary port declaration: `src/main/java/thaumcraft/common/config/research/ConfigResearchEldritch.java:134-270,296-311`.
- Handle/type adapters: `src/main/java/thaumcraft/common/config/research/ConfigResearch.java:18-39`; `src/main/java/thaumcraft/api/research/ResearchPage.java:24-88`.

## Coverage Performed

- Files/symbols inspected: `ConfigResearchEldritch#initEldritchResearchBaseline`; `ConfigResearch.recipeI`, `recipeArcane`, `recipeCrucible`, `recipeInfusion`, and `recipeList`; relevant `ResearchPage` constructors; scoped fields and initializers in `ConfigItems` and `ConfigBlocks`; `ItemResource.META_VOID_INGOT`; research static guards.
- Oracle surfaces inspected: CFR decompilation of TC4 `ConfigResearch.class#initEldritchResearch`; independent `javap -p -c` bytecode for that class/method; TC4 `ConfigItems` field declarations and CFR decompilation of `ConfigItems.class#initializeItems` for icon-handle identity.
- Comparison dimensions completed: key/category; aspect add-call order and amounts; coordinates; integer research cost/complexity argument; icon item/block, count, and metadata; page count/order/type/text or handle key; normal and hidden parent order; all fluent flags; research and item warp; item/entity/aspect triggers; registration chronology.
- Uncovered scope: Recipe implementation bodies and runtime behavior, as required by anti-scope. No manual GUI observation was attempted or needed to establish static declaration parity.

## Atomic Findings

### A-003-F01 - SANITYCHECK registration chronology differs from TC4

- Type: benign_delta
- Severity: low
- Confidence: high
- Source/oracle locator: S-004 `thaumcraft/common/config/ConfigResearch.class`, `ConfigResearch#initEldritchResearch`; CFR statement sequence and independent `javap -p -c` offsets 1187-1298 for `SANITYCHECK`, followed by `VOIDMETAL` beginning at offset 1299.
- Observed: The port declares and registers `SANITYCHECK` at `src/main/java/thaumcraft/common/config/research/ConfigResearchEldritch.java:296-311`, after `VOIDMETAL` (`:179-205`), `ESSENTIARESERVOIR` (`:207-225`), `CAP_void` plus its warp (`:227-247`), `ARMORVOIDFORTRESS` (`:249-270`), and the out-of-assignment `FOCUSPRIMAL` block (`:272-294`).
- Expected: TC4 registration sequence is `... PRIMALCRUSHER, SANITYCHECK, VOIDMETAL, ESSENTIARESERVOIR, CAP_void, ARMORVOIDFORTRESS, FOCUSPRIMAL ...`; `SANITYCHECK` is registered immediately after `PRIMALCRUSHER` and before `VOIDMETAL`.
- Exact deltas: `SANITYCHECK` moved from directly between `PRIMALCRUSHER` and `VOIDMETAL` in the oracle to after `FOCUSPRIMAL` in the port. Its own constructor values, pages, parent, flags, warp, and triggers have no delta.
- Affected paths/symbols: `src/main/java/thaumcraft/common/config/research/ConfigResearchEldritch.java:296-311`, `ConfigResearchEldritch#initEldritchResearchBaseline`.
- Evidence/reproduction: `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigResearch.class --methodname initEldritchResearch --silent true` emits `PRIMALCRUSHER`, then `SANITYCHECK`, then `VOIDMETAL`; `javap -classpath Thaumcraft-1.7.10-4.2.3.5.jar -p -c thaumcraft.common.config.ConfigResearch` independently confirms the same sequence. Port source places the blocks at the lines above.
- Impact: No current gameplay effect was verified. Both TC4 and the port store category research in a `HashMap`, and the scoped entries have distinct keys and coordinates, so insertion chronology does not define stable display iteration and does not change overlap rejection for these entries. The source remains chronologically non-parity, and order could become observable if registration gains side effects or a future key/coordinate collision is introduced.
- Regression hazards: A reorder must not change any declaration content, move the `CAP_void` warp away from its registered key, or disturb the adjacent `FOCUSPRIMAL` warp/item-warp calls. Treat chronology separately from declaration metadata.
- Test gap: No focused test asserts TC4 registration sequence. `ConfigResearchReferenceFingerprintStaticGuardTest.java:52-67` sorts extracted key/category and page-key sets, making it order-insensitive; `ConfigResearchStaticGraphTest.java:122-143` parses declarations but does not compare their order to TC4.
- Candidate disposition: deferred pending orchestrator adjudication; verified source delta, but not a confirmed behavioral defect.

### A-003-F02 - ADVALCHEMYFURNACE declaration matches TC4

- Type: parity
- Severity: none
- Confidence: high
- Source/oracle locator: S-004 `ConfigResearch.class#initEldritchResearch`, CFR statement beginning `new ResearchItem("ADVALCHEMYFURNACE", "ELDRITCH", ...)`.
- Observed: Port `ConfigResearchEldritch.java:134-153` has aspects `AURA 1`, `MAGIC 1`, `ORDER 1`, `ENTROPY 1`; coordinates `(-2, 6)`; cost/complexity `1`; icon `new ItemStack(ConfigBlocks.blockMetalDevice, 1, 3)`; pages in order `TEXT tc.research_page.ADVALCHEMYFURNACE.1`, `ARCANE_CRAFTING AdvAlchemyConstruct`, `TEXT tc.research_page.ADVALCHEMYFURNACE.2`, `COMPOUND_CRAFTING AdvAlchemyFurnace`; flag `secondary`; parents `PRIMPEARL`, `DISTILESSENTIA`, `VISPOWER`; no hidden parents, warp, or triggers.
- Expected: Exact same values and call order in TC4.
- Exact deltas: none.
- Affected paths/symbols: `ConfigResearchEldritch.java:134-153`; icon initializer `ConfigBlocks.java:107-109`; page helpers `ConfigResearch.java:22-23,38-39`.
- Evidence/reproduction: CFR binds `AdvAlchemyConstruct` as `IArcaneRecipe` and `AdvAlchemyFurnace` as `List`; port helpers return those exact types, selecting `ResearchPage(IArcaneRecipe)` -> `ARCANE_CRAFTING` at `ResearchPage.java:66-70` and `ResearchPage(List)` -> `COMPOUND_CRAFTING` at `:61-64`. Port block handle retains legacy registry path `blockMetalDevice`.
- Regression hazards: Preserve metadata `3`, the unusual compound-list fourth page, all three parent orderings, and `secondary` without `concealed`.
- Candidate disposition: preserve.

### A-003-F03 - PRIMALCRUSHER declaration matches TC4

- Type: parity
- Severity: none
- Confidence: high
- Source/oracle locator: S-004 `ConfigResearch.class#initEldritchResearch`, CFR statement beginning `new ResearchItem("PRIMALCRUSHER", "ELDRITCH", ...)`; `javap` constructor/page/parent bytecode around offsets 1034-1186.
- Observed: Port `ConfigResearchEldritch.java:155-177` has aspects `MINE 6`, `TOOL 6`, `ENTROPY 6`, `VOID 6`, `WEAPON 6`, `ELDRITCH 6`, `GREED 6`; coordinates `(2, 5)`; cost/complexity `2`; icon `new ItemStack(ConfigItems.itemPrimalCrusher)`; pages `TEXT tc.research_page.PRIMALCRUSHER.1`, `INFUSION_CRAFTING PrimalCrusher`, `TEXT tc.research_page.PRIMALCRUSHER.2`; flag `concealed`; parent `PRIMPEARL`; hidden parents `VOIDMETAL`, `ELEMENTALPICK`, `ELEMENTALSHOVEL`; no warp or triggers.
- Expected: Exact same values and call order in TC4.
- Exact deltas: none.
- Affected paths/symbols: `ConfigResearchEldritch.java:155-177`; `ConfigItems.java:559-563`; `ConfigResearch.java:30-31`.
- Evidence/reproduction: CFR and `javap` bind `PrimalCrusher` as `InfusionRecipe`; port `recipeInfusion` enforces the same class and `ResearchPage.java:84-88` produces `INFUSION_CRAFTING`. TC4 `ConfigItems#initializeItems` and port `ConfigItems.java:559-563` both bind the icon to legacy `ItemPrimalCrusher`.
- Regression hazards: Preserve all seven aspect entries, hidden-parent ordering, and `concealed` without `secondary` or warp.
- Candidate disposition: preserve.

### A-003-F04 - VOIDMETAL declaration matches TC4

- Type: parity
- Severity: none
- Confidence: high
- Source/oracle locator: S-004 `ConfigResearch.class#initEldritchResearch`, CFR `VOIDMETAL` statement; `javap` offsets 1299-1645.
- Observed: Port `ConfigResearchEldritch.java:179-205` has aspects `METAL 3`, `ELDRITCH 3`, `DARKNESS 3`, `VOID 5`; coordinates `(2, -2)`; cost/complexity `2`; icon `new ItemStack(ConfigItems.itemResource, 1, 16)`; 12 pages in order: `TEXT .1`, `CRUCIBLE_CRAFTING VoidMetal`, `TEXT .2`, then `NORMAL_CRAFTING` handles `VoidAxe`, `VoidSword`, `VoidPick`, `VoidShovel`, `VoidHoe`, `VoidHelm`, `VoidChest`, `VoidLegs`, `VoidBoots`; parents `THAUMIUM`, `ELDRITCHMINOR`; no flags, hidden parents, warp, or triggers.
- Expected: Exact same values, page count, and page order in TC4.
- Exact deltas: none.
- Affected paths/symbols: `ConfigResearchEldritch.java:179-205`; `ConfigItems.java:329-333`; `ItemResource.java:67`; `ConfigResearch.java:18-19,26-27`.
- Evidence/reproduction: CFR/bytecode casts `VoidMetal` to `CrucibleRecipe` and each equipment handle to `IRecipe` in the listed order. Port helpers enforce those classes; `ResearchPage.java:72-76` yields `CRUCIBLE_CRAFTING`, while `:35-39` yields `NORMAL_CRAFTING`. Metadata `16` is still `ItemResource.META_VOID_INGOT` at `ItemResource.java:67`, and the item retains legacy registry path `ItemResource`.
- Regression hazards: Preserve metadata `16`, exact tool-before-armor page sequence, and absence of `concealed`/`secondary` despite its Eldritch placement.
- Candidate disposition: preserve.

### A-003-F05 - ESSENTIARESERVOIR declaration matches TC4

- Type: parity
- Severity: none
- Confidence: high
- Source/oracle locator: S-004 `ConfigResearch.class#initEldritchResearch`, CFR `ESSENTIARESERVOIR` statement; `javap` offsets 1646-1797.
- Observed: Port `ConfigResearchEldritch.java:207-225` preserves the exact add-call sequence `WATER 5`, `VOID 3`, `EXCHANGE 3`, `MAGIC 5`, `VOID 5`; coordinates `(4, -3)`; cost/complexity `2`; icon `new ItemStack(ConfigBlocks.blockEssentiaReservoir)`; pages `TEXT tc.research_page.ESSENTIARESERVOIR.1`, `INFUSION_CRAFTING EssentiaReservoir`, `TEXT tc.research_page.ESSENTIARESERVOIR.2`; parents `VOIDMETAL`, `CENTRIFUGE`, `INFUSION`; no flags, hidden parents, warp, or triggers.
- Expected: Exact same duplicate-`VOID` add sequence and all other values in TC4.
- Exact deltas: none. Because `AspectList.add` accumulates an existing key (`AspectList.java:159-168`), both call chains produce effective `VOID 8`; the duplicate calls are intentional oracle parity, not an accidental port-only duplicate.
- Affected paths/symbols: `ConfigResearchEldritch.java:207-225`; `ConfigBlocks.java:119-121`; `ConfigResearch.java:30-31`.
- Evidence/reproduction: CFR and `javap` independently show `VOID 3` followed later by `VOID 5`, the same parent sequence, and `EssentiaReservoir` cast to `InfusionRecipe`. Port block handle retains legacy registry path `blockEssentiaReservoir`.
- Regression hazards: Do not normalize away either `VOID` call without preserving effective amount `8`; preserve parent order and the absence of `secondary`/`concealed`.
- Candidate disposition: preserve.

### A-003-F06 - CAP_void declaration and warp match TC4

- Type: parity
- Severity: none
- Confidence: high
- Source/oracle locator: S-004 `ConfigResearch.class#initEldritchResearch`, CFR `CAP_void` statement and following `ThaumcraftApi.addWarpToResearch("CAP_void", 1)`; `javap` offsets 1798-1964.
- Observed: Port `ConfigResearchEldritch.java:227-247` has aspects `VOID 5`, `ELDRITCH 5`, `TOOL 3`, `MAGIC 3`, `AURA 3`; coordinates `(5, -1)`; cost/complexity `3`; icon `new ItemStack(ConfigItems.itemWandCap, 1, 7)`; pages `TEXT tc.research_page.CAP_void.1`, `ARCANE_CRAFTING WandCapVoidInert`, `INFUSION_CRAFTING WandCapVoid`; flag `concealed`; parents `CAP_thaumium`, `VOIDMETAL`; research warp `1`; no hidden parents or triggers and no item warp.
- Expected: Exact same values, page types/order, and post-registration research warp in TC4.
- Exact deltas: none.
- Affected paths/symbols: `ConfigResearchEldritch.java:227-247`; `ConfigItems.java:247-251`; `ConfigResearch.java:22-23,30-31`.
- Evidence/reproduction: CFR/bytecode casts `WandCapVoidInert` to `IArcaneRecipe` and `WandCapVoid` to `InfusionRecipe`; port typed helpers select the matching page constructors/types. TC4 and port icon handles both use legacy `WandCap` metadata `7`.
- Regression hazards: Preserve lowercase key suffix in `CAP_void`, metadata `7`, mixed arcane/infusion page order, and warp on the research key only.
- Candidate disposition: preserve.

### A-003-F07 - ARMORVOIDFORTRESS declaration matches TC4

- Type: parity
- Severity: none
- Confidence: high
- Source/oracle locator: S-004 `ConfigResearch.class#initEldritchResearch`, CFR `ARMORVOIDFORTRESS` statement; `javap` offsets 1965-2153.
- Observed: Port `ConfigResearchEldritch.java:249-270` has aspects `ARMOR 5`, `ELDRITCH 3`, `CLOTH 3`, `DARKNESS 3`, `VOID 5`; coordinates `(0, -3)`; cost/complexity `3`; icon `new ItemStack(ConfigItems.itemHelmVoidRobe)`; pages `TEXT tc.research_page.ARMORVOIDFORTRESS.1`, then `INFUSION_CRAFTING` handles `VoidRobeHelm`, `VoidRobeChest`, `VoidRobeLegs`; parents `VOIDMETAL`, `ENCHFABRIC`, `ELDRITCHMAJOR`; flags `concealed` and `secondary`; no hidden parents, warp, or triggers.
- Expected: TC4 uses field `ConfigItems.itemHelmetVoidRobe` with exactly the same declaration values and page order.
- Exact deltas: none. The port field spelling changed to `itemHelmVoidRobe`, but it binds the same legacy icon object: `ConfigItems.java:662-666` constructs `ItemVoidRobeArmor` for the head slot and registers legacy path `ItemHelmetVoidFortress`, matching TC4 `ConfigItems#initializeItems`, which assigns `itemHelmetVoidRobe` and registers `ItemHelmetVoidFortress`.
- Affected paths/symbols: `ConfigResearchEldritch.java:249-270`; `ConfigItems.java:123,662-666`; `ConfigResearch.java:30-31`.
- Evidence/reproduction: CFR/bytecode identifies all three page handles as `InfusionRecipe` and the icon as `itemHelmetVoidRobe`; typed port helpers and the mapped port field preserve page type and icon identity.
- Regression hazards: Do not mistake the Java field rename for an icon mismatch; preserve the helmet icon, three-piece page order, three parents, and both flags.
- Candidate disposition: preserve.

### A-003-F08 - SANITYCHECK declaration content matches TC4

- Type: parity
- Severity: none
- Confidence: high
- Source/oracle locator: S-004 `ConfigResearch.class#initEldritchResearch`, CFR `SANITYCHECK` statement; `javap` offsets 1187-1298.
- Observed: Port `ConfigResearchEldritch.java:296-311` has aspects `MIND 5`, `ELDRITCH 3`, `SENSES 5`; coordinates `(2, 2)`; cost/complexity `1`; icon `new ItemStack(ConfigItems.itemSanityChecker)`; pages `TEXT tc.research_page.SANITYCHECK.1`, `INFUSION_CRAFTING SanityCheck`; parent `ELDRITCHMINOR`; no flags, hidden parents, warp, or triggers.
- Expected: Exact same declaration content in TC4.
- Exact deltas: none within the declaration; registration chronology is isolated as A-003-F01.
- Affected paths/symbols: `ConfigResearchEldritch.java:296-311`; `ConfigItems.java:829-833`; `ConfigResearch.java:30-31`.
- Evidence/reproduction: CFR/bytecode casts `SanityCheck` to `InfusionRecipe`; the port helper enforces the same class and page type. TC4 and port both bind the icon to legacy `ItemSanityChecker`.
- Regression hazards: Preserve the unflagged declaration and do not conflate moving the block for chronology with changing its content.
- Candidate disposition: preserve.

## Positive Parity Matrix

| Research | Port lines | Position/cost | Icon | Pages in exact order and type | Parents / hidden parents | Flags | Warp / triggers |
| --- | --- | --- | --- | --- | --- | --- | --- |
| `ADVALCHEMYFURNACE` | `134-153` | `-2,6 / 1` | `blockMetalDevice x1 meta 3` | text `.1`; arcane `AdvAlchemyConstruct`; text `.2`; compound `AdvAlchemyFurnace` | `PRIMPEARL`, `DISTILESSENTIA`, `VISPOWER` / none | secondary | none / none |
| `PRIMALCRUSHER` | `155-177` | `2,5 / 2` | `itemPrimalCrusher` | text `.1`; infusion `PrimalCrusher`; text `.2` | `PRIMPEARL` / `VOIDMETAL`, `ELEMENTALPICK`, `ELEMENTALSHOVEL` | concealed | none / none |
| `VOIDMETAL` | `179-205` | `2,-2 / 2` | `itemResource x1 meta 16` | text `.1`; crucible `VoidMetal`; text `.2`; normal `VoidAxe`, `VoidSword`, `VoidPick`, `VoidShovel`, `VoidHoe`, `VoidHelm`, `VoidChest`, `VoidLegs`, `VoidBoots` | `THAUMIUM`, `ELDRITCHMINOR` / none | none | none / none |
| `ESSENTIARESERVOIR` | `207-225` | `4,-3 / 2` | `blockEssentiaReservoir` | text `.1`; infusion `EssentiaReservoir`; text `.2` | `VOIDMETAL`, `CENTRIFUGE`, `INFUSION` / none | none | none / none |
| `CAP_void` | `227-247` | `5,-1 / 3` | `itemWandCap x1 meta 7` | text `.1`; arcane `WandCapVoidInert`; infusion `WandCapVoid` | `CAP_thaumium`, `VOIDMETAL` / none | concealed | research `1` / none |
| `ARMORVOIDFORTRESS` | `249-270` | `0,-3 / 3` | `itemHelmVoidRobe` -> legacy helmet object | text `.1`; infusion `VoidRobeHelm`; infusion `VoidRobeChest`; infusion `VoidRobeLegs` | `VOIDMETAL`, `ENCHFABRIC`, `ELDRITCHMAJOR` / none | concealed, secondary | none / none |
| `SANITYCHECK` | `296-311` | `2,2 / 1` | `itemSanityChecker` | text `.1`; infusion `SanityCheck` | `ELDRITCHMINOR` / none | none | none / none |

All aspect call chains are recorded in A-003-F02 through A-003-F08. No scoped declaration has an item, entity, or aspect trigger. Only `CAP_void` has scoped warp, exactly research warp `1`; none has item warp.

## Unknowns and Conflicts

- None. CFR source and independent bytecode inspection agree. The `itemHelmetVoidRobe` -> `itemHelmVoidRobe` Java field spelling difference was resolved by both initializers' legacy `ItemHelmetVoidFortress` binding and is not a product discrepancy.

## Test Debt

- No focused guard captures the exact seven-declaration fixture: aspects/amounts, coordinates, cost, icon metadata, flags, exact parent lists, warp/triggers, or complete page type/order.
- `src/test/java/thaumcraft/common/config/ConfigResearchReferenceFingerprintStaticGuardTest.java:35-49,52-67` verifies sorted sets of key/category pairs and text-page keys only. It cannot detect A-003-F01 or most declaration-field regressions.
- `src/test/java/thaumcraft/common/config/ConfigResearchStaticGraphTest.java:38-98,122-143` checks corpus size, reference existence, assets, localization, and cycles, but not equality with TC4 metadata or declaration chronology.
- `src/test/java/thaumcraft/common/config/ConfigRecipesInfusionResearchCoverageStaticGuardTest.java:38-70` checks infusion-handle set coverage, not which research/page owns a handle or page ordering/type.
- Candidate disposition: deferred test debt; no test edits were authorized by this read-only assignment.

## Commands and Tools

Commands run during the completed audit and packet persistence:

```text
git status --short
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigResearch.class --methodname initEldritchResearch --silent true
javap -classpath Thaumcraft-1.7.10-4.2.3.5.jar -p -c thaumcraft.common.config.ConfigResearch
javap -classpath Thaumcraft-1.7.10-4.2.3.5.jar -p thaumcraft.common.config.ConfigItems
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigItems.class --silent true --methodname initializeItems
git branch --show-current
git rev-parse HEAD
sha256sum Thaumcraft-1.7.10-4.2.3.5.jar thaumcraft_src/thaumcraft/common/config/ConfigResearch.class
```

Targeted repository `read`, `glob`, and `grep` inspections covered the paths listed in Source Anchors, the scoped handle initializers, and the three tests listed under Test Debt. `git status --short` was clean before the original read-only audit; during packet persistence it showed only the orchestrator-created untracked `.opencode/active-goal` and goal directory. No product path was modified. No compile, test, build, or smoke command was run because the assignment was static/read-only and changed no product code.

## Handoff

- Terminal status: complete
- Material finding index: A-003-F01 verified low-severity registration-order delta; A-003-F02 through A-003-F08 verified declaration parity controls; focused exact-metadata/order test debt recorded.
- Exact continuation point: none; all requested fields for all seven declarations were compared.
- Smallest next action if continued: Orchestrator normalizes A-003-F01 and parity/test-debt controls into `RECON.md`; no product action is justified by this packet alone until the benign chronology delta is adjudicated.
