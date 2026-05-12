package thaumcraft.common.lib.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thaumcraft.common.config.ConfigBlocks;
import java.util.Random;

public class WorldGenMound extends WorldGenerator {
    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        int radius = 4 + rand.nextInt(4);
        int height = 2 + rand.nextInt(3);

        // Generate mound shape
        for (int y = 0; y <= height; y++) {
            int r = (int)(radius * (1.0 - (double)y / (height + 1)));
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (dx * dx + dz * dz <= r * r) {
                        BlockPos bp = pos.add(dx, y, dz);
                        if (y == 0) {
                            world.setBlockState(bp, Blocks.GRASS.getDefaultState(), 2);
                        } else {
                            world.setBlockState(bp, Blocks.DIRT.getDefaultState(), 2);
                        }
                    }
                }
            }
        }

        // Place chest inside
        BlockPos chestPos = pos.add(0, 1, 0);
        world.setBlockState(chestPos, Blocks.CHEST.getDefaultState(), 2);

        return true;
    }
}
