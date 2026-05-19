# Durable Goal Progress

Last updated: 2026-05-19
Branch: `codex/durable-goal-stage8-9`

> Condensed with user approval. Historical checkpoint prose is split into batch archives under `docs/GOAL_PROGRESS-archive/`.

## Contract Checklist

- [x] Read source-of-truth files: `AGENTS.md`, `docs/PRD.md`, `docs/GOAL.md`, `build.gradle`, `Dockerfile`.
- [x] Read active stage plans: `docs/Stage3.md` through `docs/Stage9-e.md`.
- [x] Start from `git status --short`: clean at recon start.
- [x] Establish baseline non-GUI validation.
- [ ] Close or classify the remaining Stage 3-7 blockers before any Stage 8/9 parity claim.
- [ ] Finish Stage 8 client bootstrap, GUI, render, FX, and keybinding work.
- [ ] Finish Stage 9 recipe/content/research population.
- [ ] Complete Phase 10 polish and final non-GUI validation.
- [ ] Record GUI/manual checks as skipped where interaction or DISPLAY is unavailable.
- [ ] End with clean `git status --short` after the intended checkpoint commits.

## Current Evidence

- Live checkpoint prose moved into `docs/GOAL_PROGRESS-archive/` batch files.
- Recent validated work remains concentrated in Stage 8-c/8-d/8-e renderer, entity, and FX bursts plus Stage 9 recipe/content/research bursts.
- Stage 3-7 residual blockers stay documented and non-blocking for current burst planning.
- GUI/manual parity checks remain skipped by instruction.

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
| Stage 3-7 | Residual blockers and runtime gaps are archived; no new closure claim here. |
| Stage 8-a/b | Client bootstrap, GUI routing, and side boundaries are established. |
| Stage 8-c | Tile/block renderer, model, and resource parity is largely in place. |
| Stage 8-d | Entity renderer coverage is largely in place. |
| Stage 8-e | Dedicated FX, beams/bolts, and packet routing are largely in place. |
| Stage 9-a..e | Recipe/content/crafting/research systems are substantially ported and guarded. |
| Docs/validation | Progress history is archived into batches; live file stays concise. |

## Archive Index

| File | Source | Content |
|---|---|---|
| `old-GOAL_PROGRESS.archive.md` | GOAL_PROGRESS.md | Original snapshot before batch split |
| `batch-01-stage3-5.md` | GOAL_PROGRESS.md | Stage 3–5 checkpoint prose |
| `batch-02-stage6-7.md` | GOAL_PROGRESS.md | Stage 6–7 checkpoint prose |
| `batch-03-stage8-c.md` | GOAL_PROGRESS.md | Stage 8-c checkpoint prose |
| `batch-04-stage8-d-e.md` | GOAL_PROGRESS.md | Stage 8-d/e checkpoint prose |
| `batch-05-stage9.md` | GOAL_PROGRESS.md | Stage 9 checkpoint prose |
| `stage8-d-checkpoints.md` | Stage8-d.md §6 | 42 entity-renderer checkpoint logs |

## Next Checkpoint Candidate

- Continue grouped Stage 8/9 bursts only where runtime-safe non-GUI validation is available.
- Keep remaining Stage 3-7 blockers documented until they block a later burst.
- Preserve the GUI/manual exclusion marker for any future visual checks.

Do not mark Stage 6 or Stage 7 complete from this checkpoint alone.
