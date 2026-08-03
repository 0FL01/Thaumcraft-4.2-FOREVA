# Audit Packet: A-014 - Essentia Reservoir runtime

Goal-ID: goal-20260803-eldritch-full-parity-audit-fixes
Assignment-ID: A-014
Status: complete
Report-Revision: 1
Last-Updated: 2026-08-03

## Assignment Contract

- Scope: Compare Essentia Reservoir runtime behavior against Thaumcraft 4.2.3.5, including `BlockEssentiaReservoir`, its ItemBlock, `TileEssentiaReservoir`, transport interfaces, golem/container interactions, and rendering data only as needed to adjudicate runtime state.
- Anti-scope: Recipe and research declaration; unrelated transport devices; broad item, block, golem, or rendering audits; product edits; central Goal Ledger edits.
- Oracle and comparison direction: Bundled TC4 4.2.3.5 classes and the checked-in `_p4_ref` decompile -> Forge 1.12.2 port.
- Questions: Does the port preserve capacity, mixed essentia rules, add/take behavior, suction, connectivity, side semantics, automatic pulling, NBT/sync, placement, break/drop/pick behavior, comparator output, golem/container interaction, and render state? Are 1.7-only direction, renderer, and finite-fluid representations adapted without changing behavior?
- Expected evidence: Fresh CFR decompilation from `Thaumcraft-1.7.10-4.2.3.5.jar`, comparison with `_p4_ref`, targeted port source/test inspection, and focused existing-test execution.
- Read/write permissions: Product files and central ledger files read-only; only this report writable.
- Stop conditions: Every requested runtime surface is compared; each defect has exact both-side evidence, effect, confidence, and test gap; confirmed parity and platform adaptations are explicit.

## Source Anchors

- Original artifact: `Thaumcraft-1.7.10-4.2.3.5.jar`.
- Checked-in oracle: `_p4_ref/thaumcraft/common/tiles/TileEssentiaReservoir.java`.
- Primary port tile: `src/main/java/thaumcraft/common/tiles/TileEssentiaReservoir.java`.
- Port block and ItemBlock: `src/main/java/thaumcraft/common/blocks/BlockEssentiaReservoir.java`; `src/main/java/thaumcraft/common/blocks/ItemBlocks/BlockEssentiaReservoirItem.java`.
- Transport/container contracts: `src/main/java/thaumcraft/api/aspects/IEssentiaTransport.java`, `IAspectSource.java`, `IAspectContainer.java`, and `AspectList.java`.
- Interaction consumers: `src/main/java/thaumcraft/common/entities/ai/fluid/AIEssentiaGather.java`, `AIEssentiaEmpty.java`; `src/main/java/thaumcraft/common/entities/golems/GolemHelper.java`; `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java`; `src/main/java/thaumcraft/common/lib/events/EssentiaHandler.java`.
- Rendering state: `src/main/java/thaumcraft/client/renderers/tile/TileEssentiaReservoirRenderer.java`, `src/main/java/thaumcraft/client/renderers/item/ItemEssentiaReservoirRenderer.java`, and reservoir block/model assets.

## Coverage Performed

- Fresh CFR verification covered original `BlockEssentiaReservoir`, `BlockEssentiaReservoirItem`, `TileEssentiaReservoir`, `BlockEssentiaReservoirRenderer`, `TileEssentiaReservoirRenderer`, `AIEssentiaGather`, `AIEssentiaEmpty`, `GolemHelper`, `ItemWandCasting`, `ItemEssence`, `AspectList`, `TileThaumcraft`, `BlockFluxGas`, and `BlockFluxGoo`.
- Forge 1.12.2 `BlockFluidFinite` and `BlockFluidBase` were decompiled from the repository's pinned Forge source jar to adjudicate the legacy flux metadata-8 conversion.
- Port registration, block/ItemBlock behavior, tile methods, NBT packet base, transport helper, golem consumers, direct aspect-source consumer, comparator, spill behavior, TESR/TEISR data, model assets, and focused tests were inspected.
- Existing focused tests were run for reservoir block behavior, ItemBlock surface, tile sync/container contracts, golem extraction, and renderer fidelity.
- Uncovered validation: No manual in-game visual test, live comparator rig, tube network scenario, addon compatibility modset, or dedicated-server smoke was run. These are test gaps, not evidence that the static/runtime contracts differ beyond the findings below.

## Atomic Findings

### A-014-F01 - Reservoir no-coordinate wand callback blocks TC4 focus fall-through

