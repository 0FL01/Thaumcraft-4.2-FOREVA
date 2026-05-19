# Stage 8-d — Entity common behavior restoration — taint chicken/cow/pig/sheep/villager AI/attributes/drops, swarm/spore lifecycle, taintacle small/giant/core

Split from `stage8-d-checkpoints.md`. Batch covers checkpoints 48–58 of 82.

---

## 6. Итоговый checklist закрытия Stage 8-d


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

