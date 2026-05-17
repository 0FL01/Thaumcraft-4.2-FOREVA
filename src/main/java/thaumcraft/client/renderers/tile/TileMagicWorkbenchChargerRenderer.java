package thaumcraft.client.renderers.tile;

import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.common.tiles.TileMagicWorkbenchCharger;

public class TileMagicWorkbenchChargerRenderer extends TileEntitySpecialRenderer<TileMagicWorkbenchCharger> {
    private static final ResourceLocation RELAY_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/vis_relay.png");

    private static final int[] RELAY_COLORS = {
            0xFFFF7E,
            0xFF8844,
            0x99CCFF,
            0x80FF80,
            0xCC99FF,
            0xAAAAAA
    };

    @Override
    public void render(TileMagicWorkbenchCharger tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        float pulse = 0.95F + (float) Math.sin(ticks / 2.0F) * 0.05F;
        int color = colorForIndex(tile.color);
        int pulseColor = withAlpha((int) (220.0F * pulse), color);
        if (!VisNetHandler.isNodeValid(tile.getParent())) {
            pulseColor = withAlpha((int) (110.0F * pulse), 0x777777);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        bindTexture(RELAY_TEXTURE);

        TileRenderHelper.drawTexturedQuad(0.26F, 0xCCFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
        for (int i = 0; i < 4; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(i * 90.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.0D, 0.16D, 0.0D);
            TileRenderHelper.drawTexturedQuad(0.10F, 0x99FFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
            GlStateManager.popMatrix();
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(0.88F, 0.88F, 0.88F);
        TileRenderHelper.drawTexturedQuad(0.16F, pulseColor, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.popMatrix();

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private static int colorForIndex(int index) {
        if (index >= 0 && index < RELAY_COLORS.length) {
            Color c = new Color(RELAY_COLORS[index]);
            return (c.getRed() << 16) | (c.getGreen() << 8) | c.getBlue();
        }
        return 0xFFFFFF;
    }

    private static int withAlpha(int alpha, int rgb) {
        int a = Math.max(0, Math.min(255, alpha));
        return (a << 24) | (rgb & 0x00FFFFFF);
    }
}
