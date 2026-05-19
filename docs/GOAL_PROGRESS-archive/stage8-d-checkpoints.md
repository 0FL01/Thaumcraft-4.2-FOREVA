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

### Checkpoint 2026-05-16 — dedicated pech renderer baseline

Статус: частично продвинут.

Что сделано:

- Добавлен выделенный renderer `RenderPech` (`thaumcraft.client.renderers.entity.RenderPech`) вместо общего biped fallback для `EntityPech`.
- `RenderPech` реализует базовый reference-shaped texture routing по `entity.getPechType()` с TC4 texture set:
  - `textures/models/pech_forage.png`
  - `textures/models/pech_thaum.png`
  - `textures/models/pech_stalker.png`
- `ClientProxy.setupEntityRenderers()` обновлен: `EntityPech` теперь регистрируется через `RenderPech::new`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками:
  - explicit `EntityPech -> RenderPech` registration path;
  - наличие `RenderPech` с type-based texture routing и всеми тремя texture paths.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это baseline по texture routing; full reference parity для `RenderPech` (custom model/held item/overlay behavior) остаётся открытой по GAP-3/GAP-6.

### Checkpoint 2026-05-16 — dedicated firebat renderer baseline

Статус: частично продвинут.

Что сделано:

- Добавлен выделенный renderer `RenderFireBat` (`thaumcraft.client.renderers.entity.RenderFireBat`) вместо общего living fallback для `EntityFireBat`.
- `RenderFireBat` реализует базовый reference-shaped behavior:
  - texture routing: `firebat.png` / `vampirebat.png` через `entity.getIsVampire()`;
  - pre-render scale baseline: enlarged для `entity.getIsDevil() || entity.getIsVampire()`, reduced для остальных;
  - hanging/flying vertical transform baseline через `entity.getIsBatHanging()`.
- `ClientProxy.setupEntityRenderers()` обновлен: `EntityFireBat` теперь регистрируется через `RenderFireBat::new`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками:
  - explicit `EntityFireBat -> RenderFireBat` registration path;
  - наличие в `RenderFireBat` vampire texture routing и hanging/scale guards.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это baseline по texture/transform behavior; full reference parity для `RenderFireBat` (original `ModelFireBat`, точные animation/scale nuances) остаётся открытой по GAP-3/GAP-6.

### Checkpoint 2026-05-16 — dedicated traveling trunk renderer baseline

Статус: частично продвинут.

Что сделано:

- Добавлен выделенный renderer `RenderTravelingTrunk` (`thaumcraft.client.renderers.entity.RenderTravelingTrunk`) вместо общего living fallback для `EntityTravelingTrunk`.
- `RenderTravelingTrunk` реализует базовый reference-shaped texture routing:
  - `textures/models/trunk.png` для обычного состояния;
  - `textures/models/trunkangry.png` при `entity.getAnger() > 0`.
- В `EntityTravelingTrunk` добавлен accessor `getAnger()` для стабильного client renderer contract.
- `ClientProxy.setupEntityRenderers()` обновлен: `EntityTravelingTrunk` теперь регистрируется через `RenderTravelingTrunk::new`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками:
  - explicit `EntityTravelingTrunk -> RenderTravelingTrunk` registration path;
  - наличие anger-based texture routing в `RenderTravelingTrunk`;
  - наличие `EntityTravelingTrunk#getAnger()`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это texture-routing baseline; full reference parity для traveling trunk renderer/model behavior (оригинальная `ModelTrunk` lid/scale transforms) остаётся открытой по GAP-3/GAP-4/GAP-6.

### Checkpoint 2026-05-16 — dedicated golem base renderer texture baseline

Статус: частично продвинут.

Что сделано:

- Добавлен выделенный renderer `RenderGolemBase` (`thaumcraft.client.renderers.entity.RenderGolemBase`) вместо общего biped fallback для `EntityGolemBase`.
- `RenderGolemBase` реализует базовый reference-shaped texture routing по `entity.getGolemType()`:
  - `golem_straw.png`, `golem_wood.png`, `golem_tallow.png`, `golem_clay.png`,
    `golem_flesh.png`, `golem_stone.png`, `golem_iron.png`, `golem_thaumium.png`.
- `ClientProxy.setupEntityRenderers()` обновлен: `EntityGolemBase` теперь регистрируется через `RenderGolemBase::new`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками:
  - explicit `EntityGolemBase -> RenderGolemBase` registration path;
  - наличие type-based texture routing и полного texture set в `RenderGolemBase`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это texture-routing baseline; full reference parity для golem renderer behavior (custom golem model/accessory passes/carried-item rendering/damage overlay) остаётся открытой по GAP-3/GAP-4/GAP-6.

### Checkpoint 2026-05-16 — dedicated wisp renderer color baseline

Статус: частично продвинут.

Что сделано:

- Добавлен выделенный renderer `RenderWisp` (`thaumcraft.client.renderers.entity.RenderWisp`) вместо общего living fallback для `EntityWisp`.
- `RenderWisp` реализует базовый reference-shaped визуальный контракт:
  - использует `textures/misc/wispy.png`;
  - окрашивает рендер по `Aspect.getAspect(entity.getWispType())`;
  - после рендера сбрасывает цвет в белый для предотвращения bleed в последующие draw calls.
- `ClientProxy.setupEntityRenderers()` обновлен: `EntityWisp` теперь регистрируется через `RenderWisp::new`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками:
  - explicit `EntityWisp -> RenderWisp` registration path;
  - наличие aspect-tint color routing и color reset в `RenderWisp`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это color/texture baseline; full reference parity для `RenderWisp` (custom sprite layers/additive passes/GL behavior) остаётся открытой по GAP-3/GAP-6.

### Checkpoint 2026-05-16 — dedicated eldritch guardian/warden renderer baseline

