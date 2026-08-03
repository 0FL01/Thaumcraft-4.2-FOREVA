# Durable audit swarm protocol

Use this protocol when RECON is delegated to multiple agents.

## Orchestrator responsibilities

Before fan-out:

- establish the orchestrator as the only writer of central ledger files;
- record live Git branch, HEAD, and status so unauthorized product edits are detectable;
- create the goal directory and active pointer;
- register authoritative sources and immutable oracles;
- define the audit map and anti-scope;
- assign one `A-*` ID and one report path per shard;
- decide the promotion policy;
- decide which product paths are read-only during RECON;
- record effort/tool budgets and terminal statuses.

After each wave:

- compare live Git state to the recorded baseline and stop on unauthorized product edits;
- confirm every assignment produced a durable packet;
- read every packet rather than trusting task summaries;
- normalize all material claims into `F-*` entries;
- retain exact deltas, positive parity, unknowns, test debt, and benign adaptations;
- adjudicate duplicates and conflicts;
- directly verify high-severity findings when possible;
- run coverage lint before freezing GOAL.

## Assignment prompt contract

Each delegated task must state:

- Assignment ID and unique report path
- Scope and anti-scope
- Authoritative oracle and comparison direction
- Questions to answer
- Expected evidence surfaces
- Read/write permissions
- Effort or tool-call budget
- Stop conditions
- Required report schema

Use this directive:

```text
Your durable deliverable is the assigned report file, not your chat response. Write the packet progressively after each material finding and before context pressure. Do not edit product files. Preserve exact observed/expected values, branches, conditions, paths, symbols, commands, source locators, positive parity, unknowns, and regression hazards. Return only the report path, terminal status, and a short index. If unfinished, mark the packet partial with the exact continuation point; do not rely on conversational memory.
```

## Atomicity

A shard may own a subsystem, but its findings must be atomic. Split findings when they can be fixed, verified, waived, or regressed independently.

Good split:

- launch velocity missing;
- underwater explosion strength differs;
- probability boundary differs;
- trail FX missing.

Bad aggregate:

- “Projectile behavior differs from original.”

## Positive results

Require explicit parity entries for audited surfaces that are already correct. A later broad edit must prove those controls still hold. Do not rely on “everything else looked fine.”

## Conflict handling

When reports disagree:

1. preserve both claims and evidence;
2. identify oracle/version differences;
3. run the smallest direct adjudication;
4. record the decision and rejected interpretation;
5. leave `blocking_question` if the result changes scope and cannot be proven.

Do not average, merge, or silently choose between conflicting claims.

## Continuations

A continuation gets a new `A-*` ID and cites the prior packet. Its scope begins at the packet's explicit continuation point. The prior packet remains immutable.
