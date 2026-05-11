package thaumcraft.api.wands;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IWandTriggerManager {
    public boolean performTrigger(World var1, ItemStack var2, EntityPlayer var3, int var4, int var5, int var6, int var7, int var8);
}

