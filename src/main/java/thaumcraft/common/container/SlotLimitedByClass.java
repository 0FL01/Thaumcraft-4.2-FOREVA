package thaumcraft.common.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotLimitedByClass extends Slot {
    private final Class<?> validClass;
    private final int limit;

    public SlotLimitedByClass(Class<?> validClass, IInventory inventoryIn, int index, int xPosition, int yPosition) {
        this(validClass, inventoryIn, index, xPosition, yPosition, 64);
    }

    public SlotLimitedByClass(Class<?> validClass, IInventory inventoryIn, int index, int xPosition, int yPosition, int limit) {
        super(inventoryIn, index, xPosition, yPosition);
        this.validClass = validClass;
        this.limit = limit;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return !stack.isEmpty() && this.validClass.isAssignableFrom(stack.getItem().getClass());
    }

    @Override
    public int getSlotStackLimit() {
        return this.limit;
    }
}