- Type: defect
- Severity: medium
- Confidence: high
- Source/oracle locator: Fresh CFR of `thaumcraft/common/tiles/TileEssentiaReservoir.class` and `thaumcraft/common/items/wands/ItemWandCasting.class`; checked-in oracle `_p4_ref/thaumcraft/common/tiles/TileEssentiaReservoir.java:227-229`; port `src/main/java/thaumcraft/common/tiles/TileEssentiaReservoir.java:269-270`; port dispatch `src/main/java/thaumcraft/common/items/wands/ItemWandCasting.java:592-622`.
- Observed: Port `TileEssentiaReservoir.onWandRightClick(World, ItemStack, EntityPlayer)` returns `wandstack`. Port `ItemWandCasting.onItemRightClick` treats any non-null tile callback result as immediate `SUCCESS` and returns before focus activation.
- Expected: TC4 reservoir returns `null` from the no-coordinate callback. Original `ItemWandCasting.func_77659_a` continues to its focus activation path when the block/tile callback returns null.
- Exact delta: no-coordinate return `null` -> held `ItemStack`; focus fall-through allowed -> focus fall-through consumed while the ray trace targets the reservoir.
- Effect: After the coordinate callback handles reservoir facing and returns the ordinary pass result, a focused wand's general right-click path can be consumed by the reservoir's second callback instead of activating the focus. The reservoir has no no-coordinate action in TC4 that justifies consuming this path.
- Regression hazards: Change only the no-coordinate callback contract when implementing. Preserve the coordinate callback's facing rule, swing, dirty/sync behavior, and return `0`; preserve the held stack in `ItemWandCasting` when no wandable consumes the action; do not globally change other `IWandable` implementations, several of which intentionally return a stack.
- Test gap: `src/test/java/thaumcraft/common/blocks/BlockEssentiaReservoirParityTest.java:69-87` covers `onItemUseFirst`/coordinate dispatch once, but no test invokes the no-coordinate callback with a focused wand or proves fall-through to focus activation.
- Candidate disposition: required.

### A-014-F02 - `containerContains(null)` returns total essentia instead of TC4 zero

- Type: defect
- Severity: low
- Confidence: high
- Source/oracle locator: Fresh CFR of original `TileEssentiaReservoir.class` and `AspectList.class`; checked-in oracle `_p4_ref/thaumcraft/common/tiles/TileEssentiaReservoir.java:111-113`; port `src/main/java/thaumcraft/common/tiles/TileEssentiaReservoir.java:145-153,186-195,222-235`; port `src/main/java/thaumcraft/api/aspects/AspectList.java:119-124`.
- Observed: Port `containerContains(null)` explicitly sums every stored aspect and returns the reservoir's total. `addToContainer`, `getSuctionAmount`, and `getEssentiaAmount` then use this changed public method as an internal total helper.
- Expected: TC4 `containerContains(tag)` directly returns `essentia.getAmount(tag)`. Original `AspectList.getAmount(null)` returns zero for a normally formed list; the port's own `AspectList.getAmount(null)` also defines null as zero.
- Exact delta: null-aspect query `0` -> total mixed essentia amount. Non-null aspect queries remain exact.
- Effect: Addons or automation using the public `IAspectContainer.containerContains` contract observe a non-TC4 value for a no-aspect query. No current in-tree external consumer calls `containerContains(null)`; the practical in-tree impact is therefore low, but the public addon-facing behavior differs.
- Regression hazards: Restoring the public null contract must not break capacity or transport totals. Replace the three internal total uses with `essentia.visSize()` or an equivalent private total operation while preserving max-capacity enforcement, suction shutdown at 256, and total `getEssentiaAmount` behavior. Do not change non-null lookups or the mixed-aspect model.
- Test gap: `src/test/java/thaumcraft/common/tiles/TileEssentiaReservoirSyncRuntimeTest.java` checks non-null extraction and assignment but does not assert `containerContains(null) == 0` while mixed contents are present.
- Candidate disposition: required.

## Positive Parity

### A-014-PC01 - Capacity and mixed-aspect container semantics

- Capacity remains `256` essentia.
- The reservoir accepts every non-null aspect and may hold multiple aspect types simultaneously; there is no single-aspect filter.
- Addition is limited by total `visSize`, returns the unaccepted remainder, and can partially accept a request up to remaining capacity.
- Non-null extraction is all-or-nothing for the requested aspect and amount. Deprecated bulk `takeFromContainer(AspectList)` remains unsupported and returns false.
- `doesContainerContainAmount` and `doesContainerContain(AspectList)` preserve per-aspect checks. `setAspects` retains copy semantics, so the caller's list cannot mutate reservoir contents afterward.
- A-014-F02 is isolated to the null argument of the public `containerContains` method; total capacity behavior itself matches TC4.

