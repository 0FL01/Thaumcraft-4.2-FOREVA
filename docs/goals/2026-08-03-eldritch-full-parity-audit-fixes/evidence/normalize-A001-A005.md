# Normalization Proposal: A-001 through A-005

Goal-ID: `goal-20260803-eldritch-full-parity-audit-fixes`
Promotion policy: `confirmed_in_scope`
Oracle direction: TC4 4.2.3.5 (S-003/S-004) -> Forge 1.12.2 port (S-005)

This is a proposal-only inventory. It does not amend `RECON.md`, `GOAL.md`, or
any report. Local IDs from reports are retained. Labels beginning `A-001-N*`,
`A-004-*`, or `A-005-*` are deterministic labels for material report prose
that had no local ID.

## Index

| Proposed ID | Source local ID | Type | Disposition | Severity | Outcome group |
| --- | --- | --- | --- | --- | --- |
| F-A001-01 | A-001-F01 | benign_delta | preserve | informational | G-DECL-ROOT |
| P-A001-01..07 | A-001 category, ELDRITCHMINOR, ELDRITCHMAJOR, OCULUS, ENTEROUTER, OUTERREV, keys/names | parity | preserve | none | G-DECL-ROOT |
| A-A001-01..03 | A-001 adaptation bullets | benign_delta | preserve | informational | G-DECL-ROOT |
| U-A001-01 | A-001 unknowns | unknown | deferred | low | G-DECL-ROOT |
| T-A001-01 | A-001 test-debt bullets | test_debt | deferred | low | G-DECL-ROOT |
| F-A002-01..04 | A-002-F01..F04 | parity | preserve | none | G-DECL-PRIM |
| T-A002-01 | A-002-F05 | test_debt | deferred | low | G-DECL-PRIM |
| P-A002-01..17 | A-002-PC01..PC17 | parity | preserve | none | G-DECL-PRIM |
| A-A002-01..04 | A-002-AD01..AD04 | benign_delta | preserve | informational | G-DECL-PRIM |
| C-A002-01 | A-002 residual evidentiary limit | constraint | constraint | informational | G-DECL-PRIM |
| F-A003-01 | A-003-F01 | benign_delta | preserve | low | G-DECL-VOID |
| F-A003-02..08 | A-003-F02..F08 | parity | preserve | none | G-DECL-VOID |
| T-A003-01 | A-003 test-debt section | test_debt | deferred | low | G-DECL-VOID |
| P-A004-01..05 | A-004 result/corpus/markup/encoding/packaging | parity | preserve | none | G-LOCALIZATION |
| C-A004-01..02 | A-004 exclusions | constraint | constraint | informational | G-LOCALIZATION |
| T-A004-01..04 | A-004 test gaps | test_debt | deferred | low | G-LOCALIZATION |
| F-A005-01..04 | A-005-F01..F04 | parity | preserve | none | G-RESEARCH-ART |
| T-A005-01 | A-005 test-debt section | test_debt | deferred | low | G-RESEARCH-ART |
| C-A005-01..03 | A-005 gaps/limitations | constraint | constraint | informational | G-RESEARCH-ART |

No item is recommended as `required`: the five reports verify no promoted
defect. `duplicate` is not recommended for overlapping controls; the links
below preserve provenance and explain overlap without deleting either source
claim.

## Inventory

### G-DECL-ROOT

#### F-A001-01 (A-001-F01)
- Type/disposition/severity/confidence: `benign_delta` / `preserve` / informational / high.
- Observed: port `ConfigResearch.init()` calls `initEldritchResearchTextOnlyBaseline()` at `ConfigResearch.java:67`, registering `ELDRITCHMAJOR`, then `initEldritchResearchBaseline()` at `:68`, whose first declaration is `ELDRITCHMINOR` at `ConfigResearchEldritch.java:17-31`.
- Expected and exact delta: TC4 sequence is `ELDRITCHMINOR`, `ELDRITCHMAJOR`, `OCULUS`, `ENTEROUTER`, `OUTERREV`; port sequence is `ELDRITCHMAJOR`, `ELDRITCHMINOR`, `OCULUS`, `ENTEROUTER`, `OUTERREV`. Only the first two entries transpose; no constructor argument, page, parent, trigger, flag, warp, or recipe handle differs.
- Affected symbols/evidence: `ConfigResearch.init()` and the two split methods; CFR and `javap` confirm TC4 order. Current `ResearchCategoryList.research` is a `HashMap`, and roots have distinct coordinates `(1,0)` and `(-1,0)`, so no current user-visible effect was established.
- Disposition reason/hazard: preserve as a documented benign chronology delta, not a speculative reorder. Future ordered callbacks/storage could expose it; changing order must not alter root metadata.

