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
        String blockSource = read("src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java");
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
        assertTrue("Crucible fluid quad should cover a full block like the original UtilsFX.renderQuadFromIcon scale 1.0 path",
                source.contains("TileRenderHelper.drawTexturedQuad(0.5F"));
        assertTrue("Crucible block model should use original full-block alpha shell scale, not a shrunken basin mesh",
                blockModel.contains("\"ambientocclusion\": false")
                        && blockModel.contains("\"particle\": \"thaumcraft:blocks/crucible3\"")
                        && blockModel.contains("\"top\": \"thaumcraft:blocks/crucible1\"")
                        && blockModel.contains("\"bottom\": \"thaumcraft:blocks/crucible2\"")
                        && blockModel.contains("\"outer\": \"thaumcraft:blocks/crucible3\"")
                        && blockModel.contains("\"inner\": \"thaumcraft:blocks/crucible5\"")
                        && blockModel.contains("\"inner_bottom\": \"thaumcraft:blocks/crucible6\"")
                        && blockModel.contains("\"from\": [0, 0, 0]")
                        && blockModel.contains("\"to\": [16, 16, 16]")
                        && blockModel.contains("\"from\": [2, 0, 0]")
                        && blockModel.contains("\"to\": [2, 16, 16]")
                        && blockModel.contains("\"from\": [0, 4, 0]")
                        && blockModel.contains("\"to\": [16, 4, 16]")
                        && !blockModel.contains("\"from\": [1, 0, 1]")
                        && !blockModel.contains("\"to\": [15, 11, 3]")
                        && !blockModel.contains("\"parent\": \"block/cube_all\""));
        assertTrue("Crucible alpha-mask textures require the metal device block to render in CUTOUT",
                blockSource.contains("public BlockRenderLayer getRenderLayer()")
                        && blockSource.contains("return BlockRenderLayer.CUTOUT;"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
