# Goal: Complete TC4 Golemancy section parity

Status: active
Source: User-approved RECON, plan review, and implementation directive dated 2026-08-04
Last updated: 2026-08-04

## Objective

Complete observable Thaumcraft 4.2.3.5 parity for the full Golemancy section on fresh Forge 1.12.2 worlds. Preserve the already verified heart backend, repair the remaining shared, device, entity, and client defects, correct proven bugs from the original instead of reproducing them, validate runtime-affecting checkpoints on a dedicated server, and produce an up-to-date universal jar.

## Execution Directive

Implement R10-R17 in dependency order using small reversible commits. TC4 behavior is the default oracle. A deviation is allowed only for a reproducible original bug, a conflict with shipped research text or a gameplay invariant, or an unavoidable Minecraft/Forge 1.12 API constraint. Record every retained deviation below and cover changed state transitions with focused tests; do not reopen completed R1-R9 behavior without concrete failing evidence.

## Frozen Contract

### R10: Freeze the full-section baseline

- Scope: 39 Golemancy research nodes, 43 recipes (18 arcane, 15 crucible, 7 infusion, 3 normal), 8 golem materials, Advanced Golems, 12 cores, 6 upgrades, 8 decorations, Bell/markers/GUI, Hungry Chest, Golem Fetter, and Traveling Trunk.
- Acceptance: Fresh-1.12-only policy and parity-deviation threshold are explicit; R1-R9 evidence remains preserved; existing research/recipe corpus guards remain authoritative without a duplicate exhaustive framework.
- Status: verified by `AGENTS.md` and this frozen contract.

### R11: Restore Advanced Golem identity

- Correct `ADVANCEDGOLEM` research to use the wildcard placer tagged `advanced=1` and restore research Warp 5.
- Restore advanced, core, and combined placer item overlays for all eight material models.
- Acceptance: focused metadata/model guards pass and one manual item/research view confirms the advanced identity.
- Status: implementation and automated validation verified; manual item/research view remains pending.

### R12: Restore Fetter and base lifecycle invariants

- Restore Golem Fetter powered state `9 <-> 10`, including correct initial state on powered placement, active-state drops, and Creative visibility.
- Make paused/inactive golems actually disable AI while retaining the movement guard.
- Synchronize configured max health in initial client spawn state.
- Acceptance: focused runtime tests cover placement, power transitions, drops, AI disable/enable, and non-default client max health.
- Status: verified by focused runtime tests, dedicated-server smoke, and build.

### R13: Repair shared inventory and configuration authority

- Return the first successful sided extraction without letting inaccessible or later slots erase it.
- Use Perditio for fuzzy candidate/amount expansion, keep comparison toggles independent, and use TC4 first-ore semantics.
- Correct the inherited Empty-heart double-chest acceptance bug and the golem pickup sound route.
- Enforce core/upgrade/ownership gates server-side for golem configuration and Bell pickup.
- Reject invalid marker sides, normalize invalid colors to wildcard, and retain full-width dimensions.
- Preserve Fill precise-count behavior through 256 across persistence and live container synchronization.
- Acceptance: focused runtime tests cover the two extraction edges, Perditio/toggle matrix, partner-only double chest, raw unauthorized actions, cross-owner pickup, malformed marker boundaries, count 128/255/256 synchronization, and configure-to-Bell-to-placer round trip.
- Status: verified by focused runtime tests, dedicated-server smoke, and build.

### R14: Close independent device and residual-heart defects

- Hungry Chest: execute lid/forced animation state once per tick and synchronize the complete ingestion animation state.
- Lumber: restore `logWood`/wildcard OreDictionary log recognition.
- Liquid: retain colored marker routing and bounded capacity while restoring handler-first source priority and the strict adjacency boundary.
- Acceptance: narrow Hungry, Lumber, and Liquid runtime tests pass together with the existing R1-R9 heart suites; no universal heart lifecycle rewrite is added.
- Status: verified by focused runtime tests, dedicated-server smoke, and build.

### R15: Restore Traveling Trunk behavior

- Restore hop-based follow/combat/Stay locomotion instead of generic navigator walking.
- Correct the Earth-upgrade GUI strip, Entropy vacuum bounds, meaningful hop/open/close/death sounds, and server-authoritative owner mutation.
- Preserve safe teleport behavior and current `OwnerUUID` fresh-world persistence; do not implement legacy `Owner` migration.
- Verify observable cross-dimension behavior: non-Stay follows once without loss/duplication, Stay remains behind. Change the current transfer route only if this contract fails.
- Acceptance: focused state/runtime tests and a compact six-upgrade/manual check pass.
- Status: implementation and automated validation verified; compact manual upgrade/view check remains pending.

