# Thaumcraft 4.2.3.5 → 1.12.2 Port — Repair Plan

## Overview

The original agent ported Phases 0-7 as structural skeletons (classes compile,
registry fires), but ~50% of core gameplay logic was replaced with empty stubs
or `// TODO` comments. This document decomposes every stub into a file-level
fix plan with original source reference, fix approach, and effort estimate.

### Priority System

| Level | Meaning | Timeline |
|-------|---------|----------|
| **P0** | Blocks crash / core mechanic absent | First |
| **P1** | Feature non-functional (mobs, foci, baubles) | Second |
| **P2** | Subsystem broken (research, warp events) | Third |
| **P3** | Cosmetic/sound missing | Fourth |
| **P4** | Polish (tooltips, model registration) | Last |

### Severity Legend

- **CRITICAL**: Gameplay mechanic entirely absent (infusion, alchemy, mob AI)
- **HIGH**: Major feature broken (enchantments, events, vis cost)
- **MEDIUM**: Significant but not blocking (sound, champion FX)
- **LOW**: Cosmetic (tooltips, rarity colors)

---

## Phase 3r — Core Systems Remediation

### 3r.1 — WarpEvents (CRITICAL)

| Field | Value |
|-------|-------|
| **Files to create** | `thaumcraft/common/lib/events/WarpEvents.java` |
| **Original source** | `thaumcraft_src/thaumcraft/common/lib/events/WarpEvents.class` |
| **Missing methods** | `checkWarpEvent`, `checkDeathGaze`, `spawnMist`, `grantResearch`, `spawnGuardian`, `suddenlySpiders`, `getWarpFromGear` |
| **Dependencies** | `IPlayerKnowledge` (add `warpCount`), `EventHandlerEntity.onLivingUpdate` (must call `checkWarpEvent`) |
| **Effort** | L — ~200 lines from CFR, one new file |
| **Fix** | Decompile original with CFR, port 1:1, register on `EntityJoinWorldEvent` or call from `EventHandlerEntity` |

### 3r.2 — VisNetHandler drain/regeneration (CRITICAL)

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/api/visnet/VisNetHandler.java`, `thaumcraft/common/tiles/TileNode.java` |
| **Original source** | `thaumcraft_src/thaumcraft/api/visnet/VisNetHandler.class` |
| **Missing logic** | `onWorldTick()` body — iterate source nodes, calculate regen rate from biome aura, add vis to each node |
| **Dependencies** | `TileNode.handleRecharge()` must exist |
| **Effort** | M — ~80 lines vis regen + ~100 lines node tick handlers |
| **Fix** | `VisNetHandler.onWorldTick` → for each source node, `TileNode.addVis(toAspect, amount)`. `TileNode.handleRecharge()`: add `(nodeMax - nodeVis) * biomeAura / 100` per category per tick |

### 3r.3 — ResearchManager missing methods (CRITICAL)

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/common/lib/research/ResearchManager.java` |
| **Original source** | `thaumcraft_src/thaumcraft/common/lib/research/ResearchManager.class` |
| **Missing methods** | `createClue`, `createResearchNoteForPlayer`, `findHiddenResearch`, `findMatchingResearch`, `checkResearchCompletion`, `consumeInkFromPlayer`, `consumeInkFromTable`, `getResearchSlot`, `scheduleSave`, inner class `HexEntry` |
| **Dependencies** | `ResearchNoteData` (class must exist), `HexUtils` (class must exist) |
| **Effort** | L — ~300 lines from CFR to add to existing file |

### 3r.4 — ResearchNoteData + HexUtils classes (HIGH)

| Field | Value |
|-------|-------|
| **Files to create** | `thaumcraft/common/lib/research/ResearchNoteData.java`, `thaumcraft/common/lib/research/HexUtils.java` |
| **Original source** | `thaumcraft_src/thaumcraft/common/lib/research/ResearchNoteData.class`, `thaumcraft_src/thaumcraft/common/lib/research/HexUtils.class` |
| **Dependencies** | None |
| **Effort** | L — ~200 lines combined |

### 3r.5 — PlayerKnowledge offline methods + warp counter (HIGH)

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/common/lib/capabilities/IPlayerKnowledge.java`, `thaumcraft/common/lib/capabilities/PlayerKnowledgeCapability.java` |
| **Original source** | `thaumcraft_src/thaumcraft/common/lib/capabilities/PlayerKnowledge.class` |
| **Missing** | `getWarpCounter`/`setWarpCounter`, `getAspectPoolFor`/`addAspectPool`/`setAspectPool`, `hasDiscoveredParentAspects`. Offline `hasDiscoveredAspect`/`getAspectsDiscovered` always return false/empty |
| **Effort** | L — ~50 lines to add fields + methods |

### 3r.6 — EventHandlerEntity (HIGH)

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/common/lib/events/EventHandlerEntity.java` |
| **Original source** | `thaumcraft_src/thaumcraft/common/lib/events/EventHandlerEntity.class` |
| **Missing** | 10 empty handlers: `onLivingUpdate` (WarpEvents call), `onLivingDeath`, `onEntityInteract`, `onItemPickup`, `onItemToss`, `onArrowLoose`, `onArrowNock`, `onPlayerBreakSpeed`, `onPlayerLoadFromFile`, `onPlayerSaveToFile`, `onPlayerRightClickItem` |
| **Effort** | M — each handler needs distinct logic. `onLivingUpdate` alone needs ~40 lines (WarpEvents integration) |

### 3r.7 — EventHandlerRunic (HIGH)

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/common/lib/events/EventHandlerRunic.java` |
| **Original source** | `thaumcraft_src/thaumcraft/common/lib/events/EventHandlerRunic.class` |
| **Missing** | `onLivingUpdate` (shield regen tick), `onLivingHurt` (shield damage absorption), `onItemTooltip` (shield info) |
| **Effort** | M — ~80 lines for absorption/regen logic |

### 3r.8 — IPlayerKnowledge interface expansion (HIGH)

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/common/lib/capabilities/IPlayerKnowledge.java` |
| **Original source** | `thaumcraft_src/thaumcraft/common/lib/capabilities/IPlayerKnowledge.class` |
| **Missing** | `getWarpCounter`, `getAspectPoolFor`, `addAspectPool`, `setAspectPool`, `hasDiscoveredParentAspects` |
| **Effort** | L — ~30 lines interface methods |

