package thaumcraft.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidStack;
import thaumcraft.common.container.ContainerSpa;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.tiles.TileSpa;

public class GuiSpa extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/gui/gui_spa.png");

    private final TileSpa spa;

    public GuiSpa(InventoryPlayer playerInventory, TileSpa spa) {
        super(new ContainerSpa(playerInventory, spa));
        this.spa = spa;
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

        this.drawTexturedModalRect(this.guiLeft + 89, this.guiTop + 35,
                208, this.spa.getMix() ? 16 : 32, 8, 8);
        this.drawFluidBar();

        this.mc.getTextureManager().bindTexture(TEXTURE);
        this.drawTexturedModalRect(this.guiLeft + 106, this.guiTop + 11, 232, 0, 10, 55);
        GlStateManager.disableBlend();
    }

    private void drawFluidBar() {
        FluidStack fluid = this.spa.tank.getFluid();
        if (fluid == null || fluid.amount <= 0 || fluid.getFluid() == null) return;

        int fill = MathHelper.clamp(fluid.amount * 48 / Math.max(1, this.spa.tank.getCapacity()), 0, 48);
        if (fill <= 0) return;

        ResourceLocation still = fluid.getFluid().getStill(fluid);
        if (still == null) return;

        TextureAtlasSprite sprite = this.mc.getTextureMapBlocks().getAtlasSprite(still.toString());
        int color = fluid.getFluid().getColor(fluid);
        GlStateManager.color((float) (color >> 16 & 255) / 255.0F,
                (float) (color >> 8 & 255) / 255.0F,
                (float) (color & 255) / 255.0F,
                1.0F);
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        this.drawTexturedModalRect(this.guiLeft + 107, this.guiTop + 15 + 48 - fill, sprite, 8, fill);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (isMouseIn(mouseX, mouseY, 104, 10, 10, 55)) {
            FluidStack fluid = this.spa.tank.getFluid();
            if (fluid != null && fluid.getFluid() != null) {
                List<String> text = new ArrayList<String>();
                text.add(fluid.getFluid().getLocalizedName(fluid));
                text.add(fluid.amount + " mb");
                this.drawHoveringText(text, mouseX, mouseY);
            }
        }

        if (isMouseIn(mouseX, mouseY, 88, 34, 10, 10)) {
            List<String> text = new ArrayList<String>();
            text.add(I18n.format(this.spa.getMix() ? "text.spa.mix.true" : "text.spa.mix.false"));
            this.drawHoveringText(text, mouseX, mouseY);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isMouseIn(mouseX, mouseY, 89, 35, 8, 8)) {
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 1);
            this.playButtonClick();
        }
    }

    private boolean isMouseIn(int mouseX, int mouseY, int x, int y, int width, int height) {
        int relX = mouseX - (this.guiLeft + x);
        int relY = mouseY - (this.guiTop + y);
        return relX >= 0 && relY >= 0 && relX < width && relY < height;
    }

    private void playButtonClick() {
        if (this.mc.player != null) {
            this.mc.player.playSound(TCSounds.CAMERACLACK, 0.4F, 1.0F);
        }
    }
}
