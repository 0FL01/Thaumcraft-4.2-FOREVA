package thaumcraft.client.gui;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GuiArcaneWorkbenchVisualMathTest {

    @Test
    public void ghostColorShouldApplyOriginalDarknessAndAlphaToItemTint() {
        assertEquals(0xA8545454, GuiArcaneWorkbench.ghostColor(0xFFFFFF));
        assertEquals(0xA8422111, GuiArcaneWorkbench.ghostColor(0xC86432));
    }
}
