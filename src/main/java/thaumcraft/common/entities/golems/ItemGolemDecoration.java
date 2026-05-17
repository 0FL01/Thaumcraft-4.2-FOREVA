package thaumcraft.common.entities.golems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemGolemDecoration extends Item {
    public ItemGolemDecoration() {
        this.setMaxStackSize(64);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + "." + stack.getItemDamage();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return I18n.translateToLocal("item.ItemGolemDecoration.name") + ": " + super.getItemStackDisplayName(stack);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (int meta = 0; meta <= 7; meta++) {
                items.add(new ItemStack(this, 1, meta));
            }
        }
    }

    public static String getDecoChar(int meta) {
        switch (meta) {
            case 0:
                return "H";
            case 1:
                return "G";
            case 2:
                return "B";
            case 3:
                return "F";
            case 4:
                return "R";
            case 5:
                return "V";
            case 6:
                return "P";
            case 7:
                return "M";
            default:
                return "";
        }
    }
}
