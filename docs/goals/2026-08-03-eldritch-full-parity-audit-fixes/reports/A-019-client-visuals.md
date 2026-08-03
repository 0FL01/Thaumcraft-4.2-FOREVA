# Audit Packet: A-019 - Eldritch, Primal, Void, and Device Client Visuals

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Assignment-ID: A-019
Status: complete
Report-Revision: 1
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Audit client rendering, model routing, textures, texture stitching, display transforms, render-distance/LOD behavior, and OpenGL state for Eldritch, Primal, Void, and directly relevant device outputs against Thaumcraft 4.2.3.5.
- Anti-scope: Product fixes; central Goal Ledger edits; the nine research-only PNGs covered by A-005; unrelated gameplay, recipes, progression, registries, and server behavior except where a tile render-distance override directly controls a visual output.
- Oracle and comparison direction: S-003/S-004 bundled Thaumcraft 4.2.3.5 classes/assets -> S-005 Forge 1.12.2 port. S-006 Thaumcraft 6 may inform 1.12 display conventions, but it does not override TC4 visual behavior.
- Questions: Are TC4 field coordinates, parallax inputs, distance thresholds, fallback modes, persistent geometry, animated overlays, texture-stitch contracts, lightmap state, item variants, assets, and item transforms preserved after the 1.12 adaptation?
- Expected evidence: Direct current-source and asset inspection; CFR 0.152 decompilation and `javap` of matching TC4 classes; exact numeric thresholds and state operations; existing guard-test inspection; a manual client smoke matrix for visual claims.
- Read/write permissions: Product and central ledger files read-only; only this report writable.
- Stop conditions: Every inspected visual surface is either an atomic defect, an explicit positive-parity control, or a bounded unknown requiring manual client observation; no product edit; no unsupported visual-parity claim.
- Continuation predecessor: none.

## Coverage Performed

- Shared field rendering: `LayeredFieldPlaneHelper`, Portable Hole, linked Mirror, Eldritch Nothing, Eldritch Lock, and Eldritch Obelisk, including all six face orientations where implemented, layered/fallback textures, camera inputs, thresholds, and tile dispatch ranges.
- Primal outputs: Primal Arrow, Primal Orb, Primal Focus mounted rendering, Primal Crusher, primal wand/staff rod models, and relevant textures.
- Eldritch outputs: Orb, portal, obelisk/caps, lock/key, Nothing, crystals, crab spawner, guardian, warden, golem, crab, and Item Eldritch metadata model routes.
- Void outputs: ordinary Void armor/tools, the three TC4 Void Robe pieces, Void caps/rods, and the port-only Void Robe boots.
- Devices and item routes: tube valve/one-way/buffer, centrifuge/crystalizer handling, tables, Runic Matrix, Thaumatorium, Essentia Crystalizer, Vis Relay, Node Stabilizer/Converter/Energized Node, and representative metal/stone/wood device TEISR or baked-model paths.
- Assets: scoped port/oracle texture trees and exact paths/case. The nine A-005 research-only PNGs were deliberately excluded from this packet.
- Existing tests: client renderer registration and fidelity guards for arrows/orbs, mirrors, layered fields, Hole/Nothing/Obelisk, Eldritch TESR routing, tables, and Pech Focus depth rendering.
- Uncovered validation: no live client was launched. Static evidence establishes code/asset deltas and required smoke cases, not final visual appearance or performance.

## Threshold and Range Summary

All distance values are squared distances unless a linear distance is shown in parentheses.

| Surface | TC4 field/full-detail threshold | Port field/full-detail threshold | TC4 tile dispatch range | Port tile dispatch range | Observable delta |
|---|---:|---:|---:|---:|---|
| Portable Hole | `< 512` (`< 22.627`) | always full detail | inherited/default | inherited/default | No `particlefield32.png` fallback at distance |
| Eldritch Nothing | `< 512` (`< 22.627`) | `< 144` (`< 12`) | no custom override | `256` (`16`) | Falls back too early, then disappears at 16 |
| Eldritch Lock field | `< 512` (`< 22.627`) | `< 1024` (`< 32`) | `9216` (`96`) | `2304` (`48`) | Layered field persists too far; whole TESR stops too early |
| Eldritch Lock rings/key | always while TESR is dispatched | only `< 1024` (`< 32`) | `9216` (`96`) | `2304` (`48`) | Rings/key vanish at 32 instead of remaining to 96 |
| Eldritch Obelisk field | `< 512` (`< 22.627`) | `< 9216` (`< 96`) | `9216` (`96`) | `20736` (`144`) | Expensive layered field persists to 96 |
| Eldritch Obelisk shell/caps | always while TESR is dispatched | only `< 9216` (`< 96`) | `9216` (`96`) | `20736` (`144`) | Port-only 96-144 field-only band; shell/caps absent |
| Linked Mirror portal | `UtilsFX.isVisibleTo(1.5f, ...)`, near `< 2`, practical cap `< 400`, FOV/third-person aware | direct `<= 64` (`<= 8`) | normal TESR dispatch | normal TESR dispatch | Linked mirror becomes opaque beyond 8 |

## Atomic Findings

### A-019-F01 - Shared layered fields collapse relative face and world-camera coordinates

