package thaumcraft.common.lib.network.playerdata;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumFacing;
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
import thaumcraft.api.IScribeTools;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemResearchNotes;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeCapability;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.research.ResearchManager;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PacketPlayerCompleteRuntimeTest {

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
    public void primaryRequestCreatesResearchNoteWithoutCompletingResearch() {
        registerResearch(new ResearchItem("BASE", "TEST", new AspectList().add(Aspect.AIR, 1), 0, 0, 1, new ItemStack(Items.BOOK)));
        registerResearch(new ResearchItem("PRIMARY", "TEST", new AspectList().add(Aspect.FIRE, 2), 1, 0, 1, new ItemStack(Items.PAPER)).setParents("BASE"));

        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world, "primary_request");
        player.knowledge().addResearch("BASE");
        player.inventory.mainInventory.set(0, new ItemStack(new TestScribeTools()));
        player.inventory.mainInventory.set(1, new ItemStack(Items.PAPER, 1));

        assertTrue(PacketPlayerCompleteToServer.processRequest(player, "PRIMARY", world.provider.getDimension(), player.getName(), (byte) 1));
        assertFalse(ResearchManager.isResearchComplete(player, "PRIMARY"));
        assertTrue(ResearchManager.getResearchSlot(player, "PRIMARY") >= 0);
        assertEquals(0, countItem(player, Items.PAPER));
        assertEquals(1, player.inventory.mainInventory.get(0).getItemDamage());
    }

    @Test
    public void primaryRequestRejectsMissingPrerequisitesWithoutConsumingInputs() {
        registerResearch(new ResearchItem("BASE", "TEST", new AspectList().add(Aspect.AIR, 1), 0, 0, 1, new ItemStack(Items.BOOK)));
        registerResearch(new ResearchItem("PRIMARY", "TEST", new AspectList().add(Aspect.FIRE, 2), 1, 0, 1, new ItemStack(Items.PAPER)).setParents("BASE"));

        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world, "primary_prereq_fail");
        player.inventory.mainInventory.set(0, new ItemStack(new TestScribeTools()));
        player.inventory.mainInventory.set(1, new ItemStack(Items.PAPER, 1));

        assertFalse(PacketPlayerCompleteToServer.processRequest(player, "PRIMARY", world.provider.getDimension(), player.getName(), (byte) 1));
        assertEquals(-1, ResearchManager.getResearchSlot(player, "PRIMARY"));
        assertEquals(1, player.inventory.mainInventory.get(1).getCount());
        assertEquals(0, player.inventory.mainInventory.get(0).getItemDamage());
    }

    @Test
    public void secondaryRequestConsumesAspectCostsAndGrantsEligibleSibling() {
        registerResearch(new ResearchItem("BASE", "TEST", new AspectList().add(Aspect.AIR, 1), 0, 0, 1, new ItemStack(Items.BOOK)));
        registerResearch(new ResearchItem("SECONDARY", "TEST", new AspectList().add(Aspect.AIR, 3).add(Aspect.FIRE, 2), 1, 0, 1, new ItemStack(Items.BLAZE_POWDER))
                .setParents("BASE")
                .setSecondary()
                .setSiblings("SIBLING"));
        registerResearch(new ResearchItem("SIBLING", "TEST", new AspectList().add(Aspect.ORDER, 1), 2, 0, 1, new ItemStack(Items.COMPASS)).setParents("BASE"));

        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world, "secondary_success");
        player.knowledge().addResearch("BASE");
        player.knowledge().setAspectPool(Aspect.AIR, 5);
        player.knowledge().setAspectPool(Aspect.FIRE, 4);

        assertTrue(PacketPlayerCompleteToServer.processRequest(player, "SECONDARY", world.provider.getDimension(), player.getName(), (byte) 0));
        assertTrue(ResearchManager.isResearchComplete(player, "SECONDARY"));
        assertTrue(ResearchManager.isResearchComplete(player, "SIBLING"));
        assertEquals(2, player.knowledge().getAspectPoolFor(Aspect.AIR));
        assertEquals(2, player.knowledge().getAspectPoolFor(Aspect.FIRE));
    }

    @Test
    public void packetRejectsWrongTypeOrInsufficientAspectCostWithoutMutation() {
        registerResearch(new ResearchItem("BASE", "TEST", new AspectList().add(Aspect.AIR, 1), 0, 0, 1, new ItemStack(Items.BOOK)));
        registerResearch(new ResearchItem("PRIMARY", "TEST", new AspectList().add(Aspect.FIRE, 2), 1, 0, 1, new ItemStack(Items.PAPER)).setParents("BASE"));
        registerResearch(new ResearchItem("SECONDARY", "TEST", new AspectList().add(Aspect.AIR, 3), 2, 0, 1, new ItemStack(Items.FEATHER)).setParents("BASE").setSecondary());

        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world, "request_reject");
        player.knowledge().addResearch("BASE");
        player.knowledge().setAspectPool(Aspect.AIR, 1);
        player.inventory.mainInventory.set(0, new ItemStack(new TestScribeTools()));
        player.inventory.mainInventory.set(1, new ItemStack(Items.PAPER, 1));

        assertFalse(PacketPlayerCompleteToServer.processRequest(player, "PRIMARY", world.provider.getDimension(), player.getName(), (byte) 0));
        assertFalse(PacketPlayerCompleteToServer.processRequest(player, "SECONDARY", world.provider.getDimension(), player.getName(), (byte) 1));
        assertFalse(PacketPlayerCompleteToServer.processRequest(player, "SECONDARY", world.provider.getDimension(), player.getName(), (byte) 0));

        assertFalse(ResearchManager.isResearchComplete(player, "PRIMARY"));
        assertFalse(ResearchManager.isResearchComplete(player, "SECONDARY"));
        assertEquals(1, player.knowledge().getAspectPoolFor(Aspect.AIR));
        assertEquals(1, player.inventory.mainInventory.get(1).getCount());
    }

    private static int countItem(TestPlayer player, Item item) {
        int total = 0;
        for (ItemStack stack : player.inventory.mainInventory) {
            if (!stack.isEmpty() && stack.getItem() == item) {
                total += stack.getCount();
            }
        }
        return total;
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

    private static class TestScribeTools extends Item implements IScribeTools {
        TestScribeTools() {
            this.setMaxDamage(16);
            this.setMaxStackSize(1);
        }
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
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT), "packet_player_complete"),
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
                    return "packet_player_complete_dummy";
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
