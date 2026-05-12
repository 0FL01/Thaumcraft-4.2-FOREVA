package thaumcraft.common.lib.world;

import java.util.HashMap;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.fml.common.IWorldGenerator;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.world.biomes.BiomeHandler;
import thaumcraft.common.lib.world.biomes.BiomeTaint;
import thaumcraft.common.lib.world.dim.MazeHandler;

public class ThaumcraftWorldGenerator implements IWorldGenerator {

    public static Biome biomeTaint;
    public static Biome biomeMagicalForest;
    public static Biome biomeEerie;
    public static Biome biomeEldritchLands;
    public static HashMap<Integer, Integer> dimensionBlacklist = new HashMap<>();
    public static HashMap<Integer, Integer> biomeBlacklist = new HashMap<>();

    public static void initBiomes() {
        biomeMagicalForest = new thaumcraft.common.lib.world.biomes.BiomeMagicalForest();
        biomeTaint = new thaumcraft.common.lib.world.biomes.BiomeTaint();
        biomeEerie = new thaumcraft.common.lib.world.biomes.BiomeEerie();
        biomeEldritchLands = new thaumcraft.common.lib.world.biomes.BiomeEldritch();
    }

    public static void registerBiomeManager() {
        BiomeTaint.blobs = new WorldGenBlockBlob(ConfigBlocks.blockTaint, 0);
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
        int dim = world.provider.getDimension();

        // Check dimension blacklist
        if (dimensionBlacklist.containsKey(dim) && dimensionBlacklist.get(dim) >= 0) return;

        // Surface generation only
        if (world.provider.isNether() || world.provider.getDimensionType() == net.minecraft.world.DimensionType.THE_END) return;

        int x = chunkX * 16 + 8;
        int z = chunkZ * 16 + 8;

        Biome biome = world.getBiome(new BlockPos(x, 64, z));
        int biomeId = Biome.getIdForBiome(biome);

        // Check biome blacklist
        if (biomeBlacklist.containsKey(biomeId) && biomeBlacklist.get(biomeId) >= 2) return;

        // Generate ores
        if (Config.genCinnibar || Config.regenCinnibar) {
            generateOre(world, random, x, z, ConfigBlocks.blockCustomOre.getStateFromMeta(0), 4, 12, 10, 0, 32);
        }
        if (Config.genAmber || Config.regenAmber) {
            generateOre(world, random, x, z, ConfigBlocks.blockCustomOre.getStateFromMeta(7), 4, 8, 8, 0, 32);
        }
        if (Config.genInfusedStone || Config.regenInfusedStone) {
            for (int i = 1; i <= 6; i++) {
                generateOre(world, random, x, z, ConfigBlocks.blockCustomOre.getStateFromMeta(i), 6, 4, 6, 0, 48);
            }
        }

        // Generate trees
        if ((Config.genTrees || Config.regenTrees) && biome == biomeMagicalForest) {
            generateGreatwood(world, random, x, z, biome);
            generateSilverwood(world, random, x, z, biome);
        }

        // Generate structures (surface)
        if ((Config.genStructure || Config.regenStructure) && dim == 0) {
            generateStructures(world, random, x, z, biome);
        }
    }

    private void generateOre(World world, Random rand, int x, int z, net.minecraft.block.state.IBlockState ore, int veinSize, int veinsPerChunk, int chance, int minY, int maxY) {
        if (veinsPerChunk == 0) return;
        for (int i = 0; i < veinsPerChunk; i++) {
            if (rand.nextInt(chance) != 0) continue;
            int bx = x + rand.nextInt(16);
            int bz = z + rand.nextInt(16);
            int by = minY + rand.nextInt(maxY - minY);
            new WorldGenMinable(ore, veinSize).generate(world, rand, new BlockPos(bx, by, bz));
        }
    }

    public static void generateGreatwood(World world, Random rand, int x, int z, Biome biome) {
        if (!Config.genTrees && !Config.regenTrees) return;
        float chance = BiomeHandler.getBiomeSupportsGreatwood(biome);
        if (chance > 0 && rand.nextFloat() < chance) {
            int bx = x + rand.nextInt(16) + 8;
            int bz = z + rand.nextInt(16) + 8;
            BlockPos pos = world.getHeight(new BlockPos(bx, 0, bz));
            new WorldGenGreatwoodTrees(false).generate(world, rand, pos);
        }
    }

    public static void generateSilverwood(World world, Random rand, int x, int z, Biome biome) {
        int bx = x + rand.nextInt(16) + 8;
        int bz = z + rand.nextInt(16) + 8;
        BlockPos pos = world.getHeight(new BlockPos(bx, 0, bz));

        boolean shouldGen = biome == biomeMagicalForest
                || biome == biomeTaint
                || !BiomeDictionary.hasType(biome, BiomeDictionary.Type.MAGICAL)
                && biome != Biome.getBiome(0)  // ocean
                && biome != Biome.getBiome(1); // plains

        if (shouldGen && rand.nextInt(100) < 5) {
            new WorldGenSilverwoodTrees(false, 8, 5).generate(world, rand, pos);
        }
    }

    public static void generateFlowers(World world, Random rand, int x, int z, int flowerType) {
        int bx = x + rand.nextInt(16) + 8;
        int bz = z + rand.nextInt(16) + 8;
        BlockPos pos = world.getHeight(new BlockPos(bx, 0, bz));
        new WorldGenCustomFlowers(ConfigBlocks.blockCustomPlant.getStateFromMeta(flowerType))
                .generate(world, rand, pos);
    }

    private void generateStructures(World world, Random rand, int x, int z, Biome biome) {
        if (biome == biomeMagicalForest || biome == biomeTaint) {
            // Barrow mounds
            if (rand.nextInt(400) == 0) {
                int bx = x + rand.nextInt(16) + 8;
                int bz = z + rand.nextInt(16) + 8;
                BlockPos pos = world.getHeight(new BlockPos(bx, 0, bz));
                new WorldGenMound().generate(world, rand, pos);
            }
        }

        // Eldritch rings (sparse)
        if (rand.nextInt(800) == 0 && !MazeHandler.mazesInRange(x >> 4, z >> 4, 32, 1)) {
            int bx = x + rand.nextInt(16) + 8;
            int bz = z + rand.nextInt(16) + 8;
            BlockPos pos = world.getHeight(new BlockPos(bx, 0, bz));
            new WorldGenEldritchRing().generate(world, rand, pos);
        }

        // Hilltop stones
        if (rand.nextInt(600) == 0) {
            int bx = x + rand.nextInt(16) + 8;
            int bz = z + rand.nextInt(16) + 8;
            BlockPos pos = world.getHeight(new BlockPos(bx, 0, bz));
            new WorldGenHilltopStones().generate(world, rand, pos);
        }
    }

    public static int getFirstFreeBiomeSlot(int startingId) {
        return startingId;
    }

    public static void addDimBlacklist(int dim, int level) {
        dimensionBlacklist.put(dim, level);
    }

    public static int getDimBlacklist(int dim) {
        return dimensionBlacklist.getOrDefault(dim, -1);
    }

    public static void addBiomeBlacklist(int biome, int level) {
        biomeBlacklist.put(biome, level);
    }

    public static int getBiomeBlacklist(int biome) {
        return biomeBlacklist.getOrDefault(biome, -1);
    }
}
