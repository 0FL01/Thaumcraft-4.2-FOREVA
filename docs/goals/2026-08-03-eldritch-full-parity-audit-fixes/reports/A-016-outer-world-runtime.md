# Audit Packet: A-016 - Outer Lands world/runtime parity

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Assignment-ID: A-016
Status: complete
Report-Revision: 1
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Compare Thaumcraft 4.2.3.5 Outer Lands terrain, dimension/provider behavior, maze generation and persistence, dungeon structures and local loot, portal transfer/return, progression environment, and the Eldritch altar, portal, obelisk, cap, lock, crystal, trap, nothing, and crab-spawner blocks/tiles against the Forge 1.12.2 port.
- Anti-scope: Mob AI and drops assigned to A-017; declarations and recipes; generic research infrastructure; client rendering except where block or tile lifecycle affects runtime behavior; unrelated dimensions and world generation.
- Oracle and comparison direction: S-003/S-004 Thaumcraft 4.2.3.5 classes and jar -> S-005 Forge 1.12.2 port. Mapped Forge 1.12.2 bytecode was used to adjudicate dimension-folder, side-solid, and chunk-population platform behavior.
- Questions: Check provider and chunk behavior; seeds and maze layout; structure coordinates, metadata, and local loot; maze persistence and publication; block states and tile lifecycle; portal transfer and safe return; boss-lock progression environment; and server/thread safety.
- Expected evidence: Exact original-class decompilation, direct current-source comparison, mapped Forge bytecode where API behavior matters, focused existing-test inspection, and a command/result record.
- Read/write permissions: Product files and central Goal Ledger files read-only; only this report writable.
- Effort/tool budget: Targeted source, bytecode, and existing-test inspection. No product changes or manual world migration.
- Stop conditions: Every scoped world/runtime surface is compared, all verified deltas and positive parity are explicit, platform adaptations are classified separately, and no product path changes.
- Continuation predecessor: none.

## Coverage Performed

- Port dimension package inspected:
  - `src/main/java/thaumcraft/common/lib/world/dim/WorldProviderOuter.java`
  - `src/main/java/thaumcraft/common/lib/world/dim/ChunkProviderOuter.java`
  - `src/main/java/thaumcraft/common/lib/world/dim/MazeHandler.java`
  - `src/main/java/thaumcraft/common/lib/world/dim/MazeThread.java`
  - `src/main/java/thaumcraft/common/lib/world/dim/MazeGenerator.java`
  - `src/main/java/thaumcraft/common/lib/world/dim/Cell.java`
  - `src/main/java/thaumcraft/common/lib/world/dim/CellLoc.java`
  - `src/main/java/thaumcraft/common/lib/world/dim/GenCommon.java`
  - `src/main/java/thaumcraft/common/lib/world/dim/GenPassage.java`
  - `src/main/java/thaumcraft/common/lib/world/dim/GenPortal.java`
  - `src/main/java/thaumcraft/common/lib/world/dim/GenBossRoom.java`
  - `src/main/java/thaumcraft/common/lib/world/dim/GenKeyRoom.java`
  - `src/main/java/thaumcraft/common/lib/world/dim/GenLibraryRoom.java`
  - `src/main/java/thaumcraft/common/lib/world/dim/GenNestRoom.java`
  - `src/main/java/thaumcraft/common/lib/world/dim/Gen2x2.java`
  - `src/main/java/thaumcraft/common/lib/world/dim/MapBossData.java`
  - `src/main/java/thaumcraft/common/lib/world/dim/TeleporterThaumcraft.java`