- Type: defect
- Severity: high
- Confidence: high
- Source/oracle locator: S-005 `src/main/java/thaumcraft/client/renderers/tile/LayeredFieldPlaneHelper.java:36-65,99-100,121-189`; callers `TileHoleRenderer.java:26-59`, `TileMirrorRenderer.java:49-61`, `TileEldritchNothingRenderer.java:28-55`, and `TileEldritchObeliskRenderer.java:41-54,101-127`; S-003/S-004 matching TC4 renderers decompiled with CFR.
- Observed: Callers pass TESR-relative face coordinates (`x`, `y`, `z`) and separately pass interpolated world-space entity positions (`viewX`, `viewY`, `viewZ`). `setupTexGen` subtracts those world coordinates directly from planes formed from TESR-relative coordinates, for example `plane = y + offset`, `depth = plane - cameraY`, and equivalent X/Z formulas. The same world values are used for texture-matrix camera translations.
- Expected: TC4 keeps two distinct inputs: `TileEntityRendererDispatcher.field_147554_b/field_147555_c/field_147552_d` for the dispatcher camera translations and `ActiveRenderInfo.field_74592_a/field_74590_b/field_74591_c` for projected camera/object coordinates in the plane-depth and parallax formulas. A 1.12 port must preserve equivalent coordinate spaces instead of feeding one interpolated world position into both roles.
- Exact delta: Separate dispatcher-camera and projected-camera contracts -> one `viewX/Y/Z` contract; TESR-relative plane minus world-space camera position. The error magnitude therefore depends on world origin and sign rather than only viewer-to-plane separation.
- Effect: Layer origin, scale, and parallax can drift or distort with world coordinates and camera motion. The shared helper propagates the defect to Hole, linked Mirror, Nothing, and Obelisk fields on every routed face.
- Regression hazards: Preserve TC4 texture set, 16 layers, seed `31100L`, first-layer tunnel values (`65`, `0.125`, shade `0.1`, alpha blend), second-layer scale `0.5`, additive later layers, face winding, merged Hole rectangles, and 1.12 matrix cleanup while separating the coordinate roles.
- Candidate disposition: required.

### A-019-F02 - Shared layered fields substitute view-rotation coefficients for projected camera coordinates

- Type: defect
- Severity: high
- Confidence: high
- Source/oracle locator: S-005 `LayeredFieldPlaneHelper.java:191-232`; S-003/S-004 per-face texture-matrix formulas in `TileHoleRenderer.class`, `TileMirrorRenderer.class`, `TileEldritchNothingRenderer.class`, and `TileEldritchObeliskRenderer.class`.
- Observed: The second texture-matrix translation uses `ActiveRenderInfo.getRotationX()`, `getRotationYZ()`, and `getRotationXZ()` multiplied by `layerDepth / depth`.
- Expected: TC4 uses projected camera/object coordinates: X/Y faces use `field_74592_a` and `field_74591_c`; Z faces use `field_74592_a` and `field_74590_b`; X-normal faces use `field_74591_c` and `field_74590_b`, each multiplied by layer depth and divided by plane depth. Rotation coefficients describe camera orientation, not projected position, and are not semantic replacements.
- Exact delta: Position-dependent projected components -> bounded orientation coefficients. The numerator changes type and meaning even where the coordinate-space issue in A-019-F01 is not visible near origin.
- Effect: Translating the camera and rotating it no longer produce TC4's field parallax. Motion may appear pinned, unstable, or orientation-scaled, with different errors per face axis.
- Regression hazards: Do not merely rename 1.12 rotation getters. Establish the 1.12 equivalents of the original projected coordinates and test all positive/negative face orientations.
- Candidate disposition: required.

### A-019-F03 - Primal Arrow loses the animated elemental wisp aura

- Type: defect
- Severity: high
- Confidence: high
- Source/oracle locator: S-003/S-004 `thaumcraft/client/renderers/entity/RenderPrimalArrow.class`, CFR full class; S-005 `src/main/java/thaumcraft/client/renderers/entity/RenderPrimalArrow.java:13-38`; shared asset `textures/misc/wisp.png` SHA-256 `d211675284b73d8a380335fff34794a500b0401924cd91fa053f9eaeaa35f0ed` on both sides.
- Observed: The port maps `BlockCustomOreItem.colors[arrowType + 1]`, tints the vanilla arrow geometry with `GlStateManager.color`, renders `textures/entity/arrow.png`, and restores white. It has no second aura pass.
- Expected: TC4 renders ordinary arrow geometry and then a separate camera-facing `textures/misc/wisp.png` billboard. The aura uses frame `ticksExisted % 16`, size `0.5`, tessellator brightness `240`, the type color, alpha `(100 - arrowShake) / 100`, `depthMask(false)`, additive blend `(770,1)` for types `0..4`, and alpha blend `(770,771)` for type `5`; it restores depth writes after drawing.
- Exact delta: Six arrow types retain only a flat tint on the arrow model and lose the animated, fullbright, fading aura. Type 5 also loses its distinct non-additive blend behavior.
- Effect: All six Primal Arrow variants have materially different silhouettes, animation, glow, blend, and embedded/fade appearance.
- Regression hazards: Keep vanilla arrow texture/geometry, yaw/pitch/shake transforms, type-color indexing, fade, frame layout, blend split, depth-mask restoration, and the existing byte-identical wisp asset.
- Candidate disposition: required.

### A-019-F04 - Linked Mirror visibility is reduced to an 8-block distance test

