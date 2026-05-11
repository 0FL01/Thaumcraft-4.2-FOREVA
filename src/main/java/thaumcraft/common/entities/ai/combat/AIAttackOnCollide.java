package thaumcraft.common.entities.ai.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;

public class AIAttackOnCollide extends net.minecraft.entity.ai.EntityAIBase {

    private EntityLiving attacker;

    public AIAttackOnCollide(EntityLiving attacker) {
        this.attacker = attacker;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.shouldExecute();
    }

    @Override
    public void startExecuting() {
    }

    @Override
    public void resetTask() {
    }

    @Override
    public void updateTask() {
    }
}
