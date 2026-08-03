# Audit Packet: A-002 - Primordial declarations

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Assignment-ID: A-002
Status: no_findings
Report-Revision: 1
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Compare the complete research declarations for `PRIMPEARL`, `PRIMNODE`, `FOCUSPRIMAL`, and `ROD_primal_staff` in the Forge 1.12.2 port against TC4 4.2.3.5 `thaumcraft.common.config.ConfigResearch#initEldritchResearch`.
- Fields audited: research key/category, icon argument, graph position, aspect research costs and amounts, complexity, ordinary parents, hidden parents, flags, item/entity/aspect triggers, research and item warp, page count/order/type/arguments, and recipe-handle key/type binding.
- Anti-scope: Recipe definitions, recipe runtime behavior, item/focus/rod/projectile runtime implementation, generic research infrastructure, declarations outside these four keys, product edits, and central Goal Ledger edits.
- Oracle and comparison direction: S-003 `Thaumcraft-1.7.10-4.2.3.5.jar` and its S-004 extracted `thaumcraft/common/config/ConfigResearch.class`, method `initEldritchResearch`, are authoritative; compare TC4 4.2.3.5 -> S-005 Forge 1.12.2 port.
- Questions: Does every listed declaration field match? Are page recipe handles bound to the same key and recipe family? Are any apparent differences required Forge 1.12.2 adaptations? What exact parity remains unguarded by tests?
- Expected evidence: Exact CFR output from the original class; exact port source locations; helper/item-field definitions only where needed to prove equivalent binding; existing test locations needed to identify coverage gaps.
- Read/write permissions: Product files and central ledger files read-only; only this report is writable.
- Effort/tool budget: Targeted declaration audit only; no broad Eldritch re-audit and no runtime investigation.
- Stop conditions: Stop after every field for the four declarations is accounted for, an oracle conflict blocks comparison, or evidence would require entering anti-scope.
- Continuation predecessor: none.

## Coverage Performed

- Port declarations inspected: `src/main/java/thaumcraft/common/config/research/ConfigResearchEldritch.java:92-132`, `:272-294`, and `:313-347`.
- Binding adaptations inspected: `src/main/java/thaumcraft/common/config/research/ConfigResearch.java:18-50`; `src/main/java/thaumcraft/common/config/ConfigItems.java:62`, `:72`, `:241-245`, and `:283-287`.
- Oracle surface inspected: S-004 `thaumcraft/common/config/ConfigResearch.class`, CFR method output for `initEldritchResearch`; S-003 was also supplied directly to CFR.
- Existing test surfaces inspected: `ConfigResearchStaticGraphTest`, `ConfigResearchReferenceFingerprintStaticGuardTest`, `ConfigResearchRecipeLookupTypeAuditTest`, `ConfigResearchRecipeKeyCoverageTest`, `ConfigResearchStrictRecipeLookupStaticGuardTest`, `ConfigResearchAspectTriggerCoverageTest`, `ConfigAspectsEntityTriggerCoverageTest`, `ConfigRecipesInfusionResearchCoverageStaticGuardTest`, and `ConfigRecipesReferenceKeyCorpusStaticGuardTest`.
- Uncovered scope: none inside the assignment contract. Recipe definitions/runtime and shared infrastructure were intentionally not audited.

## Exact Oracle Evidence

The following is the exact relevant CFR output from S-004 `ConfigResearch.class#initEldritchResearch` (CFR 0.152). Line wrapping below is editorial only; identifiers, values, constructor arguments, call order, and recipe casts/keys are unchanged.

