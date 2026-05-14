# Stage 7 — Gap-анализ и план закрытия

## 1. Цель фазы

Stage 7 закрывает серверную часть генерации мира и Outer Lands для порта Thaumcraft 4.2.3.5 на Forge 1.12.2: биомы, деревья, руды, структуры, измерение Outer Lands, лабиринт, порталы, телепортацию, сохранение `labyrinth.dat`, world data и связанные регистрации/config. По PRD цель фазы: `docs/PRD.md:341-345`; текущий статус PRD прямо говорит, что baseline Outer Lands есть, но фаза не закрыта и требует runtime evidence: `docs/PRD.md:347-350`.

Фаза не может считаться завершенной сейчас: ниже остаются blocker/high gaps, а PRD требует, чтобы runtime generation работал в новом мире и косметические deferrals были документированы: `docs/PRD.md:361-364`.

## 2. Scope фазы

- Биомы Stage 7: `src/main/java/thaumcraft/common/lib/world/biomes/BiomeMagicalForest.java`, `src/main/java/thaumcraft/common/lib/world/biomes/BiomeTaint.java`, `src/main/java/thaumcraft/common/lib/world/biomes/BiomeEerie.java`, `src/main/java/thaumcraft/common/lib/world/biomes/BiomeEldritch.java`, `src/main/java/thaumcraft/common/lib/world/biomes/BiomeHandler.java`.
- Регистрация биомов и измерения: `src/main/java/thaumcraft/common/Thaumcraft.java:124-130`, `src/main/java/thaumcraft/common/Thaumcraft.java:197-201`, `src/main/java/thaumcraft/common/Thaumcraft.java:291-298`, `src/main/java/thaumcraft/common/config/Config.java:308-330`.
- Config Stage 7: biome IDs/weights, Outer Lands dimension id, and worldgen toggles: `src/main/java/thaumcraft/common/config/Config.java:41-58`, `src/main/java/thaumcraft/common/config/Config.java:78-95`, `src/main/java/thaumcraft/common/config/Config.java:230-246`.
- Surface/world generation: `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java` and generators under `src/main/java/thaumcraft/common/lib/world/WorldGen*.java`.
- Outer Lands provider/chunk generation: `src/main/java/thaumcraft/common/lib/world/dim/WorldProviderOuter.java`, `src/main/java/thaumcraft/common/lib/world/dim/ChunkProviderOuter.java`.
- Maze model/generation/persistence: `src/main/java/thaumcraft/common/lib/world/dim/Cell.java`, `src/main/java/thaumcraft/common/lib/world/dim/CellLoc.java`, `src/main/java/thaumcraft/common/lib/world/dim/MazeGenerator.java`, `src/main/java/thaumcraft/common/lib/world/dim/MazeThread.java`, `src/main/java/thaumcraft/common/lib/world/dim/MazeHandler.java`.
- Outer Lands room templates: `src/main/java/thaumcraft/common/lib/world/dim/GenCommon.java`, `src/main/java/thaumcraft/common/lib/world/dim/GenPortal.java`, `src/main/java/thaumcraft/common/lib/world/dim/GenPassage.java`, `src/main/java/thaumcraft/common/lib/world/dim/Gen2x2.java`, `src/main/java/thaumcraft/common/lib/world/dim/GenBossRoom.java`, `src/main/java/thaumcraft/common/lib/world/dim/GenKeyRoom.java`, `src/main/java/thaumcraft/common/lib/world/dim/GenNestRoom.java`, `src/main/java/thaumcraft/common/lib/world/dim/GenLibraryRoom.java`.
- Portals/teleport safety: `src/main/java/thaumcraft/common/blocks/BlockEldritchPortal.java`, `src/main/java/thaumcraft/common/lib/world/dim/TeleporterThaumcraft.java`, generated portal room at `src/main/java/thaumcraft/common/lib/world/dim/GenPortal.java:135-138`.
- Persistence/world events: `src/main/java/thaumcraft/common/lib/events/EventHandlerWorld.java:45-57`, boss world data at `src/main/java/thaumcraft/common/lib/world/dim/MapBossData.java`.
- Scenarios that should work after closure: new Overworld generation creates ores, aura nodes, magical/taint/eerie/eldritch biome behavior, Greatwood/Silverwood and plants; normal Greatwood and spider Greatwood variants generate with their reference contents; surface structures generate complete Eldritch rings, hilltop wisp altars, and mound/barrow loot/spawner layouts; Eldritch rings create matching maze reservations and saved `labyrinth.dat`; entering an Eldritch portal reaches a safe Outer Lands portal cell; Outer Lands chunks populate rooms/passages/boss/key/nest/library; saving/reloading preserves maze cells and generated rooms; `/locate`-style structure query hooks do not crash.

## 3. Источники сравнения

- PRD Stage 7 scope and risk lines: `docs/PRD.md:341-364`.
- Current implementation: `src/main/java/thaumcraft/common/lib/world/**`, `src/main/java/thaumcraft/common/lib/world/dim/**`, `src/main/java/thaumcraft/common/lib/world/biomes/**`, `src/main/java/thaumcraft/common/blocks/BlockEldritchPortal.java`, `src/main/java/thaumcraft/common/config/Config.java`, `src/main/java/thaumcraft/common/Thaumcraft.java`, `src/main/java/thaumcraft/common/lib/events/EventHandlerWorld.java`.
- Reference classes are `.class` files under `thaumcraft_src/**`; decompiled with `cfr --silent true ...` for Stage 7 classes. Key reference paths: `thaumcraft_src/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.class`, `thaumcraft_src/thaumcraft/common/lib/world/WorldGenEldritchRing.class`, `thaumcraft_src/thaumcraft/common/lib/world/WorldGenHilltopStones.class`, `thaumcraft_src/thaumcraft/common/lib/world/WorldGenMound.class`, `thaumcraft_src/thaumcraft/common/lib/world/WorldGenGreatwoodTrees.class`, `thaumcraft_src/thaumcraft/common/lib/world/dim/ChunkProviderOuter.class`, `thaumcraft_src/thaumcraft/common/lib/world/dim/WorldProviderOuter.class`, `thaumcraft_src/thaumcraft/common/lib/world/dim/MazeHandler.class`, `thaumcraft_src/thaumcraft/common/lib/world/dim/MazeGenerator.class`, `thaumcraft_src/thaumcraft/common/lib/world/dim/MazeThread.class`, `thaumcraft_src/thaumcraft/common/lib/world/dim/TeleporterThaumcraft.class`, `thaumcraft_src/thaumcraft/common/blocks/BlockEldritchPortal.class`, `thaumcraft_src/thaumcraft/common/lib/world/biomes/BiomeGenMagicalForest.class`, `thaumcraft_src/thaumcraft/common/lib/world/biomes/BiomeGenTaint.class`, `thaumcraft_src/thaumcraft/common/lib/world/biomes/BiomeGenEerie.class`, `thaumcraft_src/thaumcraft/common/lib/world/biomes/BiomeGenEldritch.class`.
- Lightweight checks used for this static analysis: `git status --short`; file discovery under `thaumcraft_src/thaumcraft/common/lib/world`; `cfr --silent true` on selected Stage 7 reference classes, including `WorldGenEldritchRing`, `WorldGenHilltopStones`, `WorldGenMound`, `WorldGenGreatwoodTrees`, and `ThaumcraftWorldGenerator`; `rg`-backed searches via tools for `Outer`, `Eldritch`, `Biome`, `Dimension`, `Teleporter`, `WorldSavedData`, `Maze`, `TODO`.
- No build, server smoke, or client smoke validation was run; this document is static gap analysis only.

