package thaumcraft.common.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
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

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class TileEssentiaReservoirSyncRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void contentMutationsNotifyClientsAndPacketsPreserveClientAnimation() {
        SyncWorld world = new SyncWorld();
        BlockPos pos = new BlockPos(0, 64, 0);
        TestReservoir source = new TestReservoir();
        world.attach(pos, source);

        assertEquals(16, source.addEssentia(Aspect.WATER, 16, EnumFacing.DOWN));
        assertEquals(16, source.containerContains(Aspect.WATER));
        assertTrue(world.notified.contains(pos));

        source.displayAspect = Aspect.AIR;
        source.colorR = 0.25F;
        NBTTagCompound packetData = source.getUpdatePacket().getNbtCompound();
        assertFalse(packetData.hasKey("displayAspect"));
        assertFalse(packetData.hasKey("colorR"));

        TestReservoir clientCopy = new TestReservoir();
        clientCopy.displayAspect = Aspect.FIRE;
        clientCopy.colorR = 0.75F;
        clientCopy.onDataPacket(null, source.getUpdatePacket());
        assertEquals(16, clientCopy.getAspects().getAmount(Aspect.WATER));
        assertSame(Aspect.FIRE, clientCopy.displayAspect);
        assertEquals(0.75F, clientCopy.colorR, 0.0F);

        world.notified.clear();
        assertEquals(4, source.takeEssentia(Aspect.WATER, 4, EnumFacing.DOWN));
        assertEquals(12, source.containerContains(Aspect.WATER));
        assertTrue(world.notified.contains(pos));
    }

    @Test
    public void assignmentAndExtractionKeepTc4ContainerContracts() {
        SyncWorld world = new SyncWorld();
        TestReservoir reservoir = new TestReservoir();
        world.attach(new BlockPos(0, 64, 0), reservoir);
        AspectList supplied = new AspectList().add(Aspect.WATER, 4);

        reservoir.setAspects(supplied);
        supplied.add(Aspect.FIRE, 7);

        assertNotSame(supplied, reservoir.getAspects());
        assertEquals(4, reservoir.containerContains(Aspect.WATER));
        assertEquals(0, reservoir.containerContains(Aspect.FIRE));
        assertTrue(reservoir.doesContainerAccept(Aspect.FIRE));
        assertEquals(0, reservoir.takeEssentia(Aspect.WATER, 5, EnumFacing.DOWN));
        assertEquals(4, reservoir.containerContains(Aspect.WATER));
        assertFalse(reservoir.takeFromContainer(new AspectList().add(Aspect.WATER, 4)));
        assertEquals(4, reservoir.containerContains(Aspect.WATER));
        assertEquals(4, reservoir.takeEssentia(Aspect.WATER, 4, EnumFacing.DOWN));
        assertEquals(0, reservoir.containerContains(Aspect.WATER));

        reservoir.maxAmount = 4;
        reservoir.setAspects(new AspectList().add(Aspect.ORDER, 4));
        assertTrue(reservoir.doesContainerAccept(Aspect.ENTROPY));
    }

    private static class TestReservoir extends TileEssentiaReservoir {
        @Override
        public void markDirty() {
        }
    }

    private static class SyncWorld extends World {
        private final Set<BlockPos> notified = new HashSet<>();

        SyncWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT), "reservoir_sync"),
                    new WorldProviderSurface(),
                    new Profiler(),
                    false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        void attach(BlockPos pos, TileEntity tile) {
            tile.setWorld(this);
            tile.setPos(pos);
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
                    return "reservoir_sync_dummy";
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
