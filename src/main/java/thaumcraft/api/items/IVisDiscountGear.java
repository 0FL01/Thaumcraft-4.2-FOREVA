package thaumcraft.api.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;

/**
 * Thaumcraft 6 compatibility interface for addons compiled against the 1.12 API.
 */
public interface IVisDiscountGear extends thaumcraft.api.IVisDiscountGear {
    int getVisDiscount(ItemStack stack, EntityPlayer player);

    @Override
    default int getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect) {
        return getVisDiscount(stack, player);
    }
}
