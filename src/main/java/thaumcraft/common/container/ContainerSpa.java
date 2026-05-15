package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import thaumcraft.common.items.ItemBathSalts;
import thaumcraft.common.tiles.TileSpa;

public class ContainerSpa extends Container {
    private final TileSpa spa;

    public ContainerSpa() {
        this(null, null);
    }

    public ContainerSpa(InventoryPlayer playerInventory, TileSpa spa) {
        this.spa = spa;
        if (spa != null) {
            this.addSlotToContainer(new SlotLimitedByClass(ItemBathSalts.class, spa, 0, 65, 31));
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
        return isUsableTile(playerIn, this.spa);
    }

    private static boolean isUsableTile(EntityPlayer player, TileEntity tile) {
        if (player == null || tile == null || tile.getWorld() == null || tile.isInvalid()) return false;
        BlockPos pos = tile.getPos();
        return tile.getWorld().getTileEntity(pos) == tile
                && player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public boolean enchantItem(EntityPlayer playerIn, int id) {
        if (id == 1 && this.spa != null) {
            this.spa.toggleMix();
            return true;
        }
        return false;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = index >= 0 && index < this.inventorySlots.size() ? this.inventorySlots.get(index) : null;
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;

        ItemStack stack = slot.getStack();
        original = stack.copy();
        if (index == 0) {
            if (!this.mergeItemStack(stack, 1, this.inventorySlots.size(), true)) return ItemStack.EMPTY;
        } else if (this.spa != null && this.spa.isItemValidForSlot(0, stack)) {
            if (!this.mergeItemStack(stack, 0, 1, false)) return ItemStack.EMPTY;
        } else if (index >= 1 && index < 28) {
            if (!this.mergeItemStack(stack, 28, 37, false)) return ItemStack.EMPTY;
        } else if (index >= 28 && index < 37) {
            if (!this.mergeItemStack(stack, 1, 28, false)) return ItemStack.EMPTY;
        } else {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY); else slot.onSlotChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(playerIn, stack);
        return original;
    }
}
