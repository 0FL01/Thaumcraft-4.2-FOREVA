package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.tiles.TileEtherealBloom;

public class TileEtherealBloomRenderer extends TileEntitySpecialRenderer<TileEtherealBloom> {
    private static final ResourceLocation BLOOM =
            new ResourceLocation("thaumcraft", "textures/misc/vortex.png");

    @Override
    public void render(TileEtherealBloom tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        float spin = (ticks * 1.8F) % 360.0F;
        float pulse = 0.22F + (float) Math.sin(ticks / 12.0F) * 0.04F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.34D, z + 0.5D);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 1);
        bindTexture(BLOOM);
        TileRenderHelper.orientBillboardToPlayer();
        GlStateManager.rotate(spin, 0.0F, 0.0F, 1.0F);
        TileRenderHelper.drawTexturedQuad(pulse, 0x88B8FFAA, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.rotate(-spin * 1.6F, 0.0F, 0.0F, 1.0F);
        TileRenderHelper.drawTexturedQuad(pulse * 1.35F, 0x66FFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
