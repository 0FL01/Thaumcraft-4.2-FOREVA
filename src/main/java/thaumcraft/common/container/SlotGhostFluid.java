package thaumcraft.common.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidUtil;

public class SlotGhostFluid extends SlotGhost {
    public SlotGhostFluid(IInventory inventory, int index, int xPosition, int yPosition) {
        super(inventory, index, xPosition, yPosition, 1);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return super.isItemValid(stack)
                && stack.getItem() != Items.BUCKET
                && FluidUtil.getFluidContained(stack) != null;
    }
}
