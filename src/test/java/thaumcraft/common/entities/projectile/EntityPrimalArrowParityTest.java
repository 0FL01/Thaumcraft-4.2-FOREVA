package thaumcraft.common.entities.projectile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.AxisAlignedBB;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EntityPrimalArrowParityTest {

    @BeforeClass
    public static void bootstrapMinecraft() {
        Bootstrap.register();
    }

    @Test
    public void sixTypesUseTc4DamageSourcesAmountsAndEffects() {
        int[] expectedDamage = {7, 7, 7, 10, 6, 6};
        String[] expectedSources = {"airarrow", "firearrow", "arrow", "arrow", "orderarrow", "arrow"};

        for (int type = 0; type < 6; ++type) {
            TestWorld world = new TestWorld();
            TestArrow arrow = new TestArrow(world, type);
            RecordingZombie target = new RecordingZombie(world);
            arrow.motionX = 3.0D;
            arrow.hit(target);

            assertEquals(expectedDamage[type], target.damage, 0.0F);
            assertNotNull(target.source);
            assertEquals(expectedSources[type], target.source.getDamageType());
            assertTrue(target.source.isProjectile());
            assertEquals(type == 0 || type == 4, target.source.isMagicDamage());
            assertEquals(type == 0 || type == 4, target.source.isUnblockable());
            assertEquals(type == 1, target.source.isFireDamage());
            assertTrue(arrow.isDead);

            if (type == 1) assertEquals(5, target.fireSeconds);
            if (type == 2) {
                assertNotNull(target.getActivePotionEffect(MobEffects.SLOWNESS));
                assertEquals(4, target.getActivePotionEffect(MobEffects.SLOWNESS).getAmplifier());
                assertEquals(200, target.getActivePotionEffect(MobEffects.SLOWNESS).getDuration());
            }
            if (type == 4) {
                assertNotNull(target.getActivePotionEffect(MobEffects.WEAKNESS));
                assertEquals(4, target.getActivePotionEffect(MobEffects.WEAKNESS).getAmplifier());
            }
            if (type == 5) {
                assertNotNull(target.getActivePotionEffect(MobEffects.WITHER));
                assertEquals(0, target.getActivePotionEffect(MobEffects.WITHER).getAmplifier());
                assertEquals(100, target.getActivePotionEffect(MobEffects.WITHER).getDuration());
            }
        }
    }

    @Test
    public void flameDoublesFireArrowIgnitionButNeverIgnitesWaterArrow() {
        TestWorld world = new TestWorld();
        TestArrow fire = new TestArrow(world, 1);
        RecordingZombie fireTarget = new RecordingZombie(world);
        fire.motionX = 3.0D;
        fire.setFire(100);
        fire.hit(fireTarget);
        assertEquals(10, fireTarget.fireSeconds);

        TestArrow water = new TestArrow(world, 2);
        RecordingZombie waterTarget = new RecordingZombie(world);
        water.motionX = 3.0D;
        water.setFire(100);
        water.hit(waterTarget);
        assertEquals(0, waterTarget.fireSeconds);
    }

    @Test
    public void boneAndPowerMutationsRetainTc4VirtualDamageScaling() {
        TestArrow earth = new TestArrow(new TestWorld(), 3);
        assertEquals(3.15D, earth.getDamage(), 0.0001D);
        earth.setDamage(earth.getDamage() + 0.5D);
        assertEquals(5.475D, earth.getDamage(), 0.0001D);

        TestArrow order = new TestArrow(new TestWorld(), 4);
        assertEquals(1.68D, order.getDamage(), 0.0001D);
        order.setDamage(order.getDamage() + 1.0D);
        assertEquals(2.144D, order.getDamage(), 0.0001D);
    }

    @Test
    public void embeddedArrowExpiresAtOneHundredTicksAndCannotBePickedUp() {
        TestArrow arrow = new TestArrow(new TestWorld(), 0);
        arrow.embedForTicks(99);

        arrow.onUpdate();

        assertTrue(arrow.isDead);
        assertEquals(net.minecraft.entity.projectile.EntityArrow.PickupStatus.DISALLOWED, arrow.pickupStatus);
        assertTrue(arrow.getArrowStackForTest().isEmpty());
    }

    private static final class TestArrow extends EntityPrimalArrow {
        private TestArrow(World world, int type) {
            super(world);
            this.setArrowType(type);
        }

        private void hit(Entity target) {
            this.onHit(new RayTraceResult(target));
        }

        private void embedForTicks(int ticks) {
            this.inGround = true;
            this.timeInGround = ticks;
        }

        private net.minecraft.item.ItemStack getArrowStackForTest() {
            return this.getArrowStack();
        }
    }

    private static final class RecordingZombie extends EntityZombie {
        private DamageSource source;
        private float damage;
        private int fireSeconds;

        private RecordingZombie(World world) {
            super(world);
        }

        @Override
        public boolean attackEntityFrom(DamageSource source, float amount) {
            this.source = source;
            this.damage = amount;
            return true;
        }

        @Override
        public void setFire(int seconds) {
            this.fireSeconds = seconds;
        }
    }

    private static final class TestWorld extends World {
        private TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "primal_arrow"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            return Blocks.AIR.getDefaultState();
        }

        @Override
        public boolean collidesWithAnyBlock(AxisAlignedBB bbox) {
            return true;
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
                @Override public String makeString() { return "PrimalArrowTest"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
