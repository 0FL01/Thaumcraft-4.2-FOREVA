# Eldritch migration fragment with Sol recursion firewall

Это иллюстрация того, как один и тот же подтверждённый RECON разделяется на **реальные обязательные исправления** и **подтверждённые, но отложенные дельты**. `Confidence: confirmed` не означает `Disposition: required`.

Предполагаемая policy:

```text
Audit-Promotion-Policy: production_gate
Candidate-Reproduction-Cap-Per-Finding: 1
```

Production envelope: поддерживаемый Forge 1.12.2 gameplay path, обычные игровые конфигурации и реальные пользовательские действия. Exact TC4 parity сама по себе не является обязательной, если дельта не влияет на поддерживаемое поведение и пользователь отдельно не потребовал byte/behavior-exact parity.

## F-001 — Primal Orb has no initial launch velocity

- Type: defect
- Disposition: required
- Severity: P0
- Confidence: confirmed
- Source IDs: S-001, S-002
- Audit IDs/Reports: A-007 / reports/A-007-primal.md
- Oracle: Thaumcraft 4.2.3.5 projectile launch behavior and supported cast path
- Observed: the created projectile receives no effective initial speed and practically does not fly.
- Expected: a normal supported cast initializes a non-zero launch vector through the valid Forge 1.12 representation.
- Exact deltas: initial velocity missing; impact probability and FX are separate findings.
- Affected paths/symbols: `EntityPrimalOrb`, `FocusPrimal`
- Primary evidence: supported cast path deterministically constructs the zero-motion projectile.
- Production Gate: pass
- Admission Basis: deterministic_supported_path
- Production Trigger/Reachability: every ordinary Primal Focus cast enters this constructor.
- Concrete Impact/Contract: the primary projectile action is visibly non-functional for every cast.
- Admission Evidence: direct call path plus focused launch observation; no synthetic state required.
- Admission Budget: reproduce_attempts=0; fix_checkpoints=1; review_passes=1
- Admission Attempts Used: 0
- Speculation Boundary: direct launch assignment and minimum focused evidence only; no projectile refactor.
- Regression hazards: hand routing, focus NBT, valid 1.12 server authority
- Outcome: R-002
- Notes/Disposition reason: admitted because the supported production path deterministically hits the fault.

## F-002 — Primal Orb underwater explosion strength differs

- Type: defect
- Disposition: required
- Severity: P1
- Confidence: confirmed
- Source IDs: S-001, S-002
- Audit IDs/Reports: A-007 / reports/A-007-primal.md
- Oracle: Thaumcraft 4.2.3.5 underwater impact branch
- Observed: underwater explosion strength is `4`.
- Expected: underwater explosion strength is `2`.
- Exact deltas: `4 -> 2` only in the underwater branch.
- Affected paths/symbols: `EntityPrimalOrb` impact handling
- Primary evidence: supported water-impact path and one focused runtime observation
- Production Gate: pass
- Admission Basis: deterministic_supported_path
- Production Trigger/Reachability: an ordinary projectile can impact while submerged in supported gameplay.
- Concrete Impact/Contract: every submerged impact doubles the intended explosion strength.
- Admission Evidence: direct branch conditions use a supported world state; one bounded runtime probe reproduces it.
- Admission Budget: reproduce_attempts=1; fix_checkpoints=1; review_passes=1
- Admission Attempts Used: 1
- Speculation Boundary: underwater constant/branch only; normal impact and generic explosion cleanup excluded.
- Regression hazards: normal impact strength and block-hit eligibility
- Outcome: R-002
- Notes/Disposition reason: admitted after one realistic supported reproduction.

## F-003 — Primal Orb probability expression differs

