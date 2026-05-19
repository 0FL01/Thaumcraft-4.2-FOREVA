# Stage 8-e Batch 03 — Shield, Sound & Combat Packets (GAP-3 middle)

8 checkpoints covering shield FX packet + runic sends, runic sound cues, sonic, wisp-zap, shock-zap, block-zap, essentia-source, and infusion-source packet handler baselines.

---

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

---

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

---

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

---

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

---

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

---

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

---

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

---

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