```java
new ResearchItem("PRIMPEARL", "ELDRITCH",
    new AspectList().add(Aspect.AIR, 8).add(Aspect.EARTH, 8)
        .add(Aspect.FIRE, 8).add(Aspect.WATER, 8)
        .add(Aspect.ORDER, 8).add(Aspect.ENTROPY, 8),
    0, 4, 1, new ItemStack(ConfigItems.itemEldritchObject, 1, 3))
    .setPages(new ResearchPage("tc.research_page.PRIMPEARL.1"),
        new ResearchPage("tc.research_page.PRIMPEARL.2"))
    .setItemTriggers(new ItemStack(ConfigItems.itemEldritchObject, 1, 3))
    .setLost().setSecondary().setSpecial().setParents("ELDRITCHMINOR")
    .registerResearchItem();

new ResearchItem("PRIMNODE", "ELDRITCH",
    new AspectList().add(Aspect.AURA, 1).add(Aspect.MAGIC, 1)
        .add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1),
    0, 6, 1,
    new ResourceLocation("thaumcraft", "textures/misc/r_nodes_2.png"))
    .setPages(new ResearchPage("tc.research_page.PRIMNODE.1"))
    .setSecondary().setParents("PRIMPEARL").registerResearchItem();
ThaumcraftApi.addWarpToResearch("PRIMNODE", 1);

new ResearchItem("FOCUSPRIMAL", "ELDRITCH",
    new AspectList().add(Aspect.AIR, 6).add(Aspect.WATER, 6)
        .add(Aspect.FIRE, 6).add(Aspect.EARTH, 6)
        .add(Aspect.ORDER, 6).add(Aspect.ENTROPY, 6)
        .add(Aspect.MAGIC, 6),
    4, 1, 2, new ItemStack(ConfigItems.itemFocusPrimal))
    .setPages(new ResearchPage("tc.research_page.FOCUSPRIMAL.1"),
        new ResearchPage((IArcaneRecipe)recipes.get("FocusPrimal")))
    .setConcealed().setParents("ELDRITCHMINOR").registerResearchItem();
ThaumcraftApi.addWarpToResearch("FOCUSPRIMAL", 2);
ThaumcraftApi.addWarpToItem(new ItemStack(ConfigItems.itemFocusPrimal), 1);

new ResearchItem("ROD_primal_staff", "ELDRITCH",
    new AspectList().add(Aspect.AIR, 9).add(Aspect.EARTH, 9)
        .add(Aspect.FIRE, 9).add(Aspect.WATER, 9)
        .add(Aspect.ORDER, 9).add(Aspect.ENTROPY, 9)
        .add(Aspect.TOOL, 9).add(Aspect.MAGIC, 12),
    6, 2, 3, new ItemStack(ConfigItems.itemWandRod, 1, 100))
    .setPages(new ResearchPage("tc.research_page.ROD_primal_staff.1"),
        new ResearchPage((InfusionRecipe)recipes.get("WandRodPrimalStaff")))
    .setHidden().setEntityTriggers("Thaumcraft.PrimalOrb")
    .setItemTriggers(new ItemStack(ConfigItems.itemFocusPrimal))
    .setParents("FOCUSPRIMAL")
    .setParentsHidden("ROD_silverwood_staff", "ROD_bone_staff",
        "ROD_greatwood_staff", "ROD_blaze_staff", "ROD_reed_staff",
        "ROD_obsidian_staff", "ROD_quartz_staff", "ROD_ice_staff")
    .registerResearchItem();
ThaumcraftApi.addWarpToResearch("ROD_primal_staff", 3);
ThaumcraftApi.addWarpToItem(
    new ItemStack(ConfigItems.itemWandRod, 1, 100), 1);
```

Oracle identity is registered in `SOURCES.md`: S-003 JAR SHA-256 `3dba9786966974701578a658d1bb369bf35bdf5f363079f5ac9c4910a39113be`; S-004 extracted `ConfigResearch.class` SHA-256 `7ed2e392b82f75f6636abd57477a2de97566b5d395a0731b4e1e5d64e45aa50d`.

## Atomic Findings

Defect finding result: **none**. No incorrect metadata, omitted metadata, extra metadata, wrong page, or wrong recipe-handle binding was found in the four declarations.

### A-002-F01 - PRIMPEARL declaration has full metadata parity