### 3r.9 — WandManager.getTotalVisDiscount (HIGH)

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/common/items/wands/WandManager.java` |
| **Original source** | `thaumcraft_src/thaumcraft/common/items/wands/WandManager.class` |
| **Missing** | `getTotalVisDiscount`: scan baubles + armor for `IVisDiscountGear`, sum discounts, subtract potion penalties |
| **Effort** | L — ~40 lines |

### 3r.10 — ItemWandCasting missing methods (HIGH)

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/common/items/wands/ItemWandCasting.java` |
| **Original source** | `thaumcraft_src/thaumcraft/common/items/wands/ItemWandCasting.class` |
| **Missing** (17+ methods) | `isStaff`, `isSceptre`, `hasRunes`, `getFocusPotency`, `getFocusFrugal`, `getFocusEnlarge`, `getFocusExtend`, `addVis`, `addRealVis`, `getAllVis`, `getAspectsWithRoom`, `storeAllVis`, `consumeVis`, `getConsumptionModifier`, `onItemUseFirst`, `onEntitySwing`, `implements IArchitect` |
| **Effort** | M — ~200 lines of method bodies to add |

### 3r.11 — Potion fixes (HIGH/MEDIUM)

| # | File | Fix | Effort |
|---|------|-----|--------|
| 1 | `PotionInfectiousVisExhaust.java` | `performEffect`: iterate entities within 4 blocks, apply VisExhaust/InfectiousVisExhaust. `isReady`: `par1 % 40 == 0` | L |
| 2 | `PotionThaumarhia.java` | Replace damage with: get block below player, set to `ConfigBlocks.blockFluxGoo` | L |
| 3 | `PotionSunScorned.java` | Add `target.heal(1.0f)` when brightness < 0.25 | L |
| 4 | `PotionBlurredVision.java` | Fix icon coords: (5,2) instead of (1,2) | L |

### 3r.12 — Enchantment fixes (HIGH)

| # | File | Fix | Effort |
|---|------|-----|--------|
| 1 | `EnchantmentFrugal.java` | Type: `ALL`. Add `canApply(ItemStack)` checks for `IWandFocus.acceptsEnchant()`. Add static `doDamage` helper | L |
| 2 | `EnchantmentHaste.java` | Type: `ARMOR`. Add `canApply` checking boots (`armorType == 3`) or `ItemHoverHarness` | L |
| 3 | `EnchantmentPotency.java` | Type: `ALL`. Max level: 3. `canApply` checks `IWandFocus` | L |
| 4 | `EnchantmentRepair.java` | Type: `ALL`. Max level: 2. `canApply` checks `IRepairable`. `canApplyTogether` blocks `Enchantment.mending` | L |
| 5 | `EnchantmentWandFortune.java` | Type: `ALL`. `canApply` checks `IWandFocus` | L |

### 3r.13 — PacketHandler: real serialization (HIGH)

| Field | Value |
|-------|-------|
| **Files to modify** | All 39 `Packet*.java` classes in `thaumcraft/common/lib/network/` |
| **Original source** | Individual `.class` files in `thaumcraft_src/thaumcraft/common/lib/network/` |
| **Missing** | `fromBytes()` and `toBytes()` in every packet — currently empty |
| **Effort** | XL — 39 files each need CFR decompile then port. Bulk of work is identifying correct fields per packet type |
| **Strategy** | Process in batches: sync packets first (aspects, research, warp), then FX packets |

### 3r.14 — InternalMethodHandler stubs (MEDIUM)

| # | Method | Fix | Effort |
|---|--------|-----|--------|
| 1 | `generateVisEffect` | Create particle FX packet (packet discriminator lookup) | M |
| 2 | `getStackInRowAndColumn` | Delegate to `TileMagicWorkbench.getStackInRowAndColumn()` | L |
| 3 | `getBonusObjectTags` | Delegate to `ThaumcraftCraftingManager.getBonusTags()` | L |
| 4 | `generateTags` | Auto-generate aspect tags from item components | L |

---

## Phase 4r — Blocks & Tile Entities Remediation

### 4r.1 — TileInfusionMatrix (CRITICAL)

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/common/tiles/TileInfusionMatrix.java` |
| **Original source** | `thaumcraft_src/thaumcraft/common/tiles/TileInfusionMatrix.class` |
| **Current state** | 9 lines — empty class |
| **Missing** | ~600 lines: surround scan (25×16×25), pedestal/symmetry management, `ThaumcraftCraftingManager.findMatchingInfusionRecipe()`, `validLocation()` stabilitas check, `craftCycle()`, 4 instability effects (`inEvZap`, `inEvHarm`, `inEvWarp`, `inEvEjectItem`), `readCustomNBT`/`writeCustomNBT`, inner class `SourceFX` |
| **Effort** | XL — full CFR decompile, port entire class |
| **Depends on** | Pedestal tile function (`TilePedestal` — already has inventory), recipe system (Phase 9) |

### 4r.2 — TileCrucible (CRITICAL)

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/common/tiles/TileCrucible.java` |
| **Original source** | `thaumcraft_src/thaumcraft/common/tiles/TileCrucible.class` |
| **Current state** | 7 lines — empty class |
| **Missing** | ~350 lines: fluid tank, `attemptSmelt()` with recipe lookup, aspect decomposition (non-primal → primal when heat > 150), `spill()`/`spillRemnants()` (flux cleanup), `getBellows()`, `readCustomNBT`/`writeCustomNBT` |
| **Effort** | XL — full CFR decompile, port entire class |

### 4r.3 — TileNode abstract class (CRITICAL)

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/common/tiles/TileNode.java` |
| **Original source** | `thaumcraft_src/thaumcraft/common/tiles/TileNode.class` |
| **Current state** | 89 lines — update() has comment-only body `// Tick logic for vis regeneration will be added in Phase 4.6` |
| **Missing** | ~500 lines: `handleDischarge()` (node sharing), `handleRecharge()` (vis regen from biome aura), `handleTaintNode()` (taint biome spread + fibre growth), `handlePureNode()` (magical forest spread), `handleDarkNode()` (eerie biome + GiantBrainyZombie), `handleHungryNodeFirst()`/`handleHungryNodeSecond()` (entity pull + block break), `handleNodeStability()` (orb emission), `onUsingWandTick()`, `checkLock()`, `locations` HashMap |
| **Effort** | XL — largest single tile entity. Port in sub-steps: recharge first, then taint/pure/dark/hungry |

### 4r.4 — TileVisRelay (CRITICAL)

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/common/tiles/TileVisRelay.java` |
| **Original source** | `thaumcraft_src/thaumcraft/common/tiles/TileVisRelay.class` |
| **Current state** | 6 lines — empty ITickable |
| **Missing** | Node linking, range checks, vis transfer between nodes |
| **Effort** | M — ~150 lines |

### 4r.5 — TileArcaneBore (CRITICAL)

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/common/tiles/TileArcaneBore.java` |
| **Original source** | `thaumcraft_src/thaumcraft/common/tiles/TileArcaneBore.class` |
| **Current state** | 6 lines — empty ITickable |
| **Missing** | ~250 lines: pickaxe simulation, fake player, enchantment handling (fortune/silk), block-breaking loop with hardness calc |
| **Effort** | L — ~250 lines, self-contained |

