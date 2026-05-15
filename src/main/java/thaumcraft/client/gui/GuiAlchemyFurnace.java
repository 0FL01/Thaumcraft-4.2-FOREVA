package thaumcraft.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.container.ContainerAlchemyFurnace;
import thaumcraft.common.tiles.TileAlchemyFurnace;

public class GuiAlchemyFurnace extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/gui/gui_alchemyfurnace.png");

    private final TileAlchemyFurnace furnace;

    public GuiAlchemyFurnace(InventoryPlayer playerInventory, TileAlchemyFurnace furnace) {
        super(new ContainerAlchemyFurnace(playerInventory, furnace));
        this.furnace = furnace;
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

        if (this.furnace.isBurning()) {
            int burn = this.furnace.getBurnTimeRemainingScaled(20);
            this.drawTexturedModalRect(this.guiLeft + 80, this.guiTop + 26 + 20 - burn,
                    176, 20 - burn, 16, burn);
        }

        int cook = this.furnace.getCookProgressScaled(46);
        this.drawTexturedModalRect(this.guiLeft + 106, this.guiTop + 13 + 46 - cook,
                216, 46 - cook, 9, cook);

        int contents = this.furnace.getContentsScaled(48);
        this.drawTexturedModalRect(this.guiLeft + 61, this.guiTop + 12 + 48 - contents,
                200, 48 - contents, 8, contents);

        this.drawTexturedModalRect(this.guiLeft + 60, this.guiTop + 8, 232, 0, 10, 55);
        GlStateManager.disableBlend();
    }
}
