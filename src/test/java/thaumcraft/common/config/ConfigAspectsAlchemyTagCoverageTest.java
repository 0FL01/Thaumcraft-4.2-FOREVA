package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigAspectsAlchemyTagCoverageTest {

    @Test
    public void configAspectsContainsThaumcraftAlchemyTagBaseline() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/config/ConfigAspects.java");

        assertTrue("ConfigAspects init must call thaumcraft alchemy baseline tags",
                source.contains("registerThaumcraftAlchemyBaseline();"));
        assertTrue("Missing shard aspect baseline", source.contains("new ItemStack(ConfigItems.itemShard, 1, 0)"));
        assertTrue("Missing balanced shard component baseline", source.contains("new ItemStack(ConfigItems.itemResource, 1, 14)"));
        assertTrue("Missing native cluster iron baseline", source.contains("new ItemStack(ConfigItems.itemNugget, 1, 16)"));
        assertTrue("Missing native cluster gold baseline", source.contains("new ItemStack(ConfigItems.itemNugget, 1, 31)"));
        assertTrue("Missing custom ore cinnabar baseline", source.contains("new ItemStack(ConfigBlocks.blockCustomOre, 1, 0)"));
        assertTrue("Missing custom ore infused baseline", source.contains("new ItemStack(ConfigBlocks.blockCustomOre, 1, 7)"));
        assertTrue("Missing blockMetalDevice baseline", source.contains("new ItemStack(ConfigBlocks.blockMetalDevice)"));
        assertTrue("Missing custom plant alchemy baseline", source.contains("new ItemStack(ConfigBlocks.blockCustomPlant, 1, 2)"));
        assertTrue("Missing essentia baseline", source.contains("new ItemStack(ConfigItems.itemEssence, 1, 0)"));
        assertTrue("Missing zombie brain aspect baseline", source.contains("new ItemStack(ConfigItems.itemZombieBrain)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
