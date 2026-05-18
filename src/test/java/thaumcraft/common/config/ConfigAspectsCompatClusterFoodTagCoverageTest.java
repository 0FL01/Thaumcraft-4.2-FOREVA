package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigAspectsCompatClusterFoodTagCoverageTest {

    @Test
    public void configAspectsRegistersCompatClusterAndFoodComplexTags() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/config/ConfigAspects.java");

        assertTrue("ConfigAspects should register compat cluster tags for copper/tin/silver/lead",
                source.contains("new ItemStack(ConfigItems.itemNugget, 1, 17)")
                        && source.contains("new ItemStack(ConfigItems.itemNugget, 1, 18)")
                        && source.contains("new ItemStack(ConfigItems.itemNugget, 1, 19)")
                        && source.contains("new ItemStack(ConfigItems.itemNugget, 1, 20)"));
        assertTrue("ConfigAspects should preserve edible nugget subtype hunger tags",
                source.contains("new ItemStack(ConfigItems.itemNuggetEdible, 1, 0)")
                        && source.contains("new ItemStack(ConfigItems.itemNuggetEdible, 1, 1)")
                        && source.contains("new ItemStack(ConfigItems.itemNuggetEdible, 1, 2)")
                        && source.contains("new ItemStack(ConfigItems.itemNuggetEdible, 1, 3)"));
        assertTrue("ConfigAspects should register TripleMeatTreat complex object tag baseline",
                source.contains("registerComplexObjectTag(")
                        && source.contains("new ItemStack(ConfigItems.itemTripleMeatTreat, 1, OreDictionary.WILDCARD_VALUE)")
                        && source.contains("new AspectList().add(Aspect.HEAL, 1).remove(Aspect.HUNGER, 1)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
