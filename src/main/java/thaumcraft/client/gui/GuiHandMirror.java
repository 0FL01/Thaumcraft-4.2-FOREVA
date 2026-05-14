package thaumcraft.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import thaumcraft.common.container.ContainerHandMirror;

public class GuiHandMirror extends GuiContainer {

    public GuiHandMirror(InventoryPlayer playerInventory, World world, int x, int y, int z) {
        super(new ContainerHandMirror(playerInventory, world, x, y, z));
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(I18n.translateToLocal("container.handmirror"), 8, 6, 4210752);
        this.fontRenderer.drawString(I18n.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultBackground();
        drawRect(this.guiLeft, this.guiTop, this.guiLeft + this.xSize, this.guiTop + this.ySize, 0xC0C6C6C6);
        drawRect(this.guiLeft + 79, this.guiTop + 23, this.guiLeft + 98, this.guiTop + 42, 0xFF555555);
        drawRect(this.guiLeft + 80, this.guiTop + 24, this.guiLeft + 97, this.guiTop + 41, 0xFF8B8B8B);
    }
}
