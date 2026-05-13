package thaumcraft.common.lib.world;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.world.dim.MazeHandler;
import thaumcraft.common.lib.world.dim.MazeThread;
import java.util.Random;

public class WorldGenEldritchRing extends WorldGenerator {
    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        if (world.provider.getDimension() != 0) return false;

        // Place obsidian ring
        int radius = 3;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz >= radius * radius - 2 && dx * dx + dz * dz <= radius * radius + 2) {
                    BlockPos bp = pos.add(dx, 0, dz);
                    world.setBlockState(bp, Blocks.OBSIDIAN.getDefaultState(), 2);
                }
            }
        }

        // Place eldritch portal in center
        world.setBlockState(pos, ConfigBlocks.blockEldritchPortal.getDefaultState(), 2);

        // Trigger maze generation
        int cx = pos.getX() >> 4;
        int cz = pos.getZ() >> 4;
        if (!MazeHandler.mazesInRange(cx, cz, 32, 32)) {
            Thread mazeThread = new Thread(new MazeThread(cx, cz, 32, 32, world.getSeed()));
            mazeThread.start();
        }

        return true;
    }
}
