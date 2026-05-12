package thaumcraft.common.entities.ai.inventory;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.golems.EntityGolemBase;

public class AIHomeDrop extends EntityAIBase {
    private EntityGolemBase theGolem;
    private int countChest = 0;
    private IInventory inv;
    int count = 0;

    public AIHomeDrop(EntityGolemBase golem) {
        this.theGolem = golem;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (theGolem.getCarried() == null || theGolem.getCarried().isEmpty()) return false;
        if (!theGolem.getNavigator().noPath()) return false;
        BlockPos home = theGolem.getHomePosition();
        if (theGolem.getDistanceSq(home.getX() + 0.5, home.getY() + 0.5, home.getZ() + 0.5) > 5.0) return false;

        net.minecraft.util.EnumFacing facing = net.minecraft.util.EnumFacing.VALUES[theGolem.homeFacing % net.minecraft.util.EnumFacing.VALUES.length];
        int cX = home.getX() - facing.getXOffset();
        int cY = home.getY() - facing.getYOffset();
        int cZ = home.getZ() - facing.getZOffset();
        TileEntity tile = theGolem.world.getTileEntity(new BlockPos(cX, cY, cZ));
        return tile == null || !(tile instanceof IInventory);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.count > 0 && (this.shouldExecute() || this.countChest > 0);
    }

    @Override
    public void resetTask() {
        try {
            if (this.inv != null && Config.golemChestInteract) {
                this.inv.closeInventory(null);
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void updateTask() {
        --this.countChest;
        --this.count;
        super.updateTask();
    }

    @Override
    public void startExecuting() {
        this.count = 200;
        BlockPos home = theGolem.getHomePosition();
        net.minecraft.util.EnumFacing facing = net.minecraft.util.EnumFacing.VALUES[theGolem.homeFacing % net.minecraft.util.EnumFacing.VALUES.length];
        int cX = home.getX() - facing.getXOffset();
        int cY = home.getY() - facing.getYOffset();
        int cZ = home.getZ() - facing.getZOffset();

        if (theGolem.getCarried() == null || theGolem.getCarried().isEmpty()) return;
        EntityItem item = new EntityItem(theGolem.world, theGolem.posX,
            theGolem.posY + (double)(theGolem.height / 2.0F), theGolem.posZ,
            theGolem.getCarried().copy());
        if (item != null) {
            double distance = theGolem.getDistance(cX + 0.5, cY + 0.5, cZ + 0.5);
            item.motionX = ((double)cX + 0.5 - theGolem.posX) * (distance / 3.0);
            item.motionY = 0.1 + ((double)cY + 0.5 - (theGolem.posY + (double)(theGolem.height / 2.0F))) * (distance / 3.0);
            item.motionZ = ((double)cZ + 0.5 - theGolem.posZ) * (distance / 3.0);
            item.setPickupDelay(10);
            theGolem.world.spawnEntity(item);
            theGolem.setCarried(ItemStack.EMPTY);
            theGolem.startActionTimer();
            theGolem.updateCarried();
        }
    }
}
