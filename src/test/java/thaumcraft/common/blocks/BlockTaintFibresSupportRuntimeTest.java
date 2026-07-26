package thaumcraft.common.blocks;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Biomes;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.blocks.ItemBlocks.BlockTaintFibresItem;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class BlockTaintFibresSupportRuntimeTest {
    private static final BlockPos POS = new BlockPos(0, 64, 0);
    private static Biome previousTaintBiome;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
        if (ConfigBlocks.blockTaintFibres == null) {
            ConfigBlocks.init();
        }
        previousTaintBiome = ThaumcraftWorldGenerator.biomeTaint;
        ThaumcraftWorldGenerator.biomeTaint = Biomes.PLAINS;
    }

    @AfterClass
    public static void restoreTaintBiome() {
        ThaumcraftWorldGenerator.biomeTaint = previousTaintBiome;
    }

    @Test
    public void fibreKeepsSolidFaceAttachmentButNotFenceOnlySupport() {
        BlockTaintFibres block = ConfigBlocks.blockTaintFibres;
        IBlockState fibres = block.getStateFromMeta(0);

        SupportWorld wallWorld = new SupportWorld();
        wallWorld.put(POS, fibres);
        wallWorld.put(POS.east(), Blocks.STONE.getDefaultState());
        block.neighborChanged(fibres, wallWorld, POS, Blocks.STONE, POS.east());
        assertSame(block, wallWorld.getBlockState(POS).getBlock());

        SupportWorld fenceWorld = new SupportWorld();
        fenceWorld.put(POS, fibres);
        fenceWorld.put(POS.down(), Blocks.OAK_FENCE.getDefaultState());
        block.neighborChanged(fibres, fenceWorld, POS, Blocks.OAK_FENCE, POS.down());
        assertTrue(fenceWorld.isAirBlock(POS));
    }

    @Test
    public void uprightTaintTypesRequireSolidFloorInsteadOfSideOrFenceSupport() {
        BlockTaintFibres block = ConfigBlocks.blockTaintFibres;
        for (int metadata = 1; metadata <= 4; metadata++) {
            IBlockState state = block.getStateFromMeta(metadata);

            SupportWorld floorWorld = new SupportWorld();
            floorWorld.put(POS, state);
            floorWorld.put(POS.down(), Blocks.STONE.getDefaultState());
            block.neighborChanged(state, floorWorld, POS, Blocks.STONE, POS.down());
            assertSame(block, floorWorld.getBlockState(POS).getBlock());

            SupportWorld wallWorld = new SupportWorld();
            wallWorld.put(POS, state);
            wallWorld.put(POS.east(), Blocks.STONE.getDefaultState());
            block.neighborChanged(state, wallWorld, POS, Blocks.STONE, POS.east());
            assertTrue(wallWorld.isAirBlock(POS));

            SupportWorld fenceWorld = new SupportWorld();
            fenceWorld.put(POS, state);
            fenceWorld.put(POS.down(), Blocks.OAK_FENCE.getDefaultState());
            fenceWorld.put(POS.east(), Blocks.STONE.getDefaultState());
            block.neighborChanged(state, fenceWorld, POS, Blocks.OAK_FENCE, POS.down());
            assertTrue(fenceWorld.isAirBlock(POS));
        }
    }

    @Test
    public void uprightItemPlacementRejectsFenceWithoutMutatingWorld() {
        BlockTaintFibres block = ConfigBlocks.blockTaintFibres;
        BlockTaintFibresItem item = new BlockTaintFibresItem(block);
        ItemStack stack = new ItemStack(item, 1, 1);

        SupportWorld floorWorld = new SupportWorld();
        floorWorld.put(POS, Blocks.STONE.getDefaultState());
        assertTrue(item.canPlaceBlockOnSide(floorWorld, POS, EnumFacing.UP, null, stack));
        assertTrue(item.placeBlockAt(stack, null, floorWorld, POS.up(), EnumFacing.UP,
                0.5F, 1.0F, 0.5F, block.getStateFromMeta(1)));
        assertSame(block, floorWorld.getBlockState(POS.up()).getBlock());

        SupportWorld fenceWorld = new SupportWorld();
        fenceWorld.put(POS, Blocks.OAK_FENCE.getDefaultState());
        fenceWorld.put(POS.up().east(), Blocks.STONE.getDefaultState());
        assertFalse(item.canPlaceBlockOnSide(fenceWorld, POS, EnumFacing.UP, null, stack));
        assertFalse(item.placeBlockAt(stack, null, fenceWorld, POS.up(), EnumFacing.UP,
                0.5F, 1.0F, 0.5F, block.getStateFromMeta(1)));
        assertTrue(fenceWorld.isAirBlock(POS.up()));
    }

    @Test
    public void fibreItemUsesRealSolidFacesAndRejectsFenceOnlyPlacement() {
        BlockTaintFibres block = ConfigBlocks.blockTaintFibres;
        BlockTaintFibresItem item = new BlockTaintFibresItem(block);
        ItemStack stack = new ItemStack(item, 1, 0);

        SupportWorld wallWorld = new SupportWorld();
        wallWorld.put(POS.east(), Blocks.STONE.getDefaultState());
        assertTrue(item.canPlaceBlockOnSide(wallWorld, POS.east(), EnumFacing.WEST, null, stack));

        SupportWorld fenceWorld = new SupportWorld();
        fenceWorld.put(POS.down(), Blocks.OAK_FENCE.getDefaultState());
        assertFalse(item.canPlaceBlockOnSide(fenceWorld, POS.down(), EnumFacing.UP, null, stack));
    }

    @Test
    public void customPlantsKeepForgeFenceRejection() {
        BlockCustomPlant block = ConfigBlocks.blockCustomPlant;
        for (int metadata = 0; metadata <= 5; metadata++) {
            SupportWorld world = new SupportWorld();
            world.put(POS.down(), Blocks.OAK_FENCE.getDefaultState());
            assertFalse("custom plant metadata " + metadata,
                    block.canPlaceBlockAt(world, POS));
        }
    }

    private static final class SupportWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();

        private SupportWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "taint_support_runtime"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        private void put(BlockPos pos, IBlockState state) {
            this.states.put(pos.toImmutable(), state);
        }

        @Override
        public Biome getBiome(BlockPos pos) {
            return Biomes.PLAINS;
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            IBlockState state = this.states.get(pos);
            return state == null ? Blocks.AIR.getDefaultState() : state;
        }

        @Override
        public boolean setBlockState(BlockPos pos, IBlockState state, int flags) {
            if (state.getBlock() == Blocks.AIR) {
                this.states.remove(pos);
            } else {
                this.states.put(pos.toImmutable(), state);
            }
            return true;
        }

        @Override
        public boolean setBlockToAir(BlockPos pos) {
            return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }

        @Override
        public boolean mayPlace(Block block, BlockPos pos, boolean skipCollisionCheck,
                                EnumFacing sidePlacedOn, Entity placer) {
            return this.getBlockState(pos).getBlock().isReplaceable(this, pos)
                    && block.canPlaceBlockAt(this, pos);
        }

        @Override public TileEntity getTileEntity(BlockPos pos) { return null; }
        @Override public Explosion createExplosion(Entity entityIn, double x, double y, double z,
                                                   float strength, boolean isSmoking) { return null; }
        @Override public void notifyBlockUpdate(BlockPos pos, IBlockState oldState,
                                                IBlockState newState, int flags) { }
        @Override public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) { }
        @Override public void updateComparatorOutputLevel(BlockPos pos, Block blockIn) { }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "taint_support_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
