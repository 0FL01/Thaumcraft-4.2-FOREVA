package thaumcraft.common.lib;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class CreativeTabThaumcraft extends CreativeTabs {

    public CreativeTabThaumcraft() {
        super("thaumcraft");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ItemStack createIcon() {
        // Will return ConfigItems.itemWandCasting once items are registered
        return new ItemStack(Items.AIR);
    }
}
