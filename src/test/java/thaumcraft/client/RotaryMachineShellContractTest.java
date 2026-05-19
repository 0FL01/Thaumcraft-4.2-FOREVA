package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class RotaryMachineShellContractTest {

    @Test
    public void centrifugeShellLivesInBlockModelWhileTesrKeepsOnlyRotaryCore() throws IOException {
        String centrifugeRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileCentrifugeRenderer.java");
        String centrifugeModel = read("src/main/java/thaumcraft/client/renderers/models/ModelCentrifuge.java");
        String centrifugeBlockModel = read("src/main/resources/assets/thaumcraft/models/block/blocktube_2.json");

        assertTrue("TileCentrifugeRenderer must keep the animated rotary core path without re-rendering the static box shell",
                centrifugeRenderer.contains("new ModelCentrifuge()")
                        && centrifugeRenderer.contains("GlStateManager.rotate(spin, 0.0F, 1.0F, 0.0F);")
                        && centrifugeRenderer.contains("model.renderSpinnyBit(MODEL_SCALE)")
                        && !centrifugeRenderer.contains("model.renderBoxes(MODEL_SCALE)"));
        assertTrue("ModelCentrifuge should still expose separate shell and rotary core parts",
                centrifugeModel.contains("crossbar")
                        && centrifugeModel.contains("dingus1")
                        && centrifugeModel.contains("dingus2")
                        && centrifugeModel.contains("core")
                        && centrifugeModel.contains("top")
                        && centrifugeModel.contains("bottom")
                        && centrifugeModel.contains("renderBoxes(float scale)")
                        && centrifugeModel.contains("renderSpinnyBit(float scale)"));
        assertTrue("Centrifuge block model should carry the top and bottom shell slabs instead of the old cube_all placeholder",
                centrifugeBlockModel.contains("\"ambientocclusion\": false")
                        && centrifugeBlockModel.contains("\"shell\": \"thaumcraft:models/centrifuge\"")
                        && centrifugeBlockModel.contains("\"from\": [4, 0, 4]")
                        && centrifugeBlockModel.contains("\"to\": [12, 4, 12]")
                        && centrifugeBlockModel.contains("\"from\": [4, 12, 4]")
                        && centrifugeBlockModel.contains("\"to\": [12, 16, 12]"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
