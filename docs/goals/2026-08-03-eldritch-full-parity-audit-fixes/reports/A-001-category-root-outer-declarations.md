# Audit Packet: A-001 — Category, roots, Oculus, and Outer declarations

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Assignment-ID: A-001
Status: complete
Report-Revision: 1
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Compare the port's `ELDRITCH` category registration and only the `ELDRITCHMINOR`, `ELDRITCHMAJOR`, `OCULUS`, `ENTEROUTER`, and `OUTERREV` research declarations: coordinates, icons/background, aspect costs and order, complexity, pages and order, parents/hidden parents, triggers, flags, warp, identifiers/localization keys, and recipe handles.
- Anti-scope: Recipe bodies; runtime consumers; other `ELDRITCH` declarations; categories outside `ELDRITCH`; implementation changes; tests or runtime validation.
- Oracle and comparison direction: S-003/S-004, `Thaumcraft-1.7.10-4.2.3.5.jar` and extracted `thaumcraft_src/thaumcraft/common/config/ConfigResearch.class`, method `initCategories()` and the relevant prefix of `initEldritchResearch()`, compared to S-005.
- Questions: Identify only evidence-backed declaration discrepancies; distinguish benign Forge 1.12 adaptations; preserve positive parity and test debt.
- Expected evidence: Exact port path/line and symbol; exact original method/decompiled declaration; observed/expected delta; user-visible effect; confidence; regression hazard; test gap.
- Read/write permissions: Product and central ledger files read-only; only this report writable.
- Effort/tool budget: Resume the completed bounded audit without broad re-exploration.
- Stop conditions: Requested declaration surface fully compared, or oracle evidence unavailable/ambiguous.
- Continuation predecessor: none

## Coverage Performed

- Port files/symbols inspected: `src/main/java/thaumcraft/common/config/research/ConfigResearch.java:52-68,91-116` (`init()`, `initCategories()`); `src/main/java/thaumcraft/common/config/research/ConfigResearchEldritch.java:16-90,350-366` (`initEldritchResearchBaseline()`, `initEldritchResearchTextOnlyBaseline()`).
- Supporting port surfaces inspected only to assess the reported ordering delta and test debt: `src/main/java/thaumcraft/api/research/ResearchCategories.java:14,41-45,65-88`; `src/main/java/thaumcraft/api/research/ResearchCategoryList.java:15`; `src/test/java/thaumcraft/common/config/ConfigResearchStaticGraphTest.java:28-33,38-98,122-143`; `src/test/java/thaumcraft/common/config/ConfigResearchReferenceFingerprintStaticGuardTest.java:22-49,52-67`.
- Oracle surfaces inspected: S-003/S-004 `thaumcraft.common.config.ConfigResearch.init()`, `initCategories()`, and `initEldritchResearch()`, decompiled from the original 4.2.3.5 class with CFR and independently inspected as bytecode with `javap` for initialization/category registration.
- Uncovered scope: Recipe definitions behind `VoidSeed` and `EldritchEye`; all runtime consumers; visual/runtime behavior. These were explicitly excluded, not omitted accidentally.

## Atomic Findings

### A-001-F01 — Root registration order is reversed

- Type: benign_delta
- Severity: informational
- Confidence: high
- Source/oracle locator: S-003/S-004 `thaumcraft.common.config.ConfigResearch.initEldritchResearch()` begins with `new ResearchItem("ELDRITCHMINOR", ...)`, immediately followed by `new ResearchItem("ELDRITCHMAJOR", ...)`; original `init()` invokes that one method after the other research-category initializers.
- Observed: S-005 `ConfigResearch.init()` calls `ConfigResearchEldritch.initEldritchResearchTextOnlyBaseline()` at `ConfigResearch.java:67`, registering `ELDRITCHMAJOR`, then calls `initEldritchResearchBaseline()` at line 68, whose first declaration registers `ELDRITCHMINOR` at `ConfigResearchEldritch.java:17-31`.
- Expected: TC4 registration sequence for the audited entries is `ELDRITCHMINOR`, `ELDRITCHMAJOR`, `OCULUS`, `ENTEROUTER`, `OUTERREV`.
- Exact deltas: Port sequence is `ELDRITCHMAJOR`, `ELDRITCHMINOR`, `OCULUS`, `ENTEROUTER`, `OUTERREV`; only the first two entries are transposed. No constructor argument, page, parent, trigger, flag, warp, or recipe handle differs as part of this delta.
- Affected paths/symbols: `src/main/java/thaumcraft/common/config/research/ConfigResearch.java:67-68`, `ConfigResearch.init()`; `src/main/java/thaumcraft/common/config/research/ConfigResearchEldritch.java:16,350`, the two split initialization methods.
- Evidence/reproduction: CFR decompilation of original `initEldritchResearch()` shows the two root declarations consecutively in minor-then-major order; the cited port call sites establish major-then-minor registration. Current `ResearchCategoryList.research` is a `HashMap`, and the two roots occupy distinct coordinates `(1,0)` and `(-1,0)`, so no present user-visible effect was established.
- User-visible effect: None expected under the inspected current registration/storage behavior. The result is retained because it is an exact source-order difference, not silently discarded as irrelevant.
- Regression hazards: Do not treat this as evidence that either root's flags, pages, graph position, or unlock semantics may change. Reordering could become observable if registration gains ordered callbacks or storage becomes insertion-ordered; conversely, changing order now without such a contract would be speculative.
- Test gap: `ConfigResearchReferenceFingerprintStaticGuardTest` normalizes keys and page keys into `TreeSet`s, so it cannot detect registration-order changes. `ConfigResearchStaticGraphTest` validates key/category/reference integrity but not declaration order.
- Candidate disposition: preserve as a documented benign delta; no product change recommended from current evidence.

