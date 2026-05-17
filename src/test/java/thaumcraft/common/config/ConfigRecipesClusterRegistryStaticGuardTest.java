package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigRecipesClusterRegistryStaticGuardTest {

    @Test
    public void configRecipesRegistersClusterRecipesAndReusesHandlesInResearchMap() throws IOException {
        String source = ConfigRecipesSourceReader.readMergedSource();

        assertTrue("ConfigRecipes must keep cluster recipe handle storage",
                source.contains("private static final IRecipe[] recipeClusters = new IRecipe[7];"));
        assertTrue("ConfigRecipes must register cluster recipes under thaumcraft ids",
                source.contains("setRegistryName(\"thaumcraft\", \"clusters\" + a)")
                        && source.contains("setRegistryName(\"thaumcraft\", \"clusters6\")")
                        && source.contains("bridge.setRecipeCluster(a, recipeCluster);")
                        && source.contains("registry.register(recipeCluster);")
                        && source.contains("bridge.setRecipeCluster(6, recipeCluster6);")
                        && source.contains("registry.register(recipeCluster6);"));
        assertTrue("ConfigResearch map should consume registered cluster handles when present",
                source.contains("if (recipeClusters[a] != null)")
                        && source.contains("ConfigResearch.recipes.put(\"Clusters\" + a, recipeClusters[a]);")
                        && source.contains("if (recipeClusters[6] != null)")
                        && source.contains("ConfigResearch.recipes.put(\"Clusters6\", recipeClusters[6]);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