### 4r.6 — TileAlchemyFurnace (CRITICAL)

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/common/tiles/TileAlchemyFurnace.java` |
| **Original source** | `thaumcraft_src/thaumcraft/common/tiles/TileAlchemyFurnace.class` |
| **Current state** | 5 lines — empty class |
| **Missing** | `ISidedInventory`, smelting with essentia generation, bellows boost, alembic integration |
| **Effort** | M — ~200 lines |

### 4r.7 — TileThaumatorium (CRITICAL)

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/common/tiles/TileThaumatorium.java` |
| **Original source** | `thaumcraft_src/thaumcraft/common/tiles/TileThaumatorium.class` |
| **Current state** | 6 lines — empty ITickable |
| **Missing** | Automated alchemy processing: item input → aspect melting → essentia output |
| **Effort** | M — ~200 lines |

### 4r.8 — TileMirrorEssentia (CRITICAL)

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/common/tiles/TileMirrorEssentia.java` |
| **Original source** | `thaumcraft_src/thaumcraft/common/tiles/TileMirrorEssentia.class` |
| **Current state** | 3 lines — empty class |
| **Missing** | Cross-dimensional essentia transport: paired mirror binding, `IEssentiaTransport` suction, chunk-load paired dimension |
| **Effort** | M — ~200 lines |

### 4r.9 — Essentia pipe network: 6 TileTube classes (CRITICAL)

| # | File to create | Source | Effort |
|---|----------------|--------|--------|
| 1 | `thaumcraft/common/tiles/TileTube.java` | `thaumcraft_src/thaumcraft/common/tiles/TileTube.class` | L |
| 2 | `thaumcraft/common/tiles/TileTubeBuffer.java` | `thaumcraft_src/thaumcraft/common/tiles/TileTubeBuffer.class` | L |
| 3 | `thaumcraft/common/tiles/TileTubeFilter.java` | `thaumcraft_src/thaumcraft/common/tiles/TileTubeFilter.class` | L |
| 4 | `thaumcraft/common/tiles/TileTubeOneway.java` | `thaumcraft_src/thaumcraft/common/tiles/TileTubeOneway.class` | L |
| 5 | `thaumcraft/common/tiles/TileTubeRestrict.java` | `thaumcraft_src/thaumcraft/common/tiles/TileTubeRestrict.class` | L |
| 6 | `thaumcraft/common/tiles/TileTubeValve.java` | `thaumcraft_src/thaumcraft/common/tiles/TileTubeValve.class` | L |

All tubes implement `IEssentiaTransport` suction model. The original uses `getSuctionAmount()`, `getEssentiaType()`, `takeEssentia()` neighbour polling via `world.getTileEntity()`. Porting this as a batch is efficient since all share the same `IEssentiaTransport` interface.

Also **must register all 6 in `ConfigBlocks.java` tile entity registry**.

### 4r.10 — Block metadata: createBlockState() for 18 blocks (CRITICAL)

Each block needs `createBlockState()`, `getStateFromMeta()`, `getMetaFromState()`,
and `getStateForPlacement()`. Block types and their properties:

| # | Block | Subtypes | Property type | Effort |
|---|-------|----------|---------------|--------|
| 1 | `BlockAiry` | 13 | `PropertyInteger("type", 0, 12)` | M |
| 2 | `BlockCosmeticOpaque` | 15 | `PropertyInteger("type", 0, 14)` | M |
| 3 | `BlockCosmeticSolid` | 15 | `PropertyInteger("type", 0, 14)` | M |
| 4 | `BlockCrystal` | 7 | `PropertyInteger("type", 0, 6)` | L |
| 5 | `BlockCustomOre` | 8 | `PropertyInteger("type", 0, 7)` | L |
| 6 | `BlockEldritch` | 6 | `PropertyInteger("type", 0, 5)` | L |
| 7 | `BlockMagicalLeaves` | 2 | `PropertyInteger("type", 0, 1)` | L |
| 8 | `BlockMagicalLog` | 2 | `PropertyEnum<EnumAxis>("axis")` + `PropertyInteger("type", 0, 1)` | M |
| 9 | `BlockManaPod` | 8 | `PropertyInteger("age", 0, 7)` | L |
| 10 | `BlockMetalDevice` | 6+ | `PropertyInteger("type", 0, N)` | L |
| 11 | `BlockStoneDevice` | 12+ | `PropertyInteger("type", 0, N)` | M |
| 12 | `BlockTable` | 5 | `PropertyInteger("type", 0, 4)` | L |
| 13 | `BlockTaint` | 3 | `PropertyInteger("type", 0, 2)` | L |
| 14 | `BlockTaintFibres` | 5 | `PropertyInteger("type", 0, 4)` | L |
| 15 | `BlockWoodenDevice` | 6+ | `PropertyInteger("type", 0, N)` | L |
| 16 | `BlockJar` | needs fix | Already has `createBlockState` but with 0 properties — add `PropertyInteger("type")` | L |
| 17 | `BlockEldritchNothing` | 13 | `PropertyInteger("type", 0, 12)` | L |
| 18 | `BlockEldritchPortal` | 0 | No subtypes — already fine | — |

Each also needs `getStateForPlacement(World, BlockPos, EnumFacing, float, float, float, int, EntityLivingBase)` to use the ItemStack metadata for subtype selection.

**This must happen before any block placing can work. P0 priority.**

### 4r.11 — Harvest levels for all blocks (CRITICAL)

Add to every block constructor or `init()`:
```java
this.setHarvestLevel("pickaxe", toolLevel);  // or "axe", "shovel"
```

Block groups and their tool requirements (from original):

| Block group | Tool | Level | Effort |
|-------------|------|-------|--------|
| `BlockCosmeticSolid` (stone-like) | pickaxe | 0-2 | L |
| `BlockCosmeticOpaque` (stone-like) | pickaxe | 0-2 | L |
| `BlockCustomOre` | pickaxe | 2-3 (amethyst = 2, etc.) | L |
| `BlockStoneDevice` | pickaxe | 0-2 | L |
| `BlockMetalDevice` | pickaxe | 1-2 | L |
| `BlockWoodenDevice` | axe | 0 | L |
| `BlockMagicalLog` | axe | 0 | L |
| `BlockTaint` (soil) | shovel | 0 | L |
| `BlockTaintFibres` | (hand) | — | L |
| `BlockCrystal` | pickaxe | 0 | L |
| `BlockEldritch` | pickaxe | 2 | L |

**19 blocks, ~5 min each = ~1.5 hours. P0 priority.**

### 4r.12 — Create 11 missing tile entity files (HIGH)

| # | File | Source | Effort |
|---|------|--------|--------|
| 1 | `TileAlchemyFurnaceAdvanced.java` | `thaumcraft_src/.../TileAlchemyFurnaceAdvanced.class` | L |
| 2 | `TileAlchemyFurnaceAdvancedNozzle.java` | `thaumcraft_src/.../TileAlchemyFurnaceAdvancedNozzle.class` | L |
| 3 | `TileArcaneLampFertility.java` | `thaumcraft_src/.../TileArcaneLampFertility.class` | L |
| 4 | `TileArcaneLampLight.java` | `thaumcraft_src/.../TileArcaneLampLight.class` | L |
| 5 | `TileBrainbox.java` | `thaumcraft_src/.../TileBrainbox.class` | L |
| 6 | `TileChestHungry.java` | `thaumcraft_src/.../TileChestHungry.class` | L |
| 7 | `TileEssentiaCrystalizer.java` | `thaumcraft_src/.../TileEssentiaCrystalizer.class` | L |
| 8 | `TileMagicBox.java` | `thaumcraft_src/.../TileMagicBox.class` | L |
| 9 | `TileMemory.java` | `thaumcraft_src/.../TileMemory.class` | L |
| 10 | `TileThaumcraftInventory.java` | `thaumcraft_src/.../TileThaumcraftInventory.class` | L |
| 11 | `TileWarded.java` | `thaumcraft_src/.../TileWarded.class` | L |

Each needs CFR decompile, 1.12.2 adaptation. ~50-150 lines each.

### 4r.13 — TileResearchTable update() (HIGH)

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/common/tiles/TileResearchTable.java` |
| **Original source** | `thaumcraft_src/thaumcraft/common/tiles/TileResearchTable.class` |
| **Current state** | 125 lines (inventory + NBT work) but `update()` is `// Research scanning logic will be added later` |
| **Missing** | Research scanning: consume aspect paper + ink, discover aspects from items in slot 0, generate `ResearchNoteData`, sync to client via packets |
| **Effort** | M — ~150 lines |

