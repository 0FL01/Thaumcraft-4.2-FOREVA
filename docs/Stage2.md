# Stage 2 — Gap-анализ и план закрытия

## 1. Цель фазы

Stage 2 должна закрыть технический слой запуска мода на Forge 1.12.2: lifecycle hooks, proxy-boundaries, Forge registries, config compatibility, network channel/packet discriminators, GUI ids, dimension/biome/villager registration, sound registration и smoke-проверки загрузки. По PRD это отдельная цель: lifecycle и registries должны быть подключены через Forge 1.12.2 API (`docs/PRD.md:222-236`), при этом registry identity, packet ordering, GUI ids и config keys являются compatibility-sensitive контрактами (`docs/PRD.md:57-67`, `docs/PRD.md:234-236`).

Stage 2 не закрывает поведение блоков, предметов, мобов, worldgen, research, recipes или client rendering, кроме их регистрации и lifecycle-связей. Зависимости на более поздние стадии отмечены как dependency, если они прямо блокируют проверку регистрации.

## 2. Scope фазы

В scope Stage 2 входят:

- `@Mod`, mod id, metadata, dependency string, `@SidedProxy`, lifecycle handlers `preInit`, `init`, `postInit`, `serverLoad`, config-changed hook.
- Forge 1.12.2 registry events для blocks/items/entities/potions/enchantments/biomes/villager professions/sounds.
- Registration boundaries: `Thaumcraft`, `CommonProxy`, `ClientProxy`, `Config`, `ConfigBlocks`, `ConfigItems`, `ConfigEntities`, `TCSounds`, `PacketHandler`.
- Registry identity для блоков, ItemBlocks, items, entities, potions, enchantments, biomes, dimensions, sounds, tile entities.
- Config loading: сохранение старых key names/default meanings, sync on config change, villager/biome/dimension/enchantment compatibility keys.
- Networking: channel name, packet classes, discriminators, order, side handlers.
- GUI ids: server/client handler ids и все `openGui` call sites.
- Lifecycle integrations: event buses, world generator registration, biome manager/dictionary registration, villager professions/careers/creation handlers, dimension registration, commands, dispenser/fuel/scan/IMC hooks where they are registration/lifecycle-only.
- Validation: `compileJava`, client smoke when possible, duplicate/missing registry-name check, and runtime registry load check.

## 3. Источники сравнения

- PRD Stage 2 goal/validation/risk: `docs/PRD.md:222-236`.
- Public contracts: `docs/PRD.md:57-67`.
- Required methodology: `docs/PRD.md:160-171`.
- Current lifecycle root: `src/main/java/thaumcraft/common/Thaumcraft.java:57-368`.
- Current proxies and GUI handler: `src/main/java/thaumcraft/common/CommonProxy.java:23-144`, `src/main/java/thaumcraft/client/ClientProxy.java:14-69`.
- Current config/registries: `src/main/java/thaumcraft/common/config/Config.java:27-400`, `src/main/java/thaumcraft/common/config/ConfigBlocks.java:7-232`, `src/main/java/thaumcraft/common/config/ConfigItems.java:45-813`, `src/main/java/thaumcraft/common/config/ConfigEntities.java:17-153`.
- Current network registration: `src/main/java/thaumcraft/common/lib/network/PacketHandler.java:48-102`.
- Current sounds: `src/main/java/thaumcraft/common/lib/TCSounds.java:13-99`, `src/main/resources/assets/thaumcraft/sounds.json:1-68`.
- Current metadata/build: `src/main/resources/mcmod.info:1-11`, `build.gradle:20-33`, `build.gradle:42-55`.
- Reference lifecycle: `thaumcraft_src/thaumcraft/common/Thaumcraft.class` decompiled with CFR.
- Reference proxy/GUI ids: `thaumcraft_src/thaumcraft/common/CommonProxy.class` decompiled with CFR.
- Reference config/blocks/items/entities/network: `thaumcraft_src/thaumcraft/common/config/Config.class`, `thaumcraft_src/thaumcraft/common/config/ConfigBlocks.class`, `thaumcraft_src/thaumcraft/common/config/ConfigItems.class`, `thaumcraft_src/thaumcraft/common/config/ConfigEntities.class`, `thaumcraft_src/thaumcraft/common/lib/network/PacketHandler.class` decompiled with CFR.
- Reference resources: `thaumcraft_src/mcmod.info`, `thaumcraft_src/assets/thaumcraft/sounds.json`, `thaumcraft_src/assets/thaumcraft/sounds/**`.
- Lightweight commands run for this analysis: `git status --short`, `javap -classpath thaumcraft_src -p -constants ...`, `cfr thaumcraft_src/... --silent true`, `rg -n ... src/main/java/thaumcraft/common src/main/java/thaumcraft/client src/main/resources/mcmod.info`.

