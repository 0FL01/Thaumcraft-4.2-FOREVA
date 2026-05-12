# Round D — Answers from Upstream 1.12.2 API Expert

Answers to 53 atomic questions. Source: validated against Forge 1.12.2 API,
MCP stable_39 mappings, and Minecraft source.

---

## Key Corrections (Read First)

| # | What we thought | Actual 1.12.2 |
|---|----------------|---------------|
| 1 | `DataSerializers.SHORT` exists | **No** — use `DataSerializers.VARINT` for short-range ints |
| 2 | `IEntityAdditionalSpawnData` in `net.minecraft.entity` | **Wrong** — it's `net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData` |
| 3 | `.egg(c1,c2)` creates `thaumcraft:spawn_egg` item | **No** — it hooks into vanilla `ItemMonsterPlacer`; no custom item |
| 4 | Forge has built-in ghost slots | **No** — port `ContainerGhostSlots` manually via `Container#slotClick(...)` |
| 5 | `DamageSource.causeIndirectEntityDamage(...)` needed | **No** — `causeIndirectDamage(Entity, EntityLivingBase)` still exists |
| 6 | `DamageSource.causeMagicDamage(EntityLivingBase)` exists | **No** — use `causeIndirectMagicDamage(Entity, Entity)` or `DamageSource.MAGIC` |
| 7 | `isReplaceableOreGen(IBlockState, IBlockAccess, BlockPos, BlockState)` | **Wrong** — signature is `(IBlockState, IBlockAccess, BlockPos, Predicate<IBlockState>)` |

---

## D1 — Mob Special Abilities

### D1.1 — EntityWatcher Gaze

**Q1. `DamageSource.causeIndirectDamage(Entity source, Entity indirectEntityIn)`**
✅ Exists, signature: `causeIndirectDamage(Entity source, EntityLivingBase indirectEntityIn)`.
Second param narrowed to `EntityLivingBase`. Old pattern works if `EntityWatcher extends EntityLivingBase`.

**Q2. `DamageSource.causeMagicDamage(EntityLivingBase)`**
❌ Does not exist in 1.12.2. Use:
- `DamageSource.causeIndirectMagicDamage(Entity source, @Nullable Entity indirectEntityIn)` — best for gaze (attributes to mob)
- `DamageSource.MAGIC` — loses source attribution
- `new EntityDamageSource("magic", this).setMagicDamage()` — explicit

**Q3. `ReflectionHelper` for `EntityMoveHelper` fields**
✅ `EntityMoveHelper.posX/posY/posZ` are **protected** (not private) in 1.12.2.
Use public getters: `getX()`, `getY()`, `getZ()`, `getSpeed()`. No reflection needed.
`EntityLookHelper` fields are private but also have public getters `getLookPosX/Y/Z`.

**Q4. `dataManager.set()` + `setEntityState(byte)`**
✅ Both patterns work in 1.12.2:
- `dataManager.set(DataParameter<T>, value)` replaces `dataWatcher.updateObject(id, value)`
- `world.setEntityState(entity, byte)` + `entity.handleStatusUpdate(byte)` — byte triggers still correct

**D1.1 Verdict:** Port `AIGuardianAttack` inner class with `dataManager`, public getters, and `causeIndirectMagicDamage`. Use `handleStatusUpdate(byte 21)` for laser beam trigger.

---

### D1.2 — EntityCultistPortal Staged Boss

**Q5. `onInitialSpawn(DifficultyInstance, IEntityLivingData)`**
✅ Signature unchanged:
```java
@Nullable
public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
```

**Q6. `setHomePosAndDistance(BlockPos, int)`**
✅ Correct replacement for `setHomeArea(int,int,int,int)`.

**Q7. `world.spawnEntity(Entity)`**
✅ Correct. Returns boolean. Must guard with `!world.isRemote`.

**Q8. Boss NBT for `stage`/`stageCounter`**
✅ Standard `nbt.setInteger("Stage", stage)` / `nbt.getInteger("Stage")`. No special boss-NBT pattern.

**D1.2 Verdict:** Straightforward port. No API surprises. Need `stage`, `stageCounter`, `pulse` fields, `spawnMinions()`, `spawnBoss()`, pierce-damage `attackEntityFrom`, potion immunity.

---

### D1.3 — EntityEldritchGuardian

**Q9. `setThrowableHeading → shoot()`**
✅ `entityThrowable.shoot(x, y, z, velocity, inaccuracy)` — exact replacement.

