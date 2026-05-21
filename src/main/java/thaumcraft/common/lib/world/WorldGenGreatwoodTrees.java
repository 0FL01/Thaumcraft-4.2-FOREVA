package thaumcraft.common.lib.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.storage.loot.LootTableList;
import thaumcraft.common.config.ConfigBlocks;

import java.util.Random;

public class WorldGenGreatwoodTrees extends WorldGenAbstractTree {
    static final byte[] otherCoordPairs = new byte[]{2, 0, 0, 1, 2, 1};
    Random rand = new Random();
    World worldObj;
    int[] basePos = new int[]{0, 0, 0};
    int heightLimit = 0;
    int height;
    double heightAttenuation = 0.618;
    double branchDensity = 1.0;
    double branchSlope = 0.38;
    double scaleWidth = 1.2;
    double leafDensity = 0.9;
    int trunkSize = 2;
    int heightLimitLimit = 11;
    int leafDistanceLimit = 4;
    int[][] leafNodes;
    private static final IBlockState GREATWOOD_LEAVES = ConfigBlocks.blockMagicalLeaves.getStateFromMeta(0);

    public WorldGenGreatwoodTrees(boolean notify) {
        super(notify);
    }

    void generateLeafNodeList() {
        this.height = (int)((double)this.heightLimit * this.heightAttenuation);
        if (this.height >= this.heightLimit) {
            this.height = this.heightLimit - 1;
        }

        int var1 = (int)(1.382 + Math.pow(this.leafDensity * (double)this.heightLimit / 13.0, 2.0));
        if (var1 < 1) {
            var1 = 1;
        }

        int[][] var2 = new int[var1 * this.heightLimit][4];
        int var3 = this.basePos[1] + this.heightLimit - this.leafDistanceLimit;
        int var4 = 1;
        int var5 = this.basePos[1] + this.height;
        int var6 = var3 - this.basePos[1];
        var2[0][0] = this.basePos[0];
        var2[0][1] = var3--;
        var2[0][2] = this.basePos[2];
        var2[0][3] = var5;

        while (var6 >= 0) {
            float var8 = this.layerSize(var6);
            if (var8 < 0.0f) {
                --var3;
                --var6;
                continue;
            }

            for (int var7 = 0; var7 < var1; ++var7) {
                double var11 = this.scaleWidth * (double)var8 * ((double)this.rand.nextFloat() + 0.328);
                double var13 = (double)this.rand.nextFloat() * 2.0 * Math.PI;
                int var15 = MathHelper.floor(var11 * Math.sin(var13) + (double)this.basePos[0] + 0.5);
                int var16 = MathHelper.floor(var11 * Math.cos(var13) + (double)this.basePos[2] + 0.5);
                int[] var17 = new int[]{var15, var3, var16};
                int[] var18 = new int[]{var15, var3 + this.leafDistanceLimit, var16};

                if (this.checkBlockLine(var17, var18) != -1) continue;

                int[] var19 = new int[]{this.basePos[0], this.basePos[1], this.basePos[2]};
                double var20 = Math.sqrt(Math.pow(Math.abs(this.basePos[0] - var17[0]), 2.0) + Math.pow(Math.abs(this.basePos[2] - var17[2]), 2.0));
                double var22 = var20 * this.branchSlope;
                int var23 = (int)((double)var17[1] - var22);
                var19[1] = var23 > var5 ? var5 : var23;

                if (this.checkBlockLine(var19, var17) != -1) continue;

                var2[var4][0] = var15;
                var2[var4][1] = var3;
                var2[var4][2] = var16;
                var2[var4][3] = var19[1];
                ++var4;
            }

            --var3;
            --var6;
        }

        this.leafNodes = new int[var4][4];
        System.arraycopy(var2, 0, this.leafNodes, 0, var4);
    }

    void genTreeLayer(int par1, int par2, int par3, float par4, byte par5) {
        int var7 = (int)((double)par4 + 0.618);
        byte var8 = otherCoordPairs[par5];
        byte var9 = otherCoordPairs[par5 + 3];
        int[] var10 = new int[]{par1, par2, par3};
        int[] var11 = new int[]{0, 0, 0};
        var11[par5] = var10[par5];

        for (int var12 = -var7; var12 <= var7; ++var12) {
            var11[var8] = var10[var8] + var12;
            for (int var13 = -var7; var13 <= var7; ++var13) {
                double var15 = Math.pow(Math.abs(var12) + 0.5, 2.0) + Math.pow(Math.abs(var13) + 0.5, 2.0);
                if (var15 > (double)(par4 * par4)) continue;

                try {
                    var11[var9] = var10[var9] + var13;
                    BlockPos pos = new BlockPos(var11[0], var11[1], var11[2]);
                    this.placeGreatwoodLeaf(pos);
                } catch (Exception ignored) {
                }
            }
        }
    }

