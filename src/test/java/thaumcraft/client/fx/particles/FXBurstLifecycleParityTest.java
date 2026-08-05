package thaumcraft.client.fx.particles;

import net.minecraft.init.Bootstrap;
import net.minecraft.profiler.Profiler;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FXBurstLifecycleParityTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void scaleThreeKeepsOriginalRandomScaleAndZeroMotion() {
        TestBurst burst = new TestBurst(new TestWorld(), 3.0F);

        assertTrue(burst.scale() >= 3.0F);
        assertTrue(burst.scale() < 6.0F);
        assertEquals(0.0D, burst.motionX(), 0.0D);
        assertEquals(0.0D, burst.motionY(), 0.0D);
        assertEquals(0.0D, burst.motionZ(), 0.0D);
    }

    @Test
    public void frameThirtyOneSurvivesUntilTheFollowingUpdate() {
        TestBurst burst = new TestBurst(new TestWorld(), 1.0F);

        for (int i = 0; i < 31; ++i) {
            burst.onUpdate();
        }

        assertEquals(31, burst.age());
        assertTrue(burst.isAlive());

        burst.onUpdate();

        assertFalse(burst.isAlive());
    }

    private static final class TestBurst extends FXBurst {
        private TestBurst(World world, float scale) {
            super(world, 1.0D, 2.0D, 3.0D, scale);
        }

        private float scale() {
            return this.particleScale;
        }

        private int age() {
            return this.particleAge;
        }

        private double motionX() {
            return this.motionX;
        }

        private double motionY() {
            return this.motionY;
        }

        private double motionZ() {
            return this.motionZ;
        }
    }

    private static final class TestWorld extends World {
        private TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "fx_burst_lifecycle"),
                    new WorldProviderSurface(), new Profiler(), true);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "fx_burst_lifecycle_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
