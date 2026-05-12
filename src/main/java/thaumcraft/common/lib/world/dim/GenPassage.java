package thaumcraft.common.lib.world.dim;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

import net.minecraft.util.math.BlockPos;
import java.util.Random;

public class GenPassage extends GenCommon {

    static void generateDefaultPassage(World world, Random random, int cx, int cz, int y, Cell cell) {
        int x = cx * 16;
        int z = cz * 16;

        GenCommon.generateConnections(world, random, cx, cz, y, cell, 4, false);

        int mod = 0;
        if (cell.north && cell.south && cell.west && cell.east && random.nextBoolean()) {
            mod = 1; // Cross-intersection mode
        }

        // Central 7x7 floor area
        for (int w = 1; w < 8; w++) {
            for (int h = 1; h < 8; h++) {
                if (w == 4 && h == 4 && mod == 1) {
                    placeBlock(world, x + 4 + w, y + 2, z + 4 + h, 7, cell);
                    placeBlock(world, x + 4 + w, y + 8, z + 4 + h, 7, cell);
                } else {
                    placeBlock(world, x + 4 + w, y + 2, z + 4 + h,
                            cell.feature == 11 && random.nextInt(3) == 0 ? STONE_TRAPPED : STONE, cell);
                    placeBlock(world, x + 4 + w, y + 8, z + 4 + h,
                            cell.feature == 11 && random.nextInt(3) == 0 ? STONE_TRAPPED : STONE, cell);
                }
                placeBlock(world, x + 4 + w, y,     z + 4 + h, BEDROCK, cell);
                placeBlock(world, x + 4 + w, y + 10, z + 4 + h, BEDROCK, cell);
                placeBlock(world, x + 4 + w, y + 1, z + 4 + h, VOID, cell);
                placeBlock(world, x + 4 + w, y + 9, z + 4 + h, VOID, cell);
            }
        }

        // --- NORTH connection ---
        if (cell.north) {
            for (int w = 2 + mod; w < 9 - mod; w++) {
                for (int h = 2 + mod; h < 9 - mod; h++) {
                    placeBlock(world, x + 3 + w, y + 10 - h, z + 5, PAT_CONNECT[h][w], EnumFacing.NORTH, cell);
                }
            }
            if (mod == 0) {
                if (cell.west) {
                    placeBlock(world, x + 6, y + 3, z + 6, 3, EnumFacing.EAST, cell);
                    placeBlock(world, x + 6, y + 7, z + 6, 5, EnumFacing.EAST, cell);
                }
                if (cell.east) {
                    placeBlock(world, x + 10, y + 3, z + 6, 3, EnumFacing.EAST, cell);
                    placeBlock(world, x + 10, y + 7, z + 6, 5, EnumFacing.EAST, cell);
                }
            }
        } else {
            // Closed north side
            for (int w = 1; w < 8; w++) {
                for (int h = 1; h < 8; h++) {
                    placeBlock(world, x + 4 + w, y + 9 - h, z + 5,
                            cell.feature == 11 && random.nextInt(3) == 0 ? STONE_TRAPPED : STONE, cell);
                    placeBlock(world, x + 4 + w, y + 9 - h, z + 4, VOID, cell);
                    placeBlock(world, x + 4 + w, y + 9 - h, z + 3, BEDROCK, cell);
                    if (h == 7) {
                        placeBlock(world, x + 4 + w, y + 1, z + 4, BEDROCK, cell);
                        placeBlock(world, x + 4 + w, y + 9, z + 4, BEDROCK, cell);
                    }
                    if (w == 7) {
                        placeBlock(world, x + 4, y + 9 - h, z + 4, BEDROCK, cell);
                        placeBlock(world, x + 12, y + 9 - h, z + 4, BEDROCK, cell);
                    }
                }
            }
            for (int w = 2; w < 7; w++) {
                placeBlock(world, x + 4 + w, y + 3, z + 6, 3, EnumFacing.EAST, cell);
                placeBlock(world, x + 4 + w, y + 7, z + 6, 5, EnumFacing.EAST, cell);
            }
        }

        // --- SOUTH connection ---
        if (cell.south) {
            for (int w = 2 + mod; w < 9 - mod; w++) {
                for (int h = 2 + mod; h < 9 - mod; h++) {
                    placeBlock(world, x + 3 + w, y + 10 - h, z + 11, PAT_CONNECT[h][w], EnumFacing.SOUTH, cell);
                }
            }
            if (mod == 0) {
                if (cell.west) {
                    placeBlock(world, x + 6, y + 3, z + 10, 4, EnumFacing.EAST, cell);
                    placeBlock(world, x + 6, y + 7, z + 10, 6, EnumFacing.EAST, cell);
                }
                if (cell.east) {
                    placeBlock(world, x + 10, y + 3, z + 10, 4, EnumFacing.EAST, cell);
                    placeBlock(world, x + 10, y + 7, z + 10, 6, EnumFacing.EAST, cell);
                }
            }
        } else {
            for (int w = 1; w < 8; w++) {
                for (int h = 1; h < 8; h++) {
                    placeBlock(world, x + 4 + w, y + 9 - h, z + 11,
                            cell.feature == 11 && random.nextInt(3) == 0 ? STONE_TRAPPED : STONE, cell);
                    placeBlock(world, x + 4 + w, y + 9 - h, z + 12, VOID, cell);
                    placeBlock(world, x + 4 + w, y + 9 - h, z + 13, BEDROCK, cell);
                    if (h == 7) {
                        placeBlock(world, x + 4 + w, y + 1, z + 12, BEDROCK, cell);
                        placeBlock(world, x + 4 + w, y + 9, z + 12, BEDROCK, cell);
                    }
                    if (w == 7) {
                        placeBlock(world, x + 4, y + 9 - h, z + 12, BEDROCK, cell);
                        placeBlock(world, x + 12, y + 9 - h, z + 12, BEDROCK, cell);
                    }
                }
            }
            for (int w = 2; w < 7; w++) {
                placeBlock(world, x + 4 + w, y + 3, z + 10, 4, EnumFacing.EAST, cell);
                placeBlock(world, x + 4 + w, y + 7, z + 10, 6, EnumFacing.EAST, cell);
            }
        }

        // --- EAST connection ---
        if (cell.east) {
            for (int w = 2 + mod; w < 9 - mod; w++) {
                for (int h = 2 + mod; h < 9 - mod; h++) {
                    placeBlock(world, x + 11, y + 10 - h, z + 3 + w, PAT_CONNECT[h][w], EnumFacing.EAST, cell);
                }
            }
            if (mod == 0) {
                if (cell.north) {
                    placeBlock(world, x + 10, y + 3, z + 6, 4, EnumFacing.NORTH, cell);
                    placeBlock(world, x + 10, y + 7, z + 6, 6, EnumFacing.NORTH, cell);
                }
                if (cell.south) {
                    placeBlock(world, x + 10, y + 3, z + 10, 4, EnumFacing.NORTH, cell);
                    placeBlock(world, x + 10, y + 7, z + 10, 6, EnumFacing.NORTH, cell);
                }
            }
        } else {
            for (int w = 1; w < 8; w++) {
                for (int h = 1; h < 8; h++) {
                    placeBlock(world, x + 11, y + 9 - h, z + 4 + w,
                            cell.feature == 11 && random.nextInt(3) == 0 ? STONE_TRAPPED : STONE, cell);
                    placeBlock(world, x + 12, y + 9 - h, z + 4 + w, VOID, cell);
                    placeBlock(world, x + 13, y + 9 - h, z + 4 + w, BEDROCK, cell);
                    if (h == 7) {
                        placeBlock(world, x + 12, y + 1, z + 4 + w, BEDROCK, cell);
                        placeBlock(world, x + 12, y + 9, z + 4 + w, BEDROCK, cell);
                    }
                    if (w == 7) {
                        placeBlock(world, x + 12, y + 9 - h, z + 4, BEDROCK, cell);
                        placeBlock(world, x + 12, y + 9 - h, z + 12, BEDROCK, cell);
                    }
                }
            }
            for (int w = 2; w < 7; w++) {
                placeBlock(world, x + 10, y + 3, z + 4 + w, 4, EnumFacing.NORTH, cell);
                placeBlock(world, x + 10, y + 7, z + 4 + w, 6, EnumFacing.NORTH, cell);
            }
        }

        // --- WEST connection ---
        if (cell.west) {
            for (int w = 2 + mod; w < 9 - mod; w++) {
                for (int h = 2 + mod; h < 9 - mod; h++) {
                    placeBlock(world, x + 5, y + 10 - h, z + 3 + w, PAT_CONNECT[h][w], EnumFacing.WEST, cell);
                }
            }
            if (mod == 0) {
                if (cell.north) {
                    placeBlock(world, x + 6, y + 3, z + 6, 3, EnumFacing.NORTH, cell);
                    placeBlock(world, x + 6, y + 7, z + 6, 5, EnumFacing.NORTH, cell);
                }
                if (cell.south) {
                    placeBlock(world, x + 6, y + 3, z + 10, 3, EnumFacing.NORTH, cell);
                    placeBlock(world, x + 6, y + 7, z + 10, 5, EnumFacing.NORTH, cell);
                }
            }
        } else {
            for (int w = 1; w < 8; w++) {
                for (int h = 1; h < 8; h++) {
                    placeBlock(world, x + 5, y + 9 - h, z + 4 + w,
                            cell.feature == 11 && random.nextInt(3) == 0 ? STONE_TRAPPED : STONE, cell);
                    placeBlock(world, x + 4, y + 9 - h, z + 4 + w, VOID, cell);
                    placeBlock(world, x + 3, y + 9 - h, z + 4 + w, BEDROCK, cell);
                    if (h == 7) {
                        placeBlock(world, x + 4, y + 1, z + 4 + w, BEDROCK, cell);
                        placeBlock(world, x + 4, y + 9, z + 4 + w, BEDROCK, cell);
                    }
                    if (w == 7) {
                        placeBlock(world, x + 4, y + 9 - h, z + 4, BEDROCK, cell);
                        placeBlock(world, x + 4, y + 9 - h, z + 12, BEDROCK, cell);
                    }
                }
            }
            for (int w = 2; w < 7; w++) {
                placeBlock(world, x + 6, y + 3, z + 4 + w, 3, EnumFacing.NORTH, cell);
                placeBlock(world, x + 6, y + 7, z + 4 + w, 5, EnumFacing.NORTH, cell);
            }
        }

        // Cross-intersection extra stairs
        if (mod == 1) {
            placeBlock(world, x + 5, y + 3, z + 5, 3, EnumFacing.EAST, cell);
            placeBlock(world, x + 5, y + 7, z + 5, 5, EnumFacing.EAST, cell);
            placeBlock(world, x + 5, y + 3, z + 6, 3, EnumFacing.NORTH, cell);
            placeBlock(world, x + 5, y + 7, z + 6, 5, EnumFacing.NORTH, cell);

            placeBlock(world, x + 11, y + 3, z + 5, 3, EnumFacing.EAST, cell);
            placeBlock(world, x + 11, y + 7, z + 5, 5, EnumFacing.EAST, cell);
            placeBlock(world, x + 11, y + 3, z + 6, 4, EnumFacing.NORTH, cell);
            placeBlock(world, x + 11, y + 7, z + 6, 6, EnumFacing.NORTH, cell);

            placeBlock(world, x + 5, y + 3, z + 11, 3, EnumFacing.NORTH, cell);
            placeBlock(world, x + 5, y + 7, z + 11, 5, EnumFacing.NORTH, cell);
            placeBlock(world, x + 6, y + 3, z + 11, 4, EnumFacing.EAST, cell);
            placeBlock(world, x + 6, y + 7, z + 11, 6, EnumFacing.EAST, cell);

            placeBlock(world, x + 11, y + 3, z + 11, 4, EnumFacing.NORTH, cell);
            placeBlock(world, x + 11, y + 7, z + 11, 6, EnumFacing.NORTH, cell);
            placeBlock(world, x + 10, y + 3, z + 11, 4, EnumFacing.EAST, cell);
            placeBlock(world, x + 10, y + 7, z + 11, 6, EnumFacing.EAST, cell);
        }

        // Feature 12: Crust fill
        if (cell.feature == 12) {
            for (int w = -4; w <= 4; w++) {
                for (int h = -4; h < 5; h++) {
                    for (int j = -4; j <= 4; j++) {
                        BlockPos p = new BlockPos(x + 8 + w, y + 4 + h, z + 8 + j);
                        if ((world.isAirBlock(p) || world.getBlockState(p).getBlock() == ConfigBlocks.blockCosmeticSolid
                                || world.getBlockState(p).getBlock() == getStairsBlock()) && random.nextBoolean()) {
                            placeBlock(world, x + 8 + w, y + 4 + h, z + 8 + j, CRUST, cell);
                        }
                    }
                }
            }
        }

        // Feature 13: Taint fibre infestation
        if (cell.feature == 13) {
            for (int w = -4; w <= 4; w++) {
                for (int h = -3; h <= 3; h++) {
                    for (int j = -4; j <= 4; j++) {
                        BlockPos p = new BlockPos(x + 8 + w, y + 4 + h, z + 8 + j);
                        if (world.isAirBlock(p) && BlockUtils.isAdjacentToSolidBlock(world, p)) {
                            if (random.nextInt(3) != 0) {
                                world.setBlockState(p, ConfigBlocks.blockTaintFibres.getStateFromMeta(random.nextInt(4) == 0 ? 1 : 0), 3);
                            }
                            Utils.setBiomeAt(world, x + 8 + w, z + 8 + j, ThaumcraftWorldGenerator.biomeTaint);
                        }
                    }
                }
            }
        }

        // Feature 14: Web nest with mind spider spawner
        if (cell.feature == 14) {
            for (int w = -3; w <= 3; w++) {
                for (int h = -3; h <= 3; h++) {
                    for (int j = -3; j <= 3; j++) {
                        BlockPos p = new BlockPos(x + 8 + w, y + 4 + h, z + 8 + j);
                        if (world.isAirBlock(p) && random.nextFloat() < 0.35f) {
                            world.setBlockState(p, Blocks.WEB.getDefaultState(), 3);
                        }
                    }
                }
            }
            world.setBlockState(new BlockPos(x + 8, y + 4, z + 8), Blocks.MOB_SPAWNER.getDefaultState(), 3);
            TileEntityMobSpawner spawner = (TileEntityMobSpawner) world.getTileEntity(new BlockPos(x + 8, y + 4, z + 8));
            if (spawner != null) {
                spawner.getSpawnerBaseLogic().setEntityId(new net.minecraft.util.ResourceLocation("thaumcraft", "mind_spider"));
            }
        }
    }
}
