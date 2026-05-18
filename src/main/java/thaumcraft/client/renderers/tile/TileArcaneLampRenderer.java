package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.ModelBoreBase;
import thaumcraft.common.tiles.TileArcaneBoreBase;
import thaumcraft.common.tiles.TileArcaneLamp;
import thaumcraft.common.tiles.TileArcaneLampGrowth;

public class TileArcaneLampRenderer extends TileEntitySpecialRenderer<TileEntity> {
    private static final ResourceLocation BORE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/Bore.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelBoreBase model = new ModelBoreBase();

    @Override
    public void render(TileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        EnumFacing facing = facingFor(tile);
        bindTexture(BORE_TEXTURE);

        // Lamp nozzle at local block orientation.
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y, z + 0.5D);
        orientNozzleByFace(facing);
        model.renderNozzle(MODEL_SCALE);
        GlStateManager.popMatrix();

        // Connector nozzle when there is a bore base in front of the lamp.
        if (tile.getWorld().getTileEntity(tile.getPos().offset(facing)) instanceof TileArcaneBoreBase) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(
                    x + 0.5D + facing.getXOffset(),
                    y + facing.getYOffset(),
                    z + 0.5D + facing.getZOffset());
            orientNozzleByFace(facing.getOpposite());
            model.renderNozzle(MODEL_SCALE);
            GlStateManager.popMatrix();
        }
    }

    private static EnumFacing facingFor(TileEntity tile) {
        if (tile instanceof TileArcaneLamp) {
            return ((TileArcaneLamp) tile).facing;
        }
        if (tile instanceof TileArcaneLampGrowth) {
            return ((TileArcaneLampGrowth) tile).facing;
        }
        return EnumFacing.DOWN;
    }

    private static void orientNozzleByFace(EnumFacing facing) {
        if (facing == null) {
            return;
        }
        switch (facing) {
            case DOWN:
                GlStateManager.translate(-0.5F, 0.5F, 0.0F);
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, -1.0F);
                break;
            case UP:
                GlStateManager.translate(0.5F, 0.5F, 0.0F);
                GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
                break;
            case NORTH:
                GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                break;
            case SOUTH:
                GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
                break;
            case WEST:
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                break;
            case EAST:
                GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
                break;
            default:
                break;
        }
    }
}
