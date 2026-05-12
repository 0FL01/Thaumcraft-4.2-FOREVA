package thaumcraft.common.entities.ai.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import thaumcraft.common.entities.golems.EntityGolemBase;

public class AIGolemAttackOnCollide extends EntityAIBase {

    EntityGolemBase theGolem;
    EntityLivingBase entityTarget;
    int attackTick;
    Path entityPathEntity;
    private int counter;

    public AIGolemAttackOnCollide(EntityGolemBase golem) {
        this.theGolem = golem;
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
        this.entityTarget = target;
        this.entityPathEntity = this.theGolem.getNavigator().getPathToEntityLiving(this.entityTarget);
        return this.entityPathEntity != null;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.shouldExecute() && !this.theGolem.getNavigator().noPath();
    }

    @Override
    public void startExecuting() {
        this.theGolem.getNavigator().tryMoveToEntityLiving(this.entityTarget, (double)this.theGolem.getAIMoveSpeed());
        this.counter = 0;
    }

    @Override
    public void resetTask() {
        this.entityTarget = null;
        this.theGolem.getNavigator().clearPath();
    }

    @Override
    public void updateTask() {
        this.theGolem.getLookHelper().setLookPositionWithEntity(this.entityTarget, 30.0F, 30.0F);
        if (this.theGolem.getEntitySenses().canSee(this.entityTarget) && --this.counter <= 0) {
            this.counter = 4 + this.theGolem.getRNG().nextInt(7);
            this.theGolem.getNavigator().tryMoveToEntityLiving(this.entityTarget, (double)this.theGolem.getAIMoveSpeed());
        }
        this.attackTick = Math.max(this.attackTick - 1, 0);
        double attackRange = (double)(this.entityTarget.width * 2.0F * this.entityTarget.width * 2.0F) + 1.0D;
        if (this.theGolem.getDistanceSq(this.entityTarget.posX, this.entityTarget.getEntityBoundingBox().minY, this.entityTarget.posZ) <= attackRange && this.attackTick <= 0) {
            this.attackTick = this.theGolem.getAttackSpeed();
            if (!this.theGolem.getHeldItemMainhand().isEmpty()) {
                this.theGolem.swingArm(net.minecraft.util.EnumHand.MAIN_HAND);
            } else {
                this.theGolem.startActionTimer();
            }
            this.theGolem.attackEntityAsMob(this.entityTarget);
        }
    }
}
