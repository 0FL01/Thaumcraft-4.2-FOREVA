package thaumcraft.common.entities.monster;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.DamageSource;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemResource;
import thaumcraft.common.items.ItemZombieBrain;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class EntityDropParityRuntimeTest {
    private ItemResource oldResource;
    private ItemZombieBrain oldZombieBrain;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Before
    public void installDropItems() {
        this.oldResource = ConfigItems.itemResource;
        this.oldZombieBrain = ConfigItems.itemZombieBrain;
        ConfigItems.itemResource = new ItemResource();
        ConfigItems.itemZombieBrain = new ItemZombieBrain();
    }

    @After
    public void restoreDropItems() {
        ConfigItems.itemResource = this.oldResource;
        ConfigItems.itemZombieBrain = this.oldZombieBrain;
    }

    @Test
    public void brainyZombieEffectiveLootUsesCustomDropsInsteadOfZombieTable() {
        TestWorld world = new TestWorld(new ScriptedRandom(new int[]{0}, new boolean[]{true, false, true}));
        TestBrainyZombie zombie = new TestBrainyZombie(world);

        zombie.dropLootForTest(false, 0);

        assertEquals(2, count(world, Items.ROTTEN_FLESH, 0));
        assertEquals(1, count(world, ConfigItems.itemZombieBrain, 0));
    }

    @Test
    public void brainyZombieRestoresRareZombieRewardAndChildDropGuard() {
        TestWorld adultWorld = new TestWorld(new ScriptedRandom(new int[]{9}, new boolean[]{false, false, false}));
        TestBrainyZombie adult = new TestBrainyZombie(adultWorld);
        setEntityRandom(adult, new ScriptedRandom(new int[]{0, 2}, new boolean[0]));

        adult.dropLootForTest(true, 0);

        assertEquals(1, count(adultWorld, Items.POTATO, 0));

        TestWorld childWorld = new TestWorld(new ScriptedRandom(new int[]{0}, new boolean[]{true, true, true}));
        TestBrainyZombie child = new TestBrainyZombie(childWorld);
        child.setChild(true);
        child.dropLootForTest(true, 10);

        assertTrue(childWorld.drops.isEmpty());
    }

    @Test
    public void giantBrainyZombieRestoresFourWayRareDropAndBrainOffset() {
        TestWorld rareWorld = new TestWorld(new ScriptedRandom(new int[]{9}, twelveFalse()));
        TestGiantBrainyZombie rareZombie = new TestGiantBrainyZombie(rareWorld);
        setEntityRandom(rareZombie, new ScriptedRandom(new int[]{0, 3}, new boolean[0]));

        rareZombie.dropLootForTest(true, 0);

        assertEquals(1, count(rareWorld, ConfigItems.itemResource, ItemResource.META_AMBER));

        TestWorld brainWorld = new TestWorld(new ScriptedRandom(new int[]{0}, twelveFalse()));
        TestGiantBrainyZombie brainZombie = new TestGiantBrainyZombie(brainWorld);
        brainZombie.setPosition(0.0D, 10.0D, 0.0D);
        brainZombie.dropLootForTest(false, 0);

        assertEquals(1, brainWorld.drops.size());
        assertSame(ConfigItems.itemZombieBrain, brainWorld.drops.get(0).getItem().getItem());
        assertEquals(12.0D, brainWorld.drops.get(0).posY, 0.0D);
    }

    @Test
    public void taintSpiderEffectiveLootUsesTaintResourcePath() {
        TestWorld world = new TestWorld(new ScriptedRandom(new int[]{0}, new boolean[]{true}));
        TestTaintSpider spider = new TestTaintSpider(world);

        spider.dropLootForTest(false, 0);

        assertEquals(1, count(world, ConfigItems.itemResource, ItemResource.META_TAINT_SLIME));
        assertEquals(1, world.drops.size());
    }

    @Test
    public void fireBatUsesInheritedGunpowderCountAndSummonedSuppression() {
        TestWorld normalWorld = new TestWorld(new ScriptedRandom(new int[0], new boolean[0]));
        TestFireBat normal = new TestFireBat(normalWorld);
        setEntityRandom(normal, new ScriptedRandom(new int[]{2, 2}, new boolean[0]));

        normal.dropLootForTest(false, 2);

        assertEquals(4, count(normalWorld, Items.GUNPOWDER, 0));

        TestWorld summonedWorld = new TestWorld(new ScriptedRandom(new int[0], new boolean[0]));
        TestFireBat summoned = new TestFireBat(summonedWorld);
        summoned.setIsSummoned(true);
        setEntityRandom(summoned, new ScriptedRandom(new int[]{2, 2}, new boolean[0]));
        summoned.dropLootForTest(false, 2);

        assertTrue(summonedWorld.drops.isEmpty());
    }

    @Test
    public void taintPigKeepsOuterNoDropGateAndBothResourceOutcomes() {
        TestWorld noDropWorld = new TestWorld(new ScriptedRandom(new int[]{1}, new boolean[0]));
        new TestTaintPig(noDropWorld).dropLootForTest(false, 0);
        assertTrue(noDropWorld.drops.isEmpty());

        TestWorld slimeWorld = new TestWorld(new ScriptedRandom(new int[]{0}, new boolean[]{true}));
        new TestTaintPig(slimeWorld).dropLootForTest(false, 0);
        assertEquals(1, count(slimeWorld, ConfigItems.itemResource, ItemResource.META_TAINT_SLIME));

        TestWorld tendrilWorld = new TestWorld(new ScriptedRandom(new int[]{0}, new boolean[]{false}));
        new TestTaintPig(tendrilWorld).dropLootForTest(false, 0);
        assertEquals(1, count(tendrilWorld, ConfigItems.itemResource, ItemResource.META_TAINT_TENDRIL));
    }

    private static int count(TestWorld world, Item item, int metadata) {
        int total = 0;
        for (EntityItem drop : world.drops) {
            if (drop.getItem().getItem() == item && drop.getItem().getMetadata() == metadata) {
                total += drop.getItem().getCount();
            }
        }
        return total;
    }

    private static boolean[] twelveFalse() {
        return new boolean[12];
    }

    private static void setEntityRandom(Entity entity, Random random) {
        setRandomField(Entity.class, entity, random);
    }

    private static void setRandomField(Class<?> owner, Object target, Random random) {
        try {
            Field field = owner.getDeclaredField("rand");
            field.setAccessible(true);
            field.set(target, random);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static final class TestBrainyZombie extends EntityBrainyZombie {
        private TestBrainyZombie(World world) {
            super(world);
        }

        private void dropLootForTest(boolean wasRecentlyHit, int looting) {
            this.dropLoot(wasRecentlyHit, looting, DamageSource.GENERIC);
        }
    }

    private static final class TestGiantBrainyZombie extends EntityGiantBrainyZombie {
        private TestGiantBrainyZombie(World world) {
            super(world);
        }

        private void dropLootForTest(boolean wasRecentlyHit, int looting) {
            this.dropLoot(wasRecentlyHit, looting, DamageSource.GENERIC);
        }
    }

    private static final class TestTaintSpider extends EntityTaintSpider {
        private TestTaintSpider(World world) {
            super(world);
        }

        private void dropLootForTest(boolean wasRecentlyHit, int looting) {
            this.dropLoot(wasRecentlyHit, looting, DamageSource.GENERIC);
        }
    }

    private static final class TestFireBat extends EntityFireBat {
        private TestFireBat(World world) {
            super(world);
        }

        private void dropLootForTest(boolean wasRecentlyHit, int looting) {
            this.dropLoot(wasRecentlyHit, looting, DamageSource.GENERIC);
        }
    }

    private static final class TestTaintPig extends EntityTaintPig {
        private TestTaintPig(World world) {
            super(world);
        }

        private void dropLootForTest(boolean wasRecentlyHit, int looting) {
            this.dropLoot(wasRecentlyHit, looting, DamageSource.GENERIC);
        }
    }

    private static final class ScriptedRandom extends Random {
        private final int[] ints;
        private final boolean[] booleans;
        private int intIndex;
        private int booleanIndex;

        private ScriptedRandom(int[] ints, boolean[] booleans) {
            this.ints = ints;
            this.booleans = booleans;
        }

        @Override
        public int nextInt(int bound) {
            int value = this.intIndex < this.ints.length ? this.ints[this.intIndex++] : 0;
            return Math.floorMod(value, bound);
        }

        @Override
        public boolean nextBoolean() {
            return this.booleanIndex < this.booleans.length && this.booleans[this.booleanIndex++];
        }
    }

    private static final class TestWorld extends World {
        private final List<EntityItem> drops = new ArrayList<>();

        private TestWorld(Random random) {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "entity_drop_parity"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            setRandomField(World.class, this, random);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        public boolean spawnEntity(Entity entity) {
            if (entity instanceof EntityItem) {
                this.drops.add((EntityItem) entity);
            }
            return true;
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "entity_drop_parity_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }

        @Override
        public IBlockState getBlockState(net.minecraft.util.math.BlockPos pos) {
            return Blocks.AIR.getDefaultState();
        }
    }
}
