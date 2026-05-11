package thaumcraft.common.lib.world;

import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

public class ThaumcraftWorldGenerator implements IWorldGenerator {

    public static Biome biomeTaint;
    public static Biome biomeMagicalForest;
    public static Biome biomeEerie;
    public static Biome biomeEldritchLands;

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        // Phase 6: world generation
    }

    public static int getFirstFreeBiomeSlot(int startingId) {
        return startingId;
    }
}