- Type: parity
- Severity: none
- Confidence: high
- Source/oracle locator: S-004 `ConfigResearch.class#initEldritchResearch`, `new ResearchItem("PRIMPEARL", ...)`; port `ConfigResearchEldritch.java:92-114`.
- Observed: The port declares category `ELDRITCH`; aspect costs `AIR=8`, `EARTH=8`, `FIRE=8`, `WATER=8`, `ORDER=8`, `ENTROPY=8`; position `(0,4)`; complexity `1`; icon Eldritch Object count `1`, metadata `3`; two text pages `.1` then `.2`; item trigger Eldritch Object count `1`, metadata `3`; flags `lost`, `secondary`, and `special`; ordinary parent `ELDRITCHMINOR`; no hidden parents; no entity/aspect trigger; and no research/item warp.
- Expected: The exact TC4 metadata listed above.
- Exact deltas: none.
- Affected paths/symbols: `ConfigResearchEldritch.initEldritchResearchBaseline`, key `PRIMPEARL`.
- Evidence/reproduction: Compare the first oracle statement above to `ConfigResearchEldritch.java:92-114`; every constructor and fluent-call argument agrees.
- Regression hazards: Aspect amount/order or complexity drift changes research cost; item metadata drift breaks discovery; flag drift changes visibility/progression; parent or page-order drift changes graph or book content.
- Candidate disposition: preserve.

### A-002-F02 - PRIMNODE declaration has full metadata and warp parity

- Type: parity
- Severity: none
- Confidence: high
- Source/oracle locator: S-004 `ConfigResearch.class#initEldritchResearch`, `new ResearchItem("PRIMNODE", ...)` and immediately following warp call; port `ConfigResearchEldritch.java:116-132`.
- Observed: The port declares category `ELDRITCH`; aspect costs `AURA=1`, `MAGIC=1`, `ORDER=1`, `ENTROPY=1`; position `(0,6)`; complexity `1`; icon `thaumcraft:textures/misc/r_nodes_2.png`; one text page `.1`; flag `secondary`; ordinary parent `PRIMPEARL`; no hidden parents or triggers; research warp `1`; and no item warp.
- Expected: The exact TC4 metadata listed above.
- Exact deltas: none.
- Affected paths/symbols: `ConfigResearchEldritch.initEldritchResearchBaseline`, key `PRIMNODE`.
- Evidence/reproduction: Compare the second oracle statement and warp call above to `ConfigResearchEldritch.java:116-132`.
- Regression hazards: Parent/secondary drift changes progression placement; icon or page drift affects the Thaumonomicon; missing or altered research warp changes gameplay consequence.
- Candidate disposition: preserve.

### A-002-F03 - FOCUSPRIMAL declaration, arcane page, and warp have full parity

- Type: parity
- Severity: none
- Confidence: high
- Source/oracle locator: S-004 `ConfigResearch.class#initEldritchResearch`, `new ResearchItem("FOCUSPRIMAL", ...)` and its two warp calls; port `ConfigResearchEldritch.java:272-294`; binding helper `ConfigResearch.java:22-24`; item identity `ConfigItems.java:72,283-287`.
- Observed: The port declares category `ELDRITCH`; aspect costs `AIR=6`, `WATER=6`, `FIRE=6`, `EARTH=6`, `ORDER=6`, `ENTROPY=6`, `MAGIC=6`; position `(4,1)`; complexity `2`; Primal Focus icon; text page `.1` followed by an arcane-recipe page bound to key `FocusPrimal`; flag `concealed`; ordinary parent `ELDRITCHMINOR`; no hidden parents or triggers; research warp `2`; and Primal Focus item warp `1`.
- Expected: The exact TC4 metadata and `(IArcaneRecipe)recipes.get("FocusPrimal")` binding shown above.
- Exact deltas: no behavioral delta. The port uses equivalent renamed item field `ConfigItems.focusPrimal` and typed helper `recipeArcane("FocusPrimal")`.
- Affected paths/symbols: `ConfigResearchEldritch.initEldritchResearchBaseline`, key `FOCUSPRIMAL`.
- Evidence/reproduction: `ConfigResearch.recipeArcane` requires and returns `IArcaneRecipe` for the same `FocusPrimal` map key; `ConfigItems.focusPrimal` is the registered `FocusPrimal` instance used for icon and item warp.
- Regression hazards: Recipe family/key or page-order drift can render the wrong recipe or fail initialization; item-identity drift can detach item warp; concealed/parent drift changes availability.
- Candidate disposition: preserve.

