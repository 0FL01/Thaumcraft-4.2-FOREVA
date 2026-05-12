package thaumcraft.common.lib.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import java.util.Random;

public class WorldGenManaPods extends WorldGenerator {

    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        return false;
    }
}
