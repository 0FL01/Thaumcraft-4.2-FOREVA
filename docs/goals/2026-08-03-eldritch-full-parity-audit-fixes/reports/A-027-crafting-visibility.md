# Audit Packet: A-027 - Crafting visibility and enforcement

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Assignment-ID: A-027
Status: complete
Report-Revision: 1
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Compare actual crafting visibility and enforcement for Eldritch recipes against TC4 4.2.3.5: research keys on recipes, arcane/crucible/infusion/normal crafting gates, containers/tables, JEI `ResearchVisibility`, server-side enforcement, wildcard/output matching, and the distinction between Thaumonomicon page handles and ability to craft.
- Anti-scope: Product changes, central Goal Ledger changes, unrelated recipe/item/runtime behavior, and JEI redesign.
- Oracle and comparison direction: Exact TC4 4.2.3.5 bytecode decompiled with CFR 0.152 -> Forge 1.12.2 port source and existing focused tests.
- Read/write permissions: Product files and central ledger read-only; only this report writable.
- Stop conditions: Each Eldritch recipe gate is classified as parity, defect, hidden-valid recipe, or test gap, with server/client visibility distinguished.

## Coverage Performed

- Port recipe registration and research declarations:
  - `src/main/java/thaumcraft/common/config/recipes/ConfigRecipesCrucibleSlice.java`
  - `ConfigRecipesArcaneSlice.java`
  - `ConfigRecipesInfusionSlice.java`
  - `ConfigRecipesInfusionEquipmentSlice.java`
  - `ConfigRecipesSpecialSlice.java`
  - `src/main/java/thaumcraft/common/config/research/ConfigResearchEldritch.java`
- Port execution paths:
  - `ShapedArcaneRecipe`, `ShapelessArcaneRecipe`, `InfusionRecipe`, `InfusionEnchantmentRecipe`, `CrucibleRecipe`
  - `ThaumcraftCraftingManager`, `ContainerArcaneWorkbench`, `SlotCraftingArcaneWorkbench`
  - `ContainerThaumatorium`, `TileThaumatorium`, `TileCrucible`, `TileInfusionMatrix`
- Port visibility paths:
  - `ThaumcraftJeiPlugin`, `JeiRecipeData`, `ResearchVisibility`, `ResearchVisibilityTicker`
  - `ThaumcraftApi.getCraftingRecipeKey` and `ResearchPage`
- Exact TC4 classes decompiled:
  - `thaumcraft/common/config/ConfigRecipes.class`
  - `thaumcraft/common/config/ConfigResearch.class`
  - `thaumcraft/api/ThaumcraftApi.class`, `ResearchPage.class`
  - `ShapedArcaneRecipe.class`, `ShapelessArcaneRecipe.class`, `InfusionRecipe.class`, `InfusionEnchantmentRecipe.class`, `CrucibleRecipe.class`
  - `ThaumcraftCraftingManager.class`, `ContainerArcaneWorkbench.class`, `SlotCraftingArcaneWorkbench.class`, `ContainerThaumatorium.class`, `TileThaumatorium.class`, `TileCrucible.class`, `TileInfusionMatrix.class`
- Existing tests inspected:
  - `ArcaneRecipeMatcherBehaviorTest`, `ArcaneWorkbenchRuntimeIntegrationTest`
  - `CrucibleResearchGateStaticGuardTest`, `TileCrucibleSmeltContractStaticGuardTest`
  - `ContainerThaumatoriumProgrammingParityStaticGuardTest`
  - `ThaumcraftCraftingManagerInfusionMatcherStaticGuardTest`
  - `JeiRecipeDataTest`, `ResearchVisibilityTest`, `ConfigRecipesInfusionResearchCoverageStaticGuardTest`
- No product or central-ledger file was edited.

## Exact Recipe-Gate Matrix

