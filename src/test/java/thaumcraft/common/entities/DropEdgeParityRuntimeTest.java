package thaumcraft.common.entities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.AxisAlignedBB;
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
import thaumcraft.common.entities.golems.EntityTravelingTrunk;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.entities.monster.boss.EntityTaintacleGiant;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DropEdgeParityRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void pechUsesOriginalBaselineAndWandDropChances() throws IOException {
        TestPech pech = new TestPech(new TestWorld());

        assertEquals(0.2F, pech.getMainhandDropChance(), 0.0F);

        String source = new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/common/entities/monster/EntityPech.java")), StandardCharsets.UTF_8);
        assertTrue(source.contains("this.setDropChance(EntityEquipmentSlot.MAINHAND, 0.2F);"));
        assertTrue(source.contains("this.setDropChance(EntityEquipmentSlot.MAINHAND, 0.1F);"));
    }

    @Test
    public void giantTaintacleSuppressesUniqueDropAcrossThirtyTwoVerticalBlocks() {
        TestWorld world = new TestWorld();
        TestTaintacleGiant source = new TestTaintacleGiant(world);
        TestTaintacleGiant other = new TestTaintacleGiant(world);
        source.setPosition(0.0D, 0.0D, 0.0D);
        other.setPosition(0.0D, 32.0D, 0.0D);
        world.entities.add(source);
        world.entities.add(other);

        source.dropFewItemsForTest();

        assertEquals(-48.0D, world.lastEntityQuery.minY, 0.0D);
        assertEquals(48.0D, world.lastEntityQuery.maxY, 0.0D);
        assertTrue(world.drops.isEmpty());
    }

    @Test
    public void travelingTrunkDropsVisibleAndHiddenPersistedSlots() {
        TestWorld world = new TestWorld();
        EntityTravelingTrunk trunk = new EntityTravelingTrunk(world);
        NBTTagList inventory = new NBTTagList();
        inventory.appendTag(stackTag(0, new ItemStack(Items.IRON_INGOT, 2)));
        inventory.appendTag(stackTag(35, new ItemStack(Items.DIAMOND, 3)));
        trunk.inventory.readFromNBT(inventory);

        assertEquals(27, trunk.inventory.getSizeInventory());
        trunk.inventory.dropAllItems();

        assertEquals(2, droppedCount(world, Items.IRON_INGOT));
        assertEquals(3, droppedCount(world, Items.DIAMOND));
        assertTrue(trunk.inventory.getStackInSlot(0).isEmpty());
        assertTrue(trunk.inventory.getStackInSlot(35).isEmpty());
    }

    private static NBTTagCompound stackTag(int slot, ItemStack stack) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByte("Slot", (byte) slot);
        stack.writeToNBT(tag);
        return tag;
    }

    private static int droppedCount(TestWorld world, net.minecraft.item.Item item) {
        int count = 0;
        for (EntityItem drop : world.drops) {
            if (drop.getItem().getItem() == item) {
                count += drop.getItem().getCount();
            }
        }
        return count;
    }

    private static final class TestPech extends EntityPech {
        private TestPech(World world) {
            super(world);
        }

        private float getMainhandDropChance() {
            return this.inventoryHandsDropChances[0];
        }
    }

    private static final class TestTaintacleGiant extends EntityTaintacleGiant {
        private TestTaintacleGiant(World world) {
            super(world);
        }

        private void dropFewItemsForTest() {
            this.dropFewItems(false, 0);
        }
    }

    private static final class TestWorld extends World {
        private final List<Entity> entities = new ArrayList<>();
        private final List<EntityItem> drops = new ArrayList<>();
        private AxisAlignedBB lastEntityQuery;

        private TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "drop_edge_parity"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        public boolean spawnEntity(Entity entity) {
            if (entity instanceof EntityItem) {
                this.drops.add((EntityItem) entity);
            }
            return true;
        }

        @Override
        public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> type, AxisAlignedBB bounds) {
            this.lastEntityQuery = bounds;
            List<T> matches = new ArrayList<>();
            for (Entity entity : this.entities) {
                if (type.isInstance(entity) && entity.getEntityBoundingBox().intersects(bounds)) {
                    matches.add(type.cast(entity));
                }
            }
            return matches;
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "drop_edge_parity_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }

        @Override
        public IBlockState getBlockState(net.minecraft.util.math.BlockPos pos) {
            return Blocks.AIR.getDefaultState();
        }
    }
}
