package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.tiles.TileNodeStabilizer;

public class TileNodeStabilizerRenderer extends TileEntitySpecialRenderer<TileNodeStabilizer> {

    private static final ResourceLocation BASE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/node_stabilizer.png");
    private static final ResourceLocation OVER_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/node_stabilizer_over.png");
    private static final ResourceLocation BUBBLE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/node_bubble.png");

    @Override
    public void render(TileNodeStabilizer tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        float progress = TileRenderHelper.clamp01(tile.count / 37.0F);
        boolean locked = tile.lock == 2;
        int overlayColor = locked ? 0xAAFF4444 : 0xAAFFFFFF;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.75D, z + 0.5D);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);

        for (int i = 0; i < 4; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(i * 90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.16D, 0.06D + progress * 0.18D, 0.0D);
            GlStateManager.rotate((ticks * (2.0F + i * 0.25F)) % 360.0F, 1.0F, 0.0F, 0.0F);
            TileRenderHelper.orientBillboardToPlayer();

            bindTexture(BASE_TEXTURE);
            TileRenderHelper.drawTexturedQuad(0.15F, 0xD8FFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
            bindTexture(OVER_TEXTURE);
            TileRenderHelper.drawTexturedQuad(0.15F, overlayColor, 0.0F, 1.0F, 0.0F, 1.0F);
            GlStateManager.popMatrix();
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, -0.16D, 0.0D);
        TileRenderHelper.orientBillboardToPlayer();
        bindTexture(BASE_TEXTURE);
        TileRenderHelper.drawTexturedQuad(0.21F, 0xE6FFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.popMatrix();

        if (tile.count > 0) {
            float bubblePulse = 0.5F + (float) Math.sin(ticks / 8.0F) * 0.1F;
            int bubbleColor = locked ? 0xCCFF4444 : 0xCCFFFFFF;
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0D, 0.65D, 0.0D);
            TileRenderHelper.orientBillboardToPlayer();
            bindTexture(BUBBLE_TEXTURE);
            TileRenderHelper.drawTexturedQuad(0.18F * progress * bubblePulse, bubbleColor, 0.0F, 1.0F, 0.0F, 1.0F);
            GlStateManager.popMatrix();
        }

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
