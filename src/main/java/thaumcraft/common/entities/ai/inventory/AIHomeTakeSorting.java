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

public class AIHomeTakeSorting extends EntityAIBase {
    private EntityGolemBase theGolem;
    private int countChest = 0;
    private IInventory inv;
    int count = 0;

    public AIHomeTakeSorting(EntityGolemBase golem) {
        this.theGolem = golem;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        BlockPos home = theGolem.getHomePosition();
        if (theGolem.getCarried() != null && !theGolem.getCarried().isEmpty()) return false;
        if (theGolem.ticksExisted % Config.golemDelay > 0) return false;
        if (!theGolem.getNavigator().noPath()) return false;
        if (theGolem.getDistanceSq(home.getX() + 0.5, home.getY() + 0.5, home.getZ() + 0.5) > 5.0) return false;

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
                ArrayList<ItemStack> neededList = GolemHelper.getItemsNeeded(theGolem, theGolem.checkOreDict());
                if (neededList != null && !neededList.isEmpty()) {
                    for (ItemStack stack : neededList) {
                        ItemStack needed = stack.copy();
                        needed.setCount(theGolem.getCarrySpace());
                        ItemStack extracted = InventoryUtils.extractStack((IInventory) tile, needed,
                            facing.ordinal(), theGolem.checkOreDict(), theGolem.ignoreDamage(), theGolem.ignoreNBT(), false);
                        if (extracted == null || extracted.isEmpty()) continue;
                        return true;
                    }
                }
            }
            if (!didRepeat) {
                TileEntity dc = InventoryUtils.getDoubleChest(tile);
                if (dc != null) { tile = dc; didRepeat = true; continue; }
            }
            repeat = false;
        }
        return false;
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
                ArrayList<ItemStack> neededList = GolemHelper.getItemsNeeded(theGolem, theGolem.checkOreDict());
                if (neededList != null && !neededList.isEmpty()) {
                    for (ItemStack stack : neededList) {
                        ItemStack needed = stack.copy();
                        needed.setCount(theGolem.getCarrySpace());
                        ItemStack result = InventoryUtils.extractStack((IInventory) tile, needed,
                            facing.ordinal(), theGolem.checkOreDict(), theGolem.ignoreDamage(), theGolem.ignoreNBT(), true);
                        if (result == null || result.isEmpty()) continue;
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
                if (theGolem.getCarried() != null && !theGolem.getCarried().isEmpty()) break;
            }
            if (!didRepeat) {
                TileEntity dc = InventoryUtils.getDoubleChest(tile);
                if (dc != null) { tile = dc; didRepeat = true; continue; }
            }
            repeat = false;
        }
    }
}
