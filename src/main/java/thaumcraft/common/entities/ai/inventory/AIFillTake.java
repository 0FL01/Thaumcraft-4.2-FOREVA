package thaumcraft.common.entities.ai.inventory;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;
import thaumcraft.common.entities.golems.EntityGolemBase;

public class AIFillTake extends net.minecraft.entity.ai.EntityAIBase {

    private EntityGolemBase golem;

    public AIFillTake(EntityGolemBase golem) {
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
