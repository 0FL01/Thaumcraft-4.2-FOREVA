# Goal Ledger schema

This schema is normative for the skill. The templates are the canonical concrete form. Read `production-admission.md` before promoting any RECON candidate.

## IDs

Use zero-padded stable IDs:

- `S-001`: source
- `A-001`: audit assignment or subagent packet
- `F-001`: atomic finding
- `R-001`: required outcome
- `E-0001`: append-only log event

Never recycle an ID. A duplicate, deferred, or invalidated item remains in the ledger with its disposition.

## File authority

- `SOURCES.md` defines where claims came from and how to retrieve or verify them.
- `RECON.md` is the immutable normalized fact/findings ledger after freeze.
- `GOAL.md` is the immutable execution contract after freeze.
- `STATE.md` is the mutable execution state, resource counters, and handoff core.
- `LOG.md` is append-only history of material transitions.
- `reports/*.md` are immutable audit packets once accepted into RECON.
- `sources/` contains immutable snapshots/excerpts when a locator alone is not reproducible.
- `evidence/` may contain bounded reproducer artifacts; an artifact does not create a requirement.

After activation, changing `GOAL.md` or `RECON.md` requires:

1. a material decision in `LOG.md`;
2. version increment;
3. explicit user/source authority and reason;
4. a fresh Production Admission Gate for any newly required finding;
5. rerun of coverage and resource checks;
6. new hashes stamped into `STATE.md` before implementation continues.

Do not edit old log events. Append a correcting event.

## Promotion policy

Allowed values:

- `explicit_only`
- `production_gate`
- `triage`

`confirmed_in_scope` is invalid. Confidence and actionability are separate dimensions.

RECON freezes `Budget-Authority`, `Audit-Assignments-Cap`, `Audit-Waves-Cap`, `Candidate-Reproduction-Cap-Per-Finding`, `Required-Findings-Cap`, `Total-Fix-Checkpoints-Cap`, and `Scope-Amendments-Cap` before fan-out. `skill-default` means `12/1/1/8/12/0`; higher values require an authoritative `S-*` source.

Under `production_gate`, a candidate is not required until the production gate passes. Under `triage`, a candidate is not required until selected by the user/source. Under `explicit_only`, only explicit source behavior, current-diff regressions, and direct blockers to explicit behavior can be required.

## Source entry

Every source needs:

- Kind
- Authority: `authoritative`, `oracle`, `evidence`, or `advisory`
- Locator
- Version/date/commit when applicable
- SHA-256 or another immutable fingerprint when practical
- Relevant scope
- Budget Grant: `none` or an exact finite grant when this source authorizes higher capacity

For repository files, use path plus Git commit. For binaries, use path plus SHA-256. For web/CRW evidence, record query or URL, retrieval timestamp, relevant excerpt/snapshot path, and content hash when practical. A URL alone is not immutable evidence.

A higher-than-default budget source uses exact syntax:

```text
audit_assignments=N; audit_waves=N; required_findings=N; total_fix_checkpoints=N; scope_amendments=N; implementation_subagent_waves=N
```

The numbers must be explicitly supported by the durable source excerpt. Otherwise use `Budget Grant: none` and `Budget-Authority: skill-default`.

## Audit limits and production envelope

Before fan-out, `RECON.md` records:

- `Budget-Authority`: `skill-default` or an authoritative `S-*` source;
- `Audit-Assignments-Cap`;
- `Audit-Waves-Cap`;
- `Audit-Waves-Used`;
- `Candidate-Reproduction-Cap-Per-Finding`;
- `Required-Findings-Cap`;
- `Total-Fix-Checkpoints-Cap`;
- `Scope-Amendments-Cap`.

Under `skill-default`, the ceilings are `12` assignments, `1` wave, `1` candidate attempt, `8` admitted findings, `12` total fix checkpoints, and `0` scope amendments. The candidate reproduction cap must always be `0` or `1`. Higher finite capacity requires an authoritative source recorded before the extra work. RECON also records the actual supported production versions/configurations, entry points/data/lifecycle, invariants, excluded paths, concrete impact threshold, and critical-risk exception policy.

“No fixed budget”, “as needed”, “until clean”, “exhaustive”, and equivalent open-ended limits are forbidden.

## Atomic finding

Every `F-*` block has:

- Type: `defect`, `parity`, `constraint`, `unknown`, `test_debt`, or `benign_delta`
- Disposition: `required`, `preserve`, `constraint`, `deferred`, `invalidated`, `duplicate`, or `blocking_question`
- Severity
- Confidence
- Source IDs
- Audit IDs/report paths
- Oracle
- Observed
- Expected
- Exact deltas
- Affected paths/symbols
- Primary evidence
- Production Gate: `pass`, `fail`, or `not_applicable`
- Admission Basis
- Production Trigger/Reachability
- Concrete Impact/Contract
- Admission Evidence
- Admission Budget
- Admission Attempts Used
- Speculation Boundary
- Regression hazards
- Outcome mapping when required
- Notes/reason for disposition

