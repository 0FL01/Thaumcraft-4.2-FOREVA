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
        rotateByOrientation(tile.orientation);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        bindTexture(TEXTURE);

        drawCrossedPillar(0.36F, 2.0F, 0.0F, 1.0F, 0.0F, 1.0F);

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private static void rotateByOrientation(byte orientation) {
        if (orientation == 3) {
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        } else if (orientation == 4) {
            GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
        } else if (orientation == 5) {
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        }
    }

    private static void drawCrossedPillar(float halfWidth, float height, float u0, float u1, float v0, float v1) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        quad(buf, -halfWidth, 0.0F, 0.0F, halfWidth, height, 0.0F, u0, u1, v0, v1);
        quad(buf, 0.0F, 0.0F, -halfWidth, 0.0F, height, halfWidth, u0, u1, v0, v1);

        tess.draw();
    }

    private static void quad(BufferBuilder buf,
                             float x0, float y0, float z0,
                             float x1, float y1, float z1,
                             float u0, float u1, float v0, float v1) {
        buf.pos(x0, y1, z0).tex(u0, v0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(x0, y0, z0).tex(u0, v1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(x1, y0, z1).tex(u1, v1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(x1, y1, z1).tex(u1, v0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
    }
}
