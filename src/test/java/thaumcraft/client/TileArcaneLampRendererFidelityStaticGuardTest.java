package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileArcaneLampRendererFidelityStaticGuardTest {

    @Test
    public void arcaneLampRendererUsesModelDrivenNozzlePath() throws IOException {
        String source = read("src/main/java/thaumcraft/client/renderers/tile/TileArcaneLampRenderer.java");

        assertTrue("Arcane lamp renderer should use ModelBoreBase nozzle geometry rather than quad fallback",
                source.contains("new ModelBoreBase()")
                        && source.contains("model.renderNozzle(MODEL_SCALE)")
                        && source.contains("TileArcaneBoreBase")
                        && source.contains("facing.getOpposite()")
                        && source.contains("orientNozzleByFace(")
                        && source.contains("TileArcaneLampGrowth")
                        && source.contains("TileArcaneLampFertility")
                        && !source.contains("TileRenderHelper.drawTexturedQuad("));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
