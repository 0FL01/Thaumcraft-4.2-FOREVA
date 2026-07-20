package thaumcraft.common.tiles;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
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
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.api.wands.IWandable;
import thaumcraft.api.wands.StaffRod;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.blocks.BlockAiry;
import thaumcraft.common.blocks.BlockJar;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.items.wands.ItemWandCasting;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TileJarNodeReleaseRuntimeTest {
    private static final BlockPos JAR_POS = new BlockPos(4, 70, -3);
    private static final String TEST_STAFF_TAG = "jar_release_test_staff";

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
    public void wandStaffAndSceptreReleaseTheStoredNodeWithItsAttributes() {
        ItemWandCasting wandItem = new ItemWandCasting();
        ItemStack wand = new ItemStack(wandItem);

        ItemStack staff = new ItemStack(wandItem);
        ItemWandCasting.setRod(staff,
                new StaffRod("jar_release_test", 100, new ItemStack(Items.STICK), 1));
        assertTrue(wandItem.isStaff(staff));

        ItemStack sceptre = new ItemStack(wandItem);
        ItemWandCasting.ensureTag(sceptre).setBoolean("sceptre", true);
        assertTrue(ItemWandCasting.isSceptre(sceptre));

        assertRelease(wandItem, wand);
        assertRelease(wandItem, staff);
        assertRelease(wandItem, sceptre);
    }

    private static void assertRelease(ItemWandCasting wandItem, ItemStack wandStack) {
        ReleaseWorld world = new ReleaseWorld();
        TileJarNode jar = new TileJarNode();
        jar.setAspects(new AspectList().add(Aspect.MAGIC, 31).add(Aspect.AIR, 17));
        jar.setNodeType(NodeType.HUNGRY);
        jar.setNodeModifier(NodeModifier.BRIGHT);
        jar.setId("released-node");
        world.attachJar(jar);

        TestPlayer player = new TestPlayer(world);
        player.setHeldItem(EnumHand.MAIN_HAND, wandStack);

        EnumActionResult result = wandItem.onItemUseFirst(player, world, JAR_POS, EnumFacing.UP,
                0.5F, 0.5F, 0.5F, EnumHand.MAIN_HAND);

        assertSame(EnumActionResult.PASS, result);
        assertTrue(jar instanceof IWandable);
        assertFalse("Released jars must not also drop as an item", jar.drop);
        assertSame(ConfigBlocks.blockAiry, world.state.getBlock());
        assertEquals(0, world.state.getValue(BlockAiry.TYPE).intValue());
        assertTrue(world.tile instanceof TileNode);

        TileNode node = (TileNode) world.tile;
        assertEquals(31, node.getAspects().getAmount(Aspect.MAGIC));
        assertEquals(17, node.getAspects().getAmount(Aspect.AIR));
        assertSame(NodeType.HUNGRY, node.getNodeType());
        assertSame(NodeModifier.BRIGHT, node.getNodeModifier());
        assertEquals("released-node", node.getId());
        assertEquals(2001, world.playedEvent);
        assertSame(SoundEvents.BLOCK_GLASS_BREAK, world.playedSound);
    }

    private static class TestPlayer extends EntityPlayer {
        TestPlayer(World world) {
            super(world, new GameProfile(UUID.randomUUID(), "jar_release_test"));
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

    private static class ReleaseWorld extends World {
        private IBlockState state = ConfigBlocks.blockJar.getDefaultState().withProperty(BlockJar.TYPE, 2);
        private TileEntity tile;
        private int playedEvent = -1;
        private SoundEvent playedSound;

        ReleaseWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "jar_release_runtime"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        void attachJar(TileJarNode jar) {
            jar.setWorld(this);
            jar.setPos(JAR_POS);
            this.tile = jar;
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            return JAR_POS.equals(pos) ? this.state : net.minecraft.init.Blocks.AIR.getDefaultState();
        }

        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return JAR_POS.equals(pos) ? this.tile : null;
        }

        @Override
        public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
            if (!JAR_POS.equals(pos)) {
                return false;
            }
            this.state = newState;
            TileNode node = new ReleaseTileNode();
            node.setWorld(this);
            node.setPos(pos);
            this.tile = node;
            return true;
        }

        @Override
        public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
        }

        @Override
        public void playEvent(int type, BlockPos pos, int data) {
            this.playedEvent = type;
        }

        @Override
        public void playSound(EntityPlayer player, BlockPos pos, SoundEvent sound, SoundCategory category,
                              float volume, float pitch) {
            this.playedSound = sound;
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "jar_release_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }

    private static class ReleaseTileNode extends TileNode {
        @Override
        public void markDirty() {
        }
    }
}
