package thaumcraft.common.entities.ai.inventory;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.lib.utils.InventoryUtils;

public class AIItemPickup extends EntityAIBase {
    private static final Field PICKUP_DELAY_FIELD = findPickupDelayField();

    private EntityGolemBase theGolem;
    private Entity targetEntity;
    int count = 0;
    private int surfaceTicks = 0;

    public AIItemPickup(EntityGolemBase golem) {
        this.theGolem = golem;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (this.theGolem.ticksExisted % Config.golemDelay > 0) return false;
        return this.findItem();
    }

    // --- Helpers ---

    private static boolean isEntityInWater(Entity entity) {
        if (entity == null) return false;
        BlockPos eyePos = new BlockPos(
            MathHelper.floor(entity.posX),
            MathHelper.floor(entity.posY + entity.getEyeHeight()),
            MathHelper.floor(entity.posZ));
        return entity.world.getBlockState(eyePos).getMaterial() == Material.WATER;
    }

    private boolean isWaterPickupTarget() {
        return this.targetEntity != null
            && isEntityInWater(this.targetEntity)
            && theGolem.isLightGolem();
    }

    // --- Item finding ---

    private boolean findItem() {
        this.targetEntity = null;
        double range = Double.MAX_VALUE;
        float dmod = theGolem.getRange();
        BlockPos home = theGolem.getHomePosition();
        AxisAlignedBB aabb = new AxisAlignedBB(home.getX(), home.getY(), home.getZ(),
            home.getX() + 1, home.getY() + 1, home.getZ() + 1).grow(dmod, dmod, dmod);
        List<Entity> targets = theGolem.world.getEntitiesWithinAABB(Entity.class, aabb);
        if (targets.isEmpty()) return false;

        for (Entity e : targets) {
            if (!(e instanceof EntityItem)) continue;
            EntityItem ei = (EntityItem) e;
            if (ei.getItem().isEmpty()) continue;
            if (hasPickupDelay(ei)) continue;
            if (!theGolem.inventory.allEmpty() && theGolem.inventory.getAmountNeededSmart(ei.getItem(),
                theGolem.checkOreDict()) <= 0) continue;
            if (theGolem.getCarried() != null && !theGolem.getCarried().isEmpty()
                && (!InventoryUtils.areItemStacksEqualStrict(theGolem.getCarried(), ei.getItem())
                || ei.getItem().getCount() > theGolem.getCarrySpace())) continue;

            // Skip underwater items if golem is heavy (sinks, can't path through water)
            if (isEntityInWater(e) && !theGolem.isLightGolem()) continue;

            // For dry targets and light golems: verify a path exists
            if (!isEntityInWater(e) && theGolem.isLightGolem()) {
                Path path = theGolem.getNavigator().getPathToXYZ(e.posX, e.posY, e.posZ);
                if (path == null || path.getCurrentPathLength() == 0) continue;
            }

            double distToHome = e.getDistanceSq(home.getX() + 0.5, home.getY() + 0.5, home.getZ() + 0.5);
            double distToGolem = e.getDistanceSq(theGolem.posX, theGolem.posY, theGolem.posZ);
            if (distToGolem < range && distToHome <= dmod * dmod) {
                range = distToGolem;
                this.targetEntity = e;
            }
        }
        return this.targetEntity != null;
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (this.count-- <= 0 || !this.targetEntity.isEntityAlive()) {
            // Item was picked up or expired — keep task alive while surfacing
            if (this.surfaceTicks > 0) return true;
            return false;
        }
        // For underwater targets: allow continuation even when navigator path ends,
        // because the final dive uses manual motion
        if (isWaterPickupTarget()) return true;
        return !this.theGolem.getNavigator().noPath();
    }

    @Override
    public void resetTask() {
        this.count = 0;
        this.targetEntity = null;
        this.surfaceTicks = 0;
        this.theGolem.getNavigator().clearPath();
    }

