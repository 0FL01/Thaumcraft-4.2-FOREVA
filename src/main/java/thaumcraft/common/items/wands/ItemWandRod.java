package thaumcraft.common.items.wands;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thaumcraft.api.wands.WandRod;

public class ItemWandRod extends Item {

    public ItemWandRod() {
        this.setMaxStackSize(64);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + "." + stack.getMetadata();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!this.isInCreativeTab(tab)) return;
        for (WandRod rod : WandRod.rods.values()) {
            ItemStack stack = rod.getItem();
            if (!stack.isEmpty() && stack.getItem() == this) {
                items.add(stack.copy());
            }
        }
    }
}
