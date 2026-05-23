package thaumcraft.client.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiResearchRecipe extends GuiScreen {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/gui/gui_researchbook.png");

    private final int paneWidth = 256;
    private final int paneHeight = 181;
    private final ResearchItem research;
    private final double guiMapX;
    private final double guiMapY;
    private ResearchPage[] pages;
    private int page;
    private int maxPages;

    public GuiResearchRecipe(ResearchItem research, int page, double guiMapX, double guiMapY) {
        this.research = research;
        this.guiMapX = guiMapX;
        this.guiMapY = guiMapY;
        this.pages = research == null || research.getPages() == null ? new ResearchPage[0] : research.getPages();
        List<ResearchPage> visiblePages = new ArrayList<>();
        visiblePages.addAll(Arrays.asList(this.pages));
        this.pages = visiblePages.toArray(new ResearchPage[0]);
        this.maxPages = this.pages.length;
        if ((page & 1) == 1) {
            --page;
        }
        this.page = Math.max(0, page);
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
            return;
        }
        if (this.page < this.maxPages - 2 && mouseX >= left + 262 && mouseX < left + 274 && mouseY >= top + 190 && mouseY < top + 198) {
            this.page = Math.min(Math.max(0, this.maxPages - 1), this.page + 2);
            if ((this.page & 1) == 1) {
                --this.page;
            }
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

        if (this.research != null) {
            String title = this.research.getName();
            this.drawCenteredString(this.fontRenderer, title, this.width / 2, top + 8, 0x303030);
            this.fontRenderer.drawSplitString(this.research.getText(), left + 18, top + 28, 110, 0x505050);
        }

        if (this.pages.length > 0) {
            int current = this.page;
            for (int side = 0; side < 2 && current + side < this.pages.length; ++side) {
                this.drawPageSummary(this.pages[current + side], side, left, top);
            }
        }

        if (this.page > 0) {
            this.drawTexturedModalRect(left - 16, top + 190, 0, 184, 12, 8);
        }
        if (this.page < this.maxPages - 2) {
            this.drawTexturedModalRect(left + 262, top + 190, 12, 184, 12, 8);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawPageSummary(ResearchPage page, int side, int left, int top) {
        int startX = left + 16 + side * 152;
        int startY = top + 28;
        if (page == null) {
            return;
        }
        this.fontRenderer.drawSplitString(page.type.name(), startX, startY, 110, 0x303030);
        String text = page.getTranslatedText();
        if (text != null && !text.isEmpty()) {
            this.fontRenderer.drawSplitString(text, startX, startY + 14, 110, 0x505050);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
