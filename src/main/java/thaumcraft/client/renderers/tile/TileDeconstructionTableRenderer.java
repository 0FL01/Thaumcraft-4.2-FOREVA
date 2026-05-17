package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.tiles.TileDeconstructionTable;

public class TileDeconstructionTableRenderer extends TileEntitySpecialRenderer<TileDeconstructionTable> {
    private static final ResourceLocation TABLE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/decontable.png");

    @Override
    public void render(TileDeconstructionTable tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        renderPlate(x, y, z);
        renderThaumometer(x, y, z, ticks);

        ItemStack input = tile.getStackInSlot(0);
        if (!input.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5D, y + 1.15D, z + 0.5D);
            GlStateManager.rotate(ticks % 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 1);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.75F);
            TileRenderHelper.renderFloatingItem(input.copy(), ticks, 0.15F, 0.65F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }

        if (tile.aspect != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5D, y + 1.08D, z + 0.5D);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(ticks % 360.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(0.20F, 0.20F, 0.20F);
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            bindTexture(tile.aspect.getImage());
            TileRenderHelper.drawTexturedQuad(0.5F, 0xCCFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }

    private void renderPlate(double x, double y, double z) {
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

    private void renderThaumometer(double x, double y, double z, float ticks) {
        if (ConfigItems.itemThaumometer == null) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.92D, z + 0.5D);
        TileRenderHelper.renderFloatingItem(new ItemStack(ConfigItems.itemThaumometer), ticks, 0.0F, 0.8F);
        GlStateManager.popMatrix();
    }
}
