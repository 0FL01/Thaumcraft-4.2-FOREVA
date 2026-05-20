package thaumcraft.common.tiles;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
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
import thaumcraft.api.aspects.Aspect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class TileResearchTablePersistenceRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void recalculateBonusKeepsPresenceOnlyAspectAmounts() throws Exception {
        DeterministicResearchWorld world = new DeterministicResearchWorld();
        TileResearchTable table = new TileResearchTable();
        table.setWorld(world);
        table.setPos(new BlockPos(0, 80, 0));

        invokeRecalculateBonus(table);
        invokeRecalculateBonus(table);

        assertEquals(1, table.bonusAspects.getAmount(Aspect.AIR));
        assertEquals(1, table.bonusAspects.getAmount(Aspect.ENTROPY));
    }

    @Test
    public void saveLoadRoundTripPreservesPresenceOnlyBonusSemantics() {
        TileResearchTable source = new TileResearchTable();
        source.bonusAspects.add(Aspect.AIR, 3);
        source.bonusAspects.add(Aspect.ORDER, 2);

        NBTTagCompound tag = new NBTTagCompound();
        source.writeCustomNBT(tag);

        TileResearchTable restored = new TileResearchTable();
        restored.readCustomNBT(tag);

        assertEquals(1, restored.bonusAspects.getAmount(Aspect.AIR));
        assertEquals(1, restored.bonusAspects.getAmount(Aspect.ORDER));
        assertEquals(0, restored.bonusAspects.getAmount(Aspect.FIRE));
    }

    private static void invokeRecalculateBonus(TileResearchTable table) throws Exception {
        Method method = TileResearchTable.class.getDeclaredMethod("recalculateBonus");
        method.setAccessible(true);
        method.invoke(table);
    }

    private static class DeterministicResearchWorld extends World {
        private final Random zeroRandom = new Random(0L) {
            @Override
            public int nextInt(int bound) {
                return 0;
            }
        };

        DeterministicResearchWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT), "research_table_persistence"),
                    new WorldProviderSurface(),
                    new Profiler(),
                    false);
            this.provider.setWorld(this);
            setWorldRandom(this.zeroRandom);
            this.chunkProvider = this.createChunkProvider();
        }

        private void setWorldRandom(Random random) {
            try {
                Field randField = World.class.getDeclaredField("rand");
                randField.setAccessible(true);
                randField.set(this, random);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean isDaytime() {
            return false;
        }

        @Override
        public int getLight(BlockPos pos) {
            return 0;
        }

        @Override
        public boolean canSeeSky(BlockPos pos) {
            return false;
        }

        @Override
        public int getHeight() {
            return 100;
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            return Blocks.AIR.getDefaultState();
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
                    return "research_table_persistence_dummy";
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
