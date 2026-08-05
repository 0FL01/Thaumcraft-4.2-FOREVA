package thaumcraft.common.tiles;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.container.ContainerFocalManipulator;
import thaumcraft.common.lib.TCSounds;

import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TileFocalManipulatorAuthorityRuntimeTest {
    private static final BlockPos POS = new BlockPos(0, 64, 0);
    private static final FocusUpgradeType TEST_UPGRADE = new FocusUpgradeType(64,
            new ResourceLocation("thaumcraft", "textures/foci/potency.png"),
            "test.focal.name", "test.focal.text", new AspectList().add(Aspect.AIR, 1));
    private static final TestFocus FOCUS = new TestFocus();
    private CommonProxy oldProxy;
    private RecordingProxy proxy;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Before
    public void setUpProxy() {
        this.oldProxy = Thaumcraft.proxy;
        this.proxy = new RecordingProxy();
        Thaumcraft.proxy = this.proxy;
    }

    @After
    public void restoreProxy() {
        Thaumcraft.proxy = this.oldProxy;
    }

    @Test
    public void renderBoundsMatchExactTc4ManipulatorExtent() {
        TestTile tile = new TestTile();
        tile.setPos(POS);

        assertEquals(new AxisAlignedBB(0.0D, 63.0D, 0.0D, 1.0D, 65.0D, 1.0D),
                tile.getRenderBoundingBox());
    }

    @Test
    public void activeClientTickEmitsOneExactTc4CraftingParticleWithoutMutation() {
        long seed = 0xC8B1234L;
        TestWorld world = new TestWorld(true, seed);
        TestTile tile = world.attach(new TestTile());
        setActive(tile);
        AspectList originalAspects = tile.aspects;
        Random oracle = new Random(seed);

        double expectedX = POS.getX() + 0.5D + (oracle.nextFloat() - oracle.nextFloat()) * 0.3F;
        double expectedY = POS.getY() + 1.25D + (oracle.nextFloat() - oracle.nextFloat()) * 0.3F;
        double expectedZ = POS.getZ() + 0.5D + (oracle.nextFloat() - oracle.nextFloat()) * 0.3F;
        float expectedRed = 0.5F + oracle.nextFloat() * 0.4F;
        float expectedGreen = 1.0F - oracle.nextFloat() * 0.4F;
        float expectedBlue = 1.0F - oracle.nextFloat() * 0.4F;
        int expectedAge = 6 + oracle.nextInt(5);
        float expectedScale = 0.7F + oracle.nextFloat() * 0.4F;

        tile.update();

        assertEquals(1, this.proxy.calls);
        assertSame(world, this.proxy.world);
        assertEquals(expectedX, this.proxy.x, 0.0D);
        assertEquals(expectedY, this.proxy.y, 0.0D);
        assertEquals(expectedZ, this.proxy.z, 0.0D);
        assertTrue(this.proxy.x >= POS.getX() + 0.2D && this.proxy.x <= POS.getX() + 0.8D);
        assertTrue(this.proxy.y >= POS.getY() + 0.95D && this.proxy.y <= POS.getY() + 1.55D);
        assertTrue(this.proxy.z >= POS.getZ() + 0.2D && this.proxy.z <= POS.getZ() + 0.8D);
        assertEquals(0.0D, this.proxy.mx, 0.0D);
        assertEquals(0.0D, this.proxy.my, 0.0D);
        assertEquals(0.0D, this.proxy.mz, 0.0D);
        assertEquals(expectedRed, this.proxy.red, 0.0F);
        assertEquals(expectedGreen, this.proxy.green, 0.0F);
        assertEquals(expectedBlue, this.proxy.blue, 0.0F);
        assertTrue(this.proxy.red >= 0.5F && this.proxy.red <= 0.9F);
        assertTrue(this.proxy.green >= 0.6F && this.proxy.green <= 1.0F);
        assertTrue(this.proxy.blue >= 0.6F && this.proxy.blue <= 1.0F);
        assertEquals(0.8F, this.proxy.alpha, 0.0F);
        assertFalse(this.proxy.loop);
        assertEquals(112, this.proxy.start);
        assertEquals(9, this.proxy.num);
        assertEquals(1, this.proxy.inc);
        assertEquals(expectedAge, this.proxy.age);
        assertEquals(0, this.proxy.delay);
        assertEquals(expectedScale, this.proxy.scale, 0.0F);
        assertTrue(this.proxy.age >= 6 && this.proxy.age <= 10);
        assertTrue(this.proxy.scale >= 0.7F && this.proxy.scale <= 1.1F);
        assertSame(originalAspects, tile.aspects);
        assertEquals(100, tile.size);
        assertEquals(1, tile.rank);
        assertEquals(TEST_UPGRADE.id, tile.upgrade);
    }

    @Test
    public void inactiveClientAndServerTicksEmitNoCraftingParticle() {
        for (int size : new int[]{0, -1}) {
            TestWorld clientWorld = new TestWorld(true, 1L);
            TestTile clientTile = clientWorld.attach(new TestTile());
            clientTile.size = size;
            clientTile.update();
        }
        assertEquals(0, this.proxy.calls);

        TestWorld serverWorld = new TestWorld(false, 1L);
        TestTile serverTile = serverWorld.attach(new TestTile());
        setActive(serverTile);
        serverTile.update();
        assertEquals(0, this.proxy.calls);
    }

    @Test
    public void everyRankUsesExactExperienceThresholdAndDoublingVisCost() {
        for (int rank = 1; rank <= 5; ++rank) {
            int required = rank * TileFocalManipulator.XP_MULT;
            int expectedVis = TileFocalManipulator.VIS_MULT << (rank - 1);

            TestWorld survivalWorld = new TestWorld();
            TestTile survivalTile = survivalWorld.attach(new TestTile());
            survivalTile.setInventorySlotContents(0, focusAtRank(rank));
            TestPlayer survival = new TestPlayer(survivalWorld, false);
            survival.experienceLevel = required - 1;
            assertFalse(survivalTile.startCraft(TEST_UPGRADE.id, survival));
            assertEquals(0, survivalTile.size);
            assertEquals(required - 1, survival.experienceLevel);

            survival.experienceLevel = required;
            assertTrue(survivalTile.startCraft(TEST_UPGRADE.id, survival));
            assertEquals(rank, survivalTile.rank);
            assertEquals(expectedVis, survivalTile.aspects.getAmount(Aspect.AIR));
            assertEquals(expectedVis, survivalTile.size);
            assertEquals(0, survival.experienceLevel);

            TestWorld creativeWorld = new TestWorld();
            TestTile creativeTile = creativeWorld.attach(new TestTile());
            creativeTile.setInventorySlotContents(0, focusAtRank(rank));
            TestPlayer creative = new TestPlayer(creativeWorld, true);
            creative.experienceLevel = required - 1;
            assertFalse(creativeTile.startCraft(TEST_UPGRADE.id, creative));
            assertEquals(required - 1, creative.experienceLevel);

            creative.experienceLevel = required;
            assertTrue(creativeTile.startCraft(TEST_UPGRADE.id, creative));
            assertEquals(expectedVis, creativeTile.aspects.getAmount(Aspect.AIR));
            assertEquals(required, creative.experienceLevel);
        }
    }

    @Test
    public void sidedInventoryExposesOneFocusSlotOnEveryFace() {
        TestTile tile = new TestTile();
        ItemStack focus = new ItemStack(FOCUS);

        for (EnumFacing face : EnumFacing.values()) {
            assertEquals(1, tile.getSlotsForFace(face).length);
            assertEquals(0, tile.getSlotsForFace(face)[0]);
            assertTrue(tile.canInsertItem(0, focus, face));
            assertFalse(tile.canInsertItem(0, new ItemStack(Items.STICK), face));
            assertTrue(tile.canExtractItem(0, focus, face));
        }

        tile.setInventorySlotContents(0, focus);
        for (EnumFacing face : EnumFacing.values()) {
            assertFalse(tile.canInsertItem(0, new ItemStack(FOCUS), face));
            assertTrue(tile.canExtractItem(0, focus, face));
        }
    }

    @Test
    public void activeCraftRejectsInsertionAndReplacementButKeepsEveryExtractionRoute() {
        TestWorld world = new TestWorld();
        TestTile tile = world.attach(new TestTile());
        ItemStack original = namedFocus("original");
        ItemStack replacement = namedFocus("replacement");
        tile.setInventorySlotContents(0, original);
        setActive(tile);
        TestPlayer player = new TestPlayer(world, false);
        ContainerFocalManipulator container = new ContainerFocalManipulator(player.inventory, tile);

        tile.setInventorySlotContents(0, replacement);
        assertSame(original, tile.getStackInSlot(0));
        assertFalse(tile.canInsertItem(0, replacement, EnumFacing.UP));
        assertFalse(container.inventorySlots.get(0).isItemValid(replacement));

        player.inventory.setInventorySlotContents(9, replacement);
        assertTrue(container.transferStackInSlot(player, 1).isEmpty());
        assertSame(original, tile.getStackInSlot(0));
        assertSame(replacement, player.inventory.getStackInSlot(9));

        player.inventory.setItemStack(replacement);
        container.slotClick(0, 0, ClickType.PICKUP, player);
        assertSame(original, tile.getStackInSlot(0));
        assertSame(replacement, player.inventory.getItemStack());

        player.inventory.setItemStack(ItemStack.EMPTY);
        container.slotClick(0, 0, ClickType.PICKUP, player);
        assertTrue(tile.getStackInSlot(0).isEmpty());
        assertSame(original, player.inventory.getItemStack());
        tick(tile, 5);
        assertSame(TCSounds.CRAFTFAIL, world.lastSound);
        assertEquals(0, tile.size);

        TestTile shifted = world.attach(new TestTile());
        ItemStack shiftedFocus = namedFocus("shifted");
        shifted.setInventorySlotContents(0, shiftedFocus);
        setActive(shifted);
        ContainerFocalManipulator shiftedContainer = new ContainerFocalManipulator(player.inventory, shifted);
        ItemStack shiftedResult = shiftedContainer.transferStackInSlot(player, 0);
        assertFalse(shiftedResult.isEmpty());
        assertEquals("shifted", shiftedResult.getDisplayName());
        assertTrue(shifted.getStackInSlot(0).isEmpty());

        TestTile automated = world.attach(new TestTile());
        ItemStack automatedFocus = namedFocus("automated");
        automated.setInventorySlotContents(0, automatedFocus);
        setActive(automated);
        assertTrue(automated.canExtractItem(0, automatedFocus, EnumFacing.DOWN));
        assertSame(automatedFocus, automated.decrStackSize(0, 1));
        assertTrue(automated.getStackInSlot(0).isEmpty());

        TestTile emptied = world.attach(new TestTile());
        emptied.setInventorySlotContents(0, namedFocus("empty"));
        setActive(emptied);
        emptied.setInventorySlotContents(0, ItemStack.EMPTY);
        assertTrue(emptied.isEmpty());

        TestTile cleared = world.attach(new TestTile());
        cleared.setInventorySlotContents(0, namedFocus("clear"));
        setActive(cleared);
        cleared.clear();
        assertTrue(cleared.isEmpty());

        TestTile removed = world.attach(new TestTile());
        ItemStack removedFocus = namedFocus("removed");
        removed.setInventorySlotContents(0, removedFocus);
        setActive(removed);
        assertSame(removedFocus, removed.removeStackFromSlot(0));
        assertTrue(removed.isEmpty());
    }

    @Test
    public void activeNbtRoundTripKeepsInventoryAndCraftState() {
        TestTile source = new TestTile();
        source.setInventorySlotContents(0, new ItemStack(Items.BLAZE_ROD));
        source.aspects = new AspectList().add(Aspect.AIR, 321).add(Aspect.FIRE, 123);
        source.size = 444;
        source.rank = 4;
        source.upgrade = TEST_UPGRADE.id;

        NBTTagCompound saved = new NBTTagCompound();
        source.writeCustomNBT(saved);
        assertEquals(1, saved.getTagList("Inventory", 10).tagCount());
        assertTrue(saved.hasKey("size"));
        assertTrue(saved.hasKey("rank"));
        assertTrue(saved.hasKey("upgrade"));

        TestTile restored = new TestTile();
        restored.readCustomNBT(saved);
        assertSame(Items.BLAZE_ROD, restored.getStackInSlot(0).getItem());
        assertEquals(321, restored.aspects.getAmount(Aspect.AIR));
        assertEquals(123, restored.aspects.getAmount(Aspect.FIRE));
        assertEquals(444, restored.size);
        assertEquals(4, restored.rank);
        assertEquals(TEST_UPGRADE.id, restored.upgrade);
    }

    @Test
    public void completionAppliesUpgradeAndExtractionCausesFailure() {
        TestWorld completionWorld = new TestWorld();
        TestTile completion = completionWorld.attach(new TestTile());
        ItemStack completedFocus = new ItemStack(FOCUS);
        completion.setInventorySlotContents(0, completedFocus);
        completion.aspects = new AspectList();
        completion.size = 200;
        completion.rank = 1;
        completion.upgrade = TEST_UPGRADE.id;

        tick(completion, 5);

        assertEquals(TEST_UPGRADE.id, FOCUS.getAppliedUpgrades(completedFocus)[0]);
        assertSame(TCSounds.WAND, completionWorld.lastSound);
        assertEquals(0, completion.size);

        TestWorld failureWorld = new TestWorld();
        TestTile failure = failureWorld.attach(new TestTile());
        ItemStack failedFocus = new ItemStack(FOCUS);
        failure.setInventorySlotContents(0, failedFocus);
        setActive(failure);
        assertSame(failedFocus, failure.removeStackFromSlot(0));

        tick(failure, 5);

        assertEquals(-1, FOCUS.getAppliedUpgrades(failedFocus)[0]);
        assertSame(TCSounds.CRAFTFAIL, failureWorld.lastSound);
        assertEquals(0, failure.size);
    }

    private static ItemStack focusAtRank(int rank) {
        ItemStack stack = new ItemStack(FOCUS);
        for (int appliedRank = 1; appliedRank < rank; ++appliedRank) {
            assertTrue(FOCUS.applyUpgrade(stack, TEST_UPGRADE, appliedRank));
        }
        return stack;
    }

    private static ItemStack namedFocus(String name) {
        ItemStack stack = new ItemStack(FOCUS);
        stack.setStackDisplayName(name);
        return stack;
    }

    private static void setActive(TileFocalManipulator tile) {
        tile.aspects = new AspectList().add(Aspect.AIR, 100);
        tile.size = 100;
        tile.rank = 1;
        tile.upgrade = TEST_UPGRADE.id;
    }

    private static void tick(TileFocalManipulator tile, int count) {
        for (int i = 0; i < count; ++i) tile.update();
    }

    private static final class TestFocus extends ItemFocusBasic {
        @Override
        public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack focusstack, int rank) {
            return rank >= 1 && rank <= 5 ? new FocusUpgradeType[]{TEST_UPGRADE} : null;
        }
    }

    private static final class TestPlayer extends EntityPlayer {
        private TestPlayer(World world, boolean creative) {
            super(world, new GameProfile(UUID.randomUUID(), "focal_authority"));
            this.capabilities.isCreativeMode = creative;
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return this.capabilities.isCreativeMode; }
    }

    private static final class TestTile extends TileFocalManipulator {
        @Override public void markDirty() { }
    }

    private static final class TestWorld extends World {
        private TileEntity tile;
        private SoundEvent lastSound;

        private TestWorld() {
            this(false, 0L);
        }

        private TestWorld(boolean remote, long seed) {
            super(null, new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                    "focal_authority"), new WorldProviderSurface(), new Profiler(), remote);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
            this.rand.setSeed(seed);
        }

        private TestTile attach(TestTile tile) {
            this.tile = tile;
            tile.setWorld(this);
            tile.setPos(POS);
            return tile;
        }

        @Override public TileEntity getTileEntity(BlockPos pos) { return this.tile; }
        @Override public IBlockState getBlockState(BlockPos pos) { return Blocks.AIR.getDefaultState(); }
        @Override public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) { }

        @Override
        public void playSound(EntityPlayer player, BlockPos pos, SoundEvent sound, SoundCategory category,
                              float volume, float pitch) {
            this.lastSound = sound;
        }

        @Override protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) { return true; }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "focal_authority_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }
    }

    private static final class RecordingProxy extends CommonProxy {
        private int calls;
        private World world;
        private double x;
        private double y;
        private double z;
        private double mx;
        private double my;
        private double mz;
        private float red;
        private float green;
        private float blue;
        private float alpha;
        private boolean loop;
        private int start;
        private int num;
        private int inc;
        private int age;
        private int delay;
        private float scale;

        @Override
        public void drawGenericParticles(World world, double x, double y, double z,
                                         double mx, double my, double mz,
                                         float red, float green, float blue, float alpha,
                                         boolean loop, int start, int num, int inc, int age, int delay, float scale) {
            ++this.calls;
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.mx = mx;
            this.my = my;
            this.mz = mz;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
            this.loop = loop;
            this.start = start;
            this.num = num;
            this.inc = inc;
            this.age = age;
            this.delay = delay;
            this.scale = scale;
        }
    }
}
