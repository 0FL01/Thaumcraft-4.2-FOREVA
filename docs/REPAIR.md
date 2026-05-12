# Thaumcraft 4.2.3.5 → 1.12.2 Port — Repair Plan

## Overview

The original agent ported Phases 0-7 as structural skeletons (classes compile,
registry fires), but ~50% of core gameplay logic was replaced with empty stubs
or `// TODO` comments. This document decomposes every stub into a file-level
fix plan with original source reference, fix approach, and effort estimate.

**Current state:** AI classes 44/44 fully ported, hostile mob + boss
task registration complete, Group B manual AI lifecycle migrated, all 11 projectiles
have full `onImpact`/`onHit` behavior from decompiled original source.
Sound system complete: 66 registered SoundEvents, 22 entity sound classes fixed.
Boss system: `EntityTaintacleGiant` with `BossInfoServer` + enrage,
`EntityCultistPortal` migrated to `EntityThaumcraftBoss`.
Alchemy system: `TileCrucible` full server port (FluidTank, heat, aspect decomp,
smelting, spill, bellows, NBT, capability sync).
Round C complete: `WarpEvents` (full ~340 lines),
`EventHandlerEntity` (16 handlers, 12 with real logic), `EventHandlerRunic` (3 handlers +
static helpers, runic charge state), `EventHandlerWorld` (11 handlers + chunk retrogen),
`ServerTickEventsFML` (world tick, block swap, chunk regen, inner classes).
Round D complete: CultistPortal boss stages, EldritchGuardian attacks,
ContainerGhostSlots + 3 container fixes, InventoryTrunk/Pech, ChampionModifiers,
generateVisEffect+PacketFXVisDrain, ItemSpawnerEgg deleted+18 eggs added,
EntityEldritchOrb Wither fix, EntityWatcher gaze (AIGuardianAttack inner class),
EntityCultistCleric ranged attack (homing orb + triple fireball + spawn data).
Pending: D2 (Pech NBT persistence/types), D8 (3 empty entity shells).

**Next milestone:** Complete all work that does NOT require Phase 8-10 (client GUI,
rendering, recipes, research data). This is documented in the
**Pre-Phase 8-10 Priority Matrix** below.

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
| **Files to modify** | 18 packet classes (8 misc + 10 playerdata stubs) in `thaumcraft/common/lib/network/` |
| **Original source** | Individual `.class` files in `thaumcraft_src/thaumcraft/common/lib/network/` |
| **Missing** | `fromBytes()` and `toBytes()` in each packet — currently empty |
| **Effort** | M — 18 files, each 5-20 lines for serialization + onMessage handler |
| **Strategy** | Process in 3 batches: (1) ScannedToServer, (2) remaining playerdata stubs, (3) misc packets |

**Note**: 14 FX packets moved to Phase 8r.1 (pure client-side, no game logic dependency).

**Status**: ⚠️ PARTIAL. Dispatch pattern in place (PacketBase.onMessage + PacketHandler.DISPATCH_HANDLER). STUB_HANDLER deleted. 7 playerdata sync packets (Aspects, Research, ScannedEntities/Items/Phenomena, Warp, ResearchComplete) have real `onMessage()` handlers. 18 remaining:

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

**Status: ✅ COMPLETED** (commit 0b893af)

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

**Status: ✅ COMPLETED** (11 block classes: CosmeticSolid/Opaque, CustomOre, Crystal, Eldritch, StoneDevice, MetalDevice, WoodenDevice, MagicalLog, Table, Taint. Remaining 8 are hand-mineable or unbreakable.)

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

### 6r.1 — AI classes: shouldExecute() → real logic (CRITICAL) ✅ DONE

**44/44 AI classes fully ported.** All batches complete.

#### Batch 1: Combat AI (9 files) ✅ DONE

| # | File | Source class | Effort | Status |
|---|------|--------------|--------|--------|
| 1 | `AIAttackOnCollide.java` | `thaumcraft_src/.../AIAttackOnCollide.class` | L | ✅ |
| 2 | `AIGolemAttackOnCollide.java` | `thaumcraft_src/.../AIGolemAttackOnCollide.class` | L | ✅ |
| 3 | `AICreeperSwell.java` | `thaumcraft_src/.../AICreeperSwell.class` | L | ✅ |
| 4 | `AIAvoidCreeperSwell.java` | `thaumcraft_src/.../AIAvoidCreeperSwell.class` | L | ✅ |
| 5 | `AICultistHurtByTarget.java` | `thaumcraft_src/.../AICultistHurtByTarget.class` | L | ✅ |
| 6 | `AIDartAttack.java` | `thaumcraft_src/.../AIDartAttack.class` | L | ✅ |
| 7 | `AIHurtByTarget.java` | `thaumcraft_src/.../AIHurtByTarget.class` | L | ✅ |
| 8 | `AILongRangeAttack.java` | `thaumcraft_src/.../AILongRangeAttack.class` | L | ✅ |
| 9 | `AINearestAttackableTarget.java` | `thaumcraft_src/.../AINearestAttackableTarget.class` | L | ✅ |

Also ported: `AINearestAttackableTargetSorter`, `AINearestButcherTarget`, `AIOldestAttackableTargetSorter`, `AITarget`.

#### Batch 2: Golem Inventory/Task AI (16 files) ✅ DONE

