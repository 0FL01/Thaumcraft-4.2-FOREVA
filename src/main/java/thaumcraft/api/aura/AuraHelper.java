package thaumcraft.api.aura;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.world.aura.AuraHandler;

/**
 * TC6 aura API compatibility facade.
 */
public final class AuraHelper {

    private AuraHelper() {
    }

    public static float getVis(World world, BlockPos pos) {
        return AuraHandler.getAuraChunk(world, pos).getVis();
    }

    public static float drainVis(World world, BlockPos pos, float amount, boolean simulate) {
        AuraChunk chunk = AuraHandler.getAuraChunk(world, pos);
        float drained = Math.min(chunk.getVis(), Math.max(0.0F, amount));
        if (!simulate) {
            chunk.setVis(chunk.getVis() - drained);
        }
        return drained;
    }

    public static void addVis(World world, BlockPos pos, float amount) {
        AuraChunk chunk = AuraHandler.getAuraChunk(world, pos);
        chunk.setVis(chunk.getVis() + amount);
    }

    public static float getFlux(World world, BlockPos pos) {
        return AuraHandler.getAuraChunk(world, pos).getFlux();
    }

    public static void addFlux(World world, BlockPos pos, float amount) {
        AuraHandler.addFlux(world, pos, amount);
    }
}
