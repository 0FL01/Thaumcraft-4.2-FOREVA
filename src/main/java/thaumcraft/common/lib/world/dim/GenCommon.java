package thaumcraft.common.lib.world.dim;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigBlocks;
import java.util.Random;

public class GenCommon {
    public static java.util.ArrayList decoCommon = new java.util.ArrayList();
    public static java.util.ArrayList crabSpawner = new java.util.ArrayList();
    public static java.util.ArrayList decoUrn = new java.util.ArrayList();

    public static void placeBlock(World world, int x, int y, int z, Block block, int meta, Cell cell) {
        if (x < -30000000 || z < -30000000 || x >= 30000000 || z >= 30000000) return;
        if (y < 0 || y >= 256) return;
        world.setBlockState(new BlockPos(x, y, z), block.getStateFromMeta(meta), 2);
    }

    public static void processDecorations(World world, Random rand, int x, int y, int z) {
        // Stub: will place decor
    }

    public static final int ROCK = 0;
    public static final int STONE = 0;
    public static final int CRUST = 0;
    public static final int STONE_NOSPAWN = 0;
    public static final int STONE_TRAPPED = 0;
}
