package thaumcraft.common.lib.world.dim;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigBlocks;

import java.util.Random;

public class GenLibraryRoom extends GenCommon {

    static void generateRoom(World world, Random random, int cx, int cz, int y, Cell cell) {
        int x = cx * 16;
        int z = cz * 16;

        // Outer wall (bedrock)
        for (int a = 1; a <= 15; a++) {
            for (int b = 1; b <= 15; b++) {
                for (int c = 0; c < 13; c++) {
                    if (a == 1 || a == 15 || b == 1 || b == 15) {
                        placeBlock(world, x + a, y + c, z + b, BEDROCK, cell);
                    }
                }
            }
        }

        // Inner void (with passage openings)
        for (int a = 2; a <= 14; a++) {
            for (int b = 2; b <= 14; b++) {
                for (int c = 1; c < 12; c++) {
                    if (a == 2 || a == 14 || b == 2 || b == 14) {
                        if (a == 2 && b > 3 && b < 12 && cell.west && c < 10) continue;
                        if (a == 14 && b > 3 && b < 12 && cell.east && c < 10) continue;
                        if (b == 2 && a > 3 && a < 12 && cell.north && c < 10) continue;
                        if (b == 14 && a > 3 && a < 12 && cell.south && c < 10) continue;
                        placeBlock(world, x + a, y + c, z + b, VOID, cell);
                    }
                }
            }
        }

        // Inner wall (stone)
        for (int a = 3; a <= 13; a++) {
            for (int b = 3; b <= 13; b++) {
                for (int c = 2; c < 11; c++) {
                    if (a == 3 || a == 13 || b == 3 || b == 13) {
                        placeBlock(world, x + a, y + c, z + b, STONE, cell);
                    }
                }
            }
        }

        // Floor/ceiling layers + corner pedestal areas
        for (int a = 2; a <= 14; a++) {
            for (int b = 2; b <= 14; b++) {
                placeBlock(world, x + a, y - 1, z + b, BEDROCK, cell);
                placeBlock(world, x + a, y,     z + b, VOID, cell);
                placeBlock(world, x + a, y + 1, z + b, STONE, cell);
                placeBlock(world, x + a, y + 12, z + b, BEDROCK, cell);
                placeBlock(world, x + a, y + 11, z + b, VOID, cell);
                placeBlock(world, x + a, y + 10, z + b, STONE, cell);

                // Corner pedestal platforms (4 corners of inner area)
                if (a > 3 && a < 13 && b > 3 && b < 13) {
                    if ((a <= 5 && b <= 5) || (a <= 5 && b >= 11) || (a >= 11 && b <= 5) || (a >= 11 && b >= 11)) {
                        placeBlock(world, x + a, y + 2, z + b, STONE, cell);
                        placeBlock(world, x + a, y + 9, z + b, STONE, cell);
                    }
                    // Pedestal blocks at exact corner positions
                    if ((a == 5 && b == 5) || (a == 5 && b == 11) || (a == 11 && b == 5) || (a == 11 && b == 11)) {
                        world.setBlockState(pos(x + a, y + 3, z + b), ConfigBlocks.blockCosmeticSolid.getStateFromMeta(15), 3);
                        world.setBlockState(pos(x + a, y + 8, z + b), ConfigBlocks.blockCosmeticSolid.getStateFromMeta(15), 3);
                    }
                }
            }
        }

        // Lower stairs (floor)
        for (int g = 0; g < 5; g++) {
            placeBlock(world, x + 6 + g, y + 2, z + 4,  STAIR_DIR, EnumFacing.NORTH, cell);
            placeBlock(world, x + 6 + g, y + 2, z + 12, STAIR_DIR, EnumFacing.SOUTH, cell);
            placeBlock(world, x + 12,    y + 2, z + 6 + g, STAIR_DIR, EnumFacing.EAST, cell);
            placeBlock(world, x + 4,     y + 2, z + 6 + g, STAIR_DIR, EnumFacing.WEST, cell);
        }

        // Upper inverted stairs (ceiling)
        for (int g = 0; g < 5; g++) {
            placeBlock(world, x + 6 + g, y + 9, z + 4,  STAIR_DIR_INV, EnumFacing.NORTH, cell);
            placeBlock(world, x + 6 + g, y + 9, z + 12, STAIR_DIR_INV, EnumFacing.SOUTH, cell);
            placeBlock(world, x + 12,    y + 9, z + 6 + g, STAIR_DIR_INV, EnumFacing.EAST, cell);
            placeBlock(world, x + 4,     y + 9, z + 6 + g, STAIR_DIR_INV, EnumFacing.WEST, cell);
        }

        // Corner pedestal decorations (lower)
        world.setBlockState(pos(x + 5, y + 4, z + 5), ConfigBlocks.blockEldritch.getStateFromMeta(5), 3);
        world.setBlockState(pos(x + 5, y + 5, z + 5), getSlabBlock(1), 3);
        world.setBlockState(pos(x + 5, y + 4, z + 11), ConfigBlocks.blockEldritch.getStateFromMeta(5), 3);
        world.setBlockState(pos(x + 5, y + 5, z + 11), getSlabBlock(1), 3);
        world.setBlockState(pos(x + 11, y + 4, z + 5), ConfigBlocks.blockEldritch.getStateFromMeta(5), 3);
        world.setBlockState(pos(x + 11, y + 5, z + 5), getSlabBlock(1), 3);
        world.setBlockState(pos(x + 11, y + 4, z + 11), ConfigBlocks.blockEldritch.getStateFromMeta(5), 3);
        world.setBlockState(pos(x + 11, y + 5, z + 11), getSlabBlock(1), 3);

        // Corner pedestal decorations (upper, inverted slabs)
        world.setBlockState(pos(x + 5, y + 7, z + 5), ConfigBlocks.blockEldritch.getStateFromMeta(5), 3);
        world.setBlockState(pos(x + 5, y + 6, z + 5), getSlabBlock(9), 3);
        world.setBlockState(pos(x + 5, y + 7, z + 11), ConfigBlocks.blockEldritch.getStateFromMeta(5), 3);
        world.setBlockState(pos(x + 5, y + 6, z + 11), getSlabBlock(9), 3);
        world.setBlockState(pos(x + 11, y + 7, z + 5), ConfigBlocks.blockEldritch.getStateFromMeta(5), 3);
        world.setBlockState(pos(x + 11, y + 6, z + 5), getSlabBlock(9), 3);
        world.setBlockState(pos(x + 11, y + 7, z + 11), ConfigBlocks.blockEldritch.getStateFromMeta(5), 3);
        world.setBlockState(pos(x + 11, y + 6, z + 11), getSlabBlock(9), 3);

        // Central altar
        world.setBlockState(pos(x + 8, y + 2, z + 8), ConfigBlocks.blockCosmeticSolid.getStateFromMeta(15), 3);
        world.setBlockState(pos(x + 8, y + 3, z + 8), ConfigBlocks.blockEldritch.getStateFromMeta(5), 3);
        world.setBlockState(pos(x + 8, y + 4, z + 8), getSlabBlock(1), 3);

        // Upper central structure
        world.setBlockState(pos(x + 8, y + 9, z + 8), ConfigBlocks.blockCosmeticSolid.getStateFromMeta(15), 3);
        world.setBlockState(pos(x + 8, y + 8, z + 8), ConfigBlocks.blockEldritch.getStateFromMeta(5), 3);
        world.setBlockState(pos(x + 8, y + 7, z + 8), getSlabBlock(9), 3);

        GenCommon.generateConnections(world, random, cx, cz, y, cell, 3, true);
    }

    private static net.minecraft.block.state.IBlockState getSlabBlock(int meta) {
        return ConfigBlocks.blockSlabStone.getStateFromMeta(meta);
    }

    private static net.minecraft.util.math.BlockPos pos(int x, int y, int z) {
        return new net.minecraft.util.math.BlockPos(x, y, z);
    }
}
