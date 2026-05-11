package thaumcraft.api.research;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.InfusionEnchantmentRecipe;
import thaumcraft.api.crafting.InfusionRecipe;

public class ResearchPage {
    public PageType type = PageType.TEXT;
    public String text = null;
    public String research = null;
    public ResourceLocation image = null;
    public AspectList aspects = null;
    public Object recipe = null;
    public ItemStack recipeOutput = null;

    public ResearchPage(String text) {
        this.type = PageType.TEXT;
        this.text = text;
    }

    public ResearchPage(String research, String text) {
        this.type = PageType.TEXT_CONCEALED;
        this.research = research;
        this.text = text;
    }

    public ResearchPage(IRecipe recipe) {
        this.type = PageType.NORMAL_CRAFTING;
        this.recipe = recipe;
        this.recipeOutput = recipe.getRecipeOutput();
    }

    public ResearchPage(IRecipe[] recipe) {
        this.type = PageType.NORMAL_CRAFTING;
        this.recipe = recipe;
    }

    public ResearchPage(IArcaneRecipe[] recipe) {
        this.type = PageType.ARCANE_CRAFTING;
        this.recipe = recipe;
    }

    public ResearchPage(CrucibleRecipe[] recipe) {
        this.type = PageType.CRUCIBLE_CRAFTING;
        this.recipe = recipe;
    }

    public ResearchPage(InfusionRecipe[] recipe) {
        this.type = PageType.INFUSION_CRAFTING;
        this.recipe = recipe;
    }

    public ResearchPage(List recipe) {
        this.type = PageType.COMPOUND_CRAFTING;
        this.recipe = recipe;
    }

    public ResearchPage(IArcaneRecipe recipe) {
        this.type = PageType.ARCANE_CRAFTING;
        this.recipe = recipe;
        this.recipeOutput = recipe.getRecipeOutput();
    }

    public ResearchPage(CrucibleRecipe recipe) {
        this.type = PageType.CRUCIBLE_CRAFTING;
        this.recipe = recipe;
        this.recipeOutput = recipe.getRecipeOutput();
    }

    public ResearchPage(ItemStack input) {
        this.type = PageType.SMELTING;
        this.recipe = input;
        this.recipeOutput = FurnaceRecipes.instance().getSmeltingResult(input);
    }

    public ResearchPage(InfusionRecipe recipe) {
        this.type = PageType.INFUSION_CRAFTING;
        this.recipe = recipe;
        this.recipeOutput = recipe.getRecipeOutput() instanceof ItemStack ? (ItemStack)recipe.getRecipeOutput() : recipe.getRecipeInput();
    }

    public ResearchPage(InfusionEnchantmentRecipe recipe) {
        this.type = PageType.INFUSION_ENCHANTMENT;
        this.recipe = recipe;
    }

    public ResearchPage(ResourceLocation image, String caption) {
        this.type = PageType.IMAGE;
        this.image = image;
        this.text = caption;
    }

    public ResearchPage(AspectList as) {
        this.type = PageType.ASPECTS;
        this.aspects = as;
    }

    public String getTranslatedText() {
        String ret = "";
        if (this.text != null && (ret = I18n.translateToLocal((String)this.text)).isEmpty()) {
            ret = this.text;
        }
        return ret;
    }

    public static enum PageType {
        TEXT,
        TEXT_CONCEALED,
        IMAGE,
        CRUCIBLE_CRAFTING,
        ARCANE_CRAFTING,
        ASPECTS,
        NORMAL_CRAFTING,
        INFUSION_CRAFTING,
        COMPOUND_CRAFTING,
        INFUSION_ENCHANTMENT,
        SMELTING;

    }
}
