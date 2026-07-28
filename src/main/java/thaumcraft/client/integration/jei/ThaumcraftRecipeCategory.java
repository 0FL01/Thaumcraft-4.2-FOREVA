package thaumcraft.client.integration.jei;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;

final class ThaumcraftRecipeCategory<T extends ThaumcraftRecipeWrapper> implements IRecipeCategory<T> {
    private final String uid;
    private final String titleKey;
    private final IDrawable background;

    ThaumcraftRecipeCategory(String uid, String titleKey, IDrawable background) {
        this.uid = uid;
        this.titleKey = titleKey;
        this.background = background;
    }

    @Override
    public String getUid() {
        return this.uid;
    }

    @Override
    public String getTitle() {
        return I18n.format(this.titleKey);
    }

    @Override
    public String getModName() {
        return "Thaumcraft";
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, T recipeWrapper, IIngredients ingredients) {
        recipeWrapper.setRecipe(recipeLayout);
    }
}
