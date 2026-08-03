# Audit Packet: A-026 - Research-Page Rendering and Typed Lookup

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Assignment-ID: A-026
Status: complete
Report-Revision: 1
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Read-only comparison of `GuiResearchRecipe`, typed recipe lookup, page dispatch/navigation, markup, image assets, recipe cycling, recipe rendering, aspect/vis/input/output display, tooltips, scale, and mouse hitboxes for every page type used by the 16 Eldritch researches.
- Declaration corpus: `ELDRITCHMINOR`, `ELDRITCHMAJOR`, `OCULUS`, `ENTEROUTER`, `OUTERREV`, `PRIMPEARL`, `PRIMNODE`, `ADVALCHEMYFURNACE`, `PRIMALCRUSHER`, `VOIDMETAL`, `ESSENTIARESERVOIR`, `CAP_void`, `ARMORVOIDFORTRESS`, `FOCUSPRIMAL`, `SANITYCHECK`, and `ROD_primal_staff`.
- Anti-scope: Recipe definitions and the research browser graph. No product, central-ledger, or reference-source edits were made.
- Oracle: `Thaumcraft-1.7.10-4.2.3.5.jar`, SHA256 `3dba9786966974701578a658d1bb369bf35bdf5f363079f5ac9c4910a39113be`, decompiled with `/usr/local/bin/cfr`.

## Coverage and Parity

- Port surfaces: `src/main/java/thaumcraft/client/gui/GuiResearchRecipe.java`, `src/main/java/thaumcraft/api/research/ResearchPage.java`, `src/main/java/thaumcraft/common/config/research/ConfigResearchEldritch.java`, typed recipe lookup and `InventoryUtils`.
- Decompiled oracle surfaces: TC4 `GuiResearchRecipe`, `TCFontRenderer`, `UtilsFX`, `ResearchPage`, `InventoryUtils`, and `ConfigResearch`.
- Direct page types exercised: text, crucible, normal crafting, arcane crafting, infusion crafting, and compound crafting. No target declaration directly uses image, aspects, smelting, or infusion-enchantment pages. `ELDRITCHMAJOR` does use inline `<IMG>` markup.
- Typed handles observed: 9 normal, 2 crucible, 3 arcane, 9 infusion, and 1 compound. Typed lookup semantics match the TC4 handles; focused lookup and static guards pass.
- `<BR>`, `<BR/>`, `<LINE>`, and `<LINE/>` parsing broadly follows TC4. No target Eldritch text uses `<LINE>`.
- Target image paths and case match TC4. `eldritchajor1.png` and `eldritchajor2.png` match the source assets byte-for-byte; the lowercase `ajor` spelling is intentional and correct. Both are 256x256 RGBA.
- Crucible and infusion positions, instability, aspect quantities, component circles, inputs/outputs, and ordinary item tooltips otherwise match the inspected TC4 paths.

## Ranked Findings

### A-026-F01 - Arcane vis row truncates one required aspect

- Type: parity_bug
- Severity: high
- Confidence: high
- Affected page: `FOCUSPRIMAL` page 2.
- Observed: The port calls `drawAspectCostRow` with `perRow = 5` and computes `visible = Math.min(perRow, aspects.size())` (`GuiResearchRecipe.java:293, 500-515`). The page has six vis aspects, each costing 25, so only five 25-vis entries are drawn: displayed total `125` instead of the required `150` and one aspect icon is absent.
- TC4 evidence: Decompiled `GuiResearchRecipe.java:545-560` iterates the full aspect collection for the row; it does not cap the row at five.
- Expected parity: Draw all six 25-vis aspects and the complete 150-vis requirement using the TC4 placement behavior.
- Impact: The player sees an incomplete arcane crafting requirement and an incorrect apparent vis total.
- Test gap: Existing tests validate lookup/page wiring and static renderer guards, but do not count rendered aspect entries or totals.
- Manual command/gap: Run the client and open Eldritch `FOCUSPRIMAL` page 2; compare all six aspect icons and the 150 vis requirement against TC4. No client smoke was run for this read-only audit.

### A-026-F02 - Recipe click-through history and return navigation are absent

- Type: parity_bug
- Severity: medium
- Confidence: high
- Affected pages: `OCULUS` page 2; `ADVALCHEMYFURNACE` pages 2 and 4; `PRIMALCRUSHER` page 2; `VOIDMETAL` pages 2 and 4-12; `ESSENTIARESERVOIR` page 2; `CAP_void` pages 2-3; `ARMORVOIDFORTRESS` pages 2-4; `FOCUSPRIMAL` page 2; `SANITYCHECK` page 2; `ROD_primal_staff` page 2.
- Observed: The port renders ordinary tooltips (`GuiResearchRecipe.java:818-853`) but has no recipe history/reference lookup, click-through handling, or return control and no corresponding `recipe.clickthrough`/`recipe.return` behavior.
- TC4 evidence: Decompiled `GuiResearchRecipe.java:407-413` adds the click-through tooltip; `1059-1087` calls `ThaumcraftApi.getCraftingRecipeKey`, stores history, and navigates; `1240-1275` renders and handles the recipe return control. The port API still exposes `ThaumcraftApi.getCraftingRecipeKey` (`ThaumcraftApi.java:177-214`).
- Expected parity: Hovering a resolvable recipe should expose the TC4 click-through action, open the referenced research, preserve history, and provide the return action.
- Impact: Cross-research recipe navigation is unavailable even though the lookup API remains present.
- Test gap: No navigation/history or tooltip-action test exists.
- Manual command/gap: Run the client, hover and click each affected recipe output/input, then use the return control and verify the original page is restored. No client smoke was run.

