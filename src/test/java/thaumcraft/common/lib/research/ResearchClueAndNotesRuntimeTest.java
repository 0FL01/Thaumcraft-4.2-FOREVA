package thaumcraft.common.lib.research;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.capabilities.Capability;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemResearchNotes;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeCapability;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ResearchClueAndNotesRuntimeTest {

    private Map<String, ResearchCategoryList> oldCategories;
    private ItemResearchNotes oldResearchNotes;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Before
    public void setUp() throws Exception {
        this.oldCategories = new LinkedHashMap<>(ResearchCategories.researchCategories);
        this.oldResearchNotes = ConfigItems.itemResearchNotes;
        ResearchCategories.researchCategories.clear();
        ResearchCategories.registerCategory("TEST", new ResourceLocation("thaumcraft", "textures/test/icon.png"), new ResourceLocation("thaumcraft", "textures/test/background.png"));
        ConfigItems.itemResearchNotes = new ItemResearchNotes();
        clearResearchCaches();
    }

    @After
    public void tearDown() throws Exception {
        ResearchCategories.researchCategories.clear();
        ResearchCategories.researchCategories.putAll(this.oldCategories);
        ConfigItems.itemResearchNotes = this.oldResearchNotes;
        clearResearchCaches();
    }

    @Test
    public void findHiddenResearchUsesLivePlayerKnowledgeForEligibleResearch() {
        registerResearch(new ResearchItem("BASE", "TEST", new AspectList().add(Aspect.AIR, 1), 0, 0, 1, new ItemStack(Items.BOOK)));
        registerResearch(new ResearchItem("HIDDEN", "TEST", new AspectList().add(Aspect.FIRE, 2), 1, 0, 1, new ItemStack(Items.BLAZE_POWDER))
                .setParents("BASE")
                .setHidden()
                .setItemTriggers(new ItemStack(Items.STICK)));

        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world, "find_hidden");
        player.knowledge().addResearch("BASE");

        assertEquals("HIDDEN", ResearchManager.findHiddenResearch(player));
    }

    @Test
    public void findMatchingResearchUsesLivePlayerKnowledgeForEligibleAspect() {
        registerResearch(new ResearchItem("BASE", "TEST", new AspectList().add(Aspect.AIR, 1), 0, 0, 1, new ItemStack(Items.BOOK)));
        registerResearch(new ResearchItem("MATCH", "TEST", new AspectList().add(Aspect.AIR, 2).add(Aspect.FIRE, 1), 1, 0, 1, new ItemStack(Items.FEATHER)).setParents("BASE"));

        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world, "find_match");
        player.knowledge().addResearch("BASE");

        assertEquals("MATCH", ResearchManager.findMatchingResearch(player, Aspect.AIR));
    }

    @Test
    public void revealDiscoveryCreatesResearchNoteForEligibleHiddenResearch() {
        registerResearch(new ResearchItem("BASE", "TEST", new AspectList().add(Aspect.AIR, 1), 0, 0, 1, new ItemStack(Items.BOOK)));
        registerResearch(new ResearchItem("HIDDEN", "TEST", new AspectList().add(Aspect.FIRE, 2), 1, 0, 1, new ItemStack(Items.BLAZE_POWDER))
                .setParents("BASE")
                .setHidden()
                .setItemTriggers(new ItemStack(Items.STICK)));

        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world, "reveal_hidden");
        player.knowledge().addResearch("BASE");
        ItemResearchNotes notes = ConfigItems.itemResearchNotes;
        ItemStack stack = holdMainHand(player, new ItemStack(notes, 1, 42));

        ActionResult<ItemStack> result = notes.onItemRightClick(world, player, EnumHand.MAIN_HAND);
        ResearchNoteData data = ResearchManager.getData(stack);

        assertEquals(EnumActionResult.SUCCESS, result.getType());
        assertEquals(0, stack.getItemDamage());
        assertNotNull(data);
        assertEquals("HIDDEN", data.key);
        assertFalse(data.isComplete());
    }

    @Test
    public void completeResearchNoteUsesLivePlayerKnowledgeAndUnlocksSibling() {
        registerResearch(new ResearchItem("BASE", "TEST", new AspectList().add(Aspect.AIR, 1), 0, 0, 1, new ItemStack(Items.BOOK)));
        registerResearch(new ResearchItem("PRIMARY", "TEST", new AspectList().add(Aspect.FIRE, 2), 1, 0, 1, new ItemStack(Items.PAPER))
                .setParents("BASE")
                .setSiblings("SIBLING"));
        registerResearch(new ResearchItem("SIBLING", "TEST", new AspectList().add(Aspect.ORDER, 1), 2, 0, 1, new ItemStack(Items.COMPASS)).setParents("BASE"));

        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world, "complete_note");
        player.knowledge().addResearch("BASE");
        ItemResearchNotes notes = ConfigItems.itemResearchNotes;
        ItemStack stack = ResearchManager.createNote(new ItemStack(notes, 1, 0), "PRIMARY", world);
        ResearchNoteData data = ResearchManager.getData(stack);
        data.complete = true;
        ResearchManager.updateData(stack, data);
        holdMainHand(player, stack);

        ActionResult<ItemStack> result = notes.onItemRightClick(world, player, EnumHand.MAIN_HAND);

        assertEquals(EnumActionResult.SUCCESS, result.getType());
        assertTrue(ResearchManager.isResearchComplete(player, "PRIMARY"));
        assertTrue(ResearchManager.isResearchComplete(player, "SIBLING"));
        assertTrue(player.getHeldItemMainhand().isEmpty());
    }

    @Test
    public void createClueAddsAtPrefixedResearchWithoutCompletingFullResearch() {
        registerResearch(new ResearchItem("CLUE", "TEST", new AspectList().add(Aspect.ORDER, 2), 1, 0, 1, new ItemStack(Items.COMPASS))
                .setHidden()
                .setItemTriggers(new ItemStack(Items.STICK)));

        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world, "create_clue");

        assertTrue(ResearchManager.createClue(world, player, new ItemStack(Items.STICK), null));
        assertTrue(ResearchManager.isResearchComplete(player, "@CLUE"));
        assertFalse(ResearchManager.isResearchComplete(player, "CLUE"));
    }

    private static ItemStack holdMainHand(TestPlayer player, ItemStack stack) {
        player.inventory.currentItem = 0;
        player.inventory.mainInventory.set(0, stack);
        return stack;
    }

    private static void registerResearch(ResearchItem item) {
        item.registerResearchItem();
    }

    private static void clearResearchCaches() throws Exception {
        clearField("playerDataCache");
        setField("allHiddenResearch", null);
        setField("allValidResearch", null);
    }

    @SuppressWarnings("unchecked")
    private static void clearField(String name) throws Exception {
        Field field = ResearchManager.class.getDeclaredField(name);
        field.setAccessible(true);
        Object value = field.get(null);
        if (value instanceof Map) {
            ((Map<?, ?>) value).clear();
        }
    }

    private static void setField(String name, Object value) throws Exception {
        Field field = ResearchManager.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(null, value);
    }

    private static class TestPlayer extends EntityPlayer {
        private final PlayerKnowledgeCapability knowledge = new PlayerKnowledgeCapability();

        TestPlayer(World world, String name) {
            super(world, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)), name));
            this.knowledge.addDiscoveredPrimalAspects();
        }

        PlayerKnowledgeCapability knowledge() {
            return this.knowledge;
        }

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return capability == null || capability == PlayerKnowledgeProvider.PLAYER_KNOWLEDGE || super.hasCapability(capability, facing);
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            if (capability == null || capability == PlayerKnowledgeProvider.PLAYER_KNOWLEDGE) {
                @SuppressWarnings("unchecked")
                T value = (T) this.knowledge;
                return value;
            }
            return super.getCapability(capability, facing);
        }

        @Override
        public boolean isSpectator() {
            return false;
        }

        @Override
        public boolean isCreative() {
            return this.capabilities.isCreativeMode;
        }
    }

    private static class TestWorld extends World {
        TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT), "research_clue_notes"),
                    new WorldProviderSurface(),
                    new Profiler(),
                    false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override
                public Chunk getLoadedChunk(int x, int z) {
                    return null;
                }

                @Override
                public Chunk provideChunk(int x, int z) {
                    return null;
                }

                @Override
                public boolean tick() {
                    return false;
                }

                @Override
                public String makeString() {
                    return "research_clue_notes_dummy";
                }

                @Override
                public boolean isChunkGeneratedAt(int x, int z) {
                    return true;
                }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
