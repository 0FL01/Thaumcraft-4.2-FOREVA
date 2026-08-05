package thaumcraft.common.tiles;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
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
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.WorldCoordinates;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.wands.StaffRod;
import thaumcraft.api.visnet.TileVisNode;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.config.ConfigBlocks;

import java.awt.Color;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
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
        if (ConfigBlocks.blockFluxGoo == null || ConfigBlocks.blockFluxGas == null) {
            ConfigBlocks.init();
        }
        GameRegistry.registerTileEntity(TileVisRelay.class, new ResourceLocation("thaumcraft", "tilevisrelay"));
    }

    @Before
    public void clearVisNetwork() {
        VisNetHandler.sources.clear();
        nearbyNodes().clear();
        TileVisRelay.nearbyPlayers.clear();
    }

    @After
    public void cleanVisNetwork() {
        VisNetHandler.sources.clear();
        nearbyNodes().clear();
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
            assertEquals("charging must not duplicate vis for " + aspect.getTag(),
                    0, source.vis.getAmount(aspect));
        }
        assertEquals("each relay should emit one rate-limited pulse event like TC4", 2, world.blockEvents);
    }

    @Test
    public void workbenchChargerRejectsStaffsWithoutDrainingSource() {
        VisWorld world = new VisWorld(false);
        BlockPos sourcePos = new BlockPos(0, 64, 0);
        BlockPos relayPos = new BlockPos(7, 64, 0);
        BlockPos chargerPos = new BlockPos(14, 64, 0);
        TestEnergizedNode source = new TestEnergizedNode();
        TestRelay relay = new TestRelay();
        TestCharger charger = new TestCharger();
        TestWorkbench workbench = new TestWorkbench();
        ItemWandCasting wandItem = new ItemWandCasting();
        ItemStack staff = new ItemStack(wandItem);
        ItemWandCasting.setRod(staff, new StaffRod("charger_test", 50, new ItemStack(Blocks.LOG), 1));

        world.attach(sourcePos, source);
        world.attach(relayPos, relay);
        world.attach(chargerPos, charger);
        world.attach(chargerPos.down(), workbench);
        world.putState(sourcePos, Blocks.STONE.getDefaultState());
        world.putState(relayPos, Blocks.STONE.getDefaultState());
        world.putState(chargerPos, Blocks.STONE.getDefaultState());
        world.putState(chargerPos.down(), Blocks.STONE.getDefaultState());
        workbench.setInventorySlotContents(10, staff);

        source.update();
        relay.update();
        charger.update();

        assertTrue(wandItem.isStaff(staff));
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            assertEquals(0, ItemWandCasting.getVis(staff, aspect));
            assertEquals(4, source.vis.getAmount(aspect));
        }
    }

    @Test
    public void workbenchChargerCapsVisAndConsumesOnlyWhatItStores() {
        VisWorld world = new VisWorld(false);
        BlockPos sourcePos = new BlockPos(0, 64, 0);
        BlockPos relayPos = new BlockPos(7, 64, 0);
        BlockPos chargerPos = new BlockPos(14, 64, 0);
        TestEnergizedNode source = new TestEnergizedNode();
        TestRelay relay = new TestRelay();
        TestCharger charger = new TestCharger();
        TestWorkbench workbench = new TestWorkbench();
        ItemWandCasting wandItem = new ItemWandCasting();
        ItemStack wand = new ItemStack(wandItem);
        int capacity = ItemWandCasting.getMaxVis(wand);
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            ItemWandCasting.setVis(wand, aspect, capacity);
        }
        ItemWandCasting.setVis(wand, Aspect.AIR, capacity - 2);

        world.attach(sourcePos, source);
        world.attach(relayPos, relay);
        world.attach(chargerPos, charger);
        world.attach(chargerPos.down(), workbench);
        world.putState(sourcePos, Blocks.STONE.getDefaultState());
        world.putState(relayPos, Blocks.STONE.getDefaultState());
        world.putState(chargerPos, Blocks.STONE.getDefaultState());
        world.putState(chargerPos.down(), Blocks.STONE.getDefaultState());
        workbench.setInventorySlotContents(10, wand);

        source.update();
        relay.update();
        charger.update();

        assertEquals(capacity, ItemWandCasting.getVis(wand, Aspect.AIR));
        assertEquals(2, source.vis.getAmount(Aspect.AIR));
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            if (aspect != Aspect.AIR) {
                assertEquals(capacity, ItemWandCasting.getVis(wand, aspect));
                assertEquals(4, source.vis.getAmount(aspect));
            }
        }
    }

    @Test
    public void infernalFurnaceDrainsIgnisThroughForcedRelayChain() {
        VisWorld world = new VisWorld(false);
        TestEnergizedNode source = new TestEnergizedNode();
        TestRelay relay = new TestRelay();
        TestArcaneFurnace furnace = new TestArcaneFurnace();
        attachForcedRelayChain(world, source, relay, furnace);

        furnace.update();

        assertEquals(4, furnace.speedyTime);
        assertEquals(0, source.vis.getAmount(Aspect.FIRE));
        assertEquals(1, world.blockEvents);
    }

    @Test
    public void fluxScrubberDrainsAerThroughForcedRelayChain() {
        VisWorld world = new VisWorld(false);
        TestEnergizedNode source = new TestEnergizedNode();
        TestRelay relay = new TestRelay();
        TestFluxScrubber scrubber = new TestFluxScrubber();
        attachForcedRelayChain(world, source, relay, scrubber);

        scrubber.update();

        assertEquals(4, scrubber.power);
        assertEquals(0, source.vis.getAmount(Aspect.AIR));
        assertEquals(1, world.blockEvents);
    }

    @Test
    public void fluxScrubberConsumesGooAndGasThroughPoweredRelayChain() {
        VisWorld world = new VisWorld(false);
        TestEnergizedNode source = new TestEnergizedNode();
        TestRelay relay = new TestRelay();
        TestFluxScrubber scrubber = new TestFluxScrubber();
        attachForcedRelayChain(world, source, relay, scrubber);
        BlockPos gooPos = scrubber.getPos().south(2);
        BlockPos gasPos = scrubber.getPos().north(2);
        world.putState(gooPos, ConfigBlocks.blockFluxGoo.getStateFromMeta(3));
        world.putState(gasPos, ConfigBlocks.blockFluxGas.getStateFromMeta(0));
        scrubber.checklist.add(new thaumcraft.api.BlockCoordinates(gooPos.getX(), gooPos.getY(), gooPos.getZ()));
        scrubber.checklist.add(new thaumcraft.api.BlockCoordinates(gasPos.getX(), gasPos.getY(), gasPos.getZ()));

        scrubber.update();
        source.update();
        scrubber.update();

        assertEquals(2, ConfigBlocks.blockFluxGoo.getMetaFromState(world.getBlockState(gooPos)));
        assertEquals(3, scrubber.power);
        assertEquals(1, scrubber.charges);
        assertEquals(1, scrubber.cleanupEffects);

        source.update();
        scrubber.update();

        assertTrue(world.isAirBlock(gasPos));
        assertEquals(2, scrubber.power);
        assertEquals(2, scrubber.charges);
        assertEquals(2, scrubber.cleanupEffects);
    }

    @Test
    public void fluxScrubberRequiresPowerAndStrictSixteenBlockRadius() {
        VisWorld world = new VisWorld(false);
        TestFluxScrubber scrubber = new TestFluxScrubber();
        BlockPos scrubberPos = new BlockPos(0, 64, 0);
        BlockPos nearbyFlux = scrubberPos.east(2);
        BlockPos boundaryFlux = scrubberPos.east(16);
        world.attach(scrubberPos, scrubber);
        world.putState(scrubberPos, Blocks.STONE.getDefaultState());
        world.putState(nearbyFlux, ConfigBlocks.blockFluxGoo.getStateFromMeta(1));
        scrubber.power = 4;
        scrubber.checklist.add(new thaumcraft.api.BlockCoordinates(nearbyFlux.getX(), nearbyFlux.getY(), nearbyFlux.getZ()));

        scrubber.update();

        assertEquals(1, ConfigBlocks.blockFluxGoo.getMetaFromState(world.getBlockState(nearbyFlux)));
        assertEquals(4, scrubber.power);
        assertEquals(0, scrubber.charges);

        world.putState(boundaryFlux, ConfigBlocks.blockFluxGas.getStateFromMeta(1));
        scrubber.power = 5;
        scrubber.checklist.clear();
        scrubber.checklist.add(new thaumcraft.api.BlockCoordinates(boundaryFlux.getX(), boundaryFlux.getY(), boundaryFlux.getZ()));
        scrubber.update();

        assertEquals(1, ConfigBlocks.blockFluxGas.getMetaFromState(world.getBlockState(boundaryFlux)));
        assertEquals(5, scrubber.power);
        assertEquals(0, scrubber.cleanupEffects);
    }

    @Test
    public void advancedAlchemyFurnaceDrainsAllRequiredPrimalsThroughForcedRelayChain() {
        VisWorld world = new VisWorld(false);
        TestEnergizedNode source = new TestEnergizedNode();
        TestRelay relay = new TestRelay();
        TestAdvancedAlchemyFurnace furnace = new TestAdvancedAlchemyFurnace();
        attachForcedRelayChain(world, source, relay, furnace);

        for (int i = 0; i < 5; ++i) {
            furnace.update();
        }

        assertEquals(4, furnace.heat);
        assertEquals(4, furnace.power1);
        assertEquals(4, furnace.power2);
        assertEquals(0, source.vis.getAmount(Aspect.FIRE));
        assertEquals(0, source.vis.getAmount(Aspect.ENTROPY));
        assertEquals(0, source.vis.getAmount(Aspect.WATER));
        assertEquals(1, world.blockEvents);
    }

    @Test
    public void relayUpdatePacketRoundTripsPlacementColorAndParentState() {
        VisWorld serverWorld = new VisWorld(false);
        TestEnergizedNode parent = new TestEnergizedNode();
        TileVisRelay relay = new TileVisRelay();
        serverWorld.attach(new BlockPos(2, 63, 1), parent);
        serverWorld.attach(new BlockPos(7, 64, -3), relay);
        relay.orientation = (byte) EnumFacing.EAST.getIndex();
        relay.color = 4;
        relay.setAttunement((byte) 4);
        relay.setParent(new WeakReference<TileVisNode>(parent));

        SPacketUpdateTileEntity packet = relay.getUpdatePacket();
        NBTTagCompound sync = packet.getNbtCompound();
        assertEquals(EnumFacing.EAST.getIndex(), sync.getByte("orientation"));
        assertEquals(4, sync.getByte("color"));
        assertEquals(4, sync.getByte("attunement"));
        assertEquals(5, sync.getByte("px"));
        assertEquals(1, sync.getByte("py"));
        assertEquals(-4, sync.getByte("pz"));

        TileVisRelay restored = new TileVisRelay();
        restored.onDataPacket(null, packet);
        assertEquals(EnumFacing.EAST.getIndex(), restored.orientation);
        assertEquals(4, restored.color);
        assertEquals(4, restored.getAttunement());
        assertTrue(restored.parentLoaded);
        assertEquals(5, restored.px);
        assertEquals(1, restored.py);
        assertEquals(-4, restored.pz);
    }

    @Test
    public void relayPropagatesColoredClientPulse() {

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

    @Test
    public void removingRelayClearsAndRecomputesCachedDrainerRoute() {
        VisWorld world = new VisWorld(false);
        TestEnergizedNode source = new TestEnergizedNode();
        TestRelay relay = new TestRelay();
        BlockPos sourcePos = new BlockPos(0, 64, 0);
        BlockPos relayPos = new BlockPos(7, 64, 0);
        BlockPos drainerPos = new BlockPos(14, 64, 0);
        world.attach(sourcePos, source);
        world.attach(relayPos, relay);
        world.putState(sourcePos, Blocks.STONE.getDefaultState());
        world.putState(relayPos, Blocks.STONE.getDefaultState());
        source.update();
        relay.update();

        assertEquals(1, VisNetHandler.drainVis(world, drainerPos.getX(), drainerPos.getY(), drainerPos.getZ(),
                Aspect.AIR, 1));
        WorldCoordinates drainer = new WorldCoordinates(drainerPos.getX(), drainerPos.getY(), drainerPos.getZ(), 0);
        assertSame(relay, nearbyNodes().get(drainer).get(0).get());

        relay.removeThisNode();
        assertTrue(nearbyNodes().isEmpty());
        source.update();
        assertEquals(0, VisNetHandler.drainVis(world, drainerPos.getX(), drainerPos.getY(), drainerPos.getZ(),
                Aspect.AIR, 1));
        assertTrue(nearbyNodes().containsKey(drainer));
        assertTrue(nearbyNodes().get(drainer).isEmpty());
    }

    @Test
    public void retuningRelayClearsAndRecomputesCachedDrainerRoute() {
        VisWorld world = new VisWorld(false);
        TestEnergizedNode source = new TestEnergizedNode();
        TestRelay relay = new TestRelay();
        BlockPos sourcePos = new BlockPos(0, 64, 0);
        BlockPos relayPos = new BlockPos(7, 64, 0);
        BlockPos drainerPos = new BlockPos(14, 64, 0);
        world.attach(sourcePos, source);
        world.attach(relayPos, relay);
        world.putState(sourcePos, Blocks.STONE.getDefaultState());
        world.putState(relayPos, Blocks.STONE.getDefaultState());
        source.update();
        relay.update();

        assertEquals(1, VisNetHandler.drainVis(world, drainerPos.getX(), drainerPos.getY(), drainerPos.getZ(),
                Aspect.AIR, 1));
        assertFalse(nearbyNodes().isEmpty());

        assertTrue(relay.setRelayColor((byte) 1));
        assertTrue(nearbyNodes().isEmpty());
        relay.update();
        assertEquals(1, VisNetHandler.drainVis(world, drainerPos.getX(), drainerPos.getY(), drainerPos.getZ(),
                Aspect.AIR, 1));
        WorldCoordinates drainer = new WorldCoordinates(drainerPos.getX(), drainerPos.getY(), drainerPos.getZ(), 0);
        assertSame(relay, nearbyNodes().get(drainer).get(0).get());
    }

    @SuppressWarnings("unchecked")
    private static Map<WorldCoordinates, ArrayList<WeakReference<TileVisNode>>> nearbyNodes() {
        try {
            Field field = VisNetHandler.class.getDeclaredField("nearbyNodes");
            field.setAccessible(true);
            return (Map<WorldCoordinates, ArrayList<WeakReference<TileVisNode>>>) field.get(null);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static RayTraceResult trace(World world, BlockPos source, BlockPos target) {
        return ThaumcraftApiHelper.rayTraceIgnoringSource(world,
                new Vec3d(source.getX() + 0.5D, source.getY() + 0.5D, source.getZ() + 0.5D),
                new Vec3d(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D),
                false, true, false);
    }

    private static void attachForcedRelayChain(VisWorld world, TestEnergizedNode source,
                                                TestRelay relay, TileEntity consumer) {
        BlockPos sourcePos = new BlockPos(0, 64, 0);
        BlockPos relayPos = new BlockPos(7, 64, 0);
        BlockPos consumerPos = new BlockPos(14, 64, 0);
        world.attach(sourcePos, source);
        world.attach(relayPos, relay);
        world.attach(consumerPos, consumer);
        world.putState(sourcePos, Blocks.STONE.getDefaultState());
        world.putState(relayPos, Blocks.STONE.getDefaultState());
        world.putState(consumerPos, Blocks.STONE.getDefaultState());

        source.update();
        relay.update();

        assertTrue(VisNetHandler.isNodeValid(relay.getParent()));
        assertSame(source, relay.getParent().get());
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

    private static class TestArcaneFurnace extends TileArcaneFurnace {
        @Override
        public void markDirty() {
        }
    }

    private static class TestFluxScrubber extends TileFluxScrubber {
        private int cleanupEffects;

        @Override
        public void markDirty() {
        }

        @Override
        void sendFluxCleanupEffect(BlockPos target) {
            this.cleanupEffects++;
        }
    }

    private static class TestAdvancedAlchemyFurnace extends TileAlchemyFurnaceAdvanced {
        @Override
        void sync(boolean relight) {
        }

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
        public boolean setBlockState(BlockPos pos, IBlockState state, int flags) {
            this.states.put(pos.toImmutable(), state);
            return true;
        }

        @Override
        public boolean setBlockToAir(BlockPos pos) {
            this.states.remove(pos);
            return true;
        }

        @Override
        public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
        }

        @Override
        public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam) {
            this.blockEvents++;
        }

        @Override
        public void playSound(net.minecraft.entity.player.EntityPlayer player, BlockPos pos, SoundEvent sound,
                              SoundCategory category, float volume, float pitch) {
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
