package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.tiles.TileNodeConverter;

public class TileNodeConverterRenderer extends TileEntitySpecialRenderer<TileNodeConverter> {

    private static final ResourceLocation BASE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/node_converter.png");
    private static final ResourceLocation OVER_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/node_converter_over.png");

    @Override
    public void render(TileNodeConverter tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        float progress = TileRenderHelper.clamp01(Math.min(50, Math.max(0, tile.count)) / 50.0F);
        int overlayColor = statusColor(tile.status);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 1.0D, z + 0.5D);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);

        GlStateManager.pushMatrix();
        GlStateManager.rotate((ticks * 0.5F) % 360.0F, 0.0F, 1.0F, 0.0F);
        TileRenderHelper.orientBillboardToPlayer();
        bindTexture(BASE_TEXTURE);
        TileRenderHelper.drawTexturedQuad(0.23F, 0xE6FFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
        bindTexture(OVER_TEXTURE);
        TileRenderHelper.drawTexturedQuad(0.23F, overlayColor, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.popMatrix();

        for (int i = 0; i < 4; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(i * 90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.20D, -0.02D + progress * 0.10D, 0.0D);
            GlStateManager.rotate((ticks * (2.0F + i * 0.3F)) % 360.0F, 1.0F, 0.0F, 0.0F);
            TileRenderHelper.orientBillboardToPlayer();
            bindTexture(BASE_TEXTURE);
            TileRenderHelper.drawTexturedQuad(0.12F, 0xD0FFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
            bindTexture(OVER_TEXTURE);
            TileRenderHelper.drawTexturedQuad(0.12F, overlayColor, 0.0F, 1.0F, 0.0F, 1.0F);
            GlStateManager.popMatrix();
        }

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private static int statusColor(int status) {
        if (status == 2) {
            return 0xCCFF004D;
        }
        if (status == 1) {
            return 0xCCFF9920;
        }
        return 0xCC90FF90;
    }
}
