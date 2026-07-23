# Goal: Correct Deconstruction Table object placement

Status: complete
Source: User-approved visual defect plan from 2026-07-23; evidence screenshot and TC4 renderer bytecode/assets
Last updated: 2026-07-23

## Objective
Make the table-mounted thaumometer lie flat on the Deconstruction Table and place the processed item inside its aperture without regressing the restored backend, GUI, aspect overlay, or meta-14 item route.

## Execution Directive
Implement the approved correction as a minimal follow-up to `eb4b8c1a`. Finish with focused tests, `./scripts/dev.sh validate --smoke`, `./scripts/dev.sh build`, a final transform audit, and one scoped commit.

## Required Outcomes
- R1: Separate the thaumometer and input render contexts.
  - Acceptance: The scanner no longer receives `TransformType.GROUND`; it uses the TC4 ENTITY-context scale and its natural horizontal OBJ plane. Regular processed items retain their independent framed ground pose, count-one rendering, rotation, hover, alpha, and additive blend.
  - Status: verified
  - Evidence: The table scanner now uses the complete TC4 ENTITY/frame transform chain and a raw table-specific TEISR entry point; the regular input retains its independent ground-model path.
- R2: Preserve the surrounding visual contract.
  - Acceptance: Scanner and input remain centered over the table; aspect geometry, GUI tint/tooltip, meta-14 TEISR route, and original assets remain unchanged.
  - Status: verified
  - Evidence: Updated visual guards cover scanner bounds/orientation, input transforms, GUI/aspect invariants, meta-14 routing, and byte-exact original assets.
- R3: Keep the correction deployable.
  - Acceptance: Focused Deconstruction Table tests, server smoke, build, and source-level transform audit pass before a scoped commit.
  - Status: verified
  - Evidence: Focused Deconstruction Table/thaumometer tests, clean-world `./scripts/dev.sh validate --smoke`, `./scripts/dev.sh build`, and final source-level transform audit passed on 2026-07-23.

## Constraints
- Preserve Java 8, Forge 1.12.2, registry/GUI/packet ids, backend behavior, and original assets.
- Do not alter global thaumometer GUI, held, ground, or fixed transforms.
- Keep `thaumcraft_src/**` and donor jars read-only.
- Do not include unrelated working-tree changes.

## Material Decisions
- The previous table-local `GROUND` approximation is invalid specifically for the custom thaumometer TEISR: its JSON ground transform rotates an already horizontal scanner plane by 90 degrees.
- Reproduce TC4's full table scanner chain: table scale `0.8`, entity bob, frame scale/offset/rotation, entity-model scale `0.5`, thaumometer ENTITY scale `0.5`, then raw TEISR rendering without its normal item-display basis adapter.
- Keep the ordinary input-item ground path separate; do not use it for the scanner.

## Current Checkpoint
- Target: complete.
- Result: R1-R3 verified; scanner source bounds are horizontal and centered at world Y `1.05..1.10`, with the processed item rendered independently inside the aperture.
- Known limitation: Automated server/static checks cannot replace a live client screenshot check of the final GL pose.

## Checkpoint History
- 2026-07-23: Replaced the shared `GROUND` route with separate scanner/input paths. The scanner now receives the TC4 table/frame/entity transforms and bypasses the item JSON X rotation and ordinary item basis adapter.
- 2026-07-23: Added a table-specific raw scanner entry point without changing normal thaumometer GUI, held, ground, fixed, or HUD behavior. Effective scanner scale is `0.25`; source-derived bounds are X `0.15..0.85`, Y `1.05..1.10`, Z `0.1969..0.8031`.
- 2026-07-23: Focused tests and build passed. The first server smoke stopped on the retained development world's already-missing `thaumcraft:focuspouchbauble`; a clean-world validation passed and the original world was restored unchanged.
