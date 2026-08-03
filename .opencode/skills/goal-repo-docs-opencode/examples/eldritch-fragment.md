# Eldritch migration fragment

Этот файл показывает, как часть пользовательского RECON должна пережить переход в Goal Ledger. Это иллюстрация схемы, а не полный готовый goal для репозитория Thaumcraft.

## RECON atomic findings

## F-001 — Primal Orb has no initial launch velocity

- Type: defect
- Disposition: required
- Severity: P0
- Confidence: confirmed
- Source IDs: S-001, S-002
- Audit IDs/Reports: A-007 / reports/A-007-primal.md
- Oracle: Thaumcraft 4.2.3.5 projectile launch behavior
- Observed: `EntityPrimalOrb.java:25-30`; the created projectile receives no effective initial speed and practically does not fly.
- Expected: casting initializes the original launch velocity through the Forge 1.12-compatible representation.
- Exact deltas: initial velocity missing; do not merge this with impact probability or FX.
- Affected paths/symbols: `src/main/java/.../EntityPrimalOrb.java`, `FocusPrimal`
- Primary evidence: focused runtime launch-motion test plus server smoke
- Regression hazards: hand routing, focus NBT, valid 1.12 server authority
- Outcome: R-002
- Notes/Disposition reason: confirmed P0 inside delegated Eldritch audit-and-fix scope

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
- Primary evidence: focused underwater-impact runtime test
- Regression hazards: normal impact branch and block-hit eligibility
- Outcome: R-002
- Notes/Disposition reason: independently verifiable from launch behavior

## F-003 — Primal Orb special-effect probability boundary differs

- Type: defect
- Disposition: required
- Severity: P1
- Confidence: confirmed
- Source IDs: S-001, S-002
- Audit IDs/Reports: A-007 / reports/A-007-primal.md
- Oracle: Thaumcraft 4.2.3.5 probability branch
- Observed: port behavior is described as `1/10%`.
- Expected: original behavior is described as `2/11%`.
- Exact deltas: preserve the exact random bound/comparison recovered by the oracle; do not paraphrase as “same chance”.
- Affected paths/symbols: `EntityPrimalOrb` special-effect random branch
- Primary evidence: deterministic boundary test against a checked oracle fixture
- Regression hazards: eligibility and placement conditions
- Outcome: R-002
- Notes/Disposition reason: exact branch expression must be copied into the real report/fixture

## F-004 — Primal Orb trail and impact feedback are absent

- Type: defect
- Disposition: required
- Severity: P1
- Confidence: confirmed
- Source IDs: S-001, S-002
- Audit IDs/Reports: A-007 / reports/A-007-primal.md
- Oracle: Thaumcraft 4.2.3.5 client FX dispatch
- Observed: trail and impact FX are absent.
- Expected: original trail and impact feedback is dispatched through valid 1.12 client representation.
- Exact deltas: both trail and impact surfaces are missing; they close independently from projectile physics.
- Affected paths/symbols: `EntityPrimalOrb`, direct FX consumers
- Primary evidence: focused FX dispatch guard/runtime observation
- Regression hazards: no client-authoritative gameplay mutation
- Outcome: R-002
- Notes/Disposition reason:

## F-005 — Void tool material tuple differs

- Type: defect
- Disposition: required
- Severity: P0
- Confidence: confirmed
- Source IDs: S-001, S-002
- Audit IDs/Reports: A-006 / reports/A-006-void.md
- Oracle: Thaumcraft 4.2.3.5 Void tool material
- Observed: durability `600`, enchantability `20`.
- Expected: durability `150`, enchantability `10`.
- Exact deltas: durability `600 -> 150`; enchantability `20 -> 10`.
- Affected paths/symbols: Void tool material registration and direct consumers
- Primary evidence: material tuple test
- Regression hazards: registry names and valid repair behavior
- Outcome: R-006
- Notes/Disposition reason:

## F-006 — Void Axe attack modifier differs

- Type: defect
- Disposition: required
- Severity: P1
- Confidence: confirmed
- Source IDs: S-001, S-002
- Audit IDs/Reports: A-006 / reports/A-006-void.md
- Oracle: Thaumcraft 4.2.3.5 Void Axe attack modifier
- Observed: attack modifier `9`.
- Expected: attack modifier `6`.
- Exact deltas: `9 -> 6`.
- Affected paths/symbols: Void Axe construction/attributes
- Primary evidence: focused attribute test
- Regression hazards: tool material tuple and attack-speed adaptation
- Outcome: R-006
- Notes/Disposition reason:

