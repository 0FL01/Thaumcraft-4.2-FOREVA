package thaumcraft.common.lib.events;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TileSensorNoteEventStaticGuardTest {

    @Test
    public void tileSensorNoteEventHooksStayWired() throws IOException {
        String tileSensor = readFile("src/main/java/thaumcraft/common/tiles/TileSensor.java");
        String eventHandlerWorld = readFile("src/main/java/thaumcraft/common/lib/events/EventHandlerWorld.java");
        String serverTick = readFile("src/main/java/thaumcraft/common/lib/events/ServerTickEventsFML.java");

        assertTrue("TileSensor must expose noteBlockEvents weak map",
                tileSensor.contains("WeakHashMap<WorldServer, ArrayList<Integer[]>> noteBlockEvents"));
        assertTrue("World unload path must remove TileSensor note events for unloading world",
                eventHandlerWorld.contains("TileSensor.noteBlockEvents.remove((WorldServer) event.getWorld())"));
        assertTrue("Note-block play event path must append instrument/note payload into TileSensor note map",
                eventHandlerWorld.contains("event.getInstrument().ordinal()")
                        && eventHandlerWorld.contains("event.getVanillaNoteId()")
                        && eventHandlerWorld.contains("TileSensor.noteBlockEvents.put(world, new ArrayList<>())"));
        assertTrue("Server world tick must clear TileSensor note event list each tick",
                serverTick.contains("TileSensor.noteBlockEvents.get((WorldServer) world)")
                        && serverTick.contains("events.clear()"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}
