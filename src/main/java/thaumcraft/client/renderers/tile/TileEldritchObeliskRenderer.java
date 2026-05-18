package thaumcraft.client.renderers.tile;

import java.util.Random;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
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

    @Override
    public void render(TileEldritchObelisk tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        boolean outer = tile.getWorld().provider.getDimension() == Config.dimensionOuterId;
        ResourceLocation sideTexture = outer ? SIDE_TEXTURE_OUTER : SIDE_TEXTURE;
        ResourceLocation capTexture = outer ? CAP_TEXTURE_OUTER : CAP_TEXTURE;
        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        float bob = (float) Math.sin(ticks / 10.0F) * 0.1F + 0.1F;
        boolean inRange = this.rendererDispatcher != null
                && this.rendererDispatcher.entity != null
                && this.rendererDispatcher.entity.getDistanceSq(tile.getPos()) < 512.0D;

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
        TileRenderHelper.drawTexturedQuad(0.52F, 0xFFFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.translate(0.0D, 3.0D, 0.0D);
        TileRenderHelper.drawTexturedQuad(0.52F, 0xFFFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.translate(0.0D, -3.0D, 0.0D);

        renderSideFields(inRange, ticks, outer);

        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void renderSideFields(boolean inRange, float ticks, boolean outer) {
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            renderFieldLayersForFacing(facing, inRange, ticks, outer);
        }
    }

    private void renderFieldLayersForFacing(EnumFacing facing, boolean inRange, float ticks, boolean outer) {
        float offset = facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? 0.499F : -0.499F;
        float min = -0.5F;
        float max = 0.5F;
        float y0 = 0.0F;
        float y1 = 3.0F;

        if (!inRange) {
            bindTexture(FIELD_TEXTURE_FALLBACK);
            GlStateManager.blendFunc(770, 771);
            drawSideFieldQuad(facing, offset, min, max, y0, y1, 0.0F, 1.0F, 0.55F, 0.55F, 0.55F, 1.0F);
            return;
        }

        Random random = new Random(31100L + facing.getIndex() * 17L);
        float time = ticks / 20.0F;
        for (int i = 0; i < 16; i++) {
            float layer = 16.0F - i;
            float bright = 1.0F / (layer + 1.0F);
            float uvScale = i == 0 ? 0.125F : (i == 1 ? 0.5F : 0.0625F);
            float uvShift = time + i * 0.125F;
            float alpha = i == 0 ? 1.0F : 0.9F;

            if (i == 0) {
                bindTexture(TUNNEL_TEXTURE);
                GlStateManager.blendFunc(770, 771);
                bright = 0.1F;
            } else {
                bindTexture(FIELD_TEXTURE);
                GlStateManager.blendFunc(1, 1);
            }

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
            drawSideFieldQuad(facing, offset, min, max, y0, y1, uvShift, uvScale, r, g, b, alpha);
        }
    }

    private static void drawSideFieldQuad(EnumFacing facing, float offset, float min, float max, float y0, float y1,
                                          float uvShift, float uvScale, float r, float g, float b, float a) {
        float u0 = uvShift;
        float v0 = uvShift;
        float u1 = uvShift + uvScale;
        float v1 = uvShift + uvScale;

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
