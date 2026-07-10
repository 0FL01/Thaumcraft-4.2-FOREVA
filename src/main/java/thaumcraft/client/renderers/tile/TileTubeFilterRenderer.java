package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import thaumcraft.common.tiles.TileTubeFilter;

public class TileTubeFilterRenderer extends TileEntitySpecialRenderer<TileTubeFilter> {
    @Override
    public void render(TileTubeFilter tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }
        TubeConduitRenderHelper.renderConduit(tile, tile, tile.openSides,
                TubeConduitRenderHelper.TubeType.FILTER, tile.aspectFilter, x, y, z);
    }
}
