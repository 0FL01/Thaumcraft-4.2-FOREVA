package thaumcraft.common.entities.ai.inventory;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.GolemHelper;
import thaumcraft.common.lib.utils.InventoryUtils;

import java.util.ArrayList;

public class AIHomeReplace extends EntityAIBase {
    private EntityGolemBase theGolem;
    private int countChest = 0;
    private IInventory inv;

    public AIHomeReplace(EntityGolemBase golem) {
        this.theGolem = golem;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        BlockPos home = theGolem.getHomePosition();
        if (theGolem.getCarried() == null || theGolem.getCarried().isEmpty()) return false;
        if (theGolem.ticksExisted % Config.golemDelay > 0) return false;
        if (!theGolem.getNavigator().noPath()) return false;
        if (theGolem.getDistanceSq(home.getX() + 0.5, home.getY() + 0.5, home.getZ() + 0.5) > 5.0) return false;

        if (GolemHelper.isOnTimeOut(theGolem, theGolem.getCarried())) return true;

        switch (theGolem.getCore()) {
            case 1: return !GolemHelper.findSomethingEmptyCore(theGolem, theGolem.getCarried());
            case 8: return !GolemHelper.findSomethingUseCore(theGolem, theGolem.getCarried());
            case 10: return !GolemHelper.findSomethingSortCore(theGolem, theGolem.getCarried());
        }

        EnumFacing facing = EnumFacing.VALUES[theGolem.homeFacing % EnumFacing.VALUES.length];
        int cX = home.getX() - facing.getXOffset();
        int cY = home.getY() - facing.getYOffset();
        int cZ = home.getZ() - facing.getZOffset();
        TileEntity tile = theGolem.world.getTileEntity(new BlockPos(cX, cY, cZ));
        ArrayList<ItemStack> neededList = GolemHelper.getItemsNeeded(theGolem, theGolem.checkOreDict());
        if (neededList != null && !neededList.isEmpty()) {
            for (ItemStack stack : neededList) {
                if (stack == null || stack.isEmpty()) continue;
                if (!InventoryUtils.areItemStacksEqual(stack, theGolem.itemCarried,
                    theGolem.checkOreDict(), theGolem.ignoreDamage(), theGolem.ignoreNBT())) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.shouldExecute() || this.countChest > 0;
    }

    @Override
    public void resetTask() {
        try {
            if (this.inv != null && Config.golemChestInteract) {
                InventoryUtils.closeInventoryForGolem(this.inv);
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void updateTask() {
        --this.countChest;
        super.updateTask();
    }

    @Override
    public void startExecuting() {
        BlockPos home = theGolem.getHomePosition();
        EnumFacing facing = EnumFacing.VALUES[theGolem.homeFacing % EnumFacing.VALUES.length];
        int cX = home.getX() - facing.getXOffset();
        int cY = home.getY() - facing.getYOffset();
        int cZ = home.getZ() - facing.getZOffset();
        TileEntity tile = theGolem.world.getTileEntity(new BlockPos(cX, cY, cZ));
        boolean repeat = true;
        boolean didRepeat = false;
        while (repeat) {
            if (didRepeat) repeat = false;
            if (tile != null && tile instanceof IInventory) {
                ItemStack result = InventoryUtils.placeItemStackIntoInventory(theGolem.getCarried(), (IInventory) tile, facing.ordinal(), true);
                if (!ItemStack.areItemStacksEqual(result, theGolem.itemCarried)) {
                    theGolem.setCarried(result);
                    try {
                        if (Config.golemChestInteract) {
                            InventoryUtils.openInventoryForGolem((IInventory) tile);
                        }
                    } catch (Exception ignored) {}
                    this.countChest = 5;
                    this.inv = (IInventory) tile;
                    break;
                }
            }
            if (!didRepeat) {
                TileEntity dc = InventoryUtils.getDoubleChest(tile);
                if (dc != null) { tile = dc; didRepeat = true; continue; }
            }
            repeat = false;
        }
    }
}
