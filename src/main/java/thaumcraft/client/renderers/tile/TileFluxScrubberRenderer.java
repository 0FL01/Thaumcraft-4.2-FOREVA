package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.ModelFluxScrubber;
import thaumcraft.common.tiles.TileFluxScrubber;

public class TileFluxScrubberRenderer extends TileEntitySpecialRenderer<TileFluxScrubber> {
    private static final ResourceLocation SCRUBBER =
            new ResourceLocation("thaumcraft", "textures/models/fluxscrubber.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelFluxScrubber model = new ModelFluxScrubber();

    @Override
    public void render(TileFluxScrubber tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        float ticks = Minecraft.getMinecraft().player == null
                ? TileRenderHelper.ticks(tile, partialTicks)
                : Minecraft.getMinecraft().player.ticksExisted + partialTicks;
        float bob = (float) Math.sin((ticks + tile.count) / 8.0F) * 0.075F + 0.075F;

        GlStateManager.pushMatrix();
        try {
            translateFromOrientation(x, y, z, tile.facing);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            bindTexture(SCRUBBER);
            model.renderCap(MODEL_SCALE);

            GlStateManager.pushMatrix();
            try {
                GlStateManager.translate(0.0D, 0.0D, -bob);
                model.renderTip(MODEL_SCALE);
            } finally {
                GlStateManager.popMatrix();
            }
        } finally {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    private static void translateFromOrientation(double x, double y, double z, EnumFacing facing) {
        GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
        int orientation = facing == null ? EnumFacing.DOWN.ordinal() : facing.ordinal();
        if (orientation == 0) {
            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
        } else if (orientation == 1) {
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        } else if (orientation == 3) {
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        } else if (orientation == 4) {
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        } else if (orientation == 5) {
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        }
        GlStateManager.translate(0.0D, 0.0D, -0.5D);
    }
}
