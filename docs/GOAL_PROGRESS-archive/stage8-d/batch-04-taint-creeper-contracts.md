# Stage 8-d — Taint creeper server contracts — armor overlay, namespace, underwater/NBT/attack/fuse/explosion/spread/fall/early-FX

Split from `stage8-d-checkpoints.md`. Batch covers checkpoints 37–47 of 82.

---

## 6. Итоговый checklist закрытия Stage 8-d


### Checkpoint 2026-05-16 — taint creeper armor-charge layer baseline

Статус: частично продвинут.

Что сделано:

- `RenderTaintCreeper` расширен charge/armor overlay pass baseline:
  - добавлен `CreeperArmorLayer` с additive blend и texture-matrix scroll profile;
  - добавлен armor texture path `textures/entity/creeper/creeper_armor.png` (vanilla overlay resource);
  - renderer теперь добавляет layer через `this.addLayer(new CreeperArmorLayer(this))`.
- `EntityTaintCreeper` расширен accessor `getPowered()` для layer-gating (`entity.getPowered()`).
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками:
  - armor texture + layer wiring contracts в `RenderTaintCreeper`;
  - `EntityTaintCreeper#getPowered()` contract.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это armor-layer baseline; полная parity по taint creeper visual stack (точные legacy lightmap/intensity нюансы и powered-state acquisition path) остаётся открытой по GAP-3/GAP-6.


### Checkpoint 2026-05-16 — fix taint creeper armor texture namespace contract

Статус: частично продвинут.

Что сделано:

- `RenderTaintCreeper` исправлен на reference-shaped armor texture namespace:
  - `ARMOR_TEXTURE` переведён на `new ResourceLocation("thaumcraft", "textures/entity/creeper/creeper_armor.png")`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` закрепляет explicit `thaumcraft` namespace contract для armor texture path.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это namespace contract fix; remaining visual нюансы taint creeper armor pass (legacy GL-state/lightmap specifics) остаются в общем client parity scope.


### Checkpoint 2026-05-16 — restore taint creeper underwater/no-despawn behavior contracts

Статус: частично продвинут.

Что сделано:

- `EntityTaintCreeper` расширен reference-shaped behavior hooks:
  - добавлен `canBreatheUnderwater() -> true`;
  - добавлен `canDespawn() -> false`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками на presence этих контрактов в `EntityTaintCreeper`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это behavior-contract baseline; полная parity по taint creeper explosion-side effects/taint spread paths остаётся в Stage 6 common-runtime scope.


### Checkpoint 2026-05-16 — restore taint creeper NBT persistence contracts

Статус: частично продвинут.

Что сделано:

- `EntityTaintCreeper` NBT persistence выровнен с reference-shaped contract:
  - `Fuse` теперь сохраняет/восстанавливает `fuseTime` (а не runtime ignite timer);
  - добавлена сохранение/загрузка `ExplosionRadius`;
  - powered-state write теперь использует reference key `"powered"`;
  - read path сделан backward-compatible: поддерживает `"powered"` и legacy `"Powered"`.
- Runtime ignite timer на load сбрасывается к baseline (`timeSinceIgnited = 0`) вместо неверного чтения из `Fuse`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками на `"powered"`/`"Fuse"`/`"ExplosionRadius"` contracts.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это NBT-contract baseline; полная parity explosion gameplay path (taint spread/potion side effects) остаётся в Stage 6 runtime scope.


### Checkpoint 2026-05-16 — restore taint creeper no-melee-hit attack contract

Статус: частично продвинут.

Что сделано:

- `EntityTaintCreeper` расширен reference-shaped attack contract:
  - добавлен override `attackEntityAsMob(Entity)` с `return true;` без стандартного `EntityMob` melee damage path.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` закрепляет presence no-melee-hit attack hook в `EntityTaintCreeper`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это attack-contract baseline; полная parity taint creeper explosion-side gameplay logic остаётся в Stage 6 runtime scope.


### Checkpoint 2026-05-16 — restore taint creeper fuse sound and state-driven ignition baseline

Статус: частично продвинут.

Что сделано:

