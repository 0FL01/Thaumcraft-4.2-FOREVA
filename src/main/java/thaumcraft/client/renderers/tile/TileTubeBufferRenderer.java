package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.client.renderers.models.ModelTubeValve;
import thaumcraft.common.tiles.TileTubeBuffer;

public class TileTubeBufferRenderer extends TileEntitySpecialRenderer<TileTubeBuffer> {
    private static final ResourceLocation VALVE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/valve.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelTubeValve model = new ModelTubeValve();

    @Override
    public void render(TileTubeBuffer tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }
        TubeConduitRenderHelper.renderConduit(tile, tile, tile.openSides,
                TubeConduitRenderHelper.TubeType.BUFFER, null, x, y, z);
        if (tile.getWorld() == null || tile.getPos() == null) {
            return;
        }
        bindTexture(VALVE_TEXTURE);
        for (EnumFacing face : EnumFacing.VALUES) {
            int idx = face.getIndex();
            if (tile.chokedSides[idx] <= 0 || !tile.openSides[idx]) {
                continue;
            }
            TileEntity neighbour = ThaumcraftApiHelper.getConnectableTile(
                    tile.getWorld(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), face);
            if (neighbour == null) {
                continue;
            }
            renderValve(x, y, z, face.getOpposite(), tile.chokedSides[idx] == 2);
        }
    }

    private void renderValve(double x, double y, double z, EnumFacing face, boolean hardChoked) {
        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
            orientByFace(face);
            GlStateManager.scale(1.2F, 1.0F, 1.2F);
            GlStateManager.translate(0.0D, -0.5D, 0.0D);
            if (hardChoked) {
                GlStateManager.color(1.0F, 0.3F, 0.3F, 1.0F);
            } else {
                GlStateManager.color(0.3F, 0.3F, 1.0F, 1.0F);
            }
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
