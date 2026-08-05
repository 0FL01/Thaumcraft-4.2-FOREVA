package thaumcraft.common.blocks;

import net.minecraft.init.Bootstrap;
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

import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BlockAiryTransientFieldRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void transientFieldsParticipateInRandomTicking() {
        assertTrue(new BlockAiry().getTickRandomly());
    }

    @Test
    public void serverTickRemovesFireAndEerieFieldsOnly() {
        BlockAiry block = new BlockAiry();
        RecordingWorld world = new RecordingWorld(false);
        BlockPos pos = new BlockPos(2, 64, 3);

        block.updateTick(world, pos, block.getDefaultState().withProperty(BlockAiry.TYPE, 10), new Random(0L));
        assertTrue(world.removed);

        world.removed = false;
        block.updateTick(world, pos, block.getDefaultState().withProperty(BlockAiry.TYPE, 11), new Random(0L));
        assertTrue(world.removed);

        world.removed = false;
        block.updateTick(world, pos, block.getDefaultState().withProperty(BlockAiry.TYPE, 0), new Random(0L));
        assertFalse(world.removed);
    }

    @Test
    public void clientTickDoesNotRemoveTransientFieldsAuthoritatively() {
        BlockAiry block = new BlockAiry();
        RecordingWorld world = new RecordingWorld(true);

        block.updateTick(world, BlockPos.ORIGIN,
                block.getDefaultState().withProperty(BlockAiry.TYPE, 10), new Random(0L));

        assertFalse(world.removed);
    }

    private static final class RecordingWorld extends World {
        private boolean removed;

        private RecordingWorld(boolean remote) {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "block_airy_transient_field"),
                    new WorldProviderSurface(), new Profiler(), remote);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        public boolean setBlockToAir(BlockPos pos) {
            this.removed = true;
            return true;
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "block_airy_transient_field_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
