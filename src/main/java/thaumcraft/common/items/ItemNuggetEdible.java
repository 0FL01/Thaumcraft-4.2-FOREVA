package thaumcraft.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemNuggetEdible extends ItemFood {
    public static final int META_CHICKEN = 0;
    public static final int META_BEEF = 1;
    public static final int META_PORK = 2;
    public static final int META_FISH = 3;
    private static final String[] NAMES = new String[]{
            "nuggetchicken",
            "nuggetbeef",
            "nuggetpork",
            "nuggetfish"
    };

    public ItemNuggetEdible() {
        super(1, 0.3f, false);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setMaxStackSize(64);
        this.setAlwaysEdible();
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 10;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        int meta = stack.getItemDamage();
        if (meta >= 0 && meta < NAMES.length) {
            return super.getTranslationKey() + "." + NAMES[meta];
        }
        return super.getTranslationKey() + "." + meta;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (int meta = 0; meta < NAMES.length; meta++) {
                items.add(new ItemStack(this, 1, meta));
            }
        }
    }
}
