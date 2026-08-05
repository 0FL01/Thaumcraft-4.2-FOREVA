package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ArchitectOverlayStaticGuardTest {

    @Test
    public void architectOverlayRoutesIndependentlyOfGogglesAndKeepsOracleCancellation() throws IOException {
        String events = read("src/main/java/thaumcraft/client/lib/RenderEventHandler.java");
        String handler = read("src/main/java/thaumcraft/client/lib/REHWandHandler.java");

        assertFalse("missing goggles must not return before architect handling",
                events.contains("if (!canShowGogglesPopups(player))"));
        assertTrue("goggles and architect rendering must remain independent branches",
                events.contains("if (canShowGogglesPopups(player))")
                        && events.indexOf("ItemStack held = player.getHeldItemMainhand();")
                        > events.indexOf("if (canShowGogglesPopups(player))"));
        assertTrue("the actual main-hand IArchitect stack and selected hit face must drive the preview",
                events.contains("held.getItem() instanceof IArchitect")
                        && events.contains("handleArchitectOverlay(held, event, player.ticksExisted, target)")
                        && handler.contains("int side = target.sideHit.getIndex();")
                        && handler.contains("IArchitect architect = (IArchitect) stack.getItem();"));
        assertTrue("vanilla highlight is canceled only after a non-empty architect overlay reports success",
                events.contains("&& this.wandHandler.handleArchitectOverlay")
                        && events.contains("event.setCanceled(true);")
                        && handler.contains("if (this.architectBlocks.isEmpty())")
                        && handler.contains("return false;")
                        && handler.contains("return true;"));
    }

    @Test
    public void architectCacheInvalidatesEveryTargetingInputAndKeepsTc4DisplayHooks() throws IOException {
        String handler = read("src/main/java/thaumcraft/client/lib/REHWandHandler.java");

        assertTrue("recomputation must be gated by the five-player-tick limiter",
                handler.contains("static final int REFRESH_TICKS = 5;")
                        && handler.contains("if (this.architectRefreshLimiter.shouldRefresh(playerTicks))")
                        && occurrences(handler, ".getArchitectBlocks(") == 1);
        assertTrue("player, world, wand and focus changes must invalidate cached coordinates",
                handler.contains("player != this.architectPlayer")
                        && handler.contains("player.world != this.architectWorld")
                        && handler.contains("stack != this.architectWandStack")
                        && handler.contains("!ItemStack.areItemStacksEqual(focusStack, this.architectFocusStack)"));
        assertTrue("target coordinate, selected face and all area state must invalidate cached coordinates",
                handler.contains("!targetPos.equals(this.architectTarget)")
                        && handler.contains("side != this.architectSide")
                        && handler.contains("areaX != this.architectAreaX")
                        && handler.contains("areaY != this.architectAreaY")
                        && handler.contains("areaZ != this.architectAreaZ")
                        && handler.contains("areaDim != this.architectAreaDim")
                        && handler.contains("this.architectBlocks = Collections.emptyList();"));
        assertTrue("coordinates must use set membership, connected sprites and exterior faces",
                handler.contains("Set<BlockPos> positions = new HashSet<>(blocks.size())")
                        && handler.contains("isArchitectFaceExterior(this.architectBlockSet, pos, face)")
                        && handler.contains("ConnectedTextureUtils.getTextureIndex"));
        assertTrue("the target-coordinate axis state must come from all three IArchitect hooks",
                handler.contains("drawArchitectAxis(targetPos")
                        && handler.contains("IArchitect.EnumAxis.X")
                        && handler.contains("IArchitect.EnumAxis.Y")
                        && handler.contains("IArchitect.EnumAxis.Z")
                        && handler.contains("ARCHITECT_ARROW_TEXTURE"));
    }

    private static int occurrences(String source, String token) {
        int count = 0;
        int at = 0;
        while ((at = source.indexOf(token, at)) >= 0) {
            count++;
            at += token.length();
        }
        return count;
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
