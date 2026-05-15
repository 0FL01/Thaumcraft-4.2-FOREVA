# Durable Goal Progress

Last updated: 2026-05-14
Branch: `codex/durable-goal-stage8-9`

## Contract Checklist

- [x] Read source-of-truth files: `AGENTS.md`, `docs/PRD.md`, `docs/GOAL.md`, `build.gradle`, `Dockerfile`.
- [x] Read active stage plans: `docs/Stage3.md` through `docs/Stage9-e.md`.
- [x] Start from `git status --short`: clean at recon start.
- [x] Work on a dedicated branch instead of `master`/`main`.
- [x] Establish baseline non-GUI validation.
- [ ] Close or explicitly classify remaining Stage 3-7 blockers before Stage 8/9 parity claims.
- [ ] Implement Stage 8-a client bootstrap and side-safe boundaries.
- [ ] Implement Stage 8-b GUI routing/screens/resources to non-GUI-verifiable completion.
- [ ] Implement Stage 8-c tile/block render registration/classes/resources to non-GUI-verifiable completion.
- [ ] Implement Stage 8-d entity render registration/classes/resources to non-GUI-verifiable completion.
- [ ] Implement Stage 8-e FX/particles/beams/bolts/shader-adjacent safe fallbacks.
- [ ] Implement Stage 9-a recipe/content foundation.
- [ ] Implement Stage 9-b arcane crafting parity.
- [ ] Implement Stage 9-c infusion crafting/enchanting parity.
- [ ] Implement Stage 9-d crucible/alchemy/thaumatorium content flow.
- [ ] Implement Stage 9-e research/Thaumonomicon progression content.
- [ ] Complete Phase 10 polish: localization, sounds, crash cleanup, performance sanity, docs/status cleanup.
- [ ] Run final non-GUI validation: `compileJava`, `build`, `test`, `check-jar`, `smoke-server`, static scans, `git diff --check`.
- [ ] Record GUI/manual graphics checks as skipped by user instruction where they require user interaction or unavailable graphics.
- [ ] End with clean `git status --short` after intended checkpoint commits, or document remaining diffs.

## Current Evidence

- `git status --short`: clean at the start of this run.
- Active branch was created from clean `master`: `codex/durable-goal-stage8-9`.
- `docs/Stage3.md` contains fresh-world core closure notes dated 2026-05-14. Remaining Stage 3 items are documented dependencies/manual scenario coverage, not a pre-Phase8 implementation blocker.
- `docs/Stage4.md` contains fresh-world server/common closure notes dated 2026-05-14. Remaining Stage 4 items are Phase 8/9 dependencies or explicitly deferred reference pieces.
- `docs/Stage5.md` remains open. Recent closure notes show substantial wand, pouch, hover harness, item resource, ItemKey, Compass Stone, Loot Bag, and focus parity work, but remaining common/server utility, relic, focus, hover, and manual scenario validation gaps remain.
- `docs/Stage6.md` remains open. Golem/trunk item routes, Pech behavior, Cultist Portal loot, boss/projectile validation, drops/sounds, and manual scenario matrix remain active blockers.
- `docs/Stage7.md` remains open. Worldgen pipeline, Eldritch ring/maze bootstrap, safe teleporter, Outer Lands room templates, BlockLoot/room block contracts, Greatwood structures, and runtime evidence remain active blockers.
- Stage 8-a through Stage 8-e are documented as not complete.
- Stage 9-a through Stage 9-e are documented as not complete.
- `docs/GOAL_PROGRESS.md` was absent at recon start and is now created to track this contract.

## Skipped GUI/Manual Graphics Checks

- None attempted in this run yet.
- Future GUI/client visual checks that require user-driven Minecraft control, screenshots, or unavailable X11/graphics stack will be recorded as: `SKIPPED by user instruction: GUI/graphics/user-interactive validation excluded`.

## Baseline Validation

- `./scripts/dev.sh compileJava` — passed on 2026-05-14 before gameplay/code changes.

## Checkpoint Log

### 2026-05-14 — Stage 6/7 BlockLoot urn/crate path

Scope:

- Ported `BlockLoot` and `BlockLootItem` for the three reference rarity metas.
- Registered `blockLootUrn` and `blockLootCrate` with item blocks.
- Copied original urn/crate textures from `thaumcraft_src/assets/thaumcraft/textures/blocks/`.
- Added simple Forge 1.12.2 blockstate/model fallbacks so the blocks resolve as resources until Stage 8 renderer parity work.
- Moved shared loot reward generation into `Utils.generateLoot(...)`.
- Replaced the Cultist Portal stage 0 vanilla chest placeholder with `blockLootCrate`.
- Replaced Outer Lands `GenCommon`/`GenNestRoom` urn/crate placeholders with `blockLootUrn`/`blockLootCrate`.

Validation:

- `./scripts/dev.sh compileJava` — initially failed because `BlockLoot.getSubBlocks(...)` used a non-existent 1.12.2 `Block.isInCreativeTab(...)`; fixed during the checkpoint.
- `./scripts/dev.sh compileJava` — passed after the fix.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state at 180s; no new crash reports and no mod-load crash marker in `run/smoke-server.log`.
- `THAUMCRAFT_SMOKE_TIMEOUT=300s ./scripts/dev.sh smoke-server` — failed by timeout before ready state; log stopped immediately after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors and no new crash reports.
- Clean recon commit `da3f307` was checked in `/tmp/tc-baseline-smoke` with `THAUMCRAFT_GRADLE_HOME='/home/opencode/ai/Thaumcraft-4.2-FOREVA/?/.gradle' ./scripts/dev.sh smoke-server`; it reproduced the same timeout before mod loading with no crash reports or crash markers. The runtime smoke failure is therefore classified as pre-existing smoke wrapper/runtime environment failure, not evidence of the BlockLoot diff crashing mod load.

Remaining limits:

