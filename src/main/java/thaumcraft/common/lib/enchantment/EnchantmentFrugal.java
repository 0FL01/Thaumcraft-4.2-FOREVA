package thaumcraft.common.lib.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemStack;
import thaumcraft.api.wands.ItemFocusBasic;

public class EnchantmentFrugal extends Enchantment {

    public EnchantmentFrugal() {
        super(Rarity.UNCOMMON, EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[]{
                EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND
        });
        this.setName("frugal");
        this.setRegistryName("thaumcraft", "frugal");
    }

    @Override
    public int getMinEnchantability(int level) {
        return 15 + (level - 1) * 8;
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
        return stack.getItem() instanceof ItemFocusBasic
                && ((ItemFocusBasic) stack.getItem()).acceptsEnchant(Enchantment.getEnchantmentID(this))
                || stack.getItem() instanceof ItemBook
                || stack.getItem() == Items.ENCHANTED_BOOK;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return this.canApply(stack);
    }
}