#### P-A001-01 through P-A001-07 (A-001 positive parity, no local IDs)
- Type/disposition/severity/confidence: `parity` / `preserve` / none / high.
- Exact controls: `ELDRITCH` category uses key `ELDRITCH`, icon `thaumcraft:textures/misc/r_eldritch.png`, background `thaumcraft:textures/gui/gui_researchbackeldritch.png`; `ELDRITCHMINOR` is `(1,0)`, complexity `0`, empty aspects, icon `r_eldritchminor.png`, page `.1` plus `VoidSeed`, flags `hidden + round + special`, and no parents/hidden parents/triggers/warp; `ELDRITCHMAJOR` is `(-1,0)`, complexity `0`, empty aspects, icon `r_eldritchmajor.png`, text pages `.1,.2`, flags `stub + hidden + round + special`, and no parents/hidden parents/triggers/recipe/warp; `OCULUS` is `(-2,2)`, complexity `1`, ordered aspects `MIND 3, DARKNESS 3, EXCHANGE 3, TRAVEL 6, ELDRITCH 6`, `EldritchEye` infusion page between text pages `.1,.2`, flags `round + concealed + special`, parents `CRIMSON, ELDRITCHMAJOR`, research warp `6`; `ENTEROUTER` is `(-3,4)`, complexity `1`, icon `r_outer.png`, text page `.1`, flags `stub + hidden + round`, parent `OCULUS`; `OUTERREV` is `(-5,3)`, complexity `1`, ordered aspects `ELDRITCH 4, MIND 4`, text page `.1`, item triggers `blockEldritch` meta `5` then `10`, flags `lost + secondary + special`, parent `ENTEROUTER`; all five IDs/page keys retain exact spelling/case.
- Affected symbols/evidence: `ConfigResearch.java:112-115`; `ConfigResearchEldritch.java:17-90,350-366`; CFR comparison recorded in A-001.
- Duplicate links: overlaps A-005-F02 for category/icon paths; recipe-handle adaptation is separately A-A001-01. Preserve both provenance records.

#### A-A001-01 through A-A001-03 (A-001 adaptations, no local IDs)
- Type/disposition/severity/confidence: `benign_delta` / `preserve` / informational / high.
- Exact adaptations: raw TC4 casts for `recipes.get("VoidSeed")` and `recipes.get("EldritchEye")` become typed `recipeCrucible("VoidSeed")` and `recipeInfusion("EldritchEye")`, retaining key/type/page position; monolithic `initEldritchResearch()` is split into baseline and text-only methods, causing only F-A001-01's order delta while preserving fields; Forge/MCP `ResourceLocation` and `ItemStack` replace 1.7.10 types without changing resource paths, identities, counts, or metadata.
- Affected symbols/evidence: `ConfigResearch.java:18-50`; `ConfigResearchEldritch.java:16,350`; original casts and port helpers.
- Disposition reason: intentional type-safe/structural platform adaptations with equivalent audited behavior. Do not deduplicate as defects.

#### U-A001-01 (A-001 unknowns, no local ID)
- Type/disposition/severity/confidence: `unknown` / `deferred` / low / high for the inspected present behavior.
- Exact unknown: no source conflict or blocking unknown was found, but future/addon registration callbacks that observe insertion order were not proven. Present storage/graph evidence establishes no user-visible effect.
- Affected symbols: `ResearchCategoryList.research`, registration chronology in `ConfigResearch.init()`.
- Reason: retain as a low-consequence deferred hazard; it does not promote F-A001-01 under current evidence.

#### T-A001-01 (A-001 test-debt bullets, no local ID)
- Type/disposition/severity/confidence: `test_debt` / `deferred` / low / high.
- Exact gap: no focused snapshot covers every audited constructor value, aspect/page order, flags, parents, triggers, warp, and recipe-handle type. `ConfigResearchReferenceFingerprintStaticGuardTest.java:35-49,52-67` normalizes keys/page keys into `TreeSet`s and cannot detect order or most field drift; `ConfigResearchStaticGraphTest.java:38-98` catches graph/reference integrity but not field-level parity.
- Reason: defer. No defect is promoted and the report explicitly performed no test edit; existing controls are retained.

