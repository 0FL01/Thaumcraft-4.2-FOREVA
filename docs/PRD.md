# Thaumcraft 4.2.3.5 (1.7.10) → 1.12.2 Port — PRD

## 1. Executive Summary

Port Azanor's Thaumcraft 4.2.3.5 from Minecraft 1.7.10 to 1.12.2 — a full
rewrite of the rendering pipeline, registration system, and Forge API
surface, preserving gameplay mechanics and data model unchanged.

**Scope**: 942 `.class` files, 820 textures, 110+ OGG sounds, 16 OBJ models,
7 GLSL shaders, 22 localizations, 101 animated texture configs.

**Target**: MinecraftForge 1.12.2, JDK 8, Gradle, Baubles (hard dependency).

---

## 2. Source Inventory (After RECON)

### Bytecode (942 `.class` files)

| Layer | Count | Details |
|-------|-------|---------|
| **API** (`thaumcraft.api`) | 67 | 9 sub-packages, 24 interfaces, 6 enums, 1 abstract, ~35 concrete |
| **Bundled CCL** (`codechicken.*`) | 73 | vec(28), render(16), uv(6), lighting(5), colour(3), raytracer(3), DepLoader(10), math(1) |
| **TrueType** (`truetyper`) | 6 | `TrueTypeFont`, `FontLoader`, `FontHelper`, `Formatter` |
| **Common server** | ~340 | blocks(71), tiles(80), items(60+), entities(90+), AI(40+), containers(26), config(9), lib(45+) |
| **Client** | ~210 | GUIs(24), TESR(52), entity renders(42), block renders(23), models(48), FX(33), lib(9) |
| **Network packets** | 30+ | 39 registered `IMessage` handlers |
| **Other** | ~60 | potions(7), enchantments(5), biomes(4), dim(19), world gen(15), crafting(5), events(10) |

### Assets

| Type | Count | Location |
|------|-------|----------|
| PNG textures | 820 | `assets/thaumcraft/textures/{blocks,items,models,gui,aspects,...}` |
| OGG sounds | 111 | `assets/thaumcraft/sounds/` |
| OBJ models | 16 | `assets/thaumcraft/textures/models/` |
| GLSL shaders | 7 | `assets/{minecraft,thaumcraft}/shaders/` |
| Lang files | 22 | `assets/thaumcraft/lang/*.lang` |
| Animated tex config | 101 | `*.png.mcmeta` alongside block textures |
| Sounds JSON | 1 | `assets/thaumcraft/sounds.json` |

---

## 3. Architecture & Dependencies

### Dependency Graph (Port Order)

```
thaumcraft.api  (no deps — port first)
       │
       ▼
thaumcraft.codechicken.*  (pure math + OpenGL — port second)
       │
       ▼
truetyper  (pure Java AWT + GL)
       │
       ▼
Core Systems:
  ├── Config
  ├── Aspect / AspectList
  ├── Network (SimpleNetworkWrapper — ports 1:1)
  ├── Event Handlers
  └── Registration Framework (RegistryEvent pattern)
            │
            ▼
Common Content:
  ├── Blocks (~71) + TileEntities (~80)
  ├── Items (~110) + Baubles (~6)
  ├── Entities (~90) + AI (~40)
  └── World Gen (biomes, dimension, features)
            │
            ▼
Client Content:
  ├── Models + JSON blockstates
  ├── TESR (52) + Entity Renderers (42)
  ├── GUIs (24)
  ├── Particles (22 custom FX classes)
  ├── Shaders (7 GLSL)
  └── Font Renderer (TrueType)
            │
            ▼
Crafting + Research Content
            │
            ▼
JEI Integration + Polish
```

### External Dependencies

| Dep | Type | 1.12.2 Version | Source |
|-----|------|---------------|--------|
| **Baubles** | Hard | `Baubles-1.12-1.5.2` | CurseMaven (ID 227083, file 2518667) |
| **MinecraftForge** | Hard | `1.12.2-14.23.5.2860` (recommended) | Forge MDK |
| **CodeChickenLib** | **Bundled** | N/A — port `thaumcraft.codechicken.*` | Internal |
| **JEI** (optional) | Soft | `jei_1.12.2` | CurseMaven, phase 9+ |
| **OptiFine** (detected) | Optional | Via reflection check | Runtime detection |

---

## 4. Porting Phases

### Phase 0 — Foundation & Tooling (estimated: 1 session)

**Goal**: Compilable Forge 1.12.2 MDK workspace with zero Thaumcraft
classes, confirming the Gradle/Baubles/Forge chain works.

**Deliverables**:
- `build.gradle` with Forge 1.12.2, Baubles via CurseMaven, JEI (optional)
- `gradlew` wrapper generated inside container
- `./gradlew setupDecompWorkspace` passing
- `./gradlew runClient` launching with Baubles loaded
- `src/main/java` structure with empty `Thaumcraft.java` @Mod class
- Build-audit script for `.gitignore` updates

**Risks**: Forge 1.12.2 Gradle scripts need JDK 8 specifically — verified in
Docker. CurseMaven may go down; have backup Maven URL.

---

### Phase 1 — API & CCL Bundled Library (estimated: 1 session)

**Goal**: `thaumcraft.api` and `thaumcraft.codechicken.*` compiling in the
1.12.2 workspace with no game dependencies.

**Deliverables**:
- Package `thaumcraft.api` — all 67 classes ported:
  - Core (`ThaumcraftApi`, `ThaumcraftApiHelper`, `ItemApi`)
  - Aspects (`Aspect`, `AspectList`, `IAspectContainer`, `IEssentiaTransport`)
  - Wands (`WandRod`, `WandCap`, `StaffRod`, `FocusUpgradeType`, `ItemFocusBasic`)
  - Research (`ResearchItem`, `ResearchPage`, `ResearchCategories`, `ScanResult`)
  - Crafting (`IArcaneRecipe`, `ShapedArcaneRecipe`, `InfusionRecipe`, `CrucibleRecipe`)
  - Nodes (`INode`, `NodeType`, `NodeModifier`, `TileVisNode`)
  - VisNet (`VisNetHandler`)
  - Internal (`IInternalMethodHandler`, `DummyInternalMethodHandler`)
  - Potions (`PotionFluxTaint`, `PotionVisExhaust`)
  - Damage sources, entity markers, interfaces
- Package `thaumcraft.codechicken.*` — 73 classes ported:
  - `lib.vec` — pure math (Vector3, Matrix4, Quat, Cuboid6, BlockCoord) — **no Forge deps**
  - `lib.colour` — pure data (ColourRGBA, ColourARGB)
  - `lib.math` — MathHelper
  - `lib.raytracer` — RayTracer, ExtendedMOP (update `MovingObjectPosition` → `RayTraceResult`)
  - `lib.render` — CCRenderState, CCModel, Vertex5, pipeline (update `Tessellator` → `BufferBuilder`, `GL11` → `GlStateManager`)
  - `lib.render.uv` — UV transforms
  - `lib.lighting` — LightMatrix, LightModel (update AO sampling)
  - `core.launch.DepLoader` — **delete** (not needed in 1.12.2)
- Package `truetyper` — 6 classes ported (update `GL11` → `GlStateManager`)

**Key decisions**:
- CCL render pipeline: adapt to `BufferBuilder` or replace with `IBakedModel`?
  **Recommendation**: adapt CCL for TESR-only; use JSON models for static blocks.
- `VisNetHandler`: pure static logic + WeakReference tree — ports as-is.
- `DummyInternalMethodHandler`: keep for API-only environments.

**Risks**: CCL `LightMatrix` uses `IBlockAccess` AO methods that changed in
1.12.2. `CCRenderState` writes to `Tessellator` — maps to `BufferBuilder`
with different API. `RayTraceResult` is a class in 1.12.2 vs
`MovingObjectPosition` enum pattern.

