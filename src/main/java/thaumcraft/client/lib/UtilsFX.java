package thaumcraft.client.lib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class UtilsFX {
    private static final Map<String, ResourceLocation> BOUND_TEXTURES = new HashMap<String, ResourceLocation>();
    private static final DecimalFormat FORMATTER = new DecimalFormat("#######.##");
    private static final ResourceLocation PARTICLE_TEXTURE = new ResourceLocation("textures/particle/particles.png");

    public static void bindTexture(String texture) {
        ResourceLocation location = BOUND_TEXTURES.containsKey(texture) ? BOUND_TEXTURES.get(texture) : new ResourceLocation("thaumcraft", texture);
        Minecraft.getMinecraft().getTextureManager().bindTexture(location);
    }

    public static void bindTexture(ResourceLocation resource) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
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
}
