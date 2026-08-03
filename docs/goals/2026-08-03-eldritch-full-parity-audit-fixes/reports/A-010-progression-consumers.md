# Audit Packet: A-010 — Eldritch progression consumers

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Assignment-ID: A-010
Status: complete
Report-Revision: 1
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Compare direct Eldritch research-ID consumers, gates, clues, unlocks, and wand triggers against Thaumcraft 4.2.3.5: `WarpEvents` thresholds/unlocks; `TileEldritchPortal` `ENTEROUTER` grant; `TileEldritchAltar` `OCULUS` gate; `ItemResource` `FOCUSPRIMAL` clue; `ItemEldritchObject` `PRIMNODE` gate and pearl-use effects; `WandManager` `OCULUS` and `ADVALCHEMYFURNACE` gates; `ConfigRecipes` wand block triggers; and `ThaumcraftApi` warp registration/lookup.
- Anti-scope: Research declarations except where needed to enumerate exact warp registrations; generic research infrastructure except where needed to establish whether a consumer grants completion or discovery and how the port replaces explicit TC4 packets; unrelated Eldritch gameplay and rendering.
- Oracle and comparison direction: S-003/S-004 Thaumcraft 4.2.3.5 bytecode -> S-005 Forge 1.12.2 port.
- Questions: Check threshold operators, side/server authority, research-key spelling and case, completion versus `@` discovery, target item/block metadata and NBT, trigger event and namespace, mutation timing, and grant timing relative to transfer/use.
- Expected evidence: Exact CFR decompilation of each TC4 method plus direct current-source comparison and focused existing-test inspection.
- Read/write permissions: Product files and central ledger files read-only; this report writable.
- Effort/tool budget: Targeted decompilation and source/test inspection only; no broad research-declaration audit.
- Stop conditions: All named consumers are compared, every delta has both-side evidence, positive parity is explicit, and no product path is changed.
- Continuation predecessor: none.

## Coverage Performed

- Port files/symbols inspected:
  - `src/main/java/thaumcraft/common/lib/WarpEvents.java` — `checkWarpEvent`
  - `src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java` — `onLivingUpdate`
  - `src/main/java/thaumcraft/common/tiles/TileEldritchPortal.java` — `update`, `transferPlayer`
  - `src/main/java/thaumcraft/common/tiles/TileEldritchAltar.java` — `onWandRightClick`, `checkForMaze`
  - `src/main/java/thaumcraft/common/items/ItemResource.java` — `onUpdate`
  - `src/main/java/thaumcraft/common/items/ItemEldritchObject.java` — `onItemUseFirst`, `transformNode`
  - `src/main/java/thaumcraft/common/items/wands/WandManager.java` — `performTrigger`, `createOculus`, `createAdvancedAlchemicalFurnace`
  - `src/main/java/thaumcraft/common/config/ConfigRecipes.java` — compound recipe/trigger registration in `init`
  - `src/main/java/thaumcraft/api/ThaumcraftApi.java` — `addWarpToItem`, `addWarpToResearch`, `getWarp`
  - `src/main/java/thaumcraft/common/config/research/ConfigResearchEldritch.java` — only the seven relevant warp-map calls
  - `src/main/java/thaumcraft/common/lib/research/ResearchManager.java` — only `isResearchComplete`/`addResearch` semantics needed to classify the consumers
- TC4 oracle surfaces inspected:
  - `thaumcraft/common/lib/WarpEvents.checkWarpEvent`
  - `thaumcraft/common/lib/events/EventHandlerEntity.livingTick(LivingUpdateEvent)`
  - `thaumcraft/common/tiles/TileEldritchPortal.func_145845_h`
  - `thaumcraft/common/tiles/TileEldritchAltar.checkForMaze`
  - `thaumcraft/common/items/ItemResource.func_77663_a`
  - `thaumcraft/common/items/ItemEldritchObject.onItemUseFirst`
  - `thaumcraft/common/items/wands/WandManager.performTrigger`, `createOculus`, `createAdvancedAlchemicalFurnace`
  - `thaumcraft/common/config/ConfigRecipes.initializeCompoundRecipes`
  - `thaumcraft/api/ThaumcraftApi.addWarpToItem`, `addWarpToResearch`, `getWarp`
  - `thaumcraft/common/config/ConfigResearch.initEldritchResearch`, restricted to warp-map calls
