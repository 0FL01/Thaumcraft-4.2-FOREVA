package thaumcraft.common.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
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

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GolemFetterRuntimeTest {
    private static final BlockPos POS = new BlockPos(0, 64, 0);

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void poweredPlacementAndNeighborChangesToggleTheActiveState() {
        BlockCosmeticSolid block = new BlockCosmeticSolid();
        TestWorld world = new TestWorld();
        world.powered = true;

        IBlockState placed = block.getStateForPlacement(world, POS, EnumFacing.UP,
                0.5F, 0.5F, 0.5F, BlockCosmeticSolid.TYPE_GOLEM_FETTER, null, EnumHand.MAIN_HAND);
        assertEquals(BlockCosmeticSolid.TYPE_GOLEM_FETTER_ACTIVE,
                placed.getValue(BlockCosmeticSolid.TYPE).intValue());

        IBlockState inactive = block.getDefaultState()
                .withProperty(BlockCosmeticSolid.TYPE, BlockCosmeticSolid.TYPE_GOLEM_FETTER);
        world.states.put(POS, inactive);
        block.onBlockAdded(world, POS, inactive);
        assertEquals(BlockCosmeticSolid.TYPE_GOLEM_FETTER_ACTIVE,
                world.getBlockState(POS).getValue(BlockCosmeticSolid.TYPE).intValue());

        world.powered = false;
        block.neighborChanged(world.getBlockState(POS), world, POS, Blocks.REDSTONE_BLOCK, POS.down());
        assertEquals(BlockCosmeticSolid.TYPE_GOLEM_FETTER,
                world.getBlockState(POS).getValue(BlockCosmeticSolid.TYPE).intValue());
    }

    @Test
    public void activeStateDropsInactiveAndIsNotCreativeVisible() {
        BlockCosmeticSolid block = new BlockCosmeticSolid();
        IBlockState active = block.getDefaultState()
                .withProperty(BlockCosmeticSolid.TYPE, BlockCosmeticSolid.TYPE_GOLEM_FETTER_ACTIVE);

        assertEquals(BlockCosmeticSolid.TYPE_GOLEM_FETTER, block.damageDropped(active));
        NonNullList<ItemStack> items = NonNullList.create();
        block.getSubBlocks(CreativeTabs.SEARCH, items);
        assertTrue(items.stream().anyMatch(stack -> stack.getItemDamage() == BlockCosmeticSolid.TYPE_GOLEM_FETTER));
        assertFalse(items.stream().anyMatch(stack -> stack.getItemDamage() == BlockCosmeticSolid.TYPE_GOLEM_FETTER_ACTIVE));
    }

    private static final class TestWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();
        private boolean powered;

        private TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "golem_fetter"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            IBlockState state = this.states.get(pos);
            return state == null ? Blocks.AIR.getDefaultState() : state;
        }

        @Override
        public boolean setBlockState(BlockPos pos, IBlockState state, int flags) {
            this.states.put(pos.toImmutable(), state);
            return true;
        }

        @Override
        public boolean isBlockPowered(BlockPos pos) {
            return this.powered;
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "golem_fetter_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
