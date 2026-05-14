package thaumcraft.common.lib.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import thaumcraft.common.config.ConfigBlocks;
import java.util.Random;

public class WorldGenManaPods extends WorldGenerator {
    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        int baseX = pos.getX();
        int baseZ = pos.getZ();
        int x = baseX;
        int z = baseZ;

        for (int y = pos.getY(); y < 128; y++) {
            BlockPos column = new BlockPos(x, 0, z);
            if (!world.isBlockLoaded(column, false)) {
                x = baseX + rand.nextInt(4) - rand.nextInt(4);
                z = baseZ + rand.nextInt(4) - rand.nextInt(4);
                continue;
            }

            if (y >= Math.min(128, world.getHeight(column).getY())) break;

            BlockPos bp = new BlockPos(x, y, z);
            if (!world.isAreaLoaded(bp.down(), bp.up(), false)) continue;

            if (canGenerateAt(world, bp)) {
                world.setBlockState(bp, ConfigBlocks.blockManaPod.getStateFromMeta(2 + rand.nextInt(5)), 2);
                return true;
            }

            x = baseX + rand.nextInt(4) - rand.nextInt(4);
            z = baseZ + rand.nextInt(4) - rand.nextInt(4);
        }
        return true;
    }

    private boolean canGenerateAt(World world, BlockPos pos) {
        if (!BiomeDictionary.hasType(world.getBiome(pos), BiomeDictionary.Type.MAGICAL)) return false;
        if (!world.isAirBlock(pos) || !world.isAirBlock(pos.down())) return false;
        BlockPos support = pos.up();
        return world.getBlockState(support).getBlock().isWood(world, support);
    }
}
