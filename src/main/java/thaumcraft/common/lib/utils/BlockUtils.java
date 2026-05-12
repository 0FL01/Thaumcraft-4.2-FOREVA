
package thaumcraft.common.lib.utils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockUtils {

    public static boolean isBlockBreakable(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock().getBlockHardness(state, world, pos) >= 0.0f;
    }

    public static void setBlock(World world, BlockPos pos, IBlockState state) {
        world.setBlockState(pos, state, 3);
    }

    /** Count how many sides of a block are exposed to air. */
    public static int countExposedSides(World world, int x, int y, int z) {
        int count = 0;
        BlockPos pos = new BlockPos(x, y, z);
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (world.isAirBlock(pos.offset(facing))) count++;
        }
        return count;
    }

    /** Check if a block has at least `count` adjacent blocks of the given type. */
    public static boolean isBlockAdjacentToAtleast(IBlockAccess world, int x, int y, int z, Block block, int maxMeta, int count) {
        int found = 0;
        BlockPos pos = new BlockPos(x, y, z);
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (world.getBlockState(pos.offset(facing)).getBlock() == block) {
                found++;
                if (found >= count) return true;
            }
        }
        return false;
    }

    /** Check if a block position is adjacent to any solid (full-cube) block. */
    public static boolean isAdjacentToSolidBlock(World world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            IBlockState state = world.getBlockState(pos.offset(facing));
            if (state.isFullBlock()) return true;
        }
        return false;
    }

    /** Break the furthest connected block of the given type from the start position. Used by lumber/harvest golems. */
    public static boolean breakFurthestBlock(World world, BlockPos pos, net.minecraft.entity.player.EntityPlayer player) {
        // Simplified: break the block at the given position
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock().isAir(state, world, pos)) return false;
        if (player != null && !world.isRemote) {
            state.getBlock().harvestBlock(world, player, pos, state, world.getTileEntity(pos), net.minecraft.item.ItemStack.EMPTY);
            world.setBlockToAir(pos);
            return true;
        }
        if (!world.isRemote) {
            world.destroyBlock(pos, true);
            return true;
        }
        return false;
    }
}
