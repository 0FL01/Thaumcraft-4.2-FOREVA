package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class ConfigRecipesSpecialRecipeLifecycleStaticGuardTest {

    @Test
    public void registerSpecialRecipesRebindsResearchRecipeMapToRegisteredSpecialRecipes() throws IOException {
        String source = ConfigRecipesSourceReader.readMergedSource();

        int registerCall = source.indexOf("ConfigRecipesSpecialSlice.registerSpecialRecipes(registry, new SpecialRecipesBridge());");
        int refreshCall = source.indexOf("refreshLateBoundResearchRecipeHandles();");

        assertTrue("ConfigRecipes must still register special recipes through the slice bridge",
                registerCall >= 0);
        assertTrue("ConfigRecipes must refresh ConfigResearch recipe handles after special recipe registration",
                refreshCall > registerCall);
        assertTrue("Late-bound research recipe refresh must republish registered mundane/block recipes",
                source.contains("ConfigResearch.recipes.put(\"MundaneAmulet\", recipeMundaneAmulet);")
                        && source.contains("ConfigResearch.recipes.put(\"BlockTallow\", recipeBlockTallow);"));
        assertTrue("Late-bound research recipe refresh must republish special research handles and JarLabel aspect variants",
                source.contains("for (Map.Entry<String, IRecipe> entry : specialResearchRecipeHandles.entrySet())")
                        && source.contains("ConfigResearch.recipes.put(\"JarLabel\" + i, recipe);"));
    }
}
