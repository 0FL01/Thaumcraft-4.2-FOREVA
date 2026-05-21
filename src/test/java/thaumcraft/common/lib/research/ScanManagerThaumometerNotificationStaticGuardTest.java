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
        assertTrue(source.contains("PlayerNotifications.addNotification"));
        assertTrue(source.contains("tc.discoveryerror"));
        assertTrue(source.contains("tc.unknownobject"));
        assertTrue(source.contains("tc.aspect.help."));
        assertTrue(source.contains("player.world.isRemote"));
    }
}