**Q10. Blindness: `MobEffects.BLINDNESS`**
✅ `new PotionEffect(MobEffects.BLINDNESS, 400, 0)` — correct 1.12.2 idiom.

**Q11. `PotionEffect.getId()` deprecation**
✅ Don't need numeric ID. Pass `Potion` directly to constructor. Use `Potion.getIdFromPotion()` only for legacy NBT.

**Q12. `NetworkRegistry.TargetPoint`**
✅ Still exists at `net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint`. Constructor unchanged.

**D1.3 Verdict:** Straightforward. Fix `EntityEldritchOrb` projectile — it uses `MobEffects.WEAKNESS` but original applies **Wither** (`MobEffects.WITHER`).

---

### D1.4 — EntityCultistCleric

**Q13. `EntitySmallFireball(World, EntityLivingBase, double, double, double)`**
✅ Constructor exists at `net.minecraft.entity.projectile.EntitySmallFireball`.

**Q14. `swingItem() → swingArm(EnumHand.MAIN_HAND)`**
✅ Correct. Vanilla auto-syncs animation to clients, no extra packet needed.

**Q15. `world.playSoundEffect(null, 1009, x, y, z, 0)` → `world.playEvent`**
✅ Use `world.playEvent(null, 1009, new BlockPos(this), 0)`. This preserves the numeric-level-event pattern.

**Q16. `IEntityAdditionalSpawnData` package**
❌ **Wrong package.** It's `net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData`, NOT `net.minecraft.entity`.
✅ For `BlockPos` in ByteBuf: use `buf.writeLong(pos.toLong())` / `BlockPos.fromLong(buf.readLong())`.
No `ByteBufUtils.writeBlockPos` in 1.12.2.

**D1.4 Verdict:** Fix the import package. Implement `attackEntityWithRangedAttack` (66% homing orb, 33% fireball ×3). Add `readSpawnData`/`writeSpawnData` for home position. Add ritualist rotation in `onUpdate`.

---

## D2 — EntityPech NBT Persistence

**Q17. PECH_TYPE byte:** ✅ `DataSerializers.BYTE` exists. Use `DataParameter<Byte>`.

**Q18. ANGER short:** ❌ `DataSerializers.SHORT` does NOT exist in 1.12.2.
Use `DataSerializers.VARINT` with `DataParameter<Integer>`. Cast in getters if needed.

**Q19. TAMED boolean:** ✅ `DataSerializers.BOOLEAN` is correct. No need for byte.

**Q20. `EntityDataManager.createKey`:** ✅ Generic signature is correct.

**Q21. `dataManager.set()` in `readEntityFromNBT`:** ✅ Safe and idiomatic. Keys must be registered in `entityInit()` first. Dirty flag is harmless — the entity isn't yet in tracking state during NBT deserialization.

**Q22. `ItemStack[]` array NBT:** ✅ Standard `NBTTagList` of `NBTTagCompound` still works. For `NonNullList<ItemStack>`, use `ItemStackHelper.saveAllItems/loadAllItems`.

**D2 Verdict:** Fix DataParameter types (PECH_TYPE → `BYTE`, ANGER → `VARINT`, TAMED → `BOOLEAN`). Implement full NBT persistence for all fields. Add `loot[9]` ItemStack array. Add `setPechType()`, `setAnger()`, `getAnger()`, `setTamed()` methods.

---

## D3 — Container canInteractWith + GhostSlots

**Q23. Forge built-in ghost slots:** ❌ No built-in mechanism.
Port `ContainerGhostSlots` manually via `Container#slotClick(...)`:
```java
@Override
public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player) {
    if (slot >= 0 && slot instanceof SlotGhost) {
        // mutate fake slot only
        return ItemStack.EMPTY;
    }
    return super.slotClick(slotId, dragType, clickType, player);
}
```
Note: method is `slotClick`, NOT `containerSlotClick`.

**Q24. `player.getDistanceSq(Entity)`:**
✅ `player.getDistanceSq(entity) <= 64.0D` — correct. Squared distance, 8-block radius.

**Q25. `entity.isDead`:**
✅ Still exists. `entity.isEntityAlive()` is cleaner for living entities but both work.

**Q26. `onContainerClosed(EntityPlayer)`:**
✅ Signature unchanged. Always call `super.onContainerClosed(player)`.

**D3 Verdict:** Create `ContainerGhostSlots` base. Implement `canInteractWith` in all 3 containers. Add `onContainerClosed` for AI resume/close sound/item cleanup.

