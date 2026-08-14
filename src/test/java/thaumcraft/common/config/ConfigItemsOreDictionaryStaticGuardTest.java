package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigItemsOreDictionaryStaticGuardTest {

    @Test
    public void configItemsKeepsReferenceOreDictionaryRegistrationBaseline() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/config/ConfigItems.java");
        String lifecycle = readFile("src/main/java/thaumcraft/common/Thaumcraft.java");

        assertFalse("ConfigItems.init must not register stacks before Forge registry events",
                source.contains("registerOreDictionary();"));
        int items = lifecycle.indexOf("event.getRegistry().registerAll(ConfigItems.getAllItems());");
        int itemBlocks = lifecycle.indexOf("ConfigBlocks.registerItemBlocks(event.getRegistry());", items);
        int oreDictionary = lifecycle.indexOf("ConfigItems.registerOreDictionary();", itemBlocks);
        assertTrue("Ore dictionary registration must run after items and ItemBlocks are registered",
                items >= 0 && itemBlocks > items && oreDictionary > itemBlocks);
        assertTrue("ConfigItems must keep reference ore dictionary aliases for nuggets and thaumic ingots",
                source.contains("OreDictionary.registerOre(\"nuggetCopper\", new ItemStack(itemNugget, 1, 1));")
                        && source.contains("OreDictionary.registerOre(\"nuggetTin\", new ItemStack(itemNugget, 1, 2));")
                        && source.contains("OreDictionary.registerOre(\"nuggetSilver\", new ItemStack(itemNugget, 1, 3));")
                        && source.contains("OreDictionary.registerOre(\"nuggetLead\", new ItemStack(itemNugget, 1, 4));")
                        && source.contains("OreDictionary.registerOre(\"nuggetThaumium\", new ItemStack(itemNugget, 1, 6));")
                        && source.contains("OreDictionary.registerOre(\"nuggetVoid\", new ItemStack(itemNugget, 1, 7));")
                        && source.contains("OreDictionary.registerOre(\"ingotThaumium\", new ItemStack(itemResource, 1, 2));")
                        && source.contains("OreDictionary.registerOre(\"ingotVoid\", new ItemStack(itemResource, 1, 16));"));
        assertTrue("ConfigItems must keep reference ore/shard/log/sapling ore dictionary aliases",
                source.contains("OreDictionary.registerOre(\"oreCinnabar\", new ItemStack(ConfigBlocks.blockCustomOre, 1, 0));")
                        && source.contains("OreDictionary.registerOre(\"oreAmber\", new ItemStack(ConfigBlocks.blockCustomOre, 1, 7));")
                        && source.contains("OreDictionary.registerOre(\"shardAir\", new ItemStack(itemShard, 1, 0));")
                        && source.contains("OreDictionary.registerOre(\"shardEntropy\", new ItemStack(itemShard, 1, 5));")
                        && source.contains("OreDictionary.registerOre(\"logWood\", new ItemStack(ConfigBlocks.blockMagicalLog, 1, 0));")
                        && source.contains("OreDictionary.registerOre(\"slabWood\", new ItemStack(ConfigBlocks.blockSlabWood, 1, 0));")
                        && source.contains("OreDictionary.registerOre(\"slabWood\", new ItemStack(ConfigBlocks.blockSlabWood, 1, 1));")
                        && source.contains("OreDictionary.registerOre(\"treeSapling\", new ItemStack(ConfigBlocks.blockCustomPlant, 1, 1));"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
