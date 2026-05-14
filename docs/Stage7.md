# Stage 7 — Gap-анализ и план закрытия

## 1. Цель фазы

Stage 7 закрывает серверную часть генерации мира и Outer Lands для порта Thaumcraft 4.2.3.5 на Forge 1.12.2: биомы, деревья, руды, структуры, измерение Outer Lands, лабиринт, порталы, телепортацию, сохранение `labyrinth.dat`, world data и связанные регистрации/config. По PRD цель фазы: `docs/PRD.md:341-345`; текущий статус PRD прямо говорит, что baseline Outer Lands есть, но фаза не закрыта и требует runtime evidence: `docs/PRD.md:347-350`.

Фаза не может считаться завершенной сейчас: ниже остаются blocker/high gaps, а PRD требует, чтобы runtime generation работал в новом мире и косметические deferrals были документированы: `docs/PRD.md:361-364`.

## 2. Scope фазы

- Биомы Stage 7: `src/main/java/thaumcraft/common/lib/world/biomes/BiomeMagicalForest.java`, `src/main/java/thaumcraft/common/lib/world/biomes/BiomeTaint.java`, `src/main/java/thaumcraft/common/lib/world/biomes/BiomeEerie.java`, `src/main/java/thaumcraft/common/lib/world/biomes/BiomeEldritch.java`, `src/main/java/thaumcraft/common/lib/world/biomes/BiomeHandler.java`.
- Регистрация биомов и измерения: `src/main/java/thaumcraft/common/Thaumcraft.java:124-130`, `src/main/java/thaumcraft/common/Thaumcraft.java:197-201`, `src/main/java/thaumcraft/common/Thaumcraft.java:291-298`, `src/main/java/thaumcraft/common/config/Config.java:308-330`.
- Config Stage 7: biome IDs/weights, Outer Lands dimension id, worldgen/retrogen toggles: `src/main/java/thaumcraft/common/config/Config.java:41-58`, `src/main/java/thaumcraft/common/config/Config.java:78-95`, `src/main/java/thaumcraft/common/config/Config.java:230-246`.
- Surface/world generation: `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java` and generators under `src/main/java/thaumcraft/common/lib/world/WorldGen*.java`.
- Outer Lands provider/chunk generation: `src/main/java/thaumcraft/common/lib/world/dim/WorldProviderOuter.java`, `src/main/java/thaumcraft/common/lib/world/dim/ChunkProviderOuter.java`.
- Maze model/generation/persistence: `src/main/java/thaumcraft/common/lib/world/dim/Cell.java`, `src/main/java/thaumcraft/common/lib/world/dim/CellLoc.java`, `src/main/java/thaumcraft/common/lib/world/dim/MazeGenerator.java`, `src/main/java/thaumcraft/common/lib/world/dim/MazeThread.java`, `src/main/java/thaumcraft/common/lib/world/dim/MazeHandler.java`.
- Outer Lands room templates: `src/main/java/thaumcraft/common/lib/world/dim/GenCommon.java`, `src/main/java/thaumcraft/common/lib/world/dim/GenPortal.java`, `src/main/java/thaumcraft/common/lib/world/dim/GenPassage.java`, `src/main/java/thaumcraft/common/lib/world/dim/Gen2x2.java`, `src/main/java/thaumcraft/common/lib/world/dim/GenBossRoom.java`, `src/main/java/thaumcraft/common/lib/world/dim/GenKeyRoom.java`, `src/main/java/thaumcraft/common/lib/world/dim/GenNestRoom.java`, `src/main/java/thaumcraft/common/lib/world/dim/GenLibraryRoom.java`.
- Portals/teleport safety: `src/main/java/thaumcraft/common/blocks/BlockEldritchPortal.java`, `src/main/java/thaumcraft/common/lib/world/dim/TeleporterThaumcraft.java`, generated portal room at `src/main/java/thaumcraft/common/lib/world/dim/GenPortal.java:135-138`.
- Persistence/world events: `src/main/java/thaumcraft/common/lib/events/EventHandlerWorld.java:45-57`, retrogen chunk NBT at `src/main/java/thaumcraft/common/lib/events/EventHandlerWorld.java:74-103`, boss world data at `src/main/java/thaumcraft/common/lib/world/dim/MapBossData.java`.
- Scenarios that should work after closure: new Overworld generation creates ores, aura nodes, magical/taint/eerie/eldritch biome behavior, Greatwood/Silverwood and plants; Eldritch rings create matching maze reservations and saved `labyrinth.dat`; entering an Eldritch portal reaches a safe Outer Lands portal cell; Outer Lands chunks populate rooms/passages/boss/key/nest/library; saving/reloading preserves maze cells and generated rooms; `/locate`-style structure query hooks do not crash; retrogen honors chunk NBT flags.

