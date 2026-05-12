package thaumcraft.common.entities.ai.misc;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thaumcraft.common.entities.golems.EntityGolemBase;

public class AIReturnHome extends EntityAIBase {

    private EntityGolemBase theGolem;
    private double movePosX;
    private double movePosY;
    private double movePosZ;
    private int pathingDelay = 0;
    private int pathingDelayInc = 5;
    private int count = 0;
    private int prevX = 0;
    private int prevY = 0;
    private int prevZ = 0;

    public AIReturnHome(EntityGolemBase golem) {
        this.theGolem = golem;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        BlockPos home = this.theGolem.getHomePosition();
        if (this.pathingDelay > 0) --this.pathingDelay;
        if (this.pathingDelay > 0
            || this.theGolem.getDistanceSq(home.getX() + 0.5, home.getY() + 0.5, home.getZ() + 0.5) < 3.0) {
            return false;
        }
        this.movePosX = home.getX() + 0.5;
        this.movePosY = home.getY() + 0.5;
        this.movePosZ = home.getZ() + 0.5;
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        BlockPos home = this.theGolem.getHomePosition();
        return this.pathingDelay <= 0
            && this.count > 0
            && !this.theGolem.getNavigator().noPath()
            && this.theGolem.getDistanceSq(home.getX() + 0.5, home.getY() + 0.5, home.getZ() + 0.5) >= 3.0;
    }

    @Override
    public void resetTask() {
    }

    @Override
    public void updateTask() {
        --this.count;
        if (this.count == 0
            && this.prevX == MathHelper.floor(this.theGolem.posX)
            && this.prevY == MathHelper.floor(this.theGolem.posY)
            && this.prevZ == MathHelper.floor(this.theGolem.posZ)) {
            Vec3d vec3 = RandomPositionGenerator.findRandomTarget(this.theGolem, 2, 1);
            if (vec3 != null) {
                this.count = 20;
                boolean path = this.theGolem.getNavigator().tryMoveToXYZ(
                    vec3.x + 0.5, vec3.y + 0.5, vec3.z + 0.5, this.theGolem.getAIMoveSpeed());
                if (!path) {
                    this.pathingDelay = this.pathingDelayInc;
                    if (this.pathingDelayInc < 50) this.pathingDelayInc += 5;
                } else {
                    this.pathingDelayInc = 5;
                }
            }
        }
    }

    @Override
    public void startExecuting() {
        this.count = 20;
        this.prevX = MathHelper.floor(this.theGolem.posX);
        this.prevY = MathHelper.floor(this.theGolem.posY);
        this.prevZ = MathHelper.floor(this.theGolem.posZ);
        boolean path = this.theGolem.getNavigator().tryMoveToXYZ(
            this.movePosX, this.movePosY, this.movePosZ, this.theGolem.getAIMoveSpeed());
        if (!path) {
            this.pathingDelay = this.pathingDelayInc;
            if (this.pathingDelayInc < 50) this.pathingDelayInc += 5;
        } else {
            this.pathingDelayInc = 5;
        }
    }
}