## Positive Parity

### Category Registration

- S-005 `ConfigResearch.initCategories()` at `ConfigResearch.java:112-115` exactly matches original `ConfigResearch.initCategories()`: key `ELDRITCH`, icon `thaumcraft:textures/misc/r_eldritch.png`, background `thaumcraft:textures/gui/gui_researchbackeldritch.png`.

### ELDRITCHMINOR

- Port `ConfigResearchEldritch.java:17-31` matches original `ConfigResearch.initEldritchResearch()`: key/category `ELDRITCHMINOR`/`ELDRITCH`; empty aspects; coordinates `(1,0)`; declared complexity `0`; icon `thaumcraft:textures/misc/r_eldritchminor.png`; pages in order `tc.research_page.ELDRITCHMINOR.1`, Crucible recipe handle `VoidSeed`; flags `hidden`, `round`, `special`; no parents, hidden parents, triggers, or research warp.

### ELDRITCHMAJOR

- Port `ConfigResearchEldritch.java:351-366` matches the original declaration field-for-field: key/category `ELDRITCHMAJOR`/`ELDRITCH`; empty aspects; coordinates `(-1,0)`; declared complexity `0`; icon `thaumcraft:textures/misc/r_eldritchmajor.png`; text pages in order `tc.research_page.ELDRITCHMAJOR.1`, `.2`; flags `stub`, `hidden`, `round`, `special`; no parents, hidden parents, triggers, recipe page, or research warp.

### OCULUS

- Port `ConfigResearchEldritch.java:33-55` matches original: key/category `OCULUS`/`ELDRITCH`; ordered aspects `MIND 3`, `DARKNESS 3`, `EXCHANGE 3`, `TRAVEL 6`, `ELDRITCH 6`; coordinates `(-2,2)`; complexity `1`; icon `ConfigItems.itemEldritchObject`, count 1, metadata 0; ordered pages `tc.research_page.OCULUS.1`, Infusion recipe handle `EldritchEye`, `tc.research_page.OCULUS.2`; flags `round`, `concealed`, `special`; ordered parents `CRIMSON`, `ELDRITCHMAJOR`; no hidden parents or triggers; research warp exactly `6`.

### ENTEROUTER

- Port `ConfigResearchEldritch.java:57-70` matches original: key/category `ENTEROUTER`/`ELDRITCH`; empty aspects; coordinates `(-3,4)`; complexity `1`; icon `thaumcraft:textures/misc/r_outer.png`; sole page `tc.research_page.ENTEROUTER.1`; flags `stub`, `hidden`, `round`; sole parent `OCULUS`; no hidden parents, triggers, recipe handle, or research warp.

### OUTERREV

- Port `ConfigResearchEldritch.java:72-90` matches original: key/category `OUTERREV`/`ELDRITCH`; ordered aspects `ELDRITCH 4`, `MIND 4`; coordinates `(-5,3)`; complexity `1`; icon `thaumcraft:textures/misc/r_outerrev.png`; sole page `tc.research_page.OUTERREV.1`; ordered item triggers `ConfigBlocks.blockEldritch` metadata 5 then metadata 10, each count 1; flags `lost`, `secondary`, `special`; sole parent `ENTEROUTER`; no hidden parents, recipe handle, or research warp.

### Keys and Names

