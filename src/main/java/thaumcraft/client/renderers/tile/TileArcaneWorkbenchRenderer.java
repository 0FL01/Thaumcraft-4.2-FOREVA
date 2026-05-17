package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.TileArcaneWorkbench;

public class TileArcaneWorkbenchRenderer extends TileEntitySpecialRenderer<TileArcaneWorkbench> {
    private static final ResourceLocation TABLE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/worktable.png");

    @Override
    public void render(TileArcaneWorkbench tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        renderTableGlyph(x, y, z);

        ItemStack wand = tile.getStackInSlot(10);
        if (!wand.isEmpty() && wand.getItem() instanceof ItemWandCasting) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.68D, y + 1.05D, z + 0.24D);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(18.0F, 0.0F, 0.0F, 1.0F);
            TileRenderHelper.renderFloatingItem(wand.copy(), ticks, 0.0F, 0.60F);
            GlStateManager.popMatrix();
        }
    }

    private void renderTableGlyph(double x, double y, double z) {
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
}