### G-DECL-PRIM

#### F-A002-01 through F-A002-04 (A-002-F01..F04)
- Type/disposition/severity/confidence: `parity` / `preserve` / none / high.
- Exact controls: `PRIMPEARL` has `(0,4)`, complexity `1`, icon/trigger `itemEldritchObject x1 meta 3`, aspects `AIR/EARTH/FIRE/WATER/ORDER/ENTROPY` each `8`, text pages `.1,.2`, flags `lost + secondary + special`, parent `ELDRITCHMINOR`, no other triggers/warp; `PRIMNODE` has `(0,6)`, complexity `1`, icon `r_nodes_2.png`, aspects `AURA/MAGIC/ORDER/ENTROPY` each `1`, page `.1`, `secondary`, parent `PRIMPEARL`, research warp `1`; `FOCUSPRIMAL` has `(4,1)`, complexity `2`, aspects `AIR/WATER/FIRE/EARTH/ORDER/ENTROPY/MAGIC` each `6`, text `.1` then `ARCANE_RECIPE(FocusPrimal)`, `concealed`, parent `ELDRITCHMINOR`, research warp `2`, item warp `Primal Focus 1`; `ROD_primal_staff` has `(6,2)`, complexity `3`, icon `itemWandRod x1 meta 100`, aspects `AIR/EARTH/FIRE/WATER/ORDER/ENTROPY/TOOL` each `9` and `MAGIC 12`, text `.1` then `INFUSION_RECIPE(WandRodPrimalStaff)`, `hidden`, entity trigger `Thaumcraft.PrimalOrb`, item trigger Primal Focus, parent `FOCUSPRIMAL`, hidden parents in exact order `ROD_silverwood_staff, ROD_bone_staff, ROD_greatwood_staff, ROD_blaze_staff, ROD_reed_staff, ROD_obsidian_staff, ROD_quartz_staff, ROD_ice_staff`, research warp `3`, rod item warp `1`.
- Affected symbols/evidence: `ConfigResearchEldritch.java:92-132,272-294,313-347`; typed helpers `ConfigResearch.java:22-50`; CFR oracle block in A-002 lines 34-85.
- Exact deltas: none. Port field `focusPrimal` is equivalent to original `itemFocusPrimal`; typed helpers preserve key and recipe family.
- Duplicate links: `PRIMPEARL` overlaps the root graph control in P-A001-02 only by parent/reference context; `PRIMNODE` icon overlaps P-A005-01/P-A005-02. No duplicate disposition recommended.

#### T-A002-01 (A-002-F05)
- Type/disposition/severity/confidence: `test_debt` / `deferred` / low / high.
- Exact gap: no focused guard pins all constructor values, aspect amounts/order, flags, exact trigger arguments, ordered parent/hidden-parent lists, page order/type/key, and adjacent warp calls for the four entries. Existing graph, corpus, recipe-family, and trigger tests check availability/integrity rather than these tuples.
- Reason: defer because A-002 has no defect and the assignment prohibited product/test edits. Preserve F-A002-01..04 as the control surface.

#### P-A002-01 through P-A002-17 (A-002-PC01..PC17)
- Type/disposition/severity/confidence: `parity` / `preserve` / none / high.
- Exact controls: PC01-04 preserve `PRIMPEARL` key/category, `(0,4)`, complexity `1`, meta `3` icon/trigger, ordered six `8` aspects, pages `.1,.2`, `lost + secondary + special`, parent `ELDRITCHMINOR`, and absence of other triggers/warp; PC05-07 preserve `PRIMNODE` key/category, `(0,6)`, complexity `1`, `r_nodes_2.png`, ordered `AURA 1, MAGIC 1, ORDER 1, ENTROPY 1`, page `.1`, `secondary`, parent `PRIMPEARL`, research warp `1`, no item warp; PC08-11 preserve `FOCUSPRIMAL` key/category, `(4,1)`, complexity `2`, seven ordered `6` aspects, pages `TEXT(.1), ARCANE_RECIPE(FocusPrimal)`, `concealed`, parent `ELDRITCHMINOR`, no triggers, research warp `2`, item warp `1`; PC12-17 preserve `ROD_primal_staff` key/category, `(6,2)`, complexity `3`, rod meta `100`, ordered aspects ending `TOOL 9, MAGIC 12`, pages `TEXT(.1), INFUSION_RECIPE(WandRodPrimalStaff)`, hidden/entity/item triggers, parent, all eight ordered hidden parents, research warp `3`, item warp `1`.
- Affected symbols: `ConfigResearchEldritch.java:92-132,272-294,313-347`.
- Duplicate links: F-A002-01..04 are the grouped finding records for these controls; retain both IDs as source-local and normalized traceability, not duplicate dispositions.

