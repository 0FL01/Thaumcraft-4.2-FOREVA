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

public class AIFillTake extends EntityAIBase {
    private EntityGolemBase theGolem;
    private int countChest = 0;
    private IInventory inv;
    int count = 0;

    public AIFillTake(EntityGolemBase golem) {
        this.theGolem = golem;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (theGolem.getCarried() != null && !theGolem.getCarried().isEmpty()) return false;
        if (theGolem.itemWatched == null || theGolem.itemWatched.isEmpty()) return false;
        if (!theGolem.getNavigator().noPath()) return false;
        if (!theGolem.hasSomething()) return false;

        EnumFacing facing = EnumFacing.VALUES[theGolem.homeFacing % EnumFacing.VALUES.length];
        BlockPos home = theGolem.getHomePosition();
        int cX = home.getX() - facing.getXOffset();
        int cY = home.getY() - facing.getYOffset();
        int cZ = home.getZ() - facing.getZOffset();

        ArrayList<IInventory> mc = GolemHelper.getMarkedContainersAdjacentToGolem(theGolem.world, theGolem);
        for (IInventory te : mc) {
            TileEntity tile = (TileEntity) te;
            if (tile == null) continue;
            BlockPos pos = tile.getPos();
            if (pos.getX() == cX && pos.getY() == cY && pos.getZ() == cZ) continue;

            ArrayList<Byte> matchingColors = theGolem.getColorsMatching(theGolem.itemWatched);
            for (byte color : matchingColors) {
                for (Integer side : GolemHelper.getMarkedSides(theGolem, tile, color)) {
                    ItemStack target = theGolem.itemWatched.copy();
                    target.setCount(theGolem.getToggles()[0] ? theGolem.getCarrySpace() : Math.min(target.getCount(), theGolem.getCarrySpace()));
                    ItemStack result = InventoryUtils.extractStack(te, target, side,
                        theGolem.checkOreDict(), theGolem.ignoreDamage(), theGolem.ignoreNBT(), true);
                    IInventory sourceInv = te;
                    if (result == null || result.isEmpty()) {
                        TileEntity dc = InventoryUtils.getDoubleChest(tile);
                        if (dc != null) {
                            result = InventoryUtils.extractStack((IInventory) dc, target, side,
                                theGolem.checkOreDict(), theGolem.ignoreDamage(), theGolem.ignoreNBT(), true);
                            sourceInv = (IInventory) dc;
                        }
                    }
                    if (result == null || result.isEmpty()) continue;
                    theGolem.setCarried(result);
                    try {
                        if (Config.golemChestInteract) {
                            InventoryUtils.openInventoryForGolem(sourceInv);
                        }
                    } catch (Exception ignored) {}
                    this.countChest = 5;
                    this.inv = sourceInv;
                    this.count = 200;
                    theGolem.itemWatched = null;
                    theGolem.updateCarried();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.count > 0 && (!theGolem.getNavigator().noPath() || this.countChest > 0);
    }

    @Override
    public void updateTask() {
        --this.count;
        --this.countChest;
        super.updateTask();
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
    public void startExecuting() {}
}
