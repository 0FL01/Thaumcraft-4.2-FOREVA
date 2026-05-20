package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.tiles.TileWandPedestal;

public class TileWandPedestalRenderer extends TileEntitySpecialRenderer<TileWandPedestal> {
    @Override
    public void render(TileWandPedestal tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        ItemStack stack = tile.getStackInSlot(0);
        if (!stack.isEmpty()) {
            float ticks = TileRenderHelper.ticks(tile, partialTicks);
            float bob = MathHelper.sin((ticks % 32767.0F) / 16.0F) * 0.05F;

            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5D, y + 1.15D + bob, z + 0.5D);
            GlStateManager.rotate(ticks % 360.0F, 0.0F, 1.0F, 0.0F);
            TileRenderHelper.renderEntityItem(tile, stack, 0.0F);
            GlStateManager.popMatrix();
        }

        if (!stack.isEmpty() && tile.draining) {
            float ticks = TileRenderHelper.ticks(tile, partialTicks);
            float bob = MathHelper.sin((ticks % 32767.0F) / 16.0F) * 0.05F;
            double sx = x + 0.5D;
            double sy = y + 1.65D - bob * 2.0F;
            double sz = z + 0.5D;
            double ex = x + 0.5D + (tile.drainX - tile.getPos().getX());
            double ey = y + 0.5D + (tile.drainY - tile.getPos().getY());
            double ez = z + 0.5D + (tile.drainZ - tile.getPos().getZ());

            GlStateManager.pushMatrix();
            TileRenderHelper.drawWispyLine(sx, sy, sz, ex, ey, ez, tile.drainColor, ticks, -0.02F, Math.min(ticks, 10.0F) / 10.0F, 0.25F);
            GlStateManager.popMatrix();
        }
    }
}
