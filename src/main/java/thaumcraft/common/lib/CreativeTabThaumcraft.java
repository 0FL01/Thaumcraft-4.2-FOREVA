package thaumcraft.common.lib;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.config.ConfigItems;

public final class CreativeTabThaumcraft extends CreativeTabs {

    public static final CreativeTabThaumcraft tabThaumcraft = new CreativeTabThaumcraft();

    public CreativeTabThaumcraft() {
        super("thaumcraft");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack createIcon() {
        if (ConfigItems.itemWandCasting != null) {
            ItemStack stack = new ItemStack(ConfigItems.itemWandCasting);
            // Set default rod and cap
            thaumcraft.common.items.wands.ItemWandCasting.setRod(stack,
                    thaumcraft.api.wands.WandRod.rods.get("wood"));
            thaumcraft.common.items.wands.ItemWandCasting.setCap(stack,
                    thaumcraft.api.wands.WandCap.caps.get("iron"));
            return stack;
        }
        // Fallback if items not yet registered
        return net.minecraft.init.Items.AIR.getDefaultInstance();
    }
}
