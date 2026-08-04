package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotGhost extends Slot {
    private final int limit;
    private boolean networkSyncView;

    public SlotGhost(IInventory inventory, int index, int xPosition, int yPosition) {
        this(inventory, index, xPosition, yPosition, 256);
    }

    public SlotGhost(IInventory inventory, int index, int xPosition, int yPosition, int limit) {
        super(inventory, index, xPosition, yPosition);
        this.limit = limit;
    }

    @Override
    public int getSlotStackLimit() {
        return this.limit;
    }

    @Override
    public net.minecraft.item.ItemStack getStack() {
        net.minecraft.item.ItemStack stack = super.getStack();
        if (!this.networkSyncView || stack.isEmpty() || stack.getCount() <= Byte.MAX_VALUE) {
            return stack;
        }
        net.minecraft.item.ItemStack safe = stack.copy();
        safe.setCount(Byte.MAX_VALUE);
        return safe;
    }

    public void setNetworkSyncView(boolean networkSyncView) {
        this.networkSyncView = networkSyncView;
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        return false;
    }
}
