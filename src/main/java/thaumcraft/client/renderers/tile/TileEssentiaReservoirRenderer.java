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
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import thaumcraft.codechicken.lib.render.CCModel;
import thaumcraft.codechicken.lib.render.CCRenderState;
import thaumcraft.common.tiles.TileEssentiaReservoir;

import java.util.Map;

public class TileEssentiaReservoirRenderer extends TileEntitySpecialRenderer<TileEssentiaReservoir> {
    private static final ResourceLocation RESERVOIR_OBJ =
            new ResourceLocation("thaumcraft", "textures/models/reservoir.obj");
    private static final ResourceLocation RESERVOIR_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/reservoir.png");

    private final CCModel model = loadReservoirModel();

    @Override
    public void render(TileEssentiaReservoir tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        renderReservoirShell(tile, x, y, z);
        renderLiquid(tile, x, y, z);
    }

    private void renderReservoirShell(TileEssentiaReservoir tile, double x, double y, double z) {
        if (model == null) {
            return;
        }

        boolean rescaleNormalEnabled = GL11.glIsEnabled(GL12.GL_RESCALE_NORMAL);
        GlStateManager.pushMatrix();
        try {
            translateFromOrientation(x, y, z,
                    (tile.facing == null ? EnumFacing.DOWN : tile.facing).ordinal());
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            bindTexture(RESERVOIR_TEXTURE);
            if (!rescaleNormalEnabled) {
                GlStateManager.enableRescaleNormal();
            }
            CCRenderState.reset();
            CCRenderState.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
            model.render(CCRenderState.normalAttrib);
            CCRenderState.draw();
        } finally {
            if (!rescaleNormalEnabled) {
                GlStateManager.disableRescaleNormal();
            }
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    private void renderLiquid(TileEssentiaReservoir tile, double x, double y, double z) {
        if (tile.essentia == null || tile.essentia.visSize() <= 0 || tile.displayAspect == null) {
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

        float r = tile.colorR;
        float g = tile.colorG;
        float b = tile.colorB;
        float a = 0.9F;
        float prevLightX = OpenGlHelper.lastBrightnessX;
        float prevLightY = OpenGlHelper.lastBrightnessY;
        boolean lightingEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING);
        boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean cullEnabled = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        int blendSrcRgb = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
        int blendDstRgb = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
        int blendSrcAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
        int blendDstAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);

        GlStateManager.pushMatrix();
        try {
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableCull();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 200.0F, 200.0F);

            Tessellator tess = Tessellator.getInstance();
            BufferBuilder buf = tess.getBuffer();
            buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            drawTexturedCuboid(buf, minX, minY, minZ, maxX, maxY, maxZ,
                    liquid.getMinU(), liquid.getMaxU(), liquid.getMinV(), liquid.getMaxV(),
                    r, g, b, a);
            tess.draw();
        } finally {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLightX, prevLightY);
            GlStateManager.tryBlendFuncSeparate(blendSrcRgb, blendDstRgb, blendSrcAlpha, blendDstAlpha);
            if (cullEnabled) {
                GlStateManager.enableCull();
            } else {
                GlStateManager.disableCull();
            }
            if (blendEnabled) {
                GlStateManager.enableBlend();
            } else {
                GlStateManager.disableBlend();
            }
            if (lightingEnabled) {
                GlStateManager.enableLighting();
            } else {
                GlStateManager.disableLighting();
            }
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
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
        face(buf, minX, minY, maxZ, minX, minY, minZ, maxX, minY, minZ, maxX, minY, maxZ, u0, u1, v0, v1, r, g, b, a);
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

    private static void translateFromOrientation(double x, double y, double z, int orientation) {
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        if (orientation == 0) {
            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
        } else if (orientation == 1) {
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        } else if (orientation == 3) {
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        } else if (orientation == 4) {
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        } else if (orientation == 5) {
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        }
        GlStateManager.translate(0.0D, 0.0D, -0.5D);
    }

    private static CCModel loadReservoirModel() {
        Map<String, CCModel> models = CCModel.parseObjModels(RESERVOIR_OBJ);
        if (models == null || models.isEmpty()) {
            return null;
        }
        CCModel reservoir = models.get("Cylinder001");
        return reservoir != null ? reservoir : models.values().iterator().next();
    }
}
