package thaumcraft.common.entities.ai.combat;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIAttackRanged;

public class AILongRangeAttack extends EntityAIAttackRanged {

    private final EntityLiving wielder;
    private double minDistance;

    public AILongRangeAttack(IRangedAttackMob mob, double min, double speed, int maxAttackTime, int attackCooldown, float maxRange) {
        super(mob, speed, maxAttackTime, attackCooldown, maxRange);
        this.minDistance = min;
        this.wielder = (EntityLiving)mob;
    }

    @Override
    public boolean shouldExecute() {
        boolean ex = super.shouldExecute();
        if (ex) {
            EntityLivingBase target = this.wielder.getAttackTarget();
            if (target == null) return false;
            if (target.isDead) {
                this.wielder.setAttackTarget(null);
                return false;
            }
            double distSq = this.wielder.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
            if (distSq < this.minDistance * this.minDistance) return false;
        }
        return ex;
    }
}
