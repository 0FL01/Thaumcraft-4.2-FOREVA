package thaumcraft.common.container;

import net.minecraft.inventory.ClickType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ContainerArcaneWorkbenchClickNormalizationTest {

    @Test
    public void throwClickUsesLegacyForcedDragTypePath() {
        assertTrue(ContainerArcaneWorkbench.isThrowClick(ClickType.THROW));
        assertFalse(ContainerArcaneWorkbench.isThrowClick(ClickType.SWAP));
        assertFalse(ContainerArcaneWorkbench.isThrowClick(ClickType.PICKUP));
    }

    @Test
    public void outputAndWandSlotsClampPositiveDragType() {
        assertEquals(0, ContainerArcaneWorkbench.normalizeDragType(0, 1));
        assertEquals(0, ContainerArcaneWorkbench.normalizeDragType(1, 8));
        assertEquals(0, ContainerArcaneWorkbench.normalizeDragType(0, 99));
    }

    @Test
    public void nonSpecialSlotsKeepDragTypeUnchanged() {
        assertEquals(2, ContainerArcaneWorkbench.normalizeDragType(2, 2));
        assertEquals(7, ContainerArcaneWorkbench.normalizeDragType(20, 7));
        assertEquals(0, ContainerArcaneWorkbench.normalizeDragType(0, 0));
        assertEquals(-1, ContainerArcaneWorkbench.normalizeDragType(1, -1));
    }
}
