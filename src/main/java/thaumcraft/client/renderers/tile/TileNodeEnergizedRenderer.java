package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.tiles.TileNodeEnergized;

public class TileNodeEnergizedRenderer extends TileEntitySpecialRenderer<TileNodeEnergized> {
    private static final ResourceLocation LIGHTNING_RING =
            new ResourceLocation("thaumcraft", "textures/items/lightningringv.png");
    private static final int RING_FRAMES = 16;

    @Override
    public void render(TileNodeEnergized tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        String nodeId = tile.getPos() == null ? "energized" : "energized:" + tile.getPos().toLong();
        TileNodeRenderer.renderNodeAt(
                tile.getAspects(),
                nodeId,
                tile.getNodeType(),
                tile.getNodeModifier(),
                x + 0.5D, y + 0.5D, z + 0.5D,
                partialTicks,
                1.0F);
        renderAnimatedRing(tile, x, y, z, partialTicks);
    }

    private void renderAnimatedRing(TileNodeEnergized tile, double x, double y, double z, float partialTicks) {
        int frames = Math.max(1, RING_FRAMES);
        int frame = (int) (((System.nanoTime() / 40_000_000L) + x) % frames);
        float u0 = frame / (float) frames;
        float u1 = (frame + 1) / (float) frames;
        float v0 = 0.0F;
        float v1 = 1.0F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 1);
        bindTexture(LIGHTNING_RING);
        GlStateManager.depthMask(false);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);

        TileRenderHelper.orientBillboardToPlayer();
        drawTexturedQuad(0.33F, u0, u1, v0, v1);

        GlStateManager.depthMask(true);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private static void drawTexturedQuad(float half, float u0, float u1, float v0, float v1) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buf.pos(-half, -half, 0.0D).tex(u0, v1).color(1.0F, 1.0F, 1.0F, 0.9F).endVertex();
        buf.pos(half, -half, 0.0D).tex(u1, v1).color(1.0F, 1.0F, 1.0F, 0.9F).endVertex();
        buf.pos(half, half, 0.0D).tex(u1, v0).color(1.0F, 1.0F, 1.0F, 0.9F).endVertex();
        buf.pos(-half, half, 0.0D).tex(u0, v0).color(1.0F, 1.0F, 1.0F, 0.9F).endVertex();
        tess.draw();
    }
}