- All five research IDs and all referenced `tc.research_page.*` keys match the original spelling and case. These declarations do not contain separate display-name literals; the standard `tc.research_name.<ID>` and `tc.research_text.<ID>` lookup convention is unchanged. Localization contents were outside A-001 and assigned separately.

## Benign Forge 1.12 Adaptations

- Original page constructors cast raw `recipes.get("VoidSeed")` to `CrucibleRecipe` and `recipes.get("EldritchEye")` to `InfusionRecipe`. The port uses `ConfigResearch.recipeCrucible("VoidSeed")` and `ConfigResearch.recipeInfusion("EldritchEye")`. The recipe keys, required types, and page positions are unchanged; typed lookup/fail-fast checking is a benign Java/port implementation adaptation.
- The original monolithic `initEldritchResearch()` is split into baseline and text-only methods in the port. The split preserves every audited declaration field, but causes A-001-F01's informational root-order delta. The split is structural; no evidence establishes that the transposition itself was an intentional behavioral adaptation.
- MCP/Forge 1.12 `ResourceLocation` and `ItemStack` types replace their 1.7.10 counterparts without changing the audited resource paths, item identities, counts, or metadata values.

## Unknowns and Conflicts

- No source conflict or blocking unknown was found.
- This bounded declaration audit did not prove behavior of future/addon registration callbacks that might observe insertion order. Current inspected storage and graph declarations establish no present user-visible effect, so that possibility remains a low-consequence unproven hazard rather than a defect.

## Test Debt

- No focused test snapshots all audited constructor values, aspect/page order, flags, parents, triggers, warp, and recipe-handle types against the TC4 oracle.
- `src/test/java/thaumcraft/common/config/ConfigResearchReferenceFingerprintStaticGuardTest.java:35-49` checks only unordered key/category and text-page-key corpora; its `TreeSet` extraction at lines 52-67 cannot catch A-001-F01 or most field-level drift.
- `src/test/java/thaumcraft/common/config/ConfigResearchStaticGraphTest.java:38-98` catches duplicate keys, invalid categories, missing parents/localization, and cycles, but does not assert the requested field-level parity.

## Commands and Validation

- `git status --short` — completed; initial A-001 audit worktree was clean. During packet persistence, only the orchestrator-created goal scaffold and `.opencode/active-goal` were untracked before this report was added.
- `jar tf Thaumcraft-1.7.10-4.2.3.5.jar | grep 'thaumcraft/common/config/ConfigResearch'` — located original `thaumcraft/common/config/ConfigResearch.class`.
- `javap -classpath Thaumcraft-1.7.10-4.2.3.5.jar -p thaumcraft.common.config.ConfigResearch` — confirmed original methods `init()`, `initCategories()`, and `initEldritchResearch()`.
- `/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --jarfilter 'thaumcraft.common.config.ConfigResearch' --methodname initEldritchResearch --silent true` — produced the exact original declaration chain used for comparison.
- `/usr/local/bin/cfr Thaumcraft-1.7.10-4.2.3.5.jar --jarfilter 'thaumcraft.common.config.ConfigResearch' --methodname initCategories --silent true` — produced the original category registration evidence.
- `javap -classpath Thaumcraft-1.7.10-4.2.3.5.jar -c -p thaumcraft.common.config.ConfigResearch | sed -n '/private static void initCategories()/,/private static void initThaumaturgyResearch()/p'` — independently confirmed the exact category key/icon/background constants.
- `javap -classpath Thaumcraft-1.7.10-4.2.3.5.jar -c -p thaumcraft.common.config.ConfigResearch | sed -n '/public static void init()/,/private static void initCategories()/p'` — confirmed original initialization reaches one `initEldritchResearch()` method after the other category research initializers.
- Targeted repository reads/searches inspected the requested port declarations and existing research guard tests.
- Validation status: read-only source/decompilation comparison complete. No tests, build, runtime smoke, or manual client validation were run, per assignment. Runtime smoke was not required because no product code changed.

## Handoff

- Terminal status: complete
- Material finding index: A-001-F01 informational benign delta, root registration order `MAJOR,MINOR` observed versus `MINOR,MAJOR` original; no evidence-backed declaration defect.
- Positive parity index: ELDRITCH category; ELDRITCHMINOR; ELDRITCHMAJOR; OCULUS; ENTEROUTER; OUTERREV; recipe-handle keys/types and page positions.
- Exact continuation point: none; A-001 scope is exhausted.
- Smallest next action if continued: orchestrator accepts and normalizes A-001-F01, parity controls, benign adaptations, and test debt into the central RECON ledger without reopening product scope.