Статус: частично продвинут.

Что сделано:

- Добавлены выделенные renderer-классы:
  - `RenderEldritchGuardian` (`thaumcraft.client.renderers.entity.RenderEldritchGuardian`);
  - `RenderEldritchWarden` (`thaumcraft.client.renderers.entity.RenderEldritchWarden`).
- `ClientProxy.setupEntityRenderers()` обновлен:
  - `EntityEldritchGuardian` теперь регистрируется через `RenderEldritchGuardian::new`;
  - `EntityEldritchWarden` теперь регистрируется через `RenderEldritchWarden::new`.
- Baseline behavior:
  - `RenderEldritchGuardian` использует dedicated texture `textures/models/eldritch_guardian.png`;
  - `RenderEldritchWarden` использует dedicated texture `textures/models/eldritch_warden.png` и baseline масштаб `1.5x` (reference-shaped warden size differentiation).
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками explicit registrations и renderer contracts для обоих классов.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это renderer identity/texture/scale baseline; full reference parity для guardian/warden render behavior (alpha/blend distance behavior, spawn-lift visuals, custom model animation nuances) остаётся открытой по GAP-3/GAP-4/GAP-6.

### Checkpoint 2026-05-16 — dedicated eldritch golem renderer baseline

Статус: частично продвинут.

Что сделано:

- Добавлен выделенный renderer `RenderEldritchGolem` (`thaumcraft.client.renderers.entity.RenderEldritchGolem`) вместо общего biped fallback для `EntityEldritchGolem`.
- Baseline behavior:
  - dedicated texture path `textures/models/eldritch_golem.png`;
  - reference-shaped size baseline через `GlStateManager.scale(2.15F, 2.15F, 2.15F)`.
- `ClientProxy.setupEntityRenderers()` обновлен: `EntityEldritchGolem` теперь регистрируется через `RenderEldritchGolem::new`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками:
  - explicit `EntityEldritchGolem -> RenderEldritchGolem` registration path;
  - наличие texture/scale contracts в `RenderEldritchGolem`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это texture/scale baseline; full reference parity для eldritch golem renderer behavior (custom golem model, boss bar/display path, additional render passes) остаётся открытой по GAP-3/GAP-4/GAP-6.

### Checkpoint 2026-05-16 — dedicated eldritch crab renderer baseline

Статус: частично продвинут.

Что сделано:

- Добавлен выделенный renderer `RenderEldritchCrab` (`thaumcraft.client.renderers.entity.RenderEldritchCrab`) вместо общего living fallback для `EntityEldritchCrab`.
- Baseline behavior:
  - dedicated base texture `textures/models/crab.png`;
  - добавлен overlay layer `CrabOverlayLayer` с `textures/models/craboverlay.png` и alpha-blend pass.
- `ClientProxy.setupEntityRenderers()` обновлен: `EntityEldritchCrab` теперь регистрируется через `RenderEldritchCrab::new`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками:
  - explicit `EntityEldritchCrab -> RenderEldritchCrab` registration path;
  - наличие base texture, overlay texture и overlay-layer wiring в `RenderEldritchCrab`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это texture/overlay baseline; full reference parity для eldritch crab renderer behavior (original custom crab model, overlay/lightmap specifics) остаётся открытой по GAP-3/GAP-4/GAP-6.

### Checkpoint 2026-05-16 — dedicated cultist portal renderer baseline

Статус: частично продвинут.

Что сделано:

- Добавлен выделенный renderer `RenderCultistPortal` (`thaumcraft.client.renderers.entity.RenderCultistPortal`) вместо общего biped fallback для `EntityCultistPortal`.
- Baseline behavior:
  - dedicated texture path `textures/misc/cultist_portal.png`.
- `ClientProxy.setupEntityRenderers()` обновлен: `EntityCultistPortal` теперь регистрируется через `RenderCultistPortal::new`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками:
  - explicit `EntityCultistPortal -> RenderCultistPortal` registration path;
  - наличие dedicated texture contract в `RenderCultistPortal`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это texture-only baseline; full reference parity для cultist portal renderer behavior (animated portal billboard, pulse/hurt-scale modulation, detailed GL pass behavior) остаётся открытой по GAP-3/GAP-6.

### Checkpoint 2026-05-16 — dedicated thaumic slime renderer baseline

Статус: частично продвинут.

Что сделано:

- Добавлен выделенный renderer `RenderThaumicSlime` (`thaumcraft.client.renderers.entity.RenderThaumicSlime`) вместо общего biped fallback для `EntityThaumicSlime`.
- Baseline behavior:
  - dedicated texture path `textures/models/tslime.png`;
  - slime-size/squish-based scale callback использует `getSlimeSize()`, `field_70811_b`, `field_70812_c`;
  - добавлен translucent gel layer `SlimeGelLayer` с тем же texture baseline.
- `ClientProxy.setupEntityRenderers()` обновлен: `EntityThaumicSlime` теперь регистрируется через `RenderThaumicSlime::new`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками:
  - explicit `EntityThaumicSlime -> RenderThaumicSlime` registration path;
  - наличие texture/scale/gel-layer contracts в `RenderThaumicSlime`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это baseline по texture/scale/layer wiring; full reference parity для thaumic slime renderer behavior (оригинальная двухмодельная render-pass конфигурация и GL normalize/blend нюансы) остаётся открытой по GAP-3/GAP-4/GAP-6.

### Checkpoint 2026-05-16 — taint spore trio dedicated renderer baseline

Статус: частично продвинут.

Почему grouped в один commit:

- `EntityTaintSpore`, `EntityTaintSporeSwarmer`, `EntityTaintSwarm` образуют единый tightly-coupled taint-spore rendering cluster (общий texture contract и соседние registration paths в одном участке `ClientProxy.setupEntityRenderers()`), поэтому зафиксированы одним checkpoint.

