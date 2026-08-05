package thaumcraft.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class KeyHandlerMiscRouteStaticGuardTest {
    @Test
    public void miscKeyKeepsIndependentRisingEdgeRouteAndYieldsSharedKeyToSelector() throws IOException {
        String source = new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/client/lib/KeyHandler.java")), StandardCharsets.UTF_8);
        int start = source.indexOf("private void handleMiscKey(EntityPlayer player)");
        int end = source.indexOf("private void releaseAllKeys()", start);
        String miscRoute = source.substring(start, end);

        assertTrue(source.contains("new KeyBinding(\"Misc Wand Toggle\","));
        assertTrue(source.contains("KeyConflictContext.IN_GAME, DEFAULT_MISC_KEY"));
        assertTrue(miscRoute.contains("shouldHandleMiscKey(focusDown, this.keyF.getKeyCode(), miscDown, this.keyG.getKeyCode())"));
        assertTrue(miscRoute.contains("if (player != null && !this.keyPressedG)"));
        assertTrue(miscRoute.contains("new PacketItemKeyToServer(player, 1)"));
        assertFalse(miscRoute.contains("PacketFocusChangeToServer"));
        assertFalse(miscRoute.contains("radialActive"));
        assertFalse(miscRoute.contains("ItemWandCasting"));
        assertFalse(miscRoute.contains("getHeldItem"));
    }
}
