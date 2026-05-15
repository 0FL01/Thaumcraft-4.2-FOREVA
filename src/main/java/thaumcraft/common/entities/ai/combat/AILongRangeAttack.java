package thaumcraft.common.entities.ai.combat;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIAttackRanged;

public class AILongRangeAttack extends EntityAIAttackRanged {

    private final EntityLiving wielder;
    private final float maxRange;
    private final double minDistance;

    public AILongRangeAttack(IRangedAttackMob mob, double min, double speed, int maxAttackTime, int attackCooldown, float maxRange) {
        super(mob, speed, maxAttackTime, attackCooldown, maxRange);
        this.minDistance = min;
        this.maxRange = maxRange;
        this.wielder = (EntityLiving)mob;
    }

    /**
     * Range: [minDistance, maxRange]. Only start attacking when the target is
     * neither too close (within minimum distance) nor too far (beyond maximum range).
     */
    @Override
    public boolean shouldExecute() {
        if (!super.shouldExecute()) return false;

        EntityLivingBase target = this.wielder.getAttackTarget();
        if (target == null) return false;
        if (target.isDead) {
            this.wielder.setAttackTarget(null);
            return false;
        }
        double distSq = this.wielder.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);

        // min range check: don't attack if target is too close
        if (distSq < this.minDistance * this.minDistance) return false;

        // max range check: don't attack if target is out of range
        float max = this.maxRange;
        if (distSq > (double)(max * max)) return false;

        return true;
    }
}
