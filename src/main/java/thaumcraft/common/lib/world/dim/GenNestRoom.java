package thaumcraft.common.lib.world.dim;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileCrystal;

import java.util.Random;

public class GenNestRoom extends GenCommon {

    static void generateRoom(World world, Random random, int cx, int cz, int y, Cell cell) {
        int x = cx * 16;
        int z = cz * 16;

        // Outer wall (bedrock)
        for (int a = 1; a <= 15; a++) {
            for (int b = 1; b <= 15; b++) {
                for (int c = 0; c < 11; c++) {
                    if (a == 1 || a == 15 || b == 1 || b == 15) {
                        placeBlock(world, x + a, y + c, z + b, BEDROCK, cell);
                    }
                }
            }
        }

        // Inner void (11 height for this room)
        for (int a = 2; a <= 14; a++) {
            for (int b = 2; b <= 14; b++) {
                for (int c = 1; c < 10; c++) {
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

        // Inner fill (crust) with randomness near passage openings
        for (int a = 3; a <= 13; a++) {
            for (int b = 3; b <= 13; b++) {
                for (int c = 2; c < 9; c++) {
                    if (a == 3 || a == 13 || b == 3 || b == 13) {
                        placeBlock(world, x + a, y + c, z + b, CRUST, cell);
                    }
                    // Random fill near non-passage sides
                    if ((a == 4 && !cell.west) || (a == 12 && !cell.east) ||
                        (b == 4 && !cell.north) || (b == 12 && !cell.south)) {
                        if (random.nextBoolean()) {
                            placeBlock(world, x + a, y + c, z + b, CRUST, cell);
                        }
                    }
                }
            }
        }

        // Floor and ceiling
        for (int a = 2; a <= 14; a++) {
            for (int b = 2; b <= 14; b++) {
                placeBlock(world, x + a, y - 1, z + b, BEDROCK, cell);
                placeBlock(world, x + a, y,     z + b, VOID, cell);
                placeBlock(world, x + a, y + 1, z + b, CRUST, cell);

                placeBlock(world, x + a, y + 11, z + b, BEDROCK, cell);
                placeBlock(world, x + a, y + 10, z + b, VOID, cell);
                placeBlock(world, x + a, y + 9, z + b, CRUST, cell);

                // Crystal ceiling growth
                if (random.nextBoolean()) {
                    placeBlock(world, x + a, y + 8, z + b, CRUST, cell);
                } else if (random.nextBoolean() && world.isAirBlock(pos(x + a, y + 8, z + b))) {
                    world.setBlockState(pos(x + a, y + 8, z + b), ConfigBlocks.blockCrystal.getStateFromMeta(7), 3);
                    TileCrystal te = (TileCrystal) world.getTileEntity(pos(x + a, y + 8, z + b));
                    if (te != null) {
                        te.orientation = (short) EnumFacing.DOWN.ordinal();
                    }
                }
            }
        }

        // Central crust pillar
        placeBlock(world, x + 8, y + 2, z + 8, CRUST, cell);
        placeBlock(world, x + 8, y + 3, z + 8, CRUST, cell);
        placeBlock(world, x + 8, y + 4, z + 8, CRUST, cell);
        placeBlock(world, x + 7, y + 2, z + 8, CRUST, cell);
        placeBlock(world, x + 8, y + 2, z + 7, CRUST, cell);
        placeBlock(world, x + 9, y + 2, z + 8, CRUST, cell);
        placeBlock(world, x + 8, y + 2, z + 9, CRUST, cell);

        // Random crust bumps
        if (random.nextBoolean()) placeBlock(world, x + 7, y + 3, z + 8, CRUST, cell);
        if (random.nextBoolean()) placeBlock(world, x + 8, y + 3, z + 7, CRUST, cell);
        if (random.nextBoolean()) placeBlock(world, x + 9, y + 3, z + 8, CRUST, cell);
        if (random.nextBoolean()) placeBlock(world, x + 8, y + 3, z + 9, CRUST, cell);
        if (random.nextBoolean()) placeBlock(world, x + 8, y + 5, z + 8, 7, cell); // crystal

        // Upper crust pillar
        placeBlock(world, x + 8, y + 8, z + 8, CRUST, cell);
        placeBlock(world, x + 8, y + 7, z + 8, CRUST, cell);
        placeBlock(world, x + 8, y + 6, z + 8, CRUST, cell);
        placeBlock(world, x + 7, y + 8, z + 8, CRUST, cell);
        placeBlock(world, x + 8, y + 8, z + 7, CRUST, cell);
        placeBlock(world, x + 9, y + 8, z + 8, CRUST, cell);
        placeBlock(world, x + 8, y + 8, z + 9, CRUST, cell);

        if (random.nextBoolean()) placeBlock(world, x + 7, y + 7, z + 8, CRUST, cell);
        if (random.nextBoolean()) placeBlock(world, x + 8, y + 7, z + 7, CRUST, cell);
        if (random.nextBoolean()) placeBlock(world, x + 9, y + 7, z + 8, CRUST, cell);
        if (random.nextBoolean()) placeBlock(world, x + 8, y + 7, z + 9, CRUST, cell);

        GenCommon.generateConnections(world, random, cx, cz, y, cell, 3, true);

        // Scatter urns/chests on floor around center
        for (int a = -5; a <= 5; a++) {
            for (int b = -5; b <= 5; b++) {
                if (random.nextFloat() < 0.15f && world.isAirBlock(pos(x + 8 + a, y + 2, z + 8 + b))) {
                    float rr = random.nextFloat();
                    int md = rr < 0.15f ? 2 : (rr < 0.4f ? 1 : 0);
                    world.setBlockState(pos(x + 8 + a, y + 2, z + 8 + b),
                            random.nextFloat() < 0.2f
                                    ? ConfigBlocks.blockLootCrate.getStateFromMeta(md)
                                    : ConfigBlocks.blockLootUrn.getStateFromMeta(md),
                            3);
                }
            }
        }
    }

    private static net.minecraft.util.math.BlockPos pos(int x, int y, int z) {
        return new net.minecraft.util.math.BlockPos(x, y, z);
    }
}
