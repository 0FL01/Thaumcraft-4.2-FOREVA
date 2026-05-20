package thaumcraft.client.renderers.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import thaumcraft.client.renderers.tile.TileAlembicRenderer;
import thaumcraft.common.tiles.TileAlembic;

public class ItemMetalDeviceRenderer extends TileEntityItemStackRenderer {

    private final TileAlembicRenderer alembicRenderer = new TileAlembicRenderer();

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack == null || stack.isEmpty() || stack.getMetadata() != 1) {
            return;
        }
        TileAlembic alembic = new TileAlembic();
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.5F, 0.0F, -0.5F);
        alembicRenderer.render(alembic, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
        GlStateManager.popMatrix();
    }
}
