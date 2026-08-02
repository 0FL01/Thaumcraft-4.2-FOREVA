package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotGhost extends Slot {
    private final int limit;

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
    public boolean canTakeStack(EntityPlayer player) {
        return false;
    }
}