- Type: defect
- Severity: high
- Confidence: high
- Source/oracle locator: S-005 `src/main/java/thaumcraft/client/renderers/tile/TileMirrorRenderer.java:38-46,98-119`; S-003/S-004 `TileMirrorRenderer.class` and `UtilsFX.class`, CFR `isVisibleTo`.
- Observed: `isVisible` returns true only when eye-to-tile-center squared distance is `<= 64.0` (8 blocks). A linked mirror that fails this test takes the same branch as an unlinked mirror and renders opaque `mirrorpane.png` without portal layers.
- Expected: TC4 calls `UtilsFX.isVisibleTo(1.5f, player, tile center)`. That function returns true unconditionally below linear distance `2`, uses `1.5 + configuredFov/2` and first-person view angle, allows third-person views independent of facing, and applies a practical linear cap of `< 400` after clamping `512` to `400`.
- Exact delta: Near/FOV/third-person/400-block semantic visibility -> distance-only 8-block visibility. This is not a lower-detail portal path; linked state is visually replaced by the inactive opaque pane.
- Effect: Linked Mirrors look unlinked through most normal TESR viewing distances and can switch abruptly at 8 blocks.
- Regression hazards: Preserve linked/unlinked state, transparent `mirrorpanetrans.png`, instability offset, frame sprite, all six orientations, third-person behavior, and an appropriate 1.12 FOV/view implementation.
- Candidate disposition: required.

### A-019-F05 - Eldritch Nothing layered-to-fallback threshold is 12 blocks instead of sqrt(512)

- Type: defect
- Severity: high
- Confidence: high
- Source/oracle locator: S-005 `src/main/java/thaumcraft/client/renderers/tile/TileEldritchNothingRenderer.java:28-55`; S-003/S-004 `TileEldritchNothingRenderer.class`, CFR `func_147500_a` and plane methods.
- Observed: The port sets `inRange` only for squared distance `< 144.0` (linear `< 12`) and uses the helper's one-quad `particlefield32.png` fallback otherwise.
- Expected: TC4 uses squared distance `< 512.0` (linear `< 22.627416998`) for the 16-layer tunnel/particle field, then its fallback at or beyond that boundary.
- Exact delta: threshold `512` -> `144`; linear transition about `22.627` -> `12`, a reduction of about `10.627` blocks.
- Effect: Nothing changes to the low-detail field much too close to the player.
- Regression hazards: Keep strict `<` boundary semantics, 16-layer near path, one-quad fallback, all six face offsets, opacity-based face eligibility, and coordinate fixes from A-019-F01/F02.
- Candidate disposition: required.

### A-019-F06 - Eldritch Nothing TESR dispatch ends at 16 blocks

- Type: defect
- Severity: high
- Confidence: high
- Source/oracle locator: S-005 `src/main/java/thaumcraft/common/tiles/TileEldritchNothing.java:20-29`; S-003/S-004 `TileEldritchNothing.class`, `javap -p` class surface.
- Observed: The port overrides `getMaxRenderDistanceSquared()` with `256.0` (16 blocks).
- Expected: TC4 `TileEldritchNothing` has no custom max-render-distance method and therefore retains normal inherited tile rendering beyond the field LOD transition. Its renderer contains a deliberate far `particlefield32.png` path.
- Exact delta: inherited/default dispatch -> explicit `256`; the port cannot show its far fallback beyond 16 even though TC4's near/far transition is at squared distance 512.
- Effect: The port falls back at 12 and disappears at 16, while TC4 remains layered to about 22.627 and then remains visible via fallback within normal tile range.
- Regression hazards: Do not remove the fallback while restoring dispatch. Preserve the one-block render bounding box unless direct clipping evidence requires otherwise.
- Candidate disposition: required.

### A-019-F07 - Eldritch Nothing suppresses shared translucent faces between adjacent Nothing blocks

- Type: defect
- Severity: high
- Confidence: high
- Source/oracle locator: S-005 `src/main/java/thaumcraft/client/renderers/tile/TileEldritchNothingRenderer.java:45-76`; S-003/S-004 `TileEldritchNothingRenderer.class`, CFR `func_147500_a`.
- Observed: `shouldRenderFace` rejects an adjacent opaque cube and separately rejects any adjacent `BlockEldritchNothing`.
- Expected: TC4 checks only whether the adjacent block is opaque. Eldritch Nothing is nonopaque, so adjacent Nothing blocks retain their facing translucent field planes.
- Exact delta: opacity-only rejection -> opacity plus same-block rejection. A connected pair loses both shared field planes; larger 2x2/volume arrangements lose every internal same-block plane.
- Effect: Connected Nothing formations have a different layered density, depth, and interior appearance from TC4.
- Regression hazards: This is not ordinary opaque-face culling. Preserve opaque-neighbor suppression and evaluate performance changes without erasing original translucent planes.
- Candidate disposition: required.