    private boolean canReplaceForLeaves(BlockPos pos) {
        if (pos.getY() < 0 || pos.getY() >= this.worldObj.getHeight()) {
            return false;
        }
        IBlockState state = this.worldObj.getBlockState(pos);
        Block block = state.getBlock();
        return block.isAir(state, this.worldObj, pos)
                || block.isLeaves(state, this.worldObj, pos)
                || block.canBeReplacedByLeaves(state, this.worldObj, pos)
                || state.getMaterial().isReplaceable();
    }

    private void placeGreatwoodLeaf(BlockPos pos) {
        if (canReplaceForLeaves(pos)) {
            this.setBlockAndNotifyAdequately(this.worldObj, pos, GREATWOOD_LEAVES);
        }
    }

    float layerSize(int par1) {
        if ((double)par1 < (double)this.heightLimit * 0.3) {
            return -1.618f;
        }
        float var2 = (float)this.heightLimit / 2.0f;
        float var3 = (float)this.heightLimit / 2.0f - (float)par1;
        float var4;
        if (var3 == 0.0f) {
            var4 = var2;
        } else if (Math.abs(var3) >= var2) {
            var4 = 0.0f;
        } else {
            var4 = (float)Math.sqrt(Math.pow(Math.abs(var2), 2.0) - Math.pow(Math.abs(var3), 2.0));
        }
        return var4 *= 0.5f;
    }

    float leafSize(int par1) {
        return par1 >= 0 && par1 < this.leafDistanceLimit
                ? (par1 != 0 && par1 != this.leafDistanceLimit - 1 ? 3.0f : 2.0f)
                : -1.0f;
    }

    void generateLeafNode(int par1, int par2, int par3) {
        int var5 = par2 + this.leafDistanceLimit;
        for (int var4 = par2; var4 < var5; ++var4) {
            float var6 = this.leafSize(var4 - par2);
            this.genTreeLayer(par1, var4, par3, var6, (byte)1);
        }
    }

    void placeBlockLine(int[] par1ArrayOfInteger, int[] par2ArrayOfInteger) {
        int[] var4 = new int[]{0, 0, 0};
        int var6 = 0;

        for (int var5 = 0; var5 < 3; ++var5) {
            var4[var5] = par2ArrayOfInteger[var5] - par1ArrayOfInteger[var5];
            if (Math.abs(var4[var5]) > Math.abs(var4[var6])) {
                var6 = var5;
            }
        }

        if (var4[var6] != 0) {
            int var9 = var4[var6] > 0 ? 1 : -1;
            double var10 = (double)var4[otherCoordPairs[var6]] / (double)var4[var6];
            double var12 = (double)var4[otherCoordPairs[var6 + 3]] / (double)var4[var6];
            int[] var14 = new int[]{0, 0, 0};
            int var16 = var4[var6] + var9;

            for (int var15 = 0; var15 != var16; var15 += var9) {
                var14[var6] = MathHelper.floor((double)(par1ArrayOfInteger[var6] + var15) + 0.5);
                var14[otherCoordPairs[var6]] = MathHelper.floor((double)par1ArrayOfInteger[otherCoordPairs[var6]] + (double)var15 * var10 + 0.5);
                var14[otherCoordPairs[var6 + 3]] = MathHelper.floor((double)par1ArrayOfInteger[otherCoordPairs[var6 + 3]] + (double)var15 * var12 + 0.5);

                int var17 = 0;
                int var18 = Math.abs(var14[0] - par1ArrayOfInteger[0]);
                int var19 = Math.abs(var14[2] - par1ArrayOfInteger[2]);
                int var20 = Math.max(var18, var19);
                if (var20 > 0) {
                    if (var18 == var20) {
                        var17 = 4;
                    } else if (var19 == var20) {
                        var17 = 8;
                    }
                }

                this.setBlockAndNotifyAdequately(this.worldObj,
                        new BlockPos(var14[0], var14[1], var14[2]),
                        ConfigBlocks.blockMagicalLog.getStateFromMeta(var17));
            }
        }
    }

