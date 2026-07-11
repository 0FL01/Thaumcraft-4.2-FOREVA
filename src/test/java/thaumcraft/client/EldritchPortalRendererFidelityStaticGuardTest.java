package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EldritchPortalRendererFidelityStaticGuardTest {

    @Test
    public void eldritchPortalRendererKeepsAtlasNearCameraAndStateContracts() throws IOException {
        String source = read("src/main/java/thaumcraft/client/renderers/tile/TileEldritchPortalRenderer.java");

        assertTrue("The portal should keep the original texture and sixteen-frame UV animation",
                source.contains("textures/misc/eldritch_portal.png")
                        && source.contains("% 16L")
                        && source.contains("frame / 16.0F")
                        && source.contains("u0 + 0.0625F"));
        assertTrue("The portal should keep the original opencount-driven opening scale",
                source.contains("Math.min(30.0F, tile.opencount + partialTicks)")
                        && source.contains("Math.min(5.0F, tile.opencount + partialTicks)")
                        && source.contains("e / 5.0F")
                        && source.contains("c / 30.0F"));
        assertTrue("A near-camera fade should prevent a portal billboard from filling the viewport",
                source.contains("nearCameraAlpha(x + 0.5D, y + 0.5D, z + 0.5D)")
                        && source.contains("NEAR_CAMERA_FADE_START")
                        && source.contains("NEAR_CAMERA_FADE_END"));
        assertTrue("The original brightness and face normal should be represented on 1.12",
                source.contains("OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 220.0F, 0.0F)")
                        && source.contains("DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL")
                        && source.contains(".normal(0.0F, 0.0F, -1.0F)"));
        assertTrue("Portal rendering should restore mutable GL state even when drawing fails",
                source.contains("boolean depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK)")
                        && source.contains("int blendSrcRgb = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB)")
                        && source.contains("try {")
                        && source.contains("} finally {")
                        && source.contains("OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, previousLightX, previousLightY)")
                        && source.contains("GlStateManager.depthMask(depthMask)")
                        && source.contains("GlStateManager.tryBlendFuncSeparate(blendSrcRgb, blendDstRgb, blendSrcAlpha, blendDstAlpha)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
