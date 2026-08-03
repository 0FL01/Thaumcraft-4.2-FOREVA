package thaumcraft.common.blocks;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.tiles.TileEssentiaReservoir;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class BlockEssentiaReservoirParityTest {
    private static final BlockPos POS = new BlockPos(0, 64, 0);

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void destructionUsesTc4ThresholdExplosionAndSuccessfulSpillCount() {
        TestReservoirBlock block = new TestReservoirBlock();
        SpillWorld belowThreshold = new SpillWorld(block, 0);
        belowThreshold.attach(reservoirWith(15));

        block.breakBlock(belowThreshold, POS, block.getDefaultState());

        assertEquals(0, belowThreshold.explosions);
        assertEquals(0, belowThreshold.placements);

        SpillWorld spilling = new SpillWorld(block, 3);
        spilling.attach(reservoirWith(16));

        block.breakBlock(spilling, POS, block.getDefaultState());

        assertEquals(1, spilling.explosions);
        assertEquals(1.0F, spilling.explosionStrength, 0.0F);
        assertFalse(spilling.explosionDamagesTerrain);
        assertEquals(2, spilling.placements);
        assertEquals(5, spilling.airChecks);
    }

    @Test
    public void wandItemAndBlockDispatchInvokeReservoirOnce() {
        TestReservoirBlock block = new TestReservoirBlock();
        SpillWorld world = new SpillWorld(block, 0);
        CountingReservoir reservoir = new CountingReservoir();
        world.attach(reservoir);
        TestPlayer player = new TestPlayer(world);
        ItemWandCasting wand = new ItemWandCasting();
        player.inventory.currentItem = 0;
        player.inventory.mainInventory.set(0, new ItemStack(wand));

        EnumActionResult result = wand.onItemUseFirst(player, world, POS, EnumFacing.NORTH,
                0.5F, 0.5F, 0.5F, EnumHand.MAIN_HAND);
        boolean activated = block.onBlockActivated(world, POS, block.getDefaultState(), player,
                EnumHand.MAIN_HAND, EnumFacing.NORTH, 0.5F, 0.5F, 0.5F);

        assertEquals(EnumActionResult.PASS, result);
        assertFalse(activated);
        assertEquals(1, reservoir.wandCallbacks);
    }

    private static TileEssentiaReservoir reservoirWith(int amount) {
        TileEssentiaReservoir reservoir = new TileEssentiaReservoir();
        reservoir.essentia.add(Aspect.WATER, amount);
        return reservoir;
    }

    private static final class TestReservoirBlock extends BlockEssentiaReservoir {
        @Override
        protected IBlockState getFluxState(boolean goo) {
            return goo ? Blocks.WATER.getDefaultState() : Blocks.FIRE.getDefaultState();
        }
    }

    private static final class CountingReservoir extends TileEssentiaReservoir {
        private int wandCallbacks;

        @Override
        public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player,
                                    int x, int y, int z, int side, int md) {
            ++this.wandCallbacks;
            return 0;
        }
    }

    private static final class TestPlayer extends EntityPlayer {
        private TestPlayer(World world) {
            super(world, new GameProfile(UUID.nameUUIDFromBytes(
                    "reservoir_wand".getBytes(StandardCharsets.UTF_8)), "reservoir_wand"));
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
    }

    private static final class SpillWorld extends World {
        private final TestReservoirBlock block;
        private final int blockedChecks;
        private TileEssentiaReservoir reservoir;
        private int airChecks;
        private int placements;
        private int explosions;
        private float explosionStrength;
        private boolean explosionDamagesTerrain;

        private SpillWorld(TestReservoirBlock block, int blockedChecks) {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "reservoir_parity"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.block = block;
            this.blockedChecks = blockedChecks;
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        private void attach(TileEssentiaReservoir reservoir) {
            this.reservoir = reservoir;
            reservoir.setWorld(this);
            reservoir.setPos(POS);
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            return pos.equals(POS) ? this.block.getDefaultState() : Blocks.AIR.getDefaultState();
        }

        @Override
        public TileEntity getTileEntity(BlockPos pos) {
            return pos.equals(POS) ? this.reservoir : null;
        }

        @Override
        public void removeTileEntity(BlockPos pos) {
            if (pos.equals(POS)) this.reservoir = null;
        }

        @Override
        public boolean isAirBlock(BlockPos pos) {
            return this.airChecks++ >= this.blockedChecks;
        }

        @Override
        public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
            ++this.placements;
            return true;
        }

        @Override
        public Explosion createExplosion(Entity entityIn, double x, double y, double z, float strength,
                                         boolean isSmoking) {
            ++this.explosions;
            this.explosionStrength = strength;
            this.explosionDamagesTerrain = isSmoking;
            return null;
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "reservoir_parity_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