| # | File | Source class | Effort | Status | Dependencies also ported |
|---|------|--------------|--------|--------|------------------------|
| 1 | `AIHomeTake.java` | `AIHomeTake.class` | L | ✅ | `InventoryUtils`, `GolemHelper.getItemsNeeded`, `getFirstItemUsingTimeout`, `getDoubleChest` |
| 2 | `AIHomePlace.java` | `AIHomePlace.class` | L | ✅ | `InventoryUtils.placeItemStackIntoInventory`, `getDoubleChest` |
| 3 | `AIHomeDrop.java` | `AIHomeDrop.class` | L | ✅ | EntityItem API (`setPickupDelay`) |
| 4 | `AIHomeReplace.java` | `AIHomeReplace.class` | L | ✅ | `GolemHelper.isOnTimeOut`, `findSomething*Core` |
| 5 | `AIHomeTakeSorting.java` | `AIHomeTakeSorting.class` | L | ✅ | `GolemHelper.getItemsNeeded`, `InventoryUtils.extractStack` |
| 6 | `AIEmptyGoto.java` | `AIEmptyGoto.class` | L | ✅ | `GolemHelper.getContainersWithRoom`, `RandomPositionGenerator`, `Vec3d` |
| 7 | `AIEmptyDrop.java` | `AIEmptyDrop.class` | L | ✅ | `GolemHelper.getMarkedBlocksAdjacentToGolem` |
| 8 | `AIEmptyPlace.java` | `AIEmptyPlace.class` | L | ✅ | `getMarkedContainersAdjacentToGolem`, `getMarkedSides` |
| 9 | `AIFillTake.java` | `AIFillTake.class` | L | ✅ | `GolemHelper.getMarkedContainersAdjacentToGolem`, `getMarkedSides` |
| 10 | `AIFillGoto.java` | `AIFillGoto.class` | L | ✅ | `GolemHelper.getMissingItems`, `getContainersWithGoods`, `OreDictionary` |
| 11 | `AIItemPickup.java` | `AIItemPickup.class` | L | ✅ | `AxisAlignedBB` search, `EntityItem.getItem()`, `InventoryUtils.areItemStacksEqualStrict` |
| 12 | `AISortingGoto.java` | `AISortingGoto.class` | L | ✅ | `GolemHelper.getMarkedContainers` |
| 13 | `AISortingPlace.java` | `AISortingPlace.class` | L | ✅ | `getMarkedContainersAdjacentToGolem`, `getMarkedSides`, `placeItemStackIntoInventory` |
| 14 | `AIHarvestCrops.java` | `AIHarvestCrops.class` | L | ✅ | `CropUtils.isGrownCrop`, `FakePlayer`, `Block.harvestBlock` |
| 15 | `AIHarvestLogs.java` | `AIHarvestLogs.class` | L | ✅ | `Utils.isWoodLog`, `BlockUtils.breakFurthestBlock` |
| 16 | `AIFish.java` | `AIFish.class` | L | ✅ | `EntityGolemBobber`, `EnumParticleTypes`, custom `WeightedLoot` (replaces removed `WeightedRandomFishable`) |

#### Batch 3: Golem Essentia/Liquid AI + Pech AI (10 files) ✅ DONE

| # | File | Source class | Effort | Status |
|---|------|--------------|--------|--------|
| 1 | `AIEssentiaGather.java` | `thaumcraft_src/.../AIEssentiaGather.class` | L | ✅ |
| 2 | `AIEssentiaEmpty.java` | `thaumcraft_src/.../AIEssentiaEmpty.class` | L | ✅ |
| 3 | `AIEssentiaGoto.java` | `thaumcraft_src/.../AIEssentiaGoto.class` | L | ✅ |
| 4 | `AILiquidGather.java` | `thaumcraft_src/.../AILiquidGather.class` | L | ✅ |
| 5 | `AILiquidEmpty.java` | `thaumcraft_src/.../AILiquidEmpty.class` | L | ✅ |
| 6 | `AILiquidGoto.java` | `thaumcraft_src/.../AILiquidGoto.class` | L | ✅ |
| 7 | `AIUseItem.java` | `thaumcraft_src/.../AIUseItem.class` | L | ✅ |
| 8 | `AIAltarFocus.java` | `thaumcraft_src/.../AIAltarFocus.class` | L | ✅ |
| 9 | `AIPechItemEntityGoto.java` | `thaumcraft_src/.../AIPechItemEntityGoto.class` | L | ✅ |
| 10 | `AIPechTradePlayer.java` | `thaumcraft_src/.../AIPechTradePlayer.class` | L | ✅ |

#### Batch 4: Misc AI (4 files) ✅ DONE

| # | File | Source class | Effort | Status |
|---|------|--------------|--------|--------|
| 1 | `AIConvertGrass.java` | `thaumcraft_src/.../AIConvertGrass.class` | L | ✅ |
| 2 | `AIDoorInteract.java` | `thaumcraft_src/.../AIDoorInteract.class` | L | ✅ |
| 3 | `AIReturnHome.java` | `thaumcraft_src/.../AIReturnHome.class` | L | ✅ |
| 4 | `AIWander.java` | `thaumcraft_src/.../AIWander.class` | L | ✅ |

### 6r.2 — Boss mob AI tasks (CRITICAL) ✅ DONE

All 5 bosses have task registration + BossInfoServer boss bar.
See `EntityThaumcraftBoss` base class for damage clamp + tracking.

| # | Boss | Status |
|---|------|--------|
| 1 | `EntityCultistLeader` | ✅ addTask + BossInfoServer |
| 2 | `EntityEldritchGolem` | ✅ addTask + makeHeadless() |
| 3 | `EntityEldritchWarden` | ✅ addTask + BossInfoServer |
| 4 | `EntityThaumcraftBoss` | ✅ BossInfoServer, damage ≤ 35, add/remove tracking |
| 5 | `EntityCultistPortal` | ⏳ spawn logic in onUpdate (Phase 6r.5) |