### A-014-PC02 - Transport side, suction, and automatic pulling

- Default transport face remains down. `isConnectable`, `canInputFrom`, and `canOutputTo` are true only for the configured face.
- Suction type remains null; suction amount remains 24 while total contents are below 256 and 0 when full; minimum suction remains 24; `setSuction` remains a no-op; extended-tube rendering remains false.
- `getEssentiaAmount` reports the total stored amount regardless of queried side, matching TC4.
- TC4 exposes a stored type only for `ForgeDirection.UNKNOWN`; because 1.12 `EnumFacing` has no UNKNOWN member, the port's `loc == null` sentinel is the correct adaptation. The first insertion-ordered aspect is returned only for that sentinel.
- `takeEssentia` and `addEssentia` retain configured-face checks and return the amount actually transferred.
- Every five server ticks, a non-full reservoir checks only the adjacent tile on its configured face. It requires a connectable `IEssentiaTransport`, output on the opposite face, positive essentia, lower remote suction, local suction meeting the remote minimum, and a non-null source type, then pulls one unit.
- The port removes TC4's second immediately repeated suction getter check after type selection. Under the transport getter contract this is a redundant pure read and does not alter normal transfer behavior.

### A-014-PC03 - Placement, wand orientation, and ItemBlock behavior

- The ItemBlock retains max damage 0, subtypes enabled, and metadata passthrough.
- Placement sets the reservoir face to the opposite of the clicked side, then dirties and synchronizes the tile. Replacing TC4's broad cast/exception suppression with an `instanceof` guard is a safe 1.12 adaptation.
- Coordinate wand use sets the face to the clicked side while sneaking and to its opposite otherwise, swings the player arm, and marks/synchronizes the tile.
- Legal reservoir block state maps to `TileEssentiaReservoir`. The port has no legacy metadata variants, so its `createNewTileEntity` handling is equivalent for representable state metadata 0.
- The separate no-coordinate callback discrepancy is A-014-F01.

### A-014-PC04 - Golem and aspect-source interactions

- Essentia-gather golems recognize reservoirs as direct transport sources, query the configured face, fall back to the unknown/null type sentinel, restrict extraction to the selected aspect's actual amount, and fill only to carry capacity.
- Essentia-empty golems recognize reservoirs as destinations without requiring a marker side match, use the reservoir's configured face, require positive compatible suction, and subtract only the amount accepted.
- `GolemHelper.findJarWithRoom` preserves reservoir candidate selection by dimension, range from golem home, positive suction, and compatible null/typed suction before nearest-destination selection.
- Port null-availability guards avoid a useless empty-type extraction attempt but do not change valid non-empty reservoir behavior.
- `EssentiaHandler` continues to consume the reservoir through its `IAspectSource` per-aspect methods. No in-tree external consumer relies on the divergent null contract in A-014-F02.

### A-014-PC05 - NBT, network sync, and client animation state

- Persistent and update-packet data retain original `Aspects` list and byte key `face`. Contents over capacity are cleared on read as in TC4.
- The port creates a fresh list before reading, preventing stale map entries while retaining the same loaded result.
- `TileThaumcraft` still writes custom NBT into tile update packets and reads it on receipt. Content mutation and face changes mark dirty and send a block update, equivalent to TC4's mark-for-update plus dirty calls.
- Client-only `displayAspect` and interpolated color values are neither written by the reservoir nor included in normal update packets, preserving local rotating-aspect animation. Optional readers for `facing`, `maxAmount`, `displayAspect`, and color keys are additive compatibility paths and do not alter ordinary TC4-authored data.
- Client ticking retains the 20-tick insertion-order aspect rotation, 20-step RGB interpolation, fill-dependent creak chance, and no server-side animation mutation.

### A-014-PC06 - Comparator, break spill, drops, and pick behavior

