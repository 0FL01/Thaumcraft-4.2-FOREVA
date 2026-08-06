package thaumcraft.common.items;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.capabilities.Capability;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeCapability;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FoodConsumableParityRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void tripleMeatTreatKeepsTc4FoodAndProbabilisticRegeneration() throws Exception {
        ItemTripleMeatTreat item = new ItemTripleMeatTreat();
        ItemStack stack = new ItemStack(item);

        assertEquals(6, item.getHealAmount(stack));
        assertEquals(0.8F, item.getSaturationModifier(stack), 0.0F);
        assertTrue(item.isWolfsFavoriteMeat());
        assertEquals(64, item.getItemStackLimit());
        assertTrue((Boolean) field(item, "alwaysEdible"));
        assertPotion(item, MobEffects.REGENERATION, 100, 0, 0.66F);
    }

    @Test
    public void zombieBrainKeepsTc4FoodAndProbabilisticHunger() throws Exception {
        ItemZombieBrain item = new ItemZombieBrain();
        ItemStack stack = new ItemStack(item);

        assertEquals(4, item.getHealAmount(stack));
        assertEquals(0.2F, item.getSaturationModifier(stack), 0.0F);
        assertTrue(item.isWolfsFavoriteMeat());
        assertEquals(64, item.getItemStackLimit());
        assertFalse((Boolean) field(item, "alwaysEdible"));
        assertPotion(item, MobEffects.HUNGER, 600, 0, 0.8F);
    }

    @Test
    public void zombieBrainWarpRollMatchesTc4BranchesAndDrawOrder() {
        ItemZombieBrain item = new ItemZombieBrain();
        TestPlayer stickyPlayer = new TestPlayer(new TestWorld(), "sticky");
        RecordingRandom stickyRoll = new RecordingRandom(0.05F, 0);

        item.applyWarp(stickyPlayer, stickyRoll);

        assertEquals(1, stickyPlayer.knowledge.getWarpSticky());
        assertEquals(0, stickyPlayer.knowledge.getWarpTemp());
        assertEquals("[float]", stickyRoll.calls.toString());

        TestPlayer temporaryPlayer = new TestPlayer(new TestWorld(), "temporary");
        RecordingRandom temporaryRoll = new RecordingRandom(0.1F, 2);

        item.applyWarp(temporaryPlayer, temporaryRoll);

        assertEquals(0, temporaryPlayer.knowledge.getWarpSticky());
        assertEquals(3, temporaryPlayer.knowledge.getWarpTemp());
        assertEquals("[float, int(3)]", temporaryRoll.calls.toString());
    }

    @Test
    public void zombieBrainRunsWarpBeforeVanillaFoodEffectRoll() throws Exception {
        String source = new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/common/items/ItemZombieBrain.java")), StandardCharsets.UTF_8);
        int method = source.indexOf("public ItemStack onItemUseFinish");
        int warp = source.indexOf("this.applyWarp", method);
        int food = source.indexOf("super.onItemUseFinish", method);

        assertTrue(method >= 0 && warp > method && food > warp);
        assertTrue(source.substring(method, food).contains("entity instanceof EntityPlayerMP"));
    }

    private static void assertPotion(ItemFood item, Object potion, int duration,
            int amplifier, float probability) throws Exception {
        PotionEffect effect = (PotionEffect) field(item, "potionId");
        assertEquals(potion, effect.getPotion());
        assertEquals(duration, effect.getDuration());
        assertEquals(amplifier, effect.getAmplifier());
        assertEquals(probability, (Float) field(item, "potionEffectProbability"), 0.0F);
    }

    private static Object field(Object target, String name) throws Exception {
        Field field = ItemFood.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }

    private static final class RecordingRandom extends Random {
        private final float floatValue;
        private final int intValue;
        private final List<String> calls = new ArrayList<String>();

        private RecordingRandom(float floatValue, int intValue) {
            this.floatValue = floatValue;
            this.intValue = intValue;
        }

        @Override
        public float nextFloat() {
            this.calls.add("float");
            return this.floatValue;
        }

        @Override
        public int nextInt(int bound) {
            this.calls.add("int(" + bound + ")");
            return this.intValue;
        }
    }

    private static final class TestPlayer extends EntityPlayer {
        private final PlayerKnowledgeCapability knowledge = new PlayerKnowledgeCapability();

        private TestPlayer(World world, String name) {
            super(world, new GameProfile(UUID.nameUUIDFromBytes(
                    name.getBytes(StandardCharsets.UTF_8)), name));
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            if (capability == PlayerKnowledgeProvider.PLAYER_KNOWLEDGE) {
                return (T) this.knowledge;
            }
            return super.getCapability(capability, facing);
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
    }

    private static final class TestWorld extends World {
        private TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL,
                            false, false, WorldType.DEFAULT), "food_consumable"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "food_consumable_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
