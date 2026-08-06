package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ClientProxyNuggetEdibleModelStaticGuardTest {

    @Test
    public void clientProxyKeepsNuggetEdibleSubtypeModelSplit() throws IOException {
        String source = readFile("src/main/java/thaumcraft/client/ClientProxy.java");
        String chickenModel = readFile("src/main/resources/assets/thaumcraft/models/item/itemnuggetedible_chicken.json");
        String beefModel = readFile("src/main/resources/assets/thaumcraft/models/item/itemnuggetedible_beef.json");
        String porkModel = readFile("src/main/resources/assets/thaumcraft/models/item/itemnuggetedible_pork.json");
        String fishModel = readFile("src/main/resources/assets/thaumcraft/models/item/itemnuggetedible_fish.json");
        String chickenItemModel = readFile("src/main/resources/assets/thaumcraft/models/item/itemnuggetchicken.json");
        String beefItemModel = readFile("src/main/resources/assets/thaumcraft/models/item/itemnuggetbeef.json");
        String porkItemModel = readFile("src/main/resources/assets/thaumcraft/models/item/itemnuggetpork.json");
        String fishItemModel = readFile("src/main/resources/assets/thaumcraft/models/item/itemnuggetfish.json");

        assertTrue("ClientProxy should map itemNuggetEdible subtype metas to dedicated models",
                source.contains("if (item == ConfigItems.itemNuggetEdible)")
                        && source.contains("new ResourceLocation(\"thaumcraft\", \"itemnuggetedible_chicken\")")
                        && source.contains("new ResourceLocation(\"thaumcraft\", \"itemnuggetedible_beef\")")
                        && source.contains("new ResourceLocation(\"thaumcraft\", \"itemnuggetedible_pork\")")
                        && source.contains("new ResourceLocation(\"thaumcraft\", \"itemnuggetedible_fish\")")
                        && source.contains("ModelLoader.setCustomModelResourceLocation(item, 0, chickenModel)")
                        && source.contains("ModelLoader.setCustomModelResourceLocation(item, 1, beefModel)")
                        && source.contains("ModelLoader.setCustomModelResourceLocation(item, 2, porkModel)")
                        && source.contains("ModelLoader.setCustomModelResourceLocation(item, 3, fishModel)"));
        assertTrue("Chicken edible nugget model should reference chicken texture",
                chickenModel.contains("\"layer0\": \"thaumcraft:items/nuggetchicken\""));
        assertTrue("Beef edible nugget model should reference beef texture",
                beefModel.contains("\"layer0\": \"thaumcraft:items/nuggetbeef\""));
        assertTrue("Pork edible nugget model should reference pork texture",
                porkModel.contains("\"layer0\": \"thaumcraft:items/nuggetpork\""));
        assertTrue("Fish edible nugget model should reference fish texture",
                fishModel.contains("\"layer0\": \"thaumcraft:items/nuggetfish\""));
        assertTrue("Separate TC4 edible nugget items should keep the same four textures",
                chickenItemModel.contains("thaumcraft:items/nuggetchicken")
                        && beefItemModel.contains("thaumcraft:items/nuggetbeef")
                        && porkItemModel.contains("thaumcraft:items/nuggetpork")
                        && fishItemModel.contains("thaumcraft:items/nuggetfish"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
