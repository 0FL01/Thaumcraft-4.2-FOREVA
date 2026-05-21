package thaumcraft.client.renderers.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import thaumcraft.client.renderers.tile.TileArcaneWorkbenchRenderer;
import thaumcraft.client.renderers.tile.TileDeconstructionTableRenderer;
import thaumcraft.client.renderers.tile.TileTableRenderer;
import thaumcraft.common.tiles.TileArcaneWorkbench;
import thaumcraft.common.tiles.TileDeconstructionTable;
import thaumcraft.common.tiles.TileTable;

public class ItemTableRenderer extends TileEntityItemStackRenderer {
    private final TileTableRenderer tableRenderer = new TileTableRenderer();
    private final TileDeconstructionTableRenderer deconstructionRenderer = new TileDeconstructionTableRenderer();
    private final TileArcaneWorkbenchRenderer arcaneWorkbenchRenderer = new TileArcaneWorkbenchRenderer();

    public ItemTableRenderer() {
        tableRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        deconstructionRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        arcaneWorkbenchRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
    }

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        int meta = stack.getMetadata();
        GlStateManager.pushMatrix();
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        if (meta == 0) {
            tableRenderer.render(new TileTable(), 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
        } else if (meta == 14) {
            deconstructionRenderer.render(new TileDeconstructionTable(), 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
        } else if (meta == 15) {
            arcaneWorkbenchRenderer.render(new TileArcaneWorkbench(), 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
        }
        GlStateManager.popMatrix();
    }
}