- Direct Outer Lands hooks inspected: `ThaumcraftWorldGenerator`, `WorldGenEldritchRing`, `EventHandlerWorld`, `EventHandlerEntity`, and the OCULUS route in `WandManager` where needed to establish generation and transfer timing.
- Blocks inspected: `BlockEldritch`, `BlockEldritchPortal`, `BlockEldritchNothing`, and `BlockCrystal`.
- Tiles inspected: `TileEldritchAltar`, `TileEldritchPortal`, `TileEldritchObelisk`, `TileEldritchCap`, `TileEldritchLock`, `TileEldritchCrystal`, `TileEldritchTrap`, `TileEldritchNothing`, `TileEldritchCrabSpawner`, and `TileCrystal`.
- Original counterparts were decompiled from the read-only `thaumcraft_src/**` classes and `Thaumcraft-1.7.10-4.2.3.5.jar`. The original dimension package, relevant blocks and tiles, `ThaumcraftWorldGenerator`, `WorldGenEldritchRing`, and `WandManager` were covered.
- Mapped Forge 1.12.2 bytecode inspected: `WorldProvider`, `Block`, `BlockStateContainer.StateImplementation`, `ChunkProviderServer`, and `Chunk`.
- Existing focused tests inspected:
  - `OuterDungeonGenerationStaticGuardTest`
  - `OuterLandsArrivalAndSpawnStaticGuardTest`
  - `MazeHandlerPersistenceStaticGuardTest`
  - `MazeGeneratorInvariantTest`
  - `WorldProviderOuterStaticGuardTest`
  - `TaintAndEldritchWorldgenCascadingGuardTest`
  - `BlockEldritchNothingParityTest`
  - `TileEldritchTilesStaticGuardTest`
  - Crystal client/TESR routing tests
- Uncovered execution: No migrated TC4 save was loaded, no deterministic crystal attachment runtime fixture was run, and no manual portal round trip was performed.

## Atomic Findings

### A-016-F01 - Fixed save folder bypasses TC4 Outer Lands data when migration is expected

- Type: unknown, conditional defect
- Severity: high if direct TC4 save migration is supported
- Confidence: high for the folder and persistence mechanics; policy-dependent disposition
- Source/oracle locator: S-003/S-004 `thaumcraft/common/lib/world/dim/WorldProviderOuter.class`, full CFR output; S-005 `src/main/java/thaumcraft/common/lib/world/dim/WorldProviderOuter.java:25-28`, `MazeHandler.java:70-123`, and `src/main/java/thaumcraft/common/lib/events/EventHandlerWorld.java:83-94`; mapped Forge 1.12.2 `net.minecraft.world.WorldProvider.getSaveFolder` bytecode.
- Observed: The port overrides `WorldProviderOuter.getSaveFolder()` and always returns `DIM_OUTERLANDS`, regardless of `Config.dimensionOuterId`.
- Expected: TC4 `WorldProviderOuter` has no save-folder override and therefore uses the normal dimension folder. Mapped Forge 1.12.2 also defaults nonzero dimensions to `DIM` plus the dimension ID. With the default Outer Lands ID `-42`, the compatible folder is `DIM-42`.
- Exact deltas:
  - TC4 provider override: none.
  - Forge-compatible default for dimension `-42`: `DIM-42`.
  - Port override: fixed `DIM_OUTERLANDS`.
  - A configured nondefault Outer Lands ID should select `DIM<id>` under the default contract, but the port still selects `DIM_OUTERLANDS`.
  - `labyrinth.dat` remains in root world storage, wrapped under `Data` with `cells` entries containing `x`, `z`, and `cell`; it is loaded and saved from server dimension 0. The maze map can therefore survive while the dimension region/entity/tile data is ignored.
- Affected paths/symbols: `WorldProviderOuter.getSaveFolder`, `Config.dimensionOuterId`, `MazeHandler.loadMaze`, `MazeHandler.saveMaze`, and the dimension lifecycle handlers in `EventHandlerWorld`.
- Evidence/reproduction: Start with a TC4 save containing modified or progressed Outer Lands chunks under `DIM-42`, then load it with the port. The provider selects `DIM_OUTERLANDS`; the old region/entity/tile data is not selected while root `labyrinth.dat` is still loaded. Existing maze cells can then regenerate into fresh chunks.
- Impact: If direct TC4 save migration is a supported goal, opened locks, defeated bosses, cleared traps, collected key/loot state, and player modifications are lost or reset in regenerated chunks. A nondefault configured dimension ID also fails to isolate its storage by ID.
- Regression hazards: Do not silently switch existing port worlds that already contain data under `DIM_OUTERLANDS`. Any implementation needs an explicit migration, dual-folder selection, or compatibility policy. Preserve the configured dimension ID, `labyrinth.dat` name and format, block/tile NBT keys, and existing-world safety.
- Candidate disposition: `blocking_question`. Promote to `required` if direct TC4 world migration is supported; classify the fixed folder as a benign new-save naming delta only if migration is explicitly unsupported. Existing port-world migration must be adjudicated separately.

