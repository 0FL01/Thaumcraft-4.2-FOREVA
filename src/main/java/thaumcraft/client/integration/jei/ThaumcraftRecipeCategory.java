package thaumcraft.client.integration.jei;

import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

final class ThaumcraftRecipeCategory<T extends ThaumcraftRecipeWrapper> implements IRecipeCategory<T> {
    private final String uid;
    private final String titleKey;
    private final IDrawable background;
    private final IDrawable decoration;
    private final int decorationX;
    private final int decorationY;

    ThaumcraftRecipeCategory(String uid, String titleKey, IDrawable background,
                             IDrawable decoration, int decorationX, int decorationY) {
        this.uid = uid;
        this.titleKey = titleKey;
        this.background = background;
        this.decoration = decoration;
        this.decorationX = decorationX;
        this.decorationY = decorationY;
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
    public void drawExtras(Minecraft minecraft) {
        this.decoration.draw(minecraft, this.decorationX, this.decorationY);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, T recipeWrapper, IIngredients ingredients) {
        recipeWrapper.setRecipe(recipeLayout);
    }
}
