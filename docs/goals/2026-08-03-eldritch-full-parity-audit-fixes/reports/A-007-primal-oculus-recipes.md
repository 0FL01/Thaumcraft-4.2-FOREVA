# Audit Packet: A-007 - Primal and Oculus recipes

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Assignment-ID: A-007
Status: complete
Report-Revision: 1
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Compare the `FocusPrimal`, `WandRodPrimalStaff`, `PrimalCrusher`, and `EldritchEye` recipe handles in the Forge 1.12.2 port recipe slices against the exact TC4 4.2.3.5 `ConfigRecipes.class` methods.
- Anti-scope: Resulting item behavior, projectile/tool/rod runtime, research declaration metadata, research graph/layout/text, and unrelated recipes.
- Oracle and comparison direction: S-003/S-004 TC4 4.2.3.5 `ConfigRecipes.initializeArcaneRecipes` and `ConfigRecipes.initializeInfusionRecipes` -> S-005 Forge 1.12.2 port.
- Questions: For each handle, compare recipe class/type, output item/meta/count/NBT, central input, surrounding inputs and stored order, aspects/vis, instability, research key, and handle binding.
- Expected evidence: CFR decompilation of the exact oracle methods; port source line locators; original and port recipe-factory type evidence; test-coverage inspection.
- Read/write permissions: Product files and central Goal Ledger files read-only; only this report writable.
- Effort/tool budget: Targeted source reads/searches, CFR, `javap`, archive hashing, and Git status/diff inspection; no build or runtime smoke for a report-only audit.
- Stop conditions: All requested fields for all four handles have direct oracle and port evidence, or an unresolvable oracle conflict is recorded.
- Continuation predecessor: none

## Coverage Performed

- Files/symbols inspected:
  - `src/main/java/thaumcraft/common/config/recipes/ConfigRecipesArcaneSlice.java:417-424,731-733` (`FocusPrimal`, `registerArcaneRecipe`).
  - `src/main/java/thaumcraft/common/config/recipes/ConfigRecipesInfusionSlice.java:137-155,253-256,268-270` (`WandRodPrimalStaff`, `registerInfusionRecipe`, `getWandRodCost`).
  - `src/main/java/thaumcraft/common/config/recipes/ConfigRecipesInfusionEquipmentSlice.java:180-190` (`PrimalCrusher`, `EldritchEye`).
  - `src/main/java/thaumcraft/api/ThaumcraftApi.java:120-139` (factory return types and registration).
  - `src/main/java/thaumcraft/api/crafting/ShapedArcaneRecipe.java:17-101,193-210` and `src/main/java/thaumcraft/api/crafting/InfusionRecipe.java:11-30,110-143` (stored recipe contracts/accessors).
  - `src/main/java/thaumcraft/common/config/research/ConfigResearch.java:16-35,42-49` and `src/main/java/thaumcraft/common/config/research/ConfigResearchEldritch.java:48,172,289,331` (handle storage and typed consumers).
  - `src/main/java/thaumcraft/common/Thaumcraft.java:142-145,199-206,365-390` (item/rod initialization and actual `primal_staff` cost).
  - Existing recipe key, family, ordering, and coverage tests under `src/test/java/thaumcraft/common/config/`.
- Oracle surfaces inspected:
  - `thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`, methods `initializeArcaneRecipes` and `initializeInfusionRecipes`, decompiled with CFR 0.152.
  - `thaumcraft_src/thaumcraft/api/ThaumcraftApi.class`, signatures and bytecode for `addArcaneCraftingRecipe` and `addInfusionCraftingRecipe`.
  - The loose oracle `ConfigRecipes.class` and the same class extracted directly from `Thaumcraft-1.7.10-4.2.3.5.jar` both have SHA-256 `311f0ce37e451d13bdc73b94f83a5d1d333ca6cc1f4e3e1594a08edd0dfbca20`.
