# Durable Goal Progress

Last updated: 2026-05-20
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
- Recent Stage 8-e bursts converted the remaining fallback particle families (`FXBreaking`/`FXSwarm`, smoke spiral/drift, sheet smoke/flame/spell emissions, and entity `BLOCK_CRACK`/`SLIME`/`WATER_BUBBLE`/`VILLAGER_ANGRY` branches) onto dedicated proxy or particle-engine paths, and restored Eldritch Golem beam-charge FX parity; each burst passed `validate`, and runtime-risk bursts also passed `validate --smoke`.
- Latest Stage 8/9 device-content follow-ups restored the missing `LampFertility` bundle end-to-end, then moved the crystal family back onto TESR/TEISR-first routing and replaced the eldritch crystal fallback with a grouped `vcrystal.obj` renderer path; both bursts passed `validate`, and the runtime-risk device/render work also passed `validate --smoke`.
- Recent Stage 9 bursts added typed `ConfigResearch` recipe-handle audits and restored the balanced shard (`itemShard:6`) / `itemResource:14` aspect derivation baseline; validation passed, with smoke run where common config registration paths changed.
- Recent Stage 8-c bursts covered dynamic brainbox/sensor/lifter and tube conduit shells; the latest follow-up moved mirror and essentia-reservoir static shells into block models, leaving TESR for portal/liquid layers and splitting normal vs essentia mirror inventory models, with `validate` and `validate --smoke` passing afterward.
- Latest Stage 8-c follow-up moved the static charger and centrifuge shells into block models (`blockmetaldevice_2`, `blocktube_2`), leaving TESR responsible only for the workbench-charger crystal pulse and centrifuge rotary core; `validate` and `validate --smoke` passed afterward.
- Latest Stage 8-c follow-up moved the static arcane-workbench and deconstruction-table shells into `blocktable_15` and `blocktable_14`, leaving TESR responsible only for wand/thaumometer/item/aspect overlays; `validate` and `validate --smoke` passed afterward.
- Latest Stage 8-c table-family follow-up moved the remaining plain-table and research-table shells into `blocktable_0`, `blocktable_2`, and `blocktable_6`, leaving TESR responsible only for research-table overlays and retiring duplicate plain-table shell rendering; `validate` and `validate --smoke` passed afterward.
- Latest common/research follow-up restored the `BlockTable` conversion contract: inkwells now form master/partner research tables with reference-shaped metadata, wand use recreates the arcane workbench block/tile path, and `TileResearchTable` again exposes expanded render bounds plus the client learn-event sound; `validate` and `validate --smoke` passed afterward.
- Recent Stage 8-c shell-split follow-ups also moved focal manipulator / flux scrubber, jar family, node-device lock shells, and hungry chest body into block models, leaving TESRs responsible only for dynamic overlays, animation pulses, pistons, bubbles, or lid paths; each checkpoint passed `validate` and `validate --smoke`.
- Latest follow-ups moved the static advanced alchemy-furnace shell into `blockstonedevice_0`, then restored its TESR panel path toward reference by keeping dedicated tank-frame rendering and atlas-based vis/lava overlays; the same burst also replaced the crucible `blockmetaldevice_0` full-cube placeholder with a shaped basin shell, restored `TileInfusionPillarRenderer`, `ModelThaumatorium`, `ModelAlembic`, `ModelVisRelay`, `ModelNodeStabilizer`, `TileEssentiaCrystalizerRenderer`, and `TileEldritchCrabSpawnerRenderer` to dedicated reference-shaped OBJ/model paths, and converted golem fishing `WATER_SPLASH` send-sites to entity-status-driven `Thaumcraft.proxy.golemFishingSplashFX(...)`; thaumatorium shell split remains deferred because its facing still lives only in tile NBT, not blockstate.
- Latest common/client follow-up restored `TileJarBrain` client-side rotation/sigh behavior used by `TileJarRenderer`, brought back `EntityPech.handleStatusUpdate(...)` tame/anger feedback via the generic particle path, recovered item-grate open/closed parity in `BlockMetalDevice` with hand/redstone toggles, thin collision, open-item drops, and shaped `blockmetaldevice_5/_6` models, and then restored tile-oriented wooden/metal device routing by pushing bellows / bore base / bore / banner / alembic / charger / vis relay back onto TESR or item-side `builtin/entity` + TEISR paths with reference-shaped `BlockWoodenDevice` bounds; `validate` and `validate --smoke` passed afterward.
- Latest thaumatorium follow-up routed `blockMetalDevice` metas `10/11` back through TESR-first world/item paths, made `TileThaumatoriumRenderer` item-safe for worldless TEISR shell renders, and replaced the stale `metalbase` fallback textures for `blockmetaldevice_10/_11` with `alchemyblock`; both `validate` and `validate --smoke` passed afterward.
- Latest crystalizer follow-up routed `blockTube` meta `7` back through TESR-first world/item paths, added `ItemTubeRenderer` + `blocktube_tesr` for inventory rendering, and made `TileEssentiaCrystalizerRenderer` worldless-safe so the dedicated crystalizer shell no longer depends on the old baked `cube_all` placeholder; both `validate` and `validate --smoke` passed afterward.
- Latest node/device follow-up kept the restored `BlockAiry` TESR/TEISR routing and `TileNode` discharge/lock path, repaired Arcane Furnace reference contracts by switching `TileArcaneFurnace` back to meta-`10` nozzle detection, adding facing-aware blockstate/item-model routing for `blockarcanefurnace`, replacing the meta `10` cube placeholder with a dedicated half-shell nozzle model, restoring the multiblock revert/drop plus core-break blaze branch in `BlockArcaneFurnace`, then restored `TileTube` vent cadence, color handoff, and client vent/creak-fizz feedback, and now also brought the `Portable Hole` / warding runtime family back toward reference with `TileHole` client sparkle cadence plus `TileWardingStone` / `TileWardingStoneFence` server update logic; focused tests and `validate`/`validate --smoke` passed afterward.
- Stage 3-7 residual blockers stay documented and non-blocking for current burst planning, and GUI/manual parity checks remain skipped by instruction.

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

Archives live in `docs/GOAL_PROGRESS-archive/` with per-source subdirectories; each subdirectory has its own `INDEX.md` for detailed batch listings.

| Source | Directory | Content |
|---|---|---|
| `docs/GOAL_PROGRESS.md` | `goal-progress/` | Checkpoint summaries and old snapshot |
| `docs/Stage8-d.md §6` | `stage8-d/` | 82 entity-renderer checkpoint logs (7 batches) |

See `docs/GOAL_PROGRESS-archive/INDEX.md` for the master index.

## Next Checkpoint Candidate

- Continue grouped Stage 8/9 bursts only where runtime-safe non-GUI validation is available, while keeping remaining Stage 3-7 blockers documented until they block a later burst.
- Preserve the GUI/manual exclusion marker for any future visual checks.

Do not mark Stage 6 or Stage 7 complete from this checkpoint alone.
