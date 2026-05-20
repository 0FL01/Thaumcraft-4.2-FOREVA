# DOCS_CHANGESET.md

## Purpose

This file is an editorial action plan for aligning the repository documentation with the current backend/common/server-side code. It is not a new audit narrative. It tells the maintainer what to delete, keep, downgrade, split, or add in the progress and stage docs.

Source priority used here:

1. `AGENTS.md`
2. `docs/PRD.md`
3. `docs/GOAL.md`
4. `docs/GOAL_PROGRESS.md`
5. `docs/Stage*.md`
6. production code
7. previous backend audit

Backend reality to preserve in docs:

- The port has substantial production code in lifecycle, registries, recipes, arcane crafting, research data, scan/research model, tiles, entities, and worldgen.
- That does not make the backend parity-complete.
- Static guard coverage and `validate --smoke` are useful signals, but they do not prove gameplay parity or malicious packet safety.
- The highest-risk backend gaps are research/progression runtime flow, research table C2S authority, scan authority, alchemy/thaumatorium/infusion runtime validation, Outer Lands portal/maze validation, and broad save/load behavior.

## Corrections / refinements to the previous audit

These points should be reflected in the edited docs so that the new documentation does not understate real progress.

- `docs/Stage9-a.md` is stale in several places. Current `ConfigRecipes` is no longer a trivial `Phase 9` stub. It is a hub plus recipe slices, calls arcane/infusion/crucible/smelting setup, registers special recipes through `RegistryEvent.Register<IRecipe>`, and uses 76 `registry.register(...)` calls in recipe registration code.
- `docs/Stage9-e.md` is stale in several details. Current code has six registered research categories and 201 `new ResearchItem(...)` instances under `src/main/java/thaumcraft/common/config/research/`. `ResearchNoteData` exists. `ResearchManager.createClue(...)` exists. `ScanManager.completeScan(...)` forwards clue data after successful scan completion. `PacketPlayerCompleteToServer` now has prerequisite/type/cost/note-creation checks.
- The previous audit’s Outer Lands warning should be sharpened, not overstated. `ChunkProviderOuter.setBlocksInChunk()` and `buildSurfaces()` are no-op by design/comment, and `ThaumcraftWorldGenerator.worldGeneration(...)` calls `MazeHandler.generateEldritch(...)` for `Config.dimensionOuterId`. The correct doc wording is “Outer Lands generation hook and maze scaffold are present, but portal entry, safe destination, maze persistence, and fresh-world runtime validation remain open,” not “generation has no hook.”
- `TileThaumatorium.completeRecipe()` clearing stored essentia should be documented as “needs runtime/reference verification,” not as a proven bug. Current `Stage9-d.md` already says it matches reference shape but still needs exact/partial/extra essentia scenarios.
- `PacketPlayerCompleteToServer` should no longer be described as a direct arbitrary research-complete bypass. The remaining problem is lack of live Thaumonomicon client route validation and incomplete end-to-end progression tests.

---

## Target file: `docs/GOAL_PROGRESS.md`

### Change 1 — Add a current-truth interpretation guard near the top

- **Existing claim / section:** top-level progress checklist and compressed checkpoint digest imply that current progress entries can be read as closure evidence.
- **Action:** add-note.
- **Replacement text proposal:** insert after `# Goal Progress` or immediately before the first checklist.

```md
## Current-truth interpretation guard

This file is a progress digest, not a parity certificate. Treat any claim such as “ported”, “guarded”, “registered”, “validated”, or “smoke passed” with the following limits:

- `static guarded` means source-shape or corpus regressions are checked; it does not prove runtime gameplay behavior.
- `validate --smoke passed` means Forge/server load stability for that checkpoint; it does not prove recipe craftability, progression correctness, malicious packet rejection, save/load symmetry, or full parity.
- Stage 8 visual/client progress must not be used as evidence that backend/common logic is complete.
- Stage 9 recipe/research corpus progress must not be used as evidence that progression is server-authoritative or end-to-end playable.

Current backend blockers remain: research/progression runtime route, research table C2S authority, scan authority, alchemy/thaumatorium/infusion runtime validation, Outer Lands portal/maze validation, and save/load behavior for complex tiles.
```

- **Reason:** `AGENTS.md` explicitly forbids parity claims based on compile success and requires runtime smoke for runtime-risk changes. The current code still has unvalidated/exploitable progression paths.
- **Evidence:** `AGENTS.md` “Do not claim parity based on compile success alone”; `PacketAspectPlaceToServer`, `PacketAspectCombinationToServer`, `PacketScannedToServer`; `GuiResearchBrowser`, `GuiResearchRecipe`, `GuiResearchTable`; previous audit findings C1/C2/H1.

### Change 2 — Split the compressed Stage 9 digest claim

- **Existing claim / section:** `Checkpoint Digest` row: `Stage 9-a..e | Recipe/content/crafting/research systems are substantially ported and guarded.`
- **Action:** split + downgrade.
- **Replacement text proposal:** replace the single row with this block.

