# Stage 8-d — Gap-анализ и план закрытия

## 1. Цель фазы

Stage 8-d закрывает только клиентский рендер зарегистрированных сущностей Thaumcraft: регистрацию entity renderers, классы renderers, классы entity models и необходимые ресурсы моделей/текстур. Цель - вернуть визуальное поведение Thaumcraft 4.2.3.5 для мобов, боссов, големов, projectiles, aspect orb, falling taint, item/grate/following/special entities и прочих зарегистрированных сущностей на Forge 1.12.2 без изменения серверной логики и registry identity.

Stage 8-d не включает TESR, GUI, particles/shaders как самостоятельные системы. Если renderer сущности напрямую рисует beam/quad/FX-подобный эффект, это учитывается только как часть renderer behavior.

## 2. Scope фазы

В scope Stage 8-d входят:

- Entity renderer registration из client proxy / клиентского lifecycle.
- Renderer classes для всех сущностей, зарегистрированных в `ConfigEntities.ENTITIES`.
- Entity model classes, используемые renderer classes.
- Entity-specific textures и OBJ/model resources, на которые ссылаются renderer/model classes.
- Smoke/manual scenarios для загрузки клиента и визуальной проверки зарегистрированных сущностей.

Текущий набор зарегистрированных сущностей задается в `src/main/java/thaumcraft/common/config/ConfigEntities.java:72-144` и регистрируется через Forge registry event в `src/main/java/thaumcraft/common/Thaumcraft.java:232-235`. В scope попадают, как минимум, эти группы:

- Base/item-like entities: `EntitySpecialItem`, `EntityPermanentItem`, `EntityFollowingItem`, `EntityAspectOrb`, `EntityFallingTaint`, `EntityItemGrate`.
- Projectiles: `EntityAlumentum`, `EntityPrimalOrb`, `EntityFrostShard`, `EntityDart`, `EntityPrimalArrow`, `EntityPechBlast`, `EntityEldritchOrb`, `EntityBottleTaint`, `EntityGolemOrb`, `EntityShockOrb`, `EntityExplosiveOrb`, `EntityEmber`, `EntityGolemBobber`.
- Golems: `EntityGolemBase`, `EntityTravelingTrunk`.
- Mobs and bosses: brainy/inhabited zombies, wisp, firebat, pech, mind spider, eldritch guardian/warden/golem/crab, cultists/leader/portal, thaumic slime, taint mobs, taintacles.
- Required client resources under `src/main/resources/assets/thaumcraft/textures/**` and model resources such as OBJ files referenced by entity renderers.

Out of scope for this document:

- Stage 8-a/b/c/e analysis.
- Stage 9 recipes/research/content work, except as a dependency if an entity cannot be spawned for manual visual testing.
- TESR, GUI, keybind, particle/shader systems unless directly invoked by an entity renderer.

## 3. Источники сравнения

Current implementation:

- `src/main/java/thaumcraft/client/ClientProxy.java:24-34` - current client display registration only assigns item model resource locations.
- `src/main/java/thaumcraft/client/ClientProxy.java:36-44` - current key/handler registration; no entity renderer registration hook exists.
- `src/main/java/thaumcraft/client/ClientProxy.java:90-105` - FX placeholders, relevant only for renderer-adjacent visual risk.
- `src/main/java/thaumcraft/common/config/ConfigEntities.java:72-144` - current registered entity scope.
- `src/main/java/thaumcraft/common/Thaumcraft.java:165-171` - current client proxy lifecycle calls.
- `src/main/java/thaumcraft/common/Thaumcraft.java:232-235` - current entity registry event.
- `src/main/resources/assets/thaumcraft/textures/models/` - currently contains only `wizard.png` and `moneychanger.png` for model textures.
- `src/main/resources/assets/thaumcraft/textures/misc/` - currently contains only `potions.png`.
- No current files exist under `src/main/java/thaumcraft/client/renderers/**`.

Reference implementation:

