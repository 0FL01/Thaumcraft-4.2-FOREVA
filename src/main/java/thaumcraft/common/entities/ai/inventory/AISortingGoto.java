package thaumcraft.common.entities.ai.inventory;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.GolemHelper;

public class AISortingGoto extends EntityAIBase {
    private EntityGolemBase theGolem;
    private double movePosX, movePosY, movePosZ;
    private BlockPos dest = null;
    int count = 0;
    int prevX = 0, prevY = 0, prevZ = 0;

    public AISortingGoto(EntityGolemBase golem) {
        this.theGolem = golem;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (theGolem.getCarried() == null || theGolem.getCarried().isEmpty()) return false;
        if (theGolem.ticksExisted % Config.golemDelay > 0) return false;

        float range = theGolem.getRange();
        IInventory destination = GolemHelper.findSortingDestination(
                theGolem, theGolem.getCarried(), range * range);
        if (!(destination instanceof TileEntity)) return false;
        this.dest = ((TileEntity) destination).getPos();
        this.movePosX = this.dest.getX();
        this.movePosY = this.dest.getY();
        this.movePosZ = this.dest.getZ();
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.count > 0 && !this.theGolem.getNavigator().noPath();
    }

    @Override
    public void updateTask() {
        --this.count;
        if (this.count == 0 && this.prevX == MathHelper.floor(theGolem.posX)
                && this.prevY == MathHelper.floor(theGolem.posY)
                && this.prevZ == MathHelper.floor(theGolem.posZ)) {
            Vec3d escape = RandomPositionGenerator.findRandomTarget(this.theGolem, 2, 1);
            if (escape != null) {
                this.count = 20;
                this.theGolem.getNavigator().tryMoveToXYZ(
                        escape.x, escape.y, escape.z, this.theGolem.getAIMoveSpeed());
            }
        }
        super.updateTask();
    }

    @Override
    public void resetTask() {
        this.dest = null;
        this.count = 0;
    }

    @Override
    public void startExecuting() {
        this.count = 200;
        this.prevX = MathHelper.floor(theGolem.posX);
        this.prevY = MathHelper.floor(theGolem.posY);
        this.prevZ = MathHelper.floor(theGolem.posZ);
        this.theGolem.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.theGolem.getAIMoveSpeed());
    }
}
