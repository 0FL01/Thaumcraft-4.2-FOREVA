package thaumcraft.common.lib.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemStack;
import thaumcraft.api.IRepairable;

public class EnchantmentRepair extends Enchantment {

    public EnchantmentRepair() {
        super(Rarity.RARE, EnumEnchantmentType.ALL, new EntityEquipmentSlot[]{
                EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND,
                EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST,
                EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET
        });
        this.setName("repair");
        this.setRegistryName("thaumcraft", "repair");
    }

    @Override
    public int getMinEnchantability(int level) {
        return 20 + (level - 1) * 10;
    }

    @Override
    public int getMaxEnchantability(int level) {
        return super.getMinEnchantability(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return true;
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return !stack.isEmpty() && stack.isItemStackDamageable()
                && (stack.getItem() instanceof IRepairable || stack.getItem() instanceof ItemBook);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return this.canApply(stack);
    }

    @Override
    protected boolean canApplyTogether(Enchantment enchantment) {
        return super.canApplyTogether(enchantment) && enchantment != Enchantments.UNBREAKING;
    }
}
