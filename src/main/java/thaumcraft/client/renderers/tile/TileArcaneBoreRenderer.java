package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.tiles.TileArcaneBore;

public class TileArcaneBoreRenderer extends TileEntitySpecialRenderer<TileArcaneBore> {

    private static final ResourceLocation VORTEX_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/vortex.png");

    @Override
    public void render(TileArcaneBore tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        EnumFacing facing = tile.orientation == null ? EnumFacing.NORTH : tile.orientation;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.65D, z + 0.5D);
        GlStateManager.rotate(-facing.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);

        if (tile.hasFocus) {
            ItemStack focus = tile.getStackInSlot(0);
            if (focus != null && !focus.isEmpty()) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0F, 0.10F, 0.0F);
                TileRenderHelper.renderFloatingItem(focus.copy(), ticks, 0.0F, 0.5F);
                GlStateManager.popMatrix();
            }
        }
        if (tile.hasPickaxe) {
            ItemStack pickaxe = tile.getStackInSlot(1);
            if (pickaxe != null && !pickaxe.isEmpty()) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0F, -0.05F, -0.16F);
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                TileRenderHelper.renderFloatingItem(pickaxe.copy(), ticks + 20.0F, 0.0F, 0.6F);
                GlStateManager.popMatrix();
            }
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, -0.18F, 0.16F);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 1);
        bindTexture(VORTEX_TEXTURE);
        TileRenderHelper.orientBillboardToPlayer();
        TileRenderHelper.drawTexturedQuad(0.18F, 0xCCFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.rotate((ticks * 4.0F) % 360.0F, 0.0F, 0.0F, 1.0F);
        TileRenderHelper.drawTexturedQuad(0.12F, 0x99CCFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
    }
}
