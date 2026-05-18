package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class ConfigRecipesNuggetConversionRegistryStaticGuardTest {

    @Test
    public void configRecipesRegistersNuggetIngotConversionBaseline() throws IOException {
        String source = ConfigRecipesSourceReader.readMergedSource();

        assertTrue("Missing iron nugget conversion registry recipes",
                source.contains("setRegistryName(\"thaumcraft\", \"nuggets_iron\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"iron_from_nuggets\")"));
        assertTrue("Missing thaumium nugget conversion registry recipes",
                source.contains("setRegistryName(\"thaumcraft\", \"nuggets_thaumium\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"thaumium_from_nuggets\")"));
        assertTrue("Missing void nugget conversion registry recipes",
                source.contains("setRegistryName(\"thaumcraft\", \"nuggets_void\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"void_from_nuggets\")"));
        assertTrue("Missing quicksilver nugget conversion registry recipes",
                source.contains("setRegistryName(\"thaumcraft\", \"nuggets_quicksilver\")")
                        && source.contains("setRegistryName(\"thaumcraft\", \"quicksilver_from_nuggets\")"));
    }
}
