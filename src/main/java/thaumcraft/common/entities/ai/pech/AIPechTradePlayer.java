package thaumcraft.common.entities.ai.pech;

import net.minecraft.entity.ai.EntityAIBase;
import thaumcraft.common.entities.monster.EntityPech;

public class AIPechTradePlayer extends EntityAIBase {

    private EntityPech pech;

    public AIPechTradePlayer(EntityPech pech) {
        this.pech = pech;
        this.setMutexBits(5);
    }

    @Override
    public boolean shouldExecute() {
        if (!this.pech.isEntityAlive()) return false;
        if (this.pech.isInWater()) return false;
        if (this.pech.isTamed()) return false;
        if (!this.pech.onGround) return false;
        if (this.pech.isRiding()) return false;
        return this.pech.trading;
    }

    @Override
    public void startExecuting() {
        this.pech.getNavigator().clearPath();
    }

    @Override
    public void resetTask() {
        this.pech.trading = false;
    }
}