```md
| Area | Condensed status |
| --- | --- |
| Stage 9-a recipe foundation | Substantial production implementation exists: recipe hub/slices, special recipe registration, smelting baseline, smelting bonuses, normal recipe handles, and research recipe map wiring. Static/corpus guards exist. Runtime craftability and full recipe parity are not proven by those guards. |
| Stage 9-b arcane crafting | One of the stronger backend areas. Arcane recipe matching, workbench container/slot flow, dynamic wand/sceptre recipes, and focused runtime integration coverage exist. Remaining edge cases still need validation, especially recipe collisions and take-result recheck behavior. |
| Stage 9-c infusion | Infusion APIs, recipe registration paths, and `TileInfusionMatrix` runtime loop are present. Do not claim parity until infusion start/drain/ingredient consumption/instability/enchantment scaling/save-load scenarios are validated. |
| Stage 9-d crucible/alchemy/thaumatorium | Crucible and thaumatorium production code plus recipe corpus are present. Runtime scenarios for exact/extra essentia, catalyst attribution, output blocking, research gates, NBT reload, and top/bottom transport remain open. |
| Stage 9-e research/progression | Research corpus and server model are much more complete than earlier docs say: six categories, 201 research items, note data, clue creation, and guarded completion packet exist. However normal Thaumonomicon/research table gameplay route, research table packet authority, scan authority, hidden/lost prerequisite behavior, and e2e progression validation remain blockers. |
```

- **Reason:** The current single sentence is too compressed and converts “large corpus + static guards” into perceived readiness.
- **Evidence:** `ConfigRecipes.java`, `ConfigRecipesSpecialSlice.java`, `ConfigRecipesCrucibleSlice.java`, `ConfigRecipesSmeltingSlice.java`; `ConfigResearch*.java` with 201 `ResearchItem`; `ArcaneWorkbenchRuntimeIntegrationTest`; `PacketAspectPlaceToServer`; `PacketScannedToServer`.

### Change 3 — Downgrade `Baseline Validation` interpretation

- **Existing claim / section:** `Baseline Validation` lists `compileJava`, `check-jar`, `validate`, `validate --smoke` as passed.
- **Action:** add-note.
- **Replacement text proposal:** append to the section.

```md
### Validation interpretation limit

The latest listed validation proves build/server-load stability for the documented checkpoint, not backend parity. It must not be used to close gameplay systems that require runtime scenarios: research note creation/solving, scan authority, malicious packet rejection, crucible/thaumatorium/infusion crafting, Outer Lands portal entry/return, or save/load of complex tile state.
```

- **Reason:** Smoke does not exercise gameplay flows or adversarial C2S payloads.
- **Evidence:** `AGENTS.md` runtime smoke language; static guard tests reading source; previous audit Test/validation quality section.

### Change 4 — Archive or shorten visually dominated Stage 8 checkpoint bullets

- **Existing claim / section:** long Stage 8 device/entity/render/FX bullet history in `GOAL_PROGRESS.md`.
- **Action:** move-to-archive + add-note.
- **Replacement text proposal:** replace long visual bullets with a concise current status and archive pointer.

```md
## Stage 8 digest

Stage 8 contains extensive client/render/GUI/FX restoration work and server-load smoke history. Detailed visual checkpoint logs should live in `docs/GOAL_PROGRESS-archive/` and must not be read as backend/common parity evidence.

Backend-relevant Stage 8 status:

- proxy separation and GUI ID routing are broadly present;
- server container routing exists for major block/tile GUIs;
- Thaumonomicon has no server container and depends on explicit server packets for progression actions;
- manual/client visual parity remains excluded/skipped unless separately recorded.
```

- **Reason:** The live progress file is too compressed and too noisy at the same time: visual progress obscures backend blockers.
- **Evidence:** `CommonProxy.getServerGuiElement(...)`, `GUI_THAUMONOMICON` returns `null`; `GuiResearchBrowser`/`GuiResearchRecipe`/`GuiResearchTable` are static/simple client screens.

### Change 5 — Add active backend blocker register

- **Existing claim / section:** no compact active blocker register exists in `GOAL_PROGRESS.md`.
- **Action:** add.
- **Replacement text proposal:** insert before `Next Checkpoint Candidate`.

```md
## Active backend blocker register

Do not claim “backend substantially complete” until these are resolved or explicitly scoped out:

1. Research table C2S authority: `PacketAspectPlaceToServer` and `PacketAspectCombinationToServer` must require the active `ContainerResearchTable`, verify tile identity/distance, validate hex membership, validate discovered/available aspects, and consume costs atomically.
2. Scan authority: `PacketScannedToServer` must not trust client-declared item/entity/phenomena scan targets without server-side held-item/range/LoS/raytrace validation.
3. Normal research progression route: Thaumonomicon/research browser actions must create notes or complete secondary research through validated server paths; current GUI route is not proven.
4. Research table NBT symmetry: `bonusAspects` amount persistence must be fixed or documented as intentionally one-bit and enforced.
5. Alchemy/thaumatorium/infusion runtime validation: at least representative crucible, thaumatorium, infusion, and enchantment scenarios must pass with save/load checks.
6. Outer Lands runtime validation: fresh-world ring/maze/portal entry/return/safe-spawn/persistence scenarios must pass.
7. Static guard overconfidence: source-shape/corpus tests must remain labelled as guards, not parity proof.
```

