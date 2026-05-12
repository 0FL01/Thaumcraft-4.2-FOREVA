package thaumcraft.common.entities.ai.pech;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathPoint;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.monster.EntityPech;

import java.util.List;

public class AIPechItemEntityGoto extends EntityAIBase {

    private EntityPech pech;
    private EntityItem targetEntity;
    float maxTargetDistance = 16.0f;
    private int count;
    private int failedPathFindingPenalty;

    public AIPechItemEntityGoto(EntityPech pech) {
        this.pech = pech;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (this.pech.ticksExisted % Config.golemDelay > 0) return false;
        if (--this.count > 0) return false;

        double range = Double.MAX_VALUE;
        List<EntityItem> targets = this.pech.world.getEntitiesWithinAABB(EntityItem.class,
            this.pech.getEntityBoundingBox().grow(this.maxTargetDistance, this.maxTargetDistance, this.maxTargetDistance));
        if (targets.isEmpty()) return false;

        for (EntityItem e : targets) {
            if (e.isDead) continue;
            ItemStack stack = e.getItem();
            if (!this.pech.canPickup(stack)) continue;
            String username = e.getOwner();
            if (username != null && username.equals("PechDrop")) continue;
            double distance = e.getDistanceSq(this.pech.posX, this.pech.posY, this.pech.posZ);
            if (distance < range && distance <= this.maxTargetDistance * this.maxTargetDistance) {
                range = distance;
                this.targetEntity = e;
            }
        }
        return this.targetEntity != null;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.targetEntity != null
            && this.targetEntity.isEntityAlive()
            && !this.pech.getNavigator().noPath()
            && this.targetEntity.getDistanceSq(this.pech) < this.maxTargetDistance * this.maxTargetDistance;
    }

    @Override
    public void resetTask() {
        this.targetEntity = null;
    }

    @Override
    public void startExecuting() {
        this.pech.getNavigator().setPath(
            this.pech.getNavigator().getPathToEntityLiving(this.targetEntity),
            this.pech.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() * 1.5);
        this.count = 0;
    }

    @Override
    public void updateTask() {
        if (this.targetEntity == null) return;
        this.pech.getLookHelper().setLookPositionWithEntity(this.targetEntity, 30.0f, 30.0f);

        if (this.pech.getEntitySenses().canSee(this.targetEntity) && --this.count <= 0) {
            this.count = this.failedPathFindingPenalty + 4 + this.pech.getRNG().nextInt(4);
            this.pech.getNavigator().tryMoveToEntityLiving(this.targetEntity,
                this.pech.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() * 1.5);
            PathPoint finalPathPoint = this.pech.getNavigator().getPath() != null
                ? this.pech.getNavigator().getPath().getFinalPathPoint() : null;
            if (finalPathPoint != null
                && this.targetEntity.getDistanceSq(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1.0) {
                this.failedPathFindingPenalty = 0;
            } else {
                this.failedPathFindingPenalty += 10;
            }
        }

        double distance = this.pech.getDistanceSq(
            this.targetEntity.posX,
            this.targetEntity.getEntityBoundingBox().minY,
            this.targetEntity.posZ);
        if (distance <= 1.5) {
            this.count = 0;
            int am = this.targetEntity.getItem().getCount();
            ItemStack is = this.pech.pickupItem(this.targetEntity.getItem());
            if (is != null && !is.isEmpty() && is.getCount() > 0) {
                this.targetEntity.setItem(is);
            } else {
                this.targetEntity.setDead();
            }
            if (is == null || is.isEmpty() || is.getCount() != am) {
                this.targetEntity.world.playSound(null, this.targetEntity.getPosition(),
                    net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("random.pop")),
                    net.minecraft.util.SoundCategory.NEUTRAL, 0.2f,
                    ((this.targetEntity.world.rand.nextFloat() - this.targetEntity.world.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
            }
        }
    }
}
