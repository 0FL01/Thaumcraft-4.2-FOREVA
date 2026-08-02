package thaumcraft.common.entities.ai.inventory;

import net.minecraft.entity.Entity;
import net.minecraft.init.Biomes;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
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
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.EnumGolemType;
import thaumcraft.common.entities.golems.GolemHelper;
import thaumcraft.common.entities.golems.ItemGolemCore;
import thaumcraft.common.entities.golems.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class SortingHeartRuntimeTest {
    private static final BlockPos HOME = new BlockPos(0, 64, 0);
    private static final BlockPos SOURCE = HOME.down();
    private static int nextEntityId = 3000;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void sortingCoreKeepsHiddenWildcardSentinelInventory() {
        EntityGolemBase golem = sortingGolem(new TestWorld());

        assertFalse(ItemGolemCore.hasInventory(10));
        assertEquals(1, golem.inventory.getSizeInventory());
        assertTrue(golem.inventory.getStackInSlot(0).isEmpty());
        assertEquals(-1, golem.colors[0]);
        assertEquals(1, golem.getColorsMatching(new ItemStack(Items.PAPER)).size());
        assertEquals(Byte.valueOf((byte) -1), golem.getColorsMatching(new ItemStack(Items.PAPER)).get(0));
    }

    @Test
    public void homeTakeFindsSourceItemAfterEmptySlotOnlyWithExternalDestination() {
        TestWorld world = new TestWorld();
        TestInventory source = world.addTile(SOURCE, new TestInventory(3));
        source.setInventorySlotContents(1, new ItemStack(Items.PAPER, 5));
        BlockPos targetPos = new BlockPos(4, 64, 0);
        TestInventory target = world.addTile(targetPos, new TestInventory(3));
        target.setInventorySlotContents(0, new ItemStack(Items.PAPER));
        EntityGolemBase golem = sortingGolem(world);
        setMarkers(golem, marker(targetPos, EnumFacing.NORTH, 9));

        AIHomeTakeSorting take = new AIHomeTakeSorting(golem);
        int expectedTaken = Math.min(5, golem.getCarryLimit());
        assertTrue(take.shouldExecute());
        take.startExecuting();

        assertEquals(expectedTaken, golem.getCarried().getCount());
        assertEquals(5 - expectedTaken, source.getStackInSlot(1).getCount());
        assertEquals(1, target.getStackInSlot(0).getCount());
    }

    @Test
    public void gotoRequiresCarriedItemAndSkipsNearerEmptyWildcardMarkedTarget() {
        TestWorld world = new TestWorld();
        BlockPos nearPos = new BlockPos(2, 64, 0);
        BlockPos farPos = new BlockPos(6, 64, 0);
        TestInventory near = world.addTile(nearPos, new TestInventory(3));
        TestInventory far = world.addTile(farPos, new TestInventory(3));
        far.setInventorySlotContents(0, new ItemStack(Items.PAPER));
        EntityGolemBase golem = sortingGolem(world);
        setMarkers(golem,
                marker(nearPos, EnumFacing.NORTH, 2),
                marker(farPos, EnumFacing.SOUTH, 14));

        AISortingGoto goTo = new AISortingGoto(golem);
        assertFalse(goTo.shouldExecute());

        golem.setCarried(new ItemStack(Items.PAPER, 3));
        assertSame(far, GolemHelper.findSortingDestination(
                golem, golem.getCarried(), golem.getRange() * golem.getRange()));
        assertTrue(goTo.shouldExecute());
        assertTrue(near.isEmpty());
    }

    @Test
    public void fullSeededTargetAndMarkedSourceAreRejected() {
        TestWorld world = new TestWorld();
        TestInventory source = world.addTile(SOURCE, new TestInventory(2));
        source.setInventorySlotContents(0, new ItemStack(Items.PAPER));
        BlockPos fullPos = new BlockPos(3, 64, 0);
        TestInventory full = world.addTile(fullPos, new TestInventory(2));
        full.setInventorySlotContents(0, new ItemStack(Items.PAPER, 64));
        full.setInventorySlotContents(1, new ItemStack(Items.APPLE, 64));
        EntityGolemBase golem = sortingGolem(world);
        golem.setCarried(new ItemStack(Items.PAPER));

        setMarkers(golem, marker(fullPos, EnumFacing.UP, 0));
        assertNull(GolemHelper.findSortingDestination(golem, golem.getCarried(), 256.0));

        setMarkers(golem, marker(SOURCE, EnumFacing.UP, 0));
        assertNull(GolemHelper.findSortingDestination(golem, golem.getCarried(), 256.0));
        golem.setCarried(ItemStack.EMPTY);
        assertFalse(new AIHomeTakeSorting(golem).shouldExecute());
        assertEquals(1, source.getStackInSlot(0).getCount());
    }

    @Test
    public void sortingDestinationHonorsMarkerSideAndPerditioComparison() {
        TestWorld world = new TestWorld();
        BlockPos sidedPos = new BlockPos(3, 64, 0);
        TestSidedInventory sided = world.addTile(sidedPos, new TestSidedInventory(EnumFacing.EAST));
        sided.setInventorySlotContents(0, new ItemStack(Items.IRON_PICKAXE, 1, 9));
        EntityGolemBase golem = sortingGolem(world);
        golem.setCarried(new ItemStack(Items.IRON_PICKAXE, 1, 3));

        setMarkers(golem, marker(sidedPos, EnumFacing.WEST, 5));
        assertNull(GolemHelper.findSortingDestination(golem, golem.getCarried(), 256.0));

        setMarkers(golem, marker(sidedPos, EnumFacing.EAST, 5));
        assertNull(GolemHelper.findSortingDestination(golem, golem.getCarried(), 256.0));
        golem.setToggle(6, true);
        assertSame(sided, GolemHelper.findSortingDestination(golem, golem.getCarried(), 256.0));
    }

    @Test
    public void placeInsertsOnlyWhileSeedAndRoomStillExist() {
        TestWorld world = new TestWorld();
        BlockPos targetPos = new BlockPos(1, 64, 0);
        TestInventory target = world.addTile(targetPos, new TestInventory(1));
        target.setInventorySlotContents(0, new ItemStack(Items.PAPER, 63));
        EntityGolemBase golem = sortingGolem(world);
        golem.setCarried(new ItemStack(Items.PAPER, 2));
        setMarkers(golem, marker(targetPos, EnumFacing.DOWN, 11));

        AISortingPlace place = new AISortingPlace(golem);
        assertTrue(place.shouldExecute());
        place.startExecuting();
        assertEquals(64, target.getStackInSlot(0).getCount());
        assertEquals(1, golem.getCarried().getCount());

        target.setInventorySlotContents(0, new ItemStack(Items.PAPER, 63));
        golem.setCarried(new ItemStack(Items.PAPER));
        assertTrue(place.shouldExecute());
        target.setInventorySlotContents(0, new ItemStack(Items.PAPER, 64));
        place.startExecuting();
        assertEquals(1, golem.getCarried().getCount());
        assertFalse(place.shouldContinueExecuting());
    }

    @Test
    public void placeUsesPartnerHalfOfSeededDoubleChest() {
        TestWorld world = new TestWorld();
        BlockPos leftPos = new BlockPos(1, 64, 0);
        BlockPos rightPos = leftPos.east();
        TileEntityChest left = world.addTile(leftPos, new TileEntityChest());
        TileEntityChest right = world.addTile(rightPos, new TileEntityChest());
        left.adjacentChestXPos = right;
        right.adjacentChestXNeg = left;
        left.setInventorySlotContents(0, new ItemStack(Items.PAPER, 64));
        for (int slot = 1; slot < left.getSizeInventory(); slot++) {
            left.setInventorySlotContents(slot, new ItemStack(Items.APPLE, 64));
        }
        EntityGolemBase golem = sortingGolem(world);
        golem.setCarried(new ItemStack(Items.PAPER, 3));
        setMarkers(golem, marker(leftPos, EnumFacing.UP, 4));

        AISortingPlace place = new AISortingPlace(golem);
        assertTrue(place.shouldExecute());
        boolean oldChestInteract = Config.golemChestInteract;
        Config.golemChestInteract = false;
        try {
            place.startExecuting();
        } finally {
            Config.golemChestInteract = oldChestInteract;
        }

        assertEquals(64, left.getStackInSlot(0).getCount());
        assertEquals(3, right.getStackInSlot(0).getCount());
        assertTrue(golem.getCarried().isEmpty());
    }

    private static EntityGolemBase sortingGolem(TestWorld world) {
        EntityGolemBase golem = new EntityGolemBase(world, EnumGolemType.WOOD, false);
        golem.setEntityId(nextEntityId++);
        golem.setCore((byte) 10);
        golem.homeFacing = EnumFacing.UP.ordinal();
        golem.setHomePosAndDistance(HOME, 32);
        golem.setPosition(HOME.getX() + 0.5, HOME.getY() + 0.5, HOME.getZ() + 0.5);
        golem.setupGolem();
        golem.setupGolemInventory();
        return golem;
    }

    private static Marker marker(BlockPos pos, EnumFacing side, int color) {
        return new Marker(pos.getX(), pos.getY(), pos.getZ(), (byte) 0,
                (byte) side.ordinal(), (byte) color);
    }

    private static void setMarkers(EntityGolemBase golem, Marker... markers) {
        ArrayList<Marker> list = new ArrayList<>();
        for (Marker marker : markers) list.add(marker);
        golem.setMarkers(list);
    }

    private static class TestInventory extends TileEntity implements IInventory {
        private final ItemStack[] stacks;

        private TestInventory(int size) {
            this.stacks = new ItemStack[size];
            clear();
        }

        @Override
        public int getSizeInventory() {
            return stacks.length;
        }

        @Override
        public boolean isEmpty() {
            for (ItemStack stack : stacks) {
                if (!stack.isEmpty()) return false;
            }
            return true;
        }

        @Override
        public ItemStack getStackInSlot(int index) {
            return stacks[index];
        }

        @Override
        public ItemStack decrStackSize(int index, int count) {
            ItemStack stack = stacks[index];
            if (stack.isEmpty()) return ItemStack.EMPTY;
            if (stack.getCount() <= count) {
                stacks[index] = ItemStack.EMPTY;
                return stack;
            }
            return stack.splitStack(count);
        }

        @Override
        public ItemStack removeStackFromSlot(int index) {
            ItemStack stack = stacks[index];
            stacks[index] = ItemStack.EMPTY;
            return stack;
        }

        @Override
        public void setInventorySlotContents(int index, ItemStack stack) {
            stacks[index] = stack == null ? ItemStack.EMPTY : stack;
        }

        @Override public int getInventoryStackLimit() { return 64; }
        @Override public void markDirty() {}
        @Override public boolean isUsableByPlayer(net.minecraft.entity.player.EntityPlayer player) { return true; }
        @Override public void openInventory(net.minecraft.entity.player.EntityPlayer player) {}
        @Override public void closeInventory(net.minecraft.entity.player.EntityPlayer player) {}
        @Override public boolean isItemValidForSlot(int index, ItemStack stack) { return true; }
        @Override public int getField(int id) { return 0; }
        @Override public void setField(int id, int value) {}
        @Override public int getFieldCount() { return 0; }

        @Override
        public void clear() {
            for (int i = 0; i < stacks.length; i++) stacks[i] = ItemStack.EMPTY;
        }

        @Override public String getName() { return "sorting_test"; }
        @Override public boolean hasCustomName() { return false; }
        @Override public ITextComponent getDisplayName() { return new TextComponentString(getName()); }
    }

    private static final class TestSidedInventory extends TestInventory implements ISidedInventory {
        private final EnumFacing allowedFace;

        private TestSidedInventory(EnumFacing allowedFace) {
            super(2);
            this.allowedFace = allowedFace;
        }

        @Override
        public int[] getSlotsForFace(EnumFacing side) {
            return new int[]{0, 1};
        }

        @Override
        public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
            return direction == allowedFace;
        }

        @Override
        public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
            return direction == allowedFace;
        }
    }

    private static final class TestWorld extends World {
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();

        private TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "sorting_heart_test"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        private <T extends TileEntity> T addTile(BlockPos pos, T tile) {
            tile.setWorld(this);
            tile.setPos(pos);
            tiles.put(pos, tile);
            return tile;
        }

        @Override
        public Biome getBiome(BlockPos pos) {
            return Biomes.PLAINS;
        }

        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return tiles.get(pos);
        }

        @Override
        public net.minecraft.block.state.IBlockState getBlockState(BlockPos pos) {
            return Blocks.CHEST.getDefaultState();
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
                @Override public String makeString() { return "sorting_heart_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
