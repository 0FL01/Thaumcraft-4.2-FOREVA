package thaumcraft.common.lib.world.dim;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.tiles.TileCrystal;
import thaumcraft.common.tiles.TileEldritchCrabSpawner;

import java.util.ArrayList;
import java.util.Random;

public class GenCommon {

    // Decoration coordinate lists (populated during room gen, processed at end)
    public static ArrayList<BlockPos> decoCommon = new ArrayList<>();
    public static ArrayList<BlockPos> crabSpawner = new ArrayList<>();
    public static ArrayList<BlockPos> decoUrn = new ArrayList<>();

    // Block ID constants for the tile-map based room generators
    public static final int BEDROCK        = 1;
    public static final int STONE          = 2;
    public static final int VOID           = 8;
    public static final int AIR_REPL       = 9;
    public static final int STAIR_DIR      = 10;
    public static final int STAIR_DIR_INV  = 11;
    public static final int SLAB           = 12;
    public static final int DOOR_BLOCK     = 15;
    public static final int DOOR_LOCK      = 16;
    public static final int VOID_DOOR      = 17;
    public static final int ROCK           = 18;
    public static final int STONE_NOSPAWN  = 19;
    public static final int STONE_TRAPPED  = 20;
    public static final int CRUST          = 21;
    public static final int BEDROCK_REPL   = 99;

    // Passage connection template (11x11)
    // 0=outer wall, 1=bedrock frame, 2=stone, 3=stair, 4=stair, 5=stair, 6=stair, 8=void, 9=air
    public static final int[][] PAT_CONNECT = {
        {0,1,1,1,1,1,1,1,1,1,0},
        {1,8,8,8,8,8,8,8,8,8,1},
        {1,8,8,2,2,2,2,2,8,8,1},
        {1,8,2,5,9,9,9,6,2,8,1},
        {1,8,2,9,9,9,9,9,2,8,1},
        {1,8,2,9,9,9,9,9,2,8,1},
        {1,8,2,9,9,9,9,9,2,8,1},
        {1,8,2,3,9,9,9,4,2,8,1},
        {1,8,8,2,2,2,2,2,8,8,1},
        {1,8,8,8,8,8,8,8,8,8,1},
        {0,1,1,1,1,1,1,1,1,1,0}
    };

    // Place block using tile-map ID (no direction)
    public static void placeBlock(World world, int x, int y, int z, int b, Cell cell) {
        placeBlock(world, x, y, z, b, EnumFacing.UP, cell);
    }

