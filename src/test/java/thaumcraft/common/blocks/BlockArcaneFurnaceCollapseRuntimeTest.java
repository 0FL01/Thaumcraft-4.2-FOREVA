package thaumcraft.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
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
import thaumcraft.common.tiles.TileArcaneFurnace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class BlockArcaneFurnaceCollapseRuntimeTest {
    private static final BlockPos CORE = new BlockPos(0, 64, 0);
    private static final BlockPos SHELL = CORE.west();

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void shellPlayerBreakRestoresFurnaceAndReleasesOneTc4Blaze() {
        BlockArcaneFurnace block = new BlockArcaneFurnace();
        FurnaceWorld world = FurnaceWorld.formed(block, true);

        assertTrue(world.destroyAsPlayer(SHELL));

        world.assertRestoredAfter(SHELL);
        assertTc4Blaze(world);
    }

    @Test
    public void directCorePlayerBreakDoesNotReleaseDuplicateBlazes() {
        BlockArcaneFurnace block = new BlockArcaneFurnace();
        FurnaceWorld world = FurnaceWorld.formed(block, true);

        assertTrue(world.destroyAsPlayer(CORE));

        world.assertRestoredAfter(CORE);
        assertTc4Blaze(world);
    }

    @Test
    public void missingCoreTileSuppressesOnlyBlazeNotRestoration() {
        BlockArcaneFurnace block = new BlockArcaneFurnace();
        FurnaceWorld world = FurnaceWorld.formed(block, false);

        assertTrue(world.destroyAsPlayer(SHELL));

        world.assertRestoredAfter(SHELL);
        assertTrue(world.spawnedEntities.isEmpty());
    }

    private static void assertTc4Blaze(FurnaceWorld world) {
        assertEquals(1, world.spawnedEntities.size());
        assertTrue(world.spawnedEntities.get(0) instanceof EntityBlaze);
        EntityBlaze blaze = (EntityBlaze) world.spawnedEntities.get(0);
        assertEquals(CORE.getX() + 0.5D, blaze.posX, 0.0D);
        assertEquals(CORE.getY() + 1.0D, blaze.posY, 0.0D);
        assertEquals(CORE.getZ() + 0.5D, blaze.posZ, 0.0D);
        assertEquals(0.0F, blaze.rotationYaw, 0.0F);
        assertEquals(0.0F, blaze.rotationPitch, 0.0F);

        PotionEffect regeneration = blaze.getActivePotionEffect(MobEffects.REGENERATION);
        PotionEffect resistance = blaze.getActivePotionEffect(MobEffects.RESISTANCE);
        assertNotNull(regeneration);
        assertNotNull(resistance);
        assertEquals(6000, regeneration.getDuration());
        assertEquals(2, regeneration.getAmplifier());
        assertEquals(12000, resistance.getDuration());
        assertEquals(0, resistance.getAmplifier());
        assertNull(blaze.getActivePotionEffect(MobEffects.FIRE_RESISTANCE));
    }

    private static final class FurnaceWorld extends World {
        private final Map<BlockPos, IBlockState> states = new HashMap<>();
        private final Map<BlockPos, TileEntity> tiles = new HashMap<>();
        private final Map<BlockPos, Integer> formedMetadata = new HashMap<>();
        private final List<Entity> spawnedEntities = new ArrayList<>();

        private FurnaceWorld() {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "infernal_furnace_collapse_runtime"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        private static FurnaceWorld formed(BlockArcaneFurnace block, boolean withCoreTile) {
            FurnaceWorld world = new FurnaceWorld();
            for (int yy = -1; yy <= 1; ++yy) {
                for (int zz = -1; zz <= 1; ++zz) {
                    for (int xx = -1; xx <= 1; ++xx) {
                        if (yy == 1 && xx == 0 && zz == 0) {
                            continue;
                        }
                        int meta = (zz + 1) * 3 + xx + 2;
                        if (yy == 0 && xx == 0 && zz == 0) {
                            meta = 0;
                        } else if (yy == 0 && xx == 1 && zz == 0) {
                            meta = 10;
                        }
                        BlockPos target = CORE.add(xx, yy, zz);
                        world.put(target, block.getDefaultState().withProperty(BlockArcaneFurnace.TYPE, meta));
                        world.formedMetadata.put(target, meta);
                    }
                }
            }
            if (withCoreTile) {
                TileArcaneFurnace furnace = new TileArcaneFurnace();
                furnace.setWorld(world);
                furnace.setPos(CORE);
                world.tiles.put(CORE, furnace);
            }
            return world;
        }

        private boolean destroyAsPlayer(BlockPos pos) {
            IBlockState oldState = this.getBlockState(pos);
            if (oldState.getBlock() == Blocks.AIR || !this.setBlockState(pos, Blocks.AIR.getDefaultState(), 3)) {
                return false;
            }
            oldState.getBlock().onPlayerDestroy(this, pos, oldState);
            return true;
        }

        private void assertRestoredAfter(BlockPos destroyedPos) {
            for (Map.Entry<BlockPos, Integer> entry : this.formedMetadata.entrySet()) {
                BlockPos pos = entry.getKey();
                int meta = entry.getValue();
                Block expected = pos.equals(destroyedPos) || meta == 0
                        ? Blocks.AIR
                        : meta == 10
                        ? Blocks.IRON_BARS
                        : meta % 2 == 0 || meta == 5
                        ? Blocks.OBSIDIAN
                        : Blocks.NETHER_BRICK;
                assertSame("Unexpected restored block at " + pos, expected, this.getBlockState(pos).getBlock());
            }
            assertSame(Blocks.AIR, this.getBlockState(CORE.up()).getBlock());
            assertFalse(this.tiles.containsKey(CORE));
        }

        private void put(BlockPos pos, IBlockState state) {
            this.states.put(pos.toImmutable(), state);
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            IBlockState state = this.states.get(pos);
            return state == null ? Blocks.AIR.getDefaultState() : state;
        }

        @Override
        public boolean setBlockState(BlockPos pos, IBlockState state, int flags) {
            IBlockState oldState = this.getBlockState(pos);
            if (oldState == state) {
                return false;
            }
            if (state.getBlock() == Blocks.AIR) {
                this.states.remove(pos);
            } else {
                this.states.put(pos.toImmutable(), state);
            }
            if (oldState.getBlock() != state.getBlock()) {
                oldState.getBlock().breakBlock(this, pos, oldState);
            }
            return true;
        }

        @Override
        public boolean setBlockToAir(BlockPos pos) {
            return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }

        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return this.tiles.get(pos);
        }

        @Override
        public void removeTileEntity(BlockPos pos) {
            this.tiles.remove(pos);
        }

        @Override
        public boolean spawnEntity(Entity entity) {
            this.spawnedEntities.add(entity);
            return true;
        }

        @Override public Explosion createExplosion(Entity entityIn, double x, double y, double z,
                                                   float strength, boolean isSmoking) { return null; }
        @Override public void notifyBlockUpdate(BlockPos pos, IBlockState oldState,
                                                IBlockState newState, int flags) { }
        @Override public void notifyNeighborsOfStateChange(BlockPos pos, Block blockType,
                                                           boolean updateObservers) { }
        @Override public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) { }
        @Override public void updateComparatorOutputLevel(BlockPos pos, Block blockIn) { }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "infernal_furnace_collapse_runtime_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
