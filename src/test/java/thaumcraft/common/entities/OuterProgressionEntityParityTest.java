package thaumcraft.common.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.lib.utils.EntityUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class OuterProgressionEntityParityTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void specialBossDropUsesExplosionImmuneEntityWithReferenceMotion() {
        TestWorld world = new TestWorld(0);
        TestEntity source = new TestEntity(world);
        source.setPosition(4.0D, 60.0D, 8.0D);

        net.minecraft.entity.item.EntityItem dropped = EntityUtils.entityDropSpecialItem(
                source, new ItemStack(Items.DIAMOND), 1.5F);

        assertTrue(dropped instanceof EntitySpecialItem);
        assertSame(dropped, world.spawned.get(0));
        assertEquals(0.0D, dropped.motionX, 0.0D);
        assertEquals(0.1D, dropped.motionY, 0.0D);
        assertEquals(0.0D, dropped.motionZ, 0.0D);
        assertFalse(dropped.attackEntityFrom(new DamageSource("test_explosion").setExplosion(), 10.0F));
    }

    @Test
    public void outerGuardianUsesAbsorptionWardWithoutChangingBaseHealth() {
        TestWorld world = new TestWorld(Config.dimensionOuterId);
        EntityEldritchGuardian guardian = new EntityEldritchGuardian(world);
        guardian.setPosition(0.0D, 64.0D, 0.0D);

        guardian.onInitialSpawn(new DifficultyInstance(EnumDifficulty.NORMAL, 0L, 0L, 0.0F), null);

        assertEquals(50.0D,
                guardian.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue(), 0.0D);
        assertEquals(25.0F, guardian.getAbsorptionAmount(), 0.0F);
    }

    private static final class TestEntity extends Entity {
        private TestEntity(World world) { super(world); }
        @Override protected void entityInit() { }
        @Override protected void readEntityFromNBT(NBTTagCompound compound) { }
        @Override protected void writeEntityToNBT(NBTTagCompound compound) { }
    }

    private static final class TestWorld extends World {
        private final List<Entity> spawned = new ArrayList<>();

        private TestWorld(int dimension) {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT), "outer_entities"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.provider.setDimension(dimension);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        public boolean spawnEntity(Entity entity) {
            this.spawned.add(entity);
            return true;
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "outer_entities_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