### 4r.14 — TileDeconstructionTable update() (HIGH)

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/common/tiles/TileDeconstructionTable.java` |
| **Original source** | `thaumcraft_src/thaumcraft/common/tiles/TileDeconstructionTable.class` |
| **Current state** | 125 lines (inventory + NBT work) but `update()` is `// Deconstruction logic will be added later` |
| **Missing** | Aspect deconstruction: over time, convert item into its component aspects, output into attached jars |
| **Effort** | M — ~150 lines |

### 4r.15 — Taint block spread (MEDIUM)

| # | File | Fix | Effort |
|---|------|-----|--------|
| 1 | `BlockTaint.java` | Add `updateTick()` with taint spread to adjacent blocks. Add `onEntityCollidedWithBlock()` with taint poison effect. Set correct AABB | L |
| 2 | `BlockTaintFibres.java` | Add `updateTick()` with `spreadFibres()` to adjacent dirt/stone. Add `onEntityCollidedWithBlock()`. Set correct bounding box | L |

### 4r.16 — BlockMagicalLog damageDropped (MEDIUM)

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/common/blocks/BlockMagicalLog.java` |
| **Fix** | `damageDropped(IBlockState state)` must return `state.getValue(TYPE) & 3` not 0 |

### 4r.17 — TileJarFillable.containerContains (LOW)

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/common/tiles/TileJarFillable.java` |
| **Fix** | `containerContains()` returns `this.amount` instead of `0` |

### 4r.18 — TilePedestal/TileResearchTable null displayName (LOW)

| Field | Value |
|-------|-------|
| **Files to modify** | `TilePedestal.java`, `TileResearchTable.java`, `TileDeconstructionTable.java`, `TileMagicWorkbench.java` |
| **Fix** | Override `getDisplayName()` to return a non-null default name |

---

## Phase 5r — Items & Baubles Remediation

### 5r.1 — Focus items: onFocusRightClick (CRITICAL)

| Field | Value |
|-------|-------|
| **Files to modify** | All 10 `Focus*.java` in `thaumcraft/common/items/wands/foci/` |
| **Original source** | `thaumcraft_src/thaumcraft/common/items/wands/foci/Focus*.class` |
| **Current state** | All 10 have identical stub: `onFocusRightClick()` returns `wandStack` with `// Phase 8:` comment |
| **Missing** | Per-focus behavior (see table below), `onUsingFocusTick()`, `onPlayerStoppedUsingFocus()` |
| **Strategy** | Batch-process by porting `IFocusEffect` interface methods. Each focus needs 3-5 methods plus custom upgrade types |

Per-focus behavior to port:

| Focus | Original behavior |
|-------|-------------------|
| `FocusShock` | Lightning bolt entity (zap), chain lightning upgrade, vis cost: 8 |
| `FocusFire` | Fireball (explosive) or flame beam (continuous), vis cost: 15/tick beam |
| `FocusFrost` | Frost shard projectile + freeze effect, vis cost: 10 |
| `FocusExcavation` | Block-breaking beam (3×3 area), fortune/silk touch support, vis cost: 12 |
| `FocusPrimal` | Random primal aspect bolt (6 types), vis cost: 20 |
| `FocusWarding` | Place warded blocks/jar, vis cost: 5 per block |
| `FocusHellbat` | Summon fire bats as projectiles |
| `FocusPech` | Trade with pechs |
| `FocusTrade` | Open bartering GUI |
| `FocusPortableHole` | Place temporary portal holes in blocks |

### 5r.2 — Relic right-click behaviors (CRITICAL)

| # | File | Fix | Effort |
|---|------|-----|--------|
| 1 | `ItemThaumometer.java` | `onItemRightClick`: ray-trace target → `ScanManager` scan → send `PacketScannedToServer` → display results GUI | L |
| 2 | `ItemThaumonomicon.java` | `onItemRightClick`: open `GuiThaumonomicon` via proxy | L |
| 3 | `ItemHandMirror.java` | `onItemRightClick`: mirror linking (sneak=bind, right-click=teleport items). NBT storage for linked mirror coordinates | M |
| 4 | `ItemResonator.java` | `onItemRightClick`: scan block for aura node → display vis in chat/tooltip | L |
| 5 | `ItemSanityChecker.java` | `onItemRightClick`: display current warp levels (normal/sticky/temporary) in chat | L |

### 5r.3 — Baubles onWornTick (CRITICAL)

| # | File | Fix | Effort |
|---|------|-----|--------|
| 1 | `ItemRingRunic.java` | `onWornTick`: recharge runic shielding from vis, decay combat timer | L |
| 2 | `ItemAmuletRunic.java` | `onWornTick`: emergency shield when < 50% HP, recharge from vis | L |
| 3 | `ItemGirdleRunic.java` | `onWornTick`: kinetic shield (absorb fall damage), recharge from vis | L |
| 4 | `ItemGirdleHover.java` | `onWornTick`: step assist, fall negate, jump boost | L |
| 5 | `ItemAmuletVis.java` | `addVis`: store vis in NBT, make available to wand via `WandManager` | L |
| 6 | `ItemHoverHarness.java` | `onArmorTick`: flight control (forward/up), hover mode toggle | M |

