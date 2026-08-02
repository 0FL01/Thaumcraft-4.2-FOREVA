package thaumcraft.common.lib.utils;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemStack;
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
import net.minecraftforge.fml.common.event.FMLInterModComms;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.blocks.BlockManaPod;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CropUtilsRuntimeTest {
    private static final BlockPos POS = new BlockPos(0, 64, 0);
    private List<String> standardCrops;
    private List<String> clickableCrops;
    private List<String> stackedCrops;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Before
    public void isolateCropRegistrations() {
        this.standardCrops = new ArrayList<>(CropUtils.standardCrops);
        this.clickableCrops = new ArrayList<>(CropUtils.clickableCrops);
        this.stackedCrops = new ArrayList<>(CropUtils.stackedCrops);
        CropUtils.standardCrops.clear();
        CropUtils.clickableCrops.clear();
        CropUtils.stackedCrops.clear();
    }

    @After
    public void restoreCropRegistrations() {
        CropUtils.standardCrops.clear();
        CropUtils.standardCrops.addAll(this.standardCrops);
        CropUtils.clickableCrops.clear();
        CropUtils.clickableCrops.addAll(this.clickableCrops);
        CropUtils.stackedCrops.clear();
        CropUtils.stackedCrops.addAll(this.stackedCrops);
    }

    @Test
    public void lifecycleRegistersTc4BaselineCrops() {
        BlockManaPod originalManaPod = ConfigBlocks.blockManaPod;
        BlockManaPod manaPod = (BlockManaPod) new BlockManaPod().setTranslationKey("test.mana_pod");
        ConfigBlocks.blockManaPod = manaPod;
        try {
            Config.initMisc();

            assertTrue(CropUtils.isStandardCrop(Blocks.MELON_BLOCK.getDefaultState()));
            assertTrue(CropUtils.isStandardCrop(Blocks.PUMPKIN.getDefaultState()));
            assertTrue(CropUtils.isStackedCrop(Blocks.REEDS.getDefaultState()));
            assertTrue(CropUtils.isStackedCrop(Blocks.CACTUS.getDefaultState()));
            assertTrue(CropUtils.isStandardCrop(manaPod.getStateFromMeta(7)));
            assertFalse(CropUtils.isStandardCrop(manaPod.getStateFromMeta(6)));

            CropWorld world = new CropWorld();
            world.put(POS, manaPod.getStateFromMeta(7));
            assertTrue(CropUtils.isGrownCrop(world, POS));
        } finally {
            ConfigBlocks.blockManaPod = originalManaPod;
        }
    }

    @Test
    public void matureStemsSurviveWhileTheirRegisteredFruitIsHarvestable() {
        Config.initMisc();
        CropWorld world = new CropWorld();
        BlockPos pumpkinStem = POS;
        BlockPos melonStem = POS.add(0, 0, 3);
        world.put(pumpkinStem, Blocks.PUMPKIN_STEM.getStateFromMeta(7));
        world.put(pumpkinStem.east(), Blocks.PUMPKIN.getDefaultState());
        world.put(melonStem, Blocks.MELON_STEM.getStateFromMeta(7));
        world.put(melonStem.west(), Blocks.MELON_BLOCK.getDefaultState());

        assertFalse(CropUtils.isGrownCrop(world, pumpkinStem));
        assertTrue(CropUtils.isGrownCrop(world, pumpkinStem.east()));
        assertFalse(CropUtils.isGrownCrop(world, melonStem));
        assertTrue(CropUtils.isGrownCrop(world, melonStem.west()));
    }

    @Test
    public void stackedReedsAndCactusPreserveTheirBaseAtEveryCommonHeight() {
        Config.initMisc();
        assertStackedColumn(Blocks.REEDS, POS);
        assertStackedColumn(Blocks.CACTUS, POS.add(4, 0, 0));
    }

    @Test
    public void legacyHarvestCropImcRegistersEveryCategory() throws Exception {
        ItemStack standard = new ItemStack(Blocks.GOLD_BLOCK);
        ItemStack clickable = new ItemStack(Blocks.DIAMOND_BLOCK);
        ItemStack stacked = new ItemStack(Blocks.IRON_BLOCK);
        CropUtils.processIMC(Arrays.asList(
                imcMessage("harvestStandardCrop", standard),
                imcMessage("harvestClickableCrop", clickable),
                imcMessage("harvestStackedCrop", stacked),
                imcMessage("unrelated", new ItemStack(Blocks.EMERALD_BLOCK))));

        assertTrue(CropUtils.isStandardCrop(Blocks.GOLD_BLOCK.getDefaultState()));
        assertTrue(CropUtils.isClickableCrop(Blocks.DIAMOND_BLOCK.getDefaultState()));
        assertTrue(CropUtils.isStackedCrop(Blocks.IRON_BLOCK.getDefaultState()));
        assertFalse(CropUtils.isStandardCrop(Blocks.EMERALD_BLOCK.getDefaultState()));
        assertFalse(CropUtils.isClickableCrop(Blocks.GOLD_BLOCK.getDefaultState()));
        assertFalse(CropUtils.isStackedCrop(Blocks.DIAMOND_BLOCK.getDefaultState()));
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

    @Test
    public void stackedRegistrationPreservesTheBaseEvenForGrowableCrops() {
        BlockCrops crop = new FiveAgeCrop();
        CropUtils.addStackedCrop(crop, 5);
        CropWorld world = new CropWorld();
        world.put(POS, crop.withAge(5));

        assertFalse(CropUtils.isGrownCrop(world, POS));
        world.put(POS.down(), crop.withAge(0));
        assertTrue(CropUtils.isGrownCrop(world, POS));
    }

    private static void assertStackedColumn(Block block, BlockPos base) {
        CropWorld world = new CropWorld();
        world.put(base, block.getDefaultState());
        assertFalse(CropUtils.isGrownCrop(world, base));

        world.put(base.up(), block.getDefaultState());
        assertFalse(CropUtils.isGrownCrop(world, base));
        assertTrue(CropUtils.isGrownCrop(world, base.up()));

        world.put(base.up(2), block.getDefaultState());
        assertFalse(CropUtils.isGrownCrop(world, base));
        assertTrue(CropUtils.isGrownCrop(world, base.up()));
        assertTrue(CropUtils.isGrownCrop(world, base.up(2)));
    }

    private static FMLInterModComms.IMCMessage imcMessage(String key, ItemStack stack) throws Exception {
        for (Constructor<?> constructor : FMLInterModComms.IMCMessage.class.getDeclaredConstructors()) {
            Class<?>[] parameters = constructor.getParameterTypes();
            if (parameters.length == 3 && parameters[0] == String.class && parameters[1] == Object.class) {
                constructor.setAccessible(true);
                return (FMLInterModComms.IMCMessage) constructor.newInstance(key, stack, null);
            }
        }
        throw new AssertionError("Forge IMC item message constructor not found");
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
