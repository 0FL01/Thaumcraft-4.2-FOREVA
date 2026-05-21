# Durable Goal Progress

Last updated: 2026-05-21
Branch: `codex/durable-goal-stage8-9`

> Condensed with user approval. Historical checkpoint prose is split into batch archives under `docs/GOAL_PROGRESS-archive/`.

## Contract Checklist

- [x] Read source-of-truth files and Stage 3-9 docs.
- [x] Start from `git status --short` and establish baseline non-GUI validation.
- [ ] Close or classify Stage 3-7 blockers before any Stage 8/9 parity claim.
- [ ] Finish Stage 8/9 implementation and validation work.
- [ ] Record GUI/manual checks as skipped where interaction or DISPLAY is unavailable.
- [ ] End with clean `git status --short` after checkpoint commits.

## Current-truth interpretation guard

This file is a progress digest, not a parity certificate.

- `static guarded` means source/corpus regressions only.
- `validate --smoke passed` means build/server-load stability only.
- Stage 8 visual progress is not backend parity evidence.
- Stage 9 recipe/research corpus progress is not server-authoritative progression proof.

Current backend blockers remain: research/progression runtime route, scan and research-table live-route/e2e validation, alchemy/thaumatorium/infusion runtime validation, Outer Lands portal/maze validation, and save/load behavior for complex tiles.

## Current Evidence

- Detailed checkpoint prose lives in `docs/GOAL_PROGRESS-archive/`; this file is a digest only.
- Stage 8 client/render/FX work is archived; Stage 9 remains runtime-open.

## Skipped GUI/Manual Graphics Checks

- Interactive GUI, renderer, screenshot, and manual playthrough checks remain skipped until a real client session is available.

