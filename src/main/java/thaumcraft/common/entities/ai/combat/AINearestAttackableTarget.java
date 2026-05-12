package thaumcraft.common.entities.ai.combat;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import thaumcraft.common.entities.golems.EntityGolemBase;

public class AINearestAttackableTarget extends EntityAITarget {

    EntityGolemBase theGolem;
    EntityLivingBase target;
    int targetChance;
    private float targetDistance;
    private AINearestAttackableTargetSorter theNearestAttackableTargetSorter;

    public AINearestAttackableTarget(EntityGolemBase golem, int chance, boolean checkSight) {
        this(golem, 0.0F, chance, checkSight, false);
    }

    public AINearestAttackableTarget(EntityGolemBase golem, float distance, int chance, boolean checkSight, boolean par6) {
        super(golem, checkSight, par6);
        this.theGolem = golem;
        this.targetDistance = 0.0F;
        this.targetChance = chance;
        this.theNearestAttackableTargetSorter = new AINearestAttackableTargetSorter(golem);
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        this.targetDistance = this.theGolem.getRange();
        if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0) {
            return false;
        }
        double dist = (double)this.targetDistance;
        List<EntityLivingBase> list = this.taskOwner.world.getEntitiesWithinAABB(EntityLivingBase.class,
            this.taskOwner.getEntityBoundingBox().grow(dist, 4.0D, dist));
        Collections.sort(list, this.theNearestAttackableTargetSorter);
        for (Entity entity : list) {
            EntityLivingBase candidate = (EntityLivingBase)entity;
            if (!this.theGolem.isValidTarget(candidate)) continue;
            this.target = candidate;
            return true;
        }
        return false;
    }

    @Override
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.target);
        super.startExecuting();
    }
}