---

### Phase 2 — Registration Framework & Networking (estimated: 1 session)

**Goal**: All registration stubs and network layer compiling, capable of
loading the mod without crashes (console log of all registered items/blocks).

**Deliverables**:
- `Thaumcraft.java` @Mod class with:
  - `preInit`/`init`/`postInit`/`serverLoad`
  - `@SidedProxy` (CommonProxy / ClientProxy)
  - `RegistryEvent.Register<Block>`, `<Item>`, `<EntityEntry>`, `<Potion>`,
    `<Enchantment>`, `<Biome>`, `<SoundEvent>` subscribers
- `Config.java` — port from `Configuration` API (same API in 1.12.2, just
  file path changes). Keep `Configuration` to minimise diff; migrate to
  `@Config` later if desired.
- `PacketHandler.java` — port 39 packet registrations (package rename only)
- `CommonProxy.java` — port 20 container/GUI handler mappings
- `ClientProxy.java` — stubs for display info registration
- All event handlers (`EventHandlerWorld`, `EventHandlerEntity`,
  `EventHandlerRunic`) subscribing correctly
- Creative tab (`CreativeTabThaumcraft`)
- `InternalMethodHandler` implementing `IInternalMethodHandler` (bridge)

**Key changes**:
- Block/item/TE registration moves to `RegistryEvent`
- Enchantment IDs no longer configurable (auto-registry)
- Potion array hack removed (registry-based)
- Biome int IDs replaced with registry names
- `@Mod.EventBusSubscriber` for registry event classes

**Risks**: Low — mostly mechanical package renames and event restructuring.
`Config` file path uses `event.getSuggestedConfigurationFile()` → use
`FMLPreInitializationEvent.getModConfigurationDirectory()` + mod ID.

---

### Phase 3 — Core Game Systems (estimated: 2 sessions)

**Goal**: Aspects work, vis network connects, wands can be held, research
can be opened (empty), player data persists.

**Deliverables**:
- **Aspect System** (`Aspect` + `AspectList`):
  - 46 aspect constants registered
  - Aspect tags on `ItemStack` via `ThaumcraftApi.registerObjectTag()`
  - `AspectSourceHelper` (essentia drain/find)
- **Vis Network** (`VisNetHandler` + `TileVisNode`):
  - Chunk-based vis regeneration
  - Node connectivity graph with `WeakReference<WorldCoordinates>` tree
  - Vis drain/source mechanics in `VisNetHandler.drainVis()`
- **Wand System** (`ItemWandCasting`, `ItemFocusBasic`):
  - Wand rod/cap registries (`WandRod.rods`, `WandCap.caps`)
  - Vis storage in NBT (6 primal aspects as `int` values per tag)
  - Wand foci delegation (right-click, use-tick, stop-use, block break)
  - Vis cost calculation (cap modifier + discounts + sceptre bonus)
  - Staff rod attributes (`AttributeModifier` for attack damage)
- **Research System**:
  - `ResearchManager` + `PlayerKnowledge` + hex grid
  - `ScanManager` (entity scanning, item scanning, phenomena)
  - Research unlock sync via packets
- **Capability System** (player data):
  - Warp (normal + sticky + temporary)
  - Known aspects discovered
  - Scanned entities/items/phenomena
  - Research completion flags
- **Potions** (7 custom): `PotionFluxTaint`, `PotionVisExhaust`, etc.
- **Enchantments** (5): Haste, Potency, Frugal, Wand Fortune, Repair

**Key changes**:
- Player data: `IExtendedEntityProperties` → `Capability<IPlayerKnowledge>`
  attached to `EntityPlayer` via `AttachCapabilitiesEvent<Entity>`
- Potion array hack removed — use `RegistryEvent.Register<Potion>` for all 7
- Enchantment IDs auto-assigned — remove manual ID config

**Risks**: Vis network has world performance implications — chunk loading
events changed in 1.12.2 (`ChunkDataEvent.Load/Save`). Research sync
packets must be carefully queued to avoid login-time desync.

---

### Phase 4 — Blocks & TileEntities (estimated: 4 sessions)

**Goal**: All 71 blocks placeable in world with correct models, 80 tile
entities functional.

**Deliverables**:
- Every `Block` subclass ported:
  - Constructor changes: `Material` + optional `MapColor`
  - `IBlockState`-aware getters (no more metadata ints)
  - `getMetaFromState` / `getStateFromMeta` for blocks that need it
  - `BlockSound` replaces sound type strings
  - Blockstate JSONs in `assets/thaumcraft/blockstates/` (~40 JSONs)
  - Model JSONs in `assets/thaumcraft/models/block/` (~60 JSONs)
- Every `TileEntity` subclass ported:
  - `updateEntity()` → `update()` (clean name)
  - `func_145839_a` → `readFromNBT`, `func_145841_b` → `writeToNBT`
  - `world.getBlock(x, y, z)` → `world.getBlockState(pos)`
  - `world.getBlockMetadata(x, y, z)` → `state.getValue(...)`
  - `markBlockForUpdate` → `world.markBlockForUpdate(pos)` (same name)
  - NBT access: `func_74768_a/62_e` → `setInteger/getInteger` (clean names)
- Key tile entities:
  - `TileInfusionMatrix` with 25x16x25 surround scan + pedestal management
  - `TileCrucible` with aspect melting + recipes
  - `TileArcaneWorkbench` with crafting matrix
  - `TileVisNode` (abstract) + subclasses (source, relay, hungry, etc.)
  - `TileJar` (essentia storage + labels)
  - `TileTube` / `TileValve` (essentia pipe suction network)
  - `TileMirrorEssentia` / `TileMirrorItem` (cross-dimensional transport)
  - `TileArcaneBore` (spelunking)
  - `TileResearchTable` (aspect discovery GUI)
  - `TilePedestal` (item display)
  - `TileAlchemyFurnace` / `TileThaumatorium`
  - Various lamp blocks, planters, loggery, etc.

**Key changes**:
- Block metadata → `IBlockState` with `Property<?>` enums
- No `RenderBlocks` — all block rendering via JSON models + `IBakedModel`
  overrides for complex shapes (jars, tubes, candles)
- OBJ models (16 files) need `OBJLoader.INSTANCE.addDomain("thaumcraft")`
  registration
- Animated textures (101 `.mcmeta` files) use `TextureAtlasSprite` system

**Risks**: **Highest effort phase**. 80 tile entities with complex NBT
serialization. Tube/pipe network uses `IEssentiaTransport` suction model
which relies on `getWorldObj().getTileEntity()` neighbour polling every tick
— same pattern works. Tainted blocks spread via `updateTick()` — port
directly. Infusion matrix stability check scans 25×16×25 volume every tick
— perf-critical, must verify no regression.

---

### Phase 5 — Items, Tools, Armor & Baubles (✅ Compiling, 1 session)

**Status**: All ~110 items ported. 0 `java` compile errors. Stubs ready for
gameplay logic in later phases.

**Deliverables**:
- **Items** (~22): `ItemResource`, `ItemShard`, `ItemEssence`,
  `ItemCrystalEssence`, `ItemNugget`, etc.
  - `getSubItems` → `getSubItems(CreativeTab, NonNullList<ItemStack>)`
  - `addInformation` tooltip patterns (vis display, warp indicator)
  - `ItemApi.getItem/Block` factories
- **Wands & Foci** (~8 + 10):
  - `ItemWandCasting` with NBT-based vis/cap/rod/focus storage
  - 10 focus items: shock, fire, frost, excavation, primal, warding,
    hellbat, pech, trade, portable hole
  - `IWandable` interaction (block + tile entity dispatch)
- **Weapons & Tools** (~19): Thaumium/Void/Elemental swords, picks, axes,
  shovels, hoes. `IRepairable` / `IRepairableExtended`.
