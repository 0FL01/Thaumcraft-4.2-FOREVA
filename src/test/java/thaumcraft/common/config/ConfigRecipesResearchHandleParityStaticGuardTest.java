package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class ConfigRecipesResearchHandleParityStaticGuardTest {

    @Test
    public void configRecipesIncludesStage9ReferenceResearchHandlesForSpecialRecipes() throws IOException {
        String source = ConfigRecipesSourceReader.readMergedSource();
        List<String> requiredKeys = Arrays.asList(
                "InfusionAltar",
                "NodeJar",
                "JarLabel",
                "JarLabelNull",
                "BlockThaumium",
                "ThaumiumHelm",
                "ThaumiumChest",
                "ThaumiumLegs",
                "ThaumiumBoots",
                "ThaumiumShovel",
                "ThaumiumPick",
                "ThaumiumAxe",
                "ThaumiumHoe",
                "ThaumiumSword",
                "VoidHelm",
                "VoidChest",
                "VoidLegs",
                "VoidBoots",
                "VoidShovel",
                "VoidPick",
                "VoidAxe",
                "VoidHoe",
                "VoidSword",
                "TallowCandle"
        );

        for (String key : requiredKeys) {
            assertTrue("Missing research recipe-handle key in ConfigRecipes: " + key,
                    source.contains("ConfigResearch.recipes.put(\"" + key + "\"")
                            || source.contains("specialResearchRecipeHandles.put(\"" + key + "\"")
                            || source.contains("bridge.addSpecialResearchRecipeHandle(\"" + key + "\""));
        }

        assertTrue("Missing JarLabel aspect-handle list capture baseline",
                source.contains("recipeJarLabelAspects.add(")
                        && source.contains("ConfigResearch.recipes.put(\"JarLabel\" + i"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