Что сделано:

- Добавлены выделенные renderer-классы:
  - `RenderTaintSpore`;
  - `RenderTaintSporeSwarmer`;
  - `RenderTaintSwarm` (dedicated noop baseline, как reference-shaped no-render class).
- `ClientProxy.setupEntityRenderers()` обновлен:
  - `EntityTaintSpore -> RenderTaintSpore::new`;
  - `EntityTaintSporeSwarmer -> RenderTaintSporeSwarmer::new`;
  - `EntityTaintSwarm -> RenderTaintSwarm::new`.
- Baseline behavior:
  - `RenderTaintSpore` использует `textures/models/taint_spore.png` и display-size scale callback (`displaySize`/`getSporeSize`);
  - `RenderTaintSporeSwarmer` использует dedicated `taint_spore.png` texture baseline;
  - `RenderTaintSwarm` выделен в dedicated noop renderer для explicit contract parity.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками explicit registration paths и renderer contracts для trio.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это baseline по renderer identity/texture/scale; full reference parity для оригинальных custom model/render-pass деталей trio остаётся открытой по GAP-3/GAP-4/GAP-6.

### Checkpoint 2026-05-16 — taintacle trio dedicated renderer baseline

Статус: частично продвинут.

Почему grouped в один commit:

- `EntityTaintacle`, `EntityTaintacleSmall`, `EntityTaintacleGiant` используют общий texture contract и общий renderer pattern (различается только scale profile), поэтому это единый tightly-coupled rendering slice.

Что сделано:

- Добавлен shared dedicated renderer `RenderTaintacle<T extends EntityLiving>`.
- `ClientProxy.setupEntityRenderers()` обновлен:
  - `EntityTaintacle -> new RenderTaintacle<>(..., 0.6F, 1.0F)`;
  - `EntityTaintacleSmall -> new RenderTaintacle<>(..., 0.45F, 0.85F)`;
  - `EntityTaintacleGiant -> new RenderTaintacle<>(..., 0.8F, 1.33F)`.
- Baseline behavior:
  - shared texture `textures/models/taintacle.png`;
  - per-registration size differentiation через `scaleMultiplier` (включая giant 1.33x baseline).
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками trio registration paths и `RenderTaintacle` contracts.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это baseline по texture/scale wiring; full reference parity для taintacle trio (custom model-driven animation nuances и giant boss display details) остаётся открытой по GAP-3/GAP-4/GAP-6.

### Checkpoint 2026-05-16 — brainy/inhabited zombie dedicated renderer baseline

Статус: частично продвинут.

Почему grouped в один commit:

- `EntityBrainyZombie`, `EntityGiantBrainyZombie`, `EntityInhabitedZombie` составляют tightly-coupled zombie-rendering slice с общим базовым `RenderZombie` поведением и thaumcraft texture-set (`bzombie`/`czombie`), поэтому закрыты одним checkpoint.

Что сделано:

- Добавлены выделенные renderer-классы:
  - `RenderBrainyZombie` (dedicated `textures/models/bzombie.png`);
  - `RenderInhabitedZombie` (dedicated `textures/models/czombie.png`).
- `ClientProxy.setupEntityRenderers()` обновлен:
  - `EntityBrainyZombie -> RenderBrainyZombie::new`;
  - `EntityGiantBrainyZombie -> RenderBrainyZombie::new`;
  - `EntityInhabitedZombie -> RenderInhabitedZombie::new`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками registration paths и texture contracts для обоих renderer-классов.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это texture-identity baseline; full reference parity для brainy/inhabited zombie renderer behavior (villager-model switching path и giant-specific anger-based scaling semantics из 1.7.10) остаётся открытой по GAP-3/GAP-6.

### Checkpoint 2026-05-16 — mind/taint spider dedicated renderer baseline

Статус: частично продвинут.

Почему grouped в один commit:

- `EntityMindSpider` и `EntityTaintSpider` составляют tightly-coupled spider-rendering pair с общими texture/eyes-layer contracts (`taint_spider` / `taint_spider_eyes`), поэтому закрыты одним checkpoint.

Что сделано:

- Добавлены выделенные renderer-классы:
  - `RenderMindSpider`;
  - `RenderTaintSpider`.
- `ClientProxy.setupEntityRenderers()` обновлен:
  - `EntityMindSpider -> RenderMindSpider::new`;
  - `EntityTaintSpider -> RenderTaintSpider::new`.
- Baseline behavior:
  - оба renderer используют `textures/models/taint_spider.png` как base texture;
  - добавлен dedicated eyes layer (`textures/models/taint_spider_eyes.png`) с additive blend;
  - baseline scale callbacks привязаны к текущему `entity.width` (для taint spider сохранен 1.25x Y-profile).
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками registration paths и spider texture/layer contracts.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это baseline по texture/eyes-layer/scale; full reference parity для spider-specific behavior (mind-spider viewer-only visibility semantics и точные reference GL/lightmap нюансы) остаётся открытой по GAP-3/GAP-6.

### Checkpoint 2026-05-16 — cultist trio dedicated renderer baseline

Статус: частично продвинут.

Почему grouped в один commit:

- `EntityCultistKnight`, `EntityCultistCleric`, `EntityCultistLeader` составляют tightly-coupled cultist-rendering group с общим texture contract (`cultist.png`) и одинаковой renderer base class, поэтому закрыты одним checkpoint.

Что сделано:

- Добавлен shared dedicated renderer `RenderCultist<T extends EntityLiving>`.
- `ClientProxy.setupEntityRenderers()` обновлен:
  - `EntityCultistKnight -> new RenderCultist<>(..., 0.5F)`;
  - `EntityCultistCleric -> new RenderCultist<>(..., 0.5F)`;
  - `EntityCultistLeader -> new RenderCultist<>(..., 0.6F)`.
