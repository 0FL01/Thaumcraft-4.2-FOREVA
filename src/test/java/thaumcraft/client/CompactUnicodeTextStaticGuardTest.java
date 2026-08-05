package thaumcraft.client;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CompactUnicodeTextStaticGuardTest {

    @Test
    public void compactAsciiRenderingShouldAlwaysRestoreTheActiveUnicodeMode() throws Exception {
        String source = new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/client/lib/UtilsFX.java")), StandardCharsets.UTF_8);

        assertTrue("Compact text must only switch an active Unicode renderer for ASCII content",
                source.contains("renderer.getUnicodeFlag()")
                        && source.contains("text.charAt(index) > 0x7f")
                        && source.contains("renderer.setUnicodeFlag(false);"));
        assertTrue("Every compact text draw must restore Unicode mode in a finally block",
                source.contains("finally {\n            endCompactAscii(renderer, compactAscii);\n        }")
                        && source.contains("renderer.setUnicodeFlag(true);"));
        assertTrue("Legacy drawTag amounts and bonus counts must use the compact ASCII scope",
                source.contains("!readableAmount && beginCompactAscii(mc.fontRenderer, am)")
                        && source.contains("boolean compactAscii = beginCompactAscii(mc.fontRenderer, am);"));
    }

    @Test
    public void auditedGuiAndTransformedAmountPathsShouldUseTheirExplicitModes() throws Exception {
        String researchTable = read("src/main/java/thaumcraft/client/gui/GuiResearchTable.java");
        String itemTooltip = read("src/main/java/thaumcraft/client/lib/ItemAspectTooltipHandler.java");
        String jei = read("src/main/java/thaumcraft/client/integration/jei/ThaumcraftRecipeWrapper.java");
        String worldOverlay = read("src/main/java/thaumcraft/client/lib/RenderEventHandler.java");
        String thaumometer = read("src/main/java/thaumcraft/client/renderers/item/ItemThaumometerRenderer.java");
        String wandHud = read("src/main/java/thaumcraft/client/lib/REHWandHandler.java");

        assertTrue("Fixed GUI grids must opt into native readable Unicode amounts",
                researchTable.contains("faded ? 0.33f : 1.0f, false, true")
                        && itemTooltip.contains("771, 1.0f, false, true")
                        && jei.contains("771, 1.0F, false, true"));
        assertTrue("Unknown world amounts and compact wand values must use crisp ASCII helpers",
                worldOverlay.contains("UtilsFX.getCompactStringWidth(mc.fontRenderer, amountText)")
                        && worldOverlay.contains("UtilsFX.drawCompactString(mc.fontRenderer, amountText")
                        && wandHud.contains("UtilsFX.drawCompactCenteredString(mc.fontRenderer, text"));
        assertTrue("Known world and model-space tags must preserve legacy relative geometry",
                worldOverlay.contains("UtilsFX.drawTag(tagX, tagY, aspect, amount, 0, 0.0D, bright, 1.0F, false);")
                        && thaumometer.contains("aspects.getAmount(aspect), 0, 0.01D, 1, 1.0F, false);"));
    }

    private static String read(String path) throws Exception {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
