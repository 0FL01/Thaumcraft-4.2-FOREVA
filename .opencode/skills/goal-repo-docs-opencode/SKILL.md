---
name: goal-repo-docs
description: Create, freeze, execute, recover, and close a repo-local Goal Ledger for one durable objective. Use for RECON or audit followed by multi-stage implementation, especially with subagents, OpenCode DCP, native Compact, or work that must survive session loss. The ledger preserves atomic findings, exact deltas, provenance, positive-parity controls, active state, evidence, and the next checkpoint. Do not use for one-turn work, routine reviews, open-ended improvement, or unrelated backlog aggregation.
metadata:
  opencode/slash: "true"
---

# Goal Ledger for OpenCode

Use a repo-local write-ahead ledger so the objective survives DCP pruning, native Compact, process restarts, subagent loss, and handoff to a fresh session.

The chat is volatile working memory. The Goal Ledger is durable execution memory. A compaction summary is only a pointer back to the ledger, never the source of truth.

## Non-negotiable invariants

1. **Write before forgetting.** Material knowledge must reach a durable file before more fan-out, a long investigation, implementation, or compression can hide it.
2. **No floating knowledge.** Every requirement, finding, preserved behavior, constraint, unknown, decision, and completion claim has a stable ID and provenance.
3. **No lossy aggregation.** Broad outcomes may group work, but completion is decided at atomic `F-*` finding level. Exact numbers, branches, conditions, symbols, paths, commands, and regression hazards stay in `RECON.md`.
4. **Preserve correct behavior.** Positive parity and intentionally retained platform adaptations are first-class `preserve` findings, not prose that may disappear.
5. **Evidence does not silently create scope.** Audit findings become implementation requirements only through the explicit promotion policy frozen before implementation.
6. **Crash-consistent state.** Record the active checkpoint in `STATE.md` before its first product edit or long-running investigation. Flush state before DCP compression or native Compact.
7. **Rehydrate before acting.** On a new session, after compaction, after returning from a long subagent wave, or whenever memory is uncertain, reload the ledger and reconcile Git state before editing.
8. **Files beat summaries.** If chat, a DCP summary, native Compact, a todo list, or a subagent answer conflicts with the ledger and repository evidence, stop and reconcile; never guess.
9. **Completion is terminal.** Close only after all required findings and preserve controls pass current evidence. Do not roll into cleanup, hardening, or a new audit.

Authority order: latest explicit user instruction; user-cited source specification; applicable repository instructions and existing contracts; frozen Goal Ledger. A lower authority cannot rewrite a higher one.

## Fit gate

Use this skill when there is one durable objective with an observable finish line and the work is multi-stage, resumable, audit-derived, or likely to span context windows.

Do not create a ledger for a one-turn edit, explanation, small isolated fix, routine review, or an objective phrased only as “improve”, “clean up”, “make robust”, “productionize”, or “refactor”. Split independently shippable objectives instead of absorbing a backlog.

## Required artifact set

Reuse a repository convention when it provides equivalent durability and traceability. Otherwise create:

```text
docs/goals/<YYYY-MM-DD>-<slug>/
├── GOAL.md        # frozen contract; changed only by versioned scope amendment
├── RECON.md       # normalized, atomic, lossless-enough finding ledger
├── SOURCES.md     # source registry and immutable locators/fingerprints
├── STATE.md       # small mutable write-ahead handoff state
├── LOG.md         # append-only material events, evidence, decisions, commits
├── reports/       # one immutable report packet per audit/subagent assignment
├── sources/       # immutable external/source snapshots when needed
└── evidence/      # small implementation/verification artifacts when needed
.opencode/active-goal  # one relative path to the active goal directory
```

Never store secrets, credentials, private dumps, generated databases, or indiscriminate logs. Store exact relevant excerpts, commands, locators, hashes, and artifacts instead of giant transcripts.

Read `references/schema.md` before creating or changing the ledger. Use the files under `templates/`; do not invent a weaker schema.

## Phase 0 — Re-entry and duplicate gate

Before planning or editing:

1. Look for `.opencode/active-goal` and the repository's normal goal locations.
2. If a matching `draft`, `active`, `blocked`, or `recovery` ledger exists, resume it instead of creating another.
3. Run the rehydration protocol in `references/compaction-protocol.md`.
4. Read the user-named source first, then only directly applicable repository instructions, target files, tests, and required gates.
5. If no ledger exists, scaffold one with `scripts/goal_init.py` or the templates.

Do not trust a prior chat statement that RECON is complete unless its reports and normalized findings exist in the goal directory.

## Phase 1 — Register authority and freeze the audit charter

Before mass fan-out, write `SOURCES.md` and the draft charter in `RECON.md`:

- objective and observable finish line;
- audit target, oracle, comparison direction, and anti-scope;
- repository/platform constraints;
- audit map with non-overlapping assignment IDs `A-*`;
- promotion policy;
- expected evidence and stop conditions.

### Promotion policy

Choose and record exactly one:

- `explicit_only`: only requirements already explicit in authoritative sources may become `required`; audit findings remain evidence, risks, or candidates.
- `confirmed_in_scope`: the user explicitly delegated “audit this bounded surface, then implement all confirmed findings”; every confirmed finding inside the charter is promoted unless explicitly disposed otherwise.
- `triage`: confirmed findings require an adjudication decision before promotion.

Never infer `confirmed_in_scope` merely because an audit was requested. When wording is ambiguous and the difference changes scope, keep the contract `draft` and record the smallest blocking decision. When it does not change behavior or boundary, use the narrowest compatible interpretation and record it.

## Phase 2 — Run RECON as a durable swarm

Read `references/audit-packet.md` before delegation.

The orchestrator is the single writer for `GOAL.md`, `RECON.md`, `SOURCES.md`, `STATE.md`, and `LOG.md`. During RECON, each subagent may write only its assigned report/evidence path. Record Git status/HEAD before a wave and verify afterward that no product path changed.

For every subagent assignment:

1. Give it one `A-*` ID, a bounded scope, explicit anti-scope, oracle, questions, effort budget, and a unique report path under `reports/`.
2. Require it to write the report packet progressively. Product files are read-only during RECON unless the user explicitly requested a reproducer artifact.
3. Require atomic local findings with observed behavior, expected behavior, exact deltas, affected symbols, evidence, confidence, and regression hazards.
4. Require positive parity, benign platform adaptations, unknowns, and test debt—not only bugs.
5. Make the subagent return only report path, terminal status, and a short index. The report, not the task response, is the durable result.
6. If the subagent cannot write files, the orchestrator must persist its complete material result to the assigned report before issuing more work.
7. If context pressure appears, the subagent writes a partial packet and stops. Spawn a continuation against that packet; never rely on a “continue from memory” instruction.

Multiple waves are allowed for uncovered map cells, conflict resolution, or continuations. Do not recursively fan out without explicit coverage IDs and budgets.

Before synthesis, prove every `A-*` assignment is terminal: `complete`, `no_findings`, `blocked`, `superseded`, or `continued_as A-*`. Missing reports are unresolved audit coverage.

## Phase 3 — Normalize RECON without destroying detail

The orchestrator reads every report and creates stable `F-*` entries in `RECON.md`.

Allowed finding types:

- `defect`
- `parity`
- `constraint`
- `unknown`
- `test_debt`
- `benign_delta`

Allowed dispositions:

- `required`
- `preserve`
- `constraint`
- `deferred`
- `invalidated`
- `duplicate`
- `blocking_question`

For each material finding preserve:

- source IDs and audit report IDs;
- oracle and comparison direction;
- exact observed state;
- exact expected state;
- exact numeric, branch, condition, metadata, protocol, or lifecycle deltas;
- affected paths and symbols;
- direct evidence and reproduction command/artifact;
- confidence and unresolved uncertainty;
- regression hazards and neighboring behavior that must remain intact;
- disposition and mapped `R-*` outcome when applicable.

Do not replace exact details with phrases such as “match original behavior”, “fix semantics”, “restore balance”, or “add tests”. Those phrases may label an outcome, but the linked `F-*` entries remain normative.

Deduplicate by linking entries and recording why; never delete a finding because it was merged. Resolve report conflicts with direct evidence or leave a `blocking_question`. Directly validate high-severity claims before freezing when the repository or oracle permits it.

RECON may be frozen only when:

- every audit assignment is terminal;
- every report finding is represented by an `F-*` entry or an explicit rejected/duplicate disposition;
- all material conflicts are resolved or blocking;
- all required exact details are durable;
- the promotion policy has been applied;
- no scope-changing unknown remains hidden in prose.

## Phase 4 — Freeze the contract with atomic traceability

Create `R-*` outcomes in `GOAL.md` only after normalized RECON.

Each `R-*` must include:

- `Covers: F-*` with one or more `required` finding IDs;
- one observable acceptance statement;
- primary evidence and required broader gates;
- smallest allowed change envelope or budget;
- stop/replan conditions.

Coverage invariants:

1. Every `required` finding is covered by exactly one `R-*`.
2. An `R-*` cannot be verified while any covered finding is unresolved.
3. Every `preserve` finding appears under Preserve Controls.
4. Every `constraint` finding appears under Constraints.
5. Every `blocking_question` prevents an `active` contract when it changes outcome or boundary.
6. Findings marked `deferred`, `invalidated`, or `duplicate` remain visible with reasons.
7. Source-derived acceptance details point to `F-*` and `S-*`; “completed RECON” is not a valid source locator.

Freeze the change envelope: target behavior/artifact, expected paths and symbols, direct consumers, allowed and forbidden artifact categories, platform/API boundaries, validation gates, and user/harness budgets.

Run:

```bash
python3 <skill-dir>/scripts/goal_lint.py <goal-dir> --stamp
python3 <skill-dir>/scripts/goal_lint.py <goal-dir>
```

Do not start product implementation while the linter reports an error. Commit the frozen ledger first only when the user, repository, or source contract requires commits.

## Phase 5 — Execute with write-ahead checkpoints

