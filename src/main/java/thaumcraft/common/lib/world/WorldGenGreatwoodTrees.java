package thaumcraft.common.lib.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import java.util.Random;

public class WorldGenGreatwoodTrees extends WorldGenAbstractTree {
    public WorldGenGreatwoodTrees(boolean notify) {
        super(notify);
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        int height = 12 + rand.nextInt(8);
        boolean flag = true;

        if (pos.getY() < 1 || pos.getY() + height + 1 > 256) return false;

        // Check space and ground
        for (int y = pos.getY(); y <= pos.getY() + height; y++) {
            int radius = y - pos.getY() < 4 ? 2 : 1;
            for (int x = pos.getX() - radius; x <= pos.getX() + radius && flag; x++) {
                for (int z = pos.getZ() - radius; z <= pos.getZ() + radius && flag; z++) {
                    if (y < 0 || y >= 256 || !this.canGrowInto(world.getBlockState(new BlockPos(x, y, z)).getBlock())) {
                        flag = false;
                    }
                }
            }
        }

        if (!flag) return false;

        Block soil = world.getBlockState(pos.down()).getBlock();
        if (soil != Blocks.GRASS && soil != Blocks.DIRT && soil != Blocks.FARMLAND) return false;

        // Set soil to dirt
        this.setDirtAt(world, pos.down());

        // Generate trunk and canopy
        IBlockState log = ConfigBlocks.blockMagicalLog.getStateFromMeta(0);
        IBlockState leaves = ConfigBlocks.blockMagicalLeaves.getStateFromMeta(0);

        // Trunk (3-wide base, tapering)
        int trunkBaseRadius = 2;
        for (int y = 0; y < height; y++) {
            int radius = y < 3 ? trunkBaseRadius : 1;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx == 0 && dz == 0 || (y < 3 && Math.abs(dx) <= 1 && Math.abs(dz) <= 1)) {
                        BlockPos bp = pos.add(dx, y, dz);
                        this.setBlockAndNotifyAdequately(world, bp, log);
                    }
                }
            }
        }

        // Canopy (large oval)
        int canopyStart = height - 6;
        for (int y = canopyStart; y <= height; y++) {
            int radius = (int)(5.0 - (y - canopyStart) * 0.8);
            if (radius < 1) radius = 1;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) == radius && Math.abs(dz) == radius && rand.nextInt(2) == 0) continue;
                    if (dx * dx + dz * dz <= radius * radius) {
                        BlockPos bp = pos.add(dx, y, dz);
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