## 4. Текущее состояние Stage 2

Текущий порт имеет базовый Forge 1.12.2 lifecycle: `@Mod` с lowercase `thaumcraft`, dependency на Forge/Baubles и proxy registration (`src/main/java/thaumcraft/common/Thaumcraft.java:57-83`), `preInit/init/postInit/serverLoad` (`src/main/java/thaumcraft/common/Thaumcraft.java:89-237`), registry event handlers для blocks/items/entities/potions/enchantments/biomes/villagers (`src/main/java/thaumcraft/common/Thaumcraft.java:241-305`), `SimpleNetworkWrapper` с тем же packet order, что у 1.7.10 reference (`src/main/java/thaumcraft/common/lib/network/PacketHandler.java:55-96`), и sound event subscriber (`src/main/java/thaumcraft/common/lib/TCSounds.java:13-96`).

Однако Stage 2 нельзя считать завершенной. Открыты blocker/high gaps по compatibility-sensitive registry identity, tile entity ids, entity names/order, GUI ids и config compatibility. Дополнительно есть lifecycle registrations, которые в reference выполнялись как часть загрузки мода, но сейчас отсутствуют или перенесены в заглушки: server command (`src/main/java/thaumcraft/common/Thaumcraft.java:234-237`), config-changed hook отсутствует, entity spawn/champion registration отсутствует, `Config.initLoot()` и `Config.initMisc()` являются заглушками (`src/main/java/thaumcraft/common/config/Config.java:300-306`, `src/main/java/thaumcraft/common/config/Config.java:397-399`). Client GUI side также явно отложен (`src/main/java/thaumcraft/client/ClientProxy.java:46-50`); это dependency на Stage 8 для экранов, но ids должны быть исправлены в Stage 2.

## 5. Gap list

### GAP-1: Registry names блоков и ItemBlocks не соответствуют reference