- This closes the direct vanilla chest/stone substitution for the shared urn/crate path only.
- Full loot-table parity still depends on completing/populating the broader loot registration tables currently deferred to Stage 9/content work.
- Stage 6 Cultist Portal stage progression/reward scenario was not manually run by user instruction.
- Stage 7 full Outer Lands room traversal, `blockSlabStone`, other room templates, and worldgen runtime evidence remain open.
- GUI/client visual validation for the new model fallbacks was not run.

### 2026-05-14 — Stage 7 cosmetic stone slab room block

Scope:

- Ported the reference `blockCosmeticSlabStone`/`blockCosmeticDoubleSlabStone` pair as Forge 1.12.2 `BlockSlab` implementations.
- Registered the single and double slab blocks; registered the `ItemSlab` bridge for the single slab item.
- Replaced `GenLibraryRoom`'s vanilla `Blocks.DOUBLE_STONE_SLAB` helper with `ConfigBlocks.blockSlabStone.getStateFromMeta(meta)`.
- Copied the reference slab source textures `arcane_stone.png` and `es_1.png` from `thaumcraft_src/assets/thaumcraft/textures/blocks/`.
- Added temporary Forge 1.12.2 blockstate/model fallbacks for arcane/eldritch bottom, top, and double slab states.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped immediately after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors, no new crash reports, and no mod-load crash markers. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- Direct `blockSlabStone` room-block substitution is closed for `GenLibraryRoom`.
- Full Outer Lands room traversal/manual generation validation remains skipped by user instruction.
- Other Stage 7 room/worldgen placeholders still need separate reference audits.
- Client visual/model inspection was not run; JSONs are resource fallbacks, not a renderer parity claim.

### 2026-05-14 — Stage 7 Eldritch room block metadata contract

Scope:

- Expanded `BlockEldritch` from the current `0..4` meta clamp to the reference `0..10` meta range used by Outer Lands rooms.
- Restored server-side tile entity creation for metas `0` altar, `1` obelisk, `3` cap, `8` lock, `9` crab spawner, and `10` trap.
- Restored reference-like hardness, resistance, light, drop, XP, and no-creature-spawn behavior for the expanded metas.
- Added temporary resource fallbacks for `blockeldritch` meta variants and copied the matching original textures from `thaumcraft_src/assets/thaumcraft/textures/blocks/`.

Validation:

- `./scripts/dev.sh compileJava` — initially failed on a duplicate `damageDropped(...)` method left from the current implementation; fixed during the checkpoint.
- `./scripts/dev.sh compileJava` — passed after the fix.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped immediately after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors, no new crash reports, and no mod-load crash markers. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- This closes the metadata/TE instantiation contract needed by generated room blocks, but not full `TileEldritchAltar`, `TileEldritchLock`, `TileEldritchCrabSpawner`, or `TileEldritchTrap` behavioral parity.
- Renderer parity for altar/obelisk/cap/lock/crab spawner/trap remains Stage 8 work; JSONs are non-GUI resource fallbacks only.
- Full Outer Lands room traversal/manual generation validation remains skipped by user instruction.

### 2026-05-14 — Stage 7 safe Outer Lands teleporter placement

Scope:

- Replaced fixed `y=60` placement in `TeleporterThaumcraft` with reference-like search for the nearest `ConfigBlocks.blockEldritchPortal` within 128 blocks.
- Added per-teleporter portal-position caching through the inherited Forge 1.12.2 `destinationCoordinateCache`.
- Zeroed entity velocity on placement and moved arrivals beside the found portal instead of inside an arbitrary fixed column.
- Added a safe top-position fallback when no existing portal can be found, avoiding silent placement at a hardcoded Y coordinate.
- Added null target-world guards in `BlockEldritchPortal` before calling `changeDimension(...)`.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped immediately after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors, no new crash reports, and no mod-load crash markers. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- Safe teleporter placement is advanced, but GAP-3 is not fully closed until trigger ownership and manual portal traversal evidence are handled.
- Portal generation/maze placement is not proven by this checkpoint.

### 2026-05-14 — Stage 7 Eldritch portal trigger ownership

Scope:

- Ported `TileEldritchPortal` to a tickable server-side trigger matching the original ownership model.
- Added a 5-tick player scan around the portal block, mounted/ridden player guards, and the original 100-tick player portal cooldown behavior.
- Moved dimension transfer out of `BlockEldritchPortal` collision handling and into the tile entity.
- Preserved the safe `TeleporterThaumcraft` placement/search path from the previous checkpoint for both Outer Lands entry and Overworld return.
- Granted `ENTEROUTER` through the current `ResearchManager` when a player enters the Outer Lands for the first time.
- Made the portal block pass-through, non-replaceable, and unbreakable, aligning it with the original non-colliding portal block contract.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped immediately after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors, no new crash reports, and no mod-load crash markers. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- Runtime portal traversal was not manually run because user-driven GUI/client control is excluded from this durable goal.
- The smoke-server environment still has the pre-existing timeout before Forge reaches ready state; full runtime proof must remain blocked until that environment issue is resolved.
- GAP-3 is advanced but not fully closed because arrival beside a generated `GenPortal` room still depends on Stage 7 ring/maze/room generation validation.

### 2026-05-14 — Stage 7 Eldritch ring and maze bootstrap

Scope:

- Replaced the placeholder Overworld `WorldGenEldritchRing` obsidian-circle-plus-portal layout with a reference-like support pad, altar, cap, obelisk, and banner layout.
- Added reference-like surface validation for ring spawn points and `MazeHandler.mazesInRange(...)` overlap checks using ring `chunkX`, `chunkZ`, `width`, and `height`.
- Moved ring maze bootstrap back into the surface structure branch with odd random `11..21` maze dimensions and `MazeThread(chunkX, chunkZ, width, height, random.nextLong())`.
- Restored reference-like sea-level/top-height gating and dark/eerie aura node creation above the ring altar.
- Added minimal persistent `TileEldritchAltar` state for `spawner`, `spawntype`, `spawnedClerics`, `open`, and `eyes`.
- Added minimal persistent `TileBanner.facing` state so generated ring banners can keep their orientation.

