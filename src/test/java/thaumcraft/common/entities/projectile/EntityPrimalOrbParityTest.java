package thaumcraft.common.entities.projectile;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
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

import java.util.UUID;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class EntityPrimalOrbParityTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void shooterConstructorLaunchesAtReferenceVelocity() {
        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world);
        player.setPosition(4.0D, 70.0D, 8.0D);
        player.rotationYaw = 0.0F;
        player.rotationPitch = 0.0F;

        EntityPrimalOrb orb = new EntityPrimalOrb(world, player, true);
        double speed = Math.sqrt(orb.motionX * orb.motionX + orb.motionY * orb.motionY + orb.motionZ * orb.motionZ);

        assertTrue("Primal orb should launch near the TC4 0.5 velocity", speed > 0.4D && speed < 0.6D);
        assertSame(player, orb.getThrower());
        assertTrue(orb.seeker);
    }

    private static class TestPlayer extends EntityPlayer {
        TestPlayer(World world) {
            super(world, new GameProfile(UUID.fromString("8dbd7f34-df34-4dcf-a1dd-94ee3db7f021"), "primal_orb_test"));
        }

        @Override
        public boolean isSpectator() {
            return false;
        }

        @Override
        public boolean isCreative() {
            return false;
        }
    }

    private static class TestWorld extends World {
        TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT), "primal_orb"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "primal_orb_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