- Baseline behavior:
  - shared texture `textures/models/cultist.png`;
  - пер-entity shadow профили сохранены по предыдущему baseline.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками cultist registration paths и texture contract `RenderCultist`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это texture/identity baseline; full reference parity для cultist renderer behavior (leader/cleric specific visual nuances, potential special overlays/held-item pose details) остаётся открытой по GAP-3/GAP-6.

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

### Checkpoint 2026-05-17 — restore taint chicken AI/fall/sound behavior baseline

Статус: частично продвинут.

Что сделано:

- `EntityTaintChicken` выровнен с reference-shaped common behavior contracts:
  - восстановлен constructor AI stack (swim, player/villager/animal attack tasks, leap, wander, watch, idle, target tasks);
  - добавлены hooks `canBreatheUnderwater() -> true` и `canDespawn() -> false`;
  - добавлен empty `fall(float, float)` override (no fall-side fuse/damage behavior);
  - восстановлен early client taint FX loop в первые `5` ticks (`particleCount(10)` + `taintLandFX(this)`);
  - death sound contract выровнен к hurt baseline (`SoundEvents.ENTITY_CHICKEN_HURT`), как в reference string-path behavior.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками на AI/fall/sound contracts `EntityTaintChicken`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common behavior baseline; визуальная точность эффекта раннего FX ограничена non-GUI validation рамками.

### Checkpoint 2026-05-17 — restore taint cow AI/attribute/sound behavior baseline

Статус: частично продвинут.

Что сделано:

- `EntityTaintCow` выровнен с reference-shaped common behavior contracts:
  - восстановлены constructor size/nav/AI baselines:
    - `setSize(0.9F, 1.3F)`;
    - `PathNavigateGround#setCanSwim(true)` when compatible;
    - attack/wander/watch/idle/task priorities + target priorities для player/villager/animal.
  - восстановлены атрибуты: `MAX_HEALTH = 40.0D`, `ATTACK_DAMAGE = 6.0D`, `MOVEMENT_SPEED = 0.27D`;
  - добавлен `canBreatheUnderwater() -> true`;
  - добавлен early client taint FX loop в первые `5` ticks (`particleCount(10)` + `taintLandFX(this)`);
  - death sound contract выровнен к hurt baseline (`SoundEvents.ENTITY_COW_HURT`) как в reference string-path shape.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками на AI/attribute/sound contracts `EntityTaintCow`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common behavior baseline; полная visual parity раннего FX и runtime боевых сценариев ограничена non-GUI validation рамками.

### Checkpoint 2026-05-17 — restore taint pig AI/attribute/sound/drop behavior baseline

Статус: частично продвинут.

Что сделано:

- `EntityTaintPig` выровнен с reference-shaped common behavior contracts:
  - восстановлены constructor size/nav/AI baselines:
    - `setSize(0.9F, 0.9F)`;
    - `PathNavigateGround#setCanSwim(true)` when compatible;
    - attack/wander/watch/idle/task priorities + target priorities для player/villager/animal.
  - восстановлены атрибуты: `MAX_HEALTH = 20.0D`, `ATTACK_DAMAGE = 4.0D`, `MOVEMENT_SPEED = 0.275D`;
  - добавлены hooks `canBreatheUnderwater() -> true`, `canDespawn() -> false`, `getMaxSpawnedInChunk() -> 2`;
  - восстановлен early client taint FX loop в первые `5` ticks (`particleCount(10)` + `taintLandFX(this)`);
  - sound/drop contracts выровнены:
    - ambient/hurt -> `SoundEvents.ENTITY_PIG_AMBIENT`;
    - death -> `SoundEvents.ENTITY_PIG_DEATH`;
    - resource-11 drop chance через `rand.nextInt(3) == 0`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками на AI/attribute/sound/drop contracts `EntityTaintPig`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common behavior baseline; полная visual parity раннего FX и runtime боевых сценариев ограничена non-GUI validation рамками.

### Checkpoint 2026-05-17 — restore taint sheep attribute/survivability/fx/sound/drop behavior baseline

Статус: частично продвинут.

Что сделано:

- `EntityTaintSheep` выровнен с reference-shaped common behavior contracts:
  - атрибуты приведены к baseline: `MAX_HEALTH = 20.0D`, `ATTACK_DAMAGE = 3.0D`, `MOVEMENT_SPEED = 0.25D`;
  - добавлены survivability hooks: `canBreatheUnderwater() -> true`, `canDespawn() -> false`, `getTotalArmorValue() -> 1`;
  - восстановлен early client FX hook для первых `5` ticks через `Thaumcraft.proxy.taintLandFX(this)`;
  - sound contracts выровнены к sheep-say baseline (ambient/hurt/death -> `SoundEvents.ENTITY_SHEEP_AMBIENT`);
  - drop contract выровнен к reference rarity: resource-11/12 branch выполняется только при `rand.nextInt(3) == 0`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками на новые attribute/survivability/fx/sound/drop contracts `EntityTaintSheep`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common behavior baseline; полная visual parity раннего FX и runtime боевых сценариев ограничена non-GUI validation рамками.

### Checkpoint 2026-05-17 — restore taint villager AI/village/attribute/fx/drop behavior baseline

Статус: частично продвинут.

Что сделано:

- `EntityTaintVillager` выровнен с reference-shaped common behavior contracts:
  - восстановлен constructor AI baseline для hostile villager-профиля:
    - path nav flags (`setBreakDoors(true)`, `setCanSwim(true)`),
    - `EntityAIMoveIndoors`, `EntityAIRestrictOpenDoor`, `EntityAIOpenDoor`,
    - `EntityAIMoveTowardsRestriction`, `EntityAIMoveThroughVillage`,
    - `AIAttackOnCollide` по `EntityPlayer`,
    - watch/wander stack и target stack (`EntityAIHurtByTarget`, nearest player target).
  - атрибуты приведены к baseline: `MAX_HEALTH = 30.0D`, `ATTACK_DAMAGE = 4.0D`, `MOVEMENT_SPEED = 0.3D`;
  - добавлены survivability hooks: `canBreatheUnderwater() -> true`, `canDespawn() -> false`;
  - восстановлен village/home update hook в `updateAITasks()` (`addToVillagerPositionList`, `getNearestVillage`, `setHomePosAndDistance`, `detachHome`);
  - восстановлен revenge-village aggro hook через `Village#addOrRenewAgressor(...)`;
  - восстановлен early client FX hook для первых `5` ticks через `Thaumcraft.proxy.taintLandFX(this)`;
  - drop contract выровнен к reference rarity/bonus shape:
    - resource-11/12 branch только при `rand.nextInt(2) == 0`;
    - отдельный bonus drop resource-18 при `rand.nextInt(13) < 1 + looting`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками на AI/village/attribute/fx/drop contracts `EntityTaintVillager`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common behavior baseline; полная parity для legacy villager-datawatcher деталей и runtime сценариев осад/деревень ограничена non-GUI validation рамками.

### Checkpoint 2026-05-17 — restore taint swarm summoned/flight/attack behavior baseline

Статус: частично продвинут.

Что сделано:

- `EntityTaintSwarm` выровнен с reference-shaped common behavior contracts:
  - восстановлены state/flag hooks: `DataParameter<Byte> FLAGS`, summoned-bit (`FLAG_SUMMONED`), `getIsSummoned()`/`setIsSummoned(...)`, `damBonus` persistence;
  - восстановлены size/attribute baselines: `setSize(2.0F, 2.0F)`, `MAX_HEALTH = 30.0D`, `ATTACK_DAMAGE = 2.0D + damBonus`;
  - восстановлены survivability/light hooks: fullbright (`getBrightnessForRender=15728880`, `getBrightness=1.0F`), underwater breathing, no-despawn, light-threshold spawn gate;
  - восстановлены flight/targeting contracts:
    - summoned starvation self-damage (`DamageSource.STARVE`, `5.0F`);
    - nearest-player acquisition (`12.0D`) when not summoned;
    - taint-biome constrained flight target refresh/steering;
    - creative-player target drop.
  - восстановлен melee side-effect baseline:
    - summoned hit marks target via `EntityUtils.setRecentlyHit(..., 100)`;
    - nausea debuff (`MobEffects.NAUSEA`, `100` ticks) on successful hit;
    - velocity restoration for attacked target после melee call.
  - восстановлены NBT/drop contracts:
    - `"Flags"`/`"damBonus"` read/write;
    - drop branch: `rand.nextBoolean()` -> `itemResource` meta `11`.
  - добавлен client-side particle baseline loop guard на `particleCount(25)` для swarm visual lifecycle without GUI coupling.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками на summoned/flight/attack/NBT/drop contracts `EntityTaintSwarm`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common/server behavior baseline; точная visual parity swarm particle FX pipeline остаётся в Stage 8-e visual scope и не подтверждается manual GUI checks по инструкции.

### Checkpoint 2026-05-17 — restore taint spore swarmer spawn-counter/swarm-burst behavior baseline

Статус: частично продвинут.

Что сделано:

- `EntityTaintSporeSwarmer` выровнен с reference-shaped common behavior contracts:
  - восстановлен constructor baseline: `spawnCounter = 500`, `setSporeSize(10)`;
  - восстановлен swarmer-size contract (`setSporeSize(...)` фиксирует hitbox `1.0F/1.0F`);
  - восстановлены атрибуты: `MAX_HEALTH = 75.0D`, `ATTACK_DAMAGE = 1.0D`;
  - восстановлен hurt-client hook (`sploosh(10)` на `attackEntityFrom(...)` client-side);
  - восстановлен swarmer update loop:
    - `pushOutOfBlocks(...)`;
    - decrement `spawnCounter`;
    - burst trigger при `spawnCounter <= 0` и nearby player (`16.0D`);
    - forced burst on hurt-resistance edge (`hurtResistantTime == 1`);
    - client swarm-particle buildup proportional to counter stage.
  - восстановлен `swarmBurst(int)` server path:
    - gore sound;
    - spawn `EntityTaintSwarm`;
    - status sync packet `world.setEntityState(this, (byte)6)`.
  - восстановлен `handleStatusUpdate((byte)6)` client path:
    - counter reset;
    - `sploosh(25)`;
    - local particle-list reset.
  - ambient/drop contracts выровнены:
    - ambient -> `TCSounds.ROOTS`;
    - drop loop дважды (`0..1`) с meta `11/12` random branch.
  - добавлены NBT hooks для `SpawnCounter` persistence.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками на spawn-counter/swarm-burst contracts `EntityTaintSporeSwarmer`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common/server baseline; точная legacy visual parity swarmer particle-object lifecycle остаётся в Stage 8-e visual scope и не подтверждается manual GUI checks по инструкции.

### Checkpoint 2026-05-17 — restore taint spore fullbright/swarm-particle/burst behavior baseline

Статус: частично продвинут.

Что сделано:

- `EntityTaintSpore` выровнен с reference-shaped common behavior contracts:
  - добавлен `swarm` particle-lifecycle list для клиентского swarm-визуального цикла;
  - восстановлены render hooks:
    - `getYOffset() -> 0.0D`;
    - `isInRangeToRenderDist(distance < 4096.0D)`;
    - `getBrightnessForRender() -> 15728880`;
    - `getBrightness() -> 1.0F`.
  - восстановлен client swarm-particle buildup в `sporeOnUpdate()`:
    - `displaySize` interpolation сохранён;
    - particle list cleanup + `SPELL_MOB` spawn branch до `getSporeSize()/3`.
  - восстановлен dedicated burst helper `sploosh(int)` и client burst path в `spiderBurst()` через `sploosh(50)`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками на size/fullbright/swarm-particle/burst contracts `EntityTaintSpore`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это non-GUI parity baseline; точный legacy `swarmParticleFX` object-lifecycle остается частью Stage 8-e visual scope.