### A-002-F04 - ROD_primal_staff declaration, triggers, hidden prerequisites, infusion page, and warp have full parity

- Type: parity
- Severity: none
- Confidence: high
- Source/oracle locator: S-004 `ConfigResearch.class#initEldritchResearch`, `new ResearchItem("ROD_primal_staff", ...)` and its two warp calls; port `ConfigResearchEldritch.java:313-347`; binding helper `ConfigResearch.java:30-32`; item identities `ConfigItems.java:62,72,241-245,283-287`.
- Observed: The port declares category `ELDRITCH`; aspect costs `AIR=9`, `EARTH=9`, `FIRE=9`, `WATER=9`, `ORDER=9`, `ENTROPY=9`, `TOOL=9`, `MAGIC=12`; position `(6,2)`; complexity `3`; Wand Rod count `1`, metadata `100` icon; text page `.1` followed by an infusion-recipe page bound to `WandRodPrimalStaff`; flag `hidden`; entity trigger `Thaumcraft.PrimalOrb`; Primal Focus item trigger; ordinary parent `FOCUSPRIMAL`; hidden parents, in order, `ROD_silverwood_staff`, `ROD_bone_staff`, `ROD_greatwood_staff`, `ROD_blaze_staff`, `ROD_reed_staff`, `ROD_obsidian_staff`, `ROD_quartz_staff`, `ROD_ice_staff`; research warp `3`; and Wand Rod metadata `100` item warp `1`.
- Expected: The exact TC4 metadata and `(InfusionRecipe)recipes.get("WandRodPrimalStaff")` binding shown above.
- Exact deltas: no behavioral delta. The port uses equivalent `ConfigItems.focusPrimal` and typed helper `recipeInfusion("WandRodPrimalStaff")`.
- Affected paths/symbols: `ConfigResearchEldritch.initEldritchResearchBaseline`, key `ROD_primal_staff`.
- Evidence/reproduction: Compare the fourth oracle statement and warp calls above to `ConfigResearchEldritch.java:313-347`; `recipeInfusion` enforces `InfusionRecipe` for the same key.
- Regression hazards: Any hidden-parent omission weakens the intended staff prerequisite set; trigger spelling/item identity drift prevents discovery; rod metadata drift targets another subtype; page type/key drift breaks recipe presentation; warp drift changes gameplay consequence.
- Candidate disposition: preserve.

### A-002-F05 - Exact per-entry primordial metadata lacks a dedicated regression guard

- Type: test_debt
- Severity: low
- Confidence: high
- Source/oracle locator: Port tests listed under Coverage Performed.
- Observed: Broad guards cover graph reference validity, key/category and text-page corpora, recipe-key availability/type families, and trigger referent availability. No focused test pins all constructor values, exact aspect amounts, exact flags, exact trigger arguments, exact ordered parent/hidden-parent lists, page order/type/key, and adjacent warp calls for these four entries.
- Expected: A focused source or runtime metadata guard could preserve the exact A-002-F01 through A-002-F04 controls if future work edits these declarations.
- Exact deltas: No current product-behavior delta; this is regression exposure only.
- Affected paths/symbols: A future test would target `ConfigResearchEldritch.initEldritchResearchBaseline` for `PRIMPEARL`, `PRIMNODE`, `FOCUSPRIMAL`, and `ROD_primal_staff`.
- Evidence/reproduction: `ConfigResearchReferenceFingerprintStaticGuardTest` fingerprints only key/category and text-page-key sets; `ConfigResearchStaticGraphTest` checks existence/cycles rather than exact prerequisites; recipe coverage/type tests do not pin association and page order; entity-trigger coverage checks resolvability rather than exact ownership; no reviewed test pins these warp amounts.
- Regression hazards: A declaration can remain syntactically valid and pass broad corpus/coverage tests while silently changing progression, cost, visibility, discovery, page semantics, or warp.
- Candidate disposition: test debt for orchestrator triage; not a confirmed declaration defect.

