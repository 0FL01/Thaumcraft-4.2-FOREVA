package thaumcraft.client.renderers.tile;

import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.blocks.BlockManaPod;
import thaumcraft.common.tiles.TileManaPod;

public class TileManaPodRenderer extends TileEntitySpecialRenderer<TileManaPod> {
    private static final ResourceLocation POD0 =
            new ResourceLocation("thaumcraft", "textures/models/manapod_0.png");
    private static final ResourceLocation POD2 =
            new ResourceLocation("thaumcraft", "textures/models/manapod_2.png");

    @Override
    public void render(TileManaPod pod, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (pod == null || pod.getWorld() == null) {
            return;
        }

        int meta = pod.getWorld().getBlockState(pod.getPos()).getValue(BlockManaPod.TYPE);
        if (meta <= 1) {
            return;
        }

        Aspect aspect = pod.aspect == null ? Aspect.PLANT : pod.aspect;
        Color ac = new Color(aspect.getColor());
        float ar = ac.getRed() / 255.0F;
        float ag = ac.getGreen() / 255.0F;
        float ab = ac.getBlue() / 255.0F;

        float br = 0.14509805F;
        float bg = 0.6156863F;
        float bb = 0.45882353F;
        float fr;
        float fg;
        float fb;
        if (meta == 7) {
            fr = ar;
            fg = ag;
            fb = ab;
        } else {
            float mix = Math.max(0.0F, meta - 2.0F);
            fr = (br + ar * mix) / (mix + 1.0F);
            fg = (bg + ag * mix) / (mix + 1.0F);
            fb = (bb + ab * mix) / (mix + 1.0F);
        }

        float ticks = TileRenderHelper.ticks(pod, partialTicks);
        float pulse = 0.90F + (float) Math.sin((ticks + (pod.hashCode() % 100)) / 8.0F) * 0.10F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.75D, z + 0.5D);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);

        if (meta > 2) {
            GlStateManager.pushMatrix();
            float innerScale = (float) (0.125D * meta * pulse);
            GlStateManager.translate(0.0D, 0.10D, 0.0D);
            GlStateManager.scale(innerScale, innerScale, innerScale);
            bindTexture(POD0);
            TileRenderHelper.drawTexturedQuad(0.75F, 0xD0FFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
            GlStateManager.popMatrix();
        }

        float outerScale = (float) (0.15D * meta);
        GlStateManager.scale(outerScale, outerScale, outerScale);
        GlStateManager.color(fr, fg, fb, 0.9F);
        bindTexture(POD2);
        TileRenderHelper.drawTexturedQuad(0.75F, 0xE6FFFFFF, 0.0F, 1.0F, 0.0F, 1.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
