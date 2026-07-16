package thaumcraft.common.lib.utils;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BlockBreakingFlowStaticGuardTest {

    @Test
    public void connectedLogHarvestUsesForgeSafeBreakFlow() throws IOException {
        String source = read("src/main/java/thaumcraft/common/lib/utils/BlockUtils.java");
        assertFalse(source.contains("Simplified: break the block at the given position"));
        assertTrue(source.contains("ForgeHooks.onBlockBreakEvent"));
        assertTrue(source.contains("world.playEvent(2001, pos, Block.getStateId(state));"));
        assertTrue(source.contains("block.removedByPlayer(state, world, pos, player, canHarvest)"));
        assertTrue(source.contains("Utils.isWoodLog(world, candidate)"));
        assertTrue(source.contains("candidate.distanceSq(pos)"));
        assertTrue(source.contains("world.sendBlockBreakProgress(breakerId, pos, progress)"));
    }

    @Test
    public void golemHarvestersRestoreTimedCracksAndCentralHarvest() throws IOException {
        String logs = read("src/main/java/thaumcraft/common/entities/ai/interact/AIHarvestLogs.java");
        String crops = read("src/main/java/thaumcraft/common/entities/ai/interact/AIHarvestCrops.java");
        assertTrue(logs.contains("(20 - theGolem.getGolemStrength() * 3) * hardness")
                && logs.contains("BlockUtils.destroyBlockPartially")
                && logs.contains("BlockUtils.breakFurthestBlock"));
        assertTrue(crops.contains("(20 - theGolem.getGolemStrength() * 2) * hardness")
                && crops.contains("BlockUtils.destroyBlockPartially")
                && crops.contains("BlockUtils.harvestBlock"));
        assertFalse(logs.contains("while (broken < 20"));
        assertFalse(crops.contains("world.setBlockToAir(targetPos)"));
    }

    @Test
    public void excavationAndBoreEmitDestroyEventBeforeMutation() throws IOException {
        assertEventBeforeMutation(
                read("src/main/java/thaumcraft/common/items/wands/foci/FocusExcavation.java"),
                "world.playEvent(2001, pos, Block.getStateId(state));",
                "world.setBlockToAir(pos);");
        assertEventBeforeMutation(
                read("src/main/java/thaumcraft/common/tiles/TileArcaneBore.java"),
                "this.world.playEvent(2001, target, Block.getStateId(state));",
                "this.world.setBlockToAir(target);");
    }

    @Test
    public void swapperEmitsSourceFxOnlyAfterSuccessfulServerReplacement() throws IOException {
        String source = read("src/main/java/thaumcraft/common/lib/events/ServerTickEventsFML.java");
        String replacement = "if (!world.setBlockState(pos, targetState, 1)) continue;";
        String event = "world.playEvent(2001, pos, Block.getStateId(sourceState));";
        String clientUpdate = "world.notifyBlockUpdate(pos, sourceState, targetState, 2);";
        int replacementIndex = source.indexOf(replacement);
        int eventIndex = source.indexOf(event, replacementIndex);
        int updateIndex = source.indexOf(clientUpdate, eventIndex);
        assertTrue("Swapper must keep the source state client-side until destroy FX are dispatched",
                replacementIndex >= 0 && eventIndex > replacementIndex && updateIndex > eventIndex);
    }

    private static void assertEventBeforeMutation(String source, String event, String mutation) {
        int eventIndex = source.indexOf(event);
        int mutationIndex = source.indexOf(mutation, eventIndex);
        assertTrue("Expected destroy event before block mutation", eventIndex >= 0 && mutationIndex > eventIndex);
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