    void generateLeaves() {
        int var2 = this.leafNodes.length;
        for (int var1 = 0; var1 < var2; ++var1) {
            int var3 = this.leafNodes[var1][0];
            int var4 = this.leafNodes[var1][1];
            int var5 = this.leafNodes[var1][2];
            this.generateLeafNode(var3, var4, var5);
        }
    }

    boolean leafNodeNeedsBase(int par1) {
        return (double)par1 >= (double)this.heightLimit * 0.2;
    }

    void generateTrunk() {
        int var1 = this.basePos[0];
        int var2 = this.basePos[1];
        int var3 = this.basePos[1] + this.height;
        int var4 = this.basePos[2];
        int[] var5 = new int[]{var1, var2, var4};
        int[] var6 = new int[]{var1, var3, var4};
        this.placeBlockLine(var5, var6);

        if (this.trunkSize == 2) {
            var5[0] = var5[0] + 1;
            var6[0] = var6[0] + 1;
            this.placeBlockLine(var5, var6);

            var5[2] = var5[2] + 1;
            var6[2] = var6[2] + 1;
            this.placeBlockLine(var5, var6);

            var5[0] = var5[0] + -1;
            var6[0] = var6[0] + -1;
            this.placeBlockLine(var5, var6);
        }
    }

    void generateLeafNodeBases() {
        int var2 = this.leafNodes.length;
        int[] var3 = new int[]{this.basePos[0], this.basePos[1], this.basePos[2]};

        for (int var1 = 0; var1 < var2; ++var1) {
            int[] var4 = this.leafNodes[var1];
            int[] var5 = new int[]{var4[0], var4[1], var4[2]};
            var3[1] = var4[3];
            int var6 = var3[1] - this.basePos[1];
            if (this.leafNodeNeedsBase(var6)) {
                this.placeBlockLine(var3, var5);
            }
        }
    }

    int checkBlockLine(int[] par1ArrayOfInteger, int[] par2ArrayOfInteger) {
        int[] var3 = new int[]{0, 0, 0};
        int var5 = 0;

        for (int var4 = 0; var4 < 3; ++var4) {
            var3[var4] = par2ArrayOfInteger[var4] - par1ArrayOfInteger[var4];
            if (Math.abs(var3[var4]) > Math.abs(var3[var5])) {
                var5 = var4;
            }
        }

        if (var3[var5] == 0) {
            return -1;
        }

        int var8 = var3[var5] > 0 ? 1 : -1;
        double var9 = (double)var3[otherCoordPairs[var5]] / (double)var3[var5];
        double var11 = (double)var3[otherCoordPairs[var5 + 3]] / (double)var3[var5];
        int[] var13 = new int[]{0, 0, 0};
        int var15 = var3[var5] + var8;

        int var14;
        for (var14 = 0; var14 != var15; var14 += var8) {
            var13[var5] = par1ArrayOfInteger[var5] + var14;
            var13[otherCoordPairs[var5]] = MathHelper.floor((double)par1ArrayOfInteger[otherCoordPairs[var5]] + (double)var14 * var9);
            var13[otherCoordPairs[var5 + 3]] = MathHelper.floor((double)par1ArrayOfInteger[otherCoordPairs[var5 + 3]] + (double)var14 * var11);

            try {
                Block block = this.worldObj.getBlockState(new BlockPos(var13[0], var13[1], var13[2])).getBlock();
                if (block != Blocks.AIR && block != ConfigBlocks.blockMagicalLeaves) {
                    break;
                }
            } catch (Exception ignored) {
            }
        }

        return var14 == var15 ? -1 : Math.abs(var14);
    }

