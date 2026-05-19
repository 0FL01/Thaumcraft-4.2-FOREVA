# Stage 8-d — Taint animal renderers — shared texture baseline, dedicated creeper/villager/chicken/cow/pig/sheep renderers, cleanup, sheep sheared/model/AI

Split from `stage8-d-checkpoints.md`. Batch covers checkpoints 26–36 of 82.

---

## 6. Итоговый checklist закрытия Stage 8-d


### Checkpoint 2026-05-16 — taint animal-like dedicated shared texture renderer baseline

Статус: частично продвинут.

Почему grouped в один commit:

- `EntityTaintChicken`, `EntityTaintCow`, `EntityTaintPig`, `EntityTaintSheep`, `EntityTaintVillager`, `EntityTaintCreeper` используют единый lightweight texture-only renderer pattern с разными model/texture bindings, поэтому вынесены в один shared renderer checkpoint.

Что сделано:

- Добавлен dedicated shared renderer `RenderTaintTextureLiving<T extends EntityLiving>`.
- `ClientProxy.setupEntityRenderers()` обновлен:
  - `EntityTaintChicken -> RenderTaintTextureLiving` (`ModelChicken`, `chicken.png`);
  - `EntityTaintCow -> RenderTaintTextureLiving` (`ModelCow`, `cow.png`);
  - `EntityTaintPig -> RenderTaintTextureLiving` (`ModelPig`, `pig.png`);
  - `EntityTaintSheep -> RenderTaintTextureLiving` (`ModelSheep2`, `sheep.png`);
  - `EntityTaintVillager -> RenderTaintTextureLiving` (`ModelVillager`, `villager.png`);
  - `EntityTaintCreeper -> RenderTaintTextureLiving` (`ModelCreeper`, `creeper.png`).
- `ClientProxyEntityRendererRegistrationStaticGuardTest` обновлен:
  - taint animal-like registration checks теперь закрепляют `RenderTaintTextureLiving`;
  - добавлен dedicated class-contract check для `RenderTaintTextureLiving`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это shared texture/model baseline; full reference parity для taint-animal render behavior остаётся открытой по GAP-3/GAP-4/GAP-6 (в частности sheep fur layer, villager scale nuance и creeper flash/armor render-pass semantics).


### Checkpoint 2026-05-16 — dedicated taint creeper flash-scale renderer baseline

Статус: частично продвинут.

Что сделано:

- Добавлен выделенный renderer `RenderTaintCreeper` (`thaumcraft.client.renderers.entity.RenderTaintCreeper`) вместо shared texture-only baseline для `EntityTaintCreeper`.
- Baseline behavior портирован по reference renderer semantics:
  - dedicated texture path `textures/models/creeper.png`;
  - flash-intensity-driven pre-render scale wobble (`preRenderCallback`);
  - flash-intensity-driven white overlay color multiplier (`getColorMultiplier`).
- В `EntityTaintCreeper` добавлен accessor `getCreeperFlashIntensity(float partialTicks)` для reference-shaped render timing interpolation.
- `ClientProxy.setupEntityRenderers()` обновлен: `EntityTaintCreeper` теперь регистрируется через `RenderTaintCreeper::new`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками:
  - explicit registration path `EntityTaintCreeper -> RenderTaintCreeper`;
  - `RenderTaintCreeper` texture/flash/scale/color contracts;
  - `EntityTaintCreeper#getCreeperFlashIntensity` contract presence.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это baseline creeper flash/scale/color parity; full reference parity по taint creeper visual stack остаётся открытой (legacy armor-pass path и прочие GL-pass нюансы) по GAP-3/GAP-6.


### Checkpoint 2026-05-16 — dedicated taint villager renderer baseline

Статус: частично продвинут.

Что сделано:

- Добавлен выделенный renderer `RenderTaintVillager` (`thaumcraft.client.renderers.entity.RenderTaintVillager`) вместо shared texture-only baseline для `EntityTaintVillager`.
- Baseline behavior:
  - dedicated texture path `textures/models/villager.png`;
  - reference-shaped pre-render scale profile (`0.9375f`) с сохранением villager shadow size (`0.5f`).
