package thaumcraft.common.entities.golems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemGolemCore extends Item {
    public ItemGolemCore() {
        this.setMaxStackSize(64);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + "." + stack.getItemDamage();
    }

    public static boolean hasGUI(int core) { return core > -1 && core != 3 && core != 4 && core != 11; }

    public static boolean hasInventory(int core) { return core != 3 && core != 4 && core != 6 && core != 11; }
}
