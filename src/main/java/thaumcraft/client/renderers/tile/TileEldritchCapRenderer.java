package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.tiles.TileEldritchAltar;

public class TileEldritchCapRenderer extends TileEntitySpecialRenderer<TileEntity> {
    private static final ResourceLocation CAP_TEXTURE =
            new ResourceLocation("thaumcraft", "textures/models/obelisk_cap.png");
    private static final ResourceLocation CAP_TEXTURE_OUTER =
            new ResourceLocation("thaumcraft", "textures/models/obelisk_cap_2.png");

    @Override
    public void render(TileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (tile == null || tile.getWorld() == null) {
            return;
        }

        ResourceLocation texture = tile.getWorld().provider.getDimension() == Config.dimensionOuterId
                ? CAP_TEXTURE_OUTER
                : CAP_TEXTURE;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.08D, z + 0.5D);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        bindTexture(texture);

        TileRenderHelper.drawTexturedQuad(0.52F, 0xFFFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
        TileRenderHelper.drawTexturedQuad(0.52F, 0xFFFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();

        if (tile instanceof TileEldritchAltar) {
            renderAltarEyes((TileEldritchAltar) tile, x, y, z, partialTicks);
        }
    }

    private static void renderAltarEyes(TileEldritchAltar altar, double x, double y, double z, float partialTicks) {
        if (ConfigItems.itemEldritchObject == null) {
            return;
        }
        int eyes = Math.max(0, Math.min(4, altar.getEyes()));
        if (eyes == 0) {
            return;
        }
        ItemStack eye = new ItemStack(ConfigItems.itemEldritchObject, 1, 0);
        float ticks = TileRenderHelper.ticks(altar, partialTicks);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.18D, z + 0.5D);
        for (int i = 0; i < eyes; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.rotate(i * 90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.42D, 0.0D, 0.0D);
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(18.0F, -1.0F, 0.0F, 0.0F);
            TileRenderHelper.renderFloatingItem(eye.copy(), ticks + i * 9.0F, 0.0F, 0.35F);
            GlStateManager.popMatrix();
        }
        GlStateManager.popMatrix();
    }
}