    // Place block using tile-map ID with direction (for stairs)
    public static void placeBlock(World world, int x, int y, int z, int b, EnumFacing dir, Cell cell) {
        Block block = null;
        int meta = 0;

        switch (b) {
            case 1: // BEDROCK
                if (world.isAirBlock(new BlockPos(x, y, z))) {
                    block = Blocks.STONE;
                }
                break;

            case 2: // STONE
                if (cell.feature != 7 || world.rand.nextInt(3) != 0) {
                    if (world.getBlockState(new BlockPos(x, y, z)).getBlock() == ConfigBlocks.blockEldritchNothing) break;
                    if (world.rand.nextInt(25) == 0) {
                        boolean crab = false;
                        boolean bl = cell.feature == 7 ? true : (crab = world.rand.nextInt(50) == 0);
                        if ((crab && cell.feature == 0) || (crab && cell.feature == 7)) {
                            crabSpawner.add(new BlockPos(x, y, z));
                        } else {
                            decoCommon.add(new BlockPos(x, y, z));
                        }
                    }
                    block = ConfigBlocks.blockCosmeticSolid;
                    meta = 11;
                }
                break;

            case 3: // Stair variant (outer corner connection)
                if (world.rand.nextFloat() < 0.005) {
                    decoUrn.add(new BlockPos(x, y, z));
                }
                block = getStairsBlock();
                switch (dir) {
                    case NORTH: case SOUTH: meta = 1; break;
                    case EAST:  case WEST:  meta = 3; break;
                }
                break;

            case 4: // Stair variant
                if (world.rand.nextFloat() < 0.005) {
                    decoUrn.add(new BlockPos(x, y, z));
                }
                block = getStairsBlock();
                switch (dir) {
                    case NORTH: case SOUTH: meta = 0; break;
                    case EAST:  case WEST:  meta = 2; break;
                }
                break;

            case 5: // Stair variant
                block = getStairsBlock();
                switch (dir) {
                    case NORTH: case SOUTH: meta = 5; break;
                    case EAST:  case WEST:  meta = 7; break;
                }
                break;

            case 6: // Stair variant
                block = getStairsBlock();
                switch (dir) {
                    case NORTH: case SOUTH: meta = 4; break;
                    case EAST:  case WEST:  meta = 6; break;
                }
                break;

            case 7: // Crystal
                block = ConfigBlocks.blockEldritch;
                meta = 4;
                break;

            case 8: // VOID
                block = ConfigBlocks.blockEldritchNothing;
                break;

            case 9: // AIR
                block = Blocks.AIR;
                decoCommon.remove(new BlockPos(x, y, z));
                crabSpawner.remove(new BlockPos(x, y, z));
                decoUrn.remove(new BlockPos(x, y, z));
                break;

            case 10: // STAIR_DIR (base stair)
                block = getStairsBlock();
                switch (dir) {
                    case NORTH: meta = 3; break;
                    case SOUTH: meta = 2; break;
                    case EAST:  meta = 0; break;
                    case WEST:  meta = 1; break;
                }
                break;

            case 11: // STAIR_DIR_INV (inverted stair)
                block = getStairsBlock();
                switch (dir) {
                    case NORTH: meta = 7; break;
                    case SOUTH: meta = 6; break;
                    case EAST:  meta = 4; break;
                    case WEST:  meta = 5; break;
                }
                break;

            case 15: // DOOR_BLOCK
                block = ConfigBlocks.blockEldritch;
                meta = 7;
                decoCommon.remove(new BlockPos(x, y, z));
                crabSpawner.remove(new BlockPos(x, y, z));
                decoUrn.remove(new BlockPos(x, y, z));
                break;

            case 16: // DOOR_LOCK
                block = ConfigBlocks.blockEldritch;
                meta = 8;
                decoCommon.remove(new BlockPos(x, y, z));
                crabSpawner.remove(new BlockPos(x, y, z));
                decoUrn.remove(new BlockPos(x, y, z));
                break;

            case 17: // VOID_DOOR
                block = ConfigBlocks.blockAiry;
                meta = 12;
                break;

            case 18: // ROCK
                if (world.getBlockState(new BlockPos(x, y, z)).getBlock() == ConfigBlocks.blockEldritchNothing) break;
                block = ConfigBlocks.blockCosmeticSolid;
                meta = 12;
                break;

            case 19: // STONE_NOSPAWN
                if (world.getBlockState(new BlockPos(x, y, z)).getBlock() == ConfigBlocks.blockEldritchNothing) break;
                block = ConfigBlocks.blockCosmeticSolid;
                meta = 13;
                break;

            case 20: // STONE_TRAPPED
                if (world.getBlockState(new BlockPos(x, y, z)).getBlock() == ConfigBlocks.blockEldritchNothing) break;
                block = ConfigBlocks.blockEldritch;
                meta = 10;
                break;

            case 21: // CRUST
                if (world.getBlockState(new BlockPos(x, y, z)).getBlock() == ConfigBlocks.blockEldritchNothing) break;
                block = ConfigBlocks.blockCosmeticSolid;
                meta = 14;
                if (world.rand.nextInt(25) == 0) {
                    block = ConfigBlocks.blockEldritch;
                    meta = 4;
                    break;
                }
                if (world.rand.nextInt(25) == 0) {
                    boolean crab = false;
                    boolean bl = cell.feature == 7 ? true :
                            cell.feature == 12 && world.rand.nextBoolean() ? true :
                                    (crab = world.rand.nextInt(25) == 0);
                    if ((crab && cell.feature == 0) || (crab && cell.feature == 7) || (crab && cell.feature == 12)) {
                        crabSpawner.add(new BlockPos(x, y, z));
                    }
                }
                break;

            case 99: // BEDROCK_REPL
                block = Blocks.STONE;
                break;
        }

        if (block != null) {
            int flags = (block == ConfigBlocks.blockEldritchNothing || block == Blocks.STONE || block == Blocks.AIR) ? 0 : 3;
            world.setBlockState(new BlockPos(x, y, z), block.getStateFromMeta(meta), flags);
        }
    }

    // Get the stairs block for eldritch dimension structures
    static Block getStairsBlock() {
        return ConfigBlocks.blockStairsEldritch;
    }