- **Reason:** Future maintainers need a short stop-list that prevents inflated closure claims.
- **Evidence:** previous audit critical/high findings; packet/tile code listed above.

---

## Target file: `docs/GOAL.md`

### Change 1 — Keep final objective but add current-status caveat

- **Existing claim / section:** final goal includes backend/frontend/static parity as a continuous target.
- **Action:** keep + add-note.
- **Replacement text proposal:** insert after the high-level goal definition.

```md
## Current backend status caveat

The final objective remains original Thaumcraft 4.2.3.5 behavior on Forge 1.12.2, but current progress must not be described as backend parity. The current backend is best described as:

- substantial lifecycle/registry/content scaffolding;
- substantial recipe and research data corpus;
- several real runtime implementations, especially arcane crafting and player knowledge persistence;
- incomplete or untrusted progression/network/runtime validation for research table, scans, alchemy, infusion, thaumatorium, Outer Lands, and complex save/load paths.

Use `runtime validated` only for behavior exercised by tests or smoke/manual scenarios that actually execute that behavior. Use `static guarded only` for source-shape/corpus checks.
```

- **Reason:** The final goal can remain ambitious, but current status must be separated from target status.
- **Evidence:** `AGENTS.md`; `docs/PRD.md` acceptance wording; previous audit.

### Change 2 — Rewrite Stage 9 priority wording

- **Existing claim / section:** `Next priority order` says Stage 9 research/progression only after recipe handles/crafting systems are substantially present.
- **Action:** rewrite.
- **Replacement text proposal:**

```md
Next backend priority order:

1. Server-authoritative research/progression packets and table logic.
2. Scan authority and server-side target validation.
3. End-to-end research note lifecycle: Thaumonomicon action -> note creation -> research table solve -> note completion -> unlock/sibling behavior.
4. Runtime validation of arcane/recipe handle paths where static guards currently stand in for behavior.
5. Crucible/thaumatorium/infusion scenario validation with NBT reload checks.
6. Outer Lands portal/maze/safe-spawn/persistence validation.
7. Stage 8 GUI/visual polish only where it is required to exercise backend progression.
```

- **Reason:** The current top backend risk is not missing recipe corpus; it is server-authoritative progression and runtime validation.
- **Evidence:** `PacketAspectPlaceToServer`, `PacketAspectCombinationToServer`, `PacketScannedToServer`; `GuiResearchBrowser`/`GuiResearchTable`; `TileResearchTable`.

### Change 3 — Rewrite Milestone I / J language

- **Existing claim / section:** `Milestone I — Stage 9-a/b/c/d recipes/crafting` and `Milestone J — Stage 9-e research/progression` say “Implement parity for...” and can be read as closure once code exists.
- **Action:** rewrite.
- **Replacement text proposal:**

```md
### Milestone I — Stage 9 recipes/crafting runtime readiness

Populate and validate recipe/crafting systems:

- recipe registration and stable ids;
- research-gated crafting;
- arcane crafting;
- infusion recipes and infusion enchantments;
- crucible recipes;
- thaumatorium programming/consumption;
- alchemy flow.

Do not mark this milestone complete from recipe corpus, research-page handles, static guards, or server smoke alone. Completion requires representative runtime scenarios that craft, reject invalid crafts, consume correct inputs/aspects/vis/essentia, and survive save/load where the system has persistent state.

### Milestone J — Stage 9-e research/progression runtime readiness

Populate and validate research/progression:

- research categories and entries;
- page data and recipe references;
- prerequisite/hidden/sibling rules;
- scan/clue/note flow;
- research table placement/combination/solving;
- progression persistence.

Do not complete research purely client-side. Do not call Stage 9-e complete until the normal gameplay route is server-authoritative and validated end-to-end.
```

- **Reason:** The words “implement parity” are too broad without acceptance vocabulary.
- **Evidence:** previous audit sections on research/progression and test quality.

### Change 4 — Add status vocabulary

- **Existing claim / section:** no canonical definitions for “implemented”, “guarded”, “validated”, “complete”.
- **Action:** add.
- **Replacement text proposal:**

```md
## Progress vocabulary

Use these terms consistently in `GOAL_PROGRESS.md` and Stage docs:

- `data corpus present`: entries/keys/assets/recipes exist in code/resources.
- `registered`: Forge/API registration path runs or is wired.
- `partially wired`: code exists but normal gameplay route or all consumers are not proven.
- `static guarded`: source-shape/corpus/ID tests protect regressions only.
- `runtime smoke passed`: mod/server loads without crash for the checkpoint.
- `runtime validated`: representative behavior was executed by test or manual/server scenario.
- `parity candidate`: implementation plus runtime validation match the reference for the scoped behavior.
- `complete`: no blocker/high gaps remain in that stage scope and docs list the validation evidence.
```

- **Reason:** Prevents future optimistic compression.
- **Evidence:** tests include both runtime integration tests and many static guards; docs currently conflate them.

---

## Target file: `docs/Stage9-a.md`