Work on one `R-*` or a tightly coupled subset of its `F-*` entries.

### Start a checkpoint

Before the first product edit or long investigation, update `STATE.md`:

- increment `State-Revision`;
- set active `R-*` and `F-*` IDs;
- set checkpoint status to `in_progress`;
- record the current hypothesis;
- record the smallest next action;
- record falsifiable expected evidence;
- record stop/replan condition;
- record working-set paths and expected dirty paths;
- record Git branch and HEAD;
- append a `checkpoint_start` event to `LOG.md`.

This is write-ahead state. If Compact occurs one tool call later, a fresh session can still continue safely.

### Execute

1. Read only the active outcome, active findings, directly relevant source entries, and affected repository files.
2. Make the smallest logically related change or experiment.
3. Run the most direct evidence for the active findings once.
4. Continue only when the result exposes a concrete in-scope cause and the next action is materially different.
5. Do not rerun an unchanged failed command or a successful check after unrelated edits.
6. A test, review, tool, or subagent can trigger an edit only when it proves a covered finding remains unresolved, the current diff caused a regression, or a mandatory affected gate fails because of the diff.
7. A new discovery is appended to RECON with provenance and disposition. It does not silently enter the current outcome.
8. Two consecutive checkpoints that neither close a required finding nor produce concrete evidence that materially changes the next in-scope action are a stop/replan condition.

Use the minimum direct proof surface that can verify each linked finding; this limits test and audit sprawl. Minimum proof does not permit deleting exact discovery details from RECON.

Validation order: existing targeted check; focused new regression check when required; affected package gate; minimal runtime/manual observation; broader workspace gate only when the contract or changed dependency surface requires it.

### Flush a material transition

Update `STATE.md` and append `LOG.md` after any of these:

- checkpoint evidence completes;
- the active hypothesis materially changes;
- the next action changes class;
- a blocker or scope amendment appears;
- a commit is about to be made;
- DCP compression or native Compact is about to run.

Do not log every read, grep, edit, or command. Preserve causal state, not telemetry noise.

### Close a checkpoint

For each active `F-*`, record terminal or next status and evidence. Derive the `R-*` status from all covered findings. Record command, concise result, affected paths, Git commit when any, and next checkpoint. Set checkpoint status `closed` before selecting new work.

Use small reversible commits when commits are required. Do not combine independent outcomes merely to reduce commit count.

## Phase 6 — DCP and native Compact protocol

Read `references/compaction-protocol.md` and install the optional OpenCode integration under `integrations/opencode/`.

Rules:

- DCP is an optimization, not storage.
- Compress only closed, superseded, or durably flushed epochs.
- Never compress the only copy of an active hypothesis, exact finding, failed attempt, command result, or subagent result.
- Keep the `<protect>...</protect>` rehydration core in `STATE.md` short and current.
- Before deliberate DCP compression, flush `STATE.md` and `LOG.md`, then focus compression on closed epochs.
- Native Compact must receive the active goal pointer, state revision, frozen hashes, active IDs, next action, expected evidence, stop condition, Git anchor, and blockers through the compaction hook.
- The first action after any compaction is rehydration from files, not implementation.

If compaction happens unexpectedly, do not trust apparent continuity. Run `scripts/goal_context.py`, inspect Git state, and reconcile before edits.

## Phase 7 — Recovery

Enter `Goal-Status: recovery` when `STATE.md` is missing, stale, hash-invalid, inconsistent with Git, or contradicted by a summary.

Recovery order:

1. Read `.opencode/active-goal`, `GOAL.md`, `RECON.md`, `SOURCES.md`, and the tail of `LOG.md`.
2. Verify contract, RECON, source-bundle, and report-bundle hashes and versions.
3. Inspect `git status`, branch, HEAD, and diff.
4. Identify the last durable checkpoint and evidence.
5. Reconstruct only what repository state and durable artifacts prove.
6. Mark uncertain work unresolved; never infer completion.
7. Write a `recovery` event, restore a valid `STATE.md`, run the linter, then resume.

## Phase 8 — Closure

Run one closure pass after the final implementation edit. It verifies the frozen contract; it is not another discovery phase.

Completion requires:

- every `required` `F-*` is `verified`, `waived_by_user`, `not_applicable`, or `superseded`, with evidence;
- every `R-*` derives to a successful terminal state;
- no finding remains `pending`, `in_progress`, `blocked`, or `regressed`;
- every Preserve Control has current evidence after the final relevant diff;
- affected constraints remain satisfied;
- required targeted, package, runtime, workspace, and build gates pass;
- the diff stays inside the approved envelope;
- `goal_lint.py` passes with current contract, RECON, source, and report hashes;
- required commits exist and the final worktree state is recorded.

Then set `Goal-Status: complete`, clear active IDs, append the completion event, and remove `.opencode/active-goal` or point it to the next explicitly requested objective. Stop substantive work.

Set `blocked` only when no approved in-scope action with a falsifiable expected result remains without external input. Set `unmet` only when the finish line is impossible inside authoritative constraints or the approved envelope. Neither status proves completion.
