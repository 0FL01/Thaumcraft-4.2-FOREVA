package thaumcraft.rendering;

import java.lang.reflect.Method;
import org.junit.Test;
import thaumcraft.client.renderers.tile.TileCrucibleRenderer;

import static org.junit.Assert.assertEquals;

public class CrucibleFluidColorContractTest {

    @Test
    public void fluidColorShouldMatchTc4AcrossCapacityAndOverflow() throws Exception {
        int[] amounts = {0, 1, 50, 100, 200, 500};
        int[] expected = {
                0xFFFFFFFF,
                0xFFD47EBE,
                0xFFBF3F9F,
                0xFFA9007F,
                0xFF7F003F,
                0xFF000000
        };
        Method getFluidColor = TileCrucibleRenderer.class
                .getDeclaredMethod("getFluidColor", int.class, int.class);
        getFluidColor.setAccessible(true);

        for (int i = 0; i < amounts.length; i++) {
            int actual = (Integer) getFluidColor.invoke(null, amounts[i], 100);
            assertEquals("ARGB at " + amounts[i] + " aspects", expected[i], actual);
        }
    }
}