    // Generate obelisk at position (used above portal)
    public static void genObelisk(World world, int x, int y, int z) {
        world.setBlockState(new BlockPos(x, y, z), ConfigBlocks.blockEldritch.getStateFromMeta(1), 3);
        world.setBlockState(new BlockPos(x, y + 1, z), ConfigBlocks.blockEldritch.getStateFromMeta(2), 3);
        world.setBlockState(new BlockPos(x, y + 2, z), ConfigBlocks.blockEldritch.getStateFromMeta(2), 3);
        world.setBlockState(new BlockPos(x, y + 3, z), ConfigBlocks.blockEldritch.getStateFromMeta(2), 3);
        world.setBlockState(new BlockPos(x, y + 4, z), ConfigBlocks.blockEldritch.getStateFromMeta(2), 3);
    }

    // Process all collected decoration positions
    public static void processDecorations(World world) {
        // Process urns: place pedestal + urn
        for (BlockPos cc : decoUrn) {
            if (world.isAirBlock(cc.up())) {
                world.setBlockState(cc, ConfigBlocks.blockCosmeticSolid.getStateFromMeta(15), 3);
                float rr = world.rand.nextFloat();
                int meta = rr < 0.025f ? 2 : (rr < 0.1f ? 1 : 0);
                world.setBlockState(cc.up(), ConfigBlocks.blockLootUrn.getStateFromMeta(meta), 3);
            }
        }

        // Process common decorations: place eldritch crystal/variant blocks
        for (BlockPos cc : decoCommon) {
            int exp = BlockUtils.countExposedSides(world, cc.getX(), cc.getY(), cc.getZ());
            if (exp <= 0 || (exp != 1 && isBedrockShowing(world, cc.getX(), cc.getY(), cc.getZ())))
                continue;
            if (BlockUtils.isBlockAdjacentToAtleast(world, cc.getX(), cc.getY(), cc.getZ(),
                    ConfigBlocks.blockEldritch, Short.MAX_VALUE, 1))
                continue;

            int meta = world.rand.nextInt(3) != 0 ? 4 : (world.rand.nextInt(8) != 0 ? 5 : 10);
            world.setBlockState(cc, ConfigBlocks.blockEldritch.getStateFromMeta(meta), 3);

            // Crystal growth from eldritch crystal
            if (meta == 4 && world.rand.nextInt(12) == 0) {
                for (EnumFacing facing : EnumFacing.VALUES) {
                    BlockPos crystalPos = cc.offset(facing);
                    if (world.isAirBlock(crystalPos)) {
                        world.setBlockState(crystalPos, ConfigBlocks.blockCrystal.getStateFromMeta(7), 3);
                        TileEntity te = world.getTileEntity(crystalPos);
                        if (te instanceof TileCrystal) {
                            ((TileCrystal) te).orientation = (short) facing.ordinal();
                        }
                        break;
                    }
                }
            }
        }

        // Process crab spawners
        for (BlockPos cc : crabSpawner) {
            int exp = BlockUtils.countExposedSides(world, cc.getX(), cc.getY(), cc.getZ());
            if (exp != 1) continue;
            if (BlockUtils.isBlockAdjacentToAtleast(world, cc.getX(), cc.getY(), cc.getZ(),
                    ConfigBlocks.blockEldritch, Short.MAX_VALUE, 1))
                continue;

            world.setBlockState(cc, ConfigBlocks.blockEldritch.getStateFromMeta(9), 3);
            TileEntity te = world.getTileEntity(cc);
            if (te instanceof TileEldritchCrabSpawner) {
                for (EnumFacing facing : EnumFacing.VALUES) {
                    if (world.isAirBlock(cc.offset(facing))) {
                        ((TileEldritchCrabSpawner) te).setFacing((byte) facing.ordinal());
                        break;
                    }
                }
            }
        }

        decoCommon.clear();
        crabSpawner.clear();
        decoUrn.clear();
    }

    // Check if a bedrock/void block is visible adjacent to this position
    static boolean isBedrockShowing(World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        for (EnumFacing dir : EnumFacing.VALUES) {
            if (world.getBlockState(pos.offset(dir)).isFullBlock()) continue;
            BlockPos opposite = pos.offset(dir.getOpposite());
            Block oppositeBlock = world.getBlockState(opposite).getBlock();
            if (oppositeBlock == Blocks.STONE || oppositeBlock == ConfigBlocks.blockEldritchNothing) {
                return true;
            }
        }
        return false;
    }

