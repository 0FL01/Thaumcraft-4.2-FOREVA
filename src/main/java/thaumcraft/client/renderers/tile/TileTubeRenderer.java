package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import thaumcraft.common.tiles.TileTube;

public class TileTubeRenderer extends TileEntitySpecialRenderer<TileTube> {
    @Override
    public void render(TileTube tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }
        TubeConduitRenderHelper.renderConduit(tile, tile, tile.openSides,
                TubeConduitRenderHelper.TubeType.ORDINARY, null, x, y, z);
    }
}
