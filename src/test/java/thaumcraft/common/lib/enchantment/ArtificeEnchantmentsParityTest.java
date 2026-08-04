package thaumcraft.common.lib.enchantment;

import net.minecraft.init.Bootstrap;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemStack;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.IRepairable;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionEnchantmentRecipe;
import thaumcraft.common.items.armor.ItemHoverHarness;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ArtificeEnchantmentsParityTest {
    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void hasteTargetsBootsHarnessesAndBooksWithTc4Costs() {
        EnchantmentHaste haste = new EnchantmentHaste();
        ItemHoverHarness harness = new ItemHoverHarness(ItemArmor.ArmorMaterial.IRON, 0,
                EntityEquipmentSlot.CHEST);

        assertTrue(haste.canApply(new ItemStack(Items.LEATHER_BOOTS)));
        assertTrue(haste.canApply(new ItemStack(harness)));
        assertTrue(haste.canApply(new ItemStack(Items.BOOK)));
        assertFalse(haste.canApply(new ItemStack(Items.LEATHER_HELMET)));
        assertFalse(haste.canApply(new ItemStack(Items.IRON_PICKAXE)));
        assertEquals(15, haste.getMinEnchantability(1));
        assertEquals(24, haste.getMinEnchantability(2));
        assertEquals(33, haste.getMinEnchantability(3));
        assertEquals(3, haste.getMaxLevel());
        assertEquals(5, recipeXp(haste));
    }

    @Test
    public void repairTargetsDamageableRepairablesAndRejectsUnbreaking() {
        EnchantmentRepair repair = new EnchantmentRepair();
        Item repairable = new RepairableItem();
        Item damageableBook = new ItemBook().setMaxDamage(10).setMaxStackSize(1);

        assertTrue(repair.canApply(new ItemStack(repairable)));
        assertTrue(repair.canApply(new ItemStack(damageableBook)));
        assertFalse(repair.canApply(new ItemStack(Items.BOOK)));
        assertFalse(repair.canApply(new ItemStack(Items.DIAMOND_PICKAXE)));
        assertEquals(20, repair.getMinEnchantability(1));
        assertEquals(30, repair.getMinEnchantability(2));
        assertEquals(2, repair.getMaxLevel());
        assertEquals(6, recipeXp(repair));
        assertFalse(repair.isCompatibleWith(Enchantments.UNBREAKING));
        assertTrue(repair.isCompatibleWith(Enchantments.EFFICIENCY));
    }

    @Test
    public void artificeEnchantmentsUseExactTc4EnglishNames() throws IOException {
        EnchantmentHaste haste = new EnchantmentHaste();
        EnchantmentRepair repair = new EnchantmentRepair();
        assertEquals("enchantment.haste", haste.getName());
        assertEquals("enchantment.repair", repair.getName());

        Properties language = new Properties();
        try (InputStream input = this.getClass().getClassLoader()
                .getResourceAsStream("assets/thaumcraft/lang/en_us.lang")) {
            assertNotNull("Processed en_us.lang resource is missing", input);
            language.load(new InputStreamReader(input, StandardCharsets.UTF_8));
        }
        assertEquals("Haste", language.getProperty(haste.getName()));
        assertEquals("Repair", language.getProperty(repair.getName()));
    }

    private static int recipeXp(net.minecraft.enchantment.Enchantment enchantment) {
        return new InfusionEnchantmentRecipe("", enchantment, 0, new AspectList(), new ItemStack[0]).recipeXP;
    }

    private static final class RepairableItem extends Item implements IRepairable {
        private RepairableItem() {
            this.setMaxDamage(100);
            this.setMaxStackSize(1);
        }
    }
}
