package thaumcraft.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemShard extends Item {

    public static final int[] colors = {
        0x000000, // unused
        0x993333, // Fire (Ignis)
        0xCCCC66, // Earth (Terra)
        0x669999, // Air (Aer)
        0x336699, // Water (Aqua)
        0x996699, // Order (Ordo)
        0x666666  // Entropy (Perditio)
    };

    public ItemShard() {
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + "." + stack.getItemDamage();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (int i = 0; i <= 6; i++) {
                items.add(new ItemStack(this, 1, i));
            }
        }
    }
}
