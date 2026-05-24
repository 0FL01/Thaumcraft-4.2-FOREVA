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

        BlockPos home = theGolem.getHomePosition();
        EnumFacing facing = EnumFacing.VALUES[theGolem.homeFacing % EnumFacing.VALUES.length];
        int cX = home.getX() - facing.getXOffset();
        int cY = home.getY() - facing.getYOffset();
        int cZ = home.getZ() - facing.getZOffset();

        ArrayList<Byte> matchingColors = theGolem.getColorsMatching(theGolem.getCarried());
        ArrayList<IInventory> mc = GolemHelper.getMarkedContainersAdjacentToGolem(theGolem.world, theGolem);
        for (IInventory te : mc) {
            TileEntity tile = (TileEntity) te;
            if (tile == null) continue;
            BlockPos pos = tile.getPos();
            if (pos.getX() == cX && pos.getY() == cY && pos.getZ() == cZ) continue;

            for (byte color : matchingColors) {
                for (Integer side : GolemHelper.getMarkedSides(theGolem, tile, color)) {
                    ItemStack is = InventoryUtils.placeItemStackIntoInventory(theGolem.getCarried(), te, side, false);
                    if (ItemStack.areItemStacksEqual(is, theGolem.itemCarried)) continue;
                    this.xx = pos.getX();
                    this.yy = pos.getY();
                    this.zz = pos.getZ();
                    return true;
                }
                TileEntity dc = InventoryUtils.getDoubleChest(tile);
                if (dc != null) {
                    for (Integer side : GolemHelper.getMarkedSides(theGolem, tile, color)) {
                        ItemStack is = InventoryUtils.placeItemStackIntoInventory(theGolem.getCarried(), (IInventory) dc, side, false);
                        if (!ItemStack.areItemStacksEqual(is, theGolem.itemCarried)) continue;
                        this.xx = pos.getX();
                        this.yy = pos.getY();
                        this.zz = pos.getZ();
                        return true;
                    }
                }
            }
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
        TileEntity tile = theGolem.world.getTileEntity(new BlockPos(this.xx, this.yy, this.zz));
        if (tile != null && !(tile.getPos().getX() == cX && tile.getPos().getY() == cY && tile.getPos().getZ() == cZ)) {
            IInventory te = (IInventory) tile;
            ArrayList<Byte> matchingColors = theGolem.getColorsMatching(theGolem.getCarried());
            for (byte color : matchingColors) {
                for (Integer side : GolemHelper.getMarkedSides(theGolem, tile, color)) {
                    theGolem.setCarried(InventoryUtils.placeItemStackIntoInventory(theGolem.getCarried(), te, side, true));
                    this.countChest = 5;
                    this.inv = te;
                    if (theGolem.getCarried() == null || theGolem.getCarried().isEmpty()) break;
                }
                if (theGolem.getCarried() != null && !theGolem.getCarried().isEmpty()) {
                    TileEntity dc = InventoryUtils.getDoubleChest(tile);
                    if (dc != null) {
                        for (Integer side : GolemHelper.getMarkedSides(theGolem, tile, color)) {
                            ItemStack is = InventoryUtils.placeItemStackIntoInventory(theGolem.getCarried(), (IInventory) dc, side, false);
                            if (ItemStack.areItemStacksEqual(is, theGolem.itemCarried)) continue;
                            theGolem.setCarried(InventoryUtils.placeItemStackIntoInventory(theGolem.getCarried(), (IInventory) dc, side, true));
                            this.countChest = 5;
                            this.inv = (IInventory) dc;
                            if (theGolem.getCarried() == null || theGolem.getCarried().isEmpty()) break;
                        }
                    }
                }
                if (this.countChest == 5) {
                    try {
                        if (Config.golemChestInteract) {
                            InventoryUtils.openInventoryForGolem(this.inv);
                        }
                    } catch (Exception ignored) {}
                    break;
                }
            }
        }
        theGolem.updateCarried();
    }
}