- `EntityTaintCreeper.onUpdate()` выровнен с reference-shaped ignite flow:
  - добавлен primed sound trigger на старте (`SoundEvents.ENTITY_CREEPER_PRIMED`) when `state > 0 && timeSinceIgnited == 0`;
  - ignite timer теперь обновляется через state accumulation (`timeSinceIgnited += state`) с нижней границей `0`;
  - explosion power path выровнен к reference baseline `1.5F` (без boosted powered multiplier в этом baseline checkpoint).
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками на fuse sound/state accumulation/1.5F explosion power contracts.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это ignite-flow baseline; полная parity taint spread/potion side-effects после взрыва остаётся в Stage 6 runtime scope.


### Checkpoint 2026-05-16 — restore taint creeper detonation taint-poison splash baseline

Статус: частично продвинут.

Что сделано:

- `EntityTaintCreeper` detonation path расширен reference-shaped side-effect baseline:
  - после server-side взрыва добавлен поиск nearby `EntityLivingBase` в радиусе `6.0D`;
  - non-tainted цели (`!ITaintedMob`) получают `Config.potionFluxTaint` (`100` ticks, amplifier `0`) при отсутствии активного эффекта.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками на contracts:
  - nearby `EntityLivingBase` splash query;
  - `ITaintedMob` исключение;
  - использование `Config.potionFluxTaint` и `addPotionEffect(...)`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это detonation poison-splash baseline; reference taint biome/fibre spread side-effects после взрыва остаются в Stage 6 runtime scope.


### Checkpoint 2026-05-16 — restore taint creeper detonation biome/fibre spread baseline

Статус: частично продвинут.

Что сделано:

- `EntityTaintCreeper` post-explosion path расширен reference-shaped taint spread baseline:
  - добавлен `10`-attempt random spread loop around blast center;
  - при chance-path выполняется biome conversion через `Utils.setBiomeAt(..., ThaumcraftWorldGenerator.biomeTaint)`;
  - добавлен fibre placement path на replaceable blocks с solid support below (`ConfigBlocks.blockTaintFibres`).
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками на loop/biome conversion/fibre placement contracts.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это biome/fibre spread baseline; deeper worldgen/taint propagation parity остаётся в общем Stage 6/7 runtime scope.


### Checkpoint 2026-05-16 — restore taint creeper max-fall-height combat hook baseline

Статус: частично продвинут.

Что сделано:

- `EntityTaintCreeper` расширен reference-shaped pathfinding/combat hook:
  - добавлен override `getMaxFallHeight()` с поведением:
    - `3` при отсутствии target;
    - `3 + (int)(getHealth() - 1.0F)` при активном target.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками на `getMaxFallHeight()` contract и formula line.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это pathfinding hook baseline; полная runtime parity taint creeper combat AI оценивается в общем Stage 6 behavior scope.


### Checkpoint 2026-05-17 — restore taint creeper fall-accelerated fuse baseline

Статус: частично продвинут.

Что сделано:

- `EntityTaintCreeper` расширен reference-shaped fall hook:
  - добавлен override `fall(float distance, float damageMultiplier)`;
  - после `super.fall(...)` ignite timer ускоряется по формуле `timeSinceIgnited += distance * 1.5F`;
  - добавлен cap `timeSinceIgnited <= fuseTime - 5`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками на fall-accelerated fuse contracts.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это fall-fuse baseline; полная parity taint creeper movement/combat runtime behavior закрывается в общем Stage 6 scope.


### Checkpoint 2026-05-17 — restore taint creeper early client taint FX baseline

Статус: частично продвинут.

Что сделано:

- `EntityTaintCreeper` расширен early-lifecycle client FX hook:
  - добавлен `onLivingUpdate()` override;
  - в первые `5` ticks на клиенте эмитится taint FX loop через side-safe proxy hooks:
    - `Thaumcraft.proxy.particleCount(10)`;
    - `Thaumcraft.proxy.taintLandFX(this)`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками на early client taint FX contracts.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это early-FX baseline; полная visual parity эффекта остаётся в non-GUI ограниченном client scope.

