# Audit Packet: A-008 — Eldritch device recipes

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Assignment-ID: A-008
Status: no_findings
Report-Revision: 1
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Compare the `AdvAlchemyConstruct`, `AdvAlchemyFurnace` compound display/trigger recipe, `EssentiaReservoir`, and `SanityCheck` definitions and research handles in `ConfigRecipesArcaneSlice.java`, `ConfigRecipesInfusionDeviceSlice.java`, `ConfigRecipesInfusionEquipmentSlice.java`, and `ConfigRecipes.java` against TC4 4.2.3.5 `ConfigRecipes.class`; verify recipe type, research gating, outputs/count/meta/NBT, every input and its order/wildcard semantics, vis/aspects/instability, trigger registration, compound layout, and research-page handle semantics.
- Anti-scope: Advanced Alchemical Furnace, Essentia Reservoir, and Sanity Checker product runtime beyond what was necessary to establish recipe registration, trigger, sentinel, and page-handle semantics; unrelated recipes; product changes.
- Oracle and comparison direction: S-003/S-004 TC4 4.2.3.5 bytecode and extracted classes -> S-005 Forge 1.12.2 port.
- Questions: Do all four handles have the original type and research gate? Are every output and input stack, count, meta, NBT state, wildcard, aspect/vis amount, instability, compound dimension/list position, and event-7 trigger exact? Do the handles select the same `ResearchPage` constructor and page type?
- Expected evidence: CFR decompilation of the exact original methods; direct reads of the scoped port definitions and typed research consumers; focused existing guard/behavior tests.
- Read/write permissions: Product and central-ledger files read-only; only this report writable.
- Effort/tool budget: One bounded audit session using local CFR, targeted repository reads/searches, and one focused Gradle test invocation.
- Stop conditions: Report a substantiated atomic discrepancy with exact evidence, or establish exact parity/benign adaptation for every scoped field and terminate `no_findings`.
- Continuation predecessor: none

## Coverage Performed

- Files/symbols inspected in S-005:
  - `src/main/java/thaumcraft/common/config/recipes/ConfigRecipesArcaneSlice.java:694-700` — `AdvAlchemyConstruct`.
  - `src/main/java/thaumcraft/common/config/recipes/ConfigRecipesInfusionDeviceSlice.java:218-228` — `EssentiaReservoir`.
  - `src/main/java/thaumcraft/common/config/recipes/ConfigRecipesInfusionEquipmentSlice.java:171-173` — `SanityCheck`.
  - `src/main/java/thaumcraft/common/config/recipes/ConfigRecipesInfusionSlice.java:241-255` — sliced initialization and handle registration.
  - `src/main/java/thaumcraft/common/config/ConfigRecipes.java:80-92,229-265` — initialization, `AdvAlchemyFurnace`, and wand-trigger wiring.
  - `src/main/java/thaumcraft/common/config/research/ConfigResearchEldritch.java:134-153,207-225,296-311` — page order and typed handle consumers.
  - `src/main/java/thaumcraft/common/config/research/ConfigResearch.java:18-49` and `src/main/java/thaumcraft/api/research/ResearchPage.java:35-69,84-88` — handle type enforcement and resulting page types.
  - `src/main/java/thaumcraft/api/ThaumcraftApi.java:120-139`, `src/main/java/thaumcraft/api/crafting/ShapedArcaneRecipe.java:37-101,134-186`, and `src/main/java/thaumcraft/api/crafting/InfusionRecipe.java:19-26,48-81` — construction and research-gate semantics.
  - `src/main/java/thaumcraft/client/gui/GuiResearchRecipe.java:383-428,757-795` and `src/main/java/thaumcraft/common/blocks/BlockHole.java:24-93` — compound-center adaptation semantics.
  - `src/main/java/thaumcraft/api/wands/WandTriggerRegistry.java:12-76` and `src/main/java/thaumcraft/common/CommonProxy.java:48` — trigger key and manager availability.
- Oracle surfaces inspected in S-004:
  - `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`: exact CFR methods `init`, `initializeArcaneRecipes`, `initializeInfusionRecipes`, and `initializeCompoundRecipes`.
  - `thaumcraft_src/thaumcraft/common/config/ConfigResearch.class`: exact CFR method `initEldritchResearch`.
  - `thaumcraft_src/thaumcraft/client/gui/GuiResearchRecipe.class`: exact CFR method `drawCompoundCraftingPage`.
  - `thaumcraft_src/thaumcraft/common/blocks/BlockHole.class`: full CFR decompilation, including the metadata-15 icon branch.
