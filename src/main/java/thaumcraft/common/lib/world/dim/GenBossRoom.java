package thaumcraft.common.lib.world.dim;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import thaumcraft.common.tiles.TileEldritchLock;

import java.util.Random;

public class GenBossRoom extends GenCommon {

    // 7x7 doorway pattern: 0=air, 1=lock, 2=door, 9=void_door
    static final int[][] PAT_DOORWAY = {
        {0,2,2,2,2,2,0},
        {2,2,9,9,9,2,2},
        {2,9,9,9,9,9,2},
        {2,9,9,1,9,9,2},
        {2,9,9,9,9,9,2},
        {2,2,9,9,9,2,2},
        {0,2,2,2,2,2,0}
    };

    static void generateRoom(World world, Random random, int cx, int cz, int y, Cell cell) {
        int x = cx * 16;
        int z = cz * 16;

        // Delegate to Gen2x2 sub-rooms based on feature
        switch (cell.feature) {
            case 2: Gen2x2.generateUpperLeft(world, random, cx, cz, y, cell); break;
            case 3: Gen2x2.generateUpperRight(world, random, cx, cz, y, cell); break;
            case 4: Gen2x2.generateLowerLeft(world, random, cx, cz, y, cell); break;
            case 5: Gen2x2.generateLowerRight(world, random, cx, cz, y, cell); break;
        }

        EnumFacing dir = null;
        if (cell.north) dir = EnumFacing.NORTH;
        if (cell.south) dir = EnumFacing.SOUTH;
        if (cell.east) dir = EnumFacing.EAST;
        if (cell.west) dir = EnumFacing.WEST;
        if (dir == null) return;

        // Place doorway pattern on the connected side
        for (int a = 0; a < 7; a++) {
            for (int b = 0; b < 7; b++) {
                int xx = dir == EnumFacing.EAST ? x + 13 : dir == EnumFacing.WEST ? x + 3 : x + 5 + a;
                int zz = dir == EnumFacing.NORTH ? z + 3 : dir == EnumFacing.SOUTH ? z + 13 : z + 5 + a;

                switch (PAT_DOORWAY[a][b]) {
                    case 1: // Lock block
                        placeBlock(world, xx, y + 2 + b, zz, DOOR_LOCK, cell);
                        TileEntity t = world.getTileEntity(pos(xx, y + 2 + b, zz));
                        if (t instanceof TileEldritchLock) {
                            ((TileEldritchLock) t).setFacing((byte) dir.ordinal());
                        }
                        break;
                    case 2: // Door block
                        placeBlock(world, xx, y + 2 + b, zz, DOOR_BLOCK, cell);
                        break;
                    case 9: // Void door
                        placeBlock(world, xx, y + 2 + b, zz, VOID_DOOR, cell);
                        break;
                }
            }
        }
    }

    private static net.minecraft.util.math.BlockPos pos(int x, int y, int z) {
        return new net.minecraft.util.math.BlockPos(x, y, z);
    }
}
