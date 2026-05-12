package thaumcraft.common.entities.ai.combat;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import thaumcraft.common.entities.golems.EntityGolemBase;

public class AIDartAttack extends EntityAIBase {

    private final EntityGolemBase theGolem;
    private EntityLivingBase attackTarget;
    private int rangedAttackTime;
    private int maxRangedAttackTime;

    public AIDartAttack(EntityGolemBase golem) {
        this.theGolem = golem;
        this.maxRangedAttackTime = 30 - this.theGolem.getUpgradeAmount(0) * 8;
        this.rangedAttackTime = this.maxRangedAttackTime / 2;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase target = this.theGolem.getAttackTarget();
        if (target == null) return false;
        if (!this.theGolem.isValidTarget(target)) {
            this.theGolem.setAttackTarget(null);
            return false;
        }
        double distSq = this.theGolem.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
        if (distSq < 9.0D) return false;
        this.attackTarget = target;
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        EntityLivingBase target = this.theGolem.getAttackTarget();
        if (target == null) return false;
        if (!this.theGolem.isValidTarget(target)) {
            this.theGolem.setAttackTarget(null);
            return false;
        }
        return !this.theGolem.getNavigator().noPath();
    }

    @Override
    public void resetTask() {
        this.attackTarget = null;
        this.rangedAttackTime = this.maxRangedAttackTime / 2;
    }

    @Override
    public void updateTask() {
        double distSq = this.theGolem.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY, this.attackTarget.posZ);
        boolean canSee = this.theGolem.getEntitySenses().canSee(this.attackTarget);
        this.theGolem.getNavigator().tryMoveToEntityLiving(this.attackTarget, (double)this.theGolem.getAIMoveSpeed());
        if (canSee) {
            this.theGolem.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
            this.rangedAttackTime = Math.max(this.rangedAttackTime - 1, 0);
            if (this.rangedAttackTime <= 0) {
                float range = this.theGolem.getRange() * 0.8F;
                if (distSq <= (double)(range * range) && canSee) {
                    this.theGolem.attackEntityWithRangedAttack(this.attackTarget);
                    this.rangedAttackTime = this.maxRangedAttackTime;
                }
            }
        }
    }
}
