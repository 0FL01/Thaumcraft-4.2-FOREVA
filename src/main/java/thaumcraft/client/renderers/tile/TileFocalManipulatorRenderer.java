package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.client.renderers.models.ModelArcaneWorkbench;
import thaumcraft.common.tiles.TileFocalManipulator;

public class TileFocalManipulatorRenderer extends TileEntitySpecialRenderer<TileFocalManipulator> {
    private static final ResourceLocation TABLE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/wandtable.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelArcaneWorkbench tableModel = new ModelArcaneWorkbench();

    @Override
    public void render(TileFocalManipulator tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        renderTableModel(x, y, z);

        ItemStack focus = tile.getStackInSlot(0);
        if (!focus.isEmpty() && focus.getItem() instanceof ItemFocusBasic) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5D, y + 1.0D, z + 0.5D);
            GlStateManager.rotate(ticks % 360.0F, 0.0F, 1.0F, 0.0F);
            TileRenderHelper.renderFloatingItem(focus.copy(), ticks, 0.2F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    private void renderTableModel(double x, double y, double z) {
        GlStateManager.pushMatrix();
        bindTexture(TABLE_TEXTURE);
        GlStateManager.translate(x + 0.5D, y + 1.0D, z + 0.5D);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        tableModel.renderAll(MODEL_SCALE);
        GlStateManager.popMatrix();
    }
}
