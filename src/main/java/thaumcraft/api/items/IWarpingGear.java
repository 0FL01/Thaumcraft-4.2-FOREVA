package thaumcraft.api.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IWarpingGear {
    int getWarp(ItemStack stack, EntityPlayer player);
}
