package thaumcraft.common.lib.research;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ScanManagerThaumometerNotificationStaticGuardTest {
    @Test
    public void thaumometerNotificationsShouldUseExplicitClientFailureHelpers() throws IOException {
        String source = new String(Files.readAllBytes(Paths.get("src/main/java/thaumcraft/common/lib/research/ScanManager.java")), StandardCharsets.UTF_8);
        String item = new String(Files.readAllBytes(Paths.get("src/main/java/thaumcraft/common/items/relics/ItemThaumometer.java")), StandardCharsets.UTF_8);
        String clientProxy = new String(Files.readAllBytes(Paths.get("src/main/java/thaumcraft/client/ClientProxy.java")), StandardCharsets.UTF_8);
        assertTrue(source.contains("public static void notifyInvalidScan(AspectList aspects, EntityPlayer player)"));
        assertTrue(source.contains("Thaumcraft.proxy.notifyThaumometerUnknownObject()"));
        assertTrue(source.contains("Thaumcraft.proxy.notifyThaumometerDiscoveryError(parent)"));
        assertTrue(item.contains("ScanManager.notifyInvalidScan(aspects, player);"));
        assertTrue(clientProxy.contains("localizeOrFallback(\"tc.discoveryerror\", \"To understand this you need to study %1$s.\")"));
        assertTrue(clientProxy.contains("localizeOrFallback(\"tc.unknownobject\", \"Nothing can be learned from this.\")"));
        assertTrue(clientProxy.contains("formatThaumometerAspectLabel(aspect)"));
        assertTrue(clientProxy.contains("aspect.getLocalizedDescription()"));
        assertTrue(clientProxy.contains("tc.aspect.help."));
    }
}
