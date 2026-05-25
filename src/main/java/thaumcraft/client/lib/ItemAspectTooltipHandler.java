package thaumcraft.client.lib;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ScanResult;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.research.ScanManager;

/**
 * Renders aspect icons and amounts below item tooltips when Shift is held.
 * Mirrors the original Thaumcraft 4.2 behavior: registered object aspects
 * (plus IEssentiaContainerItem stored aspects via getBonusTags) are displayed
 * as a row of 16x16 aspect icons with numeric amounts.
 *
 * Fires on RenderTooltipEvent.PostText so the icons appear below the
 * fully drawn vanilla tooltip without interfering with its layout.
 * A dark semi-transparent background is drawn behind the icons to ensure
 * visibility on any screen background.
 */
@SideOnly(Side.CLIENT)
public class ItemAspectTooltipHandler {

    @SubscribeEvent
    public void onPostTooltip(RenderTooltipEvent.PostText event) {
        ItemStack stack = event.getStack();
        if (stack.isEmpty()) return;
        if (!GuiScreen.isShiftKeyDown()) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null || mc.world == null) return;

        // Gate: only show aspects if the player has scanned this item.
        // Mirrors the original TC4 behavior where item aspects require discovery via Thaumometer.
        ScanResult scan = new ScanResult((byte) 1, Item.getIdFromItem(stack.getItem()), stack.getMetadata(), null, null);
        if (!ScanManager.hasBeenScanned(player, scan)) return;

        // Get aspects using the same logic as ScanManager.getObjectAspects:
        // registered object tags + bonus tags (includes IEssentiaContainerItem)
        AspectList aspects = ThaumcraftCraftingManager.getObjectTags(stack);
        aspects = ThaumcraftCraftingManager.getBonusTags(stack, aspects);
        if (aspects == null || aspects.size() <= 0) return;

        Aspect[] sorted = aspects.getAspectsSorted();
        if (sorted == null || sorted.length == 0) return;

        // Count visible aspects (non-zero amount)
        int count = 0;
        for (Aspect a : sorted) {
            if (a != null && aspects.getAmount(a) > 0) count++;
        }
        if (count == 0) return;

        int tooltipX = event.getX();
        int tooltipY = event.getY();
        int tooltipW = event.getWidth();
        int tooltipH = event.getHeight();

        int startY = tooltipY + tooltipH + 3;
        int iconSize = 16;
        int gap = 2;
        int step = iconSize + gap; // 18px per icon slot

        // Determine max width per row: from tooltipX to screen edge minus margin
        ScaledResolution sr = new ScaledResolution(mc);
        int maxRowWidth = Math.max(step, sr.getScaledWidth() - tooltipX - 8);

        // Build rows: each row fills left-to-right until maxRowWidth is exceeded
        List<List<Aspect>> rows = new ArrayList<>();
        List<Aspect> currentRow = new ArrayList<>();
        int currentRowWidth = 0;

        for (Aspect aspect : sorted) {
            if (aspect == null) continue;
            if (aspects.getAmount(aspect) <= 0) continue;
            if (currentRowWidth + step > maxRowWidth && !currentRow.isEmpty()) {
                rows.add(currentRow);
                currentRow = new ArrayList<>();
                currentRowWidth = 0;
            }
            currentRow.add(aspect);
            currentRowWidth += step;
        }
        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
        }

        int numRows = rows.size();
        int totalHeight = numRows * step - gap;

        // Widest row (for background sizing)
        int maxContentWidth = 0;
        for (List<Aspect> row : rows) {
            int rowWidth = row.size() * step - gap;
            if (rowWidth > maxContentWidth) maxContentWidth = rowWidth;
        }

        // Draw dark tooltip-like background behind all icon rows
        int bgLeft = tooltipX - 2;
        int bgTop = startY - 2;
        int bgRight = Math.max(tooltipX + tooltipW, tooltipX + maxContentWidth) + 2;
        int bgBottom = startY + totalHeight + 2;

        drawRectBackground(bgLeft, bgTop, bgRight, bgBottom);

        // Draw each row of aspect icons
        int curY = startY;
        for (List<Aspect> row : rows) {
            int curX = tooltipX;
            for (Aspect aspect : row) {
                int amount = aspects.getAmount(aspect);
                UtilsFX.drawTag(curX, curY, aspect, (float) amount, 0, 0.0, 771, 1.0f, false);
                curX += step;
            }
            curY += step;
        }

        // drawTag disables blend and enables lighting; re-enable depth for subsequent rendering
        GlStateManager.enableDepth();
    }

    private static void drawRectBackground(int left, int top, int right, int bottom) {
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        // Dark semi-transparent background matching vanilla tooltip style
        int bgColor = 0xF0100010;

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buf.pos(left, bottom, 0.0D).color(
                (float) (bgColor >> 16 & 255) / 255.0F,
                (float) (bgColor >> 8 & 255) / 255.0F,
                (float) (bgColor & 255) / 255.0F,
                (float) (bgColor >> 24 & 255) / 255.0F
        ).endVertex();
        buf.pos(right, bottom, 0.0D).color(
                (float) (bgColor >> 16 & 255) / 255.0F,
                (float) (bgColor >> 8 & 255) / 255.0F,
                (float) (bgColor & 255) / 255.0F,
                (float) (bgColor >> 24 & 255) / 255.0F
        ).endVertex();
        buf.pos(right, top, 0.0D).color(
                (float) (bgColor >> 16 & 255) / 255.0F,
                (float) (bgColor >> 8 & 255) / 255.0F,
                (float) (bgColor & 255) / 255.0F,
                (float) (bgColor >> 24 & 255) / 255.0F
        ).endVertex();
        buf.pos(left, top, 0.0D).color(
                (float) (bgColor >> 16 & 255) / 255.0F,
                (float) (bgColor >> 8 & 255) / 255.0F,
                (float) (bgColor & 255) / 255.0F,
                (float) (bgColor >> 24 & 255) / 255.0F
        ).endVertex();
        tess.draw();

        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }
}
