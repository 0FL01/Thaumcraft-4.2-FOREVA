package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import thaumcraft.common.items.wands.foci.FocusExcavation;
import thaumcraft.common.tiles.TileArcaneBore;

public class ContainerArcaneBore extends Container {
    private final TileArcaneBore tileEntity;

    public ContainerArcaneBore() {
        this(null, null);
    }

    public ContainerArcaneBore(InventoryPlayer playerInventory, TileArcaneBore tileEntity) {
        this.tileEntity = tileEntity;
        if (tileEntity != null) {
            this.addSlotToContainer(new SlotLimitedByClass(FocusExcavation.class, tileEntity, 0, 26, 18, 1));
            this.addSlotToContainer(new SlotLimitedByClass(ItemPickaxe.class, tileEntity, 1, 74, 18, 1));
        }
        if (playerInventory != null) this.bindPlayerInventory(playerInventory);
    }

    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(inventoryPlayer, col + row * 9 + 9, 8 + col * 18, 59 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot(inventoryPlayer, col, 8 + col * 18, 117));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return isUsableTile(playerIn, this.tileEntity);
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
        if (index <= 1) {
            if (!this.mergeItemStack(stack, 2, this.inventorySlots.size(), true)) return ItemStack.EMPTY;
        } else if (stack.getItem() instanceof FocusExcavation) {
            if (!this.mergeItemStack(stack, 0, 1, false)) return ItemStack.EMPTY;
        } else if (stack.getItem() instanceof ItemPickaxe) {
            if (!this.mergeItemStack(stack, 1, 2, false)) return ItemStack.EMPTY;
        } else {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY); else slot.onSlotChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(playerIn, stack);
        return original;
    }
}
