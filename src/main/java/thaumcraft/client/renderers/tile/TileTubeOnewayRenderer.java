package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.client.renderers.models.ModelTubeValve;
import thaumcraft.common.tiles.TileTubeOneway;

public class TileTubeOnewayRenderer extends TileEntitySpecialRenderer<TileTubeOneway> {
    private static final ResourceLocation VALVE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/valve.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelTubeValve model = new ModelTubeValve();

    @Override
    public void render(TileTubeOneway tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }
        if (ThaumcraftApiHelper.getConnectableTile(
                tile.getWorld(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(),
                tile.facing.getOpposite()) == null) {
            return;
        }

        bindTexture(VALVE_TEXTURE);
        renderStackedValves(x, y, z, tile.facing);
    }

    private void renderStackedValves(double x, double y, double z, EnumFacing face) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        orientByFace(face);
        GlStateManager.color(0.45F, 0.5F, 1.0F, 1.0F);
        GlStateManager.scale(1.1F, 0.5F, 1.1F);
        GlStateManager.translate(0.0D, -0.5D, 0.0D);
        model.render(MODEL_SCALE);
        GlStateManager.translate(0.0D, -0.25D, 0.0D);
        model.render(MODEL_SCALE);
        GlStateManager.translate(0.0D, -0.25D, 0.0D);
        model.render(MODEL_SCALE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private static void orientByFace(EnumFacing face) {
        switch (face) {
            case DOWN:
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case UP:
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case NORTH:
                break;
            case SOUTH:
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                break;
            case WEST:
                GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                break;
            case EAST:
                GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                break;
            default:
                break;
        }
    }
}
