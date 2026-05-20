# One-shot Codex Goal — Thaumcraft 4.2.3.5 -> Forge 1.12.2

## 0. Contract

This is the single durable goal contract for Codex CLI.

Follow `AGENTS.md` first. Source of truth order:
1. `AGENTS.md`
2. `docs/PRD.md`
3. `docs/Stage*.md`
4. current port code
5. original TC 4.2.3.5 reference under `thaumcraft_src` / original jar

Do not modify `thaumcraft_src` or the original jar. Preserve public API, registry names, NBT/save semantics, packet semantics, research/crafting/worldgen behavior, and compatibility expectations unless a deviation is unavoidable and documented.

Current backend status caveat:

- the target remains original Thaumcraft 4.2.3.5 behavior on Forge 1.12.2;
- current progress must not be described as backend parity;
- treat implementation presence, static guards, runtime smoke, and runtime validation as separate evidence levels.

Do not ask the user to approve each phase. Work autonomously across all stages until success, hard blocker, or context/budget exhaustion.

Do not use broad staging commands such as `git add .` or `git add -A`. Stage explicit paths only. Prefer checkpoint commits when a coherent milestone passes validation. Keep changes scoped and reversible.

Maintain `docs/GOAL_PROGRESS.md` during the run. After every major checkpoint, append:
- current milestone
- files changed
- validations run
- failures and fixes
- skipped GUI/manual checks
- remaining blockers

## 0.0 Progress vocabulary

Use these terms consistently in goal and progress docs:

- `data corpus present` — entries, keys, recipes, lang, or assets exist.
- `registered` — a Forge/API/lifecycle registration path exists and is wired.
- `partially wired` — some path exists, but normal gameplay or all consumers are not proven.
- `static guarded` — source/corpus/ID tests protect regressions only.
- `runtime smoke passed` — build/load/server-start stability only.
- `runtime validated` — the behavior itself was exercised by test or manual scenario.
- `parity candidate` — implementation plus runtime validation match the scoped reference behavior.
- `complete` — blocker/high gaps in the scoped stage are closed and evidence is listed.

## 0.1 Quota-saving override — implementation burst mode

This section supersedes any earlier instruction that can be interpreted as one-line gap commits, exhaustive Stage 3–7 micro-polish, or per-gap static guard/test loops.

Quota is limited. Prioritize implementation throughput over microscopic parity polishing.

Stop Stage 3–7 micro-polish unless the gap directly blocks Stage 8/9 implementation, dedicated-server load safety, registry/API/NBT/packet compatibility, or final non-GUI validation. Non-blocking Stage 3–7 residuals must be documented in `docs/GOAL_PROGRESS.md` and deferred.

Do not treat every one-line parity tweak as a standalone gap. A checkpoint gap now means a coherent subsystem bundle. Work in implementation bursts.

Next priority order:
1. Server-authoritative research/progression packets and table logic.
2. Scan authority and server-side target validation.
3. End-to-end research note lifecycle: Thaumonomicon action -> note creation -> research table solve -> note completion -> unlock/sibling behavior.
4. Runtime validation of arcane/recipe handle paths where static guards currently stand in for behavior.
5. Crucible/thaumatorium/infusion scenario validation with NBT reload checks.
6. Outer Lands portal/maze/safe-spawn/persistence validation.
7. Stage 8 GUI/visual polish only where it is required to exercise backend progression.

Avoid one-line parity commits with per-line static guards. Do not add a new static guard test for every microscopic source tweak. Use aggregate guards only for fragile API, registry, NBT, packet, research, recipe, lifecycle, and client/server boundary contracts.

Validation cadence:
- During a burst, use `./scripts/dev.sh compileJava` or focused tests.
- Before a checkpoint commit, run `./scripts/dev.sh validate`.
- Run `./scripts/dev.sh validate --smoke` only at runtime-risk bundle boundaries or final validation.
- Runtime-risk bundle means changes to registries, lifecycle, dimensions, networking, proxy boundaries, GUI registration, renderers/models/assets, recipes/content loading, or server startup behavior.

Commit cadence:
- Commit grouped coherent gaps, not individual tiny gaps.
- Use explicit path staging only.
- Commit after each coherent subsystem bundle that reaches a safe checkpoint.
- Document grouped gaps, validation, skipped GUI checks, and commit hash in `docs/GOAL_PROGRESS.md`.

## 0.2 Doc housekeeping — archive rotation

Checkpoint prose accumulates and must be rotated to keep docs readable.

