package truetyper;

import java.nio.FloatBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import truetyper.Formatter;
import truetyper.TrueTypeFont;

public class FontHelper {
    private static String formatEscape = "\u00a7";

    public static void drawString(String s, float x, float y, TrueTypeFont font, float scaleX, float scaleY, int format, float ... rgba) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        if (mc.gameSettings.hideGUI) {
            return;
        }
        int amt = 1;
        if (sr.getScaleFactor() == 1) {
            amt = 2;
        }
        FloatBuffer matrixData = BufferUtils.createFloatBuffer((int)16);
        GL11.glGetFloat((int)2982, (FloatBuffer)matrixData);
        Matrix4f matrix = new Matrix4f();
        matrix.load(matrixData);
        FontHelper.set2DMode();
        y = (float)mc.displayHeight - y * (float)sr.getScaleFactor() - font.getLineHeight() / (float)amt;
        GlStateManager.enableBlend();
        if (s.contains(formatEscape)) {
            String[] pars = s.split(formatEscape);
            float totalOffset = 0.0f;
            for (int i = 0; i < pars.length; ++i) {
                String par = pars[i];
                float[] c = rgba;
                if (i > 0) {
                    c = Formatter.getFormatted(par.charAt(0));
                    par = par.substring(1, par.length());
                }
                font.drawString(x * (float)sr.getScaleFactor() + totalOffset, y - matrix.m31 * (float)sr.getScaleFactor(), par, scaleX / (float)amt, scaleY / (float)amt, format, c);
                totalOffset += font.getWidth(par);
            }
        } else {
            font.drawString(x * (float)sr.getScaleFactor(), y - matrix.m31 * (float)sr.getScaleFactor(), s, scaleX / (float)amt, scaleY / (float)amt, format, rgba);
        }
        GlStateManager.disableBlend();
        FontHelper.set3DMode();
    }

    private static void set2DMode() {
        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, (double)mc.displayWidth, 0.0, (double)mc.displayHeight, -1.0, 1.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
    }

    private static void set3DMode() {
        GlStateManager.matrixMode(5889);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
    }
}