## Positive Parity Controls

All controls below are normative preservation evidence from this audit.

- A-002-PC01: Preserve `PRIMPEARL` key/category `PRIMPEARL|ELDRITCH`, position `(0,4)`, complexity `1`, and icon/item trigger `itemEldritchObject x1 meta 3`.
- A-002-PC02: Preserve `PRIMPEARL` ordered aspect costs `AIR 8, EARTH 8, FIRE 8, WATER 8, ORDER 8, ENTROPY 8`.
- A-002-PC03: Preserve `PRIMPEARL` page sequence `TEXT(tc.research_page.PRIMPEARL.1)`, `TEXT(tc.research_page.PRIMPEARL.2)`.
- A-002-PC04: Preserve `PRIMPEARL` flags `lost + secondary + special`, sole ordinary parent `ELDRITCHMINOR`, absence of hidden parents/other triggers, and absence of warp registrations.
- A-002-PC05: Preserve `PRIMNODE` key/category `PRIMNODE|ELDRITCH`, position `(0,6)`, complexity `1`, and icon `thaumcraft:textures/misc/r_nodes_2.png`.
- A-002-PC06: Preserve `PRIMNODE` ordered aspect costs `AURA 1, MAGIC 1, ORDER 1, ENTROPY 1`.
- A-002-PC07: Preserve `PRIMNODE` sole page `TEXT(tc.research_page.PRIMNODE.1)`, sole flag `secondary`, sole ordinary parent `PRIMPEARL`, no hidden parents/triggers, research warp `1`, and no item warp.
- A-002-PC08: Preserve `FOCUSPRIMAL` key/category `FOCUSPRIMAL|ELDRITCH`, position `(4,1)`, complexity `2`, and Primal Focus icon identity.
- A-002-PC09: Preserve `FOCUSPRIMAL` ordered aspect costs `AIR 6, WATER 6, FIRE 6, EARTH 6, ORDER 6, ENTROPY 6, MAGIC 6`.
- A-002-PC10: Preserve `FOCUSPRIMAL` page sequence `TEXT(tc.research_page.FOCUSPRIMAL.1)`, `ARCANE_RECIPE(FocusPrimal)`.
- A-002-PC11: Preserve `FOCUSPRIMAL` sole flag `concealed`, sole ordinary parent `ELDRITCHMINOR`, no hidden parents/triggers, research warp `2`, and Primal Focus item warp `1`.
- A-002-PC12: Preserve `ROD_primal_staff` key/category `ROD_primal_staff|ELDRITCH`, position `(6,2)`, complexity `3`, and Wand Rod icon `x1 meta 100`.
- A-002-PC13: Preserve `ROD_primal_staff` ordered aspect costs `AIR 9, EARTH 9, FIRE 9, WATER 9, ORDER 9, ENTROPY 9, TOOL 9, MAGIC 12`.
- A-002-PC14: Preserve `ROD_primal_staff` page sequence `TEXT(tc.research_page.ROD_primal_staff.1)`, `INFUSION_RECIPE(WandRodPrimalStaff)`.
- A-002-PC15: Preserve `ROD_primal_staff` sole flag `hidden`, entity trigger exact legacy ID `Thaumcraft.PrimalOrb`, Primal Focus item trigger, and ordinary parent `FOCUSPRIMAL`.
- A-002-PC16: Preserve the ordered hidden-parent list `ROD_silverwood_staff`, `ROD_bone_staff`, `ROD_greatwood_staff`, `ROD_blaze_staff`, `ROD_reed_staff`, `ROD_obsidian_staff`, `ROD_quartz_staff`, `ROD_ice_staff`.
- A-002-PC17: Preserve `ROD_primal_staff` research warp `3` and Wand Rod metadata `100` item warp `1`.

