package thaumcraft.common.lib.network.playerdata;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PlayerDataPacketClientBoundaryStaticGuardTest {

    private static final String[] PACKET_FILES = new String[]{
            "src/main/java/thaumcraft/common/lib/network/playerdata/PacketAspectPool.java",
            "src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncResearch.java",
            "src/main/java/thaumcraft/common/lib/network/playerdata/PacketWarpMessage.java",
            "src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncAspects.java",
            "src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncWipe.java",
            "src/main/java/thaumcraft/common/lib/network/playerdata/PacketAspectDiscovery.java",
            "src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncScannedPhenomena.java",
            "src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncScannedItems.java",
            "src/main/java/thaumcraft/common/lib/network/playerdata/PacketResearchComplete.java",
            "src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncScannedEntities.java",
            "src/main/java/thaumcraft/common/lib/network/playerdata/PacketRunicCharge.java",
            "src/main/java/thaumcraft/common/lib/network/playerdata/PacketSyncWarp.java"
    };

    @Test
    public void playerDataClientPacketsUseProxyBoundaryInsteadOfDirectMinecraftSingleton() throws IOException {
        for (String path : PACKET_FILES) {
            String source = readFile(path);
            assertFalse("Packet must not import client singleton directly: " + path,
                    source.contains("import net.minecraft.client.Minecraft;"));
            assertFalse("Packet must not call Minecraft.getMinecraft() directly: " + path,
                    source.contains("Minecraft.getMinecraft()"));
            assertTrue("Packet should schedule client work through proxy boundary: " + path,
                    source.contains("Thaumcraft.proxy.scheduleClientTask("));
        }
    }

    @Test
    public void aspectDiscoveryAndPoolPacketsShouldRestoreClientNotifications() throws IOException {
        String discovery = readFile("src/main/java/thaumcraft/common/lib/network/playerdata/PacketAspectDiscovery.java");
        String pool = readFile("src/main/java/thaumcraft/common/lib/network/playerdata/PacketAspectPool.java");
        String clientProxy = readFile("src/main/java/thaumcraft/client/ClientProxy.java");

        assertTrue(discovery.contains("Thaumcraft.proxy.notifyThaumometerAspectDiscovery(aspect);"));
        assertTrue(discovery.contains("SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP"));

        assertTrue(pool.contains("Thaumcraft.proxy.notifyThaumometerAspectPool(aspect, amount);"));
        assertTrue(pool.contains("SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP"));

        assertTrue(clientProxy.contains("notifyThaumometerAspectDiscovery"));
        assertTrue(clientProxy.contains("notifyThaumometerAspectPool"));
        assertTrue(clientProxy.contains("notifyThaumometerDiscoveryError"));
        assertTrue(clientProxy.contains("notifyThaumometerUnknownObject"));
        assertTrue(clientProxy.contains("localizeOrFallback(\"tc.addaspectdiscovery\", \"You have discovered %n!\")"));
        assertTrue(clientProxy.contains("localizeOrFallback(\"tc.addaspectpool\", \"Gained %s research point(s) for %n\")"));
        assertTrue(clientProxy.contains("localizeOrFallback(\"tc.discoveryerror\", \"To understand this you need to study %1$s.\")"));
        assertTrue(clientProxy.contains("localizeOrFallback(\"tc.unknownobject\", \"Nothing can be learned from this.\")"));
        assertTrue(clientProxy.contains("formatThaumometerAspectLabel(aspect)"));
        assertTrue(clientProxy.contains("formatThaumometerAspectDescription(missingAspect)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