- Commands/tools used:
  - `git status --short`
  - `javap -p thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class --methodname init --silent true`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class --methodname initializeArcaneRecipes --silent true`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class --methodname initializeInfusionRecipes --silent true`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class --methodname initializeCompoundRecipes --silent true`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigResearch.class --methodname initEldritchResearch --silent true`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/client/gui/GuiResearchRecipe.class --methodname drawCompoundCraftingPage --silent true`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/blocks/BlockHole.class --silent true`
  - `./scripts/dev.sh gradle test --tests thaumcraft.common.config.ConfigRecipesArcaneSliceBehaviorTest --tests thaumcraft.common.config.ConfigRecipesInfusionResearchCoverageStaticGuardTest --tests thaumcraft.common.config.ConfigRecipesReferenceKeyCorpusStaticGuardTest --tests thaumcraft.common.items.wands.WandManagerTriggerStaticGuardTest`
- Uncovered scope: Device runtime behavior was expressly excluded. No manual client rendering session was run.

## Atomic Findings

No defect was substantiated. The following atomic entries are parity/preserve evidence or benign deltas, not implementation requirements.

### A-008-F01 — Advanced Alchemical Construct arcane recipe is exact

- Type: parity
- Severity: none
- Confidence: high
- Source/oracle locator: S-004 `ConfigRecipes.class`, CFR `initializeArcaneRecipes`; S-005 `ConfigRecipesArcaneSlice.java:694-700`.
- Observed: The port stores key `AdvAlchemyConstruct` as a `ShapedArcaneRecipe` gated by `ADVALCHEMYFURNACE`. Its output is `ConfigBlocks.blockMetalDevice`, count `4`, meta `3`, with no NBT. Vis is Water `10`, Order `30`, Earth `10`. Shape is exactly `VAV / APA / VAV`; `A` is `blockMetalDevice` count `1` meta `9`, `V` is `itemResource` count `1` meta `16`, and `P` is `itemEldritchObject` count `1` meta `3`. No input is wildcarded.
- Expected: The exact TC4 values above.
- Exact deltas: none.
- Affected paths/symbols: `ConfigRecipesArcaneSlice.initializeArcaneRecipeBaseline`; `ConfigResearch.recipes["AdvAlchemyConstruct"]`.
- Evidence/reproduction: CFR emits the same `ThaumcraftApi.addArcaneCraftingRecipe("ADVALCHEMYFURNACE", new ItemStack(ConfigBlocks.blockMetalDevice, 4, 3), ... "VAV", "APA", "VAV", ...)` payload. The port helper stores the returned recipe object directly under the same key.
- Regression hazards: Output count/meta, primordial-pearl meta `3`, void-metal resource meta `16`, shape position, research string, or aspect amount drift would alter crafting or visibility.
- Candidate disposition: preserve

### A-008-F02 — Advanced Alchemical Furnace compound payload is exact

- Type: parity
- Severity: none
- Confidence: high
- Source/oracle locator: S-004 `ConfigRecipes.class`, CFR `initializeCompoundRecipes`; S-005 `ConfigRecipes.java:239-252`.
- Observed: The port stores `AdvAlchemyFurnace` as a five-element compound `List`: aspects Fire `50`, Water `50`, Order `50`; dimensions `3 x 2 x 3`; and an 18-position item list. In traversal/list order the upper layer is `metalDevice:1, metalDevice:9, metalDevice:1 / metalDevice:9, empty, metalDevice:9 / metalDevice:1, metalDevice:9, metalDevice:1`; the lower layer is `metalDevice:3, metalDevice:3, metalDevice:3 / metalDevice:3, stoneDevice:0, metalDevice:3 / metalDevice:3, metalDevice:3, metalDevice:3`. Every non-empty stack has count `1` and no NBT; no wildcard is used.
- Expected: The exact TC4 aspects, dimensions, stack metas, counts, and list positions above.
- Exact deltas: none in the represented compound structure; the empty-center representation is the benign platform adaptation in A-008-F06.
- Affected paths/symbols: `ConfigRecipes.init`; `ConfigResearch.recipes["AdvAlchemyFurnace"]`.
- Evidence/reproduction: Side-by-side CFR/source comparison of all five list members and all 18 cell positions.
- Regression hazards: The renderer consumes the list sequentially as `j`, then descending `k`, then descending `i`; changing dimensions or list order changes the displayed multiblock even if the multiset remains equal.
- Candidate disposition: preserve