- **Armor** (~15): Thaumium Fortress, Void Fortress, Cultist, Robes,
  Goggles, Boots of the Traveler. `IRunicArmor` + `IVisDiscountGear`.
- **Baubles** (~6): Ring of Runic Shielding, Amulet of Runic Shielding,
  Girdle of Runic Shielding, Ring of Hover, Hover Harness, Hover Harness
  (Belt). `IBaublesItem` integration.
- **Relics** (~5): Thaumometer, Thaumonomicon, Hand Mirror, Resonator,
  Sanity Checker.

**Key changes**:
- `ItemStack` constructors: `new ItemStack(item, count, meta)` is
  deprecated in 1.12.2 — use `new ItemStack(IItemProvider, count)` or
  `new ItemStack(Item, count, int)` (still works but warns)
- Model registration: `ModelLoader.setCustomModelResourceLocation` for
  items with subtypes (wand caps/rods/foci)
- Baubles API: Baubles 1.12 uses `capability` system — implement
  `IBaublesItem` (or use `BaublesApi` helper)
- `ItemWandCasting` tooltip shows vis — use `IGetter`/`ISmelter` etc.
- Focus items: `onEntitySwing` → `onLeftClickEntity` / `onPlayerDestroyBlock`

**Risks**: Baubles API integration changed between 1.7.10 and 1.12.2 —
need to verify `BaublesApi` methods exist in 1.12.2 version. Focus
rendering (ornament/overlay) uses CCL render pipeline.

---

### Phase 6 — Entities, Mobs & Golems (✅ Compiling, 5 sub-steps)

**Status**: All 128 entity classes ported, registered via `ConfigEntities`. 44 AI
classes in 6 sub-packages. Champion modifier framework complete. 0 compile errors.

**Sub-steps**:
- **6.1** — Registry + AI system (44 AI classes) + all entity stubs (53)
- **6.2** — Taint mobs + projectiles fleshed out (behavior, drops, AI)
- **6.3** — Eldritch/Cultist/Boss mobs fleshed out (19 entities)
- **6.4** — Golems fleshed out (EntityGolemBase, trunk, helper, items)
- **6.5** — Champion modifiers (14 effects + registry)

**Deliverables**:
- **Hostile Mobs** (~30):
  - Taint mobs: Taintacle, Taint Spider, Taint Creeper, Taint Swarm,
    Taint Chicken/Cow/Pig/Sheep/Villager, Taint Spore, Taintacle Small
  - Cultists: Cultist, Cultist Cleric, Cultist Knight, Cultist Leader (boss)
  - Eldritch: Eldritch Crab, Eldritch Guardian, Eldritch Golem (boss),
    Eldritch Warden (boss), Watcher
  - Other: Wisp, Fire Bat, Pech, Brainy Zombie, Giant Brainy Zombie,
    Inhabited Zombie, Thaumic Slime, Mind Spider
  - Champion modifiers (14 effects): Armored (24% dmg reduction), Vampiric
    (heals 50% dmg dealt), Undying (1 HP/s regen), Fire (40% set ablaze),
    Warded (15% reduction), Bold (1.5x dmg), Poison/Warp/Sickly (status),
    Spined (thorns), Grim (weakness), Mighty (knockback), Infested
- **Golems**:
  - `EntityGolemBase` with 12 core AI types (gather/empty/pickup/harvest/
    attack/fluids/essentia/lumber/use/butcher/sort/fish)
  - `EntityTravelingTrunk` — inventory follower with UUID owner tracking
  - `EntityGolemBobber` — fishing bobber
  - `GolemHelper` + `Marker` + `EnumGolemType` (8 types)
- **Projectiles** (~12):
  - `EntityAlumentum` (explosion), `EntityFrostShard` (bounce/fragile),
    `EntityDart`, `EntityPrimalArrow`, `EntityBottleTaint`,
    `EntityEldritchOrb`, `EntityExplosiveOrb`, `EntityPechBlast`,
    `EntityShockOrb`, `EntityEmber`, `EntityGolemOrb`, `EntityPrimalOrb`
- **AI System** (44 classes):
  - Golem AI: combat/fluid/inventory/interact/misc/pech — 6 sub-packages
  - Combat AI: `AIAttackOnCollide`, `AIGolemAttackOnCollide`,
    `AICreeperSwell`, `AICultistHurtByTarget`, `AIDartAttack`, etc.
  - Golem task AI: `AIHomeTake/Place/Drop/Replace`, `AIEmptyGoto/Drop/Place`,
    `AIFillTake/Goto`, `AIItemPickup`, `AISortingGoto/Place`,
    `AIEssentiaGather/Empty/Goto`, `AILiquidGather/Empty/Goto`,
    `AIHarvestCrops/Logs`, `AIFish`, `AIUseItem`

**1.12.2 findings**:
- `IEntityAdditionalSpawnData` requires `ByteBuf` (Netty), not `PacketBuffer`
- `EntityAITarget` constructor: `(EntityCreature, boolean)` in 1.12.2
- `IBossDisplayData` removed — use custom boss bar
- `EntityAIArrowAttack` removed — use `EntityAIBase`
- `getLivingSound()` renamed to `getAmbientSound()`
- `EntityDataManager` uses `DataParameter<T>` static fields via `createKey()`
- `moveEntity()` renamed to `move(MoverType, x, y, z)`
- Golem inventory uses raw `IInventory` (not `ItemStackHandler` capability)
- Blaze `getEntityAttribute` → `SharedMonsterAttributes` constants change
- `EntityEntryBuilder.create()` returns raw type (need `@SuppressWarnings`)
- `Optional<UUID>` in 1.12.2 Forge uses Guava `com.google.common.base.Optional`

**Risks**: Champion modifier system uses `IEntityLivingData` hook and
`EntityJoinWorldEvent` — same in 1.12.2. Entity renderers (42 classes)
depend on client-side work in Phase 8. Boss entities have custom AI phases
that need actual logic (currently placeholder stubs).

---

### Phase 7 — World Generation (✅ Compiling, 4 sub-steps)

**Status**: 4 biomes, Eldritch dimension, trees, structures, and world
generator all compile. Ores, trees, and structures generate via
`ThaumcraftWorldGenerator`. Custom dimension accessible via `WorldProviderOuter`.

**Sub-steps**:
- **7.1** — Missing world-gen blocks: BlockMagicalLeaves (2 types), BlockCustomOre
  (8 ores), BlockCustomPlant (6 plants), BlockCosmeticSolid (16 types),
  BlockCosmeticOpaque (5 types), BlockTaint (3 types), BlockTaintFibres (5 types),
  BlockAiry (13 types), BlockManaPod (8 stages), ItemBlocks + tile stubs
- **7.2** — Biomes: BiomeMagicalForest (custom trees/flowers/decor), BiomeTaint
  (taint blobs/fibres spread), BiomeEerie (spooky mobs), BiomeEldritch (stone
  surface), BiomeHandler (aura/aspect mapping from 30 BiomeDictionary types)
- **7.3** — Eldritch Dimension: WorldProviderOuter (black sky, fog, no day cycle),
  ChunkProviderOuter (flat stone terrain), MazeGenerator (DFS randomized maze),
  MazeThread/MazeHandler (threaded gen + hashmap storage), TeleporterThaumcraft,
  7 room gen stubs, BlockEldritch/Portal/Nothing + 8 tile entities, dimension
  registration via DimensionType/DimensionManager
- **7.4** — World features: WorldGenGreatwoodTrees (3-wide trunk, oval canopy),
  WorldGenSilverwoodTrees (node knot, layered canopy), WorldGenBigMagicTree
  (3x3 trunk, huge canopy), WorldGenManaPods, WorldGenMound/EldritchRing/
  HilltopStones/CustomFlowers, ThaumcraftWorldGenerator with ore/tree/structure
  generation and biome/dimension blacklist

