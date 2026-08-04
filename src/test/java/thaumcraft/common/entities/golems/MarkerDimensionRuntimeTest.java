package thaumcraft.common.entities.golems;

import net.minecraft.entity.Entity;
import net.minecraft.init.Biomes;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
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
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class MarkerDimensionRuntimeTest {
    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void bellMarkerNbtKeepsDimensionsOutsideByteRange() {
        ArrayList<Marker> markers = new ArrayList<>();
        markers.add(new Marker(1, 2, 3, 300, (byte) EnumFacing.NORTH.getIndex(), (byte) 4));
        markers.add(new Marker(4, 5, 6, -300, (byte) EnumFacing.SOUTH.getIndex(), (byte) -1));

        NBTTagList serialized = ItemGolemBell.writeMarkers(markers);
        assertEquals(300, serialized.getCompoundTagAt(0).getInteger("dim"));
        assertEquals(-300, serialized.getCompoundTagAt(1).getInteger("dim"));

        ItemStack bell = new ItemStack(new ItemGolemBell());
        NBTTagCompound bellNbt = new NBTTagCompound();
        bellNbt.setTag("markers", serialized);
        bell.setTagCompound(bellNbt);
        ArrayList<Marker> loaded = ItemGolemBell.getMarkers(bell);

        assertEquals(300, loaded.get(0).dim);
        assertEquals(-300, loaded.get(1).dim);
    }

    @Test
    public void entityMarkerNbtAndValidationKeepFullPositiveAndNegativeDimensions() {
        assertEntityRoundTrip(300);
        assertEntityRoundTrip(-300);
    }

    @Test
    public void malformedMarkerSidesAreDroppedAndColorsBecomeWildcardAtPersistenceBoundaries() {
        NBTTagList raw = new NBTTagList();
        raw.appendTag(markerTag(1, 2, 3, 300, -1, 4));
        raw.appendTag(markerTag(4, 5, 6, 300, 6, 4));
        raw.appendTag(markerTag(7, 8, 9, 300, EnumFacing.UP.getIndex(), 99));
        ItemStack bell = new ItemStack(new ItemGolemBell());
        NBTTagCompound bellNbt = new NBTTagCompound();
        bellNbt.setTag("markers", raw);
        bell.setTagCompound(bellNbt);

        ArrayList<Marker> bellMarkers = ItemGolemBell.getMarkers(bell);
        assertEquals(1, bellMarkers.size());
        assertEquals(EnumFacing.UP.getIndex(), bellMarkers.get(0).side);
        assertEquals(-1, bellMarkers.get(0).color);
        assertEquals(300, bellMarkers.get(0).dim);

        EntityGolemBase golem = new EntityGolemBase(new TestWorld(300), EnumGolemType.WOOD, false);
        ArrayList<Marker> assigned = new ArrayList<>();
        assigned.add(new Marker(1, 2, 3, 300, (byte) -1, (byte) 4));
        assigned.add(new Marker(7, 8, 9, 300, (byte) EnumFacing.DOWN.getIndex(), (byte) -9));
        golem.setMarkers(assigned);
        assertEquals(1, golem.getMarkers().size());
        assertEquals(EnumFacing.DOWN.getIndex(), golem.getMarkers().get(0).side);
        assertEquals(-1, golem.getMarkers().get(0).color);
    }

    private static NBTTagCompound markerTag(int x, int y, int z, int dim, int side, int color) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("x", x);
        tag.setInteger("y", y);
        tag.setInteger("z", z);
        tag.setInteger("dim", dim);
        tag.setByte("side", (byte) side);
        tag.setByte("color", (byte) color);
        return tag;
    }

    private static void assertEntityRoundTrip(int dimension) {
        TestWorld world = new TestWorld(dimension);
        EntityGolemBase source = new EntityGolemBase(world, EnumGolemType.WOOD, false);
        source.setHomePosAndDistance(BlockPos.ORIGIN, 32);
        ArrayList<Marker> markers = new ArrayList<>();
        markers.add(new Marker(7, 8, 9, dimension, (byte) EnumFacing.UP.getIndex(), (byte) 2));
        source.setMarkers(markers);
        NBTTagCompound nbt = new NBTTagCompound();
        source.writeEntityToNBT(nbt);

        NBTTagCompound markerNbt = nbt.getTagList("Markers", 10).getCompoundTagAt(0);
        assertEquals(dimension, markerNbt.getInteger("dim"));

        EntityGolemBase loaded = new EntityGolemBase(world);
        loaded.readEntityFromNBT(nbt);
        assertEquals(1, loaded.getMarkers().size());
        assertEquals(dimension, loaded.getMarkers().get(0).dim);
    }

    private static final class TestWorld extends World {
        private TestWorld(int dimension) {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "r5_marker_dimension"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.provider.setDimension(dimension);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override public Biome getBiome(BlockPos pos) { return Biomes.PLAINS; }
        @Override public TileEntity getTileEntity(BlockPos pos) { return null; }
        @Override public Explosion createExplosion(Entity entityIn, double x, double y, double z,
                                                    float strength, boolean isSmoking) { return null; }
        @Override public void notifyBlockUpdate(BlockPos pos, net.minecraft.block.state.IBlockState oldState,
                                                net.minecraft.block.state.IBlockState newState, int flags) {}
        @Override public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) {}
        @Override public void updateComparatorOutputLevel(BlockPos pos, net.minecraft.block.Block blockIn) {}

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "r5_marker_dimension_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
