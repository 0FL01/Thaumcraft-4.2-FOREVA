package thaumcraft.common.lib.world.dim;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.EntityPermanentItem;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.world.dim.Cell;
import thaumcraft.common.lib.world.dim.GenCommon;

import java.util.Random;

public class GenKeyRoom extends GenCommon {

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

        // Inner void layer (with passage openings)
        for (int a = 2; a <= 14; a++) {
            for (int b = 2; b <= 14; b++) {
                for (int c = 1; c < 12; c++) {
                    if (a == 2 || a == 14 || b == 2 || b == 14) {
                        // Skip openings for connected directions
                        if (a == 2 && b > 3 && b < 12 && cell.west && c < 10) continue;
                        if (a == 14 && b > 3 && b < 12 && cell.east && c < 10) continue;
                        if (b == 2 && a > 3 && a < 12 && cell.north && c < 10) continue;
                        if (b == 14 && a > 3 && a < 12 && cell.south && c < 10) continue;
                        placeBlock(world, x + a, y + c, z + b, VOID, cell);
                    }
                }
            }
        }

        // Inner wall (rock/stone_nospawn with opening pattern)
        for (int a = 3; a <= 13; a++) {
            for (int b = 3; b <= 13; b++) {
                for (int c = 2; c < 11; c++) {
                    if (a == 3 || a == 13 || b == 3 || b == 13) {
                        if (c > 3 && c < 9 && (a == 8 || b == 8)
                                || c > 4 && c < 8 && (a == 7 || b == 7 || a == 9 || b == 9)) {
                            if ((a == 8 || b == 8) && c == 6) continue;
                            placeBlock(world, x + a, y + c, z + b, STONE_NOSPAWN, cell);
                            continue;
                        }
                        placeBlock(world, x + a, y + c, z + b, ROCK, cell);
                    }
                }
            }
        }

        // Floor and ceiling layers
        for (int a = 2; a <= 14; a++) {
            for (int b = 2; b <= 14; b++) {
                // Floor
                placeBlock(world, x + a, y - 1, z + b, BEDROCK, cell);
                placeBlock(world, x + a, y,     z + b, VOID, cell);
                placeBlock(world, x + a, y + 1, z + b, STONE, cell);
                // Ceiling
                placeBlock(world, x + a, y + 13, z + b, BEDROCK, cell);
                placeBlock(world, x + a, y + 12, z + b, VOID, cell);
                placeBlock(world, x + a, y + 11, z + b, STONE, cell);

                // Sloped floor (pyramid steps from walls to center)
                if (a > 1 && a < 15 && b > 1 && b < 15) {
                    int q = Math.min(Math.abs(8 - a), Math.abs(8 - b));
                    for (int g = 0; g < q - 1; g++) {
                        placeBlock(world, x + a, y + 1 + g, z + b, STONE, cell);
                    }
                }
                // Sloped ceiling
                if (a > 3 && a < 13 && b > 3 && b < 13) {
                    int q = Math.min(Math.abs(8 - a), Math.abs(8 - b));
                    for (int g = 0; g < q; g++) {
                        placeBlock(world, x + a, y + 11 - g, z + b, STONE, cell);
                    }
                }
            }
        }

        // Stairways in cardinal directions
        for (int g = 0; g < 5; g++) {
            placeBlock(world, x + 6 + g, y + 2, z + 4,  STAIR_DIR, EnumFacing.NORTH, cell);
            placeBlock(world, x + 6 + g, y + 2, z + 12, STAIR_DIR, EnumFacing.SOUTH, cell);
            placeBlock(world, x + 12,    y + 2, z + 6 + g, STAIR_DIR, EnumFacing.EAST, cell);
            placeBlock(world, x + 4,     y + 2, z + 6 + g, STAIR_DIR, EnumFacing.WEST, cell);
        }

        // Generate connections
        GenCommon.generateConnections(world, random, cx, cz, y, cell, 3, true);

        // Place pedestal with key item
        world.setBlockState(pos(x + 8, y + 2, z + 8), ConfigBlocks.blockEldritch.getStateFromMeta(3), 3);
        EntityPermanentItem entityitem = new EntityPermanentItem(world,
                (double) x + 8.5, (double) y + 3.5, (double) z + 8.5,
                new ItemStack(ConfigItems.itemEldritchObject, 1, 2));
        entityitem.motionY = 0.0;
        entityitem.motionX = 0.0;
        entityitem.motionZ = 0.0;
        world.spawnEntity(entityitem);

        // Spawn eldritch guardians
        int guardianCount = 2 +
                (world.getDifficulty() == EnumDifficulty.HARD ? 2 :
                 world.getDifficulty() == EnumDifficulty.NORMAL ? 1 : 0);
        for (int qq = 0; qq < guardianCount; qq++) {
            EntityEldritchGuardian eg = new EntityEldritchGuardian(world);
            double i1 = (double) x + 8.5 + (double) (MathHelper.getInt(world.rand, 1, 3) * MathHelper.getInt(world.rand, -1, 1));
            double j1 = y + 2;
            double k1 = (double) z + 8.5 + (double) (MathHelper.getInt(world.rand, 1, 3) * MathHelper.getInt(world.rand, -1, 1));
            eg.setPosition(i1, j1, k1);
            eg.onInitialSpawn(world.getDifficultyForLocation(new net.minecraft.util.math.BlockPos(x + 8, y + 2, z + 8)), null);
            eg.setHomePosAndDistance(new net.minecraft.util.math.BlockPos(x + 8, y + 2, z + 8), 16);
            if (qq == 0 && guardianCount >= 4) {
                EntityUtils.makeChampion(eg, true);
            }
            world.spawnEntity(eg);
        }
    }

    private static net.minecraft.util.math.BlockPos pos(int x, int y, int z) {
        return new net.minecraft.util.math.BlockPos(x, y, z);
    }
}
