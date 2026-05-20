package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileArcaneLampRendererFidelityStaticGuardTest {

    @Test
    public void arcaneLampRendererUsesModelDrivenShellAndNozzlePaths() throws IOException {
        String source = read("src/main/java/thaumcraft/client/renderers/tile/TileArcaneLampRenderer.java");

        assertTrue("Arcane lamp renderer should render the lamp shell from block-atlas sprites and keep the ModelBoreBase nozzle path",
                source.contains("new ModelBoreBase()")
                        && source.contains("renderLampShell(tile, x, y, z);")
                        && source.contains("bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);")
                        && source.contains("TileRenderHelper.drawTexturedCuboid(")
                        && source.contains("\"thaumcraft:blocks/lamp_grow_top_off\"")
                        && source.contains("\"thaumcraft:blocks/lamp_fert_top_off\"")
                        && source.contains("model.renderNozzle(MODEL_SCALE)")
                        && source.contains("TileArcaneBoreBase")
                        && source.contains("facing.getOpposite()")
                        && source.contains("orientNozzleByFace(")
                        && source.contains("TileArcaneLampGrowth")
                        && source.contains("TileArcaneLampFertility"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