**1.12.2 findings**:
- `Biome.color` field unmapped in MCP stable 39 — removed from code
- `getRandomTreeGen()` renamed to `getRandomTreeFeature()` on `Biome`
- `calcSunriseColor()` renamed to `calcSunriseSunsetColors()` on `WorldProvider`
- `WorldSavedData` moved to `net.minecraft.world.storage.WorldSavedData`
- `getWelcomeMessage()`/`getDepartMessage()` removed from `WorldProvider`
- `IChunkProvider` → `IChunkGenerator` in 1.12.2; `provideChunk`→`generateChunk`,
  `getPossibleCreatures` gains `BlockPos` param
- `BiomeDictionary.Type` is a class (not enum) in 1.12.2; `getTypes()` returns
  `Set<Type>` not `Type[]`; no `FROZEN`/`DESERT` types
- `WorldInfo.getCustomData()`/`setCustomData()` removed — use `WorldSavedData`
- `DimensionType` and `DimensionManager` required for dimension registration
- Forge patches remove `WorldProvider.dimensionId` field — use
  `world.provider.getDimension()` or `getDimensionType()`

---

### Phase 7r — World Gen Remediation (estimated: 1 session)

**Цель**: Довести world gen до рабочего состояния — закрыть костыли,
заполнить заглушки, портировать недостающие блоки/сущности.

#### Known Issues

| # | Компонент | Статус | Проблема | Fix |
|---|-----------|--------|----------|-----|
| 1 | **Biome color** (все 4 биома) | ❌ Deferred | Поле `Biome.color` удалено в 1.12.2 MCP. На F3 биомы без цветного фона (косметика) | Разобраться с 1.12.2 способом установки debug overlay цвета (возможно через `Biome` events) |
| 2 | **MazeHandler persistence** (dim) | ✅ Done | `loadMaze()`/`saveMaze()` через `CompressedStreamTools` + `labyrinth.dat` (прямая файловая запись, как в оригинале) | Реализовано в `MazeHandler.loadMaze/saveMaze` |
| 3 | **7 room generators** (dim) | ✅ Done | GenBossRoom, GenKeyRoom, GenLibraryRoom, GenNestRoom, GenPassage, GenPortal, Gen2x2 — полный порт из оригиналов | Все 7 классов портированы с CFR-декопилей |
| 4 | **GenCommon.placeBlock()** (dim) | ✅ Done | 21 Block ID (BEDROCK=1…CRUST=21), PAT_CONNECT, processDecorations, generateConnections, isBedrockShowing | Реализовано в `GenCommon` |
| 5 | **BlockStairsEldritch** (dim) | ✅ Done | Новый класс `BlockStairsEldritch` (extends `BlockStairs`, ссылается на `blockCosmeticSolid`) | Зарегистрирован в `ConfigBlocks` |
| 6 | **BlockEldritchPortal.onEntityCollision** (dim) | ✅ Done | Телепортация в `OuterLands` (dim -42) через `TeleporterThaumcraft` с 40-тик кулдауном | Реализовано в `BlockEldritchPortal` + `TileEldritchPortal` |
| 7 | **EntityPermanentItem** (dim) | ✅ Done | `EntityPermanentItem` расширен: иммунитет к урону/взрывам, `lifespan = Integer.MAX_VALUE` | Реализовано |
| 8 | **Village components** | ✅ Done | ComponentWizardTower, ComponentBankerHome, VillageWizardManager, VillageBankerManager — полный порт с адаптацией под 1.12.2 API | IVillageCreationHandler (EnumFacing вместо int), setBlockState/IBlockState, generateDoor, MapGenStructureIO, VillagerProfession |
| 9 | **WorldGenSilverwoodTreesOld** | ✅ Done | Legacy tree gen портирован и встроен в WorldGenSilverwoodTrees | Полная процедурная генерация + createRandomNodeAt для узлов PURE-типа |

#### Выполнено

| Step | Scope | Статус |
|------|-------|--------|
| **7r.2** | MazeHandler persistence | ✅ CompressedStreamTools file I/O |
| **7r.3** | BlockStairsEldritch + EntityPermanentItem | ✅ |
| **7r.4** | Portal teleport (Tile + Block) | ✅ |
| **7r.5** | Room generators (7 + GenCommon) | ✅ ~800 строк |
| **7r.5** | MazeGenerator (feature assignment, exit room, dead-end rooms) | ✅ |
| **7r.5** | MazeThread (proper grid feature storage) | ✅ |
| **7r.5** | BlockUtils.countExposedSides / isBlockAdjacentToAtleast / isAdjacentToSolidBlock | ✅ |
| **7r.5** | Utils.setBiomeAt | ✅ |
| **7r.5** | EntityUtils.makeChampion (minimal) | ✅ |
| **7r.5** | TileEldritchCrabSpawner / TileEldritchLock (setFacing + NBT) | ✅ |
| **7r.6** | Village components (4 класса + профессии + регистрация) | ✅ ComponentWizardTower, ComponentBankerHome, VillageWizardManager, VillageBankerManager. Адаптация: EnumFacing coordBaseMode, IBlockState, generateDoor, MapGenStructureIO, VillagerProfession, ручной chest loot |
| **7r.7** | Silverwood trees with aura nodes | ✅ Полная процедурная генерация (4-крест ствол, сферическая крона, корни, саженцы). createRandomNodeAt для узлов PURE-типа в стволе. createNodeAt и createRandomNodeAt в ThaumcraftWorldGenerator |
| **7r.8** | Sapling growth (greatwood + silverwood) | ✅ updateTick/growGreatTree/growSilverTree в BlockCustomPlant. PropertyInteger TYPE для правильных blockstate. Саженцы растут: greatwood 1/25, silverwood 1/50 |

#### Deferred (следующая итерация)

| Step | Scope | Почему | План |
|------|-------|--------|------|
| **7r.1** | Biome color fix | `Biome.color` удалён в 1.12.2; `ColorHandlerEvent.Biome` не существует в Forge 1.12.2 | Найти 1.12.2-способ задания debug overlay цвета для биома |

**Примечание**: торговля заменена с удалённого `IVillageTradeHandler` на
1.12.2-систему `VillagerCareer.addTrade()` + кастомные `EntityVillager.ITradeList`.

---

### Phase 8 — Client GUI & Rendering (estimated: 3 sessions)

**Goal**: All GUIs open, all tile/entity renders correct, particles and
shaders work.

**Deliverables**:
- **GUIs** (~24):
  - Research Table (`GuiResearchTable`) — hex grid navigation
  - Arcane Workbench (`GuiArcaneWorkbench`) — crafting with aspect cost
  - Infusion Matrix (`GuiInfusionMatrix`) — pedestal sync display
  - Thaumonomicon (`GuiThaumonomicon`) — in-game documentation book
  - Golem GUI (`GuiGolem`) — AI core assignment + upgrades
  - Pech Trade (`GuiPech`) — trade interface
  - Hover Harness (`GuiHoverHarness`) — flight controls
  - Focal Manipulator (`GuiFocalManipulator`) — focus upgrade slots
  - Various: Deconstruction Table, Alchemy Furnace, Research Table,
    Traveling Trunk, Focus Pouch, Magic Box, Spa, Thaumatorium
  - All extend `GuiContainer` — port with `drawGuiContainerForegroundLayer`
    / `drawGuiContainerBackgroundLayer` (renamed from 1.7.10)
- **Tile Entity Renderers** (52 TESR):
  - Node renderer (auric node with spinning aspect particles)
  - Crucible renderer (liquid contents + boiling effect)
  - Arcane Bore renderer, Bellows renderer, Pedestal renderer
  - Essential jar / void jar label renderer
  - Mirror renderer (portal effect)
  - Tube/pipe renderers
  - OBJ-based model renderers (16 models: alembic, ark, crystalizer, etc.)
  - All extend `TileEntitySpecialRenderer<T>` — generic type now required
