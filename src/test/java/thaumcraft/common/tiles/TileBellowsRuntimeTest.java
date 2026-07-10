package thaumcraft.common.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class TileBellowsRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void sharedBellowsHelperRequiresExactFacingForEveryDirection() {
        BlockPos consumerPos = new BlockPos(0, 64, 0);
        for (EnumFacing direction : EnumFacing.VALUES) {
            BellowsWorld world = new BellowsWorld();
            TileBellows bellows = attachBellows(world, consumerPos.offset(direction), direction.getOpposite());

            assertEquals("correctly faced bellows should count from " + direction,
                    1, TileBellows.getBellows(world, consumerPos, EnumFacing.VALUES));

            bellows.orientation = (byte) direction.getIndex();
            assertEquals("wrongly faced bellows should not count from " + direction,
                    0, TileBellows.getBellows(world, consumerPos, EnumFacing.VALUES));
        }
    }

    @Test
    public void downwardOrientationIsNotAWildcardAndPoweredBellowsAreExcluded() {
        BlockPos consumerPos = new BlockPos(0, 64, 0);
        BellowsWorld world = new BellowsWorld();
        BlockPos bellowsPos = consumerPos.east();
        TileBellows bellows = attachBellows(world, bellowsPos, EnumFacing.DOWN);

        assertEquals(0, TileBellows.getBellows(world, consumerPos, EnumFacing.VALUES));

        bellows.orientation = (byte) EnumFacing.WEST.getIndex();
        assertEquals(1, TileBellows.getBellows(world, consumerPos, EnumFacing.VALUES));

        world.setPowered(bellowsPos, true);
        assertEquals(0, TileBellows.getBellows(world, consumerPos, EnumFacing.VALUES));
    }

    @Test
    public void arcaneFurnaceExcludesPoweredBellows() throws Exception {
        BellowsWorld world = new BellowsWorld();
        BlockPos furnacePos = new BlockPos(0, 64, 0);
        BlockPos bellowsPos = furnacePos.east(2);
        TileArcaneFurnace furnace = new TileArcaneFurnace();
        furnace.setWorld(world);
        furnace.setPos(furnacePos);
        attachBellows(world, bellowsPos, EnumFacing.WEST);

        assertEquals(1, invokeArcaneFurnaceBellows(furnace));

        world.setPowered(bellowsPos, true);
        assertEquals(0, invokeArcaneFurnaceBellows(furnace));
    }

    private static TileBellows attachBellows(BellowsWorld world, BlockPos pos, EnumFacing orientation) {
        TileBellows bellows = new TileBellows();
        bellows.orientation = (byte) orientation.getIndex();
        world.attach(pos, bellows);
        return bellows;
    }

    private static int invokeArcaneFurnaceBellows(TileArcaneFurnace furnace) throws Exception {
        Method method = TileArcaneFurnace.class.getDeclaredMethod("getBellows");
        method.setAccessible(true);
        return (Integer) method.invoke(furnace);
    }

    private static class BellowsWorld extends World {
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();
        private final Set<BlockPos> powered = new HashSet<>();

        BellowsWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT), "bellows_runtime"),
                    new WorldProviderSurface(),
                    new Profiler(),
                    false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        void attach(BlockPos pos, TileEntity tile) {
            tile.setWorld(this);
            tile.setPos(pos);
            this.tiles.put(pos, tile);
        }

        void setPowered(BlockPos pos, boolean value) {
            if (value) {
                this.powered.add(pos);
            } else {
                this.powered.remove(pos);
            }
        }

        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return this.tiles.get(pos);
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            return Blocks.AIR.getDefaultState();
        }

        @Override
        public boolean isBlockPowered(BlockPos pos) {
            return this.powered.contains(pos);
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override
                public Chunk getLoadedChunk(int x, int z) {
                    return null;
                }

                @Override
                public Chunk provideChunk(int x, int z) {
                    return null;
                }

                @Override
                public boolean tick() {
                    return false;
                }

                @Override
                public String makeString() {
                    return "bellows_runtime_dummy";
                }

                @Override
                public boolean isChunkGeneratedAt(int x, int z) {
                    return true;
                }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
