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
        String hover = readFile("src/main/java/thaumcraft/common/items/armor/Hover.java");
        String clientProxy = readFile("src/main/java/thaumcraft/client/ClientProxy.java");
        String robeChestModel = readFile("src/main/resources/assets/thaumcraft/models/item/itemchestplaterobe.json");
        String robeLegsModel = readFile("src/main/resources/assets/thaumcraft/models/item/itemleggingsrobe.json");
        String robeBootsModel = readFile("src/main/resources/assets/thaumcraft/models/item/itembootsrobe.json");

        assertTrue("ItemRobeArmor must keep uncommon rarity and thaumic-cloth repair contracts",
                robe.contains("return EnumRarity.UNCOMMON;")
                        && robe.contains("new ItemStack(ConfigItems.itemResource, 1, 7)")
                        && robe.contains("repair.isItemEqual(thaumicCloth)"));
        assertTrue("ItemRobeArmor must keep the reference vis-discount tooltip",
                robe.contains("I18n.translateToLocal(\"tc.visdiscount\")"));
        assertTrue("Robe inventory models must keep the reference dyed base and untinted overlay layers",
                robeChestModel.contains("\"layer0\": \"thaumcraft:items/clothchest\"")
                        && robeChestModel.contains("\"layer1\": \"thaumcraft:items/clothchestover\"")
                        && robeLegsModel.contains("\"layer0\": \"thaumcraft:items/clothlegs\"")
                        && robeLegsModel.contains("\"layer1\": \"thaumcraft:items/clothlegsover\"")
                        && robeBootsModel.contains("\"layer0\": \"thaumcraft:items/clothboots\"")
                        && robeBootsModel.contains("\"layer1\": \"thaumcraft:items/clothbootsover\""));
        assertTrue("Robe inventory models must tint only the base layer",
                clientProxy.contains("(stack, tintIndex) -> tintIndex == 0 ? ((ItemArmor) stack.getItem()).getColor(stack) : -1")
                        && clientProxy.contains("ConfigItems.itemChestRobe")
                        && clientProxy.contains("ConfigItems.itemLegsRobe")
                        && clientProxy.contains("ConfigItems.itemBootsRobe"));
        assertTrue("ItemBootsTraveller must keep rare rarity baseline contract",
                traveller.contains("return EnumRarity.RARE;"));
        assertTrue("BootsTraveller must keep hover-tick integration contract",
                traveller.contains("Hover.doHover(stack, player, world, player.inventory.armorInventory.indexOf(stack));"));
        assertTrue("Hover must keep traveller movement core contracts",
                hover.contains("!player.capabilities.isCreativeMode && player.moveForward > 0.0F")
                        && hover.contains("player.stepHeight = 1.0F;")
                        && hover.contains("player.moveRelative(0.0F, 0.0F, 1.0F, bonus);")
                        && hover.contains("player.jumpMovementFactor = getHover(playerId) ? 0.03F : 0.05F;")
                        && hover.contains("player.fallDistance = Math.max(0.0F, player.fallDistance - 0.25F);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