### A-019-F08 - Eldritch Lock field uses a 32-block full-detail threshold instead of sqrt(512)

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-005 `src/main/java/thaumcraft/client/renderers/tile/TileEldritchLockRenderer.java:39-60,118-181`; S-003/S-004 `TileEldritchLockRenderer.class`, CFR `func_147500_a` and field plane methods.
- Observed: Port `inRange` is squared distance `< 1024.0` (linear `< 32`). It selects 16 field layers until 32, then one `particlefield32.png` quad.
- Expected: TC4 `inrange` is squared distance `< 512.0` (linear `< 22.627416998`) and controls only layered field versus fallback.
- Exact delta: field LOD threshold `512` -> `1024`; layered rendering persists roughly `9.373` blocks farther.
- Effect: The expensive and visually denser Lock field remains active past TC4's transition.
- Regression hazards: Treat the field threshold independently from ring/key visibility and tile dispatch. Keep field dimensions `[-2,3]`, facing, textures, and fallback.
- Candidate disposition: required.

### A-019-F09 - Eldritch Lock rings and inserted key are incorrectly gated by field LOD

- Type: defect
- Severity: high
- Confidence: high
- Source/oracle locator: S-005 `TileEldritchLockRenderer.java:42-60,63-116`; S-003/S-004 `TileEldritchLockRenderer.class`, CFR `func_147500_a`.
- Observed: The port calls `renderLockRings` and `renderInsertKey` only when squared distance is `< 1024` (32 blocks). Beyond 32 only the field fallback remains.
- Expected: TC4 always renders the animated cube rings and, when `count >= 0`, the Eldritch Object metadata-2 key whenever the TESR is dispatched. Its `< 512` `inrange` value is consulted only by the field plane methods.
- Exact delta: persistent geometry/key -> geometry/key conditional on field LOD. At `>= 32`, rings and key disappear even though the renderer continues to 48 in the port.
- Effect: A visible Lock loses its defining cube structure and activation key over a 32-48 block band.
- Regression hazards: Preserve ring count/wobble, facing-axis rotation, `count` progression, key metadata `2`, insertion transform, and 1.12 entity-item rendering while decoupling geometry from field LOD.
- Candidate disposition: required.

### A-019-F10 - Eldritch Lock render distance is 48 blocks instead of 96

- Type: defect
- Severity: high
- Confidence: high
- Source/oracle locator: S-005 `src/main/java/thaumcraft/common/tiles/TileEldritchLock.java:359-369`; S-003/S-004 `TileEldritchLock.class`, CFR `func_145833_n`.
- Observed: Port `getMaxRenderDistanceSquared()` returns `2304.0` (48 blocks).
- Expected: TC4 returns `9216.0` (96 blocks).
- Exact delta: squared range `9216` -> `2304`; linear range `96` -> `48`, halved.
- Effect: All Lock visuals disappear after 48 rather than retaining rings/key and the far field through 96.
- Regression hazards: Preserve the expanded render bounding box and do not use the restored dispatch range to justify retaining the wrong full-detail threshold.
- Candidate disposition: required.

### A-019-F11 - Primal Focus depth texture exists but is neither stitched nor returned

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-003/S-004 `ItemFocusPrimal.class`, CFR icon registration and `getFocusDepthLayerIcon`; S-005 `src/main/java/thaumcraft/common/items/wands/foci/FocusPrimal.java:19-92`, `src/main/java/thaumcraft/client/ClientModelRegistry.java:44-45,73-80`, `src/main/java/thaumcraft/api/wands/ItemFocusBasic.java:87-93`, and `src/main/java/thaumcraft/client/renderers/models/gear/ModelWand.java:155-201`; asset pair `textures/items/focus_primal_depth.png`.
- Observed: The asset exists in both trees and is byte-identical with SHA-256 `7ed52bd5359ebb40cc0e3555a6c75ef5fc1609ca539af5652d1e96e5cef84f68`. `ModelWand` renders a depth cube whenever the focus returns a sprite. The port `FocusPrimal` does not override the base null method, and `ClientModelRegistry` defines/registers only `items/focus_pech_depth`, not `items/focus_primal_depth`.
- Expected: TC4 `ItemFocusPrimal` registers `thaumcraft:focus_primal_depth`, stores it, and returns it from `getFocusDepthLayerIcon`. The 1.12 equivalent should stitch `thaumcraft:items/focus_primal_depth` and return that atlas sprite, as the existing Pech Focus adaptation does.
- Exact delta: valid original depth asset and live ModelWand depth branch -> unstitched asset plus inherited null return. The mounted Primal Focus lacks its depth shell and uses ModelWand alpha `0.95` instead of the depth-present branch's `0.6` for the focus model.
- Effect: Focus mounted on a wand, sceptre, or staff appears flatter and compositionally different despite the correct asset being packaged.
- Regression hazards: Preserve the public API signature, use the block texture atlas expected by `ModelWand`, avoid recreating the asset, and do not disturb the working Pech Focus sprite.
- Candidate disposition: required.

### A-019-F12 - Portable Hole never enters TC4's distant fallback path

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-005 `src/main/java/thaumcraft/client/renderers/tile/TileHoleRenderer.java:21-59`; S-003/S-004 `TileHoleRenderer.class`, CFR `func_147500_a` and all six plane methods.
- Observed: Every merged face calls `LayeredFieldPlaneHelper.renderLayeredFaceRect(..., true, 0.5F, ...)`. The helper therefore always emits 16 tunnel/particle layers wherever the tile is dispatched.
- Expected: TC4 computes squared distance `< 512.0` (linear `< 22.627416998`) once per render. Near faces use 16 layers; far faces use one `textures/misc/particlefield32.png` quad shaded `0.5`. `TileHole` has no custom port render-distance override.
- Exact delta: strict `< 512` near/far selection -> constant `true`; deliberate far texture is unreachable from Hole.
- Effect: Distant openings retain close-range depth/animation and render cost rather than switching to TC4's stable low-detail field.
- Regression hazards: Preserve `HoleRenderBatchCache` grouping/rectangle merging, all exposed orientations, offsets `0.001/0.999`, fallback shade `0.5`, and shared helper coordinate corrections.
- Candidate disposition: required.

