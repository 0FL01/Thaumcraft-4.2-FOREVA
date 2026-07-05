package thaumcraft.common.lib.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.fml.common.IWorldGenerator;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.world.biomes.BiomeHandler;
import thaumcraft.common.lib.world.biomes.BiomeTaint;
import thaumcraft.common.lib.world.dim.MazeHandler;
import thaumcraft.common.lib.world.dim.MazeThread;
import thaumcraft.common.tiles.TileNode;

public class ThaumcraftWorldGenerator implements IWorldGenerator {

    public static Biome biomeTaint;
    public static Biome biomeMagicalForest;
    public static Biome biomeEerie;
    public static Biome biomeEldritchLands;
    public static HashMap<Integer, Integer> dimensionBlacklist = new HashMap<>();
    public static HashMap<Integer, Integer> biomeBlacklist = new HashMap<>();
    private final HashMap<Integer, Boolean> structureNode = new HashMap<>();

    // Debug flag: set true to log every node creation/update attempt
    public static boolean logNodeGen = false;

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
     * Returns false if the position is occupied by a non-replaceable block
     * that does not already carry a TileNode.
     * <p>
     * If the position is air or replaceable, places BlockAiry(meta 0) first.
     * If the block at the position already has a TileNode (e.g.
     * blockCosmeticSolid(8) or blockMagicalLog(2)), reuses it.
     * After setting type/modifier/aspects, marks the TE dirty and notifies
     * clients.
     */
    public static boolean createNodeAt(World world, BlockPos pos, NodeType nt, NodeModifier nm, AspectList al) {
        IBlockState oldState = world.getBlockState(pos);
        Block oldBlock = oldState.getBlock();
        boolean isAir = world.isAirBlock(pos);
        boolean isReplaceable = oldBlock.isReplaceable(world, pos);
        boolean alreadyHasTileNode = world.getTileEntity(pos) instanceof TileNode;

        // Already a node here — just update the TE
        if (oldBlock == ConfigBlocks.blockAiry && oldState == ConfigBlocks.blockAiry.getStateFromMeta(0)) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileNode) {
                TileNode node = (TileNode) te;
                node.setNodeType(nt);
                node.setNodeModifier(nm);
                node.setAspects(al);
                node.markDirty();
                world.notifyBlockUpdate(pos, oldState, oldState, 3);
                if (ThaumcraftWorldGenerator.logNodeGen) {
                    Thaumcraft.log.debug("Node updated at existing blockAiry(0) {}: type={} mod={} aspects={}",
                            pos, nt, nm, al.size());
                }
                return true;
            }
        }

        // Block already carries a TileNode (e.g. blockCosmeticSolid(8) or knot log)
        if (alreadyHasTileNode) {
            TileNode node = (TileNode) world.getTileEntity(pos);
            node.setNodeType(nt);
            node.setNodeModifier(nm);
            node.setAspects(al);
            node.markDirty();
            world.notifyBlockUpdate(pos, oldState, oldState, 3);
            if (ThaumcraftWorldGenerator.logNodeGen) {
                Thaumcraft.log.debug("Node updated on existing TE-carrying block {} at {}: type={} mod={} aspects={}",
                        oldState, pos, nt, nm, al.size());
            }
            return true;
        }

        // Air or replaceable — place blockAiry(0) which creates a TileNode
        if (isAir || isReplaceable) {
            world.setBlockState(pos, ConfigBlocks.blockAiry.getStateFromMeta(0), 3);
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileNode) {
                TileNode node = (TileNode) te;
                node.setNodeType(nt);
                node.setNodeModifier(nm);
                node.setAspects(al);
                node.markDirty();
                world.notifyBlockUpdate(pos, ConfigBlocks.blockAiry.getStateFromMeta(0),
                        ConfigBlocks.blockAiry.getStateFromMeta(0), 3);
                if (ThaumcraftWorldGenerator.logNodeGen) {
                    Thaumcraft.log.debug("Node created at {} (was {}): type={} mod={} aspects={}",
                            pos, oldState.getBlock().getRegistryName(), nt, nm, al.size());
                }
                return true;
            }
            // TileNode wasn't created — unexpected
            Thaumcraft.log.warn("Failed to create TileNode at {} after placing blockAiry, TE={}",
                    pos, world.getTileEntity(pos));
            return false;
        }

        // Position occupied by a non-replaceable solid block without a TileNode
        Thaumcraft.log.warn("Cannot create node at {}: occupied by {} (isAir={} replaceable={} hasTileNode={})",
                pos, oldState, isAir, isReplaceable, alreadyHasTileNode);
        return false;
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

        if (logNodeGen) {
            IBlockState stateAt = world.getBlockState(pos);
            TileEntity teAt = world.getTileEntity(pos);
            Thaumcraft.log.debug("createRandomNodeAt({}): silverwood={} eerie={} small={} type={} mod={} aspects={} block={} isAir={} te={}",
                    pos, silverwood, eerie, small, type, modifier, al.size(),
                    stateAt.getBlock().getRegistryName(), world.isAirBlock(pos),
                    teAt != null ? teAt.getClass().getSimpleName() : "null");
        }
        boolean ok = createNodeAt(world, pos, type, modifier, al);
        if (logNodeGen) {
            Thaumcraft.log.debug("createRandomNodeAt({}): result={}", pos, ok);
        }
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        worldGeneration(random, chunkX, chunkZ, world, true);
    }

    public void worldGeneration(Random random, int chunkX, int chunkZ, World world, boolean newGen) {
        int dim = world.provider.getDimension();

        if (dim == Config.dimensionOuterId) {
            MazeHandler.generateEldritch(world, random, chunkX, chunkZ);
            markChunkDirty(world, chunkX, chunkZ);
            return;
        }

        if (dim == -1) {
            generateNether(world, random, chunkX, chunkZ, newGen);
            if (!newGen) {
                markChunkDirty(world, chunkX, chunkZ);
            }
            return;
        }

        // End generation is intentionally skipped by the reference generator.
        if (world.provider.getDimensionType() != net.minecraft.world.DimensionType.THE_END) {
            generateSurface(world, random, chunkX, chunkZ, newGen);
        }

        if (!newGen) {
            markChunkDirty(world, chunkX, chunkZ);
        }
    }

    private void markChunkDirty(World world, int chunkX, int chunkZ) {
        world.getChunk(chunkX, chunkZ).markDirty();
    }

    private void generateSurface(World world, Random random, int chunkX, int chunkZ, boolean newGen) {
        int dim = world.provider.getDimension();

        int dimBlacklist = getDimBlacklist(dim);
        if (dimBlacklist == 0 || dimBlacklist == 2) return;

        int x = chunkX * 16;
        int z = chunkZ * 16;

        Biome biome = world.getBiome(new BlockPos(x + 8, 64, z + 8));
        int biomeId = Biome.getIdForBiome(biome);

        int biomeBlacklistLevel = getBiomeBlacklist(biomeId);
        if (biomeBlacklistLevel >= 2) return;

        boolean flatWorld = world.getWorldInfo().getTerrainType() == WorldType.FLAT;

        generateOres(world, random, x, z, biome, biomeBlacklistLevel, newGen);

        boolean auraGen = false;
        if (Config.genAura && (newGen || Config.regenAura) && biomeBlacklistLevel < 1) {
            auraGen = generateStructureNode(world, random, x, z);
            auraGen = generateWildNodes(world, random, x, z, auraGen) || auraGen;
        }

        if (Config.genTrees && (newGen || Config.regenTrees) && dimBlacklist == -1 && !flatWorld && biomeBlacklistLevel == -1) {
            generateVegetation(world, random, x, z, biome);
        }

        // Generate structures (surface)
        if (Config.genStructure && (newGen || Config.regenStructure) && dimBlacklist == -1 && dim == 0 && !flatWorld) {
            auraGen = generateStructures(world, random, x, z, biome, auraGen) || auraGen;
            generateTotem(world, random, chunkX, chunkZ, auraGen);
        }
    }

    private boolean generateStructureNode(World world, Random rand, int x, int z) {
        BlockPos origin = new BlockPos(x + 8, world.getHeight(new BlockPos(x + 8, 0, z + 8)).getY(), z + 8);
        BlockPos nearest = new MapGenScatteredFeature().getNearestStructurePos(world, origin, false);
        if (nearest == null || structureNode.containsKey(nearest.hashCode())) return false;

        structureNode.put(nearest.hashCode(), true);
        BlockPos nodePos = new BlockPos(nearest.getX(), world.getHeight(nearest).getY() + 3, nearest.getZ());
        createRandomNodeAt(world, nodePos, rand, false, false, false);
        return true;
    }

    private boolean generateWildNodes(World world, Random rand, int x, int z, boolean auraGen) {
        if (auraGen || Config.nodeRarity <= 0 || rand.nextInt(Config.nodeRarity) != 0) return false;

        int bx = x + rand.nextInt(16);
        int bz = z + rand.nextInt(16);
        int y = getFirstUncoveredY(world, bx, bz);
        if (y < 2) {
            y = world.provider.getAverageGroundLevel() + rand.nextInt(64) - 32 + getFirstUncoveredY(world, bx, bz);
        }
        if (y < 2) {
            y = 32 + rand.nextInt(64);
        }

        BlockPos pos = new BlockPos(bx, y, bz);
        if (world.isAirBlock(pos.up())) {
            pos = pos.up();
        }
        BlockPos candidate = pos.up(rand.nextInt(4));
        IBlockState candidateState = world.getBlockState(candidate);
        if (world.isAirBlock(candidate) || candidateState.getBlock().isReplaceable(world, candidate)) {
            pos = candidate;
        }
        if (pos.getY() > world.getActualHeight()) {
            return false;
        }

        createRandomNodeAt(world, pos, rand, false, false, false);
        return true;
    }

    private static int getFirstUncoveredY(World world, int x, int z) {
        int y = 5;
        while (y < world.getHeight() - 2 && !world.isAirBlock(new BlockPos(x, y + 1, z))) {
            ++y;
        }
        return y;
    }

    private void generateVegetation(World world, Random rand, int x, int z, Biome biome) {
        if (rand.nextInt(120) == 3) {
            generateSilverwood(world, rand, x, z, biome);
        }
        if (rand.nextInt(50) == 7) {
            generateGreatwood(world, rand, x >> 4, z >> 4);
        }

        int bx = x + rand.nextInt(16);
        int bz = z + rand.nextInt(16);
        int by = world.getHeight(new BlockPos(bx, 0, bz)).getY();
        if (by > world.getActualHeight()) {
            return;
        }
        Biome flowerBiome = world.getBiome(new BlockPos(bx, 0, bz));
        if (flowerBiome.topBlock.getBlock() == Blocks.SAND && flowerBiome.getRainfall() > 1.0f && rand.nextInt(30) == 0) {
            generateFlowers(world, rand, bx, by, bz, 3);
        }
    }

    private void generateOres(World world, Random rand, int x, int z, Biome biome, int biomeBlacklistLevel, boolean newGen) {
        if (biomeBlacklistLevel == 0 || biomeBlacklistLevel == 2) {
            return;
        }
        if (Config.genCinnibar && (newGen || Config.regenCinnibar)) {
            for (int i = 0; i < 18; ++i) {
                BlockPos pos = new BlockPos(x + rand.nextInt(16), rand.nextInt(Math.max(1, world.getActualHeight() / 5)), z + rand.nextInt(16));
                placeOreBlockIfStone(world, pos, ConfigBlocks.blockCustomOre.getStateFromMeta(0), 0);
            }
        }
        if (Config.genAmber && (newGen || Config.regenAmber)) {
            for (int i = 0; i < 20; ++i) {
                int bx = x + rand.nextInt(16);
                int bz = z + rand.nextInt(16);
                BlockPos pos = new BlockPos(bx, world.getHeight(new BlockPos(bx, 0, bz)).getY() - rand.nextInt(25), bz);
                placeOreBlockIfStone(world, pos, ConfigBlocks.blockCustomOre.getStateFromMeta(7), 2);
            }
        }
        if (Config.genInfusedStone && (newGen || Config.regenInfusedStone)) {
            for (int i = 0; i < 8; ++i) {
                int bx = x + rand.nextInt(16);
                int bz = z + rand.nextInt(16);
                int top = Math.max(5, world.getHeight(new BlockPos(bx, 0, bz)).getY() - 5);
                int by = rand.nextInt(top);
                int meta = getInfusedOreMeta(world, rand, bx, bz, biome);
                new WorldGenMinable(ConfigBlocks.blockCustomOre.getStateFromMeta(meta), 6, BlockMatcher.forBlock(Blocks.STONE))
                        .generate(world, rand, new BlockPos(bx, by, bz));
            }
        }
    }

    private boolean placeOreBlockIfStone(World world, BlockPos pos, IBlockState ore, int flags) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock().isReplaceableOreGen(state, world, pos, BlockMatcher.forBlock(Blocks.STONE))) {
            return world.setBlockState(pos, ore, flags);
        }
        return false;
    }

    private int getInfusedOreMeta(World world, Random rand, int x, int z, Biome fallbackBiome) {
        int meta = rand.nextInt(6) + 1;
        if (rand.nextInt(3) != 0) {
            return meta;
        }
        Aspect tag = BiomeHandler.getRandomBiomeTag(world.getBiome(new BlockPos(x, 0, z)), rand);
        if (tag == null) {
            tag = BiomeHandler.getRandomBiomeTag(fallbackBiome, rand);
        }
        if (tag == null) {
            return rand.nextInt(6) + 1;
        }
        if (tag == Aspect.AIR) return 1;
        if (tag == Aspect.FIRE) return 2;
        if (tag == Aspect.WATER) return 3;
        if (tag == Aspect.EARTH) return 4;
        if (tag == Aspect.ORDER) return 5;
        if (tag == Aspect.ENTROPY) return 6;
        return meta;
    }

    public static void generateGreatwood(World world, Random rand, int chunkX, int chunkZ) {
        if (!Config.genTrees && !Config.regenTrees) return;
        int bx = chunkX * 16 + 8 + MathHelper.getInt(rand, -4, 4);
        int bz = chunkZ * 16 + 8 + MathHelper.getInt(rand, -4, 4);
        Biome biome = world.getBiome(new BlockPos(bx, 0, bz));
        float chance = BiomeHandler.getBiomeSupportsGreatwood(biome);
        if (chance > 0 && rand.nextFloat() < chance) {
            BlockPos pos = world.getHeight(new BlockPos(bx, 0, bz));
            new WorldGenGreatwoodTrees(false).generate(world, rand, pos.getX(), pos.getY(), pos.getZ(), rand.nextInt(16) == 0);
        }
    }

    public static void generateSilverwood(World world, Random rand, int x, int z, Biome biome) {
        int bx = x + rand.nextInt(16);
        int bz = z + rand.nextInt(16);
        BlockPos pos = world.getHeight(new BlockPos(bx, 0, bz));

        boolean shouldGen = biome == biomeMagicalForest
                || biome == biomeTaint
                || !BiomeDictionary.hasType(biome, BiomeDictionary.Type.MAGICAL)
                && biome != Biome.getBiome(0)  // ocean
                && biome != Biome.getBiome(1); // plains

        if (shouldGen) {
            new WorldGenSilverwoodTrees(false, 7, 4).generate(world, rand, pos);
        }
    }

    public static void generateFlowers(World world, Random rand, int x, int z, int flowerType) {
        int bx = x + rand.nextInt(16);
        int bz = z + rand.nextInt(16);
        BlockPos pos = world.getHeight(new BlockPos(bx, 0, bz));
        generateFlowers(world, rand, pos.getX(), pos.getY(), pos.getZ(), flowerType);
    }

    public static boolean generateFlowers(World world, Random rand, int x, int y, int z, int flowerType) {
        BlockPos pos = new BlockPos(x, y, z);
        new WorldGenCustomFlowers(ConfigBlocks.blockCustomPlant.getStateFromMeta(flowerType))
                .generate(world, rand, pos);
        return true;
    }

    private boolean generateStructures(World world, Random rand, int x, int z, Biome biome, boolean auraGen) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        int ringX = x + rand.nextInt(16);
        int ringZ = z + rand.nextInt(16);
        int ringY = world.getHeight(new BlockPos(ringX, 0, ringZ)).getY() - 9;
        if (ringY < world.getActualHeight()) {
            BlockPos moundPos = new BlockPos(ringX, ringY, ringZ);
            if (rand.nextInt(150) == 0) {
                if (new WorldGenMound().generate(world, rand, moundPos)) {
                    createRandomNodeAt(world, moundPos.add(9, 8, 9), rand, false, true, false);
                    auraGen = true;
                }
            } else if (rand.nextInt(66) == 0) {
                WorldGenEldritchRing ring = new WorldGenEldritchRing();
                int width = 11 + rand.nextInt(6) * 2;
                int height = 11 + rand.nextInt(6) * 2;
                ring.chunkX = chunkX;
                ring.chunkZ = chunkZ;
                ring.width = width;
                ring.height = height;
                BlockPos pos = new BlockPos(ringX, ringY + 8, ringZ);
                if (ring.generate(world, rand, pos)) {
                    createRandomNodeAt(world, pos.up(2), rand, false, true, false);
                    auraGen = true;
                    Thread mazeThread = new Thread(new MazeThread(chunkX, chunkZ, width, height, rand.nextLong()));
                    mazeThread.start();
                }
            } else if (rand.nextInt(40) == 0) {
                BlockPos pos = new BlockPos(ringX, ringY + 9, ringZ);
                if (new WorldGenHilltopStones().generate(world, rand, pos)) {
                    createRandomNodeAt(world, pos.up(5), rand, false, true, false);
                    auraGen = true;
                }
            }
        }
        return auraGen;
    }

    private void generateNether(World world, Random rand, int chunkX, int chunkZ, boolean newGen) {
        boolean auraGen = false;
        boolean flatWorld = world.getWorldInfo().getTerrainType() == WorldType.FLAT;
        if (!flatWorld && Config.genStructure && (newGen || Config.regenStructure)) {
            auraGen = generateTotem(world, rand, chunkX, chunkZ, auraGen) || auraGen;
        }
        if (Config.genAura && (newGen || Config.regenAura)) {
            generateWildNodes(world, rand, chunkX * 16, chunkZ * 16, auraGen);
        }
    }

    private boolean generateTotem(World world, Random rand, int chunkX, int chunkZ, boolean auraGen) {
        int dim = world.provider.getDimension();
        if (!Config.genStructure || auraGen || Config.nodeRarity <= 0 || rand.nextInt(Config.nodeRarity * 10) != 0) {
            return false;
        }
        if (dim != 0 && dim != -1) {
            return false;
        }

        int x = chunkX * 16 + rand.nextInt(16);
        int z = chunkZ * 16 + rand.nextInt(16);
        int topY = dim == -1 ? getFirstUncoveredY(world, x, z) - 1 : world.getHeight(new BlockPos(x, 0, z)).getY() - 1;
        if (topY > world.getActualHeight()) {
            return false;
        }

        BlockPos base = new BlockPos(x, topY, z);
        IBlockState baseState = world.getBlockState(base);
        if (baseState.getBlock().isLeaves(baseState, world, base)) {
            while (topY > 40) {
                --topY;
                base = new BlockPos(x, topY, z);
                if (world.getBlockState(base).getBlock() == Blocks.GRASS) {
                    break;
                }
            }
        }

        if (isSnowLayerOrTallGrass(world, base)) {
            --topY;
            base = new BlockPos(x, topY, z);
        }
        if (!isValidTotemBase(world.getBlockState(base).getBlock())) {
            return false;
        }

        int count;
        for (count = 1; isTotemReplaceable(world, base.up(count)) && count < 3; ++count) {
        }
        if (count < 2) {
            return false;
        }

        world.setBlockState(base, ConfigBlocks.blockCosmeticSolid.getStateFromMeta(1), 3);
        count = 1;
        while (isTotemReplaceable(world, base.up(count)) && count < 5) {
            BlockPos pos = base.up(count);
            world.setBlockState(pos, ConfigBlocks.blockCosmeticSolid.getStateFromMeta(0), 3);
            if (count > 1 && rand.nextInt(4) == 0) {
                world.setBlockState(pos, ConfigBlocks.blockCosmeticSolid.getStateFromMeta(8), 3);
                createRandomNodeAt(world, pos, rand, false, true, false);
                return true;
            }
            ++count;
            if (count >= 5) {
                BlockPos node = base.up(5);
                world.setBlockState(node, ConfigBlocks.blockCosmeticSolid.getStateFromMeta(8), 3);
                createRandomNodeAt(world, node, rand, false, true, false);
                return true;
            }
        }
        return false;
    }

    private boolean isTotemReplaceable(World world, BlockPos pos) {
        return world.isAirBlock(pos) || isSnowLayerOrTallGrass(world, pos);
    }

    private boolean isSnowLayerOrTallGrass(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        return block == Blocks.SNOW_LAYER || block == Blocks.TALLGRASS;
    }

    private boolean isValidTotemBase(Block block) {
        return block == Blocks.GRASS
                || block == Blocks.SAND
                || block == Blocks.DIRT
                || block == Blocks.STONE
                || block == Blocks.NETHERRACK;
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
