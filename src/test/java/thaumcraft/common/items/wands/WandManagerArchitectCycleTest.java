package thaumcraft.common.items.wands;

import net.minecraft.init.Bootstrap;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.foci.FocusTrade;
import thaumcraft.common.items.wands.foci.FocusWarding;

import static org.junit.Assert.assertEquals;

public class WandManagerArchitectCycleTest {
    @BeforeClass
    public static void bootstrap() {
        Bootstrap.register();
    }

    @Test
    public void tradeCyclesOnlyTheThreeSupportedDimensionModes() {
        ItemFocusBasic trade = new FocusTrade();
        assertEquals(1, WandManager.getNextAreaDim(trade, 0));
        assertEquals(2, WandManager.getNextAreaDim(trade, 1));
        assertEquals(0, WandManager.getNextAreaDim(trade, 2));
        assertEquals(0, WandManager.getNextAreaDim(trade, 3));
    }

    @Test
    public void wardingRetainsItsSeparateVerticalAxisMode() {
        ItemFocusBasic warding = new FocusWarding();
        assertEquals(1, WandManager.getNextAreaDim(warding, 0));
        assertEquals(2, WandManager.getNextAreaDim(warding, 1));
        assertEquals(3, WandManager.getNextAreaDim(warding, 2));
        assertEquals(0, WandManager.getNextAreaDim(warding, 3));
    }
}
