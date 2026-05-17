package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.tiles.TileInfusionPillar;

public class TileInfusionPillarRenderer extends TileEntitySpecialRenderer<TileInfusionPillar> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/pillar.png");

    @Override
    public void render(TileInfusionPillar tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y, z + 0.5D);
        GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
        rotateByOrientation(tile.orientation);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        bindTexture(TEXTURE);

        drawPillarPrism(0.375F, 2.0F);

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private static void rotateByOrientation(byte orientation) {
        if (orientation == 3) {
            GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        } else if (orientation == 4) {
            GlStateManager.rotate(270.0F, 0.0F, 0.0F, 1.0F);
        } else if (orientation == 5) {
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        }
    }

    private static void drawPillarPrism(float half, float height) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

        // front
        face(buf, -half, 0.0F, half, half, height, half, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F);
        // back
        face(buf, half, 0.0F, -half, -half, height, -half, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, -1.0F);
        // left
        face(buf, -half, 0.0F, -half, -half, height, half, 0.0F, 1.0F, 1.0F, 0.0F, -1.0F, 0.0F, 0.0F);
        // right
        face(buf, half, 0.0F, half, half, height, -half, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F);
        // top
        topBottomFace(buf, -half, half, height, true);
        // bottom
        topBottomFace(buf, -half, half, 0.0F, false);

        tess.draw();
    }

    private static void face(BufferBuilder buf,
                             float x0, float y0, float z0,
                             float x1, float y1, float z1,
                             float u0, float u1, float v0, float v1,
                             float nx, float ny, float nz) {
        buf.pos(x0, y1, z0).tex(u0, v1).normal(nx, ny, nz).endVertex();
        buf.pos(x0, y0, z0).tex(u0, v0).normal(nx, ny, nz).endVertex();
        buf.pos(x1, y0, z1).tex(u1, v0).normal(nx, ny, nz).endVertex();
        buf.pos(x1, y1, z1).tex(u1, v1).normal(nx, ny, nz).endVertex();
    }

    private static void topBottomFace(BufferBuilder buf, float min, float max, float y, boolean top) {
        float ny = top ? 1.0F : -1.0F;
        if (top) {
            buf.pos(min, y, min).tex(0.0F, 0.0F).normal(0.0F, ny, 0.0F).endVertex();
            buf.pos(max, y, min).tex(1.0F, 0.0F).normal(0.0F, ny, 0.0F).endVertex();
            buf.pos(max, y, max).tex(1.0F, 1.0F).normal(0.0F, ny, 0.0F).endVertex();
            buf.pos(min, y, max).tex(0.0F, 1.0F).normal(0.0F, ny, 0.0F).endVertex();
        } else {
            buf.pos(min, y, max).tex(0.0F, 1.0F).normal(0.0F, ny, 0.0F).endVertex();
            buf.pos(max, y, max).tex(1.0F, 1.0F).normal(0.0F, ny, 0.0F).endVertex();
            buf.pos(max, y, min).tex(1.0F, 0.0F).normal(0.0F, ny, 0.0F).endVertex();
            buf.pos(min, y, min).tex(0.0F, 0.0F).normal(0.0F, ny, 0.0F).endVertex();
        }
    }
}