- Comparator output remains `floor(total / maxAmount * 14) + 1` for non-empty contents and 0 when empty, yielding the original 0..15 scale.
- Breaking computes `spills = total / 16`; fewer than 16 essentia produces no spill. A positive spill creates a strength-1, non-terrain-damaging explosion and makes at most 50 random air-placement attempts.
- The original post-increment break condition intentionally permits `spills + 1` successful placements. The port preserves that exact count, including two successful placements for 16..31 stored essentia.
- Successful positions below the reservoir receive flux goo; positions at or above receive flux gas.
- TC4 writes legacy finite-fluid metadata 8. In pinned Forge 1.12.2, `BlockFluidFinite` stores quanta as `LEVEL + 1`, has maximum render metadata 7, and `BlockFluidBase` defaults to that maximum. The port's flux default state therefore represents the same full eight quanta; using the default state is a required state-model adaptation, not a one-unit loss.
- Contents and facing are not copied into the normal drop or pick stack on either side. The legal block metadata/drop remains 0, so default 1.12 drop and pick behavior preserves the original empty reservoir item.

### A-014-PC07 - Block, TESR, item-render, and liquid state data

- The original custom block renderer's central cube bounds `2/16..14/16` are represented by the baked block model, while the original OBJ shell remains in the tile renderer. This division is the 1.12 replacement for `ISimpleBlockRenderingHandler` plus TESR.
- The source `reservoir.obj` and `reservoir.png` assets remain exact copies of the TC4 assets. The renderer uses OBJ group `Cylinder001` and preserves the six-facing rotation chain plus local half-block offset.
- Liquid height remains total fill ratio across a `3/16..13/16` interior with ten-sixteenths maximum liquid height, alpha 0.9, brightness 200, and the tile's interpolated RGB.
- The renderer requires non-empty contents and a selected display aspect before drawing liquid. Face, amount, selected aspect, and interpolation state therefore drive the same visible shell/liquid state.
- The TEISR composes the baked central core with the worldless reservoir TESR. Normal dropped/placed items are empty and face down as in TC4's inventory renderer; NBT-aware item display is additive and does not change ordinary drops.
- The renderer restores cull, blend, lighting, lightmap, color, and matrix state after drawing. Manual in-game visual parity was not claimed.

## Legitimate Platform Adaptations

- `ForgeDirection.UNKNOWN` is represented by null `EnumFacing` only where the legacy transport API asks for an unknown side.
- Legacy block metadata and custom block rendering are represented by 1.12 block states, a baked central cube, TESR shell/liquid, and TEISR item composition.
- Legacy flux metadata 8 is represented by Forge 1.12 finite-fluid level 7, whose `LEVEL + 1` value is eight quanta.
- `world.markBlockForUpdate` behavior is represented by tile dirtying plus `notifyBlockUpdate` and the inherited `SPacketUpdateTileEntity` path.
- Safe `instanceof` placement checks replace the original broad exception handler without changing successful placement.
- Optional NBT readers support port-era item/render data while ordinary writes remain the TC4 `Aspects` and `face` payload.

## Unknowns and Conflicts

- No oracle conflict remains for A-014-F01 or A-014-F02; fresh CFR and `_p4_ref` agree.
- No manual visual run established pixel-level OBJ/liquid parity. Static assets, transforms, bounds, colors, and renderer guards match, but visual parity remains unobserved.
- No third-party addon modset was tested. A-014-F02 is therefore proven at the public method contract level, not reproduced through a specific addon.
- No dedicated comparator or live tube-network fixture was run. Formula and transfer branch parity are source/bytecode conclusions backed by adjacent focused tests, not full in-game automation evidence.

## Test Debt

- A-014-F01: Add a focused wand behavior test that targets a reservoir with a focus, proves the coordinate facing callback occurs once, and proves the no-coordinate callback returns null so focus activation continues.
- A-014-F02: Add a mixed-content runtime assertion that non-null aspect counts remain exact while `containerContains(null)` returns zero; separately assert capacity, suction, and `getEssentiaAmount` still use total `visSize`.
- Automatic pulling lacks a reservoir-specific test matrix for configured/opposite faces, connectability, source output, suction equality, source minimum suction, null type, full capacity, five-tick cadence, and one-unit transfer.
- Comparator tests cover spill behavior but do not lock all boundary outputs, especially 0, 1, 255, and 256 essentia.
- Golem tests cover reservoir extraction into empty/partial golems but not nearest reservoir destination selection or mixed-aspect deposit into available space.
- Renderer fidelity tests are static; no manual client observation covers all six orientations, mixed-aspect color rotation, partial/full liquid heights, item view, or render-state interaction with adjacent TESRs.

## Commands and Results

All commands were run from the repository root. Original decompilation used CFR 0.152. Missing 1.7.10 dependency warnings did not prevent the relevant methods from decompiling.

