package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
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

    @Test
    public void arcaneLampInventoryModelsKeepTc4CuboidTexturesAndUvs() throws IOException {
        assertLampInventoryModel(
                read("src/main/resources/assets/thaumcraft/models/item/blockmetaldevice_7_inventory.json"),
                "thaumcraft:blocks/lamp_top", "thaumcraft:blocks/lamp_side");
        assertLampInventoryModel(
                read("src/main/resources/assets/thaumcraft/models/item/blockmetaldevice_8_inventory.json"),
                "thaumcraft:blocks/lamp_grow_top", "thaumcraft:blocks/lamp_grow_side");
        assertLampInventoryModel(
                read("src/main/resources/assets/thaumcraft/models/item/blockmetaldevice_13_inventory.json"),
                "thaumcraft:blocks/lamp_fert_top", "thaumcraft:blocks/lamp_fert_side");

        String registry = read("src/main/java/thaumcraft/client/ClientModelRegistry.java");
        assertTrue("Dynamic lamp OFF textures must be explicitly stitched for TESR world rendering",
                registry.contains("new ResourceLocation(\"thaumcraft\", \"blocks/lamp_grow_top_off\")")
                        && registry.contains("new ResourceLocation(\"thaumcraft\", \"blocks/lamp_grow_side_off\")")
                        && registry.contains("new ResourceLocation(\"thaumcraft\", \"blocks/lamp_fert_top_off\")")
                        && registry.contains("new ResourceLocation(\"thaumcraft\", \"blocks/lamp_fert_side_off\")"));
    }

    private static void assertLampInventoryModel(String model, String top, String side) {
        assertTrue("Lamp inventory model must preserve the exact TC4 item cuboid and animated ON textures",
                model.contains("\"parent\": \"block/block\"")
                        && model.contains("\"ambientocclusion\": false")
                        && model.contains("\"top\": \"" + top + "\"")
                        && model.contains("\"side\": \"" + side + "\"")
                        && model.contains("\"from\": [4, 2, 4]")
                        && model.contains("\"to\": [12, 14, 12]")
                        && countOccurrences(model, "\"uv\": [4, 12, 12, 4]") == 1
                        && countOccurrences(model, "\"uv\": [4, 4, 12, 12]") == 1
                        && countOccurrences(model, "\"uv\": [4, 2, 12, 14]") == 4
                        && !model.contains("_off"));
        assertEquals("Lamp inventory model must contain exactly one cuboid", 1,
                countOccurrences(model, "\"from\""));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    private static int countOccurrences(String source, String needle) {
        int count = 0;
        int index = 0;
        while ((index = source.indexOf(needle, index)) >= 0) {
            count++;
            index += needle.length();
        }
        return count;
    }
}
