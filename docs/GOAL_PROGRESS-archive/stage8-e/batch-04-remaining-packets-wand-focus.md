# Stage 8-e Batch 04 — Remaining Packets & Wand/Focus Visuals (GAP-3+10)

9 checkpoints covering block-dig/bubble, beam-pulse, bore dig, warp events, guardian mist, crucible FX, guardian wisp trail, taint land FX, and GAP-10 warding/portable-hole sparkle baseline.

---

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

---

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

---

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

---

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

---

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

---

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

---

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

---

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

---

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