| Recipe/handle | Type | Port recipe research key | TC4 execution gate | Port execution gate | JEI visibility key | Result |
| --- | --- | --- | --- | --- | --- | --- |
| `VoidSeed` | crucible | `VOIDMETAL` | `VOIDMETAL` | server crucible matcher checks thrower research | `VOIDMETAL` | parity |
| `VoidMetal` | crucible | `VOIDMETAL` | `VOIDMETAL` | server crucible matcher checks thrower research | `VOIDMETAL` | parity |
| `AdvAlchemyConstruct` | arcane | `ADVALCHEMYFURNACE` | same | `IArcaneRecipe.matches` checks player research | same | parity |
| `WandCapVoidInert` | arcane | `CAP_void` | same | arcane matcher checks player research | same | parity |
| `FocusPrimal` | arcane | `FOCUSPRIMAL` | same | arcane matcher checks player research | same | parity |
| `EldritchEye` | infusion | `OCULUS` | same | infusion matcher checks player research | same | parity |
| `PrimalCrusher` | infusion | `PRIMALCRUSHER` | same | infusion matcher checks player research | same | parity |
| `SanityCheck` | infusion | `SANITYCHECK` | same | infusion matcher checks player research | same | parity |
| `EssentiaReservoir` | infusion | `ESSENTIARESERVOIR` | same | infusion matcher checks player research | same | parity |
| `WandCapVoid` | infusion | `CAP_void` | same | infusion matcher checks player research | same | parity |
| `WandRodPrimalStaff` | infusion | `ROD_primal_staff` | same | infusion matcher checks player research | same | parity |
| `VoidRobeHelm`, `VoidRobeChest`, `VoidRobeLegs` | infusion | `ARMORVOIDFORTRESS` | same | infusion matcher checks player research | same | parity |
| `VoidHelm`, `VoidChest`, `VoidLegs`, `VoidBoots`, `VoidShovel`, `VoidPick`, `VoidAxe`, `VoidHoe`, `VoidSword` | normal Forge crafting | no recipe research key | no research gate | no Thaumcraft research gate; normal Forge rules apply | not custom JEI research visibility | intentional TC4 semantics, with limited-crafting edge |

The recipe keys above are the execution keys, not necessarily the research item/page keys used to display them. `VoidSeed` is shown on `ELDRITCHMINOR` but executes under `VOIDMETAL`; the port and TC4 both use that distinction.

## Atomic Findings

### A-027-F01 - Dynamic wand and sceptre recipes are omitted from JEI

- Type: hidden_valid_recipe
- Severity: moderate
- Confidence: high
- Source/oracle locator: Port `src/main/java/thaumcraft/client/integration/jei/JeiRecipeData.java:114-148`; `ArcaneWandRecipe.java:88-119`; `ArcaneSceptreRecipe.java:99-139`; `ThaumcraftJeiPlugin.java:49-70`; original `ConfigRecipes.class` dynamic recipe insertion and decompiled `ThaumcraftCraftingManager.findMatchingArcaneRecipe`.
- Observed: JEI adapts shaped and shapeless arcane recipes, but explicitly skips other `IArcaneRecipe` implementations. `ArcaneWandRecipe` and `ArcaneSceptreRecipe` therefore do not receive wrappers. Their outputs are dynamic NBT/tagged wand stacks, and `getRecipeOutput()` is empty.
- Expected: The recipes remain craftable and research-gated, but there is no JEI entry for the dynamic output combinations. This is a discoverability omission, not a server enforcement bypass.
- Reproduction: Complete the relevant cap and rod research, place compatible cap/rod inputs in the Arcane Workbench, and provide vis. A wand is craftable while no corresponding custom JEI recipe is registered. Sceptres additionally require `SCEPTRE`.
- Eldritch impact: Void-cap wand combinations and combinations using `ROD_primal_staff` are valid after research but are absent from custom JEI recipe browsing.
- Regression hazards: Any future JEI adapter must preserve dynamic output NBT, cap/rod research checks, vis calculation, and the existing optional client boundary.
- Test gap: Existing runtime tests prove dynamic wand/sceptre matching and research enforcement; JEI tests only prove collection of fixed recipes and generic skipped-recipe accounting. No test asserts this intentional omission or provides a dynamic wrapper contract.
- Candidate disposition: document as a hidden-valid recipe; no product fix was authorized by this audit.

### A-027-F02 - `doLimitedCrafting` creates a normal Void equipment edge in the Arcane Workbench

- Type: platform_edge
- Severity: low
- Confidence: high for the Arcane Workbench path
- Source/oracle locator: Port `src/main/java/thaumcraft/common/container/ArcaneWorkbenchRecipeResolver.java:24-31,54-59`; normal Void registration `ConfigRecipesSpecialSlice.java:536-636`; TC4 decompiled `ConfigRecipes.oreDictRecipe` and `initializeNormalRecipes`.
- Observed: The nine Void armor/tool recipes are ordinary `ShapedOreRecipe`s with no research key. The port resolves ordinary recipes through a 1.12 recipe-book check when `doLimitedCrafting` is enabled.
- Expected: TC4 registered the same ordinary recipes directly in the global crafting manager and had no 1.12 recipe-book gate. With the gamerule enabled, a fresh port player without the recipe-book unlock can be rejected by the Arcane Workbench even though the Void recipe has valid ingredients and TC4 would accept it.
- Reproduction: Enable `doLimitedCrafting`, use a player lacking the relevant recipe-book entry, and put a valid Void equipment pattern into the Arcane Workbench. The resolver returns no vanilla result until the ordinary recipe is unlocked.
- Scope: This does not add or remove a Thaumcraft research requirement. It is a 1.12 platform rule applied to an otherwise ungated normal recipe. Normal crafting-table behavior was not runtime-tested.
- Test gap: No test covers `doLimitedCrafting` with normal Void recipes or compares Arcane Workbench and ordinary crafting-table behavior.
- Candidate disposition: retain as an explicit platform edge unless compatibility policy requires bypassing recipe-book gating for Thaumcraft normal recipes.

