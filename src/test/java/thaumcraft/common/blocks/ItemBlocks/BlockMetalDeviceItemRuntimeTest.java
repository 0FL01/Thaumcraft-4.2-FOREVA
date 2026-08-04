package thaumcraft.common.blocks.ItemBlocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.tiles.TileArcaneLamp;
import thaumcraft.common.tiles.TileArcaneLampFertility;
import thaumcraft.common.tiles.TileArcaneLampGrowth;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BlockMetalDeviceItemRuntimeTest {
    private static final BlockPos POS = new BlockPos(0, 64, 0);

    @BeforeClass
    public static void bootstrapBlocks() {
        Bootstrap.register();
        if (ConfigBlocks.blockMetalDevice == null) {
            ConfigBlocks.init();
        }
    }

    @Test
    public void everyArcaneLampFacesTheClickedSupport() {
        BlockMetalDeviceItem item = new BlockMetalDeviceItem(ConfigBlocks.blockMetalDevice);
        for (int metadata : new int[]{7, 8, 13}) {
            IBlockState state = ConfigBlocks.blockMetalDevice.getStateFromMeta(metadata);
            for (EnumFacing side : EnumFacing.values()) {
                PlacementWorld world = new PlacementWorld();
                ItemStack stack = new ItemStack(item, 1, metadata);

                assertTrue(item.placeBlockAt(stack, null, world, POS, side, 0.5F, 0.5F, 0.5F, state));

                TileEntity tile = world.getTileEntity(POS);
                assertEquals(side.getOpposite(), facing(tile));
                NBTTagCompound saved = new NBTTagCompound();
                ((thaumcraft.api.TileThaumcraft) tile).writeCustomNBT(saved);
                assertEquals(side.getOpposite().getIndex(), saved.getInteger("orientation"));
                assertEquals(1, world.blockUpdates);
            }
        }
    }

    private static EnumFacing facing(TileEntity tile) {
        if (tile instanceof TileArcaneLamp) {
            return ((TileArcaneLamp) tile).facing;
        }
        if (tile instanceof TileArcaneLampGrowth) {
            return ((TileArcaneLampGrowth) tile).facing;
        }
        return ((TileArcaneLampFertility) tile).facing;
    }

    private static class PlacementWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();
        private int blockUpdates;

        PlacementWorld() {
            super(null, new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT),
                    "metal_device_item_runtime"), new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            IBlockState state = this.states.get(pos);
            return state == null ? Blocks.AIR.getDefaultState() : state;
        }

        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return this.tiles.get(pos);
        }

        @Override
        public boolean setBlockState(BlockPos pos, IBlockState state, int flags) {
            this.states.put(pos.toImmutable(), state);
            if (state.getBlock().hasTileEntity(state)) {
                TileEntity tile = state.getBlock().createTileEntity(this, state);
                tile.setWorld(this);
                tile.setPos(pos);
                this.tiles.put(pos.toImmutable(), tile);
            }
            return true;
        }

        @Override
        public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
            this.blockUpdates++;
        }

        @Override
        public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) {
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "metal_device_item_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
