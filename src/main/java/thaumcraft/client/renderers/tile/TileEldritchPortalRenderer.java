package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.tiles.TileEldritchPortal;

public class TileEldritchPortalRenderer extends TileEntitySpecialRenderer<TileEldritchPortal> {
    private static final ResourceLocation PORTAL =
            new ResourceLocation("thaumcraft", "textures/misc/eldritch_portal.png");

    @Override
    public void render(TileEldritchPortal tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        int openTicks = Math.max(0, tile.opencount);
        float progress = Math.min(1.0F, (openTicks + partialTicks) / 30.0F);
        if (progress <= 0.0F) {
            return;
        }

        long frame = (tile.getWorld().getTotalWorldTime() / 2L) % 16L;
        float u0 = frame / 16.0F;
        float u1 = u0 + 1.0F / 16.0F;
        float scale = 0.35F + progress * 0.8F;
        float yScale = 0.5F + progress * 0.5F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        bindTexture(PORTAL);
        TileRenderHelper.orientBillboardToPlayer();
        TileRenderHelper.drawTexturedQuad(scale, 0xCCFF33FF, u0, u1, 0.0F, yScale);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