### A-027-F03 - Thaumonomicon page handles do not enforce normal Void equipment recipes

- Type: positive_parity
- Severity: informational
- Confidence: high
- Source/oracle locator: Port normal recipe registration and bridge handles `ConfigRecipesSpecialSlice.java:536-636`; page wiring `ConfigResearchEldritch.java:191-203`; `ThaumcraftApi.getCraftingRecipeKey`; TC4 decompiled `ConfigRecipes.java:468-476,513-516` and `ResearchPage`.
- Observed: The nine normal Void recipes are stored under `VoidHelm` through `VoidSword` handles and displayed as normal crafting pages under `VOIDMETAL`, but the ordinary recipe objects carry no research key and are not checked by Thaumcraft research matchers.
- Expected: This is TC4 behavior. A player with Void ingots can craft the equipment regardless of `VOIDMETAL` completion; before research, the associated Thaumonomicon page handle may remain hidden through `getCraftingRecipeKey`.
- Effect: Page visibility and craftability intentionally diverge. JEI custom research hiding applies only to custom arcane/crucible/infusion wrappers, so these normal recipes are not hidden by `ResearchVisibility` as Thaumcraft recipes.
- Candidate disposition: positive parity; do not convert page handles into recipe gates without an explicit behavior change.

### A-027-F04 - Programmed Thaumatorium recipes bypass the current opener's research by inherited design

- Type: positive_parity
- Severity: informational
- Confidence: high
- Source/oracle locator: Port `ContainerThaumatorium.java:72-86`, `:90-116`; `TileThaumatorium.java:95-159`; original CFR `ContainerThaumatorium.java:61-101` and `TileThaumatorium.java:183-242`.
- Observed: Initial programming requires current-player research completion and catalyst match. Once the recipe hash, essentia requirements, and programmer name are persisted, server execution selects the stored recipe and does not recheck the current player's research.
- Expected: TC4 has the same behavior. This is machine-state automation, not a port research bypass.
- Reproduction: Player A researches and programs a recipe; Player B without that research supplies input/essentia to the already-programmed Thaumatorium. The machine can complete the stored recipe.
- Regression hazards: Preserve research gating during programming, recipe hash persistence, stored essentia/player arrays, and execution-time catalyst/aspect validation.
- Test gap: Existing static guards cover programming checks and persistence shape, but no runtime cross-player test demonstrates the inherited behavior.
- Candidate disposition: positive parity; no fix indicated.

## Server-Side Enforcement and Matching

- Arcane: `ShapedArcaneRecipe.matches` and `ShapelessArcaneRecipe.matches` reject incomplete research. The port's Arcane Workbench re-resolves the current player and expected output in `canTakeResult`/`prepareCraft`, then consumes vis only after validation. This is stronger against stale previews than relying on client display state.
- Infusion: `InfusionRecipe.matches` and `InfusionEnchantmentRecipe.matches` gate research before central-input and component matching. `TileInfusionMatrix.craftingStart` calls these matchers on the server-side interaction path.
- Crucible: `ThaumcraftCraftingManager.findMatchingCrucibleRecipe` copies the catalyst to count one, checks `ResearchManager.isResearchComplete(username, recipe.key)`, then checks aspects/catalyst and chooses the most aspect-specific match. `TileCrucible` obtains the thrower from the entity and falls back to thrower NBT, matching TC4.
- Thaumatorium: programming gates research; persisted recipe execution does not re-gate, matching TC4.
- Wildcards and output: Infusion matching preserves wildcard metadata and crafting NBT comparisons. JEI expands wildcard infusion inputs through subtype and ore alternatives, copies required tags, and builds dynamic NBT overlay outputs without mutating source stacks. The Primal Crusher's wildcard Void/Elemental tool components therefore have matching acceptance semantics in execution and display data.

## JEI ResearchVisibility Result

- `ThaumcraftJeiPlugin` collects only custom arcane, crucible, and infusion wrappers and tracks nonempty wrapper research keys.
- `ResearchVisibility` hides all tracked wrappers initially, then unhides them only when the exact key is present in the client capability snapshot. Player/world identity changes fail closed.
- `PacketResearchComplete` and `PacketSyncResearch` update the client research snapshot; server crafting never trusts this client visibility state.
- Normal Void recipes and dynamic wand/sceptre implementations are not represented by the custom wrapper set. Normal Void visibility therefore follows ordinary JEI/Forge recipe behavior, while dynamic wand/sceptre recipes are the hidden-valid omission in A-027-F01.

