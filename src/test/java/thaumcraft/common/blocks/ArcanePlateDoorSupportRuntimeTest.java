package thaumcraft.common.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
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
import thaumcraft.common.blocks.ItemBlocks.BlockWoodenDeviceItem;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.tiles.TileArcanePressurePlate;
import thaumcraft.common.tiles.TileOwned;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ArcanePlateDoorSupportRuntimeTest {
    private static final BlockPos POS = new BlockPos(0, 64, 0);

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
        if (ConfigBlocks.blockWoodenDevice == null) {
            ConfigBlocks.init();
        }
        if (ConfigItems.itemArcaneDoor == null) {
            ConfigItems.itemArcaneDoor = new ItemArcaneDoor();
        }
    }

    @Test
    public void creativeInventoryPublishesOnlyTheUnpressedPlate() {
        NonNullList<ItemStack> stacks = NonNullList.create();
        ConfigBlocks.blockWoodenDevice.getSubBlocks(null, stacks);

        assertEquals(1, countMetadata(stacks, 2));
        assertEquals(0, countMetadata(stacks, 3));
    }

    @Test
    public void plateItemRequiresSolidSupportForClientAndServerPlacement() {
        BlockWoodenDevice block = ConfigBlocks.blockWoodenDevice;
        BlockWoodenDeviceItem item = new BlockWoodenDeviceItem(block);
        for (int metadata : new int[]{2, 3}) {
            ItemStack stack = new ItemStack(item, 1, metadata);

            SupportWorld solidWorld = new SupportWorld();
            solidWorld.put(POS, Blocks.STONE.getDefaultState());
            assertTrue(item.canPlaceBlockOnSide(solidWorld, POS, EnumFacing.UP, null, stack));
            assertTrue(item.placeBlockAt(stack, null, solidWorld, POS.up(), EnumFacing.UP,
                    0.5F, 1.0F, 0.5F, block.getStateFromMeta(metadata)));
            assertSame(block, solidWorld.getBlockState(POS.up()).getBlock());

            SupportWorld fenceWorld = new SupportWorld();
            fenceWorld.put(POS, Blocks.OAK_FENCE.getDefaultState());
            assertFalse(item.canPlaceBlockOnSide(fenceWorld, POS, EnumFacing.UP, null, stack));
            assertFalse(item.placeBlockAt(stack, null, fenceWorld, POS.up(), EnumFacing.UP,
                    0.5F, 1.0F, 0.5F, block.getStateFromMeta(metadata)));
            assertTrue(fenceWorld.isAirBlock(POS.up()));

            SupportWorld unsupportedWorld = new SupportWorld();
            assertFalse(item.placeBlockAt(stack, null, unsupportedWorld, POS, EnumFacing.UP,
                    0.5F, 1.0F, 0.5F, block.getStateFromMeta(metadata)));
            assertTrue(unsupportedWorld.isAirBlock(POS));
        }
    }

    @Test
    public void plateSupportLossLeavesBothRuntimeStatesFloating() {
        BlockWoodenDevice block = ConfigBlocks.blockWoodenDevice;
        for (int metadata : new int[]{2, 3}) {
            SupportWorld world = new SupportWorld();
            IBlockState state = block.getStateFromMeta(metadata);
            world.put(POS, state);

            block.neighborChanged(state, world, POS, Blocks.STONE, POS.down());

            assertSame(block, world.getBlockState(POS).getBlock());
            assertEquals(metadata, world.getBlockState(POS).getValue(BlockWoodenDevice.TYPE).intValue());
            assertEquals(0, world.destroyCalls);
            assertFalse(world.dropRequested);
        }
    }

    @Test
    public void doorSupportLossLeavesBothHalvesFloating() {
        BlockArcaneDoor door = ConfigBlocks.blockArcaneDoor;
        for (IBlockState support : new IBlockState[]{Blocks.STONE.getDefaultState(),
                ConfigBlocks.blockWoodenDevice.getStateFromMeta(6)}) {
            SupportWorld world = doorWorld(door, support);
            IBlockState lower = world.getBlockState(POS);
            world.setBlockToAir(POS.down());

            door.neighborChanged(lower, world, POS, support.getBlock(), POS.down());

            assertSame(door, world.getBlockState(POS).getBlock());
            assertSame(door, world.getBlockState(POS.up()).getBlock());
            assertTrue(world.drops.isEmpty());
        }
    }

    @Test
    public void plateAndDoorShareDecodedKeyAccessIdentities() {
        BlockArcaneDoor door = ConfigBlocks.blockArcaneDoor;
        SupportWorld world = doorWorld(door, Blocks.STONE.getDefaultState());
        TileOwned doorTile = new TileOwned();
        doorTile.owner = "doorOwner";
        doorTile.accessList.add("0sharedPlayer");
        world.putTile(POS, doorTile);

        BlockPos platePos = POS.east();
        world.put(platePos, ConfigBlocks.blockWoodenDevice.getStateFromMeta(3));
        TileArcanePressurePlate plate = new TileArcanePressurePlate();
        plate.owner = "plateOwner";
        plate.accessList.add("1sharedPlayer");
        world.putTile(platePos, plate);

        door.neighborChanged(world.getBlockState(POS), world, POS, ConfigBlocks.blockWoodenDevice, platePos);
        assertTrue(world.getBlockState(POS).getValue(BlockArcaneDoor.OPEN));

        world.put(platePos, ConfigBlocks.blockWoodenDevice.getStateFromMeta(2));
        door.neighborChanged(world.getBlockState(POS), world, POS, ConfigBlocks.blockWoodenDevice, platePos);
        assertFalse(world.getBlockState(POS).getValue(BlockArcaneDoor.OPEN));

        plate.accessList.clear();
        plate.accessList.add("1someoneElse");
        world.put(platePos, ConfigBlocks.blockWoodenDevice.getStateFromMeta(3));
        door.neighborChanged(world.getBlockState(POS), world, POS, ConfigBlocks.blockWoodenDevice, platePos);
        assertFalse(world.getBlockState(POS).getValue(BlockArcaneDoor.OPEN));
    }

    private static SupportWorld doorWorld(BlockArcaneDoor door, IBlockState support) {
        SupportWorld world = new SupportWorld();
        world.put(POS.down(), support);
        world.put(POS, door.getDefaultState().withProperty(BlockArcaneDoor.HALF, BlockDoor.EnumDoorHalf.LOWER));
        world.put(POS.up(), door.getDefaultState().withProperty(BlockArcaneDoor.HALF, BlockDoor.EnumDoorHalf.UPPER));
        return world;
    }

    private static int countMetadata(List<ItemStack> stacks, int metadata) {
        int count = 0;
        for (ItemStack stack : stacks) {
            if (stack.getMetadata() == metadata) {
                count++;
            }
        }
        return count;
    }

    private static final class SupportWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();
        private final List<ItemStack> drops = new ArrayList<>();
        private int destroyCalls;
        private boolean dropRequested;

        private SupportWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "arcane_support_runtime"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        private void put(BlockPos pos, IBlockState state) {
            this.states.put(pos.toImmutable(), state);
        }

        private void putTile(BlockPos pos, TileEntity tile) {
            tile.setWorld(this);
            tile.setPos(pos);
            this.tiles.put(pos.toImmutable(), tile);
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            IBlockState state = this.states.get(pos);
            return state == null ? Blocks.AIR.getDefaultState() : state;
        }

        @Override
        public boolean setBlockState(BlockPos pos, IBlockState state, int flags) {
            if (state.getBlock() == Blocks.AIR) {
                this.states.remove(pos);
            } else {
                this.states.put(pos.toImmutable(), state);
            }
            return true;
        }

        @Override
        public boolean setBlockToAir(BlockPos pos) {
            return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }

        @Override
        public boolean destroyBlock(BlockPos pos, boolean dropBlock) {
            IBlockState state = this.getBlockState(pos);
            if (state.getBlock() == Blocks.AIR) {
                return false;
            }
            this.destroyCalls++;
            this.dropRequested = dropBlock;
            return this.setBlockToAir(pos);
        }

        @Override
        public boolean mayPlace(Block block, BlockPos pos, boolean skipCollisionCheck,
                                EnumFacing sidePlacedOn, Entity placer) {
            return this.getBlockState(pos).getBlock().isReplaceable(this, pos)
                    && block.canPlaceBlockAt(this, pos);
        }

        @Override
        public boolean spawnEntity(Entity entity) {
            if (entity instanceof EntityItem) {
                this.drops.add(((EntityItem) entity).getItem().copy());
            }
            return true;
        }

        @Override public TileEntity getTileEntity(BlockPos pos) { return this.tiles.get(pos); }
        @Override public Explosion createExplosion(Entity entityIn, double x, double y, double z,
                                                   float strength, boolean isSmoking) { return null; }
        @Override public void notifyBlockUpdate(BlockPos pos, IBlockState oldState,
                                                IBlockState newState, int flags) { }
        @Override public void notifyNeighborsOfStateChange(BlockPos pos, Block blockType,
                                                           boolean updateObservers) { }
        @Override public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) { }
        @Override public void updateComparatorOutputLevel(BlockPos pos, Block blockIn) { }
        @Override public void markBlockRangeForRenderUpdate(BlockPos rangeMin, BlockPos rangeMax) { }
        @Override public void playSound(net.minecraft.entity.player.EntityPlayer player, BlockPos pos,
                                        net.minecraft.util.SoundEvent soundIn, net.minecraft.util.SoundCategory category,
                                        float volume, float pitch) { }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "arcane_support_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
