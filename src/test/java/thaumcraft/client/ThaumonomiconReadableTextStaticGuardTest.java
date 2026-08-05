package thaumcraft.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ThaumonomiconReadableTextStaticGuardTest {

    @Test
    public void thaumonomiconAspectAmountsShouldUseNativeScaleForUnicodeGlyphs() throws IOException {
        String utils = read("src/main/java/thaumcraft/client/lib/UtilsFX.java");
        String recipe = read("src/main/java/thaumcraft/client/gui/GuiResearchRecipe.java");

        assertTrue("Aspect tag rendering must expose an opt-in readable amount path",
                utils.contains("boolean bw, boolean readableAmount)"));
        assertTrue("Readable aspect amounts must use native scale for Unicode renderers",
                utils.contains("readableAmount && mc.fontRenderer.getUnicodeFlag() ? 1.0F : 0.5F"));
        assertTrue("Resized amounts must remain anchored to the icon's lower-right corner",
                utils.contains("(x + 16.0D) / textScale")
                        && utils.contains("(y + 16.0D) / textScale"));
        assertTrue("Thaumonomicon recipe aspects must opt into readable amount rendering",
                recipe.contains("771, 1.0F, false, true"));
    }

    @Test
    public void browserDetailsShouldWrapAndUseNativeUnicodeScale() throws IOException {
        String browser = read("src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java");
        int tooltip = browser.indexOf("private void drawCurrentHighlightTooltip(");
        int click = browser.indexOf("protected void mouseClicked(", tooltip);
        String tooltipCode = browser.substring(tooltip, click);

        assertTrue("Browser detail scale must follow the active renderer instead of the locale name",
                tooltipCode.contains("this.fontRenderer.getUnicodeFlag() ? 1.0F : 0.5F"));
        assertTrue("Long translated summaries must use bounded wrapping",
                browser.contains("TOOLTIP_DETAIL_MAX_WIDTH = 190")
                        && browser.contains("listFormattedStringToWidth"));
        assertTrue("Tooltip backgrounds must follow the measured text cursor",
                tooltipCode.contains("cursorY + 3"));
        assertTrue("Research-cost tags must use the same readable amount path",
                tooltipCode.contains("GL11.GL_ONE_MINUS_SRC_ALPHA, alpha, false, true"));
        assertFalse("Browser details must not retain direct hardcoded half-scale draw blocks",
                tooltipCode.contains("GlStateManager.scale(0.5F"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
