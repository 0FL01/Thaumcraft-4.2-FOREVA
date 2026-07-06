package thaumcraft.api.crafting;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IInfusionStabiliser {
    public boolean canStabaliseInfusion(World var1, int var2, int var3, int var4);

    default boolean canStabaliseInfusion(World world, BlockPos pos) {
        return canStabaliseInfusion(world, pos.getX(), pos.getY(), pos.getZ());
    }
}
