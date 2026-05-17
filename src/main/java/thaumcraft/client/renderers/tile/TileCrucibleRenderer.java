package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
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

        float recolor = TileRenderHelper.clamp01((float) tile.tagAmount() / 100.0F);
        float r = 1.0F;
        float g = 1.0F - recolor / 3.0F;
        float b = 1.0F - recolor;
        int color = ((int) (0.88F * 255.0F) << 24)
                | ((int) (r * 255.0F) << 16)
                | ((int) (g * 255.0F) << 8)
                | (int) (b * 255.0F);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + fluidHeight, z + 0.5D);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        TileRenderHelper.drawSolidHorizontalQuad(0.33F, color);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }
}
