package thaumcraft.client.renderers.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.tiles.TileEldritchNothing;

import java.util.Random;

public class TileEldritchNothingRenderer extends TileEntitySpecialRenderer<TileEldritchNothing> {
    private static final ResourceLocation TUNNEL =
            new ResourceLocation("thaumcraft", "textures/misc/tunnel.png");
    private static final ResourceLocation PARTICLE =
            new ResourceLocation("thaumcraft", "textures/misc/particlefield.png");
    private static final ResourceLocation PARTICLE_FALLBACK =
            new ResourceLocation("thaumcraft", "textures/misc/particlefield32.png");

    @Override
    public void render(TileEldritchNothing tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
        boolean inRange = viewer != null && tile.getPos().distanceSq(viewer.posX, viewer.posY, viewer.posZ) < 512.0D;
        float time = (tile.getWorld().getTotalWorldTime() + partialTicks) / 20.0F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableFog();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);

        for (EnumFacing face : EnumFacing.VALUES) {
            if (shouldRenderFace(tile.getPos(), face)) {
                renderFace(face, time, inRange);
            }
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableFog();
        GlStateManager.popMatrix();
    }

    private boolean shouldRenderFace(BlockPos origin, EnumFacing face) {
        IBlockState adjacent = getWorld().getBlockState(origin.offset(face));
        return !adjacent.isOpaqueCube();
    }

    private void renderFace(EnumFacing face, float time, boolean inRange) {
        if (!inRange) {
            bindTexture(PARTICLE_FALLBACK);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            drawFace(face, face.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 0.999F : 0.001F,
                    0.6F, 0.6F, 0.6F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            return;
        }

        Random random = new Random(31100L + face.getIndex() * 17L);
        for (int i = 0; i < 16; i++) {
            float layer = 16.0F - i;
            float bright = 1.0F / (layer + 1.0F);
            float uvScale = i == 0 ? 0.125F : (i == 1 ? 0.5F : 0.0625F);
            float uvShift = (time / (i == 0 ? 2.5F : 1.25F)) + i * 0.11F;
            float offset = face.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE
                    ? 0.999F - i * 0.00035F
                    : 0.001F + i * 0.00035F;

            if (i == 0) {
                bindTexture(TUNNEL);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
            } else {
                bindTexture(PARTICLE);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(1, 1);
            }

            float r = i == 0 ? 1.0F : (random.nextFloat() * 0.5F + 0.1F) * bright;
            float g = i == 0 ? 1.0F : (random.nextFloat() * 0.5F + 0.4F) * bright;
            float b = i == 0 ? 1.0F : (random.nextFloat() * 0.5F + 0.5F) * bright;
            float a = i == 0 ? 0.9F : 0.35F;

            drawFace(face, offset, r, g, b, a, uvShift, uvShift, uvScale, uvScale);
        }

        GlStateManager.disableBlend();
    }

    private void drawFace(EnumFacing face, float axisOffset, float r, float g, float b, float a,
                          float uShift, float vShift, float uScale, float vScale) {
        float u0 = uShift;
        float v0 = vShift;
        float u1 = uShift + uScale;
        float v1 = vShift + vScale;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        switch (face) {
            case UP:
                v(buf, 0.0F, axisOffset, 1.0F, u1, v1, r, g, b, a);
                v(buf, 0.0F, axisOffset, 0.0F, u1, v0, r, g, b, a);
                v(buf, 1.0F, axisOffset, 0.0F, u0, v0, r, g, b, a);
                v(buf, 1.0F, axisOffset, 1.0F, u0, v1, r, g, b, a);
                break;
            case DOWN:
                v(buf, 0.0F, axisOffset, 0.0F, u1, v1, r, g, b, a);
                v(buf, 0.0F, axisOffset, 1.0F, u1, v0, r, g, b, a);
                v(buf, 1.0F, axisOffset, 1.0F, u0, v0, r, g, b, a);
                v(buf, 1.0F, axisOffset, 0.0F, u0, v1, r, g, b, a);
                break;
            case NORTH:
                v(buf, 0.0F, 1.0F, axisOffset, u1, v1, r, g, b, a);
                v(buf, 0.0F, 0.0F, axisOffset, u1, v0, r, g, b, a);
                v(buf, 1.0F, 0.0F, axisOffset, u0, v0, r, g, b, a);
                v(buf, 1.0F, 1.0F, axisOffset, u0, v1, r, g, b, a);
                break;
            case SOUTH:
                v(buf, 0.0F, 0.0F, axisOffset, u1, v1, r, g, b, a);
                v(buf, 0.0F, 1.0F, axisOffset, u1, v0, r, g, b, a);
                v(buf, 1.0F, 1.0F, axisOffset, u0, v0, r, g, b, a);
                v(buf, 1.0F, 0.0F, axisOffset, u0, v1, r, g, b, a);
                break;
            case WEST:
                v(buf, axisOffset, 1.0F, 0.0F, u1, v1, r, g, b, a);
                v(buf, axisOffset, 1.0F, 1.0F, u1, v0, r, g, b, a);
                v(buf, axisOffset, 0.0F, 1.0F, u0, v0, r, g, b, a);
                v(buf, axisOffset, 0.0F, 0.0F, u0, v1, r, g, b, a);
                break;
            case EAST:
                v(buf, axisOffset, 0.0F, 0.0F, u1, v1, r, g, b, a);
                v(buf, axisOffset, 0.0F, 1.0F, u1, v0, r, g, b, a);
                v(buf, axisOffset, 1.0F, 1.0F, u0, v0, r, g, b, a);
                v(buf, axisOffset, 1.0F, 0.0F, u0, v1, r, g, b, a);
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
