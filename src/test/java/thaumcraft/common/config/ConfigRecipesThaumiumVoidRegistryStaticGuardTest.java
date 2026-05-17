package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigRecipesThaumiumVoidRegistryStaticGuardTest {

    @Test
    public void configRecipesRegistersThaumiumVoidAndBlockThaumiumBaselineRecipes() throws IOException {
        String source = ConfigRecipesSourceReader.readMergedSource();

        assertTrue("ConfigRecipes should register thaumium block compression and decompression baseline",
                source.contains("setRegistryName(\"thaumcraft\", \"blockthaumium\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"blockthaumium_decompose\")")
                        && source.contains("'K', \"ingotThaumium\"")
                        && source.contains("new ItemStack(ConfigItems.itemResource, 9, 2)"));

        assertTrue("ConfigRecipes should register full thaumium equipment recipe baseline",
                source.contains("setRegistryName(\"thaumcraft\", \"thaumiumhelm\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"thaumiumchest\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"thaumiumlegs\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"thaumiumboots\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"thaumiumshovel\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"thaumiumpick\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"thaumiumaxe\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"thaumiumhoe\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"thaumiumsword\")"));

        assertTrue("ConfigRecipes should register full void equipment recipe baseline",
                source.contains("setRegistryName(\"thaumcraft\", \"voidhelm\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"voidchest\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"voidlegs\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"voidboots\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"voidshovel\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"voidpick\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"voidaxe\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"voidhoe\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"voidsword\")"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
