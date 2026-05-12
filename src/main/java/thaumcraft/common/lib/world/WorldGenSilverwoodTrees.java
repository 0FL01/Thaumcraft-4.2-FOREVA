package thaumcraft.common.lib.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import thaumcraft.common.config.ConfigBlocks;
import java.util.Random;

public class WorldGenSilverwoodTrees extends WorldGenAbstractTree {
    private int minHeight;
    private int extraHeight;

    public WorldGenSilverwoodTrees(boolean notify) {
        super(notify);
        this.minHeight = 8;
        this.extraHeight = 5;
    }

    public WorldGenSilverwoodTrees(boolean notify, int minHeight, int extraHeight) {
        super(notify);
        this.minHeight = minHeight;
        this.extraHeight = extraHeight;
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        int height = this.minHeight + rand.nextInt(this.extraHeight);
        boolean flag = true;

        if (pos.getY() < 1 || pos.getY() + height + 1 > 256) return false;

        // Check space and ground
        for (int y = pos.getY(); y <= pos.getY() + height; y++) {
            int radius = 2;
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

        this.setDirtAt(world, pos.down());

        IBlockState log = ConfigBlocks.blockMagicalLog.getStateFromMeta(1);
        IBlockState leaves = ConfigBlocks.blockMagicalLeaves.getStateFromMeta(1);
        IBlockState nodeLog = ConfigBlocks.blockMagicalLog.getStateFromMeta(2 | 8); // silverwood knot

        // Trunk
        for (int y = 0; y < height; y++) {
            BlockPos bp = pos.add(0, y, 0);
            this.setBlockAndNotifyAdequately(world, bp, log);
        }

        // Node at top
        BlockPos nodePos = pos.add(0, height - 1, 0);
        world.setBlockState(nodePos, nodeLog, 2);

        // Canopy (layered)
        int topY = pos.getY() + height;
        for (int layer = 0; layer < 4; layer++) {
            int layerY = topY - layer * 2;
            int radius = 4 - layer;
            if (radius < 2) radius = 2;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dz * dz <= radius * radius) {
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