#### A-A002-01 through A-A002-04 (A-002-AD01..AD04)
- Type/disposition/severity/confidence: `benign_delta` / `preserve` / informational / high.
- Exact adaptations: `itemFocusPrimal` -> `focusPrimal` while retaining the registered `FocusPrimal` identity; direct `(IArcaneRecipe)recipes.get("FocusPrimal")` -> `recipeArcane("FocusPrimal")`; direct `(InfusionRecipe)recipes.get("WandRodPrimalStaff")` -> `recipeInfusion("WandRodPrimalStaff")`; monolithic `thaumcraft.common.config.ConfigResearch` declarations -> `thaumcraft.common.config.research.ConfigResearchEldritch`, without metadata change.
- Evidence/symbols: `ConfigItems.java:72,283-287`; `ConfigResearch.java:22-50`; `ConfigResearchEldritch` baseline method.
- Disposition reason: preserve equivalent field naming, fail-fast typing, and structural port adaptation.

#### C-A002-01 (A-002 residual evidentiary limit, no local ID)
- Type/disposition/severity/confidence: `constraint` / `constraint` / informational / high.
- Exact constraint: declaration comparison does not prove recipe implementations, trigger consumers, warp consumers, pages, or unlocked gameplay execute correctly at runtime; recipe definitions/runtime and shared infrastructure were anti-scope.
- Reason: preserve audit boundary; later assignments own those surfaces. No runtime gate is proposed for this declaration-only packet.

### G-DECL-VOID

#### F-A003-01 (A-003-F01)
- Type/disposition/severity/confidence: `benign_delta` / `preserve` / low / high.
- Observed/expected/exact delta: port registers `SANITYCHECK` at `ConfigResearchEldritch.java:296-311` after `VOIDMETAL`, `ESSENTIARESERVOIR`, `CAP_void` plus warp, `ARMORVOIDFORTRESS`, and `FOCUSPRIMAL`; TC4 sequence is `... PRIMALCRUSHER, SANITYCHECK, VOIDMETAL, ESSENTIARESERVOIR, CAP_void, ARMORVOIDFORTRESS, FOCUSPRIMAL ...`. Only `SANITYCHECK` moves from immediately after `PRIMALCRUSHER` to after `FOCUSPRIMAL`; its constructor/pages/parent/flags/warp/triggers are unchanged.
- Evidence/impact: CFR and `javap` confirm order; both stores are `HashMap`s and keys/coordinates are distinct, so no current gameplay effect was verified. Order-sensitive callbacks or future collisions could expose it.
- Disposition reason: preserve the exact benign registration-order delta per normalization policy; do not reorder declaration metadata or detach the `CAP_void` warp/adjacent Focus warps.

