package thaumcraft.common.lib.events;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EventHandlerEntityTravellerJumpStaticGuardTest {

    @Test
    public void livingJumpShouldApplyTravellerBootsBonus() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java");

        assertTrue(source.contains("public void onLivingJump(LivingEvent.LivingJumpEvent event)"));
        assertTrue(source.contains("boots.getItem() != ConfigItems.itemBootsTraveller"));
        assertTrue(source.contains("player.motionY += 0.275D;"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
