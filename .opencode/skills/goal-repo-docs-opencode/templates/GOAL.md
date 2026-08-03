# Goal: {{TITLE}}

Goal-ID: {{GOAL_ID}}
Contract-Version: 1
Contract-Status: draft
Source-Registry: SOURCES.md
Recon-Ledger: RECON.md
Last-Updated: {{DATE}}

## Objective

<One observable end state.>

## Execution Directive

Complete only the frozen outcomes through their linked admitted findings and approved change envelope. Work on the smallest unresolved finding set. A confirmed delta is not automatically actionable. Do not add requirements from reviews, tests, tools, speculative risks, optional source text, adjacent discoveries, cleanup, hardening, or future-proofing. Preserve every listed parity control. Finish when all required findings and affected constraints have current evidence and the closure check passes; then stop.

## Scope Promotion

- Policy: <explicit_only | production_gate | triage>
- Authority: <S-* entries that authorize the policy>
- Production envelope: <RECON production-envelope section>
- Freeze decision: <which exact findings passed admission and why>

## Required Outcomes

<If zero findings pass admission, write `none — close terminally`; otherwise use R-* blocks below.>

## R-001 — <observable outcome>

- Covers: F-001
- Acceptance: <observable end state; exact details live in linked F-* entries>
- Primary evidence: <smallest direct command/artifact/runtime observation>
- Mandatory broader gates: <none or exact gate>
- Change envelope/budget: <finite surface and numeric checkpoint cap; Resource Governor applies>
- Stop/Replan if: <falsifiable boundary, cap, or blocker>

## Preserve Controls

- <F-ID> — <correct behavior or valid adaptation that must remain true>

## Constraints

- <F-ID> — <affected authoritative constraint>

## Non-goals

- <explicit boundary; not an audit checklist>
- Deferred RECON candidates, unsupported paths, synthetic-only failures, generic hardening, cleanup, and adjacent bugs are not implementation work.

## Change Envelope

- Target behavior/artifact:
- Expected paths, symbols, and direct consumers:
- Allowed artifacts:
- Forbidden artifacts:
- API/platform/dependency boundaries:
- User or harness budget: <finite; refer to Resource Governor; no “as needed” or “until clean”>

## Resource Governor

- Max Required Findings: 8
- Frozen Required Finding Count: <exact integer equal to required F-* count>
- Max Total Fix Checkpoints: 12
- Frozen Total Fix Checkpoints: <exact sum of required F-* fix_checkpoints>
- Max Candidate Reproduction Attempts Per Finding: 1
- Max Material Replans Per Required Finding: 2
- Max Implementation Subagent Waves: 0
- Max Closure Review Passes: 1
- Max Scope Amendments: 0
- Adjacent Finding Auto-Promotions: 0
- Post-Closure Work Items: 0
- Budget Authority: skill-default

## Validation Ladder

- Targeted evidence:
- Affected package gate:
- Runtime/manual evidence:
- Workspace/CI/build gate:
- Final preserve-control check:

## Commit Policy

- Required: <yes/no and authority>
- Granularity:
- Message convention:

## Closure Contract

- Every required F-* has a successful terminal state with evidence.
- Every R-* derives to a successful terminal state.
- Preserve controls are current after the final relevant diff.
- Constraints and change envelope remain satisfied.
- Resource counters remain within the frozen governor.
- Required validation and commits pass.
- Goal lint and hashes pass.
- Deferred findings do not block completion and are not silently promoted.
- Completion is terminal; no cleanup, hardening, extra review, or post-closure bug fixing.
