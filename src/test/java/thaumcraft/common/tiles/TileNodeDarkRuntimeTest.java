package thaumcraft.common.tiles;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.monster.EntityGiantBrainyZombie;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TileNodeDarkRuntimeTest {
    private boolean oldHardNode;
    private Biome oldEerieBiome;
    private Map<Integer, Integer> oldDimensionBlacklist;
    private Map<Integer, Integer> oldBiomeBlacklist;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Before
    public void configureDarkNodeSpawning() {
        this.oldHardNode = Config.hardNode;
        this.oldEerieBiome = ThaumcraftWorldGenerator.biomeEerie;
        this.oldDimensionBlacklist = new HashMap<>(ThaumcraftWorldGenerator.dimensionBlacklist);
        this.oldBiomeBlacklist = new HashMap<>(ThaumcraftWorldGenerator.biomeBlacklist);
        Config.hardNode = true;
        ThaumcraftWorldGenerator.biomeEerie = Biomes.PLAINS;
        ThaumcraftWorldGenerator.dimensionBlacklist.clear();
        ThaumcraftWorldGenerator.biomeBlacklist.clear();
    }

    @After
    public void restoreDarkNodeConfiguration() {
        Config.hardNode = this.oldHardNode;
        ThaumcraftWorldGenerator.biomeEerie = this.oldEerieBiome;
        ThaumcraftWorldGenerator.dimensionBlacklist.clear();
        ThaumcraftWorldGenerator.dimensionBlacklist.putAll(this.oldDimensionBlacklist);
        ThaumcraftWorldGenerator.biomeBlacklist.clear();
        ThaumcraftWorldGenerator.biomeBlacklist.putAll(this.oldBiomeBlacklist);
    }

    @Test
    public void darkNodeRejectsFloorValidZombieWhenBoundingBoxCollides() throws Exception {
        DarkNodeWorld world = new DarkNodeWorld(true);
        TileNode node = attachDarkNode(world);

        attemptZombieSpawn(node, world);

        assertTrue("The dark node should validate the complete zombie bounding box", world.collisionChecks > 0);
        assertNull("A dark node must not spawn a zombie inside solid blocks", world.spawned);
        assertEquals("Rejected spawns must not emit the spawn effect", 0, world.lastEvent);
    }

    @Test
    public void darkNodeStillSpawnsZombieInClearVolume() throws Exception {
        DarkNodeWorld world = new DarkNodeWorld(false);
        TileNode node = attachDarkNode(world);

        attemptZombieSpawn(node, world);

        assertTrue(world.collisionChecks > 0);
        assertNotNull("A dark node should keep spawning zombies at valid positions", world.spawned);
        assertTrue(world.spawned instanceof EntityGiantBrainyZombie);
        assertEquals(2004, world.lastEvent);
    }

    private static TileNode attachDarkNode(DarkNodeWorld world) throws Exception {
        TileNode node = new TestTileNode();
        node.setWorld(world);
        node.setPos(DarkNodeWorld.NODE_POS);
        node.setNodeType(NodeType.DARK);
        Field count = TileNode.class.getDeclaredField("count");
        count.setAccessible(true);
        count.setInt(node, 50);
        return node;
    }

    private static void attemptZombieSpawn(TileNode node, DarkNodeWorld world) throws Exception {
        Method handleDarkNode = TileNode.class.getDeclaredMethod("handleDarkNode", boolean.class);
        handleDarkNode.setAccessible(true);
        for (int i = 0; i < 100 && world.collisionChecks == 0 && world.spawned == null; ++i) {
            handleDarkNode.invoke(node, false);
        }
    }

    private static class TestTileNode extends TileNode {
        @Override
        public void markDirty() {
        }
    }

    private static class TestPlayer extends EntityPlayer {
        TestPlayer(World world) {
            super(world, new GameProfile(UUID.randomUUID(), "dark_node_test"));
        }

        @Override
        public boolean isSpectator() {
            return false;
        }

        @Override
        public boolean isCreative() {
            return false;
        }
    }

    private static class DarkNodeWorld extends World {
        private static final BlockPos NODE_POS = new BlockPos(0, 64, 0);
        private final boolean blocked;
        private final EntityPlayer player;
        private int collisionChecks;
        private Entity spawned;
        private int lastEvent;

        DarkNodeWorld(boolean blocked) {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "dark_node_runtime"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.blocked = blocked;
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
            this.getWorldInfo().setDifficulty(EnumDifficulty.NORMAL);
            this.rand.setSeed(0L);
            this.player = new TestPlayer(this);
        }

        @Override
        public Biome getBiome(BlockPos pos) {
            return Biomes.PLAINS;
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            return Blocks.STONE.getDefaultState();
        }

        @Override
        public int getLightFor(EnumSkyBlock type, BlockPos pos) {
            return 0;
        }

        @Override
        public int getLightFromNeighbors(BlockPos pos) {
            return 0;
        }

        @Override
        public EntityPlayer getClosestPlayer(double x, double y, double z, double distance, boolean spectator) {
            return this.player;
        }

        @Override
        public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> type, AxisAlignedBB bounds) {
            return new ArrayList<>();
        }

        @Override
        public List<AxisAlignedBB> getCollisionBoxes(Entity entity, AxisAlignedBB bounds) {
            ++this.collisionChecks;
            return this.blocked ? Collections.singletonList(bounds) : Collections.emptyList();
        }

        @Override
        public boolean checkNoEntityCollision(AxisAlignedBB bounds, Entity entity) {
            return true;
        }

        @Override
        public boolean containsAnyLiquid(AxisAlignedBB bounds) {
            return false;
        }

        @Override
        public boolean spawnEntity(Entity entity) {
            this.spawned = entity;
            return true;
        }

        @Override
        public void playEvent(int type, BlockPos pos, int data) {
            this.lastEvent = type;
        }

        @Override
        public void setEntityState(Entity entity, byte state) {
        }

        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return null;
        }

        @Override
        public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "dark_node_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