### 5r.4 — ItemPrimalCrusher wrong class hierarchy (CRITICAL)

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/common/items/equipment/ItemPrimalCrusher.java` |
| **Current state** | `extends ItemSword` — completely wrong |
| **Fix** | Change to `extends ItemTool` with both pickaxe + shovel effectiveness. Implement `IWarpingGear`. Add area-of-effect mining (3×3×3) and digging |

### 5r.5 — Void equipment IWarpingGear (HIGH)

| # | File | Fix | Effort |
|---|------|-----|--------|
| 1 | `ItemVoidSword.java` | Add `implements IWarpingGear`, `getWarp()` returns 2. Add `onUpdate()` self-damage (1 damage / 30 sec). Add wither on hit | L |
| 2 | `ItemVoidPickaxe/Axe/Shovel/Hoe.java` | Add `implements IWarpingGear`, `getWarp()` returns 1. Add `onUpdate()` self-damage | L |
| 3 | `ItemVoidArmor.java` | Add `implements IWarpingGear`, `getWarp()` returns 1 per piece | L |

### 5r.6 — ItemBowBone arrow firing (HIGH)

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/common/items/equipment/ItemBowBone.java` |
| **Fix** | `onPlayerStoppedUsing`: create `EntityArrow` with velocity from charge time, damage bonus, enchantment effects |

### 5r.7 — Elemental tool special abilities (HIGH)

| # | File | Fix | Effort |
|---|------|-----|--------|
| 1 | `ItemElementalSword.java` | `onUpdate`: flight-only. `onHit`: area damage sweep. `getIsRepairable`: check `ItemResource(ElementalShard)` | L |
| 2 | `ItemElementalPickaxe.java` | Area-of-effect mining (3×3). `getIsRepairable` | L |
| 3 | `ItemElementalAxe.java` | Tree-felling: when breaking one log, chain-break connected logs up to configurable height | M |
| 4 | `ItemElementalShovel.java` | Area-of-effect digging (3×1 path). `getIsRepairable` | L |
| 5 | `ItemElementalHoe.java` | Area-of-effect tilling (3×1 or 3×3). `getIsRepairable` | L |

### 5r.8 — Equipment getIsRepairable (HIGH)

| Field | Value |
|-------|-------|
| **Files to modify** | All 19 tool/armor files |
| **Fix** | Each class needs proper repair material check in `getIsRepairable(ItemStack)`. See per-item table below |

| Item | Repair material |
|------|----------------|
| Thaumium sword/tools/armor | `ConfigItems.itemResource` with damage=2 (thaumium ingot) |
| Void sword/tools/armor | `ConfigItems.itemResource` with damage=6 (void seed) |
| Elemental sword/tools | `ConfigItems.itemResource` with damage=12 (elemental shard) |
| Crimson blade | Iron ingot (`Items.IRON_INGOT`) |
| Primal Crusher | `ConfigItems.itemResource` with damage=12 (elemental shard) |
| Bone bow | Bone (`Items.BONE`) |
| Fortress armor | Thaumium ingot (same as above) |
| Void robe armor | Void seed (same as above) |
| Cultist armor | Iron ingot |
| Goggles | `ConfigItems.itemResource` with damage=0 (thaumium nugget) |
| Boots of Traveler | Leather (`Items.LEATHER`) |

### 5r.9 — Armor special interfaces (HIGH)

| # | File | Fix | Effort |
|---|------|-----|--------|
| 1 | `ItemFortressArmor.java` | Add `IGoggles`/`IRevealer` (reveal aura nodes). Add `ISpecialArmor` for damage reduction | L |
| 2 | `ItemVoidRobeArmor.java` | Add `IGoggles`/`IRevealer`, `IWarpingGear`, `ISpecialArmor`. Add overlay textures | L |
| 3 | `ItemRobeArmor.java` | Add `IVisDiscountGear`, dye recipe integration | L |
| 4 | `ItemBootsTraveller.java` | Restore step-height reset, jump boost, water movement handling | L |

### 5r.10 — Item model registration + tooltips (MEDIUM)

| Field | Value |
|-------|-------|
| **Files to modify** | All ~110 item classes |
| **Fix** | Add `registerModels()` to each item. Add `addInformation()` tooltips for vis display, warp indicator, etc. |
| **Effort** | XL — bulk work across all items. Can be done with search/replace patterns |

---

## Phase 6r — Entities & AI Remediation

### 6r.1 — AI classes: shouldExecute() → real logic (CRITICAL)

This is the single largest remediation task. 39/44 AI classes are empty shells.

**Strategy**: Port Combat AI, Inventory AI, and Golem task AI in 3 batches.

#### Batch 1: Combat AI (9 files)

| # | File | Source class | Effort |
|---|------|--------------|--------|
| 1 | `AIAttackOnCollide.java` | `thaumcraft_src/.../AIAttackOnCollide.class` | L |
| 2 | `AIGolemAttackOnCollide.java` | `thaumcraft_src/.../AIGolemAttackOnCollide.class` | L |
| 3 | `AICreeperSwell.java` | `thaumcraft_src/.../AICreeperSwell.class` | L |
| 4 | `AIAvoidCreeperSwell.java` | `thaumcraft_src/.../AIAvoidCreeperSwell.class` | L |
| 5 | `AICultistHurtByTarget.java` | `thaumcraft_src/.../AICultistHurtByTarget.class` | L |
| 6 | `AIDartAttack.java` | `thaumcraft_src/.../AIDartAttack.class` | L |
| 7 | `AIHurtByTarget.java` | `thaumcraft_src/.../AIHurtByTarget.class` | L |
| 8 | `AILongRangeAttack.java` | `thaumcraft_src/.../AILongRangeAttack.class` | L |
| 9 | `AINearestAttackableTarget.java` | `thaumcraft_src/.../AINearestAttackableTarget.class` | L |

#### Batch 2: Golem Inventory/Task AI (16 files)

