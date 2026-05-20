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
        String blockModel = read("src/main/resources/assets/thaumcraft/models/block/blockmetaldevice_0.json");

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
        assertTrue("Crucible block model should now carry a shaped basin shell instead of the old full cube placeholder",
                blockModel.contains("\"ambientocclusion\": false")
                        && blockModel.contains("\"particle\": \"thaumcraft:blocks/crucible3\"")
                        && blockModel.contains("\"inner\": \"thaumcraft:blocks/crucible1\"")
                        && blockModel.contains("\"bottom\": \"thaumcraft:blocks/crucible2\"")
                        && blockModel.contains("\"from\": [1, 0, 1]")
                        && blockModel.contains("\"to\": [15, 2, 15]")
                        && blockModel.contains("\"from\": [1, 2, 1]")
                        && blockModel.contains("\"to\": [15, 11, 3]")
                        && !blockModel.contains("\"parent\": \"block/cube_all\""));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
