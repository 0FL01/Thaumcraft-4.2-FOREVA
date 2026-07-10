package thaumcraft.common.items.wands;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.EnumSkyBlock;
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
import thaumcraft.common.tiles.TileAlembic;
import thaumcraft.common.tiles.TileAlchemyFurnace;
import thaumcraft.common.tiles.TileAlchemyFurnaceAdvanced;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WandManagerAdvancedAlchemyFurnaceRuntimeTest {
    private static final BlockPos CENTER = new BlockPos(0, 64, 0);

    @BeforeClass
    public static void bootstrapBlocks() {
        Bootstrap.register();
        if (ConfigBlocks.blockStoneDevice == null || ConfigBlocks.blockMetalDevice == null
                || ConfigBlocks.blockAlchemyFurnace == null) {
            ConfigBlocks.init();
        }
    }

    @Test
    public void upperSourceActivationCreatesDedicatedLayoutAndPreservesCenterContents() {
        FurnaceWorld world = new FurnaceWorld();
        placeSourceLayout(world, 1);
        world.put(CENTER.up(), Blocks.STONE.getDefaultState());
        TileAlchemyFurnace source = (TileAlchemyFurnace) world.getTileEntity(CENTER);
        source.aspects.add(Aspect.FIRE, 7);
        source.vis = source.aspects.visSize();
        source.setInventorySlotContents(0, new ItemStack(Items.STICK, 2));
        source.setInventorySlotContents(1, new ItemStack(Items.COAL, 3));

        assertTrue(WandManager.matchesAdvancedAlchemyInput(world, CENTER));
        assertTrue(WandManager.applyAdvancedAlchemyActivation(world, CENTER, 0));
        assertDedicatedLayout(world);
        assertEquals(Blocks.STONE, world.getBlockState(CENTER.up()).getBlock());

        TileAlchemyFurnaceAdvanced converted = (TileAlchemyFurnaceAdvanced) world.getTileEntity(CENTER);
        NBTTagCompound convertedData = new NBTTagCompound();
        converted.writeCustomNBT(convertedData);
        AspectList migratedAspects = new AspectList();
        migratedAspects.readFromNBT(convertedData);
        assertEquals(7, migratedAspects.getAmount(Aspect.FIRE));
        assertEquals(2, countItem(world, converted, Items.STICK));
        assertEquals(3, countItem(world, converted, Items.COAL));
    }

    @Test
    public void canonicalSourceAllowsFilledAlembicsLikeTc4() {
        FurnaceWorld world = new FurnaceWorld();
        placeSourceLayout(world, 1);
        TileAlembic alembic = (TileAlembic) world.getTileEntity(CENTER.up().north().east());
        alembic.aspect = Aspect.WATER;
        alembic.amount = 4;
        alembic.aspectFilter = Aspect.WATER;

        assertTrue(WandManager.matchesAdvancedAlchemyInput(world, CENTER));
    }

    @Test
    public void lowerNewBuildIsRejectedWithoutConsumingVis() throws Exception {
        FurnaceWorld world = new FurnaceWorld();
        placeFormerInputLayout(world, -1);
        CountingWand wand = new CountingWand();

        assertFalse(WandManager.matchesAdvancedAlchemyInput(world, CENTER));
        assertFalse(WandManager.matchesLegacyAdvancedAlchemyLayout(world, CENTER, -1));
        assertFalse(invokeCreate(world, wand));
        assertEquals(0, wand.consumeCalls);
        assertEquals(ConfigBlocks.blockStoneDevice, world.getBlockState(CENTER).getBlock());
    }

    @Test
    public void ambiguousUpperUsesCanonicalActivationWhileSafeLowerLegacyMigrates() throws Exception {
        FurnaceWorld upper = new FurnaceWorld();
        placeLegacyLayout(upper, 1);
        assertFalse(WandManager.matchesLegacyAdvancedAlchemyLayout(upper, CENTER, 1));
        assertTrue(WandManager.matchesAdvancedAlchemyInput(upper, CENTER));

        FurnaceWorld lower = new FurnaceWorld();
        placeLegacyLayout(lower, -1);
        assertTrue(WandManager.matchesLegacyAdvancedAlchemyLayout(lower, CENTER, -1));
        CountingWand lowerWand = new CountingWand();
        assertTrue(invokeCreate(lower, lowerWand));
        assertEquals(1, lowerWand.consumeCalls);
        assertDedicatedLayout(lower);
        forEachRing((dx, dz) -> assertTrue(lower.isAirBlock(CENTER.add(dx, -1, dz))));
    }

    @Test
    public void convertedAndNonmatchingLayoutsAreSafeAndIdempotent() throws Exception {
        FurnaceWorld converted = new FurnaceWorld();
        placeDedicatedLayout(converted);
        Map<BlockPos, IBlockState> before = new HashMap<>(converted.states);
        CountingWand convertedWand = new CountingWand();

        assertTrue(WandManager.matchesAdvancedAlchemyFurnace(converted, CENTER));
        assertTrue(invokeCreate(converted, convertedWand));
        assertEquals(0, convertedWand.consumeCalls);
        assertEquals(before, converted.states);

        FurnaceWorld mismatch = new FurnaceWorld();
        placeLegacyLayout(mismatch, 1);
        mismatch.put(CENTER.east().up(), Blocks.STONE.getDefaultState());
        assertFalse(WandManager.matchesLegacyAdvancedAlchemyLayout(mismatch, CENTER, 1));
        assertFalse(WandManager.matchesAdvancedAlchemyInput(mismatch, CENTER));
        assertFalse(invokeCreate(mismatch, new CountingWand()));

        FurnaceWorld unsafeLower = new FurnaceWorld();
        placeLegacyLayout(unsafeLower, -1);
        unsafeLower.put(CENTER.up().east(), Blocks.STONE.getDefaultState());
        assertFalse(WandManager.matchesLegacyAdvancedAlchemyLayout(unsafeLower, CENTER, -1));
        assertEquals(Blocks.STONE, unsafeLower.getBlockState(CENTER.up().east()).getBlock());

        FurnaceWorld filledLower = new FurnaceWorld();
        placeLegacyLayout(filledLower, -1);
        TileAlembic alembic = (TileAlembic) filledLower.getTileEntity(CENTER.down().north().east());
        alembic.aspect = Aspect.WATER;
        alembic.amount = 1;
        assertFalse(WandManager.matchesLegacyAdvancedAlchemyLayout(filledLower, CENTER, -1));

        FurnaceWorld customizedLower = new FurnaceWorld();
        placeLegacyLayout(customizedLower, -1);
        TileAlembic customized = (TileAlembic) customizedLower.getTileEntity(CENTER.down().north().east());
        customized.facing = 5;
        assertFalse(WandManager.matchesLegacyAdvancedAlchemyLayout(customizedLower, CENTER, -1));
    }

    private static void placeSourceLayout(FurnaceWorld world, int ringOffset) {
        placeCore(world);
        putMetalRing(world, 0, 3, 3);
        putMetalRing(world, ringOffset, 1, 9);
    }

    private static void placeLegacyLayout(FurnaceWorld world, int ringOffset) {
        placeSourceLayout(world, ringOffset);
    }

    private static void placeFormerInputLayout(FurnaceWorld world, int ringOffset) {
        placeCore(world);
        putMetalRing(world, 0, 1, 9);
        putMetalRing(world, ringOffset, 3, 3);
    }

    private static void placeCore(FurnaceWorld world) {
        world.put(CENTER, ConfigBlocks.blockStoneDevice.getStateFromMeta(0));
    }

    private static void putMetalRing(FurnaceWorld world, int yOffset, int cornerMeta, int cardinalMeta) {
        forEachRing((dx, dz) -> {
            boolean corner = Math.abs(dx) == 1 && Math.abs(dz) == 1;
            world.put(CENTER.add(dx, yOffset, dz),
                    ConfigBlocks.blockMetalDevice.getStateFromMeta(corner ? cornerMeta : cardinalMeta));
        });
    }

    private static void placeDedicatedLayout(FurnaceWorld world) {
        world.put(CENTER, ConfigBlocks.blockAlchemyFurnace.getStateFromMeta(0));
        forEachRing((dx, dz) -> {
            boolean corner = Math.abs(dx) == 1 && Math.abs(dz) == 1;
            world.put(CENTER.add(dx, 0, dz),
                    ConfigBlocks.blockAlchemyFurnace.getStateFromMeta(corner ? 4 : 1));
            world.put(CENTER.add(dx, 1, dz),
                    ConfigBlocks.blockAlchemyFurnace.getStateFromMeta(corner ? 2 : 3));
        });
    }

    private static void assertDedicatedLayout(FurnaceWorld world) {
        assertTrue(WandManager.matchesAdvancedAlchemyFurnace(world, CENTER));
        assertEquals(0, ConfigBlocks.blockAlchemyFurnace.getMetaFromState(world.getBlockState(CENTER)));
        forEachRing((dx, dz) -> {
            boolean corner = Math.abs(dx) == 1 && Math.abs(dz) == 1;
            assertEquals(corner ? 4 : 1, ConfigBlocks.blockAlchemyFurnace.getMetaFromState(
                    world.getBlockState(CENTER.add(dx, 0, dz))));
            assertEquals(corner ? 2 : 3, ConfigBlocks.blockAlchemyFurnace.getMetaFromState(
                    world.getBlockState(CENTER.add(dx, 1, dz))));
        });
    }

    private static int countItem(FurnaceWorld world, TileEntity tile, Item item) {
        int count = 0;
        if (tile instanceof IInventory) {
            IInventory inventory = (IInventory) tile;
            for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
                ItemStack stack = inventory.getStackInSlot(slot);
                if (!stack.isEmpty() && stack.getItem() == item) {
                    count += stack.getCount();
                }
            }
        }
        for (ItemStack stack : world.drops) {
            if (stack.getItem() == item) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static boolean invokeCreate(FurnaceWorld world, CountingWand wand) throws Exception {
        Method method = WandManager.class.getDeclaredMethod("createAdvancedAlchemicalFurnace",
                ItemStack.class, EntityPlayer.class, World.class,
                int.class, int.class, int.class, int.class);
        method.setAccessible(true);
        return (Boolean) method.invoke(null, new ItemStack(wand), null, world,
                CENTER.getX(), CENTER.getY(), CENTER.getZ(), 1);
    }

    private static void forEachRing(RingConsumer consumer) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx != 0 || dz != 0) {
                    consumer.accept(dx, dz);
                }
            }
        }
    }

    private interface RingConsumer {
        void accept(int dx, int dz);
    }

    private static class CountingWand extends ItemWandCasting {
        private int consumeCalls;

        @Override
        public boolean consumeAllVisCrafting(ItemStack stack, EntityPlayer player, AspectList cost, boolean doit) {
            this.consumeCalls++;
            return true;
        }
    }

    private static class FurnaceWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();
        private final List<ItemStack> drops = new ArrayList<>();

        FurnaceWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT), "advanced_furnace_runtime"),
                    new WorldProviderSurface(),
                    new Profiler(),
                    false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        void put(BlockPos pos, IBlockState state) {
            this.setBlockState(pos, state, 3);
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            IBlockState state = this.states.get(pos);
            return state == null ? Blocks.AIR.getDefaultState() : state;
        }

        @Override
        public boolean isAirBlock(BlockPos pos) {
            return this.getBlockState(pos).getBlock() == Blocks.AIR;
        }

        @Override
        public boolean setBlockState(BlockPos pos, IBlockState state, int flags) {
            BlockPos key = pos.toImmutable();
            this.tiles.remove(key);
            if (state.getBlock() == Blocks.AIR) {
                this.states.remove(key);
                return true;
            }
            this.states.put(key, state);
            if (state.getBlock().hasTileEntity(state)) {
                TileEntity tile = state.getBlock().createTileEntity(this, state);
                if (tile != null) {
                    tile.setWorld(this);
                    tile.setPos(key);
                    this.tiles.put(key, tile);
                }
            }
            return true;
        }

        @Override
        public boolean setBlockToAir(BlockPos pos) {
            return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }

        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return this.tiles.get(pos);
        }

        @Override
        public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) {
        }

        @Override
        public boolean checkLightFor(EnumSkyBlock lightType, BlockPos pos) {
            return true;
        }

        @Override
        public void updateComparatorOutputLevel(BlockPos pos, net.minecraft.block.Block blockIn) {
        }

        @Override
        public boolean spawnEntity(Entity entity) {
            if (entity instanceof EntityItem) {
                this.drops.add(((EntityItem) entity).getItem().copy());
            }
            return true;
        }

        @Override
        public void notifyNeighborsOfStateChange(BlockPos pos, net.minecraft.block.Block blockType, boolean updateObservers) {
        }

        @Override
        public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
        }

        @Override
        public void markBlockRangeForRenderUpdate(BlockPos rangeMin, BlockPos rangeMax) {
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
                    return "advanced_furnace_runtime_dummy";
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
