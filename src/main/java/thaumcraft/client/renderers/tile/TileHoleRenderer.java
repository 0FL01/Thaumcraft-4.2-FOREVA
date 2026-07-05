package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import thaumcraft.common.tiles.TileHole;

public class TileHoleRenderer extends TileEntitySpecialRenderer<TileHole> {
    private static final float OFFSET_NEAR = 0.001F;
    private static final float OFFSET_FAR = 0.999F;

    @Override
    public void render(TileHole tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        HoleRenderBatchCache.HoleRenderGroup group = HoleRenderBatchCache.getGroup(tile);
        if (group == null || !group.markRenderedThisFrame()) {
            return;
        }

        Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
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

        BlockPos origin = tile.getPos();
        for (HoleRenderBatchCache.MergedFaceRect rect : group.rects) {
            float axisOffset = rect.face.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? OFFSET_FAR : OFFSET_NEAR;
            LayeredFieldPlaneHelper.renderLayeredFaceRect(
                    rect.face,
                    x + rect.baseX - origin.getX(),
                    y + rect.baseY - origin.getY(),
                    z + rect.baseZ - origin.getZ(),
                    axisOffset,
                    true,
                    0.5F,
                    viewX,
                    viewY,
                    viewZ,
                    0.0F,
                    rect.sizeA,
                    0.0F,
                    rect.sizeB);
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableFog();
        GlStateManager.popMatrix();
    }
}
