package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.TileEssentiaReservoir;

public class TileEssentiaReservoirRenderer extends TileEntitySpecialRenderer<TileEssentiaReservoir> {
    private static final ResourceLocation RESERVOIR_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/reservoir.png");

    @Override
    public void render(TileEssentiaReservoir tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        orientByFace(tile.facing);
        bindTexture(RESERVOIR_TEXTURE);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        TileRenderHelper.drawTexturedQuad(0.40F, 0xCCFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        TileRenderHelper.drawTexturedQuad(0.40F, 0xCCFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);

        renderLiquid(tile);

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void renderLiquid(TileEssentiaReservoir tile) {
        if (tile.essentia == null || tile.essentia.visSize() <= 0) {
            return;
        }
        float fill = (float) tile.essentia.visSize() / (float) Math.max(1, tile.maxAmount);
        float level = -0.22F + 0.44F * TileRenderHelper.clamp01(fill);

        int color = colorFromReservoir(tile);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, level, 0.0D);
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        TileRenderHelper.drawTexturedQuad(0.24F, color, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private static int colorFromReservoir(TileEssentiaReservoir tile) {
        Aspect a = tile.displayAspect;
        if (a == null && tile.essentia != null) {
            Aspect[] aspects = tile.essentia.getAspects();
            if (aspects.length > 0) {
                a = aspects[0];
            }
        }
        if (a != null) {
            return 0xE6000000 | (a.getColor() & 0x00FFFFFF);
        }
        int r = Math.min(255, Math.max(0, (int) (tile.colorR * 255.0F)));
        int g = Math.min(255, Math.max(0, (int) (tile.colorG * 255.0F)));
        int b = Math.min(255, Math.max(0, (int) (tile.colorB * 255.0F)));
        return (0xE6 << 24) | (r << 16) | (g << 8) | b;
    }

    private static void orientByFace(EnumFacing facing) {
        if (facing == null) {
            return;
        }
        switch (facing) {
            case DOWN:
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case UP:
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
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
