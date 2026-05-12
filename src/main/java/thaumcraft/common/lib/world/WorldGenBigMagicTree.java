package thaumcraft.common.lib.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import java.util.Random;

public class WorldGenBigMagicTree extends WorldGenAbstractTree {

    public WorldGenBigMagicTree(boolean notify) {
        super(notify);
    }

    @Override
    public boolean generate(World world, Random rand, BlockPos pos) {
        return false;
    }
}
