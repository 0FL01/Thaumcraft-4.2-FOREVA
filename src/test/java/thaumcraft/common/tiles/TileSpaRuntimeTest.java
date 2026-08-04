package thaumcraft.common.tiles;

import com.mojang.authlib.GameProfile;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.blocks.BlockStoneDevice;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.items.ItemBathSalts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TileSpaRuntimeTest {
    private static final BlockPos SPA_POS = new BlockPos(0, 64, 0);

    @BeforeClass
    public static void bootstrapMinecraftStatics() throws Exception {
        Bootstrap.register();
        initializeFluidItemCapability();
        if (ConfigBlocks.blockStoneDevice == null) {
            ConfigBlocks.init();
        }
    }

    @Test
    public void mixedModePlacesOnePureSourcePerFortyTickCadence() {
        SpaWorld world = new SpaWorld();
        TileSpa spa = attachSpa(world);
        BlockPos first = SPA_POS.up();
        BlockPos second = first.north();
        world.putState(second.down(), Blocks.STONE.getDefaultState());
        spa.tank.fill(new FluidStack(FluidRegistry.WATER, 2000), true);
        spa.setInventorySlotContents(0, new ItemStack(new ItemBathSalts(), 2));

        spa.update();

        assertEquals(ConfigBlocks.blockFluidPure, world.getBlockState(first).getBlock());
        assertEquals(1000, spa.tank.getFluidAmount());
        assertEquals(1, spa.getStackInSlot(0).getCount());
        for (int tick = 0; tick < 39; tick++) {
            spa.update();
        }
        assertEquals(Blocks.AIR, world.getBlockState(second).getBlock());

        spa.update();

        assertEquals(ConfigBlocks.blockFluidPure, world.getBlockState(second).getBlock());
        assertEquals(0, spa.tank.getFluidAmount());
        assertTrue(spa.getStackInSlot(0).isEmpty());
    }

    @Test
    public void directModePlacesTankFluidWithoutConsumingBathSalts() {
        SpaWorld world = new SpaWorld();
        TileSpa spa = attachSpa(world);
        BlockPos target = SPA_POS.up();
        spa.toggleMix();
        spa.tank.fill(new FluidStack(FluidRegistry.WATER, 1000), true);
        ItemStack salts = new ItemStack(new ItemBathSalts(), 1);
        spa.setInventorySlotContents(0, salts);

        spa.update();

        assertEquals(FluidRegistry.WATER.getBlock(), world.getBlockState(target).getBlock());
        assertEquals(0, spa.tank.getFluidAmount());
        assertEquals(1, spa.getStackInSlot(0).getCount());
    }

    @Test
    public void poweredOrUnsupportedSpaConsumesNothing() {
        SpaWorld poweredWorld = new SpaWorld();
        TileSpa poweredSpa = attachSpa(poweredWorld);
        poweredWorld.setPowered(SPA_POS, true);
        fillMixed(poweredSpa);

        poweredSpa.update();

        assertEquals(1000, poweredSpa.tank.getFluidAmount());
        assertEquals(1, poweredSpa.getStackInSlot(0).getCount());

        SpaWorld unsupportedWorld = new SpaWorld();
        TileSpa unsupportedSpa = attachSpa(unsupportedWorld);
        unsupportedWorld.putState(SPA_POS.up(), Blocks.STONE.getDefaultState());
        fillMixed(unsupportedSpa);

        unsupportedSpa.update();

        assertEquals(1000, unsupportedSpa.tank.getFluidAmount());
        assertEquals(1, unsupportedSpa.getStackInSlot(0).getCount());
        assertEquals(Blocks.STONE, unsupportedWorld.getBlockState(SPA_POS.up()).getBlock());
    }

    @Test
    public void playerFilledContainerTransfersToSpaAndReturnsEmptyContainer() {
        SpaWorld world = new SpaWorld();
        TileSpa spa = attachSpa(world);
        TestPlayer player = new TestPlayer(world);
        player.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.WATER_BUCKET));
        BlockStoneDevice block = ConfigBlocks.blockStoneDevice;

        assertTrue(block.onBlockActivated(world, SPA_POS, block.getStateFromMeta(12), player,
                EnumHand.MAIN_HAND, EnumFacing.UP, 0.5F, 0.5F, 0.5F));

        assertEquals(1000, spa.tank.getFluidAmount());
        assertTrue(playerHasItem(player, Items.BUCKET));
        assertFalse(playerHasItem(player, Items.WATER_BUCKET));
    }

    private static TileSpa attachSpa(SpaWorld world) {
        TileSpa spa = new TileSpa();
        world.attach(SPA_POS, ConfigBlocks.blockStoneDevice.getStateFromMeta(12), spa);
        return spa;
    }

    private static void fillMixed(TileSpa spa) {
        spa.tank.fill(new FluidStack(FluidRegistry.WATER, 1000), true);
        spa.setInventorySlotContents(0, new ItemStack(new ItemBathSalts(), 1));
    }

    private static boolean playerHasItem(EntityPlayer player, net.minecraft.item.Item item) {
        for (ItemStack stack : player.inventory.mainInventory) {
            if (!stack.isEmpty() && stack.getItem() == item) return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static void initializeFluidItemCapability() throws Exception {
        if (CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY != null) return;
        Field providersField = CapabilityManager.class.getDeclaredField("providers");
        providersField.setAccessible(true);
        IdentityHashMap<String, Capability<?>> providers =
                (IdentityHashMap<String, Capability<?>>) providersField.get(CapabilityManager.INSTANCE);
        String capabilityName = IFluidHandlerItem.class.getName().intern();
        Capability<?> capability = providers.get(capabilityName);
        if (capability == null) {
            CapabilityFluidHandler.register();
            capability = providers.get(capabilityName);
        }
        CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY = (Capability<IFluidHandlerItem>) capability;
    }

    private static final class TestPlayer extends EntityPlayer {
        TestPlayer(World world) {
            super(world, new GameProfile(UUID.randomUUID(), "spa_runtime"));
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
    }

    private static final class SpaWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();
        private final Set<BlockPos> powered = new HashSet<>();

        SpaWorld() {
            super(null, new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT),
                    "spa_runtime"), new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        void attach(BlockPos pos, IBlockState state, TileEntity tile) {
            this.putState(pos, state);
            tile.setWorld(this);
            tile.setPos(pos);
            this.tiles.put(pos.toImmutable(), tile);
        }

        void putState(BlockPos pos, IBlockState state) {
            this.states.put(pos.toImmutable(), state);
        }

        void setPowered(BlockPos pos, boolean value) {
            if (value) this.powered.add(pos.toImmutable()); else this.powered.remove(pos);
        }

        @Override public IBlockState getBlockState(BlockPos pos) {
            IBlockState state = this.states.get(pos);
            return state == null ? Blocks.AIR.getDefaultState() : state;
        }

        @Override public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
            this.putState(pos, newState);
            return true;
        }

        @Override public TileEntity getTileEntity(BlockPos pos) { return this.tiles.get(pos); }
        @Override public boolean isBlockPowered(BlockPos pos) { return this.powered.contains(pos); }
        @Override public void markChunkDirty(BlockPos pos, TileEntity tileEntity) { }
        @Override public void notifyBlockUpdate(BlockPos pos, IBlockState oldState, IBlockState newState, int flags) { }
        @Override public void playSound(EntityPlayer player, BlockPos pos, SoundEvent sound, SoundCategory category,
                                        float volume, float pitch) { }

        @Override protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "spa_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) { return true; }
    }
}
