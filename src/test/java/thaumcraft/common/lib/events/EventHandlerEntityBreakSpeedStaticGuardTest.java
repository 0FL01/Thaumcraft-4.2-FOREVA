package thaumcraft.common.lib.events;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EventHandlerEntityBreakSpeedStaticGuardTest {

    @Test
    public void breakSpeedShouldUseHoverAerialBoostContract() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java");

        assertTrue(source.contains("!event.getEntityPlayer().onGround && Hover.getHover(event.getEntityPlayer().getEntityId())"));
        assertTrue(source.contains("event.setNewSpeed(event.getOriginalSpeed() * 5.0F);"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
