package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.tiles.TileBellows;

public class TileBellowsRenderer extends TileEntitySpecialRenderer<TileBellows> {
    private static final ResourceLocation BELLOWS =
            new ResourceLocation("thaumcraft", "textures/models/bellows.png");

    @Override
    public void render(TileBellows tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float inflate = Math.max(0.35F, Math.min(1.0F, tile.inflation));
        float front = 0.22F + inflate * 0.32F;
        float back = -0.22F - inflate * 0.08F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        rotateFacing(EnumFacing.byIndex(tile.orientation));
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        bindTexture(BELLOWS);

        drawBellows(front, back);

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private static void rotateFacing(EnumFacing face) {
        if (face == null) return;
        switch (face) {
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

    private static void drawBellows(float front, float back) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        float half = 0.36F;
        float height = 0.34F;

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        quad(buf, -half, -height, back, half, height, front);
        quad(buf, -half, -height, front, half, height, back);
        tess.draw();
    }

    private static void quad(BufferBuilder buf, float x0, float y0, float z0, float x1, float y1, float z1) {
        buf.pos(x0, y1, z0).tex(0.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(x0, y0, z0).tex(0.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(x1, y0, z1).tex(1.0D, 1.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(x1, y1, z1).tex(1.0D, 0.0D).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
    }
}