- **Entity Renderers** (42):
  - Golem renderer + texture variants
  - Taint mob renderers (slime-like, spider, crawler, swarm)
  - Cultist renderer + armor overlays
  - Eldritch guardian/golem/warden renderers
  - Projectile renderers (alumentum, arrow, dart, shard)
  - Extend `Render<T>` or `RenderLiving<T>`
- **Model Classes** (48):
  - ~27 dedicated model classes (cube-based, using `ModelBase` for entities)
  - ~15 entity model sub-models
  - ~6 gear/armor model classes
- **Custom Particle Engine** (22 FX classes + `ParticleEngine`):
  - `FXWisp`, `FXSparkle`, `FXBubble`, `FXGeneric`, `FXBurst`, etc.
  - Rendered via `RenderWorldLastEvent` on 4 layers
  - Port `EntityFX` → `Particle` (1.12.2 class rename)
  - Custom engine uses its own `ArrayList<Particle>[]` storage — keep this
    design, replace vanilla `EffectRenderer` calls
- **Beam & Bolt FX**:
  - 6 beam classes (wand beam, bore beam, power beam, golem boss beam)
  - 5 lightning bolt classes (bolt animation, arc, zap)
  - Rendered in world space with `Tessellator`/`BufferBuilder`
- **Shader System** (7 GLSL files):
  - Bloom (bloom2, bloomColor), Color Convolution, Blur, Desaturate
  - Post-process shaders applied via `EntityViewRenderEvent` or
    `RenderGameOverlayEvent`
  - Shader JSON configs in `assets/minecraft/shaders/post/`
  - Custom Thaumcraft shaders in `assets/thaumcraft/shader/` (test.vert/frag)

**Key changes**:
- `GuiContainer.drawScreen(int, int, float)` — override instead of old method
- `drawGuiContainerForegroundLayer` / `drawGuiContainerBackgroundLayer` —
  same as 1.7.10
- TESR: now generic on tile type (`TileEntitySpecialRenderer<T extends TileEntity>`)
- `RenderEntity` → `Render<T>` with generic parameter
- Model system: `ModelBase` → `ModelBase` (same class)
- Particle: `EntityFX` → `Particle`
- OpenGL: `GL11.*` → `GlStateManager.*` (bulk replace)
- `Tessellator` → `Tessellator.getInstance().getBuffer()` returns `BufferBuilder`
  with different method names (`startDrawingQuads` → `begin(7, DefaultVertexFormats...)`)

**Risks**: **Highest complexity per class ratio**. 52 TESR + 42 entity
renderers + 24 GUIs + 22 particle classes = ~140 client-side classes.
Every one needs OpenGL call updates. Shader system uses Minecraft's
internal `ShaderGroup` — same in 1.12.2. The custom particle engine is
decoupled from vanilla and ports cleanest of all client code.

---

### Phase 8r — Client Remediation

**Цель**: Портировать пропущенные клиентские классы, которые не были
созданы в Phase 8.

#### Known Issues (будет заполнено после анализа)

---

### Phase 9 — Crafting & Research Content (estimated: 2 sessions)

**Goal**: All recipes craftable, all research entries unlockable.

**Deliverables**:
- **Vanilla Recipes**: ~200 shaped/shapeless recipes ported to JSON format
  in `assets/thaumcraft/recipes/`
- **Arcane Recipes**: ~150 `ShapedArcaneRecipe` / `ShapelessArcaneRecipe`
  registrations via `IArcaneRecipe` → `RegistryEvent.Register<IRecipe>`
- **Infusion Recipes**: ~100 `InfusionRecipe` + `InfusionEnchantmentRecipe`
  registrations via `RegistryEvent.Register<IRecipe>`
- **Crucible Recipes**: ~50 `CrucibleRecipe` registrations
- **Smelting**: ~30 furnace recipe registrations
- **Research Entries**: ~200 `ResearchItem` instances with hex-grid layout,
  research pages (text, image, recipe reference, aspect diagram)
- **Loot**: `WeightedRandomLoot` for common/uncommon/rare loot bags
- **Aspect Tags**: `ThaumcraftApi.registerObjectTag` calls for all
  vanilla + Thaumcraft items (~500 registrations)
- **Entity Aspects**: `ThaumcraftApi.registerEntityTag` for all entities
- **Smelting Bonuses**: `ThaumcraftApi.addSmeltingBonus` for native clusters

**Key changes**:
- Vanilla recipes move from `GameRegistry.addRecipe()` to JSON files
- Custom recipe types (arcane, infusion, crucible) register as `IRecipe`
- Research page recipes reference via registry name, not `IRecipe` object
- `CraftingManager` removed — use `ForgeRegistries.RECIPES`

**Risks**: Low technical risk, high volume/detail work. Recipe JSONs must
use correct registry names. Research page recipe references use object
comparison — must ensure registry identity is preserved.

---

### Phase 9r — Research & Crafting Remediation

**Цель**: Заполнить заглушки в ResearchManager, ScanManager, добавить
пропущенные классы (ResearchNoteData, HexUtils, etc.), починить
эффекты зелий, зачарования.

#### Known Issues (будет заполнено после анализа)

---

### Phase 10 — Polish & Integration (estimated: 1 session)

**Goal**: Mod is playable with no crashes, all systems tested, JEI shows
recipes.

**Deliverables**:
- **JEI Integration**:
  - `IRecipeCategory` for Arcane Crafting, Infusion, Crucible
  - `IRecipeTransferHandler` for Arcane Workbench
  - Handle aspect ingredient display in JEI tooltips
- **Config GUI**:
  - Use `@Config` annotation for `GuiConfig` / `ConfigGuiFactory`
  - Categories: Enchantments, Entities, Biomes, Research, World Gen,
    Monster Spawning, Runic Shielding
- **Sound System**:
  - Verify all 111 OGG files registered as `SoundEvent` via registry
  - `sounds.json` mapping from 1.7.10 format to 1.12.2
- **Localization**: 22 `.lang` files — mostly portable as-is (same format)
- **Mod Compatibility**:
  - OptiFine detection (shader compatibility, block rendering)
  - IMC message processing for crop/lamp/biome/dimension blacklists
  - Champion mob whitelist IMC
  - MFR/Thermal Expansion safari net interactions (reflect-based — may drop)
- **Performance**:
  - Vis network chunk scanning — verify no TPS regression
  - Particled engine — check culling/frustum
  - Infusion matrix stability scan — ensure 25×16×25 scan has reasonable
    frequency
- **Crash Testing**: Load in clean world, creative tab browse, place all
  blocks, spawn all entities, disconnect TLS

**Risks**: Low. JEI integration is additive (mod works without JEI). IMC
for MFR/Thermal may fail silently — acceptable. Localization format is
identical.

---

## 5. Complexity Assessment

| Phase | Classes | Assets | Effort | Risk | Parallelizable |
|-------|---------|--------|--------|------|---------------|
| 0 — Foundation | 1 | 0 | 1 session | Low | No |
| 1 — API + CCL | 146 | 0 | 1 session | Medium | Limited (CCL render pipeline) |
| 2 — Registry + Net | ~20 | 0 | 1 session | Low | No (core framework) |
| 3 — Core Systems | ~30 | 0 | 2 sessions | High (vis net performance) | Systems within phase |
| 4 — Blocks + Tiles | 151 | ~500 tex + ~100 mcmeta + ~40 blockstates | 4 sessions | **Highest** | Blocks/blocks, tiles/tiles |
| 5 — Items + Baubles | 110 | ~200 tex | **Done** | Medium | Per-category |
| 6 — Entities + AI | 130 | ~100 tex | **Done** | High (golems) | Per-mob |
| 7 — World Gen | 35 | ~20 tex | **Done** | Medium | Biomes / dimension / features |
| 7r — World Gen Remediation | ~12 classes | 0 | 1 session | Medium (room gens) | Per-component |
| 8 — Client GUI + Render | ~140 | ~200 tex + 7 shaders | 3 sessions | **Highest** | GUI / TESR / EntityRend / FX / Shaders |
| 9 — Recipes + Research | ~450 registrations | 0 | 2 sessions | Low (volume) | Per-recipe-type |
| 10 — Polish | 5-10 | all | 1 session | Low | JEI / Config / Compatibility |

