package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.ModelArcaneWorkbench;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.TileArcaneWorkbench;

public class TileArcaneWorkbenchRenderer extends TileEntitySpecialRenderer<TileArcaneWorkbench> {
    private static final ResourceLocation TABLE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/worktable.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelArcaneWorkbench tableModel = new ModelArcaneWorkbench();

    @Override
    public void render(TileArcaneWorkbench tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        renderTableModel(x, y, z);

        ItemStack wand = tile.getStackInSlot(10);
        if (!wand.isEmpty() && wand.getItem() instanceof ItemWandCasting) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.65D, y + 1.0625D, z + 0.25D);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(20.0F, 0.0F, 0.0F, 1.0F);
            TileRenderHelper.renderFloatingItem(wand.copy(), ticks, 0.0F, 0.60F);
            GlStateManager.popMatrix();
        }
    }

    private void renderTableModel(double x, double y, double z) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 1.0D, z + 0.5D);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture(TABLE_TEXTURE);
        tableModel.renderAll(MODEL_SCALE);
        GlStateManager.popMatrix();
    }
}
