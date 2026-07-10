package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.renderers.models.ModelNodeStabilizer;
import thaumcraft.common.tiles.TileNodeStabilizer;

public class TileNodeStabilizerRenderer extends TileEntitySpecialRenderer<TileNodeStabilizer> {

    private static final ResourceLocation BASE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/node_stabilizer.png");
    private static final ResourceLocation OVER_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/node_stabilizer_over.png");
    private static final ResourceLocation BUBBLE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/node_bubble.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelNodeStabilizer model = new ModelNodeStabilizer();

    @Override
    public void render(TileNodeStabilizer tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        float ticks = Minecraft.getMinecraft().player == null
                ? TileRenderHelper.ticks(tile, partialTicks)
                : Minecraft.getMinecraft().player.ticksExisted + partialTicks;
        int lock = resolveLock(tile);
        float previousLightX = OpenGlHelper.lastBrightnessX;
        float previousLightY = OpenGlHelper.lastBrightnessY;
        int blockLight = tile.getWorld() == null ? -1 : tile.getWorld().getCombinedLight(tile.getPos(), 0);

        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(x + 0.5D, y, z + 0.5D);
            GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            bindTexture(BASE_TEXTURE);
            model.renderLock(MODEL_SCALE);

            for (int i = 0; i < 4; i++) {
                GlStateManager.pushMatrix();
                try {
                    GlStateManager.rotate(i * 90.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.translate(0.0D, 0.0D, tile.count / 100.0D);

                    applyPackedLight(blockLight);
                    bindTexture(BASE_TEXTURE);
                    model.renderPiston(MODEL_SCALE);
                    if (lock == 2) {
                        GlStateManager.color(1.0F, 0.2F, 0.2F, 1.0F);
                    }
                    if (tile.getWorld() != null) {
                        float pulse = (float) Math.sin((ticks + i * 5.0F) / 3.0F) * 0.1F + 0.9F;
                        int glow = 50 + (int) (170.0F * (tile.count / 37.0F * pulse));
                        applyPackedLight(glow);
                    }
                    bindTexture(OVER_TEXTURE);
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

        if (tile.count > 0) {
            float bubblePulse = (float) Math.sin(ticks / 8.0F) * 0.1F + 0.5F;
            int bubbleAlpha = Math.round(Math.max(0.0F, Math.min(1.0F, tile.count / 37.0F * bubblePulse)) * 255.0F);
            int bubbleColor = (bubbleAlpha << 24) | (lock == 1 ? 0xFFFFFF : 0xFF4444);
            GlStateManager.pushMatrix();
            try {
                GlStateManager.alphaFunc(GL11.GL_GREATER, 1.0F / 255.0F);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                GlStateManager.depthMask(false);
                GlStateManager.translate(x + 0.5D, y + 1.5D, z + 0.5D);
                TileRenderHelper.orientBillboardToPlayer();
                bindTexture(BUBBLE_TEXTURE);
                TileRenderHelper.drawTexturedQuad(0.9F, bubbleColor, 0.0F, 1.0F, 0.0F, 1.0F);
            } finally {
                GlStateManager.depthMask(true);
                GlStateManager.disableBlend();
                GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
                GlStateManager.popMatrix();
            }
        }
    }

    private static int resolveLock(TileNodeStabilizer tile) {
        if (tile.getWorld() != null) {
            int meta = tile.getBlockType().getMetaFromState(tile.getWorld().getBlockState(tile.getPos()));
            return meta == 10 ? 2 : 1;
        }
        return tile.lock == 2 ? 2 : 1;
    }

    private static void applyPackedLight(int light) {
        if (light >= 0) {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
                    light & 0xFFFF, light >>> 16);
        }
    }
}