#### F-A003-02 through F-A003-08 (A-003-F02..F08)
- Type/disposition/severity/confidence: `parity` / `preserve` / none / high.
- Exact controls: `ADVALCHEMYFURNACE` at `(-2,6)`, cost/complexity `1`, `AURA/MAGIC/ORDER/ENTROPY 1`, icon `blockMetalDevice x1 meta 3`, pages `TEXT .1, ARCANE_CRAFTING AdvAlchemyConstruct, TEXT .2, COMPOUND_CRAFTING AdvAlchemyFurnace`, `secondary`, parents `PRIMPEARL,DISTILESSENTIA,VISPOWER`; `PRIMALCRUSHER` at `(2,5)`, cost/complexity `2`, seven aspects `MINE/TOOL/ENTROPY/VOID/WEAPON/ELDRITCH 6`, icon `itemPrimalCrusher`, pages `TEXT .1, INFUSION_CRAFTING PrimalCrusher, TEXT .2`, `concealed`, parent `PRIMPEARL`, hidden parents `VOIDMETAL,ELEMENTALPICK,ELEMENTALSHOVEL`; `VOIDMETAL` at `(2,-2)`, cost/complexity `2`, aspects `METAL 3, ELDRITCH 3, DARKNESS 3, VOID 5`, icon `itemResource x1 meta 16`, 12 pages in exact order `TEXT .1, CRUCIBLE VoidMetal, TEXT .2, NORMAL VoidAxe, VoidSword, VoidPick, VoidShovel, VoidHoe, VoidHelm, VoidChest, VoidLegs, VoidBoots`, parents `THAUMIUM,ELDRITCHMINOR`, no flags/hidden parents/warp/triggers; `ESSENTIARESERVOIR` at `(4,-3)`, cost/complexity `2`, ordered calls `WATER 5, VOID 3, EXCHANGE 3, MAGIC 5, VOID 5` (effective `VOID 8`), pages `TEXT .1, INFUSION EssentiaReservoir, TEXT .2`, parents `VOIDMETAL,CENTRIFUGE,INFUSION`; `CAP_void` at `(5,-1)`, cost/complexity `3`, aspects `VOID 5, ELDRITCH 5, TOOL 3, MAGIC 3, AURA 3`, icon `itemWandCap x1 meta 7`, pages `TEXT .1, ARCANE WandCapVoidInert, INFUSION WandCapVoid`, `concealed`, parents `CAP_thaumium,VOIDMETAL`, research warp `1`; `ARMORVOIDFORTRESS` at `(0,-3)`, cost/complexity `3`, aspects `ARMOR 5, ELDRITCH 3, CLOTH 3, DARKNESS 3, VOID 5`, icon `itemHelmVoidRobe` mapped to legacy helmet object, pages `TEXT .1, INFUSION VoidRobeHelm, VoidRobeChest, VoidRobeLegs`, parents `VOIDMETAL,ENCHFABRIC,ELDRITCHMAJOR`, flags `concealed + secondary`; `SANITYCHECK` at `(2,2)`, cost/complexity `1`, aspects `MIND 5, ELDRITCH 3, SENSES 5`, icon `itemSanityChecker`, pages `TEXT .1, INFUSION SanityCheck`, parent `ELDRITCHMINOR`, no flags/hidden parents/warp/triggers. All have exact absence/presence of other fields stated in the source report.
- Affected symbols/evidence: `ConfigResearchEldritch.java:134-270,296-311`; helpers `ConfigResearch.java:18-39`; `ResearchPage.java:24-88`; CFR/`javap` matrix in A-003.
- Duplicate links: F-A003-08 overlaps the chronology target F-A003-01 but content is distinct; icon/path details overlap A-005-F02. Preserve as separate controls.

#### T-A003-01 (A-003 test-debt section, no local ID)
- Type/disposition/severity/confidence: `test_debt` / `deferred` / low / high.
- Exact gap: no focused guard freezes the seven-declaration aspects/amounts, coordinates, cost, icon metadata, flags, exact parent lists, warp/triggers, complete page type/order, or TC4 chronology. Existing fingerprint tests use sorted key/category and text-page sets; graph tests check references/assets/localization/cycles; infusion coverage checks handle coverage, not owner/order/type.
- Reason: defer; no declaration defect was found and no test edit was authorized. The seven parity findings and F-A003-01 remain preservation controls.

### G-LOCALIZATION

#### P-A004-01 through P-A004-05 (A-004 result/corpus/markup/encoding/packaging, no local IDs)
- Type/disposition/severity/confidence: `parity` / `preserve` / none / high.
- Exact controls: selected English corpus has `56` keys: 1 category, 16 generated names, 16 generated summaries, 23 text pages (and 55 research keys excluding category); values, capitalization, punctuation, intentional trailing spaces, page numbering, markup, image payloads, and absence of printf tokens match after removing only original CRLF `\r`; markup counts are `<BR> 48`, `<IMG> 2`, `</IMG> 2`, `<LINE> 0`, `§m 1`, `§o 2`, `§r 3`, format tokens `0`; image locators are exact `<IMG>thaumcraft:textures/misc/eldritchajor1.png:0:255:255:255:.5</IMG>` and corresponding `eldritchajor2`; both files are BOM-free UTF-8, port LF versus original CRLF is the only line-ending delta in selected bytes; `processResources` emits both `en_us.lang` and `en_US.lang`, byte-identical to port source, preserving lowercase Forge lookup and legacy-case packaging.
- Evidence/symbols: `ResearchItem.java:189-195`; `ResearchCategories.java:25-27`; `ConfigResearchEldritch.java:17-366`; port `en_us.lang:853,1023-1581`; original `en_US.lang:870,1082-1794`; `build.gradle:106-121`.
- Duplicate links: image locator portion overlaps A-005-F03; page-key presence overlaps A-001 and A-005 controls. Preserve exact localization provenance.

