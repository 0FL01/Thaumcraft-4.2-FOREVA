package thaumcraft.common.blocks;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
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
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemResource;
import thaumcraft.common.tiles.TileTubeFilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class BlockTubeFilterInteractionRuntimeTest {
    private static final BlockPos POS = new BlockPos(0, 64, 0);

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
        if (ConfigBlocks.blockTube == null) ConfigBlocks.init();
        if (ConfigItems.itemResource == null) ConfigItems.init();
    }

    @Test
    public void filterAcceptsOnlyCraftedLabelsAndReturnsOneWhenBroken() {
        TubeWorld world = new TubeWorld();
        BlockTube block = ConfigBlocks.blockTube;
        IBlockState state = block.getStateFromMeta(3);
        TestFilter filter = new TestFilter();
        world.attach(POS, state, filter);
        TestPlayer player = new TestPlayer(world);

        ItemStack essence = new ItemStack(ConfigItems.itemEssence, 1, 1);
        ConfigItems.itemEssence.setAspects(essence, new AspectList().add(Aspect.AIR, 8));
        player.setHeldItem(EnumHand.MAIN_HAND, essence);
        assertFalse(block.onBlockActivated(world, POS, state, player, EnumHand.MAIN_HAND,
                EnumFacing.NORTH, 0.5F, 0.5F, 0.5F));
        assertNull(filter.aspectFilter);
        assertEquals(1, essence.getCount());

        ItemStack label = new ItemStack(ConfigItems.itemResource, 1, ItemResource.META_LABEL);
        ConfigItems.itemResource.setAspects(label, new AspectList().add(Aspect.AIR, 0));
        player.setHeldItem(EnumHand.MAIN_HAND, label);
        assertTrue(block.onBlockActivated(world, POS, state, player, EnumHand.MAIN_HAND,
                EnumFacing.NORTH, 0.5F, 0.5F, 0.5F));
        assertSame(Aspect.AIR, filter.aspectFilter);
        assertTrue(label.isEmpty());

        block.breakBlock(world, POS, state);
        assertEquals(1, world.spawned.size());
        ItemStack returned = world.spawned.get(0).getItem();
        assertSame(ConfigItems.itemResource, returned.getItem());
        assertEquals(ItemResource.META_LABEL, returned.getMetadata());
    }

    private static final class TestFilter extends TileTubeFilter {
        @Override public void markDirtyAndSync() { }
    }

    private static final class TestPlayer extends EntityPlayer {
        TestPlayer(World world) {
            super(world, new GameProfile(UUID.randomUUID(), "tube_filter_runtime"));
        }
        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return true; }
    }

    private static final class TubeWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();
        private final List<EntityItem> spawned = new ArrayList<>();

        TubeWorld() {
            super(null, new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT),
                    "tube_filter_runtime"), new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        void attach(BlockPos pos, IBlockState state, TileEntity tile) {
            this.states.put(pos.toImmutable(), state);
            tile.setWorld(this);
            tile.setPos(pos);
            this.tiles.put(pos.toImmutable(), tile);
        }

        @Override public IBlockState getBlockState(BlockPos pos) {
            IBlockState state = this.states.get(pos);
            return state == null ? Blocks.AIR.getDefaultState() : state;
        }
        @Override public TileEntity getTileEntity(BlockPos pos) { return this.tiles.get(pos); }
        @Override public void removeTileEntity(BlockPos pos) { this.tiles.remove(pos); }
        @Override public boolean spawnEntity(Entity entity) {
            if (entity instanceof EntityItem) this.spawned.add((EntityItem) entity);
            return true;
        }

        @Override protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "tube_filter_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }
        @Override protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) { return true; }
    }
}