    // Generate connection tunnels from a room to neighboring cells
    public static void generateConnections(World world, Random random, int cx, int cz, int y, Cell cell, int depth, boolean justthetip) {
        int x = cx * 16;
        int z = cz * 16;

        if (cell.north) {
            for (int d = 0; d <= depth; d++) {
                int wStart = (d == depth && justthetip) ? 2 : ((d == depth - 1 && justthetip) ? 1 : 0);
                int wEnd   = (d == depth && justthetip) ? 9 : ((d == depth - 1 && justthetip) ? 10 : 11);
                for (int w = wStart; w < wEnd; w++) {
                    int hStart = (d == depth && justthetip) ? 2 : ((d == depth - 1 && justthetip) ? 1 : 0);
                    int hEnd   = (d == depth && justthetip) ? 9 : ((d == depth - 1 && justthetip) ? 10 : 11);
                    for (int h = hStart; h < hEnd; h++) {
                        if (d != depth || !justthetip || PAT_CONNECT[h][w] != 8) {
                            placeBlock(world, x + 3 + w, y + 10 - h, z + d, PAT_CONNECT[h][w], EnumFacing.NORTH, cell);
                        }
                    }
                }
            }
        }

        if (cell.south) {
            for (int d = 0; d <= depth; d++) {
                int wStart = (d == depth && justthetip) ? 2 : ((d == depth - 1 && justthetip) ? 1 : 0);
                int wEnd   = (d == depth && justthetip) ? 9 : ((d == depth - 1 && justthetip) ? 10 : 11);
                for (int w = wStart; w < wEnd; w++) {
                    int hStart = (d == depth && justthetip) ? 2 : ((d == depth - 1 && justthetip) ? 1 : 0);
                    int hEnd   = (d == depth && justthetip) ? 9 : ((d == depth - 1 && justthetip) ? 10 : 11);
                    for (int h = hStart; h < hEnd; h++) {
                        if (d != depth || !justthetip || PAT_CONNECT[h][w] != 8) {
                            placeBlock(world, x + 3 + w, y + 10 - h, z + 16 - d, PAT_CONNECT[h][w], EnumFacing.SOUTH, cell);
                        }
                    }
                }
            }
        }

        if (cell.east) {
            for (int d = 0; d <= depth; d++) {
                int wStart = (d == depth && justthetip) ? 2 : ((d == depth - 1 && justthetip) ? 1 : 0);
                int wEnd   = (d == depth && justthetip) ? 9 : ((d == depth - 1 && justthetip) ? 10 : 11);
                for (int w = wStart; w < wEnd; w++) {
                    int hStart = (d == depth && justthetip) ? 2 : ((d == depth - 1 && justthetip) ? 1 : 0);
                    int hEnd   = (d == depth && justthetip) ? 9 : ((d == depth - 1 && justthetip) ? 10 : 11);
                    for (int h = hStart; h < hEnd; h++) {
                        if (d != depth || !justthetip || PAT_CONNECT[h][w] != 8) {
                            placeBlock(world, x + 16 - d, y + 10 - h, z + 3 + w, PAT_CONNECT[h][w], EnumFacing.EAST, cell);
                        }
                    }
                }
            }
        }

        if (cell.west) {
            for (int d = 0; d <= depth; d++) {
                int wStart = (d == depth && justthetip) ? 2 : ((d == depth - 1 && justthetip) ? 1 : 0);
                int wEnd   = (d == depth && justthetip) ? 9 : ((d == depth - 1 && justthetip) ? 10 : 11);
                for (int w = wStart; w < wEnd; w++) {
                    int hStart = (d == depth && justthetip) ? 2 : ((d == depth - 1 && justthetip) ? 1 : 0);
                    int hEnd   = (d == depth && justthetip) ? 9 : ((d == depth - 1 && justthetip) ? 10 : 11);
                    for (int h = hStart; h < hEnd; h++) {
                        if (d != depth || !justthetip || PAT_CONNECT[h][w] != 8) {
                            placeBlock(world, x + d, y + 10 - h, z + 3 + w, PAT_CONNECT[h][w], EnumFacing.WEST, cell);
                        }
                    }
                }
            }
        }
    }
}
