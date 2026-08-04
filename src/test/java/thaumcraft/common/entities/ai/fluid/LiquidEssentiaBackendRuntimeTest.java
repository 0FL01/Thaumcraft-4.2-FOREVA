package thaumcraft.common.entities.ai.fluid;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.EnumGolemType;
import thaumcraft.common.entities.golems.GolemHelper;
import thaumcraft.common.entities.golems.Marker;
import thaumcraft.common.tiles.TileEssentiaReservoir;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class LiquidEssentiaBackendRuntimeTest {
    private static final BlockPos HOME = new BlockPos(0, 64, 0);
    private static int nextEntityId = 5000;

    @BeforeClass
    public static void bootstrapMinecraftStatics() throws Exception {
        Bootstrap.register();
        initializeFluidCapability();
    }

    @Test
    public void sourceSelectionUsesHomeClickedFacesAndHandlerPriority() {
        TestWorld world = new TestWorld(0);
        SidedFluidTile home = world.addTile(HOME.down(),
                SidedFluidTile.sink(EnumFacing.UP, FluidRegistry.WATER));
        EntityGolemBase golem = fluidGolem(world, EnumGolemType.WOOD);

        BlockPos unmarked = HOME.east();
        world.addTile(unmarked, SidedFluidTile.source(EnumFacing.NORTH, FluidRegistry.WATER, 1000));
        world.setBlock(HOME.west(), Blocks.WATER.getDefaultState());
        BlockPos wrongFace = HOME.east(2);
        world.addTile(wrongFace, SidedFluidTile.source(EnumFacing.SOUTH, FluidRegistry.WATER, 1000));
        BlockPos markedWorldSource = HOME.east(3);
        world.setBlock(markedWorldSource, Blocks.WATER.getDefaultState());
        BlockPos markedHandler = HOME.east(4);
        SidedFluidTile handler = world.addTile(markedHandler,
                SidedFluidTile.source(EnumFacing.EAST, FluidRegistry.WATER, 1000));
        BlockPos outOfRange = HOME.east(20);
        world.addTile(outOfRange, SidedFluidTile.source(EnumFacing.WEST, FluidRegistry.WATER, 1000));

        setMarkers(golem,
                marker(markedHandler, EnumFacing.EAST, -1, 0),
                marker(wrongFace, EnumFacing.NORTH, -1, 0),
                marker(outOfRange, EnumFacing.WEST, -1, 0),
                marker(markedWorldSource, EnumFacing.UP, -1, 0));

        assertTrue(containsFluid(GolemHelper.getMissingLiquids(golem), FluidRegistry.WATER));
        assertTrue(home.requestedFaces.contains(EnumFacing.UP));
        assertFalse(home.requestedFaces.contains(EnumFacing.DOWN));

        Vec3d selected = GolemHelper.findPossibleLiquid(
                new FluidStack(FluidRegistry.WATER, Integer.MAX_VALUE), golem);
        assertNotNull(selected);
        assertEquals(markedHandler.getX() + 0.5, selected.x, 0.0);
        assertEquals(markedHandler.getY() + 0.5, selected.y, 0.0);
        assertEquals(markedHandler.getZ() + 0.5, selected.z, 0.0);
        assertTrue(handler.requestedFaces.contains(EnumFacing.EAST));
        assertFalse(handler.requestedFaces.contains(EnumFacing.WEST));

        golem.fluidCarried = new FluidStack(FluidRegistry.WATER, 500);
        AILiquidEmpty empty = new AILiquidEmpty(golem);
        assertTrue(empty.shouldExecute());
        empty.startExecuting();
        assertEquals(500, home.tank.getFluidAmount());
        assertNull(golem.fluidCarried);
    }

    @Test
    public void adjacentHandlerRangeUsesStrictTwoBlockBoundary() {
        TestWorld world = new TestWorld(0);
        world.addTile(HOME.down(), SidedFluidTile.sink(EnumFacing.UP, FluidRegistry.WATER));
        EntityGolemBase golem = fluidGolem(world, EnumGolemType.WOOD);
        BlockPos inside = HOME.east();
        BlockPos boundary = HOME.east(2);
        world.addTile(inside, SidedFluidTile.source(EnumFacing.UP, FluidRegistry.WATER, 1000));
        world.addTile(boundary, SidedFluidTile.source(EnumFacing.UP, FluidRegistry.WATER, 1000));
        setMarkers(golem,
                marker(inside, EnumFacing.UP, -1, 0),
                marker(boundary, EnumFacing.UP, -1, 0));

        ArrayList<Marker> adjacent = GolemHelper.getMarkedFluidHandlersAdjacentToGolem(
                new FluidStack(FluidRegistry.WATER, 1000), world, golem);

        assertEquals(1, adjacent.size());
        assertEquals(inside.getX(), adjacent.get(0).x);
    }

    @Test
    public void ignisFiltersAndOrdoColorsRouteEachFluidWhileEmptyFiltersStayWildcard() {
        TestWorld world = new TestWorld(0);
        world.addTile(HOME.down(), SidedFluidTile.sink(EnumFacing.UP,
                FluidRegistry.WATER, FluidRegistry.LAVA));
        EntityGolemBase golem = fluidGolem(world, EnumGolemType.THAUMIUM);
        golem.setUpgrade(0, (byte) 2);
        golem.setUpgrade(1, (byte) 4);
        golem.setupGolemInventory();
        golem.inventory.setInventorySlotContents(0, new ItemStack(Items.WATER_BUCKET));
        golem.inventory.setInventorySlotContents(1, new ItemStack(Items.LAVA_BUCKET));
        golem.setColors(0, 3);
        golem.setColors(1, 7);

        BlockPos wrongWater = HOME.east();
        BlockPos water = HOME.east(3);
        BlockPos wrongLava = HOME.south();
        BlockPos lava = HOME.south(3);
        world.addTile(wrongWater, SidedFluidTile.source(EnumFacing.NORTH, FluidRegistry.WATER, 1000));
        world.addTile(water, SidedFluidTile.source(EnumFacing.NORTH, FluidRegistry.WATER, 1000));
        world.addTile(wrongLava, SidedFluidTile.source(EnumFacing.NORTH, FluidRegistry.LAVA, 1000));
        world.addTile(lava, SidedFluidTile.source(EnumFacing.NORTH, FluidRegistry.LAVA, 1000));
        setMarkers(golem,
                marker(wrongWater, EnumFacing.NORTH, 7, 0),
                marker(water, EnumFacing.NORTH, 3, 0),
                marker(wrongLava, EnumFacing.NORTH, 3, 0),
                marker(lava, EnumFacing.NORTH, 7, 0));

        ArrayList<FluidStack> missing = GolemHelper.getMissingLiquids(golem);
        assertTrue(containsFluid(missing, FluidRegistry.WATER));
        assertTrue(containsFluid(missing, FluidRegistry.LAVA));
        assertPosition(water, GolemHelper.findPossibleLiquid(
                new FluidStack(FluidRegistry.WATER, Integer.MAX_VALUE), golem));
        assertPosition(lava, GolemHelper.findPossibleLiquid(
                new FluidStack(FluidRegistry.LAVA, Integer.MAX_VALUE), golem));

        golem.inventory.clear();
        assertPosition(wrongWater, GolemHelper.findPossibleLiquid(
                new FluidStack(FluidRegistry.WATER, Integer.MAX_VALUE), golem));
    }

    @Test
    public void oneGatherUpdateCannotOverflowCarryLimitAcrossAdjacentHandlers() {
        TestWorld world = new TestWorld(0);
        world.addTile(HOME.down(), SidedFluidTile.sink(EnumFacing.UP, FluidRegistry.WATER));
        EntityGolemBase golem = fluidGolem(world, EnumGolemType.WOOD);
        BlockPos firstPos = HOME.east();
        BlockPos secondPos = HOME.south();
        SidedFluidTile first = world.addTile(firstPos,
                SidedFluidTile.source(EnumFacing.EAST, FluidRegistry.WATER, 1500));
        SidedFluidTile second = world.addTile(secondPos,
                SidedFluidTile.source(EnumFacing.SOUTH, FluidRegistry.WATER, 1500));
        setMarkers(golem,
                marker(firstPos, EnumFacing.EAST, 12, 0),
                marker(secondPos, EnumFacing.SOUTH, 6, 0));
        golem.itemWatched = new ItemStack(Items.BUCKET);

        AILiquidGather gather = new AILiquidGather(golem);
        assertTrue(gather.shouldExecute());
        gather.startExecuting();
        for (int tick = 0; tick < 10; tick++) gather.updateTask();

        assertNotNull(golem.fluidCarried);
        assertEquals(golem.getFluidCarryLimit(), golem.fluidCarried.amount);
        assertEquals(1000, first.tank.getFluidAmount() + second.tank.getFluidAmount());
        assertTrue(first.requestedFaces.contains(EnumFacing.EAST));
        assertTrue(second.requestedFaces.contains(EnumFacing.SOUTH));
    }

    @Test
    public void perditioConnectedPoolDrainsFarthestSourceFirst() {
        TestWorld world = new TestWorld(0);
        world.addTile(HOME.down(), SidedFluidTile.sink(EnumFacing.UP, FluidRegistry.WATER));
        EntityGolemBase golem = fluidGolem(world, EnumGolemType.THAUMIUM);
        golem.setUpgrade(0, (byte) 5);
        BlockPos marked = HOME.east();
        BlockPos middle = HOME.east(2);
        BlockPos farthest = HOME.east(3);
        world.setBlock(marked, Blocks.WATER.getDefaultState());
        world.setBlock(middle, Blocks.WATER.getDefaultState());
        world.setBlock(farthest, Blocks.WATER.getDefaultState());
        setMarkers(golem, marker(marked, EnumFacing.UP, -1, 0));
        golem.itemWatched = new ItemStack(Items.BUCKET);

        AILiquidGather gather = new AILiquidGather(golem);
        assertTrue(gather.shouldExecute());
        gather.startExecuting();
        for (int tick = 0; tick < 10; tick++) gather.updateTask();

        assertFalse(world.removedBlocks.isEmpty());
        assertEquals(farthest, world.removedBlocks.get(0));
        assertSame(Blocks.WATER, world.getBlockState(marked).getBlock());
        assertSame(Blocks.WATER, world.getBlockState(middle).getBlock());
    }

    @Test
    public void reservoirUnknownFaceExtractionFillsEmptyAndPartialGolems() {
        assertReservoirExtraction(0);
        assertReservoirExtraction(5);
    }

    private static void assertReservoirExtraction(int carried) {
        TestWorld world = new TestWorld(0);
        TileEssentiaReservoir reservoir = world.addTile(HOME.down(), new TileEssentiaReservoir());
        reservoir.facing = EnumFacing.UP;
        reservoir.essentia.add(Aspect.WATER, 40);
        EntityGolemBase golem = essentiaGolem(world);
        if (carried > 0) {
            golem.essentia = Aspect.WATER;
            golem.essentiaAmount = carried;
        }

        assertSame(Aspect.WATER, AIEssentiaGather.getEssentiaType(reservoir, EnumFacing.UP));
        AIEssentiaGather gather = new AIEssentiaGather(golem);
        assertTrue(gather.shouldExecute());
        gather.startExecuting();

        assertSame(Aspect.WATER, golem.essentia);
        assertEquals(golem.getCarryLimit(), golem.essentiaAmount);
        assertEquals(40 - (golem.getCarryLimit() - carried), reservoir.containerContains(Aspect.WATER));
    }

    private static EntityGolemBase fluidGolem(TestWorld world, EnumGolemType type) {
        EntityGolemBase golem = baseGolem(world, type);
        golem.setCore((byte) 5);
        golem.setupGolem();
        golem.setupGolemInventory();
        return golem;
    }

    private static EntityGolemBase essentiaGolem(TestWorld world) {
        EntityGolemBase golem = baseGolem(world, EnumGolemType.THAUMIUM);
        golem.setCore((byte) 6);
        golem.setupGolem();
        return golem;
    }

    private static EntityGolemBase baseGolem(TestWorld world, EnumGolemType type) {
        EntityGolemBase golem = new EntityGolemBase(world, type, false);
        golem.setEntityId(nextEntityId++);
        golem.homeFacing = EnumFacing.UP.getIndex();
        golem.setHomePosAndDistance(HOME, 32);
        golem.setPosition(HOME.getX() + 0.5, HOME.getY() + 0.5, HOME.getZ() + 0.5);
        return golem;
    }

    private static Marker marker(BlockPos pos, EnumFacing side, int color, int dim) {
        return new Marker(pos.getX(), pos.getY(), pos.getZ(), dim,
                (byte) side.getIndex(), (byte) color);
    }

    private static void setMarkers(EntityGolemBase golem, Marker... markers) {
        golem.setMarkers(new ArrayList<>(Arrays.asList(markers)));
    }

    private static boolean containsFluid(List<FluidStack> fluids, Fluid fluid) {
        for (FluidStack stack : fluids) {
            if (stack.getFluid() == fluid) return true;
        }
        return false;
    }

    private static void assertPosition(BlockPos expected, Vec3d actual) {
        assertNotNull(actual);
        assertEquals(expected.getX() + 0.5, actual.x, 0.0);
        assertEquals(expected.getY() + 0.5, actual.y, 0.0);
        assertEquals(expected.getZ() + 0.5, actual.z, 0.0);
    }

    @SuppressWarnings("unchecked")
    private static void initializeFluidCapability() throws Exception {
        Field providersField = CapabilityManager.class.getDeclaredField("providers");
        providersField.setAccessible(true);
        IdentityHashMap<String, Capability<?>> providers =
                (IdentityHashMap<String, Capability<?>>) providersField.get(CapabilityManager.INSTANCE);
        String capabilityName = IFluidHandler.class.getName().intern();
        Capability<?> capability = providers.get(capabilityName);
        if (capability == null) {
            CapabilityFluidHandler.register();
            capability = providers.get(capabilityName);
        }
        CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY = (Capability<IFluidHandler>) capability;
        CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY =
                (Capability<IFluidHandlerItem>) providers.get(IFluidHandlerItem.class.getName().intern());
    }

    private static final class SidedFluidTile extends TileEntity implements IFluidHandler {
        private final EnumFacing face;
        private final Set<Fluid> acceptedFluids;
        private final FluidTank tank;
        private final List<EnumFacing> requestedFaces = new ArrayList<>();

        private SidedFluidTile(EnumFacing face, FluidStack initial, Fluid... acceptedFluids) {
            this.face = face;
            this.acceptedFluids = new HashSet<>(Arrays.asList(acceptedFluids));
            this.tank = new FluidTank(16000);
            if (initial != null) this.tank.fill(initial, true);
        }

        static SidedFluidTile source(EnumFacing face, Fluid fluid, int amount) {
            return new SidedFluidTile(face, new FluidStack(fluid, amount));
        }

        static SidedFluidTile sink(EnumFacing face, Fluid... acceptedFluids) {
            return new SidedFluidTile(face, null, acceptedFluids);
        }

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
                this.requestedFaces.add(facing);
                return facing == this.face;
            }
            return super.hasCapability(capability, facing);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
                this.requestedFaces.add(facing);
                return facing == this.face ? (T) this : null;
            }
            return super.getCapability(capability, facing);
        }

        @Override
        public IFluidTankProperties[] getTankProperties() {
            return this.tank.getTankProperties();
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            if (resource == null || !this.acceptedFluids.contains(resource.getFluid())) return 0;
            return this.tank.fill(resource, doFill);
        }

        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            return this.tank.drain(resource, doDrain);
        }

        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            return this.tank.drain(maxDrain, doDrain);
        }
    }

    private static final class TestWorld extends World {
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();
        private final Map<BlockPos, IBlockState> blocks = new HashMap<>();
        private final List<BlockPos> removedBlocks = new ArrayList<>();

        private TestWorld(int dimension) {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "r5_liquid_essentia"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.provider.setDimension(dimension);
            this.chunkProvider = this.createChunkProvider();
        }

        private <T extends TileEntity> T addTile(BlockPos pos, T tile) {
            tile.setWorld(this);
            tile.setPos(pos);
            this.tiles.put(pos.toImmutable(), tile);
            return tile;
        }

        private void setBlock(BlockPos pos, IBlockState state) {
            this.blocks.put(pos.toImmutable(), state);
        }

        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return this.tiles.get(pos);
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            IBlockState state = this.blocks.get(pos);
            return state == null ? Blocks.AIR.getDefaultState() : state;
        }

        @Override
        public boolean setBlockToAir(BlockPos pos) {
            if (this.blocks.remove(pos) != null) this.removedBlocks.add(pos.toImmutable());
            return true;
        }

        @Override public Biome getBiome(BlockPos pos) { return Biomes.PLAINS; }
        @Override public Explosion createExplosion(Entity entityIn, double x, double y, double z,
                                                    float strength, boolean isSmoking) { return null; }
        @Override public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {}
        @Override public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) {}
        @Override public void updateComparatorOutputLevel(BlockPos pos, net.minecraft.block.Block blockIn) {}

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "r5_liquid_essentia_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
