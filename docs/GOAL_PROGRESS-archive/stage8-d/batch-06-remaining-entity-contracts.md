# Stage 8-d — Remaining entity server contracts — brainy zombie anger/target, mind spider viewer/sync/translucent/collision/attributes/eyes, taint spider, inhabited zombie armor

Split from `stage8-d-checkpoints.md`. Batch covers checkpoints 59–69 of 82.

---

## 6. Итоговый checklist закрытия Stage 8-d


### Checkpoint 2026-05-17 — restore giant brainy zombie anger/scale/drop baseline

Статус: частично продвинут.

Что сделано:

- `EntityGiantBrainyZombie` выровнен с reference-shaped common behavior contracts:
  - восстановлены anger-state sync contracts (`DataParameter<Float> ANGER`, `getAnger`/`setAnger`);
  - восстановлен constructor baseline:
    - `experienceValue = 15`;
    - initial scale boost (`1.2F + anger`) для размеров/step-height;
    - leap AI hook (`EntityAILeapAtTarget`, priority `2`).
  - восстановлена anger-driven runtime динамика:
    - gradual anger decay (`-0.002F` per tick, floor `>1`);
    - dynamic size re-scale (`0.6F/1.8F * (1.2F + anger)`);
    - dynamic attack scaling (`7.0 + (anger - 1.0) * 5.0`).
  - восстановлен anger-on-hit hook: `attackEntityFrom(...)` повышает anger до cap `2.0F`.
  - attributes выровнены к baseline: `MAX_HEALTH = 60.0D`, `ATTACK_DAMAGE = 7.0D`.
  - drop contracts восстановлены:
    - doubled rotten-flesh wave loops (`6 + 6`, each roll drops `2` flesh on true);
    - zombie-brain rare drop branch (`nextInt(10) - looting <= 4`).
  - добавлены NBT contracts для anger persistence (`"Anger"` float).
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками на anger/scale/attribute/drop contracts `EntityGiantBrainyZombie`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common behavior baseline; точная visual parity anger-based giant scaling в runtime scene остаётся в non-GUI scope.


### Checkpoint 2026-05-17 — restore brainy zombie target/reinforcement/drop baseline

Статус: частично продвинут.

Что сделано:

- `EntityBrainyZombie` выровнен с reference-shaped common behavior contracts:
  - восстановлен target behavior hook в constructor (`EntityAIHurtByTarget`, priority `1`);
  - attributes уточнены к reference shape:
    - `MAX_HEALTH = 25.0D`;
    - `ATTACK_DAMAGE = 5.0D`;
    - zombie reinforcement chance attribute принудительно обнулён (`EntityZombie.SPAWN_REINFORCEMENTS_CHANCE = 0.0D`);
  - drop loop сохранён в reference pattern (`3` boolean rolls на rotten flesh + zombie brain rare roll).
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками на target/reinforcement/drop contracts `EntityBrainyZombie`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common behavior baseline; полная parity brainy-zombie runtime combat scenarios остаётся в общих non-GUI runtime рамках.


### Checkpoint 2026-05-16 — restore mind spider viewer-only render gating

Статус: частично продвинут.

Что сделано:

- В `EntityMindSpider` добавлен accessor `getViewer()`.
- `RenderMindSpider` расширен reference-shaped viewer-gating в `doRender(...)`:
  - если `viewer` пустой — сущность рендерится как обычно;
  - если `viewer` задан — сущность рендерится только для клиента с matching player name.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками:
  - `RenderMindSpider` содержит `entity.getViewer()` + current-player name compare;
  - `EntityMindSpider` содержит `getViewer()` contract.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это viewer-gating baseline; полная parity по mind spider rendering (точная reference alpha-fade модель и legacy GL-pass нюансы) остаётся открытой по GAP-3/GAP-6.


### Checkpoint 2026-05-16 — restore mind spider synced harmless/viewer entity contracts

Статус: частично продвинут.

Что сделано:

- `EntityMindSpider` переведен на reference-shaped synced state contracts:
  - добавлены `DataParameter<Byte> HARMLESS` и `DataParameter<String> VIEWER` + `entityInit()` registration;
  - `isHarmless()`/`setHarmless(...)` теперь работают через `dataManager`, с `lifeSpan = 1200` при harmless-mode;
  - добавлены NBT read/write contracts для `"harmless"` и `"viewer"`;
  - добавлен `spiderScaleAmount()` accessor (`0.3F`) и `getExperiencePoints(...)` gating при harmless-state.
- `RenderMindSpider` scale callback переведен на `entity.spiderScaleAmount()` вместо fallback масштаба через width.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками:
  - `RenderMindSpider` содержит `entity.spiderScaleAmount()` contract;
  - `EntityMindSpider` содержит `HARMLESS`/`VIEWER` data-manager contracts, `isHarmless`, `spiderScaleAmount`, и NBT keys.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это entity sync/persistence baseline; полная parity по reference render-pass/alpha/lightmap деталям для mind spider остаётся открытой по GAP-3/GAP-6.


### Checkpoint 2026-05-16 — restore mind spider translucent base render pass baseline

Статус: частично продвинут.

Что сделано:

- `RenderMindSpider` расширен reference-shaped translucent base pass в `renderModel(...)`:
  - alpha profile `Math.min(0.1F, entity.ticksExisted / 100.0F)`;
  - depth-mask disable/restore around model pass;
  - blend profile `SRC_ALPHA/ONE_MINUS_SRC_ALPHA`;
  - temporary alpha-threshold shift `0.003921569F` и restore к `0.1F`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками на alpha/depth contracts в `RenderMindSpider`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это translucent base-pass baseline; полная parity по reference eye-brightness/lightmap нюансам и legacy multi-pass semantics остаётся открытой по GAP-3/GAP-6.