Validation:

- `./scripts/dev.sh compileJava` — initially failed because ring-local `bx`/`bz` variables shadowed later hilltop variables in `generateStructures(...)`; fixed during the checkpoint.
- `./scripts/dev.sh compileJava` — passed after the fix.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped immediately after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors, no new crash reports, and no mod-load crash markers. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- Full `TileEldritchAltar` cultist/guardian spawning and portal-opening behavior was not ported by this ring checkpoint; the following altar checkpoint handles the spawner part while portal opening remains open.
- Runtime ring discovery, `labyrinth.dat` save evidence, and portal traversal were not manually run because user-driven GUI/client control is excluded from this durable goal and the smoke-server environment remains blocked by the known pre-Forge timeout.
- Hilltop altar, mound/barrow, normal/spider Greatwood, and broader surface worldgen parity remain open Stage 7 work.

### 2026-05-14 — Stage 7 Eldritch altar spawner lifecycle

Scope:

- Ported the server-side `TileEldritchAltar` tick loop for generated altar spawners.
- Restored persistent altar fields for `spawner`, `spawnedClerics`, `spawntype`, `eyes`, and `open`.
- Added ritual cleric spawning for spawn type `0`, including ritualist flagging and altar home positions.
- Added follow-up guard spawning for spawn type `0` once ritual clerics exist, with nearby cultist caps.
- Added Eldritch Guardian spawning for spawn type `1`, with home positions and persistence.
- Added the reference-like `checkForMaze()` helper that starts a new odd-sized maze thread when no maze exists around the altar.

Validation:

- `./scripts/dev.sh compileJava` — initially failed because the first 1.12.2 adaptation used unavailable `World.doesBlockHaveSolidTopSurface(...)` and typed the spawn helper as `EntityLiving`, which does not expose `setHomePosAndDistance(...)`; fixed during the checkpoint.
- `./scripts/dev.sh compileJava` — passed after switching to `IBlockState.isSideSolid(...)` and `EntityCreature`.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped immediately after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors, no new crash reports, and no mod-load crash markers. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- Portal-opening behavior that consumes altar eyes/open state is still not implemented.
- Cultist/guardian spawning has not been observed in a runtime world because smoke/manual runtime validation remains environment-blocked or excluded as user-driven client control.
- Stage 6 entity combat behavior can still affect full in-world altar scenario parity.

### 2026-05-14 — Stage 6 Cultist Portal banner facing

Scope:

- Removed the `TileBanner.setFacing()` TODO from `EntityCultistPortal` now that `TileBanner` has persistent facing state.
- Ported the reference stage-0 banner facing mapping for the four generated portal banners.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped immediately after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors, no new crash reports, and no mod-load crash markers. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- Cultist Portal stage progression has not been observed in runtime/manual validation because user-driven scenarios are excluded and smoke-server remains environment-blocked.
- This only closes the direct banner-facing TODO; full boss/minion/drop/loot distribution parity remains open.

### 2026-05-14 — Stage 7 Eldritch crab spawner and trap server ticks

Scope:

- Ported `TileEldritchCrabSpawner` from its facing-only shell to the reference server tick loop.
- Restored the crab spawner's randomized startup tick, countdown/reset cadence, 16-block player activation check, 32-block nearby-crab cap, warning block event, fizz/gore sounds, and facing-based `EntityEldritchCrab` spawn with outward motion.
- Kept `facing` under the original NBT key and moved the tile to `TileThaumcraft` custom NBT like the reference.
- Ported `TileEldritchTrap` from an empty shell to the reference server tick pulse: 10-34 tick reset, 3-block player check, 2 magic damage, and 50% temporary warp application.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors, no new crash reports, and no configured crash markers. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- Client vent particles and trap zap packet visuals are still Stage 8 FX work; `PacketFXBlockZap` currently has no payload constructor/handler.
- Spawn/trap behavior has not been observed in an Outer Lands runtime traversal because smoke-server remains environment-blocked and user-driven client/manual scenarios are excluded.
- `TileEldritchLock`, boss-room lifecycle, full room traversal, and broader room-template audit remain open Stage 7 work.

### 2026-05-14 — Stage 7 hilltop wisp altar template

Scope:

- Replaced the simplified `WorldGenHilltopStones` stonebrick-ring/empty-chest placeholder with the reference-like hilltop altar structure.
- Restored surface validation through `LocationIsValidSpawn(...)`, including high-altitude gating and air/surface-cover handling.
- Restored the seven-by-seven obsidian/obsidian-totem footprint, perimeter columns, optional vines, dungeon loot chest, and central mob spawner.
- Mapped the original `Thaumcraft.Wisp` spawner target deliberately to the 1.12.2 entity registry id `thaumcraft:wisp`.
- Updated the surface structure call site so the companion aura node is only created when the hilltop altar successfully generates.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors, no new crash reports, and no configured crash markers. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- `WorldGenMound` is still the simplified placeholder and remains a Stage 7 GAP-11 blocker.
- The full reference `generateSurface(...)` control flow/chance semantics remain under GAP-1, so this checkpoint restores hilltop contents but not the entire surface worldgen pipeline.
- Hilltop generation has not been observed in a fresh runtime world because smoke-server remains environment-blocked and user-driven client/manual scenarios are excluded.

### 2026-05-14 — Stage 7 spider Greatwood contents

Scope:

- Added the reference-style `WorldGenGreatwoodTrees.generate(..., boolean spiders)` path.
- Restored the default `random.nextInt(8) == 0` spider-variant chance.
- Restored the variant contents: cave-spider spawner under the trunk, up to 50 webs placed adjacent to Greatwood logs/leaves, and a dungeon-loot chest below the spawner.
- Mapped the original `CaveSpider` spawner target to the 1.12.2 entity registry id `minecraft:cave_spider`.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors, no new crash reports, and no configured crash markers. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- The full reference vegetation pipeline is still open under GAP-1, so Greatwood/Silverwood natural placement rates and allowed biome coverage are not closed by this checkpoint.
- Silverwood parameter/chance parity and ore placement parity remain open Stage 7 work.
- Spider Greatwood has not been observed in a fresh runtime world because smoke-server remains environment-blocked and user-driven client/manual scenarios are excluded.

### 2026-05-14 — Stage 7 surface vegetation and wild aura baseline

Scope:

- Updated `ThaumcraftWorldGenerator` to sample biome data from the chunk center, matching the reference surface-generation entry point more closely.
- Restored a `generateVegetation(...)` path with the reference outer Silverwood `random.nextInt(60) == 3` and Greatwood `random.nextInt(25) == 7` chance gates.
- Allowed Greatwood/Silverwood generation outside Magical Forest when current `BiomeHandler`/biome checks allow it, rather than only calling tree generation for exact Magical Forest chunks.
- Switched Silverwood worldgen to the reference `WorldGenSilverwoodTrees(false, 7, 4)` constructor parameters and removed the extra current-port 5% inner chance.
- Added wild aura node generation gated by `Config.genAura`/`regenAura` and `Config.nodeRarity`, using `createRandomNodeAt(..., false, false, false)` independently of Silverwood trees.
- Restored flat-world skip behavior for tree/structure generation and reintroduced dimension blacklist level distinction for normal surface tree/structure generation versus ore/aura generation.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors, no new crash reports, and no configured crash markers. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- GAP-1 is still not closed: Nether node/totem generation, scattered-feature structure nodes, `newGen`/regen marker parity, full ore placement parity, and runtime evidence remain open.
- Wild aura node placement has not been observed in a fresh runtime world because smoke-server remains environment-blocked and user-driven client/manual scenarios are excluded.
- The simplified mound/barrow structure remains open under GAP-11.

### 2026-05-14 — Stage 7 structure nodes, totems, and Nether aura baseline

Scope:

- Added a `structureNode` cache to `ThaumcraftWorldGenerator` and restored scattered-feature aura node placement through `MapGenScatteredFeature#getNearestStructurePos(...)`.
- Threaded `auraGen` through scattered-feature, wild-node, structure-node, and totem generation so successful aura-source placement suppresses later duplicate node paths in the same chunk.
- Reintroduced totem pillar generation with the reference substrate checks, snow/tallgrass clearance, obsidian-totem base, obsidian-tile shaft, and node-stone cap/random mid-column node placement.
- Stopped skipping dimension `-1` in the top-level generator and added a Nether path for wild aura nodes plus the reference Nether-specific totem top-Y branch when structure generation is enabled.
- Kept End generation skipped and moved the wild-node fallback height from sea level to `WorldProvider#getAverageGroundLevel()`, matching the reference source more closely.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors. `run/crash-reports/` does not exist, and the configured crash-marker scan found no matches. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- GAP-1 is still not closed: `newGen`/regen chunk dirty-marker parity, full ore placement parity, flower placement parity, biome blacklist edge cases, and runtime generation evidence remain open.
- Nether aura/totem and scattered-feature node placement have not been observed in a fresh runtime world because smoke-server remains environment-blocked and user-driven client/manual scenarios are excluded.
- The simplified mound/barrow structure remains open under GAP-11.

### 2026-05-14 — Stage 7 ore and flower placement parity

Scope:

- Replaced the current generic ore-vein calls in `ThaumcraftWorldGenerator` with a reference-like `generateOres(...)` path.
- Restored cinnabar to 18 single-block stone replacement attempts in the lower fifth of world height.
- Restored amber to 20 single-block stone replacement attempts near terrain height minus up to 24 blocks.
- Restored infused ore to eight six-block `WorldGenMinable` veins with a one-in-three chance to select the primal meta from the local biome aspect.
- Made ore generation skip biome blacklist levels `0` and `2`, matching the original `generateOres(...)` branch.
- Restored the humid-sand flower generation branch in `generateVegetation(...)` and added the exact-position `generateFlowers(...)` overload used by the reference generator.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors. `run/crash-reports/` does not exist, and the configured crash-marker scan found no matches. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- GAP-1 is still not closed: `newGen`/regen chunk dirty-marker parity, broader biome blacklist/runtime edge cases, and runtime generation evidence remain open.
- Ore and flower placement have not been observed in a fresh runtime world because smoke-server remains environment-blocked and user-driven client/manual scenarios are excluded.
- The simplified mound/barrow structure remains open under GAP-11.

### 2026-05-14 — Stage 7 mound/barrow functional layout

Scope:

- Replaced the small procedural `WorldGenMound` placeholder with a bounded 19x19 barrow generator using the reference validation points.
- Added a buried cobblestone/mossy-cobblestone chamber, dirt/grass mound shell, and open central node handoff position.
- Placed loot urn/crate blocks at the reference offsets with matching meta probabilities and crate/urn split.
- Added a dungeon-loot-table chest at the reference chest offset, including the original trapped-chest/TNT chance.
- Configured skeleton and zombie mob spawners at the reference offsets.
- Reworked `ThaumcraftWorldGenerator.generateStructures(...)` to use the reference-like mutually exclusive mound/ring/hilltop chance ordering from the shared `getHeight(...) - 9` branch instead of the old independent Magical Forest/Taint-only mound chance.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors. `run/crash-reports/` does not exist, and the configured crash-marker scan found no matches. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- The mound shell is a compact functional equivalent rather than the exact 2,500-line fixed block dump from the 1.7.10 class.
- Mound/barrow generation has not been observed in a fresh runtime world because smoke-server remains environment-blocked and user-driven client/manual scenarios are excluded.
- GAP-1 still has `newGen`/regen chunk dirty-marker parity and broader runtime edge cases open.

