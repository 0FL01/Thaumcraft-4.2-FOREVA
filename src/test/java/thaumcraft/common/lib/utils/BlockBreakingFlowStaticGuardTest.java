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
    public void fakePlayerHarvestPostsBreakEventWithoutUsingPlayerConnection() throws IOException {
        String source = read("src/main/java/thaumcraft/common/lib/utils/BlockUtils.java");
        int fakePlayerBranch = source.indexOf("if (serverPlayer instanceof FakePlayer)");
        int breakEvent = source.indexOf("new BlockEvent.BreakEvent(world, pos, state, serverPlayer)", fakePlayerBranch);
        int eventPost = source.indexOf("MinecraftForge.EVENT_BUS.post(event);", breakEvent);
        int realPlayerHook = source.indexOf("ForgeHooks.onBlockBreakEvent", eventPost);

        assertTrue("Connectionless Forge FakePlayer must use BreakEvent directly before the real-player packet hook",
                fakePlayerBranch >= 0 && breakEvent > fakePlayerBranch && eventPost > breakEvent
                        && realPlayerHook > eventPost);
        assertTrue("Canceled fake-player breaks must remain denied",
                source.contains("xp = event.isCanceled() ? -1 : event.getExpToDrop();"));
    }

    @Test
    public void arcaneBorePostsBreakEventWithoutUsingPlayerConnection() throws IOException {
        String source = read("src/main/java/thaumcraft/common/tiles/TileArcaneBore.java");

        assertFalse("Connectionless bore FakePlayer must not use ForgeHooks.onBlockBreakEvent",
                source.contains("ForgeHooks.onBlockBreakEvent"));
        assertTrue(source.contains("new BlockEvent.BreakEvent(this.world, target, state, this.fakePlayer)")
                && source.contains("MinecraftForge.EVENT_BUS.post(event);")
                && source.contains("if (event.isCanceled()) return false;")
                && source.contains("int xp = event.getExpToDrop();"));
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
    public void chopKeepsTc4TrunkBaseTargetAcrossTopDownBreaks() throws IOException {
        String source = read("src/main/java/thaumcraft/common/entities/ai/interact/AIHarvestLogs.java");
        assertTrue(source.contains("this.distance = MathHelper.floor(golem.getRange() / 3.0F);"));
        assertTrue(source.contains("attempt < this.distance * 4"));
        assertTrue(source.contains("BlockPos below = pos.down();")
                && source.contains("below.getY() + 0.5D")
                && source.contains("< targetDistance"));
        assertTrue(source.contains("state.getBlock() == this.targetBlock")
                && source.contains("state.getBlock().getMetaFromState(state) == this.targetMeta"));

        int harvest = source.indexOf("this.harvest();");
        int rootStillWood = source.indexOf("if (Utils.isWoodLog(theGolem.world, this.targetPos))", harvest);
        int restartAtRoot = source.indexOf("this.startExecuting();", rootStillWood);
        int adjacent = source.indexOf("this.checkAdjacent();", restartAtRoot);
        assertTrue("TC4 Chop must keep working from the trunk base while upper connected logs remain",
                harvest >= 0 && rootStillWood > harvest && restartAtRoot > rootStillWood && adjacent > restartAtRoot);
        assertTrue(source.contains("private void checkAdjacent()"));
        assertFalse("A top-down break must not discard the trunk-base target", source.contains("this.targetPos = null;"));
    }

    @Test
    public void excavationEmitsDestroyEventWhileBoreUsesItsCustomCue() throws IOException {
        assertEventBeforeMutation(
                read("src/main/java/thaumcraft/common/items/wands/foci/FocusExcavation.java"),
                "world.playEvent(2001, pos, Block.getStateId(state));",
                "world.setBlockToAir(pos);");
        String bore = read("src/main/java/thaumcraft/common/tiles/TileArcaneBore.java");
        int customEvent = bore.indexOf("this.world.addBlockEvent(this.pos, ConfigBlocks.blockWoodenDevice, 99,");
        int mutation = bore.indexOf("this.world.setBlockToAir(target);", customEvent);
        assertTrue(customEvent >= 0 && mutation > customEvent);
        assertFalse(bore.contains("this.world.playEvent(2001, target, Block.getStateId(state));"));
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
