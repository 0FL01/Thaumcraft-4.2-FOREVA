package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigRecipesDynamicArcaneRegistrationStaticGuardTest {

    @Test
    public void configRecipesInitShouldKeepDynamicArcaneAndRunicRecipeRegistrationGuards() throws IOException {
        String source = ConfigRecipesSourceReader.readMergedSource();

        assertTrue("ConfigRecipes should keep dynamic recipe dedupe helper",
                source.contains("private static int pruneAndCountDynamicRecipe(Class<?> recipeClass) {")
                        && source.contains("if (count > 1) {")
                        && source.contains("recipes.remove(i);"));
        assertTrue("ConfigRecipes should keep anti-dup registration guards and add missing dynamic recipes",
                source.contains("if (pruneAndCountDynamicRecipe(ArcaneWandRecipe.class) == 0) {")
                        && source.contains("ThaumcraftApi.getCraftingRecipes().add(new ArcaneWandRecipe());")
                        && source.contains("if (pruneAndCountDynamicRecipe(ArcaneSceptreRecipe.class) == 0) {")
                        && source.contains("ThaumcraftApi.getCraftingRecipes().add(new ArcaneSceptreRecipe());")
                        && source.contains("if (pruneAndCountDynamicRecipe(InfusionRunicAugmentRecipe.class) == 0) {")
                        && source.contains("ThaumcraftApi.getCraftingRecipes().add(new InfusionRunicAugmentRecipe());"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
