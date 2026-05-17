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

Stage 2 закрыта коммитом `82e24a3` для fresh-world target. Блокирующие Stage 2 gaps по registry identity, tile entity ids, entity names/order, GUI ids, config compatibility, network order и runtime registration load закрыты или явно переведены в deferral вне Stage 2. Pre-Stage2 WIP worlds не поддерживаются; conversion tooling для них не требуется и не планируется. Таблицы старых WIP ids ниже сохранены только как registry identity audit/history, чтобы не допустить повторного silent rename.

## 5. Gap list

All gaps from the original analysis were closed in commit `82e24a3`. See Section 9 for mapping tables and Section 8 for policy decisions.

| Gap | Status | Notes |
|---|---|---|
| GAP-1 Mod lifecycle | Closed | FML pre-init/init/postInit → Forge 1.12.2 registry events |
| GAP-2 Proxy lifecycle | Closed | @Mod event bus subscriber → ClientProxy/CommonProxy |
| GAP-3 Config lifecycle | Closed | Config read/write → Forge config system |
| GAP-4 Networking lifecycle | Closed | SimpleImpl → Forge packet registration |
| GAP-5 GUI IDs | Closed | IDs preserved, container/screen split per 1.12.2 |
| GAP-6 Biome IDs | Closed | Numeric IDs → ResourceLocation registry |
| GAP-7 Dimension IDs | Closed | DimensionManager → Forge dimension registry |
| GAP-8 Sound events | Closed | Sounds → TCSounds constants + ResourceLocation |
| GAP-9 Entity IDs | Closed | EntityList → Forge entity registry |
| GAP-10 Registry validation | Closed | Static guard tests added |

## 6. Итоговый checklist закрытия Stage 2

- [x] `Thaumcraft` lifecycle order and registration boundaries are reviewed against `thaumcraft_src/thaumcraft/common/Thaumcraft.class`.
- [x] All Stage 2 config keys/defaults from reference are loaded or explicitly classified as Forge 1.12 compatibility keys.
- [x] Config-changed hook is implemented and covered by server load validation.
- [x] Block and ItemBlock registry names are audited against reference and no silent snake_case renames remain.
- [x] Item registry names are audited against reference `ConfigItems.class`; port-only split/collapsed exceptions are documented.
- [x] Tile entity id mapping is complete for implemented tile classes. Old 1.7.10/WIP save loading is out of scope because Stage 2 targets fresh worlds only.
- [x] Entity registry names/order/tracking/eggs are audited and corrected to legacy-token lowercase policy.
- [x] Entity spawn registration and champion whitelist registration are restored.
- [x] Villager professions/careers/trades are registered; old numeric villager ids are retained only as compatibility config keys.
- [x] Biome names, dictionary types, manager weights, config keys, and dimension id registration are verified by server smoke.
- [x] GUI id constants match reference and all touched `openGui` call sites use them.
- [x] Packet channel/discriminator/order/side table matches reference and dedicated server smoke found no client-only packet load failure.
- [x] Sound events in `TCSounds` match `sounds.json` and load during server smoke; no Stage 2 sound changes were required.
- [x] No Stage 2 TODO/stub remains without a documented dependency label. `CommandThaumcraft`, client screens/rendering/FX, recipes/research/content behavior and gameplay parity are later-stage deferrals; external save/WIP-world loading is unsupported unless project policy changes.
- [x] `./scripts/dev.sh compileJava` passes.
- [x] `./scripts/dev.sh smoke-server` passes for registration/lifecycle load on a fresh world.
- [x] `./scripts/dev.sh smoke-client` was attempted and failed before mod loading due LWJGL/display environment (`LinuxDisplay.getAvailableDisplayModes`), not a Thaumcraft load failure.
- [x] Duplicate/missing registry-name audit passes for Stage 2 block/item/entity identity policy.

## 7. Definition of Done

Stage 2 считается закрытой для fresh-world Forge 1.12.2 target:
- blocker/high gaps Stage 2 закрыты или явно переведены в later-stage deferrals;
- registry/lifecycle/config/network/GUI-id scope Stage 2 реализован;
- registry identity стабилизирована от коммита `82e24a3` и не должна переименовываться без отдельного documented compatibility decision;
- pre-Stage2 WIP worlds и 1.7.10 save/NBT loading не являются целью Stage 2;
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