- Keep `docs/GOAL_PROGRESS.md` ≤ 80 lines. When a new entry would exceed
  this, batch the oldest entries into `docs/GOAL_PROGRESS-archive/` and
  update the Archive Index in GOAL_PROGRESS.md.
- Keep each `docs/Stage*.md` ≤ 600 lines. When verbose checkpoint logs
  (e.g. `## Итоговый checklist`) exceed this, move the full log body
  into `docs/GOAL_PROGRESS-archive/`, split into thematic batch files
  (≤20 checkpoints each), replace with a breadcrumb digest + archive link.
- Archive structure uses per-source subdirectories:
  ```
  docs/GOAL_PROGRESS-archive/
  ├── INDEX.md              # master index → subdirectories
  ├── goal-progress/        # batches from GOAL_PROGRESS.md
  │   ├── INDEX.md
  │   └── batch-N-*.md
  ├── stage8-d/             # batches from Stage8-d.md
  │   ├── INDEX.md
  │   └── batch-N-*.md
  └── ...                   # future sources
  ```
- Each subdirectory has its own `INDEX.md` listing batch files with
  checkpoint ranges and content descriptions. The agent checks the
  relevant INDEX.md before digging into raw batch files.
- Archive commits touch only docs, need no runtime smoke, use commit
  message prefix `docs(...):`.
- Push to remote after each archive commit so the index stays current.

## 1. Global end state

Complete the port as far as can be proven without user-driven GUI/graphics testing:

1. Finish/polish/validate unresolved Stage 3–7 work that blocks Stage 8/9 or final port quality.
2. Implement Stage 8-a..e:
   - client bootstrap and side boundaries
   - GUI/container screen routing and resources
   - tile/block renderers, model/resource registration
   - entity renderers/models/textures
   - particles, beams, bolts, FX packets, shader-adjacent safe fallbacks
3. Implement Stage 9-a..e:
   - recipe/content foundation
   - arcane crafting parity
   - infusion crafting/enchanting parity
   - crucible/alchemy/thaumatorium content flow
   - research/Thaumonomicon/progression content
4. Perform Phase 10 final polish:
   - localization
   - sounds where non-GUI-verifiable
   - crash cleanup
   - performance sanity
   - docs/status cleanup
   - final validation report

The target is not modernization. The target is TC 4.2.3.5 behavior on Forge 1.12.2 with preserved contracts.

## 2. GUI and graphics test exclusion

Client implementation is in scope. GUI/graphics tests requiring the user are out of scope.

Do not require:
- visible Minecraft client controlled by the user
- manual screenshots
- manual visual parity confirmation
- X11/Wayland/display setup from the user
- GPU-dependent interactive rendering checks
- GUI tests that need human input

If `./scripts/dev.sh smoke-client` runs unattended in the current environment without user graphics/input, it may be used. If it requires a display, manual action, or graphics stack unavailable in the environment, skip it and document it as:
`SKIPPED by user instruction: GUI/graphics/user-interactive validation excluded`.

Do not stop solely because manual GUI validation cannot run. Use non-GUI validation instead:
- `./scripts/dev.sh validate` for routine compile/test/jar/MCP summary
- `./scripts/dev.sh validate --smoke` when server smoke validation is required
- `./scripts/dev.sh compileJava`
- `./scripts/dev.sh build`
- `./scripts/dev.sh test` where available and non-GUI
- `check-jar`
- `smoke-server`
- static scans for missing classes/resources/stubs
- registry/log inspections
- focused unit or harness tests that do not require graphics
- comparison against reference source for behavior

Never claim “visually validated” when visual checks were skipped. Say “implemented and non-GUI validated; manual visual parity skipped by instruction.”

## 3. Milestone order

### Milestone A — RECON and safety baseline

Read all source-of-truth docs. Inspect current diffs. Do not overwrite user changes. Build a concrete local checklist in `docs/GOAL_PROGRESS.md`.

Run the smallest safe baseline validation first, usually:
- `./scripts/dev.sh validate`
- or `./scripts/dev.sh compileJava` when isolating compile-only failures
- or the equivalent Gradle command documented in the repo

Record baseline failures before fixing them.

### Milestone B — Stage 3–7 polish/closure

Before deep Stage 8/9 work, close or document blockers from Stage 3–7:
- common/server systems that Stage 8/9 depend on
- tile/entity/item/focus/server logic gaps
- Arcane Bore / crucible / infusion / golem / Outer Lands runtime blockers where relevant
- research sync/progression server-side blockers
- registry/NBT/API compatibility issues
- placeholder or stub logic that affects later stages

