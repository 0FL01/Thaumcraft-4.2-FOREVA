package thaumcraft.common.entities.ai.interact;

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
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.EnumGolemType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class AIUseItemCadenceRuntimeTest {
    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void aerIntervalsAreStableAndBounded() {
        assertEquals(15, AIUseItem.clickIntervalTicks(0));
        assertEquals(12, AIUseItem.clickIntervalTicks(1));
        assertEquals(9, AIUseItem.clickIntervalTicks(2));
        assertEquals(3, AIUseItem.clickIntervalTicks(20));
    }

    @Test
    public void startupAndBoundarySchedulingUseTheSameUpgradeInterval() {
        for (int aer = 0; aer <= 2; aer++) {
            EntityGolemBase golem = new EntityGolemBase(new TestWorld(), EnumGolemType.THAUMIUM, true);
            golem.setCore((byte) 8);
            for (int slot = 0; slot < aer; slot++) golem.setUpgrade(slot, (byte) 0);
            int interval = AIUseItem.clickIntervalTicks(aer);
            AIUseItem use = new AIUseItem(golem);

            assertEquals("initial Aer " + aer, interval, use.nextTick);
            golem.ticksExisted = interval - 1;
            assertFalse(use.shouldExecute());
            assertEquals(interval, use.nextTick);

            golem.ticksExisted = interval;
            assertFalse("no marker is available", use.shouldExecute());
            assertEquals("boundary Aer " + aer, interval * 2, use.nextTick);
        }
    }

    private static final class TestWorld extends World {
        private TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "r8_use_cadence"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override public Biome getBiome(BlockPos pos) { return Biomes.PLAINS; }
        @Override public IBlockState getBlockState(BlockPos pos) { return Blocks.AIR.getDefaultState(); }
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
                @Override public String makeString() { return "r8_use_cadence_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
