package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.common.tiles.TileTubeBuffer;

public class TileTubeBufferRenderer extends TileEntitySpecialRenderer<TileTubeBuffer> {
    private static final ResourceLocation VALVE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/valve.png");

    @Override
    public void render(TileTubeBuffer tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
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
            int color = tile.chokedSides[idx] == 2 ? 0xE6FF5555 : 0xE65577FF;
            renderValveDisc(x, y, z, face, color, 0.42F, 1.22F);
        }
    }

    private void renderValveDisc(double x, double y, double z, EnumFacing face, int color, float depth, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(
                x + 0.5D + face.getXOffset() * depth,
                y + 0.5D + face.getYOffset() * depth,
                z + 0.5D + face.getZOffset() * depth);
        orientByFace(face);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        TileRenderHelper.drawTexturedQuad(0.17F, color, 0.0F, 1.0F, 0.0F, 1.0F);
        TileRenderHelper.drawTexturedQuad(0.09F, 0xD0FFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
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
