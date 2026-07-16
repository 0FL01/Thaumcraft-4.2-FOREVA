package thaumcraft.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thaumcraft.common.container.ContainerHoverHarness;

public class GuiHoverHarness extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/gui/guihoverharness.png");

    public GuiHoverHarness(InventoryPlayer playerInventory, World world, int x, int y, int z) {
        super(new ContainerHoverHarness(playerInventory, world, x, y, z));
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected boolean checkHotbarKeys(int keyCode) {
        return false;
    }
}
