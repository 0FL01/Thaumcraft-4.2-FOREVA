package thaumcraft.common.lib.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.IPlantable;

public class WorldGenBigMagicTree extends WorldGenAbstractTree {
    private Random rand = new Random();
    private World world;
    private BlockPos basePos = BlockPos.ORIGIN;
    private int heightLimit;
    private int height;
    private double heightAttenuation = 0.6618;
    private double branchSlope = 0.381;
    private double scaleWidth = 1.0;
    private double leafDensity = 1.0;
    private int heightLimitLimit = 12;
    private int leafDistanceLimit = 3;
    private List<FoliageCoordinates> leafNodes = new ArrayList<>();

    public WorldGenBigMagicTree(boolean notify) {
        super(notify);
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        this.world = world;
        this.rand.setSeed(rand.nextLong());
        this.basePos = pos;
        this.leafNodes.clear();
        if (this.heightLimit == 0) {
            this.heightLimit = 11 + this.rand.nextInt(this.heightLimitLimit);
        }
        if (!this.validTreeLocation()) {
            return false;
        }
        this.generateLeafNodeList();
        this.generateLeaves();
        this.generateTrunk();
        this.generateLeafNodeBases();
        return true;
    }

    private void generateLeafNodeList() {
        this.height = (int)((double)this.heightLimit * this.heightAttenuation);
        if (this.height >= this.heightLimit) {
            this.height = this.heightLimit - 1;
        }
        int leafNodeCount = (int)(1.382 + Math.pow(this.leafDensity * (double)this.heightLimit / 13.0, 2.0));
        if (leafNodeCount < 1) {
            leafNodeCount = 1;
        }
        int y = this.basePos.getY() + this.heightLimit - this.leafDistanceLimit;
        int branchY = this.basePos.getY() + this.height;
        int relativeY = y - this.basePos.getY();
        this.leafNodes.add(new FoliageCoordinates(new BlockPos(this.basePos.getX(), y--, this.basePos.getZ()), branchY));

        while (relativeY >= 0) {
            float layerSize = this.layerSize(relativeY);
            if (layerSize < 0.0f) {
                --y;
                --relativeY;
                continue;
            }
            for (int node = 0; node < leafNodeCount; ++node) {
                double distance = this.scaleWidth * (double)layerSize * ((double)this.rand.nextFloat() + 0.328);
                double angle = (double)this.rand.nextFloat() * 2.0 * Math.PI;
                int x = MathHelper.floor(distance * Math.sin(angle) + (double)this.basePos.getX() + 0.5);
                int z = MathHelper.floor(distance * Math.cos(angle) + (double)this.basePos.getZ() + 0.5);
                BlockPos leafPos = new BlockPos(x, y, z);
                if (this.checkBlockLine(leafPos, leafPos.up(this.leafDistanceLimit)) != -1) {
                    continue;
                }
                double horizontal = Math.sqrt(Math.pow(Math.abs(this.basePos.getX() - x), 2.0) + Math.pow(Math.abs(this.basePos.getZ() - z), 2.0));
                double branchDrop = horizontal * this.branchSlope;
                int leafBranchY = (double)leafPos.getY() - branchDrop > (double)branchY ? branchY : (int)((double)leafPos.getY() - branchDrop);
                BlockPos branchBase = new BlockPos(this.basePos.getX(), leafBranchY, this.basePos.getZ());
                if (this.checkBlockLine(branchBase, leafPos) != -1) {
                    continue;
                }
                this.leafNodes.add(new FoliageCoordinates(leafPos, leafBranchY));
            }
            --y;
            --relativeY;
        }
    }

    private void genTreeLayer(BlockPos pos, float size, IBlockState state) {
        int radius = (int)((double)size + 0.618);
        for (int x = -radius; x <= radius; ++x) {
            for (int z = -radius; z <= radius; ++z) {
                double distance = Math.pow((double)Math.abs(x) + 0.5, 2.0) + Math.pow((double)Math.abs(z) + 0.5, 2.0);
                if (distance > (double)(size * size)) {
                    continue;
                }
                BlockPos leafPos = pos.add(x, 0, z);
                try {
                    IBlockState target = this.world.getBlockState(leafPos);
                    Block block = target.getBlock();
                    if (block.isAir(target, this.world, leafPos) || block.isLeaves(target, this.world, leafPos)) {
                        this.setBlockAndNotifyAdequately(this.world, leafPos, state);
                    }
                }
                catch (Exception e) {
                    // silently skip — matching original 1.7.10 behavior
                }
            }
        }
    }

    private float layerSize(int y) {
        if ((double)y < (double)this.heightLimit * 0.3) {
            return -1.618f;
        }
        float halfHeight = (float)this.heightLimit / 2.0f;
        float offset = halfHeight - (float)y;
        float size = offset == 0.0f ? halfHeight : (Math.abs(offset) >= halfHeight ? 0.0f : (float)Math.sqrt(Math.pow(Math.abs(halfHeight), 2.0) - Math.pow(Math.abs(offset), 2.0)));
        return size * 0.5f;
    }

    private float leafSize(int y) {
        return y >= 0 && y < this.leafDistanceLimit ? (y != 0 && y != this.leafDistanceLimit - 1 ? 3.0f : 2.0f) : -1.0f;
    }

