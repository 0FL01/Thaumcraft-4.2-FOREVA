package thaumcraft.api.aspects;

import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;

public class AspectHelper {

    public static AspectList getObjectAspects(ItemStack stack) {
        return ThaumcraftApi.internalMethods.getObjectAspects(stack);
    }
}