### R16: Close scoped client/decorations parity

- Include Mace in the eight-decoration user-visible mapping.
- Correct Dart/Mace carried-fluid pose behavior.
- Manually check only touched views: advanced placer, eight accessories on one representative material, Fetter, Hungry ingestion animation, and Trunk hop/Earth GUI.
- Acceptance: mapping/model guards pass and manual evidence is reported honestly; no pixel-perfect claim is made.
- Status: implementation and automated validation verified; manual touched-view evidence remains pending and is not claimed.

### R17: Produce the validated artifact

- Acceptance: affected focused suites pass, `git diff --check` passes, `./scripts/dev.sh validate --smoke` reaches dedicated-server ready state without crash markers, and `./scripts/dev.sh build` produces `build/libs/Thaumcraft-1.0.0-universal.jar`.

## Constraints

- Support fresh Forge 1.12.2 worlds and data created by this port only; TC4 1.7.10 save/NBT migration is unsupported.
- Preserve Java 8, Forge 1.12.2, public API/package boundaries, registry/config/packet/GUI IDs, and current 1.12-port save contracts.
- Keep `thaumcraft_src/**`, original/donor jars, and decompiled oracle artifacts read-only.
- Use Pareto-focused state-transition tests; avoid Cartesian matrices, speculative abstractions, unrelated cleanup, and dependency changes.
- Common/server checkpoints require `./scripts/dev.sh validate --smoke`; final code requires `./scripts/dev.sh build`.

## Retained Original-Bug Corrections

- Same-crop Ordo replanting instead of planting an unrelated nearby seed.
- Use core honors the shipped Aer cadence promise (`15/12/9`) and uses atomic 1.12 block harvesting.
- In-range melee may proceed without a navigation path; Empty home markers use coordinate equality.
- Liquid marker colors route sources/destinations and multi-source transfers cannot overflow capacity.
- Fishing catch motion targets the spawned item/target center.
- Traveling Trunk teleport rejects obstructed destinations and Entropy vacuum avoids zero-distance division.
- Golem GUI actions are server-authoritative; known hidden-hitbox and stale-client behavior is not restored.

## Non-goals / Deferred

- Pixel-perfect rendering, custom dart geometry, legacy save migration, platform upgrades, TC6 modular golem rewrites, and unrelated addon shims.
- Broad malformed-NBT hardening beyond marker crash boundaries.
- Low-impact polish without a direct repro: Hungry block-face/outline details, broad golem sound/accessor API speculation, Trunk XP/fall/drowning differences, research page scale, and fluid tooltip cosmetics.

## Change Envelope

- Primary: Golemancy research/models, `thaumcraft.common.entities.golems`, golem AI, shared inventory/fluid helpers, golem/trunk containers, Hungry Chest, Fetter block states, focused tests, and this goal.
- Supporting: minimal client model/GUI/render state and existing English localization needed by scoped parity fixes.
- Forbidden: oracle edits, new dependencies, broad architecture changes, generated/decompiled artifacts, worlds/logs, and unrelated gameplay systems.

## Current Checkpoint

- Now: R17, final validation and artifact.
- Next: report automated completion and the outstanding manual visual matrix.
- Stop or replan if: oracle evidence is ambiguous, a proposed deviation fails the `AGENTS.md` threshold, or a fix would break a stable current-port save/API contract.

## Prior Verified Foundation (R1-R9, 2026-08-02)

- Shared sounds, inventory/filter/persistence, Sorting, Harvest/Lumber traversal, Liquid/Essentia, Fishing, Guard/Butcher/navigation, core/upgrade persistence, server smoke, and the universal artifact were previously completed and validated.
- Material retained evidence includes `SortingHeartRuntimeTest`, crop/replant suites, `LiquidEssentiaBackendRuntimeTest`, fishing/bobber suites, `GolemCombatNavigationRuntimeTest`, `GolemR8ContractsRuntimeTest`, `git diff --check`, `./scripts/dev.sh validate --smoke`, and `./scripts/dev.sh build`.
- R10-R17 supersede only claims contradicted by the 2026-08-04 full-section audit; all other R1-R9 evidence remains valid.
