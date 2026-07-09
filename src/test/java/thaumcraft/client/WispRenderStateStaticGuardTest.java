package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class WispRenderStateStaticGuardTest {

    @Test
    public void particleEngineRestoresStandardBlendAfterAdditiveLayers() throws IOException {
        String source = readFile("src/main/java/thaumcraft/client/fx/ParticleEngine.java");

        int additiveBlend = source.indexOf(
                "GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);");
        int disableBlend = source.lastIndexOf("GlStateManager.disableBlend();");
        int restoredBlend = source.lastIndexOf(
                "GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);");
        int popMatrix = source.lastIndexOf("GlStateManager.popMatrix();");

        assertTrue("ParticleEngine must render additive TC particle layers", additiveBlend >= 0);
        assertTrue("ParticleEngine must restore standard alpha blending after its render pass",
                restoredBlend > additiveBlend && restoredBlend > disableBlend && restoredBlend < popMatrix);
    }

    @Test
    public void wispRendererRestoresBlendAndLightmapAfterFullbrightPasses() throws IOException {
        String source = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderWisp.java");

        int savedBrightnessX = source.indexOf(
                "float previousBrightnessX = OpenGlHelper.lastBrightnessX;");
        int savedBrightnessY = source.indexOf(
                "float previousBrightnessY = OpenGlHelper.lastBrightnessY;");
        int fullbrightPass = source.indexOf(
                "OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, FULL_BRIGHT, FULL_BRIGHT);");
        int renderCore = source.indexOf("renderCore(entity, red, green, blue);");
        int renderHalo = source.indexOf("renderHalo(entity, partialTicks);");
        int restoredLightmap = source.indexOf(
                "OpenGlHelper.lightmapTexUnit, previousBrightnessX, previousBrightnessY");
        int additiveBlend = source.indexOf(
                "GlStateManager.DestFactor.ONE);");
        int restoredBlend = source.indexOf(
                "GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);");

        assertTrue("RenderWisp must capture the incoming lightmap coordinates",
                savedBrightnessX >= 0 && savedBrightnessY > savedBrightnessX && savedBrightnessY < renderCore);
        assertTrue("RenderWisp must keep its additive fullbright core and halo passes",
                fullbrightPass >= 0 && additiveBlend >= 0 && renderCore >= 0 && renderHalo > renderCore);
        assertTrue("RenderWisp must restore the incoming lightmap after its fullbright passes",
                restoredLightmap > renderHalo);
        assertTrue("RenderWisp must restore standard alpha blending after its additive passes",
                restoredBlend > renderHalo && restoredBlend < restoredLightmap);
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
