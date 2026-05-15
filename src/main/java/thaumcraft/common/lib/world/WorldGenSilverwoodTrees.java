package thaumcraft.common.lib.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.IPlantable;
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

        IBlockState soilState = world.getBlockState(pos.down());
        if (!soilState.getBlock().canSustainPlant(soilState, world, pos.down(), EnumFacing.UP, (IPlantable) Blocks.SAPLING)) return false;
        if (pos.getY() >= 256 - height - 1) return false;
        soilState.getBlock().onPlantGrow(soilState, world, pos.down(), pos);

        IBlockState log = ConfigBlocks.blockMagicalLog.getStateFromMeta(1);
        IBlockState knot = ConfigBlocks.blockMagicalLog.getStateFromMeta(2);
        IBlockState leaves = ConfigBlocks.blockMagicalLeaves.getStateFromMeta(1);

        int start = pos.getY() + height - 5;
        int end = pos.getY() + height + 3 + rand.nextInt(3);
        for (int y = start; y <= end; y++) {
            int cty = Math.min(Math.max(y, pos.getY() + height - 3), pos.getY() + height);
            for (int x = pos.getX() - 5; x <= pos.getX() + 5; x++) {
                for (int z = pos.getZ() - 5; z <= pos.getZ() + 5; z++) {
                    double d3 = x - pos.getX();
                    double d4 = y - cty;
                    double d5 = z - pos.getZ();
                    double dist = d3 * d3 + d4 * d4 + d5 * d5;
                    BlockPos lp = new BlockPos(x, y, z);
                    IBlockState ls = world.getBlockState(lp);
                    if (dist < 10 + rand.nextInt(8) && ls.getBlock().canBeReplacedByLeaves(ls, world, lp)) {
                        this.setBlockAndNotifyAdequately(world, lp, leaves);
                    }
                }
            }
        }

        int chance = (int) (height * 1.5);
        boolean lastblock = false;
        int dy;
        for (dy = 0; dy < height; dy++) {
            BlockPos bp = pos.add(0, dy, 0);
            IBlockState trunkState = world.getBlockState(bp);
            Block trunkBlock = trunkState.getBlock();
            if (trunkBlock.isAir(trunkState, world, bp)
                    || trunkBlock.isLeaves(trunkState, world, bp)
                    || trunkBlock.isReplaceable(world, bp)) {
                if (dy > 0 && !lastblock && rand.nextInt(chance) == 0) {
                    this.setBlockAndNotifyAdequately(world, bp, knot);
                    ThaumcraftWorldGenerator.createRandomNodeAt(world, bp, rand, true, false, false);
                    chance += height;
                    lastblock = true;
                } else {
                    this.setBlockAndNotifyAdequately(world, bp, log);
                    lastblock = false;
                }
            }
            this.setBlockAndNotifyAdequately(world, bp.add(-1, 0, 0), log);
            this.setBlockAndNotifyAdequately(world, bp.add(1, 0, 0), log);
            this.setBlockAndNotifyAdequately(world, bp.add(0, 0, -1), log);
            this.setBlockAndNotifyAdequately(world, bp.add(0, 0, 1), log);
        }

        this.setBlockAndNotifyAdequately(world, pos.add(0, dy, 0), log);
        this.setBlockAndNotifyAdequately(world, pos.add(-1, 0, -1), log);
        this.setBlockAndNotifyAdequately(world, pos.add(1, 0, 1), log);
        this.setBlockAndNotifyAdequately(world, pos.add(-1, 0, 1), log);
        this.setBlockAndNotifyAdequately(world, pos.add(1, 0, -1), log);

        if (rand.nextInt(3) != 0) this.setBlockAndNotifyAdequately(world, pos.add(-1, 1, -1), log);
        if (rand.nextInt(3) != 0) this.setBlockAndNotifyAdequately(world, pos.add(1, 1, 1), log);
        if (rand.nextInt(3) != 0) this.setBlockAndNotifyAdequately(world, pos.add(-1, 1, 1), log);
        if (rand.nextInt(3) != 0) this.setBlockAndNotifyAdequately(world, pos.add(1, 1, -1), log);

        this.setBlockAndNotifyAdequately(world, pos.add(-2, 0, 0), ConfigBlocks.blockMagicalLog.getStateFromMeta(5));
        this.setBlockAndNotifyAdequately(world, pos.add(2, 0, 0), ConfigBlocks.blockMagicalLog.getStateFromMeta(5));
        this.setBlockAndNotifyAdequately(world, pos.add(0, 0, -2), ConfigBlocks.blockMagicalLog.getStateFromMeta(9));
        this.setBlockAndNotifyAdequately(world, pos.add(0, 0, 2), ConfigBlocks.blockMagicalLog.getStateFromMeta(9));
        this.setBlockAndNotifyAdequately(world, pos.add(-2, -1, 0), log);
        this.setBlockAndNotifyAdequately(world, pos.add(2, -1, 0), log);
        this.setBlockAndNotifyAdequately(world, pos.add(0, -1, -2), log);
        this.setBlockAndNotifyAdequately(world, pos.add(0, -1, 2), log);

        this.setBlockAndNotifyAdequately(world, pos.add(-1, height - 4, -1), log);
        this.setBlockAndNotifyAdequately(world, pos.add(1, height - 4, 1), log);
        this.setBlockAndNotifyAdequately(world, pos.add(-1, height - 4, 1), log);
        this.setBlockAndNotifyAdequately(world, pos.add(1, height - 4, -1), log);

        if (rand.nextInt(3) == 0) this.setBlockAndNotifyAdequately(world, pos.add(-1, height - 5, -1), log);
        if (rand.nextInt(3) == 0) this.setBlockAndNotifyAdequately(world, pos.add(1, height - 5, 1), log);
        if (rand.nextInt(3) == 0) this.setBlockAndNotifyAdequately(world, pos.add(-1, height - 5, 1), log);
        if (rand.nextInt(3) == 0) this.setBlockAndNotifyAdequately(world, pos.add(1, height - 5, -1), log);

        this.setBlockAndNotifyAdequately(world, pos.add(-2, height - 4, 0), ConfigBlocks.blockMagicalLog.getStateFromMeta(5));
        this.setBlockAndNotifyAdequately(world, pos.add(2, height - 4, 0), ConfigBlocks.blockMagicalLog.getStateFromMeta(5));
        this.setBlockAndNotifyAdequately(world, pos.add(0, height - 4, -2), ConfigBlocks.blockMagicalLog.getStateFromMeta(9));
        this.setBlockAndNotifyAdequately(world, pos.add(0, height - 4, 2), ConfigBlocks.blockMagicalLog.getStateFromMeta(9));

        if (this.worldgen) {
            WorldGenCustomFlowers flowers = new WorldGenCustomFlowers(ConfigBlocks.blockCustomPlant.getStateFromMeta(2));
            flowers.generate(world, rand, pos);
        }

        return true;
    }
}