Причина: Forge/Minecraft 1.12 `ResourceLocation` нормализует paths к lowercase, поэтому exact camelCase reference names нельзя считать надежным runtime id. При этом legacy token сохраняет traceability для config/addon audits лучше, чем текущие silent snake_case renames.

Для уже существующих WIP registry ids текущего порта (`jar`, `flux_goo`, `brainy_zombie`, etc.) нужно добавить явную audit table на новый canonical id. Нельзя молча менять id без documented mapping.

ItemBlock registry name должен совпадать с registry name соответствующего block. Items должны пройти тот же audit against `ConfigItems.class`, так как текущий порт также использует snake_case item ids.

### 8.2 Tile entity id policy

Tile entity registrations должны быть вынесены из inline-блока `Thaumcraft.preInit()` в отдельный проверяемый helper/table рядом с registry/config кодом.

Для каждого tile нужно зафиксировать:

- original raw 1.7.10 id (`TileJar`, `TileSiphon`, `TileInfusionStone`, etc.);
- current port id, если уже был зарегистрирован (`thaumcraft:jar_fillable`, `thaumcraft:alembic`, etc.);
- canonical 1.12 id по legacy-normalized policy (`thaumcraft:tilejar`, `thaumcraft:tilesiphon`, etc.);
- target class или explicit missing/deferred class.

Нельзя считать raw `TileJar` и namespaced `thaumcraft:tilejar` автоматически совместимыми. Direct external NBT loading не входит в Stage 2 fresh-world target.

### 8.3 Biome numeric id policy

Старые numeric biome ids из config нужно читать и сохранять как compatibility keys, но не использовать как обязательные runtime registry ids в Forge 1.12.2.

Runtime identity для Thaumcraft biomes в 1.12 должна определяться registry names. Numeric keys (`biome_taint`, `biome_magical_forest`, `biome_eerie`, `biome_eldritch`) остаются в config с комментариями о documented no-op semantics.

Biome weights (`taint_biome_weight`, `magical_forest_biome_weight`) являются active runtime keys и должны реально применяться до `BiomeManager` registration.

`outer_lands_dim` не является biome registry id и должен реально применяться к dimension registration. Перед `DimensionType.register`/`DimensionManager.registerDimension` нужен duplicate-id guard с понятной ошибкой или documented fallback policy.

### 8.4 Missing block/tile class policy

Не создавать пустые placeholder blocks/tiles только ради заполнения reference registry list.

Каждая отсутствующая reference entry должна попасть в одну из категорий:

- **available/register-now**: class существует и может быть безопасно зарегистрирован в Stage 2;
- **collapsed-into-meta-block**: old standalone registry id сейчас представлен meta-вариантом другого block; требуется documented compatibility decision, а не silent omission;
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

These mappings document the silent WIP ids replaced by canonical Stage 2 ids. The project target is fresh worlds, so pre-Stage2 WIP worlds using old snake_case ids are unsupported. No conversion tooling is required or planned for those worlds. The mappings are retained only as audit/history for registry identity decisions.

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

Raw 1.7.10 NBT ids such as `TileJar` are not automatically equivalent to namespaced 1.12 ids such as `thaumcraft:tilejar`. Because the current target is fresh worlds, offline external-save NBT remapping is not required for Stage 2 closure.

#### Entities

Entity ids now use the reference token lowercased and the original local order. Examples: `special_item -> specialitem`, `traveling_trunk -> travelingtrunk`, `brainy_zombie -> brainyzombie`, `fire_bat -> firebat`, `thaumic_slime -> thaumslime`, `taint_villager -> taintedvillager`, `item_grate -> specialitemgrate`, and `golem_bobber -> golembobber` at the reference final id. The WIP-only `watcher` and base `cultist` entity registry entries were removed from the entity registry table; they remain represented in champion whitelist registration by class name.

### 9.3 Stage 2 policy notes after implementation

- Biome numeric ids are read and saved as compatibility keys but remain documented no-op values under Forge 1.12 registry rules; biome runtime identity is registry-name based.
- Fresh-world policy: pre-Stage2 WIP worlds and 1.7.10 saves are unsupported for Stage 2; registry mapping tables are audit/history, not an implementation requirement for external save loading.
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