### Change 1 — Replace stale “ConfigRecipes is a stub” current-state section

- **Existing claim / section:** `## 4. Текущее состояние Stage 9-a`, especially: `ConfigRecipes.init()` exists but is a stub; no recipe registry event; smelting absent; custom recipes never registered.
- **Action:** rewrite.
- **Replacement text proposal:** replace the whole `## 4. Текущее состояние Stage 9-a` section.

```md
## 4. Текущее состояние Stage 9-a

Stage 9-a is no longer at stub-only status. Current code has a real recipe registration foundation:

- `ConfigRecipes.init()` clears/rebuilds `ConfigResearch.recipes`, initializes arcane, infusion, crucible, smelting, smelting bonus, and research-layout recipe handles.
- Recipe registration is split between `ConfigRecipes.java` and slice classes under `thaumcraft.common.config.recipes`.
- `Thaumcraft.registerRecipes(RegistryEvent.Register<IRecipe>)` calls `ConfigRecipes.registerSpecialRecipes(...)`.
- `ConfigRecipesSpecialSlice` registers custom/special Forge recipes with registry names.
- `ConfigRecipesSmeltingSlice` registers smelting and smelting bonus baseline.
- `ConfigResearch.recipes` receives many recipe handles needed by Thaumonomicon pages.

However, Stage 9-a still must not be documented as complete parity. Remaining risks:

- recipe corpus/static guards do not prove all recipes are craftable in-game;
- fallback research-page recipe handles can hide missing registered recipes;
- JSON absence is not automatically a blocker where Java `IRecipe` registration is intentionally used, but each recipe must still have stable registry identity;
- recipe id coverage and duplicate/missing audits must be tied to actual registry/runtime behavior, not source text only;
- object/aspect tag coverage supports scanning but still needs gameplay validation.
```

- **Reason:** The old section is factually stale and undermines trust.
- **Evidence:** `ConfigRecipes.init()`, `Thaumcraft.registerRecipes(...)`, `ConfigRecipesSpecialSlice.registerSpecialRecipes(...)`, `ConfigRecipesSmeltingSlice.initializeSmeltingBaseline()`, `initializeSmeltingBonusBaseline()`.

### Change 2 — Downgrade/close old GAP-1 instead of leaving it as blocker

- **Existing claim / section:** `GAP-1: ConfigRecipes является заглушкой вместо registration foundation`.
- **Action:** rewrite + downgrade to “closed as stub claim; residual validation open”.
- **Replacement text proposal:**

```md
### GAP-1: Recipe registration foundation exists, but runtime craftability is not fully validated

**Статус:** implementation present; runtime validation open  
**Критичность:** high

The earlier claim that `ConfigRecipes` is a stub is stale. Current `ConfigRecipes` has a hub/slice structure and registration path for special recipes, smelting, arcane, infusion, crucible, and research recipe handles.

Remaining work is not “create the foundation from zero”; it is to validate that the foundation behaves correctly in Forge 1.12.2:

- every registered `IRecipe` has a stable registry name;
- research-page recipe handles point to actually registered or intentionally display-only recipe objects;
- representative normal/special recipes craft in a real crafting inventory;
- recipe collisions and duplicate ids are tested against the live registry;
- fallback display recipes are labelled and do not mask missing craftable recipes.
```

- **Reason:** Avoid carrying closed/stale blockers.
- **Evidence:** 76 `registry.register(...)` calls across current recipe registration code; `ConfigRecipes.registerSpecialRecipes(...)`.

### Change 3 — Rewrite JSON recipe gap

- **Existing claim / section:** `GAP-2: Forge 1.12.2 JSON recipe data отсутствует полностью` as blocker.
- **Action:** downgrade.
- **Replacement text proposal:**

```md
### GAP-2: Recipe resources use Java registration; JSON absence is not by itself a blocker

**Статус:** accepted implementation strategy, with audit required  
**Критичность:** medium

Forge 1.12.2 can load recipes from JSON or from code-registered `IRecipe` instances. Current port uses Java registration for many reference-shaped recipes. Do not document lack of JSON files as a blocker unless a specific recipe requires JSON or fails registry/runtime behavior because of the chosen strategy.

Required audit:

- list all recipe ids registered in Java;
- confirm stable registry names;
- confirm no duplicate ids;
- confirm representative recipes craft in-game;
- document any recipe intentionally display-only for research pages.
```

- **Reason:** The old blocker frames implementation strategy as failure.
- **Evidence:** `ConfigRecipesSpecialSlice.registerSpecialRecipes(...)` and recipe registry event in `Thaumcraft`.

### Change 4 — Add fallback-handle warning

- **Existing claim / section:** no explicit warning that fallback research-page recipe handles are not craftability proof.
- **Action:** add-note.
- **Replacement text proposal:**

```md
### Fallback recipe-handle warning

`ConfigRecipes.refreshLateBoundResearchRecipeHandles()` may populate `ConfigResearch.recipes` with fallback `ShapedOreRecipe`/`ShapelessOreRecipe` objects when a late-bound special recipe handle is missing. This is useful for keeping research pages from null-crashing, but it must not be counted as proof that the matching Forge recipe is registered or craftable.

Any Stage 9-a closure report must distinguish:

- registered craftable recipe;
- research display recipe handle;
- fallback display-only handle;
- missing recipe.
```

