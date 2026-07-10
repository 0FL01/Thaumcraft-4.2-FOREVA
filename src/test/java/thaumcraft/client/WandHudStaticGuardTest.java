package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class WandHudStaticGuardTest {

    @Test
    public void textOverlayShouldRenderTheCastingWandHud() throws IOException {
        String renderHandler = read("src/main/java/thaumcraft/client/lib/RenderEventHandler.java");
        String wandHandler = read("src/main/java/thaumcraft/client/lib/REHWandHandler.java");

        assertTrue(renderHandler.contains("this.wandHandler.handleCastingWandHud(mc, time, event);"));
        assertTrue(wandHandler.contains("handleCastingWandHud(Minecraft mc, long time, RenderGameOverlayEvent event)"));
        assertTrue(wandHandler.contains("instanceof ItemWandCasting"));
        assertTrue(wandHandler.contains("textures/gui/hud.png"));
        assertTrue(wandHandler.contains("mc.player.isSneaking()"));
        assertTrue(wandHandler.contains("ItemWandCasting.getConsumptionModifier"));
        assertTrue(wandHandler.contains("Config.dialBottom"));
        assertTrue(wandHandler.contains("WandManager.getCooldown"));
        assertTrue(wandHandler.contains("focus instanceof FocusTrade"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
