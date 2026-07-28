package thaumcraft.client.integration.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.lib.UtilsFX;

abstract class ThaumcraftRecipeWrapper implements IRecipeWrapper {
    private static final int ASPECT_COLUMNS = 8;

    final AspectList aspects;
    final String research;
    private final int aspectY;
    final IDrawable slotDrawable;

    ThaumcraftRecipeWrapper(JeiRecipeData.Recipe recipe, IDrawable slotDrawable, int aspectY) {
        this.aspects = recipe.aspects;
        this.research = recipe.research;
        this.slotDrawable = slotDrawable;
        this.aspectY = aspectY;
    }

    abstract void setRecipe(IRecipeLayout layout);

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        Aspect[] tags = this.getAspects();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GlStateManager.pushMatrix();
        try {
            for (int i = 0; i < tags.length; i++) {
                int x = aspectX(i, tags.length);
                int y = this.aspectY + (i / ASPECT_COLUMNS) * 18;
                UtilsFX.drawTag(x, y, tags[i], this.aspects.getAmount(tags[i]), 0, 0.0D, 771, 1.0F, false);
            }
        } finally {
            GlStateManager.popMatrix();
            GL11.glPopAttrib();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        Aspect[] tags = this.getAspects();
        for (int i = 0; i < tags.length; i++) {
            int x = aspectX(i, tags.length);
            int y = this.aspectY + (i / ASPECT_COLUMNS) * 18;
            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                return Collections.singletonList(tags[i].getName() + " " + this.aspects.getAmount(tags[i]));
            }
        }
        return Collections.emptyList();
    }

    private Aspect[] getAspects() {
        Aspect[] source = this.aspects.getAspectsSorted();
        List<Aspect> result = new ArrayList<Aspect>(source.length);
        for (Aspect aspect : source) {
            if (aspect != null) {
                result.add(aspect);
            }
        }
        return result.toArray(new Aspect[0]);
    }

    private static int aspectX(int index, int count) {
        int rowCount = Math.min(ASPECT_COLUMNS, count - index / ASPECT_COLUMNS * ASPECT_COLUMNS);
        int rowStart = (160 - rowCount * 18) / 2;
        return rowStart + index % ASPECT_COLUMNS * 18;
    }

    static final class Arcane extends ThaumcraftRecipeWrapper {
        private static final int OUTPUT_SLOT = 9;
        private final JeiRecipeData.Arcane recipe;

        Arcane(JeiRecipeData.Arcane recipe, IDrawable slotDrawable) {
            super(recipe, slotDrawable, 78);
            this.recipe = recipe;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            ingredients.setInputLists(VanillaTypes.ITEM, this.recipe.inputs);
            ingredients.setOutput(VanillaTypes.ITEM, this.recipe.output);
        }

        @Override
        void setRecipe(IRecipeLayout layout) {
            IGuiItemStackGroup stacks = layout.getItemStacks();
            for (int i = 0; i < this.recipe.inputs.size(); i++) {
                int column = this.recipe.shapeless ? i % 3 : i % this.recipe.width;
                int row = this.recipe.shapeless ? i / 3 : i / this.recipe.width;
                int xOffset = this.recipe.shapeless ? 0 : (3 - this.recipe.width) * 9;
                int yOffset = this.recipe.shapeless ? 0 : (3 - this.recipe.height) * 9;
                stacks.init(i, true, 3 + xOffset + column * 18, 10 + yOffset + row * 18);
                stacks.setBackground(i, this.slotDrawable);
                stacks.set(i, this.recipe.inputs.get(i));
            }
            stacks.init(OUTPUT_SLOT, false, 109, 28);
            stacks.setBackground(OUTPUT_SLOT, this.slotDrawable);
            stacks.set(OUTPUT_SLOT, this.recipe.output);
            if (this.recipe.shapeless) {
                layout.setShapeless();
            }
        }
    }

    static final class Crucible extends ThaumcraftRecipeWrapper {
        private final JeiRecipeData.Crucible recipe;

        Crucible(JeiRecipeData.Crucible recipe, IDrawable slotDrawable) {
            super(recipe, slotDrawable, 70);
            this.recipe = recipe;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(this.recipe.catalyst));
            ingredients.setOutput(VanillaTypes.ITEM, this.recipe.output);
        }

        @Override
        void setRecipe(IRecipeLayout layout) {
            IGuiItemStackGroup stacks = layout.getItemStacks();
            stacks.init(0, true, 29, 28);
            stacks.setBackground(0, this.slotDrawable);
            stacks.set(0, this.recipe.catalyst);
            stacks.init(1, false, 113, 28);
            stacks.setBackground(1, this.slotDrawable);
            stacks.set(1, this.recipe.output);
        }
    }

    static final class Infusion extends ThaumcraftRecipeWrapper {
        private final JeiRecipeData.Infusion recipe;

        Infusion(JeiRecipeData.Infusion recipe, IDrawable slotDrawable) {
            super(recipe, slotDrawable, 106);
            this.recipe = recipe;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            List<List<ItemStack>> inputs = new ArrayList<List<ItemStack>>(this.recipe.components.size() + 1);
            inputs.add(this.recipe.central);
            inputs.addAll(this.recipe.components);
            ingredients.setInputLists(VanillaTypes.ITEM, inputs);
            ingredients.setOutputLists(VanillaTypes.ITEM, Collections.singletonList(this.recipe.outputs));
        }

        @Override
        void setRecipe(IRecipeLayout layout) {
            IGuiItemStackGroup stacks = layout.getItemStacks();
            stacks.init(0, true, 72, 48);
            stacks.setBackground(0, this.slotDrawable);
            stacks.set(0, this.recipe.central);

            int count = this.recipe.components.size();
            for (int i = 0; i < count; i++) {
                double angle = Math.PI * 2.0D * i / Math.max(1, count);
                int x = 72 + (int) Math.round(Math.cos(angle) * 55.0D);
                int y = 48 + (int) Math.round(Math.sin(angle) * 30.0D);
                int slot = i + 1;
                stacks.init(slot, true, x, y);
                stacks.setBackground(slot, this.slotDrawable);
                stacks.set(slot, this.recipe.components.get(i));
            }

            int outputSlot = count + 1;
            stacks.init(outputSlot, false, 72, 2);
            stacks.setBackground(outputSlot, this.slotDrawable);
            stacks.set(outputSlot, this.recipe.outputs);
        }

        @Override
        public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
            super.drawInfo(minecraft, recipeWidth, recipeHeight, mouseX, mouseY);
            int tier = Math.min(5, Math.max(0, this.recipe.instability / 2));
            String text = I18n.format("tc.inst") + " " + I18n.format("tc.inst." + tier);
            minecraft.fontRenderer.drawString(text, (160 - minecraft.fontRenderer.getStringWidth(text)) / 2, 91, 0x505050);
        }
    }
}
