package thaumcraft.common.blocks;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.profiler.Profiler;
import net.minecraft.item.ItemStack;
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
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.tiles.TileAlembic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class BlockMetalDeviceAlembicInteractionTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
        if (ConfigBlocks.blockJar == null) ConfigBlocks.init();
        if (ConfigItems.itemResource == null) ConfigItems.init();
    }

    @Test
    public void sneakingWithAnEmptyHandClearsAndSynchronizesAlembicEssentia() {
        AlembicWorld world = new AlembicWorld();
        BlockMetalDevice block = new BlockMetalDevice();
        BlockPos pos = new BlockPos(0, 64, 0);
        IBlockState state = block.getStateFromMeta(1);
        TileAlembic alembic = new TestAlembic();
        alembic.aspect = Aspect.AIR;
        alembic.amount = 17;
        world.attach(pos, alembic);

        TestPlayer player = new TestPlayer(world);
        player.setSneaking(true);

        assertTrue(block.onBlockActivated(world, pos, state, player, EnumHand.MAIN_HAND,
                EnumFacing.NORTH, 0.5F, 0.5F, 0.5F));
        assertEquals(0, alembic.amount);
        assertNull(alembic.aspect);
        assertTrue(world.notified.contains(pos));
    }

    @Test
    public void aspectLabelInstallsFilterAndConsumesOneLabel() {
        AlembicWorld world = new AlembicWorld();
        BlockMetalDevice block = ConfigBlocks.blockMetalDevice;
        BlockPos pos = new BlockPos(0, 64, 0);
        IBlockState state = block.getStateFromMeta(1);
        TileAlembic alembic = new TestAlembic();
        world.attach(pos, alembic);
        TestPlayer player = new TestPlayer(world);
        ItemStack label = new ItemStack(ConfigItems.itemResource, 1, 13);
        ConfigItems.itemResource.setAspects(label, new AspectList().add(Aspect.FIRE, 0));
        player.setHeldItem(EnumHand.MAIN_HAND, label);

        assertTrue(block.onBlockActivated(world, pos, state, player, EnumHand.MAIN_HAND,
                EnumFacing.NORTH, 0.5F, 0.5F, 0.5F));

        assertSame(Aspect.FIRE, alembic.aspect);
        assertSame(Aspect.FIRE, alembic.aspectFilter);
        assertTrue(player.getHeldItem(EnumHand.MAIN_HAND).isEmpty());
        assertTrue(world.notified.contains(pos));
    }

    @Test
    public void emptyAndVoidJarsReceiveAlembicContentsWithTc4CapacityRules() {
        AlembicWorld world = new AlembicWorld();
        BlockMetalDevice block = ConfigBlocks.blockMetalDevice;
        BlockPos pos = new BlockPos(0, 64, 0);
        IBlockState state = block.getStateFromMeta(1);
        TileAlembic alembic = new TestAlembic();
        alembic.aspect = Aspect.AIR;
        alembic.amount = 20;
        world.attach(pos, alembic);
        TestPlayer player = new TestPlayer(world);
        BlockJarItem jarItem = new BlockJarItem(ConfigBlocks.blockJar);
        ItemStack jar = new ItemStack(jarItem, 1, 0);
        player.setHeldItem(EnumHand.MAIN_HAND, jar);

        assertTrue(block.onBlockActivated(world, pos, state, player, EnumHand.MAIN_HAND,
                EnumFacing.NORTH, 0.5F, 0.5F, 0.5F));
        assertEquals(20, jarItem.getAspects(jar).getAmount(Aspect.AIR));
        assertEquals(0, alembic.amount);
        assertNull(alembic.aspect);

        alembic.aspect = Aspect.AIR;
        alembic.amount = 32;
        ItemStack voidJar = new ItemStack(jarItem, 1, 3);
        jarItem.setAspects(voidJar, new AspectList().add(Aspect.AIR, 60));
        player.setHeldItem(EnumHand.MAIN_HAND, voidJar);

        assertTrue(block.onBlockActivated(world, pos, state, player, EnumHand.MAIN_HAND,
                EnumFacing.NORTH, 0.5F, 0.5F, 0.5F));
        assertEquals(64, jarItem.getAspects(voidJar).getAmount(Aspect.AIR));
        assertEquals(0, alembic.amount);
        assertNull(alembic.aspect);
    }

    @Test
    public void extractionSynchronizesAndBreakingFilteredAlembicDropsLabel() {
        AlembicWorld world = new AlembicWorld();
        BlockMetalDevice block = ConfigBlocks.blockMetalDevice;
        BlockPos pos = new BlockPos(0, 64, 0);
        IBlockState state = block.getStateFromMeta(1);
        TileAlembic alembic = new TestAlembic();
        alembic.aspect = Aspect.ORDER;
        alembic.aspectFilter = Aspect.ORDER;
        alembic.amount = 8;
        world.attach(pos, alembic);

        assertTrue(alembic.takeFromContainer(Aspect.ORDER, 1));
        assertTrue(world.notified.contains(pos));
        world.notified.clear();

        block.breakBlock(world, pos, state);

        assertEquals(1, world.spawned.size());
        ItemStack drop = world.spawned.get(0).getItem();
        assertSame(ConfigItems.itemResource, drop.getItem());
        assertEquals(13, drop.getMetadata());
    }

    private static class TestPlayer extends EntityPlayer {
        TestPlayer(World world) {
            super(world, new GameProfile(UUID.randomUUID(), "alembic_test"));
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

    private static class TestAlembic extends TileAlembic {
        @Override
        public void markDirty() {
        }
    }

    private static class AlembicWorld extends World {
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();
        private final Set<BlockPos> notified = new HashSet<>();
        private final List<EntityItem> spawned = new ArrayList<>();

        AlembicWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT), "alembic_interaction"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        void attach(BlockPos pos, TileEntity tile) {
            tile.setWorld(this);
            tile.setPos(pos);
            this.tiles.put(pos, tile);
        }

        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return this.tiles.get(pos);
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            return Blocks.AIR.getDefaultState();
        }

        @Override
        public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
            this.notified.add(pos.toImmutable());
        }

        @Override
        public boolean spawnEntity(Entity entity) {
            if (entity instanceof EntityItem) this.spawned.add((EntityItem) entity);
            return true;
        }

        @Override
        public void removeTileEntity(BlockPos pos) {
            this.tiles.remove(pos);
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
                @Override public String makeString() { return "alembic_interaction_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