### A-026-F03 - Compound ground overlay is 39 pixels too low and loses depth offset

- Type: parity_bug
- Severity: medium
- Confidence: high
- Affected page: `ADVALCHEMYFURNACE` page 4.
- Observed: The port uses `drawOverlayScaled` at `y + 78 + max(3-dx,3-dz)*8 + dx*4 + dz*4` (`GuiResearchRecipe.java:404`). For the page's `dx=3,dz=3`, this is `y + 102`, and the expression has no `dy` contribution.
- TC4 evidence: The decompiled compound transform applies `y + 108 + yoff*(1-sz)`, then `-119 + max(3-dx,3-dz)*8 + dx*4 + dz*4 + dy*50` at scale 1 for `dy <= 3`; the resulting ground overlay is `y + 63` for this structure. The port is therefore `+39` pixels relative to TC4 at the ground position and cannot reproduce the vertical layer offset.
- Expected parity: Use the TC4 compound transform and ground-overlay coordinate, including layer/depth displacement.
- Impact: The furnace structure's ground/backdrop overlay overlaps the rendered structure incorrectly.
- Test gap: No coordinate assertion or screenshot comparison covers compound overlays.
- Manual command/gap: Run the client and inspect `ADVALCHEMYFURNACE` page 4 at each compound layer; compare ground and structure alignment with TC4. No client smoke was run.

### A-026-F04 - Normal workbench recipes omit the 16x16 element overlay

- Type: parity_bug
- Severity: medium
- Confidence: high
- Affected pages: all nine `VOIDMETAL` normal recipes, pages 4-12.
- Observed: The port draws only the normal workbench backing at `GuiResearchRecipe.java:254`; it does not draw the second 16x16 element overlay.
- TC4 evidence: Decompiled normal-workbench rendering draws the 52x52 backing and a 16x16 overlay using UV/source position `(20,12)` (`GuiResearchRecipe.java:672-680`). `(20,12)` is the normal-workbench coordinate; `(20,7)` belongs to the arcane path and is not the expected value here.
- Expected parity: Draw both the 52x52 backing and the 16x16 `(20,12)` element overlay for each normal workbench recipe.
- Impact: Every affected VOIDMETAL workbench presentation is visually incomplete.
- Test gap: No overlay-presence or screenshot test covers normal recipe rendering.
- Manual command/gap: Run the client and inspect all nine `VOIDMETAL` pages for the missing 16x16 element overlay against TC4. No client smoke was run.

### A-026-F05 - Ordinary research text is lighter than TC4

- Type: parity_bug
- Severity: low
- Confidence: high
- Affected pages: all text pages in the 16 Eldritch researches.
- Observed: The port passes `0x303030` for ordinary lines in `drawMarkupText` (`GuiResearchRecipe.java:568`).
- TC4 evidence: Decompiled `GuiResearchRecipe.drawTextPage` passes color `0`; TC4 `TCFontRenderer` normalizes color 0 to opaque black (`GuiResearchRecipe.java:1232-1237`, `TCFontRenderer.java:396-410`).
- Expected parity: Ordinary research text should render opaque black, equivalent to TC4 color `0`, rather than `0x303030`.
- Impact: All ordinary research prose is slightly lighter than the original; markup layout is otherwise broadly aligned.
- Test gap: No pixel/color assertion exists for research text.
- Manual command/gap: Run the client and compare representative text pages in the Eldritch category at native GUI scale with TC4. No client smoke was run.

### A-026-F06 - Navigation arrows and arcane output hover hitboxes differ

- Type: parity_bug
- Severity: low
- Confidence: high
- Affected navigation pages: multi-spread recipe researches `OCULUS`, `ADVALCHEMYFURNACE`, `PRIMALCRUSHER`, `VOIDMETAL`, `ESSENTIARESERVOIR`, `CAP_void`, and `ARMORVOIDFORTRESS`.
- Affected arcane output pages: `ADVALCHEMYFURNACE` page 2, `CAP_void` page 2, and `FOCUSPRIMAL` page 2.
- Navigation observed: The port accepts 12x8 arrow ranges, left `left-16..left-4` and right `right+262..right+274` (`GuiResearchRecipe.java:104-120`), and renders fixed arrows without TC4 bobbing or page sound (`:150-157`).
- Navigation TC4 evidence: Decompiled hitboxes are 14x10 at the TC4 left/right positions, including the bobbing offset and page-change sound behavior.
- Arcane output observed: The port tests the output icon at `y+22..y+38`. TC4 draws the output at `y+22` but tests hover at `y+27..y+43` (decompiled `GuiResearchRecipe.java:567,572-574`), a 5-pixel vertical shift.
- Expected parity: Preserve the TC4 arrow dimensions/positions and feedback, and use the TC4 arcane output hover rectangle.
- Impact: Edge clicks can fail or activate at different coordinates; output tooltips can be unavailable over the same pixels where TC4 shows them.
- Test gap: No boundary hitbox, bobbing, sound, or tooltip-hover test exists.
- Manual command/gap: Run the client, click each arrow on its exact border, and hover arcane outputs across the five-pixel boundary. No client smoke was run.

