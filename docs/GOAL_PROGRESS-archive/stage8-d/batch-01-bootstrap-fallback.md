# Stage 8-d — Bootstrap & fallback layers — initial coverage, asset corpus, item/projectile/mob fallback renderers, coverage guard

Split from `stage8-d-checkpoints.md`. Batch covers checkpoints 1–10 of 82.

---

## 6. Итоговый checklist закрытия Stage 8-d


### Checkpoint 2026-05-16 — GAP-1 bootstrap coverage baseline

Статус: частично продвинут.

Что сделано:

- `ClientProxy.setupEntityRenderers()` больше не пустой: добавлена client-only регистрация render handler'ов для всех записей `ConfigEntities.ENTITIES`.
- Добавлен безопасный fallback renderer `RenderNoop` (`thaumcraft.client.renderers.entity.RenderNoop`) и временно назначен через `RenderingRegistry.registerEntityRenderingHandler(..., RenderNoop::new)` для полного coverage по registry scope.
- Добавлен `ClientProxyEntityRendererRegistrationStaticGuardTest`, фиксирующий loop по `ConfigEntities.ENTITIES`, вызов `RenderingRegistry.registerEntityRenderingHandler(...)` и наличие `RenderNoop`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это bootstrap/non-crash baseline, а не визуальная parity реализация Stage 8-d.
- Все renderer-specific поведения (custom модели/текстуры/passes/overlays) остаются открытыми по GAP-2..GAP-7.
- Ручная визуальная проверка по инструкции пользователя не выполнялась.


### Checkpoint 2026-05-16 — Stage 8-d asset corpus bootstrap

Статус: частично продвинут.

Что сделано:

- Скопирован отсутствующий reference corpus Stage 8-d ресурсов из `thaumcraft_src/assets/thaumcraft/` в `src/main/resources/assets/thaumcraft/` для категорий `textures/models`, `textures/entity`, `textures/misc` (включая `.obj`, `.mtl`, `.png`, `.mcmeta`).
- Добавлен `EntityRendererAssetCoverageTest` с проверкой наличия критичного подмножества entity-renderer baseline ассетов (cultist/wisp/arrow, golem/taint/pech/trunk/eldritch textures, `bucket.obj`, `orb.obj`).

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это resource bootstrap; renderer/model Java parity по GAP-2..GAP-6 остается открытой.
- Manual visual parity checks остаются пропущенными по инструкции.


### Checkpoint 2026-05-16 — item-like renderer baseline

Статус: частично продвинут.

Что сделано:

- `ClientProxy.setupEntityRenderers()` теперь регистрирует non-noop `RenderEntityItem` для item-like сущностей: `EntitySpecialItem`, `EntityPermanentItem`, `EntityFollowingItem`, `EntityItemGrate`.
- Для остальных сущностей из `ConfigEntities.ENTITIES` сохранён fallback `RenderNoop` coverage path.
- Обновлён `ClientProxyEntityRendererRegistrationStaticGuardTest`: зафиксированы item-like `RenderEntityItem` registrations и fallback branch по remaining entities.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это только item-like slice; projectile/mob/boss/golem custom renderers по GAP-2..GAP-6 остаются открытыми.
- Manual visual parity checks остаются пропущенными по инструкции.


### Checkpoint 2026-05-16 — projectile fallback renderer baseline

Статус: частично продвинут.

Что сделано:

- `ClientProxy.setupEntityRenderers()` теперь регистрирует non-noop `RenderSnowball` baseline для projectile группы:
  `EntityDart`, `EntityPrimalArrow`, `EntityBottleTaint`, `EntityAlumentum`, `EntityPrimalOrb`, `EntityFrostShard`,
  `EntityPechBlast`, `EntityEldritchOrb`, `EntityGolemOrb`, `EntityShockOrb`, `EntityExplosiveOrb`, `EntityEmber`, `EntityGolemBobber`.
- Для `EntityPrimalArrow` и `EntityBottleTaint` добавлен `ConfigItems`-aware item binding с fallback (`itemOrFallback(...)`).
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками projectile baseline registrations.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это fallback/bootstrapping слой, а не reference custom renderer parity.
- Специализированные модели/passes/GL-эффекты projectile и mob/boss renderers по GAP-2..GAP-6 остаются открытыми.


### Checkpoint 2026-05-16 — vanilla mob fallback slice

Статус: частично продвинут.

Что сделано:

- `ClientProxy.setupEntityRenderers()` дополнен non-noop vanilla fallback registrations для совместимых сущностей:
  `EntityBrainyZombie`, `EntityGiantBrainyZombie`, `EntityInhabitedZombie` -> `RenderZombie`;
  `EntityMindSpider`, `EntityTaintSpider` -> `RenderSpider`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками этой baseline-группы.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это vanilla fallback слой для type-compatible zombie/spider классов; custom TC рендеры/текстуры/passes остаются открытыми.
