package thaumcraft.common.entities.ai.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;
import thaumcraft.common.entities.monster.EntityCultistCleric;

public class AIAltarFocus extends net.minecraft.entity.ai.EntityAIBase {

    private EntityCultistCleric cleric;

    public AIAltarFocus(EntityCultistCleric cleric) {
        this.cleric = cleric;
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
