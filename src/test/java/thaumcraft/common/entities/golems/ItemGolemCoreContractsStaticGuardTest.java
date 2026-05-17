package thaumcraft.common.entities.golems;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemGolemCoreContractsStaticGuardTest {

    @Test
    public void golemCoreKeepsReferenceTooltipAndCoreCapabilityMaps() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/entities/golems/ItemGolemCore.java");

        assertTrue("ItemGolemCore must keep localized core-name tooltip contract",
                source.contains("I18n.translateToLocal(\"item.ItemGolemCore.\" + stack.getItemDamage() + \".name\")"));
        assertTrue("ItemGolemCore must keep reference blank-core rarity split",
                source.contains("return stack.getItemDamage() == 100 ? EnumRarity.COMMON : EnumRarity.UNCOMMON;"));
        assertTrue("ItemGolemCore must keep reference GUI/sort/inventory core capability mappings",
                source.contains("public static boolean hasGUI(int core)")
                        && source.contains("public static boolean canSort(int core)")
                        && source.contains("public static boolean hasInventory(int core)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