- Остальные entity группы по-прежнему проходят через fallback `RenderNoop`, пока не портированы специализированные renderers.


### Checkpoint 2026-05-16 — taint animal fallback slice

Статус: частично продвинут.

Что сделано:

- Добавлен `RenderFallbackLiving<T extends EntityLiving>` как лёгкий non-noop fallback renderer с явной `ResourceLocation` текстурой.
- `ClientProxy.setupEntityRenderers()` дополнен fallback registrations для taint animal-like группы:
  `EntityTaintChicken`, `EntityTaintCow`, `EntityTaintPig`, `EntityTaintSheep`, `EntityTaintVillager`, `EntityTaintCreeper`.
- Текстуры берутся из уже импортированного Stage 8-d asset corpus (`textures/models/chicken.png`, `cow.png`, `pig.png`, `sheep.png`, `villager.png`, `creeper.png`).
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками этих registration paths и наличия `RenderFallbackLiving`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это всё ещё fallback baseline, а не reference custom renderer parity (модели/слои/анимации/passes TC4 остаются открытыми).
- Часть entity групп Stage 8-d по-прежнему использует `RenderNoop` до портирования специализированных renderers.


### Checkpoint 2026-05-16 — cultist biped fallback slice

Статус: частично продвинут.

Что сделано:

- Добавлен `RenderFallbackBiped<T extends EntityLiving>` как лёгкий non-noop biped fallback renderer с явной `ResourceLocation` текстурой.
- `ClientProxy.setupEntityRenderers()` дополнен fallback registrations для cultist-группы:
  `EntityCultistKnight`, `EntityCultistCleric`, `EntityCultistLeader`.
- Для этой группы использован `ModelBiped` + texture baseline `textures/models/cultist.png` из Stage 8-d asset corpus.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками registration paths и наличия `RenderFallbackBiped`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это fallback baseline; reference-specific cultist render passes/armor/model parity остаются открытыми.
- Большая часть Stage 8-d entity групп всё ещё на `RenderNoop` до портирования специализированных renderer-классов.


### Checkpoint 2026-05-16 — remaining monster fallback slice

Статус: частично продвинут.

Что сделано:

- `ClientProxy.setupEntityRenderers()` расширен fallback registrations для оставшейся monster-группы:
  `EntityFireBat`, `EntityWisp`, `EntityPech`, `EntityEldritchGuardian`, `EntityEldritchWarden`,
  `EntityEldritchGolem`, `EntityEldritchCrab`, `EntityThaumicSlime`,
  `EntityTaintSpore`, `EntityTaintSporeSwarmer`, `EntityTaintSwarm`,
  `EntityTaintacle`, `EntityTaintacleSmall`, `EntityTaintacleGiant`.
- Для группы использованы `RenderFallbackLiving`/`RenderFallbackBiped` с texture baseline из `textures/models/*` и `textures/misc/wispy.png`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками этого baseline-регистрационного среза.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это fallback слой; reference-specific TC renderer parity (model/passes/animation/GL behavior) остаётся открытой.
- `RenderNoop` ещё остаётся для групп, не покрытых fallback slice (включая golem/trunk/cultist portal и иные special cases).


### Checkpoint 2026-05-16 — special entities fallback completion slice

Статус: частично продвинут.

Что сделано:

- `ClientProxy.setupEntityRenderers()` дополнен non-noop fallback registrations для оставшихся special entity classes:
  `EntityAspectOrb`, `EntityFallingTaint`, `EntityGolemBase`, `EntityTravelingTrunk`, `EntityCultistPortal`.
- После этого среза весь текущий `ConfigEntities.ENTITIES` имеет explicit non-noop baseline registration path
  (через `RenderEntityItem` / `RenderSnowball` / `RenderZombie` / `RenderSpider` / `RenderFallbackLiving` / `RenderFallbackBiped`).
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками special-entity registration paths.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это завершение fallback baseline, но не custom renderer parity Stage 8-d: reference-specific модели/слои/анимации/passes остаются открытыми.
- Visual/manual parity checks по инструкции остаются skipped.


### Checkpoint 2026-05-16 — renderer coverage guard by config

Статус: quality/verification hardening.

Что сделано:

- Добавлен `ClientProxyEntityRendererCoverageByConfigTest`, который:
  - читает `ConfigEntities.java` и извлекает все `makeEntry(Entity*.class)` entries;
  - читает `ClientProxy.java` и извлекает все `registerEntityRenderer(Entity*.class, ...)` registrations;
  - падает, если есть хотя бы один entity из `ConfigEntities`, не имеющий explicit renderer registration в `ClientProxy`.
- Тест фиксирует текущий baseline и защищает от silent regressions, когда новый entity может попасть в `RenderNoop` fallback из-за пропущенной явной регистрации.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Тест проверяет coverage по регистрациям, но не доказывает visual parity reference renderers.

