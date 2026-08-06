package thaumcraft.common.entities.ai.combat;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.MathHelper;

public class AILongRangeAttack extends EntityAIBase {

    private final EntityLiving wielder;
    private final IRangedAttackMob rangedWielder;
    private final double minDistance;
    private final double moveSpeed;
    private final int minAttackTime;
    private final int maxAttackTime;
    private final float maxRange;
    private final float maxRangeSquared;
    private EntityLivingBase target;
    private int attackTime = -1;
    private int seeTime;

    public AILongRangeAttack(IRangedAttackMob mob, double min, double speed,
            int minAttackTime, int maxAttackTime, float maxRange) {
        if (!(mob instanceof EntityLiving)) {
            throw new IllegalArgumentException("LongRangeAttackGoal requires an EntityLiving mob");
        }
        this.rangedWielder = mob;
        this.wielder = (EntityLiving) mob;
        this.minDistance = min;
        this.moveSpeed = speed;
        this.minAttackTime = minAttackTime;
        this.maxAttackTime = maxAttackTime;
        this.maxRange = maxRange;
        this.maxRangeSquared = maxRange * maxRange;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase attackTarget = this.wielder.getAttackTarget();
        if (attackTarget == null) {
            return false;
        }
        this.target = attackTarget;
        if (attackTarget.isDead) {
            this.wielder.setAttackTarget(null);
            return false;
        }
        double distanceSquared = this.wielder.getDistanceSq(
                attackTarget.posX, attackTarget.getEntityBoundingBox().minY, attackTarget.posZ);
        return distanceSquared >= this.minDistance * this.minDistance;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.shouldExecute() || !this.wielder.getNavigator().noPath();
    }

    @Override
    public void resetTask() {
        this.target = null;
        this.seeTime = 0;
        this.attackTime = -1;
    }

    @Override
    public void updateTask() {
        double distanceSquared = this.wielder.getDistanceSq(
                this.target.posX, this.target.getEntityBoundingBox().minY, this.target.posZ);
        boolean canSee = this.wielder.getEntitySenses().canSee(this.target);
        if (canSee) {
            ++this.seeTime;
        } else {
            this.seeTime = 0;
        }

        if (distanceSquared <= this.maxRangeSquared && this.seeTime >= 20) {
            this.wielder.getNavigator().clearPath();
        } else {
            this.wielder.getNavigator().tryMoveToEntityLiving(this.target, this.moveSpeed);
        }
        this.wielder.getLookHelper().setLookPositionWithEntity(this.target, 30.0F, 30.0F);

        if (--this.attackTime == 0) {
            if (distanceSquared > this.maxRangeSquared || !canSee) {
                return;
            }
            float distanceRatio = MathHelper.sqrt(distanceSquared) / this.maxRange;
            this.rangedWielder.attackEntityWithRangedAttack(
                    this.target, MathHelper.clamp(distanceRatio, 0.1F, 1.0F));
            this.attackTime = MathHelper.floor(distanceRatio
                    * (this.maxAttackTime - this.minAttackTime) + this.minAttackTime);
        } else if (this.attackTime < 0) {
            float distanceRatio = MathHelper.sqrt(distanceSquared) / this.maxRange;
            this.attackTime = MathHelper.floor(distanceRatio
                    * (this.maxAttackTime - this.minAttackTime) + this.minAttackTime);
        }
    }
}
