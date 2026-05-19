# GOAL_PROGRESS Batch 04 — Stage 8-d / 8-e

Split from `docs/GOAL_PROGRESS.md` on 2026-05-19.

## Stage 8-d

- Entity renderer coverage for golems, bosses, projectiles, taint mobs, cultists, pechs, wisps, and special cases.
- Dedicated renderer registrations replaced noop/fallback coverage for the current entity set.

## Stage 8-e

- Dedicated beams, bolts, particles, overlays, and FX packets were restored through proxy-safe routing.
- Client FX helpers, packet handlers, and particle-engine dispatch were hardened behind aggregate guards.

## Notes

- GUI/manual visual checks remain excluded from this archive.
