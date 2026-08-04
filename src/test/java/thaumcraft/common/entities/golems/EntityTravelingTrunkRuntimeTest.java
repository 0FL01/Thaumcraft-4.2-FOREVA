package thaumcraft.common.entities.golems;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class EntityTravelingTrunkRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void ownerFollowUsesTc4HopThresholdAndUpgradeSpeeds() throws Exception {
        TestWorld world = new TestWorld();
        TestLiving owner = new TestLiving(world);
        owner.setPosition(6.0D, 0.0D, 0.0D);

        TestTrunk normal = trunk(world, owner, -1);
        normal.setPosition(0.0D, 0.0D, 0.0D);
        setTimer(normal, "jumpDelay", 0);
        normal.runAi();
        normal.applyQueuedJump();

        assertEquals(6.0F, normal.forward(), 0.0F);
        assertEquals(0.03F, normal.getAIMoveSpeed(), 0.0F);
        assertTrue(normal.jumping());
        assertTrue(world.sounds.contains(SoundEvents.BLOCK_CHEST_CLOSE));
        assertTrue(normal.getNavigator().noPath());

        TestTrunk fast = trunk(world, owner, 0);
        fast.setPosition(0.0D, 0.0D, 0.0D);
        setTimer(fast, "jumpDelay", 0);
        fast.runAi();
        assertEquals(8.0F, fast.forward(), 0.0F);
        assertEquals(0.04F, fast.getAIMoveSpeed(), 0.0F);

        owner.setPosition(5.0D, 0.0D, 0.0D);
        TestTrunk atThreshold = trunk(world, owner, -1);
        atThreshold.setPosition(0.0D, 0.0D, 0.0D);
        setTimer(atThreshold, "jumpDelay", 0);
        atThreshold.runAi();
        assertEquals(0.0F, atThreshold.forward(), 0.0F);
        assertFalse(atThreshold.jumping());
    }

    @Test
    public void waterSlowsHopAndStaySuppressesOwnerFollow() throws Exception {
        TestWorld world = new TestWorld();
        TestLiving owner = new TestLiving(world);
        owner.setPosition(6.0D, 0.0D, 0.0D);

        TestTrunk swimming = trunk(world, owner, -1);
        swimming.setPosition(0.0D, 0.0D, 0.0D);
        swimming.onGround = false;
        swimming.setInWaterForTest(true);
        setTimer(swimming, "jumpDelay", 0);
        swimming.runAi();
        assertEquals(4.5F, swimming.forward(), 0.0F);

        TestTrunk staying = trunk(world, owner, 0);
        staying.setPosition(0.0D, 0.0D, 0.0D);
        staying.setStay(true);
        setTimer(staying, "jumpDelay", 0);
        staying.runAi();
        assertEquals(0.0F, staying.forward(), 0.0F);
        assertFalse(staying.jumping());
    }

    @Test
    public void fireUpgradeAcquiresAndAttacksOwnerRevengeTarget() throws Exception {
        TestWorld world = new TestWorld();
        TestLiving owner = new TestLiving(world);
        TestLiving target = new TestLiving(world);
        owner.setPosition(0.0D, 0.0D, 0.0D);
        target.setPosition(1.0D, 0.0D, 0.0D);
        owner.setRevengeTarget(target);

        TestTrunk trunk = trunk(world, owner, 2);
        trunk.setPosition(0.0D, 0.0D, 0.0D);
        setTimer(trunk, "jumpDelay", 0);
        float health = target.getHealth();
        trunk.runAi();

        assertSame(target, trunk.getAttackTarget());
        assertEquals(600, trunk.getAnger());
        assertEquals(health - 4.0F, target.getHealth(), 0.0F);
        int cooldown = getTimer(trunk, "attackCooldown");
        assertTrue(cooldown >= 10 && cooldown <= 14);
        assertEquals(6.0F, trunk.forward(), 0.0F);

        trunk.setStay(true);
        setTimer(trunk, "jumpDelay", 0);
        trunk.runAi();
        assertSame(target, trunk.getAttackTarget());
        assertEquals(599, trunk.getAnger());
        assertEquals(6.0F, trunk.forward(), 0.0F);
    }

    private static TestTrunk trunk(TestWorld world, EntityLivingBase owner, int upgrade) {
        TestTrunk trunk = new TestTrunk(world, owner);
        trunk.setEntityId(world.nextEntityId++);
        trunk.setUpgrade(upgrade);
        trunk.onGround = true;
        return trunk;
    }

    private static void setTimer(EntityTravelingTrunk trunk, String name, int value) throws Exception {
        Field field = EntityTravelingTrunk.class.getDeclaredField(name);
        field.setAccessible(true);
        field.setInt(trunk, value);
    }

    private static int getTimer(EntityTravelingTrunk trunk, String name) throws Exception {
        Field field = EntityTravelingTrunk.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.getInt(trunk);
    }

    private static final class TestTrunk extends EntityTravelingTrunk {
        private final EntityLivingBase testOwner;

        TestTrunk(World world, EntityLivingBase owner) {
            super(world);
            this.testOwner = owner;
        }

        @Override
        public Entity getOwner() {
            return this.testOwner;
        }

        @Override
        public boolean canEntityBeSeen(Entity entityIn) {
            return true;
        }

        void runAi() {
            this.updateAITasks();
        }

        void applyQueuedJump() {
            this.getJumpHelper().doJump();
        }

        float forward() {
            return this.moveForward;
        }

        boolean jumping() {
            return this.isJumping;
        }

        void setInWaterForTest(boolean inWater) {
            this.inWater = inWater;
        }
    }

    private static final class TestLiving extends EntityLiving {
        TestLiving(World world) {
            super(world);
        }
    }

    private static final class TestWorld extends World {
        private int nextEntityId = 8000;
        private final List<SoundEvent> sounds = new ArrayList<>();

        TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT), "traveling_trunk"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            return Blocks.AIR.getDefaultState();
        }

        @Override
        public void playSound(EntityPlayer player, double x, double y, double z, SoundEvent sound,
                              SoundCategory category, float volume, float pitch) {
            this.sounds.add(sound);
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "traveling_trunk_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