#### C-A004-01 through C-A004-02 (A-004 exclusions, no local IDs)
- Type/disposition/severity/confidence: `constraint` / `constraint` / informational / high.
- Exact constraints: audit covers only category/name/text/page keys used by the 16 Eldritch researches and English packaging; unrelated localization is not classified. Port ships only `en_us.lang`; absence of 21 original non-English translations is explicitly outside scope and is not a regression. No translation expansion is promoted.
- Reason: authoritative anti-scope from the report/project boundary; do not create a required localization outcome for non-English locales.

#### T-A004-01 through T-A004-04 (A-004 test gaps, no local IDs)
- Type/disposition/severity/confidence: `test_debt` / `deferred` / low / high.
- Exact gaps: `ConfigResearchStaticGraphTest.java:84-96` checks localization-key presence but not exact values/page corpus/trailing whitespace/markup/original parity; `:100-119` checks image-path resolution but not coordinates/dimensions/scale/order/text placement; `EldritchLocalizationParityTest.java:15-35` covers selected aspect/block/item display values, not the 55 research keys/category; no automated guard covers UTF-8/BOM, exact values, markup counts, page-number equality, or dual case-file generation; no manual in-game Thaumonomicon visual validation covers wrapping/image placement.
- Reason: defer. Static parity is proven and no defect is promoted; visual behavior belongs to the renderer/page audit.

### G-RESEARCH-ART

#### F-A005-01 through F-A005-04 (A-005-F01..F04)
- Type/disposition/severity/confidence: `parity` / `preserve` / none / high.
- Exact controls: all nine PNGs are byte-identical to TC4 with shared hashes and decoded properties: `gui_researchbackeldritch.png` `512x512` RGB opaque; six 32x32 research/category icons RGBA alpha `0..255`; `eldritchajor1.png` `256x256` RGBA alpha `0..251`; `eldritchajor2.png` `256x256` RGBA alpha `0..243`. Exact shared SHA-256 values are, respectively, `6500110c9f988ddf33bd577e73dc2ec566f15c7057857e306b5c69488f394df4`, `c4237a5dfd3843d224ea18f1c655e42064f881d346da248fd97a5315c37115f7`, `d9e700223a4eb573df7242fd434a3b1be19faf75b6d4326a03c87e694160108b`, `77b1ee3da7c8f1f529c2256022f5b3b7a179997e8d5b9af7305ecd616c8a6722`, `293fc96e7645539f6606b687f06a33f53cbd3204025301ee68f1631dde4e5d02`, `433d5ea35fc383fc694a18fdee802da6cc34364b11ae3c229b97810469b0ef8e`, `8f1b937a19f9c2f8c573bb1bfc5cc2dfabb808d1b5a546a657975102c0517e2b`, `67589c6cddf9f2528b6e918e5c92ef7efc884e5245f596dbfc1afdc24bde5f7d`, `8d8a689cfeca11659550e430632dbb75c99dbd0c65831b79ffbb2fc4136dfaa1` in the report matrix order.
- Exact references: category paths `thaumcraft:textures/gui/gui_researchbackeldritch.png` and `.../r_eldritch.png`; research icons `r_eldritchminor.png`, `r_eldritchmajor.png`, `r_outer.png`, `r_outerrev.png`, `r_nodes_2.png`; page image locators use exact lowercase `eldritchajor1/2` paths and `:0:255:255:255:.5`; all nine canonical paths are unique and reachable from `Thaumcraft.postInit()` -> `ConfigResearch.init()` registrations and English keys.
- Affected symbols/evidence: `ConfigResearch.java:91-116`; `ConfigResearchEldritch.java:16-132,350-366`; `Thaumcraft.java:199-207`; `en_us.lang:853,1503-1574`; original matrix in A-005.
- Duplicate links: F-A005-02 overlaps P-A001-01 and declaration portions of F-A002/F-A003; F-A005-03 overlaps P-A004-01; F-A005-04 overlaps lifecycle/reachability portions. Keep all four because payload, declaration, locator, and reachability are different atomic claims.

