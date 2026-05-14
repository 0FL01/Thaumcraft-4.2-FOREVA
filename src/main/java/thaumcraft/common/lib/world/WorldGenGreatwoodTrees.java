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
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.storage.loot.LootTableList;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;

import java.util.Random;

public class WorldGenGreatwoodTrees extends WorldGenAbstractTree {
    private final boolean worldgen;

    public WorldGenGreatwoodTrees(boolean notify) {
        super(notify);
        this.worldgen = !notify;
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        return this.generate(world, rand, pos, rand.nextInt(8) == 0);
    }

    public boolean generate(World world, Random rand, BlockPos pos, boolean spiders) {
        int height = 12 + rand.nextInt(8);
        boolean flag = true;

        if (pos.getY() < 1 || pos.getY() + height + 1 > 256) return false;
        if (this.worldgen && !world.isAreaLoaded(pos.add(-5, 0, -5), pos.add(5, height + 1, 5), false)) return false;

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

        if (spiders) {
            decorateSpiderTree(world, rand, pos);
        }
        return true;
    }

    private static void decorateSpiderTree(World world, Random rand, BlockPos pos) {
        BlockPos spawnerPos = pos.down();
        world.setBlockState(spawnerPos, Blocks.MOB_SPAWNER.getDefaultState(), 3);
        TileEntity spawnerTile = world.getTileEntity(spawnerPos);
        if (!(spawnerTile instanceof TileEntityMobSpawner)) {
            return;
        }

        ((TileEntityMobSpawner) spawnerTile).getSpawnerBaseLogic()
                .setEntityId(new ResourceLocation("minecraft", "cave_spider"));

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