### A-016-F02 - Crystal support validation ignores recorded orientation and removes generated Eldritch crystals

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-003/S-004 `thaumcraft/common/blocks/BlockCrystal.class`, CFR neighbor/support methods; S-005 `src/main/java/thaumcraft/common/blocks/BlockCrystal.java:144-170`, `src/main/java/thaumcraft/common/lib/world/dim/GenCommon.java:262-287`, and `src/main/java/thaumcraft/common/blocks/BlockEldritch.java:282-289`; mapped Forge 1.12.2 `Block.isSideSolid`, `Block.isNormalCube`, and `BlockStateContainer.StateImplementation.isSideSolid` bytecode.
- Observed: Port `BlockCrystal` calls one generic `checkAndDropBlock` path from both `onBlockAdded` and `neighborChanged`. `canBlockStay` accepts any one of six side-solid neighbors and does not inspect crystal metadata or `TileCrystal.orientation`.
- Expected: TC4 distinguishes crystal types and recorded attachment. Metas `0..6` require support and then drop when the support selected by the tile orientation ceases to be side-solid. Meta `7` resolves the opposite of the recorded orientation and drops only when that exact support becomes air, allowing attachment to the non-full Eldritch support used by dungeon generation.
- Exact deltas:
  - Ordinary metas `0..6`: recorded-face validation -> any-neighbor validation.
  - Eldritch meta `7`: exact oriented non-air support -> any side-solid support.
  - `GenCommon` places meta `7` at `crystalPos` next to an Eldritch meta `4` block, then writes `TileCrystal.orientation = facing.ordinal()` only after `setBlockState` returns.
  - `BlockCrystal.onBlockAdded` runs during placement before that orientation write and invokes the generic side-solid test.
  - `BlockEldritch.isFullCube` returns false and has no `isSideSolid` override. Forge state `isSideSolid` delegates to the block; default `Block.isSideSolid` eventually returns `isNormalCube`, which requires a full cube. The intended Eldritch support therefore fails the port check.
- Affected paths/symbols: `BlockCrystal.onBlockAdded`, `BlockCrystal.neighborChanged`, `BlockCrystal.checkAndDropBlock`, `BlockCrystal.canBlockStay`, `GenCommon.processDecorations`, `TileCrystal.orientation`, and generated crystal meta `7`.
- Evidence/reproduction:
  - Generation path: deterministically force the `GenCommon` meta `4` decoration and its `nextInt(12) == 0` crystal branch with only the intended Eldritch support. The meta `7` crystal is validated before orientation assignment and drops/removes immediately.
  - Ordinary path: place an oriented meta `0..6` crystal with its intended support plus a second solid neighbor, then remove the intended support. The port retains it because the unrelated neighbor satisfies `canBlockStay`; TC4 drops it.
- Impact: Rare dungeon Eldritch crystal decorations and their shard source disappear during generation. Ordinary crystal clusters can remain attached to the wrong face after their recorded support is removed.
- Regression hazards: Preserve metadata-specific drops, tile class selection, orientation NBT, item and world rendering routes, infusion stabilization, and existing decoration probability. Restore metadata/orientation-specific support semantics or placement ordering; do not make every Eldritch block globally side-solid.
- Candidate disposition: required.

## Positive Parity

### A-016-PC01 - Provider and environment

- Both sides use one Eldritch biome and an empty terrain source.
- Both disable sky light, return celestial angle `0.0`, use purple fog base `0xA080A0` with each channel scaled by `0.15`, report a nonsurface world, and disallow respawn.
- Average ground level remains `50`; X/Z fog, map spin, cloud height, no freezing, no snow, no lightning, and no rain/snow/ice behavior are materially faithful.
- The provider's welcome and departure messages and no-fixed-spawn-coordinate behavior are retained.