- `thaumcraft_src/thaumcraft/client/ClientProxy.class` - original client proxy; `registerDisplayInformation()` calls `setupItemRenderers`, `setupEntityRenderers`, `setupBlockRenderers`, and `setupTileRenderers`.
- `thaumcraft_src/thaumcraft/client/renderers/entity/*.class` - original entity renderer class set.
- `thaumcraft_src/thaumcraft/client/renderers/models/entities/*.class` - original entity model class set.
- `thaumcraft_src/assets/thaumcraft/textures/models/**` - original entity/model textures and model resources.
- `thaumcraft_src/assets/thaumcraft/textures/misc/**` - original wisp/cultist portal resources.
- `thaumcraft_src/assets/thaumcraft/textures/entity/**` - original arrow/creeper overlay resources.
- `Thaumcraft-1.7.10-4.2.3.5.jar` - fallback original artifact if a class must be decompiled from bytecode.

Inspection commands run for this document:

- `git status --short`
- `rg -n "Phase 8|entity renderer|Model classes|renderers" docs/PRD.md`
- `find thaumcraft_src/thaumcraft/client/renderers -type f \( -path '*/entity/*' -o -path '*/models/entities/*' \) | sort`
- `javap -classpath thaumcraft_src -p thaumcraft.client.ClientProxy`
- `javap -classpath thaumcraft_src -p -c thaumcraft.client.ClientProxy`
- `strings thaumcraft_src/thaumcraft/client/renderers/entity/*.class thaumcraft_src/thaumcraft/client/renderers/models/entities/*.class | rg 'textures/(models|items|misc|entity)'`
- `find src/main/java/thaumcraft/client -type f | sort`
- `find src/main/resources/assets/thaumcraft -maxdepth 3 -type d | sort`
- `rg -n 'ENTITIES\.add\(makeEntry' src/main/java/thaumcraft/common/config/ConfigEntities.java`

## 4. Текущее состояние Stage 8-d

Stage 8-d сейчас не закрыт.

В текущем порте уже есть common/entity registrations: `ConfigEntities.init()` наполняет `ENTITIES` в `src/main/java/thaumcraft/common/config/ConfigEntities.java:66-169`, а `Thaumcraft.registerEntities()` регистрирует их в `src/main/java/thaumcraft/common/Thaumcraft.java:232-235`. Однако клиентская часть для entity rendering отсутствует полностью: в `src/main/java/thaumcraft/client/ClientProxy.java:24-34` выполняется только универсальная регистрация item model resource locations, а вызова аналога reference `setupEntityRenderers()` нет.

В текущем source tree отсутствуют все renderer/model packages, необходимые Stage 8-d:

- Отсутствует `src/main/java/thaumcraft/client/renderers/entity/`.
- Отсутствует `src/main/java/thaumcraft/client/renderers/models/entities/`.
- Отсутствуют `src/main/java/thaumcraft/client/renderers/models/ModelFireBat.java`, `ModelCube.java` и прочие shared models, если они будут нужны при портировании reference renderers.

Reference entity renderer set содержит классы `RenderAlumentum`, `RenderAspectOrb`, `RenderBrainyZombie`, `RenderCultist`, `RenderCultistPortal`, `RenderDart`, `RenderEldritchCrab`, `RenderEldritchGolem`, `RenderEldritchGuardian`, `RenderEldritchOrb`, `RenderElectricOrb`, `RenderEmber`, `RenderExplosiveOrb`, `RenderFallingTaint`, `RenderFireBat`, `RenderFollowingItem`, `RenderFrostShard`, `RenderGolemBase`, `RenderGolemBobber`, `RenderInhabitedZombie`, `RenderMindSpider`, `RenderPechBlast`, `RenderPech`, `RenderPrimalArrow`, `RenderPrimalOrb`, `RenderSpecialItem`, `RenderTaintacle`, tainted animal/mob renderers, `RenderThaumicSlime`, `RenderTravelingTrunk`, `RenderWisp`, and several unused-or-conditional classes such as `RenderWatcher` under `thaumcraft_src/thaumcraft/client/renderers/entity/`.

Reference entity model set contains `ModelEldritchCrab`, `ModelEldritchGolem`, `ModelEldritchGuardian`, `ModelFireBat`, `ModelGolemAccessories`, `ModelGolem`, `ModelPech`, `ModelRendererTaintacle`, `ModelTaintacle`, `ModelTaintSheep1`, `ModelTaintSheep2`, `ModelTaintSpore`, `ModelTaintSporeSwarmer`, `ModelTrunk`, and `ModelWatcher` under `thaumcraft_src/thaumcraft/client/renderers/models/entities/`.

Current resources are also incomplete for Stage 8-d. Only `wizard.png`, `moneychanger.png`, and `potions.png` are present in the relevant current texture directories, while the reference renderer classes require many model/entity/misc textures and OBJ resources listed in GAP-5.

