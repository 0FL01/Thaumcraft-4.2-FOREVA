package thaumcraft.common.entities.ai.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;
import thaumcraft.common.entities.monster.EntityTaintCreeper;

public class AICreeperSwell extends net.minecraft.entity.ai.EntityAIBase {

    private EntityTaintCreeper creeper;

    public AICreeperSwell(EntityTaintCreeper creeper) {
        this.creeper = creeper;
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
