# Goal: Restore TC4 Deconstruction Table parity

Status: in progress
Source: User-approved RECON plan from 2026-07-23; original TC4 classes under `thaumcraft_src/**`
Last updated: 2026-07-23

## Objective
Make the Deconstruction Table backend, persistence, container authority, GUI, TESR, and item form match Thaumcraft 4.2.3.5 on Forge 1.12.2.

## Execution Directive
Implement the approved plan in three deployable iterations. Each iteration ends with focused tests, `./scripts/dev.sh validate --smoke`, `./scripts/dev.sh build`, and a scoped commit. Do not include unrelated working-tree changes.

## Frozen Contract

### Required Outcomes
- R1: Restore the tile backend and persistence.
  - Acceptance: Valid inputs process in 40 server ticks; the original primal reduction, `nextInt(80)` success roll, distinct-primal selection, item consumption, pending-output blocking, sided inventory, and transient progress contracts hold; canonical TC4 NBT is written with legacy port-key fallback reads.
  - Status: verified
  - Evidence: `TileDeconstructionTableRuntimeTest`; focused test, `./scripts/dev.sh validate --smoke`, and `./scripts/dev.sh build` passed on 2026-07-23.
- R2: Restore authoritative container behavior.
  - Acceptance: Progress synchronizes, shift-click follows the original order, and claiming a pending aspect awards exactly one pool point without duplication or loss before clearing and synchronizing the result.
  - Status: pending
- R3: Restore GUI, TESR, and item-form visual parity.
  - Acceptance: Aspect tint/tooltip, table overlays, input animation, blend/scale/position, and meta-14 TESR item routing match the audited TC4 paths; original assets remain unchanged.
  - Status: pending
- R4: Keep every iteration deployable and close with a TC4 branch comparison.
  - Acceptance: Each checkpoint has a scoped commit after focused validation, server smoke, and jar build; final report records hashes, files, commands, runtime status, and known visual limitations.
  - Status: pending

### Constraints
- Preserve Java 8, Forge 1.12.2, public API signatures, registry names, packet ids, GUI ids, and dimensions.
- Treat `thaumcraft_src/**` and donor jars as read-only.
- Do not introduce new assets or an `ItemStackHandler` rewrite.
- Do not broaden this task into localization or unrelated table-family cleanup.

## Iterations
1. Tile backend, inventory/NBT migration, and deterministic runtime coverage.
2. Container transfer/award authority and runtime coverage.
3. GUI/TESR/item routing, visual guards, smoke/build, and final TC4 comparison.

## Current Checkpoint
- Target: R2.
- Expected files: `ContainerDeconstructionTable`, focused container tests, and this contract.
- Stop or replan if the 1.12 capability award cannot remain atomic with pending-output clearing.

## Material Decisions
- Write canonical `Items`/`Aspect`/`CustomName`; read accidental `Inventory`/`aspect` keys as migration fallback.
- Keep `breaktime` transient and synchronized through the existing container property rather than NBT.
- Keep original textures byte-for-byte; visual work changes render paths only.

## Checkpoint History
- 2026-07-23: R1 restored the 40-tick server state machine, original primal roll/selection and consumption rules, inventory limits/usability, canonical TC4 persistence with legacy port-key reads, transient progress, and stale-client-state clearing. Focused runtime tests, server smoke, and build passed.