- Existing tests inspected:
  - `src/test/java/thaumcraft/common/tiles/TileEldritchTilesStaticGuardTest.java`
  - `src/test/java/thaumcraft/common/items/ItemResourceAlumentumKnowledgeStaticGuardTest.java`
  - `src/test/java/thaumcraft/common/items/ItemEldritchObjectCoreContractsStaticGuardTest.java`
  - `src/test/java/thaumcraft/common/items/wands/WandManagerTriggerStaticGuardTest.java`
  - `src/test/java/thaumcraft/common/items/wands/WandManagerAdvancedAlchemyFurnaceRuntimeTest.java`
  - `src/test/java/thaumcraft/common/lib/world/dim/OuterDungeonGenerationStaticGuardTest.java`
- Uncovered scope: No in-game execution or probabilistic runtime sampling was performed. Generic declaration graph, note solving, persistence, networking, and scan machinery belong to other audit assignments.

## Atomic Findings

### A-010-F01 — First OCULUS activation opens immediately instead of deferring until a later use

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: S-003/S-004 `thaumcraft/common/tiles/TileEldritchAltar.class`, CFR `checkForMaze`; `thaumcraft/common/items/wands/WandManager.class`, CFR `createOculus`; S-005 `src/main/java/thaumcraft/common/tiles/TileEldritchAltar.java:124-133,137-171` and `src/main/java/thaumcraft/common/items/wands/WandManager.java:874-889`.
- Observed: When no maze exists, the port calls `new MazeThread(...).run()` synchronously and then returns `true`. The same wand interaction therefore proceeds to consume all six primal aspects at 100 each, sets the altar open, replaces the dark node with the portal, and reports trigger success.
- Expected: TC4 `checkForMaze` creates and starts `new Thread(new MazeThread(...))`, then immediately returns `false`. Because `createOculus` requires `altar.checkForMaze()` before `consumeAllVisCrafting`, the first interaction that schedules a missing maze consumes no vis and does not open the altar. A later interaction can proceed after `mazesInRange(...)` becomes true.
- Exact deltas:
  - TC4 missing-maze branch: `Thread t = new Thread(new MazeThread(...)); t.start(); return false;`.
  - Port missing-maze branch: `new MazeThread(...).run();` followed by unconditional `return true;`.
  - TC4 first use in an ungenerated area: generate asynchronously, no vis debit, `open` remains false, dark node remains.
  - Port first use in an ungenerated area: generate synchronously, debit vis, `open` becomes true, dark node is replaced immediately.
  - Research gate itself is unchanged: both require completed uppercase key `OCULUS` before entering `createOculus`.
- Affected paths/symbols: `TileEldritchAltar.checkForMaze`, `TileEldritchAltar.onWandRightClick`, `WandManager.performTrigger` event 6, `WandManager.createOculus`.
- Evidence/reproduction: Decompile the exact TC4 methods with the commands below; compare the missing-maze branch to port lines 124-133 and the vis/open branch at lines 153-170. A focused test can clear `MazeHandler.labyrinth`, invoke the event-6 path on a valid four-eye altar/dark node, and assert first-call return/state/vis behavior.
- Impact: Player-visible progression timing and resource consumption differ, and synchronous maze generation occurs inside the activation call before the portal opens. The approved synchronous-publication adaptation avoids unsafe background mutation, but it does not require immediate activation.
- Regression hazards: Do not restore off-thread publication. Preserve completed `OCULUS` gating, four-eye and unopened checks, dark-node requirement, six-aspect 100-vis cost, server authority, synchronous maze publication, block/tile synchronization, and successful 1.12 trigger consumption. A parity-compatible resolution would need to preserve synchronous generation while separately restoring the first-call false/no-consumption branch.
- Candidate disposition: required, subject to orchestrator adjudication against the charter's synchronous-maze preserve control.