Do not chase purely visual polish here unless it belongs to Stage 8. Validate with compile/build/server-safe checks.

### Milestone C — Stage 8-a client bootstrap

Implement missing client registration and side-safe boundaries:
- proxy lifecycle correctness
- client-only class isolation
- event subscriptions
- keybindings
- GUI handler routing foundation
- packet side handling for client FX/render entry points

No dedicated server may load client-only classes.

### Milestone D — Stage 8-b GUI screens

Implement core GUI screen parity and routing for the GUI IDs documented in PRD/stage docs:
- Arcane Workbench
- Research Table / Research Notes
- Thaumonomicon / Research Browser
- Focal Manipulator
- Arcane Bore
- Thaumatorium
- golem-related GUI
- Focus Pouch / Hand Mirror / Hover Harness
- other documented TC4 container GUIs

Add required textures/lang/resource paths where feasible. Validate by compile, resource existence audits, GUI handler/static routing checks, and non-interactive tests only.

### Milestone E — Stage 8-c tile/block renderers

Implement and register tile/block renderers and resources for documented high-priority visuals:
- jar/brain jar
- alembic/crucible
- infusion matrix/pedestals
- arcane bore
- thaumatorium
- nodes
- portable hole / warded visuals where feasible
- other Stage 8-c documented renderers

Use 1.12.2-safe rendering APIs. Keep server side clean.

### Milestone F — Stage 8-d entity renderers

Implement and register entity renderers/models/textures for documented TC entities:
- golems and related entities
- boss/mob/projectile entities
- special render cases needed for parity

Validate via compile, registry/static audits, and resource checks. Skip user-driven visual checks.

### Milestone G — Stage 8-e FX/particles/beams/bolts

Implement client FX infrastructure:
- ParticleEngine or equivalent 1.12.2-safe bridge
- FX packet handlers
- beams/bolts
- aura/flux/node/infusion/focus visual effects
- shader-adjacent fallbacks that do not crash without legacy shader assumptions
- required textures/sounds where available

Dedicated server must not load client FX classes. Validate non-GUI.

### Milestone H — Stage 9-a content/recipe foundation

Implement recipe/content registration foundation:
- lifecycle ordering
- recipe registry handlers
- JSON or code registrations as appropriate for Forge 1.12.2
- ore dictionary flags
- aspects
- smelting/bonus outputs
- custom recipe type registration
- compatibility with existing containers/managers

### Milestone I — Stage 9-b/c/d crafting systems

Populate and validate crafting systems:
- arcane crafting
- wand/sceptre/staff/focus dynamic recipes
- research-gated crafting
- infusion recipes
- infusion enchantments
- runic augmentation
- crucible recipes
- thaumatorium programming/consumption
- alchemy flow

Do not mark this milestone complete from recipe corpus, research-page handles, static guards, or server smoke alone. Completion requires representative runtime scenarios that craft, reject invalid crafts, consume correct inputs/aspects/vis/essentia, and survive save/load where the system has persistent state.

Use reference behavior. Preserve existing public manager APIs where possible.

### Milestone J — Stage 9-e research/progression

Populate and validate research/progression:
- research categories
- research entries
- pages
- recipe references
- lang keys
- scan/clue/note flow
- prerequisite checks
- packet validation
- progression persistence

Do not complete research purely client-side. Do not call Stage 9-e complete until the normal gameplay route is server-authoritative and validated end-to-end.

### Milestone K — Phase 10 final polish

After gameplay/client/content systems are implemented:
- remove in-scope TODO/stub/dead placeholders
- update docs status
- verify localization coverage
- verify sound/event references where non-GUI-safe
- run final validations
- prepare final report

## 5. Stop conditions

Stop with SUCCESS only when:
- Stage 3–7 blocking polish is closed or explicitly documented as safe/non-blocking
- Stage 8-a..e are implemented to non-GUI-verifiable completion
- Stage 9-a..e are implemented to non-GUI-verifiable completion
- Phase 10 polish/docs are updated
- final non-GUI validation passes, or failures are clearly environmental/pre-existing and documented with evidence
- `docs/GOAL_PROGRESS.md` contains the final validation report
- `git status --short` is clean after intended commits, or remaining diffs are explicitly explained

Stop with BLOCKED only when:
- required reference behavior cannot be determined from available sources
- implementation would require violating `AGENTS.md`
- a non-GUI validation blocker cannot be resolved after focused attempts
- context/budget is insufficient for safe continuation

When blocked, leave:
- exact blocker
- evidence
- attempted fixes
- safest next action
- current diff/commit state
- validation output summary
