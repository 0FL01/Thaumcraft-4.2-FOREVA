package thaumcraft.common.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Biomes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.config.Config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TileNodeHungryRuntimeTest {
    private static final Item TEST_FOOD = new Item();
    private boolean oldHardNode;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Before
    public void rememberConfig() {
        this.oldHardNode = Config.hardNode;
        Config.hardNode = true;
    }

    @After
    public void restoreConfig() {
        Config.hardNode = this.oldHardNode;
    }

    @Test
    public void hungryNodePullsEntitiesAndDamagesCloseTargets() {
        HungryWorld world = new HungryWorld(Blocks.AIR.getDefaultState());
        TileNode node = attachHungryNode(world);
        TestEntity pulled = new TestEntity(world);
        pulled.setPosition(10.5D, 64.5D, 0.5D);
        TestEntity close = new TestEntity(world);
        close.setPosition(0.75D, 64.5D, 0.5D);
        world.entities.add(pulled);
        world.entities.add(close);

        node.update();

        assertTrue("Hungry nodes should pull distant entities inward", pulled.motionX < 0.0D);
        assertSame("Hungry nodes should use the original void damage source", DamageSource.OUT_OF_WORLD, close.lastDamageSource);
        assertEquals(1.0F, close.lastDamage, 0.0F);
    }

    @Test
    public void hardNodeConfigDisablesEntitySuction() {
        Config.hardNode = false;
        HungryWorld world = new HungryWorld(Blocks.AIR.getDefaultState());
        TileNode node = attachHungryNode(world);
        TestEntity entity = new TestEntity(world);
        entity.setPosition(10.5D, 64.5D, 0.5D);
        world.entities.add(entity);

        node.update();

        assertEquals(0.0D, entity.motionX, 0.0D);
        assertEquals(0.0D, entity.motionY, 0.0D);
        assertEquals(0.0D, entity.motionZ, 0.0D);
    }

    @Test
    public void hungryNodeBreaksSoftBlocksButNotHardBlocks() throws Exception {
        Config.hardNode = false;
        HungryWorld softWorld = new HungryWorld(Blocks.DIRT.getDefaultState());
        TileNode softNode = attachHungryNode(softWorld);
        runHungryBlockAttempts(softNode, 10);

        assertTrue("Hungry nodes should break a ray-traced block below hardness five", softWorld.destroyedBlocks > 0);
        assertTrue("Hungry nodes should preserve normal block drops", softWorld.lastDropBlock);

        HungryWorld hardWorld = new HungryWorld(Blocks.OBSIDIAN.getDefaultState());
        TileNode hardNode = attachHungryNode(hardWorld);
        runHungryBlockAttempts(hardNode, 10);

        assertEquals("Hungry nodes should not break blocks at or above hardness five", 0, hardWorld.destroyedBlocks);
    }

    @Test
    public void hungryNodeRefillsBeforeGrowingAndPersistsNewBaseVis() throws Exception {
        ThaumcraftApi.registerObjectTag(new ItemStack(TEST_FOOD), new AspectList().add(Aspect.AIR, 4));
        try {
            HungryWorld refillWorld = new HungryWorld(Blocks.AIR.getDefaultState());
            TileNode refillNode = attachHungryNode(refillWorld);
            refillNode.setAspects(new AspectList().add(Aspect.AIR, 2));
            assertTrue(refillNode.takeFromContainer(Aspect.AIR, 1));

            consumeTestItem(refillWorld, refillNode);

            assertEquals("A hungry node should refill missing current vis before increasing capacity", 2,
                    refillNode.getAspects().getAmount(Aspect.AIR));
            assertEquals(2, refillNode.getAspectsBase().getAmount(Aspect.AIR));

            HungryWorld growthWorld = new HungryWorld(Blocks.AIR.getDefaultState());
            TileNode growthNode = attachHungryNode(growthWorld);
            growthNode.setAspects(new AspectList().add(Aspect.FIRE, 1));

            consumeTestItem(growthWorld, growthNode);

            assertEquals("A full hungry node should gain permanent base vis from a new primal aspect", 1,
                    growthNode.getAspectsBase().getAmount(Aspect.AIR));
            assertEquals("Permanent growth should become visually charged through normal regeneration", 0,
                    growthNode.getAspects().getAmount(Aspect.AIR));

            NBTTagCompound saved = new NBTTagCompound();
            growthNode.writeCustomNBT(saved);
            TileNode restored = new TileNode();
            restored.readCustomNBT(saved);
            assertEquals(1, restored.getAspectsBase().getAmount(Aspect.AIR));
            assertEquals(0, restored.getAspects().getAmount(Aspect.AIR));

            java.lang.reflect.Field count = TileNode.class.getDeclaredField("count");
            count.setAccessible(true);
            count.setInt(growthNode, 599);
            growthNode.update();
            assertEquals("The next regenerated vis should expose permanent growth to the renderer", 1,
                    growthNode.getAspects().getAmount(Aspect.AIR));
        } finally {
            ThaumcraftApi.objectTags.remove(Arrays.asList(TEST_FOOD, 0));
        }
    }

    @Test
    public void hungryNodeKeepsOriginalRayTraceParticlesAndAspectFeeding() throws IOException {
        String source = new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/common/tiles/TileNode.java")), StandardCharsets.UTF_8);

        assertTrue(source.contains("ThaumcraftApiHelper.rayTraceIgnoringSource("));
        assertTrue(source.contains("Thaumcraft.proxy.boreDigFx("));
        assertTrue(source.contains("ScanManager.getScanAspects(new ScanResult((byte) 2"));
        assertTrue(source.contains("ResearchManager.reduceToPrimals(aspects.copy())"));
        assertTrue(source.contains("this.count % 50 != 0"));
        assertTrue(source.contains("hardness >= 0.0F && hardness < 5.0F"));
        assertTrue(source.contains("this.world.destroyBlock(target, true);"));
    }

    private static TileNode attachHungryNode(HungryWorld world) {
        TileNode node = new TestTileNode();
        node.setWorld(world);
        node.setPos(HungryWorld.NODE_POS);
        node.setNodeType(NodeType.HUNGRY);
        return node;
    }

    private static class TestTileNode extends TileNode {
        @Override
        public void markDirty() {
        }
    }

    private static void consumeTestItem(HungryWorld world, TileNode node) {
        EntityItem item = new EntityItem(world, 0.75D, 64.5D, 0.5D, new ItemStack(TEST_FOOD));
        world.entities.add(item);
        for (int i = 0; i < 5; ++i) {
            node.update();
        }
        assertFalse("Hungry nodes should consume close item entities after five void-damage ticks", item.isEntityAlive());
        world.entities.remove(item);
    }

    private static void runHungryBlockAttempts(TileNode node, int attempts) throws Exception {
        java.lang.reflect.Field count = TileNode.class.getDeclaredField("count");
        count.setAccessible(true);
        for (int i = 0; i < attempts; ++i) {
            count.setInt(node, 49);
            node.update();
        }
    }

    private static class TestEntity extends Entity {
        private DamageSource lastDamageSource;
        private float lastDamage;

        TestEntity(World world) {
            super(world);
        }

        @Override
        protected void entityInit() {
        }

        @Override
        protected void readEntityFromNBT(NBTTagCompound compound) {
        }

        @Override
        protected void writeEntityToNBT(NBTTagCompound compound) {
        }

        @Override
        public boolean attackEntityFrom(DamageSource source, float amount) {
            this.lastDamageSource = source;
            this.lastDamage = amount;
            return true;
        }
    }

    private static class HungryWorld extends World {
        private static final BlockPos NODE_POS = new BlockPos(0, 64, 0);
        private final IBlockState shellState;
        private final Set<BlockPos> destroyed = new HashSet<>();
        private final List<Entity> entities = new ArrayList<>();
        private int destroyedBlocks;
        private boolean lastDropBlock;

        HungryWorld(IBlockState shellState) {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT), "hungry_node_runtime"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.shellState = shellState;
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
            this.rand.setSeed(0L);
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            if (isShell(pos) && !this.destroyed.contains(pos)) {
                return this.shellState;
            }
            return Blocks.AIR.getDefaultState();
        }

        @Override
        public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> type, AxisAlignedBB bounds) {
            List<T> result = new ArrayList<>();
            for (Entity entity : this.entities) {
                if (type.isInstance(entity) && entity.getEntityBoundingBox().intersects(bounds)) {
                    result.add(type.cast(entity));
                }
            }
            return result;
        }

        @Override
        public int getHeight(int x, int z) {
            return 255;
        }

        @Override
        public Biome getBiome(BlockPos pos) {
            return Biomes.PLAINS;
        }

        @Override
        public boolean destroyBlock(BlockPos pos, boolean dropBlock) {
            if (!isShell(pos) || this.destroyed.contains(pos)) {
                return false;
            }
            this.destroyed.add(pos.toImmutable());
            ++this.destroyedBlocks;
            this.lastDropBlock = dropBlock;
            return true;
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
                @Override public String makeString() { return "hungry_node_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }

        private static boolean isShell(BlockPos pos) {
            int dx = Math.abs(pos.getX() - NODE_POS.getX());
            int dy = Math.abs(pos.getY() - NODE_POS.getY());
            int dz = Math.abs(pos.getZ() - NODE_POS.getZ());
            return Math.max(dx, Math.max(dy, dz)) == 1;
        }
    }
}
