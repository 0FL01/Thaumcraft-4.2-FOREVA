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
5. **Confirmed is not actionable.** Confidence answers whether a delta is real; it does not answer whether the delta is production-relevant or worth fixing. A finding becomes required only through the frozen promotion policy and Production Admission Gate.
6. **No speculative prevention.** Do not fix a merely possible failure, unsupported configuration, unreachable branch, synthetic-only reproducer, generic hardening opportunity, or adjacent cleanup unless an authoritative requirement or the current diff makes it in-scope.
7. **Hard resource governor.** RECON fan-out, candidate reproduction, replans, implementation subagents, review passes, and post-closure work have finite frozen caps. The agent cannot raise them.
8. **One-hop causality.** A required fix may address its direct cause and direct regressions. It may not recursively promote bugs discovered in dependencies, neighboring code, generated tests, reviews, or reviews of reviews.
9. **Crash-consistent state.** Record the active checkpoint in `STATE.md` before its first product edit or long-running investigation. Flush state before DCP compression or native Compact.
10. **Rehydrate before acting.** On a new session, after compaction, after returning from a long subagent wave, or whenever memory is uncertain, reload the ledger and reconcile Git state before editing.
11. **Files beat summaries.** If chat, a DCP summary, native Compact, a todo list, or a subagent answer conflicts with the ledger and repository evidence, stop and reconcile; never guess.
12. **Completion is terminal.** Close only after all required findings and preserve controls pass current evidence. Do not roll into cleanup, hardening, or a new audit.

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
- expected evidence and stop conditions;
- supported production envelope and critical-risk exceptions;
- hard resource governor: audit assignments/waves, per-candidate reproduction attempts, per-finding replans, implementation subagent waves, closure reviews, adjacent promotions, and post-closure work.

### Promotion policy

Choose and record exactly one:

- `explicit_only`: only behavior explicitly required by an authoritative source, a regression caused by the current diff, or a direct blocker to such behavior may become `required`.
- `production_gate`: audit candidates may become `required` only after the Production Admission Gate below passes. This is the default for “RECON, then fix real bugs”.
- `triage`: no audit candidate becomes `required` until the user or cited source explicitly selects it after RECON.

`confirmed_in_scope` is forbidden. “Confirmed” means the comparison is trustworthy; it does not prove production reachability, harmful impact, or favorable cost. Phrases such as “fix all confirmed bugs” do not bypass the gate unless the authoritative source explicitly defines exact parity over a finite enumerated surface and a hard resource budget.

When wording is ambiguous and the difference changes scope, use `production_gate` and the narrowest supported production envelope. Keep the contract `draft` only when a missing decision changes the requested observable result.

### Budget authority and safe defaults

Freeze capacity **before** fan-out, not after seeing how many findings exist. When no authoritative source supplies finite numbers, use `Budget-Authority: skill-default` with these ceilings:

- audit assignments: `12`;
- audit waves: `1`;
- candidate reproduction attempts per finding: `1`;
- admitted required findings: `8`;
- total fix checkpoints across all admitted findings: `12`;
- material replans per required finding: `2`;
- implementation subagent waves: `0`;
- closure review passes: `1`;
- scope amendments, adjacent auto-promotions, and post-closure work: `0`.

The agent may lower these limits but cannot raise them under `skill-default`. A higher finite limit requires exactly one authoritative `S-*` source that explicitly grants the numbers before the relevant work. Record the grant in that source using the schema's exact `Budget Grant` syntax; broad wording such as “fix everything” is not a finite grant. If more candidates pass the semantic gate than capacity permits, rank them by authoritative obligation, production incident/criticality, trigger frequency and impact, then smallest bounded cost. Admit only the top candidates that fit both caps; mark the rest `deferred` with `Admission Basis: over_capacity`. Never increase capacity merely because RECON found more work.

### Production Admission Gate

Read `references/production-admission.md` before adjudication. Freeze the supported production envelope before fan-out: versions, configurations, feature flags, real inputs/data/lifecycle, known invariants, explicit exclusions, and the critical-risk exception policy.

