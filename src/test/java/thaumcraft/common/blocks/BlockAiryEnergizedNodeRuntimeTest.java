package thaumcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
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
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileNodeConverter;
import thaumcraft.common.tiles.TileNodeEnergized;
import thaumcraft.common.tiles.TileNodeStabilizer;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BlockAiryEnergizedNodeRuntimeTest {
    private static final BlockPos NODE_POS = new BlockPos(0, 64, 0);
    private static final BlockPos STABILIZER_POS = NODE_POS.down();
    private static final BlockPos CONVERTER_POS = NODE_POS.up();

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
        if (ConfigBlocks.blockAiry == null) {
            ConfigBlocks.init();
        }
    }

    @Test
    public void intactConversionStructureDoesNotExplode() {
        ConversionFixture fixture = new ConversionFixture();

        fixture.notifyEnergizedNode(CONVERTER_POS);

        assertEquals(0, fixture.world.explosions);
        assertTrue(fixture.world.isEnergizedNodePresent());
    }

    @Test
    public void breakingConverterDuringReverseConversionExplodesEnergizedNode() {
        ConversionFixture fixture = new ConversionFixture();

        fixture.world.remove(CONVERTER_POS);
        fixture.notifyEnergizedNode(CONVERTER_POS);

        assertTrue(fixture.world.explosions > 0);
        assertFalse(fixture.world.isEnergizedNodePresent());
    }

    @Test
    public void breakingStabilizerDuringReverseConversionExplodesEnergizedNode() {
        ConversionFixture fixture = new ConversionFixture();

        fixture.world.remove(STABILIZER_POS);
        fixture.notifyEnergizedNode(STABILIZER_POS);

        assertTrue(fixture.world.explosions > 0);
        assertFalse(fixture.world.isEnergizedNodePresent());
    }

    @Test
    public void poweringStabilizerInvalidatesEnergizedNodeStructure() {
        ConversionFixture fixture = new ConversionFixture();

        fixture.world.powered.put(STABILIZER_POS, true);
        fixture.notifyEnergizedNode(STABILIZER_POS);

        assertTrue(fixture.world.explosions > 0);
        assertFalse(fixture.world.isEnergizedNodePresent());
    }

    @Test
    public void breakingEnergizedNodeStillExplodesThroughConverter() {
        ConversionFixture fixture = new ConversionFixture();

        fixture.world.remove(NODE_POS);
        ConfigBlocks.blockStoneDevice.neighborChanged(fixture.converterState, fixture.world, CONVERTER_POS,
                ConfigBlocks.blockAiry, NODE_POS);

        assertTrue(fixture.world.explosions > 0);
        assertFalse(fixture.world.isEnergizedNodePresent());
    }

    private static final class ConversionFixture {
        private final ConversionWorld world = new ConversionWorld();
        private final IBlockState nodeState = ConfigBlocks.blockAiry.getStateFromMeta(5);
        private final IBlockState converterState = ConfigBlocks.blockStoneDevice.getStateFromMeta(11);

        private ConversionFixture() {
            this.world.attach(STABILIZER_POS, ConfigBlocks.blockStoneDevice.getStateFromMeta(9),
                    new TileNodeStabilizer());
            this.world.attach(NODE_POS, this.nodeState, new TileNodeEnergized());
            TileNodeConverter converter = new TileNodeConverter();
            converter.status = 2;
            converter.count = 500;
            this.world.attach(CONVERTER_POS, this.converterState, converter);
        }

        private void notifyEnergizedNode(BlockPos changedPos) {
            ConfigBlocks.blockAiry.neighborChanged(this.nodeState, this.world, NODE_POS,
                    ConfigBlocks.blockStoneDevice, changedPos);
        }
    }

    private static final class ConversionWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();
        private final Map<BlockPos, Boolean> powered = new HashMap<>();
        private int explosions;

        private ConversionWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT),
                            "energized_node_runtime"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        private void attach(BlockPos pos, IBlockState state, TileEntity tile) {
            BlockPos key = pos.toImmutable();
            this.states.put(key, state);
            tile.setWorld(this);
            tile.setPos(key);
            this.tiles.put(key, tile);
        }

        private void remove(BlockPos pos) {
            this.states.remove(pos);
            this.tiles.remove(pos);
        }

        private boolean isEnergizedNodePresent() {
            IBlockState state = this.getBlockState(NODE_POS);
            return state.getBlock() == ConfigBlocks.blockAiry && state.getValue(BlockAiry.TYPE) == 5;
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
            this.tiles.remove(key);
            if (state.getBlock() == Blocks.AIR) {
                this.states.remove(key);
            } else {
                this.states.put(key, state);
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
                @Override public String makeString() { return "energized_node_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
