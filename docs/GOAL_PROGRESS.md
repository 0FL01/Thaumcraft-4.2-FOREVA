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

## Next Checkpoint Candidate

After the BlockLoot urn/crate checkpoint, the next pre-Phase8 candidates are:

- Stage 7 `blockSlabStone` and remaining Outer Lands room template placeholders, because they still affect traversal and room parity.
- Stage 9 loot/content registration, because `Utils.generateLoot(...)` now has a shared reward path but the full reference loot pool distribution still depends on populated content tables.
- Stage 6 server-side boss/manual scenario evidence remains excluded from user-driven validation, but static/reference parity blockers should continue to be reduced where possible.

Do not mark Stage 6 or Stage 7 complete from this checkpoint alone.
