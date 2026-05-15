package thaumcraft.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.container.ContainerMagicBox;

public class GuiMagicBox extends GuiContainer {

    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

    private final IInventory playerInventory;
    private final IInventory lowerInventory;
    private final int inventoryRows;

    public GuiMagicBox(InventoryPlayer playerInventory, TileEntity box) {
        super(new ContainerMagicBox(playerInventory, box));
        this.playerInventory = playerInventory;
        this.lowerInventory = (IInventory) box;
        this.allowUserInput = false;
        this.inventoryRows = this.lowerInventory.getSizeInventory() / 9;
        this.ySize = 114 + this.inventoryRows * 18;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String lowerName = this.lowerInventory.hasCustomName()
                ? this.lowerInventory.getName()
                : I18n.format(this.lowerInventory.getName());
        String playerName = this.playerInventory.hasCustomName()
                ? this.playerInventory.getName()
                : I18n.format(this.playerInventory.getName());
        this.fontRenderer.drawString(lowerName, 8, 6, 4210752);
        this.fontRenderer.drawString(playerName, 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
        int inventoryHeight = this.inventoryRows * 18;
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, inventoryHeight + 17);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop + inventoryHeight + 17, 0, 126, this.xSize, 96);
    }
}
