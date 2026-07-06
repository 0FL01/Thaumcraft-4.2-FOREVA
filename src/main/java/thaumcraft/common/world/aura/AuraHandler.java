package thaumcraft.common.world.aura;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Minimal TC6 aura handler facade.
 */
public final class AuraHandler {

    private static final Map<String, AuraChunk> AURA = new ConcurrentHashMap<>();

    private AuraHandler() {
    }

    public static AuraChunk getAuraChunk(int dim, int chunkX, int chunkZ) {
        String key = dim + ":" + chunkX + ":" + chunkZ;
        AuraChunk chunk = AURA.get(key);
        if (chunk == null) {
            AuraChunk created = new AuraChunk();
            AuraChunk existing = AURA.putIfAbsent(key, created);
            chunk = existing == null ? created : existing;
        }
        return chunk;
    }

    public static AuraChunk getAuraChunk(World world, BlockPos pos) {
        return getAuraChunk(getDimension(world), pos.getX() >> 4, pos.getZ() >> 4);
    }

    public static void addFlux(World world, BlockPos pos, float amount) {
        AuraChunk chunk = getAuraChunk(world, pos);
        chunk.setFlux(chunk.getFlux() + amount);
    }

    private static int getDimension(World world) {
        return world != null && world.provider != null ? world.provider.getDimension() : 0;
    }
}
