package thaumcraft.common;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ProxyUtilityGuiRoutingStaticGuardTest {

    @Test
    public void commonProxyShouldKeepUtilityServerGuiRouting() throws IOException {
        String commonProxySource = readFile("src/main/java/thaumcraft/common/CommonProxy.java");

        assertTrue("CommonProxy should keep focus pouch server route",
                commonProxySource.contains("case GUI_FOCUS_POUCH: return new ContainerFocusPouch(player.inventory, world, x, y, z);"));
        assertTrue("CommonProxy should keep hand mirror server route",
                commonProxySource.contains("case GUI_HAND_MIRROR: return new ContainerHandMirror(player.inventory, world, x, y, z);"));
        assertTrue("CommonProxy should keep hover harness server route",
                commonProxySource.contains("case GUI_HOVER_HARNESS: return new ContainerHoverHarness(player.inventory, world, x, y, z);"));
        assertTrue("CommonProxy should keep magic box server route via IInventory tile check",
                commonProxySource.contains("case GUI_MAGIC_BOX:")
                        && commonProxySource.contains("tile instanceof IInventory ? new ContainerMagicBox(player.inventory, tile) : null;"));
        assertTrue("CommonProxy should keep spa server route",
                commonProxySource.contains("case GUI_SPA:")
                        && commonProxySource.contains("tile instanceof TileSpa ? new ContainerSpa(player.inventory, (TileSpa) tile) : null;"));
    }

    @Test
    public void clientProxyShouldKeepUtilityClientGuiRouting() throws IOException {
        String clientProxySource = readFile("src/main/java/thaumcraft/client/ClientProxy.java");

        assertTrue("ClientProxy should keep focus pouch client route",
                clientProxySource.contains("case GUI_FOCUS_POUCH:")
                        && clientProxySource.contains("new GuiFocusPouch(player.inventory, world, x, y, z)"));
        assertTrue("ClientProxy should keep hand mirror client route",
                clientProxySource.contains("case GUI_HAND_MIRROR:")
                        && clientProxySource.contains("new GuiHandMirror(player.inventory, world, x, y, z)"));
        assertTrue("ClientProxy should keep hover harness client route",
                clientProxySource.contains("case GUI_HOVER_HARNESS:")
                        && clientProxySource.contains("new GuiHoverHarness(player.inventory, world, x, y, z)"));
        assertTrue("ClientProxy should keep magic box client route via IInventory tile check",
                clientProxySource.contains("case GUI_MAGIC_BOX:")
                        && clientProxySource.contains("tile instanceof IInventory")
                        && clientProxySource.contains("new GuiMagicBox(player.inventory, tile)"));
        assertTrue("ClientProxy should keep spa client route",
                clientProxySource.contains("case GUI_SPA:")
                        && clientProxySource.contains("tile instanceof TileSpa")
                        && clientProxySource.contains("new GuiSpa(player.inventory, (TileSpa) tile)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
