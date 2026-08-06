package thaumcraft.common.lib.events;

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
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.common.tiles.TileMirrorEssentia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class EssentiaHandlerMirrorRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void ignoreMirrorSkipsMirrorSourcesButStillScansOrdinarySources() {
        TestWorld world = new TestWorld();
        BlockPos originPos = new BlockPos(0, 64, 0);
        TileEntity origin = new TestOrigin();
        CountingMirror mirror = new CountingMirror();
        CountingSource ordinary = new CountingSource();
        world.attach(originPos, origin);
        world.attach(originPos.east(), mirror);
        world.attach(originPos.west(), ordinary);

        assertFalse(EssentiaHandler.drainEssentia(origin, Aspect.AIR, null, 1, true));
        assertEquals(0, mirror.attempts);
        assertEquals(1, ordinary.attempts);
    }

    @Test
    public void sourceDiscoveryExcludesTheDirectlyLinkedMirrorPeer() {
        TestWorld world = new TestWorld();
        BlockPos originPos = new BlockPos(10, 64, 0);
        CountingMirror origin = new CountingMirror();
        CountingMirror peer = new CountingMirror();
        peer.linkX = originPos.getX();
        peer.linkY = originPos.getY();
        peer.linkZ = originPos.getZ();
        peer.linkDim = 0;
        world.attach(originPos, origin);
        world.attach(originPos.east(), peer);

        assertFalse(EssentiaHandler.drainEssentia(origin, Aspect.AIR, EnumFacing.EAST, 2, false));
        assertEquals(0, peer.attempts);
    }

    private static final class CountingMirror extends TileMirrorEssentia {
        private int attempts;

        @Override
        public boolean takeFromContainer(Aspect aspect, int amount) {
            ++this.attempts;
            return false;
        }
    }

    private static final class TestOrigin extends TileEntity {
    }

    private static final class CountingSource extends TileEntity implements IAspectSource {
        private int attempts;

        @Override public AspectList getAspects() { return null; }
        @Override public void setAspects(AspectList aspects) { }
        @Override public boolean doesContainerAccept(Aspect aspect) { return false; }
        @Override public int addToContainer(Aspect aspect, int amount) { return amount; }
        @Override public boolean takeFromContainer(Aspect aspect, int amount) { ++this.attempts; return false; }
        @Override public boolean takeFromContainer(AspectList aspects) { return false; }
        @Override public boolean doesContainerContainAmount(Aspect aspect, int amount) { return false; }
        @Override public boolean doesContainerContain(AspectList aspects) { return false; }
        @Override public int containerContains(Aspect aspect) { return 0; }
    }

    private static final class TestWorld extends World {
        private final Map<BlockPos, TileEntity> tiles = new HashMap<BlockPos, TileEntity>();

        TestWorld() {
            super(null, new WorldInfo(new WorldSettings(0L, GameType.CREATIVE, false, false, WorldType.DEFAULT),
                    "essentia_mirror"), new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        void attach(BlockPos pos, TileEntity tile) {
            tile.setWorld(this);
            tile.setPos(pos);
            this.tiles.put(pos.toImmutable(), tile);
        }

        @Override public TileEntity getTileEntity(BlockPos pos) { return this.tiles.get(pos); }
        @Override public boolean isBlockLoaded(BlockPos pos) { return true; }
        @Override protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) { return true; }
        @Override protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "essentia_mirror_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }
    }
}
