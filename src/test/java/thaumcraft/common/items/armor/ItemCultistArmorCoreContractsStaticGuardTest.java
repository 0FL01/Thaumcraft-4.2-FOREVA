package thaumcraft.common.items.armor;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemCultistArmorCoreContractsStaticGuardTest {

    @Test
    public void cultistArmorFamilyKeepsReferenceRarityRepairAndWarpVisContracts() throws IOException {
        String robe = readFile("src/main/java/thaumcraft/common/items/armor/ItemCultistRobeArmor.java");
        String plate = readFile("src/main/java/thaumcraft/common/items/armor/ItemCultistPlateArmor.java");
        String leader = readFile("src/main/java/thaumcraft/common/items/armor/ItemCultistLeaderArmor.java");
        String boots = readFile("src/main/java/thaumcraft/common/items/armor/ItemCultistBoots.java");

        assertTrue("ItemCultistRobeArmor must keep uncommon rarity + leather repair + vis/warp contracts",
                robe.contains("implements IRepairable, IRunicArmor, IVisDiscountGear, IWarpingGear")
                        && robe.contains("return EnumRarity.UNCOMMON;")
                        && robe.contains("repair.getItem() == Items.LEATHER")
                        && robe.contains("return 1;")
                        && robe.contains("getVisDiscount(")
                        && robe.contains("getWarp("));
        assertTrue("ItemCultistPlateArmor must keep uncommon rarity + leather repair contracts",
                plate.contains("return EnumRarity.UNCOMMON;")
                        && plate.contains("repair.getItem() == Items.LEATHER"));
        assertTrue("ItemCultistLeaderArmor must keep rare rarity + leather repair contracts",
                leader.contains("return EnumRarity.RARE;")
                        && leader.contains("repair.getItem() == Items.LEATHER"));
        assertTrue("ItemCultistBoots must keep uncommon rarity + leather repair + vis/warp contracts",
                boots.contains("implements IRepairable, IRunicArmor, IWarpingGear, IVisDiscountGear")
                        && boots.contains("return EnumRarity.UNCOMMON;")
                        && boots.contains("repair.getItem() == Items.LEATHER")
                        && boots.contains("getWarp(")
                        && boots.contains("getVisDiscount(")
                        && boots.contains("return 1;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
