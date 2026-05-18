package thaumcraft.common.config.recipes;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;

public final class ConfigRecipesSmeltingSlice {
    private ConfigRecipesSmeltingSlice() {
    }

    public static void initializeSmeltingBonusBaseline() {
        ThaumcraftApi.addSmeltingBonus("oreGold", new ItemStack(Items.GOLD_NUGGET));
        ThaumcraftApi.addSmeltingBonus("oreIron", new ItemStack(ConfigItems.itemNugget, 1, 0));
        ThaumcraftApi.addSmeltingBonus("oreCinnabar", new ItemStack(ConfigItems.itemNugget, 1, 5));
        ThaumcraftApi.addSmeltingBonus("oreCopper", new ItemStack(ConfigItems.itemNugget, 1, 1));
        ThaumcraftApi.addSmeltingBonus("oreTin", new ItemStack(ConfigItems.itemNugget, 1, 2));
        ThaumcraftApi.addSmeltingBonus("oreSilver", new ItemStack(ConfigItems.itemNugget, 1, 3));
        ThaumcraftApi.addSmeltingBonus("oreLead", new ItemStack(ConfigItems.itemNugget, 1, 4));

        ThaumcraftApi.addSmeltingBonus(new ItemStack(ConfigItems.itemNugget, 1, 31), new ItemStack(Items.GOLD_NUGGET));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ConfigItems.itemNugget, 1, 16), new ItemStack(ConfigItems.itemNugget, 1, 0));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ConfigItems.itemNugget, 1, 21), new ItemStack(ConfigItems.itemNugget, 1, 5));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ConfigItems.itemNugget, 1, 17), new ItemStack(ConfigItems.itemNugget, 1, 1));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ConfigItems.itemNugget, 1, 18), new ItemStack(ConfigItems.itemNugget, 1, 2));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ConfigItems.itemNugget, 1, 19), new ItemStack(ConfigItems.itemNugget, 1, 3));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(ConfigItems.itemNugget, 1, 20), new ItemStack(ConfigItems.itemNugget, 1, 4));

        ThaumcraftApi.addSmeltingBonus(new ItemStack(Items.CHICKEN), new ItemStack(ConfigItems.itemNuggetEdible, 1, 0));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(Items.BEEF), new ItemStack(ConfigItems.itemNuggetEdible, 1, 1));
        ThaumcraftApi.addSmeltingBonus(new ItemStack(Items.PORKCHOP), new ItemStack(ConfigItems.itemNuggetEdible, 1, 2));
        ThaumcraftApi.addSmeltingBonus(
                new ItemStack(Items.FISH, 1, OreDictionary.WILDCARD_VALUE),
                new ItemStack(ConfigItems.itemNuggetEdible, 1, 3));
    }

    public static void initializeSmeltingBaseline() {
        FurnaceRecipes.instance().addSmeltingRecipe(
                new ItemStack(ConfigBlocks.blockCustomOre, 1, 0),
                new ItemStack(ConfigItems.itemResource, 1, 3),
                1.0F);
        FurnaceRecipes.instance().addSmeltingRecipe(
                new ItemStack(ConfigBlocks.blockCustomOre, 1, 7),
                new ItemStack(ConfigItems.itemResource, 1, 6),
                1.0F);
        FurnaceRecipes.instance().addSmeltingRecipeForBlock(
                ConfigBlocks.blockMagicalLog,
                new ItemStack(Items.COAL, 1, 1),
                0.5F);
        FurnaceRecipes.instance().addSmeltingRecipe(
                new ItemStack(ConfigItems.itemNugget, 1, 16),
                new ItemStack(Items.IRON_INGOT, 2, 0),
                1.0F);
        FurnaceRecipes.instance().addSmeltingRecipe(
                new ItemStack(ConfigItems.itemNugget, 1, 21),
                new ItemStack(ConfigItems.itemResource, 2, 3),
                1.0F);
        FurnaceRecipes.instance().addSmeltingRecipe(
                new ItemStack(ConfigItems.itemNugget, 1, 31),
                new ItemStack(Items.GOLD_INGOT, 2, 0),
                1.0F);
        FurnaceRecipes.instance().addSmeltingRecipe(
                new ItemStack(ConfigItems.itemShard, 1, 6),
                new ItemStack(ConfigItems.itemResource, 1, 14),
                1.0F);
        FurnaceRecipes.instance().addSmeltingRecipe(
                new ItemStack(ConfigItems.itemResource, 1, 18),
                new ItemStack(Items.GOLD_NUGGET),
                0.0F);
    }
}
