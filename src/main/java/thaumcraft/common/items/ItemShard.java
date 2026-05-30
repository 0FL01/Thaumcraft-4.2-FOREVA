package thaumcraft.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemShard extends Item {

    /**
     * Colors used by both item tinting and TileCrystalRenderer.
     * Indexed directly by metadata: colors[0]=Air, colors[1]=Fire, etc.
     * Derived from original 1.7.10 BlockCustomOreItem.colors[meta+1] mapping.
     */
    public static final int[] colors = {
        0xFFFF7E, // [0] Air     (meta 0) — yellow
        0xFF3C01, // [1] Fire    (meta 1) — orange-red
        0x0090FF, // [2] Water   (meta 2) — blue
        0x00A000, // [3] Earth   (meta 3) — green
        0xEECCFF, // [4] Order   (meta 4) — light purple
        0x555577  // [5] Entropy (meta 5) — grey-purple
    };

    public ItemShard() {
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    /**
     * Returns the tint color for this shard subtype.
     * Called by the IItemColor handler registered in ClientProxy.
     * Meta 6 (Balanced) returns white so the animated shard_balanced texture shows as-is.
     */
    public int getColorFromItemStack(ItemStack stack, int tintIndex) {
        int meta = stack.getItemDamage();
        if (meta >= 0 && meta < colors.length) {
            return colors[meta];
        }
        return 0xFFFFFF;
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
