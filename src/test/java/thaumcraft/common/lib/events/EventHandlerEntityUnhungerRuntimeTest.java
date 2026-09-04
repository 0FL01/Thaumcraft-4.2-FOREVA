package thaumcraft.common.lib.events;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.potions.PotionUnnaturalHunger;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class EventHandlerEntityUnhungerRuntimeTest {
    private PotionUnnaturalHunger oldUnnaturalHunger;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Before
    public void installUnnaturalHunger() {
        this.oldUnnaturalHunger = Config.potionUnnaturalHunger;
        Config.potionUnnaturalHunger = new PotionUnnaturalHunger(true, 0x55AA55);
    }

    @After
    public void restoreUnnaturalHunger() {
        Config.potionUnnaturalHunger = this.oldUnnaturalHunger;
    }

    @Test
    public void realRottenFleshUseKeepsVanillaHungerWithoutThaumcraftMessage() {
        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world, "vanilla_hunger");
        player.getFoodStats().setFoodLevel(10);
        world.rand.setSeed(1L);

        consumeFood(player, new ItemStack(Items.ROTTEN_FLESH));

        PotionEffect hunger = player.getActivePotionEffect(MobEffects.HUNGER);
        assertNotNull(hunger);
        assertEquals(600, hunger.getDuration());
        assertEquals(0, hunger.getAmplifier());
        assertEquals(14, player.getFoodStats().getFoodLevel());
        assertNotSame(MobEffects.HUNGER, Config.potionUnnaturalHunger);
        assertFalse(player.isPotionActive(Config.potionUnnaturalHunger));
        assertTrue(player.messages.isEmpty());
    }

    @Test
    public void realRottenFleshUseKeepsVanillaHungerAndReducesOnlyUnnaturalHunger() {
        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world, "unnatural_hunger");
        player.getFoodStats().setFoodLevel(10);
        player.addPotionEffect(new PotionEffect(Config.potionUnnaturalHunger, 5000, 2, true, true));
        world.rand.setSeed(1L);

        consumeFood(player, new ItemStack(Items.ROTTEN_FLESH));

        PotionEffect vanillaHunger = player.getActivePotionEffect(MobEffects.HUNGER);
        assertNotNull(vanillaHunger);
        assertEquals(600, vanillaHunger.getDuration());
        PotionEffect unnaturalHunger = player.getActivePotionEffect(Config.potionUnnaturalHunger);
        assertNotNull(unnaturalHunger);
        assertEquals(4400, unnaturalHunger.getDuration());
        assertEquals(1, unnaturalHunger.getAmplifier());
        assertEquals(14, player.getFoodStats().getFoodLevel());
        assertTranslation(player.messages, "warp.text.hunger.2");
    }

    @Test
    public void realRawChickenUseCannotTurnVanillaHungerIntoThaumcraftWarning() {
        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world, "vanilla_chicken_hunger");
        player.getFoodStats().setFoodLevel(10);
        world.rand.setSeed(1L);

        consumeFood(player, new ItemStack(Items.CHICKEN));

        assertNotNull(player.getActivePotionEffect(MobEffects.HUNGER));
        assertEquals(12, player.getFoodStats().getFoodLevel());
        assertFalse(player.isPotionActive(Config.potionUnnaturalHunger));
        assertTrue(player.messages.isEmpty());
    }

    @Test
    public void normalFoodWarnsOnlyWhileUnnaturalHungerIsActive() {
        TestPlayer player = new TestPlayer(new TestWorld(), "unnatural_hunger_food");
        player.addPotionEffect(new PotionEffect(Config.potionUnnaturalHunger, 5000, 2, true, true));

        consumeFood(player, new ItemStack(Items.BREAD));

        PotionEffect hunger = player.getActivePotionEffect(Config.potionUnnaturalHunger);
        assertNotNull(hunger);
        assertEquals(5000, hunger.getDuration());
        assertEquals(2, hunger.getAmplifier());
        assertTranslation(player.messages, "warp.text.hunger.1");
    }

    private static void finishUsing(EntityPlayer player, ItemStack used, ItemStack result) {
        LivingEntityUseItemEvent.Finish event = new LivingEntityUseItemEvent.Finish(
                player, used, 0, result);
        new EventHandlerEntity().onItemUseFinish(event);
    }

    private static void consumeFood(EntityPlayer player, ItemStack stack) {
        ItemStack used = stack.copy();
        ItemStack result = stack.onItemUseFinish(player.world, player);
        finishUsing(player, used, result);
    }

    private static void assertTranslation(List<ITextComponent> messages, String key) {
        assertEquals(1, messages.size());
        assertTrue(messages.get(0) instanceof TextComponentTranslation);
        assertEquals(key, ((TextComponentTranslation) messages.get(0)).getKey());
    }

    private static final class TestPlayer extends EntityPlayer {
        private final List<ITextComponent> messages = new ArrayList<ITextComponent>();

        private TestPlayer(World world, String name) {
            super(world, new GameProfile(UUID.nameUUIDFromBytes(
                    name.getBytes(StandardCharsets.UTF_8)), name));
        }

        @Override
        public void sendMessage(ITextComponent component) {
            this.messages.add(component);
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
    }

    private static final class TestWorld extends World {
        private TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL,
                            false, false, WorldType.DEFAULT), "event_unhunger"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
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
                @Override public String makeString() { return "event_unhunger_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