## Positive Parity

- All fixed Eldritch recipe execution keys match TC4: `VOIDMETAL`, `ADVALCHEMYFURNACE`, `CAP_void`, `FOCUSPRIMAL`, `OCULUS`, `PRIMALCRUSHER`, `SANITYCHECK`, `ESSENTIARESERVOIR`, `ARMORVOIDFORTRESS`, and `ROD_primal_staff`.
- Arcane, infusion, and crucible server gates are attached to recipe matching rather than only to page rendering.
- Normal Void equipment retains TC4's intentionally ungated ordinary crafting semantics.
- Thaumatorium programming and persisted automation behavior match TC4.
- JEI fixed-recipe research hiding uses exact recipe keys and preserves wildcard/NBT display matching for the adapted infusion recipes.

## Test Debt

- Add a JEI contract test documenting that `ArcaneWandRecipe` and `ArcaneSceptreRecipe` are skipped, or add a dynamic adapter with output-NBT coverage if product behavior changes.
- Add an Arcane Workbench test with `doLimitedCrafting=true` and a normal Void recipe, plus an ordinary crafting-table control.
- Add a runtime cross-player Thaumatorium test proving programming is gated but stored automation is not re-gated.
- Add an Eldritch matrix test that instantiates representative locked/unlocked arcane, crucible, infusion, normal, and wildcard-output cases.
- Add runtime coverage for Primal Crusher wildcard component acceptance and the corresponding JEI alternatives.

## Commands and Results

All commands were run from the repository root. No build or runtime smoke was required because this was a read-only audit and only documentation was persisted.

```text
git status --short
# Before report persistence: existing untracked .opencode/active-goal and docs/goals/... artifacts; no product edits

/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class --outputdir /home/stfu/.local/share/opencode/tool-output/tc4-i08/config --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/api/crafting/ShapedArcaneRecipe.class --outputdir /home/stfu/.local/share/opencode/tool-output/tc4-i08/api-shaped --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/api/crafting/ShapelessArcaneRecipe.class --outputdir /home/stfu/.local/share/opencode/tool-output/tc4-i08/api-shapeless --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/api/crafting/CrucibleRecipe.class --outputdir /home/stfu/.local/share/opencode/tool-output/tc4-i08/api-crucible --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/api/crafting/InfusionRecipe.class --outputdir /home/stfu/.local/share/opencode/tool-output/tc4-i08/api-infusion --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/api/crafting/InfusionEnchantmentRecipe.class --outputdir /home/stfu/.local/share/opencode/tool-output/tc4-i08/api-enchant --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/lib/crafting/ThaumcraftCraftingManager.class --outputdir /home/stfu/.local/share/opencode/tool-output/tc4-i08/manager --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/container/ContainerArcaneWorkbench.class --outputdir /home/stfu/.local/share/opencode/tool-output/tc4-i08/container-arcane --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/container/SlotCraftingArcaneWorkbench.class --outputdir /home/stfu/.local/share/opencode/tool-output/tc4-i08/slot-arcane --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/container/ContainerThaumatorium.class --outputdir /home/stfu/.local/share/opencode/tool-output/tc4-i08/container-thaumatorium --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/tiles/TileThaumatorium.class --outputdir /home/stfu/.local/share/opencode/tool-output/tc4-i08/tile-thaumatorium --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/tiles/TileCrucible.class --outputdir /home/stfu/.local/share/opencode/tool-output/tc4-i08/tile-crucible --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/tiles/TileInfusionMatrix.class --outputdir /home/stfu/.local/share/opencode/tool-output/tc4-i08/tile-infusion --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/api/ThaumcraftApi.class --outputdir /home/stfu/.local/share/opencode/tool-output/tc4-i08/api-main --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/api/research/ResearchPage.class --outputdir /home/stfu/.local/share/opencode/tool-output/tc4-i08/research-page --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigResearch.class --outputdir /home/stfu/.local/share/opencode/tool-output/tc4-i08/config-research --silent true
git diff --stat
# Before report persistence: empty
```

Runtime smoke and build were skipped: no product/common code was changed. No commit was created.

## Handoff

- Terminal status: complete
- Material finding index: `A-027-F01` moderate/high-confidence hidden-valid dynamic wand/sceptre JEI recipes; `A-027-F02` low/high-confidence `doLimitedCrafting` Arcane Workbench edge; `A-027-F03` positive parity for normal Void page-handle versus craftability semantics; `A-027-F04` positive parity for persisted Thaumatorium programming behavior; fixed Eldritch recipe-gate matrix and server-side enforcement verified.
- No product or central-ledger edit is proposed by this report.
