package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigRecipesBlockTallowDecomposeStaticGuardTest {

    @Test
    public void configRecipesRegistersBlockTallowDecomposeBaselineRecipe() throws IOException {
        String source = ConfigRecipesSourceReader.readMergedSource();

        assertTrue("ConfigRecipes should keep block tallow reverse recipe baseline",
                source.contains("new ItemStack(ConfigItems.itemResource, 9, 4)")
                        && source.contains("new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 5)")
                        && source.contains("setRegistryName(\"thaumcraft\", \"blocktallow_decompose\")"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
