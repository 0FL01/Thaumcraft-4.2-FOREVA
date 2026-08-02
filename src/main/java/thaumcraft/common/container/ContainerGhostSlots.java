package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Base container that protects ghost/fake slots from player item interaction.
 * Ghost slots visually display items but cannot be taken by the player.
 * Subclasses mark ghost slots via isGhostSlot().
 */
public abstract class ContainerGhostSlots extends Container {

    /**
     * Override in subclasses to identify which slots are ghost slots.
     */
    protected boolean isGhostSlot(Slot slot) {
        return false;
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player) {
        if (slotId >= 0 && slotId < this.inventorySlots.size()) {
            Slot slot = this.inventorySlots.get(slotId);
            if (slot != null && isGhostSlot(slot)) {
                ItemStack held = player.inventory.getItemStack();
                if (clickType == ClickType.QUICK_MOVE) {
                    if (slot.getHasStack()) {
                        if (dragType == 0) {
                            slot.putStack(ItemStack.EMPTY);
                        } else if (dragType == 1) {
                            ItemStack stack = slot.getStack().copy();
                            stack.setCount(Math.min(slot.getSlotStackLimit(), stack.getCount() + 16));
                            slot.putStack(stack);
                        }
                    }
                    return ItemStack.EMPTY;
                }
                if (clickType != ClickType.PICKUP) return ItemStack.EMPTY;

                if (!held.isEmpty()) {
                    if (!slot.isItemValid(held)) return ItemStack.EMPTY;
                    ItemStack stack = slot.getStack();
                    if (stack.isEmpty() || !stack.isItemEqual(held)
                            || !ItemStack.areItemStackTagsEqual(stack, held)) {
                        ItemStack copy = held.copy();
                        copy.setCount(Math.min(slot.getSlotStackLimit(), dragType == 0 ? held.getCount() : 1));
                        slot.putStack(copy);
                    } else {
                        ItemStack copy = stack.copy();
                        int added = dragType == 0 ? held.getCount() : 1;
                        copy.setCount(Math.min(slot.getSlotStackLimit(), copy.getCount() + added));
                        slot.putStack(copy);
                    }
                } else if (slot.getHasStack()) {
                    ItemStack stack = slot.getStack().copy();
                    stack.setCount(dragType == 0 ? stack.getCount() - 1
                            : Math.min(slot.getSlotStackLimit(), stack.getCount() + 1));
                    slot.putStack(stack.getCount() > 0 ? stack : ItemStack.EMPTY);
                }
                return ItemStack.EMPTY;
            }
        }
        return super.slotClick(slotId, dragType, clickType, player);
    }
}
