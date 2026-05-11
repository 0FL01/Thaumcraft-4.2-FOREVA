package thaumcraft.api.research;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.api.research.ScanResult;

public interface IScanEventHandler {
    public ScanResult scanPhenomena(ItemStack var1, World var2, EntityPlayer var3);
}

