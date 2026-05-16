package thaumcraft.common.lib.events;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EventHandlerEntityInteractStaticGuardTest {

    @Test
    public void golemOwnerInteractGuardStaysWired() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java");

        assertTrue("Entity interact path must guard non-owner interactions with golems",
                source.contains("event.getTarget() instanceof EntityGolemBase")
                        && source.contains("getOwnerName().length() > 0")
                        && source.contains("sendMessage(new TextComponentTranslation(\"You are not my Master!\"))")
                        && source.contains("event.setCanceled(true);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
