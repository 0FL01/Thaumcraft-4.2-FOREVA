package thaumcraft.common.container;

import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.tiles.TileThaumatorium;

public class ContainerThaumatorium extends Container {
    private final TileThaumatorium thaumatorium;
    private final EntityPlayer player;
    public final ArrayList<CrucibleRecipe> recipes = new ArrayList<CrucibleRecipe>();

    public ContainerThaumatorium() {
        this(null, null);
    }

    public ContainerThaumatorium(InventoryPlayer playerInventory, TileThaumatorium thaumatorium) {
        this.thaumatorium = thaumatorium;
        this.player = playerInventory == null ? null : playerInventory.player;
        if (this.thaumatorium != null) {
            this.thaumatorium.eventHandler = this;
            this.addSlotToContainer(new Slot(this.thaumatorium, 0, 48, 16));
        }
        if (playerInventory != null) {
            for (int row = 0; row < 3; ++row) {
                for (int col = 0; col < 9; ++col) {
                    this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
                }
            }
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(playerInventory, col, 8 + col * 18, 142));
            }
        }
        if (this.thaumatorium != null) this.onCraftMatrixChanged(this.thaumatorium);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return isUsableTile(playerIn, this.thaumatorium);
    }

    private static boolean isUsableTile(EntityPlayer player, TileEntity tile) {
        if (player == null || tile == null || tile.getWorld() == null || tile.isInvalid()) return false;
        BlockPos pos = tile.getPos();
        return tile.getWorld().getTileEntity(pos) == tile
                && player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        super.onCraftMatrixChanged(inventoryIn);
        this.updateRecipes();
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (this.thaumatorium != null && this.thaumatorium.getWorld() != null && !this.thaumatorium.getWorld().isRemote) {
            this.thaumatorium.eventHandler = null;
        }
    }

    public void updateRecipes() {
        this.recipes.clear();
        if (this.thaumatorium == null || this.player == null) return;
        for (Object recipeObject : ThaumcraftApi.getCraftingRecipes()) {
            if (!(recipeObject instanceof CrucibleRecipe)) continue;
            CrucibleRecipe recipe = (CrucibleRecipe) recipeObject;
            if (ResearchManager.isResearchComplete(this.player.getName(), recipe.key)
                    && recipe.catalystMatches(this.thaumatorium.inputStack)) {
                this.recipes.add(recipe);
                continue;
            }
            if (this.thaumatorium.recipeHash.contains(recipe.hash)) {
                this.recipes.add(recipe);
            }
        }
    }

    @Override
    public boolean enchantItem(EntityPlayer playerIn, int id) {
        if (this.thaumatorium == null || id < 0 || id >= this.recipes.size()) return false;
        CrucibleRecipe recipe = this.recipes.get(id);
        boolean found = false;
        for (int i = 0; i < this.thaumatorium.recipeHash.size(); ++i) {
            if (this.thaumatorium.recipeHash.get(i) != recipe.hash) continue;
            this.thaumatorium.recipeHash.remove(i);
            if (i < this.thaumatorium.recipeEssentia.size()) this.thaumatorium.recipeEssentia.remove(i);
            if (i < this.thaumatorium.recipePlayer.size()) this.thaumatorium.recipePlayer.remove(i);
            this.thaumatorium.currentCraft = -1;
            found = true;
            break;
        }
        if (!found) {
            this.thaumatorium.recipeEssentia.add(recipe.aspects.copy());
            this.thaumatorium.recipePlayer.add(playerIn.getName());
            this.thaumatorium.recipeHash.add(recipe.hash);
        }
        this.thaumatorium.markDirty();
        if (this.thaumatorium.getWorld() != null && !this.thaumatorium.getWorld().isRemote) {
            this.thaumatorium.getWorld().notifyBlockUpdate(this.thaumatorium.getPos(),
                    this.thaumatorium.getWorld().getBlockState(this.thaumatorium.getPos()),
                    this.thaumatorium.getWorld().getBlockState(this.thaumatorium.getPos()), 3);
        }
        this.updateRecipes();
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = index >= 0 && index < this.inventorySlots.size() ? this.inventorySlots.get(index) : null;
        if (slot == null || !slot.getHasStack()) return ItemStack.EMPTY;
        ItemStack stack = slot.getStack();
        original = stack.copy();
        if (index == 0) {
            if (!this.mergeItemStack(stack, 1, this.inventorySlots.size(), true)) return ItemStack.EMPTY;
        } else if (!this.mergeItemStack(stack, 0, 1, false)) {
            if (index >= 1 && index < 28) {
                if (!this.mergeItemStack(stack, 28, 37, false)) return ItemStack.EMPTY;
            } else if (index >= 28 && index < 37) {
                if (!this.mergeItemStack(stack, 1, 28, false)) return ItemStack.EMPTY;
            } else {
                return ItemStack.EMPTY;
            }
        }
        if (stack.isEmpty()) slot.putStack(ItemStack.EMPTY); else slot.onSlotChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(playerIn, stack);
        return original;
    }
}
