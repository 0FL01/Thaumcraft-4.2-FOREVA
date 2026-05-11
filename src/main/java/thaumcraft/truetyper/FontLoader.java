package truetyper;

import java.awt.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import truetyper.TrueTypeFont;

public class FontLoader {
    public static TrueTypeFont loadSystemFont(String name, float defSize, boolean antialias) {
        return FontLoader.loadSystemFont(name, defSize, antialias, 0);
    }

    public static TrueTypeFont loadSystemFont(String name, float defSize, boolean antialias, int type) {
        TrueTypeFont out = null;
        try {
            Font font = new Font(name, type, (int)defSize);
            font = font.deriveFont(defSize);
            out = new TrueTypeFont(font, antialias);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    public static TrueTypeFont createFont(ResourceLocation res, float defSize, boolean antialias) {
        return FontLoader.createFont(res, defSize, antialias, 0);
    }

    public static TrueTypeFont createFont(ResourceLocation res, float defSize, boolean antialias, int type) {
        TrueTypeFont out = null;
        try {
            Font font = Font.createFont(type, Minecraft.getMinecraft().getResourceManager().getResource(res).getInputStream());
            font = font.deriveFont(defSize);
            out = new TrueTypeFont(font, antialias);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }
}

