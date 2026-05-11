package thaumcraft.common.lib.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentPotency extends Enchantment {

    public EnchantmentPotency() {
        super(Rarity.UNCOMMON, EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[]{
                EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND
        });
        this.setName("potency");
        this.setRegistryName("thaumcraft", "potency");
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
        return 5;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return true;
    }
}
