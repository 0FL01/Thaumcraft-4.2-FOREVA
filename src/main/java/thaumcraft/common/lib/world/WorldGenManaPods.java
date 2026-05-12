package thaumcraft.common.lib.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import thaumcraft.common.config.ConfigBlocks;
import java.util.Random;

public class WorldGenManaPods extends WorldGenerator {
    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        for (int i = 0; i < 20; i++) {
            int x = pos.getX() + rand.nextInt(8) - rand.nextInt(8);
            int z = pos.getZ() + rand.nextInt(8) - rand.nextInt(8);
            BlockPos bp = world.getHeight(new BlockPos(x, 0, z));

            // Must be adjacent to magical log or oak/spruce log
            boolean hasWood = false;
            for (EnumFacing face : EnumFacing.values()) {
                BlockPos neighbor = bp.offset(face);
                if (world.getBlockState(neighbor).getBlock().isWood(world, neighbor)) {
                    hasWood = true;
                    break;
                }
            }
            if (!hasWood) continue;

            // Check biome
            if (!world.getBiome(bp).getBiomeName().equals("Magical Forest")) continue;

            // Place mana pod stem
            if (world.isAirBlock(bp)) {
                world.setBlockState(bp, ConfigBlocks.blockManaPod.getStateFromMeta(0), 2);
                return true;
            }
        }
        return false;
    }
}
