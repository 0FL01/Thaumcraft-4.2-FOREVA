package thaumcraft.client.renderers.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import thaumcraft.client.lib.EldritchDiagnostics;
import thaumcraft.common.tiles.TileEldritchNothing;

public class TileEldritchNothingRenderer extends TileEntitySpecialRenderer<TileEldritchNothing> {
    private static final float FACE_MIN = 0.0F;
    private static final float FACE_MAX = 1.0F;

    @Override
    public void render(TileEldritchNothing tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        EldritchDiagnostics.nothingTESRCalls++;

        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
        // LOD: full 16-layer field rendering within 32 blocks, cheap single-quad fallback beyond.
        boolean inRange = viewer != null && tile.getPos().distanceSq(viewer.posX, viewer.posY, viewer.posZ) < 256.0D;
        double viewX = 0.0D;
        double viewY = 0.0D;
        double viewZ = 0.0D;
        if (viewer != null) {
            viewX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks;
            viewY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks;
            viewZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks;
        }

        GlStateManager.pushMatrix();
        GlStateManager.disableFog();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);

        for (EnumFacing face : EnumFacing.VALUES) {
            if (shouldRenderFace(tile.getPos(), face)) {
                float offset = faceAxisOffset(face);
                EldritchDiagnostics.nothingFacesRendered++;
                if (inRange) {
                    EldritchDiagnostics.nothingInRangeFaces++;
                } else {
                    EldritchDiagnostics.nothingFarFaces++;
                }
                LayeredFieldPlaneHelper.renderLayeredFace(
                        face, x, y, z, offset, inRange, 1.0F, viewX, viewY, viewZ);
            }
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableFog();
        GlStateManager.popMatrix();
    }

    private boolean shouldRenderFace(BlockPos origin, EnumFacing face) {
        IBlockState adjacent = getWorld().getBlockState(origin.offset(face));
        return !adjacent.isOpaqueCube();
    }

    private static float faceAxisOffset(EnumFacing face) {
        switch (face) {
            case DOWN:
            case NORTH:
            case WEST:
                return FACE_MIN;
            case UP:
            case SOUTH:
            case EAST:
                return FACE_MAX;
            default:
                return FACE_MIN;
        }
    }
}