- Commands/tools used:
  - `git status --short`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class initializeArcaneRecipes --silent true`
  - `/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class initializeInfusionRecipes --silent true`
  - `javap -private thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`
  - `javap -classpath thaumcraft_src -p thaumcraft.api.ThaumcraftApi`
  - `javap -classpath thaumcraft_src -c -p thaumcraft.api.ThaumcraftApi`
  - `sha256sum thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class`
  - `unzip -p Thaumcraft-1.7.10-4.2.3.5.jar thaumcraft/common/config/ConfigRecipes.class | sha256sum`
  - Targeted repository reads and searches for the four handles, factories, consumers, wand cost, and tests.
  - `git diff --stat`
- Uncovered scope: Resulting item behavior and research metadata were intentionally excluded. No manual crafting or client display validation was required for this source-level recipe-definition audit.

## Exact Positive Parity Matrix

| Handle | Recipe type and binding | Research | Output | Central input | Surrounding inputs in stored order | Aspects / vis | Instability | Result |
|---|---|---|---|---|---|---|---|---|
| `FocusPrimal` | Oracle and port call `ThaumcraftApi.addArcaneCraftingRecipe`; both factories return `ShapedArcaneRecipe`; returned instance is stored under `ConfigResearch.recipes["FocusPrimal"]` | `FOCUSPRIMAL` | Primal focus, count 1, meta 0, no NBT | Not applicable to shaped arcane recipes | Shape rows `CQC`, `Q#Q`, `CQC`; `#` = resource meta 15; `Q` = quartz; `C` = diamond. Expanded row-major grid is diamond, quartz, diamond / quartz, resource 15, quartz / diamond, quartz, diamond | Earth 25, Entropy 25, Order 25, Air 25, Fire 25, Water 25, in that insertion order | Not applicable | Exact parity |
| `WandRodPrimalStaff` | Oracle and port call `ThaumcraftApi.addInfusionCraftingRecipe`; both factories return `InfusionRecipe`; returned instance is stored under `ConfigResearch.recipes["WandRodPrimalStaff"]` | `ROD_primal_staff` | Wand rod, count 1, meta 100, no NBT | Wand rod, count 1, meta 2, no NBT | 1. resource meta 15; 2. wand rod meta 1; 3. wand rod meta 3; 4. wand rod meta 4; 5. resource meta 15; 6. wand rod meta 5; 7. wand rod meta 6; 8. wand rod meta 7. Every count is 1 and every stack has no NBT | Air 32, Fire 32, Water 32, Earth 32, Order 32, Entropy 32, Magic 64, in that insertion order | 8 | Exact parity |
| `PrimalCrusher` | Oracle and port call `ThaumcraftApi.addInfusionCraftingRecipe`; both factories return `InfusionRecipe`; returned instance is stored under `ConfigResearch.recipes["PrimalCrusher"]` | `PRIMALCRUSHER` | Primal Crusher, count 1, meta 0, no NBT | Eldritch object, count 1, meta 3, no NBT | 1. resource meta 15; 2. wildcard void pick; 3. wildcard void shovel; 4. resource meta 15; 5. wildcard elemental pick; 6. wildcard elemental shovel. Every count is 1 and every stack has no NBT | Mine 24, Tool 24, Entropy 16, Void 16, Weapon 16, Eldritch 16, Greed 16, in that insertion order | 6 | Exact parity |
| `EldritchEye` | Oracle and port call `ThaumcraftApi.addInfusionCraftingRecipe`; both factories return `InfusionRecipe`; returned instance is stored under `ConfigResearch.recipes["EldritchEye"]` | `OCULUS` | Eldritch object, count 1, meta 0, no NBT | Ender eye, count 1, meta 0, no NBT | 1. resource meta 17; 2. gold ingot. Both counts are 1 and both stacks have no NBT | Eldritch 64, Void 16, Darkness 16, Travel 16, in that insertion order | 5 | Exact parity |

## Atomic Findings

### A-007-F01 - FocusPrimal recipe is exact positive parity

