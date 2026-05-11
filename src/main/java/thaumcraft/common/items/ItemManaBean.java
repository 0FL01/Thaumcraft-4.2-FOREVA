package thaumcraft.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemManaBean extends ItemFood {

    public ItemManaBean() {
        super(1, 0.3f, false);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setAlwaysEdible();
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + "." + stack.getItemDamage();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this, 1, 0));
        }
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            // Restore vis/fill mana - TBD
            player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 0));
        }
    }
}
