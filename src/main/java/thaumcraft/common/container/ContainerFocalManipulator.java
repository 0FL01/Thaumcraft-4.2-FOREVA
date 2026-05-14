package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.tiles.TileFocalManipulator;

public class ContainerFocalManipulator extends Container {
    private final TileFocalManipulator table;

    public ContainerFocalManipulator() {
        this(null, null);
    }

    public ContainerFocalManipulator(InventoryPlayer playerInventory, TileFocalManipulator table) {
        this.table = table;
        if (table != null) {
            this.addSlotToContainer(new SlotLimitedByClass(ItemFocusBasic.class, table, 0, 88, 60, 1));
        }
        if (playerInventory != null) {
            for (int row = 0; row < 3; ++row) {
                for (int col = 0; col < 9; ++col) {
                    this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 16 + col * 18, 151 + row * 18));
                }
            }
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(playerInventory, col, 16 + col * 18, 209));
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return isUsableTile(playerIn, this.table);
    }

    private static boolean isUsableTile(EntityPlayer player, TileEntity tile) {
        if (player == null || tile == null || tile.getWorld() == null || tile.isInvalid()) return false;
        BlockPos pos = tile.getPos();
        return tile.getWorld().getTileEntity(pos) == tile
                && player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public boolean enchantItem(EntityPlayer playerIn, int id) {
        if (this.table != null && id >= 0 && !this.table.startCraft(id, playerIn)) {
            if (this.table.getWorld() != null) {
                this.table.getWorld().playSound(null, this.table.getPos(), TCSounds.CRAFTFAIL, SoundCategory.BLOCKS, 0.33F, 1.0F);
            }
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
            if (!this.mergeItemStack(stack, 1, this.inventorySlots.size(), false)) return ItemStack.EMPTY;
        } else if (stack.getItem() instanceof ItemFocusBasic) {
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