### 6r.3 — Hostile mob AI registration (CRITICAL) ✅ DONE

All 10 hostile mobs with add-task AI have full registration.
Group B manual AI (5 entities) has lifecycle migration (updateAITasks/onLivingUpdate).

| # | Mob | AI | Status |
|---|-----|-----|--------|
| 1 | `EntityTaintacle` | `updateAITasks()` faceEntity, `onLivingUpdate()` biome dmg+flail, `move()` rooted | ✅ |
| 2 | `EntityTaintacleGiant` | Inherits Taintacle + attribute override only; **needs boss bar** | ⚠️ |
| 3 | `EntityWisp` | `updateAITasks()` waypoint+zap+typeInit, `onLivingUpdate()` particles | ✅ |
| 4 | `EntityFireBat` | `updateAITasks()` hang/fly+target+melee, `onLivingUpdate()` motion+particles | ✅ |
| 5 | `EntityTaintSpore` | `sporeOnUpdate()` growth+burst, `spiderBurst()` spawn spiders+fibre deplete | ✅ |
| 6 | `EntityThaumicSlime` | `updateAITasks()` target+spit+merge+jump, `setDead()` split | ✅ |
| 7 | `EntityCultistCleric` | ✅ 9 tasks + 2 target tasks |
| 8 | `EntityCultistKnight` | ✅ 8 tasks + 2 target tasks |
| 9 | `EntityEldritchGuardian` | ✅ 7 tasks + 3 target tasks |
| 10 | `EntityEldritchCrab` | ✅ 5 tasks + 3 target tasks |
| 11 | `EntityTaintCreeper` | ✅ 7 tasks + 2 target + onUpdate explosion |
| 12 | `EntityPech` | ✅ 9 tasks + setCombatTask() |
| 13-50 | Remaining (tame animals, passives) | Inherit vanilla AI or no task AI needed |

### 6r.4 — Projectile onImpact logic (CRITICAL) ✅ DONE

**11/11 projectiles have full `onImpact`/`onHit` behavior from CFR decompiled source.**

| # | File | Effect | Status |
|---|------|--------|--------|
| 1 | `EntityAlumentum.java` | Explosion radius 1.66, mobGriefing | ✅ |
| 2 | `EntityBottleTaint.java` | Taint poison 5-block AOE, biome conversion + fibre (10 attempts) | ✅ |
| 3 | `EntityEldritchOrb.java` | Zero-G, AOE damage `attack*0.666`, Weakness 160t | ✅ |
| 4 | `EntityEmber.java` | Fire damage + setFire, block fire placement, velocity decay | ✅ |
| 5 | `EntityExplosiveOrb.java` | Direct hit `strength*1.5`, `newExplosion`, deflectable | ✅ |
| 6 | `EntityFrostShard.java` | **Full bounce physics** (axis reversal, attenuation), Slowness, fragile shatter, rotation, DataParameter+NBT | ✅ |
| 7 | `EntityGolemOrb.java` | Homing, `thrower.attack*(0.6/1.0)`, deflectable, spawn data sync | ✅ |
| 8 | `EntityPechBlast.java` | AOE 3×3×3, `strength+2` dmg, Wither/Slowness/Weakness random or all if nightshade | ✅ |
| 9 | `EntityPrimalArrow.java` | 6 primal types via DataParameter + onHit(super) | ✅ |
| 10 | `EntityPrimalOrb.java` | Explosion r=2/4, 1-10% taintSplosion/randomNode, seeker | ✅ |
| 11 | `EntityShockOrb.java` | AOE damage (area=4, LOS check), blockAiry(type=10) placement | ✅ |

### 6r.5 — Mob special abilities (HIGH)

| # | Mob | Missing ability | Effort | Status |
|---|-----|-----------------|--------|--------|
| 1 | `EntityWisp` | Lightning zap attack, wispFX particles, drop ItemWispEssence | L | ✅ |
| 2 | `EntityWatcher` | Gaze attack: potion effect on player looking at it within 16 blocks | L | ✅ |
| 3 | `EntityPech` | Pech blast projectile when angry, NBT PECH_TYPE/ANGRY, trade GUI | M | ⏳ |
| 4 | `EntityThaumicSlime` | Split on death, spit projectile, merge, jump | L | ✅ |
| 5 | `EntityInhabitedZombie` | On death: spawn `EntityEldritchCrab` | L | ⏳ |
| 6 | `EntityCultistPortal` | Spawn cultists on timer | L | ⏳ |
| 7 | `EntityEldritchGuardian` | Fire EldritchOrb projectile, sonic screech AoE | M | ✅ |
| 8 | `EntityCultistCleric` | Fire dart projectile, healing aura | L | ✅ |

### 6r.6 — Champion modifier fixes (MEDIUM)

| # | File | Fix | Effort |
|---|------|-----|--------|
| 1 | `ChampionModMighty.java` | Implement knockback effect: `target.addVelocity(motionX*1.5, 0.3, motionZ*1.5)` | L |
| 2 | `ChampionModWarp.java` | Implement warp application: `ThaumcraftApi.addWarpToPlayer(player, 1)` | L |
| 3 | `ChampionModInfested.java` | Implement death spawn: `world.spawnEntity(new EntitySilverfish(world))` | L |
| 4 | All 14 `showFX()` | Implement client FX (particles, sounds) — post-GUI, Phase 8 dependent | M |

### 6r.7 — Sound registration + entity sound methods (MEDIUM)

