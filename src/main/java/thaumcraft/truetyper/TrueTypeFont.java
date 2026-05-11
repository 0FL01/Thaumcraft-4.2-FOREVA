package truetyper;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class TrueTypeFont {
    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_RIGHT = 1;
    public static final int ALIGN_CENTER = 2;
    private FloatObject[] charArray = new FloatObject[256];
    private Map customChars = new HashMap();
    protected boolean antiAlias;
    private float fontSize = 0.0f;
    private float fontHeight = 0.0f;
    private int fontTextureID;
    private int textureWidth = 1024;
    private int textureHeight = 1024;
    protected Font font;
    private FontMetrics fontMetrics;
    private int correctL = 9;
    private int correctR = 8;

    public TrueTypeFont(Font font, boolean antiAlias, char[] additionalChars) {
        this.font = font;
        this.fontSize = font.getSize() + 3;
        this.antiAlias = antiAlias;
        this.createSet(additionalChars);
        System.out.println("TrueTypeFont loaded: " + font + " - AntiAlias = " + antiAlias);
        this.fontHeight -= 1.0f;
        if (this.fontHeight <= 0.0f) {
            this.fontHeight = 1.0f;
        }
    }

    public TrueTypeFont(Font font, boolean antiAlias) {
        this(font, antiAlias, null);
    }

    public void setCorrection(boolean on) {
        if (on) {
            this.correctL = 2;
            this.correctR = 1;
        } else {
            this.correctL = 0;
            this.correctR = 0;
        }
    }

    private BufferedImage getFontImage(char ch) {
        float charheight;
        BufferedImage tempfontImage = new BufferedImage(1, 1, 2);
        Graphics2D g = (Graphics2D)tempfontImage.getGraphics();
        if (this.antiAlias) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        g.setFont(this.font);
        this.fontMetrics = g.getFontMetrics();
        float charwidth = this.fontMetrics.charWidth(ch) + 8;
        if (charwidth <= 0.0f) {
            charwidth = 7.0f;
        }
        if ((charheight = (float)(this.fontMetrics.getHeight() + 3)) <= 0.0f) {
            charheight = this.fontSize;
        }
        BufferedImage fontImage = new BufferedImage((int)charwidth, (int)charheight, 2);
        Graphics2D gt = (Graphics2D)fontImage.getGraphics();
        if (this.antiAlias) {
            gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        gt.setFont(this.font);
        gt.setColor(Color.WHITE);
        int charx = 3;
        int chary = 1;
        gt.drawString(String.valueOf(ch), charx, chary + this.fontMetrics.getAscent());
        return fontImage;
    }

    private void createSet(char[] customCharsArray) {
        if (customCharsArray != null && customCharsArray.length > 0) {
            this.textureWidth *= 2;
        }
        try {
            BufferedImage imgTemp = new BufferedImage(this.textureWidth, this.textureHeight, 2);
            Graphics2D g = (Graphics2D)imgTemp.getGraphics();
            g.setColor(new Color(0, 0, 0, 1));
            g.fillRect(0, 0, this.textureWidth, this.textureHeight);
            float rowHeight = 0.0f;
            float positionX = 0.0f;
            float positionY = 0.0f;
            int customCharsLength = customCharsArray != null ? customCharsArray.length : 0;
            for (int i = 0; i < 256 + customCharsLength; ++i) {
                char ch = i < 256 ? (char)i : customCharsArray[i - 256];
                BufferedImage fontImage = this.getFontImage(ch);
                FloatObject newIntObject = new FloatObject();
                newIntObject.width = fontImage.getWidth();
                newIntObject.height = fontImage.getHeight();
                if (positionX + newIntObject.width >= (float)this.textureWidth) {
                    positionX = 0.0f;
                    positionY += rowHeight;
                    rowHeight = 0.0f;
                }
                newIntObject.storedX = positionX;
                newIntObject.storedY = positionY;
                if (newIntObject.height > this.fontHeight) {
                    this.fontHeight = newIntObject.height;
                }
                if (newIntObject.height > rowHeight) {
                    rowHeight = newIntObject.height;
                }
                g.drawImage((Image)fontImage, (int)positionX, (int)positionY, null);
                positionX += newIntObject.width;
                if (i < 256) {
                    this.charArray[i] = newIntObject;
                } else {
                    this.customChars.put(new Character(ch), newIntObject);
                }
                fontImage = null;
            }
            this.fontTextureID = TrueTypeFont.loadImage(imgTemp);
        }
        catch (Exception e) {
            System.err.println("Failed to create font.");
            e.printStackTrace();
        }
    }

    private void drawQuad(float drawX, float drawY, float drawX2, float drawY2, float srcX, float srcY, float srcX2, float srcY2) {
        float DrawWidth = drawX2 - drawX;
        float DrawHeight = drawY2 - drawY;
        float TextureSrcX = srcX / (float)this.textureWidth;
        float TextureSrcY = srcY / (float)this.textureHeight;
        float SrcWidth = srcX2 - srcX;
        float SrcHeight = srcY2 - srcY;
        float RenderWidth = SrcWidth / (float)this.textureWidth;
        float RenderHeight = SrcHeight / (float)this.textureHeight;
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.pos((double)drawX, (double)drawY, 0.0).tex((double)TextureSrcX, (double)TextureSrcY).endVertex();
        buffer.pos((double)drawX, (double)(drawY + DrawHeight), 0.0).tex((double)TextureSrcX, (double)(TextureSrcY + RenderHeight)).endVertex();
        buffer.pos((double)(drawX + DrawWidth), (double)(drawY + DrawHeight), 0.0).tex((double)(TextureSrcX + RenderWidth), (double)(TextureSrcY + RenderHeight)).endVertex();
        buffer.pos((double)(drawX + DrawWidth), (double)drawY, 0.0).tex((double)(TextureSrcX + RenderWidth), (double)TextureSrcY).endVertex();
    }

    public float getWidth(String whatchars) {
        float totalwidth = 0.0f;
        FloatObject floatObject = null;
        char currentChar = '\u0000';
        float lastWidth = -10.0f;
        for (int i = 0; i < whatchars.length(); ++i) {
            currentChar = whatchars.charAt(i);
            floatObject = currentChar < '\u0100' ? this.charArray[currentChar] : (FloatObject)this.customChars.get(new Character(currentChar));
            if (floatObject == null) continue;
            totalwidth += floatObject.width / 2.0f;
            lastWidth = floatObject.width;
        }
        return this.fontMetrics.stringWidth(whatchars);
    }

    public float getHeight() {
        return this.fontHeight;
    }

    public float getHeight(String HeightString) {
        return this.fontHeight;
    }

    public float getLineHeight() {
        return this.fontHeight;
    }

    public void drawString(float x, float y, String whatchars, float scaleX, float scaleY, float ... rgba) {
        if (rgba.length == 0) {
            rgba = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        }
        this.drawString(x, y, whatchars, 0, whatchars.length() - 1, scaleX, scaleY, 0, rgba);
    }

    public void drawString(float x, float y, String whatchars, float scaleX, float scaleY, int format, float ... rgba) {
        if (rgba.length == 0) {
            rgba = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        }
        this.drawString(x, y, whatchars, 0, whatchars.length() - 1, scaleX, scaleY, format, rgba);
    }

    public void drawString(float x, float y, String whatchars, int startIndex, int endIndex, float scaleX, float scaleY, int format, float ... rgba) {
        char charCurrent;
        int i = startIndex;
        int c;
        int d;
        if (rgba.length == 0) {
            rgba = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        }
        GL11.glPushMatrix();
        GL11.glScalef((float)scaleX, (float)scaleY, (float)1.0f);
        FloatObject floatObject = null;
        float totalwidth = 0.0f;
        float startY = 0.0f;
        switch (format) {
            case 1: {
                d = -1;
                c = this.correctR;
                for (i = startIndex; i < endIndex; ++i) {
                    if (whatchars.charAt(i) != '\n') continue;
                    startY -= this.fontHeight;
                }
                break;
            }
            case 2: {
                for (int l = startIndex; l <= endIndex && (charCurrent = whatchars.charAt(l)) != '\n'; ++l) {
                    floatObject = charCurrent < '\u0100' ? this.charArray[charCurrent] : (FloatObject)this.customChars.get(new Character(charCurrent));
                    totalwidth += floatObject.width - (float)this.correctL;
                }
                totalwidth /= -2.0f;
            }
            default: {
                d = 1;
                c = this.correctL;
            }
        }
        GL11.glBindTexture((int)3553, (int)this.fontTextureID);
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        if (rgba.length == 4) {
            GlStateManager.color(rgba[0], rgba[1], rgba[2], rgba[3]);
        }
        while (i >= startIndex && i <= endIndex) {
            charCurrent = whatchars.charAt(i);
            floatObject = charCurrent < '\u0100' ? this.charArray[charCurrent] : (FloatObject)this.customChars.get(new Character(charCurrent));
            if (floatObject == null) continue;
            if (d < 0) {
                totalwidth += (floatObject.width - (float)c) * (float)d;
            }
            if (charCurrent == '\n') {
                startY -= this.fontHeight * (float)d;
                totalwidth = 0.0f;
                if (format == 2) {
                    for (int l = i + 1; l <= endIndex && (charCurrent = whatchars.charAt(l)) != '\n'; ++l) {
                        floatObject = charCurrent < '\u0100' ? this.charArray[charCurrent] : (FloatObject)this.customChars.get(new Character(charCurrent));
                        totalwidth += floatObject.width - (float)this.correctL;
                    }
                    totalwidth /= -2.0f;
                }
            } else {
                this.drawQuad(totalwidth + floatObject.width + x / scaleX, startY + y / scaleY, totalwidth + x / scaleX, startY + floatObject.height + y / scaleY, floatObject.storedX + floatObject.width, floatObject.storedY + floatObject.height, floatObject.storedX, floatObject.storedY);
                if (d > 0) {
                    totalwidth += (floatObject.width - (float)c) * (float)d;
                }
            }
            i += d;
        }
        Tessellator.getInstance().draw();
        GL11.glPopMatrix();
    }

    public static int loadImage(BufferedImage bufferedImage) {
        try {
            ByteBuffer byteBuffer;
            short width = (short)bufferedImage.getWidth();
            short height = (short)bufferedImage.getHeight();
            byte bpp = (byte)bufferedImage.getColorModel().getPixelSize();
            DataBuffer db = bufferedImage.getData().getDataBuffer();
            if (db instanceof DataBufferInt) {
                int[] intI = ((DataBufferInt)bufferedImage.getData().getDataBuffer()).getData();
                byte[] newI = new byte[intI.length * 4];
                for (int i = 0; i < intI.length; ++i) {
                    byte[] b = TrueTypeFont.intToByteArray(intI[i]);
                    int newIndex = i * 4;
                    newI[newIndex] = b[1];
                    newI[newIndex + 1] = b[2];
                    newI[newIndex + 2] = b[3];
                    newI[newIndex + 3] = b[0];
                }
                byteBuffer = ByteBuffer.allocateDirect(width * height * (bpp / 8)).order(ByteOrder.nativeOrder()).put(newI);
            } else {
                byteBuffer = ByteBuffer.allocateDirect(width * height * (bpp / 8)).order(ByteOrder.nativeOrder()).put(((DataBufferByte)bufferedImage.getData().getDataBuffer()).getData());
            }
            byteBuffer.flip();
            int internalFormat = 32856;
            int format = 6408;
            IntBuffer textureId = BufferUtils.createIntBuffer((int)1);
            GL11.glGenTextures((IntBuffer)textureId);
            GL11.glBindTexture((int)3553, (int)textureId.get(0));
            GL11.glTexParameteri((int)3553, (int)10242, (int)10496);
            GL11.glTexParameteri((int)3553, (int)10243, (int)10496);
            GL11.glTexParameteri((int)3553, (int)10240, (int)9728);
            GL11.glTexParameteri((int)3553, (int)10241, (int)9728);
            GL11.glTexEnvf((int)8960, (int)8704, (float)8448.0f);
            GLU.gluBuild2DMipmaps((int)3553, (int)internalFormat, (int)width, (int)height, (int)format, (int)5121, (ByteBuffer)byteBuffer);
            return textureId.get(0);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
            return -1;
        }
    }

    public static boolean isSupported(String fontname) {
        Font[] font = TrueTypeFont.getFonts();
        for (int i = font.length - 1; i >= 0; --i) {
            if (!font[i].getName().equalsIgnoreCase(fontname)) continue;
            return true;
        }
        return false;
    }

    public static Font[] getFonts() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    }

    public static byte[] intToByteArray(int value) {
        return new byte[]{(byte)(value >>> 24), (byte)(value >>> 16), (byte)(value >>> 8), (byte)value};
    }

    public void destroy() {
        IntBuffer scratch = BufferUtils.createIntBuffer((int)1);
        scratch.put(0, this.fontTextureID);
        GL11.glBindTexture((int)3553, (int)0);
        GL11.glDeleteTextures((IntBuffer)scratch);
    }

    private class FloatObject {
        public float width;
        public float height;
        public float storedX;
        public float storedY;

        private FloatObject() {
        }
    }
}