    private void generateLeafNode(BlockPos pos) {
        IBlockState leaves = Blocks.LEAVES.getDefaultState().withProperty(BlockLeaves.CHECK_DECAY, Boolean.FALSE);
        for (int y = 0; y < this.leafDistanceLimit; ++y) {
            float size = this.leafSize(y);
            this.genTreeLayer(pos.up(y), size, leaves);
        }
    }

    private void placeBlockLine(BlockPos start, BlockPos end, Block block) {
        BlockPos diff = end.add(-start.getX(), -start.getY(), -start.getZ());
        int max = this.getLongestAxis(diff);
        if (max == 0) {
            return;
        }
        float stepX = (float)diff.getX() / (float)max;
        float stepY = (float)diff.getY() / (float)max;
        float stepZ = (float)diff.getZ() / (float)max;
        for (int i = 0; i <= max; ++i) {
            BlockPos pos = new BlockPos(
                    MathHelper.floor((float)start.getX() + 0.5f + (float)i * stepX),
                    MathHelper.floor((float)start.getY() + 0.5f + (float)i * stepY),
                    MathHelper.floor((float)start.getZ() + 0.5f + (float)i * stepZ));
            BlockLog.EnumAxis axis = this.getLogAxis(start, pos);
            this.setBlockAndNotifyAdequately(this.world, pos, block.getDefaultState().withProperty(BlockLog.LOG_AXIS, axis));
        }
    }

    private int getLongestAxis(BlockPos diff) {
        int x = MathHelper.abs(diff.getX());
        int y = MathHelper.abs(diff.getY());
        int z = MathHelper.abs(diff.getZ());
        return Math.max(x, Math.max(y, z));
    }

    private BlockLog.EnumAxis getLogAxis(BlockPos start, BlockPos end) {
        BlockLog.EnumAxis axis = BlockLog.EnumAxis.Y;
        int x = MathHelper.abs(end.getX() - start.getX());
        int z = MathHelper.abs(end.getZ() - start.getZ());
        int max = Math.max(x, z);
        if (max > 0) {
            if (x == max) {
                axis = BlockLog.EnumAxis.X;
            } else if (z == max) {
                axis = BlockLog.EnumAxis.Z;
            }
        }
        return axis;
    }

    private void generateLeaves() {
        try {
            for (FoliageCoordinates node : this.leafNodes) {
                this.generateLeafNode(node.pos);
            }
        }
        catch (Exception e) {
            // silently skip — matching original 1.7.10 behavior
        }
    }

    private boolean leafNodeNeedsBase(int y) {
        return (double)y >= (double)this.heightLimit * 0.2;
    }

    private void generateTrunk() {
        this.placeBlockLine(this.basePos, this.basePos.up(this.height), Blocks.LOG);
    }

    private void generateLeafNodeBases() {
        for (FoliageCoordinates node : this.leafNodes) {
            BlockPos branchBase = new BlockPos(this.basePos.getX(), node.branchBase, this.basePos.getZ());
            int relativeY = node.branchBase - this.basePos.getY();
            if (this.leafNodeNeedsBase(relativeY)) {
                this.placeBlockLine(branchBase, node.pos, Blocks.LOG);
            }
        }
    }

    private int checkBlockLine(BlockPos start, BlockPos end) {
        BlockPos diff = end.add(-start.getX(), -start.getY(), -start.getZ());
        int max = this.getLongestAxis(diff);
        if (max == 0) {
            return -1;
        }
        float stepX = (float)diff.getX() / (float)max;
        float stepY = (float)diff.getY() / (float)max;
        float stepZ = (float)diff.getZ() / (float)max;
        int i = 0;
        try {
            for (i = 0; i <= max; ++i) {
                BlockPos pos = new BlockPos(
                        MathHelper.floor((float)start.getX() + (float)i * stepX),
                        MathHelper.floor((float)start.getY() + (float)i * stepY),
                        MathHelper.floor((float)start.getZ() + (float)i * stepZ));
                if (!this.isReplaceable(this.world, pos)) {
                    return i;
                }
            }
        }
        catch (Exception e) {
            // silently skip — matching original 1.7.10 behavior
        }
        return i == (max + 1) ? -1 : Math.abs(i);
    }

    private boolean validTreeLocation() {
        BlockPos soilPos = this.basePos.down();
        IBlockState soil = this.world.getBlockState(soilPos);
        boolean validSoil = soil.getBlock().canSustainPlant(soil, this.world, soilPos, EnumFacing.UP, (IPlantable)((BlockSapling)Blocks.SAPLING));
        if (!validSoil) {
            return false;
        }
        int obstruction = this.checkBlockLine(this.basePos, this.basePos.up(this.heightLimit - 1));
        if (obstruction == -1) {
            return true;
        }
        if (obstruction < 6) {
            return false;
        }
        this.heightLimit = obstruction;
        return true;
    }

    private static class FoliageCoordinates {
        private final BlockPos pos;
        private final int branchBase;

        private FoliageCoordinates(BlockPos pos, int branchBase) {
            this.pos = pos;
            this.branchBase = branchBase;
        }
    }
}
