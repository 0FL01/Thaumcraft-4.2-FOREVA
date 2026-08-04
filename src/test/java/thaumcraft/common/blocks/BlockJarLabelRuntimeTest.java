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
import net.minecraft.nbt.NBTTagCompound;
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
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.tiles.TileJarFillable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class BlockJarLabelRuntimeTest {
    private static final BlockPos POS = new BlockPos(0, 64, 0);

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
        if (ConfigBlocks.blockJar == null) ConfigBlocks.init();
        if (ConfigItems.itemResource == null) ConfigItems.init();
    }

    @Test
    public void craftedLabelAppliesRemovesAndCanBeReused() {
        JarWorld world = new JarWorld();
        BlockJar block = ConfigBlocks.blockJar;
        IBlockState state = block.getStateFromMeta(0);
        TestJar jar = new TestJar();
        world.attach(POS, state, jar);
        TestPlayer player = new TestPlayer(world);
        ItemStack label = new ItemStack(ConfigItems.itemResource, 1, 13);
        ConfigItems.itemResource.setAspects(label, new AspectList().add(Aspect.WATER, 0));
        player.setHeldItem(EnumHand.MAIN_HAND, label);

        assertTrue(block.onBlockActivated(world, POS, state, player, EnumHand.MAIN_HAND,
                EnumFacing.NORTH, 0.5F, 0.5F, 0.5F));
        assertSame(Aspect.WATER, jar.aspect);
        assertSame(Aspect.WATER, jar.aspectFilter);
        assertTrue(player.getHeldItem(EnumHand.MAIN_HAND).isEmpty());
        assertTrue(world.updateCount > 0);

        player.setSneaking(true);
        assertTrue(block.onBlockActivated(world, POS, state, player, EnumHand.MAIN_HAND,
                EnumFacing.NORTH, 0.5F, 0.5F, 0.5F));
        assertNull(jar.aspectFilter);
        assertEquals(1, world.spawned.size());
        ItemStack returned = world.spawned.get(0).getItem();
        assertSame(ConfigItems.itemResource, returned.getItem());
        assertEquals(13, returned.getMetadata());

        player.setSneaking(false);
        jar.aspect = Aspect.FIRE;
        jar.amount = 12;
        player.setHeldItem(EnumHand.MAIN_HAND, returned.copy());
        assertTrue(block.onBlockActivated(world, POS, state, player, EnumHand.MAIN_HAND,
                EnumFacing.NORTH, 0.5F, 0.5F, 0.5F));
        assertSame(Aspect.FIRE, jar.aspectFilter);
        assertTrue(player.getHeldItem(EnumHand.MAIN_HAND).isEmpty());
    }

    @Test
    public void filledAndFilteredJarItemsAreSingletonsButEmptyJarsStillStack() {
        BlockJarItem item = new BlockJarItem(Blocks.GLASS);
        ItemStack empty = new ItemStack(item, 1, 0);
        assertEquals(64, item.getItemStackLimit(empty));

        ItemStack filled = new ItemStack(item, 1, 0);
        item.setAspects(filled, new AspectList().add(Aspect.AIR, 37));
        assertEquals(1, item.getItemStackLimit(filled));

        ItemStack filteredVoid = new ItemStack(item, 1, 3);
        filteredVoid.setTagCompound(new NBTTagCompound());
        filteredVoid.getTagCompound().setString("AspectFilter", Aspect.EARTH.getTag());
        assertEquals(1, item.getItemStackLimit(filteredVoid));
    }

    private static final class TestJar extends TileJarFillable {
        @Override public void markDirty() { }
    }

    private static final class TestPlayer extends EntityPlayer {
        TestPlayer(World world) {
            super(world, new GameProfile(UUID.randomUUID(), "jar_label_runtime"));
        }
        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
    }

    private static final class JarWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();
        private final List<EntityItem> spawned = new ArrayList<>();
        private int updateCount;

        JarWorld() {
            super(null, new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT),
                    "jar_label_runtime"), new WorldProviderSurface(), new Profiler(), false);
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
        @Override public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
            ++this.updateCount;
        }
        @Override public boolean spawnEntity(Entity entity) {
            if (entity instanceof EntityItem) this.spawned.add((EntityItem) entity);
            return true;
        }
        @Override public void playSound(EntityPlayer player, BlockPos pos, SoundEvent sound, SoundCategory category,
                                        float volume, float pitch) { }

        @Override protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "jar_label_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }
        @Override protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) { return true; }
    }
}
