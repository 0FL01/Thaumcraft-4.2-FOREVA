package thaumcraft.common.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
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
import thaumcraft.api.aspects.IAspectContainer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TileAlchemyFurnaceSyncRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void legacyVisSurvivesReadAndUpdatePacketRoundTrip() {
        NBTTagCompound legacy = new NBTTagCompound();
        legacy.setShort("Vis", (short) 17);

        TestFurnace furnace = new TestFurnace();
        furnace.readCustomNBT(legacy);
        assertEquals(17, furnace.vis);

        NBTTagCompound saved = new NBTTagCompound();
        furnace.writeCustomNBT(saved);
        assertTrue(saved.hasKey("Vis"));
        assertEquals(17, saved.getShort("Vis"));
        TestFurnace restored = new TestFurnace();
        restored.readCustomNBT(saved);
        assertEquals(17, restored.vis);

        TestFurnace packetCopy = new TestFurnace();
        packetCopy.readCustomNBT(furnace.getUpdatePacket().getNbtCompound());
        assertEquals(17, packetCopy.vis);
    }

    @Test
    public void aspectDataSupportsKeylessFallbackAndBasicFurnaceIsNotAnAspectContainer() {
        TestFurnace source = new TestFurnace();
        source.aspects.add(Aspect.AIR, 4);
        source.vis = 4;
        NBTTagCompound saved = new NBTTagCompound();
        source.writeCustomNBT(saved);
        saved.removeTag("Vis");

        TestFurnace restored = new TestFurnace();
        restored.readCustomNBT(saved);

        assertEquals(4, restored.vis);
        assertFalse(IAspectContainer.class.isAssignableFrom(TileAlchemyFurnace.class));
    }

    @Test
    public void visibleFurnaceTransitionsNotifyClients() {
        SyncWorld world = new SyncWorld();
        BlockPos furnacePos = new BlockPos(0, 64, 0);
        TestFurnace furnace = new TestFurnace();
        world.attach(furnacePos, furnace);
        furnace.aspects.add(Aspect.AIR, 2);
        furnace.vis = 2;

        assertTrue(furnace.takeFromContainer(Aspect.AIR, 1));
        assertTrue(world.notified.contains(furnacePos));

        world.notified.clear();
        furnace.furnaceBurnTime = 1;
        furnace.update();
        assertTrue(world.notified.contains(furnacePos));
        assertTrue(world.relit.contains(furnacePos));
    }

    @Test
    public void transfersNotifyEmptyAndAlreadyFilledAlembicClients() {
        assertAlembicNotified(null, 0);
        assertAlembicNotified(Aspect.AIR, 1);
    }

    private static void assertAlembicNotified(Aspect initialAspect, int initialAmount) {
        SyncWorld world = new SyncWorld();
        BlockPos furnacePos = new BlockPos(0, 64, 0);
        BlockPos alembicPos = furnacePos.up();
        TestFurnace furnace = new TestFurnace();
        TestAlembic alembic = new TestAlembic();
        alembic.aspect = initialAspect;
        alembic.amount = initialAmount;
        world.attach(furnacePos, furnace);
        world.attach(alembicPos, alembic);
        furnace.aspects.add(Aspect.AIR, 1);
        furnace.vis = 1;

        for (int i = 0; i < 40; ++i) {
            furnace.update();
        }

        assertEquals(initialAmount + 1, alembic.amount);
        assertEquals(0, furnace.vis);
        assertTrue("furnace contents should be synchronized", world.notified.contains(furnacePos));
        assertTrue("attached alembic contents should be synchronized", world.notified.contains(alembicPos));
    }

    private static class TestFurnace extends TileAlchemyFurnace {
        @Override
        public void markDirty() {
        }
    }

    private static class TestAlembic extends TileAlembic {
        @Override
        public void markDirty() {
        }
    }

    private static class SyncWorld extends World {
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();
        private final Set<BlockPos> notified = new HashSet<>();
        private final Set<BlockPos> relit = new HashSet<>();

        SyncWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT), "alchemy_sync"),
                    new WorldProviderSurface(),
                    new Profiler(),
                    false);
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
        public boolean checkLightFor(EnumSkyBlock lightType, BlockPos pos) {
            this.relit.add(pos.toImmutable());
            return true;
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
                    return "alchemy_sync_dummy";
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
