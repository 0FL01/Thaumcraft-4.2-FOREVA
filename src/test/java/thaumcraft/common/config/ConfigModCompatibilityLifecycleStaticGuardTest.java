package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigModCompatibilityLifecycleStaticGuardTest {

    @Test
    public void modCompatibilityKeepsReferenceFlagScanAndPostInitOrder() throws IOException {
        String configSource = readFile("src/main/java/thaumcraft/common/config/Config.java");
        String thaumcraftSource = readFile("src/main/java/thaumcraft/common/Thaumcraft.java");

        assertTrue("Config.initModCompatibility must reset compat flags and scan OreDictionary names",
                configSource.contains("foundCopperIngot = false;")
                        && configSource.contains("foundTinIngot = false;")
                        && configSource.contains("foundSilverIngot = false;")
                        && configSource.contains("foundLeadIngot = false;")
                        && configSource.contains("for (String ore : OreDictionary.getOreNames())")
                        && configSource.contains("if (\"oreCopper\".equals(ore))")
                        && configSource.contains("foundCopperOre = true;")
                        && configSource.contains("if (\"ingotCopper\".equals(ore))")
                        && configSource.contains("foundCopperIngot = true;"));
        assertTrue("Config.initModCompatibility must keep special mining and elemental-axe log compatibility hooks",
                configSource.contains("Utils.addSpecialMiningResult(is, new ItemStack(ConfigItems.itemNugget, 1, 17), 1.0F);")
                        && configSource.contains("ItemElementalAxe.oreDictLogs.add(Arrays.<Object>asList(Item.getIdFromItem(is.getItem()), is.getItemDamage()));"));
        assertTrue("Thaumcraft.postInit must run compatibility and wand-component setup before recipes/aspects/research",
                thaumcraftSource.contains("Config.initModCompatibility();")
                        && thaumcraftSource.contains("initOptionalWandComponents();")
                        && thaumcraftSource.indexOf("Config.initModCompatibility();") < thaumcraftSource.indexOf("ConfigRecipes.init();")
                        && thaumcraftSource.indexOf("initOptionalWandComponents();") < thaumcraftSource.indexOf("ConfigRecipes.init();")
                        && thaumcraftSource.indexOf("ConfigRecipes.init();") < thaumcraftSource.indexOf("ConfigAspects.init();")
                        && thaumcraftSource.indexOf("ConfigAspects.init();") < thaumcraftSource.indexOf("ConfigResearch.init();"));
        assertTrue("Wand component lifecycle must not erase addon registrations during post-init",
                !thaumcraftSource.contains("WandRod.rods.clear();")
                        && !thaumcraftSource.contains("WandCap.caps.clear();"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
