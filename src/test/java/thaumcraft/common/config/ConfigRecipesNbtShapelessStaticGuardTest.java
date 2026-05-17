package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigRecipesNbtShapelessStaticGuardTest {

    @Test
    public void shapelessNbtOreRecipeFactoryUsesDedicatedNbtRecipeClass() throws IOException {
        String source = ConfigRecipesSourceReader.readMergedSource();

        assertTrue("ConfigRecipes should import dedicated NBT-aware shapeless recipe implementation",
                source.contains("import thaumcraft.common.lib.crafting.ShapelessNBTOreRecipe;"));
        assertTrue("shapelessNBTOreRecipe must return ShapelessNBTOreRecipe instead of plain ShapelessOreRecipe",
                source.contains("return new ShapelessNBTOreRecipe(output, recipe);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
