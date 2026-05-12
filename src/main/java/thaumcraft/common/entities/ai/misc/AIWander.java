package thaumcraft.common.entities.ai.misc;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;

public class AIWander extends EntityAIBase {

    private final EntityCreature entity;
    private double xPosition;
    private double yPosition;
    private double zPosition;
    private final double speed;
    private int executionChance = 120;
    private boolean mustUpdate;

    public AIWander(EntityCreature creature, double speed) {
        this.entity = creature;
        this.speed = speed;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (!this.mustUpdate) {
            if (this.entity.getIdleTime() >= 100) return false;
            if (this.entity.getRNG().nextInt(this.executionChance) != 0) return false;
        }

        Vec3d vec = RandomPositionGenerator.findRandomTarget(this.entity, 10, 7);
        if (vec == null) return false;

        this.xPosition = vec.x;
        this.yPosition = vec.y;
        this.zPosition = vec.z;
        this.mustUpdate = false;
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !this.entity.getNavigator().noPath();
    }

    @Override
    public void startExecuting() {
        this.entity.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
    }

    public void setWander() {
        this.mustUpdate = true;
    }
}
