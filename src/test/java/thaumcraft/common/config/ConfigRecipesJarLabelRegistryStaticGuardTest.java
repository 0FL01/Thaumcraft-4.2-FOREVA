package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigRecipesJarLabelRegistryStaticGuardTest {

    @Test
    public void configRecipesRegistersJarLabelBaselineAndNbtLabelVariants() throws IOException {
        String source = ConfigRecipesSourceReader.readMergedSource();

        assertTrue("ConfigRecipes should register baseline jar label paper/slime recipe",
                source.contains("new ItemStack(ConfigItems.itemResource, 4, 13)")
                        && source.contains("Items.SLIME_BALL")
                        && source.contains("Items.PAPER")
                        && source.contains("setRegistryName(\"thaumcraft\", \"jarlabel\")"));
        assertTrue("ConfigRecipes should register per-aspect NBT jar label recipes",
                source.contains("for (Aspect aspect : Aspect.aspects.values())")
                        && source.contains("new ShapelessNBTOreRecipe(")
                        && source.contains("new ItemStack(ConfigItems.itemEssence, 1, 1)")
                        && source.contains("setRegistryName(\"thaumcraft\", \"jarlabel_\" + aspect.getTag().toLowerCase())"));
        assertTrue("ConfigRecipes should register jar label clearing recipe",
                source.contains("setRegistryName(\"thaumcraft\", \"jarlabelnull\")")
                        && source.contains("new AspectList().add(Aspect.WATER, 1)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
