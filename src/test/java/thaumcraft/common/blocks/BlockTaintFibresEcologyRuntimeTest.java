package thaumcraft.common.blocks;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.monster.EntityTaintSpore;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BlockTaintFibresEcologyRuntimeTest {
    private static final BlockPos POS = new BlockPos(0, 64, 0);
    private static Biome previousTaintBiome;
    private static int previousTaintSpreadRate;
    private static boolean previousSpawnTaintSpore;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
        if (ConfigBlocks.blockTaintFibres == null) {
            ConfigBlocks.init();
        }
        previousTaintBiome = ThaumcraftWorldGenerator.biomeTaint;
        previousTaintSpreadRate = Config.taintSpreadRate;
        previousSpawnTaintSpore = Config.spawnTaintSpore;
        ThaumcraftWorldGenerator.biomeTaint = Biomes.PLAINS;
        Config.taintSpreadRate = 0;
        Config.spawnTaintSpore = true;
    }

    @AfterClass
    public static void restoreGlobals() {
        ThaumcraftWorldGenerator.biomeTaint = previousTaintBiome;
        Config.taintSpreadRate = previousTaintSpreadRate;
        Config.spawnTaintSpore = previousSpawnTaintSpore;
    }

    @Test
    public void spreadSelectsEachTc4GeneratedTypeButNeverMatureStalks() {
        int[][] randomValues = {
                {1},
                {0, 8},
                {0, 9, 9},
                {0, 9, 10}
        };
        for (int expectedMeta = 0; expectedMeta <= 3; expectedMeta++) {
            EcologyWorld world = new EcologyWorld(new SequenceRandom(randomValues[expectedMeta]));
            world.put(POS.down(), Blocks.STONE.getDefaultState());

            assertTrue("spread branch for metadata " + expectedMeta,
                    BlockTaintFibres.spreadFibres(world, POS));
            assertEquals(expectedMeta,
                    ConfigBlocks.blockTaintFibres.getMetaFromState(world.getBlockState(POS)));
        }
    }

    @Test
    public void spreadDoesNotReplaceExistingRareTypes() {
        BlockTaintFibres fibres = ConfigBlocks.blockTaintFibres;
        for (int meta = 1; meta <= 4; meta++) {
            EcologyWorld world = new EcologyWorld(new SequenceRandom(0, 9, 10));
            world.put(POS.down(), Blocks.STONE.getDefaultState());
            world.put(POS, fibres.getStateFromMeta(meta));

            assertFalse("metadata " + meta + " should not be replaceable",
                    fibres.isReplaceable(world, POS));
            assertFalse("spread should preserve metadata " + meta,
                    BlockTaintFibres.spreadFibres(world, POS));
            assertEquals(meta, fibres.getMetaFromState(world.getBlockState(POS)));
        }
    }

    @Test
    public void failedSpreadMaturesStalkAndResetsAnOrphanedMatureStalk() {
        BlockTaintFibres fibres = ConfigBlocks.blockTaintFibres;

        EcologyWorld maturingWorld = new EcologyWorld(new SequenceRandom());
        maturingWorld.put(POS, fibres.getStateFromMeta(3));
        maturingWorld.put(POS.down(), Blocks.STONE.getDefaultState());
        fibres.updateTick(maturingWorld, POS, maturingWorld.getBlockState(POS),
                new SequenceRandom(2, 3, 1, 0));
        assertEquals(4, fibres.getMetaFromState(maturingWorld.getBlockState(POS)));
        assertEquals(1, maturingWorld.spawnedEntities.size());
        assertTrue(maturingWorld.spawnedEntities.get(0) instanceof EntityTaintSpore);

        EcologyWorld orphanWorld = new EcologyWorld(new SequenceRandom());
        orphanWorld.put(POS, fibres.getStateFromMeta(4));
        orphanWorld.put(POS.down(), Blocks.STONE.getDefaultState());
        fibres.updateTick(orphanWorld, POS, orphanWorld.getBlockState(POS),
                new SequenceRandom(2, 3, 1));
        assertEquals(3, fibres.getMetaFromState(orphanWorld.getBlockState(POS)));
    }

    @Test
    public void successfulSpreadSkipsStalkLifecycleForThatTick() {
        BlockTaintFibres fibres = ConfigBlocks.blockTaintFibres;
        EcologyWorld world = new EcologyWorld(new SequenceRandom(1));
        world.put(POS, fibres.getStateFromMeta(3));
        world.put(POS.down(), Blocks.STONE.getDefaultState());
        world.put(POS.east().down(), Blocks.STONE.getDefaultState());

        fibres.updateTick(world, POS, world.getBlockState(POS), new SequenceRandom(2, 3, 1, 0));

        assertEquals(3, fibres.getMetaFromState(world.getBlockState(POS)));
        assertEquals(0, fibres.getMetaFromState(world.getBlockState(POS.east())));
        assertTrue(world.spawnedEntities.isEmpty());
    }

    private static final class SequenceRandom extends Random {
        private final int[] values;
        private int index;

        private SequenceRandom(int... values) {
            this.values = values;
        }

        @Override
        public int nextInt(int bound) {
            int value = this.index < this.values.length ? this.values[this.index++] : 0;
            return Math.floorMod(value, bound);
        }
    }

    private static final class EcologyWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();
        private final List<Entity> spawnedEntities = new ArrayList<>();

        private EcologyWorld(Random random) {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "taint_fibres_ecology_runtime"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.setWorldRandom(random);
            this.chunkProvider = this.createChunkProvider();
        }

        private void setWorldRandom(Random random) {
            try {
                Field field = World.class.getDeclaredField("rand");
                field.setAccessible(true);
                field.set(this, random);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        private void put(BlockPos pos, IBlockState state) {
            this.states.put(pos.toImmutable(), state);
        }

        @Override
        public Biome getBiome(BlockPos pos) {
            return Biomes.PLAINS;
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            IBlockState state = this.states.get(pos);
            return state == null ? Blocks.AIR.getDefaultState() : state;
        }

        @Override
        public boolean setBlockState(BlockPos pos, IBlockState state, int flags) {
            if (state.getBlock() == Blocks.AIR) {
                this.states.remove(pos);
            } else {
                this.states.put(pos.toImmutable(), state);
            }
            return true;
        }

        @Override
        public boolean setBlockToAir(BlockPos pos) {
            return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }

        @Override
        public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> type, AxisAlignedBB bounds) {
            return new ArrayList<>();
        }

        @Override
        public boolean spawnEntity(Entity entity) {
            this.spawnedEntities.add(entity);
            return true;
        }

        @Override
        public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam) {
        }

        @Override public TileEntity getTileEntity(BlockPos pos) { return null; }
        @Override public Explosion createExplosion(Entity entityIn, double x, double y, double z,
                                                   float strength, boolean isSmoking) { return null; }
        @Override public void notifyBlockUpdate(BlockPos pos, IBlockState oldState,
                                                IBlockState newState, int flags) { }
        @Override public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) { }
        @Override public void updateComparatorOutputLevel(BlockPos pos, Block blockIn) { }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "taint_fibres_ecology_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
