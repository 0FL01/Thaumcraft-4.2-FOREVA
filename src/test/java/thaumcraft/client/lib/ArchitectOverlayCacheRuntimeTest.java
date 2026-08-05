package thaumcraft.client.lib;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ArchitectOverlayCacheRuntimeTest {

    @Test
    public void refreshLimiterAllowsAtMostOneRecomputeEveryFivePlayerTicks() {
        REHWandHandler.ArchitectRefreshLimiter limiter = new REHWandHandler.ArchitectRefreshLimiter();

        assertTrue(limiter.shouldRefresh(20));
        for (int tick = 20; tick < 25; tick++) {
            assertFalse("tick " + tick + " must reuse the cached architect coordinates",
                    limiter.shouldRefresh(tick));
        }
        assertTrue(limiter.shouldRefresh(25));
        assertFalse(limiter.shouldRefresh(29));
        assertTrue(limiter.shouldRefresh(30));
        assertTrue("a player/world tick reset starts a new cadence", limiter.shouldRefresh(0));
    }

    @Test
    public void adjacentArchitectCoordinatesOnlyExposeExteriorFaces() {
        Set<BlockPos> blocks = new HashSet<>(Arrays.asList(BlockPos.ORIGIN, BlockPos.ORIGIN.east()));
        int exteriorFaces = 0;
        for (BlockPos pos : blocks) {
            for (EnumFacing face : EnumFacing.VALUES) {
                if (REHWandHandler.isArchitectFaceExterior(blocks, pos, face)) {
                    exteriorFaces++;
                }
            }
        }

        assertEquals(10, exteriorFaces);
        assertFalse(REHWandHandler.isArchitectFaceExterior(blocks, BlockPos.ORIGIN, EnumFacing.EAST));
        assertTrue(REHWandHandler.isArchitectFaceExterior(blocks, BlockPos.ORIGIN, EnumFacing.UP));
    }
}
