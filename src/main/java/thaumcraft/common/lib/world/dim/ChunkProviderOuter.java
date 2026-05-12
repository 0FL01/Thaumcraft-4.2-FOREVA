package thaumcraft.common.lib.world.dim;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraftforge.event.ForgeEventFactory;

public class ChunkProviderOuter implements IChunkGenerator {
    private final Random rand;
    private final World world;
    private final WorldType worldType;
    private Biome[] biomesForGeneration;

    public ChunkProviderOuter(World world, long seed, boolean mapFeaturesEnabled) {
        this.world = world;
        this.rand = new Random(seed);
        this.worldType = world.getWorldInfo().getTerrainType();
    }

    @Override
    public Chunk generateChunk(int x, int z) {
        this.rand.setSeed((long)x * 341873128712L + (long)z * 132897987541L);
        ChunkPrimer primer = new ChunkPrimer();
        this.biomesForGeneration = this.world.getBiomeProvider().getBiomes(this.biomesForGeneration, x * 16, z * 16, 16, 16);
        this.setBlocksInChunk(x, z, primer);
        this.buildSurfaces(x, z, primer);
        Chunk chunk = new Chunk(this.world, primer, x, z);
        byte[] biomeArray = chunk.getBiomeArray();
        for (int i = 0; i < biomeArray.length; i++) {
            biomeArray[i] = (byte) Biome.getIdForBiome(this.biomesForGeneration[i]);
        }
        chunk.generateSkylightMap();
        return chunk;
    }

    public void setBlocksInChunk(int x, int z, ChunkPrimer primer) {
        int seaLevel = 63;
        for (int bx = 0; bx < 16; bx++) {
            for (int bz = 0; bz < 16; bz++) {
                for (int by = 0; by < 128; by++) {
                    if (by == 0) {
                        primer.setBlockState(bx, by, bz, Blocks.BEDROCK.getDefaultState());
                    } else if (by < 60) {
                        primer.setBlockState(bx, by, bz, Blocks.STONE.getDefaultState());
                    } else {
                        primer.setBlockState(bx, by, bz, Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
    }

    public void buildSurfaces(int x, int z, ChunkPrimer primer) {
        // Flat stone surface for Outer Lands
        for (int bx = 0; bx < 16; bx++) {
            for (int bz = 0; bz < 16; bz++) {
                primer.setBlockState(bx, 60, bz, Blocks.DOUBLE_STONE_SLAB.getDefaultState());
            }
        }
    }

    @Override
    public void populate(int x, int z) {
        BlockFalling.fallInstantly = true;
        int cx = x * 16;
        int cz = z * 16;
        BlockPos blockpos = new BlockPos(cx, 0, cz);
        Biome biome = this.world.getBiome(blockpos.add(16, 0, 16));
        this.rand.setSeed(this.world.getSeed());
        long k = this.rand.nextLong() / 2L * 2L + 1L;
        long l = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed((long)x * k + (long)z * l ^ this.world.getSeed());
        boolean flag = false;
        ForgeEventFactory.onChunkPopulate(true, this, this.world, this.rand, x, z, flag);
        biome.decorate(this.world, this.rand, blockpos);
        ForgeEventFactory.onChunkPopulate(false, this, this.world, this.rand, x, z, flag);
        BlockFalling.fallInstantly = false;
    }

    @Override
    public boolean generateStructures(Chunk chunk, int x, int z) {
        return false;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        Biome biome = this.world.getBiome(pos);
        return biome.getSpawnableList(creatureType);
    }

    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
        return null;
    }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        return false;
    }

    @Override
    public void recreateStructures(Chunk chunk, int x, int z) {}
}