**Статус:** реализовано неправильно  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:48-178`
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:180-231`
- `src/main/java/thaumcraft/common/Thaumcraft.java:241-254`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigBlocks.class`

**Что не совпадает:**
Reference регистрирует legacy names вида `blockFluxGoo`, `blockCustomOre`, `blockMagicalLog`, `blockJar`, `blockPortalEldritch`, `blockCosmeticSlabWood`. Текущий порт регистрирует lowercase/snake_case names `flux_goo`, `custom_ore`, `magical_log`, `jar`, `eldritch_portal`, `stairs_eldritch` (`src/main/java/thaumcraft/common/config/ConfigBlocks.java:49-143`). Это меняет registry identity, ломает сохранения/миграцию и нарушает PRD contract (`docs/PRD.md:63`). Также часть reference blocks вообще отсутствует в `getAllBlocks()`: `blockFluidPure`, `blockFluidDeath`, `blockArcaneFurnace`, `blockAlchemyFurnace`, `blockChestHungry`, `blockCandle`, `blockArcaneDoor`, `blockLifter`, `blockMirror`, `blockTube`, `blockMagicBox`, `blockEssentiaReservoir`, `blockStairsArcaneStone`, `blockStairsGreatwood`, `blockStairsSilverwood`, slabs, loot urn/crate.

**Что нужно доделать:**
Восстановить Stage-2 registry identity table для всех reference blocks и ItemBlocks, адаптировав names к Forge 1.12.2 без silent rename.

**Как доделать:**
- Перепроверить `ConfigBlocks.init()`, `getAllBlocks()`, `registerItemBlocks()` against decompiled `ConfigBlocks.class`.
- Для каждого reference block решить: зарегистрирован сейчас, отсутствует из-за dependency, или нужен compatibility alias/migration.
- Изменить registry names на legacy-compatible names, если нет зафиксированного 1.12-only migration решения.
- Добавить отсутствующие block registration entries, если классы уже есть; если классов нет, явно создать Stage-2 blocker entries для отсутствующих classes before closure.
- Проверить duplicate names между blocks и ItemBlocks; ItemBlock registry name должен совпадать с block registry name.

**Критерии приемки:**
- [ ] Составлен и закоммичен список всех block registry names current vs reference.
- [ ] Все registered blocks/ItemBlocks имеют ожидаемые legacy-compatible names или документированную migration mapping.
- [ ] Runtime load не содержит duplicate/missing registry name errors.

**Риски / зависимости:**
Часть отсутствующих block classes является dependency на Stage 4/7 behavior, но Stage 2 всё равно должна определить их registry identity и не закрываться без плана регистрации/миграции.

### GAP-2: Tile entity ids не совпадают с reference и часть registrations отсутствует

**Статус:** реализовано неправильно  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/Thaumcraft.java:137-195`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigBlocks.class`

**Что не совпадает:**
Reference использует string ids вида `TileHole`, `TileArcaneFurnace`, `TileSiphon`, `TileJar`, `TileInfusionStone`, `TilePurifyTotem`, `TilePortalNothing`. Текущий порт регистрирует ResourceLocation ids вида `thaumcraft:hole`, `thaumcraft:arcane_furnace`, `thaumcraft:alembic`, `thaumcraft:jar_fillable`, `thaumcraft:infusion_matrix`, `thaumcraft:eldritch_nothing` (`src/main/java/thaumcraft/common/Thaumcraft.java:138-195`). Это compatibility-sensitive для сохранений. Также отсутствуют reference tile registrations для `TileChestHungry`, `TileArcaneLampFertility`, `TileNitor`, `TileMirror` variants may be partial, `TileAlchemyFurnaceAdvanced`, `TileAlchemyFurnaceAdvancedNozzle`, `TileEtherealBloom`, `TileTube*`, `TileBrainbox`, `TileWardingStone`, `TileWardingStoneFence`, `TileNodeEnergized`, `TileEssentiaCrystalizer`.

**Что нужно доделать:**
Сделать таблицу tile id mapping и зарегистрировать все tile entity ids, которые должны грузить старые NBT names или имеют 1.12 replacement.

**Как доделать:**
- Вынести tile registration в dedicated helper рядом с `ConfigBlocks`, чтобы Stage 2 registry list был проверяемым.
- Для каждого reference id указать current class или отсутствующий class.
- Использовать Forge 1.12.2 `GameRegistry.registerTileEntity(Class, ResourceLocation)` с compatibility id strategy, не меняя id silently.
- Проверить загрузку мира с NBT tile ids reference-style, если есть тестовый save; иначе добавить manual verification item.

**Критерии приемки:**
- [ ] Все reference tile ids перечислены в Stage 2 registry table.
- [ ] Нет отсутствующих tile registrations для реализованных tile classes.
- [ ] Runtime smoke подтверждает, что tile registration не падает и не конфликтует.

**Риски / зависимости:**
Некоторые tile classes относятся к Stage 4 behavior, но их ids являются Stage 2 registry compatibility. Если class отсутствует, Stage 2 нельзя закрывать как complete без documented deferral и migration decision.

### GAP-3: Entity registry names/order не сохраняют reference identity

**Статус:** реализовано неправильно  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigEntities.java:27-129`
- `src/main/java/thaumcraft/common/Thaumcraft.java:256-260`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigEntities.class`

**Что не совпадает:**
Reference registers mod entities with names such as `SpecialItem`, `PermanentItem`, `FollowItem`, `AspectOrb`, `FallingTaint`, `BrainyZombie`, `Firebat`, `ThaumSlime`, `SpecialItemGrate`, and sequential local ids. Current 1.12 registration uses lowercase/snake_case names such as `special_item`, `permanent_item`, `follow_item`, `aspect_orb`, `falling_taint`, `brainy_zombie`, `fire_bat`, `thaumic_slime`, `item_grate` (`src/main/java/thaumcraft/common/config/ConfigEntities.java:56-129`). Current order also places `EntityGolemBobber` at id 19 before monsters (`src/main/java/thaumcraft/common/config/ConfigEntities.java:76-80`), while reference registers it at the end after `SpecialItemGrate`. Current list adds `EntityWatcher` and `EntityCultist` in positions not present in the shown reference sequence (`src/main/java/thaumcraft/common/config/ConfigEntities.java:97-102`).

**Что нужно доделать:**
Define and implement 1.12-safe entity registry identity that preserves reference names and local discriminator ordering where feasible.

**Как доделать:**
- Build current-vs-reference table: class, reference name, reference order, current registry name, current order, tracking range, update frequency, velocity flag, egg colors.
- Rename 1.12 `ResourceLocation` paths only with an explicit migration decision; otherwise use names equivalent to reference lowercased only if required by Forge and documented.
- Move `EntityGolemBobber` order to match reference or document why 1.12 registry id ordering is not save/network sensitive.
- Verify `EntityEntryBuilder.name()` and registry path produce expected spawn egg/entity ids.

**Критерии приемки:**
- [ ] Entity registration table has no unexplained name/order differences.
- [ ] All entity entries load without duplicate ids/names.
- [ ] Spawn eggs and entity construction reference the expected registry ids.

**Риски / зависимости:**
Forge 1.12 requires `ResourceLocation` registry paths, but preserving identity can still use explicit path choices. Entity behavior is Stage 6 dependency; entity registration identity is Stage 2.

### GAP-4: GUI ids перепутаны относительно reference

**Статус:** реализовано неправильно  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/CommonProxy.java:44-101`
- `src/main/java/thaumcraft/client/ClientProxy.java:46-50`
- `src/main/java/thaumcraft/common/Thaumcraft.java:204-208`

