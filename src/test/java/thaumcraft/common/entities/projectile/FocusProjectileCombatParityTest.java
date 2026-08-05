package thaumcraft.common.entities.projectile;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
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
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class FocusProjectileCombatParityTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void fireballAndEarthShockLaunchBeforeSpawn() throws IOException {
        assertLaunchBeforeSpawn(read("src/main/java/thaumcraft/common/items/wands/foci/FocusFire.java"),
                "EntityExplosiveOrb orb = new EntityExplosiveOrb(world, (EntityLivingBase) player);");
        assertLaunchBeforeSpawn(read("src/main/java/thaumcraft/common/items/wands/foci/FocusShock.java"),
                "EntityShockOrb orb = new EntityShockOrb(world, (EntityLivingBase) player);");
    }

    @Test
    public void explosiveOrbRestoresClientTrail() throws IOException {
        String source = read("src/main/java/thaumcraft/common/entities/projectile/EntityExplosiveOrb.java");

        assertTrue(source.contains("if (this.world.isRemote) {"));
        assertTrue(source.contains("Thaumcraft.proxy.drawGenericParticles(this.world,"));
        assertTrue(source.contains("this.prevPosX + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.3F"));
        assertTrue(source.contains("this.prevPosY + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.3F"));
        assertTrue(source.contains("this.prevPosZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.3F"));
        assertTrue(source.contains("false, 151, 9, 1, 7 + this.rand.nextInt(5), 0, 2.0F + this.rand.nextFloat()"));
    }

    @Test
    public void explosiveOrbExposesThrowerToForgeSpawnProtocol() {
        TestWorld world = new TestWorld();
        RecordingPlayer thrower = new RecordingPlayer(world);
        EntityExplosiveOrb orb = new EntityExplosiveOrb(world, thrower);

        assertTrue(orb instanceof IThrowableEntity);
        IThrowableEntity throwable = orb;
        assertSame(thrower, throwable.getThrower());
        throwable.setThrower(null);
        assertNull(throwable.getThrower());
        throwable.setThrower(thrower);
        assertSame(thrower, throwable.getThrower());
    }

    @Test
    public void initialShockTargetUsesSharedLosAwareHelper() throws IOException {
        String source = read("src/main/java/thaumcraft/common/items/wands/foci/FocusShock.java");

        assertTrue(source.contains(
                "Entity target = EntityUtils.getPointedEntity(player.world, player, 0.0D, 20.0D, 1.1F);"));
        assertFalse(source.contains("private Entity getPointedEntity("));
    }

    @Test
    public void frostEntityImpactUsesThrownProjectileSource() {
        TestWorld world = new TestWorld();
        RecordingPlayer thrower = new RecordingPlayer(world);
        TestFrostShard shard = new TestFrostShard(world, thrower);
        RecordingEntity target = new RecordingEntity(world);
        shard.setDamage(3.0F);
        shard.ticksExisted = 6;

        shard.impact(target);

        assertEquals(3.0F, target.damage, 0.0F);
        assertTrue(target.source.isProjectile());
        assertFalse(target.source.isMagicDamage());
        assertSame(shard, target.source.getImmediateSource());
        assertSame(thrower, target.source.getTrueSource());
    }

    @Test
    public void earthShockDamagesVisibleAllEntityCubeWithIndirectMagic() {
        TestWorld world = new TestWorld();
        RecordingPlayer thrower = new RecordingPlayer(world);
        thrower.setPosition(1.0D, 64.0D, 0.0D);
        TestShockOrb orb = new TestShockOrb(world, thrower);
        orb.setPosition(0.0D, 64.0D, 0.0D);
        orb.area = 4;
        orb.damage = 7;

        RecordingEntity corner = new RecordingEntity(world);
        corner.setPosition(3.5D, 64.0D, 3.5D);
        RecordingEntity blocked = new RecordingEntity(world);
        blocked.setPosition(2.0D, 64.0D, 0.0D);
        RecordingEntity outside = new RecordingEntity(world);
        outside.setPosition(5.0D, 64.0D, 0.0D);
        world.entities.add(orb);
        world.entities.add(thrower);
        world.entities.add(corner);
        world.entities.add(blocked);
        world.entities.add(outside);
        world.blocked = blocked;

        orb.impact();

        assertSame(Entity.class, world.queriedClass);
        assertEquals(-4.0D, world.queriedBounds.minX, 0.0D);
        assertEquals(60.0D, world.queriedBounds.minY, 0.0D);
        assertEquals(-4.0D, world.queriedBounds.minZ, 0.0D);
        assertEquals(4.0D, world.queriedBounds.maxX, 0.0D);
        assertEquals(68.0D, world.queriedBounds.maxY, 0.0D);
        assertEquals(4.0D, world.queriedBounds.maxZ, 0.0D);
        assertEquals(7.0F, corner.damage, 0.0F);
        assertEquals(7.0F, thrower.damage, 0.0F);
        assertNull(blocked.source);
        assertNull(outside.source);
        assertTrue(corner.source.isMagicDamage());
        assertFalse(corner.source.isProjectile());
        assertSame(orb, corner.source.getImmediateSource());
        assertSame(thrower, corner.source.getTrueSource());
        assertTrue(world.sawImpactToCornerRay);
    }

    private static void assertLaunchBeforeSpawn(String source, String construction) {
        int constructed = source.indexOf(construction);
        int launched = source.indexOf(
                "orb.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);",
                constructed);
        int spawned = source.indexOf("world.spawnEntity(orb);", constructed);
        assertTrue(constructed >= 0 && launched > constructed && spawned > launched);
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    private static final class TestFrostShard extends EntityFrostShard {
        private TestFrostShard(World world, RecordingPlayer thrower) {
            super(world, thrower, 1.0F);
        }

        private void impact(Entity target) {
            this.onImpact(new RayTraceResult(target));
        }
    }

    private static final class TestShockOrb extends EntityShockOrb {
        private TestShockOrb(World world, RecordingPlayer thrower) {
            super(world, thrower);
        }

        private void impact() {
            this.onImpact(new RayTraceResult(new Vec3d(this.posX, this.posY, this.posZ),
                    EnumFacing.UP, new BlockPos(this)));
        }
    }

    private static class RecordingEntity extends Entity {
        private DamageSource source;
        private float damage;

        private RecordingEntity(World world) {
            super(world);
        }

        @Override protected void entityInit() { }
        @Override protected void readEntityFromNBT(NBTTagCompound compound) { }
        @Override protected void writeEntityToNBT(NBTTagCompound compound) { }

        @Override
        public boolean attackEntityFrom(DamageSource source, float amount) {
            this.source = source;
            this.damage = amount;
            return true;
        }
    }

    private static final class RecordingPlayer extends EntityPlayer {
        private DamageSource source;
        private float damage;

        private RecordingPlayer(World world) {
            super(world, new GameProfile(UUID.fromString("2b823bae-5310-49b5-8601-833b8ac44e78"),
                    "focus_projectile_test"));
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }

        @Override
        public boolean attackEntityFrom(DamageSource source, float amount) {
            this.source = source;
            this.damage = amount;
            return true;
        }
    }

    private static final class TestWorld extends World {
        private final List<Entity> entities = new ArrayList<>();
        private Class<?> queriedClass;
        private AxisAlignedBB queriedBounds;
        private Entity blocked;
        private boolean sawImpactToCornerRay;

        private TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "focus_projectile_combat"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> type, AxisAlignedBB bounds) {
            this.queriedClass = type;
            this.queriedBounds = bounds;
            List<T> matches = new ArrayList<>();
            for (Entity entity : this.entities) {
                if (type.isInstance(entity) && entity.getEntityBoundingBox().intersects(bounds)) {
                    matches.add(type.cast(entity));
                }
            }
            return matches;
        }

        @Override
        public RayTraceResult rayTraceBlocks(Vec3d start, Vec3d end, boolean stopOnLiquid,
                                             boolean ignoreBlockWithoutBoundingBox,
                                             boolean returnLastUncollidableBlock) {
            if (end.x == 3.5D && end.y == 64.0D && end.z == 3.5D) {
                this.sawImpactToCornerRay = start.x == 0.0D && start.y == 64.0D && start.z == 0.0D;
            }
            if (this.blocked != null && end.x == this.blocked.posX
                    && end.y == this.blocked.posY && end.z == this.blocked.posZ) {
                return new RayTraceResult(end, EnumFacing.UP, BlockPos.ORIGIN);
            }
            return null;
        }

        @Override
        public boolean isAirBlock(BlockPos pos) {
            return false;
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            return Blocks.STONE.getDefaultState();
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
                @Override public String makeString() { return "focus_projectile_combat_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
