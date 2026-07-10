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
        if (tile == null) {
            return;
        }
        TubeConduitRenderHelper.renderConduit(tile, tile, tile.openSides,
                TubeConduitRenderHelper.TubeType.DIRECTIONAL, null, x, y, z);
        if (tile.getWorld() == null || tile.getPos() == null) {
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
        try {
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
        } finally {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    private static void orientByFace(EnumFacing face) {
        if (face.getYOffset() == 0) {
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        } else {
            GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(90.0F, face.getYOffset(), 0.0F, 0.0F);
        }
        GlStateManager.rotate(90.0F, face.getXOffset(), face.getYOffset(), face.getZOffset());
    }
}
