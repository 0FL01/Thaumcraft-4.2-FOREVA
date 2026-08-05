package thaumcraft.common.lib.utils;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BlockUtilsC4aRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void createStackedBlockDispatchesCustomSilkOverride() {
        ItemStack result = BlockUtils.createStackedBlock(new CustomSilkBlock(), 9);

        assertEquals(Items.DIAMOND, result.getItem());
        assertEquals(2, result.getCount());
        assertEquals(7, result.getItemDamage());
    }

    @Test
    public void createStackedBlockUsesVanillaSilkMetadataNormalization() {
        ItemStack result = BlockUtils.createStackedBlock(Blocks.FURNACE, EnumFacing.SOUTH.getIndex());

        assertEquals(Item.getItemFromBlock(Blocks.FURNACE), result.getItem());
        assertEquals(0, result.getItemDamage());
    }

    @Test
    public void exposureAcceptsGlassAndOtherNonOpaqueNeighborsButRejectsSixOpaqueSides() {
        ExposureWorld world = new ExposureWorld();
        BlockPos center = new BlockPos(2, 64, 3);

        assertFalse(BlockUtils.isBlockExposed(world, center.getX(), center.getY(), center.getZ()));

        world.states.put(center.east(), Blocks.GLASS.getDefaultState());
        assertTrue(BlockUtils.isBlockExposed(world, center.getX(), center.getY(), center.getZ()));

        world.states.put(center.east(), Blocks.STONE.getDefaultState());
        world.states.put(center.down(), Blocks.TORCH.getDefaultState());
        assertTrue(BlockUtils.isBlockExposed(world, center.getX(), center.getY(), center.getZ()));
    }

    @Test
    public void representativeSilkAndExposureCallersKeepUsingSharedHelpers() throws IOException {
        assertContains("src/main/java/thaumcraft/common/items/wands/foci/FocusExcavation.java",
                "BlockUtils.createStackedBlock(block, meta)");
        assertContains("src/main/java/thaumcraft/common/items/wands/foci/FocusTrade.java",
                "BlockUtils.createStackedBlock(block, meta)");
        assertContains("src/main/java/thaumcraft/common/tiles/TileArcaneBore.java",
                "BlockUtils.createStackedBlock(block, meta)");
        assertContains("src/main/java/thaumcraft/common/items/relics/ItemThaumometer.java",
                "BlockUtils.createStackedBlock(block, dropMeta)");
        assertContains("src/main/java/thaumcraft/common/blocks/BlockEldritchNothing.java",
                "BlockUtils.isBlockExposed(world, pos.getX(), pos.getY(), pos.getZ())");
    }

    private static void assertContains(String path, String expected) throws IOException {
        String source = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        assertTrue(path + " must use the shared helper", source.contains(expected));
    }

    private static final class CustomSilkBlock extends Block {
        private CustomSilkBlock() {
            super(Material.ROCK);
        }

        @Override
        protected ItemStack getSilkTouchDrop(IBlockState state) {
            return new ItemStack(Items.DIAMOND, 2, 7);
        }
    }

    private static final class ExposureWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();

        private ExposureWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "block_utils_c4a"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            IBlockState state = this.states.get(pos);
            return state == null ? Blocks.STONE.getDefaultState() : state;
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "block_utils_c4a_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
