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

        assertTrue("TileEldritchNothingRenderer should keep reference texture set and live-time UV flow",
                source.contains("textures/misc/tunnel.png")
                        && source.contains("textures/misc/particlefield.png")
                        && source.contains("textures/misc/particlefield32.png")
                        && source.contains("System.currentTimeMillis() % 700000L"));

        assertTrue("TileEldritchNothingRenderer should keep explicit per-face plane offsets",
                source.contains("case DOWN:")
                        && source.contains("case NORTH:")
                        && source.contains("case WEST:")
                        && source.contains("return FACE_MIN;")
                        && source.contains("return FACE_MAX;"));

        assertTrue("TileEldritchNothingRenderer should keep 16-layer tunnel+particle pass with additive blend",
                source.contains("for (int i = 0; i < 16; i++)")
                        && source.contains("blendFunc(770, 771)")
                        && source.contains("blendFunc(1, 1)")
                        && source.contains("drawFace(face, offset")
                        && source.contains("FIELD_COLOR_SEED = 31100L")
                        && source.contains("faceParallaxSign(face)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
