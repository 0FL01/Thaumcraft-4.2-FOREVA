# Goal: Restore the canonical Focus Pouch

Status: complete
Source: User-approved RECON plan from 2026-07-23; original TC4 classes under `thaumcraft_src/**`
Last updated: 2026-07-23

## Objective
Restore the TC4 Focus Pouch as one canonical `thaumcraft:focuspouch` item that keeps the normal pouch texture and can be worn in the Baubles belt slot.

## Execution Directive
Complete the frozen Required Outcomes using the listed Change Envelope and Primary Evidence. Keep the change scoped to the Focus Pouch registration and its stale second-item resource route. Finish with a deployable commit and rebuilt jar.

## Frozen Contract

### Required Outcomes
- R1: Register one canonical Focus Pouch with TC4 class behavior.
  - Acceptance: `ConfigItems.itemFocusPouch` is an `ItemFocusPouchBauble` registered as `thaumcraft:focuspouch`; no second `thaumcraft:focuspouchbauble` item is registered; the item remains a `BELT` bauble and inherits pouch GUI/NBT behavior.
  - Primary evidence: Focused registration parity guard, original `ConfigItems`/`ItemFocusPouchBauble` comparison, and runtime smoke.
  - Status: verified
  - Evidence: `FocusPouchRegistrationParityStaticGuardTest`; focused test and `./scripts/dev.sh validate --smoke` passed on 2026-07-23.
- R2: Route the canonical item through the visible original texture.
  - Acceptance: The canonical model uses `focuspouch.png`; the stale `focuspouchbauble` model and localization route are removed; recipes, research, Pech trades, and the creative item all resolve to the canonical item.
  - Primary evidence: Focused resource/output guard and client-facing source/resource inspection.
  - Status: verified
  - Evidence: The focused guard verifies canonical consumers, exact TC4 texture bytes, visible alpha, and removal of the stale model/language route.
- R3: Keep the checkpoint deployable.
  - Acceptance: Focused tests, `./scripts/dev.sh validate --smoke`, and `./scripts/dev.sh build` pass before a scoped commit.
  - Primary evidence: Validation logs, rebuilt jar, and commit hash.
  - Status: verified
  - Evidence: Focused test, clean-world `./scripts/dev.sh validate --smoke`, and `./scripts/dev.sh build` passed on 2026-07-23.

### Constraints
- C1: Preserve Java 8, Forge 1.12.2, the canonical `thaumcraft:focuspouch` registry name, pouch NBT key, GUI id, and Baubles dependency.
- C2: Treat `thaumcraft_src/**` and donor jars as read-only references.
- C3: Do not include unrelated working-tree changes in the checkpoint commit.

### Non-goals
- Migrating saves or stacks using the erroneous `thaumcraft:focuspouchbauble` registry name.
- Redrawing the original fully transparent `focuspouchbauble.png` asset.
- Refactoring focus switching, pouch containers, or unrelated Baubles.

## Change Envelope
- Expected runtime paths: `ConfigItems`, `ItemFocusPouchBauble`, canonical item model/language resources, and existing recipe/research/Pech consumers.
- Allowed artifacts: Minimal registration/resource fixes, one focused regression test, this contract, validation logs, rebuilt jar, and a scoped commit.
- Forbidden artifacts: Changes under `thaumcraft_src/**`, registry migration handlers, dependency upgrades, and unrelated cleanup.

## Current Checkpoint
- Target: complete.
- Result: R1, R2, and R3 are verified; one canonical textured belt pouch is registered.

## Material Decisions
- Keep `ItemFocusPouch` as the shared GUI/NBT base class and instantiate `ItemFocusPouchBauble` for the canonical `itemFocusPouch` field, matching TC4.
- Do not add migration for the erroneous port-only `thaumcraft:focuspouchbauble` id, per the approved plan.
- Keep the original transparent donor PNG unchanged and unused; remove only the port-created model and localization route that exposed it as a second item.

## Checkpoint History
- 2026-07-23: Replaced the two registered pouch instances with one `ItemFocusPouchBauble` under `thaumcraft:focuspouch`; existing recipe, research, Pech, creative, GUI, NBT, and focus-switch consumers now share that item.
- 2026-07-23: Removed the stale second model/localization route and added a focused guard for registration, belt type, canonical outputs, and exact visible TC4 texture parity.
- 2026-07-23: The first smoke attempt reached registry loading but paused on the intentionally unsupported old `focuspouchbauble` entry in the retained development world. A clean-world rerun passed and the prior development world was restored unchanged; focused tests and the final build also passed.

## Completion
- Resolved outcomes: R1, R2, and R3 verified.
- Validation: Focused guard; `./scripts/dev.sh validate --smoke`; `./scripts/dev.sh build`.
- Known limitation: Existing stacks saved under `thaumcraft:focuspouchbauble` are not migrated.
