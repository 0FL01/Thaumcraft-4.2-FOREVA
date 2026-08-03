# OpenCode DCP and Compact continuity protocol

The objective is not to make summaries lossless. The objective is to make loss harmless without losing the scope firewall.

## Durable layers

- Raw audit packets preserve delegated evidence.
- RECON preserves normalized atomic facts, failed admission gates, and production reachability.
- GOAL preserves the frozen finish line, Resource Governor, and traceability.
- STATE preserves the current execution edge and resource counters.
- LOG preserves causal history, scope decisions, budget consumption, and evidence.
- Git preserves implementation state.

DCP and Compact may forget conversational detail because no necessary fact exists only in conversation. They must not erase the distinction between `confirmed` and `required`.

## Rehydration gate

Run this gate:

- at session start;
- after `/compact`, automatic Compact, or DCP compression;
- after a long subagent wave;
- after changing agent/model;
- when the current hypothesis, next action, or remaining budget is not certain;
- when Git state differs from remembered state.

Steps:

1. Read `.opencode/active-goal`.
2. Read `STATE.md` completely, including Resource Counters.
3. Verify contract, RECON, source-bundle, and report-bundle hashes and versions.
4. Read Scope Promotion and Resource Governor from `GOAL.md`.
5. Read Production Relevance Envelope from `RECON.md`.
6. Read active `R-*` sections from `GOAL.md`.
7. Read active `F-*` sections and referenced `S-*` entries.
8. Read the last relevant `LOG.md` events.
9. Run `git status --short`, branch, `git rev-parse HEAD`, and inspect relevant diff.
10. Reconcile discrepancies and run `goal_lint.py` before edits.

Use `scripts/goal_context.py` to produce a compact reload capsule, but still inspect Git directly.

At re-entry, repeat these rules explicitly:

- confirmed findings are not automatic requirements;
- deferred findings are not implementation work;
- adjacent discoveries default to deferred;
- no review-of-review;
- budget caps survive compaction and cannot be raised by the agent;
- completion is terminal.

## Pre-compression flush

Before deliberate DCP compression or native Compact:

1. finish or pause the current tool operation;
2. write exact new findings to a report or RECON;
3. record Production Gate and disposition for any new candidate;
4. update active hypothesis, next action, expected evidence, stop condition, working set, Git anchor, blockers, and Resource Counters in STATE;
5. append any material decision/evidence/budget use to LOG;
6. run the linter when GOAL/RECON changed;
7. only then compress.

Never compress an unpersisted subagent answer, the only copy of a failed experiment, or an unrecorded budget decision.

## DCP use

DCP should remove closed epochs and redundant tool output, not active semantic state or the scope firewall.

Recommended behavior:

- protect goal files and reports by path;
- enable `<protect>` preservation;
- keep task/skill/todo outputs protected;
- allow subagent processing only when every shard writes progressive report packets;
- ask compression to focus on closed checkpoints and superseded exploration;
- do not immortalize all user messages when large pasted sources have already been registered durably.

DCP settings are tuning, not correctness. The ledger remains correct when DCP is disabled or replaced.

## Native Compact hook

Install `integrations/opencode/goal-compaction.ts` as a project or global plugin. It injects:

- active goal path;
- state revision, rehydration core, and resource counters;
- Scope Promotion, Production Relevance Envelope, and Resource Governor;
- active outcome sections;
- active finding sections, including admission basis/budget;
- tail of material log;
- explicit instruction that files, not the generated summary, are authoritative.

It augments the default prompt instead of replacing it, reducing coupling to OpenCode prompt changes.

The summary must preserve exact path, revision, active IDs, next action, expected evidence, stop condition, Git anchor, blockers, promotion policy, production gate, and remaining resource caps. It must never infer verified/complete status or promote a finding.

## Unexpected compaction

After unexpected Compact:

```bash
python3 <skill-dir>/scripts/goal_context.py --root .
git status --short
git branch --show-current
git rev-parse HEAD
python3 <skill-dir>/scripts/goal_lint.py "$(cat .opencode/active-goal)"
```

If any result conflicts with the summary, enter recovery and use durable evidence. If the summary suggests more work than the frozen findings/budget allow, ignore it and follow the ledger.