### A-016-PC02 - Empty chunks and maze persistence

- Both chunk providers produce empty chunks, fill the biome array with the Eldritch biome, and leave structure placement to registered world generation during population.
- The Eldritch biome performs no natural decoration, so the port's empty `setBlocksInChunk` and `buildSurfaces` preserve the void terrain.
- `labyrinth.dat` remains root-level and keeps wrapper `Data`, list `cells`, and entry keys `x`, `z`, and `cell` with the same packed short values.
- Primary and `_old` backup loading remain. Scoped streams and a direct-write fallback after rename failure strengthen persistence without changing the format.

### A-016-PC03 - Maze seeds, packing, and topology

- `MazeThread` still retries `MazeGenerator` with incrementing seeds until generation succeeds, then publishes positive cells at the same center-relative offsets.
- `Cell` preserves direction bits north `1`, south `2`, east `4`, west `8`, above `16`, below `32`, with the feature in the high byte. `CellLoc` equality and coordinate identity are compatible.
- The center portal remains feature `1`; the four boss-room quadrants remain features `2..5`; one dead end remains key room `6`; nest, library, trapped, crust, taint, and web-spawner variants remain features `7..14` with the original random selection.
- Reciprocal passage connectivity, blocked-area carving, boss-room connection, and temporary feature cleanup are materially faithful.
- `MazeGeneratorInvariantTest` exercises seeds `0..255`, reciprocal connections, portal/boss/key counts, reachability, and representative nest/web features.

### A-016-PC04 - Dungeon structures and local loot

- `GenCommon` retains the 11x11 connection pattern, block-ID-to-metadata mapping, stair orientation mapping, bedrock/void shells, crust and decoration probabilities, crystal candidate selection, crab-spawner placement, and decoration cleanup.
- `GenPortal`, `GenKeyRoom`, `GenLibraryRoom`, `GenNestRoom`, and `GenPassage` preserve room geometry, cardinal openings, base Y `50`, floor/ceiling layers, stairs, pedestals, and feature-specific contents.
- The key room retains the permanent Eldritch key item metadata `2`, difficulty-scaled guardian count, positions, and home radius.
- `GenBossRoom` and `Gen2x2` retain the four-quadrant room, doorway/lock pattern, lock facing, and connected-side selection.
- Urn/crate metadata probabilities, random placement envelopes, crab-spawner candidates, and mind-spider spawner identity are materially faithful.
- Feature `14` now avoids placing web over the center spawner while building the web field. This normalizes repeated original writes but preserves the intended final spawner room.

### A-016-PC05 - Eldritch, portal, and nothing blocks

- `BlockEldritch` retains metas `0..10`, tile routing `0` altar, `1` obelisk, `3` cap, `8` lock, `9` crab spawner, and `10` trap, with no tile on other metas.
- Light, hardness, explosion resistance, knowledge-fragment and XP drops, creature-spawn rejection, lock/altar interaction gates, and the nearby multiblock break cascade are materially faithful.
- `BlockEldritchPortal` remains unbreakable, nonreplaceable, passable, collisionless, tile-backed, and dependent on Eldritch blocks above and below.
- `BlockEldritchNothing` remains unbreakable, invisible, drop-free, collisionless, selectable through an inset box, and damaging to mature noncreative entities with eight out-of-world damage.
- Nothing-block exposure still controls metadata/state and boundary-only `TileEldritchNothing` allocation. The explicit state property and tile refresh logic are the 1.12 representation of the original metadata behavior.

### A-016-PC06 - Eldritch tiles and progression environment

