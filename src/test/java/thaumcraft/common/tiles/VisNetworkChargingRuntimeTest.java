package thaumcraft.common.tiles;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.visnet.TileVisNode;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.common.items.wands.ItemWandCasting;

import java.awt.Color;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class VisNetworkChargingRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Before
    public void clearVisNetwork() {
        VisNetHandler.sources.clear();
        TileVisRelay.nearbyPlayers.clear();
    }

    @After
    public void cleanVisNetwork() {
        VisNetHandler.sources.clear();
        TileVisRelay.nearbyPlayers.clear();
    }

    @Test
    public void rayTraceSkipsCollidableSourceButStillFindsTargetsAndBlockers() {
        VisWorld world = new VisWorld(false);
        BlockPos source = new BlockPos(0, 64, 0);
        BlockPos blocker = new BlockPos(2, 64, 0);
        BlockPos target = new BlockPos(4, 64, 0);
        world.putState(source, Blocks.STONE.getDefaultState());
        world.putState(target, Blocks.STONE.getDefaultState());

        RayTraceResult clearHit = trace(world, source, target);
        assertEquals(target, clearHit.getBlockPos());

        TestRelay sourceRelay = new TestRelay();
        TestRelay targetRelay = new TestRelay();
        world.attach(source, sourceRelay);
        world.attach(target, targetRelay);
        assertTrue(VisNetHandler.canNodeBeSeen(sourceRelay, targetRelay));

        world.putState(blocker, Blocks.STONE.getDefaultState());
        RayTraceResult blockedHit = trace(world, source, target);
        assertEquals(blocker, blockedHit.getBlockPos());
        assertFalse(VisNetHandler.canNodeBeSeen(sourceRelay, targetRelay));
    }

    @Test
    public void energizedNodeChargesWorkbenchWandThroughForcedRelayChain() {
        VisWorld world = new VisWorld(false);
        BlockPos sourcePos = new BlockPos(0, 64, 0);
        BlockPos relayPos = new BlockPos(7, 64, 0);
        BlockPos chargerPos = new BlockPos(14, 64, 0);
        BlockPos workbenchPos = chargerPos.down();
        TestEnergizedNode source = new TestEnergizedNode();
        TestRelay relay = new TestRelay();
        TestCharger charger = new TestCharger();
        TestWorkbench workbench = new TestWorkbench();

        world.attach(sourcePos, source);
        world.attach(relayPos, relay);
        world.attach(chargerPos, charger);
        world.attach(workbenchPos, workbench);
        world.putState(sourcePos, Blocks.STONE.getDefaultState());
        world.putState(relayPos, Blocks.STONE.getDefaultState());
        world.putState(chargerPos, Blocks.STONE.getDefaultState());
        world.putState(workbenchPos, Blocks.STONE.getDefaultState());

        ItemWandCasting wandItem = new ItemWandCasting();
        ItemStack wand = new ItemStack(wandItem);
        workbench.setInventorySlotContents(10, wand);

        source.update();
        relay.update();
        charger.update();

        assertTrue(VisNetHandler.isNodeValid(relay.getParent()));
        assertSame(source, relay.getParent().get());
        assertTrue(VisNetHandler.isNodeValid(charger.getParent()));
        assertSame(relay, charger.getParent().get());
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            assertEquals("energized node should provide its four CV/tick for " + aspect.getTag(),
                    4, ItemWandCasting.getVis(wand, aspect));
        }
        assertEquals("each relay should emit one rate-limited pulse event like TC4", 2, world.blockEvents);
    }

    @Test
    public void relaySyncsParentOffsetAndPropagatesColoredClientPulse() {
        VisWorld serverWorld = new VisWorld(false);
        TestEnergizedNode parent = new TestEnergizedNode();
        TestRelay relay = new TestRelay();
        serverWorld.attach(new BlockPos(2, 63, 1), parent);
        serverWorld.attach(new BlockPos(7, 64, -3), relay);
        relay.setParent(new WeakReference<TileVisNode>(parent));

        NBTTagCompound sync = new NBTTagCompound();
        relay.writeCustomNBT(sync);
        assertEquals(5, sync.getByte("px"));
        assertEquals(1, sync.getByte("py"));
        assertEquals(-4, sync.getByte("pz"));

        TestRelay restored = new TestRelay();
        restored.readCustomNBT(sync);
        assertTrue(restored.parentLoaded);
        assertEquals(5, restored.px);
        assertEquals(1, restored.py);
        assertEquals(-4, restored.pz);

        VisWorld clientWorld = new VisWorld(true);
        TestRelay upstream = new TestRelay();
        TestRelay downstream = new TestRelay();
        clientWorld.attach(new BlockPos(0, 64, 0), upstream);
        clientWorld.attach(new BlockPos(4, 64, 0), downstream);
        downstream.setParent(new WeakReference<TileVisNode>(upstream));

        assertTrue(downstream.receiveClientEvent(0, 1));
        Color fire = new Color(TileVisRelay.colors[1]);
        assertEquals(5, downstream.pulse);
        assertEquals(5, upstream.pulse);
        assertEquals(fire.getRed() / 255.0F, downstream.pRed, 0.0001F);
        assertEquals(downstream.pRed, upstream.pRed, 0.0001F);
        assertEquals(downstream.pGreen, upstream.pGreen, 0.0001F);
        assertEquals(downstream.pBlue, upstream.pBlue, 0.0001F);
    }

    private static RayTraceResult trace(World world, BlockPos source, BlockPos target) {
        return ThaumcraftApiHelper.rayTraceIgnoringSource(world,
                new Vec3d(source.getX() + 0.5D, source.getY() + 0.5D, source.getZ() + 0.5D),
                new Vec3d(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D),
                false, true, false);
    }

    private static class TestEnergizedNode extends TileNodeEnergized {
        @Override
        public void markDirty() {
        }
    }

    private static class TestRelay extends TileVisRelay {
        @Override
        public void markDirty() {
        }
    }

    private static class TestCharger extends TileMagicWorkbenchCharger {
        @Override
        public void markDirty() {
        }
    }

    private static class TestWorkbench extends TileMagicWorkbench {
        @Override
        public void markDirty() {
        }
    }

    private static class VisWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();
        private int blockEvents;

        VisWorld(boolean remote) {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT), "vis_network_runtime"),
                    new WorldProviderSurface(), new Profiler(), remote);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        void putState(BlockPos pos, IBlockState state) {
            this.states.put(pos.toImmutable(), state);
        }

        void attach(BlockPos pos, TileEntity tile) {
            tile.setWorld(this);
            tile.setPos(pos);
            this.tiles.put(pos.toImmutable(), tile);
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
        public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
        }

        @Override
        public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam) {
            this.blockEvents++;
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "vis_network_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }

        @Override
        public long getTotalWorldTime() {
            return 1L;
        }
    }
}
