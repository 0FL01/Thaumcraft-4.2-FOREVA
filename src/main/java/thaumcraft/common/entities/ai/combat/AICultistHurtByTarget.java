package thaumcraft.common.entities.ai.combat;

import java.util.List;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.common.entities.monster.EntityCultist;
import thaumcraft.common.entities.monster.EntityCultistCleric;

public class AICultistHurtByTarget extends EntityAITarget {

    boolean entityCallsForHelp;
    private int revengeTimerOld;

    public AICultistHurtByTarget(EntityCreature creature, boolean callsForHelp) {
        super(creature, false);
        this.entityCallsForHelp = callsForHelp;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        int i = this.taskOwner.getRevengeTimer();
        return i != this.revengeTimerOld && this.isSuitableTarget(this.taskOwner.getRevengeTarget(), false);
    }

    @Override
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.taskOwner.getRevengeTarget());
        this.revengeTimerOld = this.taskOwner.getRevengeTimer();
        if (this.entityCallsForHelp) {
            double range = this.getTargetDistance();
            AxisAlignedBB aabb = new AxisAlignedBB(
                this.taskOwner.posX, this.taskOwner.posY, this.taskOwner.posZ,
                this.taskOwner.posX + 1.0D, this.taskOwner.posY + 1.0D, this.taskOwner.posZ + 1.0D
            ).grow(range, 10.0D, range);
            List<EntityCreature> list = this.taskOwner.world.getEntitiesWithinAABB(EntityCultist.class, aabb);
            for (EntityCreature ally : list) {
                if (this.taskOwner == ally || ally.getAttackTarget() != null
                    || ally.isOnSameTeam(this.taskOwner.getRevengeTarget())) continue;
                if (ally instanceof EntityCultistCleric && ((EntityCultistCleric)ally).getIsRitualist()) {
                    if (this.taskOwner.world.rand.nextInt(3) != 0) continue;
                    ((EntityCultistCleric)ally).setIsRitualist(false);
                    ally.setAttackTarget(this.taskOwner.getRevengeTarget());
                    continue;
                }
                ally.setAttackTarget(this.taskOwner.getRevengeTarget());
            }
        }
        super.startExecuting();
    }
}
