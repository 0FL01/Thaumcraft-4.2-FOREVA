package thaumcraft.common.entities.ai.inventory;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.GolemHelper;

import java.util.ArrayList;

public class AISortingGoto extends EntityAIBase {
    private EntityGolemBase theGolem;
    private double movePosX, movePosY, movePosZ;
    private BlockPos dest = null;
    int count = 0;

    public AISortingGoto(EntityGolemBase golem) {
        this.theGolem = golem;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (theGolem.getCarried() != null && !theGolem.getCarried().isEmpty()) return false;
        if (theGolem.ticksExisted % Config.golemDelay > 0) return false;

        ArrayList<IInventory> results = GolemHelper.getMarkedContainers(theGolem.world, theGolem);
        if (results.isEmpty()) return false;

        EnumFacing facing = EnumFacing.VALUES[theGolem.homeFacing % EnumFacing.VALUES.length];
        BlockPos home = theGolem.getHomePosition();
        int cX = home.getX() - facing.getXOffset();
        int cY = home.getY() - facing.getYOffset();
        int cZ = home.getZ() - facing.getZOffset();
        int tX = 0, tY = 0, tZ = 0;
        double range = Double.MAX_VALUE;
        float dmod = theGolem.getRange();

        for (IInventory inv : results) {
            TileEntity te = (TileEntity) inv;
            BlockPos pos = te.getPos();
            double dist = theGolem.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            if (dist < range && dist <= dmod * dmod && !(pos.getX() == cX && pos.getY() == cY && pos.getZ() == cZ)) {
                range = dist;
                tX = pos.getX(); tY = pos.getY(); tZ = pos.getZ();
                this.dest = new BlockPos(tX, tY, tZ);
            }
        }
        if (this.dest != null) {
            this.movePosX = tX; this.movePosY = tY; this.movePosZ = tZ;
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.count > 0 && !this.theGolem.getNavigator().noPath();
    }

    @Override
    public void updateTask() {
        --this.count;
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
        this.theGolem.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.theGolem.getAIMoveSpeed());
    }
}
