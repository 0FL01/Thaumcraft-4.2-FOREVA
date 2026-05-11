
package thaumcraft.common.lib.utils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockUtils {
    public static boolean isBlockBreakable(World world, BlockPos pos) {
        return true;
    }
    
    public static boolean setBlock(World world, BlockPos pos, IBlockState state) {
        return world.setBlockState(pos, state);
    }
}
