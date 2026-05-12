package thaumcraft.common.lib.utils;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.WorldCoordinates;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXVisDrain;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    private static final Map<WorldCoordinates, Long> effectBuffer = new HashMap<>();

    public static void generateVisEffect(World world, BlockPos from, BlockPos to, int color) {
        if (world.isRemote) return;

        int dim = world.provider.getDimension();
        WorldCoordinates key = new WorldCoordinates(from.getX(), from.getY(), from.getZ(), dim);
        long now = System.currentTimeMillis();
        Long last = effectBuffer.get(key);

        // Rate limit: max one packet per 500ms per source position
        if (last != null && now - last < 500L) {
            return;
        }
        effectBuffer.put(key, now + 500L + (long)(Math.random() * 100.0));

        // Cleanup stale entries (older than 10 seconds)
        if (effectBuffer.size() > 1000) {
            effectBuffer.values().removeIf(v -> now - v > 10000L);
        }

        PacketHandler.INSTANCE.sendToAllAround(
            new PacketFXVisDrain(from, to, color),
            new NetworkRegistry.TargetPoint(dim, from.getX() + 0.5, from.getY() + 0.5, from.getZ() + 0.5, 64.0));
    }

    public static void setPrivateFinalValue(Class<?> cls, Object instance, Object value, String... fieldNames) {
    }

    /** Set the biome at a given x,z position in the world (used for taint spread in Eldritch dimension). */
    public static void setBiomeAt(World world, int x, int z, Biome biome) {
        int cx = x >> 4;
        int cz = z >> 4;
        Chunk chunk = world.getChunkProvider().provideChunk(cx, cz);
        if (chunk == null) return;
        byte[] biomes = chunk.getBiomeArray();
        int index = ((z & 15) << 4) | (x & 15);
        biomes[index] = (byte) Biome.getIdForBiome(biome);
        chunk.setBiomeArray(biomes);
    }

    public static byte pack(boolean[] bits) {
        byte v = 0;
        for (int i = 0; i < Math.min(bits.length, 8); i++) {
            if (bits[i]) v |= (1 << i);
        }
        return v;
    }

    public static boolean[] unpack(byte v) {
        boolean[] bits = new boolean[8];
        for (int i = 0; i < 8; i++) {
            bits[i] = (v & (1 << i)) != 0;
        }
        return bits;
    }

    public static boolean isWoodLog(net.minecraft.world.IBlockAccess world, BlockPos pos) {
        net.minecraft.block.Block block = world.getBlockState(pos).getBlock();
        if (block == net.minecraft.init.Blocks.AIR) return false;
        // Check if the block can sustain leaves (all vanilla and most mod logs do this)
        if (block.canSustainLeaves(world.getBlockState(pos), world, pos)) return true;
        return false;
    }
}
