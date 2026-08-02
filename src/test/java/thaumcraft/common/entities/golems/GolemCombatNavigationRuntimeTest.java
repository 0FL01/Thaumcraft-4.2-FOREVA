package thaumcraft.common.entities.golems;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.math.AxisAlignedBB;
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
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.entities.ai.combat.AIDartAttack;
import thaumcraft.common.entities.ai.combat.AINearestButcherTarget;
import thaumcraft.common.entities.ai.combat.AITarget;
import thaumcraft.common.entities.ai.misc.AIOpenDoor;
import thaumcraft.common.entities.projectile.EntityDart;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class GolemCombatNavigationRuntimeTest {
    private static final BlockPos HOME = new BlockPos(0, 64, 0);
    private static int nextEntityId = 7000;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void visorSetsRecentlyHitWithoutExtendingDamageImmunity() {
        TestWorld world = new TestWorld();
        EntityGolemBase golem = golem(world, EnumGolemType.WOOD, 4);
        golem.decoration = "V";
        golem.setupGolem();
        EntityZombie target = place(new EntityZombie(world), HOME.east(2));
        target.setHealth(target.getMaxHealth());

        assertTrue(golem.attackEntityAsMob(target));
        float healthAfterFirstHit = target.getHealth();
        assertEquals(target.maxHurtResistantTime, target.hurtResistantTime);
        assertEquals(100, recentlyHit(target));

        for (int tick = 0; tick < golem.getAttackSpeed(); tick++) {
            target.onEntityUpdate();
        }

        assertTrue("equal damage must land again at the normal golem cooldown",
                golem.attackEntityAsMob(target));
        assertTrue(target.getHealth() < healthAfterFirstHit);
        assertEquals("Visor keeps the vanilla XP/recent-player eligibility window", 100, recentlyHit(target));

        TestZombie xpTarget = place(new TestZombie(world), HOME.south(2));
        xpTarget.setHealth(1.0F);
        assertTrue(golem.attackEntityAsMob(xpTarget));
        for (int tick = 0; tick < 20; tick++) xpTarget.tickDeath();
        boolean spawnedXp = false;
        for (Entity spawned : world.spawnedEntities) {
            if (spawned instanceof EntityXPOrb) spawnedXp = true;
        }
        assertTrue("recentlyHit must make golem kills XP-eligible", spawnedXp);
    }

    @Test
    public void dartsUseLeadTrajectoryAndYieldToMeleeInsideThreeBlocks() throws Exception {
        TestWorld world = new TestWorld();
        EntityGolemBase golem = golem(world, EnumGolemType.THAUMIUM, 4);
        golem.setUpgrade(0, (byte) 3);
        golem.setUpgrade(1, (byte) 3);
        golem.setupGolem();
        EntityZombie target = place(new EntityZombie(world), HOME.east(10));

        golem.attackEntityWithRangedAttack(target);

        assertEquals(1, world.spawnedEntities.size());
        EntityDart dart = (EntityDart) world.spawnedEntities.get(0);
        assertTrue("target-aware darts start in front of the shooter", dart.posX > golem.posX + 0.15D);
        assertTrue("the TC4 lead term lifts a level ten-block shot", dart.motionY > 0.0D);

        golem.setAttackTarget(target);
        golem.getNavigator().setPath(new Path(new PathPoint[]{
                new PathPoint(HOME.getX() + 1, HOME.getY(), HOME.getZ()),
                new PathPoint(HOME.getX() + 10, HOME.getY(), HOME.getZ())
        }), golem.getAIMoveSpeed());
        AIDartAttack attack = new AIDartAttack(golem);
        assertTrue(attack.shouldExecute());
        assertTrue(attack.shouldContinueExecuting());

        target.setPosition(golem.posX + 2.0D, golem.posY, golem.posZ);
        assertFalse("ranged AI must stop immediately so melee can acquire the mutex", attack.shouldContinueExecuting());

        EntityGolemBase aerGolem = golem(new TestWorld(), EnumGolemType.THAUMIUM, 4);
        aerGolem.setUpgrade(0, (byte) 0);
        aerGolem.setUpgrade(1, (byte) 0);
        Field cooldown = AIDartAttack.class.getDeclaredField("maxRangedAttackTime");
        cooldown.setAccessible(true);
        assertEquals(14, cooldown.getInt(new AIDartAttack(aerGolem)));
    }

    @Test
    public void perditioRetaliatesAgainstImmediateSourceBeforeFireImmunity() {
        TestWorld world = new TestWorld();
        EntityGolemBase golem = golem(world, EnumGolemType.THAUMIUM, 4);
        golem.setUpgrade(0, (byte) 5);
        golem.setUpgrade(1, (byte) 5);
        golem.setupGolem();
        RecordingEntity projectile = new RecordingEntity(world);
        RecordingEntity shooter = new RecordingEntity(world);
        projectile.setEntityId(nextEntityId++);
        shooter.setEntityId(nextEntityId++);

        long seed = 73L;
        int expected = 4 + new Random(seed).nextInt(4);
        golem.getRNG().setSeed(seed);
        DamageSource fireProjectile = new EntityDamageSourceIndirect("r7_fire", projectile, shooter).setFireDamage();

        assertFalse("Thaumium still rejects the incoming fire damage",
                golem.attackEntityFrom(fireProjectile, 6.0F));
        assertEquals(expected, projectile.lastDamage, 0.0F);
        assertEquals("thorns", projectile.lastSource.getDamageType());
        assertEquals(0.0F, shooter.lastDamage, 0.0F);

        golem.attackEntityFrom(DamageSource.GENERIC, 0.0F);
        assertTrue("the two-upgrade bound is [4, 7]",
                projectile.lastDamage >= 4.0F && projectile.lastDamage <= 7.0F);
    }

    @Test
    public void targetSafetyPreservesOwnerTamingAndButcherPopulationRules() {
        TestWorld world = new TestWorld();
        EntityGolemBase guard = golem(world, EnumGolemType.WOOD, 4);
        guard.setOwner("owner");
        ExposedTargetAI guardTarget = new ExposedTargetAI(guard);
        EntityVillager villager = place(new EntityVillager(world), HOME.east());
        EntityBat bat = place(new EntityBat(world), HOME.south());
        EntityGolemBase peer = golem(world, EnumGolemType.WOOD, -1);
        TestPlayer owner = place(new TestPlayer(world, "owner"), HOME.west());
        EntityCow cow = place(new EntityCow(world), HOME.north());

        assertFalse(guard.canAttackClass(EntityVillager.class));
        assertFalse(guard.canAttackClass(EntityGolemBase.class));
        assertFalse(guard.canAttackClass(EntityBat.class));
        assertFalse(guardTarget.suitable(villager));
        assertFalse(guardTarget.suitable(peer));
        assertFalse(guardTarget.suitable(bat));
        assertFalse(guardTarget.suitable(owner));
        assertTrue(guardTarget.suitable(cow));

        TestPlayer wolfOwner = place(new TestPlayer(world, "wolf_owner"), HOME.west(2));
        world.addEntity(wolfOwner);
        EntityWolf tameTaskOwner = place(new EntityWolf(world), HOME);
        tameTaskOwner.setTamed(true);
        tameTaskOwner.setOwnerId(wolfOwner.getUniqueID());
        EntityWolf otherTame = place(new EntityWolf(world), HOME.east());
        otherTame.setTamed(true);
        ExposedTargetAI tameTarget = new ExposedTargetAI(tameTaskOwner);
        assertFalse(tameTarget.suitable(wolfOwner));
        assertFalse(tameTarget.suitable(otherTame));
        assertTrue(tameTarget.suitable(cow));

        EntityGolemBase butcher = golem(world, EnumGolemType.WOOD, 9);
        EntityCow oldestAdult = place(new EntityCow(world), HOME.east(2));
        oldestAdult.ticksExisted = 100;
        EntityCow secondAdult = place(new EntityCow(world), HOME.south(2));
        secondAdult.ticksExisted = 50;
        EntityCow child = place(new EntityCow(world), HOME.west(2));
        child.setGrowingAge(-1000);
        child.ticksExisted = 200;
        world.addEntity(oldestAdult);
        world.addEntity(secondAdult);
        world.addEntity(child);
        AINearestButcherTarget butcherTarget = new AINearestButcherTarget(butcher);

        assertFalse("two adults leave no pair after butchering", butcherTarget.shouldExecute());
        EntityCow thirdAdult = place(new EntityCow(world), HOME.north(2));
        thirdAdult.ticksExisted = 25;
        world.addEntity(thirdAdult);
        assertTrue("three adults permit the oldest to be butchered", butcherTarget.shouldExecute());
        butcherTarget.startExecuting();
        assertSame(oldestAdult, butcher.getAttackTarget());
    }

    @Test
    public void aquaRangeUsesTheBlockPosOverrideAndStrictDynamicBoundary() {
        TestWorld world = new TestWorld();
        EntityGolemBase golem = golem(world, EnumGolemType.WOOD, 4);

        assertFalse(golem.isWithinHomeDistanceFromPosition(HOME.east(16)));
        golem.setUpgrade(0, (byte) 3);
        assertTrue(golem.isWithinHomeDistanceFromPosition(HOME.east(16)));
        assertFalse(golem.isWithinHomeDistanceFromPosition(HOME.east(20)));
    }

    @Test
    public void everyCoreRegistersDoorAiAndDoorAndGateCloseAfterTwentyTicks() {
        for (int core = 0; core <= 11; core++) {
            EntityGolemBase golem = golem(new TestWorld(), EnumGolemType.WOOD, core);
            boolean found = false;
            for (EntityAITasks.EntityAITaskEntry entry : golem.tasks.taskEntries) {
                if (entry.action instanceof AIOpenDoor) found = true;
            }
            assertTrue("core " + core + " must retain universal door AI", found);
        }

        assertDoorLifecycle(Blocks.OAK_DOOR.getDefaultState()
                .withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
                .withProperty(BlockDoor.OPEN, false), BlockDoor.OPEN);
        assertDoorLifecycle(Blocks.OAK_FENCE_GATE.getDefaultState()
                .withProperty(BlockFenceGate.OPEN, false), BlockFenceGate.OPEN);
    }

    @Test
    public void onlyStoneIronAndThaumiumUseHeavyWaterPolicyAndDoubleSpeed() {
        for (EnumGolemType type : EnumGolemType.values()) {
            WaterStateGolem golem = new WaterStateGolem(new TestWorld(), type);
            golem.setEntityId(nextEntityId++);
            golem.setupGolem();
            boolean heavy = type == EnumGolemType.STONE
                    || type == EnumGolemType.IRON
                    || type == EnumGolemType.THAUMIUM;
            PathNavigateGround navigator = (PathNavigateGround) golem.getNavigator();

            assertEquals(type.name(), heavy, navigator.getCanSwim());
            assertEquals(type.name(), heavy ? 0.0F : PathNodeType.WATER.getPriority(),
                    golem.getPathPriority(PathNodeType.WATER), 0.0F);
            assertTrue(type.name(), navigator.getNodeProcessor().getCanOpenDoors());

            golem.submerged = false;
            float drySpeed = golem.getAIMoveSpeed();
            golem.submerged = true;
            assertEquals(type.name(), heavy ? drySpeed * 2.0F : drySpeed,
                    golem.getAIMoveSpeed(), 0.000001F);
        }
    }

    private static void assertDoorLifecycle(IBlockState closedState,
                                            net.minecraft.block.properties.PropertyBool openProperty) {
        TestWorld world = new TestWorld();
        BlockPos doorPos = HOME.east();
        world.putBlock(doorPos, closedState);
        EntityGolemBase golem = golem(world, EnumGolemType.WOOD, 0);
        golem.collidedHorizontally = true;
        golem.getNavigator().setPath(new Path(new PathPoint[]{
                new PathPoint(doorPos.getX(), doorPos.getY(), doorPos.getZ()),
                new PathPoint(doorPos.getX() + 1, doorPos.getY(), doorPos.getZ())
        }), golem.getAIMoveSpeed());
        AIOpenDoor doorAI = new AIOpenDoor(golem);

        assertTrue(doorAI.shouldExecute());
        doorAI.startExecuting();
        assertTrue(world.getBlockState(doorPos).getValue(openProperty));
        for (int tick = 0; tick < 19; tick++) doorAI.updateTask();
        assertTrue(doorAI.shouldContinueExecuting());
        doorAI.updateTask();
        assertFalse(doorAI.shouldContinueExecuting());
        doorAI.resetTask();
        assertFalse(world.getBlockState(doorPos).getValue(openProperty));
    }

    private static int recentlyHit(EntityLivingBase entity) {
        Integer value = ObfuscationReflectionHelper.getPrivateValue(
                EntityLivingBase.class, entity, "recentlyHit", "field_70718_bc");
        return value == null ? -1 : value;
    }

    private static EntityGolemBase golem(TestWorld world, EnumGolemType type, int core) {
        EntityGolemBase golem = new EntityGolemBase(world, type, false);
        golem.setEntityId(nextEntityId++);
        golem.setHomePosAndDistance(HOME, 32);
        golem.setPosition(HOME.getX() + 0.5D, HOME.getY(), HOME.getZ() + 0.5D);
        golem.setCore((byte) core);
        golem.setupGolem();
        return golem;
    }

    private static <T extends Entity> T place(T entity, BlockPos pos) {
        entity.setEntityId(nextEntityId++);
        entity.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
        return entity;
    }

    private static final class ExposedTargetAI extends AITarget {
        private ExposedTargetAI(EntityCreature owner) {
            super(owner, 16.0F, false);
        }

        @Override
        public boolean shouldExecute() {
            return false;
        }

        private boolean suitable(EntityLivingBase target) {
            return this.isSuitableTarget(target, false);
        }
    }

    private static final class WaterStateGolem extends EntityGolemBase {
        private boolean submerged;

        private WaterStateGolem(World world, EnumGolemType type) {
            super(world, type, false);
        }

        @Override
        public boolean isInWater() {
            return this.submerged;
        }
    }

    private static final class RecordingEntity extends Entity {
        private DamageSource lastSource;
        private float lastDamage;

        private RecordingEntity(World worldIn) {
            super(worldIn);
        }

        @Override
        public boolean attackEntityFrom(DamageSource source, float amount) {
            this.lastSource = source;
            this.lastDamage = amount;
            return true;
        }

        @Override protected void entityInit() { }
        @Override protected void readEntityFromNBT(NBTTagCompound compound) { }
        @Override protected void writeEntityToNBT(NBTTagCompound compound) { }
    }

    private static final class TestZombie extends EntityZombie {
        private TestZombie(World worldIn) {
            super(worldIn);
        }

        private void tickDeath() {
            super.onDeathUpdate();
        }

        @Override
        protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
        }
    }

    private static final class TestPlayer extends EntityPlayer {
        private TestPlayer(World world, String name) {
            super(world, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)), name));
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
    }

    private static final class TestWorld extends World {
        private final Map<BlockPos, IBlockState> blocks = new HashMap<>();
        private final List<Entity> entities = new ArrayList<>();
        private final List<Entity> spawnedEntities = new ArrayList<>();

        private TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "r7_golem_combat_navigation"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        private void putBlock(BlockPos pos, IBlockState state) {
            this.blocks.put(pos.toImmutable(), state);
        }

        private void addEntity(Entity entity) {
            this.entities.add(entity);
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            IBlockState state = this.blocks.get(pos);
            return state == null ? Blocks.AIR.getDefaultState() : state;
        }

        @Override
        public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
            this.blocks.put(pos.toImmutable(), newState);
            return true;
        }

        @Override
        public boolean spawnEntity(Entity entityIn) {
            this.spawnedEntities.add(entityIn);
            return true;
        }

        @Override
        public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> entityClass,
                                                                 AxisAlignedBB aabb) {
            List<T> matches = new ArrayList<>();
            for (Entity entity : this.entities) {
                if (entityClass.isInstance(entity) && entity.getEntityBoundingBox().intersects(aabb)) {
                    matches.add(entityClass.cast(entity));
                }
            }
            return matches;
        }

        @Override
        public EntityPlayer getPlayerEntityByUUID(UUID uuid) {
            for (Entity entity : this.entities) {
                if (entity instanceof EntityPlayer && entity.getUniqueID().equals(uuid)) {
                    return (EntityPlayer) entity;
                }
            }
            return null;
        }

        @Override public Biome getBiome(BlockPos pos) { return Biomes.PLAINS; }
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
                @Override public String makeString() { return "r7_golem_combat_navigation_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
