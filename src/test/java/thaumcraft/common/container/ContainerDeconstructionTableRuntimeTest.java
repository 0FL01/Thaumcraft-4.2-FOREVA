package thaumcraft.common.container;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.capabilities.Capability;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeCapability;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.tiles.TileDeconstructionTable;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ContainerDeconstructionTableRuntimeTest {
    private AspectList previousStickTags;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Before
    public void rememberTags() {
        this.previousStickTags = ThaumcraftApi.objectTags.get(tagKey());
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.STICK), new AspectList().add(Aspect.AIR, 1));
    }

    @After
    public void restoreTags() {
        if (this.previousStickTags == null) {
            ThaumcraftApi.objectTags.remove(tagKey());
        } else {
            ThaumcraftApi.objectTags.put(tagKey(), this.previousStickTags);
        }
    }

    @Test
    public void claimAwardsExactlyOnceBeforeClearingAndSynchronizingOutput() {
        ContainerWorld world = new ContainerWorld(false);
        TestTable table = world.attach(new TestTable());
        KnowledgePlayer player = new KnowledgePlayer(world, "deconstruction_claim", true);
        player.setPosition(0.5D, 0.5D, 0.5D);
        ContainerDeconstructionTable container = new ContainerDeconstructionTable(player.inventory, table);
        table.aspect = Aspect.AIR;

        assertTrue(container.enchantItem(player, 1));
        assertEquals(1, player.knowledge.getAspectPoolFor(Aspect.AIR));
        assertNull(table.aspect);
        assertEquals(1, table.dirtyCount);
        assertEquals(1, world.updateCount);

        assertFalse(container.enchantItem(player, 1));
        assertEquals(1, player.knowledge.getAspectPoolFor(Aspect.AIR));
        assertEquals(1, table.dirtyCount);
        assertEquals(1, world.updateCount);
    }

    @Test
    public void missingCapabilityOrClientCallKeepsPendingOutput() {
        ContainerWorld serverWorld = new ContainerWorld(false);
        TestTable serverTable = serverWorld.attach(new TestTable());
        KnowledgePlayer missing = new KnowledgePlayer(serverWorld, "deconstruction_missing", false);
        ContainerDeconstructionTable serverContainer = new ContainerDeconstructionTable(missing.inventory, serverTable);
        serverTable.aspect = Aspect.FIRE;

        assertFalse(serverContainer.enchantItem(missing, 1));
        assertSame(Aspect.FIRE, serverTable.aspect);
        assertEquals(0, serverWorld.updateCount);

        ContainerWorld clientWorld = new ContainerWorld(true);
        TestTable clientTable = clientWorld.attach(new TestTable());
        KnowledgePlayer client = new KnowledgePlayer(clientWorld, "deconstruction_client", true);
        ContainerDeconstructionTable clientContainer = new ContainerDeconstructionTable(client.inventory, clientTable);
        clientTable.aspect = Aspect.EARTH;

        assertFalse(clientContainer.enchantItem(client, 1));
        assertSame(Aspect.EARTH, clientTable.aspect);
        assertEquals(0, client.knowledge.getAspectPoolFor(Aspect.EARTH));
    }

    @Test
    public void shiftClickUsesOriginalDirectPlayerInventoryOrder() {
        ContainerWorld world = new ContainerWorld(false);
        TestTable table = world.attach(new TestTable());
        KnowledgePlayer player = new KnowledgePlayer(world, "deconstruction_transfer", true);
        ContainerDeconstructionTable container = new ContainerDeconstructionTable(player.inventory, table);
        table.setInventorySlotContents(0, new ItemStack(Items.STICK, 2));

        ItemStack moved = container.transferStackInSlot(player, 0);

        assertEquals(2, moved.getCount());
        assertTrue(table.getStackInSlot(0).isEmpty());
        assertEquals(2, player.inventory.getStackInSlot(9).getCount());
        assertTrue(player.inventory.getStackInSlot(8).isEmpty());

        player.inventory.setInventorySlotContents(10, new ItemStack(Items.STICK, 3));
        moved = container.transferStackInSlot(player, 2);
        assertEquals(3, moved.getCount());
        assertEquals(3, table.getStackInSlot(0).getCount());
        assertTrue(player.inventory.getStackInSlot(10).isEmpty());
    }

    @Test
    public void progressPropertiesAndInteractionDistanceRemainAuthoritative() {
        ContainerWorld world = new ContainerWorld(false);
        TestTable table = world.attach(new TestTable());
        KnowledgePlayer player = new KnowledgePlayer(world, "deconstruction_progress", true);
        player.setPosition(0.5D, 0.5D, 0.5D);
        table.breaktime = 27;
        ContainerDeconstructionTable container = new ContainerDeconstructionTable(player.inventory, table);
        ProgressListener listener = new ProgressListener();

        container.addListener(listener);
        assertEquals(27, listener.lastProgress);
        table.breaktime = 13;
        container.detectAndSendChanges();
        assertEquals(13, listener.lastProgress);
        assertTrue(container.canInteractWith(player));

        player.setPosition(20.0D, 0.5D, 0.5D);
        assertFalse(container.canInteractWith(player));
        player.setPosition(0.5D, 0.5D, 0.5D);
        world.tile = null;
        assertFalse(container.canInteractWith(player));
    }

    private static List tagKey() {
        return Arrays.asList(Items.STICK, 0);
    }

    private static final class ProgressListener implements IContainerListener {
        private int lastProgress = -1;

        @Override
        public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {
        }

        @Override
        public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
        }

        @Override
        public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {
            if (varToUpdate == 0) this.lastProgress = newValue;
        }

        @Override
        public void sendAllWindowProperties(Container containerIn, IInventory inventory) {
        }
    }

    private static final class TestTable extends TileDeconstructionTable {
        private int dirtyCount;

        @Override
        public void markDirty() {
            ++this.dirtyCount;
        }
    }

    private static final class KnowledgePlayer extends EntityPlayer {
        private final PlayerKnowledgeCapability knowledge = new PlayerKnowledgeCapability();
        private final boolean exposeCapability;

        private KnowledgePlayer(World world, String name, boolean exposeCapability) {
            super(world, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)), name));
            this.exposeCapability = exposeCapability;
        }

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return this.exposeCapability && (capability == null || capability == PlayerKnowledgeProvider.PLAYER_KNOWLEDGE)
                    || super.hasCapability(capability, facing);
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            if (this.exposeCapability && (capability == null || capability == PlayerKnowledgeProvider.PLAYER_KNOWLEDGE)) {
                @SuppressWarnings("unchecked")
                T value = (T) this.knowledge;
                return value;
            }
            if (!this.exposeCapability && (capability == null || capability == PlayerKnowledgeProvider.PLAYER_KNOWLEDGE)) {
                return null;
            }
            return super.getCapability(capability, facing);
        }

        @Override
        public boolean isSpectator() {
            return false;
        }

        @Override
        public boolean isCreative() {
            return this.capabilities.isCreativeMode;
        }
    }

    private static final class ContainerWorld extends World {
        private TileEntity tile;
        private int updateCount;

        private ContainerWorld(boolean remote) {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT), "deconstruction_container"),
                    new WorldProviderSurface(), new Profiler(), remote);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        private TestTable attach(TestTable table) {
            this.tile = table;
            table.setWorld(this);
            table.setPos(BlockPos.ORIGIN);
            return table;
        }

        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return BlockPos.ORIGIN.equals(pos) ? this.tile : null;
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            return Blocks.AIR.getDefaultState();
        }

        @Override
        public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
            ++this.updateCount;
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override
                public Chunk getLoadedChunk(int x, int z) { return null; }

                @Override
                public Chunk provideChunk(int x, int z) { return null; }

                @Override
                public boolean tick() { return false; }

                @Override
                public String makeString() { return "deconstruction_container_dummy"; }

                @Override
                public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
