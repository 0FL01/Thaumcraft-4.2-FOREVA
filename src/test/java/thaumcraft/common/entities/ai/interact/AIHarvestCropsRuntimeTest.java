package thaumcraft.common.entities.ai.interact;

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
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.EnumGolemType;
import thaumcraft.common.lib.utils.CropUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AIHarvestCropsRuntimeTest {
    private static final BlockPos HOME = new BlockPos(0, 64, 0);
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
    public void categoryDispatchActivatesOnlyClickableCrops() {
        CropUtils.addStandardCrop(new ItemStack(Blocks.GOLD_BLOCK), 0);
        CropUtils.addClickableCrop(new ItemStack(Blocks.DIAMOND_BLOCK), 0);
        CropUtils.addStackedCrop(new ItemStack(Blocks.IRON_BLOCK), 0);

        assertFalse(AIHarvestCrops.shouldActivateCrop(Blocks.GOLD_BLOCK.getDefaultState()));
        assertTrue(AIHarvestCrops.shouldActivateCrop(Blocks.DIAMOND_BLOCK.getDefaultState()));
        assertFalse(AIHarvestCrops.shouldActivateCrop(Blocks.IRON_BLOCK.getDefaultState()));
        assertFalse(AIHarvestCrops.shouldActivateCrop(Blocks.WHEAT.getStateFromMeta(7)));
    }

    @Test
    public void aquaExtendsShuffledColumnSearchAndSearchIncludesHomeYPlusThree() {
        CropWorld world = new CropWorld();
        BlockPos crop = HOME.add(5, 3, 0);
        world.put(crop, Blocks.WHEAT.getStateFromMeta(7));

        EntityGolemBase golem = golem(world);
        AIHarvestCrops baseHarvest = new AIHarvestCrops(golem);
        assertEquals(4, baseHarvest.getSearchDistance());
        for (int i = 0; i < 81; i++) {
            assertNull(baseHarvest.findGrownCrop());
        }

        golem.setUpgrade(0, (byte) 3);
        AIHarvestCrops aquaHarvest = new AIHarvestCrops(golem);
        assertEquals(5, aquaHarvest.getSearchDistance());
        BlockPos found = null;
        for (int i = 0; i < 121 && found == null; i++) {
            found = aquaHarvest.findGrownCrop();
        }
        assertEquals(crop, found);
    }

    @Test
    public void adjacentSearchChainsWithinTc4WindowAndHomeBounds() {
        CropWorld world = new CropWorld();
        EntityGolemBase golem = golem(world);
        AIHarvestCrops harvest = new AIHarvestCrops(golem);
        BlockPos adjacent = HOME.add(2, 1, 2);
        world.put(adjacent, Blocks.CARROTS.getStateFromMeta(7));

        assertEquals(adjacent, harvest.findAdjacentCrop(HOME));

        CropWorld boundedWorld = new CropWorld();
        EntityGolemBase boundedGolem = golem(boundedWorld);
        AIHarvestCrops boundedHarvest = new AIHarvestCrops(boundedGolem);
        boundedWorld.put(HOME.add(5, 0, 0), Blocks.POTATOES.getStateFromMeta(7));
        assertNull(boundedHarvest.findAdjacentCrop(HOME.add(4, 0, 0)));
    }

    @Test
    public void continuationRejectsAChangedTargetMetadata() {
        CropWorld world = new CropWorld();
        EntityGolemBase golem = golem(world);
        BlockPos crop = HOME.east();
        world.put(crop, Blocks.WHEAT.getStateFromMeta(7));
        AIHarvestCrops harvest = new AIHarvestCrops(golem);
        harvest.setTarget(crop);
        harvest.count = 200;

        world.put(crop, Blocks.WHEAT.getStateFromMeta(6));
        assertFalse(harvest.shouldContinueExecuting());
    }

    private static EntityGolemBase golem(CropWorld world) {
        EntityGolemBase golem = new EntityGolemBase(world, EnumGolemType.WOOD, false);
        golem.setHomePosAndDistance(HOME, 32);
        golem.setPosition(HOME.getX() + 0.5D, HOME.getY(), HOME.getZ() + 0.5D);
        return golem;
    }

    private static final class CropWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();

        private CropWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "harvest_crops_runtime"),
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
                @Override public String makeString() { return "harvest_crops_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
