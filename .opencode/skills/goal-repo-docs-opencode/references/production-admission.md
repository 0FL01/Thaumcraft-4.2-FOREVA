# Production Admission Gate

This protocol prevents a confirmed audit delta from becoming an endless preventive-fix program.

## Core distinction

Three questions are independent:

1. **Is the delta real?** `Confidence` answers this.
2. **Can supported production reach it and does it matter?** The Production Gate answers this.
3. **Is it authorized and affordable inside this goal?** The promotion policy and Resource Governor answer this.

A `confirmed` finding can still be `deferred`. A synthetic test can prove that code behaves a certain way, but it cannot prove that supported production reaches the path or that the effect is worth changing.

## Freeze the production envelope

Before fan-out, record:

- supported versions, deployment modes, configurations, feature flags, and platform assumptions;
- real entry points, input/data shapes, lifecycle and concurrency patterns;
- existing invariants that make branches unreachable;
- explicitly unsupported or retired paths;
- concrete impact thresholds for correctness, security, data integrity, safety, and performance;
- the critical-risk exception policy;
- hard audit/reproduction/replan/review budgets, including a pre-RECON admitted-finding cap and total fix-checkpoint cap.

Do not use an imagined universal environment. The narrowest authoritative supported envelope is the reference.

## Allowed admission bases

A finding may become `required` only with one of these exact bases:

- `explicit_requirement`: an authoritative source directly requires the behavior.
- `production_incident`: a real production trace, issue, crash, corrupted state, user report, or equivalent direct observation proves the path and impact.
- `deterministic_supported_path`: static/runtime evidence proves a supported entry point deterministically reaches the faulty behavior with concrete impact.
- `current_diff_regression`: the current goal's diff broke an existing supported behavior or mandatory affected gate.
- `blocks_explicit_requirement`: the smallest direct blocker must change before an explicit requirement can be satisfied.
- `credible_critical_risk`: concrete preconditions establish a credible security, data-loss, safety, or irreversible-corruption risk even without a prior incident.
- `user_override`: the user explicitly selects the candidate after its trigger, impact, evidence, and cost are visible.

The following are not admission bases:

- “the code could theoretically do this”;
- a newly invented synthetic test with no supported trigger;
- style, cleanliness, robustness, future-proofing, or defense in depth;
- an advisory review finding;
- an unrelated existing test failure;
- parity difference when exact parity is not an explicit finish line;
- a bug in an unsupported configuration or retired path;
- an adjacent issue found while fixing another issue;
- a subagent's confidence or severity label by itself.

## One bounded candidate attempt

For a non-critical candidate without direct production evidence:

1. State one realistic supported trigger.
2. State one falsifiable expected observation.
3. Spend at most the frozen reproduction budget; the default maximum is one attempt.
4. If the attempt fails and no deterministic supported-path proof exists, set `Disposition: deferred` with reason `not_reproduced_within_budget`.
5. Do not broaden fixtures, mutate invariants, add fuzzing, or construct increasingly artificial states to force the failure.

A candidate is not “almost required”. Deferred means no implementation work in this goal.

## Capacity admission

Passing the semantic Production Gate does not guarantee an implementation slot. Capacity is frozen before fan-out. Under `skill-default`, at most `8` findings and `12` total fix checkpoints may be admitted. If additional candidates otherwise pass:

1. Rank explicit requirements, production incidents, and concrete critical risks first.
2. Then rank deterministic supported paths by trigger frequency, concrete impact, and smallest bounded fix cost.
3. Admit only candidates that fit both the finding-count and total-checkpoint caps.
4. Mark the rest `deferred` with `Admission Basis: over_capacity`; do not enlarge the goal.

A higher cap requires an authoritative source recorded before the additional work. The agent cannot manufacture authority after seeing a large backlog.

## Critical-risk exception

Rare does not mean irrelevant. A finding may pass without a production incident only when all concrete preconditions are recorded and the plausible outcome is one of:

- exploitable security boundary violation;
- irreversible data loss or corruption;
- safety harm;
- unrecoverable availability failure against an explicit SLO or contract.

Vague language such as “could be bad”, “might race”, or “may cause issues” is insufficient. The agent cannot declare its own critical-risk policy; it comes from the user, cited source, or repository contract.

## One-hop causal boundary

An admitted finding authorizes:

- the smallest change to its direct cause;
- directly required compatibility edits;
- regressions caused by that change;
- minimum direct evidence.

It does not authorize:

- fixing the next bug found in a dependency;
- refactoring the subsystem “while here”;
- adding generic retry/fallback/cache/validation frameworks;
- auditing every caller or callee;
- spawning a review agent to review another review;
- promoting test debt, observability, or cleanup into product work.

Record adjacent discoveries as `deferred` with provenance. Only a new user/source-authorized scope amendment can promote them.

## Resource Governor defaults

Unless an authoritative source provides different finite values before fan-out:

- audit assignments: `12`;
- audit waves: `1`;
- admitted required findings: `8`;
- total fix checkpoints: `12`;
- candidate reproduction attempts per finding: `1`;
- materially different replans per required finding: `2`;
- implementation subagent waves: `0`;
- closure review passes: `1`;
- adjacent finding auto-promotions: `0`;
- scope amendments: `0`;
- post-closure work items: `0`.

An agent may consume a budget but cannot raise it. Reaching a cap means defer, block, mark unmet, or request a versioned user-authorized scope amendment. It never means “keep trying because the next experiment is safe”.

## Required RECON fields

Every material finding records:

- `Production Gate`: `pass`, `fail`, or `not_applicable`;
- `Admission Basis`;
- `Production Trigger/Reachability`;
- `Concrete Impact/Contract`;
- `Admission Evidence`;
- `Admission Budget`: `reproduce_attempts=<N>; fix_checkpoints=<N>; review_passes=<N>`;
- `Admission Attempts Used`: `0` or `1`;
- `Speculation Boundary`;
- `Notes/Disposition reason`.

For a `required` finding, the gate must be `pass` and the basis must be one of the allowed admission bases. For `preserve` and `constraint`, use `not_applicable` with an exact reason. For a deferred candidate, use `fail` and say which gate failed.

## Examples

### Admit

```text
Primal Orb receives no launch velocity.
Production Trigger: supported cast path constructs the projectile.
Impact: every normal cast creates a nearly stationary projectile.
Basis: deterministic_supported_path.
Evidence: constructor and existing cast path prove the missing motion assignment.
Decision: required.
```

### Defer

```text
Reservoir may retain a mutable alias.
Production Trigger: no supported caller that mutates the supplied object after assignment was found.
Impact: hypothetical state drift only.
Bounded attempt: one caller search and one runtime probe found no trigger.
Decision: deferred — not_reproduced_within_budget.
```

### Do not recurse

```text
While fixing launch velocity, review notices a generic projectile abstraction could be cleaner.
Relation: independent cleanup; not caused by the diff and not required by the source.
Decision: deferred — adjacent_non_requirement.
```
