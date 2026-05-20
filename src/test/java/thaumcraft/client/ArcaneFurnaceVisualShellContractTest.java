package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ArcaneFurnaceVisualShellContractTest {

    @Test
    public void arcaneFurnaceShouldKeepNozzleFacingVariantsAndNonCubeShellModel() throws IOException {
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String blockstate = read("src/main/resources/assets/thaumcraft/blockstates/blockarcanefurnace.json");
        String nozzleModel = read("src/main/resources/assets/thaumcraft/models/block/blockarcanefurnace_10.json");

        assertTrue("Arcane Furnace item metadata should resolve through the new facing-aware blockstate variant keys",
                clientProxy.contains("registerBlockItemModel(arcaneFurnaceItem, meta, \"type=\" + meta + \",facing=north\");"));

        assertTrue("Arcane Furnace blockstate should provide every facing/type permutation in Forge's canonical key order and keep explicit nozzle rotations for meta 10",
                blockstate.contains("\"facing=north,type=0\"")
                        && blockstate.contains("\"facing=east,type=0\"")
                        && blockstate.contains("\"facing=west,type=8\": {\n      \"model\": \"thaumcraft:blockarcanefurnace_7\"")
                        && blockstate.contains("\"facing=north,type=10\"")
                        && blockstate.contains("\"facing=east,type=10\"")
                        && blockstate.contains("\"facing=south,type=10\"")
                        && blockstate.contains("\"facing=west,type=10\"")
                        && blockstate.contains("\"y\": 90")
                        && blockstate.contains("\"y\": 180")
                        && blockstate.contains("\"y\": 270"));

        assertTrue("Arcane Furnace nozzle model should no longer be a cube_all placeholder and must describe a half-block shell plus inner throat",
                nozzleModel.contains("\"ambientocclusion\": false")
                        && nozzleModel.contains("\"front\": \"thaumcraft:blocks/furnace13\"")
                        && nozzleModel.contains("\"top\": \"thaumcraft:blocks/furnace15\"")
                        && nozzleModel.contains("\"inner\": \"thaumcraft:blocks/furnace9\"")
                        && nozzleModel.contains("\"from\": [0, 0, 0]")
                        && nozzleModel.contains("\"to\": [16, 16, 8]")
                        && nozzleModel.contains("\"from\": [4, 4, 8]")
                        && nozzleModel.contains("\"to\": [12, 12, 12]")
                        && !nozzleModel.contains("\"parent\": \"block/cube_all\""));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
