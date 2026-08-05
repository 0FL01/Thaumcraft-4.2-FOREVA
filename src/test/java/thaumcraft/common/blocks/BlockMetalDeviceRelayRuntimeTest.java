package thaumcraft.common.blocks;

import com.mojang.authlib.GameProfile;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
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
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.tiles.TileMagicWorkbenchCharger;
import thaumcraft.common.tiles.TileVisRelay;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BlockMetalDeviceRelayRuntimeTest {
    private static final BlockPos POS = new BlockPos(0, 64, 0);

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
        if (ConfigBlocks.blockMetalDevice == null) ConfigBlocks.init();
        if (ConfigItems.itemShard == null) ConfigItems.init();
    }

    @Test
    public void relayAndChargerShardTuningTogglesWithoutConsumptionOrNoOpChurn() {
        TestRelay relay = new TestRelay();
        TestCharger charger = new TestCharger();
        assertShardTuning(14, relay, relay);
        assertShardTuning(2, charger, charger);
    }

    @Test
    public void clientHandlesShardTuningWithoutMutatingTheRelay() {
        RelayWorld world = new RelayWorld(true);
        TestRelay relay = new TestRelay();
        IBlockState state = ConfigBlocks.blockMetalDevice.getStateFromMeta(14);
        world.attach(POS, state, relay);
        TestPlayer player = new TestPlayer(world);
        ItemStack shard = new ItemStack(ConfigItems.itemShard, 2, 4);
        player.setHeldItem(EnumHand.MAIN_HAND, shard);

        assertTrue(ConfigBlocks.blockMetalDevice.onBlockActivated(world, POS, state, player,
                EnumHand.MAIN_HAND, EnumFacing.UP, 0.5F, 0.5F, 0.5F));
        assertEquals(-1, relay.color);
        assertEquals(-1, relay.getAttunement());
        assertEquals(0, relay.removals);
        assertEquals(2, shard.getCount());
    }

    @Test
    public void relayDropsOnceWhenItsOppositeOrientationSupportIsRemoved() {
        for (EnumFacing orientation : EnumFacing.values()) {
            RelayWorld world = new RelayWorld(false);
            TestRelay relay = new TestRelay();
            relay.orientation = (byte) orientation.getIndex();
            IBlockState state = ConfigBlocks.blockMetalDevice.getStateFromMeta(14);
            BlockPos support = POS.offset(orientation.getOpposite());
            world.attach(POS, state, relay);
            world.putState(support, Blocks.STONE.getDefaultState());

            ConfigBlocks.blockMetalDevice.neighborChanged(state, world, POS, Blocks.STONE, support);
            assertEquals(0, world.destroyCalls);

            world.putState(support, Blocks.AIR.getDefaultState());
            ConfigBlocks.blockMetalDevice.neighborChanged(state, world, POS, Blocks.AIR, support);
            ConfigBlocks.blockMetalDevice.neighborChanged(state, world, POS, Blocks.AIR, support);

            assertEquals(1, world.destroyCalls);
            assertTrue(world.dropRequested);
            assertTrue(world.isAirBlock(POS));
        }
    }

    @Test
    public void workbenchChargerDoesNotUseRelaySupportRemovalRule() {
        RelayWorld world = new RelayWorld(false);
        TestCharger charger = new TestCharger();
        IBlockState state = ConfigBlocks.blockMetalDevice.getStateFromMeta(2);
        world.attach(POS, state, charger);

        ConfigBlocks.blockMetalDevice.neighborChanged(state, world, POS, Blocks.AIR, POS.down());

        assertEquals(0, world.destroyCalls);
        assertFalse(world.isAirBlock(POS));
    }

    private static void assertShardTuning(int metadata, TileVisRelay relay, TrackingRelay tracking) {
        RelayWorld world = new RelayWorld(false);
        IBlockState state = ConfigBlocks.blockMetalDevice.getStateFromMeta(metadata);
        world.attach(POS, state, relay);
        TestPlayer player = new TestPlayer(world);
        ItemStack shard = new ItemStack(ConfigItems.itemShard, 3, 0);
        player.setHeldItem(EnumHand.MAIN_HAND, shard);

        for (int channel = 0; channel <= 5; channel++) {
            shard.setItemDamage(channel);
            activate(world, state, player);
            assertEquals(channel, relay.color);
            assertEquals(channel, relay.getAttunement());
            assertEquals(channel + 1, tracking.removals());
            assertEquals(3, shard.getCount());
        }

        activate(world, state, player);
        assertEquals(-1, relay.color);
        assertEquals(-1, relay.getAttunement());
        assertEquals(7, tracking.removals());
        assertEquals(3, shard.getCount());

        shard.setItemDamage(6);
        int dirtyBeforeNoOp = tracking.dirtyCalls();
        int updatesBeforeNoOp = world.blockUpdates;
        int soundsBeforeNoOp = world.sounds;
        activate(world, state, player);
        assertEquals(-1, relay.color);
        assertEquals(7, tracking.removals());
        assertEquals(dirtyBeforeNoOp, tracking.dirtyCalls());
        assertEquals(updatesBeforeNoOp, world.blockUpdates);
        assertEquals(soundsBeforeNoOp, world.sounds);
        assertEquals(3, shard.getCount());

        shard.setItemDamage(4);
        activate(world, state, player);
        shard.setItemDamage(6);
        activate(world, state, player);
        assertEquals(-1, relay.color);
        assertEquals(-1, relay.getAttunement());
        assertEquals(9, tracking.removals());
        assertEquals(soundsBeforeNoOp + 2, world.sounds);
        assertEquals(3, shard.getCount());
    }

    private static void activate(RelayWorld world, IBlockState state, TestPlayer player) {
        assertTrue(ConfigBlocks.blockMetalDevice.onBlockActivated(world, POS, state, player,
                EnumHand.MAIN_HAND, EnumFacing.UP, 0.5F, 0.5F, 0.5F));
    }

    private interface TrackingRelay {
        int removals();
        int dirtyCalls();
    }

    private static class TestRelay extends TileVisRelay implements TrackingRelay {
        private int removals;
        private int dirtyCalls;

        @Override
        public void removeThisNode() {
            this.removals++;
            super.removeThisNode();
        }

        @Override
        public void markDirty() {
            this.dirtyCalls++;
        }

        @Override public int removals() { return this.removals; }
        @Override public int dirtyCalls() { return this.dirtyCalls; }
    }

    private static final class TestCharger extends TileMagicWorkbenchCharger implements TrackingRelay {
        private int removals;
        private int dirtyCalls;

        @Override public void removeThisNode() {
            this.removals++;
            super.removeThisNode();
        }
        @Override public void markDirty() { this.dirtyCalls++; }
        @Override public int removals() { return this.removals; }
        @Override public int dirtyCalls() { return this.dirtyCalls; }
    }

    private static final class TestPlayer extends EntityPlayer {
        private TestPlayer(World world) {
            super(world, new GameProfile(UUID.randomUUID(), "relay_test"));
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
    }

    private static final class RelayWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();
        private int blockUpdates;
        private int sounds;
        private int destroyCalls;
        private boolean dropRequested;

        private RelayWorld(boolean remote) {
            super(null, new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                    "relay_runtime"), new WorldProviderSurface(), new Profiler(), remote);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        private void attach(BlockPos pos, IBlockState state, TileEntity tile) {
            this.putState(pos, state);
            tile.setWorld(this);
            tile.setPos(pos);
            this.tiles.put(pos.toImmutable(), tile);
        }

        private void putState(BlockPos pos, IBlockState state) {
            if (state.getBlock() == Blocks.AIR) {
                this.states.remove(pos);
            } else {
                this.states.put(pos.toImmutable(), state);
            }
        }

        @Override public IBlockState getBlockState(BlockPos pos) {
            IBlockState state = this.states.get(pos);
            return state == null ? Blocks.AIR.getDefaultState() : state;
        }
        @Override public TileEntity getTileEntity(BlockPos pos) { return this.tiles.get(pos); }
        @Override public boolean isBlockPowered(BlockPos pos) { return false; }
        @Override public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
            this.blockUpdates++;
        }
        @Override public void playSound(EntityPlayer player, BlockPos pos, SoundEvent sound,
                                        SoundCategory category, float volume, float pitch) {
            this.sounds++;
        }
        @Override public boolean destroyBlock(BlockPos pos, boolean dropBlock) {
            if (this.isAirBlock(pos)) return false;
            this.destroyCalls++;
            this.dropRequested = dropBlock;
            this.states.remove(pos);
            TileEntity tile = this.tiles.remove(pos);
            if (tile != null) tile.invalidate();
            return true;
        }
        @Override protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "relay_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }
        @Override protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) { return true; }
    }
}
