package thaumcraft.common.entities.ai.combat;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import thaumcraft.common.entities.golems.EntityGolemBase;

public class AINearestButcherTarget extends EntityAITarget {
    private final EntityGolemBase golem;
    private final AIOldestAttackableTargetSorter sorter;
    private EntityLivingBase target;

    public AINearestButcherTarget(EntityGolemBase golem) {
        super(golem, true);
        this.golem = golem;
        this.sorter = new AIOldestAttackableTargetSorter(golem);
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        double range = this.golem.getRange();
        List<EntityLivingBase> targets = this.taskOwner.world.getEntitiesWithinAABB(EntityLivingBase.class,
                this.taskOwner.getEntityBoundingBox().grow(range, 4.0D, range));
        Collections.sort(targets, this.sorter);
        for (Entity entity : targets) {
            if (!this.golem.isValidTarget(entity)) continue;
            EntityLivingBase candidate = (EntityLivingBase)entity;
            this.target = candidate;
            int count = 0;
            List<? extends EntityLivingBase> sameTypeTargets = this.taskOwner.world.getEntitiesWithinAABB(
                    candidate.getClass(), this.taskOwner.getEntityBoundingBox().grow(range, 4.0D, range));
            for (Entity sameTypeTarget : sameTypeTargets) {
                if (this.golem.isValidTarget(sameTypeTarget)) ++count;
            }
            if (count > 2) return true;
        }
        return false;
    }

    @Override
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.target);
        super.startExecuting();
    }
}
