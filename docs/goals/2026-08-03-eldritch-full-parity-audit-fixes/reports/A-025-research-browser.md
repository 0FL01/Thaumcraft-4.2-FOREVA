# Audit Packet: A-025 - Eldritch Research Browser

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes  
Assignment-ID: A-025  
Status: complete  
Report-Revision: 1  
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Compare the Forge 1.12.2 `GuiResearchBrowser` Eldritch category with exact Thaumcraft 4.2.3.5 behavior: category unlock visibility; hidden, concealed, lost, and stub states; graph nodes, edges, and hidden parents; pan bounds and coordinates; icons, highlights, tooltips; click and purchase workflow; clue and discovery states; aspect-cost affordance; and opening recipe pages.
- Anti-scope: Research manager/server correctness and research-page rendering internals. Those are cited only where their client-visible contract is necessary to explain the browser result.
- Oracle and comparison direction: CFR 0.152 decompilation of `Thaumcraft-1.7.10-4.2.3.5.jar` and `thaumcraft_src/**` -> current `src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java` and directly relevant client/sync code.
- Read/write permissions: Product and central ledger files read-only; only this report was writable.
- Stop condition: Every inspected browser surface is recorded as a verified discrepancy, positive-parity control, or bounded manual-validation gap.

## Evidence Base

- Current browser: `src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java`.
- Eldritch declarations: `src/main/java/thaumcraft/common/config/research/ConfigResearchEldritch.java` and `ConfigResearch.java`.
- Directly relevant current client state/sync: `PacketSyncResearch.java`, `PacketResearchComplete.java`, `ResearchManager.java`, `ResearchItem.java`, `ResearchCategories.java`, `AspectList.java`, and `GuiResearchRecipe.java`.
- Original exact methods decompiled with CFR: `GuiResearchBrowser`, `ConfigResearch`, `UtilsFX`, `ResearchManager`, `PacketSyncResearch`, `PacketResearchComplete`, and `AspectList` from TC4 4.2.3.5.
- Supporting 1.12 behavior: local `RenderItem` decompilation and current `UtilsFX`.
- Asset checks: `gui_research.png`, `gui_researchbackeldritch.png`, `nodes.png`, and the Thaumcraft particle sheet were compared against the original resources.

## Ranked Findings

### A-025-F01 - Dedicated-client Eldritch tab gate can remain invisible after research sync

- Type: defect
- Severity: high
- Confidence: high
- Current evidence: `GuiResearchBrowser.java:246,482,695` gates the Eldritch category with `ResearchManager.isResearchComplete(this.player, "ELDRITCHMINOR")`. The username overload reads the server/player-data cache (`ResearchManager.java:114-120,205-225`). `PacketSyncResearch.java:55-64` and `PacketResearchComplete.java:41-48` update the client player capability, not that cache.
- TC4 evidence: CFR `PacketSyncResearch` calls the client research manager before the browser map is populated (`:65-68`); CFR `ResearchManager` uses the synchronized client research state (`:389-401`).
- Exact delta: port category visibility consults a username/server-data path while client sync populates capability state; TC4 browser visibility consults the synchronized client manager state.
- Player impact: On a dedicated server, a player who is pre-granted or newly earns `ELDRITCHMINOR` can receive the research but still see no Eldritch tab or cannot select it until a separate state refresh.
- Manual smoke: Dedicated client/server with `ELDRITCHMINOR` absent, pre-granted before opening the browser, and granted while the browser is closed/open. Verify tab appearance and selection after sync and reconnect.
- Test gap: No automated dedicated-client GUI smoke was run.
- Candidate disposition: required.

### A-025-F02 - Research tooltip height and column widths diverge from TC4

- Type: defect
- Severity: high
- Confidence: high
- Current evidence: `GuiResearchBrowser.java:561-576` adds primary, secondary, and warp increments directly to `tooltipHeight`, then uses the enlarged value for the description and subsequent rows at `:579-633`. Cost/description widths use `/2` at `:560,564,568,638`.
- TC4 evidence: CFR `GuiResearchBrowser:548-567` keeps base text height separate from the extra-height accumulator and positions rows at `:569-628`; the original width divisions are `/1.9`, with missing-tooltip layout using `/1.5`.
- Exact delta: The port applies accumulated extra row height to later vertical layout instead of the TC4 base-height calculation. The port also uses `/2` where TC4 uses `/1.9` and `/1.5`.
- Player impact: Primary, secondary, and warped research tooltips can have displaced descriptions/cost rows, narrower text wrapping, and backing rectangles that do not contain the content. Secondary aspect costs are the largest visible displacement (29 px); primary and warp rows add 9 px each.
- Manual smoke: Inspect an incomplete primary-cost research, `PRIMNODE` with sufficient and insufficient aspects, and a completed warped node. Compare row order, wrapping, and tooltip backing against TC4.
- Test gap: No screenshot or automated pixel/layout test was run.
- Candidate disposition: required.

### A-025-F03 - Forbidden/warp aura uses the wrong quad size and UV animation

