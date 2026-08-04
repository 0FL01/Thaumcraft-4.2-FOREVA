package thaumcraft.common.tiles;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.init.Bootstrap;
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
import thaumcraft.api.aspects.IEssentiaTransport;

import static org.junit.Assert.assertEquals;

public class TileTubeBufferRoutingRuntimeTest {
    private static final BlockPos POS = new BlockPos(0, 64, 0);

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void lowerSuctionRequesterCannotPreemptHigherPriorityOutput() {
        BufferWorld world = new BufferWorld();
        TestBuffer buffer = new TestBuffer();
        SuctionTile requester = new SuctionTile(4, Aspect.AIR);
        SuctionTile priority = new SuctionTile(8, Aspect.AIR);
        world.attach(POS, buffer);
        world.attach(POS.east(), requester);
        world.attach(POS.west(), priority);
        buffer.aspects.add(Aspect.AIR, 2);

        assertEquals(0, buffer.takeEssentia(Aspect.AIR, 1, EnumFacing.EAST));
        assertEquals(2, buffer.aspects.getAmount(Aspect.AIR));

        priority.suction = 3;
        assertEquals(1, buffer.takeEssentia(Aspect.AIR, 1, EnumFacing.EAST));
        assertEquals(1, buffer.aspects.getAmount(Aspect.AIR));
    }

    @Test
    public void incompatibleHigherSuctionDoesNotBlockRequestedAspect() {
        BufferWorld world = new BufferWorld();
        TestBuffer buffer = new TestBuffer();
        world.attach(POS, buffer);
        world.attach(POS.east(), new SuctionTile(4, Aspect.AIR));
        world.attach(POS.west(), new SuctionTile(8, Aspect.FIRE));
        buffer.aspects.add(Aspect.AIR, 1);

        assertEquals(1, buffer.takeEssentia(Aspect.AIR, 1, EnumFacing.EAST));
    }

    private static final class TestBuffer extends TileTubeBuffer {
        @Override public void markDirty() { }
        @Override public void markDirtyAndSync() { }
    }

    private static final class SuctionTile extends TileEntity implements IEssentiaTransport {
        private int suction;
        private final Aspect suctionType;

        SuctionTile(int suction, Aspect suctionType) {
            this.suction = suction;
            this.suctionType = suctionType;
        }

        @Override public boolean isConnectable(EnumFacing face) { return true; }
        @Override public boolean canInputFrom(EnumFacing face) { return true; }
        @Override public boolean canOutputTo(EnumFacing face) { return false; }
        @Override public void setSuction(Aspect aspect, int amount) { }
        @Override public Aspect getSuctionType(EnumFacing face) { return this.suctionType; }
        @Override public int getSuctionAmount(EnumFacing face) { return this.suction; }
        @Override public int takeEssentia(Aspect aspect, int amount, EnumFacing face) { return 0; }
        @Override public int addEssentia(Aspect aspect, int amount, EnumFacing face) { return amount; }
        @Override public Aspect getEssentiaType(EnumFacing face) { return null; }
        @Override public int getEssentiaAmount(EnumFacing face) { return 0; }
        @Override public int getMinimumSuction() { return 0; }
        @Override public boolean renderExtendedTube() { return false; }
    }

    private static final class BufferWorld extends World {
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();

        BufferWorld() {
            super(null, new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT),
                    "tube_buffer_routing"), new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        void attach(BlockPos pos, TileEntity tile) {
            tile.setWorld(this);
            tile.setPos(pos);
            this.tiles.put(pos.toImmutable(), tile);
        }

        @Override public TileEntity getTileEntity(BlockPos pos) { return this.tiles.get(pos); }
        @Override protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "tube_buffer_routing_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }
        @Override protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) { return true; }
    }
}
