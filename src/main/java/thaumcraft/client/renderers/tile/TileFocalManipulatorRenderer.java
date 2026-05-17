package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.TileFocalManipulator;

public class TileFocalManipulatorRenderer extends TileEntitySpecialRenderer<TileFocalManipulator> {

    @Override
    public void render(TileFocalManipulator tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        float ticks = TileRenderHelper.ticks(tile, partialTicks);
        ItemStack focus = tile.getStackInSlot(0);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 1.1D, z + 0.5D);
        if (focus != null && !focus.isEmpty()) {
            TileRenderHelper.renderFloatingItem(focus.copy(), ticks, 0.0F, 0.8F);
        }

        Aspect[] aspects = tile.aspects == null ? null : tile.aspects.getAspects();
        if (aspects != null && aspects.length > 0) {
            for (int i = 0; i < aspects.length && i < 8; i++) {
                Aspect aspect = aspects[i];
                if (aspect == null) continue;
                float angle = ticks * (2.5F + i * 0.2F) + i * 45.0F;
                float radius = 0.22F + i * 0.01F;
                double ox = Math.cos(Math.toRadians(angle)) * radius;
                double oz = Math.sin(Math.toRadians(angle)) * radius;
                double oy = Math.sin(Math.toRadians(angle * 1.4F)) * 0.05F;

                GlStateManager.pushMatrix();
                GlStateManager.translate(ox, oy, oz);
                GlStateManager.disableLighting();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
                this.bindTexture(aspect.getImage());
                TileRenderHelper.orientBillboardToPlayer();
                int color = (0xCC << 24) | (aspect.getColor() & 0x00FFFFFF);
                TileRenderHelper.drawTexturedQuad(0.05F, color, 0.0F, 1.0F, 0.0F, 1.0F);
                GlStateManager.disableBlend();
                GlStateManager.enableLighting();
                GlStateManager.popMatrix();
            }
        }

        GlStateManager.popMatrix();
    }
}