### 2026-05-14 — Stage 7 Eldritch lock boss trigger baseline

Scope:

- Restored `BlockEldritch` meta `8` activation with `ItemEldritchObject` meta `2`, including lock countdown start, block update, item consumption outside creative mode, and runic shield charge sound.
- Changed `TileEldritchLock` to the same `TileThaumcraft` custom NBT/update path used by the original tile and persisted/synced `facing` plus `count`.
- Restored the server-side 100-tick pump countdown, ice completion sound, nearby airy-door clearing, and lock removal.
- Wired `BossMapData` load/create/update and the original `bossCount % 4` boss selection with the 25% extra-count chance.
- Spawned Warden, Golem, Cultist Portal, and five Taintacle/TaintacleGiant boss patterns at reference-like room anchors, including Warden/Golem initial spawn hooks.

Validation:

- `./scripts/dev.sh compileJava` — initially passed after removing bad imports/redundant direct home calls, then passed again after switching the lock to `TileThaumcraft` and refining spawn anchors.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors. `run/crash-reports/` does not exist, and the configured crash-marker scan found no matches. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- Full reference boss-room block mutation is not ported: obelisk/trap/urn/crate scatter, cultist-room decoration, taint/biome patching, and sparkle packets remain open.
- Boss-room activation and save/reload progression have not been observed in a fresh runtime world because smoke-server remains environment-blocked and user-driven client/manual scenarios are excluded.
- Stage 8 client lock sparkle/render parity remains open.

### 2026-05-14 — Stage 7 Eldritch altar eye and portal activation

Scope:

- Restored `BlockEldritch` meta `0` activation with `ItemEldritchObject` meta `0` while not sneaking.
- Restored altar eye insertion: increments `TileEldritchAltar.eyes`, consumes the eye item, syncs the tile, plays the crystal sound, and calls `checkForMaze()`.
- Restored the reference guardian-spawner transition when adding the third and fourth eyes by setting altar `spawner` and `spawntype = 1`.
- Made `TileEldritchAltar` wand-activatable for the portal-opening path.
- Restored the portal-opening gates: `OCULUS` research, exactly four eyes, not already open, dark `TileNode` above the altar, existing maze from `checkForMaze()`, and six primal aspects at 100 vis each through `consumeAllVisCrafting(...)`.
- On success, sets altar `open`, removes the dark node, places `blockEldritchPortal` above the altar, syncs the tile, and plays the wand sound.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors. `run/crash-reports/` does not exist, and the configured crash-marker scan found no matches. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- Generated ring activation has not been observed in a fresh runtime world because smoke-server remains environment-blocked and user-driven client/manual scenarios are excluded.
- The async case where `checkForMaze()` has just started a `MazeThread` still needs runtime/save validation before GAP-2/GAP-3 can close.

### 2026-05-14 — Stage 7 retrogen newGen marker execution

Scope:

- Added `ThaumcraftWorldGenerator.worldGeneration(Random, int, int, World, boolean newGen)` as the reference-style fresh/regen entry point.
- Routed Forge fresh world generation through `worldGeneration(..., true)`.
- Routed queued chunk regeneration through `worldGeneration(..., false)` from `ServerTickEventsFML`.
- Restored fresh-vs-regen gates for aura, structures, trees, cinnabar, amber, infused stone, and Nether branches as `Config.genX && (newGen || Config.regenX)`.
- Marked non-fresh generated chunks dirty after the generation pass.
- Avoided duplicate `ChunkLoc` queue entries when the same missing-marker chunk loads repeatedly before the queue drains.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors. `run/crash-reports/` does not exist, and the configured crash-marker scan found no matches. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- Fresh-world and retrogen runtime scenarios have not been observed because smoke-server remains environment-blocked and user-driven client/manual scenarios are excluded.
- Broader biome blacklist/runtime edge cases remain open until generation can be observed in-world.

### 2026-05-14 — Stage 7 key room arch and guardian count parity

Scope:

- Restored the reference key-room inner wall/arch block selection so default walls use `ROCK`, side arch detail uses `STONE_NOSPAWN`, and only the center arch slot remains open.
- Restored key-room guardian count to two base guardians, plus one on Normal or two on Hard.
- Restored the HARD-mode champion guardian path by making the guardian count reach four.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors. `run/crash-reports/` does not exist, and the configured crash-marker scan found no matches. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- Key-room generation has not been observed in a fresh runtime Outer Lands maze because smoke-server remains environment-blocked and user-driven client/manual scenarios are excluded.
- Full portal/passage/nest/library/boss room traversal remains unvalidated, and boss-room decorative block mutation remains open.

### 2026-05-14 — Stage 7 boss room mutation parity

Scope:

- Restored the reference Warden boss-room mutations: offset obelisks/caps, urn scatter, center traps/cosmetic supports, and the offset stair/crystal pedestal before spawning the Warden.
- Restored the Golem room mutations: three obelisks/caps, central pedestal, and corner-biased urn/crate scatter before spawning the Golem.
- Restored the Cultist room mutations: randomized eldritch floor pattern and the perimeter pillar/slab layout before spawning the Cultist Portal.
- Restored the Taint room mutations: 25x25 taint biome patching, taint fibre growth on exposed air, taint block scatter, and central taint crust before spawning the five taint bosses.
- Kept the existing reference-like boss spawn anchors and `BossMapData` selection flow from the prior lock checkpoint.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors. `run/crash-reports/` does not exist, and the configured crash-marker scan found no matches. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- Boss-room activation, room mutation, and save/reload progression have not been observed in a fresh runtime world because smoke-server remains environment-blocked and user-driven client/manual scenarios are excluded.
- Reference sparkle packets while clearing nearby airy door blocks remain Stage 8/client FX work.

