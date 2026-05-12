package thaumcraft.common.lib.utils;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class Utils {

    public static void generateVisEffect(int dim, int x, int y, int z, int x2, int y2, int z2, int color) {
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
}
