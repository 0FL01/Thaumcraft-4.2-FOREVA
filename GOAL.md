# Goal: Restore item status tooltips in custom interfaces

Status: in progress
Source: User-approved RECON plan from 2026-07-23; TC4 bytecode, TC6 donor behavior, and Forge 1.12.2 GUI lifecycle
Last updated: 2026-07-23

## Objective
Restore complete standard item-property tooltips in Thaumcraft-specific interfaces, including Focus Pouch, Arcane Bore, synthetic recipe outputs, and the research recipe viewer, without changing container behavior or unrelated visuals.

## Execution Directive
Implement the approved correction iteratively. Each iteration must remain deployable, receive focused regression coverage and a build, and end in a scoped commit. Finish with server smoke validation, a final build, and a manual client acceptance matrix where practical.

## Required Outcomes
- R1: Every direct `GuiContainer` screen renders standard slot tooltips exactly once after `super.drawScreen`.
  - Acceptance: Hovered non-empty machine, player, locked, and ghost-filter slots use `renderHoveredToolTip`; carried-stack suppression and existing custom-tooltip precedence remain intact.
  - Status: verified
  - Evidence: All 15 direct container screens now use the standard post-container path exactly once; the complete headless source guard and iteration build passed.
- R2: Manually rendered container results receive stack-aware tooltips.
  - Acceptance: The Thaumatorium recipe output and Arcane Workbench insufficient-vis ghost result use `renderToolTip` without duplicating real-slot tooltips.
  - Status: planned
- R3: Research recipe item tooltips use the standard stack-aware rendering path.
  - Acceptance: Item entries preserve their existing hitboxes while gaining standard rarity formatting, advanced-tooltip behavior, Forge render hooks, and scanned-aspect overlays; aspect-only tooltips remain unchanged.
  - Status: planned
- R4: Keep the correction deployable and regression-resistant.
  - Acceptance: A headless-safe source guard audits every direct `GuiContainer`; focused tests, build, final server smoke, and scoped commits pass.
  - Status: in progress

## Constraints
- Preserve Java 8, Forge 1.12.2, GUI/packet/registry ids, container interactions, slot validity, NBT, and original assets.
- Use the standard `renderHoveredToolTip`/`renderToolTip` paths rather than hand-building item tooltip text.
- Do not add a shared GUI superclass or `drawDefaultBackground`; avoid unrelated hierarchy and backdrop changes.
- Preserve bespoke control/aspect/research tooltips and render them after standard slot tooltips where applicable.
- Keep research-node icons and the focus radial HUD outside this correction.
- Keep `thaumcraft_src/**`, original jars, and donor artifacts read-only.
- Do not include unrelated working-tree changes.

## Material Decisions
- The defect is a 1.7.10-to-1.12.2 lifecycle mismatch: target `GuiContainer.drawScreen` computes `hoveredSlot` but does not render its tooltip.
- Apply the established local/TC6 pattern per screen instead of changing the class hierarchy.
- Treat manually rendered ItemStacks separately because `renderHoveredToolTip` can only discover real slots.
- Ensure each item tooltip is rendered exactly once so Forge tooltip events and Thaumcraft's aspect overlay are not duplicated.

## Iterations
1. Restore standard slot tooltips in all 12 affected container screens, correct Arcane Bore status-label spacing, and add a complete source guard.
2. Restore synthetic-result tooltips in Thaumatorium and Arcane Workbench.
3. Move GuiResearchRecipe item entries to the standard stack-aware tooltip path and complete final validation.

## Current Checkpoint
- Target: iteration 2.
- Iteration 1 restored standard slot tooltips in all 12 affected screens and original Arcane Bore status-label spacing.
- Focused source guard and `./scripts/dev.sh build` passed.
- Known limitation: server/static checks cannot verify final GL tooltip appearance; interactive client checks remain the visual authority.

## Checkpoint History
- 2026-07-23: User approved the three-iteration RECON plan.
- 2026-07-23: Iteration 1 restored the standard Forge item-tooltip path across all direct container screens and added complete headless regression coverage.