## 4. Текущее состояние Stage 7

- Baseline exists: world generator instance is registered at `src/main/java/thaumcraft/common/Thaumcraft.java:124-126`; biomes are initialized and registered at `src/main/java/thaumcraft/common/Thaumcraft.java:128-130` and `src/main/java/thaumcraft/common/Thaumcraft.java:291-298`; Outer Lands dimension type/dimension are registered at `src/main/java/thaumcraft/common/Thaumcraft.java:197-201`.
- Outer Lands provider/chunk generator exists: provider uses a single Eldritch biome and returns `ChunkProviderOuter`: `src/main/java/thaumcraft/common/lib/world/dim/WorldProviderOuter.java:38-48`; chunk provider makes empty chunks and populates maze rooms through `MazeHandler.generateEldritch`: `src/main/java/thaumcraft/common/lib/world/dim/ChunkProviderOuter.java:27-39`, `src/main/java/thaumcraft/common/lib/world/dim/ChunkProviderOuter.java:50-67`.
- Maze persistence exists and uses original file names/keys (`labyrinth.dat`, `labyrinth.dat_old`, `Data`, `cells`, `x`, `z`, `cell`): `src/main/java/thaumcraft/common/lib/world/dim/MazeHandler.java:43-116`; it is loaded/saved on Overworld world load/save: `src/main/java/thaumcraft/common/lib/events/EventHandlerWorld.java:45-57`.
- Several current room/template classes remain partial even after the 2026-05-14 urn/crate/slab and `BlockEldritch` content checkpoints; full traversal/runtime validation is still missing.
- Current surface generator is much narrower than the reference. It skips Outer Lands in the general world generator at `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:257-258`; generates ores with modern `WorldGenMinable` at `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:275-285`; only generates trees when the sampled biome equals Magical Forest at `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:288-292`; only generates structures in dim 0 at `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:294-297`; does not call any wild-aura-node generation path from `generate(...)` despite `Config.genAura`/`Config.regenAura` existing.
- Current Overworld structure/tree generators still contain gameplay placeholders in hilltop altar, mound/barrow, and spider Greatwood generation. `WorldGenEldritchRing` is no longer the old obsidian-plus-portal placeholder after the 2026-05-14 ring checkpoint, but it still needs runtime evidence and fuller altar lifecycle behavior.
- Portal entry now uses the reference-like ticked `TileEldritchPortal` flow and the safer `TeleporterThaumcraft` search/cache placement. This remains a partial GAP-3 closure until manual traversal evidence proves arrival/return beside generated portal rooms.

## 5. Gap list

### GAP-1: Surface worldgen pipeline is missing original aura, totem, nether, and structure-node behavior

**Статус:** частично реализовано
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:253-298`
- `src/main/java/thaumcraft/common/lib/events/EventHandlerWorld.java:74-103`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.class`

**Что не совпадает:**
Reference `ThaumcraftWorldGenerator` has a `worldGeneration(random, chunkX, chunkZ, world, newGen)` path that handles Outer Lands, Nether, End skip, surface generation, and generation markers. It calls `generateWildNodes`, `generateTotem`, `generateVegetation`, `generateOres`, and structure-node placement near scattered features. Reference `generateWildNodes` gates on `Config.genAura`, `Config.nodeRarity`, and existing structure-node generation before creating a random node with `createRandomNodeAt(..., false, false, false)`. Current `generate(...)` has no `worldGeneration` method, no `newGen` parameter, no nether branch, no wild node generation despite `Config.genAura`, no totem generation, and no `MapGenScatteredFeature` structure-node behavior. Current `createRandomNodeAt(...)` exists at `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:97-250`, but static RECON found no normal worldgen caller for wild nodes; natural node creation is therefore absent except indirect Silverwood knot attempts.

**Что нужно доделать:**
Reintroduce the reference worldgen control flow with a 1.12.2 API adaptation for fresh-world generation, preserving config toggles, dimension/biome blacklist semantics, flat-world skip behavior, nether node/totem behavior, surface aura generation, structure-node generation, vegetation, ores, structures, and chunk dirty marking where appropriate.

**Как доделать:**
- Port `worldGeneration(Random, int, int, World, boolean)` into `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java`.
- Port/adapt `generateWildNodes`, `generateTotem`, `generateSurface`, `generateVegetation`, `generateOres`, `generateNether` from `thaumcraft_src/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.class`.
- Wire `Config.genAura`, `Config.regenAura`, and `Config.nodeRarity` into the restored `generateWildNodes` path; verify that normal `blockAiry`/`TileNode` aura nodes can be created independently of Silverwood trees.
- Preserve fresh-world generation config toggles such as `Config.genAura`, `Config.genStructure`, `Config.genTrees`, and ore toggles from `src/main/java/thaumcraft/common/config/Config.java:230-246`.
- Keep Stage 7 changes isolated from unrelated entity/render/client phases.

**Критерии приемки:**
- [ ] New Overworld chunks can generate aura nodes when `Config.genAura=true` and `Config.nodeRarity` allows it.
- [ ] Wild aura nodes still appear when tree generation is disabled but aura generation is enabled, proving nodes do not depend on Silverwood generation.
- [ ] Structure-nearby aura node placement and random wild node placement both use reference-like `auraGen` suppression so a chunk does not duplicate nodes unexpectedly.
- [ ] Nether chunks can generate the original node/totem subset and End chunks remain skipped.
- [ ] Flat worlds skip structures where the reference skipped them.
- [ ] `./scripts/dev.sh smoke-server` reaches ready state after generating a new world with Thaumcraft worldgen enabled.

**Риски / зависимости:**
Dependency: Stage 3 node/tile behavior must be stable enough for generated aura nodes to work. Runtime validation is required because compile success cannot prove worldgen parity.

### GAP-2: Overworld Eldritch ring generation does not match reference maze bootstrap

**Статус:** частично реализовано
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:357-363`
- `src/main/java/thaumcraft/common/lib/world/WorldGenEldritchRing.java:12-41`
- `src/main/java/thaumcraft/common/lib/world/dim/MazeThread.java:7-48`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.class`
- `thaumcraft_src/thaumcraft/common/lib/world/WorldGenEldritchRing.class`
- `thaumcraft_src/thaumcraft/common/lib/world/dim/MazeThread.class`

**Что не совпадает:**
Reference `ThaumcraftWorldGenerator` creates Eldritch rings from the surface structure branch with `randPosY = getHeight(...) - 9`, only attempts the structure when that value is below sea level, then calls `WorldGenEldritchRing.LocationIsValidSpawn(...)` before placing blocks. It uses random odd maze width/height `11 + random.nextInt(6) * 2`, assigns those dimensions to the `WorldGenEldritchRing`, creates a dark node, and starts `new MazeThread(chunkX, chunkZ, w, h, random.nextLong())`. Reference `WorldGenEldritchRing` places cosmetic-solid/obsidian support blocks, eldritch altar/cap/obelisk metas, banners, and altar spawner state. The 2026-05-14 ring checkpoint ports this server-side ring layout, surface validation, dark node creation, and odd-dimension maze bootstrap. The follow-up altar checkpoint ports the server-side altar spawner tick and `checkForMaze()` helper. Remaining mismatch is runtime/manual evidence and the portal-opening lifecycle that consumes altar open/eye state.

