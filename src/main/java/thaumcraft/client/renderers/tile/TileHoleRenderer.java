package thaumcraft.client.renderers.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileHole;

public class TileHoleRenderer extends TileEntitySpecialRenderer<TileHole> {
    private static final float OFFSET_NEAR = 0.001F;
    private static final float OFFSET_FAR = 0.999F;

    @Override
    public void render(TileHole tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
        boolean inRange = viewer != null && tile.getPos().distanceSq(viewer.posX, viewer.posY, viewer.posZ) < 512.0D;
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
                float axisOffset = face.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? OFFSET_FAR : OFFSET_NEAR;
                LayeredFieldPlaneHelper.renderLayeredFace(
                        face, x, y, z, axisOffset, inRange, 0.5F, viewX, viewY, viewZ);
            }
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableFog();
        GlStateManager.popMatrix();
    }

    private boolean shouldRenderFace(BlockPos origin, EnumFacing face) {
        BlockPos adjPos = origin.offset(face);
        IBlockState adjState = getWorld().getBlockState(adjPos);
        return adjState.isOpaqueCube() && adjState.getBlock() != ConfigBlocks.blockHole;
    }
}
