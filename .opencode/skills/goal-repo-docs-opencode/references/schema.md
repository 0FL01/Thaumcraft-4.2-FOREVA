# Goal Ledger schema

This schema is normative for the skill. The templates are the canonical concrete form.

## IDs

Use zero-padded stable IDs:

- `S-001`: source
- `A-001`: audit assignment or subagent packet
- `F-001`: atomic finding
- `R-001`: required outcome
- `E-0001`: append-only log event

Never recycle an ID. A duplicate or invalidated item remains in the ledger with its disposition.

## File authority

- `SOURCES.md` defines where claims came from and how to retrieve or verify them.
- `RECON.md` is the immutable normalized fact/findings ledger after freeze.
- `GOAL.md` is the immutable execution contract after freeze.
- `STATE.md` is the mutable execution state and handoff core.
- `LOG.md` is append-only history of material transitions.
- `reports/*.md` are immutable audit packets once accepted into RECON.
- `sources/` contains immutable snapshots/excerpts when a locator alone is not reproducible.

After activation, changing `GOAL.md` or `RECON.md` requires:

1. a material decision in `LOG.md`;
2. version increment;
3. explicit reason and authority;
4. rerun of coverage checks;
5. new hashes stamped into `STATE.md` before implementation continues.

Do not edit old log events. Append a correcting event.

## Source entry

Every source needs:

- Kind
- Authority: `authoritative`, `oracle`, `evidence`, or `advisory`
- Locator
- Version/date/commit when applicable
- SHA-256 or another immutable fingerprint when practical
- Relevant scope

For repository files, use path plus Git commit. For binaries, use path plus SHA-256. For web/CRW evidence, record query or URL, retrieval timestamp, relevant excerpt/snapshot path, and content hash when practical. A URL alone is not immutable evidence.

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
- Regression hazards
- Outcome mapping when required
- Notes/reason for disposition

`Observed`, `Expected`, and `Exact deltas` are mandatory for `defect` and `parity`. “Match upstream” is not an exact expected state. Put concrete values and conditions here even when they are repeated by a test fixture.

A `test_debt` item is not automatically a product requirement. It becomes `required` only when the authoritative source requires tests or the test is minimum evidence for another required finding.

## Outcome

Every `R-*` has:

- `Covers`: one or more `required` `F-*` IDs
- Acceptance
- Primary evidence
- Mandatory broader gates
- Change envelope/budget
- Stop/replan condition

Do not repeat every exact delta in the outcome. Link to findings and keep exact facts in one durable place. This prevents both omission and contradictory copies.

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

Deferred, invalidated, and duplicate findings do not need execution statuses, but their reasons must remain in RECON.
