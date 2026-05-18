package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ClientFxEventLayerStaticGuardTest {

    @Test
    public void clientTickAndRenderHandlersKeepWarpFogOverlayContracts() throws IOException {
        String tickSource = read("src/main/java/thaumcraft/client/lib/ClientTickEventsFML.java");
        String renderSource = read("src/main/java/thaumcraft/client/lib/RenderEventHandler.java");

        assertTrue(tickSource.contains("if (warpVignette > 0)"));
        assertTrue(tickSource.contains("RenderEventHandler.targetBrightness = 0.0F;"));
        assertTrue(tickSource.contains("RenderEventHandler.targetBrightness = 1.0F;"));
        assertTrue(tickSource.contains("if (RenderEventHandler.fogFiddled)"));
        assertTrue(tickSource.contains("RenderEventHandler.fogTarget = 0.1F"));

        assertTrue(renderSource.contains("public static float fogTarget = 0.0F;"));
        assertTrue(renderSource.contains("public static float targetBrightness = 1.0F;"));
        assertTrue(renderSource.contains("textures/misc/vignette.png"));
        assertTrue(renderSource.contains("event.getType() == RenderGameOverlayEvent.ElementType.PORTAL"));
        assertTrue(renderSource.contains("renderVignette(targetBrightness"));
        assertTrue(renderSource.contains("GL11.glFogf(GL11.GL_FOG_DENSITY, fogTarget);"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