Sound is **completely dead** — `registerSounds()` in `Thaumcraft.java` is an empty stub.
No `sounds.json` exists, no `.ogg` files are present. 7 entity/AI files do
`SoundEvent.REGISTRY.getObject("thaumcraft:*")` which always returns `null`.
12 entity classes return `null` for all sound methods.

**This is a pre-Phase 8-10 item** — pure data + registration work, no GUI needed.

#### 6r.7.1 — Create sounds.json + register sounds ✅ DONE

**Status:** ✅ All 66 SoundEvent fields registered via `TCSounds`, 111 OGG in `assets/thaumcraft/sounds/`.
Empty `registerSounds()` stub removed from `Thaumcraft.java`.

| Field | Value |
|-------|-------|
| **Files to modify** | `Thaumcraft.java` (registerSounds body), create `assets/thaumcraft/sounds.json`, create `assets/thaumcraft/sounds/` directory |
| **Original source** | `thaumcraft_src/assets/thaumcraft/sounds.json` (sound definitions) + `thaumcraft_src/thaumcraft/common/Thaumcraft.class` (registration) |
| **Effort** | M — ~30 SoundEvent registrations + JSON file |

#### 6r.7.2 — Fix entity sound methods to return real SoundEvent ✅ DONE

All 22 entity classes with null/wrong/missing sound methods updated to return correct `SoundEvent`:

| # | Entity | Sounds from original | Effort | Status |
|--:|--------|---------------------|:------:|:-----:|
| 1 | `EntityTaintacle` | `TCSounds.ROOTS` / `TENTACLE` / `TENTACLE` | L | ✅ |
| 2 | `EntityTaintSpore` | `TCSounds.SWARM` / `GORE` / `GORE` | L | ✅ |
| 3 | `EntityTaintCreeper` | `SoundEvents.ENTITY_CREEPER_HURT/DEATH` | L | ✅ |
| 4 | `EntityPech` | `TCSounds.PECH_IDLE` / `PECH_HIT` / `PECH_DEATH` | L | ✅ |
| 5 | `EntityWatcher` | `SoundEvents.ENTITY_GUARDIAN_*_LAND` | L | ✅ |
| 6 | `EntityCultist` | null (base class, intentional silence) | L | ✅ |
| 7 | `EntityCultistCleric` | `TCSounds.CHANT` (ambient only) | L | ✅ |
| 8 | `EntityTaintChicken` | `SoundEvents.ENTITY_CHICKEN_AMBIENT/HURT/DEATH` | L | ✅ |
| 9 | `EntityTaintPig` | `SoundEvents.ENTITY_PIG_AMBIENT/HURT/DEATH` | L | ✅ |
| 10 | `EntityTaintCow` | `SoundEvents.ENTITY_COW_AMBIENT/HURT/DEATH` | L | ✅ |
| 11 | `EntityTaintSheep` | `SoundEvents.ENTITY_SHEEP_AMBIENT/HURT/DEATH` | L | ✅ |
| 12 | `EntityTaintSwarm` | `TCSounds.SWARMATTACK` (hurt/death), `null` (ambient) | L | ✅ |
| 13 | `EntityTaintVillager` | `SoundEvents.ENTITY_VILLAGER_AMBIENT/HURT/DEATH` | L | ✅ |
| 14 | `EntityWisp` | `TCSounds.WISPLIVE` / `BLOCK_FIRE_EXTINGUISH` / `WISPDEAD` | L | ✅ |
| 15 | `EntityFireBat` | `SoundEvents.ENTITY_BAT_AMBIENT/HURT/DEATH` + vol 0.1f | L | ✅ |
| 16 | `EntityEldritchCrab` | `TCSounds.CRABTALK` / `ENTITY_GUARDIAN_HURT` / `CRABDEATH` | L | ✅ |
| 17 | `EntityEldritchGuardian` | `TCSounds.EGIDLE` / none / `EGDEATH` + vol 1.5f | L | ✅ |
| 18 | `EntityEldritchWarden` | `TCSounds.EGIDLE` / none / `EGDEATH` | L | ✅ |
| 19 | `EntityEldritchGolem` | `SoundEvents.ENTITY_IRONGOLEM_HURT/DEATH` | L | ✅ |
| 20 | `EntityCultistPortal` | `TCSounds.MONOLITH` / `ZAP` / `SHOCK` | L | ✅ |
| 21 | `EntityTaintSpider` | `getSoundPitch` = 0.7f only (inherits spider sounds) | L | ✅ |
| 22 | `EntityMindSpider` | `getSoundPitch` = 0.7f only (inherits spider sounds) | L | ✅ |