    @Override
    public void updateTask() {
        double dist = this.theGolem.getDistanceSq(this.targetEntity);

        // --- Surface mode after underwater pickup ---
        if (this.surfaceTicks > 0) {
            this.theGolem.motionY += 0.06D;
            if (this.theGolem.motionY > 0.5D) this.theGolem.motionY = 0.5D;
            this.theGolem.velocityChanged = true;
            this.surfaceTicks--;
            return;
        }

        if (dist <= 2.0) {
            this.pickUp();
            // After picking up an underwater item, start surfacing boost
            if (this.theGolem.isInWater() && this.theGolem.isLightGolem()) {
                this.surfaceTicks = 30; // ~1.5 seconds of upward thrust
            }
            return;
        }

        // Dive mode for underwater items
        if (isWaterPickupTarget()) {
            double dx = targetEntity.posX - theGolem.posX;
            double dz = targetEntity.posZ - theGolem.posZ;
            double dy = targetEntity.posY - theGolem.posY;
            double horizontalSq = dx * dx + dz * dz;

            theGolem.getLookHelper().setLookPosition(
                targetEntity.posX, targetEntity.posY + 0.25D, targetEntity.posZ,
                30.0F, 30.0F);

            if (horizontalSq <= 2.25D && theGolem.isInWater()) {
                // Don't clear the path — the pathfinder already has nodes
                // at the target's Y and handles the vertical swim. Only
                // apply light horizontal correction to stay on target.

                // Small horizontal correction toward item
                theGolem.motionX += MathHelper.clamp(dx * 0.02D, -0.04D, 0.04D);
                theGolem.motionZ += MathHelper.clamp(dz * 0.02D, -0.04D, 0.04D);

                theGolem.velocityChanged = true;
                return;
            }
        }

        // Normal update for dry targets
        this.theGolem.getLookHelper().setLookPositionWithEntity(this.targetEntity, 30.0F, 30.0F);
    }

    private void pickUp() {
        int amount = 0;
        if (this.targetEntity instanceof EntityItem) {
            EntityItem ei = (EntityItem) this.targetEntity;
            ItemStack stack = ei.getItem().copy();
            amount = Math.min(ei.getItem().getCount(), theGolem.getCarrySpace());
            stack.setCount(amount);
            ei.getItem().shrink(amount);
            if (ei.getItem().getCount() <= 0) {
                ei.setDead();
            } else {
                ei.setItem(ei.getItem());
            }
            if (theGolem.getCarried() == null || theGolem.getCarried().isEmpty()) {
                theGolem.setCarried(stack);
            } else {
                theGolem.getCarried().grow(amount);
                theGolem.updateCarried();
            }
        }
        if (amount == 0) return;
        theGolem.world.playSound(null, theGolem.getPosition(),
            net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("random.pop")),
            net.minecraft.util.SoundCategory.NEUTRAL, 0.2F,
            ((theGolem.world.rand.nextFloat() - theGolem.world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
    }

    @Override
    public void startExecuting() {
        this.count = 200;
        if (isWaterPickupTarget()) {
            // For underwater items: path directly to the item's XYZ so the pathfinder
            // creates nodes at the correct depth
            this.theGolem.getNavigator().tryMoveToXYZ(
                this.targetEntity.posX,
                this.targetEntity.posY,
                this.targetEntity.posZ,
                this.theGolem.getAIMoveSpeed());
        } else {
            this.theGolem.getNavigator().tryMoveToEntityLiving(
                this.targetEntity, this.theGolem.getAIMoveSpeed());
        }
    }

    private static boolean hasPickupDelay(EntityItem item) {
        if (PICKUP_DELAY_FIELD != null) {
            try {
                return PICKUP_DELAY_FIELD.getInt(item) >= 5;
            } catch (IllegalAccessException ignored) {
                // Fall through to the public 1.12 API if the private field cannot be read.
            }
        }
        return item.cannotPickup();
    }

    private static Field findPickupDelayField() {
        try {
            return ReflectionHelper.findField(EntityItem.class, "pickupDelay", "field_145804_b");
        } catch (RuntimeException ignored) {
            return null;
        }
    }
}