- **Reason:** This is a concrete fake-completeness vector.
- **Evidence:** `ConfigRecipes.refreshLateBoundResearchRecipeHandles()` creates fallback handles for clusters, mundane baubles, flesh/tallow blocks, and other late-bound entries.

---

## Target file: `docs/Stage9-d.md`

### Change 1 — Replace stale “content data is not registered” status

- **Existing claim / section:** `API/runtime baseline exists but content data is not registered.`
- **Action:** rewrite.
- **Replacement text proposal:** replace the first paragraph and `Result` of `## 4. Текущее состояние Stage 9-d`.

```md
## 4. Текущее состояние Stage 9-d

Stage 9-d is no longer missing all alchemy/crucible content data. Current code has a substantial crucible/alchemy and thaumatorium backend baseline:

- `CrucibleRecipe` API and lookup paths exist.
- `ConfigRecipesCrucibleSlice.initializeCrucibleRecipeBaseline()` registers a broad crucible recipe corpus through `ThaumcraftApi.addCrucibleRecipe(...)`.
- `ConfigRecipesSmeltingSlice` registers smelting and smelting bonus baseline.
- `TileCrucible` has heat/fluid/aspect storage, item collision smelting, research-gated recipe matching, aspect removal, water drain, output ejection, and NBT.
- `TileThaumatorium` has recipe hashes, input/output, essentia suction/fill, completion, and NBT.
- `ContainerThaumatorium` can list/program recipes by research completion and catalyst.

Stage 9-d still cannot be considered complete. The remaining blocker is runtime validation and semantic confidence, not absence of all production code. Required validation includes crucible player attribution, research gates, exact/extra essentia behavior, output blocking, top/bottom transport, and save/load mid-state.
```

- **Reason:** Current docs contradict themselves: they say content is absent, then list current crucible corpus.
- **Evidence:** `ConfigRecipesCrucibleSlice`, `TileCrucible`, `TileThaumatorium`, `ContainerThaumatorium`.

### Change 2 — Rewrite GAP-1 from “absent” to “corpus present; parity audit open”

- **Existing claim / section:** `GAP-1: Crucible/alchemy recipe registration is absent`.
- **Action:** rewrite + downgrade.
- **Replacement text proposal:**

```md
### GAP-1: Crucible/alchemy recipe corpus is present, but parity and runtime coverage remain open

**Статус:** implementation present; parity/runtime validation open  
**Критичность:** high

The old “registration is absent” wording is stale. Current code registers a broad crucible/alchemy baseline. Remaining work:

- verify every reference Stage 9-d recipe key/research gate/catalyst/output/aspect cost against original behavior;
- verify optional ore-mod gates;
- validate at least representative recipes in-game or through a runtime harness;
- ensure `ConfigResearch.recipes` handles match the registered recipe objects where pages expect them;
- document intentional deviations.
```

- **Reason:** Keeps progress honest without deleting validation requirements.
- **Evidence:** `ConfigRecipesCrucibleSlice.initializeCrucibleRecipeBaseline()`; `ThaumcraftCraftingManager.findMatchingCrucibleRecipe(...)`.

### Change 3 — Keep GAP-6 but strengthen it as the real closure gate

- **Existing claim / section:** `GAP-6: Crucible and thaumatorium scenarios are not runtime-verified`.
- **Action:** keep + strengthen.
- **Replacement text proposal:** add to GAP-6 before criteria.

```md
This is the main Stage 9-d closure gate. Static guards, source comparison, and server smoke do not close Stage 9-d. Completion requires behavior execution.

Required minimum runtime matrix:

1. Crucible: valid recipe succeeds with completed research.
2. Crucible: same recipe fails without research.
3. Crucible: insufficient aspects fail without consuming catalyst incorrectly.
4. Crucible: no-aspect or invalid item is rejected/spilled according to reference behavior.
5. Thaumatorium: program recipe through `ContainerThaumatorium`.
6. Thaumatorium: fill exact required essentia and craft.
7. Thaumatorium: fill extra/partial essentia and verify remaining/cleared state against reference.
8. Thaumatorium: full output inventory behavior.
9. Save/reload before and after recipe completion.
10. Top/bottom transport behavior through `TileThaumatoriumTop`.
```

- **Reason:** The real missing proof is behavior, not source presence.
- **Evidence:** `TileCrucible.attemptSmelt(...)`, `TileThaumatorium.completeRecipe(...)`, `TileThaumatoriumTop`, `ContainerThaumatorium`.

---

## Target file: `docs/Stage9-e.md`

### Change 1 — Replace stale current-state section

- **Existing claim / section:** `## 4. Текущее состояние Stage 9-e`, especially statements that research note data is missing, scan clue is not wired, and completion packet bypasses rules.
- **Action:** rewrite.
- **Replacement text proposal:** replace the section with this.