### A-008-F03 — Compound triggers and research-page handle semantics are exact

- Type: parity
- Severity: none
- Confidence: high
- Source/oracle locator: S-004 `ConfigRecipes.class` CFR `initializeCompoundRecipes` and `ConfigResearch.class` CFR `initEldritchResearch`; S-005 `ConfigRecipes.java:253-265`, `ConfigResearchEldritch.java:134-153`, `ConfigResearch.java:22-49`, `ResearchPage.java:61-69`.
- Observed: Event `7` is registered for `blockMetalDevice` meta `3` under mod-id `Thaumcraft` and meta `9` under `Thaumcraft_2`, matching TC4. The `ADVALCHEMYFURNACE` research pages remain text `.1`, the `IArcaneRecipe` handle `AdvAlchemyConstruct`, text `.2`, then the `List` handle `AdvAlchemyFurnace`. The typed helpers require `IArcaneRecipe` and `List`; `ResearchPage(IArcaneRecipe)` produces `ARCANE_CRAFTING`, while `ResearchPage(List)` produces `COMPOUND_CRAFTING`.
- Expected: The exact events, block metas, mod-id partition, page order, handle keys/types, and page constructors above.
- Exact deltas: none in normal mod lifecycle. The port's null guard around trigger registration is behavior-neutral because `CommonProxy.wandManager` is a final eagerly constructed manager before post-init recipe initialization.
- Affected paths/symbols: `ConfigRecipes.init`; `WandTriggerRegistry`; `ConfigResearchEldritch.initEldritchResearchBaseline`; `ConfigResearch.recipeArcane`; `ConfigResearch.recipeList`.
- Evidence/reproduction: Exact CFR expressions and direct port lines cited above; `Thaumcraft.postInit` invokes `ConfigRecipes.init()` before `ConfigResearch.init()`, so both handles exist before strict page lookup.
- Regression hazards: Collapsing the two mod IDs would overwrite one trigger in the nested registry map; changing the compound handle from `List` would select the wrong page type or fail strict lookup.
- Candidate disposition: preserve

### A-008-F04 — Essentia Reservoir infusion recipe is exact

- Type: parity
- Severity: none
- Confidence: high
- Source/oracle locator: S-004 `ConfigRecipes.class`, CFR `initializeInfusionRecipes`; S-005 `ConfigRecipesInfusionDeviceSlice.java:218-228`.
- Observed: The port stores key `EssentiaReservoir` as an `InfusionRecipe` gated by `ESSENTIARESERVOIR`. Output is `blockEssentiaReservoir`, count `1`, default meta `0`, no NBT. Instability is `6`; aspects are Water `8`, Void `8`, Magic `8`, Exchange `8`. Central input is `blockTube` count `1` meta `4`. Components, in declaration order, are `itemResource:16`, `blockJar:0`, `blockJar:0`, `itemResource:16`, `blockJar:0`, `blockJar:0`; each count is `1`, none has NBT, and none is wildcarded.
- Expected: The exact TC4 recipe above.
- Exact deltas: none.
- Affected paths/symbols: `ConfigRecipesInfusionDeviceSlice.initializeInfusionGolemDeviceRecipeBaseline`; `ConfigResearch.recipes["EssentiaReservoir"]`.
- Evidence/reproduction: CFR emits the same output, instability, aspect chain, central stack, and six-element component array. The returned `InfusionRecipe` is stored directly and consumed through `recipeInfusion("EssentiaReservoir")`, preserving `INFUSION_CRAFTING` page type.
- Regression hazards: Tube meta `4`, resource meta `16`, four-jar count, research key, instability, and aspect totals are independently gameplay-significant.
- Candidate disposition: preserve

### A-008-F05 — Sanity Checker infusion recipe is exact

