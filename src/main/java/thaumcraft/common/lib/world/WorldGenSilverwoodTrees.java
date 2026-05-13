package thaumcraft.common.lib.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.world.WorldGenCustomFlowers;

import java.util.Random;

public class WorldGenSilverwoodTrees extends WorldGenAbstractTree {
    private int minHeight;
    private int extraHeight;
    private boolean worldgen = false;

    public WorldGenSilverwoodTrees(boolean notify) {
        super(notify);
        this.worldgen = !notify;
        this.minHeight = 8;
        this.extraHeight = 5;
    }

    public WorldGenSilverwoodTrees(boolean notify, int minHeight, int extraHeight) {
        super(notify);
        this.worldgen = !notify;
        this.minHeight = minHeight;
        this.extraHeight = extraHeight;
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        int height = this.minHeight + rand.nextInt(this.extraHeight);
        boolean flag = true;

        if (pos.getY() < 1 || pos.getY() + height + 1 > 256) return false;
        if (this.worldgen && !world.isAreaLoaded(pos.add(-6, 0, -6), pos.add(6, height + 3, 6), false)) return false;

        // Check space and ground
        for (int y = pos.getY(); y <= pos.getY() + 1 + height; y++) {
            int radius = 1;
            if (y == pos.getY()) radius = 0;
            if (y >= pos.getY() + 1 + height - 2) radius = 3;
            for (int x = pos.getX() - radius; x <= pos.getX() + radius && flag; x++) {
                for (int z = pos.getZ() - radius; z <= pos.getZ() + radius && flag; z++) {
                    if (y < 0 || y >= 256) {
                        flag = false;
                    } else {
                        Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
                        if (!block.isAir(world.getBlockState(new BlockPos(x, y, z)), world, new BlockPos(x, y, z))
                                && !block.isLeaves(world.getBlockState(new BlockPos(x, y, z)), world, new BlockPos(x, y, z))
                                && !block.isReplaceable(world, new BlockPos(x, y, z))
                                && y != pos.getY()) {
                            flag = false;
                        }
                    }
                }
            }
        }

        if (!flag) return false;

        Block soil = world.getBlockState(pos.down()).getBlock();
        if (soil != Blocks.GRASS && soil != Blocks.DIRT && soil != Blocks.FARMLAND) return false;

        this.setDirtAt(world, pos.down());

        IBlockState log = ConfigBlocks.blockMagicalLog.getStateFromMeta(1);
        IBlockState knot = ConfigBlocks.blockMagicalLog.getStateFromMeta(2);
        IBlockState leaves = ConfigBlocks.blockMagicalLeaves.getStateFromMeta(1);

        // Trunk (4-wide cross pattern matching original)
        for (int dy = 0; dy < height; dy++) {
            BlockPos bp = pos.add(0, dy, 0);
            this.setBlockAndNotifyAdequately(world, bp, log);
            this.setBlockAndNotifyAdequately(world, bp.add(-1, 0, 0), log);
            this.setBlockAndNotifyAdequately(world, bp.add(1, 0, 0), log);
            this.setBlockAndNotifyAdequately(world, bp.add(0, 0, -1), log);
            this.setBlockAndNotifyAdequately(world, bp.add(0, 0, 1), log);

            // Random chance to embed a node in the trunk (silverwood knot)
            if (dy > 0 && dy < height - 1 && rand.nextInt((int)(height * 1.5)) == 0) {
                world.setBlockState(bp, knot, 2);
                ThaumcraftWorldGenerator.createRandomNodeAt(world, bp, rand, true, false, false);
            }
        }

        // Top log
        BlockPos topPos = pos.add(0, height, 0);
        this.setBlockAndNotifyAdequately(world, topPos, log);

        // Spherical canopy with variable radius
        int topY = pos.getY() + height;
        for (int dy = -3; dy <= 3; dy++) {
            int layerY = topY + dy;
            int radius;
            if (dy < -2 || dy > 2) radius = 3;
            else if (dy < -1 || dy > 1) radius = 4;
            else radius = 5;

            // Apply slight randomness to radius per layer
            radius += rand.nextInt(2);

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    double dist = dx * dx + dz * dz + dy * dy * 0.5;
                    if (dist <= radius * radius) {
                        BlockPos bp = new BlockPos(pos.getX() + dx, layerY, pos.getZ() + dz);
                        if (world.isAirBlock(bp) || world.getBlockState(bp).getBlock().isReplaceable(world, bp)) {
                            this.setBlockAndNotifyAdequately(world, bp, leaves);
                        }
                    }
                }
            }
        }

        // Generate silverwood saplings below (worldgen only, not from saplings)
        if (this.worldgen) {
            WorldGenCustomFlowers flowers = new WorldGenCustomFlowers(ConfigBlocks.blockCustomPlant.getStateFromMeta(2));
            flowers.generate(world, rand, pos);
        }

        return true;
    }
}
