package thaumcraft.common.lib.world;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.world.dim.MazeHandler;
import thaumcraft.common.tiles.TileBanner;
import thaumcraft.common.tiles.TileEldritchAltar;
import java.util.Random;

public class WorldGenEldritchRing extends WorldGenerator {
    public int chunkX;
    public int chunkZ;
    public int width;
    public int height;

    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        if (world.provider.getDimension() != 0) return false;
        if (this.width <= 0) this.width = 11;
        if (this.height <= 0) this.height = 11;
        if (!isValidRingSpawn(world, pos.add(-3, 0, -3))
                || !isValidRingSpawn(world, pos)
                || !isValidRingSpawn(world, pos.add(3, 0, 0))
                || !isValidRingSpawn(world, pos.add(3, 0, 3))
                || !isValidRingSpawn(world, pos.add(0, 0, 3))
                || MazeHandler.mazesInRange(this.chunkX, this.chunkZ, this.width, this.height)) {
            return false;
        }

        for (int x = -3; x <= 3; ++x) {
            for (int z = -3; z <= 3; ++z) {
                if (isCorner(x, z)) continue;

                BlockPos column = pos.add(x, 0, z);
                for (int y = -4; y < 5; ++y) {
                    BlockPos target = column.up(y);
                    if (y <= 0) {
                        if (rand.nextInt(4) == 0) {
                            world.setBlockState(target, Blocks.OBSIDIAN.getDefaultState(), 3);
                        } else {
                            world.setBlockState(target, ConfigBlocks.blockCosmeticSolid.getStateFromMeta(1), 3);
                        }
                    } else {
                        world.setBlockToAir(target);
                    }
                }

                if (x == 0 && z == 0) {
                    placeCenter(world, rand, pos);
                } else if (isCapPosition(x, z)) {
                    world.setBlockState(column, ConfigBlocks.blockCosmeticSolid.getStateFromMeta(1), 3);
                    world.setBlockState(column.up(), ConfigBlocks.blockEldritch.getStateFromMeta(3), 3);
                }
            }
        }

        TileEntity altar = world.getTileEntity(pos.up());
        if (altar instanceof TileEldritchAltar
                && ((TileEldritchAltar) altar).isSpawner()
                && ((TileEldritchAltar) altar).getSpawnType() == 0) {
            placeBanner(world, pos, EnumFacing.NORTH, (byte) 8);
            placeBanner(world, pos, EnumFacing.SOUTH, (byte) 0);
            placeBanner(world, pos, EnumFacing.WEST, (byte) 12);
            placeBanner(world, pos, EnumFacing.EAST, (byte) 4);
        }

        return true;
    }

    private boolean isValidRingSpawn(World world, BlockPos start) {
        int distanceToAir = 0;
        BlockPos checkPos = start;
        while (checkPos.getY() < world.getHeight() && world.getBlockState(checkPos).getBlock() != Blocks.AIR) {
            checkPos = start.up(++distanceToAir);
        }
        if (distanceToAir > 2) {
            return false;
        }

        BlockPos surface = start.up(distanceToAir - 1);
        if (world.getBlockState(surface.up()).getBlock() != Blocks.AIR) {
            return false;
        }

        if (isValidGroundBlock(world.getBlockState(surface).getBlock())) {
            return true;
        }

        if ((world.getBlockState(surface).getBlock() == Blocks.SNOW_LAYER
                || world.getBlockState(surface).getBlock() == Blocks.SNOW)
                && isValidGroundBlock(world.getBlockState(surface.down()).getBlock())) {
            return true;
        }

        return false;
    }

    private boolean isValidGroundBlock(net.minecraft.block.Block block) {
        return block == Blocks.STONE
                || block == Blocks.SAND
                || block == Blocks.HARDENED_CLAY
                || block == Blocks.GRASS
                || block == Blocks.GRAVEL
                || block == Blocks.DIRT;
    }

    private boolean isCorner(int x, int z) {
        return Math.abs(x) == 3 && Math.abs(z) == 3;
    }

    private boolean isCapPosition(int x, int z) {
        return ((Math.abs(x) == 3 && Math.abs(z % 2) == 1)
                || (Math.abs(z) == 3 && Math.abs(x % 2) == 1))
                && Math.abs(x) != Math.abs(z);
    }

    private void placeCenter(World world, Random rand, BlockPos pos) {
        world.setBlockState(pos.up(), ConfigBlocks.blockEldritch.getStateFromMeta(0), 3);
        world.setBlockState(pos, ConfigBlocks.blockCosmeticSolid.getStateFromMeta(1), 3);

        TileEntity te = world.getTileEntity(pos.up());
        if (te instanceof TileEldritchAltar) {
            int variant = rand.nextInt(10);
            if (variant >= 1 && variant <= 4) {
                ((TileEldritchAltar) te).setSpawner(true);
                ((TileEldritchAltar) te).setSpawnType((byte) 0);
            } else if (variant == 6 || variant == 7) {
                ((TileEldritchAltar) te).setSpawner(true);
                ((TileEldritchAltar) te).setSpawnType((byte) 1);
            }
        }

        world.setBlockState(pos.up(3), ConfigBlocks.blockEldritch.getStateFromMeta(1), 3);
        for (int y = 4; y <= 7; ++y) {
            world.setBlockState(pos.up(y), ConfigBlocks.blockEldritch.getStateFromMeta(2), 3);
        }
    }

    private void placeBanner(World world, BlockPos center, EnumFacing direction, byte facing) {
        BlockPos bannerPos = center.add(-direction.getXOffset() * 3, 1, direction.getZOffset() * 3);
        world.setBlockState(bannerPos, ConfigBlocks.blockWoodenDevice.getStateFromMeta(8), 3);
        TileEntity banner = world.getTileEntity(bannerPos);
        if (banner instanceof TileBanner) {
            ((TileBanner) banner).setFacing(facing);
        }
    }
}
