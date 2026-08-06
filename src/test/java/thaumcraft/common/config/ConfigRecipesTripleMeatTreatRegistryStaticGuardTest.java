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
        assertTrue("ConfigRecipes triple-meat recipes should consume the four TC4 nugget items",
                source.contains("new ItemStack(ConfigItems.itemNuggetChicken)")
                        && source.contains("new ItemStack(ConfigItems.itemNuggetBeef)")
                        && source.contains("new ItemStack(ConfigItems.itemNuggetPork)")
                        && source.contains("new ItemStack(ConfigItems.itemNuggetFish)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