### A-010-F02 — PRIMNODE pearl use emits flux level 7 instead of TC4 metadata 8

- Type: defect
- Severity: low
- Confidence: high
- Source/oracle locator: S-003/S-004 `thaumcraft/common/items/ItemEldritchObject.class`, CFR `onItemUseFirst`; S-005 `src/main/java/thaumcraft/common/items/ItemEldritchObject.java:115-195`.
- Observed: After a server-side primordial-pearl transformation and explosion, the port attempts 33 random air placements. Below the node it places `blockFluxGoo.getStateFromMeta(7)`; at or above the node it places `blockFluxGas.getStateFromMeta(7)`.
- Expected: TC4's corresponding two `setBlock` calls use metadata `8` for both `ConfigBlocks.blockFluxGoo` and `ConfigBlocks.blockFluxGas`.
- Exact deltas:
  - Target item remains correct: Eldritch object metadata `3` (`META_ELDRITCH_OBJECT_3`).
  - Gate remains correct: completed uppercase `PRIMNODE`, with no `@PRIMNODE` discovery check and no research grant from this use.
  - Gate effects remain correct: completed `PRIMNODE` selects `nextInt(9)` instead of `nextInt(6)` for existing primal aspects, `nextInt(4)` instead of `nextInt(3)` for absent/low primals, and explosion random range multiplier `3` instead of `5`.
  - Mutation timing remains correct: server side only; shrink pearl first; mutate/mark node; explode; then attempt 33 flux placements.
  - Flux output differs only in target level: TC4 `8`; port `7` for both goo and gas.
  - No relevant item NBT predicate exists on either side.
- Affected paths/symbols: `ItemEldritchObject.onItemUseFirst`, `ItemEldritchObject.transformNode`.
- Evidence/reproduction: Exact TC4 decompilation shows `world.func_147465_d(..., ConfigBlocks.blockFluxGoo, 8, 3)` and the same metadata for gas. Port lines 190 and 193 explicitly request state meta 7. Metadata 8 remains representable by the port and is used by `src/main/java/thaumcraft/common/blocks/BlockAiry.java:234-236`, so no evidence supports a global 8-to-7 finite-fluid translation at this call site.
- Impact: The generated finite-fluid state starts one level lower than the original. Downstream behavior reads that level, including flux-gas effect duration/amplifier and decay in `BlockFluxGas`, so this is not merely a rendering delta.
- Regression hazards: Preserve item meta 3, server-only shrink/mutation, `PRIMNODE` completion semantics, random bounds, modifier transitions, explosion ordering, 33 attempts, below-node goo versus at/above-node gas split, air-only placement, and the Forge finite-fluid state conversion rather than introducing raw metadata writes.
- Candidate disposition: required.

### A-010-F03 — No behavior test fixes the missing-maze first-activation contract

- Type: test_debt
- Severity: medium
- Confidence: high
- Source/oracle locator: S-005 `src/test/java/thaumcraft/common/items/wands/WandManagerTriggerStaticGuardTest.java:67-68`; `src/test/java/thaumcraft/common/lib/world/dim/OuterDungeonGenerationStaticGuardTest.java:47-49`.
- Observed: Existing static guards require `createOculus` routing and prohibit `new Thread(new MazeThread...)`, but they do not assert first-use return value, vis consumption, altar `open`, dark-node replacement, or later-use behavior when no maze initially exists.
- Expected: A focused behavior test should preserve synchronous maze publication while fixing the intended first-use/no-consumption timing selected during adjudication of A-010-F01.
- Exact deltas: Current tests positively lock the adaptation mechanism but leave its progression side effect unconstrained.
- Affected paths/symbols: `TileEldritchAltar.checkForMaze`, `onWandRightClick`; `WandManager.createOculus`.
- Evidence/reproduction: Search the cited tests for `checkForMaze`, vis state, `isOpen`, and first/second invocation assertions; none exist.
- Regression hazards: The new test must not require unsafe background publication and must not conflate event-dispatch success with the altar's first-use progression state.
- Candidate disposition: required with A-010-F01.