## F-007 — Ender Pearl Eldritch aspect tuple differs

- Type: defect
- Disposition: required
- Severity: P0
- Confidence: confirmed
- Source IDs: S-001, S-002
- Audit IDs/Reports: A-005 / reports/A-005-aspects.md
- Oracle: Thaumcraft 4.2.3.5 resolved object aspects
- Observed: `alienis 4` and `praecantatio 2` are missing; `iter` is `2`.
- Expected: include `alienis 4`, `praecantatio 2`, and `iter 4` in the final resolved tuple.
- Exact deltas: add `alienis 4`; add `praecantatio 2`; `iter 2 -> 4`.
- Affected paths/symbols: `ConfigAspects`, Ender Pearl final tag resolution
- Primary evidence: final resolved aspect fixture, not merely direct-tag source text
- Regression hazards: port-only direct tags may compensate for recipe-derived TC4 tags and must be judged by final tuple
- Outcome: R-007
- Notes/Disposition reason:

## F-008 — Research declaration corpus is exact

- Type: parity
- Disposition: preserve
- Severity: P0
- Confidence: confirmed
- Source IDs: S-001, S-002
- Audit IDs/Reports: A-001 / reports/A-001-research-graph.md
- Oracle: Thaumcraft 4.2.3.5 research graph corpus
- Observed: `16/16` entries match.
- Expected: remain `16/16` after implementation.
- Exact deltas: none; this is a zero-delta preserve control.
- Affected paths/symbols: research graph declarations
- Primary evidence: existing graph fingerprint guard
- Regression hazards: broad research-runtime edits must not rewrite declarations
- Outcome: none
- Notes/Disposition reason: positive parity promoted to a closure control

## F-009 — Existing Void tool test encodes the audited regression

- Type: test_debt
- Disposition: required
- Severity: P0
- Confidence: confirmed
- Source IDs: S-001, S-002
- Audit IDs/Reports: A-006 / reports/A-006-void.md
- Oracle: original tool effect is Weakness
- Observed: `ItemVoidCrimsonToolsStaticGuardTest.java:24-43` asserts Wither.
- Expected: replace the wrong static contract with behavioral parity evidence for Weakness and original duration.
- Exact deltas: test contract `Wither -> Weakness`; retain exact duration from oracle packet.
- Affected paths/symbols: named test and Void tool hit behavior
- Primary evidence: corrected focused test fails before implementation and passes after
- Regression hazards: do not accept a production-only change while the wrong guard remains
- Outcome: R-006
- Notes/Disposition reason: required as minimum evidence for the covered product finding

## GOAL outcome mapping

## R-002 — Restore Primal Orb behavior

- Covers: F-001, F-002, F-003, F-004
- Acceptance: every linked physics, probability, and feedback finding is verified against its exact RECON entry.
- Primary evidence: focused launch, underwater, deterministic probability, and FX tests
- Mandatory broader gates: checkpoint server smoke
- Change envelope/budget: projectile/focus and direct FX consumers only
- Stop/Replan if: a valid 1.12 representation requires public API or packet changes outside the frozen envelope

## R-006 — Restore Void equipment behavior and tests

- Covers: F-005, F-006, F-009
- Acceptance: linked material, attack, effect, repair, armor and test findings are independently verified.
- Primary evidence: focused item/material/effect tests
- Mandatory broader gates: checkpoint server smoke
- Change envelope/budget: equipment definitions, direct consumers and focused tests
- Stop/Replan if: a registry/API compatibility boundary would be crossed

## R-007 — Restore Eldritch aspect tuples

- Covers: F-007
- Acceptance: final resolved aspect tuples match the checked oracle, including exact amounts.
- Primary evidence: resolved-object fixture
- Mandatory broader gates: aspect registry suite
- Change envelope/budget: aspects and directly required fixture/test only
- Stop/Replan if: direct tags and recipe-derived tags cannot be adjudicated by final resolved output

## Preserve Controls

- F-008 — Research graph remains exactly `16/16`.

The important property is mechanical: fixing only launch velocity leaves F-002/F-003/F-004 unresolved, so R-002 cannot become verified.
