package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.ModelNodeStabilizer;
import thaumcraft.common.tiles.TileNodeConverter;

public class TileNodeConverterRenderer extends TileEntitySpecialRenderer<TileNodeConverter> {

    private static final ResourceLocation BASE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/node_converter.png");
    private static final ResourceLocation OVER_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/node_converter_over.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelNodeStabilizer model = new ModelNodeStabilizer();

    @Override
    public void render(TileNodeConverter tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        float progress = TileRenderHelper.clamp01(Math.min(50.0F, Math.max(0.0F, tile.count)) / 137.0F);
        int overlayColor = statusColor(tile.status);
        float pulse = 0.9F + (float) Math.sin(ticks / 3.0F) * 0.1F;
        float[] color = unpackRgb(overlayColor, pulse);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 1.0D, z + 0.5D);
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableCull();

        bindTexture(BASE_TEXTURE);
        model.renderLock(MODEL_SCALE);

        bindTexture(OVER_TEXTURE);
        GlStateManager.color(color[0], color[1], color[2], 1.0F);
        model.renderLock(MODEL_SCALE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        for (int i = 0; i < 4; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(i * 90.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate((ticks * (1.8F + i * 0.25F)) % 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0D, 0.0D, progress);

            bindTexture(BASE_TEXTURE);
            model.renderPiston(MODEL_SCALE);

            bindTexture(OVER_TEXTURE);
            float pistonPulse = 0.85F + (float) Math.sin((ticks + i * 4.0F) / 3.0F) * 0.1F;
            float[] pistonColor = unpackRgb(overlayColor, pistonPulse);
            GlStateManager.color(pistonColor[0], pistonColor[1], pistonColor[2], 1.0F);
            model.renderPiston(MODEL_SCALE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private static int statusColor(int status) {
        if (status == 2) {
            return 0xCCFF004D;
        }
        if (status == 1) {
            return 0xCCFF9920;
        }
        return 0xCC90FF90;
    }

    private static float[] unpackRgb(int color, float scale) {
        float r = ((color >> 16) & 0xFF) / 255.0F * scale;
        float g = ((color >> 8) & 0xFF) / 255.0F * scale;
        float b = (color & 0xFF) / 255.0F * scale;
        return new float[]{r, g, b};
    }
}
