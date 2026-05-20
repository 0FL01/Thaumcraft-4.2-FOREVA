package thaumcraft.common.lib.research;

import com.google.common.base.Predicate;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
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
import net.minecraftforge.common.capabilities.Capability;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ScanResult;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemResearchNotes;
import thaumcraft.common.items.ItemResource;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeCapability;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ScanProgressionRuntimeTest {

    private Map<String, ResearchCategoryList> oldCategories;
    private ItemResearchNotes oldResearchNotes;
    private ItemResource oldItemResource;
    private ConcurrentHashMap<List, AspectList> oldObjectTags;
    private ConcurrentHashMap<List, int[]> oldGroupedObjectTags;
    private ArrayList<ThaumcraftApi.EntityTags> oldScanEntities;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Before
    public void setUp() throws Exception {
        this.oldCategories = new LinkedHashMap<>(ResearchCategories.researchCategories);
        this.oldResearchNotes = ConfigItems.itemResearchNotes;
        this.oldItemResource = ConfigItems.itemResource;
        this.oldObjectTags = new ConcurrentHashMap<>(ThaumcraftApi.objectTags);
        this.oldGroupedObjectTags = new ConcurrentHashMap<>(ThaumcraftApi.groupedObjectTags);
        this.oldScanEntities = new ArrayList<>(ThaumcraftApi.scanEntities);

        ResearchCategories.researchCategories.clear();
        ResearchCategories.registerCategory("TEST", new ResourceLocation("thaumcraft", "textures/test/icon.png"), new ResourceLocation("thaumcraft", "textures/test/background.png"));
        ConfigItems.itemResearchNotes = new ItemResearchNotes();
        ConfigItems.itemResource = new ItemResource();
        ThaumcraftApi.objectTags.clear();
        ThaumcraftApi.groupedObjectTags.clear();
        ThaumcraftApi.scanEntities.clear();
        clearResearchCaches();
    }

    @After
    public void tearDown() throws Exception {
        ResearchCategories.researchCategories.clear();
        ResearchCategories.researchCategories.putAll(this.oldCategories);
        ConfigItems.itemResearchNotes = this.oldResearchNotes;
        ConfigItems.itemResource = this.oldItemResource;
        ThaumcraftApi.objectTags.clear();
        ThaumcraftApi.objectTags.putAll(this.oldObjectTags);
        ThaumcraftApi.groupedObjectTags.clear();
        ThaumcraftApi.groupedObjectTags.putAll(this.oldGroupedObjectTags);
        ThaumcraftApi.scanEntities.clear();
        ThaumcraftApi.scanEntities.addAll(this.oldScanEntities);
        clearResearchCaches();
    }

    @Test
    public void itemScanAwardsAspectKnowledgeAndUnlocksAspectTriggeredClue() {
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.STICK), new AspectList().add(Aspect.AIR, 2));
        registerResearch(new ResearchItem("ASPECTSCAN", "TEST", new AspectList().add(Aspect.AIR, 1), 0, 0, 1, new ItemStack(Items.FEATHER))
                .setHidden()
                .setAspectTriggers(Aspect.AIR));

        ScanWorld world = new ScanWorld();
        TestPlayer player = new TestPlayer(world, "item_scan");
        ScanResult scan = new ScanResult((byte)1, net.minecraft.item.Item.getIdFromItem(Items.STICK), 0, null, null);

        assertTrue(ScanManager.completeScan(player, scan, "@"));
        assertTrue(player.knowledge().hasScannedItem("@" + ScanManager.generateItemHash(Items.STICK, 0)));
        assertEquals(2, player.knowledge().getAspectPoolFor(Aspect.AIR));
        assertTrue(ResearchManager.isResearchComplete(player, "@ASPECTSCAN"));
        assertFalse(ResearchManager.isResearchComplete(player, "ASPECTSCAN"));
    }

    @Test
    public void entityScanMatchesLegacyTriggerNameAndUnlocksClue() {
        ThaumcraftApi.registerEntityTag("minecraft:creeper", new AspectList().add(Aspect.ORDER, 1));
        registerResearch(new ResearchItem("ENTITYSCAN", "TEST", new AspectList().add(Aspect.ORDER, 1), 0, 0, 1, new ItemStack(Items.GUNPOWDER))
                .setHidden()
                .setEntityTriggers("Creeper"));

        ScanWorld world = new ScanWorld();
        TestPlayer player = new TestPlayer(world, "entity_scan");
        EntityCreeper creeper = new EntityCreeper(world);
        creeper.setPosition(0.0D, 0.0D, 0.0D);
        world.entities.add(creeper);
        ScanResult scan = new ScanResult((byte)2, 0, 0, creeper, null);

        assertTrue(ScanManager.completeScan(player, scan, "@"));
        assertTrue(player.knowledge().hasScannedEntity("@" + runtimeEntityHash(creeper)));
        assertTrue(ResearchManager.isResearchComplete(player, "@ENTITYSCAN"));
        assertFalse(ResearchManager.isResearchComplete(player, "ENTITYSCAN"));
    }

    @Test
    public void nodePhenomenaScanAwardsAspectsAndTracksPhenomena() {
        ScanWorld world = new ScanWorld();
        BlockPos pos = new BlockPos(0, 1, 4);
        world.setBlock(pos, Blocks.STONE.getDefaultState());
        world.setHit(new RayTraceResult(new Vec3d(0.5D, 1.0D, 4.5D), EnumFacing.UP, pos));
        world.node = new TestNodeTile();
        world.node.setWorld(world);
        world.node.setPos(pos);

        TestPlayer player = new TestPlayer(world, "node_scan");
        ScanResult scan = new ScanResult((byte)3, 0, 0, null, "NODE0:0:1:4");

        assertTrue(ScanManager.completeScan(player, scan, "@"));
        assertTrue(player.knowledge().hasScannedPhenomena("@NODE0:0:1:4"));
        assertEquals(4, player.knowledge().getAspectPoolFor(Aspect.AIR));
    }

    @Test
    public void discoveryFailSpawnsKnowledgeFragmentsWhenNoHiddenResearchIsEligible() {
        ScanWorld world = new ScanWorld();
        TestPlayer player = new TestPlayer(world, "discovery_fail");
        ItemStack stack = holdMainHand(player, new ItemStack(ConfigItems.itemResearchNotes, 1, 24));

        ActionResult<ItemStack> result = ConfigItems.itemResearchNotes.onItemRightClick(world, player, EnumHand.MAIN_HAND);

        assertEquals(EnumActionResult.SUCCESS, result.getType());
        assertTrue(stack.isEmpty());
        assertEquals(1, world.spawned.size());
        assertTrue(world.spawned.get(0) instanceof EntityItem);
        ItemStack dropped = ((EntityItem)world.spawned.get(0)).getItem();
        assertTrue(dropped.getItem() == ConfigItems.itemResource);
        assertEquals(ItemResource.META_KNOWLEDGE_FRAGMENT, dropped.getItemDamage());
        assertTrue(dropped.getCount() >= 7 && dropped.getCount() <= 9);
    }

    private static int runtimeEntityHash(Entity entity) {
        String hash = "minecraft:creeper";
        if (entity instanceof EntityPlayer) {
            hash = "player_" + ((EntityPlayer)entity).getName();
        }
        if (entity instanceof EntityCreeper && ((EntityCreeper)entity).getPowered()) {
            hash += "POWERED";
        }
        return hash.hashCode();
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

    private static class TestNodeTile extends thaumcraft.api.TileThaumcraft implements INode {
        private final AspectList aspects = new AspectList().add(Aspect.AIR, 10);

        @Override
        public String getId() {
            return "0:0:1:4";
        }

        @Override
        public AspectList getAspectsBase() {
            return this.aspects.copy();
        }

        @Override
        public NodeType getNodeType() {
            return NodeType.NORMAL;
        }

        @Override
        public void setNodeType(NodeType var1) {
        }

        @Override
        public void setNodeModifier(NodeModifier var1) {
        }

        @Override
        public NodeModifier getNodeModifier() {
            return NodeModifier.BRIGHT;
        }

        @Override
        public int getNodeVisBase(Aspect var1) {
            return this.aspects.getAmount(var1);
        }

        @Override
        public void setNodeVisBase(Aspect var1, short var2) {
        }

        @Override
        public AspectList getAspects() {
            return this.aspects.copy();
        }

        @Override
        public void setAspects(AspectList var1) {
        }

        @Override
        public boolean doesContainerAccept(Aspect var1) {
            return true;
        }

        @Override
        public int addToContainer(Aspect var1, int var2) {
            return 0;
        }

        @Override
        public boolean takeFromContainer(Aspect var1, int var2) {
            return false;
        }

        @Override
        @Deprecated
        public boolean takeFromContainer(AspectList var1) {
            return false;
        }

        @Override
        public boolean doesContainerContainAmount(Aspect var1, int var2) {
            return false;
        }

        @Override
        @Deprecated
        public boolean doesContainerContain(AspectList var1) {
            return false;
        }

        @Override
        public int containerContains(Aspect var1) {
            return this.aspects.getAmount(var1);
        }
    }

    private static class ScanWorld extends World {
        private final List<Entity> entities = new ArrayList<>();
        private final List<Entity> spawned = new ArrayList<>();
        private IBlockState state = Blocks.AIR.getDefaultState();
        private BlockPos statePos = BlockPos.ORIGIN;
        private RayTraceResult hit;
        private TestNodeTile node;

        ScanWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT), "scan_progression"),
                    new WorldProviderSurface(),
                    new Profiler(),
                    false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        void setBlock(BlockPos pos, IBlockState state) {
            this.statePos = pos;
            this.state = state;
        }

        void setHit(RayTraceResult hit) {
            this.hit = hit;
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            return pos.equals(this.statePos) ? this.state : Blocks.AIR.getDefaultState();
        }

        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return this.node != null && pos.equals(this.node.getPos()) ? this.node : null;
        }

        @Override
        public List<Entity> getEntitiesInAABBexcluding(Entity entityIn, AxisAlignedBB boundingBox, Predicate<? super Entity> predicate) {
            List<Entity> matches = new ArrayList<>();
            for (Entity entity : this.entities) {
                if (entity == entityIn || !entity.getEntityBoundingBox().intersects(boundingBox)) {
                    continue;
                }
                if (predicate == null || predicate.test(entity)) {
                    matches.add(entity);
                }
            }
            return matches;
        }

        @Override
        public boolean spawnEntity(Entity entityIn) {
            this.spawned.add(entityIn);
            return true;
        }

        @Override
        public RayTraceResult rayTraceBlocks(Vec3d start, Vec3d end, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
            return this.hit;
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
                    return "scan_progression_dummy";
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