**Референс:**
- `thaumcraft_src/thaumcraft/common/CommonProxy.class`

**Что не совпадает:**
Reference server GUI ids are: `0` golem, `1` pech, `2` traveling trunk, `3` thaumatorium, `5` focus pouch, `8` deconstruction table, `9` alchemy furnace, `10` research table, `13` arcane workbench, `15` arcane bore, `16` hand mirror, `17` hover harness, `18` magic box, `19` spa, `20` focal manipulator. Current server handler maps `0` arcane workbench, `1` research table, `2` arcane bore, `3` alchemy furnace, `4` deconstruction table, `6` golem, `7` pech, `8` traveling trunk, `9` thaumatorium, `10` hand mirror, `11` hover harness, `12` magic box, `13` spa, `14` focal manipulator (`src/main/java/thaumcraft/common/CommonProxy.java:48-99`). This breaks every persisted or cross-code GUI id assumption and conflicts with PRD compatibility rules (`docs/PRD.md:63`). Client GUI handler returns null for all ids (`src/main/java/thaumcraft/client/ClientProxy.java:46-50`); screen implementation is Stage 8 dependency, but id routing must be correct in Stage 2.

**Что нужно доделать:**
Restore reference GUI id table and update every `openGui` call site to use the same constants.

**Как доделать:**
- Create a single GUI id constants location or use existing convention; avoid magic number drift.
- Update `CommonProxy.getServerGuiElement()` ids to match reference.
- Update `openGui` callers, e.g. `EntityPech` currently opens id `7` (`src/main/java/thaumcraft/common/entities/monster/EntityPech.java:282`), but reference Pech id is `1`.
- Leave client screens as Stage 8 dependency if needed, but keep `ClientProxy.getClientGuiElement()` id switch aligned with reference and returning null only where screen class is absent.