### A-010-F04 — Pearl-use tests do not constrain PRIMNODE branch values or flux output level

- Type: test_debt
- Severity: low
- Confidence: high
- Source/oracle locator: S-005 `src/test/java/thaumcraft/common/items/ItemEldritchObjectCoreContractsStaticGuardTest.java:27-31`.
- Observed: The guard checks pearl metadata, one node-base write, container increment, and explosion presence. It does not check the `PRIMNODE` key, completed-versus-discovered semantics, the two random bounds, explosion range, operation order, 33 placements, goo/gas height split, or output level.
- Expected: Focused source or behavior evidence should lock the exact completed/uncompleted `PRIMNODE` branches and flux state level selected to resolve A-010-F02.
- Exact deltas: The current guard passes with the observed incorrect `getStateFromMeta(7)` calls.
- Affected paths/symbols: `ItemEldritchObject.onItemUseFirst`, `transformNode`.
- Evidence/reproduction: Search the cited test for `PRIMNODE`, `nextInt(9)`, `nextInt(6)`, `nextInt(4)`, `nextInt(3)`, `33`, and `getStateFromMeta`; those contracts are absent.
- Regression hazards: Avoid random/flaky assertions; use static exact-branch checks or a deterministic fake world/random source.
- Candidate disposition: required with A-010-F02.

### A-010-F05 — Threshold and direct-grant semantics rely on source review rather than focused tests

- Type: test_debt
- Severity: low
- Confidence: high
- Source/oracle locator: S-005 tests under `src/test/java/thaumcraft/common/lib/**`, `src/test/java/thaumcraft/common/tiles/**`, and `src/test/java/thaumcraft/common/items/**`.
- Observed: No focused test covers WarpEvents boundary values and successful-event timing, post-transfer `ENTEROUTER`, the primal charm's `@FOCUSPRIMAL` discovery semantics, or exact Eldritch warp-map keys/amounts. Existing guards assert only coarse source fragments for the portal and charm.
- Expected: Regression evidence should cover strict boundaries 10/25/50, grant only inside the successful warp-event branch, `ENTEROUTER` only after entering the Outer Lands, `@FOCUSPRIMAL` rather than completion, and all seven relevant warp registrations.
- Exact deltas: No current product defect was found in these consumers; this is missing positive-parity protection.
- Affected paths/symbols: `WarpEvents.checkWarpEvent`, `TileEldritchPortal.transferPlayer`, `ItemResource.onUpdate`, `ThaumcraftApi` warp map and Eldritch registration calls.
- Evidence/reproduction: Grep existing tests for `actualwarp`, all three threshold keys, `ENTEROUTER`, `@FOCUSPRIMAL`, and `addWarpToResearch`; no test jointly constrains the exact branches and values listed below.
- Regression hazards: Probabilistic paths require deterministic random/capability fixtures or exact static branch guards; do not weaken server authority merely to ease testing.
- Candidate disposition: required as focused parity controls when adjacent code is changed; otherwise preserve as explicit test debt.

## Positive Parity

### A-010-PC01 — Warp thresholds and unlock timing

- Both sides compute `actualwarp` from permanent plus sticky warp only; temporary and equipment warp do not contribute to these unlock thresholds.
- Both sides use strict operators and exact keys: `actualwarp > 10` for `@BATHSALTS`, `actualwarp > 25` for `ELDRITCHMINOR`, and `actualwarp > 50` for `ELDRITCHMAJOR`. Exact boundary values 10, 25, and 50 do not unlock.
- All three checks remain inside the successful warp-event branch after `warpCounter > 0`, positive total warp including gear, and `r <= sqrt(warpCounter)`. They do not run on every periodic check.
- `BATHSALTS` guards both completed `BATHSALTS` and discovered `@BATHSALTS`, then grants only `@BATHSALTS`. The two Eldritch keys grant completion, not discovery.
- `ELDRITCHMINOR` grants 10 primal research points before completion; `ELDRITCHMAJOR` grants 20 before completion. The port's `ResearchManager.addResearch` replaces TC4's explicit `PacketResearchComplete` plus `completeResearch` pair.
- Caller parity is preserved: TC4 `EventHandlerEntity.livingTick` and port `EventHandlerEntity.onLivingUpdate` call the method server-side every 2000 player ticks only when warp is enabled and Warp Ward is absent. The port adds a defensive `player == null || world.isRemote` guard without changing valid server behavior.