### A-019-F13 - Primal Orb billboard leaks fullbright lightmap state

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-005 `src/main/java/thaumcraft/client/renderers/entity/RenderPrimalOrb.java:87-118`, especially line 107; S-003/S-004 `RenderPrimalOrb.class`, CFR `renderEntityAt`.
- Observed: The billboard calls `OpenGlHelper.setLightmapTextureCoords(lightmapTexUnit, 240.0F, 240.0F)` and never captures or restores `OpenGlHelper.lastBrightnessX/Y`. Matrix and blend restoration do not restore lightmap coordinates.
- Expected: TC4's corresponding billboard does not change the lightmap. It draws using the existing state after the spike pass.
- Exact delta: no lightmap mutation -> global fullbright mutation `(240,240)` without restoration.
- Effect: A subsequently rendered mob, item, particle, or tile can inherit fullbright until another renderer changes the lightmap.
- Regression hazards: Preserve the Primal Orb's 12 spikes, elemental colors, additive blend, frame `% 13`, U/V row `0.125`, scale `0.5`, and alpha `0.8`. Either omit the non-original lightmap write or restore exact previous X/Y values.
- Candidate disposition: required.

### A-019-F14 - Eldritch Orb billboard leaks fullbright lightmap state

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-005 `src/main/java/thaumcraft/client/renderers/entity/RenderEldritchOrb.java:87-117`, especially line 106; S-003/S-004 `RenderEldritchOrb.class`, CFR `renderEntityAt`.
- Observed: The billboard sets lightmap `(240,240)` and does not capture/restore `OpenGlHelper.lastBrightnessX/Y`.
- Expected: TC4's corresponding billboard does not mutate lightmap coordinates.
- Exact delta: no lightmap mutation -> unbalanced fullbright write. This is independent of the Primal Orb because either renderer can be last in draw order.
- Effect: Subsequent draws can inherit fullbright after an Eldritch Orb.
- Regression hazards: Preserve 12-spike geometry, Eldritch color index `5`, alpha blend `(770,771)`, frame `% 13`, U/V row `0.1875`, and billboard size `1.0` while balancing or removing the write.
- Candidate disposition: required.

### A-019-F15 - Eldritch Obelisk layered field persists to 96 blocks instead of sqrt(512)

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-005 `src/main/java/thaumcraft/client/renderers/tile/TileEldritchObeliskRenderer.java:38-54,101-127`; S-003/S-004 `TileEldritchObeliskRenderer.class`, CFR `func_147500_a` and plane methods.
- Observed: Port `inRange` is squared distance `< 9216.0` (linear `< 96`) and controls 16-layer versus fallback side fields.
- Expected: TC4 uses squared distance `< 512.0` (linear `< 22.627416998`) for side-field complexity and `particlefield32.png` afterward.
- Exact delta: field LOD threshold `512` -> `9216`; the full layered field persists about `73.373` blocks farther.
- Effect: Obelisks retain close-range field animation and cost at long range rather than transitioning near 22.627 blocks.
- Regression hazards: Preserve four horizontal fields, dimensions `1x3`, offsets near `0.01/0.99` semantically, normal/Outer fallback shades, bob, and coordinate fixes.
- Candidate disposition: required.

### A-019-F16 - Eldritch Obelisk shell and caps are incorrectly gated by field LOD

- Type: defect
- Severity: high
- Confidence: high
- Source/oracle locator: S-005 `TileEldritchObeliskRenderer.java:54-85`; S-003/S-004 `TileEldritchObeliskRenderer.class`, CFR `func_147500_a`.
- Observed: The port wraps the textured side shell and both cap models in `if (inRange)`, where `inRange` is `< 9216` (96 blocks). Beyond 96 it draws only fallback fields.
- Expected: TC4 always draws the four textured shell sides and both OBJ `Cap` parts whenever the TESR is dispatched. Its `< 512` flag controls only each field plane's layered/fallback branch.
- Exact delta: persistent shell/caps -> shell/caps conditional on field detail. Port tile dispatch continues to 144, so this is observable rather than dead code.
- Effect: In the 96-144 block band the Obelisk becomes a field-only shape without `obelisk_side*.png` or `obelisk_cap*.png`, in both normal and Outer Lands texture sets.
- Regression hazards: Keep side-before-shell order, light sampled at `pos.up(5)`, lightmap restoration, bob, rescale normals, culling, both cap groups, and Outer textures while decoupling geometry from LOD.
- Candidate disposition: required.

