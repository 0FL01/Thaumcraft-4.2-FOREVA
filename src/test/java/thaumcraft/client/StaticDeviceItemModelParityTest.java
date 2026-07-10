package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StaticDeviceItemModelParityTest {

    @Test
    public void crucibleItemKeepsTc4InventoryExterior() throws IOException {
        String model = read("src/main/resources/assets/thaumcraft/models/item/blockmetaldevice_0_inventory.json");

        assertTrue(model.contains("\"parent\": \"block/block\"")
                && model.contains("\"down\": \"thaumcraft:blocks/crucible2\"")
                && model.contains("\"up\": \"thaumcraft:blocks/crucible4\"")
                && model.contains("\"side\": \"thaumcraft:blocks/crucible3\"")
                && model.contains("\"from\": [0, 0, 0]")
                && model.contains("\"to\": [16, 16, 16]"));
    }

    @Test
    public void lifterItemKeepsTc4ShellGlowBoundsAndColors() throws IOException {
        String model = read("src/main/resources/assets/thaumcraft/models/item/blocklifter.json");
        String color = read("src/main/java/thaumcraft/client/renderers/item/LifterItemColor.java");

        assertTrue(model.contains("\"bottom\": \"thaumcraft:blocks/arcaneearbottom\"")
                && model.contains("\"top\": \"thaumcraft:blocks/liftertop\"")
                && model.contains("\"side\": \"thaumcraft:blocks/lifterside\"")
                && model.contains("\"glow\": \"thaumcraft:blocks/animatedglow\"")
                && model.contains("\"from\": [0.16, 14.4, 0.16]")
                && model.contains("\"to\": [15.84, 15.84, 15.84]")
                && model.contains("\"from\": [0.16, 1.6, 0.16]")
                && model.contains("\"to\": [15.84, 14.4, 15.84]"));
        assertEquals(6, occurrences(model, "\"tintindex\": 0"));
        assertEquals(6, occurrences(model, "\"tintindex\": 1"));
        assertTrue(color.contains("return BlockCustomOreItem.colors[4];")
                && color.contains("return BlockCustomOreItem.colors[5];")
                && color.contains("return -1;"));
    }

    private static int occurrences(String text, String value) {
        int count = 0;
        for (int at = 0; (at = text.indexOf(value, at)) >= 0; at += value.length()) {
            count++;
        }
        return count;
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