**Критерии приемки:**
- [ ] GUI id table in code matches reference for all ids 0,1,2,3,5,8,9,10,13,15,16,17,18,19,20.
- [ ] All `openGui` call sites use constants and match the server handler.
- [ ] Manual/runtime check opens at least server containers for registered ids without wrong container routing.

**Риски / зависимости:**
Client GUI screens are Stage 8 dependency, but server container ids are Stage 2 compatibility and cannot remain shifted.

### GAP-5: Config compatibility keys/defaults are incomplete

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/Config.java:181-298`
- `src/main/java/thaumcraft/common/config/ConfigEntities.java:19-25`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/Config.class`

**Что не совпадает:**
Current config reads many general/worldgen/research/spawn/runic keys (`src/main/java/thaumcraft/common/config/Config.java:230-297`), but does not load reference biome/dimension keys from `Biomes`: `taint_biome_weight`, `biome_taint`, `magical_forest_biome_weight`, `biome_magical_forest`, `biome_eerie`, `biome_eldritch`, `outer_lands_dim`. Current biome ids remain static defaults (`src/main/java/thaumcraft/common/config/Config.java:41-58`) and are not applied to registry. Current code also omits `general.thaumcraft_villager_id`, `general.thaumcraft_banker_id`, `general.portablehole_blacklist`, and reference enchantment config ids `ench_haste`, `ench_repair`. Current `onConfigChanged` equivalent is absent, while reference has config-changed sync/save hook in `Thaumcraft.class`.

**Что нужно доделать:**
Restore all Stage 2 config keys that influence lifecycle/registries/ids and ensure defaults preserve reference meaning.

**Как доделать:**
- Add reads for biome/dimension/villager/enchantment/portable-hole blacklist keys in `Config.init()` or a dedicated compatibility loader.
- Wire config-changed event for mod id and call `Config.syncConfigurable()` plus save when changed.
- Confirm 1.12 handling of biome ids: if numeric ids cannot be hard-bound safely, document migration and still preserve keys/defaults.
- Ensure config fields are applied before related registrations: biomes before registry event, dimension before `DimensionType.register`, villager professions before registration.

**Критерии приемки:**
- [ ] Generated config contains all reference keys that affect Stage 2 registrations.
- [ ] Changing `outer_lands_dim` affects dimension registration before runtime load.
- [ ] Config-changed event updates runtime configurable values and saves changed config.

**Риски / зависимости:**
Forge 1.12 biome numeric ids differ from 1.7.10; this requires explicit migration policy, not silent ignoring.

