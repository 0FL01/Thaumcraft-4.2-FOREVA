# Active State: Full Eldritch parity audit fixes

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Goal-Status: active
State-Revision: 29
Last-Updated: 2026-08-03
Contract-Version: 2.1
Contract-SHA256: 8e61c9d38410bccb4bc113123852bade9e62a44d9b9c388a3382ad4ba53605fd
Recon-Version: 2.1
Recon-SHA256: 446b153014e62315f935223a765cbafdec9f2ace5e622faaa548202e336b79c6
Sources-SHA256: 627bad6dfae656e2f98998eacfa70f10c2c2a92db4c4126731a772f7fb3dc714
Reports-SHA256: 2563ac1d90f12a2cd83bb06486ea9782c7ad7c48d45242cabc180b4a213500f5
Git-Branch: master
Git-HEAD: c8567267
Expected-Dirty-Paths: `src/main/java/thaumcraft/common/lib/research/ScanManager.java`, `src/test/java/thaumcraft/common/lib/research/{ScanProgressionRuntimeTest.java,ScanManagerThaumometerNotificationStaticGuardTest.java}`, `docs/goals/2026-08-03-eldritch-full-parity-audit-fixes/{STATE.md,LOG.md}`
Last-Durable-Flush: 2026-08-03

<protect>
## Rehydration Core

- Active Outcomes: none
- Active Findings: none
- Checkpoint Status: closed
- Hypothesis: Confirmed; the interface override is distinct from explicit keyed scanning and now returns null without mutation, allowing the next registered handler to supply the raw target.
- Smallest Next Action: Commit R-008/F-105, then consume the single frozen closure review pass.
- Expected Evidence: The commit contains only the override, focused handler/pass-through evidence, and ledger state.
- Stop/Replan If: commit contents include explicit keyed/item/entity scan APIs, End Portal routing, award semantics, or deferred scan findings.
- Working Set: R-008 product/test diff and STATE/LOG until commit
- Last Material Command: focused scan runtime/static tests followed by `./scripts/dev.sh validate --smoke`
- Last Material Result: focused BUILD SUCCESSFUL in 18s; smoke PASS; built-in handler is side-effect-free and later handler is reached exactly once
- Blocker: none
- First Re-entry Action: verify hashes and Git, then read active R/F entries before edits
</protect>

## Outcome Status

- R-001 | verified | evidence: focused lifecycle test and `validate --smoke`, E-0012
- R-002 | verified | evidence: focused support/placement static evidence and `validate --smoke`, E-0014
- R-003 | verified | evidence: mapped Guardian method guard and `validate --smoke`, E-0016
- R-004 | verified | evidence: focused Guardian guard; `validate --smoke`; E-0018
- R-005 | verified | evidence: client capability runtime/static guards; smoke/build; E-0022
- R-006 | verified | evidence: fixed endpoint authority runtime test; smoke; E-0026
- R-007 | verified | evidence: PRIMPEARL direct and OUTERREV note clue cases; smoke; E-0028
- R-008 | verified | evidence: null/no-mutation and later-handler runtime/static evidence; smoke; E-0030

## Finding Status

- F-018 | verified | evidence: equipped base/robe and stored base each repair `5 -> 4` through `InventoryPlayer.decrementAnimations`; E-0012
- F-035 | verified | evidence: no placement-time drop; exact recorded support predicates for ordinary/meta-7 crystals; E-0014
- F-038 | verified | evidence: `getTotalArmorValue() == 4`, no custom spawn-cap override; E-0016
- F-039 | verified | evidence: constructor XP 20, no XP override, talk interval 500; E-0018
- F-085 | verified | evidence: server-first live local capability bridge exposes ELDRITCHMINOR/Eldritch aspect; E-0022
- F-087 | verified | evidence: type-1 guard precedes every mutation/completion branch; E-0026
- F-088 | verified | evidence: hidden/lost clue check precedes every completion workflow; E-0028
- F-105 | verified | evidence: interface override returns null; explicit scan APIs/End Portal retained; E-0030
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
- F-085 | checkpoints_started=2 | material_replans=0
- F-087 | checkpoints_started=2 | material_replans=0
- F-088 | checkpoints_started=1 | material_replans=0
- F-105 | checkpoints_started=1 | material_replans=0

## Resource Counters

- Implementation Subagent Waves Used: 0
- Closure Review Passes Used: 0
- Scope Amendments Used: 0
- Adjacent Finding Auto-Promotions Used: 0
- Post-Closure Work Items Used: 0

## Current Diff/Reconciliation

- Git status: user-owned `SKILL.md` remains dirty; R-008 scan/test and STATE/LOG are ready to commit
- Relevant diff summary: built-in phenomenon override is unconditional null; focused test restores handler list and proves later dispatch/no mutation
- Unexpected paths/state: HEAD advanced from `6737687` to `866fea24` during the second interrupted report wave through an unrelated general-agent configuration commit.
- Reconciliation decision: preserve user-updated `SKILL.md`; commit only R-008 scan/test/ledger paths; explicit scanning and End Portal behavior remain untouched.

## Queued Next Checkpoint

- Outcomes/Findings: closure pass after R-008 closes and commits
- Why it is next: R-008 is the final admitted outcome
- Preconditions: R-008 focused evidence/smoke pass and checkpoint commit exists
