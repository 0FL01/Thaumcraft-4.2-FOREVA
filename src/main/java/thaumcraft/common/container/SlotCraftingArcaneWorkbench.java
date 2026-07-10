package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.Collections;

public class SlotCraftingArcaneWorkbench extends Slot {
    private final EntityPlayer thePlayer;
    private final IInventory craftMatrix;
    private final ContainerArcaneWorkbench container;
    private int amountCrafted;

    public SlotCraftingArcaneWorkbench(EntityPlayer player, IInventory craftMatrix, IInventory resultInventory,
                                       ContainerArcaneWorkbench container, int index, int x, int y) {
        super(resultInventory, index, x, y);
        this.thePlayer = player;
        this.craftMatrix = craftMatrix;
        this.container = container;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return this.container != null && this.container.canTakeResult(playerIn, this.getStack());
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
        InventoryCraftResult result = (InventoryCraftResult) this.inventory;
        IRecipe recipe = result.getRecipeUsed();
        if (recipe != null && !recipe.isDynamic()) {
            this.thePlayer.unlockRecipes(Collections.singletonList(recipe));
            result.setRecipeUsed(null);
        }
    }

    @Override
    public ItemStack onTake(EntityPlayer player, ItemStack stack) {
        ContainerArcaneWorkbench.CraftTransaction transaction = this.container == null
                ? null : this.container.prepareCraft(player, stack);
        if (transaction == null) {
            stack.setCount(0);
            this.amountCrafted = 0;
            return ItemStack.EMPTY;
        }
        this.onCrafting(stack);
        this.container.finishCraft(transaction);
        return stack;
    }
}