- `ClientProxy.setupEntityRenderers()` обновлен: `EntityTaintVillager` теперь регистрируется через `RenderTaintVillager::new`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками:
  - explicit registration path `EntityTaintVillager -> RenderTaintVillager`;
  - texture + pre-render scale contracts `RenderTaintVillager`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это baseline texture/scale parity; full reference parity по taint villager visual stack (дополнительные legacy pass hooks и GL-state нюансы) остаётся открытой по GAP-3/GAP-6.


### Checkpoint 2026-05-16 — dedicated taint chicken wing-rotation renderer baseline

Статус: частично продвинут.

Что сделано:

- Добавлен выделенный renderer `RenderTaintChicken` (`thaumcraft.client.renderers.entity.RenderTaintChicken`) вместо shared texture-only baseline для `EntityTaintChicken`.
- Baseline behavior:
  - dedicated texture path `textures/models/chicken.png`;
  - reference-shaped wing-rotation callback в `handleRotationFloat` с использованием `field_756_e`, `field_752_b`, `field_757_d`, `destPos` и `MathHelper.sin(...)`.
- `ClientProxy.setupEntityRenderers()` обновлен: `EntityTaintChicken` теперь регистрируется через `RenderTaintChicken::new`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками:
  - explicit registration path `EntityTaintChicken -> RenderTaintChicken`;
  - texture + wing-rotation callback contracts `RenderTaintChicken`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это baseline texture/wing parity; full reference parity по taint chicken visual stack (legacy render-pass и GL-state детали) остаётся открытой по GAP-3/GAP-6.


### Checkpoint 2026-05-16 — dedicated taint cow renderer baseline

Статус: частично продвинут.

Что сделано:

- Добавлен выделенный renderer `RenderTaintCow` (`thaumcraft.client.renderers.entity.RenderTaintCow`) вместо shared texture-only baseline для `EntityTaintCow`.
- Baseline behavior:
  - dedicated texture path `textures/models/cow.png`;
  - dedicated model/shadow profile (`ModelCow`, `0.7f`) в renderer constructor.
- `ClientProxy.setupEntityRenderers()` обновлен: `EntityTaintCow` теперь регистрируется через `RenderTaintCow::new`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками:
  - explicit registration path `EntityTaintCow -> RenderTaintCow`;
  - `RenderTaintCow` texture contract.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это baseline texture/model parity; full reference parity по taint cow visual stack (legacy render-pass и GL-state детали) остаётся открытой по GAP-3/GAP-6.


### Checkpoint 2026-05-16 — dedicated taint pig renderer baseline

Статус: частично продвинут.

Что сделано:

- Добавлен выделенный renderer `RenderTaintPig` (`thaumcraft.client.renderers.entity.RenderTaintPig`) вместо shared texture-only baseline для `EntityTaintPig`.
- Baseline behavior:
  - dedicated texture path `textures/models/pig.png`;
  - dedicated model/shadow profile (`ModelPig`, `0.5f`) в renderer constructor.
- `ClientProxy.setupEntityRenderers()` обновлен: `EntityTaintPig` теперь регистрируется через `RenderTaintPig::new`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками:
  - explicit registration path `EntityTaintPig -> RenderTaintPig`;
  - `RenderTaintPig` texture contract.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это baseline texture/model parity; full reference parity по taint pig visual stack (legacy render-pass и GL-state детали) остаётся открытой по GAP-3/GAP-6.


### Checkpoint 2026-05-16 — dedicated taint sheep fur-layer renderer baseline

Статус: частично продвинут.

Что сделано:

- Добавлен выделенный renderer `RenderTaintSheep` (`thaumcraft.client.renderers.entity.RenderTaintSheep`) вместо shared texture-only baseline для `EntityTaintSheep`.
- Baseline behavior:
  - dedicated base texture `textures/models/sheep.png`;
  - dedicated fur-layer texture `textures/models/sheep_fur.png`;
  - добавлен `SheepFurLayer` (layer renderer) и registration через `this.addLayer(new SheepFurLayer(this))`.
- В `EntityTaintSheep` добавлен accessor `getSheared()` (текущий baseline возвращает `false`) для reference-shaped fur-layer gating.
- `ClientProxy.setupEntityRenderers()` обновлен: `EntityTaintSheep` теперь регистрируется через `RenderTaintSheep::new`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками:
  - explicit registration path `EntityTaintSheep -> RenderTaintSheep`;
  - texture + fur-layer contracts `RenderTaintSheep`;
  - `EntityTaintSheep#getSheared` contract presence.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это baseline texture/layer parity; full reference parity по taint sheep visual stack (полный shearing-state gameplay contract и legacy GL-pass нюансы) остаётся открытой по GAP-3/GAP-6.


