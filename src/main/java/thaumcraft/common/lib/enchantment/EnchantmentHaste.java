package thaumcraft.common.lib.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemStack;
import thaumcraft.common.items.armor.ItemHoverHarness;

public class EnchantmentHaste extends Enchantment {

    public EnchantmentHaste() {
        super(Rarity.UNCOMMON, EnumEnchantmentType.ARMOR, new EntityEquipmentSlot[]{
                EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST,
                EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET
        });
        this.setName("haste");
        this.setRegistryName("thaumcraft", "haste");
    }

    @Override
    public int getMinEnchantability(int level) {
        return 15 + (level - 1) * 9;
    }

    @Override
    public int getMaxEnchantability(int level) {
        return super.getMinEnchantability(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return true;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return !stack.isEmpty() && ((stack.getItem() instanceof ItemArmor
                && ((ItemArmor) stack.getItem()).armorType == EntityEquipmentSlot.FEET)
                || stack.getItem() instanceof ItemHoverHarness
                || stack.getItem() instanceof ItemBook);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return this.canApply(stack);
    }
}
