package thaumcraft.common.lib.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import java.util.Random;

public class WorldGenSilverwoodTrees extends WorldGenAbstractTree {

    public WorldGenSilverwoodTrees(boolean notify) {
        super(notify);
    }

    public WorldGenSilverwoodTrees(boolean notify, int minHeight, int extraHeight) {
        super(notify);
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        return false;
    }
}
