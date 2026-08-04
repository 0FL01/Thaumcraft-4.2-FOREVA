package thaumcraft.common.entities.golems;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
        assertSame(SoundEvents.ENTITY_ITEM_BREAK, normal.deathSound());

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

    @Test
    public void entropyVacuumUsesExactCenteredThreeBlockCube() throws Exception {
        TestWorld world = new TestWorld();
        TestTrunk trunk = trunk(world, null, 5);
        trunk.setPosition(10.0D, 20.0D, 30.0D);

        Method pullItems = EntityTravelingTrunk.class.getDeclaredMethod("pullItems");
        pullItems.setAccessible(true);
        pullItems.invoke(trunk);

        assertEquals(2, world.entityQueries.size());
        AxisAlignedBB attraction = world.entityQueries.get(1);
        assertEquals(7.0D, attraction.minX, 0.0D);
        assertEquals(17.0D, attraction.minY, 0.0D);
        assertEquals(27.0D, attraction.minZ, 0.0D);
        assertEquals(13.0D, attraction.maxX, 0.0D);
        assertEquals(23.0D, attraction.maxY, 0.0D);
        assertEquals(33.0D, attraction.maxZ, 0.0D);
    }

    @Test
    public void ownerUuidAndInventoryTransferOnceWhileStayRemainsBehind() throws Exception {
        TestWorld source = new TestWorld();
        TestWorld destination = new TestWorld();
        UUID ownerId = UUID.fromString("00000000-0000-0000-0000-000000000123");
        TestPlayer owner = new TestPlayer(destination, ownerId, "owner");
        owner.setPosition(8.0D, 70.0D, -4.0D);

        EntityTravelingTrunk trunk = new EntityTravelingTrunk(source);
        trunk.setOwnerId(ownerId);
        trunk.setUpgrade(1);
        trunk.setInvSize();
        trunk.inventory.setInventorySlotContents(35, new ItemStack(Items.DIAMOND, 3));
        trunk.setHealth(40.0F);
        trunk.setCustomNameTag("Packrat");

        NBTTagCompound saved = new NBTTagCompound();
        trunk.writeEntityToNBT(saved);
        assertTrue(saved.hasUniqueId("OwnerUUID"));
        assertFalse(saved.hasKey("Owner"));
        EntityTravelingTrunk loaded = new EntityTravelingTrunk(source);
        loaded.readEntityFromNBT(saved);
        assertEquals(ownerId, loaded.getOwnerId());
        assertEquals(3, loaded.inventory.getStackInSlot(35).getCount());

        Method transfer = EntityTravelingTrunk.class.getDeclaredMethod("transferToOwnerWorld", EntityPlayer.class);
        transfer.setAccessible(true);
        assertTrue((Boolean) transfer.invoke(trunk, owner));
        assertTrue(trunk.isDead);
        assertEquals(1, destination.spawnedTrunks.size());
        EntityTravelingTrunk copy = destination.spawnedTrunks.get(0);
        assertEquals(ownerId, copy.getOwnerId());
        assertEquals(1, copy.getUpgrade());
        assertEquals(4, copy.getRows());
        assertEquals(3, copy.inventory.getStackInSlot(35).getCount());
        assertEquals(40.0F, copy.getHealth(), 0.0F);
        assertEquals("Packrat", copy.getCustomNameTag());
        assertFalse((Boolean) transfer.invoke(trunk, owner));
        assertEquals(1, destination.spawnedTrunks.size());

        EntityTravelingTrunk staying = new EntityTravelingTrunk(source);
        staying.setOwnerId(ownerId);
        staying.setStay(true);
        assertFalse((Boolean) transfer.invoke(staying, owner));
        assertFalse(staying.isDead);
        assertEquals(1, destination.spawnedTrunks.size());
    }

    @Test
    public void containerStateAndStayMutationAreServerAuthoritative() {
        TestWorld server = new TestWorld();
        UUID ownerId = UUID.fromString("00000000-0000-0000-0000-000000000456");
        TestPlayer owner = new TestPlayer(server, ownerId, "owner");
        TestPlayer stranger = new TestPlayer(server,
                UUID.fromString("00000000-0000-0000-0000-000000000789"), "stranger");
        EntityTravelingTrunk trunk = new EntityTravelingTrunk(server);
        trunk.setOwnerId(ownerId);
        trunk.setPosition(0.0D, 0.0D, 0.0D);

        ContainerTravelingTrunk strangerContainer = new ContainerTravelingTrunk(stranger.inventory, server, trunk);
        assertTrue(trunk.isOpen());
        assertFalse(strangerContainer.enchantItem(stranger, 1));
        assertFalse(trunk.getStay());
        strangerContainer.onContainerClosed(stranger);
        assertFalse(trunk.isOpen());
        assertTrue(server.sounds.contains(SoundEvents.BLOCK_CHEST_OPEN));
        assertTrue(server.sounds.contains(SoundEvents.BLOCK_CHEST_CLOSE));

        ContainerTravelingTrunk ownerContainer = new ContainerTravelingTrunk(owner.inventory, server, trunk);
        assertTrue(ownerContainer.enchantItem(owner, 1));
        assertTrue(trunk.getStay());

        trunk.setUpgrade(3);
        assertFalse(strangerContainer.canInteractWith(stranger));
        assertTrue(ownerContainer.canInteractWith(owner));

        TestWorld client = new TestWorld(true);
        TestPlayer clientPlayer = new TestPlayer(client, ownerId, "owner");
        EntityTravelingTrunk clientTrunk = new EntityTravelingTrunk(client);
        clientTrunk.setOwnerId(ownerId);
        new ContainerTravelingTrunk(clientPlayer.inventory, client, clientTrunk);
        assertFalse(clientTrunk.isOpen());
        assertTrue(client.sounds.isEmpty());
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

        SoundEvent deathSound() {
            return this.getDeathSound();
        }
    }

    private static final class TestLiving extends EntityLiving {
        TestLiving(World world) {
            super(world);
        }
    }

    private static final class TestPlayer extends EntityPlayer {
        TestPlayer(World world, UUID id, String name) {
            super(world, new GameProfile(id, name));
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

    private static final class TestWorld extends World {
        private int nextEntityId = 8000;
        private final List<SoundEvent> sounds = new ArrayList<>();
        private final List<AxisAlignedBB> entityQueries = new ArrayList<>();
        private final List<EntityTravelingTrunk> spawnedTrunks = new ArrayList<>();

        TestWorld() {
            this(false);
        }

        TestWorld(boolean remote) {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT), "traveling_trunk"),
                    new WorldProviderSurface(), new Profiler(), remote);
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
        public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> type, AxisAlignedBB bounds) {
            this.entityQueries.add(bounds);
            return Collections.emptyList();
        }

        @Override
        public boolean spawnEntity(Entity entity) {
            if (entity instanceof EntityTravelingTrunk) {
                this.spawnedTrunks.add((EntityTravelingTrunk) entity);
            }
            return true;
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
