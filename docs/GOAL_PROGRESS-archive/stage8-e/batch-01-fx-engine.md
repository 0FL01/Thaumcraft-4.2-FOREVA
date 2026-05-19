# Stage 8-e Batch 01 — FX Engine Foundation (GAP-1, GAP-2, GAP-5, GAP-3 dispatch)

5 checkpoints covering the particle engine, client proxy FX fallback, and FX texture assets.

---

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

---

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

---

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

---

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

---

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
