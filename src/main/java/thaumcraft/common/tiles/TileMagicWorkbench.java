package thaumcraft.common.tiles;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class TileMagicWorkbench extends TileEntity {

    public ItemStack getStackInRowAndColumn(int row, int column) {
        return ItemStack.EMPTY;
    }
}
