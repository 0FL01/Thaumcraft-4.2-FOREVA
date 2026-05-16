package thaumcraft.common.lib.events;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EventHandlerEntityPickupStaticGuardTest {

    @Test
    public void fakeThaumcraftPickupGuardStaysWired() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java");

        assertTrue("EventHandlerEntity item pickup path must keep FakeThaumcraft cancel guard",
                source.contains("event.getEntityPlayer().getName().startsWith(\"FakeThaumcraft\")")
                        && source.contains("event.setCanceled(true);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
