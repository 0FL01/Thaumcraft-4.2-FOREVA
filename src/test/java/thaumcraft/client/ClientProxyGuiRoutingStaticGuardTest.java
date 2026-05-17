package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ClientProxyGuiRoutingStaticGuardTest {

    @Test
    public void clientProxyShouldKeepArcaneWorkbenchGuiRouting() throws IOException {
        String clientProxySource = readFile("src/main/java/thaumcraft/client/ClientProxy.java");
        String commonProxySource = readFile("src/main/java/thaumcraft/common/CommonProxy.java");

        assertTrue("CommonProxy should keep GUI_ARCANE_WORKBENCH id constant",
                commonProxySource.contains("public static final int GUI_ARCANE_WORKBENCH = 13;"));
        assertTrue("ClientProxy should keep Arcane Workbench GUI switch case",
                clientProxySource.contains("case GUI_ARCANE_WORKBENCH:"));
        assertTrue("ClientProxy should route TileArcaneWorkbench to GuiArcaneWorkbench",
                clientProxySource.contains("tile instanceof TileArcaneWorkbench")
                        && clientProxySource.contains("new GuiArcaneWorkbench(player.inventory, (TileArcaneWorkbench) tile)"));
    }

    @Test
    public void clientProxyShouldKeepResearchBrowserAndFocalManipulatorGuiRouting() throws IOException {
        String clientProxySource = readFile("src/main/java/thaumcraft/client/ClientProxy.java");

        assertTrue("ClientProxy should keep Thaumonomicon GUI route to GuiResearchBrowser",
                clientProxySource.contains("case GUI_THAUMONOMICON:")
                        && clientProxySource.contains("return new GuiResearchBrowser();"));
        assertTrue("ClientProxy should keep focal manipulator GUI route",
                clientProxySource.contains("case GUI_FOCAL_MANIPULATOR:")
                        && clientProxySource.contains("new GuiFocalManipulator(player.inventory, (TileFocalManipulator) tile)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
