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

        WorldGenMoundTemplate.place(world, pos);
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
