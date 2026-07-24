package thaumcraft.api;

import org.junit.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.visnet.TileVisNode;
import thaumcraft.common.world.aura.AuraHandler;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;

public class WitcheryThaumcraftSixApiShimTest {

    @Test
    public void commonInternalsMustExposeTheCanonicalEntityScanList() throws IOException {
        String internals = readFile("src/main/java/thaumcraft/api/internal/CommonInternals.java");

        assertTrue(internals.contains("public static ArrayList<ThaumcraftApi.EntityTags> scanEntities = ThaumcraftApi.scanEntities;"));
    }

    @Test
    public void visAvailabilityMustFollowTheSameRootAsConsumption() {
        TestVisSource source = new TestVisSource(12);
        TestVisRelay relay = new TestVisRelay();
        relay.setParent(new WeakReference<TileVisNode>(source));

        assertEquals(12, relay.getAvailableVis(Aspect.AIR));
        assertEquals(5, relay.consumeVis(Aspect.AIR, 5));
        assertEquals(7, relay.getAvailableVis(Aspect.AIR));
    }

    @Test
    public void tc6DrainMustDelegateToCanonicalVisNet() throws IOException {
        String handler = readFile("src/main/java/thaumcraft/common/world/aura/AuraHandler.java");
        String helper = readFile("src/main/java/thaumcraft/api/aura/AuraHelper.java");

        assertTrue(handler.contains("public static float drainVis(World world, BlockPos pos, float amount, boolean simulate)"));
        assertTrue(handler.contains("VisNetHandler.drainVis(world, pos.getX(), pos.getY(), pos.getZ(), requested, simulate)"));
        assertTrue(helper.contains("return AuraHandler.drainVis(world, pos, amount, simulate);"));
    }

    @Test
    public void unsupportedChunkAuraStateMustFailClosed() {
        try {
            AuraHandler.getAuraChunk(0, 0, 0);
            fail("TC6 chunk aura state must not create a disconnected store");
        } catch (UnsupportedOperationException expected) {
            assertTrue(expected.getMessage().contains("no safe TC4 projection"));
        }
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    private static class TestVisSource extends TileVisNode {
        private final AspectList vis = new AspectList();

        private TestVisSource(int amount) {
            this.vis.add(Aspect.AIR, amount);
        }

        @Override
        public int getRange() {
            return 8;
        }

        @Override
        public boolean isSource() {
            return true;
        }

        @Override
        public int getAvailableVis(Aspect aspect) {
            return this.vis.getAmount(aspect);
        }

        @Override
        public int consumeVis(Aspect aspect, int amount) {
            int drained = Math.min(amount, this.vis.getAmount(aspect));
            this.vis.reduce(aspect, drained);
            return drained;
        }
    }

    private static class TestVisRelay extends TileVisNode {
        @Override
        public int getRange() {
            return 8;
        }

        @Override
        public boolean isSource() {
            return false;
        }
    }
}
