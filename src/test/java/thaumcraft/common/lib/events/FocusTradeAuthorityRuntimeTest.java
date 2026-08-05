package thaumcraft.common.lib.events;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.IEventListener;
import net.minecraftforge.fml.common.eventhandler.ListenerList;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.foci.FocusTrade;
import thaumcraft.common.lib.network.PacketHandler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class FocusTradeAuthorityRuntimeTest {
    private static final BlockPos TARGET = new BlockPos(4, 70, -2);

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
        PacketHandler.init();
    }

    @Before
    @After
    public void clearSwapQueue() {
        ServerTickEventsFML.swapList.clear();
    }

    @Test
    public void offhandTradeExecutesWhileTheExactWandStackRemainsHeld() throws Exception {
        Fixture fixture = new Fixture();

        fixture.queueFromOffhandFocus();

        Queue<ServerTickEventsFML.VirtualSwapper> queue = ServerTickEventsFML.swapList.get(0);
        assertEquals(1, queue.size());
        assertEquals(EnumHand.OFF_HAND, queue.peek().hand);
        assertSame(fixture.wandStack, queue.peek().wandStack);
        assertEquals(EnumHand.OFF_HAND, fixture.player.swungHand);

        tickBlockSwap(fixture.world);

        assertEquals(Blocks.DIRT, fixture.world.getBlockState(TARGET).getBlock());
        assertEquals(0, countItem(fixture.player, Item.getItemFromBlock(Blocks.DIRT)));
        assertEquals(1, fixture.wand.simulatedCharges);
        assertEquals(1, fixture.wand.committedCharges);
        assertSame(fixture.wandStack, fixture.wand.lastChargedStack);
    }

    @Test
    public void deferredTradeAbortsAfterTheOffhandStackObjectIsReplaced() throws Exception {
        Fixture fixture = new Fixture();
        fixture.queueFromOffhandFocus();
        fixture.player.setHeldItem(EnumHand.OFF_HAND, fixture.wandStack.copy());

        tickBlockSwap(fixture.world);

        assertEquals(Blocks.STONE, fixture.world.getBlockState(TARGET).getBlock());
        assertEquals(1, countItem(fixture.player, Item.getItemFromBlock(Blocks.DIRT)));
        assertEquals(0, fixture.wand.simulatedCharges);
        assertEquals(0, fixture.wand.committedCharges);
        assertEquals(0, fixture.world.blockMutations);
    }

    @Test
    public void canceledCoordinateCapturesPositionAndHandBeforeAnyMutation() throws Exception {
        Fixture fixture = new Fixture();
        CancelingRightClick listener = new CancelingRightClick();
        ListenerList listenerList = new PlayerInteractEvent.RightClickBlock(fixture.player, EnumHand.OFF_HAND,
                TARGET, EnumFacing.UP, Vec3d.ZERO).getListenerList();
        int busId = eventBusId(MinecraftForge.EVENT_BUS);
        listenerList.register(busId, EventPriority.NORMAL, listener);
        try {
            fixture.queueFromOffhandFocus();
            fixture.world.resetMutationCounters();

            tickBlockSwap(fixture.world);

            assertEquals(TARGET, listener.pos);
            assertEquals(EnumHand.OFF_HAND, listener.hand);
            assertEquals(EnumFacing.UP, listener.face);
            assertEquals(new Vec3d(4.5D, 71.0D, -1.5D), listener.hitVec);
            assertEquals(Blocks.STONE, fixture.world.getBlockState(TARGET).getBlock());
            assertEquals(1, countItem(fixture.player, Item.getItemFromBlock(Blocks.DIRT)));
            assertEquals(0, fixture.wand.simulatedCharges);
            assertEquals(0, fixture.wand.committedCharges);
            assertEquals(0, fixture.world.blockMutations);
            assertEquals(0, fixture.world.spawnedEntities);
            assertEquals(0, fixture.world.worldEvents);
            assertEquals(0, fixture.world.blockUpdates);
        } finally {
            listenerList.unregister(busId, listener);
        }
    }

    private static void tickBlockSwap(World world) throws Exception {
        Method method = ServerTickEventsFML.class.getDeclaredMethod("tickBlockSwap", World.class);
        method.setAccessible(true);
        try {
            method.invoke(new ServerTickEventsFML(), world);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Exception) throw (Exception) e.getCause();
            if (e.getCause() instanceof Error) throw (Error) e.getCause();
            throw e;
        }
    }

    private static int eventBusId(EventBus eventBus) throws Exception {
        Field field = EventBus.class.getDeclaredField("busID");
        field.setAccessible(true);
        return field.getInt(eventBus);
    }

    private static int countItem(EntityPlayer player, net.minecraft.item.Item item) {
        int count = 0;
        for (ItemStack stack : player.inventory.mainInventory) {
            if (!stack.isEmpty() && stack.getItem() == item) count += stack.getCount();
        }
        return count;
    }

    private static final class Fixture {
        private final SwapWorld world = new SwapWorld();
        private final FocusTrade focus = new FocusTrade();
        private final RecordingWand wand = new RecordingWand(this.focus);
        private final ItemStack wandStack = new ItemStack(this.wand);
        private final RecordingPlayer player = new RecordingPlayer(this.world);

        private Fixture() {
            this.wand.expectedStack = this.wandStack;
            this.world.states.put(TARGET, Blocks.STONE.getDefaultState());
            this.player.inventory.currentItem = 0;
            this.player.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.STICK));
            this.player.setHeldItem(EnumHand.OFF_HAND, this.wandStack);
            this.player.inventory.setInventorySlotContents(1, new ItemStack(Blocks.DIRT));
            this.focus.storePickedBlock(this.wandStack, new ItemStack(Blocks.DIRT));
        }

        private void queueFromOffhandFocus() {
            RayTraceResult hit = new RayTraceResult(new Vec3d(4.5D, 71.0D, -1.5D), EnumFacing.UP, TARGET);
            this.focus.onFocusRightClick(this.wandStack, this.world, this.player, hit);
        }
    }

    private static final class RecordingWand extends ItemWandCasting {
        private final ItemFocusBasic focus;
        private final ItemStack focusStack = new ItemStack(Items.STICK);
        private ItemStack expectedStack;
        private ItemStack lastChargedStack;
        private int simulatedCharges;
        private int committedCharges;

        private RecordingWand(ItemFocusBasic focus) {
            this.focus = focus;
        }

        @Override public ItemStack getFocusItem(ItemStack stack) { return this.focusStack; }
        @Override public ItemFocusBasic getFocus(ItemStack stack) { return this.focus; }
        @Override public int getFocusTreasure(ItemStack stack) { return 0; }

        @Override
        public boolean consumeAllVis(ItemStack stack, EntityPlayer player, AspectList cost,
                                     boolean doit, boolean crafting) {
            assertSame(this.expectedStack, stack);
            this.lastChargedStack = stack;
            if (doit) this.committedCharges++;
            else this.simulatedCharges++;
            return true;
        }
    }

    private static final class RecordingPlayer extends EntityPlayer {
        private EnumHand swungHand;

        private RecordingPlayer(World world) {
            super(world, new GameProfile(UUID.randomUUID(), "trade_authority"));
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }

        @Override
        public void swingArm(EnumHand hand) {
            this.swungHand = hand;
        }
    }

    private static final class CancelingRightClick implements IEventListener {
        private BlockPos pos;
        private EnumHand hand;
        private EnumFacing face;
        private Vec3d hitVec;

        @Override
        public void invoke(Event posted) {
            if (!(posted instanceof PlayerInteractEvent.RightClickBlock)) return;
            PlayerInteractEvent.RightClickBlock event = (PlayerInteractEvent.RightClickBlock) posted;
            this.pos = event.getPos();
            this.hand = event.getHand();
            this.face = event.getFace();
            this.hitVec = event.getHitVec();
            try {
                // Unit tests do not run Forge's event transformer, which supplies isCancelable().
                Field canceled = Event.class.getDeclaredField("isCanceled");
                canceled.setAccessible(true);
                canceled.setBoolean(event, true);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static final class SwapWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();
        private int blockMutations;
        private int spawnedEntities;
        private int worldEvents;
        private int blockUpdates;

        private SwapWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "trade_authority"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        private void resetMutationCounters() {
            this.blockMutations = 0;
            this.spawnedEntities = 0;
            this.worldEvents = 0;
            this.blockUpdates = 0;
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            IBlockState state = this.states.get(pos);
            return state == null ? Blocks.STONE.getDefaultState() : state;
        }

        @Override
        public boolean setBlockState(BlockPos pos, IBlockState state, int flags) {
            this.blockMutations++;
            this.states.put(pos.toImmutable(), state);
            return true;
        }

        @Override public TileEntity getTileEntity(BlockPos pos) { return null; }
        @Override public boolean isBlockModifiable(EntityPlayer player, BlockPos pos) { return true; }

        @Override
        public boolean spawnEntity(Entity entityIn) {
            this.spawnedEntities++;
            return true;
        }

        @Override
        public void playEvent(int type, BlockPos pos, int data) {
            this.worldEvents++;
        }

        @Override
        public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
            this.blockUpdates++;
        }

        @Override
        public void playSound(EntityPlayer player, BlockPos pos, SoundEvent sound, SoundCategory category,
                              float volume, float pitch) {
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "trade_authority_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
