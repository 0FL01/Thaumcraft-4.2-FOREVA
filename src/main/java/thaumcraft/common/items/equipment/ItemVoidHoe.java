package thaumcraft.common.items.equipment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IWarpingGear;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemVoidHoe extends ItemHoe implements IRepairable, IWarpingGear {

    public ItemVoidHoe(ToolMaterial material) {
        super(material);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public int getItemEnchantability() {
        return 5;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return ItemVoidSword.isVoidToolRepair(repair) || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (entity instanceof EntityLivingBase) {
            ItemVoidSword.tryApplyVoidWither((EntityLivingBase) entity, player, 80);
        }
        return super.onLeftClickEntity(stack, player, entity);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, world, entityIn, itemSlot, isSelected);
        ItemVoidSword.repairVoid(stack, world, entityIn);
    }

    @Override
    public int getWarp(ItemStack itemstack, EntityPlayer player) {
        return 1;
    }
}