**Total estimate**: ~22-26 sessions of focused work.

---

## 6. Architectural Decisions (All Confirmed)

### Decision 1: API Packaging — Separate Mod

`thaumcraft.api` ships as a **separate JAR** (`thaumcraft-api-1.12.2.jar`).
This is the only hard dependency for addon mods (e.g., Thaumic Tinkerer,
Forbidden Magic). The main mod JAR declares it as `required-after`.

- Main JAR: `Thaumcraft-1.12.2-<version>.jar`
- API JAR: `ThaumcraftAPI-1.12.2-<version>.jar` (thin, no game code)
- Build: multi-project Gradle setup (`:api` + `:main`)
- Internal bridge: `ThaumcraftApi.internalMethods` set at startup

### Decision 2: Rendering Strategy — Hybrid (CCL for TESR Only)

| Scope | Render Approach | How |
|-------|----------------|-----|
| Simple blocks (dirt-like) | JSON model + blockstates | `assets/thaumcraft/blockstates/*.json` |
| Complex static blocks | OBJ loader wrapper | `OBJLoader.INSTANCE.addDomain("thaumcraft")` |
| Tile entities (nodes, crucible, jars, tubes, mirrors) | TESR with adapted CCL | Port `CCRenderState` → `BufferBuilder` |
| Entity models | `ModelBase` (unchanged) | OpenGL calls → `GlStateManager` |
| Particles | Custom `ParticleEngine` | 22 FX classes, `EntityFX` → `Particle` |
| Beams/bolts | Custom Tessellator/BufferBuilder | Direct vertex rendering |

- All `RenderBlocks` and `ISimpleBlockRenderingHandler` usage is **deleted**
- 52 TESR + 42 entity renders use CCL math + adapted render pipeline
- `CCRenderState`, `CCModel`, `Vertex5` ported to target `BufferBuilder`

### Decision 3: DepLoader Removal — Delete

Delete all 10 classes under `thaumcraft.codechicken.core.launch`. Remove
`FMLCorePlugin` entry from `META-INF/MANIFEST.MF`. Baubles declared in
`build.gradle` via CurseMaven replaces runtime auto-download.

### Decision 4: Potion Registration — Registry Events

Remove the reflection-based `Potion.potionTypes` array extension. Register
all 7 custom potions via `RegistryEvent.Register<Potion>`.

### Decision 5: Player Data — Capabilities

Replace `IExtendedEntityProperties` with a single
`Capability<IPlayerKnowledge>` attached via
`AttachCapabilitiesEvent<Entity>`. Include:
- Warp (normal + sticky + temporary)
- Known aspects discovered
- Scanned entities, items, phenomena
- Research completion flags

Sync via existing packet system (39 registered packets).

### Decision 6: Config — Both (Configuration First, @Config Later)

- **Now**: Keep `net.minecraftforge.common.config.Configuration` (same API
  as 1.7.10). Minimal code change to Config.java.
- **Phase 10**: Migrate to `@Config` annotation with auto-GUI. 50+ settings
  across 8 categories (Enchantments, Entities, Biomes, Research, World Gen,
  Monster Spawning, Runic Shielding, Misc).

### Decision 7: Package Naming — Keep Original Structure

Maintain `thaumcraft.common.*` and `thaumcraft.client.*` sub-packages to
preserve 1:1 mapping with decompiled CFR output. This makes diffing and
validation against the original bytecode straightforward.

### Decision 8: Localization — en_US Only First

Port all 22 `.lang` files only when strings stabilise after Phase 9.
Initial releases ship with `en_US` only.

---

## 7. Risks & Mitigations

| Risk | Impact | Likelihood | Mitigation |
|------|--------|------------|------------|
| CCL render pipeline incompatible with BufferBuilder | Blocks 52 TESR + 42 entity renders | High | Isolate CCL rendering to `BufferBuilder` wrapper; fall back to basic vanilla model for critical path |
| Forge 1.12.2 Gradle toolchain breaks | Blocks all work | Medium | Pinned known-working Gradle+Forge versions; Docker image provides reproducible env |
| Baubles 1.12 API differs from 1.7.10 | Baubles items non-functional | Medium | Decompile Baubles 1.12 API jar; adapt item implementations |
| Infusion stability scan CPU/perf | TPS lag on complex altars | Medium | Add configurable scan interval; cache stability results between changes |
| Eldritch dimension maze gen slow | Long join times | Low | Verify maze gen uses `MapGen` patterns; add progress logging |
| API separate mod adds build complexity | Multi-project gradle config | Low | Use `:` project references; API is pure interfaces, no game deps |
| OptiFine shader conflicts | Visual artifacts | Medium | Runtime detection (already in 1.7.10); fallback to non-shader rendering |
| JEI recipe integration breaks | No recipe viewing | Low | JEI is optional — mod works without it |
| **Previously unplanned: ~50% of Phase 3-6 content is placeholder stubs** | Core mechanics non-functional | **Certain** | Remediation phases 3r-6r needed before Phase 8 |

---

## 8. Success Criteria

1. `./gradlew build` produces a valid `.jar` installable as a Forge mod
2. All 71 blocks placeable with correct visuals
3. All 80 tile entities process NBT and respond to right-click
4. All ~110 items render correctly in inventory and on entities
5. Aspect system computes tags, vis network connects
6. Research hex grid renders with entries unlockable
7. Infusion crafting succeeds with animations
8. Eldritch dimension generates and is teleportable
9. All 4 custom biomes appear in world
10. Golem AI executes gathering/harvesting/guard tasks
11. 30+ taint/cultist/eldritch mobs spawn and fight
12. All ~200 research entries display with correct pages
13. All ~600 recipes (arcane + infusion + crucible + vanilla) are functional
14. JEI shows arcane/infusion/crucible recipe categories
15. 22 language files load without errors
16. No crashes on world load, dimension teleport, GUI open

---

## 9. Planned Remediation Phases

### Phase 3r — Core Systems Remediation
**Сфальсифицировано**: агент пропустил целые классы (WarpEvents), вырезал
методы из ResearchManager, сделал VisNetHandler без регенерации,
обнулил все эвент-хендлеры.

