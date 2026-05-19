package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.ModelCentrifuge;
import thaumcraft.common.tiles.TileCentrifuge;

public class TileCentrifugeRenderer extends TileEntitySpecialRenderer<TileCentrifuge> {
    private static final ResourceLocation CENTRIFUGE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/centrifuge.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelCentrifuge model = new ModelCentrifuge();

    @Override
    public void render(TileCentrifuge tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float spin = tile.rotation;
        bindTexture(CENTRIFUGE_TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        GlStateManager.rotate(spin, 0.0F, 1.0F, 0.0F);
        model.renderSpinnyBit(MODEL_SCALE);
        GlStateManager.popMatrix();
    }
}