### A-010-PC02 — ENTEROUTER completion

- Both sides inspect `EntityPlayerMP` occupants every five portal tile ticks on the server, reject riding/ridden players, refresh a positive portal cooldown to 100, and otherwise set cooldown 100 before transfer.
- Both transfer a player outside the Outer Lands to `Config.dimensionOuterId`, then check exact uppercase key `ENTEROUTER` and grant completion after the transfer call. Neither grants on exit to dimension 0.
- The port computes `targetDim` before transfer and checks `targetDim == Config.dimensionOuterId` afterward; this is equivalent to TC4's pre-transfer `player.dimension != dimensionOuterId` branch.
- The port's `ResearchManager.addResearch(player, "ENTEROUTER")` performs capability mutation and server packet synchronization in one call, replacing TC4's explicit packet plus manager completion pair.
- No target item metadata or NBT participates in this grant.

### A-010-PC03 — OCULUS completion gates

- `WandManager.performTrigger` event 6 requires completed exact uppercase key `OCULUS` on both sides; discovery `@OCULUS` is not accepted.
- TC4 applies the gate in the trigger dispatcher. The port retains that gate and adds the same completed-key check in `TileEldritchAltar.onWandRightClick`; the duplicate check is a defense-in-depth adaptation, not a broader grant.
- Four eyes, unopened altar, dark `TileNode` immediately above, valid maze, and six primal aspects at 100 vis each remain required before opening.
- TC4 `createOculus` returns false even after mutation, whereas the 1.12 port returns success from the tile callback so `ItemWandCasting.onItemUseFirst` consumes the block interaction. This return-value adaptation is intentional dispatch plumbing; A-010-F01 concerns the separate missing-maze branch and vis/open timing.

### A-010-PC04 — FOCUSPRIMAL charm clue

- Both sides run the clue only server-side while carrying item-resource metadata 15, roll `world.rand.nextInt(20000)`, remove NBT key `blurb` when present, and prioritize the `r < 20` aspect-orb branch before testing `r == 42`.
- At `r == 42`, both require a player and require that neither completed `FOCUSPRIMAL` nor discovered `@FOCUSPRIMAL` is present.
- Both emit localization key `tc.primalcharm.trigger` and grant discovery key `@FOCUSPRIMAL`, not completed `FOCUSPRIMAL`.
- The port's `ResearchManager.addResearch` replaces TC4's explicit completion packet/manager call and retains the exact `@` key.

### A-010-PC05 — PRIMNODE gate and pearl-use timing

- Both target Eldritch object metadata 3, require a `TileNode`, swing before server mutation, and shrink one pearl server-side before reading completed exact uppercase key `PRIMNODE`.
- `PRIMNODE` is a benefit gate, not a grant trigger: neither side grants or discovers PRIMNODE from pearl use.
- Both use completed PRIMNODE to select the exact random ranges described in A-010-F02, preserve non-primal degradation, preserve FADING -> PALE -> null -> possible BRIGHT transitions, mark/synchronize the node, create the explosion, and then attempt 33 air-only flux placements.
- A-010-F02 is the only verified direct-consumer delta in this method.

### A-010-PC06 — ADVALCHEMYFURNACE completion gate

- `WandManager.performTrigger` event 7 requires completed exact uppercase key `ADVALCHEMYFURNACE` on both sides. Discovery is not accepted.
- Both transformation paths are server-authoritative and charge FIRE 50, WATER 50, and ORDER 50 only for a recognized structure.
- The port has additional legacy-layout compatibility and safe inventory/tile-data handling outside this assignment's research-ID scope; those adaptations do not bypass the event-7 completion gate.

