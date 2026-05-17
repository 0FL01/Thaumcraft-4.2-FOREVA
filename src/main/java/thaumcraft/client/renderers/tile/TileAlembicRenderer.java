package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.TileAlembic;

public class TileAlembicRenderer extends TileEntitySpecialRenderer<TileAlembic> {

    private static final ResourceLocation LABEL_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/label.png");

    @Override
    public void render(TileAlembic tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        renderEssentiaColumn(tile, x, y, z);
        if (tile.aspectFilter != null) {
            renderAspectLabel(tile, x, y, z, tile.aspectFilter);
        }
    }

    private void renderEssentiaColumn(TileAlembic tile, double x, double y, double z) {
        if (tile.amount <= 0 || tile.aspect == null) {
            return;
        }
        float level = 0.1F + 0.72F * TileRenderHelper.clamp01((float) tile.amount / (float) tile.maxAmount);
        int color = 0xCC000000 | (tile.aspect.getColor() & 0x00FFFFFF);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + level, z + 0.5D);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        TileRenderHelper.drawSolidHorizontalQuad(0.20F, color);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private void renderAspectLabel(TileAlembic tile, double x, double y, double z, Aspect aspect) {
        EnumFacing facing = EnumFacing.byIndex(tile.facing);
        if (facing == null || facing.getAxis().isVertical()) {
            facing = EnumFacing.NORTH;
        }
        float lx = (float) (x + 0.5D + facing.getXOffset() * 0.409D);
        float ly = (float) (y + 0.468D);
        float lz = (float) (z + 0.5D + facing.getZOffset() * 0.409D);
        float yaw = -facing.getHorizontalAngle();

        GlStateManager.pushMatrix();
        GlStateManager.translate(lx, ly, lz);
        GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);

        bindTexture(LABEL_TEXTURE);
        TileRenderHelper.drawTexturedQuad(0.135F, 0xDDFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);

        bindTexture(aspect.getImage());
        GlStateManager.translate(0.0F, 0.0F, 0.001F);
        TileRenderHelper.drawTexturedQuad(0.06F, 0xFFFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
