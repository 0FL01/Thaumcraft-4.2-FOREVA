package thaumcraft.common.lib.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.storage.loot.LootTableList;
import thaumcraft.common.config.ConfigBlocks;

import java.util.Random;

public class WorldGenHilltopStones extends WorldGenerator {
    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        if (!this.LocationIsValidSpawn(world, pos.add(-2, 0, -2))
                || !this.LocationIsValidSpawn(world, pos)
                || !this.LocationIsValidSpawn(world, pos.add(2, 0, 0))
                || !this.LocationIsValidSpawn(world, pos.add(2, 0, 2))
                || !this.LocationIsValidSpawn(world, pos.add(0, 0, 2))) {
            return false;
        }

        int y = getSurfaceY(world, pos);
        BlockPos center = new BlockPos(pos.getX(), y, pos.getZ());
        IBlockState replaceState = world.getBiome(center).topBlock;
        boolean genVines = !world.getBiome(center).isHighHumidity();

        for (int x = center.getX() - 3; x <= center.getX() + 3; x++) {
            for (int z = center.getZ() - 3; z <= center.getZ() + 3; z++) {
                if ((x == center.getX() - 3 || x == center.getX() + 3)
                        && (z == center.getZ() - 3 || z == center.getZ() + 3)) {
                    continue;
                }

                BlockPos floor = new BlockPos(x, y, z);
                world.setBlockState(floor, rand.nextBoolean()
                        ? ConfigBlocks.blockCosmeticSolid.getStateFromMeta(1)
                        : Blocks.OBSIDIAN.getDefaultState(), 3);

                boolean stop = false;
                for (int dy = 1; dy < 5; dy++) {
                    BlockPos below = floor.down(dy);
                    if (below.getY() >= 0 && shouldBackfill(world.getBlockState(below).getBlock())) {
                        world.setBlockState(below, replaceState, 3);
                    }

                    if (x == center.getX() && z == center.getZ() && dy == 1) {
                        world.setBlockState(floor.up(dy), ConfigBlocks.blockCosmeticSolid.getStateFromMeta(1), 3);
                        placeLootChest(world, rand, floor.up(dy + 1));
                        placeWispSpawner(world, floor.up(dy - 1));
                    }

                    if (stop || !isColumnPosition(center, x, z)) {
                        continue;
                    }

                    BlockPos column = floor.up(dy);
                    world.setBlockState(column, ConfigBlocks.blockCosmeticSolid.getStateFromMeta(0), 3);
                    if (dy < 2 || !rand.nextBoolean()) {
                        continue;
                    }
                    stop = true;
                    if (!genVines) {
                        continue;
                    }

                    if (rand.nextInt(3) == 0 && world.isAirBlock(column.west())) {
                        growVines(world, column.west(), EnumFacing.EAST);
                    }
                    if (rand.nextInt(3) == 0 && world.isAirBlock(column.east())) {
                        growVines(world, column.east(), EnumFacing.WEST);
                    }
                    if (rand.nextInt(3) == 0 && world.isAirBlock(column.north())) {
                        growVines(world, column.north(), EnumFacing.SOUTH);
                    }
                    if (rand.nextInt(3) == 0 && world.isAirBlock(column.south())) {
                        growVines(world, column.south(), EnumFacing.NORTH);
                    }
                }
            }
        }

        return true;
    }

    protected Block[] GetValidSpawnBlocks() {
        return new Block[]{Blocks.STONE, Blocks.GRASS, Blocks.DIRT};
    }

    public boolean LocationIsValidSpawn(World world, BlockPos pos) {
        if (pos.getY() < 85) {
            return false;
        }

        int distanceToAir = 0;
        BlockPos checkPos = pos;
        while (!world.isAirBlock(checkPos)) {
            checkPos = pos.up(++distanceToAir);
            if (checkPos.getY() >= world.getHeight()) {
                return false;
            }
        }
        if (distanceToAir > 2) {
            return false;
        }

        BlockPos surface = pos.up(distanceToAir - 1);
        Block block = world.getBlockState(surface).getBlock();
        Block above = world.getBlockState(surface.up()).getBlock();
        Block below = world.getBlockState(surface.down()).getBlock();

        if (above != Blocks.AIR) {
            return false;
        }
        for (Block valid : this.GetValidSpawnBlocks()) {
            if (block == valid || (isSurfaceCover(block) && below == valid)) {
                return true;
            }
        }
        return false;
    }

    private int getSurfaceY(World world, BlockPos pos) {
        int distanceToAir = 0;
        BlockPos checkPos = pos;
        while (!world.isAirBlock(checkPos) && checkPos.getY() < world.getHeight()) {
            checkPos = pos.up(++distanceToAir);
        }
        return pos.getY() + distanceToAir - 1;
    }

    private static boolean isSurfaceCover(Block block) {
        return block == Blocks.TALLGRASS || block == Blocks.SNOW_LAYER;
    }

    private static boolean shouldBackfill(Block block) {
        return block == Blocks.AIR
                || block == Blocks.TALLGRASS
                || block == Blocks.RED_MUSHROOM
                || block == Blocks.BROWN_MUSHROOM
                || block == Blocks.SNOW_LAYER;
    }

    private static boolean isColumnPosition(BlockPos center, int x, int z) {
        return ((x == center.getX() - 3 || x == center.getX() + 3) && Math.abs((z - center.getZ()) % 2) == 1)
                || ((z == center.getZ() - 3 || z == center.getZ() + 3) && Math.abs((x - center.getX()) % 2) == 1);
    }

    private static void placeLootChest(World world, Random rand, BlockPos pos) {
        world.setBlockState(pos, Blocks.CHEST.getDefaultState(), 3);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityChest) {
            ((TileEntityChest) tile).setLootTable(LootTableList.CHESTS_SIMPLE_DUNGEON, rand.nextLong());
        }
    }

    private static void placeWispSpawner(World world, BlockPos pos) {
        world.setBlockState(pos, Blocks.MOB_SPAWNER.getDefaultState(), 3);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityMobSpawner) {
            ((TileEntityMobSpawner) tile).getSpawnerBaseLogic().setEntityId(new ResourceLocation("thaumcraft", "wisp"));
        }
    }

    private static void growVines(World world, BlockPos pos, EnumFacing attachedFace) {
        IBlockState state = Blocks.VINE.getDefaultState();
        switch (attachedFace) {
            case NORTH:
                state = state.withProperty(BlockVine.NORTH, true);
                break;
            case SOUTH:
                state = state.withProperty(BlockVine.SOUTH, true);
                break;
            case WEST:
                state = state.withProperty(BlockVine.WEST, true);
                break;
            case EAST:
                state = state.withProperty(BlockVine.EAST, true);
                break;
            default:
                return;
        }

        BlockPos cursor = pos;
        for (int i = 0; i < 5 && world.isAirBlock(cursor); i++) {
            world.setBlockState(cursor, state, 3);
            cursor = cursor.down();
        }
    }
}