## 3. Источники сравнения

- PRD Stage 7 scope and risk lines: `docs/PRD.md:341-364`.
- Current implementation: `src/main/java/thaumcraft/common/lib/world/**`, `src/main/java/thaumcraft/common/lib/world/dim/**`, `src/main/java/thaumcraft/common/lib/world/biomes/**`, `src/main/java/thaumcraft/common/blocks/BlockEldritchPortal.java`, `src/main/java/thaumcraft/common/config/Config.java`, `src/main/java/thaumcraft/common/Thaumcraft.java`, `src/main/java/thaumcraft/common/lib/events/EventHandlerWorld.java`.
- Reference classes are `.class` files under `thaumcraft_src/**`; decompiled with `cfr --silent true ...` for Stage 7 classes. Key reference paths: `thaumcraft_src/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.class`, `thaumcraft_src/thaumcraft/common/lib/world/WorldGenEldritchRing.class`, `thaumcraft_src/thaumcraft/common/lib/world/dim/ChunkProviderOuter.class`, `thaumcraft_src/thaumcraft/common/lib/world/dim/WorldProviderOuter.class`, `thaumcraft_src/thaumcraft/common/lib/world/dim/MazeHandler.class`, `thaumcraft_src/thaumcraft/common/lib/world/dim/MazeGenerator.class`, `thaumcraft_src/thaumcraft/common/lib/world/dim/MazeThread.class`, `thaumcraft_src/thaumcraft/common/lib/world/dim/TeleporterThaumcraft.class`, `thaumcraft_src/thaumcraft/common/blocks/BlockEldritchPortal.class`, `thaumcraft_src/thaumcraft/common/lib/world/biomes/BiomeGenMagicalForest.class`, `thaumcraft_src/thaumcraft/common/lib/world/biomes/BiomeGenTaint.class`, `thaumcraft_src/thaumcraft/common/lib/world/biomes/BiomeGenEerie.class`, `thaumcraft_src/thaumcraft/common/lib/world/biomes/BiomeGenEldritch.class`.
- Lightweight commands run for this analysis: `git status --short`; `find thaumcraft_src/thaumcraft/common/lib/world -type f`; `cfr --silent true` on selected Stage 7 reference classes; `rg`-backed searches via tools for `Outer`, `Eldritch`, `Biome`, `Dimension`, `Teleporter`, `WorldSavedData`, `Maze`, `TODO`.
- No build, server smoke, or client smoke validation was run; this document is static gap analysis only.

## 4. Текущее состояние Stage 7

