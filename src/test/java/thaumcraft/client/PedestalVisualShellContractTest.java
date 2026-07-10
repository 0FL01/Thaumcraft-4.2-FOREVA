package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class PedestalVisualShellContractTest {

    @Test
    public void stoneDevicePedestalFamilyShouldUseReferenceShapedBlockModels() throws IOException {
        String blockstate = read("src/main/resources/assets/thaumcraft/blockstates/blockstonedevice.json");
        String pedestalModel = read("src/main/resources/assets/thaumcraft/models/block/blockstonedevice_1.json");
        String wandPedestalModel = read("src/main/resources/assets/thaumcraft/models/block/blockstonedevice_5.json");
        String focusModel = read("src/main/resources/assets/thaumcraft/models/block/blockstonedevice_8.json");

        assertTrue("BlockStoneDevice blockstate should route pedestal, wand pedestal, and focus metas to their dedicated shell models",
                blockstate.contains("\"type=1\": { \"model\": \"thaumcraft:blockstonedevice_1\" }")
                        && blockstate.contains("\"type=5\": { \"model\": \"thaumcraft:blockstonedevice_5\" }")
                        && blockstate.contains("\"type=8\": { \"model\": \"thaumcraft:blockstonedevice_8\" }"));

        assertTrue("Pedestal shell model should replace the old full cube with the reference three-tier stone silhouette",
                pedestalModel.contains("\"ambientocclusion\": false")
                        && pedestalModel.contains("\"from\": [0, 0, 0]")
                        && pedestalModel.contains("\"to\": [16, 4, 16]")
                        && pedestalModel.contains("\"from\": [4, 4, 4]")
                        && pedestalModel.contains("\"to\": [12, 12, 12]")
                        && pedestalModel.contains("\"from\": [2, 12, 2]")
                        && pedestalModel.contains("\"to\": [14, 16, 14]"));

        assertTrue("Wand pedestal shell model should keep the stepped recharge-altar silhouette and the split pedestal/wand top textures",
                wandPedestalModel.contains("\"bottom\": \"thaumcraft:blocks/pedestal_top\"")
                        && wandPedestalModel.contains("\"top\": \"thaumcraft:blocks/wandpedestal_top\"")
                        && wandPedestalModel.contains("\"from\": [0, 0, 0]")
                        && wandPedestalModel.contains("\"to\": [16, 4, 16]")
                        && wandPedestalModel.contains("\"from\": [2, 4, 2]")
                        && wandPedestalModel.contains("\"to\": [14, 8, 14]")
                        && wandPedestalModel.contains("\"from\": [4, 8, 4]")
                        && wandPedestalModel.contains("\"to\": [12, 16, 12]"));

        assertTrue("Compound focus shell model should keep the cross-arm and raised-prong geometry instead of the old cube_bottom_top placeholder",
                focusModel.contains("\"parent\": \"block/block\"")
                        && focusModel.contains("\"bottom\": \"thaumcraft:blocks/wandpedestal_focus_bot\"")
                        && focusModel.contains("\"top\": \"thaumcraft:blocks/wandpedestal_focus_top\"")
                        && focusModel.contains("\"from\": [5, 0, 5]")
                        && focusModel.contains("\"to\": [11, 1, 11]")
                        && focusModel.contains("\"from\": [1, 1, 7]")
                        && focusModel.contains("\"to\": [3, 7, 9]")
                        && focusModel.contains("\"from\": [7, 1, 13]")
                        && focusModel.contains("\"to\": [9, 7, 15]"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
