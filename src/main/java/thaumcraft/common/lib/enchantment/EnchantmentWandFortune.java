package thaumcraft.common.lib.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentWandFortune extends Enchantment {

    public EnchantmentWandFortune() {
        super(Rarity.RARE, EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[]{
                EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND
        });
        this.setName("wand_fortune");
        this.setRegistryName("thaumcraft", "wand_fortune");
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
        return 3;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return true;
    }
}
