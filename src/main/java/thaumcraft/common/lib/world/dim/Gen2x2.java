package thaumcraft.common.lib.world.dim;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.Random;

public class Gen2x2 extends GenCommon {

    static void generateUpperLeft(World world, Random random, int cx, int cz, int y, Cell cell) {
        int x = cx * 16;
        int z = cz * 16;

        // Outer walls
        for (int a = 1; a <= 15; a++) {
            for (int b = 1; b <= 15; b++) {
                for (int c = 0; c < 13; c++) {
                    if (a == 1 || b == 1) {
                        placeBlock(world, x + a, y + c, z + b, BEDROCK, cell);
                    }
                }
            }
        }

        // Inner void
        for (int a = 2; a <= 15; a++) {
            for (int b = 2; b <= 15; b++) {
                for (int c = 1; c < 12; c++) {
                    if (a == 2 || b == 2) {
                        if (a == 2 && b > 4 && b < 12 && cell.west && c < 10) continue;
                        if (b == 2 && a > 4 && a < 12 && cell.north && c < 10) continue;
                        placeBlock(world, x + a, y + c, z + b, VOID, cell);
                    }
                }
            }
        }

        // Rock inner wall
        for (int a = 3; a <= 15; a++) {
            for (int b = 3; b <= 15; b++) {
                for (int c = 2; c < 11; c++) {
                    if (a == 3 || b == 3) {
                        placeBlock(world, x + a, y + c, z + b, ROCK, cell);
                    }
                }
            }
        }

        // Floor & ceiling
        for (int a = 2; a <= 15; a++) {
            for (int b = 2; b <= 15; b++) {
                placeBlock(world, x + a, y - 1, z + b, BEDROCK, cell);
                placeBlock(world, x + a, y,     z + b, VOID, cell);
                placeBlock(world, x + a, y + 1, z + b, STONE_NOSPAWN, cell);
                placeBlock(world, x + a, y + 13, z + b, BEDROCK, cell);
                placeBlock(world, x + a, y + 12, z + b, VOID, cell);
                placeBlock(world, x + a, y + 11, z + b, STONE, cell);
            }
        }

        // Stairs
        for (int g = 4; g <= 15; g++) {
            placeBlock(world, x + g, y + 2, z + 4, STAIR_DIR, EnumFacing.NORTH, cell);
            placeBlock(world, x + g, y + 10, z + 4, STAIR_DIR_INV, EnumFacing.NORTH, cell);
        }
        for (int g = 4; g <= 15; g++) {
            placeBlock(world, x + 4, y + 2, z + g, STAIR_DIR, EnumFacing.WEST, cell);
            placeBlock(world, x + 4, y + 10, z + g, STAIR_DIR_INV, EnumFacing.WEST, cell);
        }

        GenCommon.generateConnections(world, random, cx, cz, y, cell, 3, true);
    }

    static void generateUpperRight(World world, Random random, int cx, int cz, int y, Cell cell) {
        int x = cx * 16;
        int z = cz * 16;

        for (int a = 0; a <= 15; a++) {
            for (int b = 1; b <= 15; b++) {
                for (int c = 0; c < 13; c++) {
                    if (a == 15 || b == 1) {
                        placeBlock(world, x + a, y + c, z + b, BEDROCK, cell);
                    }
                }
            }
        }

        for (int a = 0; a <= 14; a++) {
            for (int b = 2; b <= 15; b++) {
                for (int c = 1; c < 12; c++) {
                    if (a == 14 || b == 2) {
                        if (a == 14 && b > 4 && b < 12 && cell.east && c < 10) continue;
                        if (b == 2 && a > 4 && a < 12 && cell.north && c < 10) continue;
                        placeBlock(world, x + a, y + c, z + b, VOID, cell);
                    }
                }
            }
        }

        for (int a = 0; a <= 13; a++) {
            for (int b = 3; b <= 15; b++) {
                for (int c = 2; c < 11; c++) {
                    if (a == 13 || b == 3) {
                        placeBlock(world, x + a, y + c, z + b, ROCK, cell);
                    }
                }
            }
        }

        for (int a = 0; a <= 14; a++) {
            for (int b = 2; b <= 15; b++) {
                placeBlock(world, x + a, y - 1, z + b, BEDROCK, cell);
                placeBlock(world, x + a, y,     z + b, VOID, cell);
                placeBlock(world, x + a, y + 1, z + b, STONE_NOSPAWN, cell);
                placeBlock(world, x + a, y + 13, z + b, BEDROCK, cell);
                placeBlock(world, x + a, y + 12, z + b, VOID, cell);
                placeBlock(world, x + a, y + 11, z + b, STONE, cell);
            }
        }

        for (int g = 0; g <= 11; g++) {
            placeBlock(world, x + g, y + 2, z + 4, STAIR_DIR, EnumFacing.NORTH, cell);
            placeBlock(world, x + g, y + 10, z + 4, STAIR_DIR_INV, EnumFacing.NORTH, cell);
        }
        for (int g = 4; g <= 15; g++) {
            placeBlock(world, x + 12, y + 2, z + g, STAIR_DIR, EnumFacing.EAST, cell);
            placeBlock(world, x + 12, y + 10, z + g, STAIR_DIR_INV, EnumFacing.EAST, cell);
        }

        GenCommon.generateConnections(world, random, cx, cz, y, cell, 3, true);
    }

