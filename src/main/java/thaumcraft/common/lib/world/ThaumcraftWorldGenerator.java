package thaumcraft.common.lib.world;

import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.fml.common.IWorldGenerator;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.world.biomes.*;
import thaumcraft.common.lib.world.biomes.BiomeTaint;

public class ThaumcraftWorldGenerator implements IWorldGenerator {

    public static Biome biomeTaint;
    public static Biome biomeMagicalForest;
    public static Biome biomeEerie;
    public static Biome biomeEldritchLands;

    public static void initBiomes() {
        biomeMagicalForest = new BiomeMagicalForest();
        biomeTaint = new BiomeTaint();
        biomeEerie = new BiomeEerie();
        biomeEldritchLands = new BiomeEldritch();
    }

    public static void registerBiomeManager() {
        BiomeTaint.blobs = new net.minecraft.world.gen.feature.WorldGenBlockBlob(
                thaumcraft.common.config.ConfigBlocks.blockTaint, 0);
        BiomeManager.addBiome(BiomeManager.BiomeType.WARM,
                new BiomeManager.BiomeEntry(biomeMagicalForest, Config.biomeMagicalForestWeight));
        BiomeManager.addBiome(BiomeManager.BiomeType.COOL,
                new BiomeManager.BiomeEntry(biomeMagicalForest, Config.biomeMagicalForestWeight));
        BiomeManager.addBiome(BiomeManager.BiomeType.WARM,
                new BiomeManager.BiomeEntry(biomeTaint, Config.biomeTaintWeight));
        BiomeManager.addBiome(BiomeManager.BiomeType.COOL,
                new BiomeManager.BiomeEntry(biomeTaint, Config.biomeTaintWeight));
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        // Phase 7: world generation
    }

    public static int getFirstFreeBiomeSlot(int startingId) {
        return startingId;
    }
}
