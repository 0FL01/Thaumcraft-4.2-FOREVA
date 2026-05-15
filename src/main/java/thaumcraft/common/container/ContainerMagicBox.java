package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class ContainerMagicBox extends Container {
    private final TileEntity box;
    private final IInventory boxInventory;
    private final int numRows;

    public ContainerMagicBox() {
        this(null, null);
    }

    public ContainerMagicBox(InventoryPlayer playerInventory, TileEntity box) {
        this.box = box;
        this.boxInventory = box instanceof IInventory ? (IInventory) box : null;
        this.numRows = this.boxInventory == null ? 0 : this.boxInventory.getSizeInventory() / 9;
        if (this.boxInventory != null) {
            if (playerInventory != null) {
                this.boxInventory.openInventory(playerInventory.player);
            }
            for (int row = 0; row < this.numRows; ++row) {
                for (int col = 0; col < 9; ++col) {
                    this.addSlotToContainer(new Slot(this.boxInventory, col + row * 9, 8 + col * 18, 18 + row * 18));
                }
            }
        }
        if (playerInventory != null) {
            int playerInventoryY = 31 + this.numRows * 18;
            for (int row = 0; row < 3; ++row) {
                for (int col = 0; col < 9; ++col) {
                    this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, playerInventoryY + row * 18));
                }
            }
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, playerInventoryY + 58));
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.boxInventory != null && this.boxInventory.isUsableByPlayer(playerIn) && isUsableTile(playerIn, this.box);
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
        int boxSlots = this.numRows * 9;
        if (index < boxSlots) {
            if (!this.mergeItemStack(stack, boxSlots, this.inventorySlots.size(), true)) return ItemStack.EMPTY;
        } else if (!this.mergeItemStack(stack, 0, boxSlots, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY); else slot.onSlotChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(playerIn, stack);
        return original;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (this.boxInventory != null) {
            this.boxInventory.closeInventory(playerIn);
        }
    }
}
