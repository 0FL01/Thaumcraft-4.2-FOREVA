package thaumcraft.common.entities.monster;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
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
import thaumcraft.common.lib.utils.EntityUtils;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class HellbatCombatLifecycleRuntimeTest {
    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void ordinaryContactExplodesOnlyOnRollZero() {
        TestWorld explodingWorld = new TestWorld();
        TestFireBat exploding = bat(explodingWorld, new ScriptedRandom(new int[]{0}, new boolean[0]));
        RecordingZombie explodingTarget = new RecordingZombie(explodingWorld);

        exploding.hit(explodingTarget);

        assertEquals(1, explodingWorld.explosions);
        assertTrue(exploding.isDead);
        assertEquals(0, explodingTarget.meleeHits);
        assertEquals(0, explodingTarget.fireCalls);

        TestWorld survivingWorld = new TestWorld();
        TestFireBat surviving = bat(survivingWorld, new ScriptedRandom(new int[]{1}, new boolean[]{true}));
        RecordingZombie survivingTarget = new RecordingZombie(survivingWorld);

        surviving.hit(survivingTarget);

        assertEquals(0, survivingWorld.explosions);
        assertEquals(1, survivingTarget.meleeHits);
        assertEquals(0, survivingTarget.fireCalls);
    }

    @Test
    public void batBombAlwaysExplodesAndDevilNeverDoes() {
        TestWorld bombWorld = new TestWorld();
        ScriptedRandom bombRandom = new ScriptedRandom(new int[]{9}, new boolean[0]);
        TestFireBat bomb = bat(bombWorld, bombRandom);
        bomb.damBonus = 3;
        bomb.setIsExplosive(true);

        bomb.hit(new RecordingZombie(bombWorld));

        assertEquals(1, bombWorld.explosions);
        assertEquals(2.49F, bombWorld.lastExplosionStrength, 0.0001F);
        assertEquals(0, bombRandom.intCalls);

        TestWorld devilWorld = new TestWorld();
        ScriptedRandom devilRandom = new ScriptedRandom(new int[]{0}, new boolean[]{false});
        TestFireBat devil = bat(devilWorld, devilRandom);
        devil.setIsDevil(true);
        devil.setIsExplosive(true);
        RecordingZombie devilTarget = new RecordingZombie(devilWorld);

        devil.hit(devilTarget);

        assertEquals(0, devilWorld.explosions);
        assertFalse(devil.isDead);
        assertEquals(0, devilTarget.meleeHits);
        assertEquals(1, devilTarget.fireCalls);
        assertEquals(0, devilRandom.intCalls);
    }

    @Test
    public void survivingContactChoosesMeleeOrIgnitionAndVampireAlwaysMelees() {
        TestWorld igniteWorld = new TestWorld();
        TestFireBat igniteBat = bat(igniteWorld, new ScriptedRandom(new int[]{1}, new boolean[]{false}));
        RecordingZombie igniteTarget = new RecordingZombie(igniteWorld);

        igniteBat.hit(igniteTarget);

        assertEquals(0, igniteTarget.meleeHits);
        assertEquals(1, igniteTarget.fireCalls);
        assertEquals(2, igniteTarget.lastFireSeconds);

        TestWorld vampireWorld = new TestWorld();
        ScriptedRandom vampireRandom = new ScriptedRandom(new int[]{1}, new boolean[]{false});
        TestFireBat vampire = bat(vampireWorld, vampireRandom);
        vampire.setIsVampire(true);
        vampire.setHealth(2.0F);
        RecordingZombie vampireTarget = new RecordingZombie(vampireWorld);

        vampire.hit(vampireTarget);

        assertEquals(1, vampireTarget.meleeHits);
        assertEquals(0, vampireTarget.fireCalls);
        assertEquals(3.0F, vampire.getHealth(), 0.0F);
        assertEquals(0, vampireRandom.booleanCalls);
    }

    @Test
    public void summonedMeleePreservesMotionClearsAirborneAndMarksRecentlyHit() {
        TestWorld world = new TestWorld();
        TestFireBat bat = bat(world, new ScriptedRandom(new int[]{1}, new boolean[]{true}));
        bat.setIsSummoned(true);
        RecordingZombie target = new RecordingZombie(world);
        target.motionX = 0.25D;
        target.motionY = -0.5D;
        target.motionZ = 0.75D;
        target.isAirBorne = true;

        bat.hit(target);

        assertEquals(0.25D, target.motionX, 0.0D);
        assertEquals(-0.5D, target.motionY, 0.0D);
        assertEquals(0.75D, target.motionZ, 0.0D);
        assertFalse(target.isAirBorne);
        assertEquals(100, EntityUtils.getRecentlyHit(target));
        assertFalse(target.hurtResistantTime == 100);
    }

    @Test
    public void devilSetterRefreshesDamageWithoutFixingPracticalHealth() {
        TestWorld world = new TestWorld();
        TestFireBat bat = bat(world, new ScriptedRandom(new int[0], new boolean[0]));
        bat.damBonus = 4;

        bat.setIsSummoned(true);
        assertEquals(6.0D, attackDamage(bat), 0.0D);

        bat.setIsDevil(true);
        assertEquals(7.0D, attackDamage(bat), 0.0D);
        assertEquals(5.0F, bat.getMaxHealth(), 0.0F);
    }

    @Test
    public void naturalBatAcquiresNearestVulnerableNonCreativePlayer() {
        TestWorld world = new TestWorld();
        TestPlayer creative = new TestPlayer(world, "creative", true, false);
        creative.setPosition(2.0D, 0.0D, 0.0D);
        TestPlayer vulnerable = new TestPlayer(world, "vulnerable", false, false);
        vulnerable.setPosition(5.0D, 0.0D, 0.0D);
        TestPlayer outside = new TestPlayer(world, "outside", false, false);
        outside.setPosition(13.0D, 0.0D, 0.0D);
        world.playerEntities.add(creative);
        world.playerEntities.add(vulnerable);
        world.playerEntities.add(outside);
        TestFireBat bat = bat(world, new ScriptedRandom(new int[0], new boolean[0]));

        bat.aiTick();

        assertSame(vulnerable, bat.getAttackTarget());
        assertEquals(0, bat.starveHits);
    }

    @Test
    public void summonedBatNeverAcquiresAndDeadTargetStarvesImmediately() {
        TestWorld world = new TestWorld();
        TestPlayer vulnerable = new TestPlayer(world, "available", false, false);
        vulnerable.setPosition(3.0D, 0.0D, 0.0D);
        world.playerEntities.add(vulnerable);
        TestFireBat bat = bat(world, new ScriptedRandom(new int[0], new boolean[0]));
        bat.setIsSummoned(true);

        bat.aiTick();

        assertSame(null, bat.getAttackTarget());
        assertEquals(1, bat.starveHits);
        assertEquals(2.0F, bat.lastStarveDamage, 0.0F);

        RecordingZombie dead = new RecordingZombie(world);
        dead.setDead();
        bat.setAttackTarget(dead);
        bat.aiTick();

        assertSame(null, bat.getAttackTarget());
        assertEquals(2, bat.starveHits);
    }

    @Test
    public void naturalTargetlessBatWandersWithoutStarving() {
        TestWorld world = new TestWorld();
        TestFireBat bat = bat(world, new ScriptedRandom(new int[0], new boolean[0]));

        bat.aiTick();

        assertSame(null, bat.getAttackTarget());
        assertEquals(0, bat.starveHits);
        assertTrue(bat.motionX != 0.0D || bat.motionY != 0.0D || bat.motionZ != 0.0D);
    }

    @Test
    public void batBombHasNoTimeoutBeyondOneHundredTicks() {
        TestWorld world = new TestWorld();
        TestFireBat bat = bat(world, new ScriptedRandom(new int[0], new boolean[0]));
        bat.setIsSummoned(true);
        bat.setIsExplosive(true);
        bat.ticksExisted = 101;

        bat.livingTick();

        assertEquals(0, world.explosions);
        assertFalse(bat.isDead);
    }

    private static TestFireBat bat(TestWorld world, Random random) {
        TestFireBat bat = new TestFireBat(world);
        bat.setIsBatHanging(false);
        setEntityRandom(bat, random);
        return bat;
    }

    private static double attackDamage(EntityFireBat bat) {
        return bat.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
    }

    private static void setEntityRandom(Entity entity, Random random) {
        try {
            Field field = Entity.class.getDeclaredField("rand");
            field.setAccessible(true);
            field.set(entity, random);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static final class TestFireBat extends EntityFireBat {
        private int starveHits;
        private float lastStarveDamage;

        private TestFireBat(World world) {
            super(world);
        }

        private void hit(Entity entity) {
            this.onHitTarget(entity);
        }

        private void aiTick() {
            this.updateAITasks();
        }

        private void livingTick() {
            this.onLivingUpdate();
        }

        @Override
        public boolean attackEntityFrom(DamageSource source, float amount) {
            if (source == DamageSource.STARVE) {
                ++this.starveHits;
                this.lastStarveDamage = amount;
                return true;
            }
            return super.attackEntityFrom(source, amount);
        }

        @Override public float getBrightness() { return 0.0F; }
        @Override public void travel(float strafe, float vertical, float forward) { }
        @Override protected void collideWithNearbyEntities() { }
    }

    private static final class RecordingZombie extends EntityZombie {
        private int meleeHits;
        private int fireCalls;
        private int lastFireSeconds;

        private RecordingZombie(World world) {
            super(world);
        }

        @Override
        public boolean attackEntityFrom(DamageSource source, float amount) {
            ++this.meleeHits;
            this.motionX = 10.0D;
            this.motionY = 11.0D;
            this.motionZ = 12.0D;
            this.isAirBorne = true;
            return true;
        }

        @Override
        public void setFire(int seconds) {
            ++this.fireCalls;
            this.lastFireSeconds = seconds;
        }
    }

    private static final class TestPlayer extends EntityPlayer {
        private final boolean creative;
        private final boolean spectator;

        private TestPlayer(World world, String name, boolean creative, boolean spectator) {
            super(world, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)), name));
            this.creative = creative;
            this.spectator = spectator;
            this.capabilities.disableDamage = creative;
        }

        @Override public boolean isCreative() { return this.creative; }
        @Override public boolean isSpectator() { return this.spectator; }
    }

    private static final class ScriptedRandom extends Random {
        private final int[] ints;
        private final boolean[] booleans;
        private int intIndex;
        private int booleanIndex;
        private int intCalls;
        private int booleanCalls;

        private ScriptedRandom(int[] ints, boolean[] booleans) {
            this.ints = ints;
            this.booleans = booleans;
        }

        @Override
        public int nextInt(int bound) {
            ++this.intCalls;
            int value = this.intIndex < this.ints.length ? this.ints[this.intIndex++] : 0;
            return Math.floorMod(value, bound);
        }

        @Override
        public boolean nextBoolean() {
            ++this.booleanCalls;
            return this.booleanIndex < this.booleans.length && this.booleans[this.booleanIndex++];
        }
    }

    private static final class TestWorld extends World {
        private int explosions;
        private float lastExplosionStrength;

        private TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "hellbat_combat_lifecycle"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        public Explosion createExplosion(Entity entity, double x, double y, double z,
                                         float strength, boolean damagesTerrain) {
            ++this.explosions;
            this.lastExplosionStrength = strength;
            return null;
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            return Blocks.AIR.getDefaultState();
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "hellbat_combat_lifecycle_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
