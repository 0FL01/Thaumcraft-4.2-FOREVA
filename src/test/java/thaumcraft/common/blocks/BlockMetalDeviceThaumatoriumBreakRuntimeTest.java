package thaumcraft.common.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
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
import thaumcraft.common.tiles.TileThaumatorium;
import thaumcraft.common.tiles.TileThaumatoriumTop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BlockMetalDeviceThaumatoriumBreakRuntimeTest {
    private static final BlockPos BASE = new BlockPos(0, 64, 0);
    private static final BlockPos TOP = BASE.up();

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
        if (ConfigBlocks.blockMetalDevice == null) ConfigBlocks.init();
    }

    @Test
    public void breakingFormedTopDropsDelegatedCatalystOnceThenCollapsesBase() {
        ThaumatoriumWorld world = new ThaumatoriumWorld();
        BlockMetalDevice block = ConfigBlocks.blockMetalDevice;
        IBlockState baseState = block.getStateFromMeta(10);
        IBlockState topState = block.getStateFromMeta(11);
        TestThaumatorium base = new TestThaumatorium();
        TestThaumatoriumTop top = new TestThaumatoriumTop();
        world.attach(BASE.down(), block.getStateFromMeta(0), null);
        world.attach(BASE, baseState, base);
        world.attach(TOP, topState, top);
        base.setInventorySlotContents(0, new ItemStack(Items.DIAMOND, 7));
        top.update();

        // Forge replaces the block state before invoking oldBlock.breakBlock.
        world.setStateOnly(TOP, Blocks.AIR.getDefaultState());
        block.breakBlock(world, TOP, topState);
        block.neighborChanged(baseState, world, BASE, Blocks.AIR, TOP);

        assertEquals(1, world.spawned.size());
        assertEquals(7, world.spawned.get(0).getItem().getCount());
        assertTrue(base.getStackInSlot(0).isEmpty());
        assertEquals(9, world.getBlockState(BASE).getValue(BlockMetalDevice.TYPE).intValue());
    }

    @Test
    public void breakingFormedBaseDropsCatalystOnceThenCollapsesTop() {
        ThaumatoriumWorld world = new ThaumatoriumWorld();
        BlockMetalDevice block = ConfigBlocks.blockMetalDevice;
        IBlockState baseState = block.getStateFromMeta(10);
        IBlockState topState = block.getStateFromMeta(11);
        TestThaumatorium base = new TestThaumatorium();
        TestThaumatoriumTop top = new TestThaumatoriumTop();
        world.attach(BASE.down(), block.getStateFromMeta(0), null);
        world.attach(BASE, baseState, base);
        world.attach(TOP, topState, top);
        base.setInventorySlotContents(0, new ItemStack(Items.DIAMOND, 5));
        top.update();

        world.setStateOnly(BASE, Blocks.AIR.getDefaultState());
        block.breakBlock(world, BASE, baseState);
        block.neighborChanged(topState, world, TOP, Blocks.AIR, BASE);

        assertEquals(1, world.spawned.size());
        assertEquals(5, world.spawned.get(0).getItem().getCount());
        assertTrue(base.getStackInSlot(0).isEmpty());
        assertEquals(9, world.getBlockState(TOP).getValue(BlockMetalDevice.TYPE).intValue());
    }

    private static final class TestThaumatorium extends TileThaumatorium {
        @Override public void markDirty() { }
    }

    private static final class TestThaumatoriumTop extends TileThaumatoriumTop {
        @Override public void markDirty() { }
    }

    private static final class ThaumatoriumWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();
        private final List<EntityItem> spawned = new ArrayList<>();

        ThaumatoriumWorld() {
            super(null, new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT),
                    "thaumatorium_break_runtime"), new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        void attach(BlockPos pos, IBlockState state, TileEntity tile) {
            this.states.put(pos.toImmutable(), state);
            if (tile != null) {
                tile.setWorld(this);
                tile.setPos(pos);
                this.tiles.put(pos.toImmutable(), tile);
            }
        }

        void setStateOnly(BlockPos pos, IBlockState state) {
            this.states.put(pos.toImmutable(), state);
        }

        @Override public IBlockState getBlockState(BlockPos pos) {
            IBlockState state = this.states.get(pos);
            return state == null ? Blocks.AIR.getDefaultState() : state;
        }
        @Override public TileEntity getTileEntity(BlockPos pos) { return this.tiles.get(pos); }
        @Override public void removeTileEntity(BlockPos pos) { this.tiles.remove(pos); }
        @Override public boolean setBlockState(BlockPos pos, IBlockState state, int flags) {
            this.states.put(pos.toImmutable(), state);
            return true;
        }
        @Override public boolean isBlockPowered(BlockPos pos) { return false; }
        @Override public boolean spawnEntity(Entity entity) {
            if (entity instanceof EntityItem) this.spawned.add((EntityItem) entity);
            return true;
        }

        @Override protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "thaumatorium_break_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }
        @Override protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) { return true; }
    }
}
