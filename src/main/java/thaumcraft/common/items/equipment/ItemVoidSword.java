package thaumcraft.common.items.equipment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IWarpingGear;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemResource;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemVoidSword extends ItemSword implements IRepairable, IWarpingGear {

    public ItemVoidSword(ToolMaterial material) {
        super(material);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return isVoidToolRepair(repair) || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (!target.world.isRemote) {
            target.addPotionEffect(new PotionEffect(MobEffects.WITHER, 80));
        }
        return super.hitEntity(stack, target, attacker);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entityIn, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, world, entityIn, itemSlot, isSelected);
        repairVoid(stack, world, entityIn);
    }

    @Override
    public int getWarp(ItemStack itemstack, EntityPlayer player) {
        return 1;
    }

    static boolean isVoidToolRepair(ItemStack repair) {
        return !repair.isEmpty() && repair.getItem() == ConfigItems.itemResource && repair.getMetadata() == ItemResource.META_CHARM;
    }

    static void repairVoid(ItemStack stack, World world, Entity entity) {
        if (!world.isRemote && stack.isItemDamaged() && entity != null && entity.ticksExisted % 20 == 0) {
            stack.setItemDamage(Math.max(0, stack.getItemDamage() - 1));
        }
    }
}
