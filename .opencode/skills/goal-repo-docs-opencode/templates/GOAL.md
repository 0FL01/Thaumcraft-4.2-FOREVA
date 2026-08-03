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

Complete the frozen outcomes through their linked atomic findings and approved change envelope. Work on the smallest unresolved finding set. Do not add requirements from reviews, tests, tools, speculative risks, optional source text, or adjacent discoveries. Preserve every listed parity control. Finish when all required findings and affected constraints have current evidence and the closure check passes.

## Scope Promotion

- Policy: <explicit_only | confirmed_in_scope | triage>
- Authority: <S-* entries that authorize the policy>
- Freeze decision: <what classes of findings were promoted and why>

## Required Outcomes

## R-001 — <observable outcome>

- Covers: F-001
- Acceptance: <observable end state; exact details live in linked F-* entries>
- Primary evidence: <smallest direct command/artifact/runtime observation>
- Mandatory broader gates: <none or exact gate>
- Change envelope/budget: <smallest allowed surface>
- Stop/Replan if: <falsifiable boundary or blocker>

## Preserve Controls

- <F-ID> — <correct behavior or valid adaptation that must remain true>

## Constraints

- <F-ID> — <affected authoritative constraint>

## Non-goals

- <explicit boundary; not an audit checklist>

## Change Envelope

- Target behavior/artifact:
- Expected paths, symbols, and direct consumers:
- Allowed artifacts:
- Forbidden artifacts:
- API/platform/dependency boundaries:
- User or harness budget:

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
- Required validation and commits pass.
- Goal lint and hashes pass.
- Completion is terminal.
