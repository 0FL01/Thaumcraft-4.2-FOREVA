package thaumcraft.common.entities.golems;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.container.SlotGhost;
import thaumcraft.common.container.SlotGhostFluid;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.IdentityHashMap;
import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GolemFilterContractsRuntimeTest {
    @BeforeClass
    public static void bootstrapMinecraftStatics() throws Exception {
        Bootstrap.register();
        initializeFluidItemCapability();
    }

    @Test
    public void fillCoreGhostClicksConfigurePreciseCountsThrough256() {
        TestWorld world = new TestWorld(false);
        TestPlayer player = new TestPlayer(world);
        EntityGolemBase golem = golemForCore(world, 0);
        ContainerGolem container = new ContainerGolem(player.inventory, golem);
        Slot slot = container.getSlot(0);

        assertTrue(slot instanceof SlotGhost);
        assertFalse(slot instanceof SlotGhostFluid);
        assertEquals(256, slot.getSlotStackLimit());

        player.inventory.setItemStack(new ItemStack(Items.PAPER, 32));
        container.slotClick(0, 0, ClickType.PICKUP, player);
        assertEquals(32, slot.getStack().getCount());
        assertEquals(32, player.inventory.getItemStack().getCount());
        assertEquals(32, golem.inventory.getAmountNeededSmart(new ItemStack(Items.PAPER), false));

        player.inventory.setItemStack(ItemStack.EMPTY);
        container.slotClick(0, 1, ClickType.PICKUP, player);
        assertEquals(33, slot.getStack().getCount());

        slot.putStack(new ItemStack(Items.PAPER, 250));
        container.slotClick(0, 1, ClickType.QUICK_MOVE, player);
        assertEquals(256, slot.getStack().getCount());
        container.slotClick(0, 0, ClickType.QUICK_MOVE, player);
        assertTrue(slot.getStack().isEmpty());
    }

    @Test
    public void emptyGatherAndUseCoresRemainUnitFilters() {
        TestWorld world = new TestWorld(false);
        TestPlayer player = new TestPlayer(world);

        for (int core : new int[]{1, 2, 8}) {
            ContainerGolem container = containerForCore(world, player, core);
            Slot slot = container.getSlot(0);
            assertTrue(slot instanceof SlotGhost);
            assertFalse(slot instanceof SlotGhostFluid);
            assertEquals(1, slot.getSlotStackLimit());

            player.inventory.setItemStack(new ItemStack(Items.PAPER, 64));
            container.slotClick(0, 0, ClickType.PICKUP, player);
            assertEquals(1, slot.getStack().getCount());
            player.inventory.setItemStack(ItemStack.EMPTY);
        }
    }

    @Test
    public void liquidCoreAcceptsOnlyFluidContainers() {
        TestWorld world = new TestWorld(false);
        TestPlayer player = new TestPlayer(world);
        ContainerGolem container = containerForCore(world, player, 5);
        Slot slot = container.getSlot(0);

        assertTrue(slot instanceof SlotGhostFluid);
        assertFalse(slot.isItemValid(new ItemStack(Items.PAPER)));
        assertTrue(slot.isItemValid(new ItemStack(Items.BUCKET)));
        assertTrue(slot.isItemValid(new ItemStack(Items.WATER_BUCKET)));

        player.inventory.setItemStack(new ItemStack(Items.PAPER));
        container.slotClick(0, 0, ClickType.PICKUP, player);
        assertTrue(slot.getStack().isEmpty());

        player.inventory.setItemStack(new ItemStack(Items.BUCKET, 8));
        container.slotClick(0, 0, ClickType.PICKUP, player);
        assertEquals(Items.BUCKET, slot.getStack().getItem());
        assertEquals(1, slot.getStack().getCount());
    }

    @Test
    public void nbtLoadPublishesRestoredFilterColors() {
        TestWorld world = new TestWorld(false);
        EntityGolemBase source = golemForCore(world, 0);
        source.setColors(0, 4);
        source.setColors(1, -1);
        source.setUpgrade(0, (byte) 2);
        NBTTagCompound nbt = new NBTTagCompound();
        source.writeEntityToNBT(nbt);

        EntityGolemBase loaded = new EntityGolemBase(world);
        loaded.readEntityFromNBT(nbt);

        assertEquals(4, loaded.getColors(0));
        assertEquals(-1, loaded.getColors(1));
        assertEquals(2, loaded.getUpgrade(0));
    }

    @Test
    public void preciseGhostCountsAboveVanillaByteRangeRoundTrip() {
        thaumcraft.common.entities.InventoryMob source = new thaumcraft.common.entities.InventoryMob(1);
        source.setInventorySlotContents(0, new ItemStack(Items.PAPER, 256));
        NBTTagList serialized = source.writeToNBT(new NBTTagList());

        thaumcraft.common.entities.InventoryMob loaded = new thaumcraft.common.entities.InventoryMob(1);
        loaded.readFromNBT(serialized);

        assertEquals(256, loaded.getStackInSlot(0).getCount());
        assertEquals(256, loaded.getAmountNeededSmart(new ItemStack(Items.PAPER), false));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void liveClientUpgradeDataUpdatesTheBackingArray() throws Exception {
        EntityGolemBase golem = new EntityGolemBase(new TestWorld(true), EnumGolemType.THAUMIUM, false);
        Field field = EntityGolemBase.class.getDeclaredField("UPGRADES_STR");
        field.setAccessible(true);
        DataParameter<String> upgradesParameter = (DataParameter<String>) field.get(null);

        golem.getDataManager().set(upgradesParameter, "25f1");

        assertArrayEquals(new byte[]{2, 5, -1, 1}, golem.upgrades);
        assertEquals(1, golem.getUpgradeAmount(5));
        assertEquals(-1, golem.getUpgrade(2));
    }

    private static ContainerGolem containerForCore(TestWorld world, TestPlayer player, int core) {
        return new ContainerGolem(player.inventory, golemForCore(world, core));
    }

    @SuppressWarnings("unchecked")
    private static void initializeFluidItemCapability() throws Exception {
        if (CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY != null) return;
        Field providersField = CapabilityManager.class.getDeclaredField("providers");
        providersField.setAccessible(true);
        IdentityHashMap<String, Capability<?>> providers =
                (IdentityHashMap<String, Capability<?>>) providersField.get(CapabilityManager.INSTANCE);
        String capabilityName = IFluidHandlerItem.class.getName().intern();
        Capability<?> capability = providers.get(capabilityName);
        if (capability == null) {
            CapabilityFluidHandler.register();
            capability = providers.get(capabilityName);
        }
        CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY =
                (Capability<IFluidHandlerItem>) capability;
    }

    private static EntityGolemBase golemForCore(TestWorld world, int core) {
        EntityGolemBase golem = new EntityGolemBase(world, EnumGolemType.WOOD, false);
        golem.setCore((byte) core);
        golem.setupGolemInventory();
        return golem;
    }

    private static final class TestPlayer extends EntityPlayer {
        private TestPlayer(World world) {
            super(world, new GameProfile(UUID.nameUUIDFromBytes("r2_golem_filter".getBytes(StandardCharsets.UTF_8)),
                    "r2_golem_filter"));
        }

        @Override
        public boolean isSpectator() {
            return false;
        }

        @Override
        public boolean isCreative() {
            return false;
        }
    }

    private static final class TestWorld extends World {
        private TestWorld(boolean remote) {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "r2_golem_filter"),
                    new WorldProviderSurface(), new Profiler(), remote);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        public Biome getBiome(BlockPos pos) {
            return Biomes.PLAINS;
        }

        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return null;
        }

        @Override
        public Explosion createExplosion(Entity entityIn, double x, double y, double z,
                                         float strength, boolean isSmoking) {
            return null;
        }

        @Override
        public void notifyBlockUpdate(BlockPos pos, net.minecraft.block.state.IBlockState oldState,
                                      net.minecraft.block.state.IBlockState newState, int flags) {
        }

        @Override
        public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) {
        }

        @Override
        public void updateComparatorOutputLevel(BlockPos pos, net.minecraft.block.Block blockIn) {
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "r2_golem_filter_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