A candidate may be `required` only when all of the following hold:

1. It is inside the frozen target surface and outside anti-scope.
2. It has one allowed admission basis: `explicit_requirement`, `production_incident`, `deterministic_supported_path`, `current_diff_regression`, `blocks_explicit_requirement`, `credible_critical_risk`, or `user_override`.
3. Its supported trigger/reachability and concrete user, contract, security, data-integrity, safety, or bounded performance impact are written down.
4. Its admission evidence is direct. A newly invented synthetic test may verify an already admitted requirement but cannot by itself admit a candidate.
5. It has a finite reproduction and fix budget. At most one bounded reproduction attempt is allowed for a non-critical audit candidate; failure to reproduce without deterministic static proof means `deferred`.
6. The smallest fix stays in the frozen envelope. Generic hardening, broad refactoring, future-proofing, observability work, retries, fallbacks, or architecture changes are not implied.
7. The causal chain is one hop: direct cause, required behavior, and regressions caused by the current diff. Adjacent independent findings remain `deferred` or enter a future goal.

A low-frequency issue may still pass as `credible_critical_risk`, but only for concrete security, data-loss, safety, or irreversible-corruption preconditions—not for vague “could happen” reasoning.

## Phase 2 — Run RECON as a durable swarm

Read `references/audit-packet.md` before delegation.

The orchestrator is the single writer for `GOAL.md`, `RECON.md`, `SOURCES.md`, `STATE.md`, and `LOG.md`. During RECON, each subagent may write only its assigned report/evidence path. Record Git status/HEAD before a wave and verify afterward that no product path changed.

For every subagent assignment:

1. Give it one `A-*` ID, a bounded scope, explicit anti-scope, oracle, questions, effort budget, and a unique report path under `reports/`.
2. Require it to write the report packet progressively. Product files are read-only during RECON unless the user explicitly requested a reproducer artifact.
3. Require atomic local findings with observed behavior, expected behavior, exact deltas, affected symbols, evidence, confidence, and regression hazards.
4. Require production-gate inputs for every candidate: supported trigger/reachability, concrete impact, direct admission evidence, and whether the claim is only synthetic or speculative. Subagents recommend; only the orchestrator adjudicates.
5. Require positive parity, benign platform adaptations, unknowns, and test debt—not only bugs.
6. Make the subagent return only report path, terminal status, and a short index. The report, not the task response, is the durable result.
7. If the subagent cannot write files, the orchestrator must persist its complete material result to the assigned report before issuing more work.
8. If context pressure appears, the subagent writes a partial packet and stops. Spawn a continuation against that packet; never rely on a “continue from memory” instruction.

Multiple waves are allowed only for uncovered frozen map cells, conflict resolution, or continuations and only inside the recorded audit-wave cap. No implementation or review subagent may open a new audit map. Do not recursively fan out, review a review, or spawn agents merely because another issue might exist.

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
- production trigger/reachability and concrete impact/contract;
- Production Gate decision, allowed admission basis, direct admission evidence, finite admission budget, and attempts used;
- speculation boundary and one-hop causal relationship;
- regression hazards and neighboring behavior that must remain intact;
- disposition and mapped `R-*` outcome when applicable.

Do not replace exact details with phrases such as “match original behavior”, “fix semantics”, “restore balance”, or “add tests”. Those phrases may label an outcome, but the linked `F-*` entries remain normative.

Deduplicate by linking entries and recording why; never delete a finding because it was merged. Resolve report conflicts with direct evidence or leave a `blocking_question`. Directly validate high-severity claims before freezing when the repository or oracle permits it.

RECON may be frozen only when:

- every audit assignment is terminal;
- every report finding is represented by an `F-*` entry or an explicit rejected/duplicate disposition;
- all material conflicts are resolved or blocking;
- all required exact details are durable;
- the promotion policy and Production Admission Gate have been applied;
- every non-admitted candidate is explicitly deferred/invalidated/duplicate with a reason;
- no scope-changing unknown remains hidden in prose;
- the frozen required-finding count fits the pre-RECON count/checkpoint caps and all finite resource limits are known.