- Baseline exists: world generator instance is registered at `src/main/java/thaumcraft/common/Thaumcraft.java:124-126`; biomes are initialized and registered at `src/main/java/thaumcraft/common/Thaumcraft.java:128-130` and `src/main/java/thaumcraft/common/Thaumcraft.java:291-298`; Outer Lands dimension type/dimension are registered at `src/main/java/thaumcraft/common/Thaumcraft.java:197-201`.
- Outer Lands provider/chunk generator exists: provider uses a single Eldritch biome and returns `ChunkProviderOuter`: `src/main/java/thaumcraft/common/lib/world/dim/WorldProviderOuter.java:38-48`; chunk provider makes empty chunks and populates maze rooms through `MazeHandler.generateEldritch`: `src/main/java/thaumcraft/common/lib/world/dim/ChunkProviderOuter.java:27-39`, `src/main/java/thaumcraft/common/lib/world/dim/ChunkProviderOuter.java:50-67`.
- Maze persistence exists and uses original file names/keys (`labyrinth.dat`, `labyrinth.dat_old`, `Data`, `cells`, `x`, `z`, `cell`): `src/main/java/thaumcraft/common/lib/world/dim/MazeHandler.java:43-116`; it is loaded/saved on Overworld world load/save: `src/main/java/thaumcraft/common/lib/events/EventHandlerWorld.java:45-57`.
- Several current room/template classes are clearly partial or temporary: `src/main/java/thaumcraft/common/lib/world/dim/GenCommon.java:258-259`, `src/main/java/thaumcraft/common/lib/world/dim/GenNestRoom.java:122-125`, `src/main/java/thaumcraft/common/lib/world/dim/GenLibraryRoom.java:126-130`.
- Current surface generator is much narrower than the reference. It skips Outer Lands in the general world generator at `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:257-258`; generates ores with modern `WorldGenMinable` at `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:275-285`; only generates trees when the sampled biome equals Magical Forest at `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:288-292`; only generates structures in dim 0 at `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:294-297`.
- Current portal collision actively teleports players at `src/main/java/thaumcraft/common/blocks/BlockEldritchPortal.java:48-78`, but current teleporter only places the entity at same X/Z and fixed Y=60: `src/main/java/thaumcraft/common/lib/world/dim/TeleporterThaumcraft.java:12-29`.

## 5. Gap list

### GAP-1: Surface worldgen pipeline is missing original aura, totem, nether, retrogen, and structure-node behavior

**Статус:** частично реализовано  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:253-298`
- `src/main/java/thaumcraft/common/lib/events/EventHandlerWorld.java:74-103`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.class`

**Что не совпадает:**
Reference `ThaumcraftWorldGenerator` has a `worldGeneration(random, chunkX, chunkZ, world, newGen)` path that handles Outer Lands, Nether, End skip, surface generation, and retrogen marking. It calls `generateWildNodes`, `generateTotem`, `generateVegetation`, `generateOres`, and structure-node placement near scattered features. Current `generate(...)` has no `worldGeneration` method, no `newGen` parameter, no nether branch, no wild node generation despite `Config.genAura`, no totem generation, no `MapGenScatteredFeature` structure-node behavior, and no direct retrogen execution path from queued chunks.

Current retrogen queue only records chunks when the `Thaumcraft` chunk NBT flag is missing: `src/main/java/thaumcraft/common/lib/events/EventHandlerWorld.java:87-101`. The actual tick execution must call a Stage 7-equivalent generator path; otherwise `regenAura`, `regenStructure`, `regenTrees`, `regenAmber`, `regenCinnibar`, `regenInfusedStone` cannot match the reference.

**Что нужно доделать:**
Reintroduce the reference worldgen control flow with a 1.12.2 API adaptation: one code path for normal generation and one for retrogen, preserving config toggles, dimension/biome blacklist semantics, flat-world skip behavior, nether node/totem behavior, surface aura generation, structure-node generation, vegetation, ores, structures, and chunk dirty marking where appropriate.

**Как доделать:**
- Port `worldGeneration(Random, int, int, World, boolean)` into `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java`.
- Port/adapt `generateWildNodes`, `generateTotem`, `generateSurface`, `generateVegetation`, `generateOres`, `generateNether` from `thaumcraft_src/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.class`.
- Wire `ServerTickEventsFML` retrogen queue to call the new retrogen path with `newGen=false`; current queue source is `src/main/java/thaumcraft/common/lib/events/EventHandlerWorld.java:93-101`.
- Preserve `Config.genAura`, `Config.regenAura`, `Config.genStructure`, `Config.regenStructure`, `Config.genTrees`, `Config.regenTrees`, ore toggles from `src/main/java/thaumcraft/common/config/Config.java:230-246`.
- Keep Stage 7 changes isolated from unrelated entity/render/client phases.

