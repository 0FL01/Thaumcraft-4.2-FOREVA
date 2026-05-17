package thaumcraft.client.renderers.tile;

import net.minecraft.init.Items;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.IScribeTools;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.tiles.TileResearchTable;

public class TileResearchTableRenderer extends TileEntitySpecialRenderer<TileResearchTable> {
    private static final ResourceLocation TABLE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/restable.png");
    private static final ResourceLocation NOTES_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/restable2.png");
    private static final ResourceLocation PARCHMENT_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/misc/parchment.png");

    @Override
    public void render(TileResearchTable tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        renderBasePlate(x, y, z);
        renderParchments(x, y, z);

        ItemStack tools = tile.getStackInSlot(0);
        if (!tools.isEmpty() && tools.getItem() instanceof IScribeTools) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.62D, y + 1.04D, z + 0.36D);
            GlStateManager.rotate(145.0F, 0.0F, 1.0F, 0.0F);
            TileRenderHelper.renderFloatingItem(new ItemStack(Items.FEATHER), ticks + 16.0F, 0.0F, 0.45F);
            GlStateManager.popMatrix();
        }

        ItemStack notes = tile.getStackInSlot(1);
        if (!notes.isEmpty() && notes.getItem() == ConfigItems.itemResearchNotes) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.38D, y + 1.02D, z + 0.58D);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            bindTexture(NOTES_TEXTURE);
            TileRenderHelper.drawTexturedQuad(0.22F, 0xCCFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.38D, y + 1.06D, z + 0.58D);
            TileRenderHelper.renderFloatingItem(notes.copy(), ticks + 6.0F, 0.0F, 0.45F);
            GlStateManager.popMatrix();
        }
    }

    private void renderBasePlate(double x, double y, double z) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 1.001D, z + 0.5D);
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        bindTexture(TABLE_TEXTURE);
        TileRenderHelper.drawTexturedQuad(0.32F, 0xCCFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void renderParchments(double x, double y, double z) {
        bindTexture(PARCHMENT_TEXTURE);
        for (int i = 0; i < 6; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.60D, y + 1.003D - i * 0.005D, z + 0.32D);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(15.0F + (i % 3) * 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(0.40F, 0.48F, 0.48F);
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            TileRenderHelper.drawTexturedQuad(0.25F, 0xCCFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }
}
