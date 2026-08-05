package thaumcraft.client;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FocusPouchClientScanOrderStaticGuardTest {
    @Test
    public void clientAndServerUseBaublesThenMainInventoryThenOffhand() throws Exception {
        assertScanOrder(read("src/main/java/thaumcraft/common/items/wands/WandManager.java"),
                "public static void changeFocus", "public static void toggleMisc");
        assertScanOrder(read("src/main/java/thaumcraft/client/lib/REHWandHandler.java"),
                "if (radialHudScale == 0.0F)", "// Grab mouse so we can track position");
    }

    private static void assertScanOrder(String source, String startMarker, String endMarker) {
        String scan = source.substring(source.indexOf(startMarker), source.indexOf(endMarker, source.indexOf(startMarker)));
        int baubles = scan.indexOf("BaublesApi.getBaublesHandler(player)");
        int mainInventory = scan.indexOf("player.inventory.mainInventory.size()");
        int offhand = scan.indexOf("player.getHeldItemOffhand()", mainInventory);
        assertTrue("Focus pouch scan order must remain Baubles, main inventory, offhand",
                baubles >= 0 && baubles < mainInventory && mainInventory < offhand);
    }

    private static String read(String path) throws Exception {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
