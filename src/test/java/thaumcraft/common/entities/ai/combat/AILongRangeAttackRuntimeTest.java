package thaumcraft.common.entities.ai.combat;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
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
import thaumcraft.common.entities.monster.boss.EntityEldritchWarden;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AILongRangeAttackRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void minimumRangeIsInclusiveAndMaximumRangeDoesNotStopChasing() {
        TestWorld world = new TestWorld();
        TestRangedMob mob = new TestRangedMob(world);
        TestRangedMob target = new TestRangedMob(world);
        AILongRangeAttack attack = new AILongRangeAttack(mob, 3.0D, 1.0D, 20, 40, 24.0F);
        mob.setPosition(0.0D, 64.0D, 0.0D);
        mob.setAttackTarget(target);

        target.setPosition(2.99D, 64.0D, 0.0D);
        assertFalse(attack.shouldExecute());

        target.setPosition(3.0D, 64.0D, 0.0D);
        assertTrue(attack.shouldExecute());

        target.setPosition(30.0D, 64.0D, 0.0D);
        assertTrue(attack.shouldExecute());
        attack.updateTask();
        assertEquals(1, mob.navigator.moveAttempts);
        assertEquals(1.0D, mob.navigator.lastSpeed, 0.0D);
        assertEquals(3, attack.getMutexBits());
    }

    @Test
    public void targetOutsideMaximumRangeNeverFiresAcrossCooldownCycles() {
        TestWorld world = new TestWorld();
        TestRangedMob mob = new TestRangedMob(world);
        TestRangedMob target = new TestRangedMob(world);
        mob.setPosition(0.0D, 64.0D, 0.0D);
        target.setPosition(30.0D, 64.0D, 0.0D);
        mob.setAttackTarget(target);
        AILongRangeAttack attack = new AILongRangeAttack(mob, 3.0D, 1.0D, 20, 40, 24.0F);

        assertTrue(attack.shouldExecute());
        for (int tick = 0; tick < 120; ++tick) {
            attack.updateTask();
        }

        assertEquals(0, mob.attacks);
        assertTrue(mob.navigator.moveAttempts > 0);
    }

    @Test
    public void visibleTargetAtMaximumRangeFiresWithTc4CooldownAndStrength() {
        TestWorld world = new TestWorld();
        TestRangedMob mob = new TestRangedMob(world);
        TestRangedMob target = new TestRangedMob(world);
        mob.setPosition(0.0D, 64.0D, 0.0D);
        target.setPosition(24.0D, 64.0D, 0.0D);
        mob.setAttackTarget(target);
        AILongRangeAttack attack = new AILongRangeAttack(mob, 3.0D, 1.0D, 20, 40, 24.0F);

        assertTrue(attack.shouldExecute());
        for (int tick = 0; tick < 40; ++tick) {
            attack.updateTask();
        }
        assertEquals(0, mob.attacks);
        attack.updateTask();

        assertEquals(1, mob.attacks);
        assertEquals(1.0F, mob.lastStrength, 0.0F);
        assertTrue(mob.navigator.clearCalls > 0);
    }

    @Test
    public void wardenUsesBossFollowRangeBaseline() {
        EntityEldritchWarden warden = new EntityEldritchWarden(new TestWorld());

        assertEquals(40.0D,
                warden.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getBaseValue(), 0.0D);
    }

    private static final class TestRangedMob extends EntityLiving implements IRangedAttackMob {
        private final RecordingNavigator navigator;
        private final RecordingSenses senses;
        private int attacks;
        private float lastStrength;

        private TestRangedMob(World world) {
            super(world);
            this.navigator = (RecordingNavigator) super.getNavigator();
            this.senses = new RecordingSenses(this);
        }

        @Override
        protected void initEntityAI() {
        }

        @Override
        protected PathNavigate createNavigator(World worldIn) {
            return new RecordingNavigator(this, worldIn);
        }

        @Override
        public PathNavigate getNavigator() {
            return this.navigator == null ? super.getNavigator() : this.navigator;
        }

        @Override
        public EntitySenses getEntitySenses() {
            return this.senses == null ? super.getEntitySenses() : this.senses;
        }

        @Override
        public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
            ++this.attacks;
            this.lastStrength = distanceFactor;
        }

        @Override
        public void setSwingingArms(boolean swingingArms) {
        }
    }

    private static final class RecordingSenses extends EntitySenses {
        private RecordingSenses(EntityLiving entity) {
            super(entity);
        }

        @Override
        public boolean canSee(Entity entity) {
            return true;
        }
    }

    private static final class RecordingNavigator extends PathNavigateGround {
        private int moveAttempts;
        private int clearCalls;
        private double lastSpeed;
        private boolean pathless;

        private RecordingNavigator(EntityLiving entity, World world) {
            super(entity, world);
        }

        @Override
        public boolean tryMoveToEntityLiving(Entity entity, double speed) {
            ++this.moveAttempts;
            this.lastSpeed = speed;
            this.pathless = false;
            return true;
        }

        @Override
        public void clearPath() {
            ++this.clearCalls;
            this.pathless = true;
        }

        @Override
        public boolean noPath() {
            return this.pathless;
        }
    }

    private static final class TestWorld extends World {
        private TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "long_range_attack_runtime"),
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
                @Override public String makeString() { return "long_range_attack_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
