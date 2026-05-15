# Durable Goal Progress

Last updated: 2026-05-15
Branch: `codex/durable-goal-stage8-9`

> Condensed with user approval. The verbose checkpoint prose was produced by another agent and moved to `docs/GOAL_PROGRESS.archive.md`.

## Contract Checklist

- [x] Read source-of-truth files: `AGENTS.md`, `docs/PRD.md`, `docs/GOAL.md`, `build.gradle`, `Dockerfile`.
- [x] Read active stage plans: `docs/Stage3.md` through `docs/Stage9-e.md`.
- [x] Start from `git status --short`: clean at recon start.
- [x] Work on a dedicated branch instead of `master`/`main`.
- [x] Establish baseline non-GUI validation.
- [ ] Close or classify the remaining Stage 3-7 blockers before any Stage 8/9 parity claim.
- [ ] Finish Stage 8 client bootstrap, GUI, render, FX, and keybinding work.
- [ ] Finish Stage 9 recipe/content/research population.
- [ ] Complete Phase 10 polish and final non-GUI validation.
- [ ] Record GUI/manual checks as skipped where interaction or DISPLAY is unavailable.
- [ ] End with clean `git status --short` after the intended checkpoint commits.

## Current Evidence

- `git status --short` was clean at recon start.
- Active branch: `codex/durable-goal-stage8-9`.
- Stage 3/4 server/common baselines are documented as partial; Stage 5-7 remain open in targeted areas.
- `./scripts/dev.sh validate` and `./scripts/dev.sh validate --smoke` pass on current builds.
- GUI/manual graphics checks remain excluded by instruction and headless environment limits.

## Skipped GUI/Manual Graphics Checks

- Interactive GUI, renderer, screenshot, and manual playthrough checks remain skipped until a real client session is available; record future ones as `SKIPPED by user instruction: GUI/graphics/user-interactive validation excluded`.

## Baseline Validation

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh validate` — passed.
- `./scripts/dev.sh validate --smoke` — passed, including server readiness and crash-marker checks.

## Checkpoint Digest

| Area | Condensed status |
| --- | --- |
| Stage 9-e | Research/lang corpus, category/icon baseline, static graph checks, clue unlocks, completion guards, expanded BASICS+THAUMATURGY slices (including infusion-dependent focus and rod/cap branches), and ALCHEMY/ARTIFICE prerequisites (`PHIAL`/`CRUCIBLE`/`NITOR`/`ALUMENTUM`/`DISTILESSENTIA`/`INFUSION`) are partially ported. |
| Stage 9-c | Infusion crafting/enchanting, runic augment, recipe-type localization, and inventory-source alignment are partially ported. |
| Stage 9-b | Arcane workbench/container behavior, static/dynamic arcane recipes, and recipe-key audits are partially ported. |
| Stage 8-a/b | Client bootstrap, keybindings, GUI baselines, textures, and rendering scaffolding are partially ported. |
| Stage 7 | Worldgen, Outer Lands, portal, boss-room, and room-template work remains partial and runtime-unverified. |
| Stage 6 | Golem, trunk, pech, loot, projectile, and boss behavior is substantially ported but still has manual runtime gaps. |
| Stage 5 | Hover Harness and utility item behavior are partially ported; client/manual checks remain open. |
| Docs/validation | Smoke wrapper, validation wrapper, and status-refresh notes are done. |

## Archive

- Full checkpoint index: `docs/GOAL_PROGRESS.archive.md`

## Next Checkpoint Candidate

- Reduce the remaining Stage 8/9 gaps only where they are still server-safe and easy to validate.
- Prefer low-risk Stage 5/6 helper fixes over new GUI or runtime-heavy work.
- Keep Stage 7 runtime evidence and any interactive client checks explicitly marked open.

Do not mark Stage 6 or Stage 7 complete from this checkpoint alone.
