package thaumcraft.client.renderers.tile;

import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import thaumcraft.codechicken.lib.render.CCModel;
import thaumcraft.codechicken.lib.render.CCRenderState;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.common.tiles.TileAlchemyFurnaceAdvanced;

public class TileAlchemyFurnaceAdvancedRenderer extends TileEntitySpecialRenderer<TileAlchemyFurnaceAdvanced> {
    private static final ResourceLocation FURNACE_MODEL =
            new ResourceLocation("thaumcraft", "textures/models/adv_alch_furnace.obj");
    private static final ResourceLocation FURNACE =
            new ResourceLocation("thaumcraft", "textures/models/alch_furnace.png");
    private static final ResourceLocation FURNACE_ON =
            new ResourceLocation("thaumcraft", "textures/models/alch_furnace_on.png");
    private static final ResourceLocation TANK =
            new ResourceLocation("thaumcraft", "textures/models/alch_furnace_tank.png");
    private static final ResourceLocation TANK_ON =
            new ResourceLocation("thaumcraft", "textures/models/alch_furnace_tank_on.png");

    private final CCModel base;
    private final CCModel tank;

    public TileAlchemyFurnaceAdvancedRenderer() {
        Map<String, CCModel> models = CCModel.parseObjModels(FURNACE_MODEL);
        CCModel parsedBase = models.get("Base");
        CCModel parsedTank = models.get("Tank");
        if (parsedBase == null || parsedTank == null) {
            throw new IllegalStateException("Advanced alchemical furnace OBJ is missing Base or Tank");
        }
        this.base = restoreObjFaceOrder(parsedBase);
        this.tank = restoreObjFaceOrder(parsedTank);
    }

