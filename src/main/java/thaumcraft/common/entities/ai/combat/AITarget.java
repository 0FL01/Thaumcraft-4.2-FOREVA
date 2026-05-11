package thaumcraft.common.entities.ai.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;
import thaumcraft.common.entities.golems.EntityGolemBase;

public class AITarget extends net.minecraft.entity.ai.EntityAIBase {

    private EntityGolemBase golem;
    private boolean checkSight;

    public AITarget(EntityGolemBase golem, boolean checkSight) {
        this.golem = golem;
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
