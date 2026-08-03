# Active State: Full Eldritch parity audit fixes

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Goal-Status: active
State-Revision: 17
Last-Updated: 2026-08-03
Contract-Version: 2.1
Contract-SHA256: 8e61c9d38410bccb4bc113123852bade9e62a44d9b9c388a3382ad4ba53605fd
Recon-Version: 2.1
Recon-SHA256: 446b153014e62315f935223a765cbafdec9f2ace5e622faaa548202e336b79c6
Sources-SHA256: 627bad6dfae656e2f98998eacfa70f10c2c2a92db4c4126731a772f7fb3dc714
Reports-SHA256: 2563ac1d90f12a2cd83bb06486ea9782c7ad7c48d45242cabc180b4a213500f5
Git-Branch: master
Git-HEAD: 7b2f19096f3e907d028f3a7208a78747907e5243
Expected-Dirty-Paths: `src/main/java/thaumcraft/common/entities/monster/EntityEldritchGuardian.java`, `src/test/java/thaumcraft/common/entities/monster/EntityEldritchGuardianStaticGuardTest.java`, `docs/goals/2026-08-03-eldritch-full-parity-audit-fixes/{STATE.md,LOG.md}`
Last-Durable-Flush: 2026-08-03

<protect>
## Rehydration Core

- Active Outcomes: none
- Active Findings: none
- Checkpoint Status: closed
- Hypothesis: Confirmed; removing the erroneous XP override preserves constructor XP 20, and `getTalkInterval() == 500` assigns the constant to its mapped TC4 role without touching sound or drop methods.
- Smallest Next Action: Commit R-004/F-039, then start the first R-005/F-085 checkpoint.
- Expected Evidence: The checkpoint commit contains only the Guardian XP/talk correction, focused guard, and ledger state.
- Stop/Replan If: commit contents include deferred Guardian/entity findings or any unrelated product/toolkit path.
- Working Set: R-004 product/test diff and STATE/LOG until commit
- Last Material Command: focused Guardian test followed by `./scripts/dev.sh validate --smoke`
- Last Material Result: focused BUILD SUCCESSFUL in 17s; smoke PASS (`git-status compile+test+reobf check-jar smoke-server`); `git diff --check` passed
- Blocker: none
- First Re-entry Action: verify hashes and Git, then read active R/F entries before edits
</protect>

## Outcome Status

- R-001 | verified | evidence: focused lifecycle test and `validate --smoke`, E-0012
- R-002 | verified | evidence: focused support/placement static evidence and `validate --smoke`, E-0014
- R-003 | verified | evidence: mapped Guardian method guard and `validate --smoke`, E-0016
- R-004 | verified | evidence: focused Guardian guard; `validate --smoke`; E-0018
- R-005 | pending | evidence: none
- R-006 | pending | evidence: none
- R-007 | pending | evidence: none
- R-008 | pending | evidence: none

## Finding Status

- F-018 | verified | evidence: equipped base/robe and stored base each repair `5 -> 4` through `InventoryPlayer.decrementAnimations`; E-0012
- F-035 | verified | evidence: no placement-time drop; exact recorded support predicates for ordinary/meta-7 crystals; E-0014
- F-038 | verified | evidence: `getTotalArmorValue() == 4`, no custom spawn-cap override; E-0016
- F-039 | verified | evidence: constructor XP 20, no XP override, talk interval 500; E-0018
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
- F-035 | checkpoints_started=1 | material_replans=0
- F-038 | checkpoints_started=1 | material_replans=0
- F-039 | checkpoints_started=1 | material_replans=0
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

- Git status: user-owned `SKILL.md` remains dirty; R-004 product/test and STATE/LOG are ready to commit
- Relevant diff summary: removed Guardian 500-XP override, added mapped 500-tick talk interval, added focused assertions
- Unexpected paths/state: HEAD advanced from `6737687` to `866fea24` during the second interrupted report wave through an unrelated general-agent configuration commit.
- Reconciliation decision: preserve user-updated `SKILL.md`; commit only the four R-004 product/test/ledger paths; keep deferred Guardian/entity findings untouched.

## Queued Next Checkpoint

- Outcomes/Findings: R-005 / F-085 after R-004 closes and commits
- Why it is next: next admitted high-impact progression defect; first of two allocated checkpoints
- Preconditions: R-004 focused evidence and common/server smoke pass; checkpoint commit exists
