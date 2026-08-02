# Goal: Complete TC4 golem heart and upgrade backend parity

Status: complete
Source: User-approved multi-agent RECON and implementation directive dated 2026-08-02
Last updated: 2026-08-02

## Objective

Restore the complete server/backend behavior of all TC4 golem hearts and their Aer, Terra, Ignis, Aqua, Ordo, and Perditio interactions on Forge 1.12.2. Resolve shared inventory, filtering, persistence, navigation, fluid, combat, crop, and fishing defects before closing individual heart behavior. Cover the high-value behavioral contracts with Pareto-focused runtime tests, validate every runtime checkpoint on a dedicated server, and finish with an up-to-date universal jar.

## Execution Directive

Complete the frozen Required Outcomes in dependency order using the listed Change Envelope and Primary Evidence. Work iteratively on the smallest unresolved backend slice, run focused tests after each slice, and do not declare a heart complete from compilation or source-string guards alone. Prefer TC4 bytecode behavior unless it conflicts with an explicit shipped research promise, a proven 1.12 API requirement, or a user-approved safety correction.

## Frozen Contract

### Required Outcomes

- R1: Remove runtime action blockers shared by Fishing, Liquid, and Essentia.
  - Acceptance: All obsolete 1.7 sound identifiers are replaced by valid 1.12 `SoundEvents`; representative fishing and transfer paths execute without null sound events.
  - Primary evidence: Focused runtime tests and `./scripts/dev.sh validate --smoke`.
  - Status: verified
  - Evidence: Fishing uses registered bobber throw/splash events and all Liquid/Essentia transfers use `ENTITY_GENERIC_SWIM`; focused guards and `./scripts/dev.sh validate --smoke` passed.

- R2: Restore shared item-filter, ghost-slot, extraction, and persistence contracts.
  - Acceptance: Extraction scans all accessible slots; Fill precise amounts are configurable and honored; Empty/Gather/Use filters remain unit filters; Liquid accepts only fluid containers; Perditio metadata/durability/NBT/ore behavior matches the reviewed TC4 contract; colors and upgrades remain synchronized after load and live changes.
  - Primary evidence: Runtime tests covering sided extraction, amount calculation, ghost clicks, fluid validation, comparison-toggle matrices, and NBT/data synchronization.
  - Status: verified
  - Evidence: Runtime tests cover normal/sided extraction, requested amounts, first-ore matching, Perditio comparison semantics, core-specific ghost limits, fluid validation, extended-count NBT, color publication, and live upgrade-array sync; focused tests and `./scripts/dev.sh validate --smoke` passed.

- R3: Restore Sorting heart end to end.
  - Acceptance: A Sorting golem extracts from home only when an in-range marked destination already contains the matching item and has room, carries the item to that destination, inserts through the marked side including double chests, excludes home, and recovers from path stalls.
  - Primary evidence: Runtime/integration inventory tests with empty, seeded, full, farther, sided, and double-chest destinations.
  - Status: verified
  - Evidence: Sorting retains a hidden wildcard sentinel, validates seeded non-home destinations with room and marker-side/Perditio rules, routes carried items, handles double chests, and keeps stall recovery; seven integration scenarios plus shared R2 suites and `./scripts/dev.sh validate --smoke` passed.

- R4: Complete Harvest and Lumber backend parity.
  - Acceptance: Baseline and IMC crop registrations work; standard/clickable/stacked categories preserve their distinct semantics; stems and stacked bases survive; Harvest uses the reviewed TC4 search/adjacency model; Ordo replants only the harvested crop using consumed drops; Lumber keeps TC4 top-down connected-log behavior and does not replant.
  - Primary evidence: Runtime crop matrix, Harvest search/Ordo tests, Lumber traversal tests, and server smoke.
  - Status: verified
  - Evidence: Baseline/IMC crop registration, standard/clickable/stacked semantics, stem/base preservation, shuffled range/adjacency behavior, exact-state continuation, and safe Ordo replanting are runtime-covered; Lumber parity guards remain green; focused suites and `./scripts/dev.sh validate --smoke` passed.

- R5: Complete Liquid and Essentia backend parity.
  - Acceptance: Marker-only in-range liquid source selection works; sided capabilities use the proven clicked/home face; Perditio drains the farthest connected source first; transfers cannot exceed capacity; Ignis fluid filters and Ordo marker colors route correctly; reservoir essentia extraction works; marker dimensions round-trip as full integers.
  - Primary evidence: Capability-backed sided tank tests, source-order tests, reservoir tests, marker NBT tests, and server smoke.
  - Status: verified
  - Evidence: Capability-backed tests prove home/clicked face use, marker-only nearest in-range selection, fluid/color routing, carry-capacity bounds, and farthest-first Perditio draining; reservoir empty/partial extraction and full signed marker-dimension NBT round trips are runtime-covered; `./scripts/dev.sh validate --smoke` passed.