### A-019-F17 - Eldritch Obelisk tile range is 144 blocks instead of TC4's 96

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-005 `src/main/java/thaumcraft/common/tiles/TileEldritchObelisk.java:62-71`; S-003/S-004 `TileEldritchObelisk.class`, CFR `func_145833_n`.
- Observed: Port `getMaxRenderDistanceSquared()` returns `20736.0` (144 blocks).
- Expected: TC4 returns `9216.0` (96 blocks).
- Exact delta: squared range `9216` -> `20736`; linear range `96` -> `144`, extended by 48 blocks. Combined with A-019-F16, the added band contains only fallback fields rather than full Obelisk geometry.
- Effect: The port dispatches a non-original partial TESR from 96 through 144 and incurs field rendering beyond TC4's range.
- Regression hazards: Coordinate the range correction with A-019-F15/F16; changing only the max range can hide the shell bug without fixing the incorrect LOD coupling.
- Candidate disposition: required.

### A-019-F18 - Port adds a non-original Void Robe boots item with inconsistent and empty visual paths

- Type: defect
- Severity: low
- Confidence: high
- Source/oracle locator: S-003/S-004 `thaumcraft/common/config/ConfigItems.class`, `javap -p` and CFR initialization; S-005 `src/main/java/thaumcraft/common/config/ConfigItems.java:123-126,662-684`, `src/main/resources/assets/thaumcraft/models/item/itembootsvoidrobe.json`, `src/main/java/thaumcraft/client/ClientProxy.java:760-775`, and `src/main/java/thaumcraft/common/items/armor/ItemVoidRobeArmor.java:61-94,173-190`.
- Observed: TC4 declares only `itemHelmetVoidRobe`, `itemChestVoidRobe`, and `itemLegsVoidRobe`. The port adds creative-tab `itemBootsVoidRobe` under legacy path `ItemBootsVoidRobe`, with no recipe found. Its item model uses ordinary `thaumcraft:items/voidboots`, whereas legitimate robe chest/legs models use robe overlay/base layers. The Void Robe item-color handler registers only helm/chest/legs, not boots, despite the class reporting `hasColor() == true`. For `EntityEquipmentSlot.FEET`, `getArmorModel` selects `model1` but enables none of head, body, arms, or legs, so the equipped boots have no visible model geometry.
- Expected: No fourth Void Robe piece, registry entry, creative stack, icon, dye surface, or equipped-foot output exists in TC4.
- Exact delta: piece count `3` -> `4`; added item path `thaumcraft:itembootsvoidrobe`; ordinary Void Boots icon substituted for a nonexistent robe asset; dye API without color-handler participation; empty equipped model visibility for FEET.
- Effect: The creative inventory exposes an invented item whose icon does not communicate robe dye state and whose equipped model is statically configured invisible.
- Regression hazards: Do not alter the three legitimate Void Robe IDs/models or ordinary Void Boots. Before removing the extra registry ID, implementation must follow the repository's persisted-data policy; if deliberately retained, it needs an explicit non-TC4 compatibility decision and coherent item/equipped/dye visuals.
- Candidate disposition: required subject to persisted-data policy; cross-confirmed independently by A-009-F03.

## Manual Client Smoke Matrix

Static evidence is sufficient to record the defects, but the following client observations are required before claiming the fixes visually complete. Distances should be measured from the viewer to tile center and checked immediately below, at, and above strict thresholds where practical.

| Smoke ID | Findings | Setup and matrix | Required observation/pass condition |
|---|---|---|---|
| M-019-01 | F01-F02 | Place Hole, linked Mirror, Nothing, and Obelisk near `(0,64,0)`, around `(8192,64,8192)`, and around `(-8192,64,-8192)`. Exercise every supported face orientation; strafe, move forward/back, change yaw/pitch, and repeat first/third person where applicable. | Layer origin, speed, scale, and parallax remain translation-invariant and match TC4 behavior; no sign/origin-dependent drift, snapping, inversion, or face-axis distortion. |
| M-019-02 | F03 | Spawn Primal Arrow types `0..5`. Observe in flight, on impact, embedded, and while `arrowShake` drives fade toward 100. Use dark and bright backgrounds. | Vanilla arrow geometry remains visible; 16-frame wisp aura is camera-facing, fullbright, type-colored, and fades by `(100-arrowShake)/100`; types `0..4` are additive and type `5` uses alpha blend; no depth-write leak. |
| M-019-03 | F04 | Use a linked normal Mirror and linked Essentia Mirror at 4, 8, 16, and 32 blocks. View toward and away from the face in first person; repeat front/rear third person and vary configured FOV. | Portal/transparent pane visibility follows TC4 FOV/third-person semantics rather than becoming opaque solely past 8 blocks; near `<2` remains visible. |
| M-019-04 | F05-F07 | Build a single Nothing block, connected `2x1`, `2x2`, and solid volume. Observe at 8, 12, 16, 22, about 22.627, and 24 blocks at origin and distant positive/negative coordinates. | Layered detail remains until squared distance 512, then fallback remains dispatched; adjacent Nothing planes retain TC4 interior depth; opaque neighbors still suppress hidden faces. |
| M-019-05 | F08-F10 | Observe Lock facing each horizontal direction at 22, 24, 32, 40, 48, 64, and 96 blocks. Test inactive `count < 0`, activating/active `count >= 0`, and an inserted key. | Field switches around sqrt(512); rings remain visible to the 96-block dispatch edge; key remains visible whenever `count >= 0`; no 32-block geometry loss or 48-block TESR cutoff. |
| M-019-06 | F11 | Mount Primal Focus on a wand, sceptre, ordinary staff, and primal staff. Inspect GUI/inventory presentation where mounted rendering is used, first-person main/off hand, and third-person hand. Compare with Pech Focus depth behavior. | Byte-identical primal depth sprite is stitched and forms the intended depth cube; no missing-sprite checkerboard, flat focus, wrong alpha, or staff offset regression. |
| M-019-07 | F12 | Make single-face and connected `3x3` Portable Hole openings on floor, ceiling, and walls. Observe at 16, 22, about 22.627, 24, 32, and 64 blocks at different world-coordinate signs. | Near path has TC4 layered depth; far path changes to one shade-0.5 `particlefield32.png` quad; merged rectangles do not change the threshold or create seams. |
| M-019-08 | F13-F14 | Fire Primal and Eldritch Orbs in a dark room beside dim mobs, dropped items, particles, and TESRs. Arrange camera/order so those objects render immediately after each orb; repeat with multiple orbs. | Orb visuals retain their reference appearance and no subsequent object becomes fullbright. Capture-before/set/restore behavior is stable under repeated draws. |
| M-019-09 | F15-F17 | Observe normal-dimension and Outer Lands Obelisks at 22, 24, 96, 112, and 144 blocks while moving across boundaries. | Field switches near sqrt(512); side shell and both caps remain through TC4's 96-block range; no field-only 96-144 band; normal and `_2` Outer textures remain correct. |
| M-019-10 | F18 | If the extra boots remain during adjudication, inspect creative icon undyed/dyed, tooltip, cauldron dye removal, equipped third-person feet, inventory, first-person hand, and dropped item beside ordinary Void Boots and the three robe pieces. | Expected TC4 result is no item. If intentionally retained, no invisible equipped model, ordinary-boots substitution, or ignored dye tint may be accepted silently. |

