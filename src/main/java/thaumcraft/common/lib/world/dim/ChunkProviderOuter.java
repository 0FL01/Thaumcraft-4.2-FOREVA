package thaumcraft.common.lib.world.dim;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.event.ForgeEventFactory;

public class ChunkProviderOuter implements IChunkGenerator {
    private final Random rand;
    private final World world;
    private Biome[] biomesForGeneration;

    public ChunkProviderOuter(World world, long seed, boolean mapFeaturesEnabled) {
        this.world = world;
        this.rand = new Random(seed);
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
        // Original Outer Lands chunks start empty; room generation fills maze cells during populate().
    }

    public void buildSurfaces(int x, int z, ChunkPrimer primer) {
        // No natural surface in the Outer Lands void.
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
        MazeHandler.generateEldritch(this.world, this.rand, x, z);
        ForgeEventFactory.onChunkPopulate(false, this, this.world, this.rand, x, z, flag);
        BlockFalling.fallInstantly = false;
    }

    @Override
    public boolean generateStructures(Chunk chunk, int x, int z) {
        return MazeHandler.getFromHashMap(new CellLoc(x, z)) != null;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        Biome biome = this.world.getBiome(pos);
        return biome.getSpawnableList(creatureType);
    }

    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
        if (!isOuterMazeStructure(structureName)) return null;
        int chunkX = position.getX() >> 4;
        int chunkZ = position.getZ() >> 4;
        for (int radius = 0; radius <= 64; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) != radius && Math.abs(dz) != radius) continue;
                    int cx = chunkX + dx;
                    int cz = chunkZ + dz;
                    if (MazeHandler.getFromHashMap(new CellLoc(cx, cz)) != null) {
                        return new BlockPos(cx * 16 + 8, 53, cz * 16 + 8);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        return isOuterMazeStructure(structureName)
                && MazeHandler.getFromHashMap(new CellLoc(pos.getX() >> 4, pos.getZ() >> 4)) != null;
    }

    @Override
    public void recreateStructures(Chunk chunk, int x, int z) {
        // Structures are generated during populate(); do not overwrite saved chunks on reload.
    }

    private static boolean isOuterMazeStructure(String structureName) {
        return structureName != null
                && ("EldritchMaze".equalsIgnoreCase(structureName)
                || "OuterLands".equalsIgnoreCase(structureName)
                || "Eldritch".equalsIgnoreCase(structureName));
    }
}