---

## D4 — InventoryTrunk + InventoryPech

**Q27. `NonNullList<ItemStack>` vs `ItemStack[]`:**
✅ Prefer `NonNullList<ItemStack>` for 1.12.2:
```java
private final NonNullList<ItemStack> stacks = NonNullList.withSize(36, ItemStack.EMPTY);
```
Eliminates null bugs.

**Q28. `ItemStack.EMPTY` vs `null`:**
✅ `getStackInSlot()` returns `ItemStack.EMPTY`. Guard setter with null→EMPTY conversion.

**Q29. `openInventory`/`closeInventory` — automatic?**
❌ Container must call explicitly:
```java
// constructor
this.trunkInv.openInventory(player);

// onContainerClosed
this.trunkInv.closeInventory(player);
```
The inventory methods then manage entity state (`ent.setOpen(true/false)`).

**D4 Verdict:** Rewrite both inventories with `NonNullList`. Fix array sizes: Trunk=36 (slotCount 27-36), Pech=5. Add NBT persistence, `dropAllItems()`, `markDirty()`.

---

## D5 — ItemSpawnerEgg Registration

**Q30. `.egg(c1,c2)` creates item?**
❌ **No.** It records egg data in `EntityEntry` but does NOT register `thaumcraft:spawn_egg` item. Empty `ItemSpawnerEgg.java` can be deleted if you don't need custom egg behavior.

**Q31. Custom egg class:**
✅ In 1.12.2 vanilla class is `ItemMonsterPlacer`, NOT `ItemSpawnEgg`. If custom behavior needed, extend `ItemMonsterPlacer`.

**Q32. Adding `.egg()` for missing 18 entities:**
✅ Sufficient for standard eggs. No extra registration. Check localization names.

**D5 Verdict:** Delete `ItemSpawnerEgg.java` (dead code). Add `.egg()` to remaining EntityEntry registrations.

---

## D6 — Champion Modifiers

**Q33. Mighty `return 0.0F` damage negation:**
✅ Effective for all normal damage going through `LivingHurtEvent`. Doesn't protect against: `/kill`, `DamageSource.OUT_OF_WORLD`, direct `setHealth(0)` bypass.

**Q34. EntityTaintSpider spawn:**
✅ Pattern: `!world.isRemote`, set position, `world.spawnEntity(entity)`. Call `onInitialSpawn(...)` if difficulty-based equipment needed.

**Q35. `showFX` stub until Phase 8:**
✅ Acceptable if no client-only classes are referenced. For `drawGenericParticles` — ensure it dispatches to no-op server proxy safely.

**D6 Verdict:** Fix Mighty to `return 0.0F`. Fix Infested to spawn `EntityTaintSpider` on entity hit (not `!isEntityAlive()`). Stub `showFX`.

---

## D7 — InternalMethodHandler Stubs

**Q36. Rate-limited FX packet:**
✅ Pattern: `SimpleNetworkWrapper.sendToAllAround(msg, new TargetPoint(dim, x, y, z, range))` works in 1.12.2.
Rate-limit with `HashMap<WorldCoordinates, Long>` + cleanup.

**Q37. `PacketFXVisDrain` serialization:**
✅ `IMessage.toBytes(ByteBuf)` / `fromBytes(ByteBuf)`. For BlockPos: `buf.writeLong(pos.toLong())` / `BlockPos.fromLong()`. No `ByteBufUtils` helpers for BlockPos in 1.12.2.

**Q38. `WorldCoordinates` as HashMap key:**
✅ Depends on correct `equals()`/`hashCode()` in Phase 1 port. Verify.

**Q39. `getStackInRowAndColumn → ItemStack.EMPTY`:**
✅ Safe stub. Arcane recipe matching won't work until `TileMagicWorkbench` is ported, but won't crash.

**Q40. `ThaumcraftCraftingManager` state:**
⚠️ **Must be checked in actual source tree.** If `getObjectTags`, `getBonusTags`, `generateTags` are stubs, don't enable recursive generateTags. Safe fallback: return cached tags or empty AspectList.

**D7 Verdict:** Implement `generateVisEffect` with rate-limited packet. Wire `PacketFXVisDrain` with BlockPos serialization. Keep `getStackInRowAndColumn` as EMPTY stub until TileMagicWorkbench is done. Verify CraftingManager state.

---

## D8 — Empty Entity Shells