| # | Компонент | Severity | Что отсутствует |
|---|-----------|----------|-----------------|
| 1 | **WarpEvents** | 🔴 CRITICAL | Весь класс — warp checks, death gaze, mist spawn, research grant, guardian/mob spawn, gear warp |
| 2 | **VisNetHandler.onWorldTick** | 🔴 CRITICAL | Регенерация vis в source nodes (тело цикла пустое — `// Regeneration handled by node` но TileVisNode НЕ регенерирует) |
| 3 | **ResearchManager** (10+ методов) | 🔴 CRITICAL | createClue, createResearchNoteForPlayer, findHiddenResearch, findMatchingResearch, checkResearchCompletion, consumeInkFromPlayer/Table, getResearchSlot, scheduleSave — ВСЕ отсутствуют |
| 4 | **ResearchNoteData** | 🔴 HIGH | Весь класс не портирован (данные пазлов исследований для стола) |
| 5 | **HexUtils** | 🔴 HIGH | Весь класс не портирован (hex grid utilities) |
| 6 | **PlayerKnowledge.hiddenResearch** | 🔴 HIGH | Offline методы возвращают false/empty (hasDiscoveredAspect, getAspectsDiscovered) |
| 7 | **EventHandlerEntity** (11 хендлеров) | 🔴 HIGH | Все пустые: onLivingUpdate (WarpEvents), onLivingDeath, onEntityInteract, onItemPickup, onItemToss, onArrowLoose, onArrowNock, onPlayerBreakSpeed, onPlayerLoadFromFile, onPlayerSaveToFile, onPlayerRightClickItem |
| 8 | **EventHandlerRunic** (3 хендлера) | 🔴 HIGH | Все пустые: shield regen tick, damage absorption, tooltip — runic shielding не работает |
| 9 | **IPlayerKnowledge** (интерфейс) | 🔴 HIGH | Отсутствуют: getWarpCounter, getAspectPoolFor, addAspectPool, setAspectPool, hasDiscoveredParentAspects |
| 10 | **WandManager** (методы) | 🔴 HIGH | getTotalVisDiscount отсутствует (baubles + armor + potion penalties для vis cost) |
| 11 | **ItemWandCasting** (17+ методов) | 🔴 HIGH | Отсутствуют: getFocusPotency/Frugal/Enlarge/Extend, isStaff/Sceptre, hasRunes, addVis, addRealVis, getAllVis, getAspectsWithRoom, storeAllVis, consumeVis, getConsumptionModifier, onItemUseFirst, onEntitySwing |
| 12 | **PotionInfectiousVisExhaust** | 🔴 HIGH | performEffect пустой (нет распространения на nearby entities каждые 40 тиков), isReady всегда false |
| 13 | **PotionThaumarhia** | 🔴 CRITICAL | Эффект неправильный: конвертация блоков под игроком в flux goo заменена на нанесение урона |
| 14 | **PotionSunScorned** | 🟡 MEDIUM | Отсутствует heal 1 HP при brightness < 0.25 |
| 15 | **PotionBlurredVision** | 🟢 LOW | Неправильные texture atlas coordinates (1,2 вместо 5,2) |
| 16 | **Все 5 enchantments** | 🔴 HIGH | EnumEnchantmentType.DIGGER вместо ALL/ARMOR — зачарования не накладываются на правильные предметы. Отсутствуют canApply() / canApplyTogether() |
| 17 | **EnchantmentFrugal** | 🔴 HIGH | Тип DIGGER (должен быть ALL) — не работает на wand foci. Отсутствует canApply(IWandFocus) |
| 18 | **EnchantmentHaste** | 🔴 HIGH | Тип DIGGER (должен быть ARMOR для boots) — не работает на броне |
| 19 | **PacketHandler** (39 пакетов) | 🔴 HIGH | Все через STUB_HANDLER — нет сериализации/десериализации, нет обработки. Player data sync, FX, GUI interactions, research sync — всё сломано |
| 20 | **InternalMethodHandler** | 🟡 MEDIUM | generateVisEffect пуст, getStackInRowAndColumn возвращает EMPTY, getBonusObjectTags/generateTags возвращают пустые листы |

### Phase 4r — Blocks & Tile Entities Remediation
**Сфальсифицировано**: 45/61 TE — пустые заглушки (≤10 non-blank lines),
6 TE не созданы (трубы), все блоки с сабтайпами без createBlockState(),
на блоках нет harvest level.

| # | Компонент | Severity | Что отсутствует |
|---|-----------|----------|-----------------|
| 1 | **TileInfusionMatrix** | 🔴 CRITICAL | ~600 lines оригинала — весь crafting engine: surround scan, pedestal management, recipe lookup, stabilitas, craftCycle, instability effects, NBT, inner class SourceFX |
| 2 | **TileCrucible** | 🔴 CRITICAL | ~350 lines оригинала — fluid tank, aspect melting, recipe lookup, decomposition, spill/bellows, NBT |
| 3 | **TileNode** (abstract) | 🔴 CRITICAL | ~600 lines оригинала — update пустой: нет handleDischarge, handleRecharge, handleTaintNode, handlePureNode, handleDarkNode, handleHungryNodeFirst/Second, handleNodeStability |
| 4 | **TileVisRelay** | 🔴 CRITICAL | Пустая ITickable заглушка — нет vis relay network |
| 5 | **TileArcaneBore** | 🔴 CRITICAL | ~250 lines оригинала — pickaxe simulation, fake player, enchantment handling, block-breaking |
| 6 | **TileAlchemyFurnace** | 🔴 CRITICAL | ISidedInventory, smelting logic, essentia generation, bellows boost, alembic integration |
| 7 | **TileThaumatorium** | 🔴 CRITICAL | Automated alchemy processing |
| 8 | **TileMirrorEssentia** | 🔴 CRITICAL | 3 lines — cross-dimensional essentia transport |
| 9 | **6 TileTube* классов** | 🔴 CRITICAL | TileTube, TileTubeBuffer, TileTubeFilter, TileTubeOneway, TileTubeRestrict, TileTubeValve — ВСЕ не созданы. Essentia pipe network отсутствует полностью |
| 10 | **18 blocks с subtypes** | 🔴 CRITICAL | Нет createBlockState() / getStateFromMeta() / getMetaFromState() — metadata теряется, все ставятся как subtype 0 |
| 11 | **Harvest levels** | 🔴 CRITICAL | 19/19 блоков без setHarvestLevel/getHarvestTool — блоки не ломаются |
| 12 | **17 missing TE файлов** | 🔴 HIGH | TileAlchemyFurnaceAdvanced, TileAlchemyFurnaceAdvancedNozzle, TileArcaneLampFertility, TileArcaneLampLight, TileBrainbox, TileChestHungry, TileEssentiaCrystalizer, TileMagicBox, TileMemory, TileThaumcraftInventory, TileWarded — 11 не создано |
| 13 | **TileResearchTable** | 🔴 HIGH | update() пустой: нет research scanning logic |
| 14 | **TileDeconstructionTable** | 🔴 HIGH | update() пустой: нет deconstruction logic |
| 15 | **BlockTaint / BlockTaintFibres** | 🟡 MEDIUM | Нет updateTick (taint spread), onEntityCollidedWithBlock (poison), правильных AABB |
| 16 | **BlockMagicalLog** | 🟡 MEDIUM | damageDropped(meta & 3) всегда возвращает 0 |
| 17 | **TileJarFillable** | 🟢 LOW | containerContains() возвращает 0 вместо this.amount |
| 18 | **TilePedestal / TileResearchTable** | 🟢 LOW | getDisplayName() возвращает null (NPE риск в GUI) |

### Phase 5r — Items & Baubles Remediation
**Сфальсифицировано**: все 10 foci без onFocusRightClick, 5 relics без
right-click, 4 baubles с пустым onWornTick, 19 tools без special abilities.

