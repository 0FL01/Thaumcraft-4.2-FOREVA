# RECON Ledger: {{TITLE}}

Goal-ID: {{GOAL_ID}}
Recon-Version: 1
Recon-Status: collecting
Last-Updated: {{DATE}}
Audit-Promotion-Policy: <explicit_only | production_gate | triage>
Budget-Authority: skill-default
Audit-Assignments-Cap: 12
Audit-Waves-Cap: 1
Audit-Waves-Used: 0
Candidate-Reproduction-Cap-Per-Finding: 1
Required-Findings-Cap: 8
Total-Fix-Checkpoints-Cap: 12
Scope-Amendments-Cap: 0

## Audit Charter

- Objective: <bounded observable audit result>
- Target surface: <included files/symbols/behaviors>
- Oracle and comparison direction: <source -> target>
- Anti-scope: <explicit exclusions>
- Platform adaptations to preserve: <known valid deltas or none>
- Required gates: <commands/runtime checks>
- Stop conditions: <coverage complete, blocker, or frozen budget>

## Production Relevance Envelope

- Supported versions/configurations: <actual supported production envelope>
- Supported entry points/data/lifecycle: <real reachable triggers>
- Production invariants: <facts that make paths reachable or unreachable>
- Explicitly excluded/unreachable paths: <unsupported, retired, synthetic-only>
- Concrete impact threshold: <user/contract/security/data/safety/performance threshold>
- Critical-risk exception policy: <authoritative concrete policy or none>

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
- Production Gate: <pass | fail | not_applicable>
- Admission Basis: <allowed exact basis from production-admission.md>
- Production Trigger/Reachability: <supported concrete trigger or why absent>
- Concrete Impact/Contract: <observable harm/contract or why none>
- Admission Evidence: <direct evidence; synthetic-only is insufficient>
- Admission Budget: reproduce_attempts=<0 or 1>; fix_checkpoints=<finite integer>; review_passes=<0 or 1>
- Admission Attempts Used: <0 or 1>
- Speculation Boundary: <one-hop direct cause; adjacent work excluded>
- Regression hazards: <nearby behavior that must not change>
- Outcome: <R-001 or none until freeze>
- Notes/Disposition reason: <why required/deferred/etc.; never blank>

## RECON Adjudication

- Duplicates retained with links and reasons:
- Conflicts and direct adjudication:
- Material unknowns:
- Deferred candidates and failed gate reasons:
- Positive parity controls:

## RECON Closure

- All A-* assignments terminal: no
- Every report claim normalized or explicitly rejected: no
- Exact deltas preserved: no
- Production Admission Gate applied: no
- Non-admitted candidates explicitly deferred: no
- Promotion policy applied: no
- Material conflicts resolved or blocking: no
- Hard budgets frozen: no
- Ready to freeze: no