- `TileEldritchAltar` retains `eyes`, `open`, `spawner`, `spawnedClerics`, and `spawntype` NBT semantics, cultist/guardian spawn timing, four-eye and unopened checks, dark-node requirement, and six primal aspects at 100 vis each for opening.
- `TileEldritchPortal` retains five-tick player scans, rider/ridden exclusion, portal cooldown 100, transfer to the configured Outer dimension or back to dimension 0, and `ENTEROUTER` completion only after entry.
- `TileEldritchObelisk` retains its Eldritch-mob regeneration/resistance aura and client effect. Its counter is not incremented in TC4 either, so the port's every-tick modulo-zero server check is original behavior rather than a port defect.
- `TileEldritchCap` remains a non-ticking render tile with the original distance contract.
- `TileEldritchLock` retains facing/count NBT, activation countdown, `BossMapData` rotation, boss-room center/exit discovery, room decoration, boss positions, local loot, nearby airy cleanup, and self-removal.
- `TileEldritchTrap` retains countdown range, three-block target radius, magic damage `2`, and probabilistic temporary warp `1..2`.
- `TileEldritchCrabSpawner` retains activation radius `16`, nearby cap greater than five in a 32-block expansion, vent warning, spawn countdowns, facing, initial spawn, helm removal, and outward motion.
- `TileCrystal.orientation` NBT and meta-to-tile routing remain faithful except for the support lifecycle in A-016-F02.
- Lock NBT defaulting absent `count` to `-1` is a robustness improvement. The lock render-distance reduction is visual/performance-only and does not alter progression.

### A-016-PC07 - Teleport and destination generation

- Transfer target dimensions, portal cooldown, rider restrictions, and post-entry research completion preserve the TC4 gameplay contract.
- Portal search remains bounded to 128 blocks around destination coordinates and uses an Eldritch portal block as the anchor.
- The concern that a first transfer could search only unpopulated empty chunks was not verified. Mapped Forge bytecode shows `ChunkProviderServer.provideChunk` loads or generates a chunk, invokes `Chunk.onLoad`, and calls `Chunk.populate`; the port's destination radius preparation can therefore trigger maze structure population before portal search.
- No additional first-entry void blocker, stale maze publication defect, or verified return-loop defect was found in scope.

### A-016-PC08 - Full scoped parity result

- Outside A-016-F01's migration-policy question and A-016-F02's crystal support lifecycle, the audited provider, terrain, maze, persistence format, structure, block/tile, lock progression, portal transfer, and safe-arrival surfaces are materially faithful or are explicit platform adaptations below.
- No additional high-confidence world/runtime defect was verified in this assignment.

## Intentional Adaptations To Preserve

- Synchronous maze publication: TC4 starts detached `MazeThread` instances and uses temporary zero-valued map reservations. The port runs `MazeThread.run()` synchronously and omits those reservations, keeping shared labyrinth and world-related mutation on the server thread. `OuterDungeonGenerationStaticGuardTest` explicitly rejects background maze threads and reservation sentinels.
- A-010 ownership boundary: TC4's first OCULUS use with no existing maze returns false before vis consumption while its background thread starts. The synchronous port generates and returns true, allowing immediate opening. Preserve synchronous publication; A-010-F01 owns adjudication of the separate first-use timing/consumption delta, so it is not duplicated as an A-016 finding.
- Cascading-generation guard: `WorldGenEldritchRing` preflights its complete footprint with `world.isAreaLoaded(...)`. `TaintAndEldritchWorldgenCascadingGuardTest` preserves this. It can make ring occurrence dependent on loaded neighboring chunks, but prevents unsafe cascading generation in 1.12.
- Safe teleporter: The port reuses one teleporter/cache per live world, prepares destination chunks, searches for a portal, requires a solid floor plus two air blocks for arrival, tries an enclosed Outer interior fallback, and uses an overworld top-block fallback. Natural Outer spawns require a maze cell and a nearby ceiling. `OuterLandsArrivalAndSpawnStaticGuardTest` preserves these safety contracts.
- Global-state cleanup: `ChunkProviderOuter.populate` restores `BlockFalling.fallInstantly` in `finally`; this prevents leaked global falling-block state after an exception.
- 1.12 collision/state representation: Non-null zero-size AABBs replace nullable no-collision boxes where 1.12 callers require an object. Block states replace metadata-only exposure/type storage, with explicit dirty/update synchronization.
- Minor benign deltas: Creative players retain a consumed lock key, successful block activation returns true to consume the 1.12 interaction, lock missing-count NBT defaults safely to `-1`, and lock render distance is reduced for performance.