**Что нужно доделать:**
Runtime-validate the reference-like ring structure and maze bootstrap added by the 2026-05-14 checkpoint, then finish the altar lifecycle that makes the generated ring usable for normal progression.

**Как доделать:**
- Validate the new ring layout, altar spawns, and maze bootstrap in a fresh runtime world.
- Port the missing `TileEldritchAltar` portal-opening lifecycle in a later tile behavior checkpoint.
- Verify `MazeHandler.mazesInRange(...)` prevents overlapping mazes with the same radius semantics as reference.

**Критерии приемки:**
- [ ] A generated Overworld Eldritch ring produces non-empty maze cells in `MazeHandler.labyrinth` around the expected chunk center.
- [ ] Maze width/height are odd values in the reference range rather than a fixed 32x32 grid.
- [ ] Ring generation creates the reference dark node/totem behavior.
- [ ] Eldritch rings fail generation on water/air/invalid surfaces and do not spawn at ocean surface with missing support blocks.
- [ ] Generated Eldritch ring contains the reference altar/cap/obelisk/banner/spawner-capable block layout, not only obsidian plus portal.
- [ ] Saving after ring generation writes expected `labyrinth.dat` cell entries.
- [ ] Manual new-world test can find an Eldritch ring and enter the associated Outer Lands maze.

**Риски / зависимости:**
Race risk: `MazeThread` is asynchronous, so saving/teleporting immediately after ring generation can race with maze population. This needs runtime validation or synchronization rules.

**Checkpoint 2026-05-14 — Eldritch ring and maze bootstrap:**
`WorldGenEldritchRing` now receives `chunkX`, `chunkZ`, `width`, and `height`, validates the reference surface points, rejects overlapping maze reservations through `MazeHandler.mazesInRange(...)`, replaces the old obsidian-circle-plus-portal placeholder with the original support pad/altar/cap/obelisk/banner layout, and preserves altar spawner metadata. The surface structure branch now uses reference-like `getHeight(...) - 9`/sea-level gating, random odd maze dimensions in the `11..21` range, dark node creation above the ring altar, and `MazeThread(chunkX, chunkZ, width, height, random.nextLong())`. `TileEldritchAltar` and `TileBanner` gained the minimal persistent state needed by this generated structure.

Remaining GAP-2 limits after this checkpoint: runtime generation has not been observed in a fresh world, async maze race/saving behavior is still unproven, and altar spawn/open/portal lifecycle remains a separate Stage 7 tile-behavior gap.

**Checkpoint 2026-05-14 — Eldritch altar spawner lifecycle:**
`TileEldritchAltar` now ticks server-side when marked as a spawner, persists the reference `spawner`, `spawnedClerics`, `spawntype`, `eyes`, and `open` fields, spawns ritual clerics before guard reinforcements for spawn type `0`, spawns Eldritch Guardians for spawn type `1`, assigns home positions/persistence, and exposes the reference-like `checkForMaze()` helper that starts a new `MazeThread` if no maze exists around the altar.

Remaining altar limits after this checkpoint: portal-opening behavior that uses eyes/open state is still not implemented, and cultist/guardian spawn behavior has not been observed in a runtime world.

### GAP-3: Portal teleporter is unsafe and does not search/cache destination portals

**Статус:** частично реализовано
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/blocks/BlockEldritchPortal.java`
- `src/main/java/thaumcraft/common/tiles/TileEldritchPortal.java`
- `src/main/java/thaumcraft/common/lib/world/dim/TeleporterThaumcraft.java:7-30`
- `src/main/java/thaumcraft/common/lib/world/dim/GenPortal.java:135-138`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/world/dim/TeleporterThaumcraft.class`
- `thaumcraft_src/thaumcraft/common/blocks/BlockEldritchPortal.class`
- `thaumcraft_src/thaumcraft/common/lib/world/dim/GenPortal.class`

**Что не совпадает:**
Reference `TeleporterThaumcraft` searches within 128 blocks for `ConfigBlocks.blockEldritchPortal`, caches portal positions by chunk/dimension key, places the entity beside the found portal with zero velocity, falls back to safe top Y in dimension 1-style logic, and expires cache entries. The 2026-05-14 teleporter checkpoint ports the search/cache/velocity-reset placement path and removes fixed `y=60` placement.

The reference `BlockEldritchPortal` is a pass-through portal block and does not own dimension transfer. The reference `TileEldritchPortal` scans nearby `EntityPlayerMP` instances every 5 ticks, uses the player portal cooldown, transfers to `Config.dimensionOuterId` or Overworld, and grants `ENTEROUTER` on first entry. The 2026-05-14 portal trigger checkpoint ports that server-side ownership to the 1.12.2 tile and removes collision-driven transfer from the block. Remaining mismatch is unvalidated portal/maze runtime behavior: the code still needs an in-world traversal proof that an Overworld portal reaches an already generated Outer Lands portal room and returns safely.

**Что нужно доделать:**
Verify the correct trigger for entering/leaving Outer Lands and runtime-test the 1.12.2 `Teleporter` adaptation. The player should arrive adjacent to an existing Eldritch portal room, not a fallback position, when a generated portal room exists.

**Как доделать:**
- Verify portal search radius, cache, velocity reset, and fallback placement in `src/main/java/thaumcraft/common/lib/world/dim/TeleporterThaumcraft.java` through runtime/manual evidence.
- Validate the ticked `TileEldritchPortal` transfer path through runtime/manual evidence.
- Confirm the new `server.getWorld(...)` null guards are sufficient in a failed/missing dimension setup.
- Ensure return teleport to dim 0 uses a valid portal/ground placement in runtime.

**Критерии приемки:**
- [ ] Entering an Overworld Eldritch portal places the player adjacent to an Outer Lands portal block generated by `GenPortal`.
- [ ] Returning from Outer Lands places the player at a safe Overworld portal/ground position.
- [ ] Player velocity is cleared after teleport.
- [ ] Rapid portal collisions do not loop or trap the player.
- [ ] Server smoke and manual teleport scenario show no void fall, suffocation, `NullPointerException`, or missing dimension crash.

**Риски / зависимости:**
Depends on GAP-2 and GAP-4 because the teleporter can only find safe portals if maze cells and portal rooms exist before teleport. High risk of player-loss bugs without manual validation.

**Checkpoint 2026-05-14:**
`TeleporterThaumcraft` no longer places entities at fixed `y=60`. It now searches the target world for the nearest `ConfigBlocks.blockEldritchPortal` within 128 blocks, caches the portal position through the Forge 1.12.2 teleporter cache, zeros motion, and places the entity beside the portal. If no portal can be found, it falls back to the target world's safe top position at the incoming X/Z instead of the old hardcoded Y. `BlockEldritchPortal` now also no-ops if the requested target `WorldServer` is unavailable instead of constructing a teleporter with `null`.

Remaining GAP-3 limits after this checkpoint were trigger ownership and manual portal traversal/save-reload validation; trigger ownership is handled by the follow-up checkpoint below.

