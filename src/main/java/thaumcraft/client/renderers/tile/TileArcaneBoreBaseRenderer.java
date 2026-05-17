package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.tiles.TileArcaneBoreBase;

public class TileArcaneBoreBaseRenderer extends TileEntitySpecialRenderer<TileArcaneBoreBase> {
    private static final ResourceLocation VORTEX_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/vortex.png");

    @Override
    public void render(TileArcaneBoreBase tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        EnumFacing facing = tile.orientation == null ? EnumFacing.NORTH : tile.orientation;
        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        float yaw = -facing.getHorizontalAngle();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.6D, z + 0.5D);
        GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 1);
        bindTexture(VORTEX_TEXTURE);
        GlStateManager.rotate((ticks * 3.0F) % 360.0F, 0.0F, 0.0F, 1.0F);
        TileRenderHelper.drawTexturedQuad(0.08F, 0x88BBD8FF, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
