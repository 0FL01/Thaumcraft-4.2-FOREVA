package thaumcraft.common.items.armor;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemRobeTravellerArmorCoreContractsStaticGuardTest {

    @Test
    public void robeAndTravellerArmorKeepReferenceRarityAndRepairContracts() throws IOException {
        String robe = readFile("src/main/java/thaumcraft/common/items/armor/ItemRobeArmor.java");
        String traveller = readFile("src/main/java/thaumcraft/common/items/armor/ItemBootsTraveller.java");

        assertTrue("ItemRobeArmor must keep uncommon rarity and thaumic-cloth repair contracts",
                robe.contains("return EnumRarity.UNCOMMON;")
                        && robe.contains("new ItemStack(ConfigItems.itemResource, 1, 7)")
                        && robe.contains("repair.isItemEqual(thaumicCloth)"));
        assertTrue("ItemBootsTraveller must keep rare rarity baseline contract",
                traveller.contains("return EnumRarity.RARE;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
