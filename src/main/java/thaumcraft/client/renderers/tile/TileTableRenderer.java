package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.tiles.TileTable;

public class TileTableRenderer extends TileEntitySpecialRenderer<TileTable> {
    private static final ResourceLocation TABLE =
            new ResourceLocation("thaumcraft", "textures/models/table.png");

    @Override
    public void render(TileTable tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        float pulse = 0.14F + (float) Math.sin(ticks / 20.0F) * 0.02F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 1.01D, z + 0.5D);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        bindTexture(TABLE);
        TileRenderHelper.orientBillboardToPlayer();
        TileRenderHelper.drawTexturedQuad(pulse, 0x66FFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
