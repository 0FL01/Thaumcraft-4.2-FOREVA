package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import thaumcraft.common.tiles.TileAlchemyFurnace;

public class ContainerAlchemyFurnace extends Container {
    private final TileAlchemyFurnace furnace;

    public ContainerAlchemyFurnace() {
        this(null, null);
    }

    public ContainerAlchemyFurnace(InventoryPlayer playerInventory, TileAlchemyFurnace furnace) {
        this.furnace = furnace;
        if (furnace != null) {
            this.addSlotToContainer(new SlotLimitedHasAspects(furnace, 0, 80, 8));
            this.addSlotToContainer(new Slot(furnace, 1, 80, 48));
        }
        if (playerInventory != null) {
            for (int row = 0; row < 3; ++row) {
                for (int col = 0; col < 9; ++col) {
                    this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
                }
            }
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 142));
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return isUsableTile(playerIn, this.furnace);
    }

    private static boolean isUsableTile(EntityPlayer player, TileEntity tile) {
        if (player == null || tile == null || tile.getWorld() == null || tile.isInvalid()) return false;
        BlockPos pos = tile.getPos();
        return tile.getWorld().getTileEntity(pos) == tile
                && player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = index >= 0 && index < this.inventorySlots.size() ? this.inventorySlots.get(index) : null;
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;
        ItemStack stack = slot.getStack();
        original = stack.copy();
        if (index < 2) {
            if (!this.mergeItemStack(stack, 2, this.inventorySlots.size(), true)) return ItemStack.EMPTY;
        } else if (this.furnace != null && this.furnace.isItemValidForSlot(0, stack)) {
            if (!this.mergeItemStack(stack, 0, 1, false)) return ItemStack.EMPTY;
        } else if (TileAlchemyFurnace.isItemFuel(stack)) {
            if (!this.mergeItemStack(stack, 1, 2, false)) return ItemStack.EMPTY;
        } else if (index >= 2 && index < 29) {
            if (!this.mergeItemStack(stack, 29, 38, false)) return ItemStack.EMPTY;
        } else if (index >= 29 && index < 38) {
            if (!this.mergeItemStack(stack, 2, 29, false)) return ItemStack.EMPTY;
        } else {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY); else slot.onSlotChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(playerIn, stack);
        return original;
    }
}