### 6r.8 — EntityTaintacleGiant champion + boss bar (MEDIUM) ✅ DONE

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/common/entities/monster/boss/EntityTaintacleGiant.java` |
| **Original source** | `thaumcraft_src/.../boss/EntityTaintacleGiant.class` |
| **Missing** | Boss bar via `BossInfoServer` (composition), champion modifiers, anger mechanic, potion effects on damage, particles, special loot drop |
| **Effort** | M — ~80 lines from CFR |
| **Dependencies** | `EntityThaumcraftBoss` (✅), `EntityUtils.makeChampion()` (✅) |

### 6r.9 — Container GUIs canInteractWith (HIGH)

| # | File | Fix | Effort |
|---|------|-----|--------|
| 1 | `ContainerGolem.java` | `canInteractWith` check player distance to entity | L |
| 2 | `ContainerTravelingTrunk.java` | `canInteractWith` check player distance to entity | L |
| 3 | `ContainerPech.java` | `canInteractWith` check player distance to entity + pech alive | L |

### 6r.10 — InventoryTrunk + InventoryPech (HIGH)

| # | File | Fix | Effort |
|---|------|-----|--------|
| 1 | `InventoryTrunk.java` | Implement `IInventory`: ItemStack[27], `getStackInSlot`, `setInventorySlotContents`, `markDirty`, NBT read/write | L |
| 2 | `InventoryPech.java` | Implement `IInventory`: ItemStack[18], pech trade inventory with input + output slots | L |

### 6r.11 — ItemSpawnerEgg registration (HIGH)

| Field | Value |
|-------|-------|
| **Files to modify** | `thaumcraft/common/config/ConfigItems.java` |
| **Fix** | Register `ItemSpawnerEgg` with registry name "thaumcraft:spawn_egg" |
| **Effort** | L — 1 line |

### 6r.12 — Entity NBT persistence fixes (MEDIUM)

| # | File | Fix | Effort | Status |
|---|------|-----|--------|--------|
| 1 | `EntityPech.java` | `readEntityFromNBT`/`writeEntityToNBT`: save/load PECH_TYPE, ANGRY | L | ⏳ |
| 2 | `EntityWisp.java` | NBT Type persistence | L | ✅ |
| 3 | `EntityFrostShard.java` | Save/load DAMAGE, FROSTY fields | L | ✅ |

### 6r.13 — Empty base entity shells (MEDIUM)

| # | File | Fix | Effort |
|---|------|-----|--------|
| 1 | `EntityAspectOrb.java` | Implement `entityInit` (data watcher), NBT save/load, `onUpdate` (lifetime + merge) | L |
| 2 | `EntityFallingTaint.java` | Implement taint falling entity (similar to EntityFallingBlock) | L |
| 3 | `EntityGolemBobber.java` | Implement fishing bobber behavior | L |

---

## Pre-Phase 8-10 Priority Matrix

Work that can and should be completed before starting Phases 8-10 (GUI, recipes,
rendering). These items require **no client-side code**, no complex recipe data,
and no research tree definitions.

### Pre-8-10 Priority Legend

| Tier | Meaning | Examples |
|------|---------|----------|
| **A** | Must-do before 8-10: unblocks gameplay | Sound, TileCrucible, boss |
| **B** | High value, standalone: event system | WarpEvents, EventHandlerEntity, EventHandlerRunic |
| **C** | Medium value: fills gaps | EntityTaintacleGiant, mob special abilities |
| **D** | Defer to Phase 8-10: needs GUI/recipes/research | Foci, InfusionMatrix, ResearchTable |

### Tier A — Must-do before Phases 8-10

#### A.1 — Sound registration (6r.7.1): sounds.json + TCSounds ✅ DONE

Files: `sounds.json` (66 entries) + 111 OGG copied, `TCSounds` class with 66 static SoundEvent fields + `registerSounds` handler. Empty stub removed from `Thaumcraft.java`.
Effort: M (~1h). Dependencies: none.

#### A.2 — Entity sound methods (6r.7.2): fix 22 entity classes ✅ DONE

All 22 entity classes with null/wrong/missing sound methods updated to return correct `SoundEvent`.
Requires A.1 to be done first.
Effort: M (~30m). Dependencies: A.1.

#### A.3 — TileCrucible (4r.2): alchemy mechanic ✅ DONE

Full server-side port: `FluidTank` with water-only restriction via `canFillFluidType`, `IFluidHandler` capability via `CapabilityFluidHandler`, heat management (fire/lava/blockAiry heat), aspect decomposition (non-primal → primal at 150+ heat), item smelting via `findMatchingCrucibleRecipe`, spill/flux overflow (`blockFluxGoo`/`blockFluxGas` placement + thickening), `spillRemnants` (wand clear), NBT persistence, `getUpdateTag/handleUpdateTag` for chunk sync, `notifyBlockUpdate` for block sync.
Client FX guarded under `// Phase 8:` comments.
Dependencies created: `InventoryFake`, `BlockFluxGoo`/`BlockFluxGas` (minimal `LEVEL 0..7` blocks), `ThaumcraftCraftingManager.findMatchingCrucibleRecipe()` ported from original.
Requires `FMLCommonHandler.instance().firePlayerCraftingEvent` — available in official 1.12.2 Forge.

#### A.4 — EntityTaintacleGiant champion + boss bar (6r.8) ✅ DONE

Boss bar via `BossInfoServer` composition, champion via `makeChampion(true)`, enrage/anger/damage-cap, `isEntityInvulnerable`, `decreaseAirSupply`, particles, special drops with proximity check.
`EntityCultistPortal` migrated to `extends EntityThaumcraftBoss`.

### Round C — Events ✅ DONE

#### C.1 — WarpEvents (3r.1) ✅ DONE

Full port from original (338 lines decompiled → ~340 lines in port).
Package: `thaumcraft.common.lib.WarpEvents` (not `events/`).
Pure static utility with 7 methods: `checkWarpEvent`, `checkDeathGaze`, `spawnMist`,
`grantResearch`, `spawnGuardian`, `suddenlySpiders`, `getWarpFromGear`.
Uses `IPlayerKnowledge` for warp state, `Config.potion*` for potion effects,
clean 1.12.2 MCP naming.
Added `warpCounter` field to `IPlayerKnowledge`/`PlayerKnowledgeCapability`.
`PacketAspectPool` filled with proper fields/serialization.

#### C.2 — EventHandlerEntity (3r.6) — core handlers ✅ DONE

