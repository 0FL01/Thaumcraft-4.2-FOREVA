package thaumcraft.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemNugget extends Item {

    public static final int META_IRON = 0;
    public static final int META_COPPER = 1;
    public static final int META_TIN = 2;
    public static final int META_SILVER = 3;
    public static final int META_LEAD = 4;
    public static final int META_QUICKSILVER = 5;
    public static final int META_THAUMIUM = 6;
    public static final int META_VOID = 7;
    public static final int META_CLUSTER_IRON = 16;
    public static final int META_CLUSTER_COPPER = 17;
    public static final int META_CLUSTER_TIN = 18;
    public static final int META_CLUSTER_SILVER = 19;
    public static final int META_CLUSTER_LEAD = 20;
    public static final int META_CLUSTER_CINNABAR = 21;
    public static final int META_CLUSTER_GOLD = 31;

    public static final String[] NAMES = {
            "nuggetiron", "nuggetcopper", "nuggettin", "nuggetsilver", "nuggetlead",
            "nuggetquicksilver", "nuggetthaumium", "nuggetvoid",
            "", "", "", "", "", "", "", "",
            "clusteriron", "clustercopper", "clustertin", "clustersilver", "clusterlead",
            "clustercinnabar", "", "", "", "", "", "", "", "", "",
            "clustergold"
    };

    public ItemNugget() {
        this.setMaxStackSize(64);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        int d = stack.getItemDamage();
        if (d >= 0 && d < NAMES.length && !NAMES[d].isEmpty()) {
            return super.getTranslationKey() + "." + NAMES[d];
        }
        return super.getTranslationKey() + "." + d;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this, 1, META_IRON));
            items.add(new ItemStack(this, 1, META_QUICKSILVER));
            items.add(new ItemStack(this, 1, META_CLUSTER_CINNABAR));
            items.add(new ItemStack(this, 1, META_THAUMIUM));
            items.add(new ItemStack(this, 1, META_VOID));
            items.add(new ItemStack(this, 1, META_CLUSTER_IRON));
            items.add(new ItemStack(this, 1, META_CLUSTER_GOLD));
            addOptionalPair(items, Config.foundCopperIngot, META_COPPER, META_CLUSTER_COPPER);
            addOptionalPair(items, Config.foundTinIngot, META_TIN, META_CLUSTER_TIN);
            addOptionalPair(items, Config.foundSilverIngot, META_SILVER, META_CLUSTER_SILVER);
            addOptionalPair(items, Config.foundLeadIngot, META_LEAD, META_CLUSTER_LEAD);
        }
    }

    private void addOptionalPair(NonNullList<ItemStack> items, boolean enabled,
            int nuggetMeta, int clusterMeta) {
        if (enabled) {
            items.add(new ItemStack(this, 1, nuggetMeta));
            items.add(new ItemStack(this, 1, clusterMeta));
        }
    }
}