## Positive Parity and Negative Controls

### Assets and paths

- Scoped texture-tree comparison found no missing, wrong-case, or misplaced original texture required by the audited outputs. Item textures compared in scope were identical; remaining tree differences were unrelated or intentional port additions.
- `textures/items/focus_primal_depth.png` and `textures/misc/wisp.png` are present and byte-identical to TC4. The defects are wiring/rendering omissions, not missing payloads; do not recreate either asset.
- The nine research-only art files audited by A-005 were excluded here. A-005 established exact binary, path, case, dimension, alpha, declaration, and localization parity for them.
- Normal/Outer Obelisk side and cap texture selection exists. A-019-F15-F17 concern threshold/range/geometry gating, not missing texture payloads.

### Renderers with no retained evidence-backed defect

- No confirmed visual regression was retained for Primal Crusher. Its simple handheld icon behavior is consistent with TC4, and `itemprimalcrusher.json` correctly uses `item/handheld` with `thaumcraft:items/primal_crusher`.
- No confirmed visual regression was retained for ordinary Void armor or tools, or for the three TC4 Void Robe pieces. `ModelRobe` is the correct original armor model; absence of a class named `ModelVoidRobe` is not a defect.
- No confirmed visual regression was retained for Eldritch Portal, Cap, Crystal, Crab Spawner, Guardian, Warden, Golem, or Crab. Item Eldritch model routes for metadata `0`, `1`, `3`, `8`, and `9` were present.
- No confirmed visual regression was retained for tubes, tables, Runic Matrix, Thaumatorium, Essentia Crystalizer, Vis Relay, Node Stabilizer, Node Converter, or Energized Node. Tube Valve, One-Way Tube, and Buffer transforms matched the original CFR behavior after expected 1.12 adaptation.
- `ItemTubeRenderer` handling of Valve metadata `1` and Crystalizer metadata `7`, while Centrifuge metadata `2` uses a static baked inventory shell, is intentional and covered by existing route tests.
- Metal/stone/wood device TEISR and baked model routes inspected had no evidence-backed transform or missing-model defect. Expected 1.12 JSON, baked-model, atlas, and TEISR adaptations are not findings by themselves.
- Wand cap and rod item models, including Void cap active/inert and primal staff rod paths, were present; no separate missing-model finding was retained.

### State negative controls

- Essentia Crystalizer also has unrestored-looking lightmap behavior, but the same behavior exists in TC4. It is therefore not a parity defect in A-019 and must not be folded into F13/F14 without new authority.
- Obelisk captures and restores `OpenGlHelper.lastBrightnessX/Y` around its sampled world-light pass. The Obelisk findings do not allege a lightmap leak.
- A static guard's presence is not visual proof. Existing tests generally check texture strings, renderer registration, broad route structure, or that a layered helper is called; they do not validate exact coordinate spaces, numeric thresholds, persistence of geometry across LOD, or post-render OpenGL state.

## Test Debt

