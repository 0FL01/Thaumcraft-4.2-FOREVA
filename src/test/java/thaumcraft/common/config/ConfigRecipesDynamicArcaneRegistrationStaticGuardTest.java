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

        assertTrue("ConfigRecipes should keep dynamic recipe presence flags",
                source.contains("boolean hasArcaneWand = false;")
                        && source.contains("boolean hasArcaneSceptre = false;")
                        && source.contains("boolean hasRunicAugment = false;"));
        assertTrue("ConfigRecipes should scan ThaumcraftApi recipe list for dynamic recipe classes",
                source.contains("if (recipe instanceof ArcaneWandRecipe)")
                        && source.contains("if (recipe instanceof ArcaneSceptreRecipe)")
                        && source.contains("if (recipe instanceof InfusionRunicAugmentRecipe)"));
        assertTrue("ConfigRecipes should keep anti-dup registration guards and add missing dynamic recipes",
                source.contains("if (!hasArcaneWand) {")
                        && source.contains("ThaumcraftApi.getCraftingRecipes().add(new ArcaneWandRecipe());")
                        && source.contains("if (!hasArcaneSceptre) {")
                        && source.contains("ThaumcraftApi.getCraftingRecipes().add(new ArcaneSceptreRecipe());")
                        && source.contains("if (!hasRunicAugment) {")
                        && source.contains("ThaumcraftApi.getCraftingRecipes().add(new InfusionRunicAugmentRecipe());"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
