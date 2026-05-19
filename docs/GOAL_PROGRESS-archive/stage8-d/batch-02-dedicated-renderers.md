# Stage 8-d — Dedicated entity renderers — pech, firebat, trunk, golem, wisp, eldritch trio, cultist portal, thaumic slime, spore/taintacle trio, zombie, spider, cultist trio

Split from `stage8-d-checkpoints.md`. Batch covers checkpoints 11–25 of 82.

---

## 6. Итоговый checklist закрытия Stage 8-d


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

