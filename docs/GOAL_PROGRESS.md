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

- Full `TileEldritchAltar` cultist/guardian spawning and portal-opening behavior is still not ported by this checkpoint.
- Runtime ring discovery, `labyrinth.dat` save evidence, and portal traversal were not manually run because user-driven GUI/client control is excluded from this durable goal and the smoke-server environment remains blocked by the known pre-Forge timeout.
- Hilltop altar, mound/barrow, normal/spider Greatwood, and broader surface worldgen parity remain open Stage 7 work.

## Next Checkpoint Candidate

After the portal trigger and ring bootstrap checkpoints, the next pre-Phase8 candidates are:

- Remaining Stage 7 surface/worldgen placeholders, especially hilltop altar, mound/barrow, and spider Greatwood.
- Remaining Stage 7 Outer Lands room/tile behavior, especially full `TileEldritchAltar`, lock, trap, crab spawner, key/boss room traversal, and maze save/load race validation.
- Stage 9 loot/content registration, because `Utils.generateLoot(...)` now has a shared reward path but the full reference loot pool distribution still depends on populated content tables.
- Stage 6 server-side boss/manual scenario evidence remains excluded from user-driven validation, but static/reference parity blockers should continue to be reduced where possible.

Do not mark Stage 6 or Stage 7 complete from this checkpoint alone.
