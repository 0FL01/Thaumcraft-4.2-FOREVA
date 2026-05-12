package thaumcraft.common.lib.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
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
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.world.biomes.BiomeHandler;
import thaumcraft.common.lib.world.biomes.BiomeTaint;
import thaumcraft.common.lib.world.dim.MazeHandler;
import thaumcraft.common.tiles.TileNode;

public class ThaumcraftWorldGenerator implements IWorldGenerator {

    public static Biome biomeTaint;
    public static Biome biomeMagicalForest;
    public static Biome biomeEerie;
    public static Biome biomeEldritchLands;
    public static HashMap<Integer, Integer> dimensionBlacklist = new HashMap<>();
    public static HashMap<Integer, Integer> biomeBlacklist = new HashMap<>();

    // Aspect caches for node generation (lazy-init)
    private static ArrayList<Aspect> basicAspects = new ArrayList<>();
    private static ArrayList<Aspect> complexAspects = new ArrayList<>();
    private static boolean aspectsInitialized = false;

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

    /**
     * Creates an aura node TileEntity at the given position.
     * If the position is air, places BlockAiry(meta 0) first.
     * Then looks for a TileNode and sets its type/modifier/aspects.
     */
    public static void createNodeAt(World world, BlockPos pos, NodeType nt, NodeModifier nm, AspectList al) {
        if (world.isAirBlock(pos)) {
            world.setBlockState(pos, ConfigBlocks.blockAiry.getStateFromMeta(0), 0);
        }
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileNode) {
            ((TileNode) te).setNodeType(nt);
            ((TileNode) te).setNodeModifier(nm);
            ((TileNode) te).setAspects(al);
        }
        world.markBlockRangeForRenderUpdate(pos, pos);
    }

    /**
     * Creates a random node at the given position.
     * The node type, modifier, and aspect list are determined by biome aura,
     * surrounding blocks, and random chance.
     *
     * @param world     the world
     * @param pos       the position
     * @param random    the RNG
     * @param silverwood if true, node type will be PURE and aura is quartered
     * @param eerie     if true, node type will be DARK
     * @param small     if true, aura is quartered (used for small nodes like totems)
     */
    public static void createRandomNodeAt(World world, BlockPos pos, Random random, boolean silverwood, boolean eerie, boolean small) {
        if (!aspectsInitialized) {
            for (Aspect as : Aspect.aspects.values()) {
                if (as.getComponents() != null) {
                    complexAspects.add(as);
                } else {
                    basicAspects.add(as);
                }
            }
            aspectsInitialized = true;
        }

        NodeType type = NodeType.NORMAL;
        if (silverwood) {
            type = NodeType.PURE;
        } else if (eerie) {
            type = NodeType.DARK;
        } else if (random.nextInt(Config.specialNodeRarity) == 0) {
            switch (random.nextInt(10)) {
                case 0:
                case 1:
                case 2:
                    type = NodeType.DARK;
                    break;
                case 3:
                case 4:
                case 5:
                    type = NodeType.UNSTABLE;
                    break;
                case 6:
                case 7:
                case 8:
                    type = NodeType.PURE;
                    break;
                case 9:
                    type = NodeType.HUNGRY;
                    break;
            }
        }

        NodeModifier modifier = null;
        if (random.nextInt(Config.specialNodeRarity / 2) == 0) {
            switch (random.nextInt(3)) {
                case 0:
                    modifier = NodeModifier.BRIGHT;
                    break;
                case 1:
                    modifier = NodeModifier.PALE;
                    break;
                case 2:
                    modifier = NodeModifier.FADING;
                    break;
            }
        }

        Biome bg = world.getBiome(pos);
        int baura = BiomeHandler.getBiomeAura(bg);

        if (type != NodeType.PURE && Biome.getIdForBiome(bg) == Biome.getIdForBiome(biomeTaint)) {
            baura = (int) ((float) baura * 1.5f);
            if (random.nextBoolean()) {
                type = NodeType.TAINTED;
                baura = (int) ((float) baura * 1.5f);
            }
        }

        if (silverwood || small) {
            baura /= 4;
        }

        int value = random.nextInt(baura / 2) + baura / 2;
        Aspect ra = BiomeHandler.getRandomBiomeTag(bg, random);
        AspectList al = new AspectList();
        if (ra != null) {
            al.add(ra, 2);
        } else {
            Aspect aa = complexAspects.get(random.nextInt(complexAspects.size()));
            al.add(aa, 1);
            aa = basicAspects.get(random.nextInt(basicAspects.size()));
            al.add(aa, 1);
        }

        for (int a2 = 0; a2 < 3; a2++) {
            if (!random.nextBoolean()) continue;
            if (random.nextInt(Config.specialNodeRarity) == 0) {
                Aspect aa = complexAspects.get(random.nextInt(complexAspects.size()));
                al.merge(aa, 1);
            } else {
                Aspect aa = basicAspects.get(random.nextInt(basicAspects.size()));
                al.merge(aa, 1);
            }
        }

        // Type-specific bonus aspects
        if (type == NodeType.HUNGRY) {
            al.merge(Aspect.HUNGER, 2);
            if (random.nextBoolean()) {
                al.merge(Aspect.GREED, 1);
            }
        } else if (type == NodeType.PURE) {
            if (random.nextBoolean()) {
                al.merge(Aspect.LIFE, 2);
            } else {
                al.merge(Aspect.ORDER, 2);
            }
        } else if (type == NodeType.DARK) {
            if (random.nextBoolean()) al.merge(Aspect.DEATH, 1);
            if (random.nextBoolean()) al.merge(Aspect.UNDEAD, 1);
            if (random.nextBoolean()) al.merge(Aspect.ENTROPY, 1);
            if (random.nextBoolean()) al.merge(Aspect.DARKNESS, 1);
        }

        // Scan 11x11x11 surroundings for water/lava/stone/leaves bonuses
        int water = 0, lava = 0, stone = 0, foliage = 0;
        for (int xx = -5; xx <= 5; xx++) {
            for (int yy = -5; yy <= 5; yy++) {
                for (int zz = -5; zz <= 5; zz++) {
                    BlockPos bp = pos.add(xx, yy, zz);
                    IBlockState state = world.getBlockState(bp);
                    Block bi = state.getBlock();
                    if (state.getMaterial() == Material.WATER) {
                        water++;
                    } else if (state.getMaterial() == Material.LAVA) {
                        lava++;
                    } else if (bi == Blocks.STONE) {
                        stone++;
                    }
                    if (bi.isLeaves(state, world, bp)) {
                        foliage++;
                    }
                }
            }
        }

        if (water > 100) al.merge(Aspect.WATER, 1);
        if (lava > 100) {
            al.merge(Aspect.FIRE, 1);
            al.merge(Aspect.EARTH, 1);
        }
        if (stone > 500) al.merge(Aspect.EARTH, 1);
        if (foliage > 100) al.merge(Aspect.PLANT, 1);

        // Spread and normalize aspect values
        int[] spread = new int[al.size()];
        float total = 0.0f;
        for (int a = 0; a < spread.length; a++) {
            spread[a] = al.getAmount(al.getAspectsSorted()[a]) == 2 ? 50 + random.nextInt(25) : 25 + random.nextInt(50);
            total += (float) spread[a];
        }
        for (int a = 0; a < spread.length; a++) {
            al.merge(al.getAspectsSorted()[a], (int) ((float) spread[a] / total * (float) value));
        }

        createNodeAt(world, pos, type, modifier, al);
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