## Platform Adaptations

- A-002-AD01: Original `ConfigItems.itemFocusPrimal` is named `ConfigItems.focusPrimal` in the port. `ConfigItems.java:72,283-287` proves it is the registered `FocusPrimal` item used consistently for icon, trigger, and item warp. This is a benign field-name adaptation, not an item-identity delta.
- A-002-AD02: Original direct cast `(IArcaneRecipe)recipes.get("FocusPrimal")` is expressed as `ConfigResearch.recipeArcane("FocusPrimal")`. `ConfigResearch.java:22-24,42-50` uses the same map key while enforcing the same `IArcaneRecipe` family. This is a fail-fast type-safety adaptation with equivalent page binding.
- A-002-AD03: Original direct cast `(InfusionRecipe)recipes.get("WandRodPrimalStaff")` is expressed as `ConfigResearch.recipeInfusion("WandRodPrimalStaff")`. `ConfigResearch.java:30-32,42-50` uses the same map key while enforcing the same `InfusionRecipe` family. This is a fail-fast type-safety adaptation with equivalent page binding.
- A-002-AD04: The original monolithic `thaumcraft.common.config.ConfigResearch` declarations are split into `thaumcraft.common.config.research.ConfigResearchEldritch` in the port. This structural split does not change any audited declaration metadata.

## Unknowns and Conflicts

- Unknowns inside scope: none.
- Oracle conflicts: none. Direct CFR output from the extracted S-004 class supplied the exact method evidence; S-003 remains controlling if extraction provenance is later disputed.
- Residual evidentiary limit: This was a declaration audit. It does not claim that recipe implementations, trigger consumers, warp consumers, pages, or unlocked gameplay execute correctly at runtime.

## Test Debt

- A-002-F05 is the only material test gap found: no dedicated test preserves the complete metadata tuples for all four declarations.
- Existing broad guards provide partial positive coverage but are not substitutes for exact per-entry parity controls.
- No test was added because assignment A-002 was read-only and the user prohibited product edits.

## Commands and Results

- `git status --short` -> no output at audit start and at completion before report materialization; the product worktree was clean.
- `/usr/local/bin/cfr thaumcraft/common/config/ConfigResearch.class --methodname initEldritchResearch --silent true` -> succeeded and emitted the exact original method reproduced above.
- `/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --methodname initEldritchResearch --silent true` -> completed but produced archive-wide output; it was not used as the primary focused evidence.
- `/usr/local/bin/cfr --help jarfilter` -> confirmed CFR's archive class-filter option while narrowing the original archive inspection.
- `/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --jarfilter 'thaumcraft\.common\.config\.ConfigResearch$' --methodname initEldritchResearch --silent true` -> completed; the directly decompiled extracted class remained the concise authoritative evidence surface.
- Build/tests/runtime smoke -> not run. No code changed, declaration parity was established by direct source/class comparison, and runtime behavior was outside scope.

## Handoff

- Terminal status: no_findings.
- Material finding index: A-002-F01 `PRIMPEARL` parity; A-002-F02 `PRIMNODE` parity; A-002-F03 `FOCUSPRIMAL` parity; A-002-F04 `ROD_primal_staff` parity; A-002-F05 exact metadata regression-test debt.
- Defect index: none.
- Preserve-control index: A-002-PC01 through A-002-PC17.
- Adaptation index: A-002-AD01 through A-002-AD04, all benign.
- Exact continuation point: none; assignment scope is exhausted.
- Smallest next action if continued: none. The orchestrator may normalize A-002-F01 through A-002-F05 and the positive controls into the central RECON ledger; that action is outside this report writer's permissions.
