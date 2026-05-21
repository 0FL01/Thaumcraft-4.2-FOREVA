package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.IScribeTools;
import thaumcraft.common.blocks.BlockTable;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.client.renderers.models.ModelResearchTable;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.research.ResearchNoteData;
import thaumcraft.common.tiles.TileResearchTable;

public class TileResearchTableRenderer extends TileEntitySpecialRenderer<TileResearchTable> {
    private static final ResourceLocation TABLE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/restable.png");
    private static final ResourceLocation NOTES_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/restable2.png");
    private static final ResourceLocation PARCHMENT_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/parchment.png");
    private static final ResourceLocation QUILL_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/quill.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelResearchTable tableModel = new ModelResearchTable();

    @Override
    public void render(TileResearchTable tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        int md = 0;
        if (tile.getWorld() != null) {
            IBlockState state = tile.getWorld().getBlockState(tile.getPos());
            if (state.getBlock() == ConfigBlocks.blockTable) {
                md = state.getValue(BlockTable.TYPE);
            }
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5F, y + 1.0F, z + 0.5F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        if (md == 2) {
            GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
        } else if (md == 3) {
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        } else if (md == 4) {
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        }

        bindTexture(TABLE_TEXTURE);
        tableModel.renderAll(MODEL_SCALE);

        ItemStack tools = tile.getStackInSlot(0);
        if (!tools.isEmpty() && tools.getItem() instanceof IScribeTools) {
            tableModel.renderInkwell(MODEL_SCALE);
            GlStateManager.pushMatrix();
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translate(-0.17F, 0.1F, -0.15F);
            GlStateManager.rotate(15.0F, 0.0F, 1.0F, 0.0F);
            bindTexture(QUILL_TEXTURE);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            TileRenderHelper.drawTexturedQuad(0.5F, 0xFFFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }

        bindTexture(PARCHMENT_TEXTURE);
        for (int a = 0; a < 6; ++a) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.1F, -0.01F - a * 0.015F, 0.35F);
            GlStateManager.rotate(90.0F, -1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(15.0F + (a % 3) * 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(0.5F, 0.6F, 0.6F);
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            TileRenderHelper.drawTexturedQuad(0.5F, 0xFFFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }

        ItemStack notes = tile.getStackInSlot(1);
        if (!notes.isEmpty() && notes.getItem() == ConfigItems.itemResearchNotes) {
            bindTexture(NOTES_TEXTURE);
            ResearchNoteData data = ResearchManager.getData(notes);
            int color = data != null ? data.color : 0x999999;
            GlStateManager.pushMatrix();
            tableModel.renderScroll(MODEL_SCALE, color);
            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();
    }
}