```md
## 4. Текущее состояние Stage 9-e

Stage 9-e has substantial data/model progress, but is not runtime/progression complete.

Current implementation present:

- `ConfigResearch.init()` registers six research categories.
- Category slices under `thaumcraft.common.config.research` contain the reference-sized corpus: 201 `ResearchItem` registrations.
- `ConfigResearch` uses strict recipe lookup behavior for recipe-backed pages.
- Research localization and baseline assets are present enough for static graph checks.
- Player knowledge capability persists research, aspects, scanned items/entities/phenomena, and warp.
- `ResearchNoteData`, note NBT read/write, note generation, note update, and note completion checks exist.
- `ResearchManager.createClue(...)` and scan clue forwarding exist.
- `PacketPlayerCompleteToServer` now checks research existence, requisites, duplicate completion, primary-vs-secondary type, note creation, aspect costs, and sibling gating.

Current blockers:

- Normal Thaumonomicon/research browser click flow is not proven to send validated progression packets.
- Research table packets are not server-authoritative enough: they do not require the active `ContainerResearchTable`, do not verify tile identity/distance, and allow client-selected q/r/aspect data to reach `TileResearchTable.placeAspect(...)`.
- `PacketScannedToServer` trusts client-declared scan targets more than a server-authoritative scan system should.
- `TileResearchTable` can persist `bonusAspects` as tag names without amounts unless one-bit semantics are explicitly intended and enforced.
- Hidden/lost clue grants need prerequisite validation against reference behavior.
- End-to-end progression runtime validation is still missing.
```

- **Reason:** Current section contains stale negatives and misses current critical blockers.
- **Evidence:** `ConfigResearch*.java`; `ResearchNoteData`; `ResearchManager.createNote/createResearchNoteForPlayer/createClue/checkResearchCompletion`; `PacketPlayerCompleteToServer`; `PacketAspectPlaceToServer`; `PacketAspectCombinationToServer`; `PacketScannedToServer`.

### Change 2 — Rewrite GAP-1, GAP-3, GAP-4 as closed/static-open rather than absent

- **Existing claim / section:** `GAP-1: Research categories and research entries are not registered`; `GAP-3: Research localization is absent`; `GAP-4: assets are missing`.
- **Action:** rewrite/downgrade.
- **Replacement text proposal:**

```md
### GAP-1: Research corpus exists; graph/runtime validation remains open

**Статус:** implementation present; static graph guarded; runtime validation open  
**Критичность:** high

The old “entries are not registered” wording is stale. Current code registers six categories and 201 research entries. Remaining work is graph correctness and runtime behavior:

- verify parent/hidden-parent/sibling links against reference;
- verify all recipe page references resolve to intended recipe objects or documented display-only handles;
- verify hidden/lost triggers under runtime scan scenarios;
- verify no impossible cycles or unreachable progression paths;
- validate representative pages through the live Thaumonomicon route once GUI action routing is functional.

### GAP-3: Research localization corpus is present; runtime page rendering still unvalidated

**Статус:** corpus present; runtime/client verification open  
**Критичность:** medium

Do not claim localization is absent. Keep a coverage test for missing keys, but treat page readability/rendering as client/manual validation, not backend closure.

### GAP-4: Baseline research assets are present; asset completeness is secondary to backend progression

**Статус:** baseline present; full visual/manual verification open  
**Критичность:** low for backend, medium for final parity

Do not let missing or unverified image-page assets block backend progression work unless they prevent opening or navigating the research system. Record visual/manual checks separately from backend completion.
```

- **Reason:** Old claims are stale; replacement preserves remaining validation truth.
- **Evidence:** current research slices; `en_us.lang`; research textures under resources; static graph tests.

### Change 3 — Replace old GAP-5 with note lifecycle + table authority split

- **Existing claim / section:** `GAP-5: Research note, discovery, and hex-grid content flow is missing`.
- **Action:** split.
- **Replacement text proposal:**

```md
### GAP-5A: Research note data flow exists, but end-to-end note gameplay is not validated

**Статус:** partial implementation; runtime validation open  
**Критичность:** high

Current code has `ResearchNoteData`, note creation/update/read/write, and completion checks. Do not describe the note model as absent. Remaining work is to execute the normal gameplay route:

- primary research click creates a note rather than directly completing research;
- note has reference-compatible NBT keys and hex grid;
- research table solves the note using original-compatible connectivity rules;
- completed note grants research only after prerequisite checks;
- discovery/fragment behavior matches hidden/lost rules.

### GAP-5B: Research table C2S authority is incomplete

**Статус:** unsafe/untrusted until fixed  
**Критичность:** blocker

`PacketAspectPlaceToServer` and `PacketAspectCombinationToServer` must be made server-authoritative. Required checks:

- player has `ContainerResearchTable` open;
- container tile identity equals packet coordinates;
- tile is usable by player/distance check passes;
- target q/r exists in the current note hex grid;
- placed aspect is discovered and available from aspect pool or actual table bonus;
- bonus aspect consumption verifies current `bonusAspects` amount;
- aspect pool / bonus / ink costs are consumed atomically after validation;
- invalid packets cannot mutate note NBT.
```

