package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EldritchNothingRendererFidelityStaticGuardTest {

    @Test
    public void eldritchNothingRendererKeepsPerFaceOffsetAndLayerContracts() throws IOException {
        String source = read("src/main/java/thaumcraft/client/renderers/tile/TileEldritchNothingRenderer.java");
        String helper = read("src/main/java/thaumcraft/client/renderers/tile/LayeredFieldPlaneHelper.java");

        assertTrue("TileEldritchNothingRenderer should route through the batched layered tunnel helper",
                source.contains("LayeredFieldPlaneHelper.addInRangeBatchFace(")
                        || source.contains("LayeredFieldPlaneHelper.renderLayeredFace("));

        assertTrue("TileEldritchNothingRenderer should keep explicit per-face plane offsets",
                source.contains("case DOWN:")
                        && source.contains("case NORTH:")
                        && source.contains("case WEST:")
                        && source.contains("return FACE_MIN;")
                        && source.contains("return FACE_MAX;"));

        assertTrue("LayeredFieldPlaneHelper should keep reference texture set and live-time texture-matrix flow",
                helper.contains("textures/misc/tunnel.png")
                        && helper.contains("textures/misc/particlefield.png")
                        && helper.contains("textures/misc/particlefield32.png")
                        && helper.contains("System.currentTimeMillis() % 700000L")
                        && helper.contains("GlStateManager.matrixMode(5890)")
                        && helper.contains("GL11.glTexGeni"));

        assertTrue("LayeredFieldPlaneHelper should keep layered tunnel+particle pass with additive blend and texgen planes",
                helper.contains("for (int i = 0; i < 16; i++)")
                        && helper.contains("GlStateManager.blendFunc(770, 771)")
                        && helper.contains("GlStateManager.blendFunc(1, 1)")
                        && helper.contains("FIELD_COLOR_SEED = 31100L")
                        && helper.contains("GL11.GL_TEXTURE_GEN_S")
                        && helper.contains("ActiveRenderInfo.getRotationX()"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
