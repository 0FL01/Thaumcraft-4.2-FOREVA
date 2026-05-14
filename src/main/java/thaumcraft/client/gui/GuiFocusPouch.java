package thaumcraft.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import thaumcraft.common.container.ContainerFocusPouch;

public class GuiFocusPouch extends GuiContainer {

    public GuiFocusPouch(InventoryPlayer playerInventory, World world, int x, int y, int z) {
        super(new ContainerFocusPouch(playerInventory, world, x, y, z));
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(I18n.translateToLocal("container.focus_pouch"), 8, 6, 4210752);
        this.fontRenderer.drawString(I18n.translateToLocal("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultBackground();
        drawRect(this.guiLeft, this.guiTop, this.guiLeft + this.xSize, this.guiTop + this.ySize, 0xC0C6C6C6);
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 6; ++col) {
                int x = this.guiLeft + 34 + col * 18;
                int y = this.guiTop + 17 + row * 18;
                drawRect(x, y, x + 19, y + 19, 0xFF555555);
                drawRect(x + 1, y + 1, x + 18, y + 18, 0xFF8B8B8B);
            }
        }
    }
}
