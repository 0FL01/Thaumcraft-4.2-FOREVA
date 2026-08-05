package thaumcraft.common.items.wands.foci;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
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
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blocks.BlockHole;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.TileHole;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class PortableHoleC5RuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
        if (ConfigBlocks.blockHole == null) {
            ConfigBlocks.blockHole = new BlockHole();
        }
    }

    @Test
    public void deniedInitialTargetDoesNotChargeMutateOrPlaySound() {
        HoleWorld world = new HoleWorld();
        world.editsAllowed = false;
        TestPlayer player = new TestPlayer(world, UUID.randomUUID(), "denied-hole");
        CountingWand wand = new CountingWand();
        ItemStack wandStack = new ItemStack(wand);
        BlockPos start = new BlockPos(4, 70, 8);
        RayTraceResult hit = new RayTraceResult(new Vec3d(4.5D, 70.5D, 8.5D), EnumFacing.NORTH, start);

        new FocusPortableHole().onFocusRightClick(wandStack, world, player, hit);

        assertEquals(0, wand.consumptions);
        assertTrue(world.transitions.isEmpty());
        assertEquals(0, world.soundCount);
        assertEquals(0, world.clientUpdates);
    }

    @Test
    public void authorizedScanTruncatesAtFirstDeniedDepth() {
        HoleWorld world = new HoleWorld();
        TestPlayer player = new TestPlayer(world, UUID.randomUUID(), "truncated-hole");
        BlockPos start = new BlockPos(1, 64, 1);
        world.denied.add(start.south(2));

        assertEquals(2, FocusPortableHole.getTunnelDistance(
                world, start, EnumFacing.NORTH, player, 8));
        assertEquals(start, world.authorizationChecks.get(0));
        assertEquals(start.south(), world.authorizationChecks.get(1));
        assertEquals(start.south(2), world.authorizationChecks.get(2));
    }

    @Test
    public void ownerUuidSurvivesSaveAndUpdatePacketAndMissingOwnerClearsIt() {
        UUID owner = UUID.fromString("00000000-0000-0000-0000-000000000501");
        TileHole source = new TileHole();
        source.setStoredBlock(Blocks.STONE.getStateFromMeta(3), (NBTTagCompound) null,
                (short) 240, (byte) 7, (byte) 5, owner);

        NBTTagCompound saved = new NBTTagCompound();
        source.writeCustomNBT(saved);
        assertEquals(owner, saved.getUniqueId("ownerUUID"));

        TileHole fromSave = new TileHole();
        fromSave.readCustomNBT(saved);
        NBTTagCompound savedAgain = new NBTTagCompound();
        fromSave.writeCustomNBT(savedAgain);
        assertEquals(owner, savedAgain.getUniqueId("ownerUUID"));
        assertEquals(240, savedAgain.getShort("countdownmax"));
        assertEquals(7, savedAgain.getByte("count"));
        assertEquals(5, savedAgain.getByte("direction"));

        TileHole fromPacket = new TileHole();
        fromPacket.onDataPacket(null, source.getUpdatePacket());
        NBTTagCompound packetRoundTrip = new NBTTagCompound();
        fromPacket.writeCustomNBT(packetRoundTrip);
        assertEquals(owner, packetRoundTrip.getUniqueId("ownerUUID"));

        fromPacket.readCustomNBT(new NBTTagCompound());
        NBTTagCompound ownerless = new NBTTagCompound();
        fromPacket.writeCustomNBT(ownerless);
        assertFalse(ownerless.hasUniqueId("ownerUUID"));
    }

    @Test
    public void offlineOwnerStopsPropagationButRestoresStateTileDataAndScheduledUpdate() {
        HoleWorld world = new HoleWorld();
        world.createRestoredDataTile = true;
        BlockPos pos = new BlockPos(10, 65, 10);
        NBTTagCompound storedTile = new NBTTagCompound();
        storedTile.setString("payload", "exact");
        TileHole hole = new TileHole();
        hole.setStoredBlock(Blocks.STONE.getDefaultState(), storedTile,
                (short) 2, (byte) 4, (byte) 2, UUID.randomUUID());
        world.attach(pos, ConfigBlocks.blockHole.getDefaultState(), hole);

        hole.update();
        assertEquals(0, hole.count);
        assertTrue(world.transitions.isEmpty());
        assertSame(ConfigBlocks.blockHole, world.getBlockState(pos).getBlock());

        hole.update();
        assertSame(Blocks.STONE, world.getBlockState(pos).getBlock());
        assertEquals(1, world.transitions.size());
        assertEquals(0, world.transitions.get(0).flags);
        assertEquals(1, world.clientUpdates);
        assertEquals(2, world.lastClientUpdateFlags);
        assertEquals(1, world.scheduledUpdates);
        assertEquals(2, world.lastScheduledDelay);
        assertEquals("exact", world.restoredDataTile.data.getString("payload"));
        assertEquals(pos.getX(), world.restoredDataTile.data.getInteger("x"));
        assertEquals(pos.getY(), world.restoredDataTile.data.getInteger("y"));
        assertEquals(pos.getZ(), world.restoredDataTile.data.getInteger("z"));
    }

    @Test
    public void ownerlessSavedHoleRestoresWithoutPropagation() {
        HoleWorld world = new HoleWorld();
        BlockPos pos = new BlockPos(12, 65, 12);
        NBTTagCompound saved = new NBTTagCompound();
        saved.setString("oldblockName", "minecraft:stone");
        saved.setShort("countdownmax", (short) 2);
        saved.setByte("count", (byte) 3);
        saved.setByte("direction", (byte) 4);
        TileHole hole = new TileHole();
        hole.readCustomNBT(saved);
        world.attach(pos, ConfigBlocks.blockHole.getDefaultState(), hole);

        hole.update();
        hole.update();

        assertEquals(0, hole.count);
        assertSame(Blocks.STONE, world.getBlockState(pos).getBlock());
        assertEquals(1, world.transitions.size());
        assertEquals(1, world.clientUpdates);
        assertEquals(1, world.scheduledUpdates);
    }

    @Test
    public void unauthorizedOpeningPlaneCellsAndCenterlineRemainUntouched() {
        HoleWorld world = new HoleWorld();
        world.editsAllowed = false;
        UUID ownerId = UUID.randomUUID();
        world.onlinePlayer = new TestPlayer(world, ownerId, "online-hole-owner");
        BlockPos pos = new BlockPos(20, 70, 20);
        TileHole hole = new TileHole();
        hole.setStoredBlock(Blocks.STONE.getDefaultState(), (NBTTagCompound) null,
                (short) 20, (byte) 2, (byte) 0, ownerId);
        world.attach(pos, ConfigBlocks.blockHole.getDefaultState(), hole);

        hole.update();

        assertEquals(0, hole.count);
        assertTrue(world.transitions.isEmpty());
        assertEquals(9, world.authorizationChecks.size());
        assertSame(ConfigBlocks.blockHole, world.getBlockState(pos).getBlock());
        for (BlockPos checked : world.authorizationChecks) {
            assertSame(Blocks.STONE, world.getBlockState(checked).getBlock());
        }
    }

    @Test
    public void failedSecondTransitionRollsBackExactStateAndRacingTileDataWithoutClientUpdate() {
        HoleWorld world = new HoleWorld();
        world.failHolePlacement = true;
        world.exposeRacingTile = true;
        world.createRestoredDataTile = true;
        BlockPos pos = new BlockPos(30, 75, 30);
        world.putState(pos, Blocks.PLANKS.getStateFromMeta(2));
        world.racingTile = new DataTile();
        world.racingTile.data.setString("payload", "rollback");
        TestPlayer player = new TestPlayer(world, UUID.randomUUID(), "rollback-hole");

        assertFalse(FocusPortableHole.createHole(world, pos.getX(), pos.getY(), pos.getZ(),
                EnumFacing.NORTH.getIndex(), (byte) 3, 120, player));

        assertEquals(3, world.transitions.size());
        assertSame(Blocks.AIR, world.transitions.get(0).state.getBlock());
        assertSame(ConfigBlocks.blockHole, world.transitions.get(1).state.getBlock());
        assertEquals(Blocks.PLANKS.getStateFromMeta(2), world.transitions.get(2).state);
        for (Transition transition : world.transitions) {
            assertEquals(0, transition.flags);
        }
        assertEquals("rollback", world.restoredDataTile.data.getString("payload"));
        assertEquals(0, world.clientUpdates);
        assertEquals(0, world.soundCount);
    }

    @Test
    public void missingPlacedTileRollsBackInsteadOfReportingSuccess() {
        HoleWorld world = new HoleWorld();
        world.createHoleTile = false;
        BlockPos pos = new BlockPos(32, 75, 32);
        world.putState(pos, Blocks.STONE.getDefaultState());
        TestPlayer player = new TestPlayer(world, UUID.randomUUID(), "missing-tile-hole");

        assertFalse(FocusPortableHole.createHole(world, pos.getX(), pos.getY(), pos.getZ(),
                EnumFacing.EAST.getIndex(), (byte) 2, 120, player));

        assertEquals(3, world.transitions.size());
        assertSame(Blocks.STONE, world.getBlockState(pos).getBlock());
        assertEquals(0, world.clientUpdates);
    }

    private static final class CountingWand extends ItemWandCasting {
        private final ItemStack focus = new ItemStack(new FocusPortableHole());
        private int consumptions;

        @Override
        public ItemStack getFocusItem(ItemStack stack) {
            return this.focus;
        }

        @Override
        public boolean consumeAllVis(ItemStack stack, EntityPlayer player, AspectList cost,
                                     boolean doit, boolean crafting) {
            this.consumptions++;
            return true;
        }
    }

    private static final class TestPlayer extends EntityPlayer {
        private TestPlayer(World world, UUID id, String name) {
            super(world, new GameProfile(id, name));
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
    }

    private static final class DataTile extends TileEntity {
        private NBTTagCompound data = new NBTTagCompound();

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound compound) {
            return this.data.copy();
        }

        @Override
        public void readFromNBT(NBTTagCompound compound) {
            this.data = compound.copy();
        }
    }

    private static final class Transition {
        private final IBlockState state;
        private final int flags;

        private Transition(IBlockState state, int flags) {
            this.state = state;
            this.flags = flags;
        }
    }

    private static final class HoleWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();
        private final Set<BlockPos> denied = new HashSet<>();
        private final List<BlockPos> authorizationChecks = new ArrayList<>();
        private final List<Transition> transitions = new ArrayList<>();
        private boolean editsAllowed = true;
        private boolean failHolePlacement;
        private boolean createHoleTile = true;
        private boolean exposeRacingTile;
        private boolean createRestoredDataTile;
        private int racingTileReads;
        private DataTile racingTile;
        private DataTile restoredDataTile;
        private EntityPlayer onlinePlayer;
        private int soundCount;
        private int clientUpdates;
        private int lastClientUpdateFlags;
        private int scheduledUpdates;
        private int lastScheduledDelay;

        private HoleWorld() {
            super(null,
                    new net.minecraft.world.storage.WorldInfo(new WorldSettings(
                            0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT), "portable_hole_c5"),
                    new WorldProviderSurface(), new Profiler(), false);
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
            this.states.put(pos.toImmutable(), state);
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            IBlockState state = this.states.get(pos);
            return state == null ? Blocks.STONE.getDefaultState() : state;
        }

        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            if (this.exposeRacingTile && this.getBlockState(pos).getBlock() == Blocks.PLANKS) {
                this.racingTileReads++;
                if (this.racingTileReads == 1) return null;
                if (this.tiles.containsKey(pos)) return this.tiles.get(pos);
                return this.racingTile;
            }
            return this.tiles.get(pos);
        }

        @Override
        public boolean setBlockState(BlockPos pos, IBlockState state, int flags) {
            this.transitions.add(new Transition(state, flags));
            if (state.getBlock() == ConfigBlocks.blockHole && this.failHolePlacement) {
                return false;
            }
            this.putState(pos, state);
            this.tiles.remove(pos);
            if (state.getBlock() == ConfigBlocks.blockHole && this.createHoleTile) {
                TileHole hole = new TileHole();
                hole.setWorld(this);
                hole.setPos(pos);
                this.tiles.put(pos.toImmutable(), hole);
            } else if (state.getBlock() != Blocks.AIR && this.createRestoredDataTile) {
                this.restoredDataTile = new DataTile();
                this.restoredDataTile.setWorld(this);
                this.restoredDataTile.setPos(pos);
                this.tiles.put(pos.toImmutable(), this.restoredDataTile);
            }
            return true;
        }

        @Override
        public boolean isBlockModifiable(EntityPlayer player, BlockPos pos) {
            this.authorizationChecks.add(pos.toImmutable());
            return this.editsAllowed && !this.denied.contains(pos);
        }

        @Override
        public EntityPlayer getPlayerEntityByUUID(UUID uuid) {
            return this.onlinePlayer != null && this.onlinePlayer.getUniqueID().equals(uuid)
                    ? this.onlinePlayer : null;
        }

        @Override
        public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
            this.clientUpdates++;
            this.lastClientUpdateFlags = flags;
        }

        @Override
        public void scheduleUpdate(BlockPos pos, Block blockIn, int delay) {
            this.scheduledUpdates++;
            this.lastScheduledDelay = delay;
        }

        @Override
        public void playSound(EntityPlayer player, BlockPos pos, SoundEvent soundIn, SoundCategory category,
                              float volume, float pitch) {
            this.soundCount++;
        }

        @Override public void markChunkDirty(BlockPos pos, TileEntity tileEntity) { }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "portable_hole_c5_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) { return true; }
    }
}