    static void generateLowerLeft(World world, Random random, int cx, int cz, int y, Cell cell) {
        int x = cx * 16;
        int z = cz * 16;

        for (int a = 1; a <= 15; a++) {
            for (int b = 0; b <= 15; b++) {
                for (int c = 0; c < 13; c++) {
                    if (a == 1 || b == 15) {
                        placeBlock(world, x + a, y + c, z + b, BEDROCK, cell);
                    }
                }
            }
        }

        for (int a = 2; a <= 15; a++) {
            for (int b = 0; b <= 14; b++) {
                for (int c = 1; c < 12; c++) {
                    if (a == 2 || b == 14) {
                        if (a == 2 && b > 4 && b < 12 && cell.west && c < 10) continue;
                        if (b == 14 && a > 4 && a < 12 && cell.south && c < 10) continue;
                        placeBlock(world, x + a, y + c, z + b, VOID, cell);
                    }
                }
            }
        }

        for (int a = 3; a <= 15; a++) {
            for (int b = 0; b <= 13; b++) {
                for (int c = 2; c < 11; c++) {
                    if (a == 3 || b == 13) {
                        placeBlock(world, x + a, y + c, z + b, ROCK, cell);
                    }
                }
            }
        }

        for (int a = 2; a <= 15; a++) {
            for (int b = 0; b <= 14; b++) {
                placeBlock(world, x + a, y - 1, z + b, BEDROCK, cell);
                placeBlock(world, x + a, y,     z + b, VOID, cell);
                placeBlock(world, x + a, y + 1, z + b, STONE_NOSPAWN, cell);
                placeBlock(world, x + a, y + 13, z + b, BEDROCK, cell);
                placeBlock(world, x + a, y + 12, z + b, VOID, cell);
                placeBlock(world, x + a, y + 11, z + b, STONE, cell);
            }
        }

        for (int g = 4; g <= 15; g++) {
            placeBlock(world, x + g, y + 2, z + 12, STAIR_DIR, EnumFacing.SOUTH, cell);
            placeBlock(world, x + g, y + 10, z + 12, STAIR_DIR_INV, EnumFacing.SOUTH, cell);
        }
        for (int g = 0; g <= 11; g++) {
            placeBlock(world, x + 4, y + 2, z + g, STAIR_DIR, EnumFacing.WEST, cell);
            placeBlock(world, x + 4, y + 10, z + g, STAIR_DIR_INV, EnumFacing.WEST, cell);
        }

        GenCommon.generateConnections(world, random, cx, cz, y, cell, 3, true);
    }

    static void generateLowerRight(World world, Random random, int cx, int cz, int y, Cell cell) {
        int x = cx * 16;
        int z = cz * 16;

        for (int a = 0; a <= 15; a++) {
            for (int b = 0; b <= 15; b++) {
                for (int c = 0; c < 13; c++) {
                    if (a == 15 || b == 15) {
                        placeBlock(world, x + a, y + c, z + b, BEDROCK, cell);
                    }
                }
            }
        }

        for (int a = 0; a <= 14; a++) {
            for (int b = 0; b <= 14; b++) {
                for (int c = 1; c < 12; c++) {
                    if (a == 14 || b == 14) {
                        if (a == 14 && b > 4 && b < 12 && cell.east && c < 10) continue;
                        if (b == 14 && a > 4 && a < 12 && cell.south && c < 10) continue;
                        placeBlock(world, x + a, y + c, z + b, VOID, cell);
                    }
                }
            }
        }

        for (int a = 0; a <= 13; a++) {
            for (int b = 0; b <= 13; b++) {
                for (int c = 2; c < 11; c++) {
                    if (a == 13 || b == 13) {
                        placeBlock(world, x + a, y + c, z + b, ROCK, cell);
                    }
                }
            }
        }

        for (int a = 0; a <= 14; a++) {
            for (int b = 0; b <= 14; b++) {
                placeBlock(world, x + a, y - 1, z + b, BEDROCK, cell);
                placeBlock(world, x + a, y,     z + b, VOID, cell);
                placeBlock(world, x + a, y + 1, z + b, STONE_NOSPAWN, cell);
                placeBlock(world, x + a, y + 13, z + b, BEDROCK, cell);
                placeBlock(world, x + a, y + 12, z + b, VOID, cell);
                placeBlock(world, x + a, y + 11, z + b, STONE, cell);
            }
        }

        for (int g = 0; g <= 11; g++) {
            placeBlock(world, x + g, y + 2, z + 12, STAIR_DIR, EnumFacing.SOUTH, cell);
            placeBlock(world, x + g, y + 10, z + 12, STAIR_DIR_INV, EnumFacing.SOUTH, cell);
        }
        for (int g = 0; g <= 12; g++) {
            placeBlock(world, x + 12, y + 2, z + g, STAIR_DIR, EnumFacing.EAST, cell);
            placeBlock(world, x + 12, y + 10, z + g, STAIR_DIR_INV, EnumFacing.EAST, cell);
        }

        GenCommon.generateConnections(world, random, cx, cz, y, cell, 3, true);
    }
}
