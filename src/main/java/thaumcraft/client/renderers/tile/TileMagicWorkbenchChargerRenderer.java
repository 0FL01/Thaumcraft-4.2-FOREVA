package thaumcraft.client.renderers.tile;

import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.client.renderers.models.ModelMagicWorkbenchCharger;
import thaumcraft.common.tiles.TileMagicWorkbenchCharger;

public class TileMagicWorkbenchChargerRenderer extends TileEntitySpecialRenderer<TileMagicWorkbenchCharger> {
    private static final ResourceLocation RELAY_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/vis_relay.png");
    private static final float MODEL_SCALE = 0.0625F;

    private static final int[] RELAY_COLORS = {
            0xFFFF7E,
            0xFF8844,
            0x99CCFF,
            0x80FF80,
            0xCC99FF,
            0xAAAAAA
    };
    private final ModelMagicWorkbenchCharger model = new ModelMagicWorkbenchCharger();

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
        float[] crystalColor = unpackRgb(pulseColor);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableCull();
        bindTexture(RELAY_TEXTURE);

        model.renderRingFloat(MODEL_SCALE);
        GlStateManager.pushMatrix();
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(0.0F, 0.0F, 0.5F);
        for (int i = 0; i < 4; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(i * 90.0F, 0.0F, 0.0F, 1.0F);
            model.renderSupport(MODEL_SCALE);
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        float crystalPulse = pulse;
        GlStateManager.scale(crystalPulse, crystalPulse, crystalPulse);
        GlStateManager.color(crystalColor[0], crystalColor[1], crystalColor[2], 1.0F);
        model.renderCrystal(MODEL_SCALE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();

        GlStateManager.enableCull();
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

    private static float[] unpackRgb(int argb) {
        float r = ((argb >> 16) & 0xFF) / 255.0F;
        float g = ((argb >> 8) & 0xFF) / 255.0F;
        float b = (argb & 0xFF) / 255.0F;
        return new float[]{r, g, b};
    }
}
