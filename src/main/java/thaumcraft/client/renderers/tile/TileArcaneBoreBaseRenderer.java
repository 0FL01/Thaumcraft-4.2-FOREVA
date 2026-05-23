package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.ModelBoreBase;
import thaumcraft.common.tiles.TileArcaneBoreBase;

public class TileArcaneBoreBaseRenderer extends TileEntitySpecialRenderer<TileArcaneBoreBase> {
    private static final ResourceLocation BORE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/bore.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelBoreBase model = new ModelBoreBase();

    @Override
    public void render(TileArcaneBoreBase tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        EnumFacing facing = tile.orientation == null ? EnumFacing.NORTH : tile.orientation;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y, z + 0.5D);

        bindTexture(BORE_TEXTURE);
        model.render(MODEL_SCALE);

        GlStateManager.pushMatrix();
        applyNozzleRotation(facing);
        model.renderNozzle(MODEL_SCALE);
        GlStateManager.popMatrix();

        GlStateManager.popMatrix();
    }

    private static void applyNozzleRotation(EnumFacing facing) {
        switch (facing) {
            case NORTH:
                GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                break;
            case SOUTH:
                GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
                break;
            case WEST:
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                break;
            case UP:
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                break;
            case DOWN:
                GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
                break;
            case EAST:
            default:
                break;
        }
    }
}
