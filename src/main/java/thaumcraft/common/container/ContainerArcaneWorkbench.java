package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import thaumcraft.common.tiles.TileArcaneWorkbench;

public class ContainerArcaneWorkbench extends Container {
    private final TileArcaneWorkbench tileEntity;

    public ContainerArcaneWorkbench() {
        this(null, null);
    }

    public ContainerArcaneWorkbench(InventoryPlayer playerInventory, TileArcaneWorkbench tileEntity) {
        this.tileEntity = tileEntity;
        if (this.tileEntity != null) {
            this.tileEntity.eventHandler = this;
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.tileEntity != null && this.tileEntity.isUsableByPlayer(playerIn) && isUsableTile(playerIn, this.tileEntity);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (this.tileEntity != null) {
            this.tileEntity.eventHandler = null;
        }
    }

    private static boolean isUsableTile(EntityPlayer player, TileEntity tile) {
        if (player == null || tile == null || tile.getWorld() == null || tile.isInvalid()) return false;
        BlockPos pos = tile.getPos();
        return tile.getWorld().getTileEntity(pos) == tile
                && player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
    }
}