| # | File | Source class | Effort |
|---|------|--------------|--------|
| 1 | `AIHomeTake.java` | `thaumcraft_src/.../AIHomeTake.class` | L |
| 2 | `AIHomePlace.java` | `thaumcraft_src/.../AIHomePlace.class` | L |
| 3 | `AIHomeDrop.java` | `thaumcraft_src/.../AIHomeDrop.class` | L |
| 4 | `AIHomeReplace.java` | `thaumcraft_src/.../AIHomeReplace.class` | L |
| 5 | `AIHomeTakeSorting.java` | `thaumcraft_src/.../AIHomeTakeSorting.class` | L |
| 6 | `AIEmptyGoto.java` | `thaumcraft_src/.../AIEmptyGoto.class` | L |
| 7 | `AIEmptyDrop.java` | `thaumcraft_src/.../AIEmptyDrop.class` | L |
| 8 | `AIEmptyPlace.java` | `thaumcraft_src/.../AIEmptyPlace.class` | L |
| 9 | `AIFillTake.java` | `thaumcraft_src/.../AIFillTake.class` | L |
| 10 | `AIFillGoto.java` | `thaumcraft_src/.../AIFillGoto.class` | L |
| 11 | `AIItemPickup.java` | `thaumcraft_src/.../AIItemPickup.class` | L |
| 12 | `AISortingGoto.java` | `thaumcraft_src/.../AISortingGoto.class` | L |
| 13 | `AISortingPlace.java` | `thaumcraft_src/.../AISortingPlace.class` | L |
| 14 | `AIHarvestCrops.java` | `thaumcraft_src/.../AIHarvestCrops.class` | L |
| 15 | `AIHarvestLogs.java` | `thaumcraft_src/.../AIHarvestLogs.class` | L |
| 16 | `AIFish.java` | `thaumcraft_src/.../AIFish.class` | L |

#### Batch 3: Golem Essentia/Liquid AI + Pech AI (10 files)

| # | File | Source class | Effort |
|---|------|--------------|--------|
| 1 | `AIEssentiaGather.java` | `thaumcraft_src/.../AIEssentiaGather.class` | L |
| 2 | `AIEssentiaEmpty.java` | `thaumcraft_src/.../AIEssentiaEmpty.class` | L |
| 3 | `AIEssentiaGoto.java` | `thaumcraft_src/.../AIEssentiaGoto.class` | L |
| 4 | `AILiquidGather.java` | `thaumcraft_src/.../AILiquidGather.class` | L |
| 5 | `AILiquidEmpty.java` | `thaumcraft_src/.../AILiquidEmpty.class` | L |
| 6 | `AILiquidGoto.java` | `thaumcraft_src/.../AILiquidGoto.class` | L |
| 7 | `AIUseItem.java` | `thaumcraft_src/.../AIUseItem.class` | L |
| 8 | `AIAltarFocus.java` | `thaumcraft_src/.../AIAltarFocus.class` | L |
| 9 | `AIPechItemEntityGoto.java` | `thaumcraft_src/.../AIPechItemEntityGoto.class` | L |
| 10 | `AIPechTradePlayer.java` | `thaumcraft_src/.../AIPechTradePlayer.class` | L |

#### Batch 4: Misc AI (4 files)

| # | File | Source class | Effort |
|---|------|--------------|--------|
| 1 | `AIConvertGrass.java` | `thaumcraft_src/.../AIConvertGrass.class` | L |
| 2 | `AIDoorInteract.java` | `thaumcraft_src/.../AIDoorInteract.class` | L |
| 3 | `AIReturnHome.java` | `thaumcraft_src/.../AIReturnHome.class` | L |
| 4 | `AIWander.java` | `thaumcraft_src/.../AIWander.class` | L |

### 6r.2 — Boss mob AI tasks (CRITICAL)

All 5 boss classes need real AI. Decomposed per-boss:

| # | Boss | AI Tasks Needed | Effort |
|---|------|-----------------|--------|
| 1 | `EntityCultistLeader` | Melee attack, GolemOrb ranged attack, strength buff aura for nearby cultists, equipment assignment (crimson void sword + cultist plate) | M |
| 2 | `EntityEldritchGolem` | Melee slam, beam attack (continuous damage), headless phase at lethal HP | M |
| 3 | `EntityEldritchWarden` | EldritchOrb ranged, sonic screech (AoE + knockback), field frenzy (enrage at < 30% HP), teleport home | M |
| 4 | `EntityThaumcraftBoss` | Enrage mechanic (when hit > 35 damage in one hit), player-count scaling, loot table | M |
| 5 | `EntityCultistPortal` | Continuous cultist spawn timer, spawn EntityCultist (base) or EntityCultistCleric/Knight | M |

### 6r.3 — Hostile mob AI registration (CRITICAL)

All 24 hostile mobs need `tasks.addTask()` and `targetTasks.addTask()` calls in
`initEntityAI()` or `setupAITasks()`. Each mob should register:

- `AIAttackOnCollide` (melee mobs) or `AILongRangeAttack` (ranged mobs)
- `AIHurtByTarget` (all)
- `AINearestAttackableTarget` targeting `EntityPlayer`
- `EntityAIWander` / `EntityAIWatchClosest` / `EntityAILookIdle`

Strategy: batch by mob family (taint, cultist, eldritch, other). Each needs
CFR decompile to get correct AI priorities and target conditions.

### 6r.4 — Projectile onImpact logic (CRITICAL)

11/12 projectiles need `onImpact(RayTraceResult)` implementations:

| # | File | onImpact effect | Effort |
|---|------|-----------------|--------|
| 1 | `EntityEldritchOrb.java` | Damage + knockback + sonic boom FX | L |
| 2 | `EntityPechBlast.java` | Damage + knockback + explosion particles | L |
| 3 | `EntityGolemOrb.java` | Damage (low) + no knockback | L |
| 4 | `EntityShockOrb.java` | Lightning damage + chain effect | L |
| 5 | `EntityExplosiveOrb.java` | Small explosion (radius 1.5), destroy fragile blocks | L |
| 6 | `EntityEmber.java` | Fire damage + set target on fire + ignite nearby blocks | L |
| 7 | `EntityPrimalOrb.java` | Damage + random primal aspect extra effect (fire/lightning/frost/etc.) | M |
| 8 | `EntityBottleTaint.java` | Splash area: apply flux taint potion, convert grass to taint | L |
| 9 | `EntityFrostShard.java` | Apply `bounce`, `fragile`, `DAMAGE`, `FROSTY` fields. Freeze water, damage + slowness | M |
| 10 | `EntityDart.java` | `getArrowStack()` return proper dart item. Damage from `DAMAGE` field | L |
| 11 | `EntityPrimalArrow.java` | Custom arrow effect: random primal aspect on hit | L |

### 6r.5 — Mob special abilities (HIGH)

| # | Mob | Missing ability | Effort |
|---|-----|-----------------|--------|
| 1 | `EntityWisp` | Lightning zap attack on nearest player every 40 ticks. Drop `ItemWispEssence` with aspect | L |
| 2 | `EntityWatcher` | Gaze attack: apply potion effect (slowness/blindness) to player looking at it within 16 blocks | L |
| 3 | `EntityPech` | Pech blast projectile when angry. NBT persist PECH_TYPE and ANGRY. Pech trade GUI (via right-click) | M |
| 4 | `EntityThaumicSlime` | Split on death: spawn 2-4 smaller slimes | L |
| 5 | `EntityInhabitedZombie` | On death: spawn `EntityEldritchCrab` | L |
| 6 | `EntityCultistPortal` | Spawn cultists on timer. `entityInit()` register data watcher for spawn timer | L |
| 7 | `EntityEldritchGuardian` | Fire EldritchOrb projectile. Sonic screech: AoE damage + weakness | M |
| 8 | `EntityCultistCleric` | Fire dart projectile. Healing aura for nearby cultists | L |

