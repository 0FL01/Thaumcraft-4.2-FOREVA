package thaumcraft.common.entities.ai.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.entities.golems.EntityGolemBase;

public abstract class AITarget extends EntityAIBase {

    protected EntityCreature taskOwner;
    protected float targetDistance;
    protected boolean shouldCheckSight;
    private boolean field_75303_a;
    private int field_75301_b;
    private int field_75302_c;
    private int field_75298_g;

    public AITarget(EntityCreature taskOwner, float targetDistance, boolean shouldCheckSight) {
        this(taskOwner, targetDistance, shouldCheckSight, false);
    }

    public AITarget(EntityCreature taskOwner, float targetDistance, boolean shouldCheckSight, boolean field_75303_a) {
        this.taskOwner = taskOwner;
        this.targetDistance = targetDistance;
        this.shouldCheckSight = shouldCheckSight;
        this.field_75303_a = field_75303_a;
    }

    @Override
    public boolean shouldContinueExecuting() {
        EntityLivingBase target = this.taskOwner.getAttackTarget();
        if (target == null) return false;
        if (!target.isEntityAlive()) return false;
        if (this.taskOwner.getDistanceSq(target) > (double)(this.targetDistance * this.targetDistance)) return false;
        if (this.shouldCheckSight) {
            if (this.taskOwner.getEntitySenses().canSee(target)) {
                this.field_75298_g = 0;
            } else if (++this.field_75298_g > 60) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void startExecuting() {
        this.field_75301_b = 0;
        this.field_75302_c = 0;
        this.field_75298_g = 0;
    }

    @Override
    public void resetTask() {
        this.taskOwner.setAttackTarget((EntityLivingBase)null);
    }

    protected boolean isSuitableTarget(EntityLivingBase target, boolean par2) {
        if (target == null) return false;
        if (target == this.taskOwner) return false;
        if (!target.isEntityAlive()) return false;
        if (this.taskOwner instanceof EntityTameable) {
            EntityTameable tameable = (EntityTameable)this.taskOwner;
            if (tameable.isTamed()) {
                if (target instanceof EntityTameable && ((EntityTameable)target).isTamed()) return false;
                if (target == tameable.getOwner()) return false;
            }
        } else {
            if (target instanceof EntityPlayer && !par2 && ((EntityPlayer)target).capabilities.disableDamage) return false;
            if (target instanceof EntityPlayer && this.taskOwner instanceof EntityGolemBase && ((EntityPlayer)target).getName().equals(((EntityGolemBase)this.taskOwner).getOwnerName())) return false;
        }
        if (!this.taskOwner.isWithinHomeDistanceFromPosition(new BlockPos(MathHelper.floor(target.posX), MathHelper.floor(target.posY), MathHelper.floor(target.posZ)))) return false;
        if (this.shouldCheckSight && !this.taskOwner.getEntitySenses().canSee(target)) return false;
        if (this.field_75303_a) {
            if (--this.field_75302_c <= 0) this.field_75301_b = 0;
            if (this.field_75301_b == 0) {
                this.field_75301_b = this.func_75295_a(target) ? 1 : 2;
            }
            if (this.field_75301_b == 2) return false;
        }
        return true;
    }

    private boolean func_75295_a(EntityLivingBase target) {
        this.field_75302_c = 10 + this.taskOwner.getRNG().nextInt(5);
        Path path = this.taskOwner.getNavigator().getPathToEntityLiving(target);
        if (path == null) return false;
        PathPoint finalPoint = path.getFinalPathPoint();
        if (finalPoint == null) return false;
        int dx = finalPoint.x - MathHelper.floor(target.posX);
        int dz = finalPoint.z - MathHelper.floor(target.posZ);
        return (double)(dx * dx + dz * dz) <= 2.25D;
    }
}
