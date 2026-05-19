package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TubeConduitBurstContractTest {

    @Test
    public void stage8cTubeConduitBurstKeepsDynamicArmsAndCoreModels() throws IOException {
        String helper = read("src/main/java/thaumcraft/client/renderers/tile/TubeConduitRenderHelper.java");
        String tubeRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileTubeRenderer.java");
        String filterRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileTubeFilterRenderer.java");
        String restrictRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileTubeRestrictRenderer.java");
        String valveRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileTubeValveRenderer.java");
        String bufferRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileTubeBufferRenderer.java");
        String onewayRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileTubeOnewayRenderer.java");
        String tubeModel = read("src/main/resources/assets/thaumcraft/models/block/blocktube_0.json");
        String filterModel = read("src/main/resources/assets/thaumcraft/models/block/blocktube_3.json");
        String bufferModel = read("src/main/resources/assets/thaumcraft/models/block/blocktube_4.json");
        String onewayModel = read("src/main/resources/assets/thaumcraft/models/block/blocktube_6.json");

        assertTrue("Tube conduit helper must drive dynamic connections through ThaumcraftApiHelper and extended-tube checks",
                helper.contains("ThaumcraftApiHelper.getConnectableTile")
                        && helper.contains("renderExtendedTube()")
                        && helper.contains("pipe_filter_core"));
        assertTrue("TileTube/Filter/Restrict renderers must delegate conduit drawing through the shared helper",
                tubeRenderer.contains("TubeConduitRenderHelper.renderConduit")
                        && filterRenderer.contains("tile.aspectFilter")
                        && restrictRenderer.contains("\"thaumcraft:blocks/pipe_restrict\""));
        assertTrue("Valve, buffer, and oneway renderers must render conduit arms before their dedicated overlays",
                valveRenderer.contains("TubeConduitRenderHelper.renderConduit(tile, tile, tile.openSides")
                        && bufferRenderer.contains("TubeConduitRenderHelper.renderConduit(tile, tile, tile.openSides")
                        && onewayRenderer.contains("TubeConduitRenderHelper.renderConduit(tile, tile, tile.openSides"));
        assertTrue("Base tube model must use a narrow stem plus pipe_2 joint instead of the old cube_all placeholder",
                tubeModel.contains("\"joint\": \"thaumcraft:blocks/pipe_2\"")
                        && tubeModel.contains("\"from\": [7, 0, 7]")
                        && tubeModel.contains("\"from\": [6.5, 6.5, 6.5]"));
        assertTrue("Filter/buffer/oneway models must preserve their dedicated core textures while dropping the full-cube placeholder shape",
                filterModel.contains("\"core\": \"thaumcraft:blocks/pipe_filter_core\"")
                        && bufferModel.contains("\"from\": [4, 4, 4]")
                        && onewayModel.contains("\"core\": \"thaumcraft:blocks/pipe_oneway\""));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
