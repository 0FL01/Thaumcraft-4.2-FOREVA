package thaumcraft.common.lib.crafting;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.AspectList;

public class ThaumcraftCraftingManager {

    public static AspectList getObjectTags(ItemStack is) {
        return new AspectList();
    }

    public static AspectList getBonusTags(ItemStack is, AspectList ot) {
        return new AspectList();
    }

    public static AspectList generateTags(Item item, int meta) {
        return new AspectList();
    }
}
