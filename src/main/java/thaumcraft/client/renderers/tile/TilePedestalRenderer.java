package thaumcraft.client.renderers.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import thaumcraft.common.tiles.TilePedestal;

public class TilePedestalRenderer extends TileEntitySpecialRenderer<TilePedestal> {

    @Override
    public void render(TilePedestal tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null) {
            return;
        }
        ItemStack stack = tile.getStackInSlot(0);
        if (stack == null || stack.isEmpty()) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        float bob = MathHelper.sin((ticks % 32767.0F) / 16.0F) * 0.05F;
        float scale = stack.getItem() instanceof ItemBlock ? 2.0F : 1.0F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 1.15D + bob, z + 0.5D);
        GlStateManager.rotate(ticks % 360.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(scale, scale, scale);
        TileRenderHelper.renderEntityItem(tile, stack, 0.0F);
        if (!Minecraft.isFancyGraphicsEnabled()) {
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            TileRenderHelper.renderEntityItem(tile, stack, 0.0F);
        }
        GlStateManager.popMatrix();
    }
}
