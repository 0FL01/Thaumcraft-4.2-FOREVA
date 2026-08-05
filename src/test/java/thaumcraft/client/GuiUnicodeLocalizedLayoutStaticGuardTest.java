package thaumcraft.client;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GuiUnicodeLocalizedLayoutStaticGuardTest {

    @Test
    public void notificationsShouldUseNativeUnicodeScaleAndMeasuredRowSpacing() throws Exception {
        String source = read("src/main/java/thaumcraft/client/lib/REHNotifyHandler.java");
        int start = source.indexOf("public void renderNotifyHUD(");
        int end = source.indexOf("public void renderAspectHUD(", start);
        String notifications = source.substring(start, end);

        assertTrue(notifications.contains("mc.fontRenderer.getUnicodeFlag() ? 1.0F : 0.5F"));
        assertTrue(notifications.contains("mc.fontRenderer.FONT_HEIGHT + 2 : 8"));
        assertTrue(notifications.contains("height - entry * rowStep"));
        assertFalse(notifications.contains("height - entry * 8"));
    }

    @Test
    public void denseGolemBlurbsShouldUseAReadableBoundedUnicodeLayout() throws Exception {
        String source = read("src/main/java/thaumcraft/client/gui/GuiGolem.java");
        assertTrue(source.contains("this.fontRenderer.getUnicodeFlag() ? 0.75F : 0.5F"));
        assertTrue(source.contains("Math.max(1, (int) (BLURB_WIDTH / blurbScale))"));
        assertTrue(source.contains("UtilsFX.drawCompactString(this.fontRenderer, text"));
        assertTrue(source.contains("UtilsFX.drawCompactCenteredString(this.fontRenderer, text"));
    }

    @Test
    public void travelingTrunkTitleShouldUseNativeBoundedUnicodeText() throws Exception {
        String source = read("src/main/java/thaumcraft/client/gui/GuiTravelingTrunk.java");
        assertTrue(source.contains("this.fontRenderer.getUnicodeFlag() ? 1.0F : 0.5F"));
        assertTrue(source.contains("this.fontRenderer.trimStringToWidth(title"));
        assertTrue(source.contains("Math.max(1, (int) (availableWidth / textScale))"));
    }

    private static String read(String path) throws Exception {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
