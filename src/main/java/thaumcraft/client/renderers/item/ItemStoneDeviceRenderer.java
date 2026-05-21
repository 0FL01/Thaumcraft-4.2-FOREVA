package thaumcraft.client.renderers.item;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import thaumcraft.client.renderers.tile.TileFocalManipulatorRenderer;
import thaumcraft.client.renderers.tile.TileFluxScrubberRenderer;
import thaumcraft.client.renderers.tile.TileNodeConverterRenderer;
import thaumcraft.client.renderers.tile.TileNodeStabilizerRenderer;
import thaumcraft.client.renderers.tile.TileRunicMatrixRenderer;
import thaumcraft.common.tiles.TileInfusionMatrix;
import thaumcraft.common.tiles.TileFocalManipulator;
import thaumcraft.common.tiles.TileFluxScrubber;
import thaumcraft.common.tiles.TileNodeConverter;
import thaumcraft.common.tiles.TileNodeStabilizer;

public class ItemStoneDeviceRenderer extends TileEntityItemStackRenderer {
    private final TileRunicMatrixRenderer matrixRenderer = new TileRunicMatrixRenderer();
    private final TileNodeStabilizerRenderer stabilizerRenderer = new TileNodeStabilizerRenderer();
    private final TileNodeConverterRenderer converterRenderer = new TileNodeConverterRenderer();
    private final TileFocalManipulatorRenderer focalManipulatorRenderer = new TileFocalManipulatorRenderer();
    private final TileFluxScrubberRenderer fluxScrubberRenderer = new TileFluxScrubberRenderer();

    public ItemStoneDeviceRenderer() {
        matrixRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        stabilizerRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        converterRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        focalManipulatorRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
        fluxScrubberRenderer.setRendererDispatcher(TileEntityRendererDispatcher.instance);
    }

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        int meta = stack.getMetadata();
        GlStateManager.pushMatrix();
        if (meta == 2) {
            TileInfusionMatrix matrix = new TileInfusionMatrix();
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            matrixRenderer.render(matrix, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
        } else if (meta == 9 || meta == 10) {
            TileNodeStabilizer stabilizer = new TileNodeStabilizer();
            stabilizer.lock = meta == 9 ? 1 : 2;
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            stabilizerRenderer.render(stabilizer, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
        } else if (meta == 11) {
            TileNodeConverter converter = new TileNodeConverter();
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            converterRenderer.render(converter, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
        } else if (meta == 13) {
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            focalManipulatorRenderer.render(new TileFocalManipulator(), 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
        } else if (meta == 14) {
            TileFluxScrubber scrubber = new TileFluxScrubber();
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);
            fluxScrubberRenderer.render(scrubber, 0.0D, 0.0D, 0.0D, partialTicks, 0, 1.0F);
        }
        GlStateManager.popMatrix();
    }
}
