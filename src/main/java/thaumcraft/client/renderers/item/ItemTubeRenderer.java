package thaumcraft.client.renderers.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import thaumcraft.client.renderers.tile.TileEssentiaCrystalizerRenderer;
import thaumcraft.client.renderers.tile.TileTubeValveRenderer;
import thaumcraft.client.renderers.tile.TubeConduitRenderHelper;
import thaumcraft.common.tiles.TileEssentiaCrystalizer;
import thaumcraft.common.tiles.TileTubeValve;

public class ItemTubeRenderer extends TileEntityItemStackRenderer {

    private final TileEssentiaCrystalizerRenderer crystalizerRenderer = new TileEssentiaCrystalizerRenderer();
    private final TileTubeValveRenderer valveRenderer = new TileTubeValveRenderer();

    public ItemTubeRenderer() {
        crystalizerRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        valveRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
    }

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        int meta = stack.getMetadata();
        GlStateManager.pushMatrix();
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        if (meta == 7) {
            TileEssentiaCrystalizer crystalizer = new TileEssentiaCrystalizer();
            crystalizerRenderer.render(crystalizer, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
        } else {
            TubeConduitRenderHelper.renderInventoryShell(meta);
            if (meta == 1) {
                valveRenderer.render(new TileTubeValve(), 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
            }
        }
        GlStateManager.popMatrix();
    }
}
