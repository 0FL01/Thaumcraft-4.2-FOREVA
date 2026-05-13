package thaumcraft.common.lib.crafting;

import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionEnchantmentRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.common.lib.research.ResearchManager;

public class ThaumcraftCraftingManager {

    public static AspectList getObjectTags(ItemStack is) {
        return ThaumcraftApiHelper.getObjectAspects(is);
    }

    public static AspectList getBonusTags(ItemStack is, AspectList ot) {
        return ThaumcraftApiHelper.getBonusObjectTags(is, ot);
    }

    public static AspectList generateTags(Item item, int meta) {
        return ThaumcraftApiHelper.generateTags(item, meta);
    }

    public static InfusionRecipe findMatchingInfusionRecipe(ArrayList<ItemStack> items, ItemStack input, EntityPlayer player) {
        if (items == null || input == null || input.isEmpty() || player == null) return null;
        for (Object recipe : ThaumcraftApi.getCraftingRecipes()) {
            if (!(recipe instanceof InfusionRecipe)) continue;
            InfusionRecipe infusionRecipe = (InfusionRecipe) recipe;
            if (infusionRecipe.matches(items, input, player.world, player)) return infusionRecipe;
        }
        return null;
    }

    public static InfusionEnchantmentRecipe findMatchingInfusionEnchantmentRecipe(ArrayList<ItemStack> items, ItemStack input, EntityPlayer player) {
        if (items == null || input == null || input.isEmpty() || player == null) return null;
        for (Object recipe : ThaumcraftApi.getCraftingRecipes()) {
            if (!(recipe instanceof InfusionEnchantmentRecipe)) continue;
            InfusionEnchantmentRecipe infusionRecipe = (InfusionEnchantmentRecipe) recipe;
            if (infusionRecipe.matches(items, input, player.world, player)) return infusionRecipe;
        }
        return null;
    }

    /**
     * Find the best matching crucible recipe for the given aspects and catalyst item.
     * Matches the original logic: creates a copy of lastDrop with stackSize=1,
     * checks research completion, matches aspects+catalyst, returns recipe with
     * most aspect types.
     */
    public static CrucibleRecipe findMatchingCrucibleRecipe(String username, AspectList aspects, ItemStack lastDrop) {
        int highest = 0;
        int index = -1;

        for (int a = 0; a < ThaumcraftApi.getCraftingRecipes().size(); ++a) {
            if (!(ThaumcraftApi.getCraftingRecipes().get(a) instanceof CrucibleRecipe)) continue;

            CrucibleRecipe recipe = (CrucibleRecipe) ThaumcraftApi.getCraftingRecipes().get(a);

            // Create a single-item copy of the catalyst stack for matching
            ItemStack temp = lastDrop.copy();
            temp.setCount(1);

            // Check research requirement
            if (!ResearchManager.isResearchComplete(username, recipe.key)) continue;

            // Check recipe match (aspects + catalyst)
            if (!recipe.matches(aspects, temp)) continue;

            // Prefer recipe with more aspect types
            int result = recipe.aspects.size();
            if (result <= highest) continue;

            highest = result;
            index = a;
        }

        if (index < 0) return null;
        return (CrucibleRecipe) ThaumcraftApi.getCraftingRecipes().get(index);
    }
}
