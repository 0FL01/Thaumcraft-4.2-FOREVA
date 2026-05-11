package thaumcraft.common.items.equipment;

import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import thaumcraft.api.IRepairable;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemBowBone extends ItemBow implements IRepairable {

    public ItemBowBone() {
        this.setMaxDamage(500);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public int getItemEnchantability() {
        return 15;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }
}
