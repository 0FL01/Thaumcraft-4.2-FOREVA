package thaumcraft.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thaumcraft.api.IRepairable;

public interface IRepairableExtended
extends IRepairable {
    public boolean doRepair(ItemStack var1, EntityPlayer var2, int var3);
}

