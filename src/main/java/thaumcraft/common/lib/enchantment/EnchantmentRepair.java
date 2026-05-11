package thaumcraft.common.lib.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentRepair extends Enchantment {

    public EnchantmentRepair() {
        super(Rarity.RARE, EnumEnchantmentType.BREAKABLE, new EntityEquipmentSlot[]{
                EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND,
                EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST,
                EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET
        });
        this.setName("repair");
        this.setRegistryName("thaumcraft", "repair");
    }

    @Override
    public int getMinEnchantability(int level) {
        return 20 + (level - 1) * 8;
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
}
