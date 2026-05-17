package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigAspectsStage9aCraftedOutputTagCoverageTest {

    @Test
    public void configAspectsCoversStage9aCraftedOutputs() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/config/ConfigAspects.java");

        assertTrue("ConfigAspects should tag key Stage 9-a utility outputs",
                source.contains("new ItemStack(ConfigBlocks.blockTable)")
                        && source.contains("new ItemStack(ConfigItems.itemThaumometer)")
                        && source.contains("new ItemStack(ConfigItems.itemInkwell)")
                        && source.contains("new ItemStack(ConfigItems.itemBaubleBlanks, 1, 0)"));

        assertTrue("ConfigAspects should tag thaumium and void crafted equipment outputs",
                source.contains("new ItemStack(ConfigItems.itemHelmThaumium)")
                        && source.contains("new ItemStack(ConfigItems.itemSwordThaumium)")
                        && source.contains("new ItemStack(ConfigItems.itemHelmVoid)")
                        && source.contains("new ItemStack(ConfigItems.itemSwordVoid)"));

        assertTrue("ConfigAspects should tag crafted thaumium/tallow block outputs",
                source.contains("new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 4)")
                        && source.contains("new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 5)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
