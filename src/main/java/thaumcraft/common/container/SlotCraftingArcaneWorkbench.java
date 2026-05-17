package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

public class SlotCraftingArcaneWorkbench extends Slot {
    private final EntityPlayer thePlayer;
    private final IInventory craftMatrix;
    private int amountCrafted;

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
    public ItemStack decrStackSize(int amount) {
        if (this.getHasStack()) {
            this.amountCrafted += Math.min(amount, this.getStack().getCount());
        }
        return super.decrStackSize(amount);
    }

    @Override
    protected void onSwapCraft(int numItemsCrafted) {
        this.amountCrafted += numItemsCrafted;
    }

    @Override
    protected void onCrafting(ItemStack stack, int amount) {
        this.amountCrafted += amount;
        this.onCrafting(stack);
    }

    @Override
    protected void onCrafting(ItemStack stack) {
        if (this.amountCrafted > 0) {
            stack.onCrafting(this.thePlayer.world, this.thePlayer, this.amountCrafted);
            FMLCommonHandler.instance().firePlayerCraftingEvent(this.thePlayer, stack, this.craftMatrix);
        }
        this.amountCrafted = 0;
    }

    @Override
    public ItemStack onTake(EntityPlayer player, ItemStack stack) {
        this.onCrafting(stack);

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
            ItemStack remainder = ForgeHooks.getContainerItem(input);
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
