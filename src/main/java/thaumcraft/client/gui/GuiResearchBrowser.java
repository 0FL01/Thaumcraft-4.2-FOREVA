package thaumcraft.client.gui;

import java.io.IOException;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiResearchBrowser extends GuiScreen {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/gui/gui_research.png");
    private final int paneWidth = 256;
    private final int paneHeight = 230;
    private FontRenderer galFontRenderer;

    @Override
    public void initGui() {
        super.initGui();
        this.galFontRenderer = this.fontRenderer != null ? this.fontRenderer : this.mc.fontRenderer;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        int left = (this.width - this.paneWidth) / 2;
        int top = (this.height - this.paneHeight) / 2;

        if (this.galFontRenderer == null) {
            this.galFontRenderer = this.fontRenderer != null ? this.fontRenderer : this.mc.fontRenderer;
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        GlStateManager.enableBlend();
        this.drawTexturedModalRect(left, top, 0, 0, this.paneWidth, this.paneHeight);
        GlStateManager.disableBlend();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode() || keyCode == 1) {
            this.mc.displayGuiScreen(null);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