### Checkpoint 2026-05-17 — restore taintacle small lifetime/attribute/no-drop baseline

Статус: частично продвинут.

Что сделано:

- `EntityTaintacleSmall` выровнен с reference-shaped common behavior contracts:
  - добавлен `lifetime` baseline (`200`);
  - constructor baseline обновлён: `setSize(0.22F, 1.0F)`, `experienceValue = 0`;
  - атрибуты выровнены: `MAX_HEALTH = 8.0D`, `ATTACK_DAMAGE = 2.0D`;
  - восстановлен lifetime-expiry behavior: при исчерпании lifetime наносится self-damage `DamageSource.STARVE` (`10.0F`);
  - spawn/drop contracts выровнены:
    - `getCanSpawnHere() -> false`;
    - `getDropItem() -> Item.getItemById(0)`;
    - `dropFewItems(...)` no-op.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками на lifetime/attribute/no-drop contracts `EntityTaintacleSmall`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это common behavior baseline для small tentacle; полная parity по taintacle ecosystem visual/FX nuances остаётся в Stage 8-d/8-e scope.

### Checkpoint 2026-05-17 — restore taintacle giant damageable-underwater survivability baseline

Статус: частично продвинут.

Что сделано:

- `EntityTaintacleGiant` выровнен с reference-shaped survivability contracts:
  - удалён ошибочный always-invulnerable override (`isEntityInvulnerable(...) -> true`), который ломал damage/enrage flow;
  - добавлен explicit underwater breathing contract `canBreatheUnderwater() -> true`;
  - сохранён reference-shaped air handling hook `decreaseAirSupply(int air) -> air`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками:
  - presence giant spawn/despawn/underwater/air contracts;
  - explicit absence regressions вида always-invulnerable override.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это survivability/combat baseline; полная parity giant boss UX/visual behavior остаётся в общем Stage 8-d/8-e render scope.

### Checkpoint 2026-05-17 — restore taintacle core spawn/target/combat baseline

Статус: частично продвинут.

Что сделано:

- `EntityTaintacle` выровнен с reference-shaped core behavior contracts:
  - восстановлен spawn-gating baseline:
    - proximity gate (no nearby taintacles in local radius);
    - substrate gate (`blockTaintFibres` type `0` or `blockTaint` type `1`) + taint biome;
    - `getYOffset() -> 0.25D`.
  - восстановлены movement/interaction hooks:
    - rooted move (X/Z clamp, no upward motion);
    - `canTriggerWalking() -> false`.
  - восстановлен target/combat lifecycle:
    - nearest non-tainted living target acquisition (`findNearestTarget`);
    - agitation gate (`getAgitationState`);
    - manual yaw tracking (`faceEntity` + `updateRotation`);
    - melee/range split in `attackTentacle(...)` with cooldown.
  - `attackEntityAsMob(...)` расширен до reference-shaped damage path:
    - tentacle damage source;
    - enchantment creature bonus/knockback/fire-aspect integration;
    - thorn/arthropod enchantment side-effects.
  - восстановлен `spawnTentacles(...)` behavior:
    - spawn `EntityTaintacleSmall` near attacker;
    - taint conversion hook for eldritch biome path (`Utils.setBiomeAt` + taint fibres placement);
    - cooldown/sound contracts.
  - `attackEntityFrom(...)` восстановлен: при дальнем атакере (`>16`) триггерит secondary tentacle spawn (non-small, server-side).
  - sound contracts дополнены reference pitch profile: `getSoundPitch() -> 1.3F - height/10.0F`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен проверками на spawn/target/combat/tentacle-spawn contracts `EntityTaintacle`.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это core behavior baseline; точная parity legacy client-only `tentacleAriseFX` visual pipeline остаётся в Stage 8-e visual scope.

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

### Checkpoint 2026-05-17 — replace aspect orb snowball fallback with dedicated renderer baseline

Статус: частично продвинут.

Что сделано:

- `EntityAspectOrb` переведен с fallback `RenderSnowball` на dedicated `RenderAspectOrb`.
- Добавлен новый `RenderAspectOrb` (billboard quad) с reference-shaped contracts:
  - particle-atlas binding (`textures/misc/particles.png`);
  - aspect tint + alpha baseline (`aspect.getColor()`, alpha `128`);
  - dest blend-factor mapping из `aspect.getBlend()` в `GlStateManager.DestFactor`;
  - age-driven render scale (`orbMaxAge - orbAge`);
  - fullbright lightmap coords и camera-facing rotation.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` обновлен:
  - registration contract: `EntityAspectOrb -> RenderAspectOrb::new`;
  - renderer contract: texture/billboard/blend/scale vertex-format surface.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это renderer behavior baseline без ручной визуальной валидации; итоговая visual parity (exact legacy GL state nuances) остаётся в общем Stage 8-d runtime/manual matrix.

### Checkpoint 2026-05-17 — replace golem/shock orb snowball fallbacks with shared electric-orb renderer baseline

Статус: частично продвинут.

Что сделано:

- Tightly-coupled projectile pair `EntityGolemOrb` + `EntityShockOrb` переведен с `RenderSnowball` на shared dedicated `RenderElectricOrb` (один checkpoint по общему reference renderer contract `RenderElectricOrb`).
- Добавлен `RenderElectricOrb` с reference-shaped contracts:
  - particle-atlas binding (`textures/misc/particles.png`);
  - additive blend billboard facing camera;
  - red-variant UV routing для `EntityGolemOrb#red`;
  - sinusoidal bob/scale profile (`sin(ticksExisted / 5.0F)`).