### 6r.6 — Champion modifier fixes (MEDIUM)

| # | File | Fix | Effort |
|---|------|-----|--------|
| 1 | `ChampionModMighty.java` | Implement knockback effect: `target.addVelocity(motionX*1.5, 0.3, motionZ*1.5)` | L |
| 2 | `ChampionModWarp.java` | Implement warp application: `ThaumcraftApi.addWarpToPlayer(player, 1)` | L |
| 3 | `ChampionModInfested.java` | Implement death spawn: `world.spawnEntity(new EntitySilverfish(world))` | L |
| 4 | All 14 `showFX()` | Implement client FX (particles, sounds) — post-GUI, Phase 8 dependent | M |

### 6r.7 — Sound events for 18 entities (MEDIUM)

| Field | Value |
|-------|-------|
| **Files to modify** | 18 entity classes returning null for sound methods |
| **Fix** | Add `getAmbientSound()`, `getHurtSound()`, `getDeathSound()` returning proper `SoundEvent` references from config |
| **Effort** | M — 18 files, bulk CFR to extract original sound paths |

### 6r.8 — Container GUIs canInteractWith (HIGH)

| # | File | Fix | Effort |
|---|------|-----|--------|
| 1 | `ContainerGolem.java` | `canInteractWith` check player distance to entity | L |
| 2 | `ContainerTravelingTrunk.java` | `canInteractWith` check player distance to entity | L |
| 3 | `ContainerPech.java` | `canInteractWith` check player distance to entity + pech alive | L |

### 6r.9 — InventoryTrunk + InventoryPech (HIGH)

| # | File | Fix | Effort |
|---|------|-----|--------|
| 1 | `InventoryTrunk.java` | Implement `IInventory`: ItemStack[27], `getStackInSlot`, `setInventorySlotContents`, `markDirty`, NBT read/write | L |
| 2 | `InventoryPech.java` | Implement `IInventory`: ItemStack[18], pech trade inventory with input + output slots | L |

### 6r.10 — ItemSpawnerEgg registration (HIGH)

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/common/config/ConfigItems.java` |
| **Fix** | Register `ItemSpawnerEgg` with registry name "thaumcraft:spawn_egg" |
| **Effort** | L — 1 line |

### 6r.11 — Entity NBT persistence fixes (MEDIUM)

| # | File | Fix | Effort |
|---|------|-----|--------|
| 1 | `EntityPech.java` | `readEntityFromNBT`/`writeEntityToNBT`: save/load PECH_TYPE, ANGRY | L |
| 2 | `EntityWisp.java` | Add NBT methods to persist WISP_TYPE | L |
| 3 | `EntityFrostShard.java` | Save/load DAMAGE, FROSTY fields | L |

### 6r.12 — Empty base entity shells (MEDIUM)

| # | File | Fix | Effort |
|---|------|-----|--------|
| 1 | `EntityAspectOrb.java` | Implement `entityInit` (data watcher), NBT save/load, `onUpdate` (lifetime + merge) | L |
| 2 | `EntityFallingTaint.java` | Implement taint falling entity (similar to EntityFallingBlock) | L |
| 3 | `EntityGolemBobber.java` | Implement fishing bobber behavior | L |

---

## Priority Execution Matrix

### P0 — Must fix first (blocks + networking)

```
Priority  Dependency Graph:
  P0: Block metadata (4r.10) →  Blocks can be placed correctly
  P0: Harvest levels (4r.11)  →  Blocks can be broken
  P0: PacketHandler (3r.13)   →  All network communication works
       ↓
  P1: Everything else (non-functional mechanics)
```

### P1 — Core mechanics

| Order | Task | Phase | Effort | Dependencies |
|-------|------|-------|--------|-------------|
| 1 | TileNode recharge (4r.3) | 4r | XL | Must fix `createBlockState` for `BlockStoneDevice` first (node block) |
| 2 | 39/44 AI classes (6r.1) | 6r | XL | None — self-contained CFR ports |
| 3 | Hostile mob AI (6r.3) | 6r | M | AI classes from 6r.1 must exist |
| 4 | Boss mob AI (6r.2) | 6r | M | AI classes from 6r.1 must exist |
| 5 | Projectile onImpact (6r.4) | 6r | M | None — self-contained |
| 6 | Focus items (5r.1) | 5r | M | `ItemWandCasting` missing methods from 3r.10 |
| 7 | Relic right-click (5r.2) | 5r | L | ScanManager from 3r.3/3r.4 must work |
| 8 | Baubles onWornTick (5r.3) | 5r | M | `ItemWandCasting` vis methods from 3r.10 |
| 9 | WarpEvents (3r.1) | 3r | L | `IPlayerKnowledge` warp counter from 3r.5 |
| 10 | Enchantment fixes (3r.12) | 3r | L | None |
| 11 | EventHandlerEntity (3r.6) | 3r | M | WarpEvents from 3r.1 |
| 12 | EventHandlerRunic (3r.7) | 3r | M | None |
| 13 | TileInfusionMatrix (4r.1) | 4r | XL | `createBlockState` for `BlockStoneDevice`, pedestal function |
| 14 | TileCrucible (4r.2) | 4r | XL | `createBlockState` for `BlockStoneDevice` |
| 15 | TileTube* network (4r.9) | 4r | L | New files, must register in ConfigBlocks |
| 16 | TileArcaneBore (4r.5) | 4r | L | None |
| 17 | WandManager discount (3r.9) | 3r | L | None |
| 18 | ItemWandCasting methods (3r.10) | 3r | M | None |
| 19 | Potion fixes (3r.11) | 3r | L | None |

### P2 — Subsystems

| Order | Task | Phase | Effort | Dependencies |
|-------|------|-------|--------|-------------|
| 1 | ResearchManager methods (3r.3) | 3r | L | None |
| 2 | ResearchNoteData + HexUtils (3r.4) | 3r | L | None |
| 3 | TileResearchTable update (4r.13) | 4r | M | ResearchNoteData/HexUtils from 3r.4 |
| 4 | TileDeconstructionTable (4r.14) | 4r | M | None |
| 5 | IPlayerKnowledge expansion (3r.8) | 3r | L | None |
| 6 | 17 missing TE files (4r.12) | 4r | L | None — new files from CFR |
| 7 | PrimalCrusher hierarchy (5r.4) | 5r | L | None |
| 8 | Void equipment (5r.5) | 5r | L | None |
| 9 | Elemental tools (5r.7) | 5r | L | None |
| 10 | Armor interfaces (5r.9) | 5r | L | None |
| 11 | Mob special abilities (6r.5) | 6r | M | Projectile classes from 6r.4 |
| 12 | Container GUIs (6r.8) | 6r | L | None |
| 13 | InventoryTrunk/Pech (6r.9) | 6r | L | None |
| 14 | ItemSpawnerEgg (6r.10) | 6r | L | None |
| 15 | ChampionMod fixes (6r.6) | 6r | L | None |
| 16 | InternalMethodHandler (3r.14) | 3r | L | None |

### P3 — Audio & FX

| Order | Task | Phase | Effort | Dependencies |
|-------|------|-------|--------|-------------|
| 1 | Sound events (6r.7) | 6r | M | Sound system registration (Phase 10) |
| 2 | Champion FX (6r.6.4) | 6r | M | Client-side (Phase 8) |
| 3 | Taint block spread (4r.15) | 4r | L | None |
| 4 | getIsRepairable (5r.8) | 5r | L | None |
| 5 | Entity NBT fixes (6r.11) | 6r | L | None |
| 6 | Empty entity shells (6r.12) | 6r | L | None |

### P4 — Polish

| Order | Task | Phase | Effort | Dependencies |
|-------|------|-------|--------|-------------|
| 1 | Model registration (5r.10) | 5r | XL | Client-side (Phase 8) |
| 2 | Tooltips (5r.10) | 5r | M | None |
| 3 | getRarity overrides | 5r | L | None |
| 4 | TileJarFillable.containerContains (4r.17) | 4r | L | None |
| 5 | null displayName (4r.18) | 4r | L | None |

---

## Cross-Phase Dependency Map

```
Phase 3r ──────────────────────────────────────────────
  3r.1 (WarpEvents) ← 3r.5 (IPlayerKnowledge)
  3r.3 (ResearchManager) ← 3r.4 (ResearchNoteData+HexUtils)
  3r.6 (EventHandlerEntity) ← 3r.1 (WarpEvents)
  3r.10 (ItemWandCasting) ← 3r.9 (WandManager)

