package thaumcraft.common.entities.projectile;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.Thaumcraft;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class GolemOrbImpactParityTest {

    private CommonProxy oldProxy;
    private RecordingProxy proxy;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Before
    public void installRecordingProxy() {
        this.oldProxy = Thaumcraft.proxy;
        this.proxy = new RecordingProxy();
        Thaumcraft.proxy = this.proxy;
    }

    @After
    public void restoreProxy() {
        Thaumcraft.proxy = this.oldProxy;
    }

    @Test
    public void golemOrbKeepsSpawnDataAndExposesThrowerToForge() {
        TestWorld world = new TestWorld();
        RecordingPlayer thrower = new RecordingPlayer(world);
        EntityGolemOrb orb = new EntityGolemOrb(world, thrower, thrower, false);

        assertTrue(orb instanceof IEntityAdditionalSpawnData);
        assertTrue(orb instanceof IThrowableEntity);
        IThrowableEntity throwable = orb;
        assertSame(thrower, throwable.getThrower());
        throwable.setThrower(null);
        assertNull(throwable.getThrower());
        throwable.setThrower(thrower);
        assertSame(thrower, throwable.getThrower());
    }

    @Test
    public void impactEmitsScaleOneBurstAtProjectilePosition() {
        TestWorld world = new TestWorld();
        RecordingPlayer thrower = new RecordingPlayer(world);
        TestGolemOrb orb = new TestGolemOrb(world, thrower);
        orb.setPosition(2.5D, 65.0D, -3.5D);

        orb.impact();

        assertEquals(1, this.proxy.bursts);
        assertSame(world, this.proxy.world);
        assertEquals(2.5D, this.proxy.x, 0.0D);
        assertEquals(65.0D, this.proxy.y, 0.0D);
        assertEquals(-3.5D, this.proxy.z, 0.0D);
        assertEquals(1.0F, this.proxy.scale, 0.0F);
        assertTrue(orb.isDead);
    }

    @Test
    public void burstRemainsBetweenDamageAndSound() throws IOException {
        String source = new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/common/entities/projectile/EntityGolemOrb.java")),
                StandardCharsets.UTF_8);
        int damage = source.indexOf("result.entityHit.attackEntityFrom(");
        int burst = source.indexOf("Thaumcraft.proxy.burst(this.world, this.posX, this.posY, this.posZ, 1.0F);");
        int sound = source.indexOf("this.playSound(TCSounds.SHOCK", burst);
        int death = source.indexOf("this.setDead();", sound);

        assertTrue(damage >= 0 && burst > damage && sound > burst && death > sound);
    }

    private static final class TestGolemOrb extends EntityGolemOrb {
        private TestGolemOrb(World world, RecordingPlayer thrower) {
            super(world, thrower, thrower, false);
        }

        private void impact() {
            this.onImpact(new RayTraceResult(new Vec3d(this.posX, this.posY, this.posZ),
                    EnumFacing.UP, new BlockPos(this)));
        }
    }

    private static final class RecordingPlayer extends EntityPlayer {
        private RecordingPlayer(World world) {
            super(world, new GameProfile(UUID.fromString("7fc4f635-8c6d-4e84-adf2-7fe4af429588"),
                    "golem_orb_test"));
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
    }

    private static final class RecordingProxy extends CommonProxy {
        private int bursts;
        private World world;
        private double x;
        private double y;
        private double z;
        private float scale;

        @Override
        public void burst(World world, double x, double y, double z, float scale) {
            ++this.bursts;
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.scale = scale;
        }
    }

    private static final class TestWorld extends World {
        private TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "golem_orb_impact"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        public void playSound(EntityPlayer player, double x, double y, double z, SoundEvent sound,
                              SoundCategory category, float volume, float pitch) {
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "golem_orb_impact_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
