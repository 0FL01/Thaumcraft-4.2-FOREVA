# OpenCode DCP and Compact continuity protocol

The objective is not to make summaries lossless. The objective is to make loss harmless.

## Durable layers

- Raw audit packets preserve delegated evidence.
- RECON preserves normalized atomic facts.
- GOAL preserves the frozen finish line and traceability.
- STATE preserves the current execution edge.
- LOG preserves causal history and decisions.
- Git preserves implementation state.

DCP and Compact may forget conversational detail because no necessary fact exists only in conversation.

## Rehydration gate

Run this gate:

- at session start;
- after `/compact`, automatic Compact, or DCP compression;
- after a long subagent wave;
- after changing agent/model;
- when the current hypothesis or next action is not certain;
- when Git state differs from remembered state.

Steps:

1. Read `.opencode/active-goal`.
2. Read `STATE.md` completely.
3. Verify contract, RECON, source-bundle, and report-bundle hashes and versions.
4. Read active `R-*` sections from `GOAL.md`.
5. Read active `F-*` sections and referenced `S-*` entries.
6. Read the last relevant `LOG.md` events.
7. Run `git status --short`, branch, `git rev-parse HEAD`, and inspect relevant diff.
8. Reconcile discrepancies before edits.
9. Run `goal_lint.py`.

Use `scripts/goal_context.py` to produce a compact reload capsule, but still inspect Git directly.

## Pre-compression flush

Before deliberate DCP compression or native Compact:

1. finish or pause the current tool operation;
2. write exact new findings to a report or RECON;
3. update active hypothesis, next action, expected evidence, stop condition, working set, Git anchor, and blockers in STATE;
4. append any material decision/evidence to LOG;
5. run the linter when GOAL/RECON changed;
6. only then compress.

Never compress an unpersisted subagent answer or the only copy of a failed experiment.

## DCP use

DCP should remove closed epochs and redundant tool output, not active semantic state.

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
- state revision and full rehydration core;
- active outcome sections;
- active finding sections;
- tail of material log;
- explicit instruction that files, not the generated summary, are authoritative.

It augments the default prompt instead of replacing it, reducing coupling to OpenCode prompt changes.

The summary must preserve exact path, revision, active IDs, next action, expected evidence, stop condition, Git anchor, and blockers. It must never infer verified or complete status.

## Unexpected compaction

After unexpected Compact:

```bash
python3 <skill-dir>/scripts/goal_context.py --root .
git status --short
git branch --show-current
git rev-parse HEAD
python3 <skill-dir>/scripts/goal_lint.py "$(cat .opencode/active-goal)"
```

If any result conflicts with the summary, enter recovery and use durable evidence.