- `ClientProxyEntityRendererRegistrationStaticGuardTest` расширен:
  - registration contracts `EntityGolemOrb -> RenderElectricOrb::new` и `EntityShockOrb -> RenderElectricOrb::new`;
  - renderer contract checks на shared particle/red-branch/scale behavior.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это renderer baseline без ручной визуальной проверки; точные legacy GL/lightmap nuances остаются в общем Stage 8-d visual parity matrix.

### Checkpoint 2026-05-17 — replace explosive orb snowball fallback with dedicated renderer baseline

Статус: частично продвинут.

Что сделано:

- `EntityExplosiveOrb` переведен с `RenderSnowball` на dedicated `RenderExplosiveOrb`.
- Добавлен `RenderExplosiveOrb` с reference-shaped contracts:
  - particles2 atlas binding (`textures/misc/particles2.png`);
  - alpha blend billboard facing camera;
  - frame animation via `ticksExisted % 4`;
  - fixed projectile scale baseline `0.7F`.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` обновлен:
  - registration contract `EntityExplosiveOrb -> RenderExplosiveOrb::new`;
  - renderer contract checks на particles2/animation/scale surface.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это renderer baseline без ручной визуальной проверки; exact legacy GL-state нюансы остаются в общем Stage 8-d visual parity matrix.

### Checkpoint 2026-05-17 — replace ember snowball fallback with dedicated renderer baseline

Статус: частично продвинут.

Что сделано:

- `EntityEmber` переведен с `RenderSnowball` на dedicated `RenderEmber`.
- Добавлен `RenderEmber` с reference-shaped contracts:
  - particle atlas binding (`textures/misc/particles.png`);
  - additive billboard facing camera;
  - duration-driven frame/scale profile (`ticksExisted / duration`, `0.25F + lifeFraction`);
  - alpha/tint and lightmap baseline for projectile FX sprite.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` обновлен:
  - registration contract `EntityEmber -> RenderEmber::new`;
  - renderer contract checks на duration/scale/frame-surface behavior.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это renderer baseline без ручной визуальной проверки; точные legacy GL nuances остаются в общем Stage 8-d visual parity matrix.

### Checkpoint 2026-05-17 — replace eldritch orb snowball fallback with dedicated renderer baseline

Статус: частично продвинут.

Что сделано:

- `EntityEldritchOrb` переведен с `RenderSnowball` на dedicated `RenderEldritchOrb`.
- Добавлен `RenderEldritchOrb` с reference-shaped contracts:
  - multi-pass renderer: spike-burst pass + billboard pass;
  - spike-burst color baseline на `BlockCustomOreItem.colors[5]`;
  - particle atlas billboard (`textures/misc/particles.png`) с frame profile `ticksExisted % 13`;
  - additive/alpha blend split и fullbright billboard lightmap baseline.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` обновлен:
  - registration contract `EntityEldritchOrb -> RenderEldritchOrb::new`;
  - renderer contract checks на spike/billboard/color/frame surface.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это renderer baseline без ручной визуальной проверки; exact legacy timing/GL nuances остаются в общем Stage 8-d visual parity matrix.

### Checkpoint 2026-05-17 — replace primal orb snowball fallback with dedicated renderer baseline

Статус: частично продвинут.

Что сделано:

- `EntityPrimalOrb` переведен с `RenderSnowball` на dedicated `RenderPrimalOrb`.
- Добавлен `RenderPrimalOrb` с reference-shaped contracts:
  - multi-pass renderer: primal-colored spike-burst + billboard pass;
  - spike-burst palette baseline `BlockCustomOreItem.colors[i / 2 + 1]`;
  - particle atlas billboard (`textures/misc/particles.png`) с frame profile `ticksExisted % 13`;
  - additive blend billboard и `0.5F` billboard scale baseline.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` обновлен:
  - registration contract `EntityPrimalOrb -> RenderPrimalOrb::new`;
  - renderer contract checks на primal spike/billboard/color/frame/scale surface.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это renderer baseline без ручной визуальной проверки; exact legacy GL/lightmap nuances остаются в общем Stage 8-d visual parity matrix.

### Checkpoint 2026-05-17 — replace pech blast snowball fallback with dedicated no-op renderer baseline

Статус: частично продвинут.

Что сделано:

- `EntityPechBlast` переведен с `RenderSnowball` на dedicated `RenderPechBlast`.
- Добавлен `RenderPechBlast` с reference-shaped contract: intentional no-op draw path (как в original `RenderPechBlast`, где render body пустой).
- `ClientProxyEntityRendererRegistrationStaticGuardTest` обновлен:
  - registration contract `EntityPechBlast -> RenderPechBlast::new`;
  - renderer contract checks на no-op semantics.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это no-op renderer baseline без ручной визуальной проверки; точные runtime визуальные ожидания для Pech blast остаются в общем Stage 8-d visual parity matrix.

### Checkpoint 2026-05-17 — replace alumentum snowball fallback with dedicated no-op renderer baseline

Статус: частично продвинут.

Что сделано:

- `EntityAlumentum` переведен с `RenderSnowball` на dedicated `RenderAlumentum`.
- Добавлен `RenderAlumentum` с reference-shaped contract: intentional no-op draw path (как в original `RenderAlumentum`, где render body пустой).
- `ClientProxyEntityRendererRegistrationStaticGuardTest` обновлен:
  - registration contract `EntityAlumentum -> RenderAlumentum::new`;
  - renderer contract checks на no-op semantics.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это no-op renderer baseline без ручной визуальной проверки; точные runtime визуальные ожидания для Alumentum остаются в общем Stage 8-d visual parity matrix.

### Checkpoint 2026-05-17 — replace dart snowball fallback with dedicated arrow renderer baseline