## Baseline Validation

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh check-jar` — passed.
- `./scripts/dev.sh validate` — passed.
- `./scripts/dev.sh validate --smoke` — passed; server readiness and crash-marker checks only.

### Validation interpretation limit

Latest validation proves build/server-load stability only, not backend parity or gameplay closure.

## Checkpoint Digest

| Area | Condensed status |
| --- | --- |
| Stage 3-7 | Residual blockers and runtime gaps are archived; no new closure claim here. |
| Stage 8 | Client/render/FX progress is archived; not backend parity evidence. |
| Stage 9 | Substantial implementation/corpus present; runtime validation and server-authoritative progression remain open. |
| Docs/validation | Progress history is archived into batches; live file stays concise. |

## Archive Index

See `docs/GOAL_PROGRESS-archive/INDEX.md` for batch listings and content.

## Active backend blocker register

Do not claim backend substantially complete until these are resolved or scoped out:

1. Normal research progression route.
2. Research table live-route/adversarial validation.
3. Scan authority live-route/adversarial validation.
4. Alchemy/thaumatorium/infusion runtime validation.
5. Outer Lands runtime validation.
6. Static guard overconfidence.

## Latest Checkpoint

- Champion display-name parity issue in already-used worlds was narrowed further: restoring `champion.mod.*` lang entries fixed the asset corpus, but champion custom names were still vulnerable because the server-side naming path depended on `I18n.translateToLocal(...)` and persisted old raw names in world data.
- `ChampionModifier.getModNameLocalized()` now falls back to the original English modifier labels when the server cannot resolve the lang key, so new champions no longer persist raw prefixes like `champion.mod.warp`.
- `EventHandlerEntity`/`EntityUtils` now repair previously saved technical champion names on mob join/load using the live `tc.mobmod` modifier, so existing worlds migrate from `champion.mod.warp Skeleton` to `Warped Skeleton` without NBT format changes.
- Added static/runtime guards for the fallback-and-repair path so champion name parity cannot silently regress.
- Champion client particles were independently regressed in the 1.12 port because `RenderEventHandler.livingTick(...)` kept only the scan-overlay cleanup path and no longer called `ChampionModifier.mods[t].effect.showFX(...)` for champion mobs.
- Restored the original client-side champion FX hook in [RenderEventHandler.java](/home/opencode/ai/Thaumcraft-4.2-FOREVA/src/main/java/thaumcraft/client/lib/RenderEventHandler.java), so champion mobs once again emit their modifier particles on the client while preserving the existing scan cleanup route.
- A follow-up client crash during `ChampionModInfested.showFX(...)` was traced to [FXBreaking.java](/home/opencode/ai/Thaumcraft-4.2-FOREVA/src/main/java/thaumcraft/client/fx/particles/FXBreaking.java): the particle already self-renders on layer 3 but still called `Particle.setParticleTexture(...)`, which Forge 1.12 rejects for that route.
- Removed the invalid `setParticleTexture(...)` call and tightened layer-3/static guards so infested champion particles can render without crashing the client tick loop.
- Thaumometer render parity was initially approached as a placement baseline and then a transform-aware arm-rendered first-person route, but live calibration showed the custom arm posture was impractical to tune. The current port cuts all custom first-person work and replaces it with a TC4.2→TC6 basis adapter (`TC4_TO_TC6_VERTICAL_CENTER`, `TC4_TO_TC6_Y_ROTATION`) applied in [ItemThaumometerRenderer.java](/home/stfu/ai/Thaumcraft-4.2-FOREVA/src/main/java/thaumcraft/client/renderers/item/ItemThaumometerRenderer.java) before delegating all perspective transforms — including first-person — to Thaumcraft 6 donor display transforms in [itemthaumometer_tesr.json](/home/stfu/ai/Thaumcraft-4.2-FOREVA/src/main/resources/assets/thaumcraft/models/item/itemthaumometer_tesr.json). The old `renderFirstPersonSetup()`, `renderFirstPersonHands()`, reflection equipped-progress fields, and the first-person matrix-null bypass in [ThaumometerPerspectiveModel.java](/home/stfu/ai/Thaumcraft-4.2-FOREVA/src/main/java/thaumcraft/client/renderers/item/ThaumometerPerspectiveModel.java) were all removed. The donor first-person Z was user-calibrated to `-14.352`. Added a static guard in [ThaumometerItemRendererContractTest.java](/home/stfu/ai/Thaumcraft-4.2-FOREVA/src/test/java/thaumcraft/client/ThaumometerItemRendererContractTest.java) for the basis adapter constants and updated display-transform values.
- Live follow-up defect analysis then narrowed the remaining thaumometer corruption to the bundled CCL vertex writer rather than transforms: the scanner body was still emitted through [CCRenderState.java](/home/opencode/ai/Thaumcraft-4.2-FOREVA/src/main/java/thaumcraft/codechicken/lib/render/CCRenderState.java), which still assumed a hard-coded 1.7.10 attribute order and even opened the pass as `DefaultVertexFormats.BLOCK` despite writing OBJ normals.
- `CCRenderState` now emits vertices by walking the active 1.12 `VertexFormat` element list instead of blindly pushing `normal/color/lightmap/pos/tex`, and `ItemThaumometerRenderer` now renders the scanner body through `DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL` with explicit `normalAttrib` loading so the model body no longer reads color/UV bytes as positions.
- Thaumometer scan targeting has now been realigned with the 1.7.10 route as well: `ItemThaumometer` restores the permissive `getPointedEntity(..., 0.5D, 10.0D, 0.0F, true)` path so dropped `EntityItem` targets can be scanned again, splits raw target acquisition from validated scan completion so client HUD, client use-ticks, and `PacketScannedToServer` authority checks no longer drift apart, and restores `Thaumcraft.proxy.blockRunes(...)` feedback for valid scan candidates. Runtime coverage now includes a dropped-item authoritative scan path that reaches `ScanManager.completeScan(...)`.
- Another live follow-up then showed the scan route was working but the client presentation was still under-ported: first-person readout only appeared during active use, scanned targets showed a collapsed `vis` line instead of the original aspect grid, and `PacketAspectDiscovery` / `PacketAspectPool` silently updated capability state without the old corner notifications. A compact `UtilsFX` / `PlayerNotifications` / `REHNotifyHandler` client surface is now restored for this checkpoint, `ItemThaumometerRenderer` once again renders aspect tags for scanned targets while the scanner is merely held in first person, and the aspect discovery/pool packets again emit localized client notifications plus orb-like audio feedback.
- Live user retest then proved two parity gaps remained: the notification HUD was wired to a dead 1.12 overlay phase, and the held-screen readout still used approximate 1.12 math instead of the original TC4.2 aspect/title layout. `RenderEventHandler` now routes notification rendering through `RenderGameOverlayEvent.Text`, `ItemThaumometerRenderer` mirrors the original `0.0075F` tag-scale / `-0.25F` title-offset scanner readout path, `ScanManager.validScan(...)` again emits `tc.discoveryerror` / `tc.unknownobject` client hints, and `en_us.lang` now contains the missing `nodetype.*`, `nodemod.*`, and full `tc.aspect.help.*` corpus required by that route.
- Another live retest showed the remaining gaps were deeper than overlay-hook wiring: scan hints still localized through legacy common-side translation and first-person thaumometer HUD was still running inside donor first-person display transforms, so corner notifications degraded to raw keys and scanned aspects/titles remained off-screen.
- Thaumometer notifications now cross a proper client proxy boundary instead of formatting in common code: `ScanManager.validScan(...)`, `PacketAspectDiscovery`, and `PacketAspectPool` call `Thaumcraft.proxy.notifyThaumometer*`, while `ClientProxy` performs the actual 1.12 client localization via `net.minecraft.client.resources.I18n` and queues `PlayerNotifications`/aspect flyouts. This removes the old `net.minecraft.util.text.translation.I18n` dependency from the active thaumometer notification route.
- Live retest immediately rejected that dedicated first-person route: the identity-matrix bypass plus custom equipped-progress/arm transform chain broke held placement more severely than it helped. The current checkpoint rolls back that layer, restores donor first-person display matrices in `ThaumometerPerspectiveModel`, and keeps the scanner/HUD content on top of the already-calibrated TC4→TC6 basis adapter instead of replacing the pose route again.
- One live issue still remains open after that rollback: passive thaumometer handling can spam `tc.discoveryerror`, most likely because the raw target fallback still reaches `ThaumcraftApi.scanEventhandlers`, and the bundled `ScanManager` treats the held stack itself as a scanable item. The next scanner checkpoint needs to separate passive HUD target acquisition from active scan validation so held readout never self-scans the thaumometer item.
- The next scanner checkpoint is now in place: the held readout is reattached to the same scanner-screen plane used by `scanscreen.png`, and active scan hints are edge-triggered through `ItemThaumometer.doActiveScan(..., notifyInvalid)` plus `ScanManager.notifyInvalidScan(...)` instead of firing from every `validScan(...)` call. This keeps passive HUD rendering notification-free while preserving one-shot `tc.discoveryerror` / `tc.unknownobject` hints on real scan attempts.
- Added focused runtime/static coverage for the corrected surface, including a fresh-player `ScanManager.validScan(...)` harness that proves `stone`/`sand` style aspect lists pass while `grass`/`herba` style and deeper compound aspects still fail until prerequisites are discovered, matching the reference semantics behind the user retest.
