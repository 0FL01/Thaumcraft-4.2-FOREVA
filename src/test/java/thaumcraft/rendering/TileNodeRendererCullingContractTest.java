package thaumcraft.rendering;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TileNodeRendererCullingContractTest {
    @Test
    public void faintTraceShouldKeepTc4StateContractWithoutDisablingCull() throws IOException {
        String faint = faintPass();
        assertFalse("TC4 faint node pass keeps culling enabled", faint.contains("disableCull"));
        assertInOrder(faint,
                "GlStateManager.alphaFunc(GL11.GL_GREATER, 1.0F / 255.0F);",
                "GlStateManager.enableBlend();",
                "GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);",
                "GlStateManager.depthMask(false);",
                "if (depthIgnore) GlStateManager.disableDepth();",
                "renderFacingStrip(renderX, renderY, renderZ, 0.0F, 0.5F, 0.1F, FRAMES, 1, frame, 0xFFFFFF);",
                "if (depthIgnore) GlStateManager.enableDepth();",
                "GlStateManager.depthMask(true);",
                "GlStateManager.disableBlend();",
                "GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);",
                "GlStateManager.popMatrix();",
                "restoreNodeLighting(lightState);");
    }

    @Test
    public void facingStripShouldPresentFrontFaceToCamera() throws IOException {
        String strip = facingStrip();
        assertFalse(strip.contains("orientBillboardToPlayer"));
        assertInOrder(strip,
                "GlStateManager.rotate(-rm.playerViewY, 0.0F, 1.0F, 0.0F);",
                "thirdPersonView == 2 ? -1 : 1) * rm.playerViewX",
                "GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);",
                "GlStateManager.rotate((float) Math.toDegrees(angle), 0.0F, 0.0F, 1.0F);",
                "TileRenderHelper.drawTexturedQuad(scale, argb, u0, u1, v0, v1);");
    }

    private static String source() throws IOException {
        return new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/client/renderers/tile/TileNodeRenderer.java")), StandardCharsets.UTF_8);
    }

    private static String faintPass() throws IOException {
        String source = source();
        int end = source.indexOf("private static boolean isHoldingThaumometer(");
        int start = source.lastIndexOf("NodeLightState lightState = enableNodeLighting();", end);
        assertTrue("Missing isolated faint node pass", start >= 0 && end > start);
        return source.substring(start, end);
    }

    private static String facingStrip() throws IOException {
        String source = source();
        int start = source.indexOf("private static void renderFacingStrip(");
        int end = source.indexOf("private static float viewerTicks(", start);
        assertTrue("Missing renderFacingStrip", start >= 0 && end > start);
        return source.substring(start, end);
    }

    private static void assertInOrder(String source, String... markers) {
        int index = -1;
        for (String marker : markers) {
            int next = source.indexOf(marker, index + 1);
            assertTrue("Missing or out-of-order source marker: " + marker, next > index);
            index = next;
        }
    }
}