- Type: parity
- Severity: none
- Confidence: high
- Source/oracle locator: S-003/S-004 `ConfigRecipes.initializeArcaneRecipes`; CFR statement beginning `ConfigResearch.recipes.put("FocusPrimal", ThaumcraftApi.addArcaneCraftingRecipe("FOCUSPRIMAL", ...))`.
- Observed: The port registers `FocusPrimal` at `ConfigRecipesArcaneSlice.java:417-424` through the binding helper at `:731-733`.
- Expected: TC4 shaped arcane recipe, handle `FocusPrimal`, research `FOCUSPRIMAL`, primal-focus output, six primal aspects at 25 each, and the exact `CQC/Q#Q/CQC` ingredient mapping.
- Exact deltas: none. Output count/meta/NBT, shape, symbol mapping, aspect values/order, research key, recipe class, and handle binding all match.
- Affected paths/symbols: `ConfigRecipesArcaneSlice.initializeArcaneRecipeBaseline`, `ConfigRecipesArcaneSlice.registerArcaneRecipe`.
- Evidence/reproduction:

```text
Oracle:
ConfigResearch.recipes.put("FocusPrimal",
    ThaumcraftApi.addArcaneCraftingRecipe("FOCUSPRIMAL",
        new ItemStack(ConfigItems.itemFocusPrimal),
        new AspectList().add(EARTH,25).add(ENTROPY,25).add(ORDER,25)
            .add(AIR,25).add(FIRE,25).add(WATER,25),
        "CQC", "Q#Q", "CQC",
        '#', new ItemStack(itemResource,1,15),
        'Q', Items.field_151128_bU,
        'C', Items.field_151045_i));

Port: ConfigRecipesArcaneSlice.java:417-424 has the same handle,
research, output, ordered aspects, rows, and mappings; 1.12 names map
field_151128_bU -> Items.QUARTZ and field_151045_i -> Items.DIAMOND.
```

- Regression hazards: Changing a symbol from a concrete item to an ore key, changing the center resource metadata, changing row order, changing aspect insertion/value, or binding the returned recipe under another handle would break parity.
- Candidate disposition: preserve

### A-007-F02 - WandRodPrimalStaff recipe is exact positive parity

- Type: parity
- Severity: none
- Confidence: high
- Source/oracle locator: S-003/S-004 `ConfigRecipes.initializeInfusionRecipes`; CFR statement beginning `ConfigResearch.recipes.put("WandRodPrimalStaff", ThaumcraftApi.addInfusionCraftingRecipe("ROD_primal_staff", ...))`.
- Observed: The port registers the recipe at `ConfigRecipesInfusionSlice.java:137-155` through `registerInfusionRecipe` at `:253-256`.
- Expected: TC4 infusion recipe with output rod meta 100, instability 8, seven aspect entries based on the `primal_staff` craft cost, silverwood rod meta 2 center, and the exact eight-component sequence.
- Exact deltas: none at runtime. The oracle calls `WandRod.rods.get("primal_staff").getCraftCost()` directly; the port calls `getWandRodCost("primal_staff")`. `Thaumcraft.java:389` registers `new StaffRod("primal", ..., 32, ...)`, whose `StaffRod` contract registers tag `primal_staff`; therefore both recipes store 32 for each primal aspect and 64 for Magic. The registration runs at `Thaumcraft.java:144` before `ConfigRecipes.init()` at `:203`.
- Affected paths/symbols: `ConfigRecipesInfusionSlice.initializeInfusionWandRecipeBaseline`, `ConfigRecipesInfusionSlice.getWandRodCost`, `Thaumcraft.initWandComponents`.
- Evidence/reproduction:

```text
Oracle payload:
handle WandRodPrimalStaff; research ROD_primal_staff;
output itemWandRod x1 meta 100; instability 8;
AIR cost, FIRE cost, WATER cost, EARTH cost, ORDER cost,
ENTROPY cost, MAGIC cost*2;
center itemWandRod x1 meta 2;
components [itemResource:15, itemWandRod:1, itemWandRod:3,
itemWandRod:4, itemResource:15, itemWandRod:5,
itemWandRod:6, itemWandRod:7].

Port: ConfigRecipesInfusionSlice.java:137-155 reproduces that payload
in the same order. Thaumcraft.java:389 fixes cost=32, hence exact stored
aspects [AIR:32,FIRE:32,WATER:32,EARTH:32,ORDER:32,ENTROPY:32,MAGIC:64].
```

