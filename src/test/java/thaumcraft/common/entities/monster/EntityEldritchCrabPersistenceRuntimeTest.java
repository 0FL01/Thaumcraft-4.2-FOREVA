package thaumcraft.common.entities.monster;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class EntityEldritchCrabPersistenceRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void mountedCrabRemainsTargetableByItsRider() {
        EntityEldritchCrab crab = new EntityEldritchCrab(new TestWorld());

        assertTrue(crab.canRiderInteract());
    }

    @Test
    public void recentlyHitCrabDropsExactlyOneVanillaEnderPearl() {
        TestWorld world = new TestWorld();
        TestCrab crab = new TestCrab(world);
        crab.setDropRandomSeed(0L);

        crab.dropFewItemsForTest(true, 0);

        assertEquals(1, world.drops.size());
        ItemStack drop = world.drops.get(0);
        assertSame(Items.ENDER_PEARL, drop.getItem());
        assertEquals(1, drop.getCount());
    }

    @Test
    public void crabDropsNothingWithoutRecentPlayerHit() {
        TestWorld world = new TestWorld();
        TestCrab crab = new TestCrab(world);
        crab.setDropRandomSeed(0L);

        crab.dropFewItemsForTest(false, 3);

        assertTrue(world.drops.isEmpty());
    }

    @Test
    public void attachedCrabSnapshotSurvivesPlayerNbtRoundTrip() {
        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world, "crab_player");
        EntityEldritchCrab crab = new EntityEldritchCrab(world);
        crab.setPosition(4.0D, 65.5D, 8.0D);
        crab.setHelm(true);
        crab.setHealth(8.5F);
        crab.setCustomNameTag("saved_crab");

        assertTrue(crab.startRiding(player, true));
        EntityEldritchCrab.preserveAttachedCrab(player);
        NBTTagCompound persisted = player.getEntityData()
                .getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        assertTrue(persisted.hasKey(EntityEldritchCrab.ATTACHED_CRAB_TAG, 10));

        NBTTagCompound snapshot = persisted.getCompoundTag(EntityEldritchCrab.ATTACHED_CRAB_TAG);
        assertTrue(snapshot.hasKey("Entity", 10));
        assertTrue(snapshot.hasKey("AttackTime", 3));

        TestPlayer loadedPlayer = new TestPlayer(world, "crab_player");
        loadedPlayer.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted.copy());

        EntityEldritchCrab restored = EntityEldritchCrab.loadAttachedCrabSnapshot(loadedPlayer);
        assertNotNull(restored);
        assertEquals(crab.getUniqueID(), restored.getUniqueID());
        assertEquals(8.5F, restored.getHealth(), 0.0F);
        assertTrue(restored.hasHelm());
        assertEquals("saved_crab", restored.getCustomNameTag());
        assertFalse(restored.isRiding());
    }

    @Test
    public void preservationWithoutCrabKeepsPendingRestoreSnapshot() {
        TestPlayer player = new TestPlayer(new TestWorld(), "stale_crab_player");
        NBTTagCompound persisted = new NBTTagCompound();
        persisted.setTag(EntityEldritchCrab.ATTACHED_CRAB_TAG, new NBTTagCompound());
        player.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);

        EntityEldritchCrab.preserveAttachedCrab(player);

        assertTrue(player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG)
                .hasKey(EntityEldritchCrab.ATTACHED_CRAB_TAG));
    }

    private static final class TestPlayer extends EntityPlayer {
        private TestPlayer(World world, String name) {
            super(world, new GameProfile(UUID.nameUUIDFromBytes(
                    name.getBytes(StandardCharsets.UTF_8)), name));
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
    }

    private static final class TestCrab extends EntityEldritchCrab {
        private TestCrab(World world) {
            super(world);
        }

        private void setDropRandomSeed(long seed) {
            this.rand.setSeed(seed);
        }

        private void dropFewItemsForTest(boolean wasRecentlyHit, int looting) {
            this.dropFewItems(wasRecentlyHit, looting);
        }
    }

    private static final class TestWorld extends World {
        private final List<ItemStack> drops = new ArrayList<>();

        private TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "eldritch_crab_persistence"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        public boolean spawnEntity(Entity entity) {
            if (entity instanceof EntityItem) {
                this.drops.add(((EntityItem) entity).getItem().copy());
            }
            return true;
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "eldritch_crab_persistence_dummy"; }
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
