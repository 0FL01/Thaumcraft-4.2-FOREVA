package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.tiles.TileCrucible;

public class TileCrucibleRenderer extends TileEntitySpecialRenderer<TileCrucible> {

    @Override
    public void render(TileCrucible tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }
        if (!tile.hasWater()) {
            return;
        }
        float fluidHeight = tile.getFluidHeight();
        TextureAtlasSprite water = Minecraft.getMinecraft().getBlockRendererDispatcher()
                .getBlockModelShapes()
                .getTexture(Blocks.WATER.getDefaultState());
        if (water == null) {
            return;
        }

        float raw = TileRenderHelper.clamp01((float) tile.tagAmount() / 100.0F);
        float recolor = raw > 0.0F ? 0.5F + raw / 2.0F : 0.0F;
        float r = 1.0F;
        float g = 1.0F - recolor / 3.0F;
        float b = 1.0F - recolor;
        float a = 1.0F - recolor / 2.0F;
        int color = ((int) (a * 255.0F) << 24)
                | ((int) (r * 255.0F) << 16)
                | ((int) (g * 255.0F) << 8)
                | (int) (b * 255.0F);
        int packedLight = tile.getWorld().getCombinedLight(tile.getPos(), 0);
        int lightU = (packedLight >> 16) & 0xFFFF;
        int lightV = packedLight & 0xFFFF;
        boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + fluidHeight, z + 0.5D);
        GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        drawFluidSurface(0.5F, color, water.getMinU(), water.getMaxU(), water.getMinV(), water.getMaxV(), lightU, lightV);
        if (!blendEnabled) {
            GlStateManager.disableBlend();
        }
        GlStateManager.popMatrix();
    }

    private static void drawFluidSurface(float half, int argb, float u0, float u1, float v0, float v1,
                                         int lightU, int lightV) {
        float a = ((argb >> 24) & 0xFF) / 255.0F;
        float r = ((argb >> 16) & 0xFF) / 255.0F;
        float g = ((argb >> 8) & 0xFF) / 255.0F;
        float b = (argb & 0xFF) / 255.0F;
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        addFluidVertex(buffer, -half, -half, u0, v1, r, g, b, a, lightU, lightV);
        addFluidVertex(buffer, half, -half, u1, v1, r, g, b, a, lightU, lightV);
        addFluidVertex(buffer, half, half, u1, v0, r, g, b, a, lightU, lightV);
        addFluidVertex(buffer, -half, half, u0, v0, r, g, b, a, lightU, lightV);
        Tessellator.getInstance().draw();
    }

    private static void addFluidVertex(BufferBuilder buffer, float x, float y, float u, float v,
                                       float r, float g, float b, float a, int lightU, int lightV) {
        buffer.pos(x, y, 0.0D)
                .color(r, g, b, a)
                .tex(u, v)
                .lightmap(lightU, lightV)
                .normal(0.0F, 0.0F, 1.0F)
                .endVertex();
    }
}
