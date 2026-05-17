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

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        float pulse = 0.92F + (float) Math.sin(ticks / 8.0F) * 0.08F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        orient(EnumFacing.byIndex(tile.getFacing()));
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        bindTexture(TEXTURE);
        drawCross(0.42F, pulse);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
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

    private static void drawCross(float half, float pulse) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        float alpha = 0.85F;
        quad(buf, -half, -half * pulse, 0.0F, half, half * pulse, 0.0F, alpha);
        quad(buf, 0.0F, -half * pulse, -half, 0.0F, half * pulse, half, alpha);
        tess.draw();
    }

    private static void quad(BufferBuilder buf,
                             float x0, float y0, float z0,
                             float x1, float y1, float z1,
                             float alpha) {
        buf.pos(x0, y1, z0).tex(0.0D, 0.0D).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buf.pos(x0, y0, z0).tex(0.0D, 1.0D).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buf.pos(x1, y0, z1).tex(1.0D, 1.0D).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
        buf.pos(x1, y1, z1).tex(1.0D, 0.0D).color(1.0F, 1.0F, 1.0F, alpha).endVertex();
    }
}