    @Override
    public void render(TileAlchemyFurnaceAdvanced tile, double x, double y, double z,
                       float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        TextureAtlasSprite fluxgoo = atlas("thaumcraft:blocks/fluxgoo");
        TextureAtlasSprite metalbase = atlas("thaumcraft:blocks/metalbase");
        TextureAtlasSprite fire = Minecraft.getMinecraft().getBlockRendererDispatcher()
                .getBlockModelShapes().getTexture(Blocks.FIRE.getDefaultState());
        float previousLightX = OpenGlHelper.lastBrightnessX;
        float previousLightY = OpenGlHelper.lastBrightnessY;
        boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean lightingEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING);
        boolean rescaleNormalEnabled = GL11.glIsEnabled(GL12.GL_RESCALE_NORMAL);
        boolean cullEnabled = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        int blendSrcRgb = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
        int blendDstRgb = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
        int blendSrcAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
        int blendDstAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);

        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(x + 0.5D, y, z + 0.5D);
            GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            if (!rescaleNormalEnabled) {
                GlStateManager.enableRescaleNormal();
            }
            GlStateManager.enableCull();

            bindTexture(tile.heat > 100 ? FURNACE_ON : FURNACE);
            renderModel(this.base);

            bindTexture(tile.vis > 0 ? TANK_ON : TANK);
            for (int side = 0; side < 4; ++side) {
                GlStateManager.pushMatrix();
                try {
                    GlStateManager.rotate(90.0F * side, 0.0F, 0.0F, 1.0F);
                    renderModel(this.tank);
                } finally {
                    GlStateManager.popMatrix();
                }
            }

            if (tile.vis > 0) {
                renderVis(tile, fluxgoo, metalbase);
            }
            if (tile.heat > 100) {
                renderHeat(tile, fire, metalbase);
            }
        } finally {
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,
                    previousLightX, previousLightY);
            GlStateManager.tryBlendFuncSeparate(blendSrcRgb, blendDstRgb, blendSrcAlpha, blendDstAlpha);
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
            if (rescaleNormalEnabled) {
                GlStateManager.enableRescaleNormal();
            } else {
                GlStateManager.disableRescaleNormal();
            }
            if (cullEnabled) {
                GlStateManager.enableCull();
            } else {
                GlStateManager.disableCull();
            }
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    private static void renderVis(TileAlchemyFurnaceAdvanced tile, TextureAtlasSprite fluxgoo,
                                  TextureAtlasSprite metalbase) {
        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(0.5F, -0.5F, 1.1F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            renderQuadCenteredFromIcon(fluxgoo, 190, 0.0F);
        } finally {
            GlStateManager.popMatrix();
        }

        float fill = 1.0F - (float) tile.vis / (float) tile.maxVis;
        for (int side = 0; side < 4; ++side) {
            GlStateManager.pushMatrix();
            try {
                GlStateManager.rotate(90.0F * side, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
                GlStateManager.translate(0.85F, -1.8F, -1.4F);
                GlStateManager.scale(0.3D, 0.6D, 1.0D);
                renderQuadCenteredFromIcon(metalbase, 150, 0.0F);
                GlStateManager.translate(0.0F, 0.0F, -0.01F);
                renderQuadCenteredFromIcon(fluxgoo, 190, fill);
            } finally {
                GlStateManager.popMatrix();
            }

            GlStateManager.pushMatrix();
            try {
                GlStateManager.rotate(90.0F * side, 0.0F, 0.0F, -1.0F);
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.translate(1.15F, 1.8F, -1.4F);
                GlStateManager.scale(-0.3D, -0.6D, -1.0D);
                renderQuadCenteredFromIcon(metalbase, 150, 0.0F);
                GlStateManager.translate(0.0F, 0.0F, 0.01F);
                renderQuadCenteredFromIcon(fluxgoo, 190, fill);
            } finally {
                GlStateManager.popMatrix();
            }
        }
    }

    private static void renderHeat(TileAlchemyFurnaceAdvanced tile, TextureAtlasSprite fire,
                                   TextureAtlasSprite metalbase) {
        float fill = 1.0F - Math.min(1.0F, (float) tile.heat / (float) tile.maxPower);
        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(0.0F, 0.0F, 1.0F);
            for (int side = 0; side < 4; ++side) {
                GlStateManager.pushMatrix();
                try {
                    GlStateManager.rotate(90.0F * side, 0.0F, 0.0F, 1.0F);
                    GlStateManager.rotate(135.0F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.translate(-0.5F, 0.0F, -1.0F);
                    renderQuadCenteredFromIcon(fire, 220, fill);
                    GlStateManager.translate(0.0F, 0.0F, 0.05F);
                    renderQuadCenteredFromIcon(metalbase, 150, 0.0F);
                } finally {
                    GlStateManager.popMatrix();
                }
            }
        } finally {
            GlStateManager.popMatrix();
        }
    }

    private static void renderQuadCenteredFromIcon(TextureAtlasSprite sprite, int brightness, float fill) {
        if (sprite == null) {
            return;
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightness, 0.0F);

        float minU = sprite.getMinU();
        float maxU = sprite.getMaxU();
        float minV = sprite.getMinV();
        float maxV = sprite.getMaxV();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buffer.pos(0.0D, 1.0D, 0.0D).tex(minU, maxV).color(255, 255, 255, 255).endVertex();
        buffer.pos(1.0D, 1.0D, 0.0D).tex(maxU, maxV).color(255, 255, 255, 255).endVertex();
        buffer.pos(1.0D, fill, 0.0D).tex(maxU, minV).color(255, 255, 255, 255).endVertex();
        buffer.pos(0.0D, fill, 0.0D).tex(minU, minV).color(255, 255, 255, 255).endVertex();
        tessellator.draw();
    }

    private static void renderModel(CCModel model) {
        CCRenderState.reset();
        CCRenderState.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
        model.render(CCRenderState.normalAttrib);
        CCRenderState.draw();
    }

    private static CCModel restoreObjFaceOrder(CCModel model) {
        // CCL reverses OBJ triangle winding; reverse it back without changing the authored normals.
        CCModel corrected = model.backfacedCopy();
        Vector3[] normals = corrected.normals();
        if (normals != null) {
            for (Vector3 normal : normals) {
                if (normal != null) {
                    normal.negate();
                }
            }
        }
        return corrected;
    }

    private static TextureAtlasSprite atlas(String sprite) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(sprite);
    }
}