**Checkpoint 2026-05-14 — portal trigger ownership:**
`TileEldritchPortal` now implements the reference-like ticked player scan. Every 5 ticks on the server it scans a grown one-block AABB, ignores mounted/ridden players, respects `EntityPlayerMP.timeUntilPortal` with the original 100-tick cooldown behavior, transfers to `Config.dimensionOuterId` or Overworld using `TeleporterThaumcraft`, null-guards missing target worlds, and grants `ENTEROUTER` through the current `ResearchManager` when entering the Outer Lands. `BlockEldritchPortal` no longer performs collision-driven dimension changes; it is now pass-through/non-replaceable/unbreakable and leaves transfer ownership to the tile entity.

Remaining GAP-3 limits after this checkpoint: the code path now matches reference ownership more closely, but no manual portal traversal/save-reload validation has been run. GAP-3 also still depends on GAP-2/GAP-4 because the teleporter can only prove arrival near an existing portal block after ring/maze/portal-room generation is validated.

### GAP-4: Outer Lands room templates are partial and contain TODO placeholders

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/world/dim/GenCommon.java:20-40`
- `src/main/java/thaumcraft/common/lib/world/dim/GenCommon.java:251-314`
- `src/main/java/thaumcraft/common/lib/world/dim/GenNestRoom.java:116-128`
- `src/main/java/thaumcraft/common/lib/world/dim/GenLibraryRoom.java:126-130`
- `src/main/java/thaumcraft/common/lib/world/dim/Gen2x2.java:10-250`
- `src/main/java/thaumcraft/common/lib/world/dim/GenBossRoom.java:23-63`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/world/dim/GenCommon.class`
- `thaumcraft_src/thaumcraft/common/lib/world/dim/GenPortal.class`
- `thaumcraft_src/thaumcraft/common/lib/world/dim/GenPassage.class`
- `thaumcraft_src/thaumcraft/common/lib/world/dim/Gen2x2.class`
- `thaumcraft_src/thaumcraft/common/lib/world/dim/GenBossRoom.class`
- `thaumcraft_src/thaumcraft/common/lib/world/dim/GenKeyRoom.class`
- `thaumcraft_src/thaumcraft/common/lib/world/dim/GenNestRoom.class`
- `thaumcraft_src/thaumcraft/common/lib/world/dim/GenLibraryRoom.class`

**Что не совпадает:**
Initial audit found temporary replacements for original loot urns/crates and slabs in `GenCommon`, `GenNestRoom`, and `GenLibraryRoom`. The direct `blockLootUrn`/`blockLootCrate`/`blockSlabStone` substitutions are now closed by the 2026-05-14 content checkpoints below. Reference room generators still include broader block placements, loot containers, spawners, slabs, urns/crates, and boss room decoration that require full room-template audit before GAP-4 can close.

**Что нужно доделать:**
Complete room-template parity and remove TODO placeholders inside Stage 7. If some original blocks are not ported, either port the missing blocks/resources within the appropriate stage boundary or explicitly defer only cosmetic/non-blocking substitutions after proving gameplay works.

**Как доделать:**
- Compare every `Gen*.java` current file against its reference `.class` counterpart.
- Restore `blockLootUrn`, `blockLootCrate`, `blockSlabStone` or the 1.12.2 equivalent if those blocks are expected by Stage 7 rooms.
- Restore chest loot, spawner, trap, lock, altar, key, boss, and decoration placements exactly enough for gameplay routes.
- Remove TODOs in Stage 7 files or mark a documented cosmetic deferral only if not gameplay-affecting.

**Критерии приемки:**
- [ ] `rg -n "TODO|placeholder|Replace with" src/main/java/thaumcraft/common/lib/world src/main/java/thaumcraft/common/lib/world/dim` returns no Stage 7 gameplay TODOs.
- [ ] Portal, passage, key, nest, library, and boss room cells generate traversable rooms matching reference dimensions/connectors.
- [ ] Loot urn/crate/chest and slab substitutions are resolved or explicitly documented as cosmetic-only with no progression impact.
- [ ] Key room spawns the key item and guardians, and boss room locks/doors orient correctly.
- [ ] Manual Outer Lands maze run can traverse from portal to key/boss-related rooms without broken doors or missing floor/ceiling blocks.

**Риски / зависимости:**
Dependency: some block/item classes may belong to content/registration work outside Stage 7. If missing blocks are not available, the Stage 7 plan must label those as dependencies instead of silently using vanilla placeholders.

**Checkpoint 2026-05-14:**
`BlockLoot`/`BlockLootItem`, `blockLootUrn`, and `blockLootCrate` are now present and registered. `GenCommon` now places `blockLootUrn` instead of vanilla stone for the urn slot, and `GenNestRoom` now uses reference-like rarity-dependent `blockLootCrate` or `blockLootUrn` instead of vanilla chest placeholders. Original urn/crate textures were copied from `thaumcraft_src/assets/`; temporary model JSONs are present only as Forge 1.12.2 resource fallbacks until Stage 8 renderer parity work.

`blockCosmeticSlabStone`/`blockCosmeticDoubleSlabStone` are now present and registered as Forge 1.12.2 slabs. `GenLibraryRoom` now uses `ConfigBlocks.blockSlabStone.getStateFromMeta(meta)` for the reference eldritch slab metas instead of the vanilla `Blocks.DOUBLE_STONE_SLAB` placeholder. Reference textures `arcane_stone.png` and `es_1.png` were copied from `thaumcraft_src/assets/`; temporary model JSONs are present only as Forge 1.12.2 resource fallbacks until Stage 8 renderer parity work.

`BlockEldritch` now supports the reference `0..10` meta range and creates tile entities for metas `0` altar, `1` obelisk, `3` cap, `8` lock, `9` crab spawner, and `10` trap. This closes the direct room metadata/TE instantiation blocker where generated door locks, crab spawners, traps, and meta 5 blocks were clamped to meta `4`. Full tile behavior and renderer parity remain separate Stage 7/8 work.

**Checkpoint 2026-05-14 — Eldritch crab spawner and trap server ticks:**
`TileEldritchCrabSpawner` now restores the reference server timer, player activation radius, nearby-crab cap, facing-based spawn position/motion, unhelmed crab spawning, spawn warning block event, and `facing` custom NBT sync. `TileEldritchTrap` now restores the reference periodic server pulse that targets nearby players, applies magic damage, and has a 50% chance to add temporary warp. Client vent particles and trap zap packets remain Stage 8 FX work because the current `PacketFXBlockZap` is still only a shell.

Remaining GAP-4 limits after this checkpoint: other room templates still need reference audit, full room traversal has not been runtime/manual validated, `TileEldritchLock`/boss-room lifecycle remains open, and loot-table distribution remains dependent on Stage 9/content registration.

### GAP-5: Outer Lands generation has no runtime smoke/manual validation evidence

**Статус:** требует проверки  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/world/dim/WorldProviderOuter.java:38-48`
- `src/main/java/thaumcraft/common/lib/world/dim/ChunkProviderOuter.java:27-67`
- `src/main/java/thaumcraft/common/lib/world/dim/MazeHandler.java:127-153`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/world/dim/WorldProviderOuter.class`
- `thaumcraft_src/thaumcraft/common/lib/world/dim/ChunkProviderOuter.class`
- `thaumcraft_src/thaumcraft/common/lib/world/dim/MazeHandler.class`