- Type: defect
- Severity: medium
- Confidence: high
- Current evidence: `GuiResearchBrowser.java:821-835` draws a 16x16 quad and samples V `5/8..6/8`.
- TC4 evidence: CFR `GuiResearchBrowser:802-812` calls `UtilsFX.renderAnimatedQuadStrip(80, 0.66, 32, 5, frame, ..., 0x440055)`. CFR `UtilsFX:256-270` uses the frame divided by 32 on both axes and renders the centered 80 px animated strip.
- Exact delta: port quad is 16x16 with fixed V range `0.625..0.75`; TC4 is an 80 px centered animated quad strip with size parameter `0.66`, frame count/UV divisor `32`, strip parameter `5`, and tint `0x440055`.
- Player impact: Warped/forbidden Eldritch nodes show a small or incorrectly sampled aura instead of TC4's large animated purple effect.
- Manual smoke: Screenshot/observe `OCULUS`, `PRIMNODE`, `CAP_void`, `FOCUSPRIMAL`, and `ROD_primal_staff` while the animation advances.
- Test gap: No live client visual smoke was run.
- Candidate disposition: required.

### A-025-F04 - Locked item-backed research icons are not dimmed

- Type: defect
- Severity: medium
- Confidence: high
- Current evidence: `GuiResearchBrowser.java:426-428` applies a GL color for locked rendering but does not control `RenderItem.renderWithColor`. Local 1.12 `RenderItem.renderItemModelIntoGUI` resets GL color to white (`RenderItem` CFR `:333-345`). Resource icons have an explicit darkening path; item-backed icons do not.
- TC4 evidence: CFR `GuiResearchBrowser:417-421` sets `RenderItem.renderWithColor=false` while drawing a locked item and restores it at `:461-463`.
- Exact delta: TC4 disables item render color for locked icons; the port relies on a GL color state that the 1.12 item renderer overwrites.
- Player impact: Locked item-backed Eldritch nodes can appear bright/full-color rather than visually unavailable. Representative affected icons are `VOIDMETAL`, `ADVALCHEMYFURNACE`, and `ESSENTIARESERVOIR`.
- Manual smoke: Open the browser with each representative research locked and compare item icon luminance with resource-backed locked nodes and completed nodes.
- Test gap: No live item-render smoke was run.
- Candidate disposition: required.

### A-025-F05 - Completion highlight binds vanilla particle sheet instead of Thaumcraft's sheet

- Type: defect
- Severity: low
- Confidence: high
- Current evidence: `GuiResearchBrowser.java:51` defines `new ResourceLocation("textures/particle/particles.png")`, used at `:435` and `:505`.
- TC4 evidence: CFR `GuiResearchBrowser:428,502` uses `ParticleEngine.particleTexture`, which resolves to `thaumcraft:textures/misc/particles.png`.
- Exact asset evidence: port Thaumcraft sheet is 256x256, SHA-256 `1fb548c3bc2bb99e7a1472c32814560cc0d637c9ba59ebb20314a251a37ae9b3`; vanilla extracted sheet is 128x128, SHA-256 `c66a9868209b3e3b47782628b1158f69ddbce727e02a6248d9cecde6e0f4935a`. The port contains the exact copied Thaumcraft asset but does not bind it here.
- Player impact: Completed-node/tab sparkle uses different artwork and atlas coordinates, producing a visibly wrong completion highlight.
- Manual smoke: Complete or inspect a completed Eldritch node and category tab while the highlight animation is active; compare sprite shape and animation with TC4.
- Test gap: No live client visual smoke was run.
- Candidate disposition: required.

## Verified Parity Controls

- All 16 Eldritch entries match TC4 keys, coordinates, complexity, aspect costs, ordinary/hidden parents, and flags.
- Hidden, concealed, lost, stub, round, secondary, and special assignments match the original registration.
- Graph edge filtering, cross-category edge suppression, hidden-parent prerequisite handling, and sibling handling match TC4.
- Eldritch graph extents are columns `-5..6` and rows `-3..6`; pan formulas and map-coordinate preservation match TC4.
- `@KEY` clue reveal and completed/hidden/lost node visibility logic match TC4.
- Direct purchase versus note workflow, aspect sufficiency checks, packet types `0/1`, purchase popup, and completed-node transition to recipe page `0` match TC4.
- Category icon, GUI frame, Eldritch background, and node texture assets match the original checked resources. The GUI/background/node asset hashes were equal to their TC4 counterparts.
- Recipe-page rendering internals and server/manager correctness were excluded from this packet.

## Validation And Smoke Requirements

- Completed: exact CFR method comparison; current-source inspection; targeted asset hash and dimension checks; clean-worktree check before report creation.
- Not run: product build, server smoke, automated client smoke, or manual in-game visual validation. This was a read-only audit and no product code was changed.
- Required before claiming visual/interaction closure: dedicated-client gate smoke for F01, tooltip cases for F02, warp aura screenshots for F03, locked item icon checks for F04, and completion-highlight screenshots for F05.
- Residual gap: Source-level evidence verifies the deltas, but final visual placement, GL-state interaction with surrounding renderers, and dedicated-client timing still require runtime observation.
