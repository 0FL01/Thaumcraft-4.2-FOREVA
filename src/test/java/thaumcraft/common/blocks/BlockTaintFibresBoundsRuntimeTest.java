package thaumcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
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

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class BlockTaintFibresBoundsRuntimeTest {
    private static final BlockPos POS = new BlockPos(0, 64, 0);

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
        if (ConfigBlocks.blockTaintFibres == null) {
            ConfigBlocks.init();
        }
    }

    @Test
    public void fibrousTaintSelectsTheFirstSupportedThinSurface() {
        IBlockState fibres = ConfigBlocks.blockTaintFibres.getStateFromMeta(0);
        for (EnumFacing facing : EnumFacing.VALUES) {
            BoundsWorld world = new BoundsWorld();
            world.support(facing);
            assertEquals(facing.getName(), expected(facing),
                    ConfigBlocks.blockTaintFibres.getBoundingBox(fibres, world, POS));
        }

        BoundsWorld precedenceWorld = new BoundsWorld();
        precedenceWorld.support(EnumFacing.DOWN);
        precedenceWorld.support(EnumFacing.UP);
        assertEquals(expected(EnumFacing.DOWN),
                ConfigBlocks.blockTaintFibres.getBoundingBox(fibres, precedenceWorld, POS));
        assertEquals(expected(EnumFacing.DOWN),
                ConfigBlocks.blockTaintFibres.getBoundingBox(fibres, new BoundsWorld(), POS));
    }

    @Test
    public void fibrousTaintRemainsPassableReplaceableAndNonColliding() {
        BoundsWorld world = new BoundsWorld();
        IBlockState fibres = ConfigBlocks.blockTaintFibres.getStateFromMeta(0);

        assertNull(ConfigBlocks.blockTaintFibres.getCollisionBoundingBox(fibres, world, POS));
        assertTrue(ConfigBlocks.blockTaintFibres.isPassable(world, POS));
        assertTrue(ConfigBlocks.blockTaintFibres.isReplaceable(world, POS));
    }

    private static AxisAlignedBB expected(EnumFacing facing) {
        switch (facing) {
            case DOWN: return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D);
            case UP: return new AxisAlignedBB(0.0D, 0.9375D, 0.0D, 1.0D, 1.0D, 1.0D);
            case NORTH: return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.0625D);
            case SOUTH: return new AxisAlignedBB(0.0D, 0.0D, 0.9375D, 1.0D, 1.0D, 1.0D);
            case WEST: return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0625D, 1.0D, 1.0D);
            case EAST: return new AxisAlignedBB(0.9375D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
            default: throw new AssertionError(facing);
        }
    }

    private static final class BoundsWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();

        private BoundsWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT),
                            "taint_fibres_bounds_runtime"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        private void support(EnumFacing facing) {
            this.states.put(POS.offset(facing), Blocks.STONE.getDefaultState());
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            IBlockState state = this.states.get(pos);
            return state == null ? Blocks.AIR.getDefaultState() : state;
        }

        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return null;
        }

        @Override
        public Explosion createExplosion(Entity entityIn, double x, double y, double z, float strength,
                                         boolean isSmoking) {
            return null;
        }

        @Override
        public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
        }

        @Override
        public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) {
        }

        @Override
        public void updateComparatorOutputLevel(BlockPos pos, Block blockIn) {
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "taint_fibres_bounds_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
