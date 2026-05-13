# Thaumcraft 4.2.3.5 -> Forge 1.12.2 Repair Mine List

## 1. Purpose

This is the active pre-Phase8 mine list. It is intentionally shorter than `docs/PRD.md` and is not a parity-complete ledger.

Use it to pick the next focused checkpoint and to avoid reopening areas that already have a common/server baseline unless current code or runtime evidence contradicts them.

Do not start Phase 8 client work as a parity claim until the mines below are verified or explicitly deferred with evidence in the checkpoint report.

## 2. Required baseline workflow

Before changing implementation code:

- Run `git status --short`.
- Read `AGENTS.md`, `docs/PRD.md`, this file, `docs/CODEX_GOAL.md`, `build.gradle`, and `Dockerfile`.
- Inspect original 1.7.10 behavior for every gameplay-critical touched class.
- Run focused validation with `./scripts/dev.sh`; use Docker unless the local environment is known Java 8/Forge-compatible.

Compile/build success is not a parity close. Runtime-affecting claims need server/client smoke or explicit manual scenario notes.

## 3. Current common/server baseline, not phase closure

The following baselines exist in current source and should not be re-audited as fresh blockers without contrary evidence:

- Aura/wand centi-vis units, wand discounts, and no passive wand recharge baseline.
- Online capability/cache-backed research lookup and restored server potion-effect baseline.
- Crucible item ingestion, water interaction, spill behavior, and aspect-container exposure baseline.
- Infusion Matrix server lifecycle baseline.
- Focus server actions for Pech, Hellbat, Trade, Excavation, Portable Hole, and Warding.
- Portable Hole and Warding server/common wrapper blocks and tile entities. Their visual renderer work remains Phase 8.
- Arcane Bore server mining-loop baseline with focus/pickaxe gates, fortune/silk handling, pickaxe damage, and base inventory output.
- Vis Amulet storage/bauble consumption integration, Thaumometer entity/block scan hook, and Frugal focus applicability baseline.
- Material-based repair items, Primal Crusher tool baseline, and targeted removal of incorrect always-false repair checks.
- Cultist Leader, Eldritch Golem, Eldritch Warden, Inhabited Zombie crab spawn, Pech death loot, and `blockAiry` meta 10/11 server baselines.
- Outer Lands registration/runtime-hook baseline.

Last recorded checkpoint evidence for the boss/special-mob pass was Docker `compileJava` and `build` on 2026-05-13. That is build evidence only, not runtime/manual parity validation.

## 4. Active pre-Phase8 mines

### M1. Build/runtime/manual validation gap

Required before treating the pre-Phase8 server gate as closed:

- Run current `compileJava`/`build` or document a pre-existing environment failure.
- Run `./scripts/dev.sh smoke-server` for common/server changes when possible.
- Document focused manual scenarios for foci, Arcane Bore, baubles/Thaumometer, repairability, bosses/special mobs, golems, and Outer Lands where those systems are claimed.

### M2. Phase 3 core-system risk

Still not closed:

- Offline `.thaum`/`.thaumbak` research migration remains deferred; current lookup is capability/cache based.
- Research sync timing and capability persistence require runtime verification.
- Runic Ring charge values exist, but tick behavior must be checked against original behavior before any parity claim.
- Vis Amulet and inventory vis consumption need manual source/combination scenarios, not only code inspection.

### M3. Phase 4 block/tile risk

Still not closed:

- Arcane Bore has a mining-loop baseline, but needs runtime/manual mining scenarios.
- `TileArcaneBoreBase` is minimal; verify no original base-side server behavior was omitted before final parity claims.
- Thaumatorium, Focal Manipulator, essentia transport, tile sync, and GUI-dependent flows remain separate risks.
- Portable Hole/Warding wrapper visuals are Phase 8, even though server wrappers exist.

### M4. Phase 5 item/equipment/focus risk

Still not closed:

- Focus server baselines need manual world/entity scenarios and cost-consumption checks.
- Focus visual feedback, beams, particles, and render-only effects are Phase 8.
- Hover Harness flight/hover mechanics remain deferred.
- Equipment and armor repairability had targeted fixes, but broad parity still needs original-reference spot checks.

### M5. Phase 6 entity/golem risk

Still not closed:

- Boss/special-mob server baselines need runtime/manual spawn and combat validation.
- Pech death loot was restored; Pech trade, taming, pickup, and combat behavior were not fully revalidated by that checkpoint.
- Golem core AI and GUI/client presentation need separate verification; renderers are Phase 8.

### M6. Phase 7 worldgen/Outer Lands risk

Still not closed:

- Outer Lands generation/runtime smoke tests are still required.
- Room-generator parity, maze persistence, structure query hooks, and portal teleport safety remain risk areas.
- Biome color/debug overlay cosmetics are not server blockers, but must be documented if deferred.

## 5. Explicit deferrals

Keep these visible in final reports until implemented or consciously accepted as out of scope:

- Offline `.thaum`/`.thaumbak` research migration: deferred; current source uses capability/cache research state.
- Portable Hole and Warding visual renderers/FX: deferred to Phase 8.
- Phase 8 client GUI/renderers/keybinds/particles/beams/bolts/shaders/resources: not complete.
- Phase 9 recipes/research/content registrations: not complete; `ConfigRecipes` and `ConfigResearch` remain placeholders.
- Hover Harness flight/hover mechanics: deferred item/equipment work.
- Outer Lands runtime smoke, room parity, maze persistence, and portal safety: Phase 7 validation remains open.

## 6. Phase 8 client backlog

Start with an inventory of current `thaumcraft.client.*` against `thaumcraft_src/thaumcraft/client/**`.

Implement in small groups:

- client proxy registrations;
- core GUI screens;
- TESRs;
- entity renderers;
- particles, beams, and bolts;
- shaders and required resources.

Validation:

- `./scripts/dev.sh compileJava`.
- `./scripts/dev.sh gradle processResources` after resource changes.
- `./scripts/dev.sh smoke-client` if display/X11 is available.
- Manual GUI/render smoke scenarios.

## 7. Phase 9 and Phase 10 backlog

Phase 9 starts after required GUI/client paths are usable enough to verify content:

- recipe registration;
- research registration;
- Thaumonomicon references;
- aspect tags;
- critical progression smoke tests.

Phase 10 polish starts only after gameplay, client, and content baselines exist. Do not use polish, localization breadth, optional integration, or broad cleanup to hide unresolved core parity gaps.

## 8. Required final report per checkpoint

Every checkpoint must end with:

- Summary of changes.
- Files modified.
- Original reference classes inspected.
- Architecture before/after.
- Acceptance checklist.
- Validation commands and results.
- Runtime/manual checks.
- Known limitations and explicit deferrals.
- Blockers.
- Suggested next checkpoint.

After a blocker is fixed, update this file factually and include validation evidence. Do not mark a phase done unless runtime/manual evidence supports it.
