package thaumcraft.common.lib.world.dim;

import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigBlocks;

import java.util.Random;

public class GenPortal extends GenCommon {

    static void generatePortal(World world, Random random, int cx, int cz, int y, Cell cell) {
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

        // Inner wall (stone) — skip corner areas for portal opening
        for (int a = 3; a <= 13; a++) {
            for (int b = 3; b <= 13; b++) {
                for (int c = 2; c < 11; c++) {
                    if (a == 3 || a == 13 || b == 3 || b == 13) {
                        if (a <= 4 && b <= 4) continue;
                        if (a <= 4 && b >= 12) continue;
                        if (a >= 12 && b <= 4) continue;
                        if (a >= 12 && b >= 12) continue;
                        placeBlock(world, x + a, y + c, z + b, STONE, cell);
                    }
                }
            }
        }

        // Floor and ceiling
        for (int a = 2; a <= 14; a++) {
            for (int b = 2; b <= 14; b++) {
                placeBlock(world, x + a, y - 1, z + b, BEDROCK, cell);
                placeBlock(world, x + a, y,     z + b, VOID, cell);
                placeBlock(world, x + a, y + 1, z + b, STONE_NOSPAWN, cell);
                placeBlock(world, x + a, y + 13, z + b, BEDROCK, cell);
                placeBlock(world, x + a, y + 12, z + b, VOID, cell);
                placeBlock(world, x + a, y + 11, z + b, STONE, cell);

                // Sloped floor
                if (a > 1 && a < 15 && b > 1 && b < 15) {
                    int q = Math.min(Math.abs(8 - a), Math.abs(8 - b));
                    for (int g = 0; g < q - 1; g++) {
                        placeBlock(world, x + a, y + 1 + g, z + b, STONE_NOSPAWN, cell);
                    }
                }
                // Sloped ceiling
                if (a > 3 && a < 13 && b > 3 && b < 13) {
                    int q = Math.min(Math.abs(8 - a), Math.abs(8 - b));
                    for (int g = 0; g < q; g++) {
                        placeBlock(world, x + a, y + 11 - g, z + b, STONE_NOSPAWN, cell);
                    }
                }
            }
        }

        // Lower stairs
        for (int g = 0; g < 5; g++) {
            placeBlock(world, x + 6 + g, y + 2, z + 4,  STAIR_DIR, EnumFacing.NORTH, cell);
            placeBlock(world, x + 6 + g, y + 2, z + 12, STAIR_DIR, EnumFacing.SOUTH, cell);
            placeBlock(world, x + 12,    y + 2, z + 6 + g, STAIR_DIR, EnumFacing.EAST, cell);
            placeBlock(world, x + 4,     y + 2, z + 6 + g, STAIR_DIR, EnumFacing.WEST, cell);
        }

        GenCommon.generateConnections(world, random, cx, cz, y, cell, 3, true);

        // Clear the four corners (make them air) for portal approach
        for (int a = 3; a <= 13; a++) {
            for (int b = 3; b <= 13; b++) {
                for (int c = 1; c < 12; c++) {
                    if ((a <= 4 && b <= 4) || (a <= 4 && b >= 12) || (a >= 12 && b <= 4) || (a >= 12 && b >= 12)) {
                        placeBlock(world, x + a, y + c, z + b, AIR_REPL, cell);
                        world.setBlockToAir(pos(x + a, y + c, z + b));
                    }
                }
            }
        }

        // Corner stairs around portal
        placeBlock(world, x + 5, y + 3, z + 5, STAIR_DIR, EnumFacing.NORTH, cell);
        placeBlock(world, x + 4, y + 3, z + 5, STAIR_DIR, EnumFacing.NORTH, cell);
        placeBlock(world, x + 5, y + 3, z + 4, STAIR_DIR, EnumFacing.WEST, cell);
        placeBlock(world, x + 5, y + 8, z + 5, STAIR_DIR_INV, EnumFacing.NORTH, cell);
        placeBlock(world, x + 4, y + 8, z + 5, STAIR_DIR_INV, EnumFacing.NORTH, cell);
        placeBlock(world, x + 5, y + 8, z + 4, STAIR_DIR_INV, EnumFacing.WEST, cell);

        placeBlock(world, x + 12, y + 3, z + 5, STAIR_DIR, EnumFacing.NORTH, cell);
        placeBlock(world, x + 11, y + 3, z + 5, STAIR_DIR, EnumFacing.NORTH, cell);
        placeBlock(world, x + 11, y + 3, z + 4, STAIR_DIR, EnumFacing.EAST, cell);
        placeBlock(world, x + 12, y + 8, z + 5, STAIR_DIR_INV, EnumFacing.NORTH, cell);
        placeBlock(world, x + 11, y + 8, z + 5, STAIR_DIR_INV, EnumFacing.NORTH, cell);
        placeBlock(world, x + 11, y + 8, z + 4, STAIR_DIR_INV, EnumFacing.EAST, cell);

        placeBlock(world, x + 5, y + 3, z + 11, STAIR_DIR, EnumFacing.SOUTH, cell);
        placeBlock(world, x + 4, y + 3, z + 11, STAIR_DIR, EnumFacing.SOUTH, cell);
        placeBlock(world, x + 5, y + 3, z + 12, STAIR_DIR, EnumFacing.WEST, cell);
        placeBlock(world, x + 5, y + 8, z + 11, STAIR_DIR_INV, EnumFacing.SOUTH, cell);
        placeBlock(world, x + 4, y + 8, z + 11, STAIR_DIR_INV, EnumFacing.SOUTH, cell);
        placeBlock(world, x + 5, y + 8, z + 12, STAIR_DIR_INV, EnumFacing.WEST, cell);

        placeBlock(world, x + 12, y + 3, z + 11, STAIR_DIR, EnumFacing.SOUTH, cell);
        placeBlock(world, x + 11, y + 3, z + 11, STAIR_DIR, EnumFacing.SOUTH, cell);
        placeBlock(world, x + 11, y + 3, z + 12, STAIR_DIR, EnumFacing.EAST, cell);
        placeBlock(world, x + 12, y + 8, z + 11, STAIR_DIR_INV, EnumFacing.SOUTH, cell);
        placeBlock(world, x + 11, y + 8, z + 11, STAIR_DIR_INV, EnumFacing.SOUTH, cell);
        placeBlock(world, x + 11, y + 8, z + 12, STAIR_DIR_INV, EnumFacing.EAST, cell);

        // Place pedestal + portal block + obelisk
        world.setBlockState(pos(x + 8, y + 2, z + 8), ConfigBlocks.blockEldritch.getStateFromMeta(3), 3);
        world.setBlockState(pos(x + 8, y + 3, z + 8), ConfigBlocks.blockEldritchPortal.getDefaultState());
        genObelisk(world, x + 8, y + 4, z + 8);
    }

    private static net.minecraft.util.math.BlockPos pos(int x, int y, int z) {
        return new net.minecraft.util.math.BlockPos(x, y, z);
    }
}
