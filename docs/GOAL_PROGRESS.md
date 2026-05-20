# Durable Goal Progress

Last updated: 2026-05-20
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
- Thaumometer render parity work has started with a narrow placement checkpoint: [ItemThaumometerRenderer.java](/home/opencode/ai/Thaumcraft-4.2-FOREVA/src/main/java/thaumcraft/client/renderers/item/ItemThaumometerRenderer.java) now recenters the flat scanner mesh on its true vertical midpoint before builtin/entity display transforms, and [itemthaumometer_tesr.json](/home/opencode/ai/Thaumcraft-4.2-FOREVA/src/main/resources/assets/thaumcraft/models/item/itemthaumometer_tesr.json) now uses less distorted first-person plus larger ground/fixed transforms.
- Added a tightened static guard in [ThaumometerItemRendererContractTest.java](/home/opencode/ai/Thaumcraft-4.2-FOREVA/src/test/java/thaumcraft/client/ThaumometerItemRendererContractTest.java) so the recentering constant and the intended handheld/ground display transform shape do not silently regress before manual client verification.
- A follow-up RECON proved the placement-only checkpoint could not achieve real parity because the 1.12 route still used a blind `TileEntityItemStackRenderer`, while the original 1.7.10 thaumometer renderer branched explicitly on `INVENTORY`, `EQUIPPED`, and `EQUIPPED_FIRST_PERSON`.
- The current client checkpoint restores that missing architecture: [ClientModelRegistry.java](/home/opencode/ai/Thaumcraft-4.2-FOREVA/src/main/java/thaumcraft/client/ClientModelRegistry.java) now wraps `thaumcraft:itemthaumometer_tesr` during `ModelBakeEvent`, [ThaumometerPerspectiveModel.java](/home/opencode/ai/Thaumcraft-4.2-FOREVA/src/main/java/thaumcraft/client/renderers/item/ThaumometerPerspectiveModel.java) captures the active `TransformType`, and [ItemThaumometerRenderer.java](/home/opencode/ai/Thaumcraft-4.2-FOREVA/src/main/java/thaumcraft/client/renderers/item/ItemThaumometerRenderer.java) once again uses context-aware GUI/ground/third-person/first-person branches with restored first-person arm posture.
