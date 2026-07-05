package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ClientProxyRunicBaubleModelStaticGuardTest {

    @Test
    public void clientProxyKeepsRunicBaubleSubtypeModelSplit() throws IOException {
        String source = readFile("src/main/java/thaumcraft/client/ClientProxy.java");
        String ringLesser = readFile("src/main/resources/assets/thaumcraft/models/item/itemringrunic_lesser.json");
        String ringNormal = readFile("src/main/resources/assets/thaumcraft/models/item/itemringrunic.json");
        String ringCharged = readFile("src/main/resources/assets/thaumcraft/models/item/itemringrunic_charged.json");
        String ringRegen = readFile("src/main/resources/assets/thaumcraft/models/item/itemringrunic_regen.json");
        String amuletNormal = readFile("src/main/resources/assets/thaumcraft/models/item/itemamuletrunic.json");
        String amuletEmergency = readFile("src/main/resources/assets/thaumcraft/models/item/itemamuletrunic_emergency.json");
        String girdleNormal = readFile("src/main/resources/assets/thaumcraft/models/item/itemgirdlerunic.json");
        String girdleKinetic = readFile("src/main/resources/assets/thaumcraft/models/item/itemgirdlerunic_kinetic.json");

        assertTrue("ClientProxy should map runic ring metas to original per-subtype models",
                source.contains("if (item == ConfigItems.itemRingRunic)")
                        && source.contains("new ResourceLocation(\"thaumcraft\", \"itemringrunic_lesser\")")
                        && source.contains("new ResourceLocation(\"thaumcraft\", \"itemringrunic\")")
                        && source.contains("new ResourceLocation(\"thaumcraft\", \"itemringrunic_charged\")")
                        && source.contains("new ResourceLocation(\"thaumcraft\", \"itemringrunic_regen\")")
                        && source.contains("ModelLoader.setCustomModelResourceLocation(item, 0, lesserModel)")
                        && source.contains("ModelLoader.setCustomModelResourceLocation(item, 1, normalModel)")
                        && source.contains("ModelLoader.setCustomModelResourceLocation(item, 2, chargedModel)")
                        && source.contains("ModelLoader.setCustomModelResourceLocation(item, 3, regenModel)"));
        assertTrue("ClientProxy should map runic amulet metas to original per-subtype models",
                source.contains("if (item == ConfigItems.itemAmuletRunic)")
                        && source.contains("new ResourceLocation(\"thaumcraft\", \"itemamuletrunic\")")
                        && source.contains("new ResourceLocation(\"thaumcraft\", \"itemamuletrunic_emergency\")")
                        && source.contains("ModelLoader.setCustomModelResourceLocation(item, 1, emergencyModel)"));
        assertTrue("ClientProxy should map runic girdle metas to original per-subtype models",
                source.contains("if (item == ConfigItems.itemGirdleRunic)")
                        && source.contains("new ResourceLocation(\"thaumcraft\", \"itemgirdlerunic\")")
                        && source.contains("new ResourceLocation(\"thaumcraft\", \"itemgirdlerunic_kinetic\")")
                        && source.contains("ModelLoader.setCustomModelResourceLocation(item, 1, kineticModel)"));
        assertTrue("Runic ring models should reference their original textures",
                ringLesser.contains("\"layer0\": \"thaumcraft:items/runic_ring_lesser\"")
                        && ringNormal.contains("\"layer0\": \"thaumcraft:items/runic_ring\"")
                        && ringCharged.contains("\"layer0\": \"thaumcraft:items/runic_ring_charged\"")
                        && ringRegen.contains("\"layer0\": \"thaumcraft:items/runic_ring_regen\""));
        assertTrue("Runic amulet models should reference their original textures",
                amuletNormal.contains("\"layer0\": \"thaumcraft:items/runic_amulet\"")
                        && amuletEmergency.contains("\"layer0\": \"thaumcraft:items/runic_amulet_emergency\""));
        assertTrue("Runic girdle models should reference their original textures",
                girdleNormal.contains("\"layer0\": \"thaumcraft:items/runic_girdle\"")
                        && girdleKinetic.contains("\"layer0\": \"thaumcraft:items/runic_girdle_kinetic\""));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
