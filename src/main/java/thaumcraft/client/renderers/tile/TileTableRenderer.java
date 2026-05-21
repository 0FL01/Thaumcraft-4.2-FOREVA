package thaumcraft.client.renderers.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.blocks.BlockTable;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.client.renderers.models.ModelTable;
import thaumcraft.common.tiles.TileTable;

public class TileTableRenderer extends TileEntitySpecialRenderer<TileTable> {
    private static final ResourceLocation TABLE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/table.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelTable tableModel = new ModelTable();

    @Override
    public void render(TileTable tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        int md = 0;
        if (tile != null && tile.getWorld() != null) {
            IBlockState state = tile.getWorld().getBlockState(tile.getPos());
            if (state.getBlock() == ConfigBlocks.blockTable) {
                md = state.getValue(BlockTable.TYPE);
            }
        }
        if (md >= 6) {
            return;
        }

        GlStateManager.pushMatrix();
        bindTexture(TABLE_TEXTURE);
        GlStateManager.translate(x + 0.5F, y + 1.0F, z + 0.5F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        if (md == 1) {
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        tableModel.renderAll(MODEL_SCALE);
        GlStateManager.popMatrix();
    }
}
