package thaumcraft.api;

import java.util.ArrayList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.api.BlockCoordinates;

public interface IArchitect {
    public ArrayList<BlockCoordinates> getArchitectBlocks(ItemStack var1, World var2, int var3, int var4, int var5, int var6, EntityPlayer var7);

    public boolean showAxis(ItemStack var1, World var2, EntityPlayer var3, int var4, EnumAxis var5);

    public static enum EnumAxis {
        X,
        Y,
        Z;

    }
}

