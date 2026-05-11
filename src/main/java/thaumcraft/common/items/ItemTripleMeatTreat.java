package thaumcraft.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemTripleMeatTreat extends ItemFood {

    public ItemTripleMeatTreat() {
        super(6, 0.8f, true);
        this.setHasSubtypes(false);
        this.setMaxDamage(0);
        this.setMaxStackSize(16);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 1));
            player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 600, 0));
        }
    }
}
