package thaumcraft.common.items.armor;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ItemVoidArmorCoreContractsStaticGuardTest {

    @Test
    public void voidArmorFamilyKeepsReferenceRarityRepairAndRevealerContracts() throws IOException {
        String voidArmor = readFile("src/main/java/thaumcraft/common/items/armor/ItemVoidArmor.java");
        String voidRobe = readFile("src/main/java/thaumcraft/common/items/armor/ItemVoidRobeArmor.java");

        assertTrue("ItemVoidArmor must keep uncommon rarity and void-ingot repair baseline",
                voidArmor.contains("return EnumRarity.UNCOMMON;")
                        && voidArmor.contains("ItemResource.META_VOID_INGOT"));
        assertTrue("ItemVoidRobeArmor must keep epic rarity and void-ingot repair baseline",
                voidRobe.contains("return EnumRarity.EPIC;")
                        && voidRobe.contains("ItemVoidArmor.isVoidArmorRepair(repair)"));
        assertTrue("ItemVoidRobeArmor must keep revealer/goggles/special-armor interface surface",
                voidRobe.contains("implements IRepairable, IRunicArmor, IVisDiscountGear, IGoggles, IRevealer, ISpecialArmor, IWarpingGear"));
        assertTrue("ItemVoidRobeArmor must keep vis-discount tooltip and helmet-only revealer gates",
                voidRobe.contains("I18n.translateToLocal(\"tc.visdiscount\")")
                        && voidRobe.contains("return this.armorType == EntityEquipmentSlot.HEAD;")
                        && voidRobe.contains("showNodes(")
                        && voidRobe.contains("showIngamePopups("));
        assertTrue("ItemVoidRobeArmor must keep special-armor mitigation hooks",
                voidRobe.contains("public ISpecialArmor.ArmorProperties getProperties(")
                        && voidRobe.contains("source.isUnblockable()")
                        && voidRobe.contains("source.isFireDamage()")
                        && voidRobe.contains("public int getArmorDisplay(")
                        && voidRobe.contains("public void damageArmor(")
                        && voidRobe.contains("net.minecraft.util.DamageSource.FALL"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
