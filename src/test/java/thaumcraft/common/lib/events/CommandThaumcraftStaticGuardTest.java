package thaumcraft.common.lib.events;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CommandThaumcraftStaticGuardTest {

    @Test
    public void commandThaumcraftShouldBeRegisteredAndExposeReferenceCommandSurface() throws IOException {
        String thaumcraft = readFile("src/main/java/thaumcraft/common/Thaumcraft.java");
        String command = readFile("src/main/java/thaumcraft/common/lib/events/CommandThaumcraft.java");

        assertTrue(thaumcraft.contains("event.registerServerCommand(new CommandThaumcraft());"));
        assertFalse(thaumcraft.contains("server command registration deferred"));

        assertTrue(command.contains("this.aliases.add(\"thaumcraft\")"));
        assertTrue(command.contains("this.aliases.add(\"thaum\")"));
        assertTrue(command.contains("this.aliases.add(\"tc\")"));
        assertTrue(command.contains("\"research\".equalsIgnoreCase(args[0])"));
        assertTrue(command.contains("\"aspect\".equalsIgnoreCase(args[0])"));
        assertTrue(command.contains("\"warp\".equalsIgnoreCase(args[0])"));
        assertTrue(command.contains("PacketHandler.INSTANCE.sendTo(new PacketSyncAspects"));
        assertTrue(command.contains("PacketHandler.INSTANCE.sendTo(new PacketSyncResearch"));
        assertTrue(command.contains("PacketHandler.INSTANCE.sendTo(new PacketWarpMessage"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
