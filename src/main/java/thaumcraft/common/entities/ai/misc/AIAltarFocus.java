package thaumcraft.common.entities.ai.misc;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.monster.EntityCultistCleric;

public class AIAltarFocus extends EntityAIBase {

    private EntityCultistCleric entity;
    private World world;

    public AIAltarFocus(EntityCultistCleric cleric) {
        this.entity = cleric;
        this.world = cleric.world;
        this.setMutexBits(7);
    }

    @Override
    public boolean shouldExecute() {
        return this.entity.getIsRitualist() && this.entity.getHomePosition() != null;
    }

    @Override
    public void startExecuting() {
    }

    @Override
    public void resetTask() {
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.entity.getIsRitualist() && this.entity.getHomePosition() != null;
    }

    @Override
    public void updateTask() {
        if (this.entity.getHomePosition() != null
            && this.entity.ticksExisted % 40 == 0) {
            BlockPos home = this.entity.getHomePosition();
            float dist = (float) this.entity.getDistanceSq(home.getX(), home.getY(), home.getZ());
            if (dist > 256.0f
                || this.world.getBlockState(new BlockPos(home.getX(), home.getY(), home.getZ())).getBlock()
                    != ConfigBlocks.blockEldritch) {
                this.entity.setIsRitualist(false);
            }
        }
    }
}