- Type: benign_delta
- Disposition: deferred
- Severity: P2
- Confidence: confirmed
- Source IDs: S-001, S-002
- Audit IDs/Reports: A-007 / reports/A-007-primal.md
- Oracle: Thaumcraft 4.2.3.5 probability branch
- Observed: port expression differs from the original random bound/comparison.
- Expected: no change is required by the current production-focused objective.
- Exact deltas: port was summarized as `1/10%`; oracle as `2/11%`.
- Affected paths/symbols: `EntityPrimalOrb` special-effect random branch
- Primary evidence: static oracle comparison
- Production Gate: fail
- Admission Basis: no_concrete_impact
- Production Trigger/Reachability: the branch is reachable, but no concrete harmful gameplay outcome or source contract was established.
- Concrete Impact/Contract: tiny probability parity delta only; exact parity is not the frozen finish line.
- Admission Evidence: comparison proves a delta, not production harm.
- Admission Budget: reproduce_attempts=0; fix_checkpoints=0; review_passes=0
- Admission Attempts Used: 0
- Speculation Boundary: do not generate large statistical suites to manufacture importance.
- Regression hazards: eligibility and placement conditions
- Outcome: none
- Notes/Disposition reason: confirmed delta, deferred because confidence is not actionability.

## F-004 — Primal Orb trail and impact FX are absent

- Type: defect
- Disposition: deferred
- Severity: P2
- Confidence: confirmed
- Source IDs: S-001, S-002
- Audit IDs/Reports: A-007 / reports/A-007-primal.md
- Oracle: Thaumcraft 4.2.3.5 client FX dispatch
- Observed: trail and impact FX are absent.
- Expected: no implementation in this production-critical goal; retain as a future UX candidate.
- Exact deltas: both trail and impact visual surfaces are missing.
- Affected paths/symbols: `EntityPrimalOrb`, direct FX consumers
- Primary evidence: direct client-code comparison
- Production Gate: fail
- Admission Basis: over_budget
- Production Trigger/Reachability: supported and visible, but it does not block gameplay correctness.
- Concrete Impact/Contract: cosmetic feedback only; user did not make visual parity mandatory.
- Admission Evidence: direct comparison, no gameplay failure.
- Admission Budget: reproduce_attempts=0; fix_checkpoints=0; review_passes=0
- Admission Attempts Used: 0
- Speculation Boundary: no client FX framework or broad rendering audit.
- Regression hazards: no client-authoritative gameplay mutation
- Outcome: none
- Notes/Disposition reason: explicitly deferred to keep the frozen production-fix budget bounded.

## F-005 — Void tools apply Wither instead of Weakness

- Type: defect
- Disposition: required
- Severity: P1
- Confidence: confirmed
- Source IDs: S-001, S-002
- Audit IDs/Reports: A-006 / reports/A-006-void.md
- Oracle: supported Void tool hit behavior and TC4 effect contract
- Observed: a normal successful hit applies `Wither`.
- Expected: a normal successful hit applies `Weakness` for the original duration.
- Exact deltas: potion identity `Wither -> Weakness`; preserve the supported hit trigger and duration.
- Affected paths/symbols: Void tool hit behavior and direct effect consumer
- Primary evidence: every normal successful supported hit enters the wrong effect branch.
- Production Gate: pass
- Admission Basis: deterministic_supported_path
- Production Trigger/Reachability: ordinary combat with a Void tool reaches this branch.
- Concrete Impact/Contract: every supported hit applies the wrong gameplay debuff.
- Admission Evidence: deterministic hit path plus focused effect observation.
- Admission Budget: reproduce_attempts=0; fix_checkpoints=1; review_passes=1
- Admission Attempts Used: 0
- Speculation Boundary: potion identity and direct assertion only; no equipment architecture refactor.
- Regression hazards: original duration, successful-hit condition, registry names
- Outcome: R-006
- Notes/Disposition reason: admitted because the wrong effect occurs on the normal supported combat path.

## F-006 — Existing test encodes the wrong Void effect