### GAP-6: Lifecycle integrations from reference are missing or stubbed

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/Thaumcraft.java:89-237`
- `src/main/java/thaumcraft/common/config/Config.java:300-306`
- `src/main/java/thaumcraft/common/config/Config.java:397-399`

**Референс:**
- `thaumcraft_src/thaumcraft/common/Thaumcraft.class`
- `thaumcraft_src/thaumcraft/common/config/Config.class`

**Что не совпадает:**
Reference lifecycle registers `EventHandlerNetwork` on FML bus, `worldEventHandler` as fuel handler, `ScanManager` as scan event handler, village structure components in preInit, config-changed hook, server command, dispenser behavior for alumentum, `ConfigEntities.initEntitySpawns()`, `ConfigItems.postInit()`, IMC runtime message handling, `Config.initLoot()`, `Config.initMisc()`. Current lifecycle registers several event handlers and world generator (`src/main/java/thaumcraft/common/Thaumcraft.java:113-130`), but has no `EventHandlerNetwork`, no `registerFuelHandler`, no `ThaumcraftApi.registerScanEventhandler(new ScanManager())`, no config-changed hook, no IMC fetch handling, no dispenser behavior, and `serverLoad` is a comment-only stub (`src/main/java/thaumcraft/common/Thaumcraft.java:234-237`). `Config.initLoot()` and `Config.initMisc()` are comment-only stubs (`src/main/java/thaumcraft/common/config/Config.java:300-306`, `src/main/java/thaumcraft/common/config/Config.java:397-399`).

**Что нужно доделать:**
Separate Stage 2 registration/lifecycle hooks from later gameplay content and restore all hooks that make registries/events/config/network function.

**Как доделать:**
- Implement or explicitly port `EventHandlerNetwork` equivalent if packet/config sync depends on FML events.
- Register `ScanManager` through `ThaumcraftApi.registerScanEventhandler` or document replacement.
- Register server command if command class is available; if absent, mark missing class and add it.
- Restore config-changed event handling.
- Restore dispenser/fuel/IMC hooks where they are pure registration; defer detailed behavior only with dependency labels.
- Move loot/misc content registration to the proper phase if out of Stage 2, but replace stubs with explicit deferral documentation so Stage 2 is not claimed complete because methods exist.

**Критерии приемки:**
- [ ] No lifecycle method in Stage 2 scope is a silent stub.
- [ ] All reference lifecycle registration hooks are either implemented or explicitly deferred as non-Stage-2 dependency.
- [ ] Runtime smoke shows event/config/network registration does not crash during mod load.

**Риски / зависимости:**
Some hook payloads are content/gameplay dependency on Stage 3-7/9. The Stage 2 requirement is to preserve registration boundaries and not hide missing hooks behind empty methods.

### GAP-7: Entity spawn, villager id, and champion whitelist registration are missing/incomplete

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigEntities.java:131-151`
- `src/main/java/thaumcraft/common/Thaumcraft.java:221-223`
- `src/main/java/thaumcraft/common/config/Config.java:253-261`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigEntities.class`
- `thaumcraft_src/thaumcraft/common/Thaumcraft.class`

**Что не совпадает:**
Current `ConfigEntities` registers villager professions/careers and village creation handlers (`src/main/java/thaumcraft/common/config/ConfigEntities.java:131-151`, `src/main/java/thaumcraft/common/Thaumcraft.java:221-223`), but there is no `initEntitySpawns()` equivalent, no `EntityRegistry.addSpawn` for Brainy Zombie/Pech/Fire Bat/Wisp, no Halloween firebat spawn branch, no champion whitelist map/IMC messages, and config villager ids `entWizardId`/`entBankerId` are static defaults not loaded from config (`src/main/java/thaumcraft/common/config/ConfigEntities.java:19-25`). Reference also registered villager numeric ids and trade handlers directly; 1.12 professions/careers replace that API, but id compatibility/defaults still need a documented migration.

**Что нужно доделать:**
Port 1.7.10 spawn registration semantics to Forge 1.12.2 and document villager id migration.

**Как доделать:**
- Add 1.12 `EntityRegistry.addSpawn` calls or `Biome.SpawnListEntry` registration matching reference conditions.
- Restore champion whitelist data structure and default entries if champion mechanics still consult it.
- Load villager id config keys or document why 1.12 professions supersede numeric ids.
- Validate `spawn_*` config booleans actually gate spawn registration.

**Критерии приемки:**
- [ ] Spawn registration for Brainy Zombie, Pech, Fire Bat, Wisp matches reference conditions.
- [ ] Champion whitelist defaults exist and are consumed by champion logic.
- [ ] Villager professions/careers/trades load without missing texture or registry errors.

**Риски / зависимости:**
Entity AI/combat behavior is Stage 6 dependency. Spawn registration and ids are Stage 2.

### GAP-8: Biome and dimension registration ignores compatibility ids and needs runtime verification

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/Thaumcraft.java:128-130`
- `src/main/java/thaumcraft/common/Thaumcraft.java:197-201`
- `src/main/java/thaumcraft/common/Thaumcraft.java:290-299`
- `src/main/java/thaumcraft/common/config/Config.java:308-395`
- `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:48-63`

