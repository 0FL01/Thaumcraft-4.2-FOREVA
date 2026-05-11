package thaumcraft.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemNuggetEdible extends ItemFood {

    public ItemNuggetEdible() {
        super(1, 0.1f, false);
        this.setHasSubtypes(false);
        this.setMaxDamage(0);
        this.setMaxStackSize(64);
        this.setAlwaysEdible();
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100, 0));
        }
    }
}
