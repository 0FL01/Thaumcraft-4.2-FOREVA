package thaumcraft.client.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.lib.utils.InventoryUtils;

@SideOnly(Side.CLIENT)
public class GuiResearchPopup extends Gui {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/achievement/achievement_background.png");
    private static final long DISPLAY_TIME_MS = 3000L;

    private final Minecraft game;
    private final RenderItem itemRender;
    private final List<ResearchItem> researchQueue = new ArrayList<>();
    private long researchTime;

    public GuiResearchPopup(Minecraft game) {
        this.game = game;
        this.itemRender = game.getRenderItem();
    }

    public void queueResearchInformation(ResearchItem research) {
        if (research == null) {
            return;
        }
        if (this.researchTime == 0L) {
            this.researchTime = Minecraft.getSystemTime();
        }
        this.researchQueue.add(research);
        GuiResearchBrowser.lastX = research.displayColumn;
        GuiResearchBrowser.lastY = research.displayRow;
    }

    public void updateResearchWindow() {
        if (this.researchQueue.isEmpty() || this.researchTime == 0L) {
            return;
        }
        double progress = (double) (Minecraft.getSystemTime() - this.researchTime) / DISPLAY_TIME_MS;
        if (progress < 0.0D || progress > 1.0D) {
            this.researchQueue.remove(0);
            this.researchTime = this.researchQueue.isEmpty() ? 0L : Minecraft.getSystemTime();
            return;
        }

        ScaledResolution resolution = new ScaledResolution(this.game);
        GL11.glViewport(0, 0, this.game.displayWidth, this.game.displayHeight);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, resolution.getScaledWidth(), resolution.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);

        double slide = progress * 2.0D;
        if (slide > 1.0D) {
            slide = 2.0D - slide;
        }
        slide = 1.0D - Math.min(1.0D, slide * 4.0D);
        slide *= slide;
        slide *= slide;
        int x = 0;
        int y = -(int) (slide * 36.0D);
        ResearchItem research = this.researchQueue.get(0);

        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.game.getTextureManager().bindTexture(TEXTURE);
        this.drawTexturedModalRect(x, y, 96, 202, 160, 32);
        this.game.fontRenderer.drawString("Research Completed!", x + 30, y + 7, 0xFFFF00);
        int nameWidth = this.game.fontRenderer.getStringWidth(research.getName());
        if (nameWidth <= 125) {
            this.game.fontRenderer.drawString(research.getName(), x + 30, y + 18, 0xFFFFFF);
        } else {
            float scale = 125.0F / (float) nameWidth;
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 30, y + 16 + 2.0F / scale, 0.0F);
            GlStateManager.scale(scale, scale, scale);
            this.game.fontRenderer.drawString(research.getName(), 0, 0, 0xFFFFFF);
            GlStateManager.popMatrix();
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        ItemStack icon = research.icon_item == null ? ItemStack.EMPTY : InventoryUtils.cycleItemStack(research.icon_item);
        if (!icon.isEmpty()) {
            this.itemRender.renderItemAndEffectIntoGUI(icon, x + 8, y + 8);
        } else if (research.icon_resource != null) {
            this.game.getTextureManager().bindTexture(research.icon_resource);
            UtilsFX.drawTexturedQuadFull(x + 8, y + 8, this.zLevel);
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }
}
