package thaumcraft.api;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ThaumcraftApiCrucibleRecipeKeyStaticGuardTest {

    @Test
    public void craftingRecipeKeyLookupKeepsCrucibleRecipeArrayPath() throws IOException {
        String source = readFile("src/main/java/thaumcraft/api/ThaumcraftApi.java");

        assertTrue("getCraftingRecipeKey must keep CrucibleRecipe[] page-recipe branch",
                source.contains("page.recipe != null && page.recipe instanceof CrucibleRecipe[]"));
        assertTrue("getCraftingRecipeKey must iterate crucible page recipes",
                source.contains("for (CrucibleRecipe cr : crs = (CrucibleRecipe[])page.recipe)"));
        assertTrue("getCraftingRecipeKey must match crucible output stack",
                source.contains("cr.getRecipeOutput().isItemEqual(stack)"));
        assertTrue("getCraftingRecipeKey must cache key/page index for crucible matches",
                source.contains("keyCache.put(key, new Object[]{ri.key, a});"));
        assertTrue("getCraftingRecipeKey must still gate by completed research",
                source.contains("ThaumcraftApiHelper.isResearchComplete(player.getName(), ri.key)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