    boolean validTreeLocation(int x, int z) {
        int[] var1 = new int[]{this.basePos[0] + x, this.basePos[1], this.basePos[2] + z};
        int[] var2 = new int[]{this.basePos[0] + x, this.basePos[1] + this.heightLimit - 1, this.basePos[2] + z};

        try {
            BlockPos soilPos = new BlockPos(this.basePos[0] + x, this.basePos[1] - 1, this.basePos[2] + z);
            IBlockState soilState = this.worldObj.getBlockState(soilPos);
            Block soil = soilState.getBlock();
            boolean isSoil = soil.canSustainPlant(soilState, this.worldObj, soilPos, EnumFacing.UP, (BlockSapling) Blocks.SAPLING);

            if (!isSoil) {
                return false;
            }

            int var4 = this.checkBlockLine(var1, var2);
            if (var4 == -1) {
                return true;
            }
            if (var4 < 6) {
                return false;
            }
            this.heightLimit = var4;
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean generate(World worldIn, Random randIn, BlockPos position) {
        return this.generate(worldIn, randIn, position.getX(), position.getY(), position.getZ(), randIn.nextInt(8) == 0);
    }

    public boolean generate(World worldIn, Random randIn, int x, int y, int z, boolean spiders) {
        this.worldObj = worldIn;
        long var6 = randIn.nextLong();
        this.rand.setSeed(var6);
        this.basePos[0] = x;
        this.basePos[1] = y;
        this.basePos[2] = z;

        if (this.heightLimit == 0) {
            this.heightLimit = this.heightLimitLimit + this.rand.nextInt(this.heightLimitLimit);
        }

        boolean valid = false;
        int chosenA = 0, chosenB = 0;

        for (int a = -1; a < 2; ++a) {
            for (int b = -1; b < 2; ++b) {
                boolean allValid = true;
                for (int tx = 0; tx < this.trunkSize && allValid; ++tx) {
                    for (int tz = 0; tz < this.trunkSize && allValid; ++tz) {
                        if (!this.validTreeLocation(tx + a, tz + b)) {
                            allValid = false;
                        }
                    }
                }
                if (allValid) {
                    valid = true;
                    chosenA = a;
                    chosenB = b;
                    break;
                }
            }
            if (valid) break;
        }

        if (!valid) {
            return false;
        }

        this.basePos[0] += chosenA;
        this.basePos[2] += chosenB;

        // Save final base position for second pass alignment
        int finalBaseX = this.basePos[0];
        int finalBaseY = this.basePos[1];
        int finalBaseZ = this.basePos[2];

        // First pass
        this.generateLeafNodeList();
        this.generateLeaves();
        this.generateLeafNodeBases();
        this.generateTrunk();

        // Second pass with wider canopy — aligned to same base as first pass
        this.scaleWidth = 1.66;
        this.basePos[0] = finalBaseX;
        this.basePos[1] = finalBaseY + this.height;
        this.basePos[2] = finalBaseZ;

        this.generateLeafNodeList();
        this.generateLeaves();
        this.generateLeafNodeBases();
        this.generateTrunk();

        if (spiders) {
            this.decorateSpiderTree(worldIn, randIn, new BlockPos(x, y, z));
        }

        return true;
    }

    private void decorateSpiderTree(World world, Random rand, BlockPos pos) {
        BlockPos spawnerPos = pos.down();
        world.setBlockState(spawnerPos, Blocks.MOB_SPAWNER.getDefaultState(), 3);
        TileEntity spawnerTile = world.getTileEntity(spawnerPos);
        if (spawnerTile instanceof TileEntityMobSpawner) {
            ((TileEntityMobSpawner) spawnerTile).getSpawnerBaseLogic()
                    .setEntityId(new ResourceLocation("minecraft", "cave_spider"));
        }

        for (int i = 0; i < 50; i++) {
            BlockPos webPos = pos.add(-7 + rand.nextInt(14), rand.nextInt(10), -7 + rand.nextInt(14));
            if (!world.isAirBlock(webPos) || !isTouchingGreatwood(world, webPos)) {
                continue;
            }
            world.setBlockState(webPos, Blocks.WEB.getDefaultState(), 3);
        }

        BlockPos chestPos = pos.down(2);
        world.setBlockState(chestPos, Blocks.CHEST.getDefaultState(), 3);
        TileEntity chestTile = world.getTileEntity(chestPos);
        if (chestTile instanceof TileEntityChest) {
            ((TileEntityChest) chestTile).setLootTable(LootTableList.CHESTS_SIMPLE_DUNGEON, rand.nextLong());
        }
    }

    private static boolean isTouchingGreatwood(World world, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            Block block = world.getBlockState(pos.offset(facing)).getBlock();
            if (block == ConfigBlocks.blockMagicalLeaves || block == ConfigBlocks.blockMagicalLog) {
                return true;
            }
        }
        return false;
    }
}
