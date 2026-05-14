package thaumcraft.common.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemBathSalts extends Item {

    public ItemBathSalts() {
        this.setMaxStackSize(64);
        this.setHasSubtypes(false);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world) {
        return 200;
    }
}
