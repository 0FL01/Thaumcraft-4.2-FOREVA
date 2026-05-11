package thaumcraft.common.entities.golems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemGolemDecoration extends Item {
    public ItemGolemDecoration() {
        this.setMaxStackSize(64);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);

    }
}
