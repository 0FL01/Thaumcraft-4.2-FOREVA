# Stage 8-e — Gap-анализ и план закрытия

## 1. Цель фазы

Stage 8-e закрывает клиентскую систему визуальных эффектов Thaumcraft 4.2.3.5 в порте на Forge 1.12.2: particle engine, FX-пакеты и их client handlers, beam/bolt/lightning эффекты, wand/focus визуальную обратную связь, runic/warp/champion/taint эффекты, Portable Hole/Warding visuals, FX-связанные ресурсы и звуки. Цель не в новом дизайне, а в восстановлении поведения оригинала с адаптацией к API Minecraft/Forge 1.12.2.

Основание scope: `docs/PRD.md:117-138`, `docs/PRD.md:365-393`, `AGENTS.md:53-58`.

## 2. Scope фазы

В scope Stage 8-e входят:

- `src/main/java/thaumcraft/client/fx/**`: particle engine, particle classes, beam classes, bolt/lightning classes, utility vectors/matrices.
- `src/main/java/thaumcraft/client/ClientProxy.java`: только FX overrides, ParticleEngine registration, client-only FX helper surface.
- `src/main/java/thaumcraft/common/lib/network/fx/**`: payload serialization, client scheduling, handler behavior, routing into client FX classes/proxy helpers.
- FX send sites and direct proxy-FX call sites needed to exercise Stage 8-e scenarios, without deep-анализ GUI/TESR/entity renderers beyond direct FX calls.
- Wand/focus feedback for shock/lightning, Portable Hole, Warding, vis drain/source stream, block sparkle/dig/bubble/arc/zap, sonic, shield, infusion/essentia source, wisp zap, beam pulse.
- Runic and champion shield visuals and their coupled sounds.
- Warp visual packets/post-processing/overlay resources where reference behavior requires them.
- Required particle, beam, ward, vortex/tunnel/vignette textures and FX-specific sound entries/assets.

Вне scope этого документа:

- Stage 8-a/b/c/d GUI/TESR/entity-renderer parity, except direct calls into FX helpers.
- Stage 9 recipes/research/content registration, except if a missing registration directly prevents FX scenario execution.
- Server gameplay correctness of foci/entities except where the current code explicitly disables or omits FX routing.

## 3. Источники сравнения

Проектные документы и constraints:

- `AGENTS.md:14-17`: reference material and asset origin rules.
- `AGENTS.md:53-58`: Phase 8 client GUI/render/FX/shader work and Portable Hole/Warding visual renderer deferrals.
- `AGENTS.md:155-177`: runtime smoke validation expectations for runtime-affecting work.
- `docs/PRD.md:117-138`: client layer owns particles, beams, bolts, shader/post-processing, sounds/visual feedback.
- `docs/PRD.md:365-393`: Phase 8 deliverables and runtime/client risk.
- `docs/PRD.md:531-538`: client smoke checklist includes particles/beams/bolts and shaders.

Reference implementation and resources:

- `thaumcraft_src/thaumcraft/client/fx/ParticleEngine.class`
- `thaumcraft_src/thaumcraft/client/fx/WRMat4.class`
- `thaumcraft_src/thaumcraft/client/fx/WRVector3.class`
- `thaumcraft_src/thaumcraft/client/fx/beams/FXArc.class`
- `thaumcraft_src/thaumcraft/client/fx/beams/FXBeam.class`
- `thaumcraft_src/thaumcraft/client/fx/beams/FXBeamBore.class`
- `thaumcraft_src/thaumcraft/client/fx/beams/FXBeamGolemBoss.class`
- `thaumcraft_src/thaumcraft/client/fx/beams/FXBeamPower.class`
- `thaumcraft_src/thaumcraft/client/fx/beams/FXBeamWand.class`
- `thaumcraft_src/thaumcraft/client/fx/bolt/FXLightningBolt.class`
- `thaumcraft_src/thaumcraft/client/fx/bolt/FXLightningBoltCommon.class`
- `thaumcraft_src/thaumcraft/client/fx/other/FXBlockWard.class`
- `thaumcraft_src/thaumcraft/client/fx/other/FXShieldRunes.class`
- `thaumcraft_src/thaumcraft/client/fx/other/FXSonic.class`
- `thaumcraft_src/thaumcraft/client/fx/particles/*.class`: 23 particle class files including `FXGeneric`, `FXWisp`, `FXBurst`, `FXSparkle`, `FXBoreParticles`, `FXEssentiaTrail`, `FXBlockRunes`, `FXSwarm`, `FXVent`.
- `thaumcraft_src/thaumcraft/common/lib/network/fx/*.class`: 14 original FX packet classes.
- `thaumcraft_src/thaumcraft/client/ClientProxy.class`: original proxy helper methods such as `blockSparkle`, `sparkle`, `burst`, `wispFX3`, `beam`, `beamCont`, `beamBore`, `beamPower`, `blockWard`, `arcLightning`.
- `thaumcraft_src/assets/thaumcraft/textures/misc/particles.png`, `particles2.png`, `particlefield.png`, `particlefield32.png`, `beam.png`, `beam1.png`, `beam2.png`, `beam3.png`, `beamh.png`, `wisp.png`, `wispy.png`, `vortex.png`, `tunnel.png`, `vignette.png`.
- `thaumcraft_src/assets/thaumcraft/sounds.json` and `thaumcraft_src/assets/thaumcraft/sounds/*.ogg`.

Current implementation inspected:

- `src/main/java/thaumcraft/client/ClientProxy.java:90-105`
- `src/main/java/thaumcraft/common/CommonProxy.java:139-160`
- `src/main/java/thaumcraft/common/lib/network/PacketHandler.java:57-100`
- `src/main/java/thaumcraft/common/lib/network/PacketBase.java:20-35`
- `src/main/java/thaumcraft/common/lib/network/fx/*.java`
- `src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:208-211`, `src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:246-257`, `src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:307-311`
- `src/main/java/thaumcraft/common/lib/WarpEvents.java:77`, `src/main/java/thaumcraft/common/lib/WarpEvents.java:132`, `src/main/java/thaumcraft/common/lib/WarpEvents.java:210-212`
- `src/main/java/thaumcraft/common/entities/monster/EntityWisp.java:173-204`
- `src/main/java/thaumcraft/common/entities/monster/EntityFireBat.java:200-205`
- `src/main/java/thaumcraft/common/entities/monster/EntityTaintacle.java:76-79`
- `src/main/java/thaumcraft/common/entities/monster/EntityEldritchGuardian.java:291-292`
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortal.java:128-149`
- `src/main/java/thaumcraft/common/items/wands/foci/FocusShock.java:92-101`
- `src/main/java/thaumcraft/common/items/wands/foci/FocusWarding.java:71-85`
- `src/main/java/thaumcraft/common/items/wands/foci/FocusPortableHole.java:100-112`
- `src/main/java/thaumcraft/common/lib/utils/Utils.java:39-41`
- `src/main/resources/assets/thaumcraft/textures/misc/potions.png`
- `src/main/resources/assets/thaumcraft/sounds.json:1-68`

Lightweight analysis commands run:

- `git status --short`
- `rg -n "Phase 8|particle|shader|beam|bolt|FX" docs/PRD.md`
- `find thaumcraft_src/thaumcraft/client/fx -type f -name '*.class'`
- `find src/main/java/thaumcraft/client -path '*fx*' -type f -name '*.java' | wc -l`
- `rg -n "PacketFX|blockSparkle\(|beam\(|bolt\(|wispFX3\(|burst\(|sparkle\(" src/main/java/thaumcraft`
- `cfr --silent true ...` for selected reference FX classes and packets, printed to tool output only.

No build or runtime smoke was run because this is documentation-only analysis and implementation code was not changed.

## 4. Текущее состояние Stage 8-e

Stage 8-e is not complete.

Current state summary:

- `src/main/java/thaumcraft/client/fx/**` is absent. The reference has 45 client FX-related `.class` files under `thaumcraft_src/thaumcraft/client/fx/**`.
- `src/main/java/thaumcraft/client/ClientProxy.java:90-105` contains three Phase 8 stub overrides: `blockSparkle`, `beam`, `bolt`. They do nothing.
- `src/main/java/thaumcraft/common/CommonProxy.java:139-160` exposes only a small subset of original FX helper methods and all methods are no-op on common side. This is acceptable for common side but the client side does not override most of the helper surface used by current entities.
- `src/main/java/thaumcraft/common/lib/network/PacketHandler.java:84-97` registers the 14 original FX packet slots, but most packet classes in `src/main/java/thaumcraft/common/lib/network/fx/**` are empty shells with only default constructors.
- `PacketFXBlockArc` serializes coordinates/entity id but has no `onMessage` handler (`src/main/java/thaumcraft/common/lib/network/fx/PacketFXBlockArc.java:24-39`).
- `PacketFXVisDrain` serializes `from`, `to`, `color` but explicitly defers client behavior to Phase 8 and returns null (`src/main/java/thaumcraft/common/lib/network/fx/PacketFXVisDrain.java:37-40`).
- Several important send sites are commented out as Phase 8 work: runic/champion shield packets and sounds in `EventHandlerRunic`, warp visual packets in `WarpEvents`, aura sparkle packets in `ServerTickEventsFML`, sonic packet in `EntityEldritchGuardian`.
- Current resource tree has `src/main/resources/assets/thaumcraft/textures/misc/potions.png` only for misc textures, while the reference has required particle/beam/warp/ward textures in `thaumcraft_src/assets/thaumcraft/textures/misc/**`.
- `src/main/resources/assets/thaumcraft/sounds.json:1-68` appears broadly aligned with the reference sound keys for FX-coupled sounds, and key FX sounds exist under `src/main/resources/assets/thaumcraft/sounds/**`; the gap is mainly that several sound calls remain disabled with Phase 8 comments.
- Reference shader directory is absent (`thaumcraft_src/assets/thaumcraft/shaders/**` not found), so there is no direct reference shader-file gap. The reference instead uses misc overlay/warp textures such as `vortex.png`, `tunnel.png`, `vignette.png`, which are missing from current resources.

## 5. Gap list

### GAP-1: Отсутствует клиентский ParticleEngine и весь пакет `thaumcraft.client.fx`

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/client/` has no `fx/` directory.
- `src/main/java/thaumcraft/client/ClientProxy.java:42-44` registers only `ClientEventHandler`, not a particle engine.
- `src/main/java/thaumcraft/client/ClientProxy.java:90-105` has Phase 8 no-op FX overrides.

**Референс:**
- `thaumcraft_src/thaumcraft/client/fx/ParticleEngine.class`
- `thaumcraft_src/thaumcraft/client/fx/particles/*.class`
- `thaumcraft_src/thaumcraft/client/fx/WRMat4.class`
- `thaumcraft_src/thaumcraft/client/fx/WRVector3.class`
- `thaumcraft_src/thaumcraft/client/ClientProxy.class`

**Что не совпадает:**

Reference has a dedicated `ParticleEngine` singleton with four render layers, dimension-separated particle lists, `RenderWorldLastEvent` rendering, `ClientTickEvent` ticking, texture switching between `textures/misc/particles.png` and `textures/misc/particles2.png`, particle cap handling, and crash wrapping for render/tick failures. Current code has no equivalent engine and no current particle classes. Direct proxy calls from current entities are therefore no-ops.

Concrete affected current call sites:

- `src/main/java/thaumcraft/common/entities/monster/EntityWisp.java:173-204` calls `Thaumcraft.proxy.burst` and `Thaumcraft.proxy.wispFX3` on client.
- `src/main/java/thaumcraft/common/entities/monster/EntityFireBat.java:200-205` calls `Thaumcraft.proxy.sparkle`.
- `src/main/java/thaumcraft/common/entities/monster/EntityTaintacle.java:76-79` calls `Thaumcraft.proxy.burst`.
- `src/main/java/thaumcraft/common/tiles/TileJarNode.java:177` calls `Thaumcraft.proxy.blockSparkle`.

**Что нужно доделать:**

Port the reference particle engine and the particle classes needed by Stage 8-e into Forge 1.12.2 client APIs.

**Как доделать:**
- Add `src/main/java/thaumcraft/client/fx/ParticleEngine.java` adapted from `thaumcraft_src/thaumcraft/client/fx/ParticleEngine.class`.
- Add required utility classes `WRMat4`, `WRVector3` if still needed by bolts/beams.
- Add particle classes under `src/main/java/thaumcraft/client/fx/particles/`: at minimum `FXGeneric`, `FXSparkle`, `FXSpark`, `FXBurst`, `FXWisp`, `FXWispArcing`, `FXWispEG`, `FXBoreParticles`, `FXBoreSparkle`, `FXEssentiaTrail`, `FXBlockRunes`, `FXSmokeTrail`, `FXSwarm`, `FXVent`, `FXBreaking`, `FXBubble`, `FXBubbleAlt`, `FXDrop`, `FXScorch`, `FXSlimyBubble`, `FXSmokeSpiral`, `FXVisSparkle`.
- Register `ParticleEngine.instance` on the Forge event bus from `ClientProxy.registerHandlers()` without breaking current `ClientEventHandler` registration.
- Adapt from `EntityFX`/1.7.10 names to 1.12.2 `Particle`/`BufferBuilder`/`Tessellator` APIs, preserving texture paths and layer semantics.
- Keep `CommonProxy` no-op methods server-safe, and implement client overrides in `ClientProxy` or a focused client FX dispatcher.

**Критерии приемки:**
- [ ] `src/main/java/thaumcraft/client/fx/ParticleEngine.java` exists and is registered client-side only.
- [ ] Direct client calls in `EntityWisp`, `EntityFireBat`, `EntityTaintacle`, and `TileJarNode` visibly spawn reference-equivalent particles.
- [ ] Particle rendering/ticking is dimension-safe and does not reference client classes from dedicated-server paths.
- [ ] Particle cap and Minecraft particle setting behavior match the original intent of reference `ClientProxy.particleCount`.

**Риски / зависимости:**

Forge 1.12.2 particle APIs differ significantly from 1.7.10 `EntityFX`; port must avoid server classloading of client-only classes. This is a blocker for almost every other Stage 8-e FX gap.

#### Checkpoint 2026-05-17 — GAP-1 ParticleEngine dispatcher baseline

Статус: частично продвинут.

Что сделано:

- `src/main/java/thaumcraft/client/fx/ParticleEngine.java` больше не пустой stub:
  - добавлен client-safe queued intake (`addEffect(World, Particle)` + `pendingParticles`);
  - добавлен tick-drain scheduler в `updateParticles(...)` с `TickEvent.Phase.END` guard и dispatch в `mc.effectRenderer.addEffect(...)`;
  - добавлен dimension gate и per-tick cap (`MAX_PARTICLE_ADDITIONS_PER_TICK`);
  - добавлен render-world bookkeeping (`lastRenderWorldTime`, `lastRenderPartialTicks`) в `onRenderWorldLast(...)`.
- `ClientProxyFxStaticGuardTest` расширен проверками, фиксирующими non-stub surface `ParticleEngine`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это dispatcher baseline без полноценного port-а reference `thaumcraft.client.fx.particles/*` классов; визуальная parity в Stage 8-e остается частичной и требует дальнейших checkpoint-ов.

### GAP-2: FX helper surface in `ClientProxy` is incomplete and current overrides are no-op

**Статус:** частично реализовано
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/CommonProxy.java:139-160`
- `src/main/java/thaumcraft/client/ClientProxy.java:90-105`

**Референс:**
- `thaumcraft_src/thaumcraft/client/ClientProxy.class`
- `thaumcraft_src/thaumcraft/client/fx/beams/FXBeamWand.class`
- `thaumcraft_src/thaumcraft/client/fx/beams/FXBeamBore.class`
- `thaumcraft_src/thaumcraft/client/fx/beams/FXBeamPower.class`
- `thaumcraft_src/thaumcraft/client/fx/other/FXBlockWard.class`

**Что не совпадает:**

The reference `ClientProxy` exposes a broad FX API: `sparkle`, `spark`, `burst`, `wispFX*`, `sourceStreamFX`, `bolt`, `nodeBolt`, `beam`, `beamCont`, `beamBore`, `beamPower`, `blockRunes`, `blockWard`, `arcLightning`, `boreDigFx`, `essentiaTrailFx`, taint/slime/tentacle helpers, infusion particles, and more. Current `CommonProxy` exposes only `blockSparkle`, `beam`, `bolt`, `burst`, `wispFX3`, `sparkle`, `particleCount`; current `ClientProxy` overrides only `blockSparkle`, `beam`, `bolt`, and those do nothing.

**Что нужно доделать:**

Define and implement the Stage 8-e subset of proxy helpers required by current port call sites and FX packets, matching reference behavior where the method exists upstream.

**Как доделать:**
- Expand `CommonProxy` only for helpers actually needed by current Stage 8-e packet handlers/entities, keeping methods no-op server-side.
- Implement `ClientProxy.blockSparkle`, `beam`, `bolt`, `burst`, `wispFX3`, `sparkle`, and any new required helpers by instantiating ported FX classes.
- Preserve original method names when practical for traceability.
- Avoid adding full GUI/TESR-specific helpers unless directly needed by Stage 8-e FX packets or existing call sites.

**Критерии приемки:**
- [ ] Every Stage 8-e direct `Thaumcraft.proxy.*FX*` call in current source has a non-no-op client implementation.
- [ ] Server/common proxy methods remain safe no-ops on dedicated server.
- [ ] `ClientProxy.blockSparkle`, `beam`, and `bolt` no longer contain Phase 8 comments or empty bodies.
- [ ] Behavior is visually comparable to the reference for block sparkle, burst, wisp trail, sparkle, beam, and lightning.

**Риски / зависимости:**

Depends on GAP-1 and GAP-5. Expanding proxy APIs too broadly can create unnecessary classloading risk; keep the implementation scoped to packet handlers and current callers.

#### Checkpoint 2026-05-16 — GAP-2 client FX fallback baseline

Статус: ключевые proxy FX helpers for current non-GUI call sites are no longer client no-op.

Что сделано:

- Implemented client fallback effects in `ClientProxy` for:
  - `blockSparkle(...)`
  - `beam(...)`
  - `bolt(...)`
  - `burst(...)`
  - `wispFX3(...)`
  - `sparkle(...)`
  - `particleCount(...)` with `Minecraft.gameSettings.particleSetting` scaling.
- Kept `CommonProxy` methods server-safe no-op; only client overrides execute particle spawning.
- Added static guard test `ClientProxyFxStaticGuardTest` to lock non-no-op proxy surface for these methods.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This checkpoint provides fallback particle/beam/lightning visuals, not full reference `EntityFX`/beam/bolt class parity.
- Manual visual comparison remains skipped by instruction.

### GAP-3: FX packet classes are mostly empty or handlerless

**Статус:** отсутствует / частично реализовано  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/network/PacketHandler.java:84-97`
- `src/main/java/thaumcraft/common/lib/network/PacketBase.java:20-35`
- `src/main/java/thaumcraft/common/lib/network/fx/PacketFXBlockSparkle.java:1-7`
- `src/main/java/thaumcraft/common/lib/network/fx/PacketFXBeamPulse.java:1-7`
- `src/main/java/thaumcraft/common/lib/network/fx/PacketFXZap.java:1-7`
- `src/main/java/thaumcraft/common/lib/network/fx/PacketFXShield.java:1-7`
- `src/main/java/thaumcraft/common/lib/network/fx/PacketFXSonic.java:1-7`
- `src/main/java/thaumcraft/common/lib/network/fx/PacketFXBlockArc.java:24-39`
- `src/main/java/thaumcraft/common/lib/network/fx/PacketFXVisDrain.java:23-40`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/network/fx/PacketFXBlockBubble.class`
- `thaumcraft_src/thaumcraft/common/lib/network/fx/PacketFXBlockDig.class`
- `thaumcraft_src/thaumcraft/common/lib/network/fx/PacketFXBlockSparkle.class`
- `thaumcraft_src/thaumcraft/common/lib/network/fx/PacketFXBlockArc.class`
- `thaumcraft_src/thaumcraft/common/lib/network/fx/PacketFXBlockZap.class`
- `thaumcraft_src/thaumcraft/common/lib/network/fx/PacketFXEssentiaSource.class`
- `thaumcraft_src/thaumcraft/common/lib/network/fx/PacketFXInfusionSource.class`
- `thaumcraft_src/thaumcraft/common/lib/network/fx/PacketFXShield.class`
- `thaumcraft_src/thaumcraft/common/lib/network/fx/PacketFXSonic.class`
- `thaumcraft_src/thaumcraft/common/lib/network/fx/PacketFXWispZap.class`
- `thaumcraft_src/thaumcraft/common/lib/network/fx/PacketFXZap.class`
- `thaumcraft_src/thaumcraft/common/lib/network/fx/PacketFXVisDrain.class`
- `thaumcraft_src/thaumcraft/common/lib/network/fx/PacketFXBeamPulse.class`
- `thaumcraft_src/thaumcraft/common/lib/network/fx/PacketFXBeamPulseGolemBoss.class`

**Что не совпадает:**

`PacketHandler` registers the 14 reference FX discriminator slots, but registration alone is not functional. Reference packet classes serialize fields and execute client-side effects in `onMessage`, for example: `PacketFXBlockSparkle` reads x/y/z/color and calls `Thaumcraft.proxy.blockSparkle(..., 7)`; `PacketFXBeamPulse` reads source/target/color and creates an `FXBeam`; `PacketFXZap` creates `FXLightningBolt`; `PacketFXShield` creates one or more `FXShieldRunes`. Current packet classes mostly have no fields, no serialization, and no handlers. `PacketFXVisDrain` has fields but explicitly returns without effect.

**Что нужно доделать:**

Port all 14 FX packet payloads and client handlers, using Forge 1.12.2 main-thread scheduling where required.

**Как доделать:**
- Implement `toBytes`, `fromBytes`, constructors, and `onMessage` for each class in `src/main/java/thaumcraft/common/lib/network/fx/**`.
- Keep `PacketHandler` discriminator order stable at `PacketHandler.java:84-97` unless a proven reference mismatch is found.
- In `onMessage`, schedule client work via Minecraft client task APIs before touching world/entities/renderers.
- Resolve entity ids using the local client world and player, equivalent to reference `getEntityByID` logic in beam/zap packets.
- Route effects into Stage 8-e client helpers/classes, not into common-only code.

**Критерии приемки:**
- [ ] All 14 FX packet classes have non-empty serialization matching reference payloads.
- [ ] All 14 FX packet classes have client handlers or documented no-op parity only where the reference is no-op.
- [ ] `PacketFXVisDrain` renders a visible vis-drain/source stream instead of returning at `src/main/java/thaumcraft/common/lib/network/fx/PacketFXVisDrain.java:39-40`.
- [ ] Packet handlers do not crash when target blocks/entities are missing or chunks are unloaded.

**Риски / зависимости:**

Depends on GAP-1, GAP-2, GAP-5, and GAP-6. Network-thread rendering is unsafe if not scheduled correctly in 1.12.2.

#### Checkpoint 2026-05-16 — GAP-3 FX handler wiring for active channels

Статус: active FX channels `PacketFXVisDrain` and `PacketFXBlockArc` now have client scheduling handlers.

Что сделано:

- `PacketFXVisDrain` now schedules client work and routes to `Thaumcraft.proxy.beam(...)` instead of returning no-op.
- `PacketFXBlockArc` now schedules client work, resolves source entity by id, and routes to `Thaumcraft.proxy.bolt(...)` with source-type color fallback.
- Added `PacketFXSerializationTest` to lock binary round-trip payloads for both packets.
- Added static handler guard coverage in `ClientProxyFxStaticGuardTest`.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Remaining FX packet families are still open in this stage and require further porting.

#### Checkpoint 2026-05-16 — GAP-3 block sparkle packet path restored

Статус: `PacketFXBlockSparkle` payload/handler and server send-site are now wired.

Что сделано:

- Implemented `PacketFXBlockSparkle` payload (`x/y/z/color`) serialization and client-scheduled `onMessage` routing into `Thaumcraft.proxy.blockSparkle(...)`.
- Restored server-side send in `ServerTickEventsFML.tickBlockSwap(...)` for block replacement sparkle (`sendToAllAround` with 32-block radius).
- Expanded FX tests:
  - `PacketFXSerializationTest` now includes `PacketFXBlockSparkle` round-trip.
  - `ClientProxyFxStaticGuardTest` now enforces packet handler scheduling + server send-site presence.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- The broader Stage 8-e packet set (beam pulse/zap/shield/sonic/etc.) remains open.

#### Checkpoint 2026-05-16 — Swapper drop and wand-sound paths restored

Статус: `ServerTickEventsFML` block-swap drop handling and wand sound cue now match reference behavior shape.

Что сделано:

- Restored silk-touch drop route in `tickBlockSwap(...)`:
  - uses `currentBlock.canSilkHarvest(world, pos, state, vs.player)` gate;
  - uses `BlockUtils.createStackedBlock(...)` for silk result.
- Restored non-silk drop route in `tickBlockSwap(...)`:
  - uses `currentBlock.getDrops(drops, world, pos, state, fortune)` with focus fortune.
- Restored queue-add wand sound cue in `addSwapper(...)`:
  - `world.playSound(player, x, y, z, TCSounds.WAND, SoundCategory.PLAYERS, 0.25f, 1.0f)`.
- Expanded static guard coverage in `ClientProxyFxStaticGuardTest`:
  - enforces silk-harvest/fortune-drop code-path presence;
  - enforces wand sound call-site presence.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This checkpoint restores block-swap drop/sound behavior only; deeper focus/block-chain parity remains outside this narrow scope.

#### Checkpoint 2026-05-16 — TileSensor note-event tracking hooks restored

Статус: server-side note-event buffering hooks for `TileSensor` are now active again.

Что сделано:

- Restored `TileSensor.noteBlockEvents` baseline buffer:
  - `WeakHashMap<WorldServer, ArrayList<Integer[]>>` field is now present in `TileSensor`.
- Restored event capture in `EventHandlerWorld.onNoteBlockPlay(...)`:
  - creates per-world list when missing;
  - stores reference-shaped payload tuple
    `(x, y, z, instrumentOrdinal, vanillaNoteId)`.
- Restored world cleanup in `EventHandlerWorld.onWorldUnload(...)`:
  - removes world entry from `TileSensor.noteBlockEvents` in a guarded `try/catch`.
- Restored per-tick list cleanup in `ServerTickEventsFML.serverWorldTick(...)`:
  - clears buffered note events for current `WorldServer`.
- Added static guard `TileSensorNoteEventStaticGuardTest` to lock this hook set.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This checkpoint restores note-event buffering hooks only; full `TileSensor` runtime parity (tone/note/redstone trigger logic) remains outside this narrow scope.

#### Checkpoint 2026-05-16 — EventHandlerWorld crafting-return hooks restored

Статус: special crafting return paths in `EventHandlerWorld.onItemCrafted(...)` are now active again.

Что сделано:

- Restored alumentum-style essence return hook:
  - `event.crafting == itemResource:13` with NBT gate;
  - iterates craft matrix and returns consumed `ItemEssence` containers by `grow(1)`.
- Restored arcane-bellows return hook:
  - `event.crafting == blockMetalDevice:3`;
  - returns center ingredient stack from craft slot `4` by `grow(1)`.
- Added static guard `EventHandlerWorldCraftingReturnStaticGuardTest` to lock both return paths.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This checkpoint restores crafting-return hooks only; harvest-drop special mining replacement path remains outside this narrow scope.

#### Checkpoint 2026-05-16 — EventHandlerEntity fake-pickup guard restored

Статус: `EventHandlerEntity.onItemPickup(...)` now restores the reference fake-player pickup cancel guard.

Что сделано:

- Restored server-side pickup cancellation for fake Thaumcraft actors:
  - `if (event.getEntityPlayer().getName().startsWith("FakeThaumcraft")) event.setCanceled(true);`
- Added static guard `EventHandlerEntityPickupStaticGuardTest` to lock this guard path.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This checkpoint restores only the fake-player pickup guard; broader research-discovery pickup parity remains out of scope.

#### Checkpoint 2026-05-16 — EventHandlerWorld harvest special-mining hook restored

Статус: `EventHandlerWorld.onHarvestDrops(...)` now restores the reference-shaped special-mining replacement path.

Что сделано:

- Restored tool/focus gate for special mining replacement:
  - `ItemElementalPickaxe`, `ItemPrimalCrusher`, or `ItemWandCasting` with `FocusExcavation.dowsing`.
- Restored replacement chance flow:
  - base fortune via `EnchantmentHelper` (or focus `treasure` level for wand focus);
  - `chance = 0.2 + fortune * 0.075`;
  - per-drop replacement via `Utils.findSpecialMiningResult(...)`.
- Restored utility baseline in `Utils` for this route:
  - `specialMiningResult` / `specialMiningChance` maps;
  - `addSpecialMiningResult(...)`;
  - `findSpecialMiningResult(...)`.
- Added static guard `EventHandlerWorldHarvestDropsStaticGuardTest`.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This checkpoint restores the replacement hook path only; full parity still depends on complete registration coverage of special-mining mappings in content init.

#### Checkpoint 2026-05-16 — EventHandlerEntity golem-owner interact guard restored

Статус: `EventHandlerEntity.onEntityInteract(...)` now restores the reference owner-check guard for golem interactions.

Что сделано:

- Restored interaction guard:
  - when target is `EntityGolemBase` with an owner;
  - and interacting player is not owner;
  - send player message `"You are not my Master!"` and cancel interaction event.
- Added static guard `EventHandlerEntityInteractStaticGuardTest`.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This checkpoint restores only the owner-interact guard path; Pech trade UX/flows remain outside this narrow scope.

#### Checkpoint 2026-05-16 — Champion modifier client FX hooks restored (fallback surface)

Статус: `ChampionMod*::showFX` placeholder cluster is now wired to client proxy fallback FX paths.

Что сделано:

- Restored `showFX(...)` implementations across champion modifier classes:
  - `ChampionModArmored`, `ChampionModBold`, `ChampionModFire`, `ChampionModGrim`,
    `ChampionModInfested`, `ChampionModMighty`, `ChampionModPoison`, `ChampionModSickly`,
    `ChampionModSpined`, `ChampionModUndying`, `ChampionModVampire`, `ChampionModWarded`, `ChampionModWarp`.
- Added/used proxy fallback surface for these hooks:
  - `CommonProxy`/`ClientProxy`: `drawGenericParticles(...)` and `slimeJumpFX(...)`;
  - champion hooks now route through `Thaumcraft.proxy.*` paths instead of TODO no-op bodies.
- Added static guard coverage:
  - `ChampionModFxStaticGuardTest` enforces showFX proxy routing and absence of placeholder TODO text;
  - `ClientProxyFxStaticGuardTest` now enforces `slimeJumpFX(...)` and `drawGenericParticles(...)` overrides.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This is fallback particle parity through proxy helpers; dedicated reference particle classes (`FXSpark`/`ParticleEngine`-backed champion visuals) remain outside this narrow checkpoint.

#### Checkpoint 2026-05-16 — GAP-3 shield packet and runic send paths restored

Статус: `PacketFXShield` now has payload/handler baseline and runic shield code paths send it again.

Что сделано:

- Implemented `PacketFXShield` payload serialization (`source`, `target`) and client-scheduled handler.
- Added client fallback routing for shield reactions:
  - always emits local burst at shield source;
  - emits bolt toward explicit attacker target when available;
  - emits directional fallback bolts for `target == -1/-2/-3` special cases.
- Restored packet sends in `EventHandlerRunic` for:
  - player runic shield absorption path (`64` radius);
  - champion shield path (`32` radius).
- Expanded test coverage:
  - `PacketFXSerializationTest` now includes `PacketFXShield` round-trip;
  - `ClientProxyFxStaticGuardTest` now asserts shield handler scheduling/proxy routing and active `EventHandlerRunic` send-sites.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This is fallback FX routing and does not yet port reference rune-render particle classes.

#### Checkpoint 2026-05-16 — GAP-8 runic shield sound cues restored

Статус: runic shield effect/charge sound hooks are now active again in runic/champion paths.

Что сделано:

- Restored player runic shield break-heal cue:
  - `TCSounds.RUNICSHIELDEFFECT` when healing upgrade triggers.
- Restored player emergency recharge cue:
  - `TCSounds.RUNICSHIELDCHARGE` when emergency upgrade refills charge.
- Restored champion/eldritch shield hit cue:
  - `TCSounds.RUNICSHIELDEFFECT` with reference-shaped hostile pitch variation.
- Expanded static guard coverage in `ClientProxyFxStaticGuardTest` to enforce presence of runic shield sound hooks.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This restores sound-cue hooks only; full reference shield rune-render class parity remains open.

#### Checkpoint 2026-05-16 — GAP-3 sonic packet baseline restored

Статус: `PacketFXSonic` payload/handler and eldritch guardian send-site are now wired.

Что сделано:

- Implemented `PacketFXSonic` payload serialization (`source` entity id) and client-scheduled handler.
- Added fallback sonic reaction routing via `Thaumcraft.proxy.burst(...)` at source entity center.
- Restored `EntityEldritchGuardian` sonic branch packet send:
  - `PacketHandler.INSTANCE.sendToAllAround(new PacketFXSonic(this.getEntityId()), TargetPoint(..., 32.0))`.
- Expanded FX tests:
  - `PacketFXSerializationTest` now includes `PacketFXSonic` round-trip.
  - `ClientProxyFxStaticGuardTest` now enforces sonic handler scheduling and guardian send-site presence.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This remains fallback visual behavior and does not yet include reference `FXSonic` renderer class parity.

#### Checkpoint 2026-05-16 — GAP-3 wisp-zap packet baseline restored

Статус: `PacketFXWispZap` payload/handler and `EntityWisp` send-site are now wired.

Что сделано:

- Implemented `PacketFXWispZap` payload serialization (`source`, `target`) and client-scheduled handler.
- Added fallback zap routing via `Thaumcraft.proxy.bolt(...)` between resolved source/target entities.
- Restored `EntityWisp` attack packet send in the reference attack window (`attackCounter == 20`) with 32-block broadcast radius.
- Expanded FX tests:
  - `PacketFXSerializationTest` now includes `PacketFXWispZap` round-trip.
  - `ClientProxyFxStaticGuardTest` now enforces wisp-zap handler scheduling/proxy routing and `EntityWisp` send-site presence.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This remains fallback visual behavior and does not yet port reference lightning renderer classes.

#### Checkpoint 2026-05-16 — GAP-3 zap packet and FocusShock chain send path restored

Статус: `PacketFXZap` payload/handler and `FocusShock` chain-lightning send-site are now wired.

Что сделано:

- Implemented `PacketFXZap` payload serialization (`source`, `target`) and client-scheduled handler.
- Added fallback zap routing via `Thaumcraft.proxy.bolt(...)` between resolved source/target entities.
- Restored `FocusShock.chainLightning(...)` packet send with reference-shaped target point:
  - `PacketHandler.INSTANCE.sendToAllAround(new PacketFXZap(center.getEntityId(), closest.getEntityId()), TargetPoint(..., 64.0))`.
- Expanded FX tests:
  - `PacketFXSerializationTest` now includes `PacketFXZap` round-trip.
  - `ClientProxyFxStaticGuardTest` now enforces zap handler scheduling/proxy routing and `FocusShock` send-site presence.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This remains fallback visual behavior and does not yet port reference `FXLightningBolt` renderer classes.

#### Checkpoint 2026-05-16 — GAP-3 block-zap packet and tile send paths restored

Статус: `PacketFXBlockZap` payload/handler baseline and active tile send-sites are now wired.

Что сделано:

- Implemented `PacketFXBlockZap` payload serialization (`x/y/z`, `dx/dy/dz`) and client-scheduled handler.
- Added fallback block-zap routing via `Thaumcraft.proxy.bolt(...)` and restored local zap sound playback on client.
- Restored packet sends in active tile paths:
  - `TileEldritchTrap.update(...)` now emits `PacketFXBlockZap` on trap hit.
  - `TileInfusionMatrix.inEvZap(...)` now emits `PacketFXBlockZap` for instability zap targets.
- Expanded FX tests:
  - `PacketFXSerializationTest` now includes `PacketFXBlockZap` round-trip.
  - `ClientProxyFxStaticGuardTest` now enforces block-zap handler scheduling/proxy routing and both tile send-sites.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This remains fallback visual behavior and does not yet port reference node/lightning renderer classes.

#### Checkpoint 2026-05-16 — GAP-3 essentia-source packet and drain send path restored

Статус: `PacketFXEssentiaSource` payload/handler baseline and essentia drain send-site are now wired.

Что сделано:

- Implemented `PacketFXEssentiaSource` payload serialization (`x/y/z`, `dx/dy/dz`, `color`) and client-scheduled handler.
- Added fallback essentia-source visual routing via `Thaumcraft.proxy.beam(...)` from sink to source offset.
- Restored essentia drain broadcast in `EssentiaHandler.drainEssentia(...)`:
  - sends `PacketFXEssentiaSource` on successful `takeFromContainer(...)` with 32-block target-point radius.
- Expanded FX tests:
  - `PacketFXSerializationTest` now includes `PacketFXEssentiaSource` round-trip.
  - `ClientProxyFxStaticGuardTest` now enforces essentia-source handler scheduling/proxy routing and active drain send-site presence.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This remains fallback visual behavior and does not yet port reference `FXEssentiaTrail` particle classes.

#### Checkpoint 2026-05-16 — GAP-3 infusion-source packet and matrix send paths restored

Статус: `PacketFXInfusionSource` payload/handler baseline and active infusion matrix send-sites are now wired.

Что сделано:

- Implemented `PacketFXInfusionSource` payload serialization (`x/y/z`, `dx/dy/dz`, `color`) and client-scheduled handler.
- Added fallback infusion-stream visual routing via `Thaumcraft.proxy.beam(...)`:
  - entity-target mode when packet carries player entity id (`dx/dy/dz == 0`);
  - pedestal-offset mode when packet carries source offset.
- Restored packet sends in active `TileInfusionMatrix` paths:
  - XP drain path (`drainRecipeXP`) now sends `PacketFXInfusionSource(..., 0,0,0, targetEntityId)`.
  - Ingredient pull wind-up path (`itemCount == 0`) now sends offset-based `PacketFXInfusionSource(...)`.
- Expanded FX tests:
  - `PacketFXSerializationTest` now includes `PacketFXInfusionSource` round-trip.
  - `ClientProxyFxStaticGuardTest` now enforces infusion-source handler scheduling/proxy routing and active matrix send-site presence.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This remains fallback visual behavior and does not yet port reference matrix-side `sourceFX` stream lifecycle classes.

#### Checkpoint 2026-05-16 — GAP-3 block-dig and block-bubble packet handler baseline restored

Статус: `PacketFXBlockDig` and `PacketFXBlockBubble` now have payload serialization and client-scheduled fallback handlers.

Что сделано:

- Implemented `PacketFXBlockBubble` payload (`x/y/z/color`) and client-scheduled fallback bubble rendering.
- Implemented `PacketFXBlockDig` payload (`x/y/z`, `bi/md`, `dx/dy/dz`) and client-scheduled fallback dig particle rendering:
  - block-backed path emits `BLOCK_CRACK` particles plus block hit sound;
  - item-backed path emits `ITEM_CRACK` particles.
- Expanded FX tests:
  - `PacketFXSerializationTest` now includes `PacketFXBlockBubble` and `PacketFXBlockDig` round-trips.
  - `ClientProxyFxStaticGuardTest` now enforces client scheduling and particle emission markers for both handlers.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Dedicated send-site restoration for these packet families remains open where corresponding gameplay paths are still stubbed.

#### Checkpoint 2026-05-16 — GAP-3 beam-pulse packet handler baseline restored

Статус: `PacketFXBeamPulse` and `PacketFXBeamPulseGolemBoss` now have payload serialization and client-scheduled fallback handlers.

Что сделано:

- Implemented `PacketFXBeamPulse` payload (`source`, `target`, `color`) and client-scheduled fallback beam routing through `Thaumcraft.proxy.beam(...)`.
- Implemented `PacketFXBeamPulseGolemBoss` payload (`source`, `target`) and client-scheduled dual-beam fallback routing for boss pulse visuals.
- Expanded FX tests:
  - `PacketFXSerializationTest` now includes round-trips for both beam-pulse packet types.
  - `ClientProxyFxStaticGuardTest` now enforces client scheduling and proxy beam routing markers for both handlers.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Dedicated server-side send-site restoration for these packet families remains open where corresponding gameplay paths are still stubbed.

#### Checkpoint 2026-05-16 — Bore dig replay channel restored (`PacketBoreDig` + `TileArcaneBore`)

Статус: arcane bore dig replay packet baseline is now wired end-to-end.

Что сделано:

- Implemented `PacketBoreDig` payload serialization (`x/y/z`, `digloc`) and client-scheduled handler.
- Restored bore-side dig replay path in `TileArcaneBore`:
  - sends `PacketBoreDig` around bore position on block mine events (64-block radius);
  - decodes packed dig offsets via `getDigEvent(...)`;
  - plays one-shot client fallback dig FX in `playClientDigFx(...)` with block crack particles and hit sound.
- Added `PacketBoreDigSerializationTest` and expanded `ClientProxyFxStaticGuardTest` coverage for packet handler + bore send/consume paths.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This remains fallback dig replay and does not yet port reference `boreDigFx` renderer classes.

#### Checkpoint 2026-05-16 — Warp event client signal packets restored (`PacketMiscEvent` + `PacketWarpMessage`)

Статус: warp-side client signal packets and `WarpEvents` send hooks are now wired again.

Что сделано:

- Implemented `PacketMiscEvent` payload/handler baseline:
  - `type` serialization;
  - client-scheduled handling for warp vignette marker (`WARP_EVENT`) and mist fog markers (`MIST_EVENT` / `MIST_EVENT_SHORT`).
- Implemented `PacketWarpMessage` payload/handler baseline:
  - `type` + `data` serialization;
  - client-scheduled warp notification text via `TextComponentTranslation`;
  - positive warp-change whisper cue via `TCSounds.WHISPERS`.
- Restored `WarpEvents` server send-sites:
  - warp trigger ping `PacketMiscEvent((short)0)`;
  - mist event ping `PacketMiscEvent((short)1)`;
  - sticky warp reduction notice `PacketWarpMessage(player, (byte)1, -1)`.
- Added static client marker fields used by packet handlers:
  - `ClientTickEventsFML.warpVignette`;
  - `RenderEventHandler.fogFiddled` / `RenderEventHandler.fogDuration`.
- Expanded tests:
  - `PacketMiscWarpSerializationTest` for round-trip payload coverage;
  - `ClientProxyFxStaticGuardTest` coverage for packet scheduling and `WarpEvents` send-site presence.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Full visual fog/vignette rendering behavior remains tied to broader Stage 8 client render handler parity.

#### Checkpoint 2026-05-16 — Eldritch guardian short-mist signal send path restored

Статус: guardian periodic short-mist packet broadcast is now active again.

Что сделано:

- Restored server-side send in `EntityEldritchGuardian.onUpdate()` for nearby players in non-Outer dimensions:
  - `PacketHandler.INSTANCE.sendTo(new PacketMiscEvent((short) 2), (EntityPlayerMP) player)`.
- Kept existing distance and difficulty gating logic intact; only reinstated the packet path where the Phase 8 placeholder existed.
- Expanded static guard coverage to enforce presence of this send-site.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Visual effect depth still depends on broader Stage 8 render/fog parity work.

#### Checkpoint 2026-05-16 — Crucible client FX hooks restored (`drawEffects` + block events)

Статус: `TileCrucible` client FX hooks are now wired again with proxy fallback visuals/sound.

Что сделано:

- Restored `TileCrucible.drawEffects()` and client update tick routing:
  - boiling froth via `Thaumcraft.proxy.crucibleFroth(...)`;
  - overflow edge froth via `Thaumcraft.proxy.crucibleFrothDown(...)`;
  - aspect-tinted bubble path via `Thaumcraft.proxy.crucibleBubble(...)`.
- Restored crucible client block-event handling in `receiveClientEvent(...)`:
  - id `1` now dispatches `Thaumcraft.proxy.blockSparkle(...)`;
  - id `2` now dispatches `Thaumcraft.proxy.crucibleBoilSound(...)` plus repeated `crucibleBoil(...)` particle calls.
- Added fallback proxy API surface for crucible FX:
  - `CommonProxy`: stubs for `crucibleFroth`, `crucibleFrothDown`, `crucibleBubble`, `crucibleBoilSound`, `crucibleBoil`;
  - `ClientProxy`: client particle/sound fallback implementations for all five methods.
- Expanded static guard coverage:
  - `TileCrucibleSmeltContractStaticGuardTest` now enforces crucible FX hook presence and removal of the old Phase 8 placeholders;
  - `ClientProxyFxStaticGuardTest` now enforces crucible proxy override presence.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This remains fallback particle/sound behavior; dedicated legacy crucible FX renderer parity is still part of broader Stage 8 visual-depth work.

#### Checkpoint 2026-05-16 — Eldritch guardian ambient wisp trail hook restored (`wispFXEG`)

Статус: guardian client ambient wisp trail path is now wired again through proxy helpers.

Что сделано:

- Compared `EntityEldritchGuardian` client update path against 1.7.10 reference (`Thaumcraft.proxy.wispFXEG(...)` call inside client tick branch).
- Restored client-side guardian ambient FX send in `EntityEldritchGuardian.onUpdate()`:
  - computes reference-shaped jittered local x/z coordinates;
  - calls `Thaumcraft.proxy.wispFXEG(world, x, posY + 0.22*height, z, this)`.
- Expanded proxy helper surface:
  - `CommonProxy` now exposes server-safe no-op `wispFXEG(...)`;
  - `ClientProxy` now overrides `wispFXEG(...)` with a fallback particle trail routed via existing `wispFX3(...)`.
- Expanded static guard coverage in `ClientProxyFxStaticGuardTest`:
  - enforces `ClientProxy.wispFXEG(...)` override presence;
  - enforces guardian call-site presence in `EntityEldritchGuardian`.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This remains fallback wisp-trail behavior and does not yet port dedicated reference `FXWispEG`/`ParticleEngine` class parity.

#### Checkpoint 2026-05-16 — Falling taint land FX hook restored (`taintLandFX`)

Статус: `EntityFallingTaint` client landing particle hook is now wired again through proxy helpers.

Что сделано:

- Compared `EntityFallingTaint` client branch against 1.7.10 reference and restored the landing FX loop:
  - client path (`onGround || fallTime == 1`) now emits 10 calls to `Thaumcraft.proxy.taintLandFX(this)`.
- Expanded proxy helper surface:
  - `CommonProxy` now exposes server-safe no-op `taintLandFX(Entity)`;
  - `ClientProxy` now overrides `taintLandFX(Entity)` with fallback purple sparkle/smoke landing particles.
- Expanded static guard coverage in `ClientProxyFxStaticGuardTest`:
  - enforces `ClientProxy.taintLandFX(...)` override presence;
  - enforces `EntityFallingTaint` call-site presence.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- This remains fallback taint landing visuals and does not yet port dedicated reference particle classes (`FXBreaking`/`ParticleEngine`) parity.

### GAP-4: Beam, wand beam, bore beam, power beam, arc, and lightning bolt classes are absent

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/client/fx/beams/**` absent.
- `src/main/java/thaumcraft/client/fx/bolt/**` absent.
- `src/main/java/thaumcraft/client/ClientProxy.java:98-105` has no-op `beam` and `bolt`.
- `PacketFXBeamPulse` and `PacketFXBeamPulseGolemBoss` now use fallback proxy beam visuals; dedicated beam renderer classes are still absent.
- `PacketFXZap`, `PacketFXWispZap`, and `PacketFXBlockZap` currently use fallback proxy bolt visuals; dedicated beam/lightning renderer classes are still absent.

**Референс:**
- `thaumcraft_src/thaumcraft/client/fx/beams/FXArc.class`
- `thaumcraft_src/thaumcraft/client/fx/beams/FXBeam.class`
- `thaumcraft_src/thaumcraft/client/fx/beams/FXBeamBore.class`
- `thaumcraft_src/thaumcraft/client/fx/beams/FXBeamGolemBoss.class`
- `thaumcraft_src/thaumcraft/client/fx/beams/FXBeamPower.class`
- `thaumcraft_src/thaumcraft/client/fx/beams/FXBeamWand.class`
- `thaumcraft_src/thaumcraft/client/fx/bolt/FXLightningBolt.class`
- `thaumcraft_src/thaumcraft/client/fx/bolt/FXLightningBoltCommon.class`
- `thaumcraft_src/thaumcraft/client/fx/bolt/FXLightningBoltCommon$*.class`

**Что не совпадает:**

Reference lightning/beam system is a dedicated renderer family with fractal lightning generation, multiple beam modes, pulse/reverse/end-mod controls, entity-targeted and coordinate-targeted beams, and golem boss variants. Current port has no class equivalents, so shock/arc/zap/beam packets and wand/focus visual feedback cannot match reference.

**Что нужно доделать:**

Port beam and bolt class families and wire them into proxy helpers and FX packet handlers.

**Как доделать:**
- Add beam classes under `src/main/java/thaumcraft/client/fx/beams/`.
- Add lightning classes under `src/main/java/thaumcraft/client/fx/bolt/`.
- Implement `ClientProxy.beam`, `ClientProxy.bolt`, and specific helpers needed by packets: arc lightning, beam pulse, golem boss pulse, block zap, wisp zap.
- Adapt original GL/Tessellator rendering to 1.12.2 render APIs and verify blending/depth state restoration.
- Use reference textures `beam.png`, `beam1.png`, `beam2.png`, `beam3.png`, `beamh.png`.

**Критерии приемки:**
- [ ] `PacketFXZap`, `PacketFXBlockZap`, and `PacketFXWispZap` create visible lightning bolts matching source/target semantics.
- [ ] `PacketFXBeamPulse` and `PacketFXBeamPulseGolemBoss` create visible beams with reference color/pulse behavior.
- [ ] `PacketFXBlockArc` creates visible arc lightning from block to entity/coordinate as in reference.
- [ ] Beam/bolt render state does not corrupt subsequent world rendering.

**Риски / зависимости:**

Rendering state bugs can affect the whole client frame. Requires GAP-9 resources. Some focus/entity send sites may also need GAP-7 routing.

### GAP-5: Required particle, beam, ward, and overlay textures are missing from current resources

**Статус:** частично реализовано
**Критичность:** blocker

**Текущая реализация:**
- `src/main/resources/assets/thaumcraft/textures/misc/` now includes the Stage 8-e baseline FX atlases and overlays copied from `thaumcraft_src`, including:
  - `particles.png`, `particles.png.mcmeta`, `particles2.png`, `particles2.png.mcmeta`
  - `particlefield.png`, `particlefield.png.mcmeta`, `particlefield32.png`
  - `beam.png`, `beam1.png`, `beam2.png`, `beam3.png`, `beamh.png`
  - `wisp.png`, `wisp.png.mcmeta`, `wispy.png`, `wispy.png.mcmeta`
  - `vortex.png`, `vortex.png.mcmeta`, `tunnel.png`, `vignette.png`, `vignette.png.mcmeta`

**Референс:**
- `thaumcraft_src/assets/thaumcraft/textures/misc/particles.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/particles.png.mcmeta`
- `thaumcraft_src/assets/thaumcraft/textures/misc/particles2.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/particles2.png.mcmeta`
- `thaumcraft_src/assets/thaumcraft/textures/misc/particlefield.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/particlefield32.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/beam.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/beam1.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/beam2.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/beam3.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/beamh.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/wisp.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/wispy.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/vortex.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/tunnel.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/vignette.png`

**Что не совпадает:**

The reference FX classes bind and animate texture atlases under `textures/misc`. Current resources do not contain the particle atlases or beam/warp textures, so even correctly ported classes would produce missing-texture output or fail to load expected resources.

**Что нужно доделать:**

Copy required FX assets from the read-only reference asset tree into current resources, preserving paths and metadata.

**Как доделать:**
- Copy only required files from `thaumcraft_src/assets/thaumcraft/textures/misc/` to `src/main/resources/assets/thaumcraft/textures/misc/`.
- Include `.mcmeta` animation metadata for animated textures.
- Do not recreate or rename assets.
- After implementation, scan client log for missing texture/resource warnings in Stage 8-e scenarios.

**Критерии приемки:**
- [x] Current resources contain `particles.png`, `particles2.png`, and their `.mcmeta` files.
- [x] Current resources contain all beam textures used by ported beam classes.
- [x] Current resources contain wisp/ward/overlay textures required by ported FX classes.
- [ ] Client log has no missing-resource errors for Stage 8-e FX scenarios.

**Риски / зависимости:**

Low implementation risk because `AGENTS.md:14-17` explicitly allows copying assets from `thaumcraft_src/assets/` as source of truth. Acceptance still depends on client runtime validation.

#### Checkpoint 2026-05-16 — GAP-5 misc FX texture baseline copied from reference assets

Статус: critical Stage 8-e misc FX texture baseline is now present under `src/main/resources/assets/thaumcraft/textures/misc/`.

Что сделано:

- Copied missing FX atlases and overlays directly from `thaumcraft_src/assets/thaumcraft/textures/misc/` into the port resource tree.
- Included animation metadata files (`.mcmeta`) for animated particle/overlay assets.
- Added static resource coverage test `FxTextureAssetCoverageTest` to enforce the baseline file set in non-GUI validation runs.

Проверки:

- `./scripts/dev.sh test` — passed.
- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Client-side runtime texture-binding/log validation remains open due GUI/manual validation exclusion.

### GAP-6: Portable Hole and Warding visual feedback is missing

**Статус:** отсутствует / частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `AGENTS.md:53-58` explicitly lists Portable Hole/Warding visual renderers as deferrals.
- `src/main/java/thaumcraft/common/items/wands/foci/FocusWarding.java:71-85` changes blocks and plays `TCSounds.ZAP`, but sends no ward visual packet and calls no `blockWard` helper.
- `src/main/java/thaumcraft/common/items/wands/foci/FocusPortableHole.java:100-112` creates hole blocks and plays teleport/fail sound, but sends no visual helper/packet.
- `src/main/java/thaumcraft/client/fx/other/FXBlockWard.java` absent.

**Референс:**
- `thaumcraft_src/thaumcraft/client/fx/other/FXBlockWard.class`
- `thaumcraft_src/thaumcraft/client/ClientProxy.class` method `blockWard(...)`
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileHoleRenderer.class`
- `thaumcraft_src/thaumcraft/client/renderers/tile/TileWardedRenderer.class`

**Что не совпадает:**

Warding and Portable Hole have server baselines, but visual parity is deferred. Warding lacks the block-face ward shimmer/rune effect represented by reference `FXBlockWard` and `ClientProxy.blockWard`. Portable Hole has no visual renderer/FX in the current scope and only plays sounds after server block changes.

**Что нужно доделать:**

Implement the Stage 8-e FX side of Warding and Portable Hole visuals and define any dependency boundary with Stage 8 TESR work.

**Как доделать:**
- Add/port `FXBlockWard` under `src/main/java/thaumcraft/client/fx/other/`.
- Add client proxy `blockWard` helper if needed by packet/focus routing.
- Send/trigger Warding visuals when blocks are warded/unwarded, preserving side and color behavior from reference.
- For Portable Hole, verify whether visual parity is owned by `TileHoleRenderer`/TESR or a direct FX helper. If TESR-owned, label that as dependency and implement Stage 8-e only for direct FX/sound coupling.
- Add missing textures needed by ward/hole visuals from `thaumcraft_src/assets/thaumcraft/textures/misc/**`.

**Критерии приемки:**
- [ ] Warding focus block ward/unward action shows a visible reference-equivalent ward effect.
- [ ] Portable Hole creation shows its reference visual effect or documented direct dependency on Stage 8 renderer work with no missing Stage 8-e FX hook.
- [ ] Warding and Portable Hole effects are client-only and do not classload client FX on a dedicated server.
- [ ] Manual scenario confirms sound plus visual feedback for both foci.

**Риски / зависимости:**

Dependency: Portable Hole full visual parity may require Stage 8 renderer/TESR work outside this chunk (`TileHoleRenderer`). This document does not analyze renderer implementation deeply by request.

### GAP-7: Runic shield and champion shield FX/sounds are disabled

**Статус:** отсутствует / частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:208-211`
- `src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:246-257`
- `src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java:307-311`
- `src/main/java/thaumcraft/common/lib/network/fx/PacketFXShield.java:1-7`
- `src/main/java/thaumcraft/common/lib/TCSounds.java:60-61`
- `src/main/resources/assets/thaumcraft/sounds.json:44-45`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/network/fx/PacketFXShield.class`
- `thaumcraft_src/thaumcraft/client/fx/other/FXShieldRunes.class`
- `thaumcraft_src/assets/thaumcraft/sounds/runicShieldEffect.ogg`
- `thaumcraft_src/assets/thaumcraft/sounds/runicShieldCharge.ogg`
- `thaumcraft_src/assets/thaumcraft/sounds.json`

**Что не совпадает:**

Current runic gameplay computes absorption and charge, but the shield FX packet send and coupled sounds are commented out. `PacketFXShield` has no fields/handler, and `FXShieldRunes` is absent. Sound registry entries and assets are present, but calls are disabled.

**Что нужно доделать:**

Port `PacketFXShield` and `FXShieldRunes`, then re-enable packet sends and shield sounds in runic/champion shield events.

**Как доделать:**
- Implement `PacketFXShield` payload `(sourceEntityId, targetEntityIdOrSentinel)` and client handler.
- Port `FXShieldRunes` under `src/main/java/thaumcraft/client/fx/other/`.
- Re-enable `PacketHandler.INSTANCE.sendToAllAround(new PacketFXShield(...))` at player and mob shield hit sites.
- Re-enable `TCSounds.RUNICSHIELDEFFECT` and `TCSounds.RUNICSHIELDCHARGE` plays with reference-like category/volume/pitch.
- Verify sentinel target behavior: normal attacker entity, `-1` general, `-2` fall, `-3` fly-into-wall.

**Критерии приемки:**
- [ ] Player runic shield hit renders directional runes.
- [ ] Fall and wall collision shield cases render the correct rune orientation.
- [ ] Champion/eldritch mob shield visual triggers around the mob.
- [ ] `runicShieldEffect` and `runicShieldCharge` sounds play when reference behavior expects them.

**Риски / зависимости:**

Depends on GAP-1 and GAP-3. Champion modifier logic itself has current simplifications outside Stage 8-e, but shield FX must work for the current eligible entity paths.

### GAP-8: Warp visual feedback and post-processing/overlay resources are not routed

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/WarpEvents.java:77`
- `src/main/java/thaumcraft/common/lib/WarpEvents.java:132`
- `src/main/java/thaumcraft/common/lib/WarpEvents.java:210-212`
- `src/main/java/thaumcraft/common/lib/network/misc/PacketMiscEvent.java`
- `src/main/java/thaumcraft/common/lib/network/playerdata/PacketWarpMessage.java`
- `src/main/resources/assets/thaumcraft/textures/misc/vortex.png` absent.
- `src/main/resources/assets/thaumcraft/textures/misc/tunnel.png` absent.
- `src/main/resources/assets/thaumcraft/textures/misc/vignette.png` absent.

**Референс:**
- `thaumcraft_src/assets/thaumcraft/textures/misc/vortex.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/vortex.png.mcmeta`
- `thaumcraft_src/assets/thaumcraft/textures/misc/tunnel.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/vignette.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/vignette.png.mcmeta`
- `thaumcraft_src/thaumcraft/client/ClientProxy.class`
- `thaumcraft_src/thaumcraft/common/lib/network/misc/PacketMiscEvent.class`
- `thaumcraft_src/thaumcraft/common/lib/network/playerdata/PacketWarpMessage.class`

**Что не совпадает:**

The reference has warp/overlay texture resources and packet-driven client feedback. Current `WarpEvents` comments out visual packet sends for generic warp pulse, sticky warp message, and mist event. Current shader directory is absent in both current and reference trees, so the practical reference target is texture-based overlay/post-processing behavior, not shader files.

**Что нужно доделать:**

Port or complete warp visual routing through misc/playerdata packets and add required overlay textures.

**Как доделать:**
- Inspect/decompile `thaumcraft_src/thaumcraft/common/lib/network/misc/PacketMiscEvent.class` and `PacketWarpMessage.class` before implementation.
- Complete current packet handlers if they do not already implement visual behavior.
- Re-enable sends at `WarpEvents.java:77`, `WarpEvents.java:132`, and `WarpEvents.java:210-212` only after handlers are client-safe.
- Add overlay textures from reference resources.
- If Forge 1.12.2 shader/post-processing is unsupported or intentionally deferred, document explicit fallback behavior and verify no hard missing shader resource exists in reference.

**Критерии приемки:**
- [ ] Warp event triggers visible client feedback when the original sends `PacketMiscEvent(0)`.
- [ ] Warp mist event triggers visible client feedback when original sends `PacketMiscEvent(1)`.
- [ ] Sticky warp changes use `PacketWarpMessage` visual/message behavior matching reference.
- [ ] No missing shader/resource warnings occur for warp feedback; any unsupported shader behavior is explicitly disabled with a reason.

**Риски / зависимости:**

Dependency: some `PacketMiscEvent` behavior may overlap with broader client overlay/HUD work. Keep Stage 8-e implementation limited to FX/post-processing feedback, not GUI analysis.

### GAP-9: Taint, wisp, firebat, and mob ambient FX call sites currently resolve to no-op helpers

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/entities/monster/EntityWisp.java:173-204`
- `src/main/java/thaumcraft/common/entities/monster/EntityFireBat.java:200-205`
- `src/main/java/thaumcraft/common/entities/monster/EntityTaintacle.java:76-79`
- `src/main/java/thaumcraft/common/CommonProxy.java:150-157`
- `src/main/java/thaumcraft/client/ClientProxy.java:90-105`

**Референс:**
- `thaumcraft_src/thaumcraft/client/fx/particles/FXBurst.class`
- `thaumcraft_src/thaumcraft/client/fx/particles/FXWisp.class`
- `thaumcraft_src/thaumcraft/client/fx/particles/FXSparkle.class`
- `thaumcraft_src/thaumcraft/client/fx/particles/FXBreaking.class`
- `thaumcraft_src/thaumcraft/client/fx/particles/FXSmokeTrail.class`
- `thaumcraft_src/thaumcraft/client/fx/particles/FXSwarm.class`
- `thaumcraft_src/thaumcraft/client/ClientProxy.class`

**Что не совпадает:**

Current entities already call proxy FX helpers on client side, but those helpers are either not overridden in `ClientProxy` or are no-op inherited common methods. This means wisp spawn/death/attack bursts, wisp aura trails, explosive firebat sparkles, and taintacle arise bursts are silently missing.

**Что нужно доделать:**

Implement the helper methods and particle classes needed by these existing call sites.

**Как доделать:**
- Implement `ClientProxy.burst`, `ClientProxy.wispFX3`, and `ClientProxy.sparkle`.
- Add/port `FXBurst`, `FXWisp`, `FXSparkle`, plus any dependencies from reference particles.
- Verify parameter mapping in `EntityWisp.java:197-203`, where the current call passes color red channel as the final `speed/gravity` argument; compare with reference entity behavior before changing code.
- Implement taint-specific helpers if further current taint entities call no-op methods after broader search.

**Критерии приемки:**
- [ ] Wisp spawn, death, attack hit, and idle aura render visible FX.
- [ ] Explosive firebat renders fire sparkle trail while explosive.
- [ ] Taintacle spawn/arise renders visible burst FX.
- [ ] Existing client-only call sites do not crash dedicated server or integrated server.

**Риски / зависимости:**

Depends on GAP-1 and GAP-2. Static review cannot confirm visual correctness; manual client scenario required.

### GAP-10: Wand/focus lightning and beam visual routing is incomplete

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/items/wands/foci/FocusShock.java:92-101`
- `src/main/java/thaumcraft/common/items/wands/foci/FocusWarding.java:71-85`
- `src/main/java/thaumcraft/common/items/wands/foci/FocusPortableHole.java:100-112`
- `src/main/java/thaumcraft/common/lib/network/fx/PacketFXZap.java`
- `src/main/java/thaumcraft/common/lib/network/fx/PacketFXWispZap.java`
- `src/main/java/thaumcraft/common/lib/network/fx/PacketFXBlockZap.java`
- `src/main/java/thaumcraft/client/ClientProxy.java:98-105`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/network/fx/PacketFXZap.class`
- `thaumcraft_src/thaumcraft/common/lib/network/fx/PacketFXWispZap.class`
- `thaumcraft_src/thaumcraft/common/lib/network/fx/PacketFXBlockZap.class`
- `thaumcraft_src/thaumcraft/client/fx/bolt/FXLightningBolt.class`
- `thaumcraft_src/thaumcraft/client/fx/beams/FXBeamWand.class`
- `thaumcraft_src/thaumcraft/client/ClientProxy.class`

**Что не совпадает:**

FocusShock, wisp-zap, and block-zap packet paths now send target-bearing packets and trigger fallback bolt visuals, but they still do not use reference `FXLightningBolt` renderer classes. Warding and Portable Hole play sounds but lack reference visual feedback. The reference creates `FXLightningBolt` for zap packets and continuous wand beam helpers for wand use cases.

**Что нужно доделать:**

Restore visual packet sends/helpers for wand/focus effects where reference behavior includes them.

**Как доделать:**
- Decompile/reference original `FocusShock`, `FocusWarding`, and `FocusPortableHole` before editing implementation.
- Add packet sends for shock target and chain lightning only where original sends them.
- Use `FXBeamWand`/continuous beam helper if original focus behavior requires held-use beam visuals.
- Keep server gameplay unchanged while adding client feedback.

**Критерии приемки:**
- [ ] Shock focus hit shows visible lightning from caster/source to target.
- [ ] Chain lightning visually jumps between chained entities.
- [ ] Warding and Portable Hole retain existing sounds and add reference-equivalent visuals.
- [ ] Visual packets are range-limited and side-safe.

**Риски / зависимости:**

Depends on GAP-3, GAP-4, GAP-6. Focus server behavior is Stage 5 dependency only if original target selection must be corrected to know where to render lightning.

#### Checkpoint 2026-05-17 — GAP-10 warding/portable-hole sparkle send-path baseline

Статус: частично продвинут.

Что сделано:

- `FocusWarding` теперь восстанавливает reference-shaped visual send-path:
  - в обоих ветках (ward и unward) добавлен `PacketFXBlockSparkle` broadcast (`0xFC9A00`, radius `32.0D`) через `PacketHandler.INSTANCE.sendToAllAround(...)`.
- `FocusPortableHole.createHole(...)` теперь восстанавливает block-sparkle cue:
  - добавлен `PacketFXBlockSparkle` broadcast (`0x400040`, radius `32.0D`) после успешной постановки `blockHole`.
- `ClientProxyFxStaticGuardTest` расширен статическими контрактами на оба focus send-path.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это сетевой visual baseline без полноценного port-а reference `FXBlockWard`/`FXBeamWand`/`FXLightningBolt`; ручная визуальная parity проверка остаётся вне текущего non-GUI scope.

### GAP-11: Sonic, essentia/vis drain, infusion source, bore/block dig/bubble/source FX packets are not functional

**Статус:** отсутствует / частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/entities/monster/EntityEldritchGuardian.java:291-292`
- `src/main/java/thaumcraft/common/lib/utils/Utils.java:39-41`
- `src/main/java/thaumcraft/common/lib/network/misc/PacketBoreDig.java`
- `src/main/java/thaumcraft/common/tiles/TileArcaneBore.java`
- `src/main/java/thaumcraft/common/lib/network/fx/PacketFXSonic.java`
- `src/main/java/thaumcraft/common/lib/network/fx/PacketFXVisDrain.java`
- `src/main/java/thaumcraft/common/lib/network/fx/PacketFXEssentiaSource.java`
- `src/main/java/thaumcraft/common/lib/network/fx/PacketFXInfusionSource.java`
- `src/main/java/thaumcraft/common/lib/network/fx/PacketFXBlockDig.java`
- `src/main/java/thaumcraft/common/lib/network/fx/PacketFXBlockBubble.java`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/network/fx/PacketFXSonic.class`
- `thaumcraft_src/thaumcraft/client/fx/other/FXSonic.class`
- `thaumcraft_src/thaumcraft/common/lib/network/fx/PacketFXVisDrain.class`
- `thaumcraft_src/thaumcraft/common/lib/network/fx/PacketFXEssentiaSource.class`
- `thaumcraft_src/thaumcraft/common/lib/network/fx/PacketFXInfusionSource.class`
- `thaumcraft_src/thaumcraft/common/lib/network/fx/PacketFXBlockDig.class`
- `thaumcraft_src/thaumcraft/common/lib/network/fx/PacketFXBlockBubble.class`
- `thaumcraft_src/thaumcraft/client/fx/particles/FXEssentiaTrail.class`
- `thaumcraft_src/thaumcraft/client/fx/particles/FXBoreParticles.class`
- `thaumcraft_src/thaumcraft/client/fx/particles/FXBoreSparkle.class`
- `thaumcraft_src/thaumcraft/client/fx/particles/FXBubble.class`

**Что не совпадает:**

Several registered packet types represent non-wand FX channels used by mobs, aura/vis/essentia transfer, infusion, bore/block interactions, and bubbles. `PacketFXSonic`, `PacketFXVisDrain`, `PacketFXEssentiaSource`, `PacketFXInfusionSource`, `PacketFXBlockDig`, `PacketFXBlockBubble`, and bore replay `PacketBoreDig` now have fallback handlers; remaining gaps are mostly renderer-parity depth and missing send-sites in still-stubbed gameplay paths.

**Что нужно доделать:**

Port these packet payloads and their particle/other FX classes, then re-enable send sites such as Eldritch Guardian sonic.

**Как доделать:**
- Implement `PacketFXSonic` and port `FXSonic`; re-enable send at `EntityEldritchGuardian.java:291-292` after validating original behavior.
- Implement `PacketFXVisDrain` client handler using reference vis/essentia trail visuals.
- Implement block dig/bubble packet payloads and helper particles.
- Verify current server send sites exist for these packets; add reference-equivalent sends only where currently missing and directly in Stage 8-e scope.

**Критерии приемки:**
- [ ] Vis drain packet from `Utils.sendVisDrainFX` creates a visible trail.
- [ ] Eldritch Guardian sonic event creates visible sonic FX and preserves blindness/warp behavior.
- [ ] Infusion/essentia source packets render visible source streams when invoked.
- [ ] Block dig/bubble packets render reference-equivalent particles.

**Риски / зависимости:**

Depends on GAP-1 and GAP-3. Some infusion source scenarios may require Stage 9 content/recipe progression to reach naturally; direct test hooks or controlled world setup may be needed for validation.

#### Checkpoint 2026-05-17 — GAP-11 elemental-axe bubble packet send-site baseline

Статус: частично продвинут.

Что сделано:

- `ItemElementalAxe` больше не stub:
  - восстановлен right-click use contract (`setActiveHand`, `EnumAction.BOW`, `72000` use duration);
  - восстановлен thaumium repair contract (`itemResource:2`);
  - восстановлен item-magnet `onUsingTick(...)` baseline с pull-clamp и `Thaumcraft.proxy.crucibleBubble(...)` trail;
  - восстановлен wood-log break FX send-path (`PacketFXBlockBubble` + `TargetPoint` radius `32.0D`) и bubble sound cue.
- Добавлен `ItemElementalAxeStaticGuardTest` для фиксации core behavior/surface contracts.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это server/common + packet baseline без ручной gameplay/visual проверки; точная parity `BlockUtils.breakFurthestBlock` traversal-логики остаётся зависимой от дальнейшего Stage 5/6 polish.

#### Checkpoint 2026-05-17 — GAP-11 elemental-pickaxe fire/use feedback baseline

Статус: частично продвинут.

Что сделано:

- `ItemElementalPickaxe` больше не stub:
  - восстановлены `toolClasses`/`rarity`/thaumium-repair contracts (`pickaxe`, `RARE`, `itemResource:2`);
  - восстановлен reference-shaped fire-on-hit hook (`onLeftClickEntity` -> `entity.setFire(2)` server-side);
  - восстановлен on-use feedback baseline (`stack.damageItem(5, player)` + `TCSounds.WANDFAIL` server cue + client `swingArm` fallback).
- Добавлен `ItemElementalPickaxeStaticGuardTest` для фиксации этих контрактов.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Полноценный client scan-overlay path (`RenderEventHandler.startScan(...)` в оригинале) в текущем порте отсутствует и остаётся отдельным Stage 8-e renderer/overlay gap.

#### Checkpoint 2026-05-17 — GAP-11 elemental-sword lift/chain-hit baseline

Статус: частично продвинут.

Что сделано:

- `ItemElementalSword` больше не stub:
  - восстановлены `RARE` rarity и thaumium repair contracts (`itemResource:2`);
  - восстановлен active-use контракт (`setActiveHand`, `72000` use duration, `EnumAction.BLOCK`);
  - восстановлен reference-shaped lift/fall-control baseline в `onUsingTick(...)` (`motionY` control + `Utils.resetFloatCounter(...)`);
  - восстановлен nearby-entity push baseline во время active-use;
  - восстановлен chain-hit AoE baseline в `onLeftClickEntity(...)` через secondary-target sweep и `player.attackTargetEntityWithCurrentItem(...)`;
  - восстановлены sound/particle feedback cues (`TCSounds.WIND`, `TCSounds.SWING`, `SMOKE_NORMAL`).
- Добавлен `ItemElementalSwordStaticGuardTest` для фиксации core behavior/surface contracts.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это server/common baseline без ручной gameplay/client visual parity проверки; точная parity для legacy `smokeSpiral(...)` visual path и pre-1.9 melee internals остаётся открытой до отдельного client/manual checkpoint.

#### Checkpoint 2026-05-17 — GAP-11 elemental-hoe till/bonemeal/tree-growth baseline

Статус: частично продвинут.

Что сделано:

- `ItemElementalHoe` больше не stub:
  - восстановлены enchantability/rarity/thaumium-repair контракты (`5`, `RARE`, `itemResource:2`);
  - восстановлен reference-shaped non-sneak `3x3` till baseline через `super.onItemUse(...)` sweep и `Thaumcraft.proxy.blockSparkle(...)`;
  - восстановлен fallback-путь bonemeal применения на target-pos с durability/sparkle/sound feedback;
  - восстановлен custom-sapling growth baseline для `blockCustomPlant` meta `0/1` (`growGreatTree`/`growSilverTree`) с reference-shaped durability thresholds/costs.
- `BlockCustomPlant.growGreatTree(...)` и `growSilverTree(...)` открыты как `public` для соответствия call-surface референса.
- Добавлен `ItemElementalHoeStaticGuardTest` для фиксации этих контрактов.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Bonemeal path реализован через `ItemDye.applyBonemeal(...)` (1.12-совместимый baseline) вместо отсутствующего legacy `Utils.useBonemealAtLoc(...)`; manual gameplay parity всё ещё требует отдельной проверки.

#### Checkpoint 2026-05-17 — GAP-11 elemental-shovel architect/burst baseline

Статус: частично продвинут.

Что сделано:

- `ItemElementalShovel` больше не stub и теперь реализует reference-shaped surface:
  - восстановлены `IArchitect` + `toolClasses`/`rarity`/thaumium-repair контракты (`shovel`, `RARE`, `itemResource:2`);
  - восстановлен `onItemUse(...)` placement-copy baseline с `3x3` sweep, orientation-aware plane offsets, inventory consume path и sparkle feedback;
  - восстановлен `onBlockStartBreak(...)` side-capture baseline (raytrace side index);
  - восстановлен `onBlockDestroyed(...)` burst-mining baseline (`3x3` plane harvest path при non-sneak и effective block gate);
  - сохранены/нормализованы orientation NBT helpers (`or`, `o % 3`) и architect preview list (`getArchitectBlocks(...)` + `showAxis(...)`).
- Добавлен `ItemElementalShovelStaticGuardTest` для фиксации этих контрактов.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Burst-mining path сейчас использует `world.destroyBlock(..., true)` как 1.12-safe baseline вместо legacy `BlockUtils.harvestBlock(...)` семантики; детальный parity по enchant/harvest edge-cases остаётся для отдельного polish checkpoint.

#### Checkpoint 2026-05-17 — GAP-11 elemental-pickaxe scan-hook baseline

Статус: частично продвинут.

Что сделано:

- Для `ItemElementalPickaxe` восстановлен reference-shaped client scan trigger path:
  - client branch `onItemUse(...)` теперь вызывает `Thaumcraft.proxy.startScan(player, pos, System.currentTimeMillis() + 5000L, 8)` перед `swingArm(...)`.
- Добавлена side-safe proxy surface:
  - `CommonProxy.startScan(Entity, BlockPos, long, int)` — server-safe no-op stub;
  - `ClientProxy.startScan(...)` — client routing в `RenderEventHandler.startScan(...)`.
- `RenderEventHandler` получил scan-state baseline (`scanEntityId`, `scanPos`, `scanExpireAtMs`, `scanRange`) и `startScan(...)` hook с базовым lifecycle cleanup в `livingTick`.
- Статические проверки расширены:
  - `ItemElementalPickaxeStaticGuardTest` now guards `startScan` call-site;
  - `ClientProxyFxStaticGuardTest` now guards proxy surface + render handler scan-state baseline.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это baseline-триггер scan lifecycle без полного HUD/overlay-рендера (`RenderEventHandler` рендер-пути всё ещё требуют отдельного Stage 8-e client polish).

#### Checkpoint 2026-05-17 — GAP-11 thaumium-tools rarity/repair contract baseline

Статус: частично продвинут.

Что сделано:

- Восстановлены reference-shaped базовые контракты для `ItemThaumium*` equipment:
  - `ItemThaumiumSword`: `UNCOMMON` rarity + thaumium repair (`itemResource:2`);
  - `ItemThaumiumAxe`: `axe` toolClass + `UNCOMMON` rarity + thaumium repair;
  - `ItemThaumiumPickaxe`: `pickaxe` toolClass + `UNCOMMON` rarity + thaumium repair;
  - `ItemThaumiumShovel`: `shovel` toolClass + `UNCOMMON` rarity + thaumium repair;
  - `ItemThaumiumHoe`: enchantability `5` + `UNCOMMON` rarity + thaumium repair.
- Добавлен `ItemThaumiumToolsStaticGuardTest` для фиксации этих family-contracts.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это контрактный baseline без ручной gameplay проверки tool-balance; combat/efficiency runtime parity остаётся в общей Stage 5/6 manual зоне.

#### Checkpoint 2026-05-17 — GAP-11 void/crimson equipment debuff+repair baseline

Статус: частично продвинут.

Что сделано:

- Восстановлены reference-shaped core contracts для void/crimson equipment family:
  - `ItemVoidSword`: `UNCOMMON` rarity + pvp-gated combat-debuff helper surface (`canApplyVoidCombatDebuff`, `tryApplyVoidWither`) + sword-hit wither baseline (`60` ticks);
  - `ItemVoidAxe`/`ItemVoidPickaxe`/`ItemVoidShovel`/`ItemVoidHoe`: `UNCOMMON` rarity, toolClass contracts (`axe`/`pickaxe`/`shovel`), `VoidHoe` enchantability `5`, pvp-gated wither-on-hit baseline (`80` ticks), shared self-repair baseline (`repairVoid`) и shared void-charm repair key;
  - `ItemCrimsonSword`: `IWarpingGear` surface, `RARE` rarity, void-charm repair key, pvp-gated dual-debuff hit baseline (`WITHER 60`, `WEAKNESS 120`), shared self-repair baseline, warp `2`.
- Добавлен `ItemVoidCrimsonToolsStaticGuardTest` для фиксации family contracts.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это gameplay-contract baseline; client tooltip/icon parity для void/crimson equipment остаётся отдельным Stage 8 renderer/UI polish.

#### Checkpoint 2026-05-17 — GAP-11 primal-arrow subtype registry baseline

Статус: частично продвинут.

Что сделано:

- `ItemPrimalArrow.getSubItems(...)` восстановлен до reference-shaped subtype coverage:
  - creative listing теперь добавляет все primal variants `meta 0..5` (вместо только `0`).
- Добавлен `ItemPrimalArrowStaticGuardTest`, фиксирующий:
  - subtype/max-stack init surface;
  - translation-key metadata suffix;
  - `meta 0..5` creative-variant contract.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это metadata-availability baseline; визуальная икон-парити primal arrow variants остаётся в client-render/manual зоне.

#### Checkpoint 2026-05-17 — GAP-11 bone-bow core draw contracts baseline

Статус: частично продвинут.

Что сделано:

- `ItemBowBone` приведён к reference-shaped core contracts:
  - durability baseline `setMaxDamage(512)` (вместо `500`);
  - enchantability baseline `3` (вместо `15`);
  - сохранён bone repair contract (`Items.BONE`);
  - добавлен early-release draw hook в `onUsingTick(...)` (`ticks > 18 -> stopActiveHand()`).
- Добавлен `ItemBowBoneStaticGuardTest` для фиксации этих контрактов.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это server/common contract baseline без полного портирования legacy custom bow charge/arrow event internals; детальная projectile-feel parity остаётся на отдельный gameplay polish.

#### Checkpoint 2026-05-17 — GAP-11 thaumium-armor/goggles repair+rarity baseline

Статус: частично продвинут.

Что сделано:

- Восстановлены базовые reference-shaped контракты для двух armor-классов:
  - `ItemThaumiumArmor`: `UNCOMMON` rarity + thaumium repair key (`itemResource:2`);
  - `ItemGoggles`: `RARE` rarity + leather repair key (`Items.LEATHER`).
- Добавлен `ItemArmorRepairRarityStaticGuardTest` для фиксации этих контрактов.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это только repair/rarity baseline; дополнительные armor tooltip/icon paths остаются в общем Stage 8 client polish.

#### Checkpoint 2026-05-17 — GAP-11 cultist-armor core contracts baseline

Статус: частично продвинут.

Что сделано:

- Восстановлены reference-shaped core contracts для cultist armor family:
  - `ItemCultistRobeArmor`: `UNCOMMON` rarity + leather repair + `IVisDiscountGear`/`IWarpingGear` surface (`1%`/`warp 1`);
  - `ItemCultistPlateArmor`: `UNCOMMON` rarity + leather repair;
  - `ItemCultistLeaderArmor`: `RARE` rarity + leather repair;
  - `ItemCultistBoots`: `UNCOMMON` rarity + leather repair + `IWarpingGear`/`IVisDiscountGear` surface (`warp 1`/`1%`).
- Добавлен `ItemCultistArmorCoreContractsStaticGuardTest` для фиксации этих family contracts.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это contract-level baseline; полный model/icon/tooltip parity для cultist armor остаётся на отдельный Stage 8 client-render polish.

#### Checkpoint 2026-05-17 — GAP-11 robe/traveller armor rarity-repair baseline

Статус: частично продвинут.

Что сделано:

- Восстановлены reference-shaped core contracts:
  - `ItemRobeArmor`: `UNCOMMON` rarity + thaumic-cloth repair key (`itemResource:7`);
  - `ItemBootsTraveller`: `RARE` rarity baseline.
- Добавлен `ItemRobeTravellerArmorCoreContractsStaticGuardTest` для фиксации этих контрактов.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это contract baseline; расширенный hover-motion parity для Boots of the Traveller остаётся отдельным gameplay polish.

#### Checkpoint 2026-05-17 — GAP-11 fortress-armor core contracts baseline

Статус: частично продвинут.

Что сделано:

- `ItemFortressArmor` доведён до reference-shaped core contracts (без клиентских model/icon tooltip веток):
  - сохранена расширенная interface surface: `ISpecialArmor` + `IGoggles` + `IRevealer`;
  - rarity/repair baseline: `RARE` + thaumium repair key (`itemResource:2`);
  - восстановлен set/mask armor-ratio bonus в `getProperties(...)` (`0.875` base + `0.125` за каждый fortress-piece слотов `1..3` + `0.05` за `mask` tag);
  - revealer hooks `showNodes(...)`/`showIngamePopups(...)` закреплены на `goggles` NBT-gate.
- Добавлен `ItemFortressArmorCoreContractsStaticGuardTest` для фиксации этих контрактов.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common/server contract baseline; полный client tooltip/model/icon parity для fortress armor остаётся отдельным Stage 8 client-render polish.

#### Checkpoint 2026-05-17 — GAP-11 fortress-armor tooltip/lang contracts baseline

Статус: частично продвинут.

Что сделано:

- `ItemFortressArmor` получил reference-shaped tooltip contracts для helm upgrades:
  - в `addInformation(...)` восстановлены `goggles`/`mask` NBT tooltip ветки;
  - goggles tooltip использует `item.ItemGoggles.name`;
  - mask tooltip использует `item.HelmetFortress.mask.<id>`.
- В `en_us.lang` добавлены отсутствующие tooltip keys из reference baseline:
  - `item.ItemGoggles.name`;
  - `item.HelmetFortress.mask.0..2`.
- `ItemFortressArmorCoreContractsStaticGuardTest` расширен проверками tooltip contracts и присутствия новых lang-ключей.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это non-GUI tooltip/lang baseline; визуальная model/icon-equipped parity fortress armor остаётся в Stage 8 client-render scope.

#### Checkpoint 2026-05-17 — GAP-11 void-armor core contracts baseline

Статус: частично продвинут.

Что сделано:

- Восстановлены reference-shaped core contracts для void armor family:
  - `ItemVoidArmor`: `UNCOMMON` rarity baseline;
  - `ItemVoidRobeArmor`: `EPIC` rarity, расширенная interface surface `IGoggles` + `IRevealer` + `ISpecialArmor`, vis-discount tooltip line, helmet-only revealer gates (`showNodes/showIngamePopups`), и special-armor mitigation hooks (`getProperties/getArmorDisplay/damageArmor`).
- Сохранены существующие void-ingot repair/self-repair и warp/vis contracts.
- Добавлен `ItemVoidArmorCoreContractsStaticGuardTest` для фиксации этих контрактов.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common/server+non-GUI tooltip baseline; полный client model/icon overlay parity для void robe остаётся в Stage 8 client-render scope.

#### Checkpoint 2026-05-17 — GAP-11 traveller-boots hover core contracts baseline

Статус: частично продвинут.

Что сделано:

- `Hover.doHover(...)` приведён к reference-shaped core movement contracts для `ItemBootsTraveller`:
  - creative+forward gate;
  - client-side step-height boost (`1.0F`) с восстановлением предыдущего значения;
  - on-ground bonus thrust (`moveRelative(..., bonus)` с water penalty);
  - airborne movement factor branch (`0.03F/0.05F` через hover state);
  - gradual fall-distance dampening (`-0.25F` с нижней границей `0`).
- `Hover.resetHover(...)` теперь использует сохранённый previous-step restore path вместо жёсткого reset.
- Расширен `ItemRobeTravellerArmorCoreContractsStaticGuardTest`:
  - фиксирует связь `ItemBootsTraveller.onArmorTick -> Hover.doHover(...)`;
  - фиксирует ключевые movement contracts в `Hover`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common/server+non-GUI contract baseline; client/manual movement-feel parity boots в реальной игре остаётся в manual runtime зоне Stage 5.

#### Checkpoint 2026-05-17 — GAP-11 hover-harness core contracts baseline

Статус: частично продвинут.

Что сделано:

- `ItemHoverHarness` доведён до reference-shaped server/common contract в right-click GUI path:
  - координаты открытия GUI теперь вычисляются через `MathHelper.floor(...)` вместо `(int)` cast, что сохраняет correct floor-behavior на отрицательных координатах.
- Добавлен `ItemHoverHarnessCoreContractsStaticGuardTest` для фиксации core contracts:
  - `EPIC` rarity + iron-ingot repair key;
  - vis-discount baseline (`Aer=5`, иначе `2`);
  - creative gate + `Hover.handleHoverArmor(...)` dispatch;
  - server-side `GUI_HOVER_HARNESS` open path с `MathHelper.floor(...)`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common/server+non-GUI contract baseline; полноценная client/manual hover toggle/sound/tooltip visual parity остаётся в Stage 5/8 manual зоне.

#### Checkpoint 2026-05-17 — GAP-11 bucket-pure use-result contracts baseline

Статус: частично продвинут.

Что сделано:

- `ItemBucketPure.onItemRightClick(...)` приведён к reference-shaped use-result semantics:
  - ветки без фактического действия (no-hit / no-edit / no-place) теперь возвращают `PASS` вместо `FAIL`;
  - успешный place сохраняет существующий split: creative — исходный stack, survival — `Items.BUCKET`.
- Добавлен `ItemBucketPureCoreContractsStaticGuardTest`, фиксирующий:
  - raytrace/no-op return contracts;
  - creative/survival result split;
  - pure-fluid source-state placement contract (`BlockFluidPure.SOURCE_LEVEL`).

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common/server+non-GUI baseline; runtime/manual сценарии с pickup/collision/WarpWard всё ещё остаются в Stage 5 manual зоне.

#### Checkpoint 2026-05-17 — GAP-11 research-notes core consumption contracts baseline

Статус: частично продвинут.

Что сделано:

- `ItemResearchNotes` приведён к reference-shaped consumption/result contracts:
  - при отсутствии requisites completion path теперь возвращает нейтральный `PASS` (вместо `FAIL`);
  - расход note (`stack.shrink(1)`) восстановлен как unconditional в двух reference-flow ветках:
    - успешный completion note;
    - unknown discovery path с `findHiddenResearch == "FAIL"` и выдачей fragments.
- Добавлен `ItemResearchNotesCoreContractsStaticGuardTest`, фиксирующий:
  - meta/rarity split (`24/42/64`, `RARE/EPIC`);
  - requisites-miss `PASS` contract;
  - mandatory note-consumption и hidden-fragment fallback contract.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common/server+non-GUI baseline; полная research-note gameplay/GUI/manual parity (table interaction/hex puzzle flow) остаётся зависимостью Stage 3/8/9 runtime/manual сценариев.

#### Checkpoint 2026-05-17 — GAP-11 eldritch-object tooltip/lang contracts baseline

Статус: частично продвинут.

Что сделано:

- `ItemEldritchObject` получил reference-shaped metadata tooltip branches в `addInformation(...)`:
  - восстановлены текстовые ветки для `meta 0..3`;
  - для `meta 4` восстановлен tooltip `Creative Mode Only`.
- В `en_us.lang` добавлены отсутствующие reference keys:
  - `item.ItemEldritchObject.text.1..6`.
- Добавлен `ItemEldritchObjectCoreContractsStaticGuardTest`, фиксирующий:
  - subtype/rarity split (`UNCOMMON/RARE/EPIC`);
  - `CRIMSON` unlock right-click path;
  - tooltip branch presence + lang key presence.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это non-GUI tooltip/lang baseline; manual runtime parity для primordial pearl node mutation/flux scatter и creative obelisk placement остаётся отдельной Stage 5/6 runtime задачей.

#### Checkpoint 2026-05-17 — GAP-11 eldritch-object use-first side-result contracts baseline

Статус: частично продвинут.

Что сделано:

- `ItemEldritchObject.onItemUseFirst(...)` выровнен по reference side-result semantics:
  - для `META_ELDRITCH_OBJECT_3` (primordial pearl/node path): server branch возвращает `SUCCESS`, client branch — `PASS`;
  - для `META_OB_PLACER` (creative obelisk path): server branch возвращает `SUCCESS`, client branch — `PASS`.
- Логика mutate/place оставлена server-only как ранее, изменения касаются только result-contract согласования между сторонами.
- `ItemEldritchObjectCoreContractsStaticGuardTest` расширен проверками этих side-result контрактов.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это non-GUI side-result baseline; manual runtime parity для полного node-mutation/scatter и obelisk placement scenarios остаётся отдельной Stage 5/6 runtime задачей.

#### Checkpoint 2026-05-17 — GAP-11 loot-bag consumption contracts baseline

Статус: частично продвинут.

Что сделано:

- `ItemLootBag.onItemRightClick(...)` приведён к reference-shaped consumption timing:
  - расход stack (`stack.shrink(1)`) вынесен из server-only блока и теперь выполняется всегда после использования.
- Добавлен `ItemLootBagCoreContractsStaticGuardTest`, фиксирующий:
  - subtype/rarity split (`common/uncommon/rare`);
  - loot-roll baseline (`8 + rand.nextInt(5)`) и `Utils.generateLoot(...)` dispatch;
  - post-use consumption placement outside `if (!world.isRemote)` branch;
  - наличие loot-bag lang/tooltip keys.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common/server+non-GUI baseline; полный loot-table distribution parity и runtime/manual opening scenarios остаются зависимостью Stage 9 content + manual validation.

### GAP-12: FX registration exists, but send-site coverage and manual scenario validation are incomplete

**Статус:** требует проверки  
**Критичность:** medium

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/network/PacketHandler.java:57-100`
- `src/main/java/thaumcraft/common/lib/events/ServerTickEventsFML.java:179`
- `src/main/java/thaumcraft/common/tiles/TileCrucible.java:513`
- `src/main/java/thaumcraft/common/entities/monster/boss/EntityCultistPortal.java:128-149`
- `docs/PRD.md:531-538`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/network/PacketHandler.class`
- `thaumcraft_src/thaumcraft/common/lib/network/fx/*.class`
- `thaumcraft_src/thaumcraft/client/ClientProxy.class`

**Что не совпадает:**

`PacketHandler` has reference-like FX packet registration slots, but static analysis shows multiple sends are commented out or impossible because constructors do not exist. There is no evidence of client smoke/manual validation for particles/beams/bolts/shader fallback. `docs/PRD.md:531-538` explicitly requires these client checks before parity claims.

**Что нужно доделать:**

After implementation, audit all Stage 8-e FX packet constructors/send sites and run client/manual validation scenarios.

**Как доделать:**
- Run `rg -n "Phase 8:.*FX|PacketFX|Thaumcraft\.proxy\.(burst|sparkle|wisp|beam|bolt|block)" src/main/java/thaumcraft` after implementing gaps.
- Ensure no Stage 8-e TODO/stub comments remain in current source.
- Run `./scripts/dev.sh compileJava` and, because this is runtime/client-affecting, `./scripts/dev.sh smoke-client` where display/X11 is available.
- Manually validate controlled scenarios: wisp spawn/death/attack, shock focus hit/chain, Warding, Portable Hole, runic shield hit/recharge, Eldritch Guardian sonic, Cultist Portal block arc, vis drain/source stream, warp event/mist, taintacle/firebat ambient FX.

**Критерии приемки:**
- [ ] All registered FX packets have at least one verified send path or documented intentionally-unused status matching reference.
- [ ] No `Phase 8`/TODO/stub comments remain inside Stage 8-e implementation files.
- [ ] `compileJava` passes after implementation.
- [ ] Client smoke reaches main menu/load phase with no crash markers and no new Stage 8-e missing-resource errors.
- [ ] Manual Stage 8-e scenario checklist is recorded in this document or a follow-up checkpoint report.

**Риски / зависимости:**

Client smoke may require display/X11. Some gameplay scenarios depend on earlier server phases being functional enough to spawn entities/use foci, but visual helper packet tests can still be performed through controlled commands/dev setup.

## 6. Итоговый checklist закрытия Stage 8-e

- [ ] Port `src/main/java/thaumcraft/client/fx/ParticleEngine.java` and register it client-side.
- [ ] Port required particle classes under `src/main/java/thaumcraft/client/fx/particles/`.
- [ ] Port beam classes under `src/main/java/thaumcraft/client/fx/beams/`.
- [ ] Port lightning/bolt classes under `src/main/java/thaumcraft/client/fx/bolt/`.
- [ ] Port other FX classes under `src/main/java/thaumcraft/client/fx/other/`: `FXBlockWard`, `FXShieldRunes`, `FXSonic`.
- [ ] Implement Stage 8-e client proxy helpers and keep common proxy server-safe.
- [ ] Implement all 14 `src/main/java/thaumcraft/common/lib/network/fx/**` packet payloads and client handlers.
- [ ] Re-enable Stage 8-e packet/sound send sites only after client handlers exist.
- [ ] Copy required FX textures from `thaumcraft_src/assets/thaumcraft/textures/misc/` into current resources.
- [ ] Verify `src/main/resources/assets/thaumcraft/sounds.json` and sound assets cover all FX-coupled sounds; add only reference-compatible missing entries if found.
- [ ] Verify reference shader files do not exist; implement texture-based overlay/post-processing behavior or explicitly document unsupported shader fallback.
- [ ] Remove Stage 8-e TODO/stub comments from implementation files after implementation.
- [ ] Run `./scripts/dev.sh compileJava`.
- [ ] Run `./scripts/dev.sh smoke-client` if display/X11 is available.
- [ ] Manually verify particle/beam/bolt/shield/ward/portable-hole/warp/taint/sonic scenarios.

## 7. Definition of Done

Stage 8-e считается ПОЛНОСТЬЮ завершенной только если:
- все blocker gaps закрыты;
- все high gaps закрыты;
- все элементы из scope Stage 8-e реализованы;
- текущая реализация соответствует референсу по поведению;
- все нужные файлы, ресурсы и регистрации присутствуют;
- отсутствуют TODO и заглушки внутри scope Stage 8-e;
- проект собирается без ошибок;
- базовые игровые сценарии Stage 8-e проверены вручную или тестами;
- ./docs/Stage8-e.md обновлен и не содержит критичных открытых вопросов.

## 8. Открытые вопросы

### Q1: Portable Hole: Stage 8-e или renderer dependency?

**Resolution:** разделить ответственность. Stage 8-e должен закрыть packet decode/dispatch, FX hooks и sounds. Но полная визуальная parity Portable Hole зависит от `TileHoleRenderer` / renderer-side отображения hole contents.

Формулировка: "Stage 8-e owns Portable Hole FX/sound/packet hooks, but not full renderer parity. Full visual parity remains blocked on the Stage 8 renderer/TESR implementation and must not be claimed until `TileHoleRenderer` or equivalent is complete."

### Q2: Warp post-processing: shader или overlay/resource route?

**Resolution:** считать overlay/resource route первичным, так как decompile не обнаружил shader-файлов, а поведение выглядит как resource/overlay based. Безопаснее не выдумывать shader pipeline.

Формулировка: "Warp post-processing should be implemented as the reference overlay/resource pipeline unless `PacketMiscEvent` or the relevant overlay classes prove a shader-based route. Do not introduce a new shader dependency without reference evidence."

### Q3: Как покрыть packets, если Phase 5/6/7 сценарии недоступны?

**Resolution:** через controlled test worlds/commands/direct triggers. Это приемлемо, если честно задокументировано. Acceptance должен требовать: packet decode не падает, side dispatch корректный, handler не грузит client-only classes на server, эффекты вызываются на client side.

Формулировка: "If normal gameplay paths are blocked by earlier phase dependencies, Stage 8-e packet coverage may use controlled worlds, commands, or direct packet trigger scenarios. The dependency must be documented; silent omission is not acceptable."
