package thaumcraft.common.lib.network.misc;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PacketMiscClientBoundaryStaticGuardTest {

    @Test
    public void miscClientPacketsUseProxyBoundaryInsteadOfDirectMinecraftSingleton() throws IOException {
        assertProxyClientBoundary("src/main/java/thaumcraft/common/lib/network/misc/PacketMiscEvent.java");
        assertProxyClientBoundary("src/main/java/thaumcraft/common/lib/network/misc/PacketBoreDig.java");
        assertProxyClientBoundary("src/main/java/thaumcraft/common/lib/network/misc/PacketNote.java");
    }

    private static void assertProxyClientBoundary(String path) throws IOException {
        String source = readFile(path);
        assertFalse("Packet must not import client singleton directly: " + path,
                source.contains("import net.minecraft.client.Minecraft;"));
        assertFalse("Packet must not call Minecraft.getMinecraft() directly: " + path,
                source.contains("Minecraft.getMinecraft()"));
        assertTrue("Packet should schedule client work through proxy boundary: " + path,
                source.contains("Thaumcraft.proxy.scheduleClientTask("));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
