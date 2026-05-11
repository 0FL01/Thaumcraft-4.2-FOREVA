package thaumcraft.common.entities.ai.pech;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;
import thaumcraft.common.entities.monster.EntityPech;

public class AIPechTradePlayer extends net.minecraft.entity.ai.EntityAIBase {

    private EntityPech pech;

    public AIPechTradePlayer(EntityPech pech) {
        this.pech = pech;
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
