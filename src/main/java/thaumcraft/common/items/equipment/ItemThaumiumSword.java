package thaumcraft.common.items.equipment;

import net.minecraft.item.Item;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import thaumcraft.api.IRepairable;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemThaumiumSword extends ItemSword implements IRepairable {

    public ItemThaumiumSword(Item.ToolMaterial material) {
        super(material);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public float getAttackDamage() {
        return 6.0F;
    }

    @Override
    public com.google.common.collect.Multimap<String, net.minecraft.entity.ai.attributes.AttributeModifier> getAttributeModifiers(
            net.minecraft.inventory.EntityEquipmentSlot slot, ItemStack stack) {
        return ToolAttributeHelper.withMainHandDamage(super.getAttributeModifiers(slot, stack), slot, 6.0D);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        ItemStack thaumiumIngot = new ItemStack(ConfigItems.itemResource, 1, 2);
        return repair.isItemEqual(thaumiumIngot) || super.getIsRepairable(toRepair, repair);
    }
}