Full port from original (625 lines decompiled → ~300 lines in port).
16 `@SubscribeEvent` handlers: 12 with real logic (warp events, death gaze,
zombie brain drops, arrow interception, break speed, item use finish)
+ 4 empty stubs for Phase 8 features.
Added: `AttackEntityEvent` (left-click Pech), `LivingEntityUseItemEvent.Finish`
(replaces original `PlayerUseItemEvent.Finish`), `LivingEvent.LivingJumpEvent`.
Removed: `ItemExpireEvent` (not in original).

#### C.3 — EventHandlerRunic (3r.7) ✅ DONE

Full port from original (307 lines decompiled → ~300 lines in port).
3 `@SubscribeEvent` handlers: `livingTick` (runic charge scan + recharge from vis),
`entityHurt` (damage absorption + fortress mask effects + kinetic/healing/emergency
upgrades), `tooltipEvent` (runic charge + warp display).
3 static helpers: `getFinalCharge`, `getFinalWarp`, `getHardening`.
State managed via `HashMap<Integer, Integer[]>` keyed by player entity ID.
(Future: migrate to Capability-based storage.)
Champion mob handling simplified (no `CHAMPION_MOD` attribute in port).
Uses `BaublesApi.getBaublesHandler(player)` + `IBaublesItemHandler` for 7 bauble slots.

#### C.4 — EventHandlerWorld (3r.7b) ✅ DONE

Full port from original (246 lines decompiled → ~220 lines in port).
9 `@SubscribeEvent` handlers: world load/save/unload, chunk save/load (retrogen key),
item crafted (warp on craft), harvest drops (special mining), block place/multi-place
(boss proximity check), note block play (TileSensor), fill bucket (custom fluids).
+1 post-Phase 8 handler: `FurnaceFuelBurnTimeEvent` (replaces deprecated `IFuelHandler`).
`isNearActiveBoss` helper for Eldritch dimension block protection.

#### C.5 — ServerTickEventsFML (3r.7c): chunk regeneration + block swap queue ✅ DONE

Full port from original (233 lines decompiled → ~220 lines in port).
`TickEvent.WorldTickEvent` handler with server-side/Phase.END filter.
`tickChunkRegeneration`: processes `chunksToGenerate` queue (up to 10 chunks/tick),
re-runs world gen for retrogen.
`tickBlockSwap`: processes `swapList` queue for focus area-block replacement
(Portable Hole). Handles silk touch / fortune, chain propagation, creative mode.
`addSwapper`: static entry point for focus to queue block swaps.
Inner classes: `RestorableWardedBlock` (block NBT snapshot), `VirtualSwapper` (swap op).
Uses `BlockUtils.breakFurthestBlock` for adjacency chain detection.

### Round D — Remaining gaps

| # | Task | Phase | Effort | Dependencies |
|---|------|-------|--------|-------------|
| 1 | Mob special abilities: CultistPortal spawn timer, Watcher gaze, EldritchGuardian projectile | 6r.5 | M | Projectiles (✅) |
| 2 | Entity NBT persistence: EntityPech PECH_TYPE/ANGRY | 6r.12 | L | None |
| 3 | Container GUIs canInteractWith (3 containers) | 6r.9 | L | None |
| 4 | InventoryTrunk + InventoryPech | 6r.10 | L | None |
| 5 | ItemSpawnerEgg registration | 6r.11 | L | None |
| 6 | Champion modifier Mighty/Warp/Infested | 6r.6 | L | None |
| 7 | InternalMethodHandler stubs (generateVisEffect) | 3r.14 | L | None |
| 8 | Empty entity shells: EntityAspectOrb, EntityFallingTaint, EntityGolemBobber | 6r.13 | L | None |

### Tier D — Safely defer to Phase 8-10

These items depend on GUI, rendering, recipe system, or research data:

| # | Task | Reason to defer |
|---|------|----------------|
| 1 | Focus onFocusRightClick (10 foci) | Wand interaction GUI needed |
| 2 | TileInfusionMatrix | Depends on recipe system (Phase 9) |
| 3 | TileResearchTable update() | Depends on ResearchNoteData + research system |
| 4 | TileDeconstructionTable update() | Connects to infusion/essentia system |
| 5 | ResearchManager missing methods | Research data is Phase 9 |
| 6 | FX network packets (14 files) | Pure client-side, needs rendering |
| 7 | ChampionFX.showFX() | Client particles, Phase 8 |
| 8 | Model registration + tooltips | Client-side, Phase 8 |
| 9 | Most empty TEs (Tube*, Bore, AlchemyFurnace, etc.) | Connect to systems not yet built |
| 10 | Foci, relics, baubles onWornTick | Depend on ItemWandCasting (Phase 10) |

---

## Phase 8r — Client Network & FX

### 8r.1 — FX network packets (14 files)

**Moved from 3r.13.** These are pure client-side visual packets (particles, beams, zaps, bubbles).
No game logic dependency — safe to defer until Phase 8 client rendering.

| # | File | Effort |
|---|------|--------|
| 1 | `PacketFXBeamPulse.java` | L |
| 2 | `PacketFXBeamPulseGolemBoss.java` | L |
| 3 | `PacketFXBlockArc.java` | L |
| 4 | `PacketFXBlockBubble.java` | L |
| 5 | `PacketFXBlockDig.java` | L |
| 6 | `PacketFXBlockSparkle.java` | L |
| 7 | `PacketFXBlockZap.java` | L |
| 8 | `PacketFXEssentiaSource.java` | L |
| 9 | `PacketFXInfusionSource.java` | L |
| 10 | `PacketFXShield.java` | L |
| 11 | `PacketFXSonic.java` | L |
| 12 | `PacketFXVisDrain.java` | L |
| 13 | `PacketFXWispZap.java` | L |
| 14 | `PacketFXZap.java` | L |

