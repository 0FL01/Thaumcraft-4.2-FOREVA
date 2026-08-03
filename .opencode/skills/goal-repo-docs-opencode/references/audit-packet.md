# Durable audit swarm protocol

Use this protocol when RECON is delegated to multiple agents. Read `production-admission.md` first.

## Orchestrator responsibilities

Before fan-out:

- establish the orchestrator as the only writer of central ledger files;
- record live Git branch, HEAD, and status so unauthorized product edits are detectable;
- create the goal directory and active pointer;
- register authoritative sources and immutable oracles;
- freeze the actual supported production envelope, not an imagined universal environment;
- define the audit map and anti-scope;
- assign one `A-*` ID and one report path per shard;
- decide the promotion policy;
- freeze assignment/wave/reproduction budgets plus admitted-finding and total fix-checkpoint capacity;
- decide which product paths are read-only during RECON;
- record terminal statuses.

After each wave:

- compare live Git state to the recorded baseline and stop on unauthorized product edits;
- increment and check the audit-wave counter;
- confirm every assignment produced a durable packet;
- read every packet rather than trusting task summaries;
- normalize all material claims into `F-*` entries;
- retain exact deltas, positive parity, unknowns, test debt, benign adaptations, production-gate inputs, and failed-gate reasons;
- adjudicate duplicates and conflicts;
- directly verify only high-impact candidates that have a supported trigger and remain inside the one-attempt budget;
- defer synthetic-only, unsupported, impact-free, adjacent, over-budget, or over-capacity candidates;
- run coverage, production-admission, and resource lint before freezing GOAL.

## Assignment prompt contract

Each delegated task must state:

- Assignment ID and unique report path
- Scope and anti-scope
- Authoritative oracle and comparison direction
- Supported production envelope
- Questions to answer
- Expected evidence surfaces
- Read/write permissions
- Effort or tool-call budget
- At most one bounded candidate reproduction attempt
- Stop conditions
- Required report schema

Use this directive:

```text
Your durable deliverable is the assigned report file, not your chat response. Write the packet progressively after each material finding and before context pressure. Do not edit product files. Preserve exact observed/expected values, branches, conditions, paths, symbols, commands, source locators, positive parity, unknowns, and regression hazards.

For every candidate, separately record: whether the delta is real; the concrete supported production trigger; concrete user/contract/security/data/safety/performance impact; direct admission evidence; and whether the claim is synthetic-only or speculative. Confidence and severity do not create implementation scope. Spend at most one bounded reproduction attempt and do not construct increasingly artificial states to force a failure. Recommend admit/defer, but do not assign `required` or reserve an implementation slot; the orchestrator adjudicates and may still defer an otherwise valid candidate as `over_capacity`.

Return only the report path, terminal status, and a short index. If unfinished, mark the packet partial with the exact continuation point; do not rely on conversational memory.
```

## Atomicity

A shard may own a subsystem, but its findings must be atomic. Split findings when they can be fixed, verified, waived, deferred, or regressed independently.

Good split:

- launch velocity missing;
- underwater explosion strength differs;
- probability boundary differs;
- trail FX missing.

Bad aggregate:

- “Projectile behavior differs from original.”

Atomicity does not mean every atomic delta must be fixed. It means each delta can pass or fail the admission gate independently.

## Production relevance

A subagent does not get to call something a production bug merely because it can imagine an input. Require a supported entry point, realistic state/configuration, and concrete impact. If the only way to reproduce is to violate a frozen invariant, mutate private state, use an unsupported configuration, or create a bespoke synthetic harness, record that limitation and recommend `defer`.

## Positive results

Require explicit parity entries for audited surfaces that are already correct. A later broad edit must prove those controls still hold. Do not rely on “everything else looked fine.”

## Conflict handling

When reports disagree:

1. preserve both claims and evidence;
2. identify oracle/version and production-envelope differences;
3. run the smallest direct adjudication inside the remaining budget;
4. record the decision and rejected interpretation;
5. leave `blocking_question` if the result changes source scope and cannot be proven.

Do not average, merge, or silently choose between conflicting claims.

## Recursion firewall

- No task may spawn another audit/review task unless the orchestrator assigned a new `A-*` inside the frozen wave cap.
- No implementation or closure-review task may open new audit coverage.
- No review-of-review.
- No adjacent candidate is promoted because it appeared while investigating an admitted finding.
- Reaching a budget cap is a terminal disposition, not permission to continue with a different tool.

## Continuations

A continuation gets a new `A-*` ID and cites the prior packet. Its scope begins at the packet's explicit continuation point. The prior packet remains immutable. A continuation consumes the frozen assignment/wave budget.
