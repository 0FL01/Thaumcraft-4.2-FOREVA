package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class CrucibleRendererFidelityStaticGuardTest {

    @Test
    public void tileCrucibleRendererUsesWaterAtlasUvAndCombinedLight() throws IOException {
        String source = read("src/main/java/thaumcraft/client/renderers/tile/TileCrucibleRenderer.java");

        assertTrue("Crucible renderer should resolve water atlas sprite for the surface quad",
                source.contains("Blocks.WATER.getDefaultState()"));
        assertTrue("Crucible renderer should use world combined light at tile position",
                source.contains("getCombinedLight(tile.getPos(), 0)"));
        assertTrue("Crucible renderer should wire combined light into lightmap coordinates",
                source.contains("OpenGlHelper.setLightmapTextureCoords"));
        assertTrue("Crucible renderer should map quad UVs from water sprite bounds",
                source.contains("water.getMinU()")
                        && source.contains("water.getMaxU()")
                        && source.contains("water.getMinV()")
                        && source.contains("water.getMaxV()"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