- Regression hazards: Recipe construction before rod registration would activate the port helper's zero-cost fallback; changing staff registration cost/tag or component order would also break this preserved recipe payload.
- Candidate disposition: preserve

### A-007-F03 - PrimalCrusher recipe is exact positive parity

- Type: parity
- Severity: none
- Confidence: high
- Source/oracle locator: S-003/S-004 `ConfigRecipes.initializeInfusionRecipes`; CFR statement beginning `ConfigResearch.recipes.put("PrimalCrusher", ThaumcraftApi.addInfusionCraftingRecipe("PRIMALCRUSHER", ...))`.
- Observed: The port registers the recipe at `ConfigRecipesInfusionEquipmentSlice.java:180-186` through `ConfigRecipesInfusionSlice.registerInfusionRecipe`.
- Expected: TC4 infusion recipe with the exact Crusher output, center, aspects, instability, six surrounding stacks, wildcards, research key, and handle.
- Exact deltas: none. Oracle `Short.MAX_VALUE` and port `OreDictionary.WILDCARD_VALUE` are both numeric metadata `32767`.
- Affected paths/symbols: `ConfigRecipesInfusionEquipmentSlice.initializeInfusionEquipmentArmorRecipeBaseline`.
- Evidence/reproduction:

```text
Oracle payload:
handle PrimalCrusher; research PRIMALCRUSHER;
output itemPrimalCrusher x1 meta 0; instability 6;
aspects [MINE:24,TOOL:24,ENTROPY:16,VOID:16,WEAPON:16,ELDRITCH:16,GREED:16];
center itemEldritchObject x1 meta 3;
components [itemResource:15, itemPickVoid:32767,
itemShovelVoid:32767, itemResource:15,
itemPickElemental:32767, itemShovelElemental:32767].

Port: ConfigRecipesInfusionEquipmentSlice.java:180-186 contains the
same values and sequence, spelling metadata 32767 as
OreDictionary.WILDCARD_VALUE.
```

- Regression hazards: Replacing wildcard tool metadata with meta 0 would reject damaged tools; dropping either repeated resource-15 component would lower the recipe requirement; reordered arrays would lose exact stored-order parity even though TC4 infusion matching is multiset-based.
- Candidate disposition: preserve

### A-007-F04 - EldritchEye recipe is exact positive parity

- Type: parity
- Severity: none
- Confidence: high
- Source/oracle locator: S-003/S-004 `ConfigRecipes.initializeInfusionRecipes`; CFR statement beginning `ConfigResearch.recipes.put("EldritchEye", ThaumcraftApi.addInfusionCraftingRecipe("OCULUS", ...))`.
- Observed: The port registers the recipe at `ConfigRecipesInfusionEquipmentSlice.java:188-190` through `ConfigRecipesInfusionSlice.registerInfusionRecipe`.
- Expected: TC4 infusion recipe with eldritch-object meta 0 output, instability 5, specified aspects, ender-eye center, resource-17 and gold components in that order, research `OCULUS`, and handle `EldritchEye`.
- Exact deltas: none. All stacks have count 1 and no NBT.
- Affected paths/symbols: `ConfigRecipesInfusionEquipmentSlice.initializeInfusionEquipmentArmorRecipeBaseline`.
- Evidence/reproduction:

```text
Oracle payload:
handle EldritchEye; research OCULUS;
output itemEldritchObject x1 meta 0; instability 5;
aspects [ELDRITCH:64,VOID:16,DARKNESS:16,TRAVEL:16];
center Items.field_151061_bv (ender eye) x1 meta 0;
components [itemResource:17, Items.field_151043_k (gold ingot)].

Port: ConfigRecipesInfusionEquipmentSlice.java:188-190 reproduces the
same payload and order using Items.ENDER_EYE and Items.GOLD_INGOT.
```