- **Reason:** “note flow missing” is stale; the real blocker is authoritative interaction.
- **Evidence:** `TileResearchTable.placeAspect(...)`; `PacketAspectPlaceToServer`; `PacketAspectCombinationToServer`; `ContainerResearchTable.canInteractWith(...)`.

### Change 4 — Rewrite GAP-6 clue wording

- **Existing claim / section:** `GAP-6: Hidden/lost research discovery from scans is not wired`.
- **Action:** rewrite.
- **Replacement text proposal:**

```md
### GAP-6: Hidden/lost scan clues are wired, but prerequisite/runtime behavior needs verification

**Статус:** wired; verification open  
**Критичность:** high

The old “not wired” wording is stale. `ResearchManager.createClue(...)` exists and `ScanManager.completeScan(...)` forwards clue data after successful scans. Remaining work:

- verify hidden/lost clues do not bypass parent or hidden-parent prerequisites;
- verify item/entity/aspect triggers match original behavior with 1.12 entity ids;
- verify `@KEY` clue state does not accidentally complete full research;
- validate knowledge-fragment behavior against eligible hidden research;
- add runtime tests or manual scenarios with populated trigger-bearing content.
```

- **Reason:** Corrects stale implementation claim while preserving risk.
- **Evidence:** `ResearchManager.createClue(...)`, `findHiddenResearch(...)`, `ScanManager.completeScan(...)`.

### Change 5 — Rewrite GAP-7 completion packet wording

- **Existing claim / section:** `GAP-7: Research completion packet bypasses original progression rules`.
- **Action:** rewrite.
- **Replacement text proposal:**

```md
### GAP-7: Research completion packet has server checks; live route and adversarial tests remain open

**Статус:** partial implementation; runtime/adversarial validation open  
**Критичность:** high

Do not describe `PacketPlayerCompleteToServer` as a simple direct-complete bypass anymore. It now checks research existence, duplicate completion, dimension/user, requisites, primary vs secondary action type, note creation, aspect cost, and sibling gating.

Remaining work:

- verify normal Thaumonomicon client actions actually send this packet with correct type semantics;
- add tests that invalid packets cannot create notes or complete research without prerequisites/costs;
- validate secondary research aspect cost consumption with populated research data;
- validate primary note creation consumes paper/ink and does not add the research key directly;
- validate sibling grants only after valid completion.
```

- **Reason:** The earlier gap title is now misleading.
- **Evidence:** `PacketPlayerCompleteToServer.onMessage(...)`, `consumeResearchCost(...)`, `ResearchManager.createResearchNoteForPlayer(...)`.

### Change 6 — Add new scan-authority gap

- **Existing claim / section:** no explicit server-authority gap for `PacketScannedToServer`.
- **Action:** add.
- **Replacement text proposal:**

```md
### GAP-9: Scan packet is not server-authoritative enough

**Статус:** unsafe/untrusted until fixed  
**Критичность:** blocker

`PacketScannedToServer` reconstructs scan results from client payload. Server-side `ScanManager.completeScan(...)` validates duplicate/aspect/prerequisite rules, but the server does not fully recompute whether the player could physically scan the declared target.

Required work:

- verify held thaumometer or valid scanner item on server;
- server-raytrace block/entity target with range and line of sight;
- for entity scans, verify entity exists, is within range, and is visible/targeted;
- for item scans, verify target item/block comes from server-observed state, not arbitrary item id/meta;
- for phenomena scans, restrict strings to server-known nearby phenomena;
- add malicious-payload rejection tests.
```

- **Reason:** Scan is a progression input and must not trust client declarations.
- **Evidence:** `PacketScannedToServer`; `ItemThaumometer.doScan(...)` client-side raytrace; `ScanManager.completeScan(...)`.

### Change 7 — Update Stage9-e checklist

- **Existing claim / section:** checklist items still say “Port research note NBT/data flow”, “Wire scan-triggered hidden clue unlocks”, “Fix server-side completion packet”.
- **Action:** rewrite checklist items.
- **Replacement text proposal:** replace those checklist items.

```md
- [x] Register six research categories and reference-sized research corpus in category slice files.
- [x] Add baseline research note data model and NBT read/write/update flow.
- [x] Add scan-triggered hidden/lost clue wiring through `ResearchManager.createClue(...)`.
- [x] Harden `PacketPlayerCompleteToServer` against direct arbitrary completion for primary/secondary research flows.
- [ ] Validate full research graph against reference keys, parents, hidden parents, siblings, triggers, aspects, coordinates, complexity, flags, icons, and page order.
- [ ] Make research table aspect placement/combination packets server-authoritative.
- [ ] Make scan completion server-authoritative.
- [ ] Wire and validate normal Thaumonomicon/research-browser action flow.
- [ ] Validate end-to-end progression: scan -> clue/aspect -> note creation -> research table solve -> note completion -> unlock.
- [ ] Fix or document/enforce `TileResearchTable.bonusAspects` persistence semantics.
- [ ] Run runtime/manual validation for representative Thaumonomicon content and progression scenarios.
```

- **Reason:** Reflects current implementation while keeping blockers visible.
- **Evidence:** code and previous audit.

