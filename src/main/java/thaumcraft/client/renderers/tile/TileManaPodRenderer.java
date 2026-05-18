package thaumcraft.client.renderers.tile;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.blocks.BlockManaPod;
import thaumcraft.client.renderers.models.ModelManaPod;
import thaumcraft.common.tiles.TileManaPod;

public class TileManaPodRenderer extends TileEntitySpecialRenderer<TileManaPod> {
    private static final ResourceLocation POD0 =
            new ResourceLocation("thaumcraft", "textures/models/manapod_0.png");
    private static final ResourceLocation POD2 =
            new ResourceLocation("thaumcraft", "textures/models/manapod_2.png");
    private static final float MODEL_SCALE = 0.0625F;

    private final ModelManaPod model = new ModelManaPod();

    @Override
    public void render(TileManaPod pod, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (pod == null) {
            return;
        }

        int meta;
        Aspect aspect = Aspect.PLANT;
        int bright = 20;
        if (pod.getWorld() == null) {
            meta = 5;
        } else {
            meta = pod.getWorld().getBlockState(pod.getPos()).getValue(BlockManaPod.TYPE);
            if (pod.aspect != null) {
                aspect = pod.aspect;
            }
            bright = pod.getWorld().getBlockState(pod.getPos()).getPackedLightmapCoords(pod.getWorld(), pod.getPos());
        }

        if (meta > 1) {
            float br = 0.14509805F;
            float bg = 0.6156863F;
            float bb = 0.45882353F;
            float fr = br;
            float fg = bg;
            float fb = bb;
            if (pod.aspect != null) {
                Color color = new Color(aspect.getColor());
                float ar = color.getRed() / 255.0F;
                float ag = color.getGreen() / 255.0F;
                float ab = color.getBlue() / 255.0F;
                if (meta == 7) {
                    fr = ar;
                    fg = ag;
                    fb = ab;
                } else {
                    float m = meta - 2.0F;
                    fr = (br + ar * m) / (m + 1.0F);
                    fg = (bg + ag * m) / (m + 1.0F);
                    fb = (bb + ab * m) / (m + 1.0F);
                }
            }

            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.translate(x + 0.5D, y + 0.75D, z + 0.5D);
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);

            float prevX = OpenGlHelper.lastBrightnessX;
            float prevY = OpenGlHelper.lastBrightnessY;
            int k0 = bright % 65536;
            int l0 = bright / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, k0, l0);

            if (meta > 2) {
                EntityPlayerSP player = Minecraft.getMinecraft().player;
                float ticks = (player == null ? TileRenderHelper.ticks(pod, partialTicks) : player.ticksExisted + partialTicks)
                        + (pod.hashCode() % 100);
                float pulse = MathHelper.sin(ticks / 8.0F) * 0.1F + 0.9F;
                int glow = meta * 10 + (int) (150.0F * pulse);
                int k = glow % 65536;
                int l = glow / 65536;
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, k, l);

                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0D, 0.1D, 0.0D);
                float scale = 0.125F * meta * pulse;
                GlStateManager.scale(scale, scale, scale);
                bindTexture(POD0);
                model.pod0.render(MODEL_SCALE);
                GlStateManager.popMatrix();
            }

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, k0, l0);
            float outerScale = 0.15F * meta;
            GlStateManager.scale(outerScale, outerScale, outerScale);
            GlStateManager.color(fr, fg, fb, 0.9F);
            bindTexture(POD2);
            model.pod2.render(MODEL_SCALE);

            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevX, prevY);
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }
}