- Regression hazards: Output and central input use visually/semantically related objects but are not interchangeable; changing output metadata or using an ender pearl centrally would break parity.
- Candidate disposition: preserve

### A-007-F05 - Forge/MCP recipe adaptations are benign and preserve the TC4 payload

- Type: benign_delta
- Severity: none
- Confidence: high
- Source/oracle locator: S-003/S-004 recipe statements and `ThaumcraftApi` bytecode; S-005 port recipe/factory sources listed under Coverage Performed.
- Observed: The port uses 1.12 named constants, renamed logical item fields, a shared registration helper, `OreDictionary.WILDCARD_VALUE`, and a null-safe wand-cost lookup.
- Expected: Platform/API adaptations may differ in spelling or control structure but must preserve each recipe object's type and stored payload.
- Exact deltas:
  - Oracle obfuscated `Items.field_151128_bU`, `field_151045_i`, `field_151061_bv`, and `field_151043_k` are port `Items.QUARTZ`, `DIAMOND`, `ENDER_EYE`, and `GOLD_INGOT` respectively.
  - Oracle `ConfigItems.itemFocusPrimal` is port `ConfigItems.focusPrimal`; both are the logical primal-focus output.
  - Oracle inline `ConfigResearch.recipes.put(key, factory(...))` is port `registerArcaneRecipe`/`registerInfusionRecipe`; each helper stores the exact factory return value under the supplied key.
  - Oracle wildcard `Short.MAX_VALUE` is port `OreDictionary.WILDCARD_VALUE`; both equal 32767.
  - Port `getWandRodCost` returns zero for a missing rod instead of throwing as the oracle direct lookup would. This does not alter the audited recipe because `primal_staff` is registered with cost 32 before recipe initialization.
- Affected paths/symbols: The four registration call sites and their two helper methods.
- Evidence/reproduction: Original `javap` confirms the factories construct/return `ShapedArcaneRecipe` and `InfusionRecipe`; port `ThaumcraftApi.java:120-139` does the same. Port helper bindings are at `ConfigRecipesArcaneSlice.java:731-733` and `ConfigRecipesInfusionSlice.java:253-256`.
- Regression hazards: The wand-cost fallback is only benign while lifecycle order guarantees `primal_staff` registration first. Helpers must continue storing the same instance returned by the factory.
- Candidate disposition: preserve

### A-007-F06 - Existing tests do not lock the four exact recipe payloads

- Type: test_debt
- Severity: low
- Confidence: high
- Source/oracle locator: S-005 tests listed below.
- Observed:
  - `src/test/java/thaumcraft/common/config/ConfigRecipesArcaneSliceBehaviorTest.java:70-85,99-126` checks complete arcane key order and two unrelated ingredient contracts; `FocusPrimal` appears only in the expected key list at `:118`.
  - `src/test/java/thaumcraft/common/config/ConfigRecipesReferenceKeyCorpusStaticGuardTest.java:16-24,85,104,187,296` checks source-level key corpus presence, not runtime payloads.
  - `src/test/java/thaumcraft/common/config/ConfigResearchRecipeLookupTypeAuditTest.java:23-37,45-101` checks that lookup families match registration families through regex extraction, not exact recipe instances or fields.
  - `src/test/java/thaumcraft/common/config/ConfigRecipesInfusionResearchCoverageStaticGuardTest.java:19-70` checks infusion key counts and research-page coverage, not output, inputs, aspects, research strings, or instability.
- Expected: A focused behavior test should initialize the relevant slices and assert all matrix fields for these four handles, including returned class and map identity, output count/meta/NBT, research, aspect values, instability where applicable, center, exact ordered component arrays, and the expanded shaped-arcane grid.
- Exact deltas: Production recipe data has no defect; regression coverage is missing for every requested payload field except handle presence/family, plus arcane corpus order.
- Affected paths/symbols: Tests for `ConfigRecipesArcaneSlice`, `ConfigRecipesInfusionSlice`, and `ConfigRecipesInfusionEquipmentSlice`.
- Evidence/reproduction: Repository search found no test that obtains these four recipe handles and asserts `ShapedArcaneRecipe.getInput()` or `InfusionRecipe.getRecipeInput()/getComponents()/getAspects()/getInstability()/getResearch()` against the oracle values.
- Regression hazards: A future metadata, count, NBT, aspect, research-key, instability, center, or component regression can pass current guards as long as the same handle string and broad registration family remain present.
- Candidate disposition: deferred test debt unless promoted by the orchestrator; no product defect requires a fix from this audit.

