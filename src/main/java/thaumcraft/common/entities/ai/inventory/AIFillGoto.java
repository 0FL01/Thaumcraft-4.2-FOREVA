package thaumcraft.common.entities.ai.inventory;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.GolemHelper;

import java.util.ArrayList;

public class AIFillGoto extends EntityAIBase {
    private EntityGolemBase theGolem;
    private double movePosX, movePosY, movePosZ;
    private BlockPos dest = null;
    int count = 0;
    int prevX = 0, prevY = 0, prevZ = 0;

    public AIFillGoto(EntityGolemBase golem) {
        this.theGolem = golem;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (theGolem.getCarried() != null && !theGolem.getCarried().isEmpty()) return false;
        if (theGolem.ticksExisted % Config.golemDelay > 0) return false;
        if (!theGolem.hasSomething()) return false;

        ArrayList<ItemStack> mi = GolemHelper.getMissingItems(theGolem);
        if (mi == null || mi.isEmpty()) return false;

        ArrayList<ItemStack> missingItems = new ArrayList<>();
        if (theGolem.checkOreDict()) {
            for (ItemStack stack : mi) {
                int[] ids = net.minecraftforge.oredict.OreDictionary.getOreIDs(stack);
                if (ids.length > 0) {
                    for (int id : ids) {
                        String oreName = net.minecraftforge.oredict.OreDictionary.getOreName(id);
                        if (oreName != null && !oreName.isEmpty()) {
                            net.minecraft.util.NonNullList<ItemStack> ores = net.minecraftforge.oredict.OreDictionary.getOres(oreName);
                            for (ItemStack ore : ores) {
                                missingItems.add(ore.copy());
                            }
                        }
                    }
                } else {
                    missingItems.add(stack.copy());
                }
            }
        } else {
            for (ItemStack stack : mi) {
                missingItems.add(stack.copy());
            }
        }

        ArrayList<IInventory> results = new ArrayList<>();
        for (ItemStack stack : missingItems) {
            theGolem.itemWatched = stack.copy();
            ArrayList<Byte> matchingColors = theGolem.getColorsMatching(theGolem.itemWatched);
            for (byte color : matchingColors) {
                results = GolemHelper.getContainersWithGoods(theGolem.world, theGolem, theGolem.itemWatched, color);
            }
            if (!results.isEmpty()) break;
        }

        if (results == null || results.isEmpty()) return false;

        EnumFacing facing = EnumFacing.VALUES[theGolem.homeFacing % EnumFacing.VALUES.length];
        BlockPos home = theGolem.getHomePosition();
        int cX = home.getX() - facing.getXOffset();
        int cY = home.getY() - facing.getYOffset();
        int cZ = home.getZ() - facing.getZOffset();
        int tX = 0, tY = 0, tZ = 0;
        double range = Double.MAX_VALUE;
        float dmod = theGolem.getRange();

        for (IInventory iInventory : results) {
            TileEntity te = (TileEntity) iInventory;
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
