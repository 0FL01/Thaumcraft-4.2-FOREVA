package thaumcraft.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.items.wands.ItemWandCasting;

final class ArcaneWorkbenchRecipeResolver {
    private ArcaneWorkbenchRecipeResolver() {
    }

    static Resolution resolve(IInventory workbench, InventoryCrafting matrix, EntityPlayer player) {
        if (workbench == null || matrix == null || player == null || player.world == null) {
            return Resolution.NONE;
        }

        IRecipe vanilla = CraftingManager.findMatchingRecipe(matrix, player.world);
        if (vanilla != null) {
            if (!isRecipeAllowed(vanilla, player)) {
                return Resolution.NONE;
            }
            ItemStack output = normalize(vanilla.getCraftingResult(matrix));
            if (!output.isEmpty()) {
                return Resolution.vanilla(vanilla, output);
            }
        }

        for (Object candidate : ThaumcraftApi.getCraftingRecipes()) {
            if (!(candidate instanceof IArcaneRecipe)) continue;
            IArcaneRecipe recipe = (IArcaneRecipe) candidate;
            if (!recipe.matches(workbench, player.world, player)) continue;

            ItemStack output = normalize(recipe.getCraftingResult(workbench));
            AspectList cost = recipe.getAspects() != null ? recipe.getAspects() : recipe.getAspects(workbench);
            if (output.isEmpty() || cost == null || cost.size() <= 0) {
                return Resolution.NONE;
            }

            ItemStack wandStack = workbench.getStackInSlot(10);
            boolean craftable = isValidWand(wandStack)
                    && ((ItemWandCasting) wandStack.getItem()).consumeAllVisCrafting(wandStack, player, cost, false);
            return Resolution.arcane(recipe, output, cost, craftable);
        }
        return Resolution.NONE;
    }

    private static boolean isRecipeAllowed(IRecipe recipe, EntityPlayer player) {
        return recipe.isDynamic()
                || !player.world.getGameRules().getBoolean("doLimitedCrafting")
                || !(player instanceof EntityPlayerMP)
                || ((EntityPlayerMP) player).getRecipeBook().isUnlocked(recipe);
    }

    static boolean isValidWand(ItemStack stack) {
        return !stack.isEmpty()
                && stack.getItem() instanceof ItemWandCasting
                && !((ItemWandCasting) stack.getItem()).isStaff(stack);
    }

    private static ItemStack normalize(ItemStack stack) {
        return stack == null ? ItemStack.EMPTY : stack;
    }

    static final class Resolution {
        static final Resolution NONE = new Resolution(null, null, ItemStack.EMPTY, new AspectList(), false);

        final IRecipe vanillaRecipe;
        final IArcaneRecipe arcaneRecipe;
        final ItemStack output;
        final AspectList cost;
        final boolean craftable;

        private Resolution(IRecipe vanillaRecipe, IArcaneRecipe arcaneRecipe, ItemStack output, AspectList cost, boolean craftable) {
            this.vanillaRecipe = vanillaRecipe;
            this.arcaneRecipe = arcaneRecipe;
            this.output = output;
            this.cost = cost;
            this.craftable = craftable;
        }

        static Resolution vanilla(IRecipe recipe, ItemStack output) {
            return new Resolution(recipe, null, output, new AspectList(), true);
        }

        static Resolution arcane(IArcaneRecipe recipe, ItemStack output, AspectList cost, boolean craftable) {
            return new Resolution(null, recipe, output, cost, craftable);
        }

        boolean isVanilla() {
            return this.vanillaRecipe != null;
        }

        boolean isArcane() {
            return this.arcaneRecipe != null;
        }
    }
}
