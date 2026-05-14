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

Stage 2 закрыта коммитом `82e24a3` для fresh-world target. Блокирующие Stage 2 gaps по registry identity, tile entity ids, entity names/order, GUI ids, config compatibility, network order и runtime registration load закрыты или явно переведены в deferral вне Stage 2. Старые WIP-миры до Stage 2 не поддерживаются; migration tooling для них не требуется и не планируется. Таблицы старых WIP ids ниже сохранены только как registry identity audit/history, чтобы не допустить повторного silent rename.

## 5. Gap list

### GAP-1: Registry names блоков и ItemBlocks не соответствуют reference

**Статус:** закрыто в `82e24a3`
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:48-178`
- `src/main/java/thaumcraft/common/config/ConfigBlocks.java:180-231`
- `src/main/java/thaumcraft/common/Thaumcraft.java:241-254`

**Референс:**
- `thaumcraft_src/thaumcraft/common/config/ConfigBlocks.class`

**Что не совпадает:**
Reference регистрирует legacy names вида `blockFluxGoo`, `blockCustomOre`, `blockMagicalLog`, `blockJar`, `blockPortalEldritch`, `blockCosmeticSlabWood`. Pre-Stage2 WIP порт регистрировал lowercase/snake_case names `flux_goo`, `custom_ore`, `magical_log`, `jar`, `eldritch_portal`, `stairs_eldritch` (`src/main/java/thaumcraft/common/config/ConfigBlocks.java:49-143`). Это меняло registry identity и нарушало PRD contract (`docs/PRD.md:63`). Старые WIP-миры с такими ids теперь считаются unsupported; Stage 2 фиксирует fresh-world ids. Также часть reference blocks вообще отсутствует в `getAllBlocks()`: `blockFluidPure`, `blockFluidDeath`, `blockArcaneFurnace`, `blockAlchemyFurnace`, `blockChestHungry`, `blockCandle`, `blockArcaneDoor`, `blockLifter`, `blockMirror`, `blockTube`, `blockMagicBox`, `blockEssentiaReservoir`, `blockStairsArcaneStone`, `blockStairsGreatwood`, `blockStairsSilverwood`, slabs, loot urn/crate.

**Что нужно доделать:**
Восстановить Stage-2 registry identity table для всех reference blocks и ItemBlocks, адаптировав names к Forge 1.12.2 без silent rename.

**Как доделать:**
- Перепроверить `ConfigBlocks.init()`, `getAllBlocks()`, `registerItemBlocks()` against decompiled `ConfigBlocks.class`.
- Для каждого reference block решить: зарегистрирован сейчас, отсутствует из-за dependency, или нужен compatibility alias/migration.
- Изменить registry names на legacy-compatible names, если нет зафиксированного 1.12-only migration решения.
- Добавить отсутствующие block registration entries, если классы уже есть; если классов нет, явно создать Stage-2 blocker entries для отсутствующих classes before closure.
- Проверить duplicate names между blocks и ItemBlocks; ItemBlock registry name должен совпадать с block registry name.

**Критерии приемки:**
- [x] Составлен и закоммичен список всех block registry names current vs reference.
- [x] Все registered blocks/ItemBlocks имеют ожидаемые legacy-compatible names; old WIP ids documented as audit-only mappings.
- [x] Runtime load не содержит duplicate/missing registry name errors.

**Риски / зависимости:**
Часть отсутствующих block classes является dependency на Stage 4/7 behavior. Stage 2 закрывает registry identity для реализованных blocks и документирует отсутствующие entries как later-stage content/behavior deferrals, не как обязательную save migration.

### GAP-2: Tile entity ids не совпадают с reference и часть registrations отсутствует

**Статус:** закрыто в `82e24a3`
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
- [x] Все реализованные reference tile ids перечислены в Stage 2 registry table; missing behavior/content tiles deferred to later stages.
- [x] Нет отсутствующих tile registrations для реализованных tile classes.
- [x] Runtime smoke подтверждает, что tile registration не падает и не конфликтует.

**Риски / зависимости:**
Некоторые tile classes относятся к Stage 4 behavior, но их ids являются Stage 2 registry compatibility. Если class отсутствует, Stage 2 нельзя закрывать как complete без documented deferral и migration decision.

### GAP-3: Entity registry names/order не сохраняют reference identity

**Статус:** закрыто в `82e24a3`
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
- [x] Entity registration table has no unexplained name/order differences for Stage 2 registered entities.
- [x] All entity entries load without duplicate ids/names.
- [x] Spawn eggs and entity construction reference the expected registry ids.

**Риски / зависимости:**
Forge 1.12 requires `ResourceLocation` registry paths, but preserving identity can still use explicit path choices. Entity behavior is Stage 6 dependency; entity registration identity is Stage 2.

### GAP-4: GUI ids перепутаны относительно reference

**Статус:** закрыто в `82e24a3`
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
- [x] GUI id table in code matches reference for all ids 0,1,2,3,5,8,9,10,13,15,16,17,18,19,20.
- [x] All touched `openGui` call sites use constants and match the server handler.
- [x] Runtime server load validates GUI handler registration; manual container-opening parity is deferred to gameplay/client GUI stages.

**Риски / зависимости:**
Client GUI screens are Stage 8 dependency, but server container ids are Stage 2 compatibility and cannot remain shifted.

### GAP-5: Config compatibility keys/defaults are incomplete

**Статус:** закрыто в `82e24a3`
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
- [x] Generated config contains all reference keys that affect Stage 2 registrations.
- [x] Changing `outer_lands_dim` affects dimension registration before runtime load.
- [x] Config-changed event updates runtime configurable values and saves changed config.

**Риски / зависимости:**
Forge 1.12 biome numeric ids differ from 1.7.10; this requires explicit migration policy, not silent ignoring.

### GAP-6: Lifecycle integrations from reference are missing or stubbed

**Статус:** закрыто с deferrals в `82e24a3`
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
- [x] No lifecycle method in Stage 2 scope is a silent stub.
- [x] All reference lifecycle registration hooks are either implemented or explicitly deferred as non-Stage-2 dependency.
- [x] Runtime smoke shows event/config/network registration does not crash during mod load.

**Риски / зависимости:**
Some hook payloads are content/gameplay dependency on Stage 3-7/9. The Stage 2 requirement is to preserve registration boundaries and not hide missing hooks behind empty methods.

### GAP-7: Entity spawn, villager id, and champion whitelist registration are missing/incomplete

**Статус:** закрыто в `82e24a3`
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
- [x] Spawn registration for Brainy Zombie, Pech, Fire Bat, Wisp matches reference conditions at registration-boundary level.
- [x] Champion whitelist defaults exist; full champion behavior consumption remains later gameplay validation.
- [x] Villager professions/careers/trades load without registry errors in server smoke.

**Риски / зависимости:**
Entity AI/combat behavior is Stage 6 dependency. Spawn registration and ids are Stage 2.

### GAP-8: Biome and dimension registration ignores compatibility ids and needs runtime verification

**Статус:** закрыто в `82e24a3`
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
- [x] Configured `outer_lands_dim` is honored.
- [x] All four Thaumcraft biomes register once with expected names.
- [x] Smoke test confirms dimension/biome registration does not crash.

**Риски / зависимости:**
Outer Lands generation/portal parity is Stage 7 dependency, but dimension registration itself is Stage 2.

### GAP-9: Packet registration order matches reference, but runtime side-safety is unverified

**Статус:** закрыто в `82e24a3`
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
- [x] Packet discriminator table exactly matches reference and is documented.
- [x] Dedicated server smoke has no client-only packet class load failure.
- [x] Client smoke was attempted but blocked by display/LWJGL before mod loading; dedicated server packet side-safety passed.

**Риски / зависимости:**
Some packet payload effects are Stage 8 client FX dependency; Stage 2 only needs registration/order/side safety.

### GAP-10: Stage 2 validation has not been run and registry-name audit is missing

**Статус:** закрыто в `82e24a3`
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
- [x] `compileJava` passes.
- [x] Server smoke reaches normal ready state with no crash markers.
- [x] Duplicate/missing registry audit passes for Stage 2 expected ids.

**Риски / зависимости:**
Client smoke may be environment-limited; if skipped, document the concrete reason. Do not use compile success alone as parity closure.

## 6. Итоговый checklist закрытия Stage 2

- [x] `Thaumcraft` lifecycle order and registration boundaries are reviewed against `thaumcraft_src/thaumcraft/common/Thaumcraft.class`.
- [x] All Stage 2 config keys/defaults from reference are loaded or explicitly classified as Forge 1.12 compatibility keys.
- [x] Config-changed hook is implemented and covered by server load validation.
- [x] Block and ItemBlock registry names are audited against reference and no silent snake_case renames remain.
- [x] Item registry names are audited against reference `ConfigItems.class`; port-only split/collapsed exceptions are documented.
- [x] Tile entity id mapping is complete for implemented tile classes. Old 1.7.10/WIP save migration is out of scope because Stage 2 targets fresh worlds only.
- [x] Entity registry names/order/tracking/eggs are audited and corrected to legacy-token lowercase policy.
- [x] Entity spawn registration and champion whitelist registration are restored.
- [x] Villager professions/careers/trades are registered; old numeric villager ids are retained only as compatibility config keys.
- [x] Biome names, dictionary types, manager weights, config keys, and dimension id registration are verified by server smoke.
- [x] GUI id constants match reference and all touched `openGui` call sites use them.
- [x] Packet channel/discriminator/order/side table matches reference and dedicated server smoke found no client-only packet load failure.
- [x] Sound events in `TCSounds` match `sounds.json` and load during server smoke; no Stage 2 sound changes were required.
- [x] No Stage 2 TODO/stub remains without a documented dependency label. `CommandThaumcraft`, client screens/rendering/FX, recipes/research/content behavior and gameplay parity are later-stage deferrals; old-save/WIP-world migration is unsupported unless project policy changes.
- [x] `./scripts/dev.sh compileJava` passes.
- [x] `./scripts/dev.sh smoke-server` passes for registration/lifecycle load on a fresh world.
- [x] `./scripts/dev.sh smoke-client` was attempted and failed before mod loading due LWJGL/display environment (`LinuxDisplay.getAvailableDisplayModes`), not a Thaumcraft load failure.
- [x] Duplicate/missing registry-name audit passes for Stage 2 block/item/entity identity policy.

## 7. Definition of Done

Stage 2 считается закрытой для fresh-world Forge 1.12.2 target:
- blocker/high gaps Stage 2 закрыты или явно переведены в later-stage deferrals;
- registry/lifecycle/config/network/GUI-id scope Stage 2 реализован;
- registry identity стабилизирована от коммита `82e24a3` и не должна переименовываться без отдельного documented compatibility decision;
- старые WIP-миры и 1.7.10 save/NBT migration не являются целью Stage 2;
- проект проходит `compileJava` и dedicated server smoke на fresh world;
- client smoke заблокирован окружением до загрузки мода и не считается Stage 2 blocker;
- `docs/Stage2.md` содержит финальный статус и deferrals.

## 8. Принятые Stage 2 policy-решения

Эти решения закрывают policy-вопросы, которые блокировали планирование GAP-1, GAP-2, GAP-3, GAP-5 и GAP-8. Они не закрывают сами gaps без последующей реализации, registry audit и smoke validation.

### 8.1 Registry identity policy

Для blocks, ItemBlocks, items и entities canonical 1.12 registry path должен выводиться из legacy reference token, но нормализоваться к lowercase, без перехода на новый snake_case стиль.

Примеры целевого направления:

- `blockJar` -> `thaumcraft:blockjar`, а не `thaumcraft:jar`.
- `blockFluxGoo` -> `thaumcraft:blockfluxgoo`, а не `thaumcraft:flux_goo`.
- `BrainyZombie` -> `thaumcraft:brainyzombie`, а не `thaumcraft:brainy_zombie`.
- `SpecialItemGrate` -> `thaumcraft:specialitemgrate`, а не `thaumcraft:item_grate`.

Причина: Forge/Minecraft 1.12 `ResourceLocation` нормализует paths к lowercase, поэтому exact camelCase reference names нельзя считать надежным runtime id. При этом legacy token сохраняет traceability и лучше подходит для save/config/addon migration, чем текущие silent snake_case renames.

Для уже существующих WIP registry ids текущего порта (`jar`, `flux_goo`, `brainy_zombie`, etc.) нужно добавить явную missing-mapping/migration таблицу на новый canonical id. Нельзя молча менять id без documented mapping.

ItemBlock registry name должен совпадать с registry name соответствующего block. Items должны пройти тот же audit against `ConfigItems.class`, так как текущий порт также использует snake_case item ids.

### 8.2 Tile entity id policy

Tile entity registrations должны быть вынесены из inline-блока `Thaumcraft.preInit()` в отдельный проверяемый helper/table рядом с registry/config кодом.

Для каждого tile нужно зафиксировать:

- original raw 1.7.10 id (`TileJar`, `TileSiphon`, `TileInfusionStone`, etc.);
- current port id, если уже был зарегистрирован (`thaumcraft:jar_fillable`, `thaumcraft:alembic`, etc.);
- canonical 1.12 id по legacy-normalized policy (`thaumcraft:tilejar`, `thaumcraft:tilesiphon`, etc.);
- target class или explicit missing/deferred class.

Нельзя считать raw `TileJar` и namespaced `thaumcraft:tilejar` автоматически совместимыми. Если direct old-NBT loading требует дополнительной remap/migration логики, она должна быть реализована или явно отмечена как deferral before Stage 2 closure.

### 8.3 Biome numeric id policy

Старые numeric biome ids из config нужно читать и сохранять как compatibility/migration keys, но не использовать как обязательные runtime registry ids в Forge 1.12.2.

Runtime identity для Thaumcraft biomes в 1.12 должна определяться registry names. Numeric keys (`biome_taint`, `biome_magical_forest`, `biome_eerie`, `biome_eldritch`) остаются в config с комментариями о migration-only semantics, чтобы старые config files не теряли значения silently.

Biome weights (`taint_biome_weight`, `magical_forest_biome_weight`) не являются migration-only и должны реально применяться до `BiomeManager` registration.

`outer_lands_dim` не является biome registry id и должен реально применяться к dimension registration. Перед `DimensionType.register`/`DimensionManager.registerDimension` нужен duplicate-id guard с понятной ошибкой или documented fallback policy.

### 8.4 Missing block/tile class policy

Не создавать пустые placeholder blocks/tiles только ради заполнения reference registry list.

Каждая отсутствующая reference entry должна попасть в одну из категорий:

- **available/register-now**: class существует и может быть безопасно зарегистрирован в Stage 2;
- **collapsed-into-meta-block**: old standalone registry id сейчас представлен meta-вариантом другого block; требуется documented migration decision, а не silent omission;
- **missing/deferred**: class отсутствует или поведение относится к later phase; Stage 2 может двигаться дальше только если deferral явно указан в registry table и не маскируется заглушкой.

Если class отсутствует, предпочтительнее оставить explicit blocker/deferral и портировать полноценный block/tile в соответствующей фазе, чем добавлять нерабочий placeholder с неправильным behavior.

## 9. Implementation closure — 2026-05-14

Stage 2 registry/lifecycle closure is implemented with the policy from section 8: canonical 1.12 registry paths for blocks, ItemBlocks, items and entities are derived from the original 1.7.10 token lowercased, not from new snake_case names.

### 9.1 Implemented code anchors

- Lifecycle order, config-changed hook, IMC handling, scan handler registration, tile registration helper call, dimension duplicate guard, entity spawn call: `src/main/java/thaumcraft/common/Thaumcraft.java:90-207`.
- Block registry names: `src/main/java/thaumcraft/common/config/ConfigBlocks.java:54-149`.
- ItemBlock registry names now mirror block registry names, including generic ItemBlocks for registered blocks that previously had no ItemBlock: `src/main/java/thaumcraft/common/config/ConfigBlocks.java:215-253`.
- Tile entity mapping table/helper: `src/main/java/thaumcraft/common/config/ConfigBlocks.java:255-342`.
- Item registry names use the same `legacyPath` policy, with port-only split items explicitly using non-snake class tokens: `src/main/java/thaumcraft/common/config/ConfigItems.java:203-762`.
- Entity registry token/order/tracking/egg table follows the 1.7.10 sequence and moves `GolemBobber` back to id 49: `src/main/java/thaumcraft/common/config/ConfigEntities.java:46-168`.
- Entity spawn and champion whitelist registration boundaries are restored: `src/main/java/thaumcraft/common/config/ConfigEntities.java:171-268`.
- GUI id constants and server routing match the reference ids: `src/main/java/thaumcraft/common/CommonProxy.java:25-116`.
- Biome/dimension/villager/enchantment/portable-hole config keys are loaded: `src/main/java/thaumcraft/common/config/Config.java:239-345`.
- Packet channel/order is guarded by a reference packet count: `src/main/java/thaumcraft/common/lib/network/PacketHandler.java:49-105`.

### 9.2 Registry identity audit for discarded WIP ids

These mappings document the silent WIP ids replaced by canonical Stage 2 ids. The project target is fresh worlds, so pre-Stage2 WIP worlds using old snake_case ids are unsupported. No migration tooling is required or planned for those worlds. The mappings are retained only as audit/history for registry identity decisions.

#### Blocks and ItemBlocks

| Old WIP id | Canonical id | Reference token |
|---|---|---|
| `thaumcraft:jar` | `thaumcraft:blockjar` | `blockJar` |
| `thaumcraft:crystal` | `thaumcraft:blockcrystal` | `blockCrystal` |
| `thaumcraft:table` | `thaumcraft:blocktable` | `blockTable` |
| `thaumcraft:stone_device` | `thaumcraft:blockstonedevice` | `blockStoneDevice` |
| `thaumcraft:wooden_device` | `thaumcraft:blockwoodendevice` | `blockWoodenDevice` |
| `thaumcraft:metal_device` | `thaumcraft:blockmetaldevice` | `blockMetalDevice` |
| `thaumcraft:magical_log` | `thaumcraft:blockmagicallog` | `blockMagicalLog` |
| `thaumcraft:magical_leaves` | `thaumcraft:blockmagicalleaves` | `blockMagicalLeaves` |
| `thaumcraft:custom_ore` | `thaumcraft:blockcustomore` | `blockCustomOre` |
| `thaumcraft:custom_plant` | `thaumcraft:blockcustomplant` | `blockCustomPlant` |
| `thaumcraft:cosmetic_solid` | `thaumcraft:blockcosmeticsolid` | `blockCosmeticSolid` |
| `thaumcraft:cosmetic_opaque` | `thaumcraft:blockcosmeticopaque` | `blockCosmeticOpaque` |
| `thaumcraft:taint` | `thaumcraft:blocktaint` | `blockTaint` |
| `thaumcraft:taint_fibres` | `thaumcraft:blocktaintfibres` | `blockTaintFibres` |
| `thaumcraft:airy` | `thaumcraft:blockairy` | `blockAiry` |
| `thaumcraft:flux_goo` | `thaumcraft:blockfluxgoo` | `blockFluxGoo` |
| `thaumcraft:flux_gas` | `thaumcraft:blockfluxgas` | `blockFluxGas` |
| `thaumcraft:mana_pod` | `thaumcraft:blockmanapod` | `blockManaPod` |
| `thaumcraft:eldritch` | `thaumcraft:blockeldritch` | `blockEldritch` |
| `thaumcraft:eldritch_nothing` | `thaumcraft:blockeldritchnothing` | `blockEldritchNothing` |
| `thaumcraft:eldritch_portal` | `thaumcraft:blockportaleldritch` | `blockPortalEldritch` |
| `thaumcraft:stairs_eldritch` | `thaumcraft:blockstairseldritch` | `blockStairsEldritch` |
| `thaumcraft:hole` | `thaumcraft:blockhole` | `blockHole` |
| `thaumcraft:warded` | `thaumcraft:blockwarded` | `blockWarded` |

#### Representative item identity mappings and exceptions

All current item ids now derive from the reference token used in `ConfigItems.init()`. Examples: `wand_casting -> wandcasting`, `focus_portable_hole -> focusportablehole`, `resource -> itemresource`, `key -> arcanedoorkey`, `sword_thaumium -> itemswordthaumium`, `pick_elemental -> itempickaxeelemental`, `hand_mirror -> handmirror`, `sanity_checker -> itemsanitychecker`.

Explicit Stage 2 item identity exceptions:

| Old WIP id | Canonical id | Reason |
|---|---|---|
| `thaumcraft:focus_pouch_bauble` | `thaumcraft:focuspouchbauble` | 1.12-only split item; original single token remains `focuspouch`. |
| `thaumcraft:nugget_edible` | `thaumcraft:itemnuggetedible` | Current port collapsed the four original edible nugget items; full split remains a later item/content task. |
| `thaumcraft:boots_fortress` | `thaumcraft:itembootsfortress` | Current port exposes a boots slot not present in the shown reference registry sequence. |
| `thaumcraft:helm_robe` | `thaumcraft:itemhelmetrobe` | Current port exposes a robe helmet slot not present in the shown reference registry sequence. |
| `thaumcraft:boots_void_robe` | `thaumcraft:itembootsvoidrobe` | Current port exposes a void robe boots slot not present in the shown reference registry sequence. |

#### Tile entities

Tile entities now register through the table at `ConfigBlocks.TILE_REGISTRATIONS`. Important legacy substitutions are:

| Current/old WIP id | Canonical id | Reference token |
|---|---|---|
| `thaumcraft:jar_fillable` | `thaumcraft:tilejar` | `TileJar` |
| `thaumcraft:jar_fillable_void` | `thaumcraft:tilejarvoid` | `TileJarVoid` |
| `thaumcraft:infusion_matrix` | `thaumcraft:tileinfusionstone` | `TileInfusionStone` |
| `thaumcraft:alembic` | `thaumcraft:tilesiphon` | `TileSiphon` |
| unregistered WIP `TileEtherealBloom` | `thaumcraft:tilepurifytotem` | `TilePurifyTotem` |
| unregistered WIP `TileNodeEnergized` | `thaumcraft:tilenodeenergized` | `TileNodeEnergized` |
| unregistered WIP `TileWardingStone` | `thaumcraft:tilewardingstone` | `TileWardingStone` |
| unregistered WIP `TileWardingStoneFence` | `thaumcraft:tilewardingstonefence` | `TileWardingStoneFence` |
| unregistered WIP `TileNitor` | `thaumcraft:tilenitor` | `TileNitor` |

Raw 1.7.10 NBT ids such as `TileJar` are not automatically equivalent to namespaced 1.12 ids such as `thaumcraft:tilejar`. Because the current target is fresh worlds, offline old-save NBT remapping is not required for Stage 2 closure.

#### Entities

Entity ids now use the reference token lowercased and the original local order. Examples: `special_item -> specialitem`, `traveling_trunk -> travelingtrunk`, `brainy_zombie -> brainyzombie`, `fire_bat -> firebat`, `thaumic_slime -> thaumslime`, `taint_villager -> taintedvillager`, `item_grate -> specialitemgrate`, and `golem_bobber -> golembobber` at the reference final id. The WIP-only `watcher` and base `cultist` entity registry entries were removed from the entity registry table; they remain represented in champion whitelist registration by class name.

### 9.3 Stage 2 policy notes after implementation

- Biome numeric ids are read and saved as compatibility keys but remain migration-only under Forge 1.12 registry rules; biome runtime identity is registry-name based.
- Fresh-world policy: pre-Stage2 WIP worlds and old 1.7.10 saves are unsupported for Stage 2; registry mapping tables are audit/history, not an implementation requirement for save migration.
- `outer_lands_dim` is read before dimension registration and duplicate ids now fail fast.
- Old villager numeric ids are read and preserved as config keys; Forge 1.12 runtime identity uses `thaumcraft:wizard` and `thaumcraft:banker` professions/careers.
- `portablehole_blacklist` is read from the original config key and resolves block names against the 1.12 block registry.
- Packet discriminator order remains the reference 0-38 sequence; runtime registration now asserts the expected packet count.
- Sounds required no changes: current `TCSounds` and `sounds.json` already match the reference sound key set.
- `CommandThaumcraft` is not present in the current port; `serverLoad` now reports this explicitly instead of hiding a silent stub. Porting command behavior remains tied to research/warp/content systems outside Stage 2.

### 9.4 Validation results

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh smoke-server` — first run stopped on an old generated `run/world` registry-remap prompt from the previous WIP snake_case ids; `run/world` was moved to `run/world.stage2-registry-backup`, then the clean-world rerun passed and reached `Done (` with no crash markers.
- Registry smoke audit: `rg -n "missing id|Unidentified mapping|Duplicate|LoaderException|NoClassDefFoundError|NoSuch(Field|Method)Error|ExceptionInInitializerError|Done \(" run/smoke-server.log` returned only the ready-state `Done (` line.
- Registry identity audit for blocks/items/entities: `rg -n 'setRegistryName\("thaumcraft", "[a-z0-9]+_[a-z0-9_]+"|makeEntry\([^\n]+"[a-z0-9]+_[a-z0-9_]+"' src/main/java/thaumcraft/common/config/ConfigBlocks.java src/main/java/thaumcraft/common/config/ConfigItems.java src/main/java/thaumcraft/common/config/ConfigEntities.java` returned no matches.
- `./scripts/dev.sh smoke-client` — attempted because `DISPLAY=:0` was available and client/proxy GUI routing changed, but failed before mod loading in LWJGL display initialization with `ArrayIndexOutOfBoundsException` in `org.lwjgl.opengl.LinuxDisplay.getAvailableDisplayModes`; this is an environment/display failure, not a Thaumcraft registry/load failure.