### A-010-PC07 — Wand trigger events, target metadata, and namespaces

`ConfigRecipes` matches TC4 exactly for the audited routing surface:

| Event | Block | Meta | Namespace |
| --- | --- | ---: | --- |
| 0 | bookshelf | 0 | `Thaumcraft` |
| 1 | cauldron | -1 | `Thaumcraft` |
| 2 | obsidian | -1 | `Thaumcraft` |
| 2 | nether brick | -1 | `Thaumcraft` |
| 2 | iron bars | -1 | `Thaumcraft` |
| 3 | stone device | 2 | `Thaumcraft` |
| 4 | glass | -1 | `Thaumcraft` |
| 5 | metal device | 9 | `Thaumcraft` |
| 6 | Eldritch block | 0 | `Thaumcraft` |
| 7 | metal device | 3 | `Thaumcraft` |
| 7 | metal device | 9 | `Thaumcraft_2` |

- Exact capitalization of `Thaumcraft` and `Thaumcraft_2` is preserved.
- Wildcard metadata remains `-1`; no NBT predicate participates in trigger lookup.

### A-010-PC08 — Eldritch warp registrations and lookup keys

The relevant calls match TC4 exactly:

| Registration | TC4 | Port |
| --- | ---: | ---: |
| research `OCULUS` | 6 | 6 |
| research `PRIMNODE` | 1 | 1 |
| research `CAP_void` | 1 | 1 |
| research `FOCUSPRIMAL` | 2 | 2 |
| primal focus item, default meta 0 | 1 | 1 |
| research `ROD_primal_staff` | 3 | 3 |
| wand rod item meta 100 | 1 | 1 |

- `ThaumcraftApi.addWarpToResearch` stores the exact string key on both sides.
- `ThaumcraftApi.addWarpToItem` and `getWarp(ItemStack)` key by item identity plus metadata on both sides. Item count and NBT are intentionally ignored.
- `getWarp(String)` performs an exact string lookup. Case and underscores therefore remain significant.
- No audited registration is misspelled, omitted, duplicated with a conflicting amount, or attached to the wrong metadata.

## Intentional Adaptations To Preserve

- Server authority: Capability/research mutation remains server-side; client packets report authoritative changes rather than initiating them.
- Consolidated research mutation: Port `ResearchManager.addResearch` combines the TC4 explicit completion packet and manager mutation, including warp application for completed research. Do not reintroduce duplicate packet sends at these call sites.
- Synchronous maze publication: The 1.12 port intentionally calls `MazeThread.run()` rather than mutating shared maze state from a background thread. Preserve this safety adaptation while adjudicating A-010-F01's first-use return and consumption timing.
- Oculus dispatch return: The port's successful event-6 path must return success so the 1.12 `EnumActionResult` interaction is consumed; TC4's unconditional false return is not directly portable.
- Duplicate altar gate: Keeping an `OCULUS` completion check inside `TileEldritchAltar.onWandRightClick` is harmless defense in depth in addition to `WandManager.performTrigger`.
- Finite-fluid states: Continue using `getStateFromMeta`/block state conversion rather than raw legacy metadata writes. No evidence supports changing original level 8 to 7 specifically for pearl output.
- 1.12 teleporter and synchronization: Preserve `TeleporterThaumcraft.forWorld`, safe destination behavior, capability sync, `markDirty`, and block notifications; these do not alter the audited research keys.
- Advanced-furnace compatibility: Preserve safe legacy layout conversion and inventory transfer/drop behavior while retaining the completed `ADVALCHEMYFURNACE` gate.

## Unknowns and Conflicts

