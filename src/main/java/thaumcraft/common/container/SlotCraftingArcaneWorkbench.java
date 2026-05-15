package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

public class SlotCraftingArcaneWorkbench extends Slot {
    private final EntityPlayer thePlayer;
    private final IInventory craftMatrix;

    public SlotCraftingArcaneWorkbench(EntityPlayer player, IInventory craftMatrix, IInventory resultInventory, int index, int x, int y) {
        super(resultInventory, index, x, y);
        this.thePlayer = player;
        this.craftMatrix = craftMatrix;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack onTake(EntityPlayer player, ItemStack stack) {
        FMLCommonHandler.instance().firePlayerCraftingEvent(this.thePlayer, stack, this.craftMatrix);

        AspectList cost = ThaumcraftCraftingManager.findMatchingArcaneRecipeAspects(this.craftMatrix, this.thePlayer);
        ItemStack wandStack = this.craftMatrix.getStackInSlot(10);
        if (cost.size() > 0 && !wandStack.isEmpty() && wandStack.getItem() instanceof ItemWandCasting) {
            ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
            wand.consumeAllVisCrafting(wandStack, player, cost, true);
        }

        for (int i = 0; i < 9; i++) {
            ItemStack input = this.craftMatrix.getStackInSlot(i);
            if (input.isEmpty()) continue;
            this.craftMatrix.decrStackSize(i, 1);
            ItemStack remainder = input.getItem().hasContainerItem(input) ? input.getItem().getContainerItem(input) : ItemStack.EMPTY;
            if (remainder.isEmpty()) continue;

            if (this.craftMatrix.getStackInSlot(i).isEmpty()) {
                this.craftMatrix.setInventorySlotContents(i, remainder);
            } else if (!this.thePlayer.inventory.addItemStackToInventory(remainder.copy())) {
                this.thePlayer.dropItem(remainder, false);
            }
        }

        return stack;
    }
}
