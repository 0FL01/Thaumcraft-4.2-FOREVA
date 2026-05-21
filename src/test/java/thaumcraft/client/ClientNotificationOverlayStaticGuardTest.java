package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ClientNotificationOverlayStaticGuardTest {

    @Test
    public void renderEventHandlerShouldRoutePlayerNotificationsThroughDedicatedHudRenderer() throws IOException {
        String renderHandler = read("src/main/java/thaumcraft/client/lib/RenderEventHandler.java");
        String notifyHandler = read("src/main/java/thaumcraft/client/lib/REHNotifyHandler.java");
        String notifications = read("src/main/java/thaumcraft/client/lib/PlayerNotifications.java");

        assertTrue(renderHandler.contains("private final REHNotifyHandler notifyHandler = new REHNotifyHandler();"));
        assertTrue(renderHandler.contains("renderNotifications(RenderGameOverlayEvent.Text event)"));
        assertTrue(renderHandler.contains("this.notifyHandler.handleNotifications("));

        assertTrue(notifyHandler.contains("renderNotifyHUD("));
        assertTrue(notifyHandler.contains("renderAspectHUD("));
        assertTrue(notifyHandler.contains("PlayerNotifications.getListAndUpdate(time)"));
        assertTrue(notifyHandler.contains("PlayerNotifications.getAspectListAndUpdate(time)"));

        assertTrue(notifications.contains("addNotification(String text, Aspect aspect)"));
        assertTrue(notifications.contains("addAspectNotification(Aspect aspect)"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
