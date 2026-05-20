package thaumcraft.client.renderers.tile;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.renderers.models.ModelEldritchCap;
import thaumcraft.common.config.Config;
import thaumcraft.common.tiles.TileEldritchObelisk;

public class TileEldritchObeliskRenderer extends TileEntitySpecialRenderer<TileEldritchObelisk> {
    private static final ResourceLocation SIDE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/obelisk_side.png");
    private static final ResourceLocation CAP_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/obelisk_cap.png");
    private static final ResourceLocation SIDE_TEXTURE_OUTER =
            new ResourceLocation("thaumcraft", "textures/models/obelisk_side_2.png");
    private static final ResourceLocation CAP_TEXTURE_OUTER =
            new ResourceLocation("thaumcraft", "textures/models/obelisk_cap_2.png");
    private static final ResourceLocation TUNNEL_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/tunnel.png");
    private static final ResourceLocation FIELD_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/particlefield.png");
    private static final ResourceLocation FIELD_TEXTURE_FALLBACK =
            new ResourceLocation("thaumcraft", "textures/misc/particlefield32.png");
    private static final ModelEldritchCap CAP_MODEL = new ModelEldritchCap();

    @Override
    public void render(TileEldritchObelisk tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        boolean outer = tile.getWorld() != null && tile.getWorld().provider.getDimension() == Config.dimensionOuterId;
        ResourceLocation sideTexture = outer ? SIDE_TEXTURE_OUTER : SIDE_TEXTURE;
        ResourceLocation capTexture = outer ? CAP_TEXTURE_OUTER : CAP_TEXTURE;
        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        float bob = (float) Math.sin(ticks / 10.0F) * 0.1F + 0.1F;
        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
        boolean inRange = tile.getWorld() != null
                && viewer != null
                && tile.getPos().distanceSq(viewer.posX, viewer.posY, viewer.posZ) < 512.0D;
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
        GlStateManager.translate(x + 0.5D, y + bob + 1.0D, z + 0.5D);
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);

        bindTexture(sideTexture);
        renderSides(0.48F, 3.0F);

        bindTexture(capTexture);
        renderObeliskCapPair();