**Референс:**
- `thaumcraft_src/thaumcraft/common/Thaumcraft.class`
- `thaumcraft_src/thaumcraft/common/config/Config.class`

**Что не совпадает:**
Current biomes are instantiated in `ThaumcraftWorldGenerator.initBiomes()` and registered through `RegistryEvent.Register<Biome>` (`src/main/java/thaumcraft/common/Thaumcraft.java:128-130`, `src/main/java/thaumcraft/common/Thaumcraft.java:290-299`), but reference config-created biomes using config ids and collision handling before registration. Current static config ids (`src/main/java/thaumcraft/common/config/Config.java:41-58`) are not loaded/applied. Current dimension registers a `DimensionType` directly with `Config.dimensionOuterId` (`src/main/java/thaumcraft/common/Thaumcraft.java:197-201`), while reference used provider type and dimension id after reading `outer_lands_dim`. No runtime smoke evidence confirms dimension id/type uniqueness.

**Что нужно доделать:**
Make biome/dimension registration explicitly compatible with 1.12 registry rules while preserving config keys/defaults.

**Как доделать:**
- Load biome/dimension config keys before biome/dimension registration.
- Verify registry names: current biome classes set `biome_magical_forest`, `biome_taint`, `biome_eerie`, `biome_eldritch`; compare to intended 1.12 names and document mapping.
- Guard `DimensionType.register`/`DimensionManager.registerDimension` against duplicate ids and fail with clear log.
- Run client or server smoke to confirm no duplicate biome/dimension registry failures.

**Критерии приемки:**
- [ ] Configured `outer_lands_dim` is honored or migration is documented.
- [ ] All four Thaumcraft biomes register once with expected names.
- [ ] Smoke test confirms dimension/biome registration does not crash.

**Риски / зависимости:**
Outer Lands generation/portal parity is Stage 7 dependency, but dimension registration itself is Stage 2.

### GAP-9: Packet registration order matches reference, but runtime side-safety is unverified

**Статус:** требует проверки  
**Критичность:** medium

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/network/PacketHandler.java:48-102`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/network/PacketHandler.class`

**Что не совпадает:**
Static comparison shows packet channel name is equivalent (`thaumcraft`) and current discriminator order/sides match reference from `PacketBiomeChange` id `0` through `PacketFXBeamPulseGolemBoss` id `38` (`src/main/java/thaumcraft/common/lib/network/PacketHandler.java:55-96`). The implementation uses a shared lambda dispatcher (`src/main/java/thaumcraft/common/lib/network/PacketHandler.java:51-53`) instead of each packet class as its own 1.7.10 `IMessageHandler`, which is a valid 1.12 adaptation if every packet extends `PacketBase` and side handlers schedule safely. This was not runtime-smoke-tested here.

**Что нужно доделать:**
Verify all registered packet classes serialize/deserialize and execute on the expected logical side without client-only class loading on a dedicated server.

**Как доделать:**
- Add or run a packet registration smoke test during client/server load.
- Inspect each registered packet for `Side.CLIENT`/`Side.SERVER` class references and thread scheduling.
- Keep packet discriminator table in code/docs so future packets cannot reorder silently.

**Критерии приемки:**
- [ ] Packet discriminator table exactly matches reference and is documented.
- [ ] Dedicated server smoke has no client-only packet class load failure.
- [ ] Client smoke reaches main menu/load phase without packet registration errors.

**Риски / зависимости:**
Some packet payload effects are Stage 8 client FX dependency; Stage 2 only needs registration/order/side safety.

### GAP-10: Stage 2 validation has not been run and registry-name audit is missing

**Статус:** требует проверки  
**Критичность:** high

**Текущая реализация:**
- `docs/PRD.md:228-232`
- `AGENTS.md:138-180`

**Референс:**
- `docs/PRD.md:228-232`

