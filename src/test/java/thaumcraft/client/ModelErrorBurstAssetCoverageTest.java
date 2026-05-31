package thaumcraft.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ModelErrorBurstAssetCoverageTest {

    @Test
    public void doorOpenModelsShouldTargetVanilla112DoorParents() throws IOException {
        assertTrue(read("src/main/resources/assets/thaumcraft/models/block/blockarcanedoor_top_open.json")
                .contains("\"parent\": \"minecraft:block/door_top\""));
        assertTrue(read("src/main/resources/assets/thaumcraft/models/block/blockarcanedoor_top_open_rh.json")
                .contains("\"parent\": \"minecraft:block/door_top_rh\""));
        assertTrue(read("src/main/resources/assets/thaumcraft/models/block/blockarcanedoor_bottom_open.json")
                .contains("\"parent\": \"minecraft:block/door_bottom\""));
        assertTrue(read("src/main/resources/assets/thaumcraft/models/block/blockarcanedoor_bottom_open_rh.json")
                .contains("\"parent\": \"minecraft:block/door_bottom_rh\""));
    }

    @Test
    public void fluidAndFluxBlocksShouldKeepLoadable112Blockstates() throws IOException {
        String pure = read("src/main/resources/assets/thaumcraft/blockstates/blockfluidpure.json");
        String death = read("src/main/resources/assets/thaumcraft/blockstates/blockfluiddeath.json");
        String goo = read("src/main/resources/assets/thaumcraft/blockstates/blockfluxgoo.json");
        String gas = read("src/main/resources/assets/thaumcraft/blockstates/blockfluxgas.json");

        assertTrue(pure.contains("\"model\": \"forge:fluid\"") && pure.contains("\"fluid\": \"fluidPure\"") && pure.contains("\"15\": {}"));
        assertTrue(death.contains("\"model\": \"forge:fluid\"") && death.contains("\"fluid\": \"fluidDeath\"") && death.contains("\"15\": {}"));
        assertTrue(goo.contains("\"model\": \"forge:fluid\"") && goo.contains("\"fluid\": \"fluxgoo\"") && goo.contains("\"15\": {}") && !goo.contains("\"fluxGoo\""));
        assertTrue(gas.contains("\"model\": \"forge:fluid\"") && gas.contains("\"fluid\": \"fluxgas\"") && gas.contains("\"15\": {}") && !gas.contains("\"fluxGas\""));

        String gooModel = read("src/main/resources/assets/thaumcraft/models/block/blockfluxgoo.json");
        assertTrue(gooModel.contains("\"parent\": \"minecraft:block/cube_all\"") && gooModel.contains("\"all\": \"thaumcraft:blocks/fluxgoo\""));

        String gasItem = read("src/main/resources/assets/thaumcraft/models/item/blockfluxgas.json");
        assertTrue(gasItem.contains("\"parent\": \"minecraft:block/cube_all\"") && gasItem.contains("\"all\": \"thaumcraft:blocks/fluxgas\""));
    }

    @Test
    public void arcaneFurnaceBlockstateShouldCoverEveryFacingTypePermutation() throws IOException {
        JsonObject variants = new JsonParser()
                .parse(read("src/main/resources/assets/thaumcraft/blockstates/blockarcanefurnace.json"))
                .getAsJsonObject()
                .getAsJsonObject("variants");

        String[] facings = {"north", "east", "south", "west"};
        for (int type = 0; type <= 10; type++) {
            for (String facing : facings) {
                assertTrue("Missing arcane furnace variant facing=" + facing + ",type=" + type,
                        variants.has("facing=" + facing + ",type=" + type));
            }
        }
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
