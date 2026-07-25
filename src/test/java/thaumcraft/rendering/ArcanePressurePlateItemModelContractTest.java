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
    public void worldModelsUseExplicitVisiblePlateGeometry() throws IOException {
        JsonObject blockstate = parse(Paths.get(
                "src/main/resources/assets/thaumcraft/blockstates/blockwoodendevice.json"));
        JsonObject variants = blockstate.getAsJsonObject("variants");
        assertEquals("thaumcraft:blockwoodendevice_2",
                variants.getAsJsonObject("type=2").get("model").getAsString());
        assertEquals("thaumcraft:blockwoodendevice_3",
                variants.getAsJsonObject("type=3").get("model").getAsString());

        assertWorldModel("blockwoodendevice_2.json", "applate1", "[15.0,1.0,15.0]");
        assertWorldModel("blockwoodendevice_3.json", "applate2", "[15.0,0.5,15.0]");
    }

    private static void assertWorldModel(String fileName, String texture, String expectedTo) throws IOException {
        JsonObject model = parse(Paths.get(
                "src/main/resources/assets/thaumcraft/models/block/" + fileName));
        assertEquals("block/block", model.get("parent").getAsString());
        assertFalse(model.get("ambientocclusion").getAsBoolean());
        assertEquals("thaumcraft:blocks/" + texture,
                model.getAsJsonObject("textures").get("particle").getAsString());

        JsonObject element = model.getAsJsonArray("elements").get(0).getAsJsonObject();
        assertEquals("[1.0,0.0,1.0]", element.getAsJsonArray("from").toString());
        assertEquals(expectedTo, element.getAsJsonArray("to").toString());
        JsonObject faces = element.getAsJsonObject("faces");
        assertEquals(6, faces.entrySet().size());
        for (String direction : FACES) {
            JsonObject face = faces.getAsJsonObject(direction);
            assertEquals("#texture", face.get("texture").getAsString());
            if ("down".equals(direction)) {
                assertTrue("down face must have cullface:down for ground contact", face.has("cullface"));
                assertEquals("down", face.get("cullface").getAsString());
            } else {
                assertFalse("world face (except down) must not be neighbor-culled: " + direction, face.has("cullface"));
            }
        }
    }

    private static JsonObject parse(Path path) throws IOException {
        return new JsonParser().parse(read(path)).getAsJsonObject();
    }

    private static String read(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