## Unknowns and Conflicts

- Direct TC4 save migration policy is not frozen in the audited sources. That policy controls whether A-016-F01 is a required compatibility defect or a benign new-save folder choice.
- Existing port worlds may already contain Outer Lands data under `DIM_OUTERLANDS`. A migration fix aimed only at `DIM-42` could lose those worlds; both directions require explicit handling.
- A-010-F01 establishes the original first-use OCULUS timing. A-016 establishes that detached publication must not be restored. The implementation must satisfy both decisions after central adjudication.
- The safe overworld fallback may place a returning player at the top of the obelisk when no same-level safe portal approach is found. No manual round trip proved harmful placement, so this remains test debt rather than a defect.
- A-016-F02 is established statically from placement order and mapped Forge solidity behavior, but no in-game deterministic crystal reproduction was run.

## Test Debt

- A-016-F01: Add a migrated-save fixture covering a populated `DIM-42`, an existing `DIM_OUTERLANDS`, root `labyrinth.dat` consistency, and a nondefault configured Outer dimension ID. The test must encode an explicit migration policy rather than silently preferring one folder.
- A-016-F02: Add deterministic block lifecycle tests for a meta `7` crystal attached to non-air but non-side-solid Eldritch meta `4`, placement-time callback ordering, and ordinary metas detaching when their recorded support is removed despite another solid neighbor.
- Add focused structure snapshots or block/meta invariants for each feature room. Current tests strongly guard publication and selected contracts but do not compare every generated room footprint.
- Add a server runtime portal round-trip test covering first-entry chunk population, enclosed arrival, return placement, no void/fall spawn, cooldown, and portal reuse.
- Add direct persistence round-trip coverage for all maze coordinates and packed feature values across primary and backup files; current coverage is primarily static.
- Existing tests do not turn A-016-F01 or A-016-F02 into executable regression controls.

## Commands And Results

All commands were run from the repository root. Original artifacts remained read-only. CFR warnings about absent 1.7.10 dependencies did not prevent the relevant methods from decompiling.