        renderSideFields(inRange, time, outer, viewX, viewY, viewZ);

        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private static void renderObeliskCapPair() {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        CAP_MODEL.renderCap();
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, 3.0D, 0.0D);
        GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
        CAP_MODEL.renderCap();
        GlStateManager.popMatrix();
    }

    private void renderSideFields(boolean inRange, float time, boolean outer, double viewX, double viewY, double viewZ) {
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            renderFieldLayersForFacing(facing, inRange, time, outer, viewX, viewY, viewZ);
        }
    }

    private void renderFieldLayersForFacing(
            EnumFacing facing, boolean inRange, float time, boolean outer, double viewX, double viewY, double viewZ) {
        float offset = facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 0.499F : -0.499F;
        float min = -0.5F;
        float max = 0.5F;
        float y0 = 0.0F;
        float y1 = 3.0F;

        if (!inRange) {
            bindTexture(FIELD_TEXTURE_FALLBACK);
            GlStateManager.blendFunc(770, 771);
            drawSideFieldQuad(facing, offset, min, max, y0, y1, 0.0F, 0.0F, 1.0F, 1.0F, 0.55F, 0.55F, 0.55F, 1.0F);
            return;
        }

        Random random = new Random(31100L + facing.getIndex() * 17L);
        for (int i = 0; i < 16; i++) {
            float layerDepth = 16.0F - i;
            float bright = 1.0F / (layerDepth + 1.0F);
            float uvScale = 0.0625F;

            if (i == 0) {
                bindTexture(TUNNEL_TEXTURE);
                GlStateManager.blendFunc(770, 771);
                bright = 0.1F;
                layerDepth = 65.0F;
                uvScale = 0.125F;
            } else {
                bindTexture(FIELD_TEXTURE);
                GlStateManager.blendFunc(1, 1);
                if (i == 1) {
                    uvScale = 0.5F;
                }
            }

            float uvShift = time + (float) (i * i * 4321 + i * 9) * 2.0F;
            float parallaxScale = uvScale * (0.75F + layerDepth * 0.015625F);
            float[] parallax = parallaxOffsets(facing, viewX, viewY, viewZ, parallaxScale);
            float uShift = uvShift * uvScale + parallax[0];
            float vShift = uvShift * uvScale + parallax[1];

            float r;
            float g;
            float b;
            if (i == 0) {
                r = 1.0F;
                g = 1.0F;
                b = 1.0F;
            } else {
                r = (random.nextFloat() * 0.5F + 0.1F) * bright;
                g = (random.nextFloat() * 0.5F + 0.4F) * bright;
                b = (random.nextFloat() * 0.5F + 0.5F) * bright;
                if (outer) {
                    r *= 0.9F;
                    g *= 0.75F;
                    b *= 1.15F;
                }
            }
            drawSideFieldQuad(facing, offset, min, max, y0, y1, uShift, vShift, uvScale, uvScale, r, g, b, 1.0F);
        }
    }

    private static float[] parallaxOffsets(EnumFacing facing, double viewX, double viewY, double viewZ, float scale) {
        float rotX = ActiveRenderInfo.getRotationX();
        float rotZ = ActiveRenderInfo.getRotationZ();
        float rotYZ = ActiveRenderInfo.getRotationYZ();
        float rotXY = ActiveRenderInfo.getRotationXY();
        float rotXZ = ActiveRenderInfo.getRotationXZ();
        float u;
        float v;
        switch (facing) {
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
        return new float[]{u * scale, v * scale};
    }

    private static void drawSideFieldQuad(EnumFacing facing, float offset, float min, float max, float y0, float y1,
                                          float uShift, float vShift, float uScale, float vScale,
                                          float r, float g, float b, float a) {
        float u0 = uShift;
        float v0 = vShift;
        float u1 = uShift + uScale;
        float v1 = vShift + vScale;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        switch (facing) {
            case NORTH:
                v(buf, min, y1, offset, u1, v1, r, g, b, a);
                v(buf, min, y0, offset, u1, v0, r, g, b, a);
                v(buf, max, y0, offset, u0, v0, r, g, b, a);
                v(buf, max, y1, offset, u0, v1, r, g, b, a);
                break;
            case SOUTH:
                v(buf, min, y0, offset, u1, v1, r, g, b, a);
                v(buf, min, y1, offset, u1, v0, r, g, b, a);
                v(buf, max, y1, offset, u0, v0, r, g, b, a);
                v(buf, max, y0, offset, u0, v1, r, g, b, a);
                break;
            case WEST:
                v(buf, offset, y1, min, u1, v1, r, g, b, a);
                v(buf, offset, y1, max, u1, v0, r, g, b, a);
                v(buf, offset, y0, max, u0, v0, r, g, b, a);
                v(buf, offset, y0, min, u0, v1, r, g, b, a);
                break;
            case EAST:
                v(buf, offset, y0, min, u1, v1, r, g, b, a);
                v(buf, offset, y0, max, u1, v0, r, g, b, a);
                v(buf, offset, y1, max, u0, v0, r, g, b, a);
                v(buf, offset, y1, min, u0, v1, r, g, b, a);
                break;
            default:
                break;
        }

        tess.draw();
    }

    private static void renderSides(float half, float height) {
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        face(buf, -half, 0.0F, -half, half, height, -half, 0.0F, 1.0F);
        face(buf, -half, 0.0F, half, half, height, half, 0.0F, 1.0F);
        faceZ(buf, -half, 0.0F, -half, -half, height, half, 0.0F, 1.0F);
        faceZ(buf, half, 0.0F, -half, half, height, half, 0.0F, 1.0F);

        tess.draw();
    }

    private static void face(BufferBuilder buf, float x0, float y0, float z, float x1, float y1, float z1, float v0, float v1) {
        buf.pos(x0, y1, z).tex(0.0D, v0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(x0, y0, z).tex(0.0D, v1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(x1, y0, z1).tex(1.0D, v1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(x1, y1, z1).tex(1.0D, v0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
    }

    private static void faceZ(BufferBuilder buf, float x, float y0, float z0, float x1, float y1, float z1, float v0, float v1) {
        buf.pos(x, y1, z0).tex(0.0D, v0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(x, y0, z0).tex(0.0D, v1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(x1, y0, z1).tex(1.0D, v1).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        buf.pos(x1, y1, z1).tex(1.0D, v0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
    }

    private static void v(BufferBuilder buf, float x, float y, float z,
                          float u, float v, float r, float g, float b, float a) {
        buf.pos(x, y, z).tex(u, v).color(r, g, b, a).endVertex();
    }
}
