package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigRecipesSmeltingBonusCoverageTest {

    @Test
    public void configRecipesIncludesReferenceSmeltingBonusBaseline() throws IOException {
        String source = ConfigRecipesSourceReader.readMergedSource();

        assertTrue("ConfigRecipes init must call smelting bonus baseline",
                source.contains("initializeSmeltingBonusBaseline();"));

        assertTrue("Missing oreGold smelting bonus mapping", source.contains("addSmeltingBonus(\"oreGold\""));
        assertTrue("Missing oreIron smelting bonus mapping", source.contains("addSmeltingBonus(\"oreIron\""));
        assertTrue("Missing oreCinnabar smelting bonus mapping", source.contains("addSmeltingBonus(\"oreCinnabar\""));
        assertTrue("Missing oreCopper smelting bonus mapping", source.contains("addSmeltingBonus(\"oreCopper\""));
        assertTrue("Missing oreTin smelting bonus mapping", source.contains("addSmeltingBonus(\"oreTin\""));
        assertTrue("Missing oreSilver smelting bonus mapping", source.contains("addSmeltingBonus(\"oreSilver\""));
        assertTrue("Missing oreLead smelting bonus mapping", source.contains("addSmeltingBonus(\"oreLead\""));

        assertTrue("Missing cluster-gold smelting bonus mapping", source.contains("new ItemStack(ConfigItems.itemNugget, 1, 31)"));
        assertTrue("Missing cluster-iron smelting bonus mapping", source.contains("new ItemStack(ConfigItems.itemNugget, 1, 16)"));
        assertTrue("Missing cluster-cinnabar smelting bonus mapping", source.contains("new ItemStack(ConfigItems.itemNugget, 1, 21)"));
        assertTrue("Missing cluster-copper smelting bonus mapping", source.contains("new ItemStack(ConfigItems.itemNugget, 1, 17)"));
        assertTrue("Missing cluster-tin smelting bonus mapping", source.contains("new ItemStack(ConfigItems.itemNugget, 1, 18)"));
        assertTrue("Missing cluster-silver smelting bonus mapping", source.contains("new ItemStack(ConfigItems.itemNugget, 1, 19)"));
        assertTrue("Missing cluster-lead smelting bonus mapping", source.contains("new ItemStack(ConfigItems.itemNugget, 1, 20)"));

        assertTrue("Missing raw chicken smelting bonus mapping", source.contains("new ItemStack(Items.CHICKEN)"));
        assertTrue("Missing raw beef smelting bonus mapping", source.contains("new ItemStack(Items.BEEF)"));
        assertTrue("Missing raw porkchop smelting bonus mapping", source.contains("new ItemStack(Items.PORKCHOP)"));
        assertTrue("Missing raw fish smelting bonus mapping", source.contains("new ItemStack(Items.FISH, 1, OreDictionary.WILDCARD_VALUE)"));
        assertTrue("Missing chicken edible nugget smelting bonus output mapping",
                source.contains("new ItemStack(ConfigItems.itemNuggetEdible, 1, 0)"));
        assertTrue("Missing beef edible nugget smelting bonus output mapping",
                source.contains("new ItemStack(ConfigItems.itemNuggetEdible, 1, 1)"));
        assertTrue("Missing pork edible nugget smelting bonus output mapping",
                source.contains("new ItemStack(ConfigItems.itemNuggetEdible, 1, 2)"));
        assertTrue("Missing fish edible nugget smelting bonus output mapping",
                source.contains("new ItemStack(ConfigItems.itemNuggetEdible, 1, 3)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
