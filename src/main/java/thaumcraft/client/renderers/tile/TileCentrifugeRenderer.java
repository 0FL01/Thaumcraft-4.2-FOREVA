package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.tiles.TileCentrifuge;

public class TileCentrifugeRenderer extends TileEntitySpecialRenderer<TileCentrifuge> {
    private static final ResourceLocation CENTRIFUGE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/centrifuge.png");

    @Override
    public void render(TileCentrifuge tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float spin = tile.rotation + partialTicks * 4.0F;
        bindTexture(CENTRIFUGE_TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);

        TileRenderHelper.drawTexturedQuad(0.30F, 0xCCFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        TileRenderHelper.drawTexturedQuad(0.30F, 0xCCFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);

        GlStateManager.rotate(spin, 0.0F, 1.0F, 0.0F);
        int coreColor = tile.aspectIn != null ? 0xFFAAEEFF : (tile.aspectOut != null ? 0xFFE6FFD0 : 0x99FFFFFF);
        TileRenderHelper.drawTexturedQuad(0.16F, coreColor, 0.0F, 1.0F, 0.0F, 1.0F);

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
