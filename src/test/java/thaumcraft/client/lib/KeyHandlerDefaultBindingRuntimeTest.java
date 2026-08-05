package thaumcraft.client.lib;

import org.junit.Test;
import org.lwjgl.input.Keyboard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class KeyHandlerDefaultBindingRuntimeTest {

    @Test
    public void selectorDefaultsToGInsteadOfVanillaSwapHandsF() {
        assertEquals(Keyboard.KEY_G, KeyHandler.DEFAULT_FOCUS_KEY);
        assertFalse(KeyHandler.DEFAULT_FOCUS_KEY == Keyboard.KEY_F);
        assertEquals(Keyboard.KEY_NONE, KeyHandler.DEFAULT_MISC_KEY);
    }

    @Test
    public void selectorWinsWhenUserOptionsBindBothActionsToSamePhysicalKey() {
        assertFalse(KeyHandler.shouldHandleMiscKey(true, Keyboard.KEY_G, true, Keyboard.KEY_G));
        assertTrue(KeyHandler.shouldHandleMiscKey(true, Keyboard.KEY_G, true, Keyboard.KEY_V));
        assertTrue(KeyHandler.shouldHandleMiscKey(false, Keyboard.KEY_G, true, Keyboard.KEY_G));
        assertFalse(KeyHandler.shouldHandleMiscKey(false, Keyboard.KEY_G, false, Keyboard.KEY_V));
    }
}
