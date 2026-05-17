package thaumcraft.common.entities.golems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import thaumcraft.common.lib.CreativeTabThaumcraft;

import java.util.List;

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

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this, 1, 100));
            for (int meta = 0; meta <= 11; meta++) {
                items.add(new ItemStack(this, 1, meta));
            }
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return stack.getItemDamage() == 100 ? EnumRarity.COMMON : EnumRarity.UNCOMMON;
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.translateToLocal("item.ItemGolemCore." + stack.getItemDamage() + ".name"));
    }

    public static boolean hasGUI(int core) {
        switch (core) {
            case 0:
            case 1:
            case 2:
            case 4:
            case 5:
            case 8:
            case 10:
                return true;
            default:
                return false;
        }
    }

    public static boolean canSort(int core) {
        switch (core) {
            case 0:
            case 1:
            case 2:
            case 8:
            case 10:
                return true;
            default:
                return false;
        }
    }

    public static boolean hasInventory(int core) {
        switch (core) {
            case 0:
            case 1:
            case 2:
            case 5:
            case 8:
                return true;
            default:
                return false;
        }
    }
}
