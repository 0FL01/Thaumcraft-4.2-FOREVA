package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class SanityCheckerHudStaticGuardTest {

    @Test
    public void textOverlayShouldRenderTheOriginalSanityCheckerGauge() throws IOException {
        String source = read("src/main/java/thaumcraft/client/lib/RenderEventHandler.java");

        assertTrue(source.contains("this.renderSanityHud(mc);"));
        assertTrue(source.contains("instanceof ItemSanityChecker"));
        assertTrue(source.contains("knowledge.getTotalWarp()"));
        assertTrue(source.contains("knowledge.getWarpPerm()"));
        assertTrue(source.contains("knowledge.getWarpSticky()"));
        assertTrue(source.contains("knowledge.getWarpTemp()"));
        assertTrue(source.contains("GlStateManager.scale(0.5F, 0.5F, 1.0F);"));
        assertTrue(source.contains("UtilsFX.drawTexturedQuad(1, 1, 152, 0, 20, 76, -90.0D);"));
        assertTrue(source.contains("GlStateManager.color(1.0F, 0.5F, 1.0F, 1.0F);"));
        assertTrue(source.contains("GlStateManager.color(0.75F, 0.0F, 0.75F, 1.0F);"));
        assertTrue(source.contains("GlStateManager.color(0.5F, 0.0F, 0.5F, 1.0F);"));
        assertTrue(source.contains("UtilsFX.drawTexturedQuad(1, 1, 176, 0, 20, 76, -90.0D);"));
        assertTrue(source.contains("UtilsFX.drawTexturedQuad(1, 1, 216, 0, 20, 16, -90.0D);"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
