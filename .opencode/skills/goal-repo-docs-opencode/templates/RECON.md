# RECON Ledger: {{TITLE}}

Goal-ID: {{GOAL_ID}}
Recon-Version: 1
Recon-Status: collecting
Last-Updated: {{DATE}}
Audit-Promotion-Policy: <explicit_only | confirmed_in_scope | triage>

## Audit Charter

- Objective: <bounded observable audit result>
- Target surface: <included files/symbols/behaviors>
- Oracle and comparison direction: <source -> target>
- Anti-scope: <explicit exclusions>
- Platform adaptations to preserve: <known valid deltas or none>
- Required gates: <commands/runtime checks>
- Stop conditions: <coverage complete, blocker, budget>

## Audit Coverage

- A-001 | status: planned | report: reports/A-001-<slug>.md | scope: <non-overlapping shard>

Terminal assignment statuses: `complete`, `no_findings`, `blocked`, `superseded`, or `continued_as A-###`.

## Finding Ledger

## F-001 — <atomic title>

- Type: defect
- Disposition: <required | preserve | constraint | deferred | invalidated | duplicate | blocking_question>
- Severity: <P0 | P1 | P2 | informational>
- Confidence: <confirmed | high | medium | low>
- Source IDs: S-001
- Audit IDs/Reports: A-001 / reports/A-001-<slug>.md
- Oracle: <exact version/path/symbol/behavior>
- Observed: <exact current state>
- Expected: <exact required or preserved state>
- Exact deltas: <numbers, branches, conditions, metadata, lifecycle, protocol>
- Affected paths/symbols: <paths and symbols>
- Primary evidence: <command, artifact, runtime observation, fixture>
- Regression hazards: <nearby behavior that must not change>
- Outcome: <R-001 or none until freeze>
- Notes/Disposition reason:

## RECON Adjudication

- Duplicates retained with links and reasons:
- Conflicts and direct adjudication:
- Material unknowns:
- Rejected candidates:
- Positive parity controls:

## RECON Closure

- All A-* assignments terminal: no
- Every report claim normalized or explicitly rejected: no
- Exact deltas preserved: no
- Promotion policy applied: no
- Material conflicts resolved or blocking: no
- Ready to freeze: no
