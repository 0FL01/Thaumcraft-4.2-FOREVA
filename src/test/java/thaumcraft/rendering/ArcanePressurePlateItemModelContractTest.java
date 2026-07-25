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

        JsonObject gui = model.getAsJsonObject("display").getAsJsonObject("gui");
        assertEquals("[30,225,0]", gui.getAsJsonArray("rotation").toString());
        assertEquals("[0.8125,0.8125,0.8125]", gui.getAsJsonArray("scale").toString());

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

    private static JsonObject parse(Path path) throws IOException {
        return new JsonParser().parse(read(path)).getAsJsonObject();
    }

    private static String read(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }
}