```text
git status --short
unzip -Z1 Thaumcraft-1.7.10-4.2.3.5.jar 'thaumcraft/common/*/*EssentiaReservoir*.class'
unzip -Z1 Thaumcraft-1.7.10-4.2.3.5.jar 'thaumcraft/common/entities/ai/fluid/*.class'
unzip -Z1 Thaumcraft-1.7.10-4.2.3.5.jar 'thaumcraft/common/entities/golems/GolemHelper.class'
cfr --help jarfilter
cfr Thaumcraft-1.7.10-4.2.3.5.jar --silent true --jarfilter 'TileEssentiaReservoir'
cfr Thaumcraft-1.7.10-4.2.3.5.jar --silent true --jarfilter '^thaumcraft.common.blocks.BlockEssentiaReservoir'
cfr Thaumcraft-1.7.10-4.2.3.5.jar --silent true --jarfilter '^thaumcraft.common.entities.ai.fluid.AIEssentia(Gather|Empty)$'
cfr Thaumcraft-1.7.10-4.2.3.5.jar --silent true --jarfilter '^thaumcraft.common.entities.golems.GolemHelper$'
cfr Thaumcraft-1.7.10-4.2.3.5.jar --silent true --jarfilter '^thaumcraft.common.items.wands.ItemWandCasting$' --methodname 'onItemRightClick'
cfr Thaumcraft-1.7.10-4.2.3.5.jar --silent true --jarfilter '^thaumcraft.common.items.ItemEssence$'
cfr Thaumcraft-1.7.10-4.2.3.5.jar --silent true --jarfilter '^thaumcraft.api.aspects.AspectList$'
cfr Thaumcraft-1.7.10-4.2.3.5.jar --silent true --jarfilter '^thaumcraft.api.TileThaumcraft$'
cfr Thaumcraft-1.7.10-4.2.3.5.jar --silent true --jarfilter '^thaumcraft.client.renderers.block.BlockEssentiaReservoirRenderer$'
cfr Thaumcraft-1.7.10-4.2.3.5.jar --silent true --jarfilter '^thaumcraft.common.blocks.BlockFlux(Gas|Goo)$'
cfr .gradle_home/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeSrc-1.12.2-14.23.5.2847.jar --silent true --jarfilter '^net.minecraftforge.fluids.BlockFluidFinite$'
cfr .gradle_home/caches/minecraft/net/minecraftforge/forge/1.12.2-14.23.5.2847/stable/39/forgeSrc-1.12.2-14.23.5.2847.jar --silent true --jarfilter '^net.minecraftforge.fluids.BlockFluidBase$'
./scripts/dev.sh gradle test --tests thaumcraft.common.tiles.TileEssentiaReservoirSyncRuntimeTest --tests thaumcraft.common.blocks.BlockEssentiaReservoirParityTest --tests thaumcraft.common.blocks.ItemBlocks.BlockEssentiaReservoirItemStaticGuardTest --tests thaumcraft.common.entities.ai.fluid.LiquidEssentiaBackendRuntimeTest --tests thaumcraft.client.FluxReservoirRendererFidelityStaticGuardTest
git status --short
```

- Focused test result: `BUILD SUCCESSFUL` in 4 seconds; nine actionable tasks, three executed and six up-to-date.
- Original audit worktree result: initial and final `git status --short` were clean.
- Packet-persistence status: the orchestrator-created `.opencode/active-goal` and goal directory were already untracked before this report was added. Only this report was written by A-014; no product or central-ledger file was edited.
- Build: not run because only this Markdown report was added and no product/resource code changed.
- Runtime smoke: not required and not run because no common/server runtime path changed.
- Manual client smoke: not run; no visual parity claim is made beyond static data and asset evidence.

## Handoff

- Terminal status: complete.
- Defect index: A-014-F01 medium no-coordinate wand callback blocks TC4 focus fall-through; A-014-F02 low null container query returns total instead of zero.
- Positive parity index: A-014-PC01 capacity/mixed container; A-014-PC02 transport/suction/pulling; A-014-PC03 placement/wand orientation; A-014-PC04 golem/aspect-source interaction; A-014-PC05 NBT/sync/animation; A-014-PC06 comparator/spill/drop/pick; A-014-PC07 render-state data.
- Exact continuation point: none; the packet is ready for orchestrator normalization. Product fixes must preserve the listed parity controls and platform adaptations.
- Smallest next action if continued: Normalize A-014-F01 and A-014-F02 into central findings, then add the two focused regression tests before changing product behavior.
