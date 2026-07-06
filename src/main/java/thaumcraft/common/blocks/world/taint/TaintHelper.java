package thaumcraft.common.blocks.world.taint;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.blocks.BlockTaintFibres;

/**
 * TC6 taint helper facade backed by this port's TC4 taint fibres logic.
 */
public final class TaintHelper {

    private TaintHelper() {
    }

    public static void addTaintSeed(World world, BlockPos pos) {
        if (world != null && pos != null) {
            BlockTaintFibres.spreadFibres(world, pos);
        }
    }

    public static void spreadFibres(World world, BlockPos pos, boolean notify) {
        if (world != null && pos != null) {
            BlockTaintFibres.spreadFibres(world, pos);
        }
    }
}