### Checkpoint 2026-05-16 — remove obsolete shared taint texture renderer

Статус: частично продвинут.

Что сделано:

- Удалён obsolete renderer `RenderTaintTextureLiving` после последовательного перевода taint animal-like registrations на dedicated renderer-классы.
- `ClientProxy` очищен от неиспользуемого `RenderTaintTextureLiving` import.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` обновлен:
  - удалён старый class-contract check на `RenderTaintTextureLiving`;
  - добавлен guard, что `ClientProxy` больше не содержит `RenderTaintTextureLiving` ссылок.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это cleanup/consistency checkpoint; visual parity open-items по taint entity stack остаются прежними и закрываются отдельными behavior-ориентированными checkpoint’ами.


### Checkpoint 2026-05-16 — restore taint sheep sheared-state contract baseline

Статус: частично продвинут.

Что сделано:

- `EntityTaintSheep` обновлен до reference-shaped sheared-state baseline:
  - добавлен data parameter `SHEEP_FLAGS` через `EntityDataManager`;
  - добавлены `getSheared()` / `setSheared(boolean)` bit-flag accessors;
  - восстановлены `isShearable(...)` и `onSheared(...)` (state flip + wool drops);
  - восстановлена NBT-персистентность по ключу `"Sheared"` (`writeEntityToNBT` / `readEntityFromNBT`).
- Это убирает renderer-gating заглушку (`getSheared() -> false`) и привязывает `RenderTaintSheep` fur-layer к реальному entity state.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` усилен проверками sheared-state contracts (`DataParameter`, `entityInit`, accessors, shear path, wool drops, NBT key).

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это sheared-state baseline; полная parity по taint sheep AI/animation/FX деталям остаётся открытой по GAP-3/GAP-6 и закрывается отдельными checkpoint’ами.


### Checkpoint 2026-05-16 — harden taint sheep model safety (no vanilla sheep cast)

Статус: частично продвинут.

Что сделано:

- Для `RenderTaintSheep` добавлены dedicated модели без vanilla-cast риска:
  - `ModelTaintSheep1` (fur layer model);
  - `ModelTaintSheep2` (base model).
- `RenderTaintSheep` переведен с vanilla `ModelSheep1/ModelSheep2` на `ModelTaintSheep1/ModelTaintSheep2`.
- Новые модели используют `instanceof EntityTaintSheep` + hooks `getHeadRotationPointY(...)`/`getHeadRotationAngleX(...)`, а не прямой cast к `EntitySheep`.
- `EntityTaintSheep` расширен head-animation baseline:
  - `sheepTimer`;
  - `handleStatusUpdate((byte)10)` timer trigger;
  - `onLivingUpdate` client-side timer decay;
  - `getHeadRotationPointY(...)` / `getHeadRotationAngleX(...)` reference-shaped animation profile.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками:
  - `RenderTaintSheep` model wiring;
  - `ModelTaintSheep1/2` taint-hook contracts;
  - `EntityTaintSheep` animation hook/timer contracts.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это runtime-safety + animation baseline; полная parity по taint sheep AI/FX workflow (например grass-convert timer source) остаётся открытой по GAP-3/GAP-6.


### Checkpoint 2026-05-16 — restore taint sheep AI/timer baseline for render sync

Статус: частично продвинут.

Что сделано:

- `EntityTaintSheep` расширен reference-shaped AI baseline:
  - добавлен `AIConvertGrass` task (`convertGrassAI`);
  - восстановлены ключевые task/target-task registrations (swim, convert, player/villager collide attack, wander/watch/idle, hurt-by, nearest player/villager targets).
- `EntityTaintSheep#updateAITasks()` теперь синхронизирует `sheepTimer` из `convertGrassAI.getConvertTimer()`, связывая head-animation hooks с реальным AI cycle.
- В `AIConvertGrass` добавлен getter `getConvertTimer()` для entity-side timer sync.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` усилен проверками:
  - наличие `convertGrassAI` wiring;
  - task/target-task registration contracts;
  - `updateAITasks()` timer sync contract.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это baseline AI/timer sync; полная parity по taint sheep behaviour (включая возможные дополнительные 1.7 spawn/interaction nuances) остаётся открытой и закрывается отдельными checkpoint’ами.

