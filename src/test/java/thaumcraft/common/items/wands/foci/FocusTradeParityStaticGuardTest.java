package thaumcraft.common.items.wands.foci;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FocusTradeParityStaticGuardTest {

    @Test
    public void tradeFocusShouldKeepImmediateClientSwingFeedback() throws IOException {
        String source = read("src/main/java/thaumcraft/common/items/wands/foci/FocusTrade.java");
        int pickedStart = source.indexOf("ItemStack picked = this.getPickedBlock(wandStack);");
        int serverWork = source.indexOf("ItemWandCasting wand =", pickedStart);
        String clientFeedback = source.substring(pickedStart, serverWork);

        assertTrue(clientFeedback.contains("if (world.isRemote)"));
        assertTrue(clientFeedback.contains("player.swingArm(net.minecraft.util.EnumHand.MAIN_HAND);"));
    }

    @Test
    public void swapQueueMustNeverBreakNeighboursBeforeTheyAreReplaced() throws IOException {
        String source = read("src/main/java/thaumcraft/common/lib/events/ServerTickEventsFML.java");
        String tickSwap = method(source, "private void tickBlockSwap", "public static void addSwapper");

        assertFalse(tickSwap.contains("breakFurthestBlock"));
        assertTrue(tickSwap.contains("BlockUtils.isBlockExposed(world, np.getX(), np.getY(), np.getZ())"));
        assertTrue(tickSwap.contains("queue.offer(new VirtualSwapper"));
    }

    @Test
    public void swapShouldChargeAndRewardOnlyAfterSuccessfulPlacement() throws IOException {
        String source = read("src/main/java/thaumcraft/common/lib/events/ServerTickEventsFML.java");
        String tickSwap = method(source, "private void tickBlockSwap", "public static void addSwapper");
        int placement = tickSwap.indexOf("if (!world.setBlockState(pos, targetState, 3)) continue;");

        assertTrue(placement >= 0);
        assertTrue(tickSwap.indexOf("decrStackSize(slot, 1)") > placement);
        assertTrue(tickSwap.indexOf("focus.getVisCost(focusStack), true, false)") > placement);
        assertTrue(tickSwap.indexOf("addItemStackToInventory(is)") > placement);
        assertTrue(tickSwap.contains("if (targetBlock == Blocks.AIR) continue;"));
    }

    @Test
    public void successfulSwapShouldKeepTc4SparklesSoundAndCorrectBreakState() throws IOException {
        String source = read("src/main/java/thaumcraft/common/lib/events/ServerTickEventsFML.java");

        assertTrue(source.contains("new PacketFXBlockSparkle(vs.x, vs.y, vs.z, 0xC0C0FF)"));
        assertTrue(source.contains("world.playEvent(2001, pos, Block.getStateId(sourceState));"));
        assertTrue(source.contains("world.playSound(null, pos, TCSounds.WAND, SoundCategory.PLAYERS"));
    }

    private static String method(String source, String startMarker, String endMarker) {
        int start = source.indexOf(startMarker);
        int end = source.indexOf(endMarker, start);
        return source.substring(start, end);
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