#### T-A005-01 (A-005 test-debt section, no local ID)
- Type/disposition/severity/confidence: `test_debt` / `deferred` / low / high.
- Exact gap: no focused guard freezes the nine asset hashes and declarations. This is a possible future regression guard, not a confirmed product defect or automatically promoted requirement.
- Reason: defer; binary/reference parity is already evidenced and renderer behavior is outside A-005.

#### C-A005-01 through C-A005-03 (A-005 gaps/limitations, no local IDs)
- Type/disposition/severity/confidence: `constraint` / `constraint` / informational / high.
- Exact constraints: manual in-game visual validation was not run, so exact files/references prove asset parity but not renderer/page-layout parity; renderer implementations, filtering/scaling, UV/layout, GUI composition, and page parsing are explicitly outside A-005; original non-English translation absence is not an asset defect and the unusual `eldritchajor*` spelling was checked only to establish intentional path parity.
- Reason: retain the boundary and defer visual claims to the assigned renderer/page audits. Do not “correct” the historical filename spelling.

## Duplicate/Overlap Links

- `F-A001-01` and `F-A003-01` are distinct benign chronology deltas: the first transposes `ELDRITCHMAJOR`/`ELDRITCHMINOR`; the second moves `SANITYCHECK` from between `PRIMALCRUSHER` and `VOIDMETAL` to after `FOCUSPRIMAL`. Neither is a duplicate.
- `P-A001-01` and `F-A005-02` both cover category/research icon paths; retain the declaration-scope and asset-scope evidence separately.
- `F-A002-01..04` and `P-A002-01..17` are grouped finding and control views of the same four declarations, not competing claims; source IDs remain linked rather than marked duplicate.
- `F-A003-02..08` and `F-A005-02` overlap on icon identities; `F-A003` preserves declaration metadata while `F-A005` preserves asset path resolution.
- `P-A004-01` and `F-A005-03` overlap on the two embedded image locators; localization corpus and asset/reference reachability remain separate proof surfaces.

## Completeness Reconciliation

Every report heading/local index read from A-001 through A-005 is represented above:

- A-001: `A-001-F01`; positive parity headings `Category Registration`, `ELDRITCHMINOR`, `ELDRITCHMAJOR`, `OCULUS`, `ENTEROUTER`, `OUTERREV`, `Keys and Names` -> `P-A001-01..07`; `Benign Forge 1.12 Adaptations` -> `A-A001-01..03`; `Unknowns and Conflicts` -> `U-A001-01`; `Test Debt` -> `T-A001-01`.
- A-002: `A-002-F01`, `A-002-F02`, `A-002-F03`, `A-002-F04`, `A-002-F05`; `A-002-PC01..PC17`; `A-002-AD01..AD04`; `Unknowns and Conflicts` (none, retained as no-item status in the source); residual evidentiary limit -> `C-A002-01`; `Test Debt` is represented by `T-A002-01`.
- A-003: `A-003-F01` through `A-003-F08`; `Positive Parity Matrix` and each `A-003-F02..F08` parity record -> `F-A003-02..08`; `Unknowns and Conflicts` (none, retained as no-item status in the source); `Test Debt` -> `T-A003-01`.
- A-004: `Result`/no verified discrepancy, `Corpus`, `Value And Markup Results`, `Encoding And Bytes`, and `Resource Case Semantics` -> `P-A004-01..05`; `Exclusions` -> `C-A004-01..02`; `Test Gaps` -> `T-A004-01..04`.
- A-005: `A-005-F01`, `A-005-F02`, `A-005-F03`, `A-005-F04`; `Positive Parity` is included in those four controls; `Unknowns and Conflicts` (none, retained as no-item status in the source); `Gaps and Limitations` -> `C-A005-01..03`; `Test Debt` -> `T-A005-01`.

Reconciliation result: all local atomic finding IDs, all positive/parity controls,
all benign adaptations/deltas, all explicit material constraints/unknowns, and
all material test-debt statements in the five reports have a proposal item or
an explicit no-item acknowledgement above. No required outcome is proposed.
