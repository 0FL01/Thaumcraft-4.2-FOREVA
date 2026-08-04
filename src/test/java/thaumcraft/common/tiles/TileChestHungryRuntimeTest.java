package thaumcraft.common.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TileChestHungryRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void lidAdvancesOnlyOncePerTick() {
        TileChestHungry chest = new TileChestHungry();
        new TestWorld().attach(chest);
        chest.numUsingPlayers = 1;

        chest.update();

        assertEquals(0.1F, chest.lidAngle, 0.0F);
        assertEquals(0.0F, chest.prevLidAngle, 0.0F);
    }

    @Test
    public void clientEventRestoresCompleteForcedEatingCycle() {
        TileChestHungry chest = new TileChestHungry();
        new TestWorld().attach(chest);

        assertTrue(chest.receiveClientEvent(3, TileChestHungry.EAT_LID_TICKS));
        chest.update();
        assertEquals(0.35F, chest.lidAngle, 0.0F);

        for (int tick = 1; tick < TileChestHungry.EAT_LID_TICKS; ++tick) {
            chest.update();
        }
        assertEquals(1.0F, chest.lidAngle, 0.0F);

        chest.update();
        assertEquals(0.65F, chest.lidAngle, 0.0001F);
    }

    private static final class TestWorld extends World {
        TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT), "hungry_chest"),
                    new WorldProviderSurface(),
                    new Profiler(),
                    true);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        void attach(TileChestHungry chest) {
            chest.setWorld(this);
            chest.setPos(BlockPos.ORIGIN);
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            return Blocks.AIR.getDefaultState();
        }

        @Override
        public void playSound(EntityPlayer player, BlockPos pos, SoundEvent soundIn,
                              SoundCategory category, float volume, float pitch) {
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
                    return "HungryChestTest";
                }

                @Override
                public boolean isChunkGeneratedAt(int x, int z) {
                    return false;
                }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
