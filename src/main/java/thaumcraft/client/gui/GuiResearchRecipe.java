package thaumcraft.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.crafting.IShapedRecipe;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.InfusionEnchantmentRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.lib.utils.InventoryUtils;

public class GuiResearchRecipe extends GuiScreen {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/gui/gui_researchbook.png");
    private static final ResourceLocation OVERLAY = new ResourceLocation("thaumcraft", "textures/gui/gui_researchbook_overlay.png");
    private static final int PAGE_WIDTH = 139;
    private static final int LEFT_PAGE_OFFSET = -15;
    private static final int PAGE_STRIDE = 152;

    private final int paneWidth = 256;
    private final int paneHeight = 181;
    private final ResearchItem research;
    private final double guiMapX;
    private final double guiMapY;
    private final FontRenderer researchFontRenderer;
    private ResearchPage[] pages;
    private int page;
    private int maxPages;
    private long lastCycle;
    private int cycle = -1;
    private List<String> tooltip;
    private int tooltipX;
    private int tooltipY;

    public GuiResearchRecipe(ResearchItem research, int page, double guiMapX, double guiMapY) {
        this.research = research;
        this.guiMapX = guiMapX;
        this.guiMapY = guiMapY;
        Minecraft minecraft = Minecraft.getMinecraft();
        this.researchFontRenderer = new FontRenderer(minecraft.gameSettings,
                new ResourceLocation("minecraft", "textures/font/ascii.png"), minecraft.getTextureManager(), true);
        ResearchPage[] sourcePages = research == null || research.getPages() == null ? new ResearchPage[0] : research.getPages();
        List<ResearchPage> visiblePages = new ArrayList<ResearchPage>();
        EntityPlayer player = minecraft.player;
        for (ResearchPage visiblePage : Arrays.asList(sourcePages)) {
            if (visiblePage != null && visiblePage.type == ResearchPage.PageType.TEXT_CONCEALED
                    && player != null && !ThaumcraftApiHelper.isResearchComplete(player.getName(), visiblePage.research)) {
                continue;
            }
            visiblePages.add(visiblePage);
        }
        this.pages = visiblePages.toArray(new ResearchPage[0]);
        this.maxPages = this.pages.length;
        if ((page & 1) == 1) {
            --page;
        }
        this.page = Math.max(0, Math.min(page, Math.max(0, this.maxPages - 1)));
        if ((this.page & 1) == 1) {
            --this.page;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode() || keyCode == 1) {
            this.mc.displayGuiScreen(new GuiResearchBrowser(this.guiMapX, this.guiMapY));
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int left = (this.width - this.paneWidth) / 2;
        int top = (this.height - this.paneHeight) / 2;
        if (this.page > 0 && mouseX >= left - 16 && mouseX < left - 4 && mouseY >= top + 190 && mouseY < top + 198) {
            this.page = Math.max(0, this.page - 2);
            this.resetRecipeCycle();
            return;
        }
        if (this.page < this.maxPages - 2 && mouseX >= left + 262 && mouseX < left + 274 && mouseY >= top + 190 && mouseY < top + 198) {
            this.page = Math.min(Math.max(0, this.maxPages - 1), this.page + 2);
            if ((this.page & 1) == 1) {
                --this.page;
            }
            this.resetRecipeCycle();
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        int left = (this.width - this.paneWidth) / 2;
        int top = (this.height - this.paneHeight) / 2;
        float scaledLeft = ((float) this.width - this.paneWidth * 1.3F) / 2.0F;
        float scaledTop = ((float) this.height - this.paneHeight * 1.3F) / 2.0F;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.translate(scaledLeft, scaledTop, 0.0F);
        GlStateManager.enableBlend();
        GlStateManager.scale(1.3F, 1.3F, 1.0F);
        this.drawTexturedModalRect(0, 0, 0, 0, this.paneWidth, this.paneHeight);
        GlStateManager.popMatrix();

        this.tooltip = null;
        this.updateRecipeCycle();
        if (this.pages.length > 0) {
            for (int side = 0; side < 2 && this.page + side < this.pages.length; ++side) {
                this.drawPage(this.pages[this.page + side], side, left, top, mouseX, mouseY);
            }
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        if (this.page > 0) {
            this.drawTexturedModalRect(left - 16, top + 190, 0, 184, 12, 8);
        }
        if (this.page < this.maxPages - 2) {
            this.drawTexturedModalRect(left + 262, top + 190, 12, 184, 12, 8);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.tooltip != null && !this.tooltip.isEmpty()) {
            this.drawHoveringText(this.tooltip, this.tooltipX, this.tooltipY);
        }
    }

    private void drawPage(ResearchPage page, int side, int x, int y, int mouseX, int mouseY) {
        if (page == null) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
        if (this.page == 0 && side == 0 && this.research != null) {
            this.drawResearchTitle(x, y);
            y += 25;
        }

        switch (page.type) {
            case TEXT:
            case TEXT_CONCEALED:
                this.drawTextPage(side, x, y - 10, page.getTranslatedText());
                break;
            case IMAGE:
                this.drawImagePage(side, x, y - 10, page);
                break;
            case ASPECTS:
                this.drawAspectPage(side, x - 8, y - 8, mouseX, mouseY, page.aspects);
                break;
            case CRUCIBLE_CRAFTING:
                this.drawCruciblePage(side, x - 4, y - 8, mouseX, mouseY, page);
                break;
            case ARCANE_CRAFTING:
                this.drawArcaneCraftingPage(side, x - 4, y - 8, mouseX, mouseY, page);
                break;
            case COMPOUND_CRAFTING:
                this.drawCompoundCraftingPage(side, x - 4, y - 8, mouseX, mouseY, page);
                break;
            case INFUSION_CRAFTING:
                this.drawInfusionPage(side, x - 4, y - 8, mouseX, mouseY, page);
                break;
            case NORMAL_CRAFTING:
                this.drawCraftingPage(side, x - 4, y - 8, mouseX, mouseY, page);
                break;
            case SMELTING:
                this.drawSmeltingPage(side, x - 4, y - 8, mouseX, mouseY, page);
                break;
            case INFUSION_ENCHANTMENT:
                this.drawInfusionEnchantingPage(side, x - 4, y - 8, mouseX, mouseY, page);
                break;
            default:
                break;
        }
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.popMatrix();
    }

    private void drawResearchTitle(int x, int y) {
        this.mc.getTextureManager().bindTexture(TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(x + 4, y - 13, 24, 184, 96, 4);
        this.drawTexturedModalRect(x + 4, y + 4, 24, 184, 96, 4);
        this.drawCenteredScaledString(this.research.getName(), x + 52, y - 6, 130, 0x303030);
    }

    private void drawTextPage(int side, int x, int y, String text) {
        this.drawMarkupText(text == null ? "" : text, x + LEFT_PAGE_OFFSET + side * PAGE_STRIDE, y, PAGE_WIDTH);
    }

    private void drawImagePage(int side, int x, int y, ResearchPage page) {
        int start = side * PAGE_STRIDE;
        if (page.image != null) {
            this.drawInlineImage(page.image, x + start + 11, y + 8, 0, 0, 128, 128, 0.75F, false);
        }
        if (page.text != null && !page.text.isEmpty()) {
            this.drawMarkupText(page.getTranslatedText(), x + LEFT_PAGE_OFFSET + start, y + 112, PAGE_WIDTH);
        }
    }

    private void drawCraftingPage(int side, int x, int y, int mouseX, int mouseY, ResearchPage pageParm) {
        IRecipe recipe = this.selectCycled(pageParm.recipe, IRecipe.class);
        if (recipe == null) {
            return;
        }
        int start = side * PAGE_STRIDE;
        this.drawOverlayScaled(x + start, y, 2, 32, 60, 15, 52, 52, 2.0F);
        String label = recipe instanceof IShapedRecipe ? I18n.format("recipe.type.workbench") : I18n.format("recipe.type.workbenchshapeless");
        this.drawCenteredScaledString(label, x + start + 56, y, 112, 0x505050);

        ItemStack output = this.safeCopy(recipe.getRecipeOutput());
        this.drawItemStack(output, x + start + 48, y + 32);
        this.addItemTooltip(output, mouseX, mouseY, x + start + 48, y + 32);

        NonNullList<Ingredient> ingredients = recipe.getIngredients();
        int recipeWidth = recipe instanceof IShapedRecipe ? Math.max(1, ((IShapedRecipe) recipe).getRecipeWidth()) : 3;
        int recipeHeight = recipe instanceof IShapedRecipe ? Math.max(1, ((IShapedRecipe) recipe).getRecipeHeight()) : Math.max(1, (ingredients.size() + 2) / 3);
        int max = Math.min(ingredients.size(), recipeWidth * recipeHeight);
        for (int i = 0; i < max && i < 9; ++i) {
            Ingredient ingredient = ingredients.get(i);
            if (ingredient == null || ingredient == Ingredient.EMPTY) {
                continue;
            }
            int slotX = x + start + 16 + i % recipeWidth * 32;
            int slotY = y + 76 + i / recipeWidth * 32;
            ItemStack stack = this.displayStack(ingredient);
            this.drawItemStack(stack, slotX, slotY);
            this.addItemTooltip(stack, mouseX, mouseY, slotX, slotY);
        }
    }

    private void drawArcaneCraftingPage(int side, int x, int y, int mouseX, int mouseY, ResearchPage pageParm) {
        IArcaneRecipe recipe = this.selectCycled(pageParm.recipe, IArcaneRecipe.class);
        if (recipe == null) {
            return;
        }
        int start = side * PAGE_STRIDE;
        this.drawOverlayScaled(x + start, y, 2, 27, 112, 15, 52, 52, 2.0F);
        this.drawOverlayScaled(x + start, y, 20, 7, 20, 3, 16, 16, 2.0F);
        this.drawOverlayScaled(x + start, y + 164, 0, 0, 68, 76, 12, 12, 2.0F, 0.4F);
        this.drawCenteredScaledString(I18n.format("recipe.type.arcane"), x + start + 56, y, 112, 0x505050);

        ItemStack output = this.safeCopy(recipe.getRecipeOutput());
        this.drawItemStack(output, x + start + 48, y + 22);
        this.addItemTooltip(output, mouseX, mouseY, x + start + 48, y + 22);
        this.drawAspectCostRow(recipe.getAspects(), x + start + 14, y + 172, 5, mouseX, mouseY, 1);

        if (recipe instanceof ShapedArcaneRecipe) {
            ShapedArcaneRecipe shaped = (ShapedArcaneRecipe) recipe;
            Object[] items = shaped.getInput();
            this.drawObjectGrid(items, shaped.width, shaped.height, x + start + 16, y + 66, mouseX, mouseY);
        } else if (recipe instanceof ShapelessArcaneRecipe) {
            ArrayList items = ((ShapelessArcaneRecipe) recipe).getInput();
            this.drawObjectGrid(items == null ? null : items.toArray(), 3, items == null ? 0 : (items.size() + 2) / 3, x + start + 16, y + 66, mouseX, mouseY);
        }
    }

    private void drawCruciblePage(int side, int x, int y, int mouseX, int mouseY, ResearchPage pageParm) {
        CrucibleRecipe recipe = this.selectCycled(pageParm.recipe, CrucibleRecipe.class);
        if (recipe == null) {
            return;
        }
        int start = side * PAGE_STRIDE;
        this.drawCenteredScaledString(I18n.format("recipe.type.crucible"), x + start + 56, y, 112, 0x505050);
        this.drawOverlayScaled(x + start, y + 28, 0, 0, 0, 3, 56, 17, 2.0F);
        this.drawOverlayScaled(x + start, y + 92, 0, 0, 0, 20, 56, 48, 2.0F);
        this.drawOverlayScaled(x + start + 42, y + 76, 0, 0, 100, 84, 11, 13, 2.0F);

        ItemStack output = this.safeCopy(recipe.getRecipeOutput());
        ItemStack catalyst = this.displayStack(recipe.catalyst);
        this.drawItemStack(output, x + start + 48, y + 36);
        this.drawItemStack(catalyst, x + start + 26, y + 72);
        this.addItemTooltip(output, mouseX, mouseY, x + start + 48, y + 36);
        this.addItemTooltip(catalyst, mouseX, mouseY, x + start + 26, y + 72);
        this.drawAspectGrid(recipe.aspects, x + start + 28, y + 128, 3, mouseX, mouseY, 1);
    }

    private void drawSmeltingPage(int side, int x, int y, int mouseX, int mouseY, ResearchPage pageParm) {
        ItemStack input = pageParm.recipe instanceof ItemStack ? (ItemStack) pageParm.recipe : ItemStack.EMPTY;
        if (this.isEmpty(input)) {
            return;
        }
        ItemStack output = FurnaceRecipes.instance().getSmeltingResult(input);
        if (this.isEmpty(output)) {
            return;
        }
        int start = side * PAGE_STRIDE;
        this.drawCenteredScaledString(I18n.format("recipe.type.smelting"), x + start + 56, y, 112, 0x505050);
        this.drawOverlayScaled(x + start, y + 28, 0, 0, 0, 192, 56, 64, 2.0F);
        this.drawItemStack(input, x + start + 48, y + 64);
        this.drawItemStack(output, x + start + 48, y + 144);
        this.addItemTooltip(input, mouseX, mouseY, x + start + 48, y + 64);
        this.addItemTooltip(output, mouseX, mouseY, x + start + 48, y + 144);
    }

    private void drawInfusionPage(int side, int x, int y, int mouseX, int mouseY, ResearchPage pageParm) {
        InfusionRecipe recipe = this.selectCycled(pageParm.recipe, InfusionRecipe.class);
        if (recipe == null) {
            return;
        }
        int start = side * PAGE_STRIDE;
        this.drawCenteredScaledString(I18n.format("recipe.type.infusion"), x + start + 56, y, 112, 0x505050);
        this.drawInstability(recipe.getInstability(), x + start + 56, y + 194);
        this.drawOverlayScaled(x + start, y + 20, 0, 0, 0, 3, 56, 17, 2.0F);
        this.drawOverlayScaled(x + start, y + 58, 0, 0, 200, 77, 60, 44, 2.0F);

        AspectList aspects = recipe.getAspects();
        this.drawAspectGrid(aspects, x + start + 8, y + 164, 5, mouseX, mouseY, 1);

        ItemStack output = this.infusionOutputStack(recipe);
        ItemStack input = this.displayStack(recipe.getRecipeInput());
        this.drawItemStack(output, x + start + 48, y + 28);
        this.drawItemStack(input, x + start + 48, y + 94);
        this.addItemTooltip(output, mouseX, mouseY, x + start + 48, y + 28);
        this.addItemTooltip(input, mouseX, mouseY, x + start + 48, y + 94);
        this.drawInfusionComponents(recipe.getComponents(), x + start + 56, y + 102, mouseX, mouseY);
    }

    private void drawInfusionEnchantingPage(int side, int x, int y, int mouseX, int mouseY, ResearchPage pageParm) {
        InfusionEnchantmentRecipe recipe = this.selectCycled(pageParm.recipe, InfusionEnchantmentRecipe.class);
        if (recipe == null || recipe.enchantment == null) {
            return;
        }
        int start = side * PAGE_STRIDE;
        Enchantment enchantment = recipe.enchantment;
        int level = 1 + (int) (System.currentTimeMillis() / 1000L % Math.max(1, enchantment.getMaxLevel()));
        this.drawCenteredScaledString(I18n.format("recipe.type.infusionenchantment"), x + start + 56, y, 112, 0x505050);
        this.drawInstability(recipe.instability, x + start + 56, y + 194);
        this.drawCenteredScaledString(enchantment.getTranslatedName(level), x + start + 56, y + 24, 112, 0x705090);
        this.drawCenteredScaledString(recipe.recipeXP * level + " levels", x + start + 56, y + 40, 112, 0x508850);
        this.drawOverlayScaled(x + start, y + 58, 0, 0, 200, 77, 60, 44, 2.0F);
        this.drawAspectGrid(recipe.aspects, x + start + 8, y + 164, 5, mouseX, mouseY, level);
        this.drawInfusionComponents(recipe.components, x + start + 56, y + 102, mouseX, mouseY);
    }

    private void drawCompoundCraftingPage(int side, int x, int y, int mouseX, int mouseY, ResearchPage page) {
        if (!(page.recipe instanceof List)) {
            return;
        }
        List recipe = (List) page.recipe;
        if (recipe.size() < 5 || !(recipe.get(1) instanceof Integer) || !(recipe.get(2) instanceof Integer) || !(recipe.get(3) instanceof Integer) || !(recipe.get(4) instanceof List)) {
            return;
        }
        AspectList aspects = recipe.get(0) instanceof AspectList ? (AspectList) recipe.get(0) : null;
        int dx = (Integer) recipe.get(1);
        int dy = (Integer) recipe.get(2);
        int dz = (Integer) recipe.get(3);
        List items = (List) recipe.get(4);
        int start = side * PAGE_STRIDE;
        this.drawCenteredScaledString(I18n.format("recipe.type.construct"), x + start + 56, y, 112, 0x505050);
        this.drawOverlayScaled(x + start + 48, y + 174, 0, 0, 68, 76, 12, 12, 2.0F, aspects == null || aspects.size() <= 0 ? 0.0F : 0.4F);
        this.drawAspectCostRow(aspects, x + start + 14, y + 182, 5, mouseX, mouseY, 1);

        this.mc.getTextureManager().bindTexture(OVERLAY);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
        GlStateManager.enableBlend();
        this.drawOverlayScaled(x + start - 8, y + 78 + Math.max(3 - dx, 3 - dz) * 8 + dx * 4 + dz * 4, 0, 0, 0, 72, 64, 44, 2.0F, 0.5F);

        int xoff = 64 - (dx * 16 + dz * 16) / 2;
        int yoff = -dy * 25;
        float scale = dy > 3 ? Math.max(0.4F, 1.0F - (dy - 3) * 0.2F) : 1.0F;
        int index = 0;
        for (int j = 0; j < dy; ++j) {
            for (int k = dz - 1; k >= 0; --k) {
                for (int i = dx - 1; i >= 0; --i) {
                    if (index >= items.size()) {
                        return;
                    }
                    Object raw = items.get(index++);
                    ItemStack stack = this.displayStack(raw);
                    if (this.isEmpty(stack)) {
                        continue;
                    }
                    int slotX = (int) (x + start + xoff * (1.0F + (1.0F - scale)) + (i * 16 + k * 16) * scale);
                    int slotY = (int) (y + 108 + yoff * scale + (-i * 8 + k * 8 + j * 50) * scale);
                    this.drawItemStack(stack, slotX, slotY, scale);
                    this.addItemTooltip(stack, mouseX, mouseY, slotX, slotY, (int) (16 * scale), (int) (16 * scale));
                }
            }
        }
    }

    private void drawAspectPage(int side, int x, int y, int mouseX, int mouseY, AspectList aspects) {
        if (aspects == null || aspects.size() <= 0) {
            return;
        }
        int start = side * PAGE_STRIDE;
        int count = 0;
        for (Aspect aspect : aspects.getAspectsSorted()) {
            if (aspect == null) {
                continue;
            }
            int rowY = y + count * 50;
            GlStateManager.pushMatrix();
            GlStateManager.scale(2.0F, 2.0F, 1.0F);
            this.drawAspectTag((x + start) / 2, rowY / 2, aspect, aspects.getAmount(aspect));
            GlStateManager.popMatrix();
            this.drawCenteredScaledString(aspect.getName(), x + start + 16, rowY + 33, 80, 0x505050);
            if (aspect.getComponents() != null && aspect.getComponents().length == 2) {
                this.drawAspectTag(x + start + 54, rowY + 4, aspect.getComponents()[0], 0);
                this.drawAspectTag(x + start + 96, rowY + 4, aspect.getComponents()[1], 0);
                this.fontRenderer.drawString("=", x + start + 39, rowY + 12, 0x999999);
                this.fontRenderer.drawString("+", x + start + 83, rowY + 12, 0x999999);
                this.drawCenteredScaledString("\u00a7o" + aspect.getComponents()[0].getName(), x + start + 62, rowY + 30, 70, 0x505050);
                this.drawCenteredScaledString("\u00a7o" + aspect.getComponents()[1].getName(), x + start + 104, rowY + 30, 70, 0x505050);
            } else {
                this.fontRenderer.drawString(I18n.format("tc.aspect.primal"), x + start + 48, rowY + 12, 0x444444);
            }
            this.addAspectTooltip(aspect, mouseX, mouseY, x + start, rowY, 40, 40, 1);
            ++count;
        }
    }

    private void drawObjectGrid(Object[] items, int width, int height, int x, int y, int mouseX, int mouseY) {
        if (items == null || width <= 0) {
            return;
        }
        int max = Math.min(items.length, Math.min(9, width * Math.max(1, height)));
        for (int i = 0; i < max; ++i) {
            Object raw = items[i];
            ItemStack stack = this.displayStack(raw);
            if (this.isEmpty(stack)) {
                continue;
            }
            int slotX = x + i % width * 32;
            int slotY = y + i / width * 32;
            this.drawItemStack(stack, slotX, slotY);
            this.addItemTooltip(stack, mouseX, mouseY, slotX, slotY);
        }
    }

    private void drawInfusionComponents(ItemStack[] components, int centerX, int centerY, int mouseX, int mouseY) {
        if (components == null || components.length == 0) {
            return;
        }
        float pieSlice = 360.0F / components.length;
        float currentRot = -90.0F;
        for (ItemStack component : components) {
            int slotX = centerX + (int) (MathHelper.cos(currentRot / 180.0F * (float) Math.PI) * 40.0F) - 8;
            int slotY = centerY + (int) (MathHelper.sin(currentRot / 180.0F * (float) Math.PI) * 40.0F) - 8;
            ItemStack stack = this.displayStack(component);
            this.drawItemStack(stack, slotX, slotY);
            this.addItemTooltip(stack, mouseX, mouseY, slotX, slotY);
            currentRot += pieSlice;
        }
    }

    private void drawInstability(int instability, int centerX, int y) {
        int tier = Math.min(5, Math.max(0, instability / 2));
        this.drawCenteredScaledString(I18n.format("tc.inst") + " " + I18n.format("tc.inst." + tier), centerX, y, 126, 0x505050);
    }

    private void drawAspectCostRow(AspectList aspects, int x, int y, int perRow, int mouseX, int mouseY, int amountMultiplier) {
        if (aspects == null || aspects.size() <= 0) {
            return;
        }
        int visible = Math.min(perRow, aspects.size());
        int startX = x + (5 - visible) * 8;
        int count = 0;
        for (Aspect tag : aspects.getAspectsSortedAmount()) {
            if (tag == null || count >= perRow) {
                continue;
            }
            int tx = startX + 18 * count;
            this.drawAspectTag(tx, y, tag, aspects.getAmount(tag) * amountMultiplier);
            this.addAspectTooltip(tag, mouseX, mouseY, tx, y, 16, 16, amountMultiplier);
            ++count;
        }
    }

    private void drawAspectGrid(AspectList aspects, int x, int y, int perRow, int mouseX, int mouseY, int amountMultiplier) {
        if (aspects == null || aspects.size() <= 0 || perRow <= 0) {
            return;
        }
        Aspect[] tags = aspects.getAspectsSorted();
        int tagCount = 0;
        for (Aspect tag : tags) {
            if (tag != null) {
                ++tagCount;
            }
        }
        if (tagCount == 0) {
            return;
        }
        int rows = (tagCount - 1) / perRow;
        int shift = (perRow - tagCount % perRow) * 10;
        int sy = y - 10 * rows;
        int total = 0;
        for (Aspect tag : tags) {
            if (tag == null) {
                continue;
            }
            int row = total / perRow;
            int col = total % perRow;
            int centered = 0;
            if (row >= rows && (rows > 1 || tagCount < perRow)) {
                centered = 1;
            }
            int tx = x + col * 20 + shift * centered;
            int ty = sy + row * 20;
            this.drawAspectTag(tx, ty, tag, aspects.getAmount(tag) * amountMultiplier);
            this.addAspectTooltip(tag, mouseX, mouseY, tx, ty, 16, 16, amountMultiplier);
            ++total;
        }
    }

    private void drawAspectTag(int x, int y, Aspect aspect, int amount) {
        thaumcraft.client.lib.UtilsFX.drawTag(x, y, aspect, amount, 0, this.zLevel, 771, 1.0F, false);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private int drawMarkupText(String text, int x, int y, int width) {
        List<String> inserts = new ArrayList<String>();
        String formatted = this.prepareMarkupText(text, inserts);
        List<String> lines = this.researchFontRenderer.listFormattedStringToWidth(formatted, width);
        for (String line : lines) {
            int insertion = this.markupInsertionIndex(line, inserts.size());
            if (insertion < 0) {
                this.researchFontRenderer.drawString(line, x, y, 0x303030);
            } else {
                String token = inserts.get(insertion);
                if (token.startsWith("<LINE")) {
                    this.drawTextDivider(x, y, width);
                } else if (token.startsWith("<IMG>")) {
                    int end = token.indexOf("</IMG>");
                    if (end >= 0) {
                        int imageBottom = this.drawImageToken(token.substring(5, end), x, y, width);
                        y += Math.max(0, imageBottom - y - this.researchFontRenderer.FONT_HEIGHT);
                    }
                }
            }
            y += this.researchFontRenderer.FONT_HEIGHT;
        }
        return y;
    }

    private String prepareMarkupText(String text, List<String> inserts) {
        String formatted = (text == null ? "" : text).replace("\r", "").replace("<BR>", "\n").replace("<BR/>", "\n");
        while (true) {
            int line = formatted.indexOf("<LINE>");
            int shortLine = formatted.indexOf("<LINE/>");
            if (line < 0 || shortLine >= 0 && shortLine < line) {
                line = shortLine;
            }
            int image = formatted.indexOf("<IMG>");
            if (line < 0 && image < 0) {
                return formatted;
            }

            if (line >= 0 && (image < 0 || line < image)) {
                int length = formatted.startsWith("<LINE/>", line) ? 7 : 6;
                int index = inserts.size();
                inserts.add("<LINE>");
                formatted = formatted.substring(0, line) + "\n@" + index + "@\n" + formatted.substring(line + length);
                continue;
            }

            int end = formatted.indexOf("</IMG>", image + 5);
            if (end < 0) {
                formatted = formatted.substring(0, image) + formatted.substring(image + 5);
                continue;
            }
            String token = formatted.substring(image, end + 6);
            int index = inserts.size();
            inserts.add(token);
            formatted = formatted.substring(0, image) + "\n@" + index + "@\n" + formatted.substring(end + 6);
        }
    }

    private int markupInsertionIndex(String line, int insertionCount) {
        int start = line.indexOf('@');
        int end = start < 0 ? -1 : line.indexOf('@', start + 1);
        if (start < 0 || end <= start + 1) {
            return -1;
        }
        try {
            int index = Integer.parseInt(line.substring(start + 1, end));
            return index >= 0 && index < insertionCount ? index : -1;
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    private void drawTextDivider(int x, int y, int width) {
        this.mc.getTextureManager().bindTexture(TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(x + (width - 96) / 2, y + 2, 24, 184, 96, 4);
    }

    private int drawImageToken(String spec, int x, int y, int width) {
        try {
            String[] parts = spec.split(":");
            if (parts.length < 7) {
                return y;
            }
            String domain = parts[0];
            String path = parts[1];
            int u = Integer.parseInt(parts[2]);
            int v = Integer.parseInt(parts[3]);
            int w = Integer.parseInt(parts[4]);
            int h = Integer.parseInt(parts[5]);
            float scale = Float.parseFloat(parts[6]);
            ResourceLocation texture = new ResourceLocation(domain, path);
            return this.drawInlineImage(texture, x, y, u, v, w, h, scale, true);
        } catch (RuntimeException ignored) {
            return y;
        }
    }

    private int drawInlineImage(ResourceLocation texture, int x, int y, int u, int v, int sourceWidth, int sourceHeight, float scale, boolean centered) {
        int drawWidth = Math.max(1, Math.round(sourceWidth * scale));
        int drawHeight = Math.max(1, Math.round(sourceHeight * scale));
        int drawX = centered ? x + (PAGE_WIDTH - drawWidth) / 2 : x;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);
        float textureScale = 1.0F / 256.0F;
        float u0 = u * textureScale;
        float v0 = v * textureScale;
        float u1 = (u + sourceWidth) * textureScale;
        float v1 = (v + sourceHeight) * textureScale;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(drawX, y + drawHeight, this.zLevel).tex(u0, v1).endVertex();
        buffer.pos(drawX + drawWidth, y + drawHeight, this.zLevel).tex(u1, v1).endVertex();
        buffer.pos(drawX + drawWidth, y, this.zLevel).tex(u1, v0).endVertex();
        buffer.pos(drawX, y, this.zLevel).tex(u0, v0).endVertex();
        tessellator.draw();
        return y + drawHeight;
    }

    private void drawOverlayScaled(int x, int y, int translateX, int translateY, int u, int v, int width, int height, float scale) {
        this.drawOverlayScaled(x, y, translateX, translateY, u, v, width, height, scale, 1.0F);
    }

    private void drawOverlayScaled(int x, int y, int translateX, int translateY, int u, int v, int width, int height, float scale, float alpha) {
        if (alpha <= 0.0F) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        GlStateManager.enableBlend();
        this.mc.getTextureManager().bindTexture(OVERLAY);
        GlStateManager.translate(x, y, 0.0F);
        GlStateManager.scale(scale, scale, 1.0F);
        this.drawTexturedModalRect(translateX, translateY, u, v, width, height);
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawCenteredScaledString(String text, int centerX, int y, int maxWidth, int color) {
        if (text == null) {
            return;
        }
        int width = this.fontRenderer.getStringWidth(text);
        if (width <= maxWidth) {
            this.fontRenderer.drawString(text, centerX - width / 2, y, color);
            return;
        }
        float scale = (float) maxWidth / (float) width;
        GlStateManager.pushMatrix();
        GlStateManager.translate(centerX - width * scale / 2.0F, y, 0.0F);
        GlStateManager.scale(scale, scale, 1.0F);
        this.fontRenderer.drawString(text, 0, 0, color);
        GlStateManager.popMatrix();
    }

    private <T> T selectCycled(Object recipe, Class<T> type) {
        Object selected = recipe;
        if (recipe instanceof Object[]) {
            Object[] recipes = (Object[]) recipe;
            if (recipes.length == 0) {
                return null;
            }
            selected = recipes[Math.max(0, this.cycle) % recipes.length];
        }
        return type.isInstance(selected) ? type.cast(selected) : null;
    }

    private void updateRecipeCycle() {
        if (this.lastCycle < System.currentTimeMillis()) {
            ++this.cycle;
            this.lastCycle = System.currentTimeMillis() + 1000L;
        }
    }

    private void resetRecipeCycle() {
        this.lastCycle = 0L;
        this.cycle = -1;
    }

    private ItemStack infusionOutputStack(InfusionRecipe recipe) {
        Object output = recipe.getRecipeOutput();
        if (output instanceof ItemStack) {
            return this.displayStack(output);
        }
        ItemStack display = this.displayStack(recipe.getRecipeInput());
        if (output instanceof Object[] && ((Object[]) output).length >= 2 && !this.isEmpty(display)) {
            Object[] tag = (Object[]) output;
            if (tag[0] instanceof String && tag[1] instanceof NBTBase) {
                display = display.copy();
                display.setTagInfo((String) tag[0], (NBTBase) tag[1]);
            }
        }
        return display;
    }

    private ItemStack displayStack(Object input) {
        if (input == null) {
            return ItemStack.EMPTY;
        }
        if (input instanceof Ingredient) {
            ItemStack[] matches = ((Ingredient) input).getMatchingStacks();
            if (matches == null || matches.length == 0) {
                return ItemStack.EMPTY;
            }
            return this.safeCopy(matches[(int) (System.currentTimeMillis() / 1000L % matches.length)]);
        }
        if (input instanceof List) {
            List list = (List) input;
            if (list.isEmpty()) {
                return ItemStack.EMPTY;
            }
            return this.displayStack(list.get((int) (System.currentTimeMillis() / 1000L % list.size())));
        }
        if (input instanceof ItemStack[]) {
            ItemStack[] stacks = (ItemStack[]) input;
            if (stacks.length == 0) {
                return ItemStack.EMPTY;
            }
            return this.safeCopy(stacks[(int) (System.currentTimeMillis() / 1000L % stacks.length)]);
        }
        ItemStack stack = InventoryUtils.cycleItemStack(input);
        return this.safeCopy(stack);
    }

    private ItemStack safeCopy(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack copy = stack.copy();
        if (copy.getCount() <= 0) {
            copy.setCount(1);
        }
        return copy;
    }

    private void drawItemStack(ItemStack stack, int x, int y) {
        this.drawItemStack(stack, x, y, 1.0F);
    }

    private void drawItemStack(ItemStack stack, int x, int y, float scale) {
        if (this.isEmpty(stack)) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 100.0F);
        GlStateManager.scale(scale, scale, 1.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.enableGUIStandardItemLighting();
        this.itemRender.renderItemAndEffectIntoGUI(stack, 0, 0);
        this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, stack, 0, 0, null);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void addItemTooltip(ItemStack stack, int mouseX, int mouseY, int x, int y) {
        this.addItemTooltip(stack, mouseX, mouseY, x, y, 16, 16);
    }

    private void addItemTooltip(ItemStack stack, int mouseX, int mouseY, int x, int y, int width, int height) {
        if (this.isEmpty(stack) || !this.isMouseIn(mouseX, mouseY, x, y, Math.max(1, width), Math.max(1, height))) {
            return;
        }
        ITooltipFlag flag = this.mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL;
        this.setTooltip(stack.getTooltip(this.mc.player, flag), mouseX, mouseY);
    }

    private void addAspectTooltip(Aspect aspect, int mouseX, int mouseY, int x, int y, int width, int height, int amountMultiplier) {
        if (aspect == null || !this.isMouseIn(mouseX, mouseY, x, y, width, height)) {
            return;
        }
        List<String> lines = new ArrayList<String>();
        lines.add(aspect.getName());
        if (amountMultiplier > 0) {
            lines.add(aspect.getLocalizedDescription());
        }
        this.setTooltip(lines, mouseX, mouseY - 8);
    }

    private void setTooltip(List<String> lines, int x, int y) {
        this.tooltip = lines == null ? Collections.<String>emptyList() : lines;
        this.tooltipX = x;
        this.tooltipY = y;
    }

    private boolean isMouseIn(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    private boolean isEmpty(ItemStack stack) {
        return stack == null || stack.isEmpty();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
