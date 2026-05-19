# Stage 8-e Batch 05 — GAP-11 Elemental Tools

6 checkpoints covering elemental-axe bubble/baseline, elemental-pickaxe fire/feedback, elemental-sword lift/chain, elemental-hoe till/growth, elemental-shovel architect/burst, and elemental-pickaxe scan-hook baseline.

---

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

---

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

---

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

---

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

---

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

---

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
