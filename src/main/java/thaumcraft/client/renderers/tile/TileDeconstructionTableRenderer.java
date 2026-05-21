package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thaumcraft.client.renderers.models.ModelArcaneWorkbench;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.tiles.TileDeconstructionTable;

public class TileDeconstructionTableRenderer extends TileEntitySpecialRenderer<TileDeconstructionTable> {
    private static final ResourceLocation TABLE_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/decontable.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelArcaneWorkbench tableModel = new ModelArcaneWorkbench();

    @Override
    public void render(TileDeconstructionTable tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }

        GlStateManager.pushMatrix();
        bindTexture(TABLE_TEXTURE);
        GlStateManager.translate(x + 0.5F, y + 1.0F, z + 0.5F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        tableModel.renderAll(MODEL_SCALE);
        GlStateManager.popMatrix();

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        renderThaumometer(tile, x, y, z);

        ItemStack input = tile.getStackInSlot(0);
        if (!input.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5D, y + 1.15D, z + 0.5D);
            GlStateManager.rotate(ticks % 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 1);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.75F);
            GlStateManager.translate(0.0F, MathHelper.sin(ticks / 14.0F) * 0.2F + 0.2F, 0.0F);
            renderItemGround(tile, input.copy(), 0.65F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }

        if (tile.aspect != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5D, y + 1.08D, z + 0.5D);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(ticks % 360.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.scale(0.024F, 0.024F, 0.024F);
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

    private void renderThaumometer(TileDeconstructionTable tile, double x, double y, double z) {
        if (ConfigItems.itemThaumometer == null) {
            return;
        }
        ItemStack thaumometer = new ItemStack(ConfigItems.itemThaumometer);
        if (thaumometer.isEmpty()) return;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.92D, z + 0.5D);
        GlStateManager.scale(0.8F, 0.8F, 0.8F);
        TileRenderHelper.renderEntityItem(tile, thaumometer, 0.0F);
        GlStateManager.popMatrix();
    }

    private void renderItemGround(TileDeconstructionTable tile, ItemStack stack, float scale) {
        if (stack.isEmpty()) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        TileRenderHelper.renderEntityItem(tile, stack, 0.0F);
        GlStateManager.popMatrix();
    }
}
