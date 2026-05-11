package thaumcraft.common.entities.ai.misc;

import net.minecraft.entity.ai.EntityAIBase;

public class AIWander extends EntityAIBase {
    public AIWander() {
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() { return false; }
}