| # | Компонент | Severity | Что отсутствует |
|---|-----------|----------|-----------------|
| 1 | **10 focus items** — onFocusRightClick | 🔴 CRITICAL | Все 10 foci возвращают wandStack без эффекта. Отсутствуют onUsingFocusTick, onPlayerStoppedUsingFocus |
| 2 | **10 focus items** — vis cost/upgrades | 🔴 HIGH | Нет getActivationCooldown, getAnimation, upgrade type definitions, isVisCostPerTick |
| 3 | **ItemWandCasting** — до 20 методов | 🔴 HIGH | isStaff/Sceptre/HasRunes, getFocusPotency/Frugal/Enlarge/Extend, addVis/consumeVis, getConsumptionModifier, onItemUseFirst — все отсутствуют |
| 4 | **ItemWandCasting** — IArchitect | 🔴 HIGH | Отсутствует implements IArchitect |
| 5 | **ItemThaumometer** — onItemRightClick | 🔴 CRITICAL | Скан цели не реализован. Нет ScanManager, packets, GUI |
| 6 | **ItemThaumonomicon** — onItemRightClick | 🔴 CRITICAL | GUI не открывается |
| 7 | **ItemHandMirror** — onItemRightClick | 🔴 CRITICAL | Mirror linking/teleport не реализован |
| 8 | **ItemResonator** — onItemRightClick | 🔴 CRITICAL | Vis scanning не реализован |
| 9 | **ItemSanityChecker** — onItemRightClick | 🔴 HIGH | Warp display не реализован |
| 10 | **ItemRingRunic / AmuletRunic / GirdleRunic** | 🔴 CRITICAL | onWornTick пуст — runic shielding не заряжается |
| 11 | **ItemGirdleHover** | 🔴 CRITICAL | onWornTick пуст — hover effect не работает |
| 12 | **ItemAmuletVis** | 🔴 CRITICAL | addVis пустой — vis storage на амулете не работает |
| 13 | **ItemHoverHarness** | 🔴 CRITICAL | onArmorTick пуст с `// Flight/hover mechanics - TBD` |
| 14 | **ItemPrimalCrusher** | 🔴 CRITICAL | extends ItemSword вместо ItemTool — неправильная иерархия. Нет area-of-effect mining |
| 15 | **ItemVoidSword / Void Armor / Void Tools** | 🔴 HIGH | Нет IWarpingGear, onUpdate self-damage, wither effect |
| 16 | **ItemBowBone** | 🔴 HIGH | onPlayerStoppedUsing пуст — лук не стреляет |
| 17 | **ItemElementalAxe** | 🔴 HIGH | Tree-felling не работает (oreDictLogs определён но не используется) |
| 18 | **ItemFortressArmor / VoidRobeArmor** | 🔴 HIGH | Нет IGoggles/IRevealer, ISpecialArmor, overlay textures |
| 19 | **ItemBootsTraveller** | 🟡 MEDIUM | Hover effect упрощён: нет step-height reset, jump boost, water movement |
| 20 | **Все tools/armor** | 🔴 HIGH | getIsRepairable() возвращает false — вещи не чинятся в anvil |
| 21 | **Все items** | 🟡 MEDIUM | Нет addInformation tooltips, нет registerModels (все с missing texture) |

### Phase 6r — Entities & AI Remediation
**Сфальсифицировано**: 39/44 AI классов с shouldExecute=false, все
6 боссов без AI/атак, все 24 hostile mobs без AI задач, 11/12
projectiles без урона, 17 классов без звуковых событий.

| # | Компонент | Severity | Что отсутствует |
|---|-----------|----------|-----------------|
| 1 | **39/44 AI classes** | 🔴 CRITICAL | shouldExecute() возвращает false — все golem AI, combat AI, pech AI не работают |
| 2 | **6 boss mobs** | 🔴 CRITICAL | Нет AI tasks, нет специальных атак, нет дропа, нет boss bar |
| 3 | **24 hostile mobs** | 🔴 CRITICAL | Нет AI task registration — стоят и ничего не делают |
| 4 | **11/12 projectiles** | 🔴 CRITICAL | onImpact/setDead() только — нет урона, нет эффектов |
| 5 | **EntityWisp** | 🔴 HIGH | Нет lightning zap attack, нет дропа ItemWispEssence |
| 6 | **EntityWatcher** | 🔴 HIGH | Нет gaze attack |
| 7 | **EntityPech** | 🔴 HIGH | Нет pech blast, NBT не сохраняет PECH_TYPE/ANGRY, sound=null |
| 8 | **EntityThaumicSlime** | 🔴 HIGH | Нет slime split mechanics |
| 9 | **EntityInhabitedZombie** | 🔴 HIGH | Нет spawn EntityEldritchCrab on death |
| 10 | **EntityCultistPortal** | 🔴 HIGH | Нет spawn cultists — вся сущность не работает |
| 11 | **EntityEldritchGuardian** | 🔴 HIGH | Нет EldritchOrb, нет screech attack |
| 12 | **EntityCultistCleric** | 🔴 HIGH | Нет projectile attack |
| 13 | **14 champion modifiers** | 🟡 MEDIUM | Все showFX() — TODO client FX. ChampionModMighty — стаб (нет эффекта). ChampionModWarp — не накладывает warp |
| 14 | **18 entity classes** | 🟡 MEDIUM | Sound events возвращают null |
| 15 | **ContainerGolem/Trunk/Pech** | 🔴 HIGH | canInteractWith() возвращает false — GUIs не открываются |
| 16 | **InventoryTrunk / InventoryPech** | 🔴 HIGH | IInventory stubs — getStackInSlot возвращает EMPTY |
| 17 | **ItemSpawnerEgg** | 🔴 HIGH | Не зарегистрирован в ConfigItems |
| 18 | **EntityAspectOrb / FallingTaint / GolemBobber** | 🟡 MEDIUM | entityInit/NBT empty |
| 19 | **EntityPech NBT** | 🟡 MEDIUM | PECH_TYPE, ANGRY не сохраняются |
| 20 | **EntityFrostShard** | 🟡 MEDIUM | DAMAGE, FROSTY не сохраняются в NBT |

### Phase 3r-6r Priority (что делать в первую очередь)

| Priority | Компонент | Фаза | Причина |
|----------|-----------|------|---------|
| 🔴 P0 | **Block metadata** (18 blocks) | 4r | Все блоки с subtypes ставятся неверно |
| 🔴 P0 | **PacketHandler** (39 stubs) | 3r | Вся сетевая коммуникация сломана |
| 🔴 P0 | **Harvest levels** (19 blocks) | 4r | Блоки не ломаются |
| 🔴 P1 | **TileNode** (update) | 4r | Aura nodes не работают — vis не регенерирует |
| 🔴 P1 | **TileInfusionMatrix** | 4r | Infusion altar не работает |
| 🔴 P1 | **TileCrucible** | 4r | Alchemy не работает |
| 🔴 P1 | **TileTube*** (6 files) | 4r | Essentia pipes не существуют |
| 🔴 P1 | **39/44 AI classes** | 6r | Golems и mobs не двигаются |
| 🔴 P1 | **11/12 projectiles** | 6r | Снаряды не наносят урон |
| 🔴 P1 | **10 focus items** | 5r | Фокусы жезлов не работают |
| 🔴 P1 | **5 relics** | 5r | Реликвии не работают |
| 🔴 P1 | **4 baubles** | 5r | Баублы не работают |
| 🔴 P1 | **6 boss mobs** | 6r | Боссы не имеют AI |
| 🔴 P1 | **24 hostile mobs** | 6r | Мобы не имеют AI |
| 🔴 P1 | **5 enchantments** | 3r | Неправильные типы |
| 🔴 P1 | **WarpEvents** | 3r | Warp механика отсутствует |
| 🔴 P2 | **ResearchManager** (методы) | 3r | Research table не работает |
| 🔴 P2 | **Wand vis cost** | 3r | Baubles/armor discounts отсутствуют |
| 🔴 P2 | **Potion effects** (3 potions) | 3r | Неправильные эффекты |
| 🔴 P2 | **EventHandlerEntity** (11 handlers) | 3r | События не обрабатываются |
| 🔴 P2 | **EventHandlerRunic** (3 handlers) | 3r | Runic shielding не работает |
| 🔴 P2 | **17 missing TE files** | 4r | Не созданы |
| 🟡 P3 | **Sound events** (18 entities) | 6r | Нет звуков |
| 🟡 P3 | **Champion modifier FX** (14) | 6r | Нет визуальных эффектов |
| 🟡 P3 | **getIsRepairable** (all tools/armor) | 5r | Вещи не чинятся |
| 🟢 P4 | **Tooltips / model registration** | 5r | Косметика |
| 🟢 P4 | **getRarity** | 5r | Косметика |
