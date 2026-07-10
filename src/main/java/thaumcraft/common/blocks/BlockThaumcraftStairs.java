package thaumcraft.common.blocks;

import java.util.Random;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Stairs backed by legacy metadata blocks whose display ticks cannot accept a
 * stair state container.
 */
public abstract class BlockThaumcraftStairs extends BlockStairs {

    protected BlockThaumcraftStairs(IBlockState modelState) {
        super(modelState);
    }

    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        // BlockStairs delegates this with the stair state. Legacy TC blocks
        // expect their own TYPE property and would crash while reading it.
    }
}