### 2026-05-15 — Stage 6 Pech trade output generation

Scope:

- Restored the server-side Pech trade-roll button path through `ContainerPech.enchantItem(..., 0)`.
- Added current-port Pech trade tables split by Pech type, mapping reference outputs to available 1.12.2 items/blocks where direct equivalents exist.
- Restored value splitting, one-item offers from the Pech carried pack, high-value shared loot offers, one-input consumption after a roll, de-tame chance with Pech trade sound, and `PechDrop` tagging for unclaimed GUI drops.

Validation:

- `./scripts/dev.sh compileJava` — initially failed because `Items.ENCHANTED_BOOK` is typed as `Item` in 1.12.2; fixed by using `ItemEnchantedBook.getEnchantedItemStack(...)`, then passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors. `run/crash-reports/` does not exist, and the configured crash-marker scan found no matches. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- `GuiPech` is still absent because `ClientProxy` returns `null` for `GUI_PECH`; player-driven GUI validation remains Phase 8/client work.
- Original potion metadata and candle trade outputs do not have direct current-port equivalents in this branch and remain documented content dependencies.
- Manual Pech trade interaction, output extraction, and save/reload scenarios remain unrun because user-driven manual validation is excluded and smoke-server remains environment-blocked.

### 2026-05-15 — Stage 6 Pech spawn variants

Scope:

- Added `EntityPech.onInitialSpawn(...)` to restore random mainhand equipment selection from the reference spawn path.
- Restored Pech-focus wand setup with starting primal vis and type `1`, bow setup with type `2`, melee/tool/fishing-rod equipment possibilities, reduced wand drop chance, difficulty-based enchantment for non-wand gear, pickup-loot chance, and combat-task refresh after spawn setup.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors. `run/crash-reports/` does not exist, and the configured crash-marker scan found no matches. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- Type-specific Pech display-name localization is restored in the Pech type-name checkpoint below.
- Spawn variants have not been observed in a runtime world because smoke-server remains environment-blocked and user-driven manual scenarios are excluded.
- Pech group anger/trade runtime scenarios remain separate Stage 6 work.

### 2026-05-15 — Stage 6 Pech group anger

Scope:

- Restored player-damage group retaliation in `EntityPech.attackEntityFrom(...)` for nearby Pechs in the reference search volume.
- Added `EntityPech.becomeAngryAt(...)` to set revenge and attack targets, reset taming, refresh combat AI, apply the reference anger timer, and emit Pech charge status/sound when entering anger.
- Updated `EntityPech.onUpdate()` so angry Pechs reacquire their revenge target while anger remains and clear player targeting when the timer expires.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors. `run/crash-reports/` does not exist, and the configured crash-marker scan found no matches. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- Pech group anger, charge sound/status, and anger expiry have not been observed in a runtime world because smoke-server remains environment-blocked and user-driven manual scenarios are excluded.
- Client angry particle/status visual handling remains Phase 8/client work if reference visual parity is required beyond the emitted status byte.

### 2026-05-15 — Stage 6 Pech type names

Scope:

- Restored `EntityPech.getName()` to use the reference `PechType` name switch while preserving custom names.
- Added original English Pech type strings for default, mage, and stalker Pechs.
- Added lowercase 1.12 entity-name aliases beside the original `entity.Thaumcraft.Pech.*` localization keys.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors. `run/crash-reports/` does not exist, and the configured crash-marker scan found no matches. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- Type names have not been observed on in-game Pech entities because smoke-server remains environment-blocked and user-driven manual scenarios are excluded.
- Natural/command-spawn variant runtime coverage remains open under the Stage 6 manual matrix.

### 2026-05-15 — Stage 6 projectile sounds/status

Scope:

- Restored `EntityGolemOrb` reference `shock` impact sound and `zap` redirect sound instead of generic block event placeholders.
- Restored `EntityGolemOrb` squared-distance homing acceleration to match the reference path.
- Restored `EntityEldritchOrb` reference fizz sound, status byte `16`, and next-tick expiry after impact.
- Restored the original `0.1F` collision border on Golem orb, Eldritch orb, and Pech blast, while documenting Pech blast impact particles as Phase 8 visual work.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors. `run/crash-reports/` does not exist, and the configured crash-marker scan found no matches. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- Projectile sound/status behavior has not been observed in a runtime world because smoke-server remains environment-blocked and user-driven manual scenarios are excluded.
- Client burst/wisp particle rendering remains Phase 8/client work, and the broader projectile runtime sweep remains open under S6-PROJ-01.

### 2026-05-15 — Stage 6 Cultist and Taint Swarm drops

Scope:

- Restored base Cultist common drops: knowledge fragment, void seed, and coin rolls now apply to Cultist Knight, Cultist Cleric, and the Cultist Leader `super.dropFewItems(...)` path.
- Added a 1.12-compatible base Cultist rare eldritch-object drop through the existing `dropFewItems(...)` path.
- Fixed Taint Swarm drops to the reference 50% taint-slime-only result by removing the non-reference guaranteed taint-tendril fallback.
- Documented reference-confirmed silent sound slots for base Cultists and Taint Swarm ambient sound.

Validation:

- `./scripts/dev.sh compileJava` — initially failed because `dropRareDrop(int)` is not a 1.12 superclass hook; fixed by moving the rare drop into `dropFewItems(...)`, then passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors. `run/crash-reports/` does not exist, and the configured crash-marker scan found no matches. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- Cultist and Taint Swarm drops/sounds have not been observed in a runtime world because smoke-server remains environment-blocked and user-driven manual scenarios are excluded.
- The broader Stage 6 drop/sound table remains open for other mobs and bosses.

### 2026-05-15 — Stage 6 entity registry mapping

Scope:

