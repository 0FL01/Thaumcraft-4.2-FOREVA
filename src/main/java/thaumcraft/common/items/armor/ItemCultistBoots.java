package thaumcraft.common.items.armor;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IRunicArmor;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.IWarpingGear;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemCultistBoots extends ItemArmor implements IRepairable, IRunicArmor, IWarpingGear, IVisDiscountGear {

    public ItemCultistBoots(ArmorMaterial material, int renderIndex, EntityEquipmentSlot slot) {
        super(material, renderIndex, slot);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public int getRunicCharge(ItemStack itemstack) {
        return 0;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return !repair.isEmpty() && repair.getItem() == Items.LEATHER || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getWarp(ItemStack itemstack, net.minecraft.entity.player.EntityPlayer player) {
        return 1;
    }

    @Override
    public int getVisDiscount(ItemStack stack, net.minecraft.entity.player.EntityPlayer player, Aspect aspect) {
        return 1;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "thaumcraft:textures/models/cultistboots.png";
    }
}