### A-026-F07 - Inline Eldritch image placement and flow differ by fractional pixels

- Type: parity_bug
- Severity: low
- Confidence: high for the arithmetic delta; low for user-visible severity.
- Affected pages: `ELDRITCHMAJOR` pages 1-2, which use inline `<IMG>` markup.
- Observed: The port rounds the half-scale 255x255 image to `128x128` with `Math.round`, centers it as `x + (PAGE_WIDTH - drawWidth)/2`, and advances flow from the rounded image bottom.
- TC4 evidence: Decompiled `TCFontRenderer` uses a 0.5 scale, retaining a 127.5 pixel logical size; its x formula is `par2 - 3 + width/2 - sourceWidth/2*scale`, and its vertical advance is `sourceHeight*scale - FONT_HEIGHT`. The port omits the `-3` x term and rounds the dimensions.
- Expected parity: Preserve TC4's fractional half-scale and x adjustment before advancing the text flow.
- Delta: The image is approximately 2.5 pixels to the right in the port; subsequent vertical flow differs by about 1 pixel because the port advances from 128 rather than 127.5.
- Impact: Inline research art and following text can be subtly displaced, potentially affecting line placement at page boundaries.
- Test gap: No pixel-level image geometry or text-flow test exists.
- Manual command/gap: Run the client at the supported GUI scales and compare both `ELDRITCHMAJOR` pages to TC4 screenshots or a pixel capture. No client smoke was run.

### A-026-F08 - Damageable wildcard cycling includes invalid max-damage value

- Type: parity_bug
- Severity: low
- Confidence: high.
- Affected page: `PRIMALCRUSHER` page 2, for the wildcard pickaxe/shovel components, including void and elemental variants.
- Observed: The port cycles damageable wildcard metadata with `System.currentTimeMillis()/10 % (it.getMaxDamage()+1)` (`InventoryUtils.java:472-475`), producing values `0..maxDamage`.
- TC4 evidence: Decompiled `InventoryUtils.cycleItemStack` uses modulus `maxDamage` (`InventoryUtils.java:490-495`), producing only `0..maxDamage-1`.
- Expected parity: Use the exclusive upper bound `maxDamage`; never display the invalid metadata value equal to maximum damage.
- Impact: A timing-dependent display frame can show an invalid damage value or an incorrect wildcard item appearance.
- Test gap: Existing wildcard guards do not exercise the exact `maxDamage` boundary and no deterministic cycling test covers damageable items.
- Manual command/gap: Run the client while observing the wildcard tool components over repeated cycle boundaries, or add a deterministic boundary test before product work. No client smoke was run.

## Validation and Runtime Gap

- Command run: `./scripts/dev.sh gradle test --tests thaumcraft.common.config.ConfigResearchRecipeLookupTypeAuditTest --tests thaumcraft.common.config.ConfigResearchStrictRecipeLookupStaticGuardTest --tests thaumcraft.client.GuiResearchRecipeStaticGuardTest --tests thaumcraft.client.ResearchRecipeWildcardDisplayStaticGuardTest --tests thaumcraft.common.clientguard.ResearchRecipeItemTooltipStaticGuardTest`
- Result: `BUILD SUCCESSFUL`.
- Worktree result before report creation: product/reference sources were unchanged; this report is the only requested audit artifact.
- Runtime smoke: Not required for a read-only audit and not run. Client visual/manual smoke was skipped; the exact manual checks are listed per finding.

## Index

| Index | Finding | Severity | Affected surface |
|---|---|---|---|
| F01 | Arcane vis row truncates six 25-vis aspects to five | high | `FOCUSPRIMAL` p2 |
| F02 | Recipe click-through/history/return absent | medium | Listed recipe pages across 11 researches |
| F03 | Compound ground overlay is `+39` px and lacks `dy` | medium | `ADVALCHEMYFURNACE` p4 |
| F04 | Missing 16x16 `(20,12)` workbench overlay | medium | `VOIDMETAL` p4-p12 |
| F05 | Text `0x303030` versus TC4 opaque black | low | All Eldritch text pages |
| F06 | Arrow and arcane output hitbox deltas | low | Multi-spread and three arcane pages |
| F07 | Inline image approximately `+2.5` px x and `+1` px flow delta | low | `ELDRITCHMAJOR` p1-p2 |
| F08 | Wildcard modulus includes `maxDamage` | low | `PRIMALCRUSHER` p2 |
