package thaumcraft.common.entities.ai.inventory;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.GolemHelper;
import thaumcraft.common.entities.golems.Marker;

import java.util.ArrayList;

public class AIEmptyGoto extends EntityAIBase {
    private EntityGolemBase theGolem;
    private double movePosX, movePosY, movePosZ;
    private BlockPos dest = null;
    int count = 0;
    int prevX = 0, prevY = 0, prevZ = 0;

    public AIEmptyGoto(EntityGolemBase golem) {
        this.theGolem = golem;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (theGolem.getCarried() == null || theGolem.getCarried().isEmpty()) return false;
        if (theGolem.ticksExisted % Config.golemDelay > 0) return false;

        ArrayList<Byte> matchingColors = theGolem.getColorsMatching(theGolem.getCarried());
        for (byte color : matchingColors) {
            ArrayList<IInventory> results = GolemHelper.getContainersWithRoom(theGolem.world, theGolem, color);
            if (results.isEmpty()) continue;

            EnumFacing facing = EnumFacing.VALUES[theGolem.homeFacing % EnumFacing.VALUES.length];
            BlockPos home = theGolem.getHomePosition();
            int cX = home.getX() - facing.getXOffset();
            int cY = home.getY() - facing.getYOffset();
            int cZ = home.getZ() - facing.getZOffset();
            int tX = 0, tY = 0, tZ = 0;
            double range = Double.MAX_VALUE;
            float dmod = theGolem.getRange();

            for (IInventory te : results) {
                TileEntity tile = (TileEntity) te;
                BlockPos pos = tile.getPos();
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
        }

        for (byte color : matchingColors) {
            ArrayList<Marker> markers = theGolem.getMarkers();
            for (Marker marker : markers) {
                if (marker.color != color && color != -1) continue;
                TileEntity te = theGolem.world.getTileEntity(new BlockPos(marker.x, marker.y, marker.z));
                if (te != null && te instanceof IInventory) continue;
                this.movePosX = marker.x; this.movePosY = marker.y; this.movePosZ = marker.z;
                return true;
            }
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
        if (this.count == 0 && this.prevX == MathHelper.floor(theGolem.posX)
            && this.prevY == MathHelper.floor(theGolem.posY)
            && this.prevZ == MathHelper.floor(theGolem.posZ)) {
            Vec3d var2 = RandomPositionGenerator.findRandomTarget(this.theGolem, 2, 1);
            if (var2 != null) {
                this.count = 20;
                this.theGolem.getNavigator().tryMoveToXYZ(var2.x, var2.y, var2.z, this.theGolem.getAIMoveSpeed());
            }
        }
        super.updateTask();
    }

    @Override
    public void resetTask() {
        this.count = 0;
        this.dest = null;
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