### Checkpoint 2026-05-16 — restore mind spider harmless collision/attack contracts

Статус: частично продвинут.

Что сделано:

- `EntityMindSpider` расширен reference-shaped harmless behavior hooks:
  - добавлен `getYOffset()` contract (`0.0D` when harmless, `0.1D` otherwise);
  - добавлен explicit `canBeCollidedWith() -> true`;
  - добавлен `canTriggerWalking() -> false`;
  - добавлен harmless attack-gate в `attackEntityAsMob(...)` (no attack when harmless, иначе `super` path).
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен контрактами на новые harmless/collision/attack hooks в `EntityMindSpider`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это harmless collision/attack baseline; точный legacy target-acquisition нюанс pre-1.8 (`findPlayerToAttack` path) напрямую не репродуцируется в 1.12 AI-task модели и остаётся в составе общей behavior parity валидации.


### Checkpoint 2026-05-16 — fix mind spider attribute contract (attack damage vs movement speed)

Статус: частично продвинут.

Что сделано:

- В `EntityMindSpider.applyEntityAttributes()` исправлен reference-shaped combat contract:
  - второй baseline attribute переключен с `SharedMonsterAttributes.MOVEMENT_SPEED` на `SharedMonsterAttributes.ATTACK_DAMAGE` со значением `1.0D`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен check’ом, закрепляющим `ATTACK_DAMAGE` contract и отсутствие `MOVEMENT_SPEED` override в `EntityMindSpider`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это attribute-contract fix; дополнительные pre-1.8 AI-targeting нюансы остаются в общем behavior parity scope.


### Checkpoint 2026-05-16 — restore mind spider fullbright eyes lightmap baseline

Статус: частично продвинут.

Что сделано:

- `RenderMindSpider.SpiderEyesLayer` расширен reference-shaped fullbright lightmap hook:
  - добавлен `int i = 61680` split (`j/k`) и `OpenGlHelper.setLightmapTextureCoords(...)`;
  - eyes layer сохраняет additive blend/alpha-disable/depth-mask profile.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками на `61680` lightmap baseline и `OpenGlHelper.setLightmapTextureCoords(...)` contract.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это eyes-lightmap baseline; точные legacy multi-pass/lightmap restore нюансы за пределами текущего non-GUI checkpoint и закрываются общей client parity валидацией.


### Checkpoint 2026-05-16 — restore taint spider scale/eyes lightmap baseline

Статус: частично продвинут.

Что сделано:

- `EntityTaintSpider` расширен accessor `spiderScaleAmount()` (`0.4F`) для reference-shaped render scale contract.
- `RenderTaintSpider` переведен на `entity.spiderScaleAmount()` в `preRenderCallback(...)` (с сохранением `Y * 1.25F` profile).
- `RenderTaintSpider.SpiderEyesLayer` расширен fullbright lightmap hook:
  - добавлен `int i = 61680` split (`j/k`) и `OpenGlHelper.setLightmapTextureCoords(...)`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками:
  - scale accessor + lightmap contracts в `RenderTaintSpider`;
  - `EntityTaintSpider#spiderScaleAmount()` contract.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это scale/eyes-lightmap baseline; remaining taint spider gameplay parity (entity attributes/size/drop-frequency nuances from Stage 6 scope) закрывается отдельным common-side checkpoint.


### Checkpoint 2026-05-16 — restore taint spider common behavior baseline (size/attributes/loot)

Статус: частично продвинут.

Что сделано:

- `EntityTaintSpider` приведен к reference-shaped baseline, критичному для entity/render parity:
  - constructor size restored to `0.4F x 0.3F`, XP baseline restored to `2`;
  - attributes restored to `MAX_HEALTH = 5.0D`, `ATTACK_DAMAGE = 2.0D`;
  - `getYOffset()` restored to `0.1D`;
  - loot drop gating restored to `1/6` chance, with existing resource 11/12 split preserved.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками на size/XP/attributes/y-offset/loot contracts для `EntityTaintSpider`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Точный legacy pre-1.8 target-acquisition path (`findPlayerToAttack`) не переносится 1:1 в 1.12 AI-task model и остаётся в общем Stage 6 behavior parity scope.


### Checkpoint 2026-05-17 — restore inhabited zombie armor/spawn/no-drop behavior baseline

Статус: частично продвинут.

Что сделано:

- `EntityInhabitedZombie` доведен до reference-shaped common contracts:
  - восстановлен `onInitialSpawn(...)` armor-setup baseline: `HEAD` всегда + `CHEST`/`LEGS` c шансом `0.9F` на `HARD` и `0.6F` на остальных сложностях;
  - добавлен explicit no-drop contract через `getDropItem() -> Item.getItemById(0)`;
  - добавлен пустой `onDeath(DamageSource)` override для сохранения reference death-path suppression;
  - в `onDeathUpdate()` XP-spawn gate дополнен `canDropLoot()` как в reference-shaped flow;
  - сохранены crab-spawn path (`EntityEldritchCrab#setHelm(true)`), explosion particle burst, anti-cluster spawn gate и `CRABTALK` ambient/hurt contracts.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками на новые `EntityInhabitedZombie` contracts (`onInitialSpawn` armor chance, no-drop hook, crab spawn, death suppression).

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- В 1.12-порте отсутствуют отдельные `itemHelmetCultistPlate`/`itemChestCultistPlate`/`itemLegsCultistPlate` item-singletons из legacy surface; текущий baseline использует доступный `ConfigItems.itemCultistPlate` для эквивалентного armor-presence contract.

