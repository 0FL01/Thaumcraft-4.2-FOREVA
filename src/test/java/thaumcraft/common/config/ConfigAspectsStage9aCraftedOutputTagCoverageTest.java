package thaumcraft.common.config;

import org.junit.Test;
import net.minecraftforge.oredict.OreDictionary;

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
        assertTrue("ConfigAspects should preserve extended utility tags for loot/bauble and primal output helpers",
                source.contains("new ItemStack(ConfigItems.itemLootBag, 1, 0)")
                        && source.contains("new ItemStack(ConfigItems.itemLootBag, 1, 1)")
                        && source.contains("new ItemStack(ConfigItems.itemLootBag, 1, 2)")
                        && source.contains("new ItemStack(ConfigItems.itemBaubleBlanks, 1, 3)")
                        && source.contains("new ItemStack(ConfigItems.itemPrimalArrow)")
                        && source.contains("new ItemStack(ConfigItems.itemGoggles, 1, OreDictionary.WILDCARD_VALUE)")
                        && source.contains("new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 1)"));

        assertTrue("ConfigAspects should tag thaumium and void crafted equipment outputs",
                source.contains("new ItemStack(ConfigItems.itemHelmThaumium)")
                        && source.contains("new ItemStack(ConfigItems.itemSwordThaumium)")
                        && source.contains("new ItemStack(ConfigItems.itemHelmVoid)")
                        && source.contains("new ItemStack(ConfigItems.itemSwordVoid)"));

        assertTrue("ConfigAspects should tag crafted thaumium/tallow block outputs",
                source.contains("new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 4)")
                        && source.contains("new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 5)"));
        assertTrue("ConfigAspects should cover cultist/focus/eldritch progression outputs",
                source.contains("new ItemStack(ConfigItems.focusPech)")
                        && source.contains("new ItemStack(ConfigItems.itemCultistPlate, 1, OreDictionary.WILDCARD_VALUE)")
                        && source.contains("new ItemStack(ConfigItems.itemCultistRobe, 1, OreDictionary.WILDCARD_VALUE)")
                        && source.contains("new ItemStack(ConfigItems.itemCultistLeader, 1, OreDictionary.WILDCARD_VALUE)")
                        && source.contains("new ItemStack(ConfigItems.itemCultistBoots, 1, OreDictionary.WILDCARD_VALUE)")
                        && source.contains("new ItemStack(ConfigItems.itemEldritchObject, 1, 0)")
                        && source.contains("new ItemStack(ConfigItems.itemEldritchObject, 1, 3)")
                        && source.contains("new ItemStack(ConfigBlocks.blockEldritch, 1, OreDictionary.WILDCARD_VALUE)")
                        && source.contains("new ItemStack(ConfigBlocks.blockEldritchPortal)")
                        && source.contains("new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 11)")
                        && source.contains("new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 12)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
