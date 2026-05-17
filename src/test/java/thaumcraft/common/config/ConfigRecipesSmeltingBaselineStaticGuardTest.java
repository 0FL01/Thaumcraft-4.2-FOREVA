package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigRecipesSmeltingBaselineStaticGuardTest {

    @Test
    public void configRecipesKeepsReferenceSmeltingOutputBaseline() throws IOException {
        String source = ConfigRecipesSourceReader.readMergedSource();

        assertTrue("ConfigRecipes init must execute smelting baseline before smelting bonuses",
                source.contains("initializeSmeltingBaseline();")
                        && source.indexOf("initializeSmeltingBaseline();") < source.indexOf("initializeSmeltingBonusBaseline();"));
        assertTrue("ConfigRecipes smelting baseline must include custom ore and magical log outputs",
                source.contains("new ItemStack(ConfigBlocks.blockCustomOre, 1, 0)")
                        && source.contains("new ItemStack(ConfigItems.itemResource, 1, 3)")
                        && source.contains("new ItemStack(ConfigBlocks.blockCustomOre, 1, 7)")
                        && source.contains("new ItemStack(ConfigItems.itemResource, 1, 6)")
                        && source.contains("addSmeltingRecipeForBlock(")
                        && source.contains("ConfigBlocks.blockMagicalLog")
                        && source.contains("new ItemStack(Items.COAL, 1, 1)"));
        assertTrue("ConfigRecipes smelting baseline must include cluster and shard smelting conversions",
                source.contains("new ItemStack(ConfigItems.itemNugget, 1, 16)")
                        && source.contains("new ItemStack(Items.IRON_INGOT, 2, 0)")
                        && source.contains("new ItemStack(ConfigItems.itemNugget, 1, 21)")
                        && source.contains("new ItemStack(ConfigItems.itemResource, 2, 3)")
                        && source.contains("new ItemStack(ConfigItems.itemNugget, 1, 31)")
                        && source.contains("new ItemStack(Items.GOLD_INGOT, 2, 0)")
                        && source.contains("new ItemStack(ConfigItems.itemShard, 1, 6)")
                        && source.contains("new ItemStack(ConfigItems.itemResource, 1, 14)")
                        && source.contains("new ItemStack(ConfigItems.itemResource, 1, 18)")
                        && source.contains("new ItemStack(Items.GOLD_NUGGET)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
