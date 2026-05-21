package thaumcraft.common.lib.research;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ScanManagerThaumometerNotificationStaticGuardTest {
    @Test
    public void validScanShouldRestoreClientUnknownAndDiscoveryHints() throws IOException {
        String source = new String(Files.readAllBytes(Paths.get("src/main/java/thaumcraft/common/lib/research/ScanManager.java")), StandardCharsets.UTF_8);
        String clientProxy = new String(Files.readAllBytes(Paths.get("src/main/java/thaumcraft/client/ClientProxy.java")), StandardCharsets.UTF_8);
        assertTrue(source.contains("Thaumcraft.proxy.notifyThaumometerUnknownObject()"));
        assertTrue(source.contains("Thaumcraft.proxy.notifyThaumometerDiscoveryError(parent)"));
        assertTrue(source.contains("player.world.isRemote"));
        assertTrue(clientProxy.contains("net.minecraft.client.resources.I18n.format(\"tc.discoveryerror\", missing)"));
        assertTrue(clientProxy.contains("net.minecraft.client.resources.I18n.format(\"tc.unknownobject\")"));
        assertTrue(clientProxy.contains("tc.aspect.help."));
    }
}
