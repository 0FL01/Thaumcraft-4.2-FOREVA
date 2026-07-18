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
        String clientProxy = readFile("src/main/java/thaumcraft/client/ClientProxy.java");
        String voidRobeHelmModel = readFile("src/main/resources/assets/thaumcraft/models/item/itemhelmetvoidfortress.json");
        String voidRobeChestModel = readFile("src/main/resources/assets/thaumcraft/models/item/itemchestplatevoidfortress.json");
        String voidRobeLegsModel = readFile("src/main/resources/assets/thaumcraft/models/item/itemleggingsvoidfortress.json");

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
        assertTrue("Void Robe inventory models must keep the reference dyed base and untinted detail layers",
                voidRobeHelmModel.contains("\"layer0\": \"thaumcraft:items/voidrobehelm\"")
                        && !voidRobeHelmModel.contains("layer1")
                        && voidRobeChestModel.contains("\"layer0\": \"thaumcraft:items/voidrobechestover\"")
                        && voidRobeChestModel.contains("\"layer1\": \"thaumcraft:items/voidrobechest\"")
                        && voidRobeLegsModel.contains("\"layer0\": \"thaumcraft:items/voidrobelegsover\"")
                        && voidRobeLegsModel.contains("\"layer1\": \"thaumcraft:items/voidrobelegs\""));
        assertTrue("Void Robe inventory models must tint only their base layer",
                clientProxy.contains("(stack, tintIndex) -> tintIndex == 0 ? ((ItemArmor) stack.getItem()).getColor(stack) : -1")
                        && clientProxy.contains("ConfigItems.itemHelmVoidRobe")
                        && clientProxy.contains("ConfigItems.itemChestVoidRobe")
                        && clientProxy.contains("ConfigItems.itemLegsVoidRobe"));
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
