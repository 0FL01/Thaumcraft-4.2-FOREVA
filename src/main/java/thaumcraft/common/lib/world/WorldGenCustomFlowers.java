package thaumcraft.common.lib.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thaumcraft.common.config.ConfigBlocks;
import java.util.Random;

public class WorldGenCustomFlowers extends WorldGenerator {
    private IBlockState flower;

    public WorldGenCustomFlowers(IBlockState flower) {
        this.flower = flower;
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        for (int i = 0; i < 64; i++) {
            BlockPos bp = pos.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
            if (world.isAirBlock(bp) && bp.getY() < 255) {
                IBlockState state = world.getBlockState(bp.down());
                if (state.getBlock().canSustainPlant(state, world, bp.down(), net.minecraft.util.EnumFacing.UP, (net.minecraft.block.BlockSapling)null)) {
                    world.setBlockState(bp, this.flower, 2);
                }
            }
        }
        return true;
    }
}
