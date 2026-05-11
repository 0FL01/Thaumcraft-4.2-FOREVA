package thaumcraft.common.lib.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentHaste extends Enchantment {

    public EnchantmentHaste() {
        super(Rarity.UNCOMMON, EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[]{
                EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND
        });
        this.setName("haste");
        this.setRegistryName("thaumcraft", "haste");
    }

    @Override
    public int getMinEnchantability(int level) {
        return 10 + (level - 1) * 8;
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