**Q41. `onCollideWithPlayer(EntityPlayer)`:**
✅ Still exists on `Entity` in 1.12.2.

**Q42. `world.playerEntities`:**
✅ Still `List<EntityPlayer>`. Guard against null/dead/world mismatch.

**Q43. Sound mappings:**
- `"random.fizz"` → `SoundEvents.BLOCK_FIRE_EXTINGUISH`
- `"random.orb"` → `SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP`

**Q44. `Block.getIdFromBlock()` / `Block.getBlockById()`:**
✅ Still exist. For modded blocks, prefer `block.getRegistryName()` for NBT stability.

**Q45. `isReplaceableOreGen` signature (CORRECTION):**
❌ **Not** `(IBlockState, IBlockAccess, BlockPos, BlockState)`.
✅ Correct signature: `isReplaceableOreGen(IBlockState state, IBlockAccess world, BlockPos pos, Predicate<IBlockState> target)`.
```java
state.getBlock().isReplaceableOreGen(state, world, pos, BlockMatcher.forBlock(Blocks.STONE))
```

**Q46. Extend `EntityFallingBlock`?**
⚠️ Discouraged for faithful port. Vanilla `EntityFallingBlock` has its own landing semantics. Custom `Entity` with manual gravity is simpler for taint-block-specific behavior.

**Q47. `MovingObjectPosition → RayTraceResult`:**
✅ `world.rayTraceBlocks(Vec3d, Vec3d)` returns `RayTraceResult`.
BlockPos: `result.getBlockPos()`.

**Q48. `Vec3.createVectorHelper → new Vec3d(x,y,z)`:**
✅ Correct.

**Q49. `Material.water`:**
✅ `net.minecraft.block.material.Material.WATER` exists.

**Q50. `EnumParticleTypes.SPLASH`:**
✅ Server: `((WorldServer)world).spawnParticle(EnumParticleTypes.SPLASH, x, y, z, count, dx, dy, dz, speed)`.
Client: `world.spawnParticle(EnumParticleTypes.SPLASH, x, y, z, 0, 0, 0)`.

**Q51. `EntityGolemBase fisher` cross-dimension:**
✅ Guard: `if (fisher == null || fisher.isDead || fisher.world != this.world) { setDead(); }`.

**D8 Verdict:** Port all three entity shells. EntityAspectOrb — attraction logic + `onCollideWithPlayer`. EntityFallingTaint — manual gravity + taint block placement. EntityGolemBobber — bobber physics + fishing timers.

---

## Cross-Cutting

**Q52. `dataManager.set()` in `readEntityFromNBT`:**
✅ Safe and idiomatic. Keys must be registered in `entityInit()` first.

**Q53. `setEntityState(byte)` / `handleStatusUpdate(byte)`:**
✅ Pattern unchanged in 1.12.2. Good for one-shot entity triggers.

---

## 1.12.2 Reference Table (Corrected)

| 1.7.10 | 1.12.2 | Status |
|--------|--------|--------|
| `dataWatcher.addObject(id, value)` | `dataManager.register(key, defaultValue)` in `entityInit()` | ✅ |
| `causeIndirectDamage(Entity, Entity)` | `causeIndirectDamage(Entity, EntityLivingBase)` — 2nd param narrowed | ✅ Fine |
| `causeMagicDamage(EntityLivingBase)` | ❌ Use `causeIndirectMagicDamage(Entity, @Nullable Entity)` | ❌ **Fix this** |
| `Potion.blindness.getId()` | `MobEffects.BLINDNESS` (pass Potion directly) | ✅ |
| `IEntityAdditionalSpawnData` (entity pkg) | `net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData` | ❌ **Fix package** |
| `DataSerializers.SHORT` | **Does not exist** — use `DataSerializers.VARINT` | ❌ **Fix type** |
| `ItemSpawnerEgg` | `ItemMonsterPlacer` (vanilla) | ⚠️ Different class name |
| `ContainerGhostSlots` | No Forge built-in — port manually via `slotClick` | ✅ Manual port |
| `isReplaceableOreGen(state, world, pos, blockState)` | `isReplaceableOreGen(state, world, pos, Predicate<IBlockState>)` | ❌ **Fix signature** |
| `EntityFallingBlock` | Same class exists | ✅ Can use or skip |
| `ByteBufUtils.writeBlockPos` | **Does not exist** — use `buf.writeLong(pos.toLong())` | ✅ Simple pattern |
