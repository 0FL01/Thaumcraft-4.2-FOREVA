package thaumcraft.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class KeyHandlerRadialLifecycleStaticGuardTest {
    @Test
    public void radialMouseUngrabKeepsFocusKeyTrackedUntilPhysicalRelease() throws IOException {
        String source = new String(Files.readAllBytes(Paths.get(
                "src/main/java/thaumcraft/client/lib/KeyHandler.java")), StandardCharsets.UTF_8);
        String clientTick = section(source, "public void clientTick", "private void handleFocusKey");
        String focusRoute = section(source, "private void handleFocusKey", "private void handleHoverKey");
        String focusRelease = focusRoute.substring(focusRoute.indexOf("} else {\n            radialActive = false;"));
        String globalRelease = source.substring(source.indexOf("private void releaseAllKeys()"));

        assertTrue(clientTick.contains("if (!minecraft.inGameHasFocus)"));
        assertTrue(clientTick.contains("if (radialActive && minecraft.currentScreen == null && Display.isActive())"));
        assertTrue(clientTick.contains("handleFocusKey(player);\n            } else {\n                releaseAllKeys();"));
        assertTrue(clientTick.contains("}\n            return;"));
        assertTrue(focusRoute.contains("this.keyPressedF = true;"));

        assertTrue(focusRelease.contains("radialActive = false;"));
        assertTrue(focusRelease.contains("this.keyPressedF = false;"));
        assertFalse(focusRelease.contains("radialLock = true"));

        assertTrue(globalRelease.contains("radialActive = false;"));
        assertTrue(globalRelease.contains("this.keyPressedF = false;"));
        assertTrue(source.contains("new KeyBinding(\"Wand Focus Selector\","));
        assertTrue(source.contains("KeyConflictContext.IN_GAME, DEFAULT_FOCUS_KEY"));
        assertTrue(source.contains("new KeyBinding(\"Misc Wand Toggle\","));
    }

    private static String section(String source, String startMarker, String endMarker) {
        int start = source.indexOf(startMarker);
        int end = source.indexOf(endMarker, start);
        assertTrue("missing start marker: " + startMarker, start >= 0);
        assertTrue("missing end marker: " + endMarker, end > start);
        return source.substring(start, end);
    }
}
