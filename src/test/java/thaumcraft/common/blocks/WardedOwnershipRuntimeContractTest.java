package thaumcraft.common.blocks;

import com.mojang.authlib.GameProfile;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
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
import thaumcraft.common.config.Config;
import thaumcraft.common.tiles.TileArcanePressurePlate;
import thaumcraft.common.tiles.TileOwned;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class WardedOwnershipRuntimeContractTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void ownedTilesSurviveStateChangesWithinTheirBlock() {
        BlockWoodenDevice woodenDevice = new BlockWoodenDevice();
        BlockArcaneDoor door = new BlockArcaneDoor();
        TileOwned owned = new TileOwned();

        assertFalse(owned.shouldRefresh(null, BlockPos.ORIGIN,
                woodenDevice.getStateFromMeta(2), woodenDevice.getStateFromMeta(3)));
        assertFalse(owned.shouldRefresh(null, BlockPos.ORIGIN,
                door.getDefaultState(), door.getDefaultState().withProperty(BlockArcaneDoor.OPEN, true)));
        assertTrue(owned.shouldRefresh(null, BlockPos.ORIGIN,
                woodenDevice.getStateFromMeta(2), Blocks.STONE.getDefaultState()));
    }

    @Test
    public void onlyPressurePlateWoodenStatesOwnTheirPlacement() {
        BlockWoodenDevice block = new BlockWoodenDevice();

        assertTrue(block.hasTileEntity(block.getStateFromMeta(2)));
        assertTrue(block.hasTileEntity(block.getStateFromMeta(3)));
        assertTrue(block.createTileEntity(null, block.getStateFromMeta(2)) instanceof TileArcanePressurePlate);
        assertTrue(block.createTileEntity(null, block.getStateFromMeta(3)) instanceof TileArcanePressurePlate);
        assertFalse(block.hasTileEntity(block.getStateFromMeta(6)));
        assertFalse(block.hasTileEntity(block.getStateFromMeta(7)));
        assertNull(block.createTileEntity(null, block.getStateFromMeta(6)));
        assertNull(block.createTileEntity(null, block.getStateFromMeta(7)));
    }

    @Test
    public void hardenedGlassUsesAnOwnedTileOnlyForItsWardedState() {
        BlockCosmeticOpaque block = new BlockCosmeticOpaque();

        assertFalse(block.hasTileEntity(block.getStateFromMeta(0)));
        assertNull(block.createTileEntity(null, block.getStateFromMeta(0)));
        assertTrue(block.hasTileEntity(block.getStateFromMeta(2)));
        assertTrue(block.createTileEntity(null, block.getStateFromMeta(2)) instanceof TileOwned);
    }

    @Test
    public void cosmeticOpaqueStatesKeepTc4LightOpacity() {
        BlockCosmeticOpaque block = new BlockCosmeticOpaque();
        OwnedWorld world = new OwnedWorld();
        int[] expectedOpacity = {3, 3, 0, 0, 0};

        for (int type = 0; type < expectedOpacity.length; type++) {
            IBlockState state = block.getStateFromMeta(type);
            assertEquals("state opacity for type " + type, expectedOpacity[type], state.getLightOpacity());
            assertEquals("world opacity for type " + type, expectedOpacity[type],
                    state.getLightOpacity(world, BlockPos.ORIGIN));
        }
    }

    @Test
    public void playerPlacementAlwaysReplacesCopiedOwnership() {
        OwnedWorld world = new OwnedWorld();
        TestPlayer player = new TestPlayer(world, "new_owner");
        BlockPos glassPos = new BlockPos(0, 64, 0);
        BlockPos platePos = glassPos.east();
        BlockCosmeticOpaque glass = new BlockCosmeticOpaque();
        BlockWoodenDevice woodenDevice = new BlockWoodenDevice();
        TestOwnedTile glassTile = new TestOwnedTile();
        TestPressurePlate plateTile = new TestPressurePlate();
        glassTile.owner = "copied_owner";
        plateTile.owner = "copied_owner";
        world.attach(glassPos, glass.getStateFromMeta(2), glassTile);
        world.attach(platePos, woodenDevice.getStateFromMeta(2), plateTile);

        glass.onBlockPlacedBy(world, glassPos, glass.getStateFromMeta(2), player, new ItemStack(Items.STICK));
        woodenDevice.onBlockPlacedBy(world, platePos, woodenDevice.getStateFromMeta(2), player,
                new ItemStack(Items.STICK));

        assertEquals("new_owner", glassTile.owner);
        assertEquals("new_owner", plateTile.owner);
    }

    @Test
    public void wardedGlassAndPressurePlateKeepTc4Protection() {
        OwnedWorld world = new OwnedWorld();
        BlockPos glassPos = new BlockPos(0, 64, 0);
        BlockPos platePos = glassPos.east();
        BlockCosmeticOpaque glass = new BlockCosmeticOpaque();
        BlockWoodenDevice woodenDevice = new BlockWoodenDevice();
        IBlockState glassState = glass.getStateFromMeta(2);
        IBlockState plateState = woodenDevice.getStateFromMeta(3);
        world.attach(glassPos, glassState, new TestOwnedTile());
        world.attach(platePos, plateState, new TestPressurePlate());
        boolean previousWardedStone = Config.wardedStone;

        try {
            Config.wardedStone = true;
            assertEquals(-1.0F, glass.getBlockHardness(glassState, world, glassPos), 0.0F);
            assertEquals(-1.0F, woodenDevice.getBlockHardness(plateState, world, platePos), 0.0F);
            assertSame(Item.getItemFromBlock(glass), glass.getItemDropped(glassState, world.rand, 0));
            assertSame(Items.AIR, woodenDevice.getItemDropped(plateState, world.rand, 0));
            assertFalse(glass.canEntityDestroy(glassState, world, glassPos, null));
            assertFalse(woodenDevice.canEntityDestroy(plateState, world, platePos, null));
            assertEquals(999.0F, glass.getExplosionResistance(world, glassPos, null, null), 0.0F);
            assertEquals(999.0F, woodenDevice.getExplosionResistance(world, platePos, null, null), 0.0F);

            glass.onBlockExploded(world, glassPos, null);
            woodenDevice.onBlockExploded(world, platePos, null);
            assertSame(glass, world.getBlockState(glassPos).getBlock());
            assertSame(woodenDevice, world.getBlockState(platePos).getBlock());

            Config.wardedStone = false;
            assertEquals(5.0F, glass.getBlockHardness(glassState, world, glassPos), 0.0F);
            assertEquals(2.0F, woodenDevice.getBlockHardness(plateState, world, platePos), 0.0F);
            assertEquals(2, woodenDevice.damageDropped(plateState));
        } finally {
            Config.wardedStone = previousWardedStone;
        }
    }

    private static class TestOwnedTile extends TileOwned {
        @Override
        public void markDirty() {
        }
    }

    private static class TestPressurePlate extends TileArcanePressurePlate {
        @Override
        public void markDirty() {
        }
    }

    private static class TestPlayer extends EntityPlayer {
        TestPlayer(World world, String name) {
            super(world, new GameProfile(UUID.randomUUID(), name));
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

    private static class OwnedWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();

        OwnedWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT),
                            "warded_ownership_runtime"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        void attach(BlockPos pos, IBlockState state, TileEntity tile) {
            this.states.put(pos.toImmutable(), state);
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
        public TileEntity getTileEntity(BlockPos pos) {
            return this.tiles.get(pos);
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "warded_ownership_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