- Documented the Stage 6 reference token to current Forge 1.12 registry-name mapping.
- Documented current local registration order for Stage 6 entities.
- Documented egg color parity and the decision to use Forge 1.12 entity eggs as the fresh-world replacement for the absent 1.7.10 `ItemSpawnerEgg`.
- Corrected the Stage 6 note that current registry paths are lowercase legacy tokens, not snake_case.

Validation:

- `git diff --check` — passed.
- No compile/build/smoke command required for this documentation-only checkpoint.

Remaining limits:

- Runtime registry smoke remains blocked by the pre-Forge smoke-server timeout, so duplicate/missing registry warnings and actual Forge egg spawning remain unobserved.
- External 1.7.10 save/item compatibility remains out of scope for the active fresh-world target.

### 2026-05-15 — Stage 6 Inhabited Zombie crab spawn

Scope:

- Restored Inhabited Zombie reference health, attack damage, zero reinforcement chance, Cultist targeting, local spawn-density guard, and crabtalk/hurt sounds.
- Changed Inhabited Zombie death update to terminate after the manual crab/XP path instead of continuing through vanilla zombie death update.
- Restored Eldritch Crab helm NBT persistence, natural helm initialization, size, XP value, attack damage, helm-dependent armor/speed, and cultist-plate drop when the helm breaks.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors. `run/crash-reports/` does not exist, and the configured crash-marker scan found no matches. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- Inhabited Zombie kill scenarios and crab save/reload have not been observed in a runtime world because smoke-server remains environment-blocked and user-driven manual scenarios are excluded.
- Original Inhabited Zombie cultist helmet/legs/chest spawn equipment remains a content dependency because this branch currently exposes aggregate cultist armor items rather than the original separate helmet/legs/chest fields.

### 2026-05-15 — Stage 6 Cultist baseline attributes

Scope:

- Restored base Cultist size, XP value, break/enter-door navigation flags, 32-block follow range, and 0.3 movement speed from the reference.
- Removed the non-reference base Cultist max-health override so subclasses own their health values.
- Restored Cultist Knight max health from `35` to the reference `36`.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors. `run/crash-reports/` does not exist, and the configured crash-marker scan found no matches. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- Cultist runtime combat/team/equipment scenarios remain unobserved because smoke-server remains environment-blocked and user-driven manual scenarios are excluded.
- Cultist Knight attack/armor placeholders are intentionally left unchanged in this checkpoint until the missing separate reference armor piece items are resolved.

### 2026-05-15 — Stage 6 base boss behavior

Scope:

- Restored `EntityThaumcraftBoss` reference-derived XP, home persistence, spawn-home assignment, spawn-timer invulnerability/push suppression, air-supply immunity, non-despawn behavior, and eldritch-mob team rule.
- Restored boss anger/enrage damage cap, buffs, player message, anger particles, passive regen, aggro accounting, target reassessment, and player-count health/damage scaling.
- Restored inherited base-boss rare loot drops for Eldritch Golem/Warden-style bosses while keeping Cultist Leader's reference override to its own rare loot bag.
- Restored Eldritch Golem spawn/headless transition timers and Eldritch Warden spawn timer/status trigger.
- Added English localization for `tc.boss.enrage`.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors. `run/crash-reports/` does not exist, and the configured crash-marker scan found no matches. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- Boss combat, aggro retargeting, player scaling, spawn invulnerability, and reward drops have not been observed in a runtime world because smoke-server remains environment-blocked and user-driven manual scenarios are excluded.
- Champion-name parity remains a separate dependency because the current branch still has a simplified champion modifier helper and no restored `EntityUtils.CHAMPION_MOD` custom attribute path.
- Eldritch Golem low-hardness block-breaking / `BlockLoot` stomping is restored in the next checkpoint below, but remains runtime-unobserved.

### 2026-05-15 — Stage 6 Eldritch Golem movement behavior

Scope:

- Restored Eldritch Golem iron-golem step sound override.
- Restored movement block-crack particles using current block state ids.
- Restored server-side `BlockLoot` destruction when the Golem walks over loot blocks.
- Restored server-side low-hardness block breaking directly in the moving Golem's path.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors. `run/crash-reports/` does not exist, and the configured crash-marker scan found no matches. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- Eldritch Golem movement block breaking, `BlockLoot` stomping, and step sound have not been observed in a runtime world because smoke-server remains environment-blocked and user-driven manual scenarios are excluded.
- Headless combat timing and save/reload persistence still need runtime scenario evidence before GAP-8 can close.

### 2026-05-15 — Stage 6 projectile sweep

Scope:

- Restored Primal Orb random drift, seeker targeting against nearest non-owner living entity, squared-distance seeker acceleration, water material impact trigger, and `0.1F` collision border.
- Restored Shock Orb impact `shock` sound, redirect `zap` sound, `0.1F` collision border, thrower-inclusive area damage search, and reference-like block-Air placement scan with line-of-sight check.
- Restored Explosive Orb `0.1F` collision border.
- Restored Frost Shard constructor scatter application for scattershot/normal/boulder focus paths.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors. `run/crash-reports/` does not exist, and the configured crash-marker scan found no matches. This matches the clean `da3f307` baseline reproduction recorded above.

Remaining limits:

- Projectile damage/effect/sound/block side effects have not been observed in a runtime world because smoke-server remains environment-blocked and user-driven manual scenarios are excluded.
- Client particles for projectile trails/impacts remain Phase 8 FX work.

### 2026-05-15 — Stage 6 golem item registration

Scope:

