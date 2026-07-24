package thaumcraft.common.world.aura;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.visnet.VisNetHandler;

/**
 * Minimal TC6 aura handler facade.
 */
public final class AuraHandler {

    private AuraHandler() {
    }

    public static AuraChunk getAuraChunk(int dim, int chunkX, int chunkZ) {
        throw new UnsupportedOperationException("TC6 chunk aura state has no safe TC4 projection");
    }

    public static AuraChunk getAuraChunk(World world, BlockPos pos) {
        throw new UnsupportedOperationException("TC6 chunk aura state has no safe TC4 projection");
    }

    public static void addFlux(World world, BlockPos pos, float amount) {
        throw new UnsupportedOperationException("TC6 flux has no safe TC4 projection");
    }

    public static float drainVis(World world, BlockPos pos, float amount, boolean simulate) {
        if (pos == null || amount < 1.0F) {
            return 0.0F;
        }
        int requested = (int) Math.floor(amount);
        return VisNetHandler.drainVis(world, pos.getX(), pos.getY(), pos.getZ(), requested, simulate);
    }

}
