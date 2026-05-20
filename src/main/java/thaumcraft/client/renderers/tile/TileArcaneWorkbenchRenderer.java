package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.TileArcaneWorkbench;

public class TileArcaneWorkbenchRenderer extends TileEntitySpecialRenderer<TileArcaneWorkbench> {
    @Override
    public void render(TileArcaneWorkbench tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        ItemStack wand = tile.getStackInSlot(10);
        if (!wand.isEmpty() && wand.getItem() instanceof ItemWandCasting) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.65D, y + 1.0625D, z + 0.25D);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(20.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(0.60F, 0.60F, 0.60F);
            TileRenderHelper.renderEntityItem(tile, wand, 0.0F);
            GlStateManager.popMatrix();
        }
    }
}