- Added append-only `ConfigItems` registrations for the original TC4 golem/trunk item tokens: `TrunkSpawner`, `ItemGolemPlacer`, `ItemGolemCore`, `ItemGolemUpgrade`, `GolemBell`, and `ItemGolemDecoration`.
- Restored creative metadata ranges for golem placers, golem cores, golem upgrades, decorations, and traveling trunk spawner.
- Restored reference stack-size/subtype basics, golem core GUI/inventory helper metadata, golem core/upgrade rarity, and the decoration metadata-to-character helper.
- Added English localization for golem/trunk item display names and golem upgrade descriptions.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors. `run/crash-reports/` does not exist, and the configured crash-marker scan found no matches. This matches the clean `da3f307` baseline reproduction recorded above.
- `git diff --check` — passed.

Remaining limits:

- Golem and traveling trunk placement/on-use behavior remains open; this checkpoint only restores the registry and metadata surface needed to reach those workflows.
- Bell marker/linking, decoration, healing, and wheat interaction parity remains open.
- Golem/trunk runtime placement evidence remains unavailable while smoke-server is blocked before ready state and user-driven manual scenarios are excluded.

### 2026-05-15 — Stage 6 golem and trunk placement

Scope:

- Ported `ItemGolemPlacer` server-side spawn behavior for golem metadata, advanced/core/upgrades/deco/marker/inventory NBT, home/facing setup, owner/custom name state, entity spawn, sound, and survival stack consumption.
- Ported `ItemTrunkSpawner` server-side spawn behavior for owner UUID, custom name, upgrade/inventory NBT, living initialization, entity spawn, and survival stack consumption.
- Restored reference golem type ordering and baseline values so item metadata maps to the correct golem material.
- Added golem setup/read helpers for inventory sizing, upgrade sync, decoration sync, and placement/reload persistence.
- Added minimal traveling trunk upgrade/inventory-size and owner UUID persistence needed by the item route.
- Added `ItemGolemBell.getMarkers(...)` marker NBT restore support for golem placer stacks.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors. `run/crash-reports/` does not exist, and the configured crash-marker scan found no matches. This matches the clean `da3f307` baseline reproduction recorded above.
- `git diff --check` — passed.

Remaining limits:

- Bell marker editing/linking, decoration application/removal, golem healing/wheat interaction, and full trunk upgrade behavior remain open.
- Golem/trunk runtime placement evidence remains unavailable while smoke-server is blocked before ready state and user-driven manual scenarios are excluded.
- Client renderer/model parity remains Phase 8 work.

### 2026-05-15 — Stage 6 golem bell and decoration interactions

Scope:

- Restored marker equality/fuzzy matching to include side and color for side-specific and colored golem markers.
- Ported `ItemGolemBell` golem linking and block marker editing, including marker NBT, home data, color cycling for order-upgraded golems, shift-remove behavior, stale-link cleanup, golem marker sync, and orb feedback sound.
- Restored empty-marker reset behavior for linked golems.
- Restored golem decoration application conflict groups, camera-clack sound, decoration data sync, setup refresh, and stack consumption.
- Restored wheat healing, prevented bell/wand-held interactions from opening the golem GUI, resized golem inventories after upgrades, and sent bootup status after core application.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors. `run/crash-reports/` does not exist, and the configured crash-marker scan found no matches. This matches the clean `da3f307` baseline reproduction recorded above.
- `git diff --check` — passed.

Remaining limits:

- Bell left-click pickup/packing behavior for golems and traveling trunks remains open.
- Full traveling trunk upgrade behavior remains open beyond the minimal placement/persistence route.
- Runtime marker/deco/healing evidence remains unavailable while smoke-server is blocked before ready state and user-driven manual scenarios are excluded.
- Client marker visuals and golem/trunk rendering remain Phase 8 work.

### 2026-05-15 — Stage 6 golem bell pickup

Scope:

- Ported bell left-click pickup for traveling trunks, including owner check for upgrade `3`, sneak upgrade split-drop chance, order-upgrade inventory packing, normal inventory drops, zap sound, and entity removal.
- Ported bell left-click pickup for golems, including placer metadata, advanced/core/upgrades/deco/marker/inventory NBT packing, sneak core/upgrades split-drop behavior, custom name preservation, carried-item drop, zap sound, and entity removal.
- Added `EntityGolemBase.dropStuff()` as the shared carried-item drop path used by normal drops and bell pickup.

Validation:

- `./scripts/dev.sh compileJava` — passed.
- `./scripts/dev.sh build` — passed.
- `./scripts/dev.sh check-jar` — failed before jar inspection because the wrapper's expected MCP mapping cache file is still absent at `.gradle_home/caches/minecraft/de/oceanlabs/mcp/mcp_stable/39/1.12.2/srgs/mcp-srg.srg`.
- `./scripts/dev.sh smoke-server` — failed by timeout before ready state; log again stopped after `Calling tweak class net.minecraftforge.fml.common.launcher.FMLServerTweaker`, with only Log4j console appender initialization errors. `run/crash-reports/` does not exist, and the configured crash-marker scan found no matches. This matches the clean `da3f307` baseline reproduction recorded above.
- `git diff --check` — passed.

Remaining limits:

- Full traveling trunk upgrade behavior remains open beyond pickup/packing and placement persistence.
- Runtime pickup/packing evidence remains unavailable while smoke-server is blocked before ready state and user-driven manual scenarios are excluded.
- Client pickup animation/visual parity remains Phase 8 work.

## Next Checkpoint Candidate

After the portal trigger and ring bootstrap checkpoints, the next pre-Phase8 candidates are:

- Remaining Stage 7 surface/worldgen runtime evidence and broader biome blacklist edge cases.
- Remaining Stage 7 Outer Lands room/tile behavior, especially key/boss room traversal, boss-room runtime/save evidence, and maze save/load race validation.
- Stage 9 loot/content registration, because `Utils.generateLoot(...)` now has a shared reward path but the full reference loot pool distribution still depends on populated content tables.
- Stage 6 server-side boss/manual scenario evidence remains excluded from user-driven validation, but static/reference parity blockers should continue to be reduced where possible.

Do not mark Stage 6 or Stage 7 complete from this checkpoint alone.
