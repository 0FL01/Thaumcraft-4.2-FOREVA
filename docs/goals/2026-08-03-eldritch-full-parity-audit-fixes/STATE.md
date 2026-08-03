# Active State: Full Eldritch parity audit fixes

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Goal-Status: active
State-Revision: 11
Last-Updated: 2026-08-03
Contract-Version: 2.1
Contract-SHA256: 8e61c9d38410bccb4bc113123852bade9e62a44d9b9c388a3382ad4ba53605fd
Recon-Version: 2.1
Recon-SHA256: 446b153014e62315f935223a765cbafdec9f2ace5e622faaa548202e336b79c6
Sources-SHA256: 627bad6dfae656e2f98998eacfa70f10c2c2a92db4c4126731a772f7fb3dc714
Reports-SHA256: 2563ac1d90f12a2cd83bb06486ea9782c7ad7c48d45242cabc180b4a213500f5
Git-Branch: master
Git-HEAD: f27fddfb4b003f76f870d2ce6010f473b2e153cb
Expected-Dirty-Paths: `src/main/java/thaumcraft/common/items/armor/ItemVoidArmor.java`, `src/main/java/thaumcraft/common/items/armor/ItemVoidRobeArmor.java`, `src/test/java/thaumcraft/common/items/armor/ItemVoidArmorParityTest.java`, `docs/goals/2026-08-03-eldritch-full-parity-audit-fixes/{STATE.md,LOG.md}`
Last-Durable-Flush: 2026-08-03

<protect>
## Rehydration Core

- Active Outcomes: none
- Active Findings: none
- Checkpoint Status: closed
- Hypothesis: Confirmed; 1.12 `InventoryPlayer.decrementAnimations` reaches equipped armor through ordinary `onUpdate`, so removing the duplicate armor callback preserves both equipped and stored repair with one invocation.
- Smallest Next Action: Commit R-001/F-018, then start R-002/F-035 from that commit.
- Expected Evidence: The checkpoint commit contains only the two Void armor callback removals, the focused lifecycle assertion, and ledger state; user-owned `SKILL.md` remains excluded.
- Stop/Replan If: commit contents include a deferred candidate or any unrelated product/toolkit path.
- Working Set: R-001 product/test diff and STATE/LOG until commit
- Last Material Command: `./scripts/dev.sh validate --smoke`
- Last Material Result: PASS (`git-status compile+test+reobf check-jar smoke-server`); focused `ItemVoidArmorParityTest` also passed, BUILD SUCCESSFUL in 18s
- Blocker: none
- First Re-entry Action: verify hashes and Git, then read active R/F entries before edits
</protect>

## Outcome Status

- R-001 | verified | evidence: focused lifecycle test and `validate --smoke`, E-0012
- R-002 | pending | evidence: none
- R-003 | pending | evidence: none
- R-004 | pending | evidence: none
- R-005 | pending | evidence: none
- R-006 | pending | evidence: none
- R-007 | pending | evidence: none
- R-008 | pending | evidence: none

## Finding Status

- F-018 | verified | evidence: equipped base/robe and stored base each repair `5 -> 4` through `InventoryPlayer.decrementAnimations`; E-0012
- F-035 | pending | evidence: none
- F-038 | pending | evidence: none
- F-039 | pending | evidence: none
- F-085 | pending | evidence: none
- F-087 | pending | evidence: none
- F-088 | pending | evidence: none
- F-105 | pending | evidence: none
- F-127 | pending | evidence: reports/proposals accepted; final-diff check pending
- F-128 | pending | evidence: reports/proposals accepted; final-diff check pending
- F-129 | pending | evidence: A-017 preserve evidence; final-diff check pending
- F-130 | pending | evidence: A-017 preserve evidence; final-diff check pending
- F-131 | pending | evidence: adaptation reports accepted; final-diff check pending
- F-133 | pending | evidence: deferred policy boundary recorded

## Finding Work Counters

- F-018 | checkpoints_started=1 | material_replans=0
- F-035 | checkpoints_started=0 | material_replans=0
- F-038 | checkpoints_started=0 | material_replans=0
- F-039 | checkpoints_started=0 | material_replans=0
- F-085 | checkpoints_started=0 | material_replans=0
- F-087 | checkpoints_started=0 | material_replans=0
- F-088 | checkpoints_started=0 | material_replans=0
- F-105 | checkpoints_started=0 | material_replans=0

## Resource Counters

- Implementation Subagent Waves Used: 0
- Closure Review Passes Used: 0
- Scope Amendments Used: 0
- Adjacent Finding Auto-Promotions Used: 0
- Post-Closure Work Items Used: 0

## Current Diff/Reconciliation

- Git status: user-owned `SKILL.md` remains dirty; ledger committed at `f27fddfb`; no product paths dirty at checkpoint start
- Relevant diff summary: removed duplicate `onArmorTick` repair callbacks from base/robe armor; added one focused inventory-lifecycle test; R-001 evidence passed
- Unexpected paths/state: HEAD advanced from `6737687` to `866fea24` during the second interrupted report wave through an unrelated general-agent configuration commit.
- Reconciliation decision: preserve user-updated `SKILL.md`; use `f27fddfb` as R-001 baseline; freeze exactly 8 findings/10 checkpoints and exclude every deferred candidate from implementation.

## Queued Next Checkpoint

- Outcomes/Findings: R-002 / F-035 after the R-001 checkpoint commit
- Why it is next: next smallest deterministic supported-path finding
- Preconditions: R-001 targeted evidence and common/server smoke pass; checkpoint commit exists
