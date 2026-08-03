package thaumcraft.common.lib.research;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ScanManagerThaumometerNotificationStaticGuardTest {
    @Test
    public void thaumometerNotificationsShouldUseExplicitClientFailureHelpers() throws IOException {
        String source = new String(Files.readAllBytes(Paths.get("src/main/java/thaumcraft/common/lib/research/ScanManager.java")), StandardCharsets.UTF_8);
        String item = new String(Files.readAllBytes(Paths.get("src/main/java/thaumcraft/common/items/relics/ItemThaumometer.java")), StandardCharsets.UTF_8);
        String clientProxy = new String(Files.readAllBytes(Paths.get("src/main/java/thaumcraft/client/ClientProxy.java")), StandardCharsets.UTF_8);
        assertTrue(source.contains("public static void notifyInvalidScan(AspectList aspects, EntityPlayer player)"));
        assertTrue(source.contains("Thaumcraft.proxy.notifyThaumometerUnknownObject()"));
        assertTrue(source.contains("Thaumcraft.proxy.notifyThaumometerDiscoveryError(parent)"));
        assertTrue(item.contains("ScanManager.notifyInvalidScan(ScanManager.getScanAspects(current, world), player);"));
        assertTrue(clientProxy.contains("localizeOrFallback(\"tc.discoveryerror\", \"To understand this you need to study %1$s.\")"));
        assertTrue(clientProxy.contains("localizeOrFallback(\"tc.unknownobject\", \"Nothing can be learned from this.\")"));
        assertTrue(clientProxy.contains(".replace(\"%n\", aspect.getName())"));
        assertTrue(clientProxy.contains("aspect.getLocalizedDescription()"));
        assertTrue(clientProxy.contains("tc.aspect.help."));

        String phenomenonOverride = "public ScanResult scanPhenomena(ItemStack stack, World world, EntityPlayer player) {\n        return null;\n    }";
        assertTrue(source.contains(phenomenonOverride));
        assertFalse(source.contains("return stack == null || stack.isEmpty() ? null : scanItem(player, stack);"));
        assertTrue(item.contains("for (IScanEventHandler handler : ThaumcraftApi.scanEventhandlers)"));
        assertTrue(item.contains("if (result != null)"));
    }
}
