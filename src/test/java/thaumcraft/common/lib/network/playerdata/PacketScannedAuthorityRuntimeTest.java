package thaumcraft.common.lib.network.playerdata;

import com.mojang.authlib.GameProfile;
import com.google.common.base.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumFacing;
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
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.api.research.ScanResult;
import thaumcraft.common.items.relics.ItemThaumometer;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeCapability;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.research.ScanManager;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class PacketScannedAuthorityRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void validBlockPayloadMustMatchHeldThaumometerServerTarget() {
        ScanWorld world = new ScanWorld();
        BlockPos pos = new BlockPos(0, 1, 4);
        world.setBlock(pos, Blocks.STONE.getDefaultState());
        world.setHit(new RayTraceResult(new Vec3d(0.5D, 1.0D, 4.5D), EnumFacing.UP, pos));
        ScanPlayer player = new ScanPlayer(world, "scan_block");
        player.setPosition(0.0D, 0.0D, 0.0D);
        player.rotationYaw = 0.0F;
        player.rotationPitch = 0.0F;
        player.inventory.mainInventory.set(player.inventory.currentItem, new ItemStack(new ItemThaumometer()));

        ScanResult serverScan = PacketScannedToServer.findAuthoritativeMatchingScan(
                player, (byte) 1, net.minecraft.item.Item.getIdFromItem(net.minecraft.item.Item.getItemFromBlock(Blocks.STONE)), 0, 0, "", "@");
        assertNotNull(serverScan);

        ScanResult forged = PacketScannedToServer.findAuthoritativeMatchingScan(
                player, (byte) 1, net.minecraft.item.Item.getIdFromItem(net.minecraft.init.Items.DIAMOND), 0, 0, "", "@");
        assertNull(forged);
    }

    @Test
    public void entityPayloadMustMatchServerObservedPointedEntityAndHeldThaumometer() {
        ScanWorld world = new ScanWorld();
        ScanPlayer player = new ScanPlayer(world, "scan_entity");
        player.setPosition(0.0D, 0.0D, 0.0D);
        player.rotationYaw = 0.0F;
        player.rotationPitch = 0.0F;
        player.inventory.mainInventory.set(player.inventory.currentItem, new ItemStack(new ItemThaumometer()));

        TestEntity entity = new TestEntity(world);
        entity.setDimensions(0.9F, 1.8F);
        entity.setPosition(0.0D, 1.0D, 4.0D);
        world.entities.add(entity);

        ScanResult serverScan = PacketScannedToServer.findAuthoritativeMatchingScan(
                player, (byte) 2, 0, 0, entity.getEntityId(), "", "@");
        assertNotNull(serverScan);

        ScanResult forged = PacketScannedToServer.findAuthoritativeMatchingScan(
                player, (byte) 2, 0, 0, entity.getEntityId() + 99, "", "@");
        assertNull(forged);
    }

    @Test
    public void droppedItemEntityMustRemainTargetableForThaumometerScanCompletion() {
        ThaumcraftApi.registerObjectTag(new ItemStack(Items.STICK), new AspectList().add(Aspect.AIR, 1));

        ScanWorld world = new ScanWorld();
        ScanPlayer player = new ScanPlayer(world, "scan_item_entity");
        player.setPosition(0.0D, 0.0D, 0.0D);
        player.rotationYaw = 0.0F;
        player.rotationPitch = 0.0F;
        ItemStack thaumometerStack = new ItemStack(new ItemThaumometer());
        player.inventory.mainInventory.set(player.inventory.currentItem, thaumometerStack);

        NonCollidableItemEntity itemEntity = new NonCollidableItemEntity(world, new ItemStack(Items.STICK));
        itemEntity.setPosition(0.0D, 1.0D, 4.0D);
        world.entities.add(itemEntity);

        ItemThaumometer thaumometer = (ItemThaumometer) thaumometerStack.getItem();
        ScanResult clientTarget = thaumometer.findScanTarget(thaumometerStack, world, player);
        assertNotNull(clientTarget);
        assertEquals(2, clientTarget.type);
        assertSame(itemEntity, clientTarget.entity);
        assertNotNull(PacketScannedToServer.findAuthoritativeMatchingScan(
                player, (byte) 2, 0, 0, itemEntity.getEntityId(), "", "@"));
        assertTrue(ScanManager.completeScan(player, clientTarget, "@"));
        assertTrue(player.knowledge.hasScannedItem("@"
                + ScanManager.generateItemHash(Items.STICK, 0)));
    }

    @Test
    public void nodePhenomenaPayloadMustMatchServerObservedNodeTarget() {
        ScanWorld world = new ScanWorld();
        BlockPos pos = new BlockPos(0, 1, 4);
        world.setBlock(pos, Blocks.STONE.getDefaultState());
        world.setHit(new RayTraceResult(new Vec3d(0.5D, 1.0D, 4.5D), EnumFacing.UP, pos));
        world.node = new TestNodeTile("1:2:3:4");
        world.node.setWorld(world);
        world.node.setPos(pos);

        ScanPlayer player = new ScanPlayer(world, "scan_node");
        player.setPosition(0.0D, 0.0D, 0.0D);
        player.rotationYaw = 0.0F;
        player.rotationPitch = 0.0F;
        player.inventory.mainInventory.set(player.inventory.currentItem, new ItemStack(new ItemThaumometer()));

        assertNotNull(PacketScannedToServer.findAuthoritativeMatchingScan(
                player, (byte) 3, 0, 0, 0, "NODE1:2:3:4", "@"));
        assertNull(PacketScannedToServer.findAuthoritativeMatchingScan(
                player, (byte) 3, 0, 0, 0, "NODE999:9:9:9", "@"));
    }

    @Test
    public void packetMustRejectMissingThaumometerAndNonAtPrefix() {
        ScanWorld world = new ScanWorld();
        BlockPos pos = new BlockPos(0, 1, 4);
        world.setBlock(pos, Blocks.STONE.getDefaultState());
        world.setHit(new RayTraceResult(new Vec3d(0.5D, 1.0D, 4.5D), EnumFacing.UP, pos));
        ScanPlayer player = new ScanPlayer(world, "scan_reject");
        player.setPosition(0.0D, 0.0D, 0.0D);
        player.rotationYaw = 0.0F;
        player.rotationPitch = 0.0F;

        assertNull(PacketScannedToServer.findAuthoritativeMatchingScan(
                player, (byte) 1, net.minecraft.item.Item.getIdFromItem(net.minecraft.item.Item.getItemFromBlock(Blocks.STONE)), 0, 0, "", "@"));

        player.inventory.mainInventory.set(player.inventory.currentItem, new ItemStack(new ItemThaumometer()));
        assertNull(PacketScannedToServer.findAuthoritativeMatchingScan(
                player, (byte) 1, net.minecraft.item.Item.getIdFromItem(net.minecraft.item.Item.getItemFromBlock(Blocks.STONE)), 0, 0, "", "#"));

        assertFalse(PacketScannedToServer.matchesPayload(null, (byte) 1, 1, 0, 0, ""));
    }

    @Test
    public void dispatchMustApplyAuthoritativeScanOnlyForMatchingPlayerAndDimension() {
        ScanWorld world = new ScanWorld();
        BlockPos pos = new BlockPos(0, 1, 4);
        ThaumcraftApi.registerObjectTag(new ItemStack(net.minecraft.item.Item.getItemFromBlock(Blocks.STONE)), new AspectList().add(Aspect.EARTH, 1));
        world.setBlock(pos, Blocks.STONE.getDefaultState());
        world.setHit(new RayTraceResult(new Vec3d(0.5D, 1.0D, 4.5D), EnumFacing.UP, pos));
        ScanPlayer player = new ScanPlayer(world, "scan_dispatch");
        player.setPosition(0.0D, 0.0D, 0.0D);
        player.rotationYaw = 0.0F;
        player.rotationPitch = 0.0F;
        player.inventory.mainInventory.set(player.inventory.currentItem, new ItemStack(new ItemThaumometer()));

        int stoneId = net.minecraft.item.Item.getIdFromItem(net.minecraft.item.Item.getItemFromBlock(Blocks.STONE));
        assertTrue(PacketScannedToServer.dispatch(player, player.getEntityId(), world.provider.getDimension(), (byte) 1, stoneId, 0, 0, "", "@"));
        assertTrue(player.knowledge.hasScannedItem("@" + thaumcraft.common.lib.research.ScanManager.generateItemHash(
                net.minecraft.item.Item.getItemFromBlock(Blocks.STONE), 0)));

        int before = player.knowledge.getScannedItems().size();
        assertFalse(PacketScannedToServer.dispatch(player, player.getEntityId() + 1, world.provider.getDimension(), (byte) 1, stoneId, 0, 0, "", "@"));
        assertFalse(PacketScannedToServer.dispatch(player, player.getEntityId(), world.provider.getDimension() + 1, (byte) 1, stoneId, 0, 0, "", "@"));
        assertEquals(before, player.knowledge.getScannedItems().size());
    }

    private static class ScanPlayer extends EntityPlayer {
        private final PlayerKnowledgeCapability knowledge = new PlayerKnowledgeCapability();

        ScanPlayer(World world, String name) {
            super(world, new GameProfile(UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8)), name));
            this.knowledge.addDiscoveredPrimalAspects();
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

    private static class TestEntity extends Entity {
        TestEntity(World worldIn) {
            super(worldIn);
        }

        void setDimensions(float width, float height) {
            this.setSize(width, height);
        }

        @Override
        protected void entityInit() {
        }

        @Override
        protected void readEntityFromNBT(net.minecraft.nbt.NBTTagCompound compound) {
        }

        @Override
        protected void writeEntityToNBT(net.minecraft.nbt.NBTTagCompound compound) {
        }

        @Override
        public boolean canBeCollidedWith() {
            return true;
        }
    }

    private static class NonCollidableItemEntity extends EntityItem {
        NonCollidableItemEntity(World worldIn, ItemStack stack) {
            super(worldIn, 0.0D, 0.0D, 0.0D, stack);
        }

        @Override
        public boolean canBeCollidedWith() {
            return false;
        }
    }

    private static class TestNodeTile extends thaumcraft.api.TileThaumcraft implements INode {
        private final String id;
        private final AspectList aspects = new AspectList().add(Aspect.AIR, 10);

        TestNodeTile(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return this.id;
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
        private IBlockState state = Blocks.AIR.getDefaultState();
        private BlockPos statePos = BlockPos.ORIGIN;
        private RayTraceResult hit;
        private TestNodeTile node;

        ScanWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT), "scan_world"),
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
        public net.minecraft.tileentity.TileEntity getTileEntity(BlockPos pos) {
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
                    return "scan_dummy";
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
