package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigRecipesNormalRecipeRegistryStaticGuardTest {

    @Test
    public void configRecipesRegistersBaselineMundaneAndBlockRecipes() throws IOException {
        String source = ConfigRecipesSourceReader.readMergedSource();

        assertTrue("ConfigRecipes must register mundane bauble recipes in Forge registry",
                source.contains("recipeMundaneAmulet = new ShapedOreRecipe")
                        && source.contains("setRegistryName(\"thaumcraft\", \"mundaneamulet\")")
                        && source.contains("recipeMundaneRing = new ShapedOreRecipe")
                        && source.contains("setRegistryName(\"thaumcraft\", \"mundanering\")")
                        && source.contains("recipeMundaneBelt = new ShapedOreRecipe")
                        && source.contains("setRegistryName(\"thaumcraft\", \"mundanebelt\")"));
        assertTrue("ConfigRecipes must register baseline flesh/tallow block recipes in Forge registry",
                source.contains("recipeBlockFlesh = new ShapedOreRecipe")
                        && source.contains("setRegistryName(\"thaumcraft\", \"blockflesh\")")
                        && source.contains("recipeBlockTallow = new ShapedOreRecipe")
                        && source.contains("setRegistryName(\"thaumcraft\", \"blocktallow\")"));
        assertTrue("ConfigResearch recipe map should use registered baseline recipes when available",
                source.contains("if (recipeMundaneAmulet != null)")
                        && source.contains("ConfigResearch.recipes.put(\"MundaneAmulet\", recipeMundaneAmulet);")
                        && source.contains("if (recipeBlockTallow != null)")
                        && source.contains("ConfigResearch.recipes.put(\"BlockTallow\", recipeBlockTallow);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
