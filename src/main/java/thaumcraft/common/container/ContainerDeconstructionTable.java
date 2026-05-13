package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import thaumcraft.common.tiles.TileDeconstructionTable;

public class ContainerDeconstructionTable extends Container {
    private final TileDeconstructionTable table;

    public ContainerDeconstructionTable() {
        this(null, null);
    }

    public ContainerDeconstructionTable(InventoryPlayer playerInventory, TileDeconstructionTable table) {
        this.table = table;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.table != null && this.table.isUsableByPlayer(playerIn) && isUsableTile(playerIn, this.table);
    }

    private static boolean isUsableTile(EntityPlayer player, TileEntity tile) {
        if (player == null || tile == null || tile.getWorld() == null || tile.isInvalid()) return false;
        BlockPos pos = tile.getPos();
        return tile.getWorld().getTileEntity(pos) == tile
                && player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
    }
}
