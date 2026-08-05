package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ClientProxyVisAmuletModelStaticGuardTest {

    @Test
    public void visAmuletMetasUseTheirTc4Textures() throws IOException {
        String source = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String lesserModel = read("src/main/resources/assets/thaumcraft/models/item/itemamuletvis_lesser.json");
        String normalModel = read("src/main/resources/assets/thaumcraft/models/item/itemamuletvis.json");

        assertTrue(source.contains("if (item == ConfigItems.itemAmuletVis)")
                && source.contains("new ResourceLocation(\"thaumcraft\", \"itemamuletvis_lesser\")")
                && source.contains("ModelLoader.setCustomModelResourceLocation(item, 0, lesserModel)")
                && source.contains("ModelLoader.setCustomModelResourceLocation(item, 1, normalModel)"));
        assertTrue(lesserModel.contains("\"layer0\": \"thaumcraft:items/vis_amulet_lesser\""));
        assertTrue(normalModel.contains("\"layer0\": \"thaumcraft:items/vis_amulet\""));
        assertTextureExists("vis_amulet_lesser.png");
        assertTextureExists("vis_amulet.png");
    }

    private static void assertTextureExists(String name) {
        Path texture = Paths.get("src/main/resources/assets/thaumcraft/textures/items", name);
        assertTrue(texture + " must resolve", Files.isRegularFile(texture));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
