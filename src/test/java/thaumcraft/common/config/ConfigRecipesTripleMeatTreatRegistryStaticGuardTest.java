package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigRecipesTripleMeatTreatRegistryStaticGuardTest {

    @Test
    public void configRecipesRegistersTripleMeatTreatSecretRecipeBaseline() throws IOException {
        String source = ConfigRecipesSourceReader.readMergedSource();

        assertTrue("ConfigRecipes should register all reference triple-meat combination recipes",
                source.contains("setRegistryName(\"thaumcraft\", \"triplemeattreat_chicken_beef_pork\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"triplemeattreat_chicken_beef_fish\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"triplemeattreat_chicken_pork_fish\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"triplemeattreat_beef_pork_fish\")"));
        assertTrue("ConfigRecipes triple-meat recipes should consume edible nuggets by type meta",
                source.contains("new ItemStack(ConfigItems.itemNuggetEdible, 1, 0)")
                        && source.contains("new ItemStack(ConfigItems.itemNuggetEdible, 1, 1)")
                        && source.contains("new ItemStack(ConfigItems.itemNuggetEdible, 1, 2)")
                        && source.contains("new ItemStack(ConfigItems.itemNuggetEdible, 1, 3)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