## Phase 4 — Freeze the contract with atomic traceability

Create `R-*` outcomes in `GOAL.md` only after normalized RECON. If zero findings pass admission, create no outcomes, set the goal terminally `complete` after preserve/constraint evidence, and stop. Never promote a candidate merely to avoid an empty implementation plan.

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

Freeze the change envelope: target behavior/artifact, expected paths and symbols, direct consumers, allowed and forbidden artifact categories, platform/API boundaries, validation gates, and hard user/harness budgets. Add the Resource Governor from the template. `No fixed budget`, `as needed`, `until clean`, and equivalent open-ended limits are forbidden. The frozen required-finding count must equal the actual number of `required` findings and stay at or below the pre-RECON cap. The sum of their `fix_checkpoints` must equal the frozen total and stay at or below the pre-RECON total checkpoint cap. Adjacent auto-promotions and post-closure work must be zero.

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
- increment the active findings' `checkpoints_started` counters and keep their `material_replans` counters current;
- record Git branch and HEAD;
- append a `checkpoint_start` event to `LOG.md`.

This is write-ahead state. If Compact occurs one tool call later, a fresh session can still continue safely.

### Execute

1. Read only the active outcome, active findings, directly relevant source entries, and affected repository files.
2. Make the smallest logically related change or experiment.
3. Run the most direct evidence for the active findings once.
4. Continue only when the result exposes a concrete in-scope cause and the next action is materially different.
5. Do not rerun an unchanged failed command or a successful check after unrelated edits.
6. A test, review, tool, or subagent can trigger an edit only when it proves a covered finding remains unresolved, the current diff caused a regression, or a mandatory affected gate fails because of the diff. It cannot create a preventive-hardening requirement.
7. A new discovery is appended to RECON with provenance and disposition. Default it to `deferred`; it may become `required` only through a versioned scope amendment authorized by the user/source and a fresh Production Admission Gate.
8. Spend at most the frozen reproduction attempts, fix checkpoints, and materially different replans. Increment the durable per-finding counters before consuming each unit. When a cap is reached, mark the candidate `deferred` or the required finding `blocked`/`unmet`; do not keep searching because another safe experiment exists.
9. Two consecutive checkpoints that neither close a required finding nor produce concrete evidence that materially changes the next in-scope action are a stop/replan condition. This never authorizes an audit expansion.

Use the minimum direct proof surface that can verify each linked finding; this limits test and audit sprawl. Do not add fuzzing, property suites, broad matrices, generic observability, or defensive branches unless a frozen finding specifically requires them. A reproducer for a deferred candidate stays under `evidence/` and is not promoted into the product test suite by default. Minimum proof does not permit deleting exact discovery details from RECON.

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

Run at most the frozen number of closure passes after the final implementation edit; the default and recommended cap is one. It verifies the frozen contract and current-diff regressions only. It is not another discovery phase, audit wave, hardening pass, or invitation to review the review.

Completion requires:

- every `required` `F-*` is `verified`, `waived_by_user`, `not_applicable`, or `superseded`, with evidence;
- every `R-*` derives to a successful terminal state;
- no finding remains `pending`, `in_progress`, `blocked`, or `regressed`;
- every Preserve Control has current evidence after the final relevant diff;
- affected constraints remain satisfied;
- required targeted, package, runtime, workspace, and build gates pass;
- the diff stays inside the approved envelope;
- `goal_lint.py` passes with current contract, RECON, source, and report hashes;
- required commits exist and the final worktree state is recorded;
- resource counters remain inside the frozen governor;
- no deferred/candidate finding was silently promoted and no post-closure work was started.

Then set `Goal-Status: complete`, clear active IDs, append the completion event, and remove `.opencode/active-goal` or point it to the next explicitly requested objective. Stop substantive work immediately. Do not spend remaining budget on cleanup, hardening, extra tests, speculative bug fixes, or a fresh audit.

Set `blocked` only when no approved in-scope action with a falsifiable expected result remains without external input. Set `unmet` only when the finish line is impossible inside authoritative constraints or the approved envelope. Neither status proves completion.
