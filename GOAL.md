# Goal: Restore TC4 Warp event parity

Status: active
Source: User-approved RECON plan from 2026-07-19; original TC4 classes under `thaumcraft_src/**`
Last updated: 2026-07-19

## Objective
Make the supported Warp event path on Forge 1.12.2 match Thaumcraft 4.2.3.5 for event scheduling, guardian and mind-spider manifestations, and non-spawn effects.

## Execution Directive
Complete the frozen Required Outcomes using the listed Change Envelope and Primary Evidence. Work on the smallest unresolved outcome. Do not add requirements from reviews, tests, tools, speculative risks, or optional source text. Finish when every required outcome is resolved and affected constraints remain satisfied.

## Frozen Contract

### Required Outcomes
- R1: Restore the Warp event lifecycle.
  - Source: Approved plan, step 1; original `Thaumcraft` and `EventHandlerEntity` behavior.
  - Acceptance: Normal Warp changes arm `warpCounter`; event checks run every 2000 player ticks only outside wuss mode and Warp Ward; Death Gaze runs every 10 active ticks; player death does not reset the counter.
  - Primary evidence: Focused lifecycle guard test and `./scripts/dev.sh validate --smoke`.
  - Status: verified
  - Evidence: `WarpLifecycleStaticGuardTest`; `./scripts/dev.sh validate --smoke` and `./scripts/dev.sh build` passed on 2026-07-19.
- R2: Restore guardian and mind-spider manifestation behavior.
  - Source: Approved plan, step 2; original `WarpEvents`, `EntityMindSpider`, and `EntityEldritchGuardian` behavior.
  - Acceptance: Spawn selection and placement stay equivalent; harmless viewer-bound spiders retain pursuit AI, cannot attack, remain damageable, do not trigger pressure plates, and use no vanilla loot; Warp guardians without a home may despawn.
  - Primary evidence: Focused entity parity guards, `./scripts/dev.sh validate --smoke`, and controlled runtime observation where available.
  - Status: pending
  - Evidence:
- R3: Restore non-spawn Warp effect and feedback parity.
  - Source: Approved plan, step 3; original `WarpEvents`, Death Gaze, and `PacketWarpMessage` behavior.
  - Acceptance: Warp potion curatives and ambient/particle flags, Death Gaze target filtering, and Warp HUD notifications/whisper conditions match TC4 behavior on 1.12.2.
  - Primary evidence: Focused effect/network guards, existing packet tests, and `./scripts/dev.sh validate --smoke`.
  - Status: pending
  - Evidence:
- R4: Keep every iteration deployable and compare the final path with TC4.
  - Source: User instruction to commit and deployment-check every iteration and compare everything.
  - Acceptance: Each checkpoint has a scoped commit after focused validation, server smoke, and jar build; closure includes a final source comparison of every changed runtime branch.
  - Primary evidence: Checkpoint history with commit hashes and successful commands.
  - Status: in_progress
  - Evidence:

### Constraints
- C1: Preserve Java 8, Forge 1.12.2, public `thaumcraft.api.*` signatures, registry names, NBT keys, packet ids, GUI ids, and dimension ids.
- C2: Treat `thaumcraft_src/**` and donor jars as read-only references.
- C3: Runtime-affecting checkpoints require server smoke; code checkpoints end with `./scripts/dev.sh build`.

### Non-goals
- Migrating external TC4 player save files into the 1.12 capability format.
- Refactoring unrelated entity, research, capability, rendering, or networking code.
- Adding production debug commands, dependencies, or new public API.

## Change Envelope
- Target: Existing Warp lifecycle, manifestation entities, potion effects, and client feedback.
- Expected paths, symbols, and direct consumers: `Thaumcraft` Warp helpers; `EventHandlerEntity`; `WarpEvents`; `EntityMindSpider`; `EntityEldritchGuardian`; `PacketWarpMessage`; focused tests and this contract.
- Allowed artifacts: Minimal Java fixes, focused tests, `GOAL.md`, ignored validation/recon logs, and iteration commits.
- Forbidden artifacts: Changes under `thaumcraft_src/**`, dependency/config upgrades, new persistent state, registry/API renames, broad cleanup.
- User or harness budget: One deployable commit per approved plan iteration; compare changed behavior with TC4 before advancing.

## Current Checkpoint
- Closes: R2; advances R4.
- Smallest next action: Restore Mind Spider harmless behavior and guardian despawn parity without changing the proven spawn helper.
- Expected evidence: Focused entity parity guards, `./scripts/dev.sh validate --smoke`, then `./scripts/dev.sh build` pass before commit.
- Stop or replan if: 1.12 entity APIs cannot preserve the original behavior without a registry, NBT, or public API change.

## Current State
- Resolved: R1 Warp mutation, cadence, suppression, and Death Gaze scheduling parity.
- Last relevant evidence: Focused lifecycle test, server smoke, and build passed.
- Blocker: None.
- Next: Implement R2.

## Material Decisions
- 2026-07-19: Split work into lifecycle, manifestations, and effects/feedback so each commit is independently deployable.
- 2026-07-19: Keep TC4 save migration and unrelated low-impact rendering differences outside the approved envelope.

## Checkpoint History
- 2026-07-19: Contract frozen from the approved RECON plan; R1 selected first.
- 2026-07-19: R1 verified. Restored counter arming, 2000-tick Warp scheduling, Warp Ward/wuss suppression, 10-tick Death Gaze scheduling, and removed the non-original death reset. Focused test, server smoke, and build passed; iteration commit pending.

## Completion
- Resolved outcomes:
- Commands and artifacts:
- Constraint and diff-scope check:
- Final status:
