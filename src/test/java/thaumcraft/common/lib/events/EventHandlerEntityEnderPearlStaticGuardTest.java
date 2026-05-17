package thaumcraft.common.lib.events;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EventHandlerEntityEnderPearlStaticGuardTest {

    @Test
    public void entityJoinWorldShouldCancelEnderPearlsNearWardedTiles() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/events/EventHandlerEntity.java");

        assertTrue(source.contains("event.getEntity() instanceof EntityEnderPearl"));
        assertTrue(source.contains("tile instanceof TileOwned"));
        assertTrue(source.contains("The magic of a nearby warded object destroys the ender pearl."));
        assertTrue(source.contains("pearl.setDead();"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
