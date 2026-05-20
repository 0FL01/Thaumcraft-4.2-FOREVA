package thaumcraft.client.renderers.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import thaumcraft.client.renderers.tile.TileRunicMatrixRenderer;
import thaumcraft.common.tiles.TileInfusionMatrix;

public class ItemStoneDeviceRenderer extends TileEntityItemStackRenderer {

    private final TileRunicMatrixRenderer matrixRenderer = new TileRunicMatrixRenderer();

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        if (stack.getMetadata() != 2) {
            return;
        }

        TileInfusionMatrix matrix = new TileInfusionMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        matrixRenderer.render(matrix, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
        GlStateManager.popMatrix();
    }
}
