package thaumcraft.common.items.armor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IRunicArmor;
import thaumcraft.api.IWarpingGear;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemResource;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemVoidArmor extends ItemArmor implements IRepairable, IRunicArmor, IWarpingGear {

    public ItemVoidArmor(ArmorMaterial material, int renderIndex, EntityEquipmentSlot slot) {
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
        return isVoidArmorRepair(repair) || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, world, entityIn, itemSlot, isSelected);
        repairVoidArmor(stack, world, entityIn);
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        repairVoidArmor(stack, world, player);
    }

    @Override
    public int getWarp(ItemStack itemstack, EntityPlayer player) {
        return 1;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        if (slot == EntityEquipmentSlot.LEGS) {
            return "thaumcraft:textures/models/void_2.png";
        }
        return "thaumcraft:textures/models/void_1.png";
    }

    static boolean isVoidArmorRepair(ItemStack repair) {
        return !repair.isEmpty() && repair.getItem() == ConfigItems.itemResource && repair.getMetadata() == ItemResource.META_VOID_INGOT;
    }

    static void repairVoidArmor(ItemStack stack, World world, Entity entity) {
        if (!world.isRemote && stack.isItemDamaged() && entity != null && entity.ticksExisted % 20 == 0) {
            stack.setItemDamage(Math.max(0, stack.getItemDamage() - 1));
        }
    }
}
