package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.renderers.models.ModelTable;
import thaumcraft.common.tiles.TileTable;

public class TileTableRenderer extends TileEntitySpecialRenderer<TileTable> {
    private static final ResourceLocation TABLE =
            new ResourceLocation("thaumcraft", "textures/models/table.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelTable tableModel = new ModelTable();

    @Override
    public void render(TileTable tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        int md = tile.getBlockMetadata();
        if (md >= 6) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 1.0D, z + 0.5D);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        if (md == 1) {
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        bindTexture(TABLE);
        tableModel.renderAll(MODEL_SCALE);
        GlStateManager.popMatrix();
    }
}
