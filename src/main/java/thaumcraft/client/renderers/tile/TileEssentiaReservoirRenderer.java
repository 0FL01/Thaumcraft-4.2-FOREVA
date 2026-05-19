package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.TileEssentiaReservoir;

public class TileEssentiaReservoirRenderer extends TileEntitySpecialRenderer<TileEssentiaReservoir> {
    @Override
    public void render(TileEssentiaReservoir tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        renderLiquid(tile, x, y, z);
    }

    private void renderLiquid(TileEssentiaReservoir tile, double x, double y, double z) {
        if (tile.essentia == null || tile.essentia.visSize() <= 0) {
            return;
        }
        TextureAtlasSprite liquid = Minecraft.getMinecraft().getTextureMapBlocks()
                .getAtlasSprite("thaumcraft:blocks/animatedglow");
        if (liquid == null) {
            return;
        }

        float fill = (float) tile.essentia.visSize() / (float) Math.max(1, tile.maxAmount);
        fill = TileRenderHelper.clamp01(fill);
        float minX = (float) x + 3.0F / 16.0F;
        float maxX = (float) x + 13.0F / 16.0F;
        float minZ = (float) z + 3.0F / 16.0F;
        float maxZ = (float) z + 13.0F / 16.0F;
        float minY = (float) y + 3.0F / 16.0F;
        float maxY = minY + (10.0F / 16.0F) * fill;

        int color = colorFromReservoir(tile);
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        float a = ((color >> 24) & 0xFF) / 255.0F;
        float prevLightX = OpenGlHelper.lastBrightnessX;
        float prevLightY = OpenGlHelper.lastBrightnessY;

        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200.0F, 200.0F);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        drawTexturedCuboid(buf, minX, minY, minZ, maxX, maxY, maxZ,
                liquid.getMinU(), liquid.getMaxU(), liquid.getMinV(), liquid.getMaxV(),
                r, g, b, a);

        tess.draw();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLightX, prevLightY);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private static void drawTexturedCuboid(BufferBuilder buf,
                                           float minX, float minY, float minZ,
                                           float maxX, float maxY, float maxZ,
                                           float u0, float u1, float v0, float v1,
                                           float r, float g, float b, float a) {
        face(buf, minX, maxY, maxZ, minX, minY, maxZ, maxX, minY, maxZ, maxX, maxY, maxZ, u0, u1, v0, v1, r, g, b, a);
        face(buf, maxX, maxY, minZ, maxX, minY, minZ, minX, minY, minZ, minX, maxY, minZ, u0, u1, v0, v1, r, g, b, a);
        face(buf, minX, maxY, minZ, minX, minY, minZ, minX, minY, maxZ, minX, maxY, maxZ, u0, u1, v0, v1, r, g, b, a);
        face(buf, maxX, maxY, maxZ, maxX, minY, maxZ, maxX, minY, minZ, maxX, maxY, minZ, u0, u1, v0, v1, r, g, b, a);
        face(buf, minX, maxY, minZ, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, u0, u1, v0, v1, r, g, b, a);
        face(buf, minX, minY, maxZ, minX, minY, minZ, maxX, minY, minZ, maxX, minY, maxZ, u0, u1, v0, v1, r, g, b, a * 0.9F);
    }

    private static void face(BufferBuilder buf,
                             float x1, float y1, float z1,
                             float x2, float y2, float z2,
                             float x3, float y3, float z3,
                             float x4, float y4, float z4,
                             float u0, float u1, float v0, float v1,
                             float r, float g, float b, float a) {
        buf.pos(x1, y1, z1).tex(u0, v0).color(r, g, b, a).endVertex();
        buf.pos(x2, y2, z2).tex(u0, v1).color(r, g, b, a).endVertex();
        buf.pos(x3, y3, z3).tex(u1, v1).color(r, g, b, a).endVertex();
        buf.pos(x4, y4, z4).tex(u1, v0).color(r, g, b, a).endVertex();
    }

    private static int colorFromReservoir(TileEssentiaReservoir tile) {
        int rgb = reservoirRgb(tile);
        int alpha = computeLiquidAlpha(tile);
        return (alpha << 24) | (rgb & 0x00FFFFFF);
    }

    private static int reservoirRgb(TileEssentiaReservoir tile) {
        Aspect aspect = tile.displayAspect;
        if (aspect == null && tile.essentia != null) {
            Aspect[] aspects = tile.essentia.getAspects();
            if (aspects.length > 0) {
                aspect = aspects[0];
            }
        }
        if (aspect != null) {
            return aspect.getColor() & 0x00FFFFFF;
        }
        int r = Math.max(0, Math.min(255, (int) (tile.colorR * 255.0F)));
        int g = Math.max(0, Math.min(255, (int) (tile.colorG * 255.0F)));
        int b = Math.max(0, Math.min(255, (int) (tile.colorB * 255.0F)));
        return (r << 16) | (g << 8) | b;
    }

    private static int computeLiquidAlpha(TileEssentiaReservoir tile) {
        float fullness = tile.essentia == null ? 0.0F : (float) tile.essentia.visSize() / Math.max(1.0F, tile.maxAmount);
        return Math.max(0, Math.min(255, (int) (140.0F + 80.0F * TileRenderHelper.clamp01(fullness))));
    }

    private static void orientByFace(EnumFacing facing) {
        if (facing == null) {
            return;
        }
        switch (facing) {
            case DOWN:
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case UP:
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case NORTH:
                break;
            case SOUTH:
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                break;
            case WEST:
                GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                break;
            case EAST:
                GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                break;
            default:
                break;
        }
    }
}