**Критерии приемки:**
- [ ] New Overworld chunks can generate aura nodes when `Config.genAura=true` and `Config.nodeRarity` allows it.
- [ ] Retrogen queued chunks execute the same generation categories enabled by `Config.regen*` and set/consume chunk NBT correctly.
- [ ] Nether chunks can generate the original node/totem subset and End chunks remain skipped.
- [ ] Flat worlds skip structures where the reference skipped them.
- [ ] `./scripts/dev.sh smoke-server` reaches ready state after generating a new world with Thaumcraft worldgen enabled.

**Риски / зависимости:**
Dependency: Stage 3 node/tile behavior must be stable enough for generated aura nodes to work. Runtime validation is required because compile success cannot prove worldgen parity.

### GAP-2: Overworld Eldritch ring generation does not match reference maze bootstrap

**Статус:** реализовано неправильно  
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
Reference `ThaumcraftWorldGenerator` creates Eldritch rings from the surface structure branch with random odd maze width/height `11 + random.nextInt(6) * 2`, assigns those dimensions to the `WorldGenEldritchRing`, creates a dark node, and starts `new MazeThread(chunkX, chunkZ, w, h, random.nextLong())`. Current `WorldGenEldritchRing` ignores ring width/height, places a simple obsidian circle plus portal, and starts `MazeThread(cx, cz, 32, 32, world.getSeed())`: `src/main/java/thaumcraft/common/lib/world/WorldGenEldritchRing.java:17-36`. Current surface structure chance also differs: `rand.nextInt(800)` at `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:358`, while the reference branch uses ring chance inside the structure branch and also creates the maze dimensions/dark node.

**Что нужно доделать:**
Port the reference ring structure and maze bootstrap behavior instead of the current placeholder ring. The ring must reserve/generate a maze whose dimensions and seed come from the worldgen branch, and the surface ring must match original block layout and node behavior.

**Как доделать:**
- Add `chunkX`, `chunkZ`, `width`, `height` fields or equivalent to `WorldGenEldritchRing` if needed by the reference generator.
- Move maze-thread creation back to the worldgen structure branch, or make `WorldGenEldritchRing` receive the exact reference dimensions/seed.
- Port reference block layout from `thaumcraft_src/thaumcraft/common/lib/world/WorldGenEldritchRing.class` to `src/main/java/thaumcraft/common/lib/world/WorldGenEldritchRing.java`.
- Ensure a dark/eerie aura node is generated at the ring as in the reference branch.
- Verify `MazeHandler.mazesInRange(...)` prevents overlapping mazes with the same radius semantics as reference.

**Критерии приемки:**
- [ ] A generated Overworld Eldritch ring produces non-empty maze cells in `MazeHandler.labyrinth` around the expected chunk center.
- [ ] Maze width/height are odd values in the reference range rather than a fixed 32x32 grid.
- [ ] Ring generation creates the reference dark node/totem behavior.
- [ ] Saving after ring generation writes expected `labyrinth.dat` cell entries.
- [ ] Manual new-world test can find an Eldritch ring and enter the associated Outer Lands maze.

**Риски / зависимости:**
Race risk: `MazeThread` is asynchronous, so saving/teleporting immediately after ring generation can race with maze population. This needs runtime validation or synchronization rules.

### GAP-3: Portal teleporter is unsafe and does not search/cache destination portals

**Статус:** реализовано неправильно  
**Критичность:** blocker