```text
git status --short
jar tf Thaumcraft-1.7.10-4.2.3.5.jar | grep -E '^thaumcraft/common/(lib/world/dim|blocks/BlockEldritch|tiles/TileEldritch)' | sort
/usr/local/bin/cfr --silent true --showversion false --extraclasspath Thaumcraft-1.7.10-4.2.3.5.jar thaumcraft_src/thaumcraft/common/blocks/BlockCrystal.class
/usr/local/bin/cfr --silent true --showversion false --extraclasspath Thaumcraft-1.7.10-4.2.3.5.jar thaumcraft_src/thaumcraft/common/lib/world/dim/WorldProviderOuter.class
cfr thaumcraft_src/thaumcraft/common/lib/world/dim/ChunkProviderOuter.class --silent true
cfr thaumcraft_src/thaumcraft/common/lib/world/dim/MazeGenerator.class --silent true
cfr thaumcraft_src/thaumcraft/common/lib/world/dim/MazeHandler.class --silent true
cfr thaumcraft_src/thaumcraft/common/lib/world/dim/MazeThread.class --silent true
cfr thaumcraft_src/thaumcraft/common/lib/world/dim/TeleporterThaumcraft.class --silent true
cfr thaumcraft_src/thaumcraft/common/lib/world/dim/GenCommon.class --silent true
cfr thaumcraft_src/thaumcraft/common/lib/world/dim/GenPassage.class --silent true
cfr thaumcraft_src/thaumcraft/common/lib/world/dim/GenPortal.class --silent true
cfr thaumcraft_src/thaumcraft/common/lib/world/dim/GenBossRoom.class --silent true
cfr thaumcraft_src/thaumcraft/common/lib/world/dim/GenKeyRoom.class --silent true
cfr thaumcraft_src/thaumcraft/common/lib/world/dim/GenLibraryRoom.class --silent true
cfr thaumcraft_src/thaumcraft/common/lib/world/dim/GenNestRoom.class --silent true
cfr thaumcraft_src/thaumcraft/common/blocks/BlockEldritch.class --silent true
cfr thaumcraft_src/thaumcraft/common/blocks/BlockEldritchPortal.class --silent true
cfr thaumcraft_src/thaumcraft/common/blocks/BlockEldritchNothing.class --silent true
cfr thaumcraft_src/thaumcraft/common/tiles/TileEldritchAltar.class --silent true
cfr thaumcraft_src/thaumcraft/common/tiles/TileEldritchPortal.class --silent true
cfr thaumcraft_src/thaumcraft/common/tiles/TileEldritchObelisk.class --silent true
cfr thaumcraft_src/thaumcraft/common/tiles/TileEldritchLock.class --silent true
cfr thaumcraft_src/thaumcraft/common/tiles/TileEldritchTrap.class --silent true
cfr thaumcraft_src/thaumcraft/common/tiles/TileEldritchNothing.class --silent true
cfr thaumcraft_src/thaumcraft/common/tiles/TileEldritchCrabSpawner.class --silent true
cfr thaumcraft_src/thaumcraft/common/lib/world/ThaumcraftWorldGenerator.class --silent true
cfr thaumcraft_src/thaumcraft/common/lib/world/WorldGenEldritchRing.class --silent true
cfr thaumcraft_src/thaumcraft/common/items/wands/WandManager.class --silent true
javap -classpath /home/stfu/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeBin-1.12.2-14.23.5.2847.jar -c -p net.minecraft.world.WorldProvider
javap -classpath /home/stfu/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeBin-1.12.2-14.23.5.2847.jar -c -p net.minecraft.block.Block
javap -classpath /home/stfu/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeBin-1.12.2-14.23.5.2847.jar -c -p net.minecraft.block.state.BlockStateContainer$StateImplementation
javap -classpath /home/stfu/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeBin-1.12.2-14.23.5.2847.jar -c -p net.minecraft.world.gen.ChunkProviderServer
javap -classpath /home/stfu/.gradle/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeBin-1.12.2-14.23.5.2847.jar -c -p net.minecraft.world.chunk.Chunk
./scripts/dev.sh test
```

- Initial audit status: clean; no output from `git status --short`.
- CFR result: Corresponding original provider, chunk, maze, structure, block, tile, transfer, and progression methods were recovered with sufficient control flow and constants for comparison.
- Forge bytecode result: Default nonzero save folder is `DIM<dimensionId>`; state side solidity delegates to the block and ordinary blocks ultimately use normal-cube semantics; `provideChunk` loads/generates and participates in chunk population.
- Test result: `BUILD SUCCESSFUL in 6s`; 9 actionable tasks, 3 executed and 6 up-to-date.
- Runtime smoke: Not required and not run because this was a read-only audit/report with no runtime product change.
- Manual validation: Migrated-save and in-game crystal/portal reproductions were not run.
- Product diff: none.

## Handoff

- Terminal status: complete.
- Material finding index: A-016-F01 conditional high `DIM-42`/`DIM_OUTERLANDS` migration question; A-016-F02 medium crystal orientation/support defect.
- Positive parity index: A-016-PC01 provider/environment; A-016-PC02 empty chunks/persistence; A-016-PC03 maze seeds/topology; A-016-PC04 structures/local loot; A-016-PC05 Eldritch/portal/nothing blocks; A-016-PC06 tiles/progression; A-016-PC07 teleport/population; A-016-PC08 full scoped result.
- Adaptation index: synchronous maze publication, A-010 timing boundary, cascading-generation preflight, safe teleporter/spawn restrictions, global-state cleanup, 1.12 state/AABB conversion, and minor interaction/performance deltas.
- Exact continuation point: Packet is ready for central normalization. A-016-F01 requires migration-policy adjudication; A-016-F02 can be promoted with its focused lifecycle test debt. No further A-016 discovery is required first.
- Smallest next action if continued: Normalize both findings, all eight preserve controls, the migration conflict, and test debt into central RECON without editing product code during report fan-out.
