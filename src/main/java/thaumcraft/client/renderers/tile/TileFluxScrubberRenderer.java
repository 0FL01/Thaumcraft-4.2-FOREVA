package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.tiles.TileFluxScrubber;

public class TileFluxScrubberRenderer extends TileEntitySpecialRenderer<TileFluxScrubber> {
    private static final ResourceLocation SCRUBBER =
            new ResourceLocation("thaumcraft", "textures/models/fluxscrubber.png");
    private static final ResourceLocation WISPY =
            new ResourceLocation("thaumcraft", "textures/misc/wispy.png");

    @Override
    public void render(TileFluxScrubber tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        float powerScale = Math.min(1.0F, tile.power / 25.0F);
        float chargeScale = Math.min(1.0F, tile.charges / 16.0F);
        float essentiaScale = Math.min(1.0F, tile.essentia / 4.0F);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.65D, z + 0.5D);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 1);

        bindTexture(SCRUBBER);
        TileRenderHelper.orientBillboardToPlayer();
        float base = 0.27F + powerScale * 0.07F;
        TileRenderHelper.drawTexturedQuad(base, (0x80 << 24) | 0x00CCAAFF, 0.0F, 1.0F, 0.0F, 1.0F);

        bindTexture(WISPY);
        GlStateManager.rotate((ticks * (2.0F + chargeScale * 5.0F)) % 360.0F, 0.0F, 0.0F, 1.0F);
        TileRenderHelper.drawTexturedQuad(base * 1.35F, (0x66 << 24) | 0x00FF66CC, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.rotate((ticks * (-3.0F - essentiaScale * 6.0F)) % 360.0F, 0.0F, 0.0F, 1.0F);
        TileRenderHelper.drawTexturedQuad(base * 0.7F, (0x99 << 24) | 0x00AA88FF, 0.0F, 1.0F, 0.0F, 1.0F);

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
