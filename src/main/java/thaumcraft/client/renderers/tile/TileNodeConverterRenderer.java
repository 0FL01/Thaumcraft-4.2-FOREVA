package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
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
        if (tile == null) {
            return;
        }

        float progress = Math.min(50.0F, tile.count) / 137.0F;
        float[] color = statusColor(tile.status);
        float ticks = Minecraft.getMinecraft().player == null
                ? TileRenderHelper.ticks(tile, partialTicks)
                : Minecraft.getMinecraft().player.ticksExisted + partialTicks;
        float previousLightX = OpenGlHelper.lastBrightnessX;
        float previousLightY = OpenGlHelper.lastBrightnessY;
        int blockLight = tile.getWorld() == null ? -1 : tile.getWorld().getCombinedLight(tile.getPos(), 0);

        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(x + 0.5D, y + 1.0D, z + 0.5D);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            applyPackedLight(blockLight);
            bindTexture(BASE_TEXTURE);
            model.renderLock(MODEL_SCALE);
            if (tile.getWorld() != null) {
                applyGlowLight(progress, ticks);
            }
            bindTexture(OVER_TEXTURE);
            GlStateManager.color(color[0], color[1], color[2], 1.0F);
            model.renderLock(MODEL_SCALE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            for (int i = 0; i < 4; i++) {
                GlStateManager.pushMatrix();
                try {
                    GlStateManager.rotate(i * 90.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.translate(0.0D, 0.0D, progress);

                    applyPackedLight(blockLight);
                    bindTexture(BASE_TEXTURE);
                    model.renderPiston(MODEL_SCALE);
                    if (tile.getWorld() != null) {
                        applyGlowLight(progress, ticks + i * 5.0F);
                    }
                    bindTexture(OVER_TEXTURE);
                    GlStateManager.color(color[0], color[1], color[2], 1.0F);
                    model.renderPiston(MODEL_SCALE);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                } finally {
                    GlStateManager.popMatrix();
                }
            }
        } finally {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, previousLightX, previousLightY);
            GlStateManager.popMatrix();
        }
    }

    private static float[] statusColor(int status) {
        if (status == 2) {
            return new float[]{1.0F, 0.0F, 0.3F};
        }
        if (status == 1) {
            return new float[]{1.0F, 0.6F, 0.1F};
        }
        return new float[]{0.5F, 1.0F, 0.5F};
    }

    private static void applyGlowLight(float progress, float ticks) {
        float pulse = (float) Math.sin(ticks / 3.0F) * 0.1F + 0.9F;
        int glow = 50 + (int) (170.0F * (progress * 2.5F * pulse));
        applyPackedLight(glow);
    }

    private static void applyPackedLight(int light) {
        if (light >= 0) {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
                    light & 0xFFFF, light >>> 16);
        }
    }
}
