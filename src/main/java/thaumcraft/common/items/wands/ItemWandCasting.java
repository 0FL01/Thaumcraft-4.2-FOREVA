package thaumcraft.common.items.wands;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.AspectList;

public class ItemWandCasting extends Item {

    public boolean consumeAllVis(ItemStack stack, EntityPlayer player, AspectList cost, boolean doit, boolean crafting) {
        return false;
    }

    public boolean consumeAllVisCrafting(ItemStack stack, EntityPlayer player, AspectList cost, boolean doit) {
        return false;
    }

    public ItemStack getFocusItem(ItemStack stack) {
        return ItemStack.EMPTY;
    }

    public thaumcraft.api.wands.ItemFocusBasic getFocus(ItemStack stack) {
        return null;
    }

    public int getFocusTreasure(ItemStack stack) {
        return 0;
    }
}
