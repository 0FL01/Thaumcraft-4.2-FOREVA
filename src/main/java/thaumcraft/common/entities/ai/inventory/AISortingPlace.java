package thaumcraft.common.entities.ai.inventory;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.GolemHelper;
import thaumcraft.common.lib.utils.InventoryUtils;

public class AISortingPlace extends EntityAIBase {
    private EntityGolemBase theGolem;
    private int countChest = 0;
    private IInventory inv;
    private int xx, yy, zz;
    int count = 0;

    public AISortingPlace(EntityGolemBase golem) {
        this.theGolem = golem;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (theGolem.getCarried() == null || theGolem.getCarried().isEmpty()) return false;
        if (!theGolem.getNavigator().noPath()) return false;

        IInventory destination = GolemHelper.findSortingDestination(
                theGolem, theGolem.getCarried(), GolemHelper.ADJACENT_RANGE);
        if (!(destination instanceof TileEntity)) return false;
        BlockPos pos = ((TileEntity) destination).getPos();
        this.xx = pos.getX();
        this.yy = pos.getY();
        this.zz = pos.getZ();
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.count > 0 && (this.shouldExecute() || this.countChest > 0);
    }

    @Override
    public void resetTask() {
        try {
            if (this.inv != null && Config.golemChestInteract) {
                InventoryUtils.closeInventoryForGolem(this.inv);
            }
        } catch (Exception ignored) {}
        this.inv = null;
        this.countChest = 0;
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
        this.countChest = 0;
        this.inv = null;
        TileEntity tile = theGolem.world.getTileEntity(new BlockPos(this.xx, this.yy, this.zz));
        if (tile instanceof IInventory && GolemHelper.isValidSortingDestination(
                theGolem, (IInventory) tile, theGolem.getCarried(), GolemHelper.ADJACENT_RANGE)) {
            IInventory destination = (IInventory) tile;
            for (int side : GolemHelper.getMarkedSides(theGolem, tile, (byte) -1)) {
                ItemStack carried = theGolem.getCarried();
                if (!GolemHelper.canSortItemIntoInventory(theGolem, destination, carried, side)) continue;
                ItemStack remaining = InventoryUtils.placeItemStackIntoInventory(carried, destination, side, true);
                if (ItemStack.areItemStacksEqual(remaining, carried)) continue;
                theGolem.setCarried(remaining);
                this.countChest = 5;
                this.inv = destination;
                try {
                    if (Config.golemChestInteract) {
                        InventoryUtils.openInventoryForGolem(this.inv);
                    }
                } catch (Exception ignored) {}
                break;
            }
        }
        theGolem.updateCarried();
    }
}
