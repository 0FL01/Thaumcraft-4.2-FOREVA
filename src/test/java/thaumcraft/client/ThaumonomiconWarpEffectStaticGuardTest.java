package thaumcraft.client;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ThaumonomiconWarpEffectStaticGuardTest {

    private static final Path NODE_ATLAS = Paths.get(
            "src/main/resources/assets/thaumcraft/textures/misc/nodes.png");

    @Test
    public void forbiddenResearchShouldUseTheOriginalAnimatedAuraStrip() throws Exception {
        String browser = new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/client/gui/GuiResearchBrowser.java")), StandardCharsets.UTF_8);
        int start = browser.indexOf("private void drawForbidden(");
        int end = browser.indexOf("private void drawQuad(", start);
        String renderer = browser.substring(start, end);

        assertTrue("Warp aura must use the atlas' 32-frame row coordinates",
                renderer.contains("float v0 = 5.0F / frames;")
                        && renderer.contains("float v1 = 6.0F / frames;"));
        assertTrue("Warp aura must retain TC4's centered 80x80 geometry",
                renderer.contains("this.drawQuad(x - 40.0, y - 40.0, 80.0, 80.0"));
        assertFalse("Warp aura must not sample the transparent eighth-based region",
                renderer.contains("/ 8.0F"));
    }

    @Test
    public void everyFrameInTheForbiddenAuraStripShouldContainVisiblePixels() throws Exception {
        BufferedImage atlas = ImageIO.read(NODE_ATLAS.toFile());
        assertEquals(2048, atlas.getWidth());
        assertEquals(2048, atlas.getHeight());
        int frames = 32;
        int cell = atlas.getWidth() / frames;

        for (int frame = 0; frame < frames; ++frame) {
            boolean visible = false;
            int minX = frame * cell;
            int minY = 5 * cell;
            for (int y = minY; y < minY + cell && !visible; ++y) {
                for (int x = minX; x < minX + cell; ++x) {
                    if ((atlas.getRGB(x, y) >>> 24) != 0) {
                        visible = true;
                        break;
                    }
                }
            }
            assertTrue("Forbidden aura frame " + frame + " is fully transparent", visible);
        }
    }
}