- A-010-F01 conflicts only at the implementation-mechanism level with unsafe TC4 background generation. The gameplay oracle clearly establishes first-use false/no-consumption timing, while the charter clearly requires synchronous publication. The smallest adjudication is whether synchronous generation should still return false on the first call; no source conflict exists about the original branch.
- No conflict was found for flux level 8: TC4 is explicit, and the port can represent/use level 8 elsewhere. If a global finite-fluid translation rule is later produced by another report, A-010-F02 should be rechecked against that direct evidence rather than inferred away.
- No other material unknown remains in the assigned consumer surface.

## Test Debt

- A-010-F03: Missing first/second OCULUS activation behavior test with no pre-existing maze.
- A-010-F04: Missing deterministic PRIMNODE branch and flux-level guard.
- A-010-F05: Missing boundary/timing tests for warp unlocks, post-transfer `ENTEROUTER`, `@FOCUSPRIMAL`, and exact warp-map registrations.
- `WandManagerTriggerStaticGuardTest` currently checks several trigger registrations and routing methods but does not lock every event-6/event-7 research gate and both event-7 target registrations as one complete table.
- `TileEldritchTilesStaticGuardTest` asserts the `ENTEROUTER` add call exists but not that it occurs after entry transfer and never on exit.
- `ItemResourceAlumentumKnowledgeStaticGuardTest` asserts `@FOCUSPRIMAL` exists but not metadata 15, server side, random branch ordering, or completed/discovered guards.

## Commands And Results

All commands were run from the repository root. CFR 0.152 decompiled the exact extracted TC4 classes. Missing 1.7.10 dependency warnings did not prevent the relevant methods from decompiling.

```text
git status --short
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/lib/WarpEvents.class --methodname checkWarpEvent --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/lib/events/EventHandlerEntity.class --methodname livingTick --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/tiles/TileEldritchPortal.class --methodname func_145845_h --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/tiles/TileEldritchAltar.class --methodname checkForMaze --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/items/ItemResource.class --methodname func_77663_a --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/items/ItemEldritchObject.class --methodname onItemUseFirst --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/items/wands/WandManager.class --methodname performTrigger --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/items/wands/WandManager.class --methodname createOculus --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/items/wands/WandManager.class --methodname createAdvancedAlchemicalFurnace --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigRecipes.class --methodname initializeCompoundRecipes --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/api/ThaumcraftApi.class --methodname addWarpToItem --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/api/ThaumcraftApi.class --methodname addWarpToResearch --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/api/ThaumcraftApi.class --methodname getWarp --silent true
/usr/local/bin/cfr thaumcraft_src/thaumcraft/common/config/ConfigResearch.class --methodname initEldritchResearch --silent true
javap -classpath thaumcraft_src -p thaumcraft.common.config.ConfigRecipes
javap -classpath thaumcraft_src -p thaumcraft.api.ThaumcraftApi
javap -classpath thaumcraft_src -p thaumcraft.common.items.wands.WandManager
```

- Result: Two behavior deltas verified with exact both-side evidence; all other named direct-consumer contracts matched.
- Tests/build: Not run. This was a read-only audit packet materialization with no product edits; existing tests were inspected only to identify coverage gaps.
- Runtime smoke: Not required and not run because no product/runtime path changed.
- Product diff: none.

## Handoff

- Terminal status: complete
- Material finding index: A-010-F01 first OCULUS activation timing/consumption; A-010-F02 pearl flux meta 7 versus 8; A-010-F03 missing Oculus timing test; A-010-F04 missing PRIMNODE/flux test; A-010-F05 missing direct-consumer boundary/grant/warp-map tests.
- Positive parity index: A-010-PC01 warp thresholds; A-010-PC02 `ENTEROUTER`; A-010-PC03 `OCULUS` gates; A-010-PC04 `@FOCUSPRIMAL`; A-010-PC05 `PRIMNODE`; A-010-PC06 `ADVALCHEMYFURNACE`; A-010-PC07 wand triggers; A-010-PC08 warp registrations.
- Exact continuation point: none; packet is ready for orchestrator normalization and A-010-F01 adaptation adjudication.
- Smallest next action if continued: Normalize each material finding and all eight preserve controls into the central RECON ledger without editing product code during RECON.
