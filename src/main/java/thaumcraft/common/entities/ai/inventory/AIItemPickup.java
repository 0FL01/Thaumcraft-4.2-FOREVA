package thaumcraft.common.entities.ai.inventory;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.lib.utils.InventoryUtils;

public class AIItemPickup extends EntityAIBase {
    private static final Field PICKUP_DELAY_FIELD = findPickupDelayField();

    private EntityGolemBase theGolem;
    private Entity targetEntity;
    int count = 0;

    public AIItemPickup(EntityGolemBase golem) {
        this.theGolem = golem;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (this.theGolem.ticksExisted % Config.golemDelay > 0) return false;
        return this.findItem();
    }

    private boolean findItem() {
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
        return this.count-- > 0 && !this.theGolem.getNavigator().noPath() && this.targetEntity.isEntityAlive();
    }

    @Override
    public void resetTask() {
        this.count = 0;
        this.targetEntity = null;
        this.theGolem.getNavigator().clearPath();
    }

    @Override
    public void updateTask() {
        this.theGolem.getLookHelper().setLookPositionWithEntity(this.targetEntity, 30.0F, 30.0F);
        double dist = this.theGolem.getDistanceSq(this.targetEntity);
        if (dist <= 2.0) {
            this.pickUp();
        }
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
        this.theGolem.getNavigator().tryMoveToEntityLiving(this.targetEntity, this.theGolem.getAIMoveSpeed());
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
