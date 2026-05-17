package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigRecipesCustomRecipeIdStaticGuardTest {

    @Test
    public void configRecipesKeepsThaumcraftNamespaceForCustomRecipeIds() throws IOException {
        String source = ConfigRecipesSourceReader.readMergedSource();

        assertTrue("Custom robe dye recipes must register under thaumcraft namespace",
                source.contains("new RecipesRobeArmorDyes().setRegistryName(\"thaumcraft\", \"robearmordye\")")
                        && source.contains("new RecipesVoidRobeArmorDyes().setRegistryName(\"thaumcraft\", \"voidrobearmordye\")"));
        assertTrue("ConfigRecipes must not register custom recipes under forge namespace",
                !source.contains("setRegistryName(\"forge\", \"robearmordye\")")
                        && !source.contains("setRegistryName(\"forge\", \"voidrobearmordye\")"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