**Что не совпадает:**
Static comparison shows a plausible 1.12.2 adaptation, but no evidence that a new world reaches ready state, an Outer Lands dimension loads, chunks populate, or maze rooms generate without crashes. PRD explicitly lists this as a known risk: `docs/PRD.md:352-356`. Current `ChunkProviderOuter.populate` calls `biome.decorate` and then `MazeHandler.generateEldritch`: `src/main/java/thaumcraft/common/lib/world/dim/ChunkProviderOuter.java:61-65`, while the reference `ChunkProviderOuter` invokes biome decoration and Outer room generation via the original generator/hook pipeline. This adaptation needs runtime proof.

**Что нужно доделать:**
Run and document dedicated runtime scenarios after code gaps are closed: server smoke, new world generation, entering Outer Lands, forced chunk population around a maze, save/reload, and portal return.

**Как доделать:**
- Run `./scripts/dev.sh compileJava` after implementation changes.
- Run `./scripts/dev.sh smoke-server` for common/server generation changes.
- Add a manual Stage 7 smoke checklist to the final implementation report: create new world, locate/generate ring, enter portal, traverse several maze chunks, save/reload, re-enter, return to Overworld.
- Inspect `run/smoke-server.log` for crash markers listed in `AGENTS.md`.

**Критерии приемки:**
- [ ] Server smoke reaches `Done (` with no new crash report.
- [ ] New world generates Thaumcraft ores/trees/structures without chunk population crash.
- [ ] Outer Lands dimension loads and populates at least portal, passage, key/nest/library/boss room cell types.
- [ ] Save/reload preserves maze cells and previously generated rooms.
- [ ] No `NoSuchMethodError`, `NoSuchFieldError`, `ClassCastException`, `NullPointerException`, or missing tile entity crash appears during Stage 7 scenarios.

**Риски / зависимости:**
Runtime validation may expose Stage 6 entity issues in guardian/boss/spawner rooms; those should be labeled as dependencies if entity behavior blocks Stage 7 room verification.

### GAP-6: Maze persistence and async generation can race with save/load and teleport

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/world/dim/MazeHandler.java:14-41`
- `src/main/java/thaumcraft/common/lib/world/dim/MazeHandler.java:70-116`
- `src/main/java/thaumcraft/common/lib/world/dim/MazeThread.java:15-47`
- `src/main/java/thaumcraft/common/lib/events/EventHandlerWorld.java:45-57`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/world/dim/MazeHandler.class`
- `thaumcraft_src/thaumcraft/common/lib/world/dim/MazeThread.class`

**Что не совпадает:**
Current NBT format matches the reference at a high level, but generation remains asynchronous through the surface structure branch's `new Thread(new MazeThread(...)).start()` and only positive cells are saved: `src/main/java/thaumcraft/common/lib/world/dim/MazeHandler.java:57-66`. The reference also uses async `MazeThread`, and the 2026-05-14 ring checkpoint restored odd reference-like dimensions/seeds. Remaining risk is runtime ordering: the maze cells must exist before an altar/portal entry path allows the player to transfer. Load/save is tied to dimension 0 only: `src/main/java/thaumcraft/common/lib/events/EventHandlerWorld.java:45-57`; this matches original file location but needs validation in integrated server lifecycle.

**Что нужно доделать:**
Keep the original file format but make Stage 7 scenarios safe: maze cells must exist before portal use, saving must not miss just-created mazes, and reload must rehydrate `MazeHandler.labyrinth` before Outer Lands population queries.

**Как доделать:**
- After fixing GAP-2, audit whether `MazeThread` should remain asynchronous or complete before enabling/placing the portal.
- If async remains, gate portal activation until `MazeHandler.getFromHashMap(new CellLoc(cx, cz))` or the generated portal cell exists.
- Add logging or debug-only verification around `MazeHandler.saveMaze` cell count during manual smoke.
- Verify `labyrinth.dat_new` to `labyrinth.dat_old` rename sequence handles first-save/no-current-file cases.

**Критерии приемки:**
- [ ] Immediately after ring generation, maze cells are present before a player can enter the portal.
- [ ] Saving writes non-zero cell count to `labyrinth.dat`.
- [ ] Reloading the Overworld before entering Outer Lands restores the same maze cells.
- [ ] Outer Lands chunk population after reload uses restored cells and does not create empty void-only arrival chunks.
- [ ] No concurrent modification/race crash occurs during save while a maze thread is running.

**Риски / зависимости:**
Changing async behavior can affect worldgen performance. If synchronization is added, it must not stall the server excessively during chunk population.

### GAP-7: Structure query hooks are new 1.12.2 behavior and need correctness checks

**Статус:** требует проверки  
**Критичность:** medium

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/world/dim/ChunkProviderOuter.java:69-117`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/world/dim/ChunkProviderOuter.class`

**Что не совпадает:**
Reference `ChunkProviderOuter.func_147416_a(...)` returns `null` for nearest structure queries. Current 1.12.2 implementation adds `generateStructures`, `getNearestStructurePos`, and `isInsideStructure` backed by `MazeHandler`: `src/main/java/thaumcraft/common/lib/world/dim/ChunkProviderOuter.java:69-105`. This may be a useful 1.12.2 adaptation, but it is not original behavior and must not crash or report stale structures from a global labyrinth map.

**Что нужно доделать:**
Verify that these hooks are necessary for Forge 1.12.2 compatibility and that they operate only on loaded/restored maze data for the current save.

**Как доделать:**
- Confirm whether any vanilla/Forge command or feature calls these methods for the Outer Lands provider.
- If retained, restrict accepted names to documented Stage 7 names and ensure stale data is cleared on world load/unload.
- Add manual check with `/locate` or an equivalent call if available in the dev environment.

**Критерии приемки:**
- [ ] `getNearestStructurePos` returns null before any maze exists.
- [ ] After maze generation/load, structure queries return positions inside generated maze cells only.
- [ ] Structure query names are documented and case handling is intentional.
- [ ] Querying after world reload does not use stale cells from another save.
- [ ] No command/runtime crash occurs when querying structures in Outer Lands.

**Риски / зависимости:**
This is a 1.12.2 adaptation rather than reference parity. It should be kept only if it solves a Forge API need or helps validation without changing gameplay.

### GAP-8: Biome registration/config ID parity is incomplete and cosmetic biome behavior needs verification

**Статус:** частично реализовано  
**Критичность:** medium

