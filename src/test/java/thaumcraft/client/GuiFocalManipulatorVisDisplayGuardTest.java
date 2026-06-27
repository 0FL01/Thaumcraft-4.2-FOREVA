package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Guards the Focal Manipulator GUI against the centi-vis display regression: Vis
 * is stored internally as centi-vis (100 = 1.0 Vis), so every on-screen Vis value
 * must be divided by 100 before rendering. The 1.7.10 original divided by 100.0f
 * with a DecimalFormat in its aspect list; the 1.12.2 port must do the same.
 */
public class GuiFocalManipulatorVisDisplayGuardTest {

    @Test
    public void focalManipulatorGuiDisplaysVisInWholeUnitsNotCentiVis() throws IOException {
        String source = read("src/main/java/thaumcraft/client/gui/GuiFocalManipulator.java");

        // The shared centi-vis -> Vis formatter must exist (matches the 1.7.10
        // DecimalFormat("#######.#") used for the wand table aspect list).
        assertTrue("GUI must declare a Vis DecimalFormat for centi-vis conversion",
                source.contains("DecimalFormat(\"#######.#\")"));

        // The per-aspect upgrade cost must be divided by 100, not shown raw.
        assertTrue("selectedCost amount must be divided by 100.0F for display",
                source.contains("VIS_FORMAT.format((float) this.selectedCost.getAmount(aspect) / 100.0F)"));
        assertFalse("selectedCost amount must not be rendered as a raw int",
                source.contains("String.valueOf(this.selectedCost.getAmount(aspect))"));

        // The in-progress remaining Vis text must likewise be divided by 100.
        assertTrue("remaining visSize must be divided by 100.0F for display",
                source.contains("VIS_FORMAT.format((float) remaining / 100.0F)"));
        assertFalse("remaining visSize must not be concatenated as a raw int",
                source.contains("wandtable.text1\") + \": \" + remaining"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
