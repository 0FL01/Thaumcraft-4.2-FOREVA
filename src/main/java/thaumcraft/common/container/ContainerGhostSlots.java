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
                // Ghost slot: only allow putting items in, never taking out
                ItemStack held = player.inventory.getItemStack();
                if (!held.isEmpty()) {
                    // Put a copy of held item into ghost slot
                    ItemStack copy = held.copy();
                    copy.setCount(1);
                    slot.putStack(copy);
                } else if (clickType == ClickType.QUICK_MOVE && slot.getHasStack()) {
                    // Shift-click on ghost: no-op
                } else {
                    // Right-click on empty ghost with empty hand: clear
                    slot.putStack(ItemStack.EMPTY);
                }
                return ItemStack.EMPTY;
            }
        }
        return super.slotClick(slotId, dragType, clickType, player);
    }
}