**Текущая реализация:**
- `src/main/java/thaumcraft/common/config/Config.java:41-47`
- `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:48-65`
- `src/main/java/thaumcraft/common/Thaumcraft.java:291-298`
- `src/main/java/thaumcraft/common/lib/world/biomes/BiomeMagicalForest.java:74-82`
- `src/main/java/thaumcraft/common/lib/world/biomes/BiomeTaint.java:47-65`
- `src/main/java/thaumcraft/common/lib/world/biomes/BiomeEerie.java:43-60`
- `src/main/java/thaumcraft/common/lib/world/biomes/BiomeEldritch.java:29-37`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/world/biomes/BiomeGenMagicalForest.class`
- `thaumcraft_src/thaumcraft/common/lib/world/biomes/BiomeGenTaint.class`
- `thaumcraft_src/thaumcraft/common/lib/world/biomes/BiomeGenEerie.class`
- `thaumcraft_src/thaumcraft/common/lib/world/biomes/BiomeGenEldritch.class`
- `thaumcraft_src/thaumcraft/common/lib/world/biomes/BiomeHandler.class`

**Что не совпадает:**
Config keeps original numeric biome ID fields (`biomeTaintID`, `biomeMagicalForestID`, `biomeEerieID`, `biomeEldritchID`) at `src/main/java/thaumcraft/common/config/Config.java:41-45`, but current 1.12.2 biome registration uses registry names only and never reads those IDs. That may be acceptable for Forge 1.12.2, but it is a compatibility-sensitive contract under PRD `docs/PRD.md:61-69` and must be documented/validated. PRD also calls biome color/debug overlay cosmetics a known risk: `docs/PRD.md:352-358`. Current biome color overrides exist, but no runtime/client verification is recorded.

**Что нужно доделать:**
Decide and document the 1.12.2-compatible policy for numeric biome IDs, and verify biome colors/spawns/decorators under runtime. Do not silently break config identity.

**Как доделать:**
- Audit config loading for biome ID properties; currently `syncConfigurable()` does not load them: `src/main/java/thaumcraft/common/config/Config.java:230-298`.
- If 1.12.2 cannot safely force numeric biome IDs, document the registry-name/no-op policy in Stage 7 closure notes.
- Verify `BiomeDictionary` tags registered in `src/main/java/thaumcraft/common/config/Config.java:308-330` affect `BiomeHandler` aura/tag/greatwood logic.
- Manually inspect Magical Forest/Taint/Eerie/Eldritch colors and decorator behavior after the server-side blockers are fixed; defer only cosmetic mismatches.

**Критерии приемки:**
- [ ] Biome registry names are stable and documented.
- [ ] Numeric biome ID config handling is either implemented or explicitly documented as a Forge 1.12 registry-name/no-op policy.
- [ ] `BiomeHandler.getBiomeAura`, `getRandomBiomeTag`, and `getBiomeSupportsGreatwood` return sane values for Thaumcraft and vanilla biomes.
- [ ] Magical Forest, Taint, Eerie, and Eldritch biomes spawn/decorate without server crash.
- [ ] Cosmetic color/debug overlay differences are documented as deferred only after gameplay generation works.

**Риски / зависимости:**
Client-side biome color validation overlaps Stage 8 rendering, but server spawn/decorator and biome dictionary behavior are Stage 7.

### GAP-9: Tree, plant, and ore generation probabilities/placement differ from reference

**Статус:** частично реализовано  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:275-285`
- `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:288-335`
- `src/main/java/thaumcraft/common/lib/world/WorldGenGreatwoodTrees.java:24-89`
- `src/main/java/thaumcraft/common/lib/world/WorldGenSilverwoodTrees.java:14-127`
- `src/main/java/thaumcraft/common/lib/world/biomes/BiomeMagicalForest.java:57-120`
- `src/main/java/thaumcraft/common/lib/world/biomes/BiomeTaint.java:68-110`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.class`
- `thaumcraft_src/thaumcraft/common/lib/world/WorldGenGreatwoodTrees.class`
- `thaumcraft_src/thaumcraft/common/lib/world/WorldGenSilverwoodTrees.class`
- `thaumcraft_src/thaumcraft/common/lib/world/WorldGenCustomFlowers.class`
- `thaumcraft_src/thaumcraft/common/lib/world/WorldGenManaPods.class`

**Что не совпадает:**
Reference vegetation runs in surface generation for non-blacklisted biomes, with Silverwood chance `random.nextInt(60) == 3`, Greatwood chance `random.nextInt(25) == 7`, and shimmerleaf-like flower placement in sandy/humid conditions. Reference `generateSilverwood(...)` constructs `new WorldGenSilverwoodTrees(false, 7, 4)` and the Silverwood generator can place `blockMagicalLog` meta `2` plus `createRandomNodeAt(..., true, false, false)` for a pure knot node. Current generator only calls `generateGreatwood`/`generateSilverwood` when the sampled biome is exactly Magical Forest: `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:288-292`, so most reference tree generation paths are skipped. Current `generateSilverwood(...)` has an internal 5% chance and uses `new WorldGenSilverwoodTrees(false, 8, 5)` at `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:322-335`; Magical Forest biome decoration can also pick Silverwood with the same `8,5` parameters at `src/main/java/thaumcraft/common/lib/world/biomes/BiomeMagicalForest.java:57-60`, but that does not replace the missing global `generateVegetation` path. Current `WorldGenSilverwoodTrees` statically has a knot-node path: it places meta `2` and calls `createRandomNodeAt(..., true, false, false)` at `src/main/java/thaumcraft/common/lib/world/WorldGenSilverwoodTrees.java:70-87`, and `BlockMagicalLog` creates a `TileNode` for `TYPE == 2` at `src/main/java/thaumcraft/common/blocks/BlockMagicalLog.java:49-58`; this still needs runtime validation after natural Silverwood generation is restored. Reference `generateGreatwood(...)` calls `WorldGenGreatwoodTrees.generate(..., random.nextInt(8) == 0)`, and the spider variant places a `CaveSpider` spawner under the trunk, web blocks near Greatwood logs/leaves, and a dungeon-loot chest below the spawner. Current `generateGreatwood(...)` always calls `new WorldGenGreatwoodTrees(false).generate(...)` without a spider-variant flag: `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:311-318`; current `WorldGenGreatwoodTrees` only generates trunk/canopy and has no `CaveSpider` spawner, webs, or loot chest path at `src/main/java/thaumcraft/common/lib/world/WorldGenGreatwoodTrees.java:24-89`. Current ore generation uses `WorldGenMinable` vein counts/chances at `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:275-285`; reference cinnabar/amber placement is per-block attempts with different height rules, and infused stone chooses aspect metadata partly from biome tag.

**Что нужно доделать:**
Port reference vegetation/ore probability and placement rules, adapted to 1.12.2 APIs, while preserving current block states/metas.

**Как доделать:**
- Move tree generation into a reference-like `generateVegetation` path rather than only Magical Forest chunks.
- Match Silverwood/Greatwood chance gating and `BiomeHandler.getBiomeSupportsGreatwood` logic, including reference Silverwood chance `random.nextInt(60) == 3` before `generateSilverwood(...)`.
- Align or explicitly justify Silverwood generator parameters: reference uses `WorldGenSilverwoodTrees(false, 7, 4)`, while current worldgen and Magical Forest decorator use `8, 5`.
- Runtime-validate Silverwood knot-node creation: generated meta `2` log must create a `TileNode`, become `NodeType.PURE`, retain aspects, and survive save/reload.
- Port/adapt `WorldGenGreatwoodTrees.generate(World, Random, ..., boolean spiders)` semantics: `random.nextInt(8) == 0` spider variant, `CaveSpider` spawner below trunk, up to 50 webs adjacent to Greatwood leaves/logs, and a dungeon-loot chest below the spawner.
- Restore cinnabar/amber/infused stone placement counts, Y-ranges, and biome-aspect metadata selection.
- Verify `WorldGenGreatwoodTrees`, `WorldGenSilverwoodTrees`, `WorldGenBigMagicTree`, `WorldGenManaPods`, and `WorldGenCustomFlowers` against reference classes after generator-level parity is fixed.

**Критерии приемки:**
- [ ] Greatwood and Silverwood can generate outside Magical Forest where reference conditions allow.
- [ ] Natural Silverwood generation uses the reference global vegetation path and does not rely only on `BiomeMagicalForest.getRandomTreeFeature(...)`.
- [ ] Cinnabar, amber, and infused stone use reference-like count/chance/Y/metadata behavior.
- [ ] Biome blacklist levels block trees/ores/structures at the same levels as reference.
- [ ] Generated Silverwood still creates a pure node when reference behavior expects it; the knot block has `TYPE == 2`, a `TileNode`, `NodeType.PURE`, non-empty aspects, and persists after chunk save/reload.
- [ ] Spider Greatwood variant can generate at the reference chance and includes a `CaveSpider` spawner, webs near Greatwood blocks, and a populated dungeon-loot chest.
- [ ] Manual new-world sampling confirms ores and trees appear at plausible original rates.

**Риски / зависимости:**
Ore/block metadata must match existing 1.12.2 blockstate mappings in `ConfigBlocks`; wrong metadata can create invalid ore variants.

### GAP-10: Boss/world data exists but integration with boss-room lifecycle is unverified

**Статус:** требует проверки  
**Критичность:** medium

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/world/dim/MapBossData.java:1-23`
- `src/main/java/thaumcraft/common/lib/world/dim/GenBossRoom.java:23-63`
- `src/main/java/thaumcraft/common/lib/events/EventHandlerWorld.java:137-149`
- `src/main/java/thaumcraft/common/lib/events/EventHandlerWorld.java:207-220`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/world/dim/MapBossData.class`
- `thaumcraft_src/thaumcraft/common/lib/world/dim/GenBossRoom.class`

**Что не совпадает:**
`MapBossData` itself matches the simple reference field/key shape (`bossCount`), but static search did not confirm full integration for boss room progression, boss count updates, or persisted boss-room state. Current block placement cancellation now checks `Config.dimensionOuterId`, so the previously documented hard-coded `-42` safety bypass is resolved in current source; boss-room lifecycle and save/reload integration remain unverified.

**Что нужно доделать:**
Verify and wire boss-room world data where the reference uses it, and keep configured dimension checks covered by validation.

**Как доделать:**
- Search and compare reference boss-room and altar/lock lifecycle code against current `GenBossRoom`, `TileEldritchLock`, boss entities, and `MapBossData` usage.
- Keep `EventHandlerWorld` boss-placement safety tied to `Config.dimensionOuterId` and validate it with a changed Outer Lands dimension id.
- Validate block placement prevention during active boss fights in Outer Lands.

**Критерии приемки:**
- [ ] `MapBossData` is loaded/saved and updated wherever boss-room progression requires it.
- [ ] Boss-room locks/doors and active boss block-placement prevention work in configured Outer Lands dimension.
- [ ] Changing `dimensionOuterId` in config does not bypass boss-room safety checks.
- [ ] Save/reload does not reset boss-room progression incorrectly.
- [ ] Manual boss-room smoke scenario documents any remaining dependency on Stage 6 entity behavior.

**Риски / зависимости:**
Dependency: boss entity combat behavior belongs mainly to Stage 6. Stage 7 should verify room/world-data integration and label combat AI issues as Stage 6 dependencies.

### GAP-11: Overworld hilltop altar and mound/barrow structures are gameplay placeholders

**Статус:** реализовано неправильно  
**Критичность:** high

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:346-370`
- `src/main/java/thaumcraft/common/lib/world/WorldGenHilltopStones.java:14-31`
- `src/main/java/thaumcraft/common/lib/world/WorldGenMound.java:14-36`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.class`
- `thaumcraft_src/thaumcraft/common/lib/world/WorldGenHilltopStones.class`
- `thaumcraft_src/thaumcraft/common/lib/world/WorldGenMound.class`

**Что не совпадает:**
Current `generateStructures(...)` calls simplified placeholder generators with independent chances at `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:346-370`. `WorldGenHilltopStones` currently places only a stonebrick/cobblestone-like ring and an empty vanilla chest: `src/main/java/thaumcraft/common/lib/world/WorldGenHilltopStones.java:14-31`. The reference hilltop altar rejects low/invalid surfaces through `LocationIsValidSpawn(...)`, builds a full altar/stones layout, places a dungeon-loot chest, places a mob spawner, and sets its entity id to `Thaumcraft.Wisp`. `WorldGenMound` currently places only a small procedural grass/dirt mound and an empty vanilla chest: `src/main/java/thaumcraft/common/lib/world/WorldGenMound.java:14-36`. The reference mound/barrow is a large fixed structure with interior block template, loot urn/crate placements (`blockLootCrate`/`blockLootUrn`), a populated dungeon chest, and `Skeleton`/`Zombie` spawners. This directly covers the reported bug where an altar/mound appears as only a ring plus empty chest and lacks wisp spawner, structure columns/details, and populated loot.

**Что нужно доделать:**
Port the original Overworld surface structure templates and validation rules for `WorldGenHilltopStones` and `WorldGenMound`, not just their existence in the simplified generator branch. These are Stage 7 gameplay structures because they affect world exploration, loot, mob spawning, and aura/node placement.

**Как доделать:**
- Fold `WorldGenHilltopStones` and `WorldGenMound` back into the reference-like `generateSurface(...)` branch from GAP-1 so they share original `randPosY = getHeight(...) - 9`, sea-level, flat-world, blacklist, and chance semantics.
- Port/adapt `WorldGenHilltopStones.LocationIsValidSpawn(...)`, full block layout, columns/details/vines, populated dungeon chest, mob spawner tile setup, and `Thaumcraft.Wisp` spawner id.
- Port/adapt `WorldGenMound` fixed barrow template, support/fill behavior, loot urn/crate placements, populated dungeon chest, trap/loot details, and `Skeleton`/`Zombie` spawner ids.
- Ensure generated chests use the Forge 1.12.2 loot/table or inventory-fill equivalent of original `ChestGenHooks.getInfo("dungeonChest")` without leaving empty vanilla chests.
- If `blockLootUrn`/`blockLootCrate` are still missing, resolve them with the Stage 7 block-content work rather than silently substituting empty chests.

**Критерии приемки:**
- [ ] Generated hilltop altar refuses low/invalid/water surfaces and only generates where `LocationIsValidSpawn`-equivalent checks pass.
- [ ] Generated hilltop altar contains the reference altar/stones layout, populated loot chest, and a spawner configured for `Thaumcraft.Wisp`.
- [ ] Generated mound/barrow contains the original internal structure, loot urn/crate placements or documented functional equivalents, populated loot chest, and skeleton/zombie spawners.
- [ ] Chests generated by `WorldGenHilltopStones` and `WorldGenMound` are not empty in a fresh runtime world.
- [ ] Manual new-world sampling covers at least one successful hilltop altar and one mound/barrow, and records no chunk-population crash.

**Риски / зависимости:**
Dependency: loot urn/crate block registration and mob entity ids must be available before full parity. If entity ids changed during the 1.12.2 port, the executor must map them deliberately and document the registry-name compatibility decision instead of leaving vanilla spawners unconfigured.

**Checkpoint 2026-05-14 — Hilltop wisp altar template:**
`WorldGenHilltopStones` is no longer a stonebrick-ring/empty-chest placeholder. It now ports the reference surface validation, rejects low/invalid placements, builds the seven-by-seven obsidian/obsidian-totem altar footprint, places variable perimeter columns with vine growth, adds a dungeon-loot-table chest, and configures the central mob spawner for the ported `thaumcraft:wisp` entity id. The structure call site now only creates the companion aura node after a successful hilltop generation.

Remaining GAP-11 limits after this checkpoint: `WorldGenMound` is still a placeholder, the broader `generateSurface(...)`/chance semantics remain part of GAP-1, and hilltop generation has not been observed in a runtime world because server smoke remains environment-blocked.

## 6. Итоговый checklist закрытия Stage 7

- [ ] GAP-1 closed: reference-like fresh-world generation pipeline restored and tested.
- [ ] GAP-2 closed: Eldritch ring generation and maze bootstrap match reference behavior.
- [ ] GAP-3 closed: teleporter searches/caches safe portal destinations and manual portal scenario passes.
- [ ] GAP-4 closed: Outer Lands room templates have no gameplay TODO/placeholders and generate traversable rooms.
- [ ] GAP-5 closed: server smoke plus manual new-world/Outer Lands generation validation documented.
- [ ] GAP-6 closed: maze save/load/race safety verified with `labyrinth.dat` persistence.
- [ ] GAP-7 closed or documented: structure query hooks are safe and intentional.
- [ ] GAP-8 closed or documented: biome registration/config ID policy and color/decorator verification complete.
- [ ] GAP-9 closed: tree/plant/ore generation probabilities and placement match reference closely enough.
- [ ] GAP-10 closed or dependency-labeled: boss room world data and dimension safety integration verified.
- [ ] GAP-11 closed: hilltop altar and mound/barrow Overworld structures have reference-like validation, blocks, spawners, and populated loot.
- [ ] `rg -n "TODO|TBD|placeholder|Replace with|return null|no-op" src/main/java/thaumcraft/common/lib/world src/main/java/thaumcraft/common/lib/world/dim src/main/java/thaumcraft/common/blocks/BlockEldritchPortal.java` reviewed with no unresolved Stage 7 gameplay stubs.
- [ ] `./scripts/dev.sh compileJava` passes after implementation work.
- [ ] `./scripts/dev.sh smoke-server` passes after implementation work.
- [ ] Manual Stage 7 smoke notes include new world generation, Eldritch ring, hilltop altar, mound/barrow, normal/spider Greatwood, portal entry, maze traversal, save/reload, and portal return.

## 7. Definition of Done

Stage 7 считается ПОЛНОСТЬЮ завершенной только если:
- все blocker gaps закрыты;
- все high gaps закрыты;
- все элементы из scope Stage 7 реализованы;
- текущая реализация соответствует референсу по поведению;
- все нужные файлы, ресурсы и регистрации присутствуют;
- отсутствуют TODO и заглушки внутри scope Stage 7;
- проект собирается без ошибок;
- базовые игровые сценарии Stage 7 проверены вручную или тестами;
- ./docs/Stage7.md обновлен и не содержит критичных открытых вопросов.

## 8. Открытые вопросы

- Решение по numeric biome IDs: для Forge 1.12.2 идентичность биомов фиксируется registry names, а не numeric IDs. Старые config keys из `src/main/java/thaumcraft/common/config/Config.java:41-45` нужно читать как documented no-op, но не пытаться принудительно назначать numeric IDs. В closure notes Stage 7 явно указать эту политику и стабильные registry names.
- Решение по portal trigger: safe `TeleporterThaumcraft` placement/search/caching и reference-like ticked `TileEldritchPortal` trigger закрыты отдельными checkpoints. Полное закрытие GAP-3 все еще требует runtime evidence прибытия рядом с существующим portal block из `src/main/java/thaumcraft/common/lib/world/dim/GenPortal.java:135-138`, возврата в Overworld, save/reload traversal и проверки loop/cooldown behavior.
- Решение по недостающим room blocks: direct blockers `blockLootUrn`, `blockLootCrate`, `blockSlabStone` закрыты отдельными content checkpoints; оставшиеся room/block substitutions закрывать в Stage 7 как часть server-side traversal/room parity, а не silently defer. Допустимы только явно документированные cosmetic deferrals после runtime-проверки, что progression/traversal/loot не ломаются. Assets брать из `thaumcraft_src/assets/`.
- Дополнительный blocker к плану закрытия: direct `BlockEldritch` meta/TE contract закрыт отдельным checkpoint: блок больше не clamp'ит generated room metas `7/8/9/10` в `0..4` и создает нужные tile entities. `TileEldritchAltar` теперь имеет server-side spawner/checkForMaze baseline, но portal-opening behavior и runtime evidence остаются открытыми. `TileEldritchCrabSpawner` и `TileEldritchTrap` имеют server-side tick baseline; client FX/runtime evidence остаются открытыми. `TileEldritchLock` все еще требует отдельной parity-проверки.

## 9. Утвержденный прагматичный план закрытия

Выбран server-side pragmatic closure вместо документального deferral или полной Stage 7/Stage 8 parity. Цель — закрыть gameplay blockers Stage 7 без обязательной клиентской visual parity.

1. Config/checkpoint:
   - читать numeric biome ID config keys как documented no-op;
   - читать `outer_lands_dim` в `Config.dimensionOuterId`;
   - поддерживать `Config.dimensionOuterId` в Outer Lands safety checks;
   - зафиксировать registry-name policy для биомов в closure notes.
2. Portal/checkpoint:
   - основной teleport trigger перенесен в tickable `TileEldritchPortal` по reference flow;
   - `BlockEldritchPortal.onEntityCollision` больше не выполняет dimension transfer;
   - портировать safe portal search/cache/placement в `TeleporterThaumcraft`;
   - добавить guard на отсутствующий target dimension/world.
3. Outer Lands block contract/checkpoint:
   - расширить `BlockEldritch` до metas, используемых Stage 7 rooms;
   - восстановить минимально необходимые server-side tile entity contracts для altar, lock, crab spawner, trap, cap/obelisk;
   - не считать rooms валидными, пока generated metas не создают корректные blocks/TE.
4. Room content/checkpoint:
   - добавить минимальные `BlockLoot`/`BlockLootItem` для urn/crate или функционально эквивалентный 1.12.2 порт;
   - добавить/заменить stone slab equivalent для library rooms;
   - убрать Stage 7 gameplay TODO/placeholders из room generators.
5. Worldgen/maze/checkpoint:
   - восстановить normal generation control flow в `ThaumcraftWorldGenerator`;
   - исправить Eldritch ring bootstrap: reference-like width/height/seed, sea-level/surface validation, full altar layout и dark node/altar behavior;
   - восстановить `WorldGenHilltopStones`/`WorldGenMound` templates: validation, filled loot, `Thaumcraft.Wisp`/skeleton/zombie spawners, urn/crate placements;
   - восстановить normal/spider Greatwood generation path: non-Magical-Forest vegetation chance, `CaveSpider` spawner, webs, and loot chest;
   - проверить maze race: portal activation only after maze cells exist or generation is completed.
6. Validation/checkpoint:
   - `./scripts/dev.sh compileJava`;
   - `./scripts/dev.sh smoke-server`;
   - manual Stage 7 smoke: new world generation, Eldritch ring, hilltop altar, mound/barrow, normal/spider Greatwood, portal entry, several Outer Lands chunks, traversal, save/reload, return portal.
