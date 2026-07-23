package thaumcraft.common.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
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
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TileDeconstructionTableRuntimeTest {
    private AspectList previousStickTags;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Before
    public void rememberTags() {
        this.previousStickTags = ThaumcraftApi.objectTags.get(tagKey());
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
    public void validInputCompletesAfterFortyTicksAndSelectsDistinctPrimal() {
        registerStickTags(new AspectList().add(Aspect.LIGHT, 1));
        DeconstructionWorld world = new DeconstructionWorld(new SequenceRandom(0, 1));
        TestTable table = world.attach(new TestTable());
        table.setInventorySlotContents(0, new ItemStack(Items.STICK, 2));
        table.dirtyCount = 0;

        table.update();
        assertEquals(39, table.breaktime);
        assertEquals(2, table.getStackInSlot(0).getCount());
        assertEquals(1, world.updateCount);

        for (int i = 0; i < 38; ++i) table.update();
        assertEquals(1, table.breaktime);
        assertEquals(2, table.getStackInSlot(0).getCount());

        table.update();
        assertEquals(0, table.breaktime);
        assertEquals(1, table.getStackInSlot(0).getCount());
        assertSame(Aspect.FIRE, table.aspect);
        assertEquals(2, world.updateCount);
        assertEquals(2, table.dirtyCount);
    }

    @Test
    public void failedRollStillConsumesOneInput() {
        registerStickTags(new AspectList().add(Aspect.AIR, 1));
        DeconstructionWorld world = new DeconstructionWorld(new SequenceRandom(79));
        TestTable table = world.attach(new TestTable());
        table.setInventorySlotContents(0, new ItemStack(Items.STICK));

        for (int i = 0; i < 40; ++i) table.update();

        assertEquals(0, table.breaktime);
        assertTrue(table.getStackInSlot(0).isEmpty());
        assertNull(table.aspect);
        assertEquals(2, world.updateCount);
    }

    @Test
    public void pendingAspectBlocksWorkAndInvalidatedInputResetsProgress() {
        registerStickTags(new AspectList().add(Aspect.AIR, 1));
        DeconstructionWorld world = new DeconstructionWorld(new SequenceRandom(0, 0));
        TestTable table = world.attach(new TestTable());
        table.setInventorySlotContents(0, new ItemStack(Items.STICK));
        table.aspect = Aspect.FIRE;

        table.update();
        assertEquals(0, table.breaktime);
        assertEquals(1, table.getStackInSlot(0).getCount());

        table.aspect = null;
        table.update();
        assertEquals(39, table.breaktime);

        table.setInventorySlotContents(0, ItemStack.EMPTY);
        table.update();
        assertEquals(0, table.breaktime);
    }

    @Test
    public void canonicalNbtRoundTripMigratesLegacyKeysAndKeepsProgressTransient() {
        TileDeconstructionTable source = new TileDeconstructionTable();
        source.setInventorySlotContents(0, new ItemStack(Items.STICK, 3));
        source.aspect = Aspect.AIR;
        source.breaktime = 17;
        source.setGuiDisplayName("Named table");

        NBTTagCompound saved = new NBTTagCompound();
        source.writeCustomNBT(saved);
        assertTrue(saved.hasKey("Items", 9));
        assertTrue(saved.hasKey("Aspect", 8));
        assertFalse(saved.hasKey("Inventory"));
        assertFalse(saved.hasKey("aspect"));
        assertFalse(saved.hasKey("breaktime"));

        TileDeconstructionTable restored = new TileDeconstructionTable();
        restored.readCustomNBT(saved);
        assertEquals(3, restored.getStackInSlot(0).getCount());
        assertSame(Aspect.AIR, restored.aspect);
        assertEquals(0, restored.breaktime);
        assertEquals("Named table", source.getName());

        NBTTagCompound legacy = new NBTTagCompound();
        NBTTagList inventory = new NBTTagList();
        NBTTagCompound item = new NBTTagCompound();
        item.setByte("Slot", (byte) 0);
        new ItemStack(Items.STICK, 2).writeToNBT(item);
        inventory.appendTag(item);
        legacy.setTag("Inventory", inventory);
        legacy.setString("aspect", Aspect.FIRE.getTag());
        legacy.setInteger("breaktime", 20);

        TestTable migrated = new TestTable();
        migrated.readCustomNBT(legacy);
        assertEquals(2, migrated.getStackInSlot(0).getCount());
        assertSame(Aspect.FIRE, migrated.aspect);
        assertEquals(0, migrated.breaktime);
    }

    @Test
    public void emptyUpdatePacketClearsVisibleStateWithoutOverwritingContainerProgress() {
        TestTable source = new TestTable();
        TestTable client = new TestTable();
        client.setInventorySlotContents(0, new ItemStack(Items.STICK));
        client.aspect = Aspect.EARTH;
        client.breaktime = 11;

        client.readCustomNBT(source.getUpdatePacket().getNbtCompound());

        assertTrue(client.getStackInSlot(0).isEmpty());
        assertNull(client.aspect);
        assertEquals(11, client.breaktime);
    }

    @Test
    public void inventoryKeepsOriginalLimitsAndSidedAutomationRules() {
        registerStickTags(new AspectList().add(Aspect.AIR, 1));
        TestTable table = new TestTable();
        table.setInventorySlotContents(0, new ItemStack(Items.STICK, 80));

        assertEquals(64, table.getStackInSlot(0).getCount());
        assertEquals(0, table.getSlotsForFace(EnumFacing.UP).length);
        assertEquals(1, table.getSlotsForFace(EnumFacing.NORTH).length);
        assertFalse(table.canInsertItem(0, new ItemStack(Items.STICK), EnumFacing.UP));
        assertTrue(table.canInsertItem(0, new ItemStack(Items.STICK), EnumFacing.NORTH));
        assertTrue(table.canExtractItem(0, table.getStackInSlot(0), EnumFacing.DOWN));
    }

    private static void registerStickTags(AspectList tags) {
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.STICK), tags);
    }

    private static List tagKey() {
        return Arrays.asList(Items.STICK, 0);
    }

    private static final class TestTable extends TileDeconstructionTable {
        private int dirtyCount;

        @Override
        public void markDirty() {
            ++this.dirtyCount;
        }
    }

    private static final class SequenceRandom extends Random {
        private final int[] values;
        private int index;

        private SequenceRandom(int... values) {
            this.values = values;
        }

        @Override
        public int nextInt(int bound) {
            int value = this.index < this.values.length ? this.values[this.index++] : 0;
            return Math.floorMod(value, bound);
        }
    }

    private static final class DeconstructionWorld extends World {
        private TileEntity tile;
        private int updateCount;

        private DeconstructionWorld(Random random) {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT), "deconstruction_table"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.setWorldRandom(random);
            this.chunkProvider = this.createChunkProvider();
        }

        private TestTable attach(TestTable table) {
            this.tile = table;
            table.setWorld(this);
            table.setPos(BlockPos.ORIGIN);
            return table;
        }

        private void setWorldRandom(Random random) {
            try {
                Field field = World.class.getDeclaredField("rand");
                field.setAccessible(true);
                field.set(this, random);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
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
                public String makeString() { return "deconstruction_table_dummy"; }

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