- Type: parity
- Severity: none
- Confidence: high
- Source/oracle locator: S-004 `ConfigRecipes.class`, CFR `initializeInfusionRecipes`; S-005 `ConfigRecipesInfusionEquipmentSlice.java:171-173`.
- Observed: The port stores key `SanityCheck` as an `InfusionRecipe` gated by `SANITYCHECK`. Output is `itemSanityChecker`, count `1`, default meta `0`, no NBT. Instability is `4`; aspects are Mind `24`, Senses `24`, Eldritch `8`. Central input is `itemThaumometer`, count `1`, default meta `0`. Components, in declaration order, are `itemResource` count `1` meta `10`, `itemZombieBrain` count `1` default meta `0`, and vanilla diamond count `1` default meta `0`; no stack has NBT or a wildcard.
- Expected: The exact TC4 recipe above.
- Exact deltas: none.
- Affected paths/symbols: `ConfigRecipesInfusionEquipmentSlice.initializeInfusionEquipmentArmorRecipeBaseline`; `ConfigResearch.recipes["SanityCheck"]`.
- Evidence/reproduction: CFR emits the same output, instability, aspect chain, central stack, and three-element component array. The returned `InfusionRecipe` is stored directly and consumed through `recipeInfusion("SanityCheck")`, preserving `INFUSION_CRAFTING` page type.
- Regression hazards: Mirror-glass resource meta `10`, central Thaumometer, component membership, research key, instability, and aspect totals are independently gameplay-significant.
- Candidate disposition: preserve

### A-008-F06 — Transparent compound-center sentinel is safely adapted to 1.12 empty-stack semantics

- Type: benign_delta
- Severity: none
- Confidence: high
- Source/oracle locator: S-004 `ConfigRecipes.class` CFR `initializeCompoundRecipes`, `BlockHole.class` full CFR, and `GuiResearchRecipe.class` CFR `drawCompoundCraftingPage`; S-005 `ConfigRecipes.java:245-252`, `BlockHole.java:24-93`, and `GuiResearchRecipe.java:383-428,757-795`.
- Observed: TC4 creates `empty = new ItemStack(ConfigBlocks.blockHole, 1, 15)` and places it at compound index `4`. Original `BlockHole` returns its `thaumcraft:empty` icon specifically for meta `15`, making that rendered center visually blank. The 1.12 port places `ItemStack.EMPTY` at the same index. Its compound renderer converts null/empty values to `ItemStack.EMPTY` and skips drawing them.
- Expected: Compound index `4` must be an intentionally blank upper-layer center without a visible item or tooltip.
- Exact deltas: Representation changed from a non-null `blockHole` count `1` meta `15` stack with a transparent icon to canonical `ItemStack.EMPTY`; index and visible result remain unchanged.
- Affected paths/symbols: `ConfigResearch.recipes["AdvAlchemyFurnace"]`; `GuiResearchRecipe.drawCompoundCraftingPage`.
- Evidence/reproduction: Original `BlockHole.func_149691_a(int i, int m)` returns `icon2` when `m == 15`, and `icon2` is registered as `thaumcraft:empty`; original compound rendering draws non-null entries. Port rendering skips `isEmpty(stack)` entries. Both produce one blank cell at list index `4`.
- Regression hazards: Replacing `ItemStack.EMPTY` with a visible block/item, removing the empty check, or moving the sentinel would create a false required block in the research diagram.
- Candidate disposition: preserve

### A-008-F07 — Sliced infusion registration reorders the two distinct recipes without changing matching

- Type: benign_delta
- Severity: none
- Confidence: high
- Source/oracle locator: S-004 `ConfigRecipes.class` CFR `initializeInfusionRecipes`; S-005 `ConfigRecipes.java:87-92`, `ConfigRecipesInfusionDeviceSlice.java:218-228`, and `ConfigRecipesInfusionEquipmentSlice.java:171-173`.
- Observed: TC4 registers `SanityCheck` immediately before `EssentiaReservoir`. The port's slice call order registers `EssentiaReservoir` during the golem/device slice before `SanityCheck` during the equipment slice. Each recipe retains its exact internal component declaration order.
- Expected: Each recipe must preserve its own definition and must resolve without a first-match collision.
- Exact deltas: Global crafting-list order of these two handles is reversed. Their central inputs (`blockTube:4` versus `itemThaumometer`) and outputs are distinct, so neither `InfusionRecipe.matches`, `ThaumcraftApi.getInfusionRecipe`, nor research-handle lookup can confuse them.
- Affected paths/symbols: `ConfigRecipes.init`; `ThaumcraftApi.craftingRecipes` ordering for these two entries.
- Evidence/reproduction: Direct comparison of original method order and port slice call order; inspection of `InfusionRecipe.matches` and `ThaumcraftApi.getInfusionRecipe` confirms no shared match/output key.
- Regression hazards: This remains benign only while the two central inputs and outputs remain distinct; broad recipe reordering should not be justified from this narrow case.
- Candidate disposition: preserve

