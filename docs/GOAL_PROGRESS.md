# Durable Goal Progress

Last updated: 2026-05-20
Branch: `codex/durable-goal-stage8-9`

> Condensed with user approval. Historical checkpoint prose is split into batch archives under `docs/GOAL_PROGRESS-archive/`.

## Contract Checklist

- [x] Read source-of-truth files and Stage 3-9 docs.
- [x] Start from `git status --short` and establish baseline non-GUI validation.
- [ ] Close or classify Stage 3-7 blockers before any Stage 8/9 parity claim.
- [ ] Finish Stage 8/9 implementation and validation work.
- [ ] Record GUI/manual checks as skipped where interaction or DISPLAY is unavailable.
- [ ] End with clean `git status --short` after checkpoint commits.

## Current-truth interpretation guard

This file is a progress digest, not a parity certificate.

- `static guarded` means source/corpus regressions only.
- `validate --smoke passed` means build/server-load stability only.
- Stage 8 visual progress is not backend parity evidence.
- Stage 9 recipe/research corpus progress is not server-authoritative progression proof.

Current backend blockers remain: research/progression runtime route, research table C2S authority, scan authority, alchemy/thaumatorium/infusion runtime validation, Outer Lands portal/maze validation, and save/load behavior for complex tiles.

## Current Evidence

- Detailed checkpoint prose lives in `docs/GOAL_PROGRESS-archive/`; this file is a digest only.
- Stage 8 client/render/FX work is archived; Stage 9 remains runtime-open.

## Skipped GUI/Manual Graphics Checks

- Interactive GUI, renderer, screenshot, and manual playthrough checks remain skipped until a real client session is available.

## Baseline Validation

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh validate` — passed.
- `./scripts/dev.sh validate --smoke` — passed; server readiness and crash-marker checks only.

### Validation interpretation limit

Latest validation proves build/server-load stability only, not backend parity or gameplay closure.

## Checkpoint Digest

| Area | Condensed status |
| --- | --- |
| Stage 3-7 | Residual blockers and runtime gaps are archived; no new closure claim here. |
| Stage 8 | Client/render/FX progress is archived; not backend parity evidence. |
| Stage 9 | Substantial implementation/corpus present; runtime validation and server-authoritative progression remain open. |
| Docs/validation | Progress history is archived into batches; live file stays concise. |

## Archive Index

See `docs/GOAL_PROGRESS-archive/INDEX.md` for batch listings and content.

## Active backend blocker register

Do not claim backend substantially complete until these are resolved or scoped out:

1. Research table C2S authority.
2. Scan authority.
3. Normal research progression route.
4. Research table NBT symmetry.
5. Alchemy/thaumatorium/infusion runtime validation.
6. Outer Lands runtime validation.
7. Static guard overconfidence.

## Next Checkpoint Candidate

- Continue grouped Stage 8/9 bursts only where runtime-safe non-GUI validation is available.
- Preserve the GUI/manual exclusion marker and do not mark Stage 6 or Stage 7 complete from this checkpoint alone.