**Что не совпадает:**
PRD requires `compileJava`, `runClient` smoke when possible, and duplicate/missing registry-name verification (`docs/PRD.md:228-232`). No Stage 2 document existed before this analysis, and this analysis only ran lightweight static commands. Because Stage 2 touches mod loading, registries, config, networking, GUI registration, dimensions, and sounds, AGENTS requires runtime smoke validation before claiming completion (`AGENTS.md:158-180`).

**Что нужно доделать:**
Add repeatable Stage 2 validation checklist and run it after closing blocker/high gaps.

**Как доделать:**
- Run `./scripts/dev.sh compileJava`.
- Run `./scripts/dev.sh smoke-server` for common/server registration safety.
- Run `./scripts/dev.sh smoke-client` if display/X11 is available.
- Add a registry audit command/test that lists block/item/entity/potion/enchantment/biome/sound/tile ids and detects duplicates/missing expected names.
- Run `./scripts/dev.sh check-jar` after producing a jar intended for Forge runtime.

**Критерии приемки:**
- [ ] `compileJava` passes.
- [ ] Server smoke reaches normal ready state with no crash markers.
- [ ] Duplicate/missing registry audit passes for Stage 2 expected ids.

**Риски / зависимости:**
Client smoke may be environment-limited; if skipped, document the concrete reason. Do not use compile success alone as parity closure.

## 6. Итоговый checklist закрытия Stage 2

- [ ] `Thaumcraft` lifecycle order and registration boundaries are reviewed against `thaumcraft_src/thaumcraft/common/Thaumcraft.class`.
- [ ] All Stage 2 config keys/defaults from reference are loaded or explicitly migrated.
- [ ] Config-changed hook is implemented and tested.
- [ ] Block and ItemBlock registry names are audited against reference and no silent renames remain.
- [ ] Item registry names are audited against reference `ConfigItems.class`; missing Stage 2 registration entries are documented or fixed.
- [ ] Tile entity id mapping is complete and save-compatible.
- [ ] Entity registry names/order/tracking/eggs are audited and corrected or explicitly migrated.
- [ ] Entity spawn registration and champion whitelist registration are restored.
- [ ] Villager professions/careers/trades and old villager id config compatibility are resolved.
- [ ] Biome names, dictionary types, manager weights, config keys, and dimension id registration are verified.
- [ ] GUI id constants match reference and all `openGui` call sites use them.
- [ ] Packet channel/discriminator/order/side table matches reference and is runtime-smoke-tested.
- [ ] Sound events in `TCSounds` match `sounds.json` and load during client/server smoke.
- [ ] No Stage 2 TODO/stub remains without a documented dependency label.
- [ ] `./scripts/dev.sh compileJava` passes.
- [ ] `./scripts/dev.sh smoke-server` passes for registration/lifecycle load.
- [ ] `./scripts/dev.sh smoke-client` passes or is skipped with a concrete environment reason.
- [ ] Duplicate/missing registry-name audit passes.

## 7. Definition of Done

Stage 2 считается ПОЛНОСТЬЮ завершенной только если:
- все blocker gaps закрыты;
- все high gaps закрыты;
- все элементы из scope Stage 2 реализованы;
- текущая реализация соответствует референсу по поведению;
- все нужные файлы, ресурсы и регистрации присутствуют;
- отсутствуют TODO и заглушки внутри scope Stage 2;
- проект собирается без ошибок;
- базовые игровые сценарии Stage 2 проверены вручную или тестами;
- ./docs/Stage2.md обновлен и не содержит критичных открытых вопросов.

## 8. Открытые вопросы

- Нужно принять явную policy для 1.12 registry names: сохранять reference-style names (`blockJar`, `BrainyZombie`, `TileJar`) как registry paths where possible или вводить documented migration aliases. Без этой policy нельзя закрыть GAP-1, GAP-2 и GAP-3.
- Нужно решить, как 1.12 biome registry должен трактовать старые numeric biome ids из config: honoring ids where possible, migration-only keys, or documented non-support.
- Нужно подтвердить, какие отсутствующие reference block/tile classes должны быть registered placeholders in Stage 2, а какие являются strictly later-phase dependency. До этого Stage 2 нельзя считать complete.
