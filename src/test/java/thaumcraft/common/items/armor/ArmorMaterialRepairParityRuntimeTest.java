package thaumcraft.common.items.armor;

import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.junit.Test;
import thaumcraft.api.ThaumcraftApi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ArmorMaterialRepairParityRuntimeTest {

    @Test
    public void publicArmorMaterialsMapTc4LogicalSlotsToForgeOrdering() {
        assertMaterial(ThaumcraftApi.armorMatThaumium, 25, 2, 6, 5, 2, 25);
        assertMaterial(ThaumcraftApi.armorMatSpecial, 25, 1, 3, 2, 1, 25);
        assertMaterial(ThaumcraftApi.armorMatVoidFortress, 18, 4, 8, 7, 4, 10);
    }

    @Test
    public void configUsesTc4CultistMaterialFamilies() throws IOException {
        String source = read("src/main/java/thaumcraft/common/config/ConfigItems.java");

        assertTrue(source.contains("ARMOR_CULTIST = ArmorMaterial.IRON;"));
        assertTrue(source.contains("ARMOR_CULTIST_PLATE = ArmorMaterial.IRON;"));
        assertTrue(source.contains("ARMOR_CULTIST_LEADER = thaumcraft.api.ThaumcraftApi.armorMatThaumiumFortress;"));
        assertTrue(source.contains("ARMOR_CULTIST_BOOTS = ArmorMaterial.IRON;"));
        assertFalse(source.contains("setRepairItem(ARMOR_CULTIST"));
    }

    @Test
    public void cultistArmorAcceptsOnlyIronRepair() {
        assertRepair(new ItemCultistRobeArmor(ItemArmor.ArmorMaterial.IRON, 0, EntityEquipmentSlot.HEAD), Items.IRON_INGOT);
        assertRepair(new ItemCultistPlateArmor(ItemArmor.ArmorMaterial.IRON, 0, EntityEquipmentSlot.CHEST), Items.IRON_INGOT);
        assertRepair(new ItemCultistLeaderArmor(ThaumcraftApi.armorMatThaumiumFortress, 0, EntityEquipmentSlot.CHEST), Items.IRON_INGOT);
        assertRepair(new ItemCultistBoots(ItemArmor.ArmorMaterial.IRON, 0, EntityEquipmentSlot.FEET), Items.IRON_INGOT);
    }

    @Test
    public void gogglesAndHarnessAcceptOnlyGoldRepair() {
        assertRepair(new ItemGoggles(ThaumcraftApi.armorMatSpecial, 0, EntityEquipmentSlot.HEAD), Items.GOLD_INGOT);
        assertRepair(new ItemHoverHarness(ThaumcraftApi.armorMatSpecial, 0, EntityEquipmentSlot.CHEST), Items.GOLD_INGOT);
    }

    private static void assertMaterial(ItemArmor.ArmorMaterial material, int durability,
            int head, int chest, int legs, int feet, int enchantability) {
        assertEquals(durability * 11, material.getDurability(EntityEquipmentSlot.HEAD));
        assertEquals(durability * 16, material.getDurability(EntityEquipmentSlot.CHEST));
        assertEquals(durability * 15, material.getDurability(EntityEquipmentSlot.LEGS));
        assertEquals(durability * 13, material.getDurability(EntityEquipmentSlot.FEET));
        assertEquals(head, material.getDamageReductionAmount(EntityEquipmentSlot.HEAD));
        assertEquals(chest, material.getDamageReductionAmount(EntityEquipmentSlot.CHEST));
        assertEquals(legs, material.getDamageReductionAmount(EntityEquipmentSlot.LEGS));
        assertEquals(feet, material.getDamageReductionAmount(EntityEquipmentSlot.FEET));
        assertEquals(enchantability, material.getEnchantability());
    }

    private static void assertRepair(ItemArmor armor, net.minecraft.item.Item accepted) {
        ItemStack armorStack = new ItemStack(armor);
        assertTrue(armor.getIsRepairable(armorStack, new ItemStack(accepted)));
        assertFalse(armor.getIsRepairable(armorStack, new ItemStack(Items.LEATHER)));
        assertFalse(armor.getIsRepairable(armorStack, new ItemStack(Items.DIAMOND)));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
