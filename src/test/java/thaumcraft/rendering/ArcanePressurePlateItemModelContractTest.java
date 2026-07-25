package thaumcraft.rendering;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ArcanePressurePlateItemModelContractTest {
    private static final String[] FACES = {"down", "up", "north", "south", "west", "east"};

    @Test
    public void originalInventoryCuboidUsesItsOwnModelRoute() throws IOException {
        JsonObject model = parse(Paths.get(
                "src/main/resources/assets/thaumcraft/models/item/blockwoodendevice_2_inventory.json"));
        assertEquals("block/thin_block", model.get("parent").getAsString());
        assertFalse(model.get("ambientocclusion").getAsBoolean());
        assertFalse("standard block GUI scale keeps the plate inside its slot", model.has("display"));

        JsonObject textures = model.getAsJsonObject("textures");
        assertEquals("thaumcraft:blocks/applate1", textures.get("particle").getAsString());
        assertEquals("thaumcraft:blocks/applate1", textures.get("texture").getAsString());

        JsonObject element = model.getAsJsonArray("elements").get(0).getAsJsonObject();
        assertEquals("[1,0,1]", element.getAsJsonArray("from").toString());
        assertEquals("[15,2,15]", element.getAsJsonArray("to").toString());
        JsonObject faces = element.getAsJsonObject("faces");
        assertEquals(6, faces.entrySet().size());
        for (String direction : FACES) {
            JsonObject face = faces.getAsJsonObject(direction);
            assertEquals("#texture", face.get("texture").getAsString());
            assertFalse(face.has("cullface"));
        }

        String proxy = read(Paths.get("src/main/java/thaumcraft/client/ClientProxy.java"));
        assertTrue(proxy.contains(
                "registerBuiltinItemModel(woodenDeviceItem, 2, \"blockwoodendevice_2_inventory\");"));
    }

    @Test
    public void worldPlateUsesDedicatedRendererAtOriginalHeights() throws IOException {
        String block = read(Paths.get("src/main/java/thaumcraft/common/blocks/BlockWoodenDevice.java"));
        assertTrue(block.contains(
                "return meta == 0 || meta == 2 || meta == 3 || meta == 4 || meta == 5 || meta == 8"));

        String proxy = read(Paths.get("src/main/java/thaumcraft/client/ClientProxy.java"));
        assertTrue(proxy.contains("ClientRegistry.bindTileEntitySpecialRenderer(TileArcanePressurePlate.class, new TileArcanePressurePlateRenderer());"));

        String renderer = read(Paths.get(
                "src/main/java/thaumcraft/client/renderers/tile/TileArcanePressurePlateRenderer.java"));
        assertTrue(renderer.contains("float height = type == 3 ? 0.5F / 16.0F : 1.0F / 16.0F;"));
        assertTrue(renderer.contains("textures/blocks/applate1.png"));
        assertTrue(renderer.contains("textures/blocks/applate2.png"));
        assertTrue(renderer.contains("textures/blocks/applate3.png"));
    }

    private static JsonObject parse(Path path) throws IOException {
        return new JsonParser().parse(read(path)).getAsJsonObject();
    }

    private static String read(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
