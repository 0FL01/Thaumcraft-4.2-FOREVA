package thaumcraft.client.renderers.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import thaumcraft.client.renderers.tile.TileFluxScrubberRenderer;
import thaumcraft.client.renderers.tile.TileNodeConverterRenderer;
import thaumcraft.client.renderers.tile.TileNodeStabilizerRenderer;
import thaumcraft.common.tiles.TileFluxScrubber;
import thaumcraft.common.tiles.TileNodeConverter;
import thaumcraft.common.tiles.TileNodeStabilizer;

public class ItemStoneDeviceRenderer extends TileEntityItemStackRenderer {
    private final TileNodeStabilizerRenderer stabilizerRenderer = new TileNodeStabilizerRenderer();
    private final TileNodeConverterRenderer converterRenderer = new TileNodeConverterRenderer();
    private final TileFluxScrubberRenderer fluxScrubberRenderer = new TileFluxScrubberRenderer();

    public ItemStoneDeviceRenderer() {
        stabilizerRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        converterRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        fluxScrubberRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
    }

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        int meta = stack.getMetadata();
        GlStateManager.pushMatrix();
        try {
            // Forge's TEISR entry translation supplies the single -0.5 item offset used by TC4 here.
            if (meta == 9 || meta == 10) {
                TileNodeStabilizer stabilizer = new TileNodeStabilizer();
                stabilizer.lock = meta == 9 ? 1 : 2;
                stabilizerRenderer.render(stabilizer, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
            } else if (meta == 11) {
                TileNodeConverter converter = new TileNodeConverter();
                converterRenderer.render(converter, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
            } else if (meta == 14) {
                TileFluxScrubber scrubber = new TileFluxScrubber();
                fluxScrubberRenderer.render(scrubber, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
            }
        } finally {
            GlStateManager.popMatrix();
        }
    }
}
