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
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.ModelAlchemyFurnaceAdvanced;
import thaumcraft.common.tiles.TileAlchemyFurnace;
import org.lwjgl.opengl.GL11;

public class TileAlchemyFurnaceAdvancedRenderer extends TileEntitySpecialRenderer<TileAlchemyFurnace> {
    private static final ResourceLocation FURNACE =
            new ResourceLocation("thaumcraft", "textures/models/alch_furnace.png");
    private static final ResourceLocation FURNACE_ON =
            new ResourceLocation("thaumcraft", "textures/models/alch_furnace_on.png");
    private static final ResourceLocation TANK =
            new ResourceLocation("thaumcraft", "textures/models/alch_furnace_tank.png");
    private static final ResourceLocation TANK_ON =
            new ResourceLocation("thaumcraft", "textures/models/alch_furnace_tank_on.png");
    private static final ResourceLocation PANEL_BACKING =
            new ResourceLocation("thaumcraft", "blocks/al_furnace_side");
    private static final ResourceLocation PANEL_FILL =
            new ResourceLocation("thaumcraft", "blocks/al_furnace_top_filled");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelAlchemyFurnaceAdvanced model = new ModelAlchemyFurnaceAdvanced();

    @Override
    public void render(TileAlchemyFurnace tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float content = Math.min(1.0F, tile.vis / 50.0F);
        boolean burning = tile.isBurning();
        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        float pulse = burning ? (0.85F + (float) Math.sin(ticks / 6.0F) * 0.15F) : 0.65F;
        float heatNorm = tile.currentItemBurnTime <= 0 ? 0.0F : Math.min(1.0F, (float) tile.furnaceBurnTime / (float) tile.currentItemBurnTime);
        TextureAtlasSprite panelBacking = atlas(PANEL_BACKING);
        TextureAtlasSprite panelFill = atlas(PANEL_FILL);
        TextureAtlasSprite lava = Minecraft.getMinecraft().getBlockRendererDispatcher()
                .getBlockModelShapes()
                .getTexture(Blocks.LAVA.getDefaultState());

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y, z + 0.5D);
        GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
        GlStateManager.disableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        bindTexture(content > 0.0F ? TANK_ON : TANK);
        for (int side = 0; side < 4; side++) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(side * 90.0F, 0.0F, 0.0F, 1.0F);
            model.renderTankPanel(MODEL_SCALE);
            GlStateManager.popMatrix();
        }

        if (content > 0.0F) {
            float fillWidth = 1.0F - content;
            int overlayAlpha = Math.max(0x66, (int) (0xFF * pulse));
            int overlayColor = (overlayAlpha << 24) | 0xFFFFFF;
            if (panelBacking != null && panelFill != null) {
                renderVisPanels(panelBacking, panelFill, fillWidth, overlayColor);
            }
        }

        if (burning && heatNorm > 0.0F && panelBacking != null && lava != null) {
            renderHeatPanels(panelBacking, lava, 1.0F - heatNorm);
        }

        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void renderVisPanels(TextureAtlasSprite panelBacking, TextureAtlasSprite panelFill, float fillWidth, int overlayColor) {
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        float prevLightX = OpenGlHelper.lastBrightnessX;
        float prevLightY = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 190.0F, 190.0F);

        for (int side = 0; side < 4; side++) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(side * 90.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
            GlStateManager.translate(0.85F, -1.8F, -1.4F);
            GlStateManager.scale(0.3F, 0.6F, 1.0F);
            drawAtlasQuad(panelBacking, 0xFFFFFFFF, 0.0F);
            GlStateManager.translate(0.0F, 0.0F, -0.01F);
            drawAtlasQuad(panelFill, overlayColor, fillWidth);
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            GlStateManager.rotate(side * 90.0F, 0.0F, 0.0F, -1.0F);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translate(1.15F, 1.8F, -1.4F);
            GlStateManager.scale(-0.3F, -0.6F, -1.0F);
            drawAtlasQuad(panelBacking, 0xFFFFFFFF, 0.0F);
            GlStateManager.translate(0.0F, 0.0F, 0.01F);
            drawAtlasQuad(panelFill, overlayColor, fillWidth);
            GlStateManager.popMatrix();
        }

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLightX, prevLightY);
    }

    private void renderHeatPanels(TextureAtlasSprite panelBacking, TextureAtlasSprite lava, float fillWidth) {
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        float prevLightX = OpenGlHelper.lastBrightnessX;
        float prevLightY = OpenGlHelper.lastBrightnessY;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 220.0F, 220.0F);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 1.0F);
        for (int side = 0; side < 4; side++) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(side * 90.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(135.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(-0.5F, 0.0F, -1.0F);
            drawAtlasQuad(lava, 0xFFFFFFFF, fillWidth);
            GlStateManager.translate(0.0F, 0.0F, 0.05F);
            drawAtlasQuad(panelBacking, 0xD0FFFFFF, 0.0F);
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLightX, prevLightY);
    }

    private static void drawAtlasQuad(TextureAtlasSprite sprite, int argb, float width) {
        if (sprite == null) {
            return;
        }
        float clampedWidth = Math.max(0.0F, Math.min(1.0F, width));
        float drawHeight = 1.0F - clampedWidth;
        if (drawHeight <= 0.0F) {
            return;
        }

        float alpha = ((argb >> 24) & 0xFF) / 255.0F;
        float red = ((argb >> 16) & 0xFF) / 255.0F;
        float green = ((argb >> 8) & 0xFF) / 255.0F;
        float blue = (argb & 0xFF) / 255.0F;
        float u0 = sprite.getMinU();
        float u1 = sprite.getMaxU();
        float v0 = sprite.getMinV();
        float v1 = sprite.getMaxV();
        float vFill = v0 + (v1 - v0) * drawHeight;

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        buf.pos(0.0D, 1.0D, 0.0D).tex(u0, v0).color(red, green, blue, alpha).endVertex();
        buf.pos(1.0D, 1.0D, 0.0D).tex(u1, v0).color(red, green, blue, alpha).endVertex();
        buf.pos(1.0D, clampedWidth, 0.0D).tex(u1, vFill).color(red, green, blue, alpha).endVertex();
        buf.pos(0.0D, clampedWidth, 0.0D).tex(u0, vFill).color(red, green, blue, alpha).endVertex();
        tess.draw();
        GlStateManager.disableBlend();
    }

    private static TextureAtlasSprite atlas(ResourceLocation sprite) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(sprite.toString());
    }
}
