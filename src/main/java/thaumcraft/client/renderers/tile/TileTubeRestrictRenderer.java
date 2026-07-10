package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import thaumcraft.common.tiles.TileTubeRestrict;

public class TileTubeRestrictRenderer extends TileEntitySpecialRenderer<TileTubeRestrict> {
    @Override
    public void render(TileTubeRestrict tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }
        TubeConduitRenderHelper.renderConduit(tile, tile, tile.openSides,
                TubeConduitRenderHelper.TubeType.RESTRICTED, null, x, y, z);
    }
}