## Positive Parity

- Preserve A-008-F01: `AdvAlchemyConstruct` must remain a shaped arcane recipe with gate `ADVALCHEMYFURNACE`, output `4 x blockMetalDevice:3`, exact `VAV / APA / VAV` mapping, and Water `10` / Order `30` / Earth `10` vis.
- Preserve A-008-F02: `AdvAlchemyFurnace` must remain a compound `List` with Fire/Water/Order `50`, dimensions `3 x 2 x 3`, and the exact 18-cell sequence.
- Preserve A-008-F03: Keep both event-7 trigger registrations and their separate `Thaumcraft` / `Thaumcraft_2` namespaces; keep the advanced-furnace research's arcane and compound pages in positions 2 and 4 with typed handles.
- Preserve A-008-F04: `EssentiaReservoir` must retain gate `ESSENTIARESERVOIR`, instability `6`, four aspects at `8`, tube-buffer center, and the exact six components.
- Preserve A-008-F05: `SanityCheck` must retain gate `SANITYCHECK`, instability `4`, Mind/Senses/Eldritch `24/24/8`, Thaumometer center, and resource-10/brain/diamond components.
- Preserve A-008-F06: The advanced-furnace upper center remains blank through canonical `ItemStack.EMPTY` plus renderer skip; do not restore the legacy transparent fake stack merely for byte-level similarity.
- Preserve A-008-F07: Do not treat the behavior-neutral cross-slice order reversal as permission to reorder recipes with overlapping match predicates.

## Unknowns and Conflicts

- None. No source conflict or unresolved scope-changing question was found.

## Test Debt

- `ConfigRecipesArcaneSliceBehaviorTest` proves the arcane key/order corpus but does not assert `AdvAlchemyConstruct` output, shape mapping, vis, research gate, or absence of wildcards/NBT.
- `ConfigRecipesInfusionResearchCoverageStaticGuardTest` proves key/page coverage but does not field-assert `EssentiaReservoir` or `SanityCheck` output, central input, component sequence, instability, aspects, wildcard, or NBT state.
- `ConfigRecipesReferenceKeyCorpusStaticGuardTest` proves only that all four keys occur.
- `WandManagerTriggerStaticGuardTest` asserts the meta-9 `Thaumcraft_2` event-7 registration but does not independently assert the meta-3 `Thaumcraft` event-7 registration or compound payload.
- No focused test asserts the `AdvAlchemyFurnace` five-element list schema, `3 x 2 x 3` dimensions, all 18 positions, typed `List` page handle, or `ItemStack.EMPTY` sentinel index.
- No manual client test in this audit opened the `ADVALCHEMYFURNACE` compound research page to confirm the transparent center and isometric placement. Source-level oracle/renderer evidence is high confidence, but visual smoke remains the smallest proof for display parity.
- These gaps did not substantiate a product defect and were not promoted by this read-only assignment.

## Validation

- Focused command: `./scripts/dev.sh gradle test --tests thaumcraft.common.config.ConfigRecipesArcaneSliceBehaviorTest --tests thaumcraft.common.config.ConfigRecipesInfusionResearchCoverageStaticGuardTest --tests thaumcraft.common.config.ConfigRecipesReferenceKeyCorpusStaticGuardTest --tests thaumcraft.common.items.wands.WandManagerTriggerStaticGuardTest`
- Result: `BUILD SUCCESSFUL in 6s`; all selected tests passed.
- Read-only integrity: `git status --short` was empty at the start and end of the original audit session. This packet was added only after the user explicitly requested persistence; no product or central-ledger file was edited by A-008.
- Runtime smoke: not required and not run because this was a read-only recipe/page audit and device runtime was explicitly excluded.
- Build: not required and not run because no Java/resource/product file changed.

## Handoff

- Terminal status: no_findings
- Material finding index: A-008-F01 parity `AdvAlchemyConstruct`; A-008-F02 parity `AdvAlchemyFurnace` payload; A-008-F03 parity triggers/page handles; A-008-F04 parity `EssentiaReservoir`; A-008-F05 parity `SanityCheck`; A-008-F06 benign transparent-sentinel adaptation; A-008-F07 benign cross-slice registration order; test debt only, no defects.
- Exact continuation point: none; scoped audit is complete.
- Smallest next action if continued: Orchestrator accepts this packet and normalizes A-008-F01 through A-008-F07 plus the explicit test-debt entries into central RECON; A-008 must not edit the central ledger.