`Observed`, `Expected`, and `Exact deltas` are mandatory for `defect` and `parity`. “Match upstream” is not an exact expected state. Put concrete values and conditions here even when they are repeated by a test fixture.

`Admission Budget` has exact syntax:

```text
reproduce_attempts=<0 or 1>; fix_checkpoints=<finite non-negative integer>; review_passes=<0 or 1>
```

`Admission Attempts Used` is a finite integer `0` or `1`, cannot exceed `reproduce_attempts`, and is frozen with RECON. `not_reproduced_within_budget` requires that the available attempt was actually consumed.

A required finding must have `Production Gate: pass` and one allowed basis:

- `explicit_requirement`
- `production_incident`
- `deterministic_supported_path`
- `current_diff_regression`
- `blocks_explicit_requirement`
- `credible_critical_risk`
- `user_override`

A preserve or constraint finding uses `not_applicable` with basis `preserve_control` or `constraint`. A non-admitted candidate uses `fail` and one concrete failed-gate basis such as `not_reproduced_within_budget`, `unsupported_or_unreachable`, `no_concrete_impact`, `speculative_hardening`, `over_budget`, `over_capacity`, `outside_scope`, `adjacent_non_requirement`, `synthetic_only`, `triage_not_selected`, `insufficient_authority`, `duplicate`, or `invalidated`.

A `test_debt` item is not automatically a product requirement. It becomes `required` only when an authoritative source explicitly requires that test artifact. When an existing test merely blocks verification of another admitted finding, the minimum assertion edit is charged to that finding's evidence/change budget rather than receiving another product outcome. A synthetic test cannot be the sole admission evidence.

## Outcome

When the admitted required-finding count is zero, `GOAL.md` has no `R-*` outcomes. After any preserve/constraint checks, the goal becomes terminally `complete`; an agent must not promote a candidate to manufacture work.

Every existing `R-*` has:

- `Covers`: one or more admitted `required` `F-*` IDs
- Acceptance
- Primary evidence
- Mandatory broader gates
- Change envelope/budget with a finite numeric checkpoint cap or explicit Resource Governor reference
- Stop/replan condition

Do not repeat every exact delta in the outcome. Link to findings and keep exact facts in one durable place. This prevents both omission and contradictory copies.

## Resource Governor

Every frozen `GOAL.md` records:

- `Max Required Findings`;
- `Frozen Required Finding Count`;
- `Max Total Fix Checkpoints`;
- `Frozen Total Fix Checkpoints`;
- `Max Candidate Reproduction Attempts Per Finding`;
- `Max Material Replans Per Required Finding`;
- `Max Implementation Subagent Waves`;
- `Max Closure Review Passes`;
- `Max Scope Amendments`;
- `Adjacent Finding Auto-Promotions`;
- `Post-Closure Work Items`;
- `Budget Authority`.

The maxima must match the pre-RECON caps. The frozen count must equal the actual required-finding count and not exceed `Max Required Findings`. The frozen total must equal the sum of required findings' `fix_checkpoints` and not exceed `Max Total Fix Checkpoints`. Candidate reproduction attempts and closure review passes may not exceed `1`; material replans may not exceed `2`; adjacent auto-promotions and post-closure work must be `0`. Under `skill-default`, implementation subagent waves and scope amendments are also `0`. All values are finite. Only an authoritative source may authorize a higher versioned capacity.

`STATE.md` tracks one work-counter row for every required finding:

```text
- F-001 | checkpoints_started=<N> | material_replans=<N>
```

`checkpoints_started` may not exceed that finding's `fix_checkpoints`; `material_replans` may not exceed the governor. It also tracks:

- Implementation Subagent Waves Used
- Closure Review Passes Used
- Scope Amendments Used
- Adjacent Finding Auto-Promotions Used
- Post-Closure Work Items Used

Used values may not exceed the frozen governor. Auto-promotions and post-closure work remain zero.

## Runtime state

`STATE.md` must stay short enough to reload on every re-entry. It contains:

- goal and checkpoint status
- state revision and timestamps
- frozen contract, RECON, source-bundle, and report-bundle versions/hashes
- Git branch/HEAD and expected dirty paths
- active outcome/finding IDs
- hypothesis, next action, expected evidence, stop condition
- working set and last material command/result
- blockers
- outcome and finding status maps
- resource counters

The `<protect>` block is intentionally concise. It is the minimum continuation capsule, not a replacement for GOAL/RECON.

## Status vocabulary

Goal status:

- `draft`
- `active`
- `blocked`
- `recovery`
- `complete`
- `unmet`

Checkpoint status:

- `planned`
- `in_progress`
- `verifying`
- `committing`
- `closed`

Required finding and outcome status:

- `pending`
- `in_progress`
- `verified`
- `blocked`
- `waived_by_user`
- `not_applicable`
- `superseded`

Preserve finding status:

- `pending`
- `verified`
- `regressed`
- `not_applicable`
- `superseded`

Constraint finding status:

- `pending`
- `satisfied`
- `violated`
- `not_applicable`
- `superseded`

Deferred, invalidated, and duplicate findings do not need execution statuses, but their reasons and failed admission gate remain in RECON.
