package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.tiles.TileArcaneBoreBase;
import thaumcraft.common.tiles.TileArcaneLamp;
import thaumcraft.common.tiles.TileArcaneLampGrowth;

public class TileArcaneLampRenderer extends TileEntitySpecialRenderer<TileEntity> {
    private static final ResourceLocation BORE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/Bore.png");

    @Override
    public void render(TileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        EnumFacing facing = facingFor(tile);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        orientByFace(facing);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        bindTexture(BORE_TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, 0.0D, -0.48D);
        TileRenderHelper.drawTexturedQuad(0.24F, 0xE6FFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
        TileRenderHelper.drawTexturedQuad(0.16F, 0x99CCFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.popMatrix();

        if (tile.getWorld().getTileEntity(tile.getPos().offset(facing)) instanceof TileArcaneBoreBase) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0D, 0.0D, -0.96D);
            TileRenderHelper.drawTexturedQuad(0.17F, 0xCCFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
            GlStateManager.popMatrix();
        }

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
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

    private static void orientByFace(EnumFacing facing) {
        if (facing == null) {
            return;
        }
        switch (facing) {
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