**Текущая реализация:**
- `src/main/java/thaumcraft/common/blocks/BlockEldritchPortal.java:48-78`
- `src/main/java/thaumcraft/common/lib/world/dim/TeleporterThaumcraft.java:7-30`
- `src/main/java/thaumcraft/common/lib/world/dim/GenPortal.java:135-138`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/world/dim/TeleporterThaumcraft.class`
- `thaumcraft_src/thaumcraft/common/blocks/BlockEldritchPortal.class`
- `thaumcraft_src/thaumcraft/common/lib/world/dim/GenPortal.class`

**Что не совпадает:**
Reference `TeleporterThaumcraft` searches within 128 blocks for `ConfigBlocks.blockEldritchPortal`, caches portal positions by chunk/dimension key, places the entity beside the found portal with zero velocity, falls back to safe top Y in dimension 1-style logic, and expires cache entries. Current teleporter always places the entity at current integer X/Z and fixed `y=60`: `src/main/java/thaumcraft/common/lib/world/dim/TeleporterThaumcraft.java:12-18`; `makePortal` returns true without creating/finding a portal: `src/main/java/thaumcraft/common/lib/world/dim/TeleporterThaumcraft.java:26-29`. This can drop players into void/solid rooms and does not guarantee arrival near the generated `GenPortal` block.

Current `BlockEldritchPortal` also adds collision-driven dimension switching in the block itself: `src/main/java/thaumcraft/common/blocks/BlockEldritchPortal.java:48-78`; the decompiled reference block is mostly non-colliding/invisible and neighbor-stability logic, so teleport trigger ownership needs verification against tile/entity/item interactions outside this file.

**Что нужно доделать:**
Port/adapt the reference portal search/cache/placement algorithm to 1.12.2 `Teleporter` APIs and verify the correct trigger for entering/leaving Outer Lands. The player should arrive adjacent to an existing Eldritch portal room, not fixed Y=60.

**Как доделать:**
- Implement portal search radius, cache, velocity reset, and cache cleanup in `src/main/java/thaumcraft/common/lib/world/dim/TeleporterThaumcraft.java`.
- Validate `BlockEldritchPortal.onEntityCollision` against original trigger flow; keep it only if the 1.12.2 port intentionally needs block collision to replace original portal activation.
- Ensure `server.getWorld(Config.dimensionOuterId)` is non-null before calling `changeDimension`; handle missing dimension as a safe no-op/logged failure.
- Ensure return teleport to dim 0 uses a valid portal/ground placement.

**Критерии приемки:**
- [ ] Entering an Overworld Eldritch portal places the player adjacent to an Outer Lands portal block generated by `GenPortal`.
- [ ] Returning from Outer Lands places the player at a safe Overworld portal/ground position.
- [ ] Player velocity is cleared after teleport.
- [ ] Rapid portal collisions do not loop or trap the player.
- [ ] Server smoke and manual teleport scenario show no void fall, suffocation, `NullPointerException`, or missing dimension crash.

**Риски / зависимости:**
Depends on GAP-2 and GAP-4 because the teleporter can only find safe portals if maze cells and portal rooms exist before teleport. High risk of player-loss bugs without manual validation.

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
Current room generators have temporary replacements for original loot urns/crates and slabs: `GenCommon` places vanilla stone instead of `ConfigBlocks.blockLootUrn`: `src/main/java/thaumcraft/common/lib/world/dim/GenCommon.java:252-260`; `GenNestRoom` places vanilla chests for both urn/crate branches and carries TODO: `src/main/java/thaumcraft/common/lib/world/dim/GenNestRoom.java:116-125`; `GenLibraryRoom` uses vanilla `Blocks.DOUBLE_STONE_SLAB` with TODO for `ConfigBlocks.blockSlabStone`: `src/main/java/thaumcraft/common/lib/world/dim/GenLibraryRoom.java:126-130`. Reference room generators include extensive block placements, loot containers, spawners, slabs, urns/crates, and boss room decoration.

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
Current NBT format matches the reference at a high level, but generation remains asynchronous (`new Thread(new MazeThread(...)).start()` at `src/main/java/thaumcraft/common/lib/world/WorldGenEldritchRing.java:35-36`) and only positive cells are saved: `src/main/java/thaumcraft/common/lib/world/dim/MazeHandler.java:57-66`. The reference also uses async `MazeThread`, but current code changes the ring dimensions/seed and exposes teleport from `BlockEldritchPortal` immediately, increasing the chance that a player can teleport before the maze cell map is ready. Load/save is tied to dimension 0 only: `src/main/java/thaumcraft/common/lib/events/EventHandlerWorld.java:45-57`; this matches original file location but needs validation in integrated server lifecycle.

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
Decide and document the 1.12.2-compatible policy for legacy biome IDs, and verify biome colors/spawns/decorators under runtime. Do not silently break config identity.

**Как доделать:**
- Audit config loading for biome ID properties; currently `syncConfigurable()` does not load them: `src/main/java/thaumcraft/common/config/Config.java:230-298`.
- If 1.12.2 cannot safely force numeric biome IDs, document the migration/compatibility reason in Stage 7 closure notes.
- Verify `BiomeDictionary` tags registered in `src/main/java/thaumcraft/common/config/Config.java:308-330` affect `BiomeHandler` aura/tag/greatwood logic.
- Manually inspect Magical Forest/Taint/Eerie/Eldritch colors and decorator behavior after the server-side blockers are fixed; defer only cosmetic mismatches.

**Критерии приемки:**
- [ ] Biome registry names are stable and documented.
- [ ] Legacy biome ID config handling is either implemented or explicitly documented as an unavoidable 1.12.2 migration.
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
- `src/main/java/thaumcraft/common/lib/world/biomes/BiomeMagicalForest.java:57-120`
- `src/main/java/thaumcraft/common/lib/world/biomes/BiomeTaint.java:68-110`

**Референс:**
- `thaumcraft_src/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.class`
- `thaumcraft_src/thaumcraft/common/lib/world/WorldGenGreatwoodTrees.class`
- `thaumcraft_src/thaumcraft/common/lib/world/WorldGenSilverwoodTrees.class`
- `thaumcraft_src/thaumcraft/common/lib/world/WorldGenCustomFlowers.class`
- `thaumcraft_src/thaumcraft/common/lib/world/WorldGenManaPods.class`

**Что не совпадает:**
Reference vegetation runs in surface generation for non-blacklisted biomes, with Silverwood chance `random.nextInt(60) == 3`, Greatwood chance `random.nextInt(25) == 7`, and shimmerleaf-like flower placement in sandy/humid conditions. Current generator only calls `generateGreatwood`/`generateSilverwood` when the sampled biome is exactly Magical Forest: `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:288-292`, so most reference tree generation paths are skipped. Current ore generation uses `WorldGenMinable` vein counts/chances at `src/main/java/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.java:275-285`; reference cinnabar/amber placement is per-block attempts with different height rules, and infused stone chooses aspect metadata partly from biome tag.

**Что нужно доделать:**
Port reference vegetation/ore probability and placement rules, adapted to 1.12.2 APIs, while preserving current block states/metas.

**Как доделать:**
- Move tree generation into a reference-like `generateVegetation` path rather than only Magical Forest chunks.
- Match Silverwood/Greatwood chance gating and `BiomeHandler.getBiomeSupportsGreatwood` logic.
- Restore cinnabar/amber/infused stone placement counts, Y-ranges, and biome-aspect metadata selection.
- Verify `WorldGenGreatwoodTrees`, `WorldGenSilverwoodTrees`, `WorldGenBigMagicTree`, `WorldGenManaPods`, and `WorldGenCustomFlowers` against reference classes after generator-level parity is fixed.

**Критерии приемки:**
- [ ] Greatwood and Silverwood can generate outside Magical Forest where reference conditions allow.
- [ ] Cinnabar, amber, and infused stone use reference-like count/chance/Y/metadata behavior.
- [ ] Biome blacklist levels block trees/ores/structures at the same levels as reference.
- [ ] Generated Silverwood still creates a pure node when reference behavior expects it.
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
`MapBossData` itself matches the simple reference field/key shape (`bossCount`), but static search did not confirm full integration for boss room progression, boss count updates, or persisted boss-room state. Current block placement cancellation checks the Outer Lands dimension through `DimensionManager.getProviderType(-42).getId()` and a comment says to replace with `Config.dimensionOuterId`: `src/main/java/thaumcraft/common/lib/events/EventHandlerWorld.java:211-214`. That hard-coded dimension lookup can be wrong if config changes and is inside Stage 7 boss/Outer Lands safety behavior.

**Что нужно доделать:**
Verify and wire boss-room world data where the reference uses it, and replace hard-coded dimension checks in Stage 7 safety logic with `Config.dimensionOuterId`.

**Как доделать:**
- Search and compare reference boss-room and altar/lock lifecycle code against current `GenBossRoom`, `TileEldritchLock`, boss entities, and `MapBossData` usage.
- Replace hard-coded `-42` dimension safety check with `Config.dimensionOuterId` in `EventHandlerWorld` if implementation work is in scope.
- Validate block placement prevention during active boss fights in Outer Lands.

**Критерии приемки:**
- [ ] `MapBossData` is loaded/saved and updated wherever boss-room progression requires it.
- [ ] Boss-room locks/doors and active boss block-placement prevention work in configured Outer Lands dimension.
- [ ] Changing `dimensionOuterId` in config does not bypass boss-room safety checks.
- [ ] Save/reload does not reset boss-room progression incorrectly.
- [ ] Manual boss-room smoke scenario documents any remaining dependency on Stage 6 entity behavior.

**Риски / зависимости:**
Dependency: boss entity combat behavior belongs mainly to Stage 6. Stage 7 should verify room/world-data integration and label combat AI issues as Stage 6 dependencies.

## 6. Итоговый checklist закрытия Stage 7

- [ ] GAP-1 closed: reference-like worldgen/retrogen pipeline restored and tested.
- [ ] GAP-2 closed: Eldritch ring generation and maze bootstrap match reference behavior.
- [ ] GAP-3 closed: teleporter searches/caches safe portal destinations and manual portal scenario passes.
- [ ] GAP-4 closed: Outer Lands room templates have no gameplay TODO/placeholders and generate traversable rooms.
- [ ] GAP-5 closed: server smoke plus manual new-world/Outer Lands generation validation documented.
- [ ] GAP-6 closed: maze save/load/race safety verified with `labyrinth.dat` persistence.
- [ ] GAP-7 closed or documented: structure query hooks are safe and intentional.
- [ ] GAP-8 closed or documented: biome registration/config ID policy and color/decorator verification complete.
- [ ] GAP-9 closed: tree/plant/ore generation probabilities and placement match reference closely enough.
- [ ] GAP-10 closed or dependency-labeled: boss room world data and dimension safety integration verified.
- [ ] `rg -n "TODO|TBD|placeholder|Replace with|return null|no-op" src/main/java/thaumcraft/common/lib/world src/main/java/thaumcraft/common/lib/world/dim src/main/java/thaumcraft/common/blocks/BlockEldritchPortal.java` reviewed with no unresolved Stage 7 gameplay stubs.
- [ ] `./scripts/dev.sh compileJava` passes after implementation work.
- [ ] `./scripts/dev.sh smoke-server` passes after implementation work.
- [ ] Manual Stage 7 smoke notes include new world generation, Eldritch ring, portal entry, maze traversal, save/reload, and portal return.

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

- Нужно подтвердить точный 1.12.2-safe способ сохранить или документированно заменить legacy numeric biome IDs из `src/main/java/thaumcraft/common/config/Config.java:41-45`.
- Нужно подтвердить, является ли collision-driven teleport in `src/main/java/thaumcraft/common/blocks/BlockEldritchPortal.java:48-78` намеренной 1.12.2 заменой оригинального trigger flow или временной реализацией.
- Нужно определить, какие недостающие room blocks (`blockLootUrn`, `blockLootCrate`, `blockSlabStone`) должны закрываться в Stage 7, а какие являются dependency на content/registration этап, если соответствующие блоки еще не портированы.