Each needs fromBytes/toBytes for position/color/target fields + a side-only onMessage
to spawn the corresponding particle effect on the client.

---

## Priority Execution Matrix (updated: commit 1796857)

### P0 — Must fix first (blocks + networking) ✅ DONE

```
P0: Block metadata (4r.10)       ✅  →  Blocks can be placed correctly
P0: Harvest levels (4r.11)        ✅  →  Blocks can be broken
P0: PacketHandler (3r.13)         ⚠️  →  Dispatch works, 11 non-FX packets still need serialization
```

### P1 — Core mechanics

| Order | Task | Phase | Effort | Status |
|-------|------|-------|--------|--------|
| 1 | AI classes 44/44 (6r.1) | 6r | XL | ✅ *All 44 done* |
| 2 | Hostile mob AI registration (6r.3) | 6r | M | ✅ *Group A + Group B + bosses* |
| 3 | Boss mob AI tasks (6r.2) | 6r | M | ✅ *addTask + BossInfoServer* |
| 4 | Projectile onImpact (6r.4) | 6r | M | ✅ *11/11 full behavior* |
| 5 | Group B manual AI lifecycle | 6r | M | ✅ *5 entities* |
| 6 | **Sound registration (A.1)** | 6r | M | ✅ *all 66 SoundEvents registered* |
| 7 | **Entity sound methods (A.2)** | 6r | M | ✅ *22 entity classes fixed* |
| 8 | **TileCrucible (A.3)** | 4r | XL | ✅ *full server logic + FluidTank + flux blocks* |
| 9 | **EntityTaintacleGiant boss (A.4)** | 6r | M | ✅ *boss bar + champion + enrage* |
| 10 | **WarpEvents (C.1)** | 3r | M | ✅ *full 338-line port* |
| 11 | **EventHandlerEntity (C.2)** | 3r | M | ✅ *16 handlers, 12 real* |
| 12 | **EventHandlerRunic (C.3)** | 3r | M | ✅ *3 handlers + helpers* |
| 13 | **EventHandlerWorld (C.4)** | 3r | M | ✅ *11 handlers + retrogen* |
| 14 | **ServerTickEventsFML (C.5)** | 3r | M | ✅ *world tick + swap* |
| 15 | Focus items (5r.1) | 5r | M | *defer to 8-10* |
| 16 | Relic right-click (5r.2) | 5r | L | *defer to 8-10* |
| 17 | Baubles onWornTick (5r.3) | 5r | M | *defer to 8-10* |
| 18 | TileInfusionMatrix (4r.1) | 4r | XL | *defer to 8-10* |
| 19 | TileTube* network (4r.9) | 4r | L | *defer to 8-10* |
| 20 | TileNode recharge (4r.3) | 4r | XL | *defer to 8-10* |
| 21 | Enchantment fixes (3r.12) | 3r | L | *defer to 8-10* |
| 22 | WandManager discount (3r.9) | 3r | L | *defer to 8-10* |
| 23 | ItemWandCasting methods (3r.10) | 3r | M | *defer to 8-10* |
| 24 | Potion fixes (3r.11) | 3r | L | *defer to 8-10* |

### P2 — Subsystems

| Order | Task | Phase | Effort | Dependencies |
|-------|------|-------|--------|-------------|
| 1 | ResearchManager methods (3r.3) | 3r | L | *defer to 8-10* |
| 2 | ResearchNoteData + HexUtils (3r.4) | 3r | L | *defer to 8-10* |
| 3 | TileResearchTable update (4r.13) | 4r | M | *defer to 8-10* |
| 4 | TileDeconstructionTable (4r.14) | 4r | M | *defer to 8-10* |
| 5 | IPlayerKnowledge expansion (3r.8) | 3r | L | *defer to 8-10* |
| 6 | 17 missing TE files (4r.12) | 4r | L | *defer to 8-10* |
| 7 | PrimalCrusher hierarchy (5r.4) | 5r | L | *defer to 8-10* |
| 8 | Void equipment (5r.5) | 5r | L | *defer to 8-10* |
| 9 | Elemental tools (5r.7) | 5r | L | *defer to 8-10* |
| 10 | Armor interfaces (5r.9) | 5r | L | *defer to 8-10* |
| 11 | Mob special abilities (6r.5) | 6r | M | *defer to 8-10* |
| 12 | Container GUIs (6r.9) | 6r | L | *defer to 8-10* |
| 13 | InventoryTrunk/Pech (6r.10) | 6r | L | *defer to 8-10* |
| 14 | ItemSpawnerEgg (6r.11) | 6r | L | *defer to 8-10* |
| 15 | ChampionMod Mighty/Warp/Infested (6r.6) | 6r | L | *defer to 8-10* |
| 16 | InternalMethodHandler (3r.14) | 3r | L | *defer to 8-10* |

### P3 — Audio & FX (mostly deferred)

| Order | Task | Phase | Effort | Status |
|-------|------|-------|--------|--------|
| 1 | **Sound registration (A.1)** | 3r | M | **Pre-8-10** |
| 2 | **Entity sound methods (A.2)** | 6r | M | **Pre-8-10** |
| 3 | Champion FX (6r.6.4) | 6r | M | *defer (Phase 8)* |
| 4 | Taint block spread (4r.15) | 4r | L | *defer* |
| 5 | getIsRepairable (5r.8) | 5r | L | *defer* |
| 6 | Entity NBT fixes (6r.12) | 6r | L | *partial fix in pre-8-10* |
| 7 | Empty entity shells (6r.13) | 6r | L | *defer* |

### P4 — Polish

