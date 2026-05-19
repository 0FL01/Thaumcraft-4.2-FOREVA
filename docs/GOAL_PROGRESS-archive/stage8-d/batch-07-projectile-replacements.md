# Stage 8-d — Projectile renderer replacements — aspect orb, electric/explosive/eldritch/primal orb, ember, pech blast, alumentum, dart, primal arrow, golem bobber, falling taint, frost shard

Split from `stage8-d-checkpoints.md`. Batch covers checkpoints 70–82 of 82.

---

## 6. Итоговый checklist закрытия Stage 8-d


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

