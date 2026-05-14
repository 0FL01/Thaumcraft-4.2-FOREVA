package thaumcraft.common.lib.world;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.storage.loot.LootTableList;
import thaumcraft.common.config.ConfigBlocks;

public class WorldGenMound extends WorldGenerator {

    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        if (!this.LocationIsValidSpawn(world, pos.add(9, 9, 9))
                || !this.LocationIsValidSpawn(world, pos.add(0, 9, 0))
                || !this.LocationIsValidSpawn(world, pos.add(18, 9, 0))
                || !this.LocationIsValidSpawn(world, pos.add(18, 9, 18))
                || !this.LocationIsValidSpawn(world, pos.add(0, 9, 18))) {
            return false;
        }

        buildBarrowShell(world, rand, pos);
        buildInterior(world, rand, pos);
        placeLoot(world, rand, pos);
        placeSpawner(world, pos.add(4, 5, 4), new ResourceLocation("minecraft", "skeleton"));
        placeSpawner(world, pos.add(4, 5, 14), new ResourceLocation("minecraft", "zombie"));
        return true;
    }

    protected Block[] GetValidSpawnBlocks() {
        return new Block[]{Blocks.STONE, Blocks.GRASS, Blocks.DIRT};
    }

    public boolean LocationIsValidSpawn(World world, BlockPos pos) {
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

    private static void buildBarrowShell(World world, Random rand, BlockPos origin) {
        BlockPos center = origin.add(9, 0, 9);
        for (int dx = 0; dx <= 18; dx++) {
            for (int dz = 0; dz <= 18; dz++) {
                double distance = Math.sqrt(Math.pow(dx - 9, 2) + Math.pow(dz - 9, 2));
                if (distance > 9.5) {
                    continue;
                }

                int top = 8 + Math.max(0, (int) Math.floor((9.5 - distance) / 2.0));
                for (int dy = 0; dy <= top; dy++) {
                    BlockPos at = origin.add(dx, dy, dz);
                    if (dy == top) {
                        world.setBlockState(at, Blocks.GRASS.getDefaultState(), 3);
                    } else if (dy >= 8) {
                        world.setBlockState(at, Blocks.DIRT.getDefaultState(), 3);
                    } else if (isRoomBoundary(dx, dy, dz)) {
                        world.setBlockState(at, rand.nextInt(4) == 0
                                ? Blocks.MOSSY_COBBLESTONE.getDefaultState()
                                : Blocks.COBBLESTONE.getDefaultState(), 3);
                    } else if (isInsideRoom(dx, dy, dz)) {
                        world.setBlockToAir(at);
                    } else {
                        world.setBlockState(at, Blocks.COBBLESTONE.getDefaultState(), 3);
                    }
                }
            }
        }

        for (int dy = 2; dy <= 9; dy++) {
            world.setBlockToAir(center.up(dy));
        }
    }

    private static void buildInterior(World world, Random rand, BlockPos origin) {
        for (int dx = 4; dx <= 13; dx++) {
            for (int dz = 4; dz <= 14; dz++) {
                for (int dy = 2; dy <= 6; dy++) {
                    world.setBlockToAir(origin.add(dx, dy, dz));
                }
            }
        }

        for (int dz = 7; dz <= 11; dz++) {
            world.setBlockToAir(origin.add(9, 1, dz));
            world.setBlockToAir(origin.add(10, 1, dz));
        }

        for (int dx = 5; dx <= 13; dx += 4) {
            for (int dz = 5; dz <= 13; dz += 4) {
                if (rand.nextBoolean()) {
                    world.setBlockState(origin.add(dx, 2, dz), Blocks.COBBLESTONE.getDefaultState(), 3);
                }
            }
        }
    }

    private static boolean isInsideRoom(int dx, int dy, int dz) {
        return dx >= 4 && dx <= 13 && dz >= 4 && dz <= 14 && dy >= 1 && dy <= 7;
    }

    private static boolean isRoomBoundary(int dx, int dy, int dz) {
        if (!isInsideRoom(dx, dy, dz)) {
            return false;
        }
        return dx == 4 || dx == 13 || dz == 4 || dz == 14 || dy == 1 || dy == 7;
    }

    private static void placeLoot(World world, Random rand, BlockPos origin) {
        placeLootContainer(world, rand, origin.add(9, 1, 7));
        placeLootContainer(world, rand, origin.add(9, 1, 11));

        BlockPos chestPos = origin.add(10, 1, 9);
        if (rand.nextInt(3) == 0) {
            world.setBlockState(chestPos, Blocks.TRAPPED_CHEST.getDefaultState(), 3);
            world.setBlockState(chestPos.down(2), Blocks.TNT.getDefaultState(), 3);
        } else {
            world.setBlockState(chestPos, Blocks.CHEST.getDefaultState(), 3);
        }

        TileEntity tile = world.getTileEntity(chestPos);
        if (tile instanceof TileEntityChest) {
            ((TileEntityChest) tile).setLootTable(LootTableList.CHESTS_SIMPLE_DUNGEON, rand.nextLong());
        }
    }

    private static void placeLootContainer(World world, Random rand, BlockPos pos) {
        float roll = rand.nextFloat();
        int meta = roll < 0.1f ? 2 : (roll < 0.33f ? 1 : 0);
        IBlockState state = (world.rand.nextFloat() < 0.3f ? ConfigBlocks.blockLootCrate : ConfigBlocks.blockLootUrn)
                .getStateFromMeta(meta);
        world.setBlockState(pos, state, 3);
    }

    private static void placeSpawner(World world, BlockPos pos, ResourceLocation entityId) {
        world.setBlockState(pos, Blocks.MOB_SPAWNER.getDefaultState(), 3);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityMobSpawner) {
            ((TileEntityMobSpawner) tile).getSpawnerBaseLogic().setEntityId(entityId);
        }
    }

    private static boolean isSurfaceCover(Block block) {
        return block == Blocks.TALLGRASS || block == Blocks.SNOW_LAYER;
    }
}