Статус: частично продвинут.

Что сделано:

- `EntityDart` переведен с `RenderSnowball` на dedicated `RenderDart`.
- Добавлен `RenderDart` с reference-shaped baseline contract:
  - dedicated arrow texture path (`textures/entity/arrow.png`);
  - explicit `RenderArrow<EntityDart>` renderer type вместо snowball fallback.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` обновлен:
  - registration contract `EntityDart -> RenderDart::new`;
  - renderer contract checks на `RenderArrow` + arrow texture binding.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это renderer baseline без ручной визуальной проверки; точные runtime анимационные/GL нюансы legacy `RenderDart` остаются в общем Stage 8-d visual parity matrix.

### Checkpoint 2026-05-17 — replace primal arrow snowball fallback with dedicated typed arrow renderer baseline

Статус: частично продвинут.

Что сделано:

- `EntityPrimalArrow` переведен с `RenderSnowball` на dedicated `RenderPrimalArrow`.
- Добавлен `RenderPrimalArrow` с reference-shaped baseline contracts:
  - dedicated arrow texture path (`textures/entity/arrow.png`);
  - explicit `RenderArrow<EntityPrimalArrow>` renderer type;
  - базовый type-driven tint hook через `BlockCustomOreItem.colors` и `entity.getArrowType()` с обязательным reset GL color.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` обновлен:
  - registration contract `EntityPrimalArrow -> RenderPrimalArrow::new`;
  - renderer contract checks на typed arrow, texture, type-driven tint surface и color reset.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это renderer baseline без ручной визуальной проверки; точные legacy multi-pass/overlay нюансы `RenderPrimalArrow` остаются в общем Stage 8-d visual parity matrix.

### Checkpoint 2026-05-17 — replace golem bobber snowball fallback with dedicated tethered renderer baseline

Статус: частично продвинут.

Что сделано:

- `EntityGolemBobber` переведен с `RenderSnowball` на dedicated `RenderGolemBobber`.
- Добавлен `RenderGolemBobber` с reference-shaped baseline contracts:
  - particle-atlas bobber quad (`textures/particle/particles.png`) с camera-facing sprite transform;
  - отдельный fisher tether line pass при `entity.fisher != null`;
  - reference-driven tether math hooks (`fisher.rightArm / 3.0F`, body yaw interpolation, quadratic segment sag) и explicit GL texture/lighting toggles для линии.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` обновлен:
  - registration contract `EntityGolemBobber -> RenderGolemBobber::new`;
  - renderer contract checks на bobber atlas texture + tether render surface.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это renderer baseline без ручной визуальной проверки; точные legacy визуальные нюансы линии/анимации bobber остаются в общем Stage 8-d visual parity matrix.

### Checkpoint 2026-05-17 — replace falling taint snowball fallback with dedicated block-model renderer baseline

Статус: частично продвинут.

Что сделано:

- `EntityFallingTaint` переведен с `RenderSnowball` на dedicated `RenderFallingTaint`.
- Добавлен `RenderFallingTaint` с reference-shaped baseline contracts:
  - block atlas binding (`TextureMap.LOCATION_BLOCKS_TEXTURE`);
  - metadata-driven imitated state (`block.getStateFromMeta(entity.metadata)`);
  - world-state divergence gate (`world.getBlockState(blockPos)`), как в original render gate;
  - model-only block render path через `BlockRendererDispatcher` + `renderModel(...)` с translation в local render space.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` обновлен:
  - registration contract `EntityFallingTaint -> RenderFallingTaint::new`;
  - renderer contract checks на atlas/state/renderModel/lighting baseline.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это renderer baseline без ручной визуальной проверки; точные legacy визуальные нюансы block-shading/face culling остаются в общем Stage 8-d visual parity matrix.

### Checkpoint 2026-05-17 — replace frost shard snowball fallback with dedicated shard renderer baseline

Статус: частично продвинут.

Что сделано:

- `EntityFrostShard` переведен с `RenderSnowball` на dedicated `RenderFrostShard`.
- Добавлен `RenderFrostShard` с reference-shaped baseline contracts:
  - dedicated frost shard texture (`textures/blocks/frostshard.png`);
  - entity-id-seeded random scaling surface (`new Random(entity.getEntityId())`) и damage-driven size baseline (`entity.getDamage() * 0.1F`);
  - explicit blend/cull state setup и non-noop cross-quad shard draw path.
- `ClientProxyEntityRendererRegistrationStaticGuardTest` обновлен:
  - registration contract `EntityFrostShard -> RenderFrostShard::new`;
  - renderer contract checks на texture/random-scaling/blend/cross-quad surface.

Проверки:

- `./scripts/dev.sh validate --smoke` — passed.

Ограничения:

- Это renderer baseline без ручной визуальной проверки; точные legacy OBJ-модель/mesh нюансы `RenderFrostShard` остаются в общем Stage 8-d visual parity matrix.

- [ ] Add client-only entity renderer registration hook.
- [ ] Register every entity from `ConfigEntities.ENTITIES` with a custom or vanilla-equivalent renderer.
- [ ] Port item-like/transient/projectile renderers.
- [ ] Port mob, boss, golem, trunk, taint, cultist, eldritch, wisp/firebat renderers.
- [ ] Port all entity model classes needed by registered renderers.
- [ ] Copy all required entity renderer textures and OBJ/model resources from `thaumcraft_src/assets/thaumcraft/`.
- [ ] Replace all 1.7-only rendering APIs with Forge/Minecraft 1.12.2 equivalents.
- [ ] Verify no client renderer classes are referenced from common/server-only initialization.
- [ ] Run compile/build/check-jar validation after implementation.
- [ ] Run server smoke to check side separation.
- [ ] Run client smoke and manual spawn/render scenarios for every renderer group.
- [ ] Update this document with closed gaps and validation evidence after implementation.

