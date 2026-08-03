package thaumcraft.common.items.equipment;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
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
import thaumcraft.common.entities.EntityFollowingItem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ItemPrimalCrusherParityTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void effectiveSetAndSpeedMatchTc4() throws Exception {
        Field field = ItemPrimalCrusher.class.getDeclaredField("EFFECTIVE_BLOCKS");
        field.setAccessible(true);
        Set<Block> effective = (Set<Block>) field.get(null);

        assertTrue(effective.contains(Blocks.ICE));
        assertTrue(effective.contains(Blocks.RAIL));
        assertTrue(effective.contains(Blocks.DETECTOR_RAIL));
        assertTrue(effective.contains(Blocks.GOLDEN_RAIL));
        assertTrue(effective.contains(Blocks.ACTIVATOR_RAIL));
        assertFalse(effective.contains(Blocks.EMERALD_ORE));
        assertFalse(effective.contains(Blocks.QUARTZ_ORE));
        assertFalse(effective.contains(Blocks.WEB));
        assertFalse(effective.contains(Blocks.CONCRETE));

        ItemPrimalCrusher crusher = new ItemPrimalCrusher(Item.ToolMaterial.DIAMOND);
        ItemStack stack = new ItemStack(crusher);
        assertEquals(8.0F, crusher.getDestroySpeed(stack, Blocks.STONE.getDefaultState()), 0.0F);
        assertEquals(1.0F, crusher.getDestroySpeed(stack, Blocks.GLASS.getDefaultState()), 0.0F);
        assertEquals(8.0F, crusher.getDestroySpeed(stack, Blocks.DIRT.getDefaultState()), 0.0F);
    }

    @Test
    public void aoeUsesPlayerHarvestCreatesFollowingDropsAndChargesOnlySecondaries() {
        TestWorld world = new TestWorld();
        TestPlayer player = new TestPlayer(world);
        ItemPrimalCrusher crusher = new ItemPrimalCrusher(Item.ToolMaterial.DIAMOND);
        ItemStack stack = new ItemStack(crusher);
        player.inventory.mainInventory.set(0, stack);
        player.inventory.currentItem = 0;

        BlockPos center = new BlockPos(0, 64, 0);
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                world.states.put(center.add(x, 0, z), Blocks.STONE.getDefaultState());
            }
        }
        world.states.put(center, Blocks.AIR.getDefaultState());

        assertTrue(crusher.onBlockDestroyed(stack, world, Blocks.STONE.getDefaultState(), center, player));

        int following = 0;
        for (Entity entity : world.entities) {
            if (!entity.isDead && entity instanceof EntityFollowingItem) following++;
        }
        assertEquals(8, following);
        assertEquals(8, stack.getItemDamage());
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                assertTrue(world.getBlockState(center.add(x, 0, z)).getBlock() == Blocks.AIR);
            }
        }
    }

    private static final class TestPlayer extends EntityPlayer {
        private TestPlayer(World world) {
            super(world, new GameProfile(
                    UUID.fromString("1dfd9ec9-ed58-4dcc-b7a2-d635dfdf6a04"), "crusher_test"));
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
    }

    private static final class TestWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();
        private final List<Entity> entities = new ArrayList<>();

        private TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT), "crusher"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            IBlockState state = this.states.get(pos);
            return state == null ? Blocks.AIR.getDefaultState() : state;
        }

        @Override
        public boolean setBlockState(BlockPos pos, IBlockState state, int flags) {
            this.states.put(pos.toImmutable(), state);
            return true;
        }

        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return null;
        }

        @Override
        public boolean isBlockModifiable(EntityPlayer player, BlockPos pos) {
            return true;
        }

        @Override
        public boolean spawnEntity(Entity entity) {
            this.entities.add(entity);
            return true;
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
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "crusher_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
