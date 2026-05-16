package thaumcraft.common.items.armor;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemArmorRepairRarityStaticGuardTest {

    @Test
    public void thaumiumArmorAndGogglesKeepReferenceRepairRarityContracts() throws IOException {
        String thaumium = readFile("src/main/java/thaumcraft/common/items/armor/ItemThaumiumArmor.java");
        String goggles = readFile("src/main/java/thaumcraft/common/items/armor/ItemGoggles.java");

        assertTrue("ItemThaumiumArmor must keep uncommon rarity and thaumium repair contract",
                thaumium.contains("return EnumRarity.UNCOMMON;")
                        && thaumium.contains("new ItemStack(ConfigItems.itemResource, 1, 2)")
                        && thaumium.contains("repair.isItemEqual(thaumiumIngot)"));
        assertTrue("ItemGoggles must keep rare rarity and leather repair contract",
                goggles.contains("return EnumRarity.RARE;")
                        && goggles.contains("repair.getItem() == Items.LEATHER"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
