package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.tiles.TileTubeValve;

public class TileTubeValveRenderer extends TileEntitySpecialRenderer<TileTubeValve> {
    private static final ResourceLocation VALVE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/valve.png");

    @Override
    public void render(TileTubeValve tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        float rotation = ticks * 6.0F;
        float sink = (float) (Math.sin(ticks / 12.0F) * 0.05F + 0.05F);
        bindTexture(VALVE_TEXTURE);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        orientByFace(tile.facing);
        GlStateManager.rotate(rotation, 0.0F, 0.0F, 1.0F);
        GlStateManager.translate(0.0D, -Math.min(0.12D, sink), 0.0D);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        TileRenderHelper.drawTexturedQuad(0.22F, tile.allowFlow ? 0xE67FC8FF : 0xE6FF6666, 0.0F, 1.0F, 0.0F, 1.0F);
        TileRenderHelper.drawTexturedQuad(0.10F, 0xD0FFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
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
