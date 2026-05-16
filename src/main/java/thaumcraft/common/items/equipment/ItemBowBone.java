package thaumcraft.common.items.equipment;

import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.api.IRepairable;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemBowBone extends ItemBow implements IRepairable {

    public ItemBowBone() {
        this.setMaxDamage(512);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public int getItemEnchantability() {
        return 3;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return !repair.isEmpty() && repair.getItem() == Items.BONE || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
        int ticks = this.getMaxItemUseDuration(stack) - count;
        if (ticks > 18 && entity instanceof EntityPlayer) {
            ((EntityPlayer) entity).stopActiveHand();
        }
    }
}
