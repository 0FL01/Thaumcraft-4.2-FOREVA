package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.tiles.TileCrystal;
import thaumcraft.common.tiles.TileEldritchCrystal;

public class TileEldritchCrystalRenderer extends TileEntitySpecialRenderer<TileEldritchCrystal> {
    private static final ResourceLocation BASE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/blocks/crust.png");
    private static final ResourceLocation CRYSTAL_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/vcrystal.png");

    @Override
    public void render(TileEldritchCrystal tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        int rotationStep = Math.floorMod(tile.hashCode(), 4);
        float pulse = 0.92F + (float) Math.sin(TileRenderHelper.ticks(tile, partialTicks) / 6.0F) * 0.08F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        orientByAttachment(tile.orientation, rotationStep);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);

        bindTexture(BASE_TEXTURE);
        TileRenderHelper.drawTexturedQuad(0.32F, 0xCCFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        TileRenderHelper.drawTexturedQuad(0.32F, 0xCCFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);

        bindTexture(CRYSTAL_TEXTURE);
        int alphaPulse = Math.min(255, Math.max(0, (int) (220.0F * pulse)));
        int color = (alphaPulse << 24) | 0x00FFFFFF;
        TileRenderHelper.drawTexturedQuad(0.26F, color, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        TileRenderHelper.drawTexturedQuad(0.26F, color, 0.0F, 1.0F, 0.0F, 1.0F);

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private static void orientByAttachment(short orientation, int quarterTurns) {
        EnumFacing face = EnumFacing.byIndex(orientation);
        if (face != null) {
            switch (face) {
                case DOWN:
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                    break;
                case UP:
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                    break;
                case NORTH:
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case SOUTH:
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
        GlStateManager.translate(0.0F, 0.0F, -0.5F);
        GlStateManager.rotate(90.0F * quarterTurns, 0.0F, 0.0F, 1.0F);
    }
}
