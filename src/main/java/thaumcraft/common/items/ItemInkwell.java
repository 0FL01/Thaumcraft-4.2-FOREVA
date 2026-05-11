package thaumcraft.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thaumcraft.api.IScribeTools;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemInkwell extends Item implements IScribeTools {

    public ItemInkwell() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setMaxDamage(125);
        this.setMaxDamage(0);
        this.setNoRepair();
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this, 1, 0));
        }
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + "." + stack.getItemDamage();
    }
}
