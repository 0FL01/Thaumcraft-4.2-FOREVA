package thaumcraft.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.container.ContainerResearchTable;
import thaumcraft.common.tiles.TileResearchTable;

public class GuiResearchTable extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/gui/guiresearchtable2.png");

    private final TileResearchTable tile;

    public GuiResearchTable(EntityPlayer player, TileResearchTable tile) {
        super(new ContainerResearchTable(player.inventory, tile));
        this.tile = tile;
        this.xSize = 255;
        this.ySize = 255;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        if (this.tile.isInvalid() && this.mc.player != null) {
            this.mc.player.closeScreen();
            return;
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        GlStateManager.enableBlend();
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 255, 167);
        this.drawTexturedModalRect(this.guiLeft + 40, this.guiTop + 167, 0, 166, 184, 88);
        GlStateManager.disableBlend();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
