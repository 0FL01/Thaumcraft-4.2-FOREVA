package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import thaumcraft.common.tiles.TileCrucible;

public class TileCrucibleRenderer extends TileEntitySpecialRenderer<TileCrucible> {

    @Override
    public void render(TileCrucible tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }
        float fluidHeight = tile.getFluidHeight();
        if (fluidHeight <= 0.3001F) {
            return;
        }
        TextureAtlasSprite water = Minecraft.getMinecraft().getBlockRendererDispatcher()
                .getBlockModelShapes()
                .getTexture(Blocks.WATER.getDefaultState());
        if (water == null) {
            return;
        }

        float raw = TileRenderHelper.clamp01((float) tile.tagAmount() / 100.0F);
        float recolor = raw > 0.0F ? 0.5F + raw / 2.0F : 0.0F;
        float r = 1.0F;
        float g = 1.0F - recolor / 3.0F;
        float b = 1.0F - recolor;
        float a = 1.0F - recolor / 2.0F;
        int color = ((int) (a * 255.0F) << 24)
                | ((int) (r * 255.0F) << 16)
                | ((int) (g * 255.0F) << 8)
                | (int) (b * 255.0F);
        int packedLight = tile.getWorld().getCombinedLight(tile.getPos(), 0);
        float prevLightX = OpenGlHelper.lastBrightnessX;
        float prevLightY = OpenGlHelper.lastBrightnessY;
        float lightX = packedLight & 0xFFFF;
        float lightY = (packedLight >> 16) & 0xFFFF;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + fluidHeight, z + 0.5D);
        GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightX, lightY);
        TileRenderHelper.drawTexturedQuad(0.5F, color, water.getMinU(), water.getMaxU(), water.getMinV(), water.getMaxV());
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevLightX, prevLightY);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
