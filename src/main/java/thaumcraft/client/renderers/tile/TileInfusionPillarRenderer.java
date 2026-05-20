package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.ModelInfusionPillar;
import thaumcraft.common.tiles.TileInfusionPillar;

public class TileInfusionPillarRenderer extends TileEntitySpecialRenderer<TileInfusionPillar> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/pillar.png");
    private static final ModelInfusionPillar MODEL = new ModelInfusionPillar();

    @Override
    public void render(TileInfusionPillar tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y, z + 0.5D);
        GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
        rotateByOrientation(tile.orientation);
        bindTexture(TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        MODEL.render();
        GlStateManager.popMatrix();
    }

    private static void rotateByOrientation(byte orientation) {
        if (orientation == 3) {
            GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        } else if (orientation == 4) {
            GlStateManager.rotate(270.0F, 0.0F, 0.0F, 1.0F);
        } else if (orientation == 5) {
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        }
    }
}