- R6: Complete Fishing backend parity and special upgrades.
  - Acceptance: Fishing uses the reviewed TC4 loot table, damage and enchant rules; Aer extra-catch, Ignis cooking/fire, Ordo good-loot, and Perditio junk-reduction effects are deterministic under tests; bobber lifecycle/backend flags are correct.
  - Primary evidence: Seeded loot tests, upgrade probability-boundary tests, catch entity tests, and server smoke.
  - Status: verified
  - Evidence: Runtime tests cover exact loot descriptors/weights, TC4 damage and guaranteed-enchantment construction, Aer/Terra/Ordo/Perditio probability boundaries, Ignis smelting and burning catch entities, and bobber flags/effective lifetime; Fishing sound guards remain green and `./scripts/dev.sh validate --smoke` passed.

- R7: Complete Guard, Butcher, ranged combat, retaliation, and shared navigation parity.
  - Acceptance: Visor attribution does not create damage immunity; darts use the target-aware trajectory and hand off to melee inside three blocks; Perditio retaliates against the reviewed immediate source; Guard/Butcher exclusions and population rules hold; Aqua home range is dynamic; all hearts can open and close supported doors/fence gates; heavy golems retain reviewed water movement.
  - Primary evidence: Combat runtime tests, target-selection tests, door/gate tests, range/movement tests, and server smoke.
  - Status: verified
  - Evidence: Runtime tests cover Visor XP attribution without immunity, target-aware darts and melee handoff, immediate-source Perditio retaliation before fire immunity, Guard exclusions/owner/tame safety, Butcher pair preservation, strict Aqua home range, universal wooden-door/fence-gate lifecycle, and heavy-water policy/speed; focused suites and `./scripts/dev.sh validate --smoke` passed.

- R8: Close remaining heart/upgrade persistence and research contracts.
  - Acceptance: Bell pickup/replacement preserves toggles and filter colors; client/backend calculations use synchronized golem type, upgrades, and carried fluid; Use Aer cadence honors the shipped research promise; missing Butcher warp and golem research sibling are restored; every core capability map and core-specific upgrade promise has regression coverage.
  - Primary evidence: NBT/bell round-trip tests, live sync tests where practical, cadence tests, research guards, full validation, and manual gameplay matrix.
  - Status: verified
  - Evidence: Runtime tests cover bell/placer toggles, colors, filters and normalized hidden Sorting state; synchronized type/upgrades/full-width fluid identity+amount drive client calculations/render data; Use Aer cadence is 15/12/9 ticks; exact capability/slot/duplicate-upgrade and core-specific gates are covered; research siblings/warp guards and `./scripts/dev.sh validate --smoke` passed.

- R9: Produce the validated release artifact.
  - Acceptance: All focused and non-GUI tests pass, dedicated-server smoke reaches ready state without crash markers, `git diff --check` passes, and `./scripts/dev.sh build` produces the final universal jar.
  - Primary evidence: Exact commands and artifact path in the final report.
  - Status: verified
  - Evidence: `git diff --check`, `./scripts/dev.sh validate --smoke`, and `./scripts/dev.sh build` all passed; the release artifact is `build/libs/Thaumcraft-1.0.0-universal.jar`.

### Constraints

- Preserve Java 8, Forge 1.12.2, TC4 package/API identities, registry ids, packet ids, and existing saved worlds unless an explicit compatible migration is included.
- Keep `thaumcraft_src/**` and donor jars read-only; do not commit decompiled output, logs, worlds, generated remap state, or third-party binaries.
- Keep the user-approved same-crop Harvest replant safety rule even though original TC4 accepted any nearby plantable item.
- Keep corrected Ordo GUI gating rather than restoring known hidden-hitbox and hover-gate bugs from TC4.
- Runtime-affecting checkpoints require `./scripts/dev.sh validate --smoke`; final code requires `./scripts/dev.sh build`.
- Tests follow Pareto coverage: prioritize shared helpers, state transitions, upgrade gates, routing decisions, capacity/inventory invariants, and deterministic RNG boundaries over exhaustive rendering or trivial accessors.

### Non-goals

- Pixel-perfect client renderer/model parity unrelated to backend correctness, including custom dart geometry.
- Reproducing known TC4 item duplication, capacity overflow, unrelated-seed planting, hidden GUI hitbox, or stale-client bugs.
- Dependency, platform, mapping, or architecture upgrades.
- Rewriting golems into the Thaumcraft 6 modular golem system.

