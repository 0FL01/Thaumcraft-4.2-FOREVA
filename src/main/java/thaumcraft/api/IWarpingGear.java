package thaumcraft.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IWarpingGear extends thaumcraft.api.items.IWarpingGear {
    @Override
    public int getWarp(ItemStack var1, EntityPlayer var2);
}
