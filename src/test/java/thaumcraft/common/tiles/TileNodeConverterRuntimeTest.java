package thaumcraft.common.tiles;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import thaumcraft.api.WorldCoordinates;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.common.blocks.BlockAiry;
import thaumcraft.common.config.ConfigBlocks;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class TileNodeConverterRuntimeTest {
    private static final BlockPos NODE_POS = new BlockPos(0, 64, 0);
    private static final BlockPos STABILIZER_POS = NODE_POS.down();
    private static final BlockPos CONVERTER_POS = NODE_POS.up();

    @Parameterized.Parameters(name = "{0}-{1}")
    public static Collection<Object[]> nodeIdentities() {
        return Arrays.asList(new Object[][]{
                {NodeType.HUNGRY, NodeModifier.BRIGHT},
                {NodeType.DARK, NodeModifier.PALE},
                {NodeType.PURE, NodeModifier.FADING},
                {NodeType.TAINTED, null}
        });
    }

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
        if (ConfigBlocks.blockAiry == null) {
            ConfigBlocks.init();
        }
    }

    private final NodeType nodeType;
    private final NodeModifier nodeModifier;

    public TileNodeConverterRuntimeTest(NodeType nodeType, NodeModifier nodeModifier) {
        this.nodeType = nodeType;
        this.nodeModifier = nodeModifier;
    }

    @After
    public void clearVisSources() {
        VisNetHandler.sources.remove(0);
    }

    @Test
    public void conversionUsesFollowingUpdateAtBothBoundaries() {
        ConversionFixture fixture = ConversionFixture.natural(NodeType.NORMAL, null,
                new AspectList().add(Aspect.AIR, 8));
        fixture.converter.status = 1;
        fixture.converter.count = 999;
        int initialVis = fixture.naturalNode().getAspects().visSize();

        fixture.converter.update();

        assertEquals(1000, fixture.converter.count);
        assertTrue(fixture.world.isNodeType(0));
        assertEquals(initialVis - 1, fixture.naturalNode().getAspects().visSize());

        fixture.converter.update();

        assertTrue(fixture.world.isNodeType(5));
        assertEquals(2, fixture.converter.status);
        assertEquals(1000, fixture.converter.count);

        fixture.world.powered.put(CONVERTER_POS, false);
        fixture.converter.count = 51;
        fixture.converter.update();

        assertEquals(50, fixture.converter.count);
        assertTrue(fixture.world.isNodeType(5));

        fixture.converter.update();

        assertTrue(fixture.world.isNodeType(0));
        assertEquals(0, fixture.converter.status);
    }

    @Test
    public void inactiveStabilizerStopsDrainAndRestoringItResumesForwardProgress() {
        ConversionFixture fixture = ConversionFixture.natural(NodeType.NORMAL, null,
                new AspectList().add(Aspect.AIR, 8));
        fixture.converter.status = 1;
        fixture.converter.count = 100;
        int initialVis = fixture.naturalNode().getAspects().visSize();

        fixture.world.remove(STABILIZER_POS);
        fixture.converter.update();

        assertEquals(0, fixture.converter.status);
        assertEquals(99, fixture.converter.count);
        assertEquals(initialVis, fixture.naturalNode().getAspects().visSize());

        fixture.attachStabilizer(9);
        fixture.world.powered.put(STABILIZER_POS, true);
        fixture.converter.update();

        assertEquals(0, fixture.converter.status);
        assertEquals(98, fixture.converter.count);
        assertEquals(initialVis, fixture.naturalNode().getAspects().visSize());

        fixture.world.powered.put(STABILIZER_POS, false);
        fixture.converter.update();

        assertEquals(1, fixture.converter.status);
        assertEquals(99, fixture.converter.count);
        assertEquals(initialVis - 1, fixture.naturalNode().getAspects().visSize());

        fixture.converter.count = 1000;
        fixture.world.remove(STABILIZER_POS);
        int visBeforeCompletionAttempt = fixture.naturalNode().getAspects().visSize();
        fixture.converter.update();

        assertEquals(0, fixture.converter.status);
        assertEquals(999, fixture.converter.count);
        assertTrue(fixture.world.isNodeType(0));
        assertEquals(visBeforeCompletionAttempt, fixture.naturalNode().getAspects().visSize());
    }

    @Test
    public void statusRefreshPreservesCountdownAndReversalSafetyWindow() {
        ConversionFixture stable = ConversionFixture.energized();
        stable.converter.status = 2;
        stable.converter.count = 400;
        int dirtyBefore = stable.converter.dirtyCalls;
        int updatesBefore = stable.world.blockUpdates;

        stable.converter.checkStatus();
        stable.converter.checkStatus();

        assertEquals(2, stable.converter.status);
        assertEquals(400, stable.converter.count);
        assertEquals(dirtyBefore, stable.converter.dirtyCalls);
        assertEquals(updatesBefore, stable.world.blockUpdates);

        ConversionFixture unsafe = ConversionFixture.energized();
        unsafe.converter.status = 2;
        unsafe.converter.count = 51;
        unsafe.world.powered.put(STABILIZER_POS, true);
        unsafe.converter.checkStatus();

        assertEquals(1, unsafe.world.explosions);
        assertFalse(unsafe.world.isNodeType(5));
        assertEquals(0, unsafe.converter.status);
        assertEquals(50, unsafe.converter.count);

        ConversionFixture safe = ConversionFixture.energized();
        safe.converter.status = 2;
        safe.converter.count = 50;
        safe.world.powered.put(STABILIZER_POS, true);
        safe.converter.checkStatus();

        assertEquals(0, safe.world.explosions);
        assertEquals(2, safe.converter.status);
        assertEquals(50, safe.converter.count);

        safe.converter.update();

        assertEquals(0, safe.world.explosions);
        assertTrue(safe.world.isNodeType(0));
    }

    @Test
    public void nodeIdentityModifierAndBaseAspectsRoundTripWithEmptyCurrentVis() {
        AspectList base = new AspectList().add(Aspect.AIR, 36).add(Aspect.MAGIC, 9);
        ConversionFixture fixture = ConversionFixture.natural(this.nodeType, this.nodeModifier, base);
        fixture.converter.status = 1;
        fixture.converter.count = 1000;

        fixture.converter.update();

        TileNodeEnergized energized = fixture.energizedNode();
        assertSame(this.nodeType, energized.getNodeType());
        assertSame(this.nodeModifier, energized.getNodeModifier());
        assertAspectAmounts(base, energized.getAuraBase());

        energized.update();
        Map<WorldCoordinates, WeakReference<thaumcraft.api.visnet.TileVisNode>> sources =
                VisNetHandler.sources.get(0);
        assertTrue(sources != null && sources.containsKey(energized.getLocation()));

        fixture.world.powered.put(CONVERTER_POS, false);
        fixture.converter.count = 50;
        fixture.converter.update();

        TileNode restored = fixture.naturalNode();
        assertTrue(energized.isInvalid());
        assertFalse(VisNetHandler.sources.get(0).containsKey(energized.getLocation()));
        assertSame(this.nodeType, restored.getNodeType());
        assertSame(this.nodeModifier, restored.getNodeModifier());
        assertAspectAmounts(base, restored.getAspectsBase());
        assertEquals(0, restored.getAspects().visSize());
    }

    private static void assertAspectAmounts(AspectList expected, AspectList actual) {
        assertEquals(expected.size(), actual.size());
        for (Aspect aspect : expected.getAspects()) {
            assertEquals(expected.getAmount(aspect), actual.getAmount(aspect));
        }
    }

    private static final class ConversionFixture {
        private final ConversionWorld world = new ConversionWorld();
        private final TestNodeConverter converter = new TestNodeConverter();

        private static ConversionFixture natural(NodeType type, NodeModifier modifier, AspectList aspects) {
            ConversionFixture fixture = new ConversionFixture();
            TileNode node = new TestTileNode();
            node.setAspects(aspects);
            node.setNodeType(type);
            node.setNodeModifier(modifier);
            fixture.world.attach(NODE_POS, ConfigBlocks.blockAiry.getStateFromMeta(0), node);
            return fixture;
        }

        private static ConversionFixture energized() {
            ConversionFixture fixture = new ConversionFixture();
            TileNodeEnergized node = new TileNodeEnergized();
            node.setAspects(new AspectList().add(Aspect.AIR, 16));
            node.setupNode();
            fixture.world.attach(NODE_POS, ConfigBlocks.blockAiry.getStateFromMeta(5), node);
            return fixture;
        }

        private ConversionFixture() {
            this.attachStabilizer(9);
            this.world.attach(CONVERTER_POS, ConfigBlocks.blockStoneDevice.getStateFromMeta(11), this.converter);
            this.world.powered.put(CONVERTER_POS, true);
        }

        private void attachStabilizer(int meta) {
            this.world.attach(STABILIZER_POS, ConfigBlocks.blockStoneDevice.getStateFromMeta(meta),
                    new TileNodeStabilizer());
        }

        private TileNode naturalNode() {
            return (TileNode) this.world.getTileEntity(NODE_POS);
        }

        private TileNodeEnergized energizedNode() {
            return (TileNodeEnergized) this.world.getTileEntity(NODE_POS);
        }
    }

    private static final class TestNodeConverter extends TileNodeConverter {
        private int dirtyCalls;

        @Override
        public void markDirty() {
            this.dirtyCalls++;
        }
    }

    private static final class TestTileNode extends TileNode {
        @Override
        public void markDirty() {
        }
    }

    private static final class ConversionWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();
        private final Map<BlockPos, Boolean> powered = new HashMap<>();
        private int explosions;
        private int blockUpdates;

        private ConversionWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT),
                            "node_converter_runtime"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
            this.rand.setSeed(0L);
        }

        private void attach(BlockPos pos, IBlockState state, TileEntity tile) {
            BlockPos key = pos.toImmutable();
            TileEntity old = this.tiles.put(key, tile);
            if (old != null) {
                old.invalidate();
            }
            this.states.put(key, state);
            tile.setWorld(this);
            tile.setPos(key);
        }

        private void remove(BlockPos pos) {
            this.setBlockToAir(pos);
        }

        private boolean isNodeType(int type) {
            IBlockState state = this.getBlockState(NODE_POS);
            return state.getBlock() == ConfigBlocks.blockAiry && state.getValue(BlockAiry.TYPE) == type;
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            IBlockState state = this.states.get(pos);
            return state == null ? Blocks.AIR.getDefaultState() : state;
        }

        @Override
        public boolean isAirBlock(BlockPos pos) {
            return this.getBlockState(pos).getBlock() == Blocks.AIR;
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
            TileEntity old = this.tiles.remove(key);
            if (old != null) {
                old.invalidate();
            }
            if (state.getBlock() == Blocks.AIR) {
                this.states.remove(key);
                return true;
            }
            this.states.put(key, state);
            if (state.getBlock() == ConfigBlocks.blockAiry) {
                int type = state.getValue(BlockAiry.TYPE);
                TileEntity replacement = type == 0 ? new TestTileNode()
                        : type == 5 ? new TileNodeEnergized() : null;
                if (replacement != null) {
                    replacement.setWorld(this);
                    replacement.setPos(key);
                    this.tiles.put(key, replacement);
                }
            }
            return true;
        }

        @Override
        public boolean setBlockToAir(BlockPos pos) {
            return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }

        @Override
        public Explosion createExplosion(Entity entityIn, double x, double y, double z, float strength,
                                         boolean isSmoking) {
            this.explosions++;
            return null;
        }

        @Override
        public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
            this.blockUpdates++;
        }

        @Override
        public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam) {
        }

        @Override
        public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) {
        }

        @Override
        public void updateComparatorOutputLevel(BlockPos pos, Block blockIn) {
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "node_converter_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
