package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigRecipesCosmeticPlantRegistryStaticGuardTest {

    @Test
    public void configRecipesRegistersCosmeticAndPlantBaselineRecipes() throws IOException {
        String source = ConfigRecipesSourceReader.readMergedSource();

        assertTrue("ConfigRecipes should register custom-plant quicksilver and sugar conversions",
                source.contains("setRegistryName(\"thaumcraft\", \"quicksilverplant\")")
                        && source.contains("new ItemStack(ConfigBlocks.blockCustomPlant, 1, 2)")
                        && source.contains("setRegistryName(\"thaumcraft\", \"sugarplant\")")
                        && source.contains("new ItemStack(ConfigBlocks.blockCustomPlant, 1, 3)"));

        assertTrue("ConfigRecipes should register cosmetic opaque and stone decorative baseline recipes",
                source.contains("setRegistryName(\"thaumcraft\", \"cosmeticopaque0\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"cosmeticopaque1\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"cosmeticsolid1\")")
                        && source.contains("new ItemStack(ConfigItems.itemResource, 1, 6)")
                        && source.contains("new ItemStack(Blocks.MOSSY_COBBLESTONE)"));

        assertTrue("ConfigRecipes should register reverse resource extraction from cosmetic opaque variants",
                source.contains("setRegistryName(\"thaumcraft\", \"resource6fromopaque0\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"resource6fromopaque1\")")
                        && source.contains("new ItemStack(ConfigItems.itemResource, 4, 6)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
