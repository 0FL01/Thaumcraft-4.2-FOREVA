package thaumcraft.common.items.equipment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IWarpingGear;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemCrimsonSword extends ItemSword implements IRepairable, IWarpingGear {

    public ItemCrimsonSword(ToolMaterial material) {
        super(material);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return ItemVoidSword.isVoidToolRepair(repair) || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (ItemVoidSword.canApplyVoidCombatDebuff(target, attacker)) {
            target.addPotionEffect(new PotionEffect(MobEffects.WITHER, 60));
            target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 120));
        }
        return super.hitEntity(stack, target, attacker);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, world, entityIn, itemSlot, isSelected);
        ItemVoidSword.repairVoid(stack, world, entityIn);
    }

    @Override
    public int getWarp(ItemStack itemstack, EntityPlayer player) {
        return 2;
    }
}
