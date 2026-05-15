package thaumcraft.client.gui;

import java.io.IOException;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.common.container.ContainerThaumatorium;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.tiles.TileThaumatorium;

public class GuiThaumatorium extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/gui/gui_thaumatorium.png");

    private final TileThaumatorium inventory;
    private final ContainerThaumatorium container;
    private int index;
    private int lastSize;
    private int startAspect;

    public GuiThaumatorium(InventoryPlayer playerInventory, TileThaumatorium inventory) {
        super(new ContainerThaumatorium(playerInventory, inventory));
        this.inventory = inventory;
        this.container = (ContainerThaumatorium) this.inventorySlots;
        this.container.updateRecipes();
        this.lastSize = this.container.recipes.size();
        this.refreshIndex();
    }

    void refreshIndex() {
        if (this.inventory.recipeHash != null && !this.container.recipes.isEmpty()) {
            for (int i = 0; i < this.container.recipes.size(); ++i) {
                if (this.inventory.recipeHash.contains(this.container.recipes.get(i).hash)) {
                    this.index = i;
                    break;
                }
            }
        }
        this.startAspect = 0;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        GlStateManager.enableBlend();
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        if (!this.container.recipes.isEmpty()) {
            this.updateIndex();
            CrucibleRecipe recipe = this.container.recipes.get(this.index);
            this.drawRecipeControls(recipe);
            this.drawSelectionState(recipe, mouseX, mouseY);
            this.drawAspects(recipe);
            this.drawOutput(recipe, mouseX, mouseY);
            this.drawRecipeCount();
        }

        GlStateManager.disableBlend();
    }

    private void updateIndex() {
        if (this.lastSize != this.container.recipes.size()) {
            this.lastSize = this.container.recipes.size();
            this.refreshIndex();
        }
        if (this.index < 0) {
            this.index = 0;
        }
        if (this.index >= this.container.recipes.size()) {
            this.index = this.container.recipes.size() - 1;
        }
    }

    private void drawRecipeControls(CrucibleRecipe recipe) {
        this.mc.getTextureManager().bindTexture(TEXTURE);
        if (this.container.recipes.size() > 1) {
            this.drawTexturedModalRect(this.guiLeft + 128, this.guiTop + 16, this.index > 0 ? 192 : 176, 16, 16, 8);
            this.drawTexturedModalRect(this.guiLeft + 128, this.guiTop + 24,
                    this.index < this.container.recipes.size() - 1 ? 192 : 176, 24, 16, 8);
        }
        if (recipe.aspects != null && recipe.aspects.size() > 6) {
            this.drawTexturedModalRect(this.guiLeft + 32, this.guiTop + 40, this.startAspect > 0 ? 192 : 176, 32, 8, 16);
            this.drawTexturedModalRect(this.guiLeft + 136, this.guiTop + 40,
                    this.startAspect < recipe.aspects.size() - 1 ? 200 : 184, 32, 8, 16);
        } else {
            this.startAspect = 0;
        }
    }

    private void drawSelectionState(CrucibleRecipe recipe, int mouseX, int mouseY) {
        boolean hoveringOutput = isMouseIn(mouseX, mouseY, 112, 16, 16, 16);
        if (this.inventory.recipeHash != null && !this.inventory.recipeHash.isEmpty()) {
            if (hoveringOutput || isSelected(recipe)) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.getTextureManager().bindTexture(TEXTURE);
                this.drawTexturedModalRect(this.guiLeft + 104, this.guiTop + 8, 176, 96, 48, 48);
            }
            float alpha = 1.0F + MathHelper.sin((float) this.mc.player.ticksExisted / 5.0F) * 0.4F;
            GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
            this.mc.getTextureManager().bindTexture(TEXTURE);
            this.drawTexturedModalRect(this.guiLeft + 88, this.guiTop + 16, 176, 56, 24, 24);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private void drawAspects(CrucibleRecipe recipe) {
        if (recipe.aspects == null) return;
        Aspect[] aspects = recipe.aspects.getAspectsSorted();
        int drawn = 0;
        for (int i = 0; i < aspects.length && drawn < 6; ++i) {
            Aspect aspect = aspects[i];
            if (i < this.startAspect || aspect == null) continue;
            int x = this.guiLeft + 40 + 16 * drawn;
            int y = this.guiTop + 40;
            this.mc.getTextureManager().bindTexture(aspect.getImage());
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 16, 16, 16.0F, 16.0F);

            this.mc.getTextureManager().bindTexture(TEXTURE);
            this.drawTexturedModalRect(this.guiLeft + 41 + 16 * drawn, this.guiTop + 57, 176, 8, 14, 6);
            if (isSelected(recipe)) {
                int required = Math.max(1, recipe.aspects.getAmount(aspect));
                int fill = MathHelper.clamp((int) ((float) this.inventory.essentia.getAmount(aspect) / (float) required * 12.0F), 0, 12);
                int color = aspect.getColor();
                GlStateManager.color((float) (color >> 16 & 255) / 255.0F,
                        (float) (color >> 8 & 255) / 255.0F,
                        (float) (color & 255) / 255.0F,
                        1.0F);
                this.drawTexturedModalRect(this.guiLeft + 42 + 16 * drawn, this.guiTop + 58, 176, 0, fill, 4);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }
            ++drawn;
        }
    }

    private void drawOutput(CrucibleRecipe recipe, int mouseX, int mouseY) {
        ItemStack output = recipe.getRecipeOutput();
        if (output.isEmpty()) return;

        boolean disabled = this.inventory.recipeHash.size() >= this.inventory.maxRecipes && !isSelected(recipe);
        if (disabled) {
            float alpha = 0.6F + MathHelper.sin((float) this.mc.player.ticksExisted / 4.0F) * 0.3F;
            GlStateManager.color(0.5F, 0.5F, 0.5F, alpha);
        }

        RenderHelper.enableGUIStandardItemLighting();
        this.itemRender.renderItemAndEffectIntoGUI(output, this.guiLeft + 112, this.guiTop + 16);
        this.itemRender.renderItemOverlayIntoGUI(this.fontRenderer, output, this.guiLeft + 112, this.guiTop + 16, null);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawRecipeCount() {
        if (this.inventory.maxRecipes <= 1) return;
        String text = this.inventory.recipeHash.size() + "/" + this.inventory.maxRecipes;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) (this.guiLeft + 136), (float) (this.guiTop + 33), 0.0F);
        GlStateManager.scale(0.5F, 0.5F, 1.0F);
        this.fontRenderer.drawString(text, -this.fontRenderer.getStringWidth(text) / 2, 0, 0xFFFFFF);
        GlStateManager.popMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.container.recipes.isEmpty() || this.index < 0 || this.index >= this.container.recipes.size()) return;

        CrucibleRecipe recipe = this.container.recipes.get(this.index);
        if (isMouseIn(mouseX, mouseY, 112, 16, 16, 16) && canToggle(recipe)) {
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, this.index);
            this.playButtonSelect();
            return;
        }

        if (this.container.recipes.size() > 1) {
            if (this.index > 0 && isMouseIn(mouseX, mouseY, 128, 16, 16, 8)) {
                --this.index;
                this.playButtonClick();
                return;
            }
            if (this.index < this.container.recipes.size() - 1 && isMouseIn(mouseX, mouseY, 128, 24, 16, 8)) {
                ++this.index;
                this.playButtonClick();
                return;
            }
        }

        if (recipe.aspects != null && recipe.aspects.size() > 6) {
            if (this.startAspect > 0 && isMouseIn(mouseX, mouseY, 32, 40, 8, 16)) {
                --this.startAspect;
                this.playButtonClick();
                return;
            }
            if (this.startAspect < recipe.aspects.size() - 1 && isMouseIn(mouseX, mouseY, 136, 40, 8, 16)) {
                ++this.startAspect;
                this.playButtonClick();
            }
        }
    }

    private boolean canToggle(CrucibleRecipe recipe) {
        return this.inventory.recipeHash.size() < this.inventory.maxRecipes || isSelected(recipe);
    }

    private boolean isSelected(CrucibleRecipe recipe) {
        return this.inventory.recipeHash != null && this.inventory.recipeHash.contains(recipe.hash);
    }

    private boolean isMouseIn(int mouseX, int mouseY, int x, int y, int width, int height) {
        int relX = mouseX - (this.guiLeft + x);
        int relY = mouseY - (this.guiTop + y);
        return relX >= 0 && relY >= 0 && relX < width && relY < height;
    }

    private void playButtonSelect() {
        if (this.mc.player != null) {
            this.mc.player.playSound(TCSounds.HHON, 0.3F, 1.0F);
        }
    }

    private void playButtonClick() {
        if (this.mc.player != null) {
            this.mc.player.playSound(TCSounds.CAMERACLACK, 0.4F, 1.0F);
        }
    }
}
