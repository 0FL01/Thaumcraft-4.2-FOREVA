package thaumcraft.common;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class CommonProxyServerGuiRoutingStaticGuardTest {

    @Test
    public void commonProxyShouldKeepServerArcaneWorkbenchAndFocalManipulatorRouting() throws IOException {
        String commonProxySource = readFile("src/main/java/thaumcraft/common/CommonProxy.java");

        assertTrue("CommonProxy should keep GUI_ARCANE_WORKBENCH id constant",
                commonProxySource.contains("public static final int GUI_ARCANE_WORKBENCH = 13;"));
        assertTrue("CommonProxy should keep Arcane Workbench server GUI switch case",
                commonProxySource.contains("case GUI_ARCANE_WORKBENCH:"));
        assertTrue("CommonProxy should route TileArcaneWorkbench to ContainerArcaneWorkbench",
                commonProxySource.contains("tile instanceof TileArcaneWorkbench")
                        && commonProxySource.contains("new ContainerArcaneWorkbench(player.inventory, (TileArcaneWorkbench) tile)"));

        assertTrue("CommonProxy should keep GUI_FOCAL_MANIPULATOR id constant",
                commonProxySource.contains("public static final int GUI_FOCAL_MANIPULATOR = 20;"));
        assertTrue("CommonProxy should keep focal manipulator server GUI switch case",
                commonProxySource.contains("case GUI_FOCAL_MANIPULATOR:"));
        assertTrue("CommonProxy should route TileFocalManipulator to ContainerFocalManipulator",
                commonProxySource.contains("tile instanceof TileFocalManipulator")
                        && commonProxySource.contains("new ContainerFocalManipulator(player.inventory, (TileFocalManipulator) tile)"));
    }

    @Test
    public void commonProxyShouldKeepThaumonomiconServerGuiNullRoute() throws IOException {
        String commonProxySource = readFile("src/main/java/thaumcraft/common/CommonProxy.java");

        assertTrue("CommonProxy should keep GUI_THAUMONOMICON id constant",
                commonProxySource.contains("public static final int GUI_THAUMONOMICON = 12;"));
        assertTrue("CommonProxy should keep thaumonomicon server GUI branch returning null",
                commonProxySource.contains("case GUI_THAUMONOMICON: return null;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
