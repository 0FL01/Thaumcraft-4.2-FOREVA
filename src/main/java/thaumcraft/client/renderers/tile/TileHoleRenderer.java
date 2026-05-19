package thaumcraft.client.renderers.tile;

import java.util.Random;
import net.minecraft.client.renderer.ActiveRenderInfo;
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
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileHole;

public class TileHoleRenderer extends TileEntitySpecialRenderer<TileHole> {
    private static final ResourceLocation TUNNEL = new ResourceLocation("thaumcraft", "textures/misc/tunnel.png");
    private static final ResourceLocation PARTICLE_FIELD = new ResourceLocation("thaumcraft", "textures/misc/particlefield.png");
    private static final ResourceLocation PARTICLE_FIELD_FALLBACK = new ResourceLocation("thaumcraft", "textures/misc/particlefield32.png");
    private static final float OFFSET_NEAR = 0.001F;
    private static final float OFFSET_FAR = 0.999F;
    private static final long FIELD_COLOR_SEED = 31100L;

    @Override
    public void render(TileHole tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
        boolean inRange = viewer != null && tile.getPos().distanceSq(viewer.posX, viewer.posY, viewer.posZ) < 512.0D;
        float time = (float) (System.currentTimeMillis() % 700000L) / 250000.0F;
        double viewX = 0.0D;
        double viewY = 0.0D;
        double viewZ = 0.0D;
        if (viewer != null) {
            viewX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks;
            viewY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks;
            viewZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableFog();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);

        for (EnumFacing face : EnumFacing.VALUES) {
            if (shouldRenderFace(tile.getPos(), face)) {
                renderFace(face, time, inRange, viewX, viewY, viewZ);
            }
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableFog();
        GlStateManager.popMatrix();
    }

    private boolean shouldRenderFace(BlockPos origin, EnumFacing face) {
        BlockPos adjPos = origin.offset(face);
        IBlockState adjState = getWorld().getBlockState(adjPos);
        return adjState.isOpaqueCube() && adjState.getBlock() != ConfigBlocks.blockHole;
    }

    private void renderFace(EnumFacing face, float time, boolean inRange, double viewX, double viewY, double viewZ) {
        float axisOffset = face.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? OFFSET_FAR : OFFSET_NEAR;
        if (!inRange) {
            bindTexture(PARTICLE_FIELD_FALLBACK);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            drawFace(face, axisOffset,
                    0.5F, 0.5F, 0.5F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            return;
        }

        Random random = new Random(FIELD_COLOR_SEED);
        for (int i = 0; i < 16; i++) {
            float layerDepth = 16.0F - i;
            float shade = 1.0F / (layerDepth + 1.0F);
            float uvScale = 0.0625F;
            float uvShift = time;

            if (i == 0) {
                bindTexture(TUNNEL);
                layerDepth = 65.0F;
                uvScale = 0.125F;
                shade = 0.1F;
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
            } else if (i == 1) {
                bindTexture(PARTICLE_FIELD);
                uvScale = 0.5F;
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(1, 1);
            } else {
                bindTexture(PARTICLE_FIELD);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(1, 1);
            }

            uvShift += (float) (i * i * 4321 + i * 9) * 2.0F;
            float parallaxScale = uvScale * (0.75F + layerDepth * 0.015625F);
            float[] parallax = parallaxOffsets(face, viewX, viewY, viewZ, parallaxScale, faceParallaxSign(face));
            float uShift = uvShift * uvScale + parallax[0];
            float vShift = uvShift * uvScale + parallax[1];

            float r = i == 0 ? 1.0F : (random.nextFloat() * 0.5F + 0.1F);
            float g = i == 0 ? 1.0F : (random.nextFloat() * 0.5F + 0.4F);
            float b = i == 0 ? 1.0F : (random.nextFloat() * 0.5F + 0.5F);
            r *= shade;
            g *= shade;
            b *= shade;

            drawFace(face, axisOffset, r, g, b, 1.0F, uShift, vShift, uvScale, uvScale);
        }

        GlStateManager.disableBlend();
    }

    private static float[] parallaxOffsets(
            EnumFacing face, double viewX, double viewY, double viewZ, float scale, float sign) {
        float rotX = ActiveRenderInfo.getRotationX();
        float rotZ = ActiveRenderInfo.getRotationZ();
        float rotYZ = ActiveRenderInfo.getRotationYZ();
        float rotXY = ActiveRenderInfo.getRotationXY();
        float rotXZ = ActiveRenderInfo.getRotationXZ();

        float u;
        float v;
        switch (face) {
            case UP:
            case DOWN:
                u = (float) (viewX * rotX + viewZ * rotYZ);
                v = (float) (viewX * rotZ + viewZ * rotXY);
                break;
            case NORTH:
            case SOUTH:
                u = (float) (viewX * rotX + viewY * rotXZ);
                v = (float) (viewX * rotZ + viewY * rotXY);
                break;
            case WEST:
            case EAST:
                u = (float) (viewZ * rotYZ + viewY * rotXZ);
                v = (float) (viewZ * rotXY + viewY * rotX);
                break;
            default:
                u = 0.0F;
                v = 0.0F;
        }
        return new float[]{u * scale * sign, v * scale * sign};
    }

    private static float faceParallaxSign(EnumFacing face) {
        return face.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? -1.0F : 1.0F;
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
