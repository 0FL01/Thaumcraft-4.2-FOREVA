package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigRecipesBasicsRecipeParityStaticGuardTest {

    @Test
    public void introductoryResearchRecipesKeepTc4235Ingredients() throws IOException {
        String source = ConfigRecipesSourceReader.readMergedSource();
        String scribe2 = between(source, "IRecipe recipeScribe2 = new ShapelessOreRecipe",
                "IRecipe recipeScribe3 = new ShapelessOreRecipe");
        String thaumometer = between(source, "IRecipe recipeThaumometer = new ShapedOreRecipe",
                "IRecipe recipeWandCapIron = new ShapedOreRecipe");

        assertTrue("Scribe2 must use the TC4 glass bottle, feather, and black dye inputs",
                scribe2.contains("Items.GLASS_BOTTLE")
                        && scribe2.contains("Items.FEATHER")
                        && scribe2.contains("\"dyeBlack\""));
        assertFalse("Scribe2 must not substitute a second generic dye for the glass bottle",
                scribe2.contains("Items.DYE"));

        assertTrue("Thaumometer must use the two TC4 gold ingots",
                thaumometer.contains("'I', Items.GOLD_INGOT"));
        assertFalse("Thaumometer must not substitute iron for gold",
                thaumometer.contains("Items.IRON_INGOT"));
    }

    private static String between(String source, String start, String end) {
        int from = source.indexOf(start);
        int to = source.indexOf(end, from + start.length());
        assertTrue("Missing source section start: " + start, from >= 0);
        assertTrue("Missing source section end: " + end, to > from);
        return source.substring(from, to);
    }
}
