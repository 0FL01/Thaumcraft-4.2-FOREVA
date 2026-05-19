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
        assertTrue("Missing balanced shard aspect baseline", source.contains("new ItemStack(ConfigItems.itemShard, 1, 6)"));
        assertTrue("Missing balanced shard component derivation baseline",
                source.contains("new AspectList(new ItemStack(ConfigItems.itemShard, 1, 6)).add(Aspect.MAGIC, 2).remove(Aspect.CRYSTAL)"));
        assertTrue("Missing native cluster iron baseline", source.contains("new ItemStack(ConfigItems.itemNugget, 1, 16)"));
        assertTrue("Missing native cluster gold baseline", source.contains("new ItemStack(ConfigItems.itemNugget, 1, 31)"));
        assertTrue("Missing custom ore cinnabar baseline", source.contains("new ItemStack(ConfigBlocks.blockCustomOre, 1, 0)"));
        assertTrue("Missing custom ore infused baseline", source.contains("new ItemStack(ConfigBlocks.blockCustomOre, 1, 7)"));
        assertTrue("Missing blockMetalDevice baseline", source.contains("new ItemStack(ConfigBlocks.blockMetalDevice)"));
        assertTrue("Missing custom plant alchemy baseline", source.contains("new ItemStack(ConfigBlocks.blockCustomPlant, 1, 2)"));
        assertTrue("Missing essentia baseline", source.contains("new ItemStack(ConfigItems.itemEssence, 1, 0)"));
        assertTrue("Missing zombie brain aspect baseline", source.contains("new ItemStack(ConfigItems.itemZombieBrain)"));
        assertTrue("Missing candle/arcane furnace alchemy baseline tags",
                source.contains("new ItemStack(ConfigBlocks.blockCandle)")
                        && source.contains("new ItemStack(ConfigBlocks.blockArcaneFurnace, 1, OreDictionary.WILDCARD_VALUE)"));
        assertTrue("Missing taint fibres/magical wood alchemy baseline tags",
                source.contains("new ItemStack(ConfigBlocks.blockTaintFibres, 1, 0)")
                        && source.contains("new ItemStack(ConfigBlocks.blockMagicalLog, 1, 0)")
                        && source.contains("new ItemStack(ConfigBlocks.blockMagicalLeaves, 1, 0)"));
        assertTrue("Missing alchemy ore-dictionary dust/nugget baselines",
                source.contains("registerObjectTag(\"dustGlowstone\"")
                        && source.contains("registerObjectTag(\"nuggetIron\"")
                        && source.contains("registerObjectTag(\"oreIron\"")
                        && source.contains("registerObjectTag(\"oreGold\""));
        assertTrue("Missing thaumic essentia/thaumonomicon support baselines",
                source.contains("new ItemStack(ConfigItems.itemWispEssence, 1, 0)")
                        && source.contains("new ItemStack(ConfigItems.itemCrystalEssence, 1, 0)")
                        && source.contains("new ItemStack(ConfigItems.itemThaumonomicon, 1, OreDictionary.WILDCARD_VALUE)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
