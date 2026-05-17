package thaumcraft.client.renderers.tile;

import java.util.Random;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.tiles.TileMirror;
import thaumcraft.common.tiles.TileMirrorEssentia;

public class TileMirrorRenderer extends TileEntitySpecialRenderer<TileEntity> {
    private static final ResourceLocation TUNNEL = new ResourceLocation("thaumcraft", "textures/misc/tunnel.png");
    private static final ResourceLocation PARTICLE = new ResourceLocation("thaumcraft", "textures/misc/particlefield.png");
    private static final float INSET = 0.1875F;
    private static final float OFFSET_NEAR = 0.01F;
    private static final float OFFSET_FAR = 0.99F;

    @Override
    public void render(TileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null || !isLinked(tile)) {
            return;
        }

        EnumFacing facing = EnumFacing.byIndex(tile.getBlockMetadata() % 6);
        if (facing == null) {
            facing = EnumFacing.NORTH;
        }

        float jitter = (tile instanceof TileMirror)
                ? Math.min(0.4F, ((TileMirror) tile).instability / 120.0F)
                : 0.0F;
        float overlayAlpha = 0.70F + jitter;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();

        bindTexture(TUNNEL);
        GlStateManager.blendFunc(770, 771);
        drawInsetFace(facing, isPositiveFace(facing) ? OFFSET_FAR : OFFSET_NEAR, 1.0F, 1.0F, 1.0F, overlayAlpha);

        bindTexture(PARTICLE);
        GlStateManager.blendFunc(1, 1);
        Random rand = new Random(tile.getPos().toLong() ^ 0x1F1F1F1FL);
        float r = 0.35F + rand.nextFloat() * 0.25F;
        float g = 0.35F + rand.nextFloat() * 0.35F;
        float b = 0.75F + rand.nextFloat() * 0.20F;
        drawInsetFace(facing, isPositiveFace(facing) ? OFFSET_FAR - 0.002F : OFFSET_NEAR + 0.002F,
                r, g, b, 0.45F + jitter * 0.5F);

        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private static boolean isLinked(TileEntity tile) {
        if (tile instanceof TileMirror) {
            return ((TileMirror) tile).linked;
        }
        if (tile instanceof TileMirrorEssentia) {
            return ((TileMirrorEssentia) tile).linked;
        }
        return false;
    }

    private static boolean isPositiveFace(EnumFacing face) {
        return face.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE;
    }

    private static void drawInsetFace(EnumFacing face, float axisOffset, float r, float g, float b, float a) {
        float min = INSET;
        float max = 1.0F - INSET;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        switch (face) {
            case UP:
                v(buf, min, axisOffset, max, 0.0F, 1.0F, r, g, b, a);
                v(buf, min, axisOffset, min, 0.0F, 0.0F, r, g, b, a);
                v(buf, max, axisOffset, min, 1.0F, 0.0F, r, g, b, a);
                v(buf, max, axisOffset, max, 1.0F, 1.0F, r, g, b, a);
                break;
            case DOWN:
                v(buf, min, axisOffset, min, 0.0F, 1.0F, r, g, b, a);
                v(buf, min, axisOffset, max, 0.0F, 0.0F, r, g, b, a);
                v(buf, max, axisOffset, max, 1.0F, 0.0F, r, g, b, a);
                v(buf, max, axisOffset, min, 1.0F, 1.0F, r, g, b, a);
                break;
            case NORTH:
                v(buf, min, max, axisOffset, 0.0F, 1.0F, r, g, b, a);
                v(buf, min, min, axisOffset, 0.0F, 0.0F, r, g, b, a);
                v(buf, max, min, axisOffset, 1.0F, 0.0F, r, g, b, a);
                v(buf, max, max, axisOffset, 1.0F, 1.0F, r, g, b, a);
                break;
            case SOUTH:
                v(buf, min, min, axisOffset, 0.0F, 1.0F, r, g, b, a);
                v(buf, min, max, axisOffset, 0.0F, 0.0F, r, g, b, a);
                v(buf, max, max, axisOffset, 1.0F, 0.0F, r, g, b, a);
                v(buf, max, min, axisOffset, 1.0F, 1.0F, r, g, b, a);
                break;
            case WEST:
                v(buf, axisOffset, max, min, 0.0F, 1.0F, r, g, b, a);
                v(buf, axisOffset, max, max, 0.0F, 0.0F, r, g, b, a);
                v(buf, axisOffset, min, max, 1.0F, 0.0F, r, g, b, a);
                v(buf, axisOffset, min, min, 1.0F, 1.0F, r, g, b, a);
                break;
            case EAST:
                v(buf, axisOffset, min, min, 0.0F, 1.0F, r, g, b, a);
                v(buf, axisOffset, min, max, 0.0F, 0.0F, r, g, b, a);
                v(buf, axisOffset, max, max, 1.0F, 0.0F, r, g, b, a);
                v(buf, axisOffset, max, min, 1.0F, 1.0F, r, g, b, a);
                break;
            default:
                break;
        }

        tess.draw();
    }

    private static void v(BufferBuilder buf, float x, float y, float z,
                          float u, float v, float r, float g, float b, float a) {
        buf.pos(x, y, z).tex(u, v).color(r, g, b, a).endVertex();
    }
}
