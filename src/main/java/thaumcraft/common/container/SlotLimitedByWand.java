package thaumcraft.common.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.wands.ItemWandCasting;

public class SlotLimitedByWand extends Slot {
    private final int limit;

    public SlotLimitedByWand(IInventory inventory, int index, int x, int y) {
        this(inventory, index, x, y, 64);
    }

    public SlotLimitedByWand(IInventory inventory, int index, int x, int y, int limit) {
        super(inventory, index, x, y);
        this.limit = limit;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return !stack.isEmpty()
                && stack.getItem() instanceof ItemWandCasting
                && !((ItemWandCasting) stack.getItem()).isStaff(stack);
    }

    @Override
    public int getSlotStackLimit() {
        return this.limit;
    }
}
