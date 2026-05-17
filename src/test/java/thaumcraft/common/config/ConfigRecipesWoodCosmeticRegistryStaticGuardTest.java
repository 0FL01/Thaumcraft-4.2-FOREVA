package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigRecipesWoodCosmeticRegistryStaticGuardTest {

    @Test
    public void configRecipesAndBlocksKeepWoodStairsAndSlabBaseline() throws IOException {
        String recipesSource = ConfigRecipesSourceReader.readMergedSource();
        String blocksSource = readFile("src/main/java/thaumcraft/common/config/ConfigBlocks.java");

        assertTrue("ConfigBlocks should define and register greatwood/silverwood stairs and wooden slab blocks",
                blocksSource.contains("public static BlockStairsGreatwood blockStairsGreatwood;")
                        && blocksSource.contains("public static BlockStairsSilverwood blockStairsSilverwood;")
                        && blocksSource.contains("public static BlockCosmeticWoodSlab blockSlabWood;")
                        && blocksSource.contains("public static BlockCosmeticWoodSlab blockDoubleSlabWood;")
                        && blocksSource.contains("legacyPath(\"blockStairsGreatwood\")")
                        && blocksSource.contains("legacyPath(\"blockStairsSilverwood\")")
                        && blocksSource.contains("legacyPath(\"blockCosmeticSlabWood\")")
                        && blocksSource.contains("legacyPath(\"blockCosmeticDoubleSlabWood\")"));

        assertTrue("ConfigRecipes should register greatwood/silverwood stairs and slab baseline recipes",
                recipesSource.contains("setRegistryName(\"thaumcraft\", \"blockstairsgreatwood\")")
                        && recipesSource.contains("setRegistryName(\"thaumcraft\", \"blockstairssilverwood\")")
                        && recipesSource.contains("setRegistryName(\"thaumcraft\", \"blockslabgreatwood\")")
                        && recipesSource.contains("setRegistryName(\"thaumcraft\", \"blockslabsilverwood\")")
                        && recipesSource.contains("new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 6)")
                        && recipesSource.contains("new ItemStack(ConfigBlocks.blockWoodenDevice, 1, 7)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
