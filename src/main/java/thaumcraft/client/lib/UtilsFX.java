package thaumcraft.client.lib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.config.Config;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderItem;

public class UtilsFX {
    private static final Map<String, ResourceLocation> BOUND_TEXTURES = new HashMap<String, ResourceLocation>();
    private static final DecimalFormat FORMATTER = new DecimalFormat("#######.##");
    private static final ResourceLocation PARTICLE_TEXTURE = new ResourceLocation("thaumcraft", "textures/misc/particles.png");

    public static void bindTexture(String texture) {
        ResourceLocation location = BOUND_TEXTURES.containsKey(texture) ? BOUND_TEXTURES.get(texture) : new ResourceLocation("thaumcraft", texture);
        Minecraft.getMinecraft().getTextureManager().bindTexture(location);
    }

    public static void bindTexture(ResourceLocation resource) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
    }

    public static void drawFloatyLine(double x, double y, double z, double x2, double y2, double z2,
                                      float partialTicks, int color, String texture, float speed, float distance) {
        drawFloatyLine(x, y, z, x2, y2, z2, partialTicks, color, texture, speed, distance, 0.15F);
    }

    public static void drawFloatyLine(double x, double y, double z, double x2, double y2, double z2,
                                      float partialTicks, int color, String texture, float speed, float distance, float width) {
        if (distance <= 0.0F) {
            return;
        }
        Entity renderEntity = Minecraft.getMinecraft().getRenderViewEntity();
        if (renderEntity == null) {
            renderEntity = Minecraft.getMinecraft().player;
        }
        if (renderEntity == null) {
            return;
        }

        long timeLong = (System.nanoTime() / 30000000L) % 32767L;
        float time = (float) timeLong;
        Color tint = new Color(color);
        float red = tint.getRed() / 255.0F;
        float green = tint.getGreen() / 255.0F;
        float blue = tint.getBlue() / 255.0F;
        double dx = x - x2;
        double dy = y - y2;
        double dz = z - z2;
        float dist = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        float blocks = Math.round(dist);
        float length = blocks * ((float) Config.golemLinkQuality / 2.0F);
        if (length <= 0.0F) {
            return;
        }

        double interpX = renderEntity.lastTickPosX + (renderEntity.posX - renderEntity.lastTickPosX) * partialTicks;
        double interpY = renderEntity.lastTickPosY + (renderEntity.posY - renderEntity.lastTickPosY) * partialTicks;
        double interpZ = renderEntity.lastTickPosZ + (renderEntity.posZ - renderEntity.lastTickPosZ) * partialTicks;
        GlStateManager.translate(-interpX + x2, -interpY + y2, -interpZ + z2);

        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        bindTexture(texture);
        GlStateManager.disableCull();

        drawFloatyStrip(x, y, z, x2, y2, z2, dist, length, time, red, green, blue, speed, distance, width, true);
        drawFloatyStrip(x, y, z, x2, y2, z2, dist, length, time, red, green, blue, speed, distance, width, false);

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
    }

    private static void drawFloatyStrip(double x, double y, double z, double x2, double y2, double z2,
                                        float dist, float length, float time,
                                        float red, float green, float blue,
                                        float speed, float distance, float width,
                                        boolean vertical) {
        double deltaX = x - x2;
        double deltaY = y - y2;
        double deltaZ = z - z2;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);

        for (int i = 0; i <= length * distance; ++i) {
            float beamPos = (float) i / length;
            float centerWeight = 1.0F - Math.abs((float) i - length / 2.0F) / (length / 2.0F);
            double waveX = MathHelper.sin((float) ((z % 16.0D + dist * (1.0F - beamPos) * Config.golemLinkQuality / 2.0F
                    - (time % 32767.0F / 5.0F)) / 4.0D)) * 0.5F * centerWeight;
            double waveY = MathHelper.sin((float) ((x % 16.0D + dist * (1.0F - beamPos) * Config.golemLinkQuality / 2.0F
                    - (time % 32767.0F / 5.0F)) / 3.0D)) * 0.5F * centerWeight;
            double waveZ = MathHelper.sin((float) ((y % 16.0D + dist * (1.0F - beamPos) * Config.golemLinkQuality / 2.0F
                    - (time % 32767.0F / 5.0F)) / 2.0D)) * 0.5F * centerWeight;
            double beamX = deltaX + waveX;
            double beamY = deltaY + waveY;
            double beamZ = deltaZ + waveZ;
            float texU = (1.0F - beamPos) * dist - time * speed;

            if (vertical) {
                buffer.pos(beamX * beamPos, beamY * beamPos - width, beamZ * beamPos)
                        .tex(texU, 1.0F).color(red, green, blue, centerWeight).endVertex();
                buffer.pos(beamX * beamPos, beamY * beamPos + width, beamZ * beamPos)
                        .tex(texU, 0.0F).color(red, green, blue, centerWeight).endVertex();
            } else {
                buffer.pos(beamX * beamPos - width, beamY * beamPos, beamZ * beamPos)
                        .tex(texU, 1.0F).color(red, green, blue, centerWeight).endVertex();
                buffer.pos(beamX * beamPos + width, beamY * beamPos, beamZ * beamPos)
                        .tex(texU, 0.0F).color(red, green, blue, centerWeight).endVertex();
            }
        }

        tessellator.draw();
    }

    public static void drawTexturedQuad(int x, int y, int u, int v, int width, int height, double zLevel) {
        float du = 0.00390625F;
        float dv = 0.00390625F;
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y + height, zLevel).tex((u) * du, (v + height) * dv).endVertex();
        buffer.pos(x + width, y + height, zLevel).tex((u + width) * du, (v + height) * dv).endVertex();
        buffer.pos(x + width, y, zLevel).tex((u + width) * du, v * dv).endVertex();
        buffer.pos(x, y, zLevel).tex(u * du, v * dv).endVertex();
        Tessellator.getInstance().draw();
    }

    public static void drawTexturedQuadFull(int x, int y, double zLevel) {
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x, y + 16, zLevel).tex(0.0D, 1.0D).endVertex();
        buffer.pos(x + 16, y + 16, zLevel).tex(1.0D, 1.0D).endVertex();
        buffer.pos(x + 16, y, zLevel).tex(1.0D, 0.0D).endVertex();
        buffer.pos(x, y, zLevel).tex(0.0D, 0.0D).endVertex();
        Tessellator.getInstance().draw();
    }

    public static void drawTag(int x, int y, Aspect aspect, float amount, int bonus, double z, int blend, float alpha, boolean bw) {
        drawTag((double) x, (double) y, aspect, amount, bonus, z, blend, alpha, bw);
    }

    public static void drawTag(double x, double y, Aspect aspect, float amount, int bonus, double z, int blend, float alpha, boolean bw) {
        if (aspect == null || aspect.getImage() == null) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        Color color = new Color(aspect.getColor());

        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, blend == 1 ? GlStateManager.DestFactor.ONE : GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        bindTexture(aspect.getImage());
        if (!bw) {
            GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, alpha);
        } else {
            GlStateManager.color(0.1F, 0.1F, 0.1F, alpha * 0.8F);
        }
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        float red = !bw ? color.getRed() / 255.0F : 0.1F;
        float green = !bw ? color.getGreen() / 255.0F : 0.1F;
        float blue = !bw ? color.getBlue() / 255.0F : 0.1F;
        float tagAlpha = !bw ? alpha : alpha * 0.8F;
        buffer.pos(x, y + 16.0D, z).tex(0.0D, 1.0D).color(red, green, blue, tagAlpha).endVertex();
        buffer.pos(x + 16.0D, y + 16.0D, z).tex(1.0D, 1.0D).color(red, green, blue, tagAlpha).endVertex();
        buffer.pos(x + 16.0D, y, z).tex(1.0D, 0.0D).color(red, green, blue, tagAlpha).endVertex();
        buffer.pos(x, y, z).tex(0.0D, 0.0D).color(red, green, blue, tagAlpha).endVertex();
        Tessellator.getInstance().draw();

        if (amount > 0.0F) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            String am = FORMATTER.format(amount);
            int sw = mc.fontRenderer.getStringWidth(am);
            if (blend > 1) {
                for (int a = -1; a <= 1; ++a) {
                    for (int b = -1; b <= 1; ++b) {
                        if ((a != 0 || b != 0) && (a == 0 || b == 0)) {
                            mc.fontRenderer.drawString(am, a + 32 - sw + (int) x * 2, b + 32 - mc.fontRenderer.FONT_HEIGHT + (int) y * 2, 0);
                        }
                    }
                }
            }
            mc.fontRenderer.drawString(am, 32 - sw + (int) x * 2, 32 - mc.fontRenderer.FONT_HEIGHT + (int) y * 2, 0xFFFFFF);
            GlStateManager.popMatrix();
        }

        if (bonus > 0) {
            GlStateManager.pushMatrix();
            bindTexture(PARTICLE_TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int px = 16 * ((mc.player == null ? 0 : mc.player.ticksExisted) & 15);
            drawTexturedQuad((int) x - 4, (int) y - 4, px, 80, 16, 16, z);
            if (bonus > 1) {
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
                String am = Integer.toString(bonus);
                int sw = mc.fontRenderer.getStringWidth(am) / 2;
                if (blend > 1) {
                    for (int a = -1; a <= 1; ++a) {
                        for (int b = -1; b <= 1; ++b) {
                            if ((a != 0 || b != 0) && (a == 0 || b == 0)) {
                                mc.fontRenderer.drawString(am, 8 - sw + a + (int) x * 2, 15 + b - mc.fontRenderer.FONT_HEIGHT + (int) y * 2, 0);
                            }
                        }
                    }
                }
                mc.fontRenderer.drawString(am, 8 - sw + (int) x * 2, 15 - mc.fontRenderer.FONT_HEIGHT + (int) y * 2, 0xFFFFFF);
            }
            GlStateManager.popMatrix();
        }

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    public static void renderQuadCenteredFromTexture(String texture, float scale, float red, float green, float blue, int brightness, int blend, float opacity) {
        bindTexture(texture);
        renderQuadCenteredFromTexture(scale, red, green, blue, brightness, blend, opacity);
    }

    public static void renderQuadCenteredFromTexture(float scale, float red, float green, float blue, int brightness, int blend, float opacity) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GL11.glScalef(scale, scale, scale);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                blend == 1 ? GlStateManager.DestFactor.ONE : GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, opacity);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-0.5, 0.5, 0.0).tex(0.0, 1.0).endVertex();
        buffer.pos(0.5, 0.5, 0.0).tex(1.0, 1.0).endVertex();
        buffer.pos(0.5, -0.5, 0.0).tex(1.0, 0.0).endVertex();
        buffer.pos(-0.5, -0.5, 0.0).tex(0.0, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.disableBlend();
    }

    public static void drawCustomTooltip(GuiScreen gui, RenderItem itemRenderer, FontRenderer fr, List<String> lines, int x, int y, int subTipColor) {
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableDepth();
        if (!lines.isEmpty()) {
            int maxW = 0;
            for (String line : lines) {
                int w = fr.getStringWidth(line);
                if (w > maxW) maxW = w;
            }
            int tipX = x + 12;
            int tipY = y - 12;
            int height = 8;
            if (lines.size() > 1) {
                height += 2 + (lines.size() - 1) * 10;
            }
            itemRenderer.zLevel = 300.0F;
            int bg = -267386864;
            drawGradientRect(tipX - 3, tipY - 4, tipX + maxW + 3, tipY - 3, bg, bg);
            drawGradientRect(tipX - 3, tipY + height + 3, tipX + maxW + 3, tipY + height + 4, bg, bg);
            drawGradientRect(tipX - 3, tipY - 3, tipX + maxW + 3, tipY + height + 3, bg, bg);
            drawGradientRect(tipX - 4, tipY - 3, tipX - 3, tipY + height + 3, bg, bg);
            drawGradientRect(tipX + maxW + 3, tipY - 3, tipX + maxW + 4, tipY + height + 3, bg, bg);
            int border1 = 0x505000FF;
            int border2 = (border1 & 0xFEFEFE) >> 1 | border1 & 0xFF000000;
            drawGradientRect(tipX - 3, tipY - 3 + 1, tipX - 3 + 1, tipY + height + 3 - 1, border1, border2);
            drawGradientRect(tipX + maxW + 2, tipY - 3 + 1, tipX + maxW + 3, tipY + height + 3 - 1, border1, border2);
            drawGradientRect(tipX - 3, tipY - 3, tipX + maxW + 3, tipY - 3 + 1, border1, border1);
            drawGradientRect(tipX - 3, tipY + height + 2, tipX + maxW + 3, tipY + height + 3, border2, border2);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                line = i == 0 ? "\u00a7" + Integer.toHexString(subTipColor) + line : "\u00a77" + line;
                fr.drawString(line, tipX, tipY, -1);
                if (i == 0) tipY += 2;
                tipY += 10;
            }
            itemRenderer.zLevel = 0.0F;
        }
        GlStateManager.enableDepth();
    }

    public static void drawGradientRect(int x1, int y1, int x2, int y2, int color1, int color2) {
        float a1 = (float)(color1 >> 24 & 0xFF) / 255.0F;
        float r1 = (float)(color1 >> 16 & 0xFF) / 255.0F;
        float g1 = (float)(color1 >> 8 & 0xFF) / 255.0F;
        float b1 = (float)(color1 & 0xFF) / 255.0F;
        float a2 = (float)(color2 >> 24 & 0xFF) / 255.0F;
        float r2 = (float)(color2 >> 16 & 0xFF) / 255.0F;
        float g2 = (float)(color2 >> 8 & 0xFF) / 255.0F;
        float b2 = (float)(color2 & 0xFF) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(x2, y1, 300.0).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x1, y1, 300.0).color(r1, g1, b1, a1).endVertex();
        buffer.pos(x1, y2, 300.0).color(r2, g2, b2, a2).endVertex();
        buffer.pos(x2, y2, 300.0).color(r2, g2, b2, a2).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}
