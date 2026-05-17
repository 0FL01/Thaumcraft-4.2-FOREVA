package thaumcraft.client.renderers.tile;

import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemDye;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thaumcraft.client.renderers.models.ModelBanner;
import thaumcraft.common.tiles.TileBanner;

public class TileBannerRenderer extends TileEntitySpecialRenderer<TileBanner> {
    private static final ResourceLocation BANNER_BLANK = new ResourceLocation("thaumcraft", "textures/models/banner_blank.png");
    private static final ResourceLocation BANNER_CULTIST = new ResourceLocation("thaumcraft", "textures/models/banner_cultist.png");
    private final ModelBanner model = new ModelBanner();

    @Override
    public void render(TileBanner banner, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (banner == null) {
            return;
        }

        long worldTicks = banner.getWorld() != null ? banner.getWorld().getTotalWorldTime() : 0L;
        GlStateManager.pushMatrix();
        bindTexture(banner.getAspect() == null && banner.getColor() == -1 ? BANNER_CULTIST : BANNER_BLANK);
        GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if (banner.getWorld() != null) {
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            float facing = (banner.getFacing() * 360.0F) / 16.0F;
            GlStateManager.rotate(facing, 0.0F, 1.0F, 0.0F);
        }

        if (!banner.getWall()) {
            model.renderPole();
        } else {
            GlStateManager.translate(0.0F, 0.0F, -0.4125F);
        }

        model.renderBeam();
        if (banner.getColor() != -1) {
            int colorIndex = banner.getColor() & 0xFF;
            if (colorIndex >= 0 && colorIndex < ItemDye.DYE_COLORS.length) {
                Color color = new Color(ItemDye.DYE_COLORS[colorIndex]);
                GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);
            }
        }
        model.renderTabs();

        float waveTicks = (float) (banner.getPos().getX() * 7 + banner.getPos().getY() * 9 + banner.getPos().getZ() * 13)
                + worldTicks + partialTicks;
        float rx = (0.005F + 0.005F * MathHelper.cos((float) (waveTicks * Math.PI * 0.02F))) * (float) Math.PI;
        model.banner.rotateAngleX = rx;
        model.renderBanner();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if (banner.getAspect() != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 0.05001F);
            GlStateManager.scale(0.0375F, 0.0375F, 0.0375F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-rx * 57.295776F * 2.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            bindTexture(banner.getAspect().getImage());
            TileRenderHelper.drawTexturedQuad(8.0F, 0xFFFFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }

        GlStateManager.popMatrix();
    }
}
