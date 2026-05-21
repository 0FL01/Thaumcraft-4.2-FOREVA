package thaumcraft.client.lib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.config.Config;

import java.awt.Color;
import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class REHNotifyHandler {
    private static final ResourceLocation PARTICLE_TEXTURE = new ResourceLocation("textures/particle/particles.png");
    private static final ResourceLocation THAUMONOMICON_TEXTURE = new ResourceLocation("thaumcraft", "textures/items/thaumonomicon.png");

    public void handleNotifications(Minecraft mc, long time, ScaledResolution resolution) {
        if (PlayerNotifications.getListAndUpdate(time).size() > 0) {
            renderNotifyHUD(mc, resolution.getScaledWidth_double(), resolution.getScaledHeight_double(), time);
        }
        if (PlayerNotifications.getAspectListAndUpdate(time).size() > 0) {
            renderAspectHUD(mc, resolution.getScaledWidth_double(), resolution.getScaledHeight_double(), time);
        }
    }

    public void renderNotifyHUD(Minecraft mc, double sw, double sh, long time) {
        ArrayList<PlayerNotifications.Notification> notifications = PlayerNotifications.getListAndUpdate(time);
        float shift = -8.0F;

        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();

        int width = (int) sw;
        int height = (int) sh;
        for (int entry = 0; entry < notifications.size() && entry < Config.notificationMax; ++entry) {
            PlayerNotifications.Notification notification = notifications.get(entry);
            String text = notification.text;
            int size = mc.fontRenderer.getStringWidth(text) / 2;
            int alpha = 255;
            if (entry == notifications.size() - 1 && notification.created > time) {
                alpha = 255 - (int) ((float) (notification.created - time) / (float) (Config.notificationDelay / 4) * 240.0F);
            }
            if (notification.expire < time + Config.notificationDelay) {
                alpha = (int) (255.0F - (float) (time + Config.notificationDelay - notification.expire) / (float) Config.notificationDelay * 240.0F);
                shift = -8.0F * ((float) alpha / 255.0F);
            }

            int textColor = (alpha / 2 << 24) | 0xFFFFFF;
            GlStateManager.pushMatrix();
            GlStateManager.translate(width - size - 10.0F, (float) (height - entry * 8) + shift, 0.0F);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            mc.fontRenderer.drawStringWithShadow(text, -4.0F, -8.0F, textColor);
            GlStateManager.popMatrix();

            if (notification.image != null) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(width - 9.0F, (float) (height - entry * 8) + shift - 6.0F, 0.0F);
                GlStateManager.scale(0.03125F, 0.03125F, 0.03125F);
                mc.getTextureManager().bindTexture(notification.image);
                Color color = new Color(notification.color);
                GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, alpha / 511.0F);
                UtilsFX.drawTexturedQuad(0, 0, 0, 0, 256, 256, -90.0D);
                GlStateManager.popMatrix();
            }

            if (entry == notifications.size() - 1 && notification.created > time) {
                float scale = (float) (notification.created - time) / (float) (Config.notificationDelay / 4);
                alpha = 255 - (int) (scale * 240.0F);
                GlStateManager.pushMatrix();
                GlStateManager.translate((width - 5.0F) - 8.0F * scale - (1.0F - scale) * (1.0F - scale) * (1.0F - scale) * size * 3.0F,
                        (float) (height - entry * 8) + shift - 2.0F - 8.0F * scale,
                        0.0F);
                GlStateManager.scale(scale, scale, scale);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F - alpha / 511.0F);
                mc.getTextureManager().bindTexture(PARTICLE_TEXTURE);
                int px = 16 * ((mc.player == null ? 0 : mc.player.ticksExisted) + entry * 3 & 15);
                UtilsFX.drawTexturedQuad(0, 0, px, 80, 16, 16, -90.0D - notifications.size());
                GlStateManager.popMatrix();
            }
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    public void renderAspectHUD(Minecraft mc, double sw, double sh, long time) {
        ArrayList<PlayerNotifications.AspectNotification> notifications = PlayerNotifications.getAspectListAndUpdate(time);
        float mainAlpha = 0.0F;
        int width = (int) sw;
        int height = (int) sh;

        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();

        for (PlayerNotifications.AspectNotification notification : notifications) {
            if (notification.created > time || notification.aspect == null || notification.aspect.getImage() == null) {
                continue;
            }

            int startX = (int) (sw * notification.startX);
            int startY = (int) (sh * notification.startY);
            int endX = width;
            int endY = -8;
            int bezierX = (int) (width * (0.25F + notification.startX));
            int bezierY = (int) (height * notification.startY);
            double t = (double) (time - notification.created) / (double) (notification.expire - notification.created);
            double x = (1.0D - t) * (1.0D - t) * startX + 2.0D * (1.0D - t) * t * bezierX + t * t * endX;
            double y = (1.0D - t) * (1.0D - t) * startY + 2.0D * (1.0D - t) * t * bezierY + t * t * endY;
            float alpha = 1.0F;
            if (t < 0.3D) {
                alpha = (float) (t / 0.3D);
            } else if (t > 0.66D) {
                alpha = (float) (1.0D - (t - 0.66D) / 0.34D);
            }
            mainAlpha = Math.max(mainAlpha, alpha);

            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, 0.0D);
            GlStateManager.scale(0.075F * alpha, 0.075F * alpha, 0.075F * alpha);
            mc.getTextureManager().bindTexture(notification.aspect.getImage());
            Color color = new Color(notification.aspect.getColor());
            GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, alpha * 0.66F);
            UtilsFX.drawTexturedQuad(0, 0, 0, 0, 256, 256, -90.0D);
            GlStateManager.popMatrix();
        }

        if (mainAlpha > 0.0F) {
            GlStateManager.pushMatrix();
            mc.getTextureManager().bindTexture(THAUMONOMICON_TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, mainAlpha);
            GlStateManager.translate(width - 16.0D, 0.0D, 0.0D);
            GlStateManager.scale(0.0625D, 0.0625D, 0.0625D);
            UtilsFX.drawTexturedQuad(0, 0, 0, 0, 256, 256, -90.0D);
            GlStateManager.popMatrix();
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