## Positive Parity

- All four requested handles are bound to the same recipe family and exact factory return type as TC4.
- All four handle keys and internal research keys match exactly.
- All output items, counts, metadata, and absent NBT match exactly.
- All infusion central inputs and ordered surrounding arrays match exactly.
- `FocusPrimal` shape rows, mappings, and expanded row-major ingredient positions match exactly.
- All aspect names, values, and insertion order match exactly; `WandRodPrimalStaff` resolves to exact values 32/64 under the real lifecycle.
- All three infusion instability values match exactly: staff 8, Crusher 6, and Eye 5.
- The port's helpers preserve direct handle identity by storing the same object returned from the recipe factory.
- No recipe-definition defect was found in the assigned surface.

## Unknowns and Conflicts

- None. The loose extracted oracle class exactly matches the class in the authoritative TC4 jar by SHA-256.

## Adaptations

- Named 1.12 vanilla constants replace obfuscated 1.7.10 fields without changing item identity.
- The logical primal-focus field was renamed from `itemFocusPrimal` to `focusPrimal`; recipe output identity is preserved.
- Shared port helpers replace four inline map writes while preserving factory type, return-instance identity, key, and payload.
- `OreDictionary.WILDCARD_VALUE` replaces `Short.MAX_VALUE` with the same numeric metadata 32767.
- The port's wand-cost helper is null-safe. It is a benign lifecycle adaptation for this recipe because `primal_staff` cost 32 is registered before recipe construction; preserving that ordering is required.

## Test Debt

- Existing guards prove key presence, registration family, research-page coverage, and broad arcane order, but not the complete data of any of the four audited recipes.
- Smallest useful missing check: one focused recipe-payload behavior test covering the exact matrix above and asserting factory-list/map instance identity. This would detect all requested recipe-data regressions without auditing resulting item behavior or research metadata.

## Validation

- Oracle authenticity: pass. Loose and jar-extracted `ConfigRecipes.class` SHA-256 values both equal `311f0ce37e451d13bdc73b94f83a5d1d333ca6cc1f4e3e1594a08edd0dfbca20`.
- CFR decompilation: pass. Exact statements for all four handles were recovered from `initializeArcaneRecipes`/`initializeInfusionRecipes`.
- Factory class/type verification: pass. Original `ThaumcraftApi` signatures/bytecode and port source both construct and return `ShapedArcaneRecipe` or `InfusionRecipe` as reported.
- Port source comparison: pass. Every requested field is represented in the positive parity matrix with call-site and helper locators.
- Git scope check before report write: `git status --short` showed only the orchestrator's untracked `.opencode/active-goal` and goal directory; no product changes were present.
- Tests: not run. This audit made no product change and existing tests do not directly validate the four payloads.
- Build: not run. AGENTS.md requires a final rebuild when code changes; this report-only task changes no code.
- Runtime smoke: not required and not run. No common/server behavior or registration code changed.
- Manual client validation: not applicable; visual behavior is outside scope.
- Commit: none.

## Handoff

- Terminal status: complete
- Material finding index: `A-007-F01` FocusPrimal parity; `A-007-F02` WandRodPrimalStaff parity; `A-007-F03` PrimalCrusher parity; `A-007-F04` EldritchEye parity; `A-007-F05` benign Forge/MCP adaptations; `A-007-F06` exact-payload test debt.
- Exact continuation point: none; all assigned recipe fields are covered and no production discrepancy was found.
- Smallest next action if continued: Orchestrator normalizes F01-F05 as preserve controls/benign adaptation evidence and adjudicates F06 under the frozen promotion policy.