- F01-F02: Existing `MirrorRendererFidelityStaticGuardTest`, `EldritchNothingRendererFidelityStaticGuardTest`, and `WardedHoleRendererFidelityStaticGuardTest` positively assert `ActiveRenderInfo.getRotationX()` and broad texgen flow, so they currently encode the wrong semantic substitution rather than testing translation-invariant parallax.
- F03: `ClientProxyEntityRendererRegistrationStaticGuardTest:220-226` requires only a typed `RenderArrow`, arrow texture, type color, and color reset. It passes without wisp texture, 16-frame aura, fade, fullbright, depth mask, or blend split.
- F04: Mirror guards require `if (linked && isVisible(tile))` but do not constrain the visibility function's near/FOV/third-person/range semantics.
- F05-F10 and F12, F15-F17: Existing renderer guards do not lock squared thresholds `512`, `9216`, fallback reachability, persistent rings/key/shell/caps, or tile max render distances.
- F11: Pech Focus has a focused stitch/depth guard, but no equivalent guard requires `FOCUS_PRIMAL_DEPTH_SPRITE`, stitch registration, and `FocusPrimal.getFocusDepthLayerIcon` together.
- F13-F14: Orb guards verify spike/billboard texture and frame basics but do not reject unbalanced `setLightmapTextureCoords` or require capture/restore.
- F18: No test rejects the fourth Void Robe item or checks its model layers, item-color registration, and FEET model visibility. A-009 records the corresponding registration-surface debt.
- Focused static/runtime guards are useful after implementation, but manual client smoke remains mandatory for parallax, animation, blend, LOD transitions, and item/equipped appearance.

## Commands and Evidence Results

- Worktree checks: `git status --short` before materialization and after the original read-only audit. The original audit began and ended clean; at materialization time the goal directory and active-goal pointer were already untracked concurrent work. No unrelated file was edited.
- TC4 renderer decompilation used CFR 0.152 on:
  - `thaumcraft_src/thaumcraft/client/renderers/entity/RenderPrimalArrow.class`
  - `RenderPrimalOrb.class` and `RenderEldritchOrb.class`
  - `thaumcraft_src/thaumcraft/client/renderers/tile/TileHoleRenderer.class`
  - `TileMirrorRenderer.class`, `TileEldritchNothingRenderer.class`, `TileEldritchLockRenderer.class`, and `TileEldritchObeliskRenderer.class`
  - `thaumcraft_src/thaumcraft/client/lib/UtilsFX.class --methodname isVisibleTo`
  - `thaumcraft_src/thaumcraft/common/items/wands/foci/ItemFocusPrimal.class`
- TC4 tile/class surfaces used `javap -classpath thaumcraft_src -p` for `TileEldritchNothing`, `TileEldritchLock`, `TileEldritchObelisk`, and `ConfigItems`; CFR confirmed Lock and Obelisk `func_145833_n()` each return `9216.0`.
- Asset identity used `sha256sum` on both port/oracle copies of `focus_primal_depth.png` and `wisp.png`; both pairs matched the hashes recorded above.
- Current source, models, assets, and tests were inspected with targeted repository reads, globs, and regex searches.
- Tests/build: not run because only this documentation report changed and no product behavior was modified.
- Runtime/manual client smoke: required for implementation closure, not run during the audit. Automated client smoke is not a routine repository validation path.
- Server smoke: not required; this packet changes no common/server/product path.
- Commit: none.

## Unknowns and Limitations

- Exact in-game severity of F01/F02 can vary by camera, face, world coordinates, driver, and render ordering. The semantic input mismatch is statically established, but visual closure requires M-019-01.
- Lightmap leaks F13/F14 are order-dependent. Static state imbalance is established; M-019-08 must demonstrate that the selected fix preserves orb appearance and following draws.
- The port's persisted-world obligation for `thaumcraft:itembootsvoidrobe` is unknown. TC4 provides no compatibility basis for the ID, but removal must follow explicit repository policy.
- No client screenshots, frame captures, GPU traces, or performance profiles were produced. No statement in this packet claims complete visual parity based on compile success.
- Display transforms and TEISR routes marked as parity were assessed statically against original behavior and existing conventions; they remain subject to ordinary manual visual regression testing when adjacent code changes.

## Handoff

- Terminal status: complete.
- Defect index: A-019-F01 coordinate-space collapse; F02 projected-coordinate/rotation substitution; F03 missing Primal Arrow aura; F04 linked Mirror 8-block visibility; F05 Nothing 12-block LOD; F06 Nothing 16-block dispatch; F07 Nothing shared-face suppression; F08 Lock 32-block field LOD; F09 Lock rings/key LOD gate; F10 Lock 48/96 range; F11 Primal Focus depth stitch/return; F12 Hole unreachable fallback; F13 Primal Orb lightmap leak; F14 Eldritch Orb lightmap leak; F15 Obelisk 96-block field LOD; F16 Obelisk shell/cap gate; F17 Obelisk 144/96 range; F18 non-original/inconsistent Void Robe boots.
- Manual smoke index: M-019-01 field coordinates/parallax; M-019-02 arrow aura; M-019-03 Mirror visibility; M-019-04 Nothing LOD/range/faces; M-019-05 Lock LOD/geometry/range; M-019-06 Primal Focus depth; M-019-07 Hole fallback; M-019-08 orb lightmap; M-019-09 Obelisk LOD/geometry/range; M-019-10 Void Robe boots adjudication.
- Preserve index: original scoped asset payloads/paths; Primal Crusher; ordinary Void gear; three original Void Robe pieces and `ModelRobe`; listed Eldritch entities/tiles without findings; device/tube/table routes; Item Eldritch metadata routes; wand cap/rod models; TC4-matching Crystalizer lightmap behavior; Obelisk balanced sampled-light pass.
- Exact continuation point: Orchestrator may normalize the 18 atomic defects, negative controls, test debt, and ten smoke groups into central findings without further A-019 discovery work.
- Smallest next action if continued: adjudicate shared field coordinate semantics and the three LOD/range families before implementation; keep product and central-ledger edits outside this packet.