| Order | Task | Phase | Effort | Status |
|-------|------|-------|--------|--------|
| 1 | Model registration (5r.10) | 5r | XL | *defer (Phase 8)* |
| 2 | Tooltips (5r.10) | 5r | M | *defer* |
| 3 | getRarity overrides | 5r | L | *defer* |
| 4 | TileJarFillable.containerContains (4r.17) | 4r | L | *defer* |
| 5 | null displayName (4r.18) | 4r | L | *defer* |

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

## File-Level Execution Order (Updated: commit 1796857)

### ✅ Completed Rounds
```
Round 1: P0 Foundation               ✅ Block metadata + harvest levels + packet dispatch
Round 2: P1 AI (Combat + Inventory)   ✅ AI Batch 1 (9) + Batch 2 (16)
Round 3: P1 AI (Essentia + Misc)      ✅ AI Batch 3 (10) + Batch 4 (4)
Round 4: P1 AI (Registration)         ✅ Hostile mob AI + boss AI + Group B manual
Round 5: P1 Projectiles               ✅ 11/11 onImpact full behavior
```

### Pre-Phase 8-10 Execution Order (Pending)

These rounds produce BUILD SUCCESSFUL after each step.

```
Round A: Sound + Boss + Alchemy (A.5 — EntityCultistPortal)
  A1. Create sounds.json + TCSounds (66 SoundEvents)           ✅ done
  A2. Fix entity sound methods (22 classes)                    ✅ done
  A3. TileCrucible full server logic                           ✅ done
  A4. EntityTaintacleGiant champion + boss bar + full behavior ✅ done
  A5. EntityCultistPortal → EntityThaumcraftBoss               ✅ done

Round B: Alchemy
  B1. TileCrucible: full CFR port (fluid tank, attemptSmelt, aspect decomposition, spill, bellows) ✅ done

Round C: Events
  C1. WarpEvents: new class from CFR (checkWarpEvent, checkDeathGaze, spawnMist, etc.)   ✅ done
  C2. EventHandlerEntity: fill 11 live handlers                                            ✅ done
  C3. EventHandlerRunic: shield regen + absorption                                        ✅ done
  C4. EventHandlerWorld: world save/load + block events                                    ✅ done
  C5. ServerTickEventsFML: chunk generation + block swap queue                             ✅ done

Round D: Remaining gaps
  D1. Mob special abilities (CultistPortal, Watcher, EldritchGuardian, CultistCleric)
  D2. EntityPech NBT persistence (PECH_TYPE, ANGRY)
  D3. Container GUIs canInteractWith (3 files)
  D4. InventoryTrunk + InventoryPech
  D5. ItemSpawnerEgg registration
  D6. ChampionMod Mighty/Warp/Infested
  D7. InternalMethodHandler stubs
  D8. Empty entity shells (EntityAspectOrb, EntityFallingTaint, EntityGolemBobber)

#### Round D Status

| # | Scope | Status |
|---|-------|--------|
| D1.2 | CultistPortal: full boss spawn logic (stages, banners, crates, minions, bosses) | ✅ done |
| D1.3 | EldritchGuardian: ranged attacks (orb/screech), magic halving, fire on hit, NBT home, HP regen | ✅ done |
| D3 | ContainerGhostSlots + canInteractCheck + onContainerClosed (3 containers) | ✅ done |
| D4 | InventoryTrunk (36 slots, NBT, dropAll) + InventoryPech (5 slots, trade input) | ✅ done |
| D5 | ItemSpawnerEgg deleted + 18 entity eggs added | ✅ done |
| D6 | ChampionMod Mighty/Warp/Infested | ✅ done |
| D7 | PacketFXVisDrain + Utils.generateVisEffect + InternalMethodHandler wiring | ✅ done |
| D8 | EntityEldritchOrb: Weakness→Wither fix | ✅ done |
```

### Deferred to Phase 8-10
```
Round 6: P1 Items (Foci, Relics, Baubles)       — needs wand GUI
Round 7: P1 Core (TileInfusionMatrix)             — needs recipe system
Round 8: P2 Research (ResearchManager, etc.)      — needs research data
Round 9: Missing TEs (Tube*, Bore, Furnace, etc.) — connect to other systems
Round 10: P3-P4 Polish (models, tooltips)          — client-side
```

---

## Appendix: Audit Coverage Map (Updated: commit 1796857)

| Phase | Total classes | Verified | Remaining stubs | Stub rate | Notes |
|-------|--------------|----------|----------------|-----------|-------|
| 0-2 | ~20 | 20 | 0 | 0% | Framework complete |
| 3 | ~30 | 30 | ~12 (events, handlers) | **~40%** | Events, WarpEvents, Runic still stub |
| 4 | 151 | 80 | ~49 (TEs) + 0 (blocks) | **~32%** | 49/61 TEs empty; blocks all ✅ |
| 5 | 110 | 55 | ~55 (all foci + relics) | **~50%** | Foci 10/10 stub; relics 0/5 exist |
| 6 | 130 | 128 | ~6 | **~5%** | AI 44/44 ✅, projectiles 11/11 ✅, bosses ✅ |
| 7 | 35 | 35 | 0 | 0% | World gen complete |
| **Total** | **~476** | **348** | **~122** | **~26%** | Down from ~55% at project start |

**Key improvements since initial audit:**
- AI classes: 39/44 stubs → 44/44 done
- Projectiles: 10/11 stubs → 11/11 full behavior
- Boss AI: 0/5 → 5/5 registration + bar
- Group B manual AI: 0/5 → 5/5 lifecycle migration
- Sound: not tracked → 0% (pre-8-10 work remains)
