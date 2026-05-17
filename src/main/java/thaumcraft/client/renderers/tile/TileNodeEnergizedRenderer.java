package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.tiles.TileNodeEnergized;

public class TileNodeEnergizedRenderer extends TileEntitySpecialRenderer<TileNodeEnergized> {
    private static final ResourceLocation BUBBLE =
            new ResourceLocation("thaumcraft", "textures/misc/node_bubble.png");

    @Override
    public void render(TileNodeEnergized tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        float pulse = 0.30F + (float) Math.sin(ticks / 8.0F) * 0.06F;
        int color = 0x88A0D6FF;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 1);
        bindTexture(BUBBLE);

        TileRenderHelper.orientBillboardToPlayer();
        TileRenderHelper.drawTexturedQuad(pulse, color, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.rotate((ticks * 3.0F) % 360.0F, 0.0F, 0.0F, 1.0F);
        TileRenderHelper.drawTexturedQuad(pulse * 1.20F, 0x70FFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.rotate((ticks * -2.0F) % 360.0F, 0.0F, 0.0F, 1.0F);
        TileRenderHelper.drawTexturedQuad(pulse * 0.75F, 0xAA88CCFF, 0.0F, 1.0F, 0.0F, 1.0F);

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
