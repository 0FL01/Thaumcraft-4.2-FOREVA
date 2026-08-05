package thaumcraft.common.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.blocks.BlockMagicalLog;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.EntityAspectOrb;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TileNodeLifecycleRuntimeTest {
    private static final BlockPos NODE_POS = new BlockPos(0, 64, 0);
    private static final BlockPos STABILIZER_POS = NODE_POS.down();

    private Biome oldTaint;
    private Biome oldEerie;
    private Biome oldMagicalForest;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
        if (ConfigBlocks.blockAiry == null) {
            ConfigBlocks.init();
        }
    }

    @Before
    public void isolateBiomeSideEffects() {
        this.oldTaint = ThaumcraftWorldGenerator.biomeTaint;
        this.oldEerie = ThaumcraftWorldGenerator.biomeEerie;
        this.oldMagicalForest = ThaumcraftWorldGenerator.biomeMagicalForest;
        ThaumcraftWorldGenerator.biomeTaint = null;
        ThaumcraftWorldGenerator.biomeEerie = null;
        ThaumcraftWorldGenerator.biomeMagicalForest = null;
    }

    @After
    public void restoreBiomeSideEffects() {
        ThaumcraftWorldGenerator.biomeTaint = this.oldTaint;
        ThaumcraftWorldGenerator.biomeEerie = this.oldEerie;
        ThaumcraftWorldGenerator.biomeMagicalForest = this.oldMagicalForest;
    }

    @Test
    public void erosionRunsAt1200ButNot1199AndDecrementsZeroCurrentBase() throws Exception {
        RecordingRandom random = new RecordingRandom(new int[]{1}, new boolean[0]);
        LifecycleWorld world = new LifecycleWorld(random, false);
        TestTileNode node = attachNode(world, ConfigBlocks.blockMagicalLog.getStateFromMeta(2),
                new AspectList().add(Aspect.AIR, 2));
        node.setNodeModifier(NodeModifier.PALE);
        assertTrue(node.takeFromContainer(Aspect.AIR, 2));
        setCount(node, 1198);

        node.update();

        assertEquals(2, node.getNodeVisBase(Aspect.AIR));
        assertTrue(random.operations.isEmpty());

        node.update();

        assertEquals(Arrays.asList("int:20"), random.operations);
        assertEquals(1, node.getNodeVisBase(Aspect.AIR));
        assertEquals(0, node.getAspects().getAmount(Aspect.AIR));
    }

    @Test
    public void erosionRemovalStopsAfterFirstAspectAndUsesModifierRngInOracleOrder() throws Exception {
        RecordingRandom random = new RecordingRandom(new int[]{1, 0, 0}, new boolean[0]);
        LifecycleWorld world = new LifecycleWorld(random, false);
        TestTileNode node = attachNode(world, ConfigBlocks.blockMagicalLog.getStateFromMeta(2),
                new AspectList().add(Aspect.AIR, 1).add(Aspect.FIRE, 1));
        assertTrue(node.takeFromContainer(Aspect.AIR, 1));
        assertTrue(node.takeFromContainer(Aspect.FIRE, 1));
        setCount(node, 1199);

        node.update();

        assertEquals(0, node.getNodeVisBase(Aspect.AIR));
        assertEquals(1, node.getNodeVisBase(Aspect.FIRE));
        assertFalse(node.getAspects().aspects.containsKey(Aspect.AIR));
        assertTrue(node.getAspects().aspects.containsKey(Aspect.FIRE));
        assertSame(NodeModifier.FADING, node.getNodeModifier());
        assertEquals(Arrays.asList("int:20", "int:5", "int:5"), random.operations);
    }

    @Test
    public void brightModifierDegradesOnlyToUnmodifiedOnOneRemovalRoll() throws Exception {
        RecordingRandom random = new RecordingRandom(new int[]{1, 0}, new boolean[0]);
        LifecycleWorld world = new LifecycleWorld(random, false);
        TestTileNode node = attachNode(world, ConfigBlocks.blockMagicalLog.getStateFromMeta(2),
                new AspectList().add(Aspect.AIR, 1));
        node.setNodeModifier(NodeModifier.BRIGHT);
        assertTrue(node.takeFromContainer(Aspect.AIR, 1));
        setCount(node, 1199);

        node.update();

        assertNull(node.getNodeModifier());
        assertEquals(Arrays.asList("int:20", "int:5"), random.operations);
    }

    @Test
    public void emptyAiryNodeIsRemovedAndEmptySilverwoodKnotDowngrades() throws Exception {
        RecordingRandom airyRandom = new RecordingRandom(new int[]{1, 1}, new boolean[0]);
        LifecycleWorld airyWorld = new LifecycleWorld(airyRandom, false);
        TestTileNode airyNode = attachNode(airyWorld, ConfigBlocks.blockAiry.getStateFromMeta(0), new AspectList());
        airyWorld.putState(STABILIZER_POS, ConfigBlocks.blockStoneDevice.getStateFromMeta(9));
        setCount(airyNode, 1199);

        airyNode.update();

        assertSame(Blocks.AIR, airyWorld.getBlockState(NODE_POS).getBlock());
        assertTrue(airyNode.isInvalid());

        RecordingRandom logRandom = new RecordingRandom(new int[]{1, 1}, new boolean[0]);
        LifecycleWorld logWorld = new LifecycleWorld(logRandom, false);
        TestTileNode logNode = attachNode(logWorld, ConfigBlocks.blockMagicalLog.getStateFromMeta(2), new AspectList());
        setCount(logNode, 1199);

        logNode.update();

        IBlockState downgraded = logWorld.getBlockState(NODE_POS);
        assertSame(ConfigBlocks.blockMagicalLog, downgraded.getBlock());
        assertEquals(Integer.valueOf(1), downgraded.getValue(BlockMagicalLog.TYPE));
        assertTrue(logNode.isInvalid());
    }

    @Test
    public void unstableNodeLosesOneSelectedPrimalAndSpawnsValueOneOrbAtTick100() throws Exception {
        RecordingRandom random = new RecordingRandom(new int[]{0}, new boolean[]{true});
        LifecycleWorld world = new LifecycleWorld(random, false);
        TestTileNode node = attachNode(world, ConfigBlocks.blockMagicalLog.getStateFromMeta(2),
                new AspectList().add(Aspect.AIR, 2));
        node.setNodeType(NodeType.UNSTABLE);
        setCount(node, 98);

        node.update();

        assertEquals(2, node.getAspects().getAmount(Aspect.AIR));
        assertTrue(world.spawned.isEmpty());

        node.update();

        assertEquals(1, node.getAspects().getAmount(Aspect.AIR));
        assertEquals(1, world.spawned.size());
        EntityAspectOrb orb = (EntityAspectOrb) world.spawned.get(0);
        assertSame(Aspect.AIR, orb.getAspect());
        assertEquals(1, orb.getAspectValue());
        assertEquals(Arrays.asList("boolean", "int:1"), random.operations);
    }

    @Test
    public void lockOneAndTwoPreventLossAndUseExactRecoveryBoundsAndLiterals() throws Exception {
        assertLockRecovery(9, 1, 10000, 12500);
        assertLockRecovery(10, 2, 5000, 6250);
    }

    @Test
    public void poweredStabilizerGivesLockZeroAndAllowsUnstableOrbLoss() throws Exception {
        RecordingRandom random = new RecordingRandom(new int[]{0}, new boolean[]{true});
        LifecycleWorld world = new LifecycleWorld(random, false);
        TestTileNode node = attachNode(world, ConfigBlocks.blockAiry.getStateFromMeta(0),
                new AspectList().add(Aspect.AIR, 1));
        node.setNodeType(NodeType.UNSTABLE);
        node.setNodeModifier(NodeModifier.FADING);
        world.putState(STABILIZER_POS, ConfigBlocks.blockStoneDevice.getStateFromMeta(9));
        world.powered.put(STABILIZER_POS, true);
        setCount(node, 99);

        node.update();

        assertEquals(0, node.getLock());
        assertEquals(0, node.getAspects().getAmount(Aspect.AIR));
        assertEquals(1, world.spawned.size());
        assertEquals(Arrays.asList("boolean", "int:1"), random.operations);
    }

    @Test
    public void sharedStabilizerCheckSupportsTileRequirementAndClientRetraction() {
        LifecycleWorld world = new LifecycleWorld(new RecordingRandom(new int[0], new boolean[0]), true);
        world.putState(STABILIZER_POS, ConfigBlocks.blockStoneDevice.getStateFromMeta(9));
        world.putState(NODE_POS, ConfigBlocks.blockAiry.getStateFromMeta(0));

        assertEquals(1, NodeStabilizerHelper.getActiveLock(world, STABILIZER_POS, false));
        assertEquals(0, NodeStabilizerHelper.getActiveLock(world, STABILIZER_POS, true));

        TileNodeStabilizer stabilizer = new TileNodeStabilizer();
        world.attachTile(STABILIZER_POS, stabilizer);
        assertEquals(1, NodeStabilizerHelper.getActiveLock(world, STABILIZER_POS, true));

        stabilizer.update();
        assertEquals(1, stabilizer.lock);
        assertEquals(1, stabilizer.count);

        world.powered.put(STABILIZER_POS, true);
        stabilizer.update();
        assertEquals(0, stabilizer.lock);
        assertEquals(0, stabilizer.count);

        world.powered.put(STABILIZER_POS, false);
        world.putState(STABILIZER_POS, ConfigBlocks.blockStoneDevice.getStateFromMeta(10));
        stabilizer.update();
        assertEquals(2, stabilizer.lock);
        assertEquals(1, stabilizer.count);
    }

    @Test
    public void jarredAndEnergizedNodesRemainOutsideNaturalTileNodeLifecycle() {
        assertFalse(ITickable.class.isAssignableFrom(TileJarNode.class));
        assertFalse(TileNode.class.isAssignableFrom(TileNodeEnergized.class));
    }

    private static void assertLockRecovery(int stabilizerMeta, int expectedLock,
                                           int unstableBound, int fadingBound) throws Exception {
        RecordingRandom random = new RecordingRandom(new int[]{42, 69}, new boolean[]{true});
        LifecycleWorld world = new LifecycleWorld(random, false);
        TestTileNode node = attachNode(world, ConfigBlocks.blockAiry.getStateFromMeta(0),
                new AspectList().add(Aspect.AIR, 2));
        node.setNodeType(NodeType.UNSTABLE);
        node.setNodeModifier(NodeModifier.FADING);
        world.putState(STABILIZER_POS, ConfigBlocks.blockStoneDevice.getStateFromMeta(stabilizerMeta));
        setCount(node, 99);

        node.update();

        assertEquals(expectedLock, node.getLock());
        assertSame(NodeType.NORMAL, node.getNodeType());
        assertSame(NodeModifier.PALE, node.getNodeModifier());
        assertEquals(2, node.getAspects().getAmount(Aspect.AIR));
        assertTrue(world.spawned.isEmpty());
        assertEquals(Arrays.asList("boolean", "int:" + unstableBound, "int:" + fadingBound),
                random.operations);
    }

    private static TestTileNode attachNode(LifecycleWorld world, IBlockState state, AspectList aspects) {
        TestTileNode node = new TestTileNode();
        node.setAspects(aspects);
        world.putState(NODE_POS, state);
        world.attachTile(NODE_POS, node);
        return node;
    }

    private static void setCount(TileNode node, int value) throws Exception {
        Field field = TileNode.class.getDeclaredField("count");
        field.setAccessible(true);
        field.setInt(node, value);
    }

    private static final class TestTileNode extends TileNode {
        @Override
        public void markDirty() {
        }
    }

    private static final class RecordingRandom extends Random {
        private final int[] ints;
        private final boolean[] booleans;
        private final List<String> operations = new ArrayList<>();
        private int intIndex;
        private int booleanIndex;

        private RecordingRandom(int[] ints, boolean[] booleans) {
            this.ints = ints;
            this.booleans = booleans;
        }

        @Override
        public int nextInt(int bound) {
            this.operations.add("int:" + bound);
            int value = this.intIndex < this.ints.length ? this.ints[this.intIndex++] : 0;
            return Math.floorMod(value, bound);
        }

        @Override
        public boolean nextBoolean() {
            this.operations.add("boolean");
            return this.booleanIndex < this.booleans.length && this.booleans[this.booleanIndex++];
        }
    }

    private static final class LifecycleWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();
        private final Map<BlockPos, Boolean> powered = new HashMap<>();
        private final List<Entity> spawned = new ArrayList<>();

        private LifecycleWorld(Random random, boolean remote) {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT),
                            "node_lifecycle"),
                    new WorldProviderSurface(), new Profiler(), remote);
            this.provider.setWorld(this);
            setWorldRandom(random);
            this.chunkProvider = this.createChunkProvider();
        }

        private void setWorldRandom(Random random) {
            try {
                Field field = World.class.getDeclaredField("rand");
                field.setAccessible(true);
                field.set(this, random);
            } catch (ReflectiveOperationException e) {
                throw new AssertionError(e);
            }
        }

        private void putState(BlockPos pos, IBlockState state) {
            this.states.put(pos.toImmutable(), state);
        }

        private void attachTile(BlockPos pos, TileEntity tile) {
            BlockPos key = pos.toImmutable();
            tile.setWorld(this);
            tile.setPos(key);
            this.tiles.put(key, tile);
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            IBlockState state = this.states.get(pos);
            return state == null ? Blocks.AIR.getDefaultState() : state;
        }

        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return this.tiles.get(pos);
        }

        @Override
        public boolean isBlockPowered(BlockPos pos) {
            return Boolean.TRUE.equals(this.powered.get(pos));
        }

        @Override
        public boolean setBlockState(BlockPos pos, IBlockState state, int flags) {
            BlockPos key = pos.toImmutable();
            this.states.put(key, state);
            this.tiles.remove(key);
            return true;
        }

        @Override
        public boolean setBlockToAir(BlockPos pos) {
            BlockPos key = pos.toImmutable();
            this.states.remove(key);
            this.tiles.remove(key);
            return true;
        }

        @Override
        public boolean spawnEntity(Entity entityIn) {
            this.spawned.add(entityIn);
            return true;
        }

        @Override
        public Biome getBiome(BlockPos pos) {
            return Biomes.PLAINS;
        }

        @Override
        public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "node_lifecycle_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