## 5. Gap list

### GAP-1: Нет регистрации entity renderers в клиентском lifecycle

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/client/ClientProxy.java:24-34`
- `src/main/java/thaumcraft/client/ClientProxy.java:36-44`
- `src/main/java/thaumcraft/common/Thaumcraft.java:165-171`

**Референс:**
- `thaumcraft_src/thaumcraft/client/ClientProxy.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/*.class`

**Что не совпадает:**

В reference `ClientProxy.registerDisplayInformation()` вызывает private `setupEntityRenderers()` сразу после item renderer setup. Декомпиляция через `javap -c` показывает, что reference `setupEntityRenderers()` регистрирует renderer для `EntityItemGrate`, `EntitySpecialItem`, `EntityFollowingItem`, `EntityPermanentItem`, `EntityAspectOrb`, `EntityGolemBobber`, `EntityGolemBase`, `EntityWisp`, всех projectile classes, mobs, bosses, taint mobs, `EntityTravelingTrunk`, `EntityBottleTaint`, `EntityEldritchCrab`, а также villager skins.

В current `ClientProxy.registerDisplayInformation()` только проходит по `ConfigItems.getAllItems()` и вызывает `ModelLoader.setCustomModelResourceLocation()` для items. Нет вызова `RenderingRegistry.registerEntityRenderingHandler` или 1.12.2 equivalent `RenderingRegistry.registerEntityRenderingHandler(EntityClass, IRenderFactory)`.

**Что нужно доделать:**

Добавить Forge 1.12.2 entity renderer registration в client-only code path. Регистрация должна покрывать все сущности из `ConfigEntities.ENTITIES`, для которых reference имеет renderer или vanilla-compatible renderer.

**Как доделать:**
- Добавить метод наподобие `setupEntityRenderers()` в `src/main/java/thaumcraft/client/ClientProxy.java` или отдельный client-only registrar class, вызываемый из `registerDisplayInformation()` или корректного client init/preinit hook.
- Использовать `net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler` с `IRenderFactory` и `RenderManager`, а не 1.7.10 constructor style.
- Зарегистрировать `EntityItemGrate` через 1.12 item renderer equivalent, `EntityBottleTaint` через snowball/item renderer equivalent, а custom entities через ported custom renderers.
- Проверить, что common/server classes не импортируют client renderer packages.
- Сверить mapping current registered classes из `src/main/java/thaumcraft/common/config/ConfigEntities.java:72-144` с registration table.

**Критерии приемки:**
- [ ] `ClientProxy` или client registrar содержит explicit renderer registration для всех Stage 8-d registered entity classes.
- [ ] Dedicated server smoke не загружает client renderer classes и не падает на `ClassNotFoundException`/`NoClassDefFoundError`.
- [ ] Client smoke доходит до mod loading/main menu без missing renderer crashes.

**Риски / зависимости:**

Forge 1.12.2 renderer registration API отличается от 1.7.10. Нельзя механически копировать constructors из reference; каждый renderer должен быть адаптирован под `RenderManager`. Runtime smoke обязателен, потому что client/server side separation риск высокий по PRD `docs/PRD.md:389-393`.

### GAP-2: Отсутствуют renderer classes для item-like, orb, projectile и transient entities

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/client/renderers/entity/` отсутствует
- `src/main/java/thaumcraft/common/config/ConfigEntities.java:72-91`
- `src/main/java/thaumcraft/common/config/ConfigEntities.java:142-144`

**Референс:**
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderSpecialItem.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderFollowingItem.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderAspectOrb.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderFallingTaint.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderAlumentum.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderPrimalOrb.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderFrostShard.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderDart.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderPrimalArrow.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderPechBlast.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderEldritchOrb.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderElectricOrb.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderExplosiveOrb.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderEmber.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderGolemBobber.class`

**Что не совпадает:**

Current registers the underlying entities in `ConfigEntities`, but has no renderer implementations for their visuals. Reference has specialized renderers for item stacks, following/special permanent items, aspect orb billboard, falling taint block render, projectile quads/models, primal arrow trail-like rendering, electric/golem/shock orb visuals, ember/explosive/eldritch orb visuals, frost shard OBJ rendering, and golem fishing bobber rendering.

Without these classes, client fallback behavior is either no renderer, wrong vanilla renderer, or runtime failure once renderer registration is added. This blocks all projectile/focus visual parity and item-like entity visuals.

**Что нужно доделать:**

Port each reference renderer in this group to Forge 1.12.2 and Minecraft 1.12 rendering APIs.

**Как доделать:**
- Add `src/main/java/thaumcraft/client/renderers/entity/RenderSpecialItem.java` for `EntitySpecialItem` and `EntityPermanentItem`.
- Add `RenderFollowingItem.java` for `EntityFollowingItem`.
- Add `RenderAspectOrb.java` for `EntityAspectOrb`.
- Add `RenderFallingTaint.java` for `EntityFallingTaint`, adapted from 1.7 `RenderBlocks` to 1.12 block rendering APIs.
- Add projectile renderers: `RenderAlumentum`, `RenderPrimalOrb`, `RenderFrostShard`, `RenderDart`, `RenderPrimalArrow`, `RenderPechBlast`, `RenderEldritchOrb`, `RenderElectricOrb`, `RenderExplosiveOrb`, `RenderEmber`, `RenderGolemBobber`.
- Register `EntityGolemOrb` and `EntityShockOrb` with the adapted electric orb renderer, matching reference `setupEntityRenderers()`.
- Register `EntityBottleTaint` with a 1.12 equivalent of `RenderSnowball` using `ConfigItems.itemBottleTaint` from `src/main/java/thaumcraft/common/config/ConfigEntities.java:87` and item definition in `ConfigItems`.
- Verify GL state restoration in every custom renderer, especially alpha/blend/lighting/depth mask changes.

**Критерии приемки:**
- [ ] Every current item-like/projectile/transient entity in `ConfigEntities.java:72-91` and `ConfigEntities.java:142-144` has an explicit renderer registration.
- [ ] Projectiles spawned by wand foci and mobs render with reference-equivalent color, scale, rotation, alpha/blend, and texture behavior.
- [ ] Aspect orb, falling taint, special/permanent/following items, item grate, bottle taint, and golem bobber render without missing texture or GL state corruption.

**Риски / зависимости:**

Several reference renderers are GL immediate-mode style and use 1.7.10 names/APIs. `RenderFrostShard` references `textures/models/orb.obj`; OBJ loading in 1.12 may require Forge model loader adaptation. Projectile spawn paths may depend on common/focus behavior being usable enough for manual testing.

### GAP-3: Отсутствуют renderer classes для мобов, боссов, големов и taint-сущностей

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/client/renderers/entity/` отсутствует
- `src/main/java/thaumcraft/common/config/ConfigEntities.java:93-140`

**Референс:**
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderGolemBase.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderTravelingTrunk.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderBrainyZombie.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderInhabitedZombie.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderWisp.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderFireBat.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderPech.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderMindSpider.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderEldritchGuardian.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderCultist.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderCultistPortal.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderEldritchGolem.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderEldritchCrab.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderThaumicSlime.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderTaintSpider.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderTaintacle.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderTaintSpore.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderTaintSporeSwarmer.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderTaintSwarm.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderTaintChicken.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderTaintCow.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderTaintCreeper.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderTaintPig.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderTaintSheep.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/RenderTaintVillager.class`

**Что не совпадает:**

Current has common mob/entity classes and registrations, but no visual renderers. Reference mob/boss renderers include non-trivial behavior: giant brainy zombie scaling, pech held item/overlay handling, firebat hanging/flying transforms and alternate vampire texture, cultist floating line/beam-style visual, eldritch guardian/warden texture selection and scaling, eldritch golem scaling/headless-related model behavior, eldritch crab overlay pass, taint spider eye pass, taint sheep wool pass, taint creeper armor/effect pass, taintacle length/scale variants, thaumic slime translucent pass, golem carried item/accessory/damage rendering, and traveling trunk angry texture/lid transform.

**Что нужно доделать:**

Port all mob/boss/golem renderers that correspond to current registered entities and preserve reference-specific render passes, scaling, overlays, carried items, held items, death/animation transforms, and texture selection.

**Как доделать:**
- Add renderer classes under `src/main/java/thaumcraft/client/renderers/entity/` with 1.12 `RenderLiving`, `RenderBiped`, `RenderZombie`, `RenderSlime`, or custom `Render` bases as appropriate.
- For `EntityGolemBase`, port carried item rendering, golem type texture selection, damage overlay, accessories/decorations, bucket model handling, and color/upgrades/decorations interpretation from current golem data accessors.
- For `EntityTravelingTrunk`, port normal/angry texture selection and trunk lid animation.
- For `EntityPech`, port pech type texture selection, held item rendering, overlay behavior, and GUI/trading visual assumptions.
- For cultists and bosses, port armor/held item visual handling and special portal/cultist line visuals.
- For taint mobs, port overlay/pass behavior rather than falling back to vanilla animals with changed textures.
- Register every renderer through the Stage 8-d registrar from GAP-1.

**Критерии приемки:**
- [ ] Every mob/boss/golem entity in `ConfigEntities.java:93-140` has explicit renderer registration.
- [ ] Visual variants render correctly: golem type/decorations/damage/carrying, pech type, firebat variants, eldritch guardian vs warden, taint mob overlays, cultist/portal visuals.
- [ ] Spawn eggs or commands can spawn each registered mob/boss without client crash, missing texture, or invisible entity.

**Риски / зависимости:**

This is the largest behavior-risk area in Stage 8-d. Some visuals depend on current entity data manager fields and methods matching original semantics. If common entity state accessors differ from reference, renderer porting may expose common-side parity gaps, but those should be treated as dependencies and not solved by changing renderer identity silently.

### GAP-4: Отсутствуют entity model classes

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/client/renderers/models/entities/` отсутствует
- `src/main/java/thaumcraft/client/renderers/models/` отсутствует

**Референс:**
- `thaumcraft_src/thaumcraft/client/renderers/models/entities/ModelEldritchCrab.class`
- `thaumcraft_src/thaumcraft/client/renderers/models/entities/ModelEldritchGolem.class`
- `thaumcraft_src/thaumcraft/client/renderers/models/entities/ModelEldritchGuardian.class`
- `thaumcraft_src/thaumcraft/client/renderers/models/entities/ModelFireBat.class`
- `thaumcraft_src/thaumcraft/client/renderers/models/entities/ModelGolemAccessories.class`
- `thaumcraft_src/thaumcraft/client/renderers/models/entities/ModelGolem.class`
- `thaumcraft_src/thaumcraft/client/renderers/models/entities/ModelPech.class`
- `thaumcraft_src/thaumcraft/client/renderers/models/entities/ModelRendererTaintacle.class`
- `thaumcraft_src/thaumcraft/client/renderers/models/entities/ModelTaintacle.class`
- `thaumcraft_src/thaumcraft/client/renderers/models/entities/ModelTaintSheep1.class`
- `thaumcraft_src/thaumcraft/client/renderers/models/entities/ModelTaintSheep2.class`
- `thaumcraft_src/thaumcraft/client/renderers/models/entities/ModelTaintSpore.class`
- `thaumcraft_src/thaumcraft/client/renderers/models/entities/ModelTaintSporeSwarmer.class`
- `thaumcraft_src/thaumcraft/client/renderers/models/entities/ModelTrunk.class`

**Что не совпадает:**

Reference renderers instantiate custom model classes for golems, trunk, pech, eldritch mobs, firebat, taintacle, spores, taint sheep overlays, and crab. Current source tree has no corresponding model classes. Vanilla fallback models cannot reproduce reference shapes/animations for these entities.

`ModelWatcher` exists in reference at `thaumcraft_src/thaumcraft/client/renderers/models/entities/ModelWatcher.class`, but `EntityWatcher` is not currently registered in `ConfigEntities.ENTITIES`; this is a dependency note only, not a Stage 8-d blocker for currently registered entities.

**Что нужно доделать:**

Port all model classes used by registered entity renderers and update obfuscated 1.7.10 methods to 1.12.2 method names.

**Как доделать:**
- Add `src/main/java/thaumcraft/client/renderers/models/entities/ModelGolem.java`, `ModelGolemAccessories.java`, `ModelTrunk.java`, `ModelPech.java`, `ModelFireBat.java`, `ModelEldritchGuardian.java`, `ModelEldritchGolem.java`, `ModelEldritchCrab.java`, `ModelTaintacle.java`, `ModelRendererTaintacle.java`, `ModelTaintSpore.java`, `ModelTaintSporeSwarmer.java`, `ModelTaintSheep1.java`, `ModelTaintSheep2.java`.
- Keep original model field names where practical for traceability.
- Translate `func_78088_a`, `func_78087_a`, `func_78086_a` and related obfuscated model methods to 1.12.2 names such as `render`, `setRotationAngles`, and `setLivingAnimations`.
- Verify model rotations against entity state fields such as attack timers, headless state, carried item state, trunk angry state, taintacle length, and sheep head animation.
- Do not port unused `ModelWatcher` unless `EntityWatcher` becomes registered or a renderer needs it.

**Критерии приемки:**
- [ ] Every custom renderer for registered entities compiles without missing model classes.
- [ ] Custom model geometry and animation visibly match reference for golems, pech, trunk, taintacles, eldritch mobs, firebat, spores, crab, and taint sheep.
- [ ] No placeholder model classes or empty `render`/`setRotationAngles` implementations remain in Stage 8-d scope.

**Риски / зависимости:**

Model porting is sensitive to MCP name changes and 1.12 `ModelRenderer` behavior. `ModelRendererTaintacle` used custom display-list behavior in 1.7; it must be adapted carefully or replaced with equivalent 1.12-safe rendering while preserving shape and animation.

### GAP-5: Отсутствуют required entity textures and model resources

**Статус:** отсутствует  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/resources/assets/thaumcraft/textures/models/wizard.png`
- `src/main/resources/assets/thaumcraft/textures/models/moneychanger.png`
- `src/main/resources/assets/thaumcraft/textures/misc/potions.png`

**Референс:**
- `thaumcraft_src/assets/thaumcraft/textures/entity/arrow.png`
- `thaumcraft_src/assets/thaumcraft/textures/entity/creeper/creeper_armor.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/cultist_portal.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/wisp.png`
- `thaumcraft_src/assets/thaumcraft/textures/misc/wispy.png`
- `thaumcraft_src/assets/thaumcraft/textures/models/*.png`
- `thaumcraft_src/assets/thaumcraft/textures/models/*.obj`

**Что не совпадает:**

Reference renderers/models require these assets that are absent from current resources:

- `assets/thaumcraft/textures/entity/arrow.png`
- `assets/thaumcraft/textures/entity/creeper/creeper_armor.png`
- `assets/thaumcraft/textures/misc/cultist_portal.png`
- `assets/thaumcraft/textures/misc/wisp.png`
- `assets/thaumcraft/textures/misc/wispy.png`
- `assets/thaumcraft/textures/models/bucket.obj`
- `assets/thaumcraft/textures/models/bucket.png`
- `assets/thaumcraft/textures/models/bzombie.png`
- `assets/thaumcraft/textures/models/bzombievil.png`
- `assets/thaumcraft/textures/models/chicken.png`
- `assets/thaumcraft/textures/models/cow.png`
- `assets/thaumcraft/textures/models/crab.png`
- `assets/thaumcraft/textures/models/craboverlay.png`
- `assets/thaumcraft/textures/models/creeper.png`
- `assets/thaumcraft/textures/models/cultist.png`
- `assets/thaumcraft/textures/models/czombie.png`
- `assets/thaumcraft/textures/models/eldritch_golem.png`
- `assets/thaumcraft/textures/models/eldritch_guardian.png`
- `assets/thaumcraft/textures/models/eldritch_warden.png`
- `assets/thaumcraft/textures/models/firebat.png`
- `assets/thaumcraft/textures/models/golem_clay.png`
- `assets/thaumcraft/textures/models/golem_damage.png`
- `assets/thaumcraft/textures/models/golem_decoration.png`
- `assets/thaumcraft/textures/models/golem_flesh.png`
- `assets/thaumcraft/textures/models/golem_iron.png`
- `assets/thaumcraft/textures/models/golem_stone.png`
- `assets/thaumcraft/textures/models/golem_straw.png`
- `assets/thaumcraft/textures/models/golem_tallow.png`
- `assets/thaumcraft/textures/models/golem_thaumium.png`
- `assets/thaumcraft/textures/models/golem_wood.png`
- `assets/thaumcraft/textures/models/orb.obj`
- `assets/thaumcraft/textures/models/pech_forage.png`
- `assets/thaumcraft/textures/models/pech_stalker.png`
- `assets/thaumcraft/textures/models/pech_thaum.png`
- `assets/thaumcraft/textures/models/pig.png`
- `assets/thaumcraft/textures/models/sheep.png`
- `assets/thaumcraft/textures/models/sheep_fur.png`
- `assets/thaumcraft/textures/models/taint_spider.png`
- `assets/thaumcraft/textures/models/taint_spider_eyes.png`
- `assets/thaumcraft/textures/models/taint_spore.png`
- `assets/thaumcraft/textures/models/taintacle.png`
- `assets/thaumcraft/textures/models/trunk.png`
- `assets/thaumcraft/textures/models/trunkangry.png`
- `assets/thaumcraft/textures/models/tslime.png`
- `assets/thaumcraft/textures/models/vampirebat.png`
- `assets/thaumcraft/textures/models/villager.png`
- `assets/thaumcraft/textures/models/watcher.png` and `watcher_beam.png` only if `EntityWatcher` is brought into registration scope.

**Что нужно доделать:**

Copy required assets from `thaumcraft_src/assets/thaumcraft/` into `src/main/resources/assets/thaumcraft/` preserving paths, because AGENTS.md identifies `thaumcraft_src/assets/` as the source of truth for ported assets.

**Как доделать:**
- Copy exact reference resources into the same relative paths under `src/main/resources/assets/thaumcraft/`.
- Include `.mcmeta` files where reference textures use animation metadata.
- For OBJ resources such as `bucket.obj` and `orb.obj`, verify 1.12 loader/resource path expectations and update renderer loading code accordingly.
- Run a missing-resource scan after implementing renderers to catch string/path differences introduced during porting.

**Критерии приемки:**
- [ ] All texture/model resource paths referenced by Stage 8-d renderer/model classes exist under `src/main/resources/assets/thaumcraft/`.
- [ ] Client log has no missing texture/model warnings for Stage 8-d entities during smoke scenarios.
- [ ] Copied assets preserve original filenames, relative paths, and metadata from `thaumcraft_src/assets/thaumcraft/`.

**Риски / зависимости:**

Resource paths are public presentation contracts per PRD `docs/PRD.md:140-158`. Renaming paths to fit current code would create avoidable parity risk; renderer code should use original paths unless Forge 1.12 resource loading requires a documented technical adaptation.

### GAP-6: Reference renderer behavior has not been ported or parity-checked

**Статус:** отсутствует  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/client/ClientProxy.java:24-105`
- `src/main/java/thaumcraft/client/renderers/entity/` отсутствует
- `src/main/java/thaumcraft/client/renderers/models/entities/` отсутствует

**Референс:**
- `thaumcraft_src/thaumcraft/client/renderers/entity/*.class`
- `thaumcraft_src/thaumcraft/client/renderers/models/entities/*.class`

**Что не совпадает:**

Даже после добавления class shells, Stage 8-d не будет закрыт без переноса renderer-specific behavior. Reference renderers содержат много поведения, которое нельзя заменить простыми vanilla renderers: texture pass selection, alpha/blend/depth state, billboards, custom quads, held item rendering, golem carried items, overlay passes, entity scaling, model animations, portal/wisp visuals, primal arrow colors, taintacle length, taint sheep wool pass, taint creeper charged overlay, eldritch boss scaling and alternate textures.

Current code не содержит ни одного такого behavior point.

**Что нужно доделать:**

Для каждого renderer/model класса выполнить method-by-method comparison against reference и портировать поведение на 1.12.2 APIs.

**Как доделать:**
- Для каждого renderer из GAP-2/GAP-3 снять reference structure через CFR/javap и вручную перенести в Java source с 1.12 method names.
- Составить registration matrix: entity class -> renderer class -> model class -> textures/resources -> smoke spawn command/scenario.
- Проверить GL state push/pop или `GlStateManager` symmetry для blend, alpha, lighting, cull, depth mask, rescale normal, color reset.
- Для renderers, которые используют item/block rendering, заменить 1.7 `RenderBlocks`, `IIcon`, `Tessellator` usage на 1.12 `RenderItem`, `BlockRendererDispatcher`, `TextureAtlasSprite`, `BufferBuilder`/`Tessellator` equivalents.
- Для boss/mob renderers проверить current entity accessors and data manager fields before adding visual assumptions.

**Критерии приемки:**
- [ ] Каждый renderer имеет documented/static comparison с reference class and methods before being marked done.
- [ ] Нет placeholder render methods, empty render passes, or TODO stubs in `thaumcraft.client.renderers.entity` and `thaumcraft.client.renderers.models.entities`.
- [ ] Visual behavior for variants and special passes is manually verified or covered by targeted smoke checks.

**Риски / зависимости:**

Некоторые reference renderers используют old GL/Tessellator idioms and obfuscated MCP 1.7 method names. Static compile success не доказывает visual parity; нужен client runtime/manual validation. Particle/shader systems may affect perceived visuals for wisp/orb/cultist effects, but standalone Stage 8-d acceptance should focus on renderer-local drawing and not require completing unrelated Stage 8 chunks.

### GAP-7: Нет Stage 8-d smoke/manual validation coverage

**Статус:** требует проверки  
**Критичность:** high

**Текущая реализация:**
- `docs/PRD.md:389-393`
- `docs/PRD.md:483-492`
- `docs/PRD.md:534-535`
- `src/main/java/thaumcraft/common/config/ConfigEntities.java:72-144`

**Референс:**
- `thaumcraft_src/thaumcraft/client/ClientProxy.class`
- `thaumcraft_src/thaumcraft/client/renderers/entity/*.class`
- `thaumcraft_src/thaumcraft/client/renderers/models/entities/*.class`

**Что не совпадает:**

PRD requires runtime smoke for client rendering risk, including that entity renderers do not crash for registered mobs/projectiles. No Stage 8-d-specific validation matrix exists, and no client smoke/manual scenarios have been recorded for entity rendering. Current `ClientProxy` has no entity renderer code to validate yet.

**Что нужно доделать:**

Define and run a minimal but complete Stage 8-d validation matrix after implementation.

**Как доделать:**
- Run `./scripts/dev.sh compileJava` after renderer/model source is added.
- Run `./scripts/dev.sh build` and `./scripts/dev.sh check-jar` before a checkpoint intended for normal Forge/Prism usage.
- Run `./scripts/dev.sh smoke-client` if display/X11 is available.
- Run `./scripts/dev.sh smoke-server` to confirm client-only renderer classes are not loaded on dedicated server.
- In a client dev world, spawn or trigger each entity group: item-like entities, aspect orb, falling taint, every projectile renderer, golem/trunk variants, pech variants, wisp/firebat, eldritch mobs/bosses, cultists/portal, thaumic slime, taint mobs/taintacles.
- Record any scenarios that cannot be triggered due to non-Stage8-d content dependencies as dependencies, not as Stage 8-d completion.

**Критерии приемки:**
- [ ] Client smoke reaches main menu or playable world with no Stage 8-d renderer crash markers.
- [ ] Dedicated server smoke passes with no client classloading failures.
- [ ] Manual/entity spawn matrix covers every renderer registration or documents a concrete dependency for any missing scenario.

**Риски / зависимости:**

Some entities may be hard to trigger naturally until recipes/research/spawn content are complete. This is a Stage 9/content dependency only for manual scenario setup; it does not remove the need to validate renderers via spawn commands, test items, or controlled dev-world setup.

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

## 7. Definition of Done

Stage 8-d считается ПОЛНОСТЬЮ завершенной только если:
- все blocker gaps закрыты;
- все high gaps закрыты;
- все элементы из scope Stage 8-d реализованы;
- текущая реализация соответствует референсу по поведению;
- все нужные файлы, ресурсы и регистрации присутствуют;
- отсутствуют TODO и заглушки внутри scope Stage 8-d;
- проект собирается без ошибок;
- базовые игровые сценарии Stage 8-d проверены вручную или тестами;
- ./docs/Stage8-d.md обновлен и не содержит критичных открытых вопросов.

## 8. Открытые вопросы

- `EntityWatcher` and `RenderWatcher` exist in reference material (`thaumcraft_src/thaumcraft/common/entities/monster/EntityWatcher.class`, `thaumcraft_src/thaumcraft/client/renderers/entity/RenderWatcher.class`, `thaumcraft_src/thaumcraft/client/renderers/models/entities/ModelWatcher.class`) and current source has `src/main/java/thaumcraft/common/entities/monster/EntityWatcher.java`, but `EntityWatcher` is not registered in `src/main/java/thaumcraft/common/config/ConfigEntities.java:72-144`. Stage 8-d should not port/register `RenderWatcher` unless entity registration is added by the relevant common/content scope.
- Static analysis cannot confirm final visual parity for GL state, animation timing, model rotations, held items, overlays, and resource binding. These require client runtime/manual verification after implementation.
