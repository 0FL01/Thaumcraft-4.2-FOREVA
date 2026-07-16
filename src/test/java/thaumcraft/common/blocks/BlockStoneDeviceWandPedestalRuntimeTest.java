package thaumcraft.common.blocks;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
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
import thaumcraft.common.blocks.ItemBlocks.BlockStoneDeviceItem;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.items.baubles.ItemAmuletVis;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.TileWandPedestal;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class BlockStoneDeviceWandPedestalRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
        if (ConfigBlocks.blockStoneDevice == null) {
            ConfigBlocks.init();
        }
    }

    @Test
    public void basePedestalInsertsAndExtractsOnlyRechargeableItems() {
        PedestalWorld world = new PedestalWorld(false);
        BlockStoneDevice block = ConfigBlocks.blockStoneDevice;
        BlockPos pos = new BlockPos(0, 64, 0);
        TestWandPedestal pedestal = new TestWandPedestal();
        world.attach(pos, block.getStateFromMeta(5), pedestal);
        TestPlayer player = new TestPlayer(world);
        ItemWandCasting wandItem = new ItemWandCasting();

        player.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(wandItem));
        assertTrue(block.onBlockActivated(world, pos, block.getStateFromMeta(5), player, EnumHand.MAIN_HAND,
                EnumFacing.UP, 0.5F, 0.5F, 0.5F));
        assertSame(wandItem, pedestal.getStackInSlot(0).getItem());
        assertTrue(player.getHeldItem(EnumHand.MAIN_HAND).isEmpty());

        assertTrue(block.onBlockActivated(world, pos, block.getStateFromMeta(5), player, EnumHand.MAIN_HAND,
                EnumFacing.UP, 0.5F, 0.5F, 0.5F));
        assertTrue(pedestal.getStackInSlot(0).isEmpty());
        assertTrue(playerHasItem(player, wandItem));

        player.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.STICK));
        assertFalse(block.onBlockActivated(world, pos, block.getStateFromMeta(5), player, EnumHand.MAIN_HAND,
                EnumFacing.UP, 0.5F, 0.5F, 0.5F));
        assertTrue(pedestal.getStackInSlot(0).isEmpty());
    }

    @Test
    public void compoundFocusRedirectsToPedestalButRemainsPlaceable() {
        PedestalWorld world = new PedestalWorld(false);
        BlockStoneDevice block = ConfigBlocks.blockStoneDevice;
        BlockPos basePos = new BlockPos(0, 64, 0);
        BlockPos focusPos = basePos.up();
        TestWandPedestal pedestal = new TestWandPedestal();
        world.attach(basePos, block.getStateFromMeta(5), pedestal);
        world.putState(focusPos, block.getStateFromMeta(8));
        TestPlayer player = new TestPlayer(world);
        ItemAmuletVis amuletItem = new ItemAmuletVis();

        player.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(amuletItem));
        assertTrue(block.onBlockActivated(world, focusPos, block.getStateFromMeta(8), player, EnumHand.MAIN_HAND,
                EnumFacing.UP, 0.5F, 0.5F, 0.5F));
        assertSame(amuletItem, pedestal.getStackInSlot(0).getItem());

        player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
        assertTrue(block.onBlockActivated(world, focusPos, block.getStateFromMeta(8), player, EnumHand.MAIN_HAND,
                EnumFacing.UP, 0.5F, 0.5F, 0.5F));
        assertTrue(pedestal.getStackInSlot(0).isEmpty());

        ItemStack focus = new ItemStack(new BlockStoneDeviceItem(block), 1, 8);
        player.setHeldItem(EnumHand.MAIN_HAND, focus);
        assertFalse(block.onBlockActivated(world, basePos, block.getStateFromMeta(5), player, EnumHand.MAIN_HAND,
                EnumFacing.UP, 0.5F, 0.5F, 0.5F));
        assertTrue(pedestal.getStackInSlot(0).isEmpty());
        assertEquals(1, focus.getCount());
    }

    @Test
    public void comparatorUsesTotalWandFillBeforeGenericPedestalInventoryLevel() {
        PedestalWorld world = new PedestalWorld(false);
        BlockStoneDevice block = ConfigBlocks.blockStoneDevice;
        BlockPos pos = new BlockPos(0, 64, 0);
        TestWandPedestal pedestal = new TestWandPedestal();
        world.attach(pos, block.getStateFromMeta(5), pedestal);
        ItemWandCasting wandItem = new ItemWandCasting();
        ItemStack wand = new ItemStack(wandItem);
        int max = ItemWandCasting.getMaxVis(wand);
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            ItemWandCasting.setVis(wand, aspect, max / 2);
        }
        pedestal.setInventorySlotContents(0, wand);

        assertEquals(8, block.getComparatorInputOverride(block.getStateFromMeta(5), world, pos));
    }

    private static boolean playerHasItem(EntityPlayer player, net.minecraft.item.Item item) {
        for (ItemStack stack : player.inventory.mainInventory) {
            if (!stack.isEmpty() && stack.getItem() == item) return true;
        }
        return false;
    }

    private static class TestPlayer extends EntityPlayer {
        TestPlayer(World world) {
            super(world, new GameProfile(UUID.randomUUID(), "wand_pedestal_test"));
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

    private static class TestWandPedestal extends TileWandPedestal {
        @Override
        public void markDirty() {
        }
    }

    private static class PedestalWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();

        PedestalWorld(boolean remote) {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT),
                            "wand_pedestal_runtime"),
                    new WorldProviderSurface(), new Profiler(), remote);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        void attach(BlockPos pos, IBlockState state, TileEntity tile) {
            this.putState(pos, state);
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
        public void playSound(EntityPlayer player, BlockPos pos, SoundEvent sound, SoundCategory category,
                              float volume, float pitch) {
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "wand_pedestal_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
