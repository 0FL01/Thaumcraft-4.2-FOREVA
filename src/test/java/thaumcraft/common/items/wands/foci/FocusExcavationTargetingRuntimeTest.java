package thaumcraft.common.items.wands.foci;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
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
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.items.wands.ItemWandCasting;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FocusExcavationTargetingRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void targetsBlocksThroughTenButNotBeyondAndUsesTc4RayFlags() {
        assertTargeting(5.0D, true);
        assertTargeting(10.0D, true);
        assertTargeting(10.01D, false);
    }

    private static void assertTargeting(double targetDistance, boolean expectedHit) {
        RayWorld world = new RayWorld(targetDistance);
        TestPlayer player = new TestPlayer(world, "excavation_" + targetDistance);
        player.setPosition(0.0D, 64.0D, 0.0D);
        player.rotationYaw = 0.0F;
        player.rotationPitch = 0.0F;
        ItemStack wandStack = new ItemStack(new UnlimitedWand());

        new FocusExcavation().onUsingFocusTick(wandStack, player, 0);

        assertEquals(10.0D, world.start.distanceTo(world.end), 0.000001D);
        assertFalse(world.stopOnLiquid);
        assertTrue(world.ignoreBlockWithoutBoundingBox);
        assertFalse(world.returnLastUncollidableBlock);
        assertEquals(expectedHit, world.targetStateRead);
    }

    private static final class UnlimitedWand extends ItemWandCasting {
        private final ItemStack focusStack = new ItemStack(Items.STICK);

        @Override
        public ItemStack getFocusItem(ItemStack stack) {
            return this.focusStack;
        }

        @Override
        public boolean consumeAllVis(ItemStack stack, EntityPlayer player, AspectList cost,
                                     boolean doit, boolean crafting) {
            return true;
        }
    }

    private static final class TestPlayer extends EntityPlayer {
        private TestPlayer(World world, String name) {
            super(world, new GameProfile(UUID.randomUUID(), name));
        }

        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative() { return false; }
    }

    private static final class RayWorld extends World {
        private final double targetDistance;
        private Vec3d start;
        private Vec3d end;
        private boolean stopOnLiquid;
        private boolean ignoreBlockWithoutBoundingBox;
        private boolean returnLastUncollidableBlock;
        private BlockPos targetPos;
        private boolean targetStateRead;

        private RayWorld(double targetDistance) {
            super(null,
                    new WorldInfo(new WorldSettings(0L, GameType.SURVIVAL, false, false, WorldType.DEFAULT),
                            "excavation_targeting"),
                    new WorldProviderSurface(), new Profiler(), false);
            this.targetDistance = targetDistance;
            this.provider.setWorld(this);
            this.chunkProvider = this.createChunkProvider();
        }

        @Override
        public RayTraceResult rayTraceBlocks(Vec3d start, Vec3d end, boolean stopOnLiquid,
                                              boolean ignoreBlockWithoutBoundingBox,
                                              boolean returnLastUncollidableBlock) {
            this.start = start;
            this.end = end;
            this.stopOnLiquid = stopOnLiquid;
            this.ignoreBlockWithoutBoundingBox = ignoreBlockWithoutBoundingBox;
            this.returnLastUncollidableBlock = returnLastUncollidableBlock;
            if (this.targetDistance > start.distanceTo(end)) return null;

            Vec3d hit = start.add(end.subtract(start).normalize().scale(this.targetDistance));
            this.targetPos = new BlockPos(hit);
            return new RayTraceResult(hit, EnumFacing.NORTH, this.targetPos);
        }

        @Override
        public IBlockState getBlockState(BlockPos pos) {
            if (pos.equals(this.targetPos)) this.targetStateRead = true;
            return Blocks.STONE.getDefaultState();
        }

        @Override
        public boolean isBlockModifiable(EntityPlayer player, BlockPos pos) {
            return true;
        }

        @Override
        protected IChunkProvider createChunkProvider() {
            return new IChunkProvider() {
                @Override public Chunk getLoadedChunk(int x, int z) { return null; }
                @Override public Chunk provideChunk(int x, int z) { return null; }
                @Override public boolean tick() { return false; }
                @Override public String makeString() { return "excavation_targeting_dummy"; }
                @Override public boolean isChunkGeneratedAt(int x, int z) { return true; }
            };
        }

        @Override
        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return true;
        }
    }
}