## Change Envelope

- Target: `thaumcraft.common.entities.golems`, golem AI packages, `InventoryMob`, inventory/fluid helpers, golem containers/ghost slots, crop registration utilities and IMC handling, focused tests, golem research registration, and this goal.
- Allowed supporting changes: synchronized display state needed to represent authoritative backend state, existing English localization keys required by restored feedback, and minimal test fixtures.
- Forbidden supporting changes: unrelated gameplay systems, donor edits, new dependencies, speculative abstractions, and visual-only scope expansion before R1-R9 close.
- User or harness budget: iterative implementation from R1 through R9; no intermediate commit required unless requested.

## Current Checkpoint

- Closes: R9 and the complete TC4 golem heart/upgrade backend parity goal.
- Smallest next action: Manual in-game spot checks or commit on user request.
- Expected evidence: Complete; automated validation, dedicated-server readiness, and release artifact are recorded below.
- Stop or replan if: a required 1.12 behavior cannot be represented without breaking a stable public/NBT contract, or runtime evidence contradicts the reviewed side/direction mapping.

## Current State

- Resolved: R1-R9; all reviewed heart and upgrade backend contracts are implemented and the final universal jar is built.
- Last relevant evidence: `git diff --check`, `./scripts/dev.sh validate --smoke`, and `./scripts/dev.sh build` passed.
- Blocker: None.
- Next: Optional manual gameplay spot checks and commit.

## Material Decisions

- 2026-08-02: Shared backend invariants are repaired before individual heart AI so later fixes rely on one authoritative filter/extraction model.
- 2026-08-02: The shipped Use+Aer research promise is an acceptance contract even though the TC4 binary appears not to apply the upgrade correctly.
- 2026-08-02: Fluid capability faces are changed only after a sided 1.12 runtime test proves the correct marker/home face.
- 2026-08-02: Same-crop Ordo replanting remains stricter than TC4 to prevent observed wheat-for-beetroot replacement.

## Checkpoint History

- 2026-08-02: User requested mass parallel audit of every heart and improvement. Eight general agents audited core contracts, inventory/sorting, Harvest/Lumber, Use/Fishing, combat, fluids/essentia, and generic upgrades against TC4.
- 2026-08-02: User approved full iterative implementation with Pareto-focused backend test coverage and final build.
- 2026-08-02: R1 replaced obsolete Fishing/Liquid/Essentia sound lookups with registered 1.12 events; focused tests and dedicated-server smoke passed.
- 2026-08-02: R2 restored extraction scans, TC4 filter amount/comparison behavior, core-specific item/fluid ghost slots, extended precise-count persistence, color publication, and live client upgrade-array synchronization; focused runtime tests and dedicated-server smoke passed.
- 2026-08-02: R3 restored Sorting's hidden wildcard state, seeded destination routing, source exclusion, marker-side comparisons, double-chest insertion, and stall recovery; integration tests and dedicated-server smoke passed.
- 2026-08-02: R4 restored baseline/IMC crop registrations, crop-category semantics, stem/stacked-base preservation, TC4 Harvest search/continuation/adjacency behavior, and retained safe same-crop Ordo replanting; focused runtime suites and dedicated-server smoke passed.
- 2026-08-02: R5 restored marker-only nearest fluid selection, proved and applied direct capability faces, bounded multi-source transfers, farthest-first Perditio draining, fluid/color routing, reservoir extraction, and full-width marker dimensions; capability-backed tests and dedicated-server smoke passed.
- 2026-08-02: R6 restored exact Fishing loot construction, deterministic upgrade effects, Ignis smelting/fire, and effective bobber flags/lifetime; runtime suites and dedicated-server smoke passed.
- 2026-08-02: R7 restored Visor attribution, target-aware darts/melee handoff, immediate-source retaliation, target safety, dynamic Aqua home range, universal door/gate handling, and heavy-water behavior; runtime suites and dedicated-server smoke passed.
- 2026-08-02: R8 preserved bell toggles/colors, synchronized type/upgrades/full-width fluid display state, fulfilled Use Aer cadence, restored research sibling/warp metadata, and added exact core/upgrade contract coverage; focused suites and dedicated-server smoke passed.
- 2026-08-02: R9 passed full validation, dedicated-server smoke, jar checks, and the final Gradle build; `build/libs/Thaumcraft-1.0.0-universal.jar` was produced.

## Completion

- Resolved outcomes: R1-R9.
- Commands and artifacts: `git diff --check`; `./scripts/dev.sh validate --smoke`; `./scripts/dev.sh build`; `build/libs/Thaumcraft-1.0.0-universal.jar`.
- Final status: complete.
