package thaumcraft.common.tiles;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
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
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.StaffRod;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TileNodeWandDrainRuntimeTest {
    private static final String TEST_STAFF_TAG = "node_drain_test_staff";

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
        if (ConfigBlocks.blockAiry == null) {
            ConfigBlocks.init();
        }
    }

    @After
    public void removeTestStaffRod() {
        WandRod.rods.remove(TEST_STAFF_TAG);
    }

    @Test
    public void wandStaffAndSceptreDrainNonCollidingAuraNodesWhileTargeted() {
        ItemWandCasting wandItem = new ItemWandCasting();
        ItemStack wand = new ItemStack(wandItem);

        ItemStack staff = new ItemStack(wandItem);
        ItemWandCasting.setRod(staff,
                new StaffRod("node_drain_test", 100, new ItemStack(Items.STICK), 1));
        assertTrue(wandItem.isStaff(staff));

        ItemStack sceptre = new ItemStack(wandItem);
        ItemWandCasting.ensureTag(sceptre).setBoolean("sceptre", true);
        assertTrue(ItemWandCasting.isSceptre(sceptre));

        assertDrainsNode(wandItem, wand);
        assertDrainsNode(wandItem, staff);
        assertDrainsNode(wandItem, sceptre);
    }

    private static void assertDrainsNode(ItemWandCasting wandItem, ItemStack wandStack) {
        NodeWorld world = new NodeWorld();
        TestTileNode node = new TestTileNode();
        node.setAspects(new AspectList().add(Aspect.AIR, 5));
        world.attachNode(node);

        TestPlayer player = new TestPlayer(world);
        player.setPosition(0.5D, NodeWorld.NODE_POS.getY() + 0.5D - player.getEyeHeight(), 0.5D);
        player.rotationYaw = 0.0F;
        player.rotationYawHead = 0.0F;
        player.rotationPitch = 0.0F;
        player.setHeldItem(EnumHand.MAIN_HAND, wandStack);
        ItemWandCasting.setVis(wandStack, Aspect.AIR, 1);

        ActionResult<ItemStack> result = wandItem.onItemRightClick(world, player, EnumHand.MAIN_HAND);

        assertSame(EnumActionResult.SUCCESS, result.getType());
        assertSame(wandStack, result.getResult());
        assertTrue(player.isHandActive());
        assertSame(EnumHand.MAIN_HAND, player.getActiveHand());
        assertSame(node, wandItem.getObjectInUse(wandStack, world));

        wandItem.onUsingTick(wandStack, player, 5);

        assertTrue("Targeting the node must keep wand use active", player.isHandActive());
        assertEquals(101, ItemWandCasting.getVis(wandStack, Aspect.AIR));
        assertEquals(4, node.getAspects().getAmount(Aspect.AIR));

        player.rotationYaw = 180.0F;
        player.rotationYawHead = 180.0F;
        wandItem.onUsingTick(wandStack, player, 4);
        assertFalse("Looking away must stop node draining", player.isHandActive());
    }

    private static class TestPlayer extends EntityPlayer {
        TestPlayer(World world) {
            super(world, new GameProfile(UUID.randomUUID(), "node_drain_test"));
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            if (capability == null || capability == PlayerKnowledgeProvider.PLAYER_KNOWLEDGE) {
                return null;
            }
            return super.getCapability(capability, facing);
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

    private static class TestTileNode extends TileNode {
        @Override
        public void markDirty() {
        }
    }

    private static class NodeWorld extends World {
        private static final BlockPos NODE_POS = new BlockPos(0, 64, 3);
        private final IBlockState nodeState = ConfigBlocks.blockAiry.getStateFromMeta(0);
        private TileNode node;

        NodeWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "node_drain_runtime"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        void attachNode(TileNode node) {
            node.setWorld(this);
            node.setPos(NODE_POS);
            this.node = node;
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            return NODE_POS.equals(pos) ? this.nodeState : net.minecraft.init.Blocks.AIR.getDefaultState();
        }

        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return NODE_POS.equals(pos) ? this.node : null;
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
                @Override public String makeString() { return "node_drain_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