---

## Target file: `docs/Stage4.md`

### Change 1 — Add “stale gap list” status note near the top

- **Existing claim / section:** old gaps describe several systems as empty shells or stubs, while closure notes later record many implementations.
- **Action:** add-note.
- **Replacement text proposal:** insert after `## 4. Текущее состояние Stage 4` heading.

```md
### Current interpretation note

This stage document contains historical gaps and later closure notes. Do not read the early gap titles as current truth without checking the closure notes and production code.

Current backend state: many Stage 4 systems are materially implemented, including Arcane Bore, Thaumatorium, Focal Manipulator, Crucible, Infusion Matrix, Portable Hole, Warding, and several containers/tiles. Remaining Stage 4 risk is mainly runtime behavior and save/load validation, not absence of all server code.

Still open for backend confidence:

- Crucible recipe scenarios are Phase 9/content-dependent.
- Thaumatorium exact/extra essentia, output blocking, top/bottom transport, and NBT reload need validation.
- Infusion Matrix instability, pedestal geometry, essentia drain, enchantment cost scaling, and mid-craft reload need validation.
- Essentia transport parity and registered stub-like tiles must not be counted as complete without runtime checks.
```

- **Reason:** Stage4 has both old gaps and closure notes; readers can misread early sections as current truth.
- **Evidence:** Stage4 closure notes; current `TileArcaneBore`, `TileThaumatorium`, `TileFocalManipulator`, `TileCrucible`, `TileInfusionMatrix`.

---

## Target file: `docs/Stage6.md`

### Change 1 — Add explicit non-closure note

- **Existing claim / section:** many checkpoints can look like broad entity/golem closure.
- **Action:** add-note.
- **Replacement text proposal:** insert near current status or before checkpoint list.

```md
### Current backend interpretation note

Stage 6 has many entity/golem/mob implementations and static guard checkpoints, but it must not be marked complete from class presence, registration, or source-shape tests. Entity parity requires runtime behavior evidence.

Do not claim Stage 6 complete until representative dedicated-server scenarios pass for:

- golem placement, core behavior, upgrades, inventory, combat, liquid/essentia handling, save/load;
- traveling trunk placement, inventory, defense, dimension transfer, save/load;
- pech anger/trade/spawn variants;
- cultist portal reward path;
- boss special attacks and phase flows;
- projectile effects/status behavior where server-visible.
```

- **Reason:** Entity AI/combat parity cannot be proven statically.
- **Evidence:** Stage6 gap/checkpoint list; previous audit entity section.

---

## Target file: `docs/Stage7.md`

### Change 1 — Clarify Outer Lands generation status

- **Existing claim / section:** Outer Lands gaps can be read as either no generation at all or fully restored baseline depending on section.
- **Action:** rewrite/add-note.
- **Replacement text proposal:** insert after `## 4. Текущее состояние Stage 7`.

```md
### Current Outer Lands interpretation note

Outer Lands is partially wired, not parity-complete.

Present:

- `WorldProviderOuter` and `ChunkProviderOuter` exist.
- Empty chunk primer methods are intentional for void-like Outer Lands; maze cells are generated through `ThaumcraftWorldGenerator.worldGeneration(...)` when `dim == Config.dimensionOuterId`.
- `MazeHandler.generateEldritch(...)` routes saved maze cells to room/passage generators.
- Overworld Eldritch ring/altar/maze bootstrap code exists.

Still open:

- fresh-world runtime proof that ring generation creates and persists non-empty maze cells;
- portal activation and teleport entry/return validation;
- safe spawn/destination behavior in void-like terrain;
- async `MazeThread` save/load race behavior;
- `TeleporterThaumcraft.makePortal(...)` currently returns true without building a portal, so portal creation semantics must be verified or fixed.

Do not claim “Outer Lands baseline restored” without runtime evidence for ring -> maze -> portal -> dimension -> return path.
```

- **Reason:** Corrects the overly simple “no-op generation” interpretation while keeping blockers.
- **Evidence:** `ChunkProviderOuter`, `ThaumcraftWorldGenerator.worldGeneration(...)`, `MazeHandler.generateEldritch(...)`, `TeleporterThaumcraft.makePortal(...)`.

---

## Optional target file: `docs/PRD.md`

### Change — Add acceptance vocabulary reference

- **Existing claim / section:** PRD already says validation matters, but does not enforce progress vocabulary.
- **Action:** add-note.
- **Replacement text proposal:** add under validation/acceptance criteria.

```md
### Progress wording policy

Progress docs must separate implementation presence from validation evidence:

- `implemented` means code exists and is wired into lifecycle/registration.
- `static guarded` means tests protect source/corpus/ID shape only.
- `runtime smoke passed` means mod/server load stability only.
- `runtime validated` means the behavior itself was executed through test/manual scenario.
- `parity candidate` requires implementation plus runtime validation against reference behavior.

No stage may be called complete from compile, static guard tests, recipe/research corpus counts, or server smoke alone.
```

- **Reason:** Makes `AGENTS.md` operational inside product docs.
- **Evidence:** current static guards vs runtime integration tests distinction.