- Type: test_debt
- Disposition: deferred
- Severity: P1
- Confidence: confirmed
- Source IDs: S-001, S-002
- Audit IDs/Reports: A-006 / reports/A-006-void.md
- Oracle: admitted F-005 requires Weakness; current test asserts Wither.
- Observed: the existing assertion freezes the audited regression.
- Expected: change only this assertion as minimum evidence while implementing F-005; do not make test debt a separate product outcome.
- Exact deltas: asserted effect `Wither -> Weakness`; no broad test expansion.
- Affected paths/symbols: named test and Void hit behavior
- Primary evidence: direct verification artifact for F-005
- Production Gate: fail
- Admission Basis: adjacent_non_requirement
- Production Trigger/Reachability: not an independent production behavior.
- Concrete Impact/Contract: the assertion is handled inside F-005's evidence budget, not as another required finding.
- Admission Evidence: exact assertion is directly coupled to F-005.
- Admission Budget: reproduce_attempts=0; fix_checkpoints=0; review_passes=0
- Admission Attempts Used: 0
- Speculation Boundary: one assertion only; no test-suite cleanup or matrix.
- Regression hazards: test must not invent additional product requirements
- Outcome: none
- Notes/Disposition reason: durable test debt, but no independent implementation slot; minimum edit is authorized by F-005.

## F-007 — Research declaration corpus is exact

- Type: parity
- Disposition: preserve
- Severity: P0
- Confidence: confirmed
- Source IDs: S-001, S-002
- Audit IDs/Reports: A-001 / reports/A-001-research-graph.md
- Oracle: Thaumcraft 4.2.3.5 research graph corpus
- Observed: `16/16` entries match.
- Expected: remain `16/16` after implementation.
- Exact deltas: none; zero-delta preserve control.
- Affected paths/symbols: research graph declarations
- Primary evidence: existing graph fingerprint guard
- Production Gate: not_applicable
- Admission Basis: preserve_control
- Production Trigger/Reachability: not applicable; this is already-correct behavior.
- Concrete Impact/Contract: broad edits must not regress the exact corpus.
- Admission Evidence: current `16/16` fingerprint.
- Admission Budget: reproduce_attempts=0; fix_checkpoints=0; review_passes=0
- Admission Attempts Used: 0
- Speculation Boundary: validation only; no declaration rewrite.
- Regression hazards: research-runtime edits must not rewrite declarations
- Outcome: none
- Notes/Disposition reason: positive parity retained as a closure control.

## Outcome mapping

## R-002 — Restore production-relevant Primal Orb behavior

- Covers: F-001, F-002
- Acceptance: normal casts launch correctly and supported underwater impacts use strength `2`.
- Primary evidence: focused launch and one underwater-impact test
- Mandatory broader gates: checkpoint server smoke
- Change envelope/budget: projectile launch/underwater branch and direct tests only; max 2 checkpoints; Resource Governor applies
- Stop/Replan if: two materially different direct attempts fail or the fix needs public API changes

`F-003` and `F-004` do not block `R-002`, because they were not admitted.

## R-006 — Restore admitted Void hit behavior

- Covers: F-005
- Acceptance: normal Void-tool hits apply `Weakness`; the focused gate verifies only that admitted behavior.
- Primary evidence: focused hit-effect test
- Mandatory broader gates: checkpoint server smoke
- Change envelope/budget: direct hit-effect branch and its minimum focused assertion; max 1 checkpoint; Resource Governor applies
- Stop/Replan if: a registry/API boundary or the frozen replan cap is reached

## Preserve Controls

- F-007 — Research graph remains exactly `16/16`.

## Resource Governor fragment

```text
Budget-Authority: skill-default
Max Required Findings: 8
Frozen Required Finding Count: 3
Max Total Fix Checkpoints: 12
Frozen Total Fix Checkpoints: 3
Max Candidate Reproduction Attempts Per Finding: 1
Max Material Replans Per Required Finding: 2
Max Implementation Subagent Waves: 0
Max Closure Review Passes: 1
Max Scope Amendments: 0
Adjacent Finding Auto-Promotions: 0
Post-Closure Work Items: 0
```

Главное свойство: Sol видит F-003/F-004, но не имеет права тратить на них implementation-токены. Они durable, не забыты, однако не входят в finish line.
