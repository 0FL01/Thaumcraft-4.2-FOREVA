package thaumcraft.common.lib.utils;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
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
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CropUtilsRuntimeTest {
    private static final BlockPos POS = new BlockPos(0, 64, 0);

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void vanillaCropsUseTheirActualMaximumAge() {
        CropWorld world = new CropWorld();

        world.put(POS, Blocks.WHEAT.getStateFromMeta(6));
        assertFalse(CropUtils.isGrownCrop(world, POS));
        world.put(POS, Blocks.WHEAT.getStateFromMeta(7));
        assertTrue(CropUtils.isGrownCrop(world, POS));

        world.put(POS, Blocks.BEETROOTS.getStateFromMeta(2));
        assertFalse(CropUtils.isGrownCrop(world, POS));
        world.put(POS, Blocks.BEETROOTS.getStateFromMeta(3));
        assertTrue(CropUtils.isGrownCrop(world, POS));
    }

    @Test
    public void addonCropsCanUseNonSevenMaximumAgeAndRegistryFallbacks() {
        BlockCrops crop = new FiveAgeCrop();
        CropWorld world = new CropWorld();

        world.put(POS, crop.withAge(4));
        assertFalse(CropUtils.isGrownCrop(world, POS));
        world.put(POS, crop.withAge(5));
        assertTrue(CropUtils.isGrownCrop(world, POS));

        IBlockState registeredState = crop.withAge(2);
        String key = crop.getTranslationKey() + crop.getMetaFromState(registeredState);
        CropUtils.standardCrops.add(key);
        try {
            world.put(POS, registeredState);
            assertTrue(CropUtils.isGrownCrop(world, POS));
        } finally {
            CropUtils.standardCrops.remove(key);
        }
    }

    private static final class FiveAgeCrop extends BlockCrops {
        @Override
        public int getMaxAge() {
            return 5;
        }
    }

    private static final class CropWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();

        private CropWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "crop_utils_runtime"),
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

        @Override public TileEntity getTileEntity(BlockPos pos) { return null; }
        @Override public Explosion createExplosion(Entity entityIn, double x, double y, double z,
                                                   float strength, boolean isSmoking) { return null; }
        @Override public void notifyBlockUpdate(BlockPos pos, IBlockState oldState,
                                                IBlockState newState, int flags) { }
        @Override public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) { }
        @Override public void updateComparatorOutputLevel(BlockPos pos, net.minecraft.block.Block blockIn) { }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "crop_utils_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
