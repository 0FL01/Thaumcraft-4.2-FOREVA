package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.tiles.TileWandPedestal;

public class TileWandPedestalRenderer extends TileEntitySpecialRenderer<TileWandPedestal> {
    private static final ResourceLocation WISPY =
            new ResourceLocation("thaumcraft", "textures/misc/wispy.png");

    @Override
    public void render(TileWandPedestal tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        ItemStack stack = tile.getStackInSlot(0);
        if (!stack.isEmpty()) {
            float ticks = TileRenderHelper.ticks(tile, partialTicks);
            float scale = stack.getItem() instanceof ItemBlock ? 2.0F : 1.0F;
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5D, y + 1.15D, z + 0.5D);
            TileRenderHelper.renderFloatingItem(stack.copy(), ticks, 0.0F, scale);
            GlStateManager.popMatrix();
        }

        if (!tile.focus.isEmpty()) {
            float ticks = TileRenderHelper.ticks(tile, partialTicks);
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5D, y + 1.40D, z + 0.5D);
            TileRenderHelper.renderFloatingItem(tile.focus.copy(), ticks + 23.0F, 0.0F, 0.5F);
            GlStateManager.popMatrix();
        }

        if (!stack.isEmpty() && !tile.focus.isEmpty()) {
            float ticks = TileRenderHelper.ticks(tile, partialTicks);
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5D, y + 1.26D, z + 0.5D);
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 1);
            bindTexture(WISPY);
            TileRenderHelper.orientBillboardToPlayer();
            float scale = 0.16F + (float) Math.sin(ticks / 7.0F) * 0.02F;
            TileRenderHelper.drawTexturedQuad(scale, 0xA0B89DFF, 0.0F, 1.0F, 0.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }
}
