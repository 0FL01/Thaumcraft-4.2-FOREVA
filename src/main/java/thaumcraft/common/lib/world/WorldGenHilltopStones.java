package thaumcraft.common.lib.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thaumcraft.common.config.ConfigBlocks;
import java.util.Random;

public class WorldGenHilltopStones extends WorldGenerator {
    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        int radius = 3 + rand.nextInt(3);

        // Circle of stone blocks
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int dist = dx * dx + dz * dz;
                if (dist >= radius * radius - 3 && dist <= radius * radius + 3) {
                    BlockPos bp = pos.add(dx, 0, dz);
                    world.setBlockState(bp, Blocks.STONEBRICK.getDefaultState(), 2);
                }
            }
        }

        // Chest in center
        BlockPos chestPos = pos.add(0, 1, 0);
        if (world.isAirBlock(chestPos)) {
            world.setBlockState(chestPos, Blocks.CHEST.getDefaultState(), 2);
        }

        return true;
    }
}
