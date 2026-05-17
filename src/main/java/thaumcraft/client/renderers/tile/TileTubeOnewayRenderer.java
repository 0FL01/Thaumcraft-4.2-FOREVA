package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.common.tiles.TileTubeOneway;

public class TileTubeOnewayRenderer extends TileEntitySpecialRenderer<TileTubeOneway> {
    private static final ResourceLocation VALVE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/valve.png");

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
        for (int i = 0; i < 3; i++) {
            renderDisc(x, y, z, tile.facing, -0.10F - i * 0.11F, 0xCC7290FF);
        }
    }

    private void renderDisc(double x, double y, double z, EnumFacing face, float localOffset, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(
                x + 0.5D + face.getXOffset() * localOffset,
                y + 0.5D + face.getYOffset() * localOffset,
                z + 0.5D + face.getZOffset() * localOffset);
        orientByFace(face);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        TileRenderHelper.drawTexturedQuad(0.18F, color, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
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
