package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.tiles.TileFocalManipulator;

public class TileFocalManipulatorRenderer extends TileEntitySpecialRenderer<TileFocalManipulator> {
    @Override
    public void render(TileFocalManipulator tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        ItemStack focus = tile.getStackInSlot(0);
        if (!focus.isEmpty() && focus.getItem() instanceof ItemFocusBasic) {
            float hover = MathHelper.sin(ticks / 14.0F) * 0.2F + 0.2F;
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5D, y + 1.0D, z + 0.5D);
            GlStateManager.rotate(ticks % 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, hover, 0.0F);
            RenderHelper.enableStandardItemLighting();
            net.minecraft.client.Minecraft.getMinecraft().getRenderItem()
                    .renderItem(focus.copy(), ItemCameraTransforms.TransformType.GROUND);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
        }
    }
}
