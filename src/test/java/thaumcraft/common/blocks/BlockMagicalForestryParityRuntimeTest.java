package thaumcraft.common.blocks;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.blocks.ItemBlocks.BlockMagicalLeavesItem;

import java.lang.reflect.Method;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class BlockMagicalForestryParityRuntimeTest {

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void leafMetadataRoundTripsTypePlayerAndDecayBits() {
        BlockMagicalLeaves leaves = new BlockMagicalLeaves();
        assertTrue(leaves instanceof BlockLeaves);
        for (int meta : new int[]{0, 1, 4, 5, 8, 9, 12, 13}) {
            assertEquals(meta, leaves.getMetaFromState(leaves.getStateFromMeta(meta)));
        }

        assertTrue(leaves.getStateFromMeta(0).getValue(BlockLeaves.DECAYABLE));
        assertFalse(leaves.getStateFromMeta(0).getValue(BlockLeaves.CHECK_DECAY));
        assertFalse(leaves.getStateFromMeta(4).getValue(BlockLeaves.DECAYABLE));
        assertTrue(leaves.getStateFromMeta(8).getValue(BlockLeaves.CHECK_DECAY));

        BlockMagicalLeavesItem item = new BlockMagicalLeavesItem(leaves);
        assertEquals(4, item.getMetadata(0));
        assertEquals(5, item.getMetadata(1));
    }

    @Test
    public void onlyNaturalDecayingLeavesRollExactSaplingOdds() {
        BlockMagicalLeaves leaves = new BlockMagicalLeaves();
        RecordingRandom random = new RecordingRandom();

        assertTrue(BlockMagicalLeaves.shouldDropSapling(leaves.getStateFromMeta(8), random));
        assertEquals(200, random.lastBound);
        assertTrue(BlockMagicalLeaves.shouldDropSapling(leaves.getStateFromMeta(9), random));
        assertEquals(250, random.lastBound);

        random.lastBound = -1;
        assertFalse(BlockMagicalLeaves.shouldDropSapling(leaves.getStateFromMeta(0), random));
        assertEquals(-1, random.lastBound);
        assertFalse(BlockMagicalLeaves.shouldDropSapling(leaves.getStateFromMeta(12), random));
        assertEquals(-1, random.lastBound);
    }

    @Test
    public void leavesHaveNoDefaultDropAndSilverwoodEmitsLight() {
        BlockMagicalLeaves leaves = new BlockMagicalLeaves();
        assertSame(Items.AIR, leaves.getItemDropped(leaves.getStateFromMeta(0), new Random(1L), 0));
        assertEquals(0, leaves.quantityDropped(new Random(1L)));
        assertEquals(0, leaves.getLightValue(leaves.getStateFromMeta(0), null, BlockPos.ORIGIN));
        assertEquals(7, leaves.getLightValue(leaves.getStateFromMeta(1), null, BlockPos.ORIGIN));

        BlockMagicalLog log = new BlockMagicalLog();
        assertEquals(0, log.getLightValue(log.getStateFromMeta(0), null, BlockPos.ORIGIN));
        assertEquals(0, log.getLightValue(log.getStateFromMeta(1), null, BlockPos.ORIGIN));
        assertEquals(7, log.getLightValue(log.getStateFromMeta(2), null, BlockPos.ORIGIN));
        assertEquals(7, log.getLightValue(log.getStateFromMeta(3), null, BlockPos.ORIGIN));
    }

    @Test
    public void logUsesVanillaRadiusFourDecayTrigger() throws Exception {
        Method breakBlock = BlockMagicalLog.class.getMethod("breakBlock",
                World.class, BlockPos.class, net.minecraft.block.state.IBlockState.class);
        assertSame(BlockLog.class, breakBlock.getDeclaringClass());
    }

    private static final class RecordingRandom extends Random {
        private int lastBound = -1;

        @Override
        public int nextInt(int bound) {
            this.lastBound = bound;
            return 0;
        }
    }
}
