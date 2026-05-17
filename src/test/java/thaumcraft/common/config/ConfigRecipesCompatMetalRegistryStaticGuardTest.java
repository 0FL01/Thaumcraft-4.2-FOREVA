package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigRecipesCompatMetalRegistryStaticGuardTest {

    @Test
    public void configRecipesRegistersCompatNuggetRecipesAndNativeSmeltingFallbacks() throws IOException {
        String source = ConfigRecipesSourceReader.readMergedSource();

        assertTrue("ConfigRecipes should invoke compat nugget registration for copper/tin/silver/lead",
                source.contains("registerCompatNuggetRecipes(registry, \"copper\", \"ingotCopper\", 1, 17);")
                        && source.contains("registerCompatNuggetRecipes(registry, \"tin\", \"ingotTin\", 2, 18);")
                        && source.contains("registerCompatNuggetRecipes(registry, \"silver\", \"ingotSilver\", 3, 19);")
                        && source.contains("registerCompatNuggetRecipes(registry, \"lead\", \"ingotLead\", 4, 20);"));

        assertTrue("Compat helper should register ingot->compat nugget and compat nugget->ingot recipes with stable ids",
                source.contains("setRegistryName(\"thaumcraft\", \"compat_\" + metalName + \"_nuggets_\" + index)")
                        && source.contains("setRegistryName(\"thaumcraft\", \"compat_\" + metalName + \"_ingot\")")
                        && source.contains("new ItemStack(ConfigItems.itemNugget, 9, compatNuggetMeta)")
                        && source.contains("new ItemStack(ConfigItems.itemNugget, 1, compatNuggetMeta)"));

        assertTrue("Compat helper should restore native nugget smelting fallback into first external ingot",
                source.contains("FurnaceRecipes.instance().addSmeltingRecipe(")
                        && source.contains("new ItemStack(ConfigItems.itemNugget, 1, nativeNuggetMeta)")
                        && source.contains("smeltingOutput.setCount(2);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