Phase 4r ──────────────────────────────────────────────
  4r.1 (TileInfusionMatrix) ← 4r.10 (createBlockState for BlockStoneDevice)
  4r.2 (TileCrucible) ← 4r.10 (createBlockState for BlockStoneDevice)
  4r.3 (TileNode) ← 4r.10 (createBlockState for BlockStoneDevice)
  4r.13 (TileResearchTable) ← 3r.3+3r.4 (ResearchManager+NoteData)

Phase 5r ──────────────────────────────────────────────
  5r.1 (Focus items) ← 3r.10 (ItemWandCasting methods)
  5r.3 (Baubles) ← 3r.10 (ItemWandCasting vis)

Phase 6r ──────────────────────────────────────────────
  6r.2 (Boss AI) ← 6r.1 (AI classes)
  6r.3 (Hostile mob AI) ← 6r.1 (AI classes)
  6r.5 (Mob abilities) ← 6r.4 (Projectiles)
  6r.6.4 (Champion FX) ← Phase 8 (Client)
```

---

## File-Level Execution Order (Optimal)

This is the recommended sequence of git commits. Each produces BUILD SUCCESSFUL.

### Round 1: P0 Foundation
```
1. Block metadata: 18 blocks + createBlockState/getStateFromMeta/getMetaFromState + getStateForPlacement
2. Harvest levels: setHarvestLevel on all 19 block classes
3. PacketHandler: port fromBytes/toBytes for all 39 packets (can be spread over multiple commits)
```

### Round 2: P1 Tile Entities
```
4. TileNode: handleRecharge + handleDischarge (vis regen & node sharing)
5. TileNode: handleTaintNode + handlePureNode + handleDarkNode + handleHungryNode + handleNodeStability
6. TileInfusionMatrix: full CFR port
7. TileCrucible: full CFR port
8. TileTube*: create 6 new tile entity files + register in ConfigBlocks
9. TileArcaneBore: full CFR port
10. TileAlchemyFurnace + TileThaumatorium + TileMirrorEssentia + TileVisRelay
```

### Round 3: P1 AI
```
11. AI Batch 1 (Combat): 9 classes
12. AI Batch 2 (Inventory): 16 classes
13. AI Batch 3 (Essentia/Liquid/Pech): 10 classes
14. AI Batch 4 (Misc): 4 classes
15. Hostile mob AI task registration: 24 mobs
16. Boss mob AI: 5 bosses
17. Projectile onImpact: 11 projectiles
```

### Round 4: P1 Items
```
18. Focus items: 10 foci behavior
19. Relic right-click: 5 relics
20. Bauble onWornTick: 4 baubles + HoverHarness
21. Enchantment fixes: 5 enchantments
22. ItemWandCasting missing methods
23. WandManager.getTotalVisDiscount
```

### Round 5: P1 Core
```
24. WarpEvents class
25. EventHandlerEntity: 11 handlers
26. EventHandlerRunic: 3 handlers
27. Potion fixes: 3 potions
28. InternalMethodHandler stubs
```

### Round 6: P2 Research + Missing TEs
```
29. ResearchManager missing methods + inner classes
30. ResearchNoteData + HexUtils
31. TileResearchTable update
32. TileDeconstructionTable update
33. IPlayerKnowledge expansion
34. 17 missing TE files
35. PrimalCrusher fix + Void equipment + Elemental tools
36. Armor interfaces
37. Mob special abilities
38. Container GUIs + InventoryTrunk/Pech
39. ItemSpawnerEgg registration
40. ChampionMod Mighty + Warp + Infested
```

### Round 7: P3-P4 Polish
```
41. Sound events for 18 entities
42. Taint spread mechanics
43. getIsRepairable for all tools/armor
44. Entity NBT persistence
45. Empty entity shells
46. Model registration + tooltips
47. Minor fixes (containerContains, displayName, rarity)
```

---

## Appendix: Audit Coverage Map

This map shows which original classes were verified vs. trusted-as-ported.

| Phase | Total classes | Verified | Trusted | Stub rate |
|-------|--------------|----------|---------|-----------|
| 0-2 | ~20 | 20 | 0 | 0% (framework) |
| 3 | ~30 | 30 | 0 | **~60%** |
| 4 | 151 | 80 | 71 (blocks) | **~74%** |
| 5 | 110 | 55 | 55 | **~40%** |
| 6 | 130 | 90 | 40 | **~85%** |
| 7 | 35 | 35 | 0 | **~20%** (trees simplified) |
| **Total** | **~476** | **310** | **166** | **~55%** |
