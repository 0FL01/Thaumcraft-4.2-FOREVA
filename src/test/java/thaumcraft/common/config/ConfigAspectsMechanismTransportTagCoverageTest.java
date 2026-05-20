package thaumcraft.common.config;

import org.junit.Test;
import net.minecraftforge.oredict.OreDictionary;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigAspectsMechanismTransportTagCoverageTest {

    @Test
    public void configAspectsCoversVanillaMechanismAndTransportFamily() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/config/ConfigAspects.java");

        assertTrue("ConfigAspects should keep the grouped utility item tags used by Stage 9 object scans",
                source.contains("new ItemStack(Items.BOW)")
                        && source.contains("new ItemStack(Items.CAKE)")
                        && source.contains("new ItemStack(Items.MINECART)")
                        && source.contains("new ItemStack(Items.BOAT)")
                        && source.contains("new ItemStack(Items.REPEATER)")
                        && source.contains("new ItemStack(Items.COMPASS)")
                        && source.contains("new ItemStack(Items.CLOCK)"));

        assertTrue("ConfigAspects should keep the grouped mechanism and transport block tags",
                source.contains("new ItemStack(Blocks.DISPENSER)")
                        && source.contains("new ItemStack(Blocks.RAIL, 1, OreDictionary.WILDCARD_VALUE)")
                        && source.contains("new ItemStack(Blocks.DAYLIGHT_DETECTOR)")
                        && source.contains("new ItemStack(Blocks.REDSTONE_TORCH, 1, OreDictionary.WILDCARD_VALUE)")
                        && source.contains("new ItemStack(Blocks.REDSTONE_LAMP, 1, OreDictionary.WILDCARD_VALUE)")
                        && source.contains("new ItemStack(Blocks.CRAFTING_TABLE)")
                        && source.contains("new ItemStack(Blocks.ENCHANTING_TABLE)")
                        && source.contains("new ItemStack(Blocks.ANVIL, 1, OreDictionary.WILDCARD_VALUE)")
                        && source.contains("new ItemStack(Blocks.PISTON, 1, OreDictionary.WILDCARD_VALUE)")
                        && source.contains("new ItemStack(Blocks.STICKY_PISTON, 1, OreDictionary.WILDCARD_VALUE)")
                        && source.contains("new ItemStack(Blocks.ENDER_CHEST, 1, OreDictionary.WILDCARD_VALUE)")
                        && source.contains("new ItemStack(Blocks.HOPPER, 1, OreDictionary.WILDCARD_VALUE)")
                        && source.contains("new ItemStack(Blocks.DROPPER, 1, OreDictionary.WILDCARD_VALUE)")
                        && source.contains("new ItemStack(Blocks.TRAPPED_CHEST, 1, OreDictionary.WILDCARD_VALUE)")
                        && source.contains("new ItemStack(Blocks.BEACON, 1, OreDictionary.WILDCARD_VALUE)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
