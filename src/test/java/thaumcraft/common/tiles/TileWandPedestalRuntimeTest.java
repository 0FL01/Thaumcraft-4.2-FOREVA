package thaumcraft.common.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
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
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.items.baubles.ItemAmuletVis;
import thaumcraft.common.items.wands.ItemWandCasting;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TileWandPedestalRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
        if (ConfigBlocks.blockStoneDevice == null) {
            ConfigBlocks.init();
        }
    }

    @Test
    public void slotAutomationAndAspectViewAreRestrictedToWandsAndAmulets() {
        TestWandPedestal pedestal = new TestWandPedestal();
        ItemWandCasting wandItem = new ItemWandCasting();
        ItemStack wand = new ItemStack(wandItem);
        ItemStack amulet = new ItemStack(new ItemAmuletVis());

        assertTrue(pedestal.isItemValidForSlot(0, wand));
        assertTrue(pedestal.isItemValidForSlot(0, amulet));
        assertFalse(pedestal.isItemValidForSlot(0, new ItemStack(Items.STICK)));
        assertTrue(pedestal.canInsertItem(0, wand, EnumFacing.UP));

        ItemWandCasting.setVis(wand, Aspect.AIR, 250);
        pedestal.setInventorySlotContents(0, wand);
        assertFalse(pedestal.canInsertItem(0, amulet, EnumFacing.UP));
        assertTrue(pedestal.canExtractItem(0, wand, EnumFacing.DOWN));
        assertNotNull(pedestal.getAspects());
        assertEquals(2, pedestal.getAspects().getAmount(Aspect.AIR));
        assertTrue(pedestal.doesContainerAccept(Aspect.AIR));
        assertEquals(0, pedestal.addToContainer(Aspect.AIR, 1));
        assertFalse(pedestal.takeFromContainer(Aspect.AIR, 1));
        assertFalse(pedestal.doesContainerContainAmount(Aspect.AIR, 1));
        assertEquals(0, pedestal.containerContains(Aspect.AIR));

        pedestal.setInventorySlotContents(0, ItemStack.EMPTY);
        assertNull(pedestal.getAspects());
    }

    @Test
    public void nearbyNaturalNodeChargesWandAndJarNodeIsExcluded() {
        PedestalWorld world = new PedestalWorld();
        TestWandPedestal pedestal = attachPedestal(world);
        TestNode node = new TestNode(new AspectList().add(Aspect.FIRE, 2));
        world.attach(new BlockPos(2, 64, 0), node);
        ItemWandCasting wandItem = new ItemWandCasting();
        ItemStack wand = configuredWand(wandItem);
        pedestal.setInventorySlotContents(0, wand);

        tick(pedestal, 5);

        assertEquals(100, ItemWandCasting.getVis(wand, Aspect.FIRE));
        assertEquals(1, node.getAspects().getAmount(Aspect.FIRE));

        PedestalWorld jarWorld = new PedestalWorld();
        TestWandPedestal jarPedestal = attachPedestal(jarWorld);
        TestJarNode jarNode = new TestJarNode();
        jarNode.setAspects(new AspectList().add(Aspect.FIRE, 2));
        jarWorld.attach(new BlockPos(2, 64, 0), jarNode);
        ItemStack jarWand = configuredWand(wandItem);
        jarPedestal.setInventorySlotContents(0, jarWand);

        tick(jarPedestal, 5);

        assertEquals(0, ItemWandCasting.getVis(jarWand, Aspect.FIRE));
        assertEquals(2, jarNode.getAspects().getAmount(Aspect.FIRE));
    }

    @Test
    public void compoundAspectRequiresRechargeFocusAbovePedestal() {
        PedestalWorld world = new PedestalWorld();
        TestWandPedestal pedestal = attachPedestal(world);
        TestNode node = new TestNode(new AspectList().add(Aspect.MOTION, 3));
        world.attach(new BlockPos(2, 64, 0), node);
        ItemWandCasting wandItem = new ItemWandCasting();
        ItemStack wand = configuredWand(wandItem);
        pedestal.setInventorySlotContents(0, wand);

        tick(pedestal, 5);
        assertEquals(0, wandItem.getAllVis(wand).visSize());
        assertEquals(3, node.getAspects().getAmount(Aspect.MOTION));

        world.putState(pedestal.getPos().up(), ConfigBlocks.blockStoneDevice.getStateFromMeta(8));
        tick(pedestal, 5);

        assertEquals(100, wandItem.getAllVis(wand).visSize());
        assertEquals(2, node.getAspects().getAmount(Aspect.MOTION));
    }

    @Test
    public void nearbyNaturalNodeChargesVisAmulet() {
        PedestalWorld world = new PedestalWorld();
        TestWandPedestal pedestal = attachPedestal(world);
        TestNode node = new TestNode(new AspectList().add(Aspect.ORDER, 2));
        world.attach(new BlockPos(0, 66, 0), node);
        ItemAmuletVis amuletItem = new ItemAmuletVis();
        ItemStack amulet = new ItemStack(amuletItem);
        pedestal.setInventorySlotContents(0, amulet);

        tick(pedestal, 5);

        assertEquals(100, amuletItem.getVis(amulet, Aspect.ORDER));
        assertEquals(1, node.getAspects().getAmount(Aspect.ORDER));
    }

    private static TestWandPedestal attachPedestal(PedestalWorld world) {
        TestWandPedestal pedestal = new TestWandPedestal();
        world.attach(new BlockPos(0, 64, 0), pedestal);
        world.putState(pedestal.getPos(), ConfigBlocks.blockStoneDevice.getStateFromMeta(5));
        return pedestal;
    }

    private static ItemStack configuredWand(ItemWandCasting item) {
        WandCap cap = new WandCap("pedestal_test_cap", 1.0F, new ItemStack(Items.IRON_NUGGET), 1);
        WandRod rod = new WandRod("pedestal_test_rod", 100, new ItemStack(Items.STICK), 1);
        ItemStack stack = new ItemStack(item);
        ItemWandCasting.setCap(stack, cap);
        ItemWandCasting.setRod(stack, rod);
        return stack;
    }

    private static void tick(TileWandPedestal pedestal, int count) {
        for (int i = 0; i < count; i++) {
            pedestal.update();
        }
    }

    private static class TestWandPedestal extends TileWandPedestal {
        @Override
        public void markDirty() {
        }
    }

    private static class TestJarNode extends TileJarNode {
        @Override
        public void markDirty() {
        }
    }

    private static class TestNode extends TileEntity implements INode {
        private AspectList aspects;
        private AspectList base;
        private NodeType type = NodeType.NORMAL;
        private NodeModifier modifier;

        TestNode(AspectList aspects) {
            this.aspects = aspects.copy();
            this.base = aspects.copy();
        }

        @Override public String getId() { return "pedestal_test_node"; }
        @Override public AspectList getAspects() { return this.aspects; }
        @Override public AspectList getAspectsBase() { return this.base; }
        @Override public void setAspects(AspectList aspects) { this.aspects = aspects.copy(); }
        @Override public NodeType getNodeType() { return this.type; }
        @Override public void setNodeType(NodeType type) { this.type = type; }
        @Override public void setNodeModifier(NodeModifier modifier) { this.modifier = modifier; }
        @Override public NodeModifier getNodeModifier() { return this.modifier; }
        @Override public int getNodeVisBase(Aspect aspect) { return this.base.getAmount(aspect); }
        @Override public void setNodeVisBase(Aspect aspect, short amount) { this.base.merge(aspect, amount); }
        @Override public boolean doesContainerAccept(Aspect aspect) { return true; }
        @Override public int addToContainer(Aspect aspect, int amount) { this.aspects.add(aspect, amount); return 0; }
        @Override public boolean takeFromContainer(Aspect aspect, int amount) {
            return this.aspects.getAmount(aspect) >= amount && this.aspects.remove(aspect, amount) != null;
        }
        @Override public boolean takeFromContainer(AspectList aspects) { return false; }
        @Override public boolean doesContainerContainAmount(Aspect aspect, int amount) {
            return this.aspects.getAmount(aspect) >= amount;
        }
        @Override public boolean doesContainerContain(AspectList aspects) { return false; }
        @Override public int containerContains(Aspect aspect) { return this.aspects.getAmount(aspect); }
    }

    private static class PedestalWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();

        PedestalWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT),
                            "wand_pedestal_charge_runtime"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        void attach(BlockPos pos, TileEntity tile) {
            tile.setWorld(this);
            tile.setPos(pos);
            this.tiles.put(pos.toImmutable(), tile);
        }

        void putState(BlockPos pos, IBlockState state) {
            this.states.put(pos.toImmutable(), state);
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            IBlockState state = this.states.get(pos);
            return state == null ? Blocks.AIR.getDefaultState() : state;
        }

        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return this.tiles.get(pos);
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
                @Override public String makeString() { return "wand_pedestal_charge_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
