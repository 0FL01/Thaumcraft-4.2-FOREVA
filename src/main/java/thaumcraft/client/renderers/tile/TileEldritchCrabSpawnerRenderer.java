package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.tiles.TileEldritchCrabSpawner;

public class TileEldritchCrabSpawnerRenderer extends TileEntitySpecialRenderer<TileEldritchCrabSpawner> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/crabvent.png");

    @Override
    public void render(TileEldritchCrabSpawner tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        orient(EnumFacing.byIndex(tile.getFacing()));
        GlStateManager.enableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        bindTexture(TEXTURE);
        renderVentGeometry();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    private static void orient(EnumFacing facing) {
        if (facing == null) {
            return;
        }
        switch (facing) {
            case DOWN:
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case UP:
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case NORTH:
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                break;
            case WEST:
                GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                break;
            case EAST:
                GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                break;
            case SOUTH:
            default:
                break;
        }
    }

    private static void renderVentGeometry() {
        drawTexturedCuboid(-0.30F, -0.30F, -0.05F, 0.30F, 0.30F, 0.05F);
        drawTexturedCuboid(-0.12F, -0.12F, 0.05F, 0.12F, 0.12F, 0.30F);
        drawTexturedCuboid(-0.20F, -0.04F, 0.02F, 0.20F, 0.04F, 0.18F);
        drawTexturedCuboid(-0.04F, -0.20F, 0.02F, 0.04F, 0.20F, 0.18F);
    }

    private static void drawTexturedCuboid(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

        face(buf, minX, maxY, maxZ, minX, minY, maxZ, maxX, minY, maxZ, maxX, maxY, maxZ, 0.0F, 1.0F, 0.0F, 1.0F, 0, 0, 1);
        face(buf, maxX, maxY, minZ, maxX, minY, minZ, minX, minY, minZ, minX, maxY, minZ, 0.0F, 1.0F, 0.0F, 1.0F, 0, 0, -1);
        face(buf, minX, maxY, minZ, minX, minY, minZ, minX, minY, maxZ, minX, maxY, maxZ, 0.0F, 1.0F, 0.0F, 1.0F, -1, 0, 0);
        face(buf, maxX, maxY, maxZ, maxX, minY, maxZ, maxX, minY, minZ, maxX, maxY, minZ, 0.0F, 1.0F, 0.0F, 1.0F, 1, 0, 0);
        face(buf, minX, maxY, minZ, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, 0.0F, 1.0F, 0.0F, 1.0F, 0, 1, 0);
        face(buf, minX, minY, maxZ, minX, minY, minZ, maxX, minY, minZ, maxX, minY, maxZ, 0.0F, 1.0F, 0.0F, 1.0F, 0, -1, 0);

        tess.draw();
    }

    private static void face(BufferBuilder buf,
                             float x1, float y1, float z1,
                             float x2, float y2, float z2,
                             float x3, float y3, float z3,
                             float x4, float y4, float z4,
                             float u0, float u1, float v0, float v1,
                             float nx, float ny, float nz) {
        buf.pos(x1, y1, z1).tex(u0, v0).normal(nx, ny, nz).endVertex();
        buf.pos(x2, y2, z2).tex(u0, v1).normal(nx, ny, nz).endVertex();
        buf.pos(x3, y3, z3).tex(u1, v1).normal(nx, ny, nz).endVertex();
        buf.pos(x4, y4, z4).tex(u1, v0).normal(nx, ny, nz).endVertex();
    }

}
