package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigItemsEdibleNuggetRegistryParityTest {

    @Test
    public void separateTc4ItemsCoexistWithHiddenLegacyCarrier() throws IOException {
        String items = read("src/main/java/thaumcraft/common/config/ConfigItems.java");
        String trades = read("src/main/java/thaumcraft/common/lib/world/ThaumcraftVillagerTrades.java");

        assertTrue(items.contains("legacyPath(\"ItemNuggetEdible\")")
                && items.contains("new ItemNuggetEdible()"));
        assertTrue(items.contains("legacyPath(\"ItemNuggetChicken\")")
                && items.contains("legacyPath(\"ItemNuggetBeef\")")
                && items.contains("legacyPath(\"ItemNuggetPork\")")
                && items.contains("legacyPath(\"ItemNuggetFish\")"));
        assertTrue(trades.contains("new ItemStack(ConfigItems.itemNuggetChicken)")
                && trades.contains("new ItemStack(ConfigItems.itemNuggetBeef)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
