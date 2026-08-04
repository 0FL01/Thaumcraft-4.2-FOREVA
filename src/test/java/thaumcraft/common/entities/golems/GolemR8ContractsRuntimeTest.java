package thaumcraft.common.entities.golems;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
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
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.entities.GolemIds;
import thaumcraft.api.entities.IGolemInfo;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.container.SlotGhostFluid;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class GolemR8ContractsRuntimeTest {
    private static final boolean[] HAS_GUI = {
            true, true, true, false, true, true, false, false, true, false, true, false
    };
    private static final boolean[] CAN_SORT = {
            true, true, true, false, false, false, false, false, true, false, true, false
    };
    private static final boolean[] HAS_INVENTORY = {
            true, true, true, false, false, true, false, false, true, false, false, false
    };

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void everyCoreCapabilityMapMatchesTc4() {
        for (int core = 0; core <= 11; core++) {
            assertEquals("hasGUI core " + core, HAS_GUI[core], ItemGolemCore.hasGUI(core));
            assertEquals("canSort core " + core, CAN_SORT[core], ItemGolemCore.canSort(core));
            assertEquals("hasInventory core " + core, HAS_INVENTORY[core], ItemGolemCore.hasInventory(core));
        }
    }

    @Test
    public void inventoryAndLiquidFilterSizesMatchZeroOneAndTwoIgnisUpgrades() {
        TestWorld world = new TestWorld(false);
        TestPlayer player = new TestPlayer(world);
        for (int ignis = 0; ignis <= 2; ignis++) {
            for (int core = 0; core <= 11; core++) {
                EntityGolemBase golem = golemWithUpgrades(world, core, 2, ignis);
                int expected = expectedInventorySize(core, ignis);
                if (expected < 0) {
                    assertNull("core " + core + " with " + ignis + " Ignis", golem.inventory);
                    continue;
                }
                assertEquals("core " + core + " with " + ignis + " Ignis",
                        expected, golem.inventory.getSizeInventory());
                assertEquals(expected, golem.colors.length);

                if (core == 5) {
                    ContainerGolem container = new ContainerGolem(player.inventory, golem);
                    for (int slot = 0; slot < expected; slot++) {
                        assertTrue(container.getSlot(slot) instanceof SlotGhostFluid);
                    }
                }
                if (core == 10) {
                    assertEquals(-1, golem.colors[0]);
                }
            }
        }
    }

    @Test
    public void interactionRejectsAThirdDuplicateUpgrade() {
        TestWorld world = new TestWorld(false);
        TestPlayer player = new TestPlayer(world);
        EntityGolemBase golem = new EntityGolemBase(world, EnumGolemType.THAUMIUM, true);
        Item previousUpgrade = ConfigItems.itemGolemUpgrade;
        ConfigItems.itemGolemUpgrade = new ItemGolemUpgrade();
        try {
            for (int installed = 0; installed < 2; installed++) {
                player.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(ConfigItems.itemGolemUpgrade, 1, 0));
                assertTrue(golem.processInteract(player, EnumHand.MAIN_HAND));
            }
            player.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(ConfigItems.itemGolemUpgrade, 1, 0));
            assertFalse(golem.processInteract(player, EnumHand.MAIN_HAND));

            assertEquals(2, golem.getUpgradeAmount(0));
            assertEquals(-1, golem.getUpgrade(2));
            assertEquals(1, player.getHeldItemMainhand().getCount());
        } finally {
            ConfigItems.itemGolemUpgrade = previousUpgrade;
        }
    }

    @Test
    public void bellPlacerRoundTripPreservesConfigurationAndExistingState() {
        TestWorld world = new TestWorld(false);
        EntityGolemBase source = new EntityGolemBase(world, EnumGolemType.THAUMIUM, false);
        source.setCore((byte) 0);
        source.setUpgrade(0, (byte) 2);
        source.setUpgrade(1, (byte) 4);
        source.setupGolemInventory();
        source.setTogglesValue((byte) 0xA5);
        source.setColors(0, 3);
        source.setColors(11, 15);
        source.inventory.setInventorySlotContents(0, new ItemStack(Items.PAPER, 32));
        ArrayList<Marker> markers = new ArrayList<>();
        markers.add(new Marker(4, 65, -7, 0, (byte) 2, (byte) 3));
        source.setMarkers(markers);

        ItemGolemPlacer placerItem = new ItemGolemPlacer();
        ItemStack placer = new ItemStack(placerItem, 1, EnumGolemType.THAUMIUM.ordinal());
        ItemGolemBell.writeGolemStateToPlacer(placer, source);
        assertTrue(placer.getTagCompound().hasKey("toggles"));
        assertTrue(placer.getTagCompound().hasKey("colors"));

        assertTrue(placerItem.spawnCreature(world, 0.5D, 64.0D, 0.5D, 1, placer, new TestPlayer(world)));
        EntityGolemBase restored = world.lastSpawned;
        assertNotNull(restored);
        assertEquals(source.getTogglesValue(), restored.getTogglesValue());
        assertArrayEquals(source.colors, restored.colors);
        assertArrayEquals(source.upgrades, restored.upgrades);
        assertEquals(32, restored.inventory.getStackInSlot(0).getCount());
        assertEquals(markers, restored.getMarkers());
    }

    @Test
    public void placerNormalizesShortAndLongColorArraysAndKeepsSortingWildcard() {
        TestWorld world = new TestWorld(false);

        EntityGolemBase shortColors = spawnTaggedGolem(world, 0,
                new byte[]{2, -1}, new byte[]{6});
        assertEquals(12, shortColors.colors.length);
        assertEquals(6, shortColors.getColors(0));
        for (int slot = 1; slot < shortColors.colors.length; slot++) {
            assertEquals(-1, shortColors.getColors(slot));
        }

        EntityGolemBase longColors = spawnTaggedGolem(world, 0,
                new byte[]{-1, -1}, new byte[]{0, 1, 2, 3, 4, 5, 6, 7});
        assertArrayEquals(new byte[]{0, 1, 2, 3, 4, 5}, longColors.colors);

        EntityGolemBase sorting = spawnTaggedGolem(world, 10,
                new byte[]{-1, -1}, new byte[]{9});
        assertArrayEquals(new byte[]{-1}, sorting.colors);
        assertEquals(-1, sorting.getColors(0));
    }

    @Test
    public void synchronizedTypeUpgradesAndFluidCoverSpawnSnapshotAndLiveChanges() throws Exception {
        EntityGolemBase server = new EntityGolemBase(new TestWorld(false), EnumGolemType.THAUMIUM, true);
        server.setCore((byte) 5);
        server.setUpgrade(0, (byte) 1);
        server.setUpgrade(1, (byte) 1);
        server.fluidCarried = new FluidStack(FluidRegistry.WATER, 123456789);
        server.updateCarried();

        EntityGolemBase client = new EntityGolemBase(new TestWorld(true));
        copySyncedState(server, client);
        ByteBuf spawnData = Unpooled.buffer();
        server.writeSpawnData(spawnData);
        client.readSpawnData(spawnData);

        IGolemInfo info = client;

        assertSame(EnumGolemType.WOOD, client.golemType);
        assertSame(EnumGolemType.THAUMIUM, client.getGolemType());
        assertEquals(GolemIds.TYPE_THAUMIUM, info.getGolemTypeId());
        assertEquals(GolemIds.CORE_DECANTING, info.getCore());
        assertTrue(info.isAdvancedGolem());
        assertEquals(server.getMaxHealth(), client.getMaxHealth(), 0.0F);
        assertEquals(2, info.getUpgradeAmount(GolemIds.UPGRADE_EARTH));
        assertEquals(64, client.getCarryLimit());
        assertNull(client.fluidCarried);
        assertSame(FluidRegistry.WATER, client.getFluidCarried().getFluid());
        assertEquals(123456789, client.getFluidCarried().amount);
        assertSame(server.fluidCarried, server.getFluidCarried());
        assertTrue(server.getCarriedForDisplay().isEmpty());

        server.setUpgrade(0, (byte) 0);
        server.fluidCarried = new FluidStack(FluidRegistry.LAVA, 70001);
        server.updateCarried();
        copySyncedState(server, client);

        assertEquals(1, info.getUpgradeAmount(GolemIds.UPGRADE_AIR));
        assertEquals(1, info.getUpgradeAmount(GolemIds.UPGRADE_EARTH));
        assertEquals(48, client.getCarryLimit());
        assertSame(FluidRegistry.LAVA, client.getFluidCarried().getFluid());
        assertEquals(70001, client.getFluidCarried().amount);
    }

    @Test
    public void pausedAndFetteredGolemsDisableAi() {
        EntityGolemBase golem = new EntityGolemBase(new TestWorld(false), EnumGolemType.WOOD, false);
        assertFalse(golem.isAIDisabled());

        golem.paused = true;
        assertTrue(golem.isAIDisabled());
        assertEquals(0.0F, golem.getAIMoveSpeed(), 0.0F);

        golem.paused = false;
        golem.inactive = true;
        assertTrue(golem.isAIDisabled());
        assertEquals(0.0F, golem.getAIMoveSpeed(), 0.0F);
    }

    @Test
    public void entityNbtLoadRetainsFiltersAndPublishesFullFluidState() throws Exception {
        TestWorld serverWorld = new TestWorld(false);
        EntityGolemBase source = new EntityGolemBase(serverWorld, EnumGolemType.THAUMIUM, false);
        source.setCore((byte) 5);
        source.setUpgrade(0, (byte) 2);
        source.setupGolemInventory();
        source.setColors(0, 11);
        source.fluidCarried = new FluidStack(FluidRegistry.LAVA, 123456789);
        source.updateCarried();
        NBTTagCompound nbt = new NBTTagCompound();
        source.writeEntityToNBT(nbt);

        EntityGolemBase loaded = new EntityGolemBase(serverWorld);
        loaded.readEntityFromNBT(nbt);
        assertSame(EnumGolemType.THAUMIUM, loaded.getGolemType());
        assertEquals(2, loaded.inventory.getSizeInventory());
        assertEquals(11, loaded.getColors(0));
        assertEquals(-1, loaded.getColors(1));
        assertEquals(2, loaded.getUpgrade(0));
        assertSame(FluidRegistry.LAVA, loaded.fluidCarried.getFluid());
        assertEquals(123456789, loaded.fluidCarried.amount);

        EntityGolemBase client = new EntityGolemBase(new TestWorld(true));
        client.setCore((byte) 5);
        copySyncedState(loaded, client);
        assertSame(FluidRegistry.LAVA, client.getFluidCarried().getFluid());
        assertEquals(123456789, client.getFluidCarried().amount);
    }

    @Test
    public void guardOrderUpgradeRetainsAllFourToggleBits() {
        TestWorld world = new TestWorld(false);
        EntityGolemBase guard = golemWithUpgrades(world, 4, 4, 0);
        guard.setHomePosAndDistance(BlockPos.ORIGIN, 32);
        EntityCow cow = new EntityCow(world);
        cow.setPosition(1.0D, 0.0D, 0.0D);

        assertFalse(guard.isValidTarget(cow));
        guard.setUpgrade(0, (byte) 4);
        assertEquals(1, guard.getUpgradeAmount(4));
        assertTrue(guard.isValidTarget(cow));
        assertTrue(guard.canAttackHostiles());
        assertTrue(guard.canAttackAnimals());
        assertTrue(guard.canAttackPlayers());
        assertTrue(guard.canAttackCreepers());

        for (int toggle = 1; toggle <= 4; toggle++) guard.setToggle(toggle, true);
        assertFalse(guard.canAttackHostiles());
        assertFalse(guard.canAttackAnimals());
        assertFalse(guard.canAttackPlayers());
        assertFalse(guard.canAttackCreepers());
        assertFalse(guard.isValidTarget(cow));
    }

    private static EntityGolemBase golemWithUpgrades(TestWorld world, int core, int upgrade, int count) {
        EntityGolemBase golem = new EntityGolemBase(world, EnumGolemType.THAUMIUM, true);
        for (int slot = 0; slot < count; slot++) golem.setUpgrade(slot, (byte) upgrade);
        golem.setCore((byte) core);
        golem.setupGolemInventory();
        return golem;
    }

    private static int expectedInventorySize(int core, int ignis) {
        if (core == 5) return 1 + ignis;
        if (core == 0 || core == 1 || core == 2 || core == 8) return 6 + ignis * 6;
        if (core == 10) return 1;
        return -1;
    }

    private static EntityGolemBase spawnTaggedGolem(TestWorld world, int core,
                                                     byte[] upgrades, byte[] colors) {
        ItemGolemPlacer item = new ItemGolemPlacer();
        ItemStack stack = new ItemStack(item, 1, EnumGolemType.THAUMIUM.ordinal());
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByte("core", (byte) core);
        tag.setByteArray("upgrades", upgrades);
        tag.setByteArray("colors", colors);
        stack.setTagCompound(tag);
        assertTrue(item.spawnCreature(world, 0.5D, 64.0D, 0.5D, 1, stack, new TestPlayer(world)));
        return world.lastSpawned;
    }

    private static void copySyncedState(EntityGolemBase source, EntityGolemBase target) throws Exception {
        copyDataParameter(source, target, "GOLEM_TYPE");
        copyDataParameter(source, target, "UPGRADES_STR");
        copyDataParameter(source, target, "CARRIED_FLUID");
        copyDataParameter(source, target, "CARRIED_FLUID_AMOUNT");
    }

    @SuppressWarnings("unchecked")
    private static <T> void copyDataParameter(EntityGolemBase source, EntityGolemBase target,
                                              String fieldName) throws Exception {
        Field field = EntityGolemBase.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        DataParameter<T> parameter = (DataParameter<T>) field.get(null);
        target.getDataManager().set(parameter, source.getDataManager().get(parameter));
    }

    private static final class TestPlayer extends EntityPlayer {
        private TestPlayer(World world) {
            super(world, new GameProfile(UUID.nameUUIDFromBytes(
                    "r8_golem_contracts".getBytes(StandardCharsets.UTF_8)), "r8_golem_contracts"));
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
    }

    private static final class TestWorld extends World {
        private EntityGolemBase lastSpawned;

        private TestWorld(boolean remote) {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "r8_golem_contracts"),
                    new WorldProviderSurface(), new Profiler(), remote);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        public boolean spawnEntity(Entity entityIn) {
            if (entityIn instanceof EntityGolemBase) this.lastSpawned = (EntityGolemBase) entityIn;
            return true;
        }

        @Override public Biome getBiome(BlockPos pos) { return Biomes.PLAINS; }
        @Override public IBlockState getBlockState(BlockPos pos) { return Blocks.AIR.getDefaultState(); }
        @Override public TileEntity getTileEntity(BlockPos pos) { return null; }
        @Override public Explosion createExplosion(Entity entityIn, double x, double y, double z,
                                                   float strength, boolean isSmoking) { return null; }
        @Override public void notifyBlockUpdate(BlockPos pos, IBlockState oldState,
                                                IBlockState newState, int flags) { }
        @Override public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) { }
        @Override public void updateComparatorOutputLevel(BlockPos pos, net.minecraft.block.Block blockIn) { }
        @Override public void playSound(EntityPlayer player, BlockPos pos, SoundEvent soundIn,
                                        SoundCategory category, float volume, float pitch) { }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "r8_golem_contracts_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
