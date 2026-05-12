package thaumcraft.common.lib.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import thaumcraft.common.config.ConfigBlocks;
import java.util.Random;

public class WorldGenBigMagicTree extends WorldGenAbstractTree {
    public WorldGenBigMagicTree(boolean notify) {
        super(notify);
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        int height = 20 + rand.nextInt(12);
        if (pos.getY() < 1 || pos.getY() + height + 1 > 256) return false;

        // Check ground
        BlockPos ground = pos.down();
        if (world.getBlockState(ground).getBlock() != Blocks.GRASS
                && world.getBlockState(ground).getBlock() != Blocks.DIRT) return false;

        this.setDirtAt(world, ground);

        IBlockState log = ConfigBlocks.blockMagicalLog.getStateFromMeta(0);
        IBlockState leaves = ConfigBlocks.blockMagicalLeaves.getStateFromMeta(0);

        // Giant trunk (3x3)
        for (int y = 0; y < height; y++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos bp = pos.add(dx, y, dz);
                    this.setBlockAndNotifyAdequately(world, bp, log);
                }
            }
        }

        // Huge canopy
        int topY = pos.getY() + height;
        for (int layer = 0; layer < 5; layer++) {
            int layerY = topY - layer;
            int radius = 7 - layer / 2;
            if (radius < 3) radius = 3;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    int dist = dx * dx + dz * dz;
                    if (dist <= radius * radius && dist > 4) {
                        BlockPos bp = new BlockPos(pos.getX() + dx, layerY, pos.getZ() + dz);
                        if (world.isAirBlock(bp)) {
                            this.setBlockAndNotifyAdequately(world, bp, leaves);
                        }
                    }
                }
            }
        }

        return true;
    }
}
