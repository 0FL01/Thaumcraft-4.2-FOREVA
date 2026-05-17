package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import thaumcraft.common.tiles.TilePedestal;

public class TilePedestalRenderer extends TileEntitySpecialRenderer<TilePedestal> {

    @Override
    public void render(TilePedestal tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }
        ItemStack stack = tile.getStackInSlot(0);
        if (stack == null || stack.isEmpty()) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        float scale = stack.getItem() instanceof ItemBlock ? 2.0F : 1.0F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 1.15D, z + 0.5D);
        TileRenderHelper.renderFloatingItem(stack.copy(), ticks, 0.0F, scale);
        GlStateManager.popMatrix();
    }
}
