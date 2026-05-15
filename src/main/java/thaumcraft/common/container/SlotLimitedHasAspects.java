package thaumcraft.common.container;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

public class SlotLimitedHasAspects extends Slot {

    public SlotLimitedHasAspects(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if (stack.isEmpty()) return false;
        AspectList tags = ThaumcraftCraftingManager.getObjectTags(stack);
        tags = ThaumcraftCraftingManager.getBonusTags(stack, tags);
        return tags != null && tags.size() > 0;
    }
}
