package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.tiles.TileInfusionMatrix;

public class TileRunicMatrixRenderer extends TileEntitySpecialRenderer<TileInfusionMatrix> {

    private static final ResourceLocation INFUSER_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/infuser.png");

    @Override
    public void render(TileInfusionMatrix tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null || (!tile.active && !tile.crafting && tile.startUp <= 0.0F)) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        float startup = tile.startUp <= 0.0F ? 1.0F : tile.startUp;
        float instability = Math.min(6.0F, 1.0F + tile.instability * 0.66F);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.rotate((ticks * startup) % 360.0F, 0.0F, 1.0F, 0.0F);
        bindTexture(INFUSER_TEXTURE);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 1);

        for (int i = 0; i < 8; i++) {
            float phase = ticks / (10.0F + i * 0.75F);
            float ox = (float) Math.cos(phase + i) * 0.25F;
            float oy = (float) Math.sin(phase * 1.3F + i) * 0.07F * startup;
            float oz = (float) Math.sin(phase + i) * 0.25F;
            GlStateManager.pushMatrix();
            GlStateManager.translate(ox, oy, oz);
            GlStateManager.rotate((ticks * (3.0F + i)) % 360.0F, 1.0F, 1.0F, 0.0F);
            TileRenderHelper.orientBillboardToPlayer();
            int color = ((int) (Math.min(0.85F, 0.35F + startup * 0.45F) * 255.0F) << 24) | 0xCC00FF;
            float half = 0.07F + 0.015F * (instability / 6.0F);
            TileRenderHelper.drawTexturedQuad(half, color, 0.0F, 1.0F, 0.0F, 1.0F);
            GlStateManager.popMatrix();
        }

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
