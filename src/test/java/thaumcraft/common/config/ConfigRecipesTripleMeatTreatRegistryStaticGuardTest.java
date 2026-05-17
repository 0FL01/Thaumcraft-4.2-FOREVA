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

        assertTrue("ConfigRecipes should register triple meat treat shapeless recipe with stable thaumcraft id",
                source.contains("new ItemStack(ConfigItems.itemTripleMeatTreat)")
                        && source.contains("new ItemStack(Items.SUGAR)")
                        && source.contains("new ItemStack(ConfigItems.itemNuggetEdible)")
                        && source.contains("setRegistryName(\"thaumcraft\", \"triplemeattreat\")"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
