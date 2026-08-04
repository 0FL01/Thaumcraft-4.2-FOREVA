package thaumcraft.common.items.equipment;

import com.google.common.base.Predicate;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.DamageSource;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ItemElementalSwordRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void nearbyTargetsAreHitWithoutReenteringTheSwordCallback() {
        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world);
        RecordingZombie primary = new RecordingZombie(world);
        RecordingZombie secondary = new RecordingZombie(world);
        RecordingZombie tertiary = new RecordingZombie(world);
        place(player, 0.0D);
        place(primary, 1.0D);
        place(secondary, 1.5D);
        place(tertiary, 2.0D);
        world.addEntity(player);
        world.addEntity(primary);
        world.addEntity(secondary);
        world.addEntity(tertiary);

        ItemElementalSword sword = new TestElementalSword();
        ItemStack stack = new ItemStack(sword);
        player.setHeldItem(net.minecraft.util.EnumHand.MAIN_HAND, stack);
        player.chargeAttack();
        player.onGround = false;

        player.attackTargetEntityWithCurrentItem(primary);

        assertEquals(1, primary.hitCount);
        assertEquals(1, secondary.hitCount);
        assertEquals(1, tertiary.hitCount);
        assertTrue(primary.getHealth() < primary.getMaxHealth());
        assertTrue(secondary.getHealth() < secondary.getMaxHealth());
        assertTrue(tertiary.getHealth() < tertiary.getMaxHealth());
        assertEquals(primary.getHealth(), secondary.getHealth(), 0.0F);
        assertEquals(primary.getHealth(), tertiary.getHealth(), 0.0F);
        assertEquals(3, stack.getItemDamage());
    }

    @Test
    public void secondarySweepNeverHitsAnotherPlayer() {
        TestWorld world = new TestWorld();
        TestPlayer attacker = new TestPlayer(world);
        TestPlayer bystander = new TestPlayer(world);
        RecordingZombie primary = new RecordingZombie(world);
        place(attacker, 0.0D);
        place(primary, 1.0D);
        place(bystander, 1.5D);
        world.addEntity(attacker);
        world.addEntity(primary);
        world.addEntity(bystander);

        ItemElementalSword sword = new TestElementalSword();
        ItemStack stack = new ItemStack(sword);
        attacker.setHeldItem(net.minecraft.util.EnumHand.MAIN_HAND, stack);
        float health = bystander.getHealth();

        sword.onLeftClickEntity(stack, attacker, primary);

        assertEquals(health, bystander.getHealth(), 0.0F);
    }

    private static void place(Entity entity, double x) {
        entity.setPosition(x, 64.0D, 0.0D);
    }

    private static final class RecordingZombie extends EntityZombie {
        private int hitCount;

        private RecordingZombie(World worldIn) {
            super(worldIn);
        }

        @Override
        public boolean attackEntityFrom(DamageSource source, float amount) {
            this.hitCount++;
            return super.attackEntityFrom(source, amount);
        }
    }

    private static final class TestElementalSword extends ItemElementalSword {
        private TestElementalSword() {
            super(Item.ToolMaterial.IRON);
        }

        @Override
        public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
            stack.damageItem(1, attacker);
            return false;
        }
    }

    private static final class TestPlayer extends EntityPlayer {
        private TestPlayer(World world) {
            super(world, new GameProfile(
                    UUID.nameUUIDFromBytes("elemental_sword".getBytes(StandardCharsets.UTF_8)),
                    "elemental_sword"));
        }

        private void chargeAttack() {
            this.ticksSinceLastSwing = 100;
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
    }

    private static final class TestWorld extends World {
        private final List<Entity> entities = new ArrayList<>();

        private TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "elemental_sword"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        private void addEntity(Entity entity) {
            this.entities.add(entity);
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            return Blocks.AIR.getDefaultState();
        }

        @Override
        public List<Entity> getEntitiesInAABBexcluding(Entity excluded, AxisAlignedBB bounds,
                                                        Predicate<? super Entity> predicate) {
            List<Entity> matches = new ArrayList<>();
            for (Entity entity : this.entities) {
                if (entity != excluded
                        && entity.getEntityBoundingBox().intersects(bounds)
                        && (predicate == null || predicate.apply(entity))) {
                    matches.add(entity);
                }
            }
            return matches;
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
                @Override public String makeString() { return "elemental_sword_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
