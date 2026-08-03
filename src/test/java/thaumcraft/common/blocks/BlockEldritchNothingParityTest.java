package thaumcraft.common.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
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
import thaumcraft.common.tiles.TileEldritchNothing;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class BlockEldritchNothingParityTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void defaultVoidStateIsSolidIdentityButHasNoCollisionOrTile() {
        BlockEldritchNothing block = new BlockEldritchNothing();
        TestWorld world = new TestWorld();
        BlockPos pos = new BlockPos(0, 64, 0);
        IBlockState state = block.getDefaultState();
        world.states.put(pos, state);

        assertFalse(block.isAir(state, world, pos));
        assertEquals(-1.0F, block.getBlockHardness(state, world, pos), 0.0F);
        assertEquals(3, block.getLightValue(state, world, pos));
        assertEquals(new AxisAlignedBB(0.125D, 0.125D, 0.125D, 0.875D, 0.875D, 0.875D),
                block.getBoundingBox(state, world, pos));
        assertEquals(new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D),
                block.getCollisionBoundingBox(state, world, pos));
        assertFalse(block.hasTileEntity(state));
        assertNull(block.createTileEntity(world, state));
    }

    @Test
    public void exposureStateAllocatesOnlyBoundaryTileAndRemovesItWhenEnclosed() {
        BlockEldritchNothing block = new BlockEldritchNothing();
        TestWorld world = new TestWorld();
        BlockPos pos = new BlockPos(0, 64, 0);
        world.states.put(pos, block.getDefaultState());
        world.states.put(pos.east(), Blocks.AIR.getDefaultState());

        block.neighborChanged(world.getBlockState(pos), world, pos, Blocks.AIR, pos.east());
        IBlockState exposed = world.getBlockState(pos);
        assertTrue(exposed.getValue(BlockEldritchNothing.EXPOSED));
        assertTrue(block.hasTileEntity(exposed));
        assertNotNull(world.getTileEntity(pos));

        world.states.put(pos.east(), Blocks.STONE.getDefaultState());
        block.neighborChanged(exposed, world, pos, Blocks.STONE, pos.east());
        IBlockState enclosed = world.getBlockState(pos);
        assertFalse(enclosed.getValue(BlockEldritchNothing.EXPOSED));
        assertFalse(block.hasTileEntity(enclosed));
        assertNull(world.getTileEntity(pos));
    }

    @Test
    public void matureEntityTakesEightPointsOfVoidDamage() {
        BlockEldritchNothing block = new BlockEldritchNothing();
        TestWorld world = new TestWorld();
        TestEntity entity = new TestEntity(world);
        entity.ticksExisted = 21;

        block.onEntityCollision(world, BlockPos.ORIGIN, block.getDefaultState(), entity);

        assertEquals(DamageSource.OUT_OF_WORLD, entity.source);
        assertEquals(8.0F, entity.damage, 0.0F);
    }

    private static final class TestEntity extends Entity {
        private DamageSource source;
        private float damage;

        private TestEntity(World world) {
            super(world);
        }

        @Override protected void entityInit() { }
        @Override protected void readEntityFromNBT(NBTTagCompound compound) { }
        @Override protected void writeEntityToNBT(NBTTagCompound compound) { }

        @Override
        public boolean attackEntityFrom(DamageSource source, float amount) {
            this.source = source;
            this.damage = amount;
            return true;
        }
    }

    private static final class TestWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();

        private TestWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT), "eldritch_void"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            IBlockState state = this.states.get(pos);
            return state == null ? Blocks.STONE.getDefaultState() : state;
        }

        @Override
        public boolean setBlockState(BlockPos pos, IBlockState state, int flags) {
            this.states.put(pos.toImmutable(), state);
            return true;
        }

        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return this.tiles.get(pos);
        }

        @Override
        public void setTileEntity(BlockPos pos, TileEntity tile) {
            tile.setWorld(this);
            tile.setPos(pos);
            this.tiles.put(pos.toImmutable(), tile);
        }

        @Override
        public void removeTileEntity(BlockPos pos) {
            this.tiles.remove(pos);
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "eldritch_void_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
